package cliqueTreeEnumeration;
import mpi.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashSet;

public class cliqueEnumerate 
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
			HashSet<String> cliques = new HashSet<String>();
			
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
				int arr[] = {size};
				MPI.COMM_WORLD.Bcast(arr, 0, 1, MPI.INT, 0);
				MPI.COMM_WORLD.Bcast(nodes, 0, size, MPI.OBJECT, 0);
				
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
				
				
				ArrayList<ArrayList<Integer>> finalCliques = new ArrayList<ArrayList<Integer>>();
				for (int i=1; i<numProcesses; i++) {
					ArrayList<ArrayList<Integer>> cliqueIn_i = new ArrayList<ArrayList<Integer>>();
					MPI.COMM_WORLD.Bcast(cliqueIn_i, 0, 1, MPI.OBJECT, i);
					for (ArrayList<Integer> temp: cliqueIn_i) {
						finalCliques.add(temp);
					}
				}
				
				//Printing final cliques
				for (ArrayList<Integer> temp1: finalCliques) {
					for (Integer temp2: temp1) {
						System.out.print(temp2);
					}
					System.out.println();
				}
				
				
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
			System.out.println("Received neighbors");
			//Receiving indexes of nodes to work on
			int indexes[] = new int[2];
			MPI.COMM_WORLD.Recv(indexes, 0, 2, MPI.INT, 0, 11);
			System.out.println("Received indexes");
			
			final int ID_final = ID;
			
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
				//print(cp);
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
			System.out.println("Running thread");
			String IDs = Thread.currentThread().getName();
			int v = Integer.parseInt(IDs);
			Node vertex = nodes[v];
			
			cp_struct cp = new cp_struct();
			cp.cand.addAll(vertex.neighbors);
			
			ArrayList<Node> temp = new ArrayList<Node>();
			temp = cliqueEnumerate(cp);
			/*
			String cliques = "";
			if (temp!=null) {
				for (Node i: temp) {
					cliques += (i.nodeID) +" ";
				}
				cliques += "\n";
				
			}*/
			int cliqueLength[] = {temp.size()};
			Node[] clique_nodes = new Node[cliqueLength[0]]; 
			temp.toArray(clique_nodes);
			MPI.COMM_WORLD.Send(cliqueLength, 0, 1, MPI.INT, ID_final, v);
			MPI.COMM_WORLD.Send(clique_nodes, 0, cliqueLength[0], MPI.OBJECT, ID_final, v);
			}
		};
			Thread[] thread = new Thread[indexes[1] - indexes[0]];
			System.out.println("Num threads "+(indexes[1] - indexes[0]));
			for (int i=1; i<thread.length; i++) {
		    	thread[i] = new Thread(threads);
		    	thread[i].setName(i+"");
		    	thread[i].start();
		    }
			
		    thread[0] = new Thread(threads);
		    thread[0].setName(0+"");
		    thread[0].start();
		    ArrayList<Node> collected_cliques = new ArrayList<Node>();
		    for (int i=0; i<thread.length; i++) {
		    	//String cliques = "";
		    	int cliquesLength[] = new int[1];
		    	MPI.COMM_WORLD.Recv(cliquesLength, 0, 1, MPI.INT, ID_final, i);
		    	System.out.println("Clique length: "+cliquesLength[0]);
		    	Node cliques[] = new Node[cliquesLength[0]];
		    	MPI.COMM_WORLD.Recv(cliques, 0, cliquesLength[0], MPI.OBJECT, ID_final, i);
				for(Node n: cliques)
				{
					collected_cliques.add(n);
				}
		    }
		    
		    System.out.println("Collect cliques: "+collected_cliques.get(0).nodeID);
		    
		    for(int i=0; i<thread.length; i++) {
		    	thread[i].join();
		    }
		    
		    
		    
		    //MPI.COMM_WORLD.Bcast(maximalCliques, 0, 1, MPI.OBJECT, ID);
		    
		}
	    MPI.Finalize();
		
	}
}