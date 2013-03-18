package chess.piece;

import java.util.Iterator;


public class PieceListIterator implements Iterator<PieceListNode>{
	
	private PieceListNode nextNode;
	
	public PieceListIterator(PieceListNode firstNode) {
		
		this.nextNode = firstNode;
	}
	/**
	 * Checks to see whether there are any more pieces in the list.
	 */
	public boolean hasNext() {
		
		return nextNode != null;
	}
	/**
	 * Returns the next Piece in the list.
	 */
	public PieceListNode next() {
		
		PieceListNode rtn = nextNode;
		nextNode = nextNode.getNext();
		return rtn;
	}
	
	public void remove() {
		
		// not necessary
	}
}
