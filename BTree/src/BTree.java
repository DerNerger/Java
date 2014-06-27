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
		this.fatherPointer = fatherPointer;
		this.fatherTree = fatherTree;
		this.left = left;
		isLeaf = left.getSon() == null;
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
					Pointer<T> newPointer = new Pointer<>(node,current);
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
		if(isLeaf)
		{
			current.setNext(new Node<T>(elem));
			currentCount++;
			checkOverflow();
		}
		else
		{
			current.getSon().insert(elem);
			return;
		}
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
	
	private void checkUnderFlow()
	{
		if(currentCount < degree)
		{
			BTree<T> rightBrother = null;
			if(fatherPointer.hasNext())
				rightBrother = fatherPointer.getNext().getP().getSon();
			
			BTree<T> leftBrother = null;
			if(fatherPointer.hasPrecursor())
				leftBrother = fatherPointer.getPrecursor().getSon();
			
			int maxElements = 0;
			if(rightBrother != null)
				maxElements = rightBrother.currentCount;
			if(leftBrother != null && leftBrother.currentCount > maxElements)
				maxElements = leftBrother.currentCount;
		}
	}
	
	public void delete(T element)
	{
		Pointer<T> current = left;
		while(current.hasNext())
		{
			Node<T> node = current.getNext();
			int comp = element.compareTo(node.getElem());
			if(comp == 0)
			{
				//loesche
				
				//element ist im Blatt
				if(isLeaf)
				{
					if(node.getP()!= null)
						current.setNext(node.getP().getNext());
					else
						current.setNext(null);
					currentCount--;
					checkUnderFlow();
				}
				//element ist kein Blatt
				else
				{
					//kleinsten knoten des rechten teilbaums suchen
					BTree<T> currentTree = node.getP().getSon();
					while(currentTree.left.getSon() != null)
						currentTree = currentTree.left.getSon();
					
					//und diesen an die Position des zu loeschenden elements schieben
					node.setElem(currentTree.left.getNext().getElem());
					currentTree.delete(node.getElem());
				}
				return;
			}
			if(comp < 0)
				if(isLeaf)
					throw new IllegalArgumentException("element not found");
				else
				{
					current.getSon().delete(element);
					return;
				}
			current = current.getNext().getP();
		}
		if(isLeaf)
			throw new IllegalArgumentException("element not found");
		else
		{
			current.getSon().delete(element);
			return;
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
		
		//System.out.println(tree);
		
		tree.insert(6);
		tree.insert(7);
		tree.insert(8);
		
		//System.out.println(tree);
		
		tree.insert(303);
		tree.insert(304);
		tree.insert(305);
		
		//System.out.println(tree);
		
		tree.insert(306);
		tree.insert(307);
		tree.insert(308);
		
		System.out.println(tree);
		
		tree.delete(300);
		System.out.println(tree);
	}
}
