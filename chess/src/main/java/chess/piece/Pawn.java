package chess.piece;

import chess.util.Colour;

/**
 * 
 * @author Jarana, Demian Till
 * Pawn class which extends Piece
 * Gives directions for the move generator to work with
 *
 */

public class Pawn extends Piece{
	
	private byte forwardDir;
	private byte[] captureDirs;
	
	public Pawn(Colour colour, byte position) {
		
		super(colour, Type.PAWN, position);
		captureDirs = new byte[2];
		if(colour == Colour.WHITE){
			forwardDir = (byte)16;
			captureDirs[0] = (byte)17;
			captureDirs[1] = (byte)15;
		}
		
		else{
			forwardDir = (byte)-16;
			captureDirs[0] = (byte)-17;
			captureDirs[1] = (byte)-15;
		}
	}
	public byte getForwardDir(){
		return forwardDir;
	}
	public byte[] getCaptureDirs(){
		return captureDirs;
	}
	public boolean oneOffFinalRow() {
		
		// return true if has moved and is on one of the second from last rows
		return (hasMoved() && (getPosition() + forwardDir >= 96 || getPosition() + forwardDir <= 31));
	}
}
