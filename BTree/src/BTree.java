import java.util.LinkedList;
import java.util.Queue;

public class BTree<T extends Comparable<T>> {

	private int degree;
	private int currentCount;
	private boolean isLeaf;
	private Pointer<T> left;
	private Pointer<T> fatherPointer;
	private BTree<T> fatherTree;
	
	public BTree(int degree)
	{
		this.degree = degree;
		currentCount = 0;
		isLeaf = true;
		fatherPointer = null;
		fatherTree = null;
		left = new Pointer<>();
	}
	
	public BTree(int degree, Pointer<T> left, Pointer<T> fatherPointer, BTree<T> fatherTree)
	{
		this.degree = degree;
		currentCount = degree;
		isLeaf = true;
		this.fatherPointer = fatherPointer;
		this.fatherTree = fatherTree;
		this.left = left;
	}
	
	public T getElement(T element)
	{
		Pointer<T> current = left;
		while(current.hasNext())
		{
			Node<T> node = current.getNext();
			int comp = element.compareTo(node.getElem());
			if(comp == 0)
				return node.getElem();
			if(comp < 0)
				if(isLeaf)
					throw new IllegalArgumentException("element not found");
				else
					return current.getSon().getElement(element);
			current = current.getNext().getP();
		}
		if(isLeaf)
			throw new IllegalArgumentException("element not found");
		else
			return current.getSon().getElement(element);
	}
	
	public void insert(T elem)
	{
		Pointer<T> current = left;
		while(current.hasNext())
		{
			Node<T> node = current.getNext();
			int comp = elem.compareTo(node.getElem());
			if(comp == 0)
				throw new IllegalArgumentException("no duplicate elements");
			if(comp < 0)
			{
				if(isLeaf)
				{
					Pointer<T> newPointer = new Pointer<>(node);
					Node<T> newNode = new Node<T>(elem, newPointer);
					current.setNext(newNode);
					currentCount++;
					checkOverflow();
					return;
				}
				else
				{
					current.getSon().insert(elem);
					return;
				}
			}
			else
				current = node.getP();
		}
		current.setNext(new Node<T>(elem));
		currentCount++;
		checkOverflow();
	}
	
	private void checkOverflow() {
		if(currentCount > 2*degree)
		{
			//mitte finden
			Pointer<T> current = left;
			for (int i = 0; i < degree; i++)
				current = current.getNext().getP();
			
			//linken Teilbaum aufbauen
			Pointer<T> leftPointer = left;
			
			//rechten Teilbaum aufbauen
			Pointer<T> rightPointer = current.getNext().getP();
			
			//mittleres Element entfernen
			Node<T> middleNode = current.getNext();
			current.setNext(null);
			
			if(fatherTree == null)
			{
				Pointer<T> r = new Pointer<>();
				r.setSon(new BTree<>(degree,rightPointer,r,this));
				middleNode.setP(r);
				Pointer<T> l = new Pointer<>();
				l.setSon(new BTree<>(degree, leftPointer,l,this));
				l.setNext(middleNode);
				left = l;
				isLeaf = false;
				currentCount = 1;
			}
			else
			{
				Pointer<T> r = new Pointer<>();
				r.setNext(fatherPointer.getNext());
				r.setSon(new BTree<>(degree, rightPointer, r, fatherTree));
				fatherPointer.setSon(new BTree<>(degree, leftPointer, fatherPointer, fatherTree));
				fatherPointer.setNext(middleNode);
				middleNode.setP(r);
				fatherTree.currentCount ++;
				fatherTree.checkOverflow();
			}
		}
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Queue<BTree<T>> q = new LinkedList<>();
		q.offer(this);
		q.offer(null);
		while(!q.isEmpty())
		{
			BTree<T> currentTree = q.poll();
			if(currentTree != null)
			{
				Pointer<T> currentPointer = currentTree.left;
				sb.append(" |");
				while(currentPointer.hasNext())
				{
					if(currentPointer.getSon() != null)
						q.offer(currentPointer.getSon());
					if(currentPointer.hasNext())
						sb.append(currentPointer.getNext().getElem().toString()+" ");
					currentPointer = currentPointer.getNext().getP();
				}
				sb.append("| ");
				if(currentPointer.getSon() != null)
					q.offer(currentPointer.getSon());
			}
			else
			{
				if(!q.isEmpty())
				{
					sb.append("\n");
					q.offer(null);
				}
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<>(2);
		tree.insert(1);
		tree.insert(2);
		tree.insert(300);
		tree.insert(301);
		tree.insert(302);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
		
		System.out.println(tree);
	}

}
