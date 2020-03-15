/*
* @authon rajatrai101
*/

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

class Node {
	int deg = 0;
	int data;
	Node next;
	Node prev;
	Node child;
	String key;
	Node parent;
	boolean childCutVal = false;

	/**
	 * Constructs a new Node with the data/value specified by the elem
	 * 
	 * @param keyValue The keyVal is the value of the string associated with this
	 *                 node.
	 * @param count    The hash tag count stored in this node.
	 */
	Node(String keyValue, int count) {
		next = prev = this;
		data = count;
		key = keyValue;
	}
}

class fibonacciHeap {
	Queue<Node> queueA = new LinkedList<Node>();
	/* Pointer to the maximum element in the heap. */
	Node max = null;
	HashMap<String, Node> hashMap = new HashMap<String, Node>();
	/* Heap size is maintained explicitly to avoid recomputation. */
	int size = 0;

	/**
	 * Inserts a node into the Fibonacci heap with the specified value and key
	 *
	 * @param key   The associated hashtag/key
	 * @param value The value to insert.
	 * @return An Node representing that element in the heap.
	 */
	public Node insert(String key, int value) {
		Node result = new Node(key, value);

		/* Merge this singleton list with the tree list. */
		max = combineLists(max, result);

		/* Increase the heap size */
		++size;

		return result;
	}

	/**
	 * Returns whether the heap is empty.
	 *
	 * @return Whether the heap is empty.
	 */
	public boolean isEmpty() {
		return max == null;
	}

	/**
	 * Returns the max value of the heap
	 * 
	 * @return The largest element of the heap.
	 * @throws NoSuchElementException If the heap is empty.
	 */
	public Node max() {
		if (isEmpty())
			throw new NoSuchElementException("Heap is empty.");
		return max;
	}

	/**
	 * Dequeues and returns the maximum element of the Fibonacci heap. If the heap
	 * is empty, this throws a NoSuchElementException.
	 *
	 * @return The largest element of the Fibonacci heap.
	 * @throws NoSuchElementException If the heap is empty.
	 */
	public Node removeMax() {
		if (isEmpty()) {
			throw new NoSuchElementException("Heap is empty.");
		}
		--size;
		/* Maximum element that is to be removed */
		Node maxElem = max;

		/*
		 * Remove the max elem from the root list if max elem is the only elem in the
		 * root list, make max null else, arbitrarily reassign max to the node next to
		 * max
		 */
		if (max.next == max) {
			max = null;
		} else {
			max.prev.next = max.next;
			max.next.prev = max.prev;
			max = max.next;
		}

		/* Reassign the parent field of the children of the removed max to null */
		if (maxElem.child != null) {
			Node curr = maxElem.child;
			do {
				curr.parent = null;
				curr = curr.next;
			} while (curr != maxElem.child);
		}

		/* move the children of the old max to the root list */
		max = combineLists(max, maxElem.child);

		if (max == null)// If the list becomes empty, return the max and we are done
			return maxElem;

		/*
		 * Treetable that keeps track of the dgree of the subtrees during pairwise
		 * combine
		 */
		List<Node> treeTab = new ArrayList<Node>();

		/* List of nodes to visit during the traversal */
		List<Node> nodeToVisit = new ArrayList<Node>();

		for (Node curr = max; nodeToVisit.isEmpty() || nodeToVisit.get(0) != curr; curr = curr.next)
			nodeToVisit.add(curr);

		for (Node curr : nodeToVisit) {
			while (true) {
				while (curr.deg >= treeTab.size()) {
					treeTab.add(null);
				}

				/* Keep traversing until two trees of the same degree are found */
				if (treeTab.get(curr.deg) == null) {
					treeTab.set(curr.deg, curr);
					break;
				}

				Node other = treeTab.get(curr.deg);
				treeTab.set(curr.deg, null); // Clear the old slot

				Node min = (other.data < curr.data) ? other : curr;
				Node max = (other.data < curr.data) ? curr : other;

				/* Remove the minimum element from the list */
				min.next.prev = min.prev;
				min.prev.next = min.next;

				/* Make the min child of max by pairwise combine */
				min.next = min.prev = min;
				max.child = combineLists(max.child, min);
				min.parent = max;

				min.childCutVal = false;
				++max.deg;
				curr = max;
			}
			// Update the max element
			if (curr.data >= max.data) {
				max = curr;
			}
		}
		return maxElem;
	}

