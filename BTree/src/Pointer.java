public class Pointer<T extends Comparable<T>> {
	private BTree<T> son;
	private Node<T> next;
	private Pointer<T> precursor;
	
	public Pointer(BTree<T> son, Node<T> next, Pointer<T> precursor)
	{
		this.son = son;
		this.next = next;
		this.precursor = precursor;
	}
	
	public Pointer()
	{
		this.next = null;
		this.son = null;
		this.precursor = null;
	}
	
	public Pointer(Node<T> next, Pointer<T> precursor)
	{
		this.next = next;
		this.son = null;
		this.precursor = precursor;
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
	
	public boolean hasPrecursor()
	{
		return precursor != null;
	}

	public Pointer<T> getPrecursor() {
		return precursor;
	}

	public void setPrecursor(Pointer<T> precursor) {
		this.precursor = precursor;
	}
	
	
}
