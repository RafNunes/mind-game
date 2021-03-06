package chess.piece;

import java.util.Iterator;


public class PieceList implements Iterable<PieceListNode>{

	private PieceListNode head;	// the head is always empty and simply points to the first real node
								// this allows nodes to detach themselves from the list
	
	public PieceList() {
		
		head = new PieceListNode();
	}
	
	/**
	 *  makes a node with the provided piece and adds it to the list
	 * @param piece
	 */
	public void addPiece(Piece piece) {
		
		PieceListNode newNode = new PieceListNode(piece);
		newNode.setNext(head.getNext());
		if(head.getNext() != null) head.getNext().setPrevious(newNode);
		head.setNext(newNode);
		newNode.setPrevious(head);
	}
	/**
	 * Adds a premade node to the list.
	 * @param newNode
	 */
	public void addNode(PieceListNode newNode) {
		
		newNode.setNext(head.getNext());
		if(head.getNext() != null) head.getNext().setPrevious(newNode);
		head.setNext(newNode);
		newNode.setPrevious(head);
	}
	
	public Iterator<PieceListNode> iterator() {
		
		return new PieceListIterator(head.getNext());
	}
	
	public String toString() {
		String s="";
		for(PieceListNode n : this) {
			
			s+=n.toString();
		}
		return s;
	}
}
