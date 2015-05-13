package cliqueTreeEnumeration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class read_files2 {
	public static String[] sort(String[] Array)
	{
		  boolean swap = true;
		    int j = 0;
		    while (swap) {
		        swap = false;
		        j++;
		        for (int i = 0; i < Array.length - j; i++) {
		            if (Array[i].compareTo(Array[i + 1]) > 0) {
		                String tmp = Array[i];
		                Array[i] = Array[i + 1];
		                Array[i + 1] = tmp;
		                swap = true;
		            }
		        }
		        }
		        return Array;
	}
	public static ArrayList<String[]> find_cliques(
			ArrayList<String[]> checkfor, Node[] nodes, int size, int numnodes) {
		String[][] check = new String[checkfor.size()][];
		ArrayList<String[]> res = new ArrayList<String[]>();
		int j = 0;
		for (String[] arrs : checkfor) {
			check[j] = new String[size];
			System.arraycopy(arrs, 0, check[j], 0, size);
			j++;
		}
		for (int i = 0; i < check.length; i++) {
			int flag = 0, m = 0;
			for (j = 0; j < numnodes; j++) {
				if (nodes[j].nodeID.equals(check[i][0])) {
					m = j;
				}
			}
			for (j = 1; j < check[i].length; j++) {
				if (nodes[m].neighbors.contains(check[i][j])) {
					flag = 1;
				} else {
					flag = 0;
				}
			}
			if (flag == 1) {
				res.add(check[i]);
			}
		}
		return res;
	}

	public static ArrayList<String[]> grouping(Node n, int s) {
		String[] res = new String[s];
		String name = "result";
		ArrayList<String[]> result = new ArrayList<String[]>();
		doCombine(n.neighbors, res, 0, 0, s, result);
		for (Object[] resl : result) {
			for (Object o : resl) {
				System.out.print(o);
			}
			System.out.println();
		}
		return result;
	}

	private static void doCombine(ArrayList<String> arr, String[] res,
			int currIndex, int level, int r, ArrayList<String[]> result) {

		if (level == r) {
			String[] inter = new String[r];
			System.arraycopy(res, 0, inter, 0, r);
			result.add(inter);
			inter = null;
			return;
		}
		for (int i = currIndex; i < arr.size(); i++) {
			res[level] = arr.get(i);
			doCombine(arr, res, i + 1, level + 1, r, result);
		}
	}

	public static void main(String args[])
{
String file_name = args[0];
System.out.println("File:"+file_name);
Node[] nodes = null;
int size = 0;
try {
	File file = new File(file_name);
	FileReader fileReader = new FileReader(file);
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	String line;
	int n=0;	
	int j=0;
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
			int b = Node.index.get(parts[1]);
			nodes[a].addNeighbor(nodes[a], nodes[b]);			
		}
		}
	}
bufferedReader.close();
} catch (IOException e) {
	e.printStackTrace();
}

   for(Node i:nodes) {
		i.printNeighbors(i);		
	   
   }
ArrayList<String[]> master_list = new ArrayList<String[]>();
for(int i=0;i<size;i++)
{
System.out.println("N = " +nodes[i].neighbors.size());
int k = nodes[i].neighbors.size();
if (k==1)
{
String[] n = new String[2];
n[0] = nodes[i].nodeID;
n[1] = nodes[i].neighbors.get(0);
int flag = 0;
    	for(String[] cont : master_list)
    	{
    		if(Arrays.equals(cont,n))
    		{
    		flag = 1;
    		}
    	}
    	if(flag==1)
    	System.out.println("Already there");
    	else
    	{
    		System.out.println("Adding");
    		master_list.add(array);
    	}
}
else
{
while(k>1)
{
System.out.println("Check for combi size: "+k);
	ArrayList<String[]> result = grouping(nodes[i],k);
    ArrayList<String[]> cliques = find_cliques(result,nodes,k,size);
    if(cliques.isEmpty())
    	System.out.println("No Cliques");
    else
    {
    String[] r = new String[cliques.get(0).length];
    r = cliques.get(0);
    System.out.println("Clique found: " + nodes[i].nodeID + r[0] + r[1]);
    System.out.println("Checking if it already exists");
    for(String[] cli : cliques)
    {
    		String[] array = new String[k+1];
      		array[0] = nodes[i].nodeID;
      		System.arraycopy(cli, 0, array,1,k);
      		
    	array = sort(array);
    	System.out.println("Sorting");
    	for(String a: array)
    	{
    		System.out.print(a);
    	}
    	int flag = 0;
    	for(String[] cont : master_list)
    	{
    		if(Arrays.equals(cont,array))
    		{
    		flag = 1;
    		}
    	}
    	if(flag==1)
    	System.out.println("Already there");
    	else
    	{
    		System.out.println("Adding");
    		master_list.add(array);
    	}
    }
    }
    System.out.println("master list size: "+master_list.size());
    for (String[] a: master_list)
    {
    	System.out.print("Master ");
    	for(String m: a)
    	{
    		System.out.print(m);
    	}
    	System.out.println();
    }
k--;
}
}
}
}
}
