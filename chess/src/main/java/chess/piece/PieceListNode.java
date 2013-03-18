package chess.piece;

import chess.piece.Piece.Type;


public class PieceListNode {

	private PieceListNode previous;
	private PieceListNode next;
	private Piece piece;

	/**
	 * Removes the specified node from the list.
	 */
	public void detachSelfFromList() {
		
		previous.setNext(next);
		if(next != null) next.setPrevious(previous);
	}
	
	public PieceListNode getPrevious() {
		return previous;
	}
	public void setPrevious(PieceListNode previous) {
		this.previous = previous;
	}
	public PieceListNode getNext() {
		return next;
	}
	public void setNext(PieceListNode next) {
		this.next = next;
	}
	public Piece getPiece() {
		return piece;
	}
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	public PieceListNode() {
		
		previous = null;
		next = null;
		piece = null;
	}
	
	public PieceListNode(Piece piece) {
		
		this.piece = piece;
	}
	
	public String toString() {
		
		return piece.toString();
	}
}
