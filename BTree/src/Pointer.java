public class Pointer<T extends Comparable<T>> {
	private BTree<T> son;
	private Node<T> next;
	
	public Pointer(BTree<T> son, Node<T> next)
	{
		this.son = son;
		this.next = next;
	}
	
	public Pointer()
	{
		this.next = null;
		this.son = null;
	}
	
	public Pointer(Node<T> next)
	{
		this.next = next;
		this.son = null;
	}

	public BTree<T> getSon() {
		return son;
	}

	public void setSon(BTree<T> son) {
		this.son = son;
	}

	public Node<T> getNext() {
		return next;
	}

	public void setNext(Node<T> next) {
		this.next = next;
	}
	
	public boolean hasNext()
	{
		return next != null;
	}
}
