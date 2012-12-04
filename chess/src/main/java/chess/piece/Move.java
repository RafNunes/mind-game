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
	 */
	public Piece makeMove(Piece p,boolean promotion, Type t){
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
	 */
	private Piece promotion(Piece p, Type t) {
		Piece d = new Piece(p.getColour(),t,endpos);
		return d;
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
