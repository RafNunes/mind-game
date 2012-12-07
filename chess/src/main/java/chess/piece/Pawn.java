package chess.piece;

import chess.util.Colour;

/**
 * 
 * @author Jarana
 * Pawn class which extens Piece
 * Gives directions for the move generator to work with
 *
 */

public class Pawn extends Piece{
	
	private boolean hasMoved;
	private byte forward;
	private byte[] capture;
	
	public Pawn(Colour colour, byte position) {
		super(colour,Type.PAWN , position);
		capture = new byte[2];
		hasMoved = false;
		if(colour == Colour.WHITE){
			forward = (byte)16;
			capture[0] = (byte)17;
			capture[1] = (byte)15;
		}
		
		else{
			forward = (byte)-16;
			capture[0] = (byte)-17;
			capture[1] = (byte)-15;
		}
	}
	public void setHasMoved(){
		hasMoved = true;
	}
	public boolean getHasMoved(){
		return hasMoved;
	}
	public byte getForward(){
		return forward;
	}
	public byte[] getCapture(){
		return capture;
	}
	
}
