package trial;

public class generate_graph {
public static void main(String[] args)
{
int no_nodes = Integer.parseInt(args[0]);
System.out.println("*Vertices " +no_nodes);
for(int i = 1;i<=no_nodes;i++)
{
System.out.print(i +" \"");
System.out.print((i-1) + "\"");	
System.out.println();
}
System.out.println("*Edges");
for(int i = 1;i<no_nodes;i++)
{
for(int j=i+1;j<=no_nodes;j++)
{
System.out.println(i + " "+ j);	
}
}
}
}
