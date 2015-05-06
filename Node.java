package cliqueTreeEnumeration;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
public class Node implements Serializable {
	/*
	 * HashMap to store indices of each node
	 */
	public static HashMap<String,Integer> index = new HashMap<String, Integer>();
	public static int i = 0;
	static final long serialVersionUID = 1;
	/*
	 * Node ID
	 */
	public String nodeID;
	
	/*
	 * List of neighbors
	 */
	public ArrayList<Node> neighbors;
	
	/*
	 * Constructor
	 * @param name : name of the node
	 */
	public Node(String name) {
		this.nodeID = name;
		this.neighbors = new ArrayList<Node>();
		index.put(name, i);
		i += 1;
	}
	
	/*
	 * Adds a neighbor to current node
	 */
	public void addNeighbor(Node a, Node b) {
		a.neighbors.add(b);
		b.neighbors.add(a);
	}
	
	/*
	 * print the adjacency list of node
	 */
	public void printNeighbors(Node p) {
		System.out.print(p.nodeID+" ");
		for (Node n: p.neighbors) {
			System.out.print(n.nodeID + " ");
		}
		System.out.println();
	}
	
}
