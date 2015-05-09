package cliqueTreeEnumeration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
class counter
{
	public int counter;
	public counter()
	{
		this.counter = 0;
	}
	public void add()
	{
		this.counter += 1;
	}
	public void print()
	{
		System.out.println("Counter is " + this.counter);
	}
}
public class CopyOfenumerate {
	public static void print(cp_struct cp)
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
	public static void cliqueEnumerate(cp_struct cp,counter counter) {
		System.out.println("Enumerating "+counter.counter+"Input lists");
		print(cp);
		if (cp.cand.isEmpty() && cp.not.isEmpty()) {
				for(Node n: cp.compsub)
				{
					System.out.println("Clique" + n.nodeID);
				}
				return;
			}
			else {
				int max = 0;
				Node fixp = null;
				for (Node i: cp.cand) {
		//			System.out.println("Nodes "+i.nodeID);
					int count = 0;
					for (Node j: i.neighbors) {
						if (i!=j) {
			//				System.out.println("iner"+j.nodeID);

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

				System.out.println("Curr_v "+fixp.nodeID);
				
				Node cur_v = fixp;
				System.out.println("cur_v "+cur_v.nodeID);
				while (cur_v!=null) {
					cp_struct cp_new = new cp_struct();
					cp_new.compsub = cp.compsub;
					for (Node i: cp.not) {
						if (cur_v.neighbors.contains(i)) {
			//				System.out.println("Adding to new not"+i.nodeID);
							cp_new.not.add(i);
						}
					}
					
					for (Node i: cur_v.neighbors) {
						if (cp.cand.contains(i)) {
				//			System.out.println("Adding to new cand"+i.nodeID);
							cp_new.cand.add(i);
						}
					}
					cp_new.cand.remove(cur_v);
					cp_new.compsub.add(cur_v);
					System.out.println("New:");
					print(cp_new);
					counter.add();
					//if(counter.counter<3)
					//{
						cliqueEnumerate(cp_new,counter);
					//}
					System.out.println("Here?");
					cp.not.add(cur_v);
					cp.cand.remove(cur_v);
					
					cur_v = null;
					for (Node i: cp.cand) {
						if (!fixp.neighbors.contains(i)) {
							cur_v = i;
							break;
						}
						
					}
					
				}
		}
		return;
	}

	public static void main(String[] args) throws NumberFormatException, IOException
	{
		System.out.println("In main");
		cp_struct input = new cp_struct();
		String file_name = args[0];
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
		for(Node i : nodes)
		{
			for (Node j: i.neighbors)
			{
				System.out.print(j.nodeID);
			}
			System.out.println();
		}
		bufferedReader.close();
		ArrayList<Node> output = new ArrayList<Node>();
		for (int i =0;i<nodes[1].neighbors.size();i++)
		{
			//if (Integer.parseInt(nodes[2].nodeID) < Integer.parseInt(nodes[2].neighbors.get(i).nodeID))
				input.cand.add(nodes[1].neighbors.get(i));
			//else 
				//input.not.add(nodes[2].neighbors.get(i));			
		}
	
		counter count = new counter();
		cliqueEnumerate(input,count);
		
	}

}