	/**
	 * Increases the key of a node by the specified value
	 *
	 * @param heapNode The element whose data should be increased.
	 * @param addVal   The value by which the Node's value needs to be increased
	 */
	public void increaseKey(Node heapNode, int addVal) {
		/* Increase the node's value by addVal */
		heapNode.data += addVal;

		/*
		 * If the node's new value is greater than that of it's parent then cut the node
		 * and insert it into the root list
		 */
		if (heapNode.parent != null && heapNode.data >= heapNode.parent.data) {
			childCut(heapNode);
		}

		/*
		 * If the new value of the node is greater than the max node's value then the
		 * max node is pointed to the current node with the increased value
		 */
		if (heapNode.data >= max.data) {
			max = heapNode;
		}
	}

	/**
     * Deletes this Node from the Fibonacci heap that contains it.
     *
     * It is assumed that the heapNode belongs in this heap if it is present in the hashmap/fibbonacci heap
     *
     * @param heapNode The entry to delete.
     */
    public void deleteKey(Node heapNode) {
        /* Use increaseKey to drop the entry's key to infinity.  This will
		 * guarantee that the node is cut and set to the global maximum.
		 */
		if(hashMap.containsKey(heapNode.key))
        	increaseKey(heapNode, Integer.MAX_VALUE);

        /* Call removeMax to remove it. */
        removeMax();
    }

	/**
	 * Merges two doubly linked lists in O(1)time
	 * 
	 * @param p A pointer into p of the q linked lists.
	 * @param q A pointer into the other of the q linked lists.
	 * @return A pointer to the smallest element of the resulting list.
	 */
	private static Node combineLists(Node p, Node q) {
		if (p == null && q == null) { // Both null, resulting list is null.
			return null;
		} else if (p != null && q == null) { // q is null, result is p.
			return p;
		} else if (p == null && q != null) { // p is null, result is q.
			return q;
		} else { // combine the lists if both p and q is non null
			Node pNext = p.next;
			p.next = q.next;
			p.next.prev = p;
			q.next = pNext;
			q.next.prev = q;

			/* A pointer to larger node is returned */
			return p.data > q.data ? p : q;
		}
	}

	/**
	 * Removes the first N max elements from the Fibonacci heap.
	 * 
	 * @param n The number of maxes to remove
	 * @throws IOException
	 */
	public void printNMaxes(int n) throws IOException {
		/* The output is written to the output_file.txt */
		FileWriter writer = new FileWriter(new File("output_file.txt"), true);
		PrintWriter pw = new PrintWriter(writer);
		for (int i = 0; i < n; i++) {
			/* Call removeMax n times to remove n maxes */
			Node currMax = removeMax();
			pw.write(currMax.key);
			if (i != n - 1) {
				pw.write(",");
			}
			// print(max,1);
			queueA.add(currMax);
		}
		pw.println();
		pw.flush();
		pw.close();
		while (!queueA.isEmpty()) {
			Node ins = (Node) queueA.remove();
			Node res = insert(ins.key, ins.data);
			hashMap.get(ins.key).next = res;
		}
	}

	/**
	 * Recursively cuts the marked parents of a node
	 *
	 * @param heapNode The node to cut from its parent.
	 */
	private void childCut(Node heapNode) {
		heapNode.childCutVal = false;

		if (heapNode.parent == null)
			return;

		/* remove the node from it's siblings list */
		if (heapNode.next != heapNode) {
			heapNode.next.prev = heapNode.prev;
			heapNode.prev.next = heapNode.next;
		}

		/* Change the child pointer of the parent of the cut node, if necessary */
		if (heapNode.parent.child == heapNode) {
			if (heapNode.next != heapNode) {
				heapNode.parent.child = heapNode.next;
			} else {
				heapNode.parent.child = null;
			}
		}

		--heapNode.parent.deg;

		/* Add the cut node to the root list */
		heapNode.prev = heapNode.next = heapNode;
		max = combineLists(max, heapNode);

		/*
		 * Recursively cut the parents if marked already else mark their childCut to
		 * true
		 */
		if (heapNode.parent.childCutVal)
			childCut(heapNode.parent);
		else
			heapNode.parent.childCutVal = true;

		heapNode.parent = null;
	}
}