package cliqueTreeEnumeration;
import mpi.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

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
					//	parts[1]=parts[1].replace("\"", "");
						nodes[n] = new Node(parts[0]);
						n++;
					}
					else if(!parts[0].equals("*Edges"))
					{
						int a = Node.index.get(parts[0]);
						int b = Node.index.get(parts[1]);
						nodes[a].addNeighbor(nodes[a], nodes[b]);			
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
			//Spawning threads for each process
			final int ID_final = ID;
			Runnable threads = new Runnable()
			{
				public void cliqueEnumerate(Node vertex, ArrayList<ArrayList<Node>> cliques) {
					cp_struct cp = new cp_struct();
					
					Stack<cp_struct> cp_stack = new Stack<cp_struct>();
					
					int ID = Integer.parseInt(vertex.nodeID);
					for (Node n: vertex.neighbors) {
						if (ID < Integer.parseInt(n.nodeID)) {
							cp.cand.add(n);
						}
						else {
							cp.not.add(n);
						}
						cp_stack.push(cp);
					}
					while (!cp_stack.empty()) {
						cp_struct curr = cp_stack.pop();
						if (curr.cand.isEmpty() && curr.not.isEmpty()) {
							cliques.add(curr.compsub);
						}
						else {
							for (Node n: cp.cand) {
								cliqueEnumerate(n, cliques);
							}
							
						}
					}
					
				}
				
				public void run()
				{
					String IDs = Thread.currentThread().getName();
					int v = Integer.parseInt(IDs);
					Node vertex = nodes[v];
					ArrayList<ArrayList<Node>> cliques = new ArrayList<ArrayList<Node>>();
					cliqueEnumerate(vertex, cliques);
					MPI.COMM_WORLD.Send(cliques, 0, 1, MPI.OBJECT, ID_final, Integer.parseInt(IDs));
				}
			};
			Thread[] thread = new Thread[indexes[1] - indexes[0]];
			for (int i=1; i<thread.length; i++) {
		    	thread[i] = new Thread(threads);
		    	thread[i].setName(i+"");
		    	thread[i].start();
		    }
			
		    thread[0] = new Thread(threads);
		    thread[0].setName(0+"");
		    thread[0].start();
		    
		    ArrayList<ArrayList<Integer>> maximalCliques = new ArrayList<ArrayList<Integer>>(); 
		    for (int i=0; i<thread.length; i++) {
		    	ArrayList<ArrayList<Integer>> cliques = new ArrayList<ArrayList<Integer>>();
		    	MPI.COMM_WORLD.Recv(cliques, 0, 1, MPI.OBJECT, ID_final, i);
		    	for (ArrayList<Integer> temp : cliques) {
		    		maximalCliques.add(temp);
		    		
		    	}
		    }
		    
		    for(int i=0; i<thread.length; i++) {
		    	thread[i].join();
		    }
		    
		    MPI.COMM_WORLD.Bcast(maximalCliques, 0, 1, MPI.OBJECT, ID);
		    
		}
	    MPI.Finalize();
		
	}
}