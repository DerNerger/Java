
public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

	private T elem;
	private Node<T> next;
	private Node<T> prev;
	private BTree<T> child;
	
	public Node(T elem)
	{
		this.elem = elem;
	}
	
	public Node()
	{
		this.elem = null;
	}
	
	public boolean isLast()
	{
		return elem == null;
	}

	@Override
	public int compareTo(Node<T> o) {
		return elem.compareTo(o.elem);
	}

	public Node<T> getNext()
	{
		return next;
	}
	
	public void setNext(Node<T> next)
	{
		if(this.next != null && this.next.prev == this)
			this.next.prev = null;
		if(next != null)
			next.prev = this;
		this.next = next;
	}
	
	public void setChild(BTree<T> child)
	{
		if(this.child != null && this.child.getFather().getFatherNode() == this)
			this.child.getFather().setFatherNode(null);
		if(child != null)
			child.getFather().setFatherNode(this);
		this.child = child;
	}

	public T getElem() {
		return elem;
	}

	public Node<T> getPrev() {
		return prev;
	}

	public BTree<T> getChild() {
		return child;
	}
	

	public void setElem(T elem) {
		this.elem = elem;
	}

	public String toString()
	{
		if(this.isLast())
			return ";";
		String back = elem.toString();
		if(!next.isLast())
			back += ", "+next.toString();
		return back;
	}
}
