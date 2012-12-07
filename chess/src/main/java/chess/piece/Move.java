package chess.piece;

import chess.piece.Piece.Type;

/**
 * 
 * @author Craig Martin
 * @version 0.1 - first move representation
 * @since 03/12/12
 */

public class Move {
	private byte startpos;
	private byte endpos;

	public Move(byte start, byte end){
		this.startpos = start;
		this.endpos = end;
	}
	
	/**
	 * Current test method, used for updating the coordinates and activating the promotion method
	 * setting the coordinates will not be done from here permanently 
	 * @param p
	 * @param promotion
	 * @throws InvalidPiece 
	 */
	public Piece makeMove(Piece p,boolean promotion, Type t) throws InvalidPiece{
		p.setCoordinates(endpos);
		if (promotion){
			p = promotion(p, t);
			
		}
		return p;
	}

	/** 
	 * Could this be changed to just colour?
	 * @param p piece to be promoted
	 * @param t the type of piece to be promoted 
	 * @param endpos the coordinates of the new promoted piece
	 * @throws InvalidPiece 
	 * Returns null if something goes wrong
	 */
	private Piece promotion(Piece p, Type t) throws InvalidPiece {
		switch(t){
		case KNIGHT: 
			OnePiece k = new OnePiece(p.getColour(),t,p.getCoordinates());
			return k;
		case QUEEN:
			SlidingPiece q = new SlidingPiece(p.getColour(),t,p.getCoordinates());
			return q;
		case BISHOP:
			SlidingPiece b = new SlidingPiece(p.getColour(),t,p.getCoordinates());
			return b;
		case ROOK:
			SlidingPiece r = new SlidingPiece(p.getColour(),t,p.getCoordinates());
			return r;
		}
		return null;
	}

	
	//getters and setters
	
	
	public byte getStartpos() {
		return startpos;
	}

	public void setStartpos(byte startpos) {
		this.startpos = startpos;
	}

	public byte getEndpos() {
		return endpos;
	}

	public void setEndpos(byte endpos) {
		this.endpos = endpos;
	}
	
	public String toString(){
		return "Start " + startpos + " End " + endpos;
	}
}
