import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

class FibonacciHeap {

	private int size = 0;
	private Node max = null;
	private String outputFilename;
	private HashMap<String, Node> hashMap;

	public FibonacciHeap(String filename) {
		outputFilename = filename;
		hashMap = new HashMap<String, Node>();
	}

	public void insert(String key, int value) {
		Node newNode = new Node(key, value);
		max = meld(newNode, max);
		++size;
		hashMap.put(key, newNode);
	}

	public boolean isEmpty() {
		return max == null && size == 0;
	}

	public boolean hasKey(String keyword) {
		return hashMap.containsKey(keyword);
	}

	public void increaseKey(String keyword, int count) {
		increaseKey(hashMap.get(keyword), count);
	}

	public Node max() {
		if (isEmpty())
			throw new NoSuchElementException("Heap is empty.");
		return max;
	}

	public Node removeMax() {
		if (isEmpty())
			throw new NoSuchElementException("Heap is empty.");

		Node maxElem = max;

		--size;
		hashMap.remove(maxElem.key);

		if (max.next == max)
			max = null;
		else {
			max.prev.next = max.next;
			max.next.prev = max.prev;
			max = max.prev;
		}

		if (maxElem.child != null) {
			Node curr = maxElem.child;
			do {
				curr.parent = null;
				curr = curr.next;
			} while (curr != maxElem.child);
		}

		max = meld(max, maxElem.child);

		if (max == null)
			return maxElem;

		List<Node> degreeTable = new ArrayList<Node>();

		List<Node> nodeList = new ArrayList<Node>();

		for (Node curr = max; nodeList.isEmpty() || nodeList.get(0) != curr; curr = curr.next)
			nodeList.add(curr);

		// for (Node curr : nodeList) {
		nodeList.forEach(curr -> {
			while (true) {
				while (curr.deg >= degreeTable.size()) {
					degreeTable.add(null);
				}

				if (degreeTable.get(curr.deg) == null) {
					degreeTable.set(curr.deg, curr);
					break;
				}

				Node match = degreeTable.get(curr.deg);
				degreeTable.set(curr.deg, null);

				Node min = match.count < curr.count ? match : curr;
				Node max = min == match ? curr : match;

				min.next.prev = min.prev;
				min.prev.next = min.next;

				min.next = min.prev = min;
				max.child = meld(max.child, min);
				min.parent = max;

				min.childCut = false;
				++max.deg;
				curr = max;
			}
			max = curr.count >= max.count ? curr : max;
		});

		return maxElem;
	}

	public void increaseKey(Node heapNode, int count) {
		heapNode.count += count;
		if (heapNode.parent != null && heapNode.count >= heapNode.parent.count)
			childCut(heapNode);
		if (heapNode.count >= max.count)
			max = heapNode;
	}

	public void remove(Node heapNode) {
		if (hashMap.containsKey(heapNode.key)) {
			increaseKey(heapNode, Integer.MAX_VALUE);
			Node max = removeMax();
			hashMap.remove(max.key);
		} else
			throw new NoSuchElementException("No such element found in the heap");

	}

	public void printNMaxes(int n) throws IOException {
		List<Node> outputNodes = new LinkedList<Node>();
		String outputLine = "";
		while (n-- > 0) {
			Node currMax = removeMax();
			outputLine += currMax.key;
			if (n > 0)
				outputLine += ",";
			outputNodes.add(currMax);
		}

		outputNodes.forEach(node -> insert(node.key, node.count));
		outputNodes.clear();

		if (outputFilename != null) {
			FileWriter writer = new FileWriter(new File(outputFilename), true);
			PrintWriter pw = new PrintWriter(writer);
			pw.println(outputLine);
			pw.close();
		} else
			System.out.println(outputLine);
	}

	private Node meld(Node p, Node q) {
		if (p == null && q == null)
			return null;
		else if (p != null && q == null)
			return p;
		else if (p == null && q != null)
			return q;
		else {
			Node pNext = p.next;
			p.next = q.next;
			p.next.prev = p;
			q.next = pNext;
			q.next.prev = q;
			return p.count > q.count ? p : q;
		}
	}

	private void childCut(Node heapNode) {
		heapNode.childCut = false;

		if (heapNode.parent == null)
			return;

		if (heapNode.next != heapNode) {
			heapNode.next.prev = heapNode.prev;
			heapNode.prev.next = heapNode.next;
		}

		if (heapNode.parent.child == heapNode)
			if (heapNode.next != heapNode)
				heapNode.parent.child = heapNode.next;
			else
				heapNode.parent.child = null;

		heapNode.parent.deg--;

		heapNode.next = heapNode.prev = heapNode;
		max = meld(max, heapNode);

		if (heapNode.parent.childCut)
			childCut(heapNode.parent);
		else
			heapNode.parent.childCut = true;

		heapNode.parent = null;
	}
}