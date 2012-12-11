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
	
	private static final byte[] whiteCaptureDirs = new byte[]{(byte)15, (byte)17};
	private static final byte[] blackCaptureDirs = new byte[]{(byte)-15, (byte)-17};
	
	private byte forwardDir;
	private byte[] captureDirs;
	private final byte startingPos;
	
	public static byte[] getCaptureDirs(Colour c) {
		
		switch(c) {
		
		case WHITE: return whiteCaptureDirs;
		case BLACK: return blackCaptureDirs;
		default: return null;
		}
	}
	
	public Pawn(Colour colour, byte position) {
		
		
		super(colour, Type.PAWN, position);
		captureDirs = new byte[2];
		startingPos = position;
		
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
		return (hasMoved() && (getPosition() >= 96 || getPosition() <= 31));
	}
	public byte getStartingPos() {
		
		return startingPos;
	}
}
