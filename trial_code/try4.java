package trial;

import java.util.ArrayList;

public class try4 {
    public static void main(String[] args) {
        String[] arr = {"A","B","C","D","E"};
        int r = 3;
        combine(arr, 3);
    }

    private static void combine(String[] arr, int r) {
        String[] res = new String[r];
        ArrayList<String[]> result = new ArrayList<String[]>();
        ArrayList<String[]> answer = new ArrayList<String[]>();
        answer = doCombine(arr, res, 0, 0, r,result);
        System.out.println("\nIn main "+result.size());
        for (Object[] array : answer)
        {
        	  for (Object o : array)
        	    System.out.print("item: " + o);    
        		System.out.println();
        	}
    }
        private static ArrayList<String[]> doCombine(String[] arr, String[] res, int currIndex, int level, int r, ArrayList<String[]> result) {
        
        if(level == r){
            String[] inter = new String[r];
            System.arraycopy(res,0,inter,0,r);
            result.add(inter);
            inter = null; 
            return result;
     	}
       for (int i = currIndex; i < arr.length; i++)
        {
    	  res[level] = arr[i];
      	  doCombine(arr, res, i+1, level+1, r, result);//,result);        
        }
	return result;
    }

    private static void printArray(String[] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.print(res[i] + " ");
        }
        System.out.println();
    }
}