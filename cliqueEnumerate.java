package cliqueTreeEnumeration;
import mpi.*;
import java.io.*;
public class cliqueEnumerate {
public static void main(String args[])
{
	MPI.Init(args);
	int ID = MPI.COMM_WORLD.Rank();
	int numProcesses = MPI.COMM_WORLD.Size();
	int master = 0;
	Node[] nodes = null;
	int size = 0;
	if(ID==master)
	{
		System.out.println("Hi from MAster <"+ID+">");
		String file_name = args[3];
		
		try {
			File file = new File(file_name);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
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
			MPI.COMM_WORLD.Bcast(nodes, 0, size, MPI.OBJECT, 0);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	else
	{
		MPI.COMM_WORLD.Bcast(nodes, 0, size, MPI.OBJECT, 0);
		System.out.println("Hi from Slave <"+nodes[0].neighbors.size()+">");				
	}
    MPI.Finalize();
	
}
}