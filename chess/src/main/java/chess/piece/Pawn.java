package chess.piece;

import chess.util.Colour;

/**
 * 
 * @author Jarana
 * Pawn class which extends Piece
 * Gives directions for the move generator to work with
 *
 */

public class Pawn extends Piece{
	
	private boolean hasMoved;
	private byte forwardDir;
	private byte[] captureDirs;
	
	public Pawn(Colour colour, byte position) {
		
		super(colour, Type.PAWN, position);
		hasMoved = false;
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
	public void setHasMoved(){
		hasMoved = true;
	}
	public boolean getHasMoved(){
		return hasMoved;
	}
	public byte getForwardDir(){
		return forwardDir;
	}
	public byte[] getCaptureDirs(){
		return captureDirs;
	}
	
}
