package cliqueTreeEnumeration;
import mpi.*;

import java.util.*;
import java.lang.*;
import java.io.*;
public class cliqueEnumerate {
public static void main(String args[])
{
	MPI.Init(args);
	int ID = MPI.COMM_WORLD.Rank();
	int sized = MPI.COMM_WORLD.Size();
	int master = 0;
	if(ID==master)
	{
	System.out.println("Hi from MAster <"+ID+">");
	String file_name = args[3];
	Node[] nodes = null;
try {
	File file = new File(file_name);
	FileReader fileReader = new FileReader(file);
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	String line;
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
} catch (IOException e) {
	e.printStackTrace();
}
for(Node i:nodes) {
	i.printNeighbors(i);		

	}
	}
	else
	{
		System.out.println("Hi from Slave <"+ID+">");				
	}
    MPI.Finalize();
	
}
}
