package trial;

import java.util.ArrayList;

class variable
{
public int count;
//public String[][] result;
variable(int a)
{
this.count = a;	
//this.result[a] = null;
}
/*
public void equate(String[] res, int n)
{
	result[n]=res;
}*/
public void add()
{
this.count ++;	
}
public void print()
{
	System.out.println("Counter is "+this.count);
	//System.out.println("Result is " + this.result[this.count][0]);
}
}
public class try4 {
    public static void main(String[] args) {
        String[] arr = {"A","B","C","D","E"};
        int r = 3;
        combine(arr, 3);
    }

    private static void combine(String[] arr, int r) {
        String[] res = new String[r];
        //String[][] result = null;
        ArrayList<String[]> result = new ArrayList<String[]>();
        variable v = new variable(-1);
       // v.print();
        doCombine(arr, res, 0, 0, r,v,result);
        //String[][] inter = (String[][]) result.toArray();
        // String[] strings = (String[]) result.toArray();
         
        //System.out.println(strings[0] + strings[1]+ strings[2]);
        System.out.println("\nIn main"+result.size());
        for (Object[] array : result)
        	  for (Object o : array)
        	    System.out.print("item: " + o);    
        		System.out.println();
    }
    /*    String[][] w = (String[][])result.toArray(new String[result.size()][]);
        for (int i=0;i<10;i++)
        {
        	for(int j=0;j<3;j++)
        	{
        		System.out.print(w[i][j]);
        	}
        	System.out.println();
        }
        //String[] w = result.toArray(new String[result.size()]);
      */  
   
    /*
    private static void makeCombinations(String arr[], int combns) {
    	if (combns>arr.length) {
    		return;
    	}
    	
    }*/
        private static void doCombine(String[] arr, String[] res, int currIndex, int level, int r,variable counter, ArrayList<String[]> result) {
        
        if(level == r){
            printArray(res);
            String[] inter = new String[r];
            inter = res;
        //    printArray(inter);
     //       counter.equate(res, counter.count);
//		result[counter.count] = res; 
//      result[counter] = res;
//            System.out.print("counter:"+ );
            //counter.print();
            result.add(inter);
            for (Object[] array : result)
          	  {
            	for (Object o : array)
          	    {System.out.print("item: " + o);}
            	System.out.println();
          	  }
           inter = null; 
            return;
     	}
       for (int i = currIndex; i < arr.length; i++)
        {
    	   counter.add();
//    	   result[counter.count] = res;
           res[level] = arr[i];
           System.out.print(result.size());
       	   doCombine(arr, res, i+1, level+1, r,counter, result);//,result);        
        }
    }

    private static String[] printArray(String[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.print(res[i] + " ");
        }
        System.out.println();
        return res;
    }
}