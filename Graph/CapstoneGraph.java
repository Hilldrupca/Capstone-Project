package Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

import Loader.GraphLoader;

public class CapstoneGraph implements Graph{
	public HashMap<Integer,HashSet<Integer>> SOCIAL_NETWORK_GRAPH;
	private HashMap<Integer,Integer> ADOPTED_STATE;
	private LinkedList<Integer> NEW_ADOPTED_NEIGHBOR;
	private int STEP_COUNTER;
	private double THRESHOLD;
	public CapstoneGraphAnalyzer analyzer;
	public double projTime;

	public CapstoneGraph() {
		SOCIAL_NETWORK_GRAPH = new HashMap<Integer,HashSet<Integer>>();
		ADOPTED_STATE = new HashMap<Integer,Integer>();
		NEW_ADOPTED_NEIGHBOR = new LinkedList<Integer>();
		STEP_COUNTER = 0;
		THRESHOLD = 0.0;
		analyzer = new CapstoneGraphAnalyzer();
		projTime = 0;
	}
	
	//Used to populate SOCIAL_NETWORK_GRAPH by adding only nodes it doesn't already contain
	public void addVertex(int num) {
		// TODO Auto-generated method stub
		if(SOCIAL_NETWORK_GRAPH.containsKey(num)) {
			return;
		}
		SOCIAL_NETWORK_GRAPH.put(num,new HashSet<Integer>());
	}

	//Used to associate unique edges with nodes in SOCIAL_NETWORK_GRAPH
	public void addEdge(int from, int to) {
		// TODO Auto-generated method stub
		if(!SOCIAL_NETWORK_GRAPH.containsKey(from)) {
			throw new IndexOutOfBoundsException("Map does not contain that key");
		}
		HashSet<Integer> addedEdge = SOCIAL_NETWORK_GRAPH.get(from);
		addedEdge.add(to);
		SOCIAL_NETWORK_GRAPH.put(from, addedEdge);
	}
	
	//Clears member variables. Used at the beginning of each iteration.
	private void clearIterationVariables() {
		STEP_COUNTER = 0;
		ADOPTED_STATE.clear();
		NEW_ADOPTED_NEIGHBOR.clear();
	}
	
	//Updates the minimum threshold to convert a node from behavior B to behavior A
	public void updateThreshold(double a, double b) {
		THRESHOLD = b/(a+b);
	}
	
	/*
	 * Selects and adds the initial adopters of the new behavior to ADOPTED_STATE in
	 * a breadth first search manner until the initial number of adopters has been reached.
	 * This method also initializes NEW_ADOPTED_NEIGHBOR using a helper method.
	 * @id = the starting node
	 * @numOfAdopters = additional neighbor nodes to add
	 */
	private void establishInitialAdjacentAdopters(int id, int numOfAdopters) {
		Queue<Integer> additionalAdopters = new LinkedList<Integer>();
		
		if(numOfAdopters == 1)
			ADOPTED_STATE.put(id, STEP_COUNTER);
		else{
			additionalAdopters.add(id);
			
			while(!additionalAdopters.isEmpty()) {
				Integer curr = additionalAdopters.poll();
				if(numOfAdopters == 0)
					break;
				
				if(!ADOPTED_STATE.containsKey(curr)) {
					ADOPTED_STATE.put(curr, STEP_COUNTER);
					numOfAdopters--;
					
					if(numOfAdopters > 0)
						additionalAdopters.addAll(SOCIAL_NETWORK_GRAPH.get(curr));
			
				}
			}
		}
		
		updateAdoptedNeighbors();
	}
	
	//Helper method to update NEW_ADOPTED_NEIGHBOR that contains unchanged nodes connected to changed nodes.
	private void updateAdoptedNeighbors() {
		
		
		for(int j : ADOPTED_STATE.keySet()) {
			for(int k : SOCIAL_NETWORK_GRAPH.get(j)) {
				if(!ADOPTED_STATE.containsKey(k) && !NEW_ADOPTED_NEIGHBOR.contains(k))
					NEW_ADOPTED_NEIGHBOR.add(k);
			}
		}
		
		

	}

	/*
	 * Iterates the flow of information from each node in the graph.
	 * @upperInitialAdopters = additional neighbor nodes to potentially loop over.
	 * @loop = determines whether or not to iterate possibilities from a starting node up to a starting node + upperInitialAdopters.
	 * @return = links a node id (key) to a list of iterations from 1 starting node up to upperInitialAdopters starting nodes. 
	 */	
	public HashMap<Integer,ArrayList<HashMap<Integer,Integer>>> loopAllInfoFlow(int upperInitialAdopters, boolean loop){
		HashMap<Integer,ArrayList<HashMap<Integer,Integer>>> iterationInformation = 
				new HashMap<Integer,ArrayList<HashMap<Integer,Integer>>>();
		
		for(int k : SOCIAL_NETWORK_GRAPH.keySet()) {
			
			iterationInformation.put(k,loopInformationFlow(k,upperInitialAdopters,loop));
			
		}
		return iterationInformation;
	}
	
