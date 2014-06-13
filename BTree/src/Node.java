public class Node<T extends Comparable<T>> {
	private T elem;
	private Pointer<T> p;
	
	public Node(T elem, Pointer<T> p)
	{
		if(elem == null || p == null)
			throw new IllegalArgumentException("Argument null");
		this.elem = elem;
		this.p = p;
	}
	
	public Node(T elem)
	{
		if(elem == null)
			throw new IllegalArgumentException("Argument null");
		this.elem = elem;
		this.p = new Pointer<>();
	}

	public T getElem() {
		return elem;
	}

	public void setElem(T elem) {
		this.elem = elem;
	}

	public Pointer<T> getP() {
		return p;
	}

	public void setP(Pointer<T> p) {
		this.p = p;
	}
	
}
