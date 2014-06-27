
public class BTree<T extends Comparable<T>> {

	private int degree;
	private boolean isLeaf;
	private Node<T> left;
	private int size = 0;
	private Father<T> father;
	
	public BTree(int degree)
	{
		this.degree = degree;
		left = new Node<T>();
		isLeaf = true;
		this.father = null;
		this.father = new Father<T>();
	}
	
	public BTree(BTree<T> fatherTree, Node<T> fatherNode)
	{
		this.degree = fatherTree.degree;
		this.father = new Father<T>();
		father.setFatherTree(fatherTree);
		father.setFatherNode(fatherNode);
		isLeaf = true;
	}
	
	public void insert(T elem)
	{
		Node<T> nodeToInsert = new Node<>(elem);
		Node<T> current = left;
		while(!current.isLast())
		{
			int comp = current.compareTo(nodeToInsert);
			if(comp == 0)
				throw new IllegalArgumentException("dublicatet elements!");
			if(comp > 0)
				break;
			else
				current = current.getNext();
		}
		if(isLeaf)
		{
			if(current == left)
				left = nodeToInsert;
			else
				current.getPrev().setNext(nodeToInsert);
				
			nodeToInsert.setNext(current);
			size++;
			checkOverflow();
		}
		else
		{
			current.getChild().insert(elem);
		}
	}
	
	public void delete(T elem)
	{
		Node<T> current = left;
		while(current != null)
		{
			if(current.isLast())
				if(isLeaf)
					throw new IllegalArgumentException("Element not found");
				else{
					current.getChild().delete(elem);
					return;
				}
				
			int comp = current.getElem().compareTo(elem);
			if(comp == 0)
			{
				//element found
				if(isLeaf)
				{
					if(left == current)
					{
						left = current.getNext();
						current.setNext(null);
					}
					else
						current.getPrev().setNext(current.getNext());
					size--;
					checkUnderflow();
				}
				else
				{
					//hole kleinstes Blatt aus rechtem Teilbaum
					BTree<T> currentTree = current.getNext().getChild();
					while(!currentTree.isLeaf)
						currentTree = currentTree.left.getChild();
					Node<T> leftNode = currentTree.left;
					
					//ersetze damit geloeschtes Element
					current.setElem(leftNode.getElem());
					currentTree.left = leftNode.getNext();
					leftNode.setNext(null);
					currentTree.size--;
					currentTree.checkUnderflow();
				}
				return;
			}
			if(comp > 0)
				if(isLeaf)
					throw new IllegalArgumentException("Element not found");
				else
				{
					current.getChild().delete(elem);
					return;
				}
			else
				current = current.getNext();
		}
		throw new IllegalArgumentException("Element not found");
	}
	
	private void checkUnderflow() {
		if(size < degree)
		{
			//find brothers
			BTree<T> leftBrother = null;
			BTree<T> rightBrother = null;
			if(father.getFatherNode().getNext() != null)
				rightBrother = father.getFatherNode().getNext().getChild();
			if(father.getFatherNode().getPrev() != null)
				leftBrother = father.getFatherNode().getPrev().getChild();
			
			//Merge
			if((leftBrother == null && rightBrother.size == degree) || 
					(rightBrother== null && leftBrother.size == degree) ||
					(leftBrother !=null && rightBrother != null && 
					leftBrother.size == degree && rightBrother.size ==degree))
			{
				//TODO: Merge
			}
			//balance
			else
			{
				//balance with left Brother
				if(leftBrother != null)
				{
					Node<T> current = leftBrother.left;
					while(!current.isLast())
						current = current.getNext();
					Node<T> newLeft = new Node<>(father.getFatherNode().getPrev().getElem());
					newLeft.setNext(left);
					newLeft.setChild(current.getChild());
					left = newLeft;
					father.getFatherNode().getPrev().setElem(current.getPrev().getElem());
					current.setChild(current.getPrev().getChild());
					current.getPrev().getPrev().setNext(current);
					leftBrother.size--;
					this.size++;
				}
				//balance with right brother
				else
				{
					Node<T> current = left;
					while(!current.isLast())
						current = current.getNext();
					Node<T> newRight = new Node<>(father.getFatherNode().getElem());
					Node<T> rightBrotherLeft = rightBrother.left;
					current.getPrev().setNext(newRight);
					newRight.setNext(current);
					newRight.setChild(current.getChild());
					current.setChild(rightBrotherLeft.getChild());
					father.getFatherNode().setElem(rightBrotherLeft.getElem());
					rightBrother.left = rightBrotherLeft.getNext();
					rightBrotherLeft.setNext(null);
					rightBrother.size--;
					this.size++;
				}
			}
			
		}
	}

