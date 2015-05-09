package cliqueTreeEnumeration;
import mpi.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashSet;
import java.util.Arrays;

public class cliqueEnumerate_v2
{	
	public static void main(String args[]) throws InterruptedException
	{
		MPI.Init(args);
		int ID = MPI.COMM_WORLD.Rank();
		int numProcesses = MPI.COMM_WORLD.Size();
		int master = 0;
		
		if(ID==master)
		{
			String file_name = args[3];
			HashSet<String> cliqueSet = new HashSet<String>();
		    int noThreads[] = {Integer.parseInt(args[4])};
			try {
				File file = new File(file_name);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				Node[] nodes = null;
				int size = 0;
				int n=0;
				
				while ((line = bufferedReader.readLine()) != null) {
					String[] parts = line.split(" ");
					if(parts[0].equals("*Vertices"))
					{
						size = Integer.parseInt(parts[1]);
						nodes = new Node[size];
					}
					else
					{
					if(n<size)
					{
						nodes[n] = new Node(parts[0]);
						n++;
					}
					else if(!parts[0].equals("*Edges"))
					{
						int a = Node.index.get(parts[0]);
						int b = 0;
						if (nodes.length>1) {
							b = Node.index.get(parts[1]);
						}
						
						if (nodes.length>1) {
							nodes[a].addNeighbor(nodes[a], nodes[b]);
						}
					}
					}
				}
				bufferedReader.close();
				long start_time = System.currentTimeMillis();
				int arr[] = {size};
				MPI.COMM_WORLD.Bcast(arr, 0, 1, MPI.INT, 0);
				MPI.COMM_WORLD.Bcast(nodes, 0, size, MPI.OBJECT, 0);
				MPI.COMM_WORLD.Bcast(noThreads, 0,1, MPI.INT, 0);
				int nodesPerProcess = size / (numProcesses - 1);
				int excessNodes = size % (numProcesses -1);
				int start = 0, end = nodesPerProcess + excessNodes;
				
				int a[] = {start, end};
				MPI.COMM_WORLD.Send(a, 0, 2, MPI.INT, 1, 11);
				
				for (int i=2; i<numProcesses; i++) {
					start = end;
					end = end + nodesPerProcess;
					int temp[] = {start, end};
					MPI.COMM_WORLD.Send(temp, 0, 2, MPI.INT, i, 11);
				}
				
				
				String finalCliques = "";
				for (int i=1; i<numProcesses; i++) {
					int lenCollectCliques[] = new int[1];
					MPI.COMM_WORLD.Recv(lenCollectCliques, 0, 1, MPI.INT, i, 100);
					//System.out.println("Length of cliques set "+lenCollectCliques[0]+" from "+i);
				    byte[] collectCliquesBytes = new byte[lenCollectCliques[0]];
				    MPI.COMM_WORLD.Recv(collectCliquesBytes, 0, lenCollectCliques[0], MPI.BYTE, i, 200);
				    String cliquesFromP = new String(collectCliquesBytes);
			    	finalCliques += cliquesFromP;
				}
				long end_time = System.currentTimeMillis();
				//Printing final cliques
				System.out.println("Printing final cliques:");
				String cliqueArray[] = finalCliques.split("\\n");
				for(String c: cliqueArray) {
			    	cliqueSet.add(c.trim());
			    }
				System.out.println(cliqueSet.size() + " cliques found.");
				
			     for(String c: cliqueSet) {
			    	System.out.println(c);
			    }
				System.out.println("Time taken for processing: "+(end_time-start_time)+"ms");
			} 
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			
			
		}
		else
		{
			//Receiving list of neighbors
			int arr[] = new int[1];
			MPI.COMM_WORLD.Bcast(arr, 0, 1, MPI.INT, 0);
			Node nodes_msg[] = new Node[arr[0]];
			MPI.COMM_WORLD.Bcast(nodes_msg, 0, arr[0], MPI.OBJECT, 0);
			final Node nodes[] = nodes_msg; 
			int noThreads[] = new int[1];
			MPI.COMM_WORLD.Bcast(noThreads, 0,1, MPI.INT, 0);
			//System.out.println("Received neighbors");
			//Receiving indexes of nodes to work on
			int indexes[] = new int[2];
			MPI.COMM_WORLD.Recv(indexes, 0, 2, MPI.INT, 0, 11);
			int numNodes = indexes[1] - indexes[0];
			final int ID_final = ID;
			int workThread = numNodes/noThreads[0];
			int leftThread = numNodes%noThreads[0];
			//Spawning threads for each process
			Runnable threads = new Runnable()
			{	
				
				public void print(cp_struct cp)
				{
					System.out.println("Candidate List:");
					for(Node i: cp.cand)
						System.out.print(i.nodeID + " ");
					System.out.println("\nNot List:");
					for(Node i: cp.not)
						System.out.print(i.nodeID + " ");
					System.out.println("\nCompsub list");
					for(Node i: cp.compsub)
						System.out.print(i.nodeID + " ");
					System.out.println();
					
				}
				
				public ArrayList<Node> cliqueEnumerate(cp_struct cp) {
				
				if (cp.cand.isEmpty() && cp.not.isEmpty()) {
					return cp.compsub;
				}
				else {
					int max = 0;
					Node fixp = null;
					for (Node i: cp.cand) {
						int count = 0;
						for (Node j: i.neighbors) {
								if (i!=j) {
									if (i.neighbors.contains(j)) {
										count+=1;
									}
								}
						}
						if (count>max) {
							fixp = i;
							max = count;
						}
					}

					Node cur_v = fixp;
					while (cur_v!=null) {
						
						cp_struct cp_new = new cp_struct();
						cp_new.compsub = cp.compsub;
						for (Node i: cp.not) {
							if (cur_v.neighbors.contains(i)) {
								cp_new.not.add(i);
							}
						}
						
						for (Node i: cur_v.neighbors) {
							if (cp.cand.contains(i)) {
								cp_new.cand.add(i);
							}
						}
						cp_new.cand.remove(cur_v);
						cp_new.compsub.add(cur_v);
						cliqueEnumerate(cp_new);
						cp.not.add(cur_v);
						cp.cand.remove(cur_v);
						cur_v = null;
					}
				}
				return cp.compsub;
						
			}
		public void run()
		{
			String IDs = Thread.currentThread().getName();
			int v = Integer.parseInt(IDs);
			Node vertex = nodes[v];
			int no_vertices = 0;
			if (v == indexes[0])
			{
				no_vertices = workThread + leftThread;
			}
			else
			{
				no_vertices = workThread;
			}
			Node vertex1[] = new Node[no_vertices];
			String allCliques = "";
			for (int let = 0;let<no_vertices;let++)
			{
				int minus = v - indexes[0];
			    int n=v+minus+let;
			vertex1[let] = nodes[n];
			cp_struct cp = new cp_struct();
			cp.cand.addAll(vertex1[let].neighbors);
			
			ArrayList<Node> temp = new ArrayList<Node>();
			temp = cliqueEnumerate(cp);
			
			String cliques = "";
			if (temp!=null) {
				Integer cliqueID[] = new Integer[temp.size()];
				int j=0;
				for (Node i: temp) {
					cliqueID[j] = Integer.parseInt(i.nodeID);
					j+=1;
				}
				Arrays.sort(cliqueID);
				
				
				for (int i: cliqueID) {
					cliques += (i +" ");
				}
				
				cliques += "\n";
			}
			allCliques += cliques;
			}
			byte[] cliquesBytes = allCliques.getBytes();			
			int cliqueLength[] = {cliquesBytes.length};
			
			MPI.COMM_WORLD.Send(cliqueLength, 0, 1, MPI.INT, ID_final, v);
			MPI.COMM_WORLD.Send(cliquesBytes, 0, cliqueLength[0], MPI.BYTE, ID_final, v);
			}
		};
			Thread[] thread = null;
			if(noThreads[0]<=numNodes)
			{
				thread = new Thread[noThreads[0]];
			}
			else
			{
				thread = new Thread[numNodes];
			}
			
			for (int i=1; i<thread.length; i++) {
				if ((i+indexes[0])<arr[0]) {
					thread[i] = new Thread(threads);
			    	thread[i].setName((i+indexes[0])+"");
			    	thread[i].start();
				}
		    	
		    }
			if (indexes[0]<arr[0]) {
			    thread[0] = new Thread(threads);
			    thread[0].setName((0+indexes[0])+"");
			    thread[0].start();
			}
			
		    String collectCliques = "";
		    for (int i=0; i<thread.length; i++) {
		    	int cliquesLength[] = new int[1];
		    	MPI.COMM_WORLD.Recv(cliquesLength, 0, 1, MPI.INT, ID_final, (i+indexes[0]));		    
		    	byte byteCliques[] = new byte[cliquesLength[0]];
		    	MPI.COMM_WORLD.Recv(byteCliques, 0, cliquesLength[0], MPI.BYTE, ID_final, (i+indexes[0]));
		    	String cliques = new String(byteCliques);
		    	collectCliques += cliques;
			}
		    
		    for(int i=0; i<thread.length; i++) {
		    	thread[i].join();
		    }
		    
		    byte[] collectCliquesBytes = collectCliques.getBytes();
		    int lenCollectCliques[] = {collectCliquesBytes.length};
		    MPI.COMM_WORLD.Send(lenCollectCliques, 0, 1, MPI.INT, 0, 100);
		    MPI.COMM_WORLD.Send(collectCliquesBytes, 0, lenCollectCliques[0], MPI.BYTE, 0, 200);   
		    		   
		}
	    MPI.Finalize();
		
	}
}