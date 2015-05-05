package cliqueTreeEnumeration;
import mpi.*;
import java.io.*;

public class cliqueEnumerate 
{	
	public static void main(String args[])
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
			Node nodes[] = new Node[arr[0]];
			MPI.COMM_WORLD.Bcast(nodes, 0, arr[0], MPI.OBJECT, 0);
			System.out.println("Received neighbors");
			//Receiving indexes of nodes to work on
			int indexes[] = new int[2];
			MPI.COMM_WORLD.Recv(indexes, 0, 2, MPI.INT, 0, 11);
			System.out.println("Received indexes");
			//Spawning threads for each process
			int no_of_threads = indexes[1]-indexes[0];
			Thread[] threads = new Thread[no_of_threads];
			thread_ops[] ths = new thread_ops[no_of_threads];
			for (int i=0; i<(indexes[1]-indexes[0]); i++) {
				threads[i] = new Thread(ths[i]);			
				threads[i].start();	
			}
			
		}
	    MPI.Finalize();
		
	}
}