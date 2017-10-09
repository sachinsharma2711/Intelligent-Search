package search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class IntelligentSearch {

	public static void main(String[] args) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("input.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		String algorithm = null;
		String startState = null;

		HashMap<String, ArrayList<Node>> adjacentNodes = new HashMap<String, ArrayList<Node>>();
		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
		HashMap<String, Integer> distance = new HashMap<String, Integer>();
		HashMap<String, Integer> heuristic = new HashMap<String, Integer>();

		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");

		// Read the algorithm
		try {
			algorithm = br.readLine().trim();

			// Read the start state
			startState = br.readLine().trim();
			// Read the goal state
			String goalState = br.readLine().trim();
			// Read number of live traffic lines
			int n = Integer.parseInt(br.readLine().trim());

			for (int i = 0; i < n; i++) {
				line = br.readLine().trim();
				if (line == null) {
					System.out.println("Invalid input file format");
					throw new Exception("Invalid input file format");
				}
				String[] trafficData = line.split(" ");
				String fromNode = trafficData[0];
				String toNode = trafficData[1];
				int cost = Integer.parseInt(trafficData[2]);
				ArrayList<Node> nodeList = adjacentNodes.get(trafficData[0]);
				if (nodeList == null) {
					nodeList = new ArrayList<Node>();
				}
				Node node = new Node(toNode, cost);
				nodeList.add(node);
				adjacentNodes.put(fromNode, nodeList);
			}

			// Read number of Sunday traffic Lines
			int m = Integer.parseInt(br.readLine().trim());

			for (int i = 0; i < m; i++) {
				line = br.readLine().trim();
				String[] keyValue = line.split(" ");
				heuristic.put(keyValue[0], Integer.parseInt(keyValue[1]));
			}
			if (br.readLine() == null) {
				System.out.println("End of input file");
			}
			if (algorithm.equals("BFS")) {
				Queue<String> queue = new LinkedList<String>();
				ArrayList<Node> nodeList;
				HashMap<String, String> predecessor = new HashMap<String, String>();
				String currentState = null;
				Stack<String> stack = new Stack<String>();

				queue.add(startState);
				visited.put(startState, true);
				distance.put(startState, 0);
				while (!queue.isEmpty()) {
					currentState = queue.poll();
					if (currentState.equals(goalState)) {
						break;
					}
					// expand nodes and add to the queue
					if ((nodeList = adjacentNodes.get(currentState)) != null) {
						for (Node node : nodeList) {
							String nodeState = node.getState();
							if (visited.get(nodeState) == null) {
								visited.put(nodeState, true);
								predecessor.put(nodeState, currentState);
								distance.put(nodeState, distance.get(currentState) + 1);
								queue.add(nodeState);
							}
						}
					}
				}
				while (!currentState.equals(startState)) {
					stack.push(currentState);
					currentState = predecessor.get(currentState);
				}
				writer.println(startState + " " + 0);
				while (!stack.isEmpty()) {
					String state = stack.pop();
					writer.println(state + " " + distance.get(state));
				}
				writer.close();

			} else if (algorithm.equals("DFS")) {
				Stack<String> stack = new Stack<String>();
				Deque<String> deque = new ArrayDeque<String>();
				String currentState = startState;
				ArrayList<Node> nodeList;
				HashMap<String, String> predecessor = new HashMap<String, String>();
				
				deque.addFirst(startState);
				visited.put(startState, true);
				distance.put(startState, 0);
				while (!deque.isEmpty()) {
					currentState = deque.poll();
					if (currentState.equals(goalState)) {
						break;
					}
					if ((nodeList = adjacentNodes.get(currentState)) != null) {
						Collections.reverse(nodeList);
						for (Node node : nodeList) {
							String nodeState = node.getState();
							if (visited.get(nodeState) == null) {
								visited.put(nodeState, true);
								predecessor.put(nodeState, currentState);
								distance.put(nodeState, distance.get(currentState) + 1);
								deque.addFirst(nodeState);
							}
						}
					}
				}
				while (!currentState.equals(startState)) {
					stack.push(currentState);
					currentState = predecessor.get(currentState);
				}
				writer.println(startState + " " + 0);
				while (!stack.isEmpty()) {
					String state = stack.pop();
					writer.println(state + " " + distance.get(state));
				}
				writer.close();

			} else if (algorithm.equals("UCS")) {
				Comparator<Node> comparator = new NodeComparator();
				PriorityQueue<Node> queue = new PriorityQueue<Node>(10, comparator);
				ArrayList<Node> nodeList;
				HashMap<String, String> predecessor = new HashMap<String, String>();
				Node currentNode = null;
				String currentState = "";
				Stack<String> stack = new Stack<String>();

				Node startNode = new Node(startState, 0);
				queue.add(startNode);
				visited.put(startState, true);
				distance.put(startState, 0);
				while (!queue.isEmpty()) {
					currentNode = queue.poll();
					currentState = currentNode.getState();
					if (currentState.equals(goalState)) {
						break;
					}
					// expand nodes and add to the queue
					if ((nodeList = adjacentNodes.get(currentState)) != null) {
						for (Node node : nodeList) {
							String nodeState = node.getState();
							if (visited.get(nodeState) == null) {
								visited.put(nodeState, true);
								predecessor.put(nodeState, currentState);
								int nodeCost = distance.get(currentState) + node.getCost();
								distance.put(nodeState, nodeCost);
								queue.add(new Node(nodeState, nodeCost));
							} else if ((distance.get(currentState) + node.getCost()) < distance.get(node.getState())) {
								predecessor.put(nodeState, currentState);
								distance.put(nodeState, distance.get(currentState) + node.getCost());
								boolean nodeInQueue = false;
								for (Node p : queue) {
									if (p.getState().equals(nodeState)) {
										nodeInQueue = true;
										queue.remove(p);
										Node newNode = new Node(nodeState, distance.get(nodeState));
										queue.add(newNode);
										break;
									}
								}
								if (!nodeInQueue) {
									Node newNode = new Node(nodeState, distance.get(nodeState));
									queue.add(newNode);
								}
							}
						}
					}
				}
				while (!currentState.equals(startState)) {
					stack.push(currentState);
					currentState = predecessor.get(currentState);
				}
				writer.println(startState + " " + 0);
				while (!stack.isEmpty()) {
					String state = stack.pop();
					writer.println(state + " " + distance.get(state));
				}
				writer.close();

			} else if (algorithm.equals("A*")) {

				Comparator<Node> comparator = new NodeComparator();
				PriorityQueue<Node> queue = new PriorityQueue<Node>(10, comparator);
				ArrayList<Node> nodeList;
				HashMap<String, String> predecessor = new HashMap<String, String>();
				Node currentNode = null;
				String currentState = "";
				Stack<String> stack = new Stack<String>();

				Node startNode = new Node(startState, 0);
				queue.add(startNode);
				visited.put(startState, true);
				distance.put(startState, 0);
				while (!queue.isEmpty()) {
					currentNode = queue.poll();
					currentState = currentNode.getState();
					if (currentState.equals(goalState)) {
						break;
					}
					// expand nodes and add to the queue
					if ((nodeList = adjacentNodes.get(currentState)) != null) {
						for (Node node : nodeList) {
							String nodeState = node.getState();
							if (visited.get(nodeState) == null) {
								visited.put(nodeState, true);
								predecessor.put(nodeState, currentState);
								int nodeCost = distance.get(currentState) + node.getCost();
								distance.put(nodeState, nodeCost);
								queue.add(new Node(nodeState, nodeCost + heuristic.get(nodeState)));
							} else if ((distance.get(currentState) + node.getCost()) < distance.get(node.getState())) {
								predecessor.put(nodeState, currentState);
								distance.put(nodeState, distance.get(currentState) + node.getCost());
								boolean nodeInQueue = false;
								for (Node p : queue) {
									if (p.getState().equals(nodeState)) {
										nodeInQueue = true;
										queue.remove(p);
										Node newNode = new Node(nodeState, distance.get(nodeState) + heuristic.get(nodeState));
										queue.add(newNode);
										break;
									}
								}
								if (!nodeInQueue) {
									Node newNode = new Node(nodeState, distance.get(nodeState) + heuristic.get(nodeState));
									queue.add(newNode);
								}
							}
						}
					}
				}
				while (!currentState.equals(startState)) {
					stack.push(currentState);
					currentState = predecessor.get(currentState);
				}
				writer.println(startState + " " + 0);
				while (!stack.isEmpty()) {
					String state = stack.pop();
					writer.println(state + " " + distance.get(state));
				}
				writer.close();

			} else {
				System.out.println("Algorithm not recognized");
			}
		} catch (IOException e) {

		}
	}
}

class Node {
	private String state;
	private int cost;

	public Node(String state, int cost) {
		this.state = state;
		this.cost = cost;
	}

	public String getState() {
		return state;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
