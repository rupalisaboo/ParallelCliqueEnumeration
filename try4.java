package trial;

import java.awt.List;
import java.lang.reflect.Array;
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
        ArrayList<String[]> result = new ArrayList<String[]>();
        variable v = new variable(-1);
        doCombine(arr, res, 0, 0, r,v,result);
        System.out.println("res\n"+res[0]+res[1]+res[2]);
        System.out.println("\nIn main"+result.size());
        //Trying to print the result
        for (Object[] array : result)
        {
        	  for (Object o : array)
        	    System.out.print("item: " + o);    
        		System.out.println();
        	}
    }
        private static void doCombine(String[] arr, String[] res, int currIndex, int level, int r,variable counter, ArrayList<String[]> result) {
        
        if(level == r){
            printArray(res);
     //http://stackoverflow.com/questions/18277657/creating-arraylist-of-arrays - We have similar pblm
            String[] inter = new String[r];
            inter = res;
            result.add(inter);
            //Tryinh to see wht the array list has every time
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
           res[level] = arr[i];
//           System.out.print(result.size());
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