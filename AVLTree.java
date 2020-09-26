



/**  Implementation of an AVL tree. */
public class AVLTree extends BinarySearchTree implements Dictionary {

  public AVLTree(Comparator c)  { super(c); }

  public AVLTree() { super(); }

  /** Nested class for the nodes of an AVL tree. */ 
  protected static class AVLNode extends BTNode {

    protected int height;  // we add a height field to a BTNode

    /** Preferred constructor */
    AVLNode(Object element, BTPosition parent,
	    BTPosition left, BTPosition right) {
      super(element, parent, left, right);
      height = 0;
      if (left != null) 
        height = Math.max(height, 1 + ((AVLNode) left).getHeight());
      if (right != null) 
        height = Math.max(height, 1 + ((AVLNode) right).getHeight());
    } // we assume that the parent will revise its height if needed

    public void setHeight(int h) { height = h; }

    public int getHeight() { return height; }
  }


  /**
    * Print the content of the tree root.
    */
  public void printRoot() {
     System.out.println("printRoot: " + value(left(root())).toString() + " "
        +  value(root()).toString() + " " + value(right(root())).toString());
  }


  /**
    * Print the the tree using preorder traversal.
    */
  public void preorderPrint(Position v) {
      if (isInternal(v))
         System.out.print( value(v).toString() + " " );
      if (hasLeft(v)) preorderPrint(left(v));
      if (hasRight(v)) preorderPrint(right(v));
  }


  /** Creates a new binary search tree node (overrides super's version). */
  protected BTPosition createNode(Object element, BTPosition parent,
              BTPosition left, BTPosition right) {
    return new AVLNode(element,parent,left,right);  // now use AVL nodes
  }


  /** Returns the height of a node (call back to an AVLNode). */
  protected int height(Position p)  {
    return ((AVLNode) p).getHeight();
  }


  /** Sets the height of an internal node (call back to an AVLNode). */
  protected void setHeight(Position p)  { // called only if p is internal
    ((AVLNode) p).setHeight(1+Math.max(height(left(p)), height(right(p))));
  }


  /** Returns whether a node has balance factor between -1 and 1. */
  protected boolean isBalanced(Position p)  {
    int bf = height(left(p)) - height(right(p));
    return ((-1 <= bf) &&  (bf <= 1));
  }


  /** Returns a child of p with height no smaller than that of the other child 
    */
  protected Position tallerChild(Position p)  {
    if (height(left(p)) > height(right(p))) return left(p);
    else if (height(left(p)) < height(right(p))) return right(p);
    // equal height children - break tie using parent's type
    if (isRoot(p)) return left(p);
    if (p == left(parent(p))) return left(p);
    else return right(p);
  }


  /**
    * Insert a new element into the AVL tree.
    */
  public Entry insert(Object k, Object v) throws InvalidKeyException  {
    Entry toReturn = super.insert(k, v); // calls our new createNode method
    rebalance(actionPos); // rebalance up from the insertion position
    return toReturn;
  }


  /**
    * Remove an element from the AVL tree.
    */
  public Entry remove(Entry ent) throws InvalidEntryException {
    Entry toReturn = super.remove(ent);
    if (toReturn != null)   // we actually removed something
      rebalance(actionPos);  // rebalance up the tree
    return toReturn;
  }


  /**  
    * Rebalance method called by insert and remove.  Traverses the path from 
    * zPos to the root. For each node encountered, we recompute its height 
    * and perform a trinode restructuring if it's unbalanced.
    */
  protected void rebalance(Position zPos) {
    if(isInternal(zPos))
       setHeight(zPos);
    while (!isRoot(zPos)) {  // traverse up the tree towards the root
      zPos = parent(zPos);
      setHeight(zPos);
      if (!isBalanced(zPos)) { 
	// Perform a trinode restructuring starting from zPos's tallest grandchild
        Position xPos =  tallerChild(tallerChild(zPos));
        zPos=restructure(xPos); // tri-node restructure 
        setHeight(left(zPos));  // recompute heights
        setHeight(right(zPos));
        
        setHeight(zPos);
      }
    }
  } 





   // ***************************************
   // DO NOT MODIFY THE CODE ABOVE THIS LINE.
   // ADD YOUR CODE BELOW THIS LINE.
   //
   // ***************************************


  /** 
   * Perform a trinode restructuring starting from x, z's tallest grandchild.
   * Input: xPos, position of (pointer to) x
   * Output: position of (pointer to) the new root of the subtree that was restructured.
   */
  protected Position restructure( Position xPos ) {

	// COMPLETE THIS METHOD

	// You may add your own method(s) to this file.
	Position yPos=parent(xPos);
	Position zPos=parent(parent(xPos));
	if(checksingle(xPos,yPos,zPos)) {
		singleRotation(xPos,yPos,zPos);
	}
	else {
		Position right=right(root());
		Position left=left(root());
		//check right child == z
		while(!isExternal(right)) {
			if(right==zPos || right(zPos)==yPos) {
				doubleRotationright(xPos,yPos,zPos);
				break;
			}
			right=right(right);
		}
		while(!isExternal(left)) {
			if(left==zPos || left(zPos)==yPos) {
				doubleRotationleft(xPos,yPos,zPos);
				break;
			}
			left=left(left);
		}
	}
//	actionPos=zPos;
	return ( root() );	// replace this line with your code

  } // restructure
  
  void singleRotation(Position xPos,Position yPos,Position zPos) {
	  if(xPos==right(yPos)) {
		 //remove xpos
		  if(zPos==root()) {
		 removeall(xPos);
		 //remove ypos
		 removeall(yPos);
		 super.expandExternal(left(zPos), null, null);
		 super.replace(left(zPos), zPos.element());
		 super.replace(root(), yPos.element());
		 super.expandExternal(right(root()), null, null);
		 super.replace(right(root()), xPos.element());
		  }
		  else {
			  if(left(zPos).element()==null) {
				  super.expandExternal(left(zPos), null, null);
				  super.replace(left(zPos), zPos.element());
				  super.replace(zPos, yPos.element());
				  super.replace(yPos, xPos.element());
				  removeall(xPos);

			  }
			  else {
				  //search left side
				  Position left=left(root());
				  while(!isExternal(left)) {
					  left=left(left);
				  }
				  super.expandExternal(left, null, null);
				  setHeight(left);
				  super.expandExternal(right(parent(left)), null, null);
				  setHeight(right(parent(left)));
				  //push from left down to up
				  Position leftnext = left(root());
				  while(!isExternal(leftnext)) {
					  
					  leftnext=left(leftnext);
				  }
				  Position parent=parent(parent(leftnext));
				  super.replace(right(parent), left(zPos).element());
				  setHeight(right(parent));
				  while(parent!=root()) {
					  super.replace(left(parent), parent.element());
					  setHeight(left(parent));
					  parent=parent(parent);
				  }
				  super.replace(left(parent), parent.element());
				  setHeight(left(parent));
				  
				  //push from right down to up
				  Position rightnext = right(root());
				  while(!isExternal(rightnext)) {
					  super.replace(parent(rightnext), rightnext.element());
					  setHeight(parent(rightnext));
					  rightnext=right(rightnext);
				  }
				  removeall(parent(rightnext));
				  removeall(left(zPos));
			  }  
		  }
		 
	  }
	  // single rotation to left
	  else if(xPos==left(yPos)) {
		  if(zPos==root()) {
			  removeall(xPos);
				 //remove ypos
				 removeall(yPos);
				 super.expandExternal(right(zPos), null, null);
				 super.replace(right(zPos), zPos.element());
				 super.replace(root(), yPos.element());
				 super.expandExternal(left(root()), null, null);
				 super.replace(left(root()), xPos.element());
		  }
		  else {
		  if(right(zPos).element()==null) {
			  super.expandExternal(right(zPos), null, null);
			  super.replace(right(zPos), zPos.element());
			  super.replace(zPos, yPos.element());
			  super.replace(yPos, xPos.element());
			  removeall(xPos);

		  }
		  else {
			  //search left side
			  Position right=right(root());
			  while(!isExternal(right)) {
				  right=right(right);
			  }
			  super.expandExternal(right, null, null);
			  setHeight(right);
			  super.expandExternal(left(parent(right)), null, null);
			  setHeight(left(parent(right)));
			  //push from left down to up
			  Position rightnext = right(root());
			  while(!isExternal(rightnext)) {
				  
				  rightnext=right(rightnext);
			  }
			  Position parent=parent(parent(rightnext));
			  super.replace(left(parent), right(zPos).element());
			  setHeight(left(parent));
			  while(parent!=root()) {
				  super.replace(right(parent), parent.element());
				  setHeight(right(parent));
				  parent=parent(parent);
			  }
			  super.replace(right(parent), parent.element());
			  setHeight(right(parent));
			  
			  //push from right down to up
			  Position leftnext = left(root());
			  while(!isExternal(leftnext)) {
				  super.replace(parent(leftnext), leftnext.element());
				  setHeight(parent(leftnext));
				  leftnext=left(leftnext);
			  }
			  removeall(parent(leftnext));
			  removeall(right(zPos));
		  }
		  
		  }
		  
		  
	  }
	  
  }
  // double rotation
  void doubleRotationright(Position xPos,Position yPos,Position zPos) {
	  if(left(zPos).element()==null && right(yPos).element()==null) {
			 super.expandExternal(left(zPos), null, null);
			 super.replace(left(zPos), zPos.element());
			 super.replace(zPos, xPos.element());
			 removeall(xPos);
	  }
	  else if(left(zPos).element()!=null && right(zPos).element()!=null && xPos==left(yPos) &&right(yPos).element()!=null && left(yPos).element()!=null) {
		  Position left=left(root());
		  while(!isExternal(left)) {
			  left=left(left);
		  }
		  super.expandExternal(left, null, null);
		  super.expandExternal(right(parent(left)), null, null);
		  super.replace(right(parent(left)), left(xPos).element());
		  Position leftchild=left(left);
		  while(leftchild!=root()) {
			  super.replace(leftchild, parent(leftchild).element());
			  leftchild=parent(leftchild);
		  }
		  super.replace(root(), xPos.element());
		  removeall(left(xPos));
		  removeall(xPos);
		  

		  
	  }
	  
	  
  }
  void doubleRotationleft(Position xPos,Position yPos,Position zPos) {
	  if(right(zPos).element()==null && left(yPos).element()==null) {
			 super.expandExternal(right(zPos), null, null);
			 super.replace(right(zPos), zPos.element());
			 super.replace(zPos, xPos.element());
			 removeall(xPos);
	  }
	  else if(right(zPos).element()!=null && left(zPos).element()!=null && xPos==right(yPos) &&left(yPos).element()!=null && right(yPos).element()!=null) {
		  Position right=right(root());
		  while(!isExternal(right)) {
			  right=right(right);
		  }
		  super.expandExternal(right, null, null);
		  super.expandExternal(left(parent(right)), null, null);
		  super.replace(left(parent(right)), right(xPos).element());
		  Position rightchild=right(right);
		  while(rightchild!=root()) {
			  super.replace(rightchild, parent(rightchild).element());
			  rightchild=parent(rightchild);
		  }
		  super.replace(root(), xPos.element());
		  removeall(right(xPos));
		  removeall(xPos);
	  }
	  
	  
	  
  }
  void removeall(Position pos) {
	  Position parent=parent(pos);
	  if(pos==right(parent)) {
	  super.remove(right(pos));
	  super.remove(left(pos));
	  super.remove(pos);
	  super.insertRight(parent, null);
	  }
	  else if(pos==left(parent)) {
		  super.remove(right(pos));
		  super.remove(left(pos));
		  super.remove(pos);
		  super.insertLeft(parent, null);
		  }
	  
	  while(parent!=root()) {
		  setHeight(parent);
		  parent=parent(parent);
	  }
	  setHeight(root);
  }
  boolean checksingle(Position xPos,Position yPos,Position zPos) {
	 
	  
	  return (xPos==right(yPos) && yPos==right(zPos)) || (xPos==left(yPos) && yPos==left(zPos));
  }
  
  
  
  
  
  
  
} // end AVLTree class