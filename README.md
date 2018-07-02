# Capstone-Project: Information Cascade Throughout a Graph
Hello and welcome to my capstone project for the Coursera specialization Object Oriented Java Programming: Data Structures and Beyond. The project involved working with the abstract data type known as graphs, and either standard or custom algorithms. My focus was on modeling the spread of information throughout a graph. A couple real life examples of this would be a music group suddenly surging into popularity, or a stock market crash like the one in 1929. I modeled this using a simple but expandable threshold between two behaviors, A and B (default) . That is, if a node has enough neighbors who have adopted behavior A such that the ratio of A/(A+B) is greater than the ratio, then the node will adopt behavior A. This spread proceeds in incremental steps so that a general speed can be determined.


# Data
This folder contains the data sets I worked with and each represents an undirected graph. The files contains two columns of integers that represent the start-end relationship between two nodes. The first column is the start node and the second is the end node.

* Note the data sets do not belong to me, nor did I generate them. They were provided for us by the University of California San Diego (UCSD).


# Graph
This folder contains the Graph* interface, CapstoneGraph and CapstoneGraphAnalyzer classes. CapstoneGraph stores the graph once it is loaded and performs the methods that model the information cascade. CapstoneGraphAnalyzer is passed information from CapstoneGraph as it models to determine the nodes best suited to more completely spread information.

* Note I did not create the Graph interface. This file was provided to us by UCSD, and I did not make any modifications to it nor do I take any credit for it.


# Loader
This folder contains the GraphLoader class used by CapstoneGraph to create the graph objects from the .txt files in the Data folder.

* Note I did not create the GraphLoader class. This filie was provided to us by UCSD, and I did not make any modifications to it nor do I take any credit for it.
