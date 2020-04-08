import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class implementation of the customized Max Fibonacci Heap with a hash map
 * data structure
 */
public class MaxFibonacciHeap {
	// Number of nodes in the Max Fibonacci heap.
	private int size = 0;
	// Maximum node in the Max Fibonacci heap.
	private Node max = null;
	// Name of the output file. Null if no output file name is provided
	private String outputFileName;
	// Hashmap for fast lookup of keys and their associated nodes
	private HashMap<String, Node> hashMap;

	/**
	 * Constructs an empty Max Fibonacci Heap and initializes a new HashMap and
	 * output file name with the one passed in as parameter.
	 * 
	 * @param filename Name of the output file or null if none is specified
	 */
	public MaxFibonacciHeap(String filename) {
		outputFileName = filename;
		hashMap = new HashMap<>();
	}

	/**
	 * Inserts nodes into the Max Fibonacci heap.
	 * 
	 * @param key   String to be inserted in the Max Fibonacci Heap
	 * @param value Count of the key to be inserted
	 */
	public void insert(String key, int value) {
		Node newNode = new Node(key, value);
		max = union(newNode, max);
		size++;
		hashMap.put(key, newNode);
	}

	/**
	 * Returns whether the heap is empty.
	 * 
	 * @return Returns TRUE if the heap is empty and FALSE otherwise.
	 */
	public boolean isEmpty() {
		return max == null && size == 0;
	}

	/**
	 * Returns whether the heap has a Node entry with a string Keyword.
	 * 
	 * @param keyword Keyword to be checked for in the heap
	 * @return Returns TRUE if the heap has a Node with the provided keyword and
	 *         FALSE otherwise.
	 */
	public boolean hasKey(String keyword) {
		return hashMap.containsKey(keyword);
	}

	/**
	 * Increases the key of the specified keyword by the value passed.
	 * 
	 * @param keyword The keyword whose count has to be increased.
	 * @param count   The number by which the count has to be incremented
	 */
	public void increaseKey(String keyword, int count) {
		increaseKey(hashMap.get(keyword), count);
	}

	/**
	 * Returns an Entry object corresponding to the maximum element of the Fibonacci
	 * heap, throwing a NoSuchElementException if the heap is empty.
	 *
	 * @return The largest element of the heap.
	 * @throws NoSuchElementException If the heap is empty.
	 */
	public Node max() {
		if (isEmpty())
			throw new NoSuchElementException("No element in the Heap.");
		return max;
	}

	/**
	 * Removes and returns the maximum element of the Fibonacci heap. If the heap is
	 * empty, this throws a NoSuchElementException.
	 *
	 * @return The largest element of the Fibonacci heap.
	 * @throws NoSuchElementException If the heap is empty.
	 */
	public Node removeMax() {
		// Check if the heap is empty
		if (isEmpty())
			throw new NoSuchElementException("No element in the Heap.");
		// Take the max node
		Node maxNode = max;

		// Decrease the size of the heap and remove the key from the hashmap
		size--;
		hashMap.remove(maxNode.key);

		// Remove the max node from the top level circular linked list
		if (max.next == max) // If there is only one node in the Max Fibonacci Heap
			max = null;
		else { // If there are more than one node in the Max Fibonacci Heap
			max.next.prev = max.prev;
			max.prev.next = max.next;
			max = max.prev;
		}

		// Mark all children's (if any) of the max node to have no parent
		if (maxNode.child != null) {
			Node curr = maxNode.child;
			do {
				curr.parent = null;
				curr = curr.next;
			} while (curr != maxNode.child);
		}

		// Add all children's of max to the top level circular linked list
		max = union(max, maxNode.child);

		// If the heap is not empty after removing max node,do pairwise combining
		if (!isEmpty())
			pairwiseCombine();

		return maxNode;
	}

	/**
	 * Function to perform pairwise combine operation i.e. degree wise merging of
	 * fibonacci heaps using degree table after removeMax operation
	 */
	private void pairwiseCombine() {
		// Degree table to store the references of Nodes with corresponding degrees
		ArrayList<Node> degreeTable = new ArrayList<>();
		// List of nodes in the top level circular linked list to run pairwise combine
		ArrayList<Node> nodeList = new ArrayList<>();

		// Add the nodes in the top level list to the nodeList
		Node ptr = max;
		do {
			nodeList.add(ptr);
			ptr = ptr.next;
		} while (!Objects.equals(ptr, max));

		// start pairwise merging
		nodeList.forEach(node -> {
			while (true) {
				// Create the degree table fo the appropriate size
				while (degreeTable.size() <= node.degree) {
					degreeTable.add(null);
				}

				// Add the current node to the degree table if no node of the same degree
				// discovered yet. If yes then start again
				if (degreeTable.get(node.degree) == null) {
					degreeTable.set(node.degree, node);
					break;
				}
				// If matching degree node is present then combine the current node and the
				// match node
				Node matchNode = degreeTable.get(node.degree);
				degreeTable.set(node.degree, null);

				// Find the node with the max count of the two
				Node minNode, maxNode;
				minNode = matchNode.count < node.count ? matchNode : node;
				maxNode = minNode == matchNode ? node : matchNode;

				// Remove the node with the lesser count from the top level circular linked list
				minNode.next.prev = minNode.prev;
				minNode.prev.next = minNode.next;
				minNode.next = minNode.prev = minNode;

				// Merge the min node with the children's of the max node and make max it's
				// parent
				maxNode.child = union(maxNode.child, minNode);
				minNode.parent = maxNode;

				// Reset the child cut value of the min node
				minNode.childCut = false;
				// Increase the degree of the max node due to the new child node
				maxNode.degree++;
				// set the max Node as the current node
				node = maxNode;
			}
			// update the global max node
			max = node.count >= max.count ? node : max;
		});
	}

