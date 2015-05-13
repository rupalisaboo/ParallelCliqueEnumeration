package cliqueTreeEnumeration;
import java.io.*;
import java.util.ArrayList;
public class read_files {
public static String[][] grouping(Node n,int s)
{
	String[] res = new String[s];
    String name = "result";
    ArrayList<String[]> result = new ArrayList<String[]>();
  	doCombine(n.neighbors, res, 0, 0, s,result);
  	String[][] array = new String[result.size()][]; 
  	int j =0;
  	for (String[] arrs : result)
    {
  		array[j] = new String[s+1];
  		array[j][0] = n.nodeID;
  		System.arraycopy(arrs, 0, array[j],1,s);
  		j++;
    }
  	return array;
}
private static void doCombine(ArrayList<String> arr, String[] res, int currIndex, int level, int r, ArrayList<String[]> result) {
    
    if(level == r){
        String[] inter = new String[r];
        System.arraycopy(res,0,inter,0,r);
        result.add(inter);
        inter = null; 
        return;
 	}
   for (int i = currIndex; i < arr.size(); i++)
    {
	  res[level] = arr.get(i);
  	  doCombine(arr, res, i+1, level+1, r, result);        
    }
}

public static void main(String args[])
{
String file_name = args[0];
System.out.println("File:"+file_name);
Node[] nodes = null;
try {
	File file = new File(file_name);
	FileReader fileReader = new FileReader(file);
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	String line;
	int size = 0;
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
   grouping(nodes[1],2);
}
}