	/*
	 * Iterates the flow of information from a beginning node and up to upperInitialAdopters initial nodes if loop == true.
	 * If loop == false, the method iterates only from the beginning node + upperInitialAdopters.
	 * @return = a list of HashMap<Integer,Integer> resulting from the iteration over a different number of starting nodes.
	 */
	private ArrayList<HashMap<Integer,Integer>> loopInformationFlow(int id, int upperInitialAdopters, boolean loop){
		ArrayList<HashMap<Integer,Integer>> nodesConverted = new ArrayList<HashMap<Integer,Integer>>();

		for(int j = 1; j <= upperInitialAdopters; j++) {
			if(loop == false)
				j = upperInitialAdopters;
			clearIterationVariables();
			establishInitialAdjacentAdopters(id, j);
			HashMap<Integer,Integer> nodesToAnalyze = singleInformationFlowIteration();
			analyzer.analyzeGraph(id, nodesToAnalyze);
			nodesConverted.add(nodesToAnalyze);
		}

		analyzer.printAnalyzed(id, SOCIAL_NETWORK_GRAPH.size());

		return nodesConverted;
	}
	
	/*
	 * Determines if the flow of information has stopped for the current step count.
	 * @return = a Hashmap<Integer,Integer> linking a node id (key) to a conversion step (value).
	 */
	private HashMap<Integer,Integer> singleInformationFlowIteration(){

		boolean continueCheck = true;
		while(continueCheck == true) {
			continueCheck = stepInformationFlow();
			if(continueCheck == true)
				updateAdoptedNeighbors();
		}

		return new HashMap<Integer,Integer>(ADOPTED_STATE);
	}
	
	//Checks whether or not a node has enough neighbors based on ADOPTED_STATE to change from behavior B to behavior A.
	private boolean stepInformationFlow() {
		boolean behaviorChange = false;
		STEP_COUNTER++;
		ArrayList<Integer> newlyAdded = new ArrayList<Integer>();

		while(!NEW_ADOPTED_NEIGHBOR.isEmpty()) {
			int j = NEW_ADOPTED_NEIGHBOR.poll();
			double a = 0; //Neighbors who have adopted new behavior
			double b = 0; //Neighbors with default behavior
			
			//Determine how many of j's neighbors have behavior a or b.
			for(int k : SOCIAL_NETWORK_GRAPH.get(j)) {
				if(ADOPTED_STATE.containsKey(k))
					a++;
				else
					b++;

			}

			behaviorChange = checkIfBehaviorChange(a,b,j);
			
			if(behaviorChange == true)
				newlyAdded.add(j);
		}

		for(int k : newlyAdded)
			ADOPTED_STATE.put(k,STEP_COUNTER);
		
		return behaviorChange;
	}
	
	/*
	 * Helper method to determine if the ratio of a nodes neighbors that have changed to behavior A
	 * has exceeded the threshold for the current node to also change to behavior A.
	 */
	private boolean checkIfBehaviorChange(double a, double b, int id) {
		double ratio = a/(a+b);
		
		if(THRESHOLD <= ratio && !ADOPTED_STATE.containsKey(id)) 
			return true;
		
		else
			return false;
	}
	
	/*
	 * Tester method to determine if establishInitialAdjacentAdopters() and 
	 * updateAdoptedNeighbors() are working correctly.
	 */
	public void printAdopted() {
		System.out.println(ADOPTED_STATE.toString());
		System.out.println(NEW_ADOPTED_NEIGHBOR.toString());
	}
	
	public void printAnalyzed() {
		analyzer.print();
		analyzer.clear();
	}
	
	//Inherited method stub (not used)
	public Graph getEgonet(int x) {
		return new CapstoneGraph();
	}
	
	//Inherited method stub (not used)
	public LinkedList<Graph> getSCCs(){
		return new LinkedList<Graph>();
	}
	
	//Inherited method stub (not used)
	public HashMap<Integer,HashSet<Integer>> exportGraph() {
		return new HashMap<Integer,HashSet<Integer>>();
	}
	
	public static void main(String[] args) {
		CapstoneGraph graph = new CapstoneGraph();
		//GraphLoader.loadGraph(graph, "data/small_test_graph.txt");
		//GraphLoader.loadGraph(graph, "data/facebook_1000.txt");
		GraphLoader.loadGraph(graph, "data/facebook_2000.txt");
		//GraphLoader.loadGraph(graph, "data/facebook_ucsd.txt");  //Run times will always be long with this file
		
		double increment = .25; //Percentage to increment for new behavior 
		double startingRatio = 2; //Beginning ratio of behavior A to behavior B. Keep in mind b/(a+b).
		double endRatio = 6;
		
		/*
		 * If using the ucsd facebook file, do not increase beyond 1. Run time increases dramatically
		 * even with incrementLoopInitialAdopters set to false.
		 */
		int initialAdopters = 10;
		
		/*
		 * Determines whether or not to iterate the flow of information starting with 1 adopter all the way up to initialAdopters.
		 * False will only iterate the flow of information with a max of initialAdopters. No looping. 
		 */
		boolean incrementLoopInitialAdopters = false;  //Set to false to help reduce run times

		for(double j = startingRatio; j <= endRatio; j += increment) {
			System.out.println("Current ratio of A/B is " + j);
			graph.updateThreshold(j,1);
			
			graph.loopAllInfoFlow(initialAdopters, incrementLoopInitialAdopters);
			
			graph.printAnalyzed();
			System.out.println("");
		}

	}
}
