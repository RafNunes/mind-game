package chess.piece;

import java.util.Iterator;


public class PieceListIterator implements Iterator<PieceListNode>{
	
	private PieceListNode nextNode;
	
	public PieceListIterator(PieceListNode firstNode) {
		
		this.nextNode = firstNode;
	}

	public boolean hasNext() {
		
		return nextNode != null;
	}
	
	public PieceListNode next() {
		
		PieceListNode rtn = nextNode;
		nextNode = nextNode.getNext();
		return rtn;
	}
	
	public void remove() {
		
		// not necessary
	}
}
