package Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CapstoneGraphAnalyzer {
	private HashMap<Integer,HashMap<Integer,Integer>> ANALYZED_GRAPH;
	private HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> STEP_OF_CONVERSION;
	public double projTime;
	public BestNodes bn;
	
	public CapstoneGraphAnalyzer() {
		ANALYZED_GRAPH = new HashMap<Integer,HashMap<Integer,Integer>>();
		STEP_OF_CONVERSION = new HashMap<Integer,HashMap<Integer, ArrayList<Integer>>>();
		projTime = 0;
		bn = new BestNodes();
	}
	
	/*
	 * Slightly faster version of analyzeGraph(). Instead of disassembling CAP_GRAPH to determine
	 * the least work iteration, each iteration is passed in along with a starting node id.
	 * This skips the first for loop of analyzeGraph(). 
	 */
	public void analyzeGraph(int id, HashMap<Integer,Integer> singleIteration) {
		
		if(!ANALYZED_GRAPH.containsKey(id))
			ANALYZED_GRAPH.put(id, singleIteration);
		
		if(singleIteration.size() > ANALYZED_GRAPH.get(id).size())
			ANALYZED_GRAPH.put(id, singleIteration);
			
		organizeStepConverted(id,singleIteration);
	}
	
	
	/*
	 * Organizes nodes converted based on what step they were converted on. This allows for a
	 * chronological representation of information flow throughout the graph.
	 */
	private void organizeStepConverted(int node, HashMap<Integer,Integer> curMap) {
		HashMap<Integer,ArrayList<Integer>> organizedNodeList = new HashMap<Integer,ArrayList<Integer>>();
		
		for(int j : curMap.keySet()) {
			ArrayList<Integer> nodeList = organizedNodeList.get(curMap.get(j));
			if(nodeList == null)
				nodeList = new ArrayList<Integer>();
			
			nodeList.add(j);
			Collections.sort(nodeList);
			organizedNodeList.put(curMap.get(j), nodeList);
		}
		
		STEP_OF_CONVERSION.put(node, organizedNodeList);
	}
	
	public void printAnalyzed(int j, int size) {
		
		HashMap<Integer,ArrayList<Integer>> convertedNodes = STEP_OF_CONVERSION.get(j);
		double numConverted = ANALYZED_GRAPH.get(j).size();	
		double percentConverted = numConverted/size*100;
		bn.check(j, convertedNodes.get(0).size(), percentConverted, STEP_OF_CONVERSION.get(j).size());
		
		//Uncomment lines that involve the variable 'output' to see more detail about the best iterations
		//String output = "";
		//output += "\n" + "Initial node: " + j;
		//output += ", converted " + String.format("%.2f", percentConverted) + "%";
		//output += "        " + "\t" + STEP_OF_CONVERSION.get(j);
		//System.out.println(output);
		//System.out.println("Note that the above node lists are organized based on what step they were converted!");
	}
	
	public void clear() {
		ANALYZED_GRAPH.clear();
		STEP_OF_CONVERSION.clear();
		bn = new BestNodes();
	}
	
	public void print() {
		System.out.println(bn.print());
	}
	
	
	//Helper class to keep track of starter nodes from printAnalyzed() with the highest percent conversion 
	public class BestNodes{
		ArrayList<Integer> starterNodes;
		ArrayList<Integer> numNeighborNodes;
		ArrayList<Integer> stepList;
		double percent;
		
		public BestNodes() {
			starterNodes = new ArrayList<Integer>();
			numNeighborNodes = new ArrayList<Integer>();
			stepList = new ArrayList<Integer>();
			percent = 0.0;
		}
		
		public void check(int start, int numNeighbors, double percentConverted, int step) {
			if(percent > percentConverted)
				return;
			
			if(percent == percentConverted) {
				starterNodes.add(start);
				numNeighborNodes.add(numNeighbors - 1);
				stepList.add(step);
			}
			
			else {
				starterNodes.clear();
				starterNodes.add(start);
				
				numNeighborNodes.clear();
				numNeighborNodes.add(numNeighbors - 1);
				
				stepList.clear();
				stepList.add(step);
				
				percent = percentConverted;
			}
		}
		
		public String print() {
			String output = "";
			
			if(starterNodes.size() == 1) {
				output += "The best node to convert is " + starterNodes.get(0) + " with " + numNeighborNodes.get(0) + " additional initial converted neighbors, converting "
					+ String.format("%.2f", percent) + "% after " + stepList.get(0) + " steps.";
			}
			if(starterNodes.size() > 1) {
				output += "The best conversion of " + String.format("%.2f", percent) + "% is achieved with " + starterNodes.size() + " unique starting nodes:";
				for(int j = 0; j < starterNodes.size(); j++) {
					output += "\n" + "\t" + "Node " + starterNodes.get(j) + " with " + numNeighborNodes.get(j);
					output += " additional initial converted neighbors after " + stepList.get(j) + " steps.";
				}
			}
			return output;
		}
		
	}
}