	private void checkOverflow() {
		if(size > 2 * degree)
		{
			Node<T> current = left;
			//find middle element
			for (int i = 0; i < degree; i++)
				current = current.getNext();
			
			//isroot?
			if(this.isRoot())
			{
				//build left tree
				BTree<T> tree1 = new BTree<>(this, current);
				tree1.left = this.left;
				Node<T> newTailNode = new Node<>();
				newTailNode.setChild(current.getChild());
				current.getPrev().setNext(newTailNode);
				
				//add new tailNode for Father
				Node<T> fatherTailNode = new Node<>();
				
				//build right tree
				BTree<T> tree2 = new BTree<>(this, fatherTailNode);
				tree2.left = current.getNext();
				
				//concatenate trees
				current.setNext(fatherTailNode);
				fatherTailNode.setChild(tree2);
				current.setChild(tree1);
				this.left = current;
				this.checkLeaf();
				this.size = 1;
				tree1.checkLeaf();
				tree1.size = degree;
				tree2.checkLeaf();
				tree2.size = degree;
			}
			else
			{
				//get Father
				BTree<T> fatherTree = father.getFatherTree();
				Node<T> fatherNode = father.getFatherNode();
				
				//build right tree
				BTree<T> tree1 = new BTree<>(fatherTree, current);
				tree1.left = current.getNext();
				
				//build left tree from this
				Node<T> newTailNode = new Node<>();
				newTailNode.setChild(current.getChild());
				current.getPrev().setNext(newTailNode);
				
				//insert current into father
				fatherNode.getPrev().setNext(current);
				current.setNext(fatherNode);
				
				//concatenate
				current.setChild(this);
				current.getNext().setChild(tree1);
				tree1.size = degree;
				this.size = degree;
				fatherTree.size++;
				fatherTree.checkOverflow();
			}
		}
	}
	
	public T get(T elem)
	{
		Node<T> current = left;
		while(current != null)
		{
			if(current.isLast())
				if(isLeaf)
					throw new IllegalArgumentException("Element not found");
				else
					return current.getChild().get(elem);
			int comp = current.getElem().compareTo(elem);
			if(comp == 0)
				return current.getElem();
			if(comp > 0)
				if(isLeaf)
					throw new IllegalArgumentException("Element not found");
				else
					return current.getChild().get(elem);
			else
				current = current.getNext();
		}
		throw new IllegalArgumentException("Element not found");
	}
	
	private void checkLeaf()
	{
		if(left == null || left.getChild() == null)
			isLeaf = true;
		else
			isLeaf = false;
	}
	
	public Father<T> getFather()
	{
		return father;
	}
	
	public boolean isRoot()
	{
		return father.getFatherTree() == null ;
	}

	public String toString()
	{
		return left.toString();
	}
	
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<>(2);
		tree.insert(5);
		tree.insert(1);
		tree.insert(3);
		tree.insert(2);
		tree.insert(6);
		tree.insert(7);
		tree.insert(8);
		tree.insert(9);
		tree.insert(0);
		tree.delete(5);
	}
}
