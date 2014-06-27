
public class Father<T extends Comparable<T>> {
	
	private Node<T> fatherNode;
	private BTree<T> fatherTree;
	
	public Father(Node<T> fatherNode, BTree<T> fatherTree)
	{
		this.fatherNode = fatherNode;
		this.fatherTree = fatherTree;
	}
	
	public Father()
	{
		this.fatherTree = null;
		this.fatherNode = null;
	}

	public Node<T> getFatherNode() {
		return fatherNode;
	}

	public void setFatherNode(Node<T> fatherNode) {
		this.fatherNode = fatherNode;
	}

	public BTree<T> getFatherTree() {
		return fatherTree;
	}

	public void setFatherTree(BTree<T> fatherTree) {
		this.fatherTree = fatherTree;
	}

	
}
