package cliqueTreeEnumeration;

import java.util.ArrayList;

public class cp_struct {
	ArrayList<Node> cand;
	ArrayList<Node> compsub; 
	ArrayList<Node> not;
	
	cp_struct() {
		this.cand = new ArrayList<Node>();
		this.compsub = new ArrayList<Node>();
		this.not = new ArrayList<Node>();
	}
}