	/**
	 * Increases the count of the specified element by the value passed.
	 * 
	 * @param node  The Node whose count had be increased.
	 * @param count The number by which the count has to be incremented
	 * 
	 */
	public void increaseKey(Node node, int count) {
		node.count += count;
		if (node.parent != null && node.count >= node.parent.count)
			cascadingCut(node);
		if (node.count >= max.count)
			max = node;
	}

	/**
	 * Removes the specified Node from the Max Fibonacci heap if it contains it. If
	 * the Node is not present in the heap, this function throws an
	 * NoSuchElementException.
	 * 
	 * @param node The node to be removed.
	 */
	public void remove(Node node) {
		if (hashMap.containsKey(node.key)) {
			increaseKey(node, Integer.MAX_VALUE);
			Node max = removeMax();
			hashMap.remove(max.key);
		} else {
			throw new NoSuchElementException("No such element found in the heap");
		}

	}

	/**
	 * Prints the specified number of maximum elements in the output file/stdout. If
	 * the output file is not present, this function throws an IOException.
	 * 
	 * @param n The number of maximum counts to be printed.
	 */
	public void printNHighest(int n) throws IOException {
		// List to maintain the the output nodes
		List<Node> outputNodes = new ArrayList<>();
		// Build the output
		StringBuilder outputLine = new StringBuilder();
		while (n-- > 0) {
			Node currMax = removeMax();
			outputLine.append(currMax.key);
			if (n > 0)
				outputLine.append(",");
			outputNodes.add(currMax);
		}
		// check if to output in file or standard output
		if (outputFileName != null) {
			FileWriter writer = new FileWriter(new File(outputFileName), true);
			PrintWriter pw = new PrintWriter(writer);
			pw.println(outputLine);
			pw.close();
		} else
			System.out.println(outputLine);

		// Insert the nodes back in our Max Fibonacci Heap
		outputNodes.forEach(node -> insert(node.key, node.count));
		// clear the output nodes
		outputNodes.clear();
	}

	/**
	 * Merges the two disjoint circular linked lists together into one circular
	 * linked list. This function compares the counts of both the nodes of the
	 * linked list and returns the one which is larger to be saved as the max node.
	 * 
	 * @param listA A pointer to the first circular linked lists.
	 * @param listB A pointer to the other circular linked lists.
	 * @return A pointer to the larger element of the two elements.
	 */
	private Node union(Node listA, Node listB) {
		if (listA == null && listB == null) // if both the lists are empty
			return null;
		if (listA != null && listB == null)// if either one of the lists is empty, return the second one
			return listA;
		if (listA == null)
			return listB;
		// Interlink nodes
		Node temp = listA.next;
		listA.next = listB.next;
		listA.next.prev = listA;
		listB.next = temp;
		listB.next.prev = listB;
		return listA.count > listB.count ? listA : listB;
	}

	/**
	 * Cuts a node from its parent. If the parent was already marked, recursively
	 * cuts that node from its parent as well. Also resets the childcut value for
	 * the nodes encountered.
	 * 
	 * @param node Node to run cascading cut
	 */
	private void cascadingCut(Node node) {
		node.childCut = false;// reset the childcut of the node
		Node parent = node.parent;// node to store the parent node
		if (null != parent) {// if the node is a top level node
			node.parent = null; // break uplink with the parent node

			if (parent.degree > 1) {// if there are more than one children, remove the node from the circular list
				node.next.prev = node.prev;
				node.prev.next = node.next;
			}

			if (parent.child == node) // if parent has the node as the first child, update it
				parent.child = parent.degree > 1 ? node.next : null;

			parent.degree--;// decrease the degree

			node.next = node.prev = node; // isolate the node
			max = union(max, node);// merge it to the top level circular linked list

			if (parent.childCut)// check is move up
				cascadingCut(parent);
			else// mark child cut
				parent.childCut = true;

		}
	}
}