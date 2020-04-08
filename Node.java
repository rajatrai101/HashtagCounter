class Node {
	int degree = 0; // number of children of the node
	int count; // the hashtag’s count
	Node next; // right neighbour reference
	Node prev; // left neighbour reference
	Node child; // reference to the chid node
	String key; // the hashtag
	Node parent; // reference to the parent node
	boolean childCut = false; // child cut Boolean

	/**
	 * Creates a new node with the passed keyValue and the keyCount values.
	 * 
	 * @param keyValue The keyVal is the value of the string associated with the
	 *                 node.
	 * @param keyCount The count stored in the node.
	 */
	Node(String keyValue, int keyCount) {
		next = prev = this;
		parent = child = null;
		count = keyCount;
		key = keyValue;
	}
}