/***********************************************************
******  Parallel Programming Clique Tree Enumeration  ******
******  Project members:                              ******
******  Rupali Saboo rsaboo1@jhu.edu                  ******
******  Sindhuula Selvaraju sselvar4@jhu.edu          ******
***********************************************************/

/***********************************************************
To compile, type:
javac -cp mpj.jar cliqueEnumerate.java Node.java cp_struct.java
************************************************************/

/***********************************************************
To run the code, go into code directory and type:
mpjrun.sh -np <number of processes> cliqueEnumerate /path/to/data/<filename>.txt <number of threads>
************************************************************/


/***********************************************************
Data:
We generated data either by having arbitrary edges between vertices, or by having fixed number of
neighbors for every vertex in the graph.
All data files follow the following format:
For plots where the performance is to be compared with increase in
number of vertices, keeping everything else constant, use these:
 
file_<number_of_vertices>_<number_of_neighbors_per_vertex>.txt

Example: file_10_10.txt will have 10 vertices and each vertex will 
have 10 neighbors (this is a clique the size of the graph)

For running the code on arbitrary graphs, with no control on number
or size of cliques, use these:

random_<number_of_vertices>.txt

Example: random_1000.txt consists of a graph with 1000 vertices.
************************************************************/
