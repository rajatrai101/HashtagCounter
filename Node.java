/*
* @authon rajatrai101
*/

class Node {
	int deg = 0; // number of childer of the node
	int count; // the hashtag’s count
	Node next; // right neighbour reference
	Node prev; // right neighbour reference
	Node child; // reference to the chid node
	String key; // the hashtag
	Node parent; // reference to the parent node
	boolean childCut = false; // child cut Boolean

	/**
	 * Constructs a new Fibbonacci heap node with the keyValue and the keyCount
	 * value specified.
	 * 
	 * @param keyValue The keyVal is the value of the string associated with this
	 *                 node.
	 * @param keyCount The hash tag count stored in this node.
	 */
	Node(String keyValue, int keyCount) {
		next = prev = this;
		count = keyCount;
		key = keyValue;
	}
}