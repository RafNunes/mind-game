package chess.piece;

import chess.piece.Piece.Type;
import chess.util.Colour;

/**
 * 
 * @author Craig Martin
 * These pieces can only move to certain squares. 
 * They can not move to multiple squares in the same directions
 */

public class OnePiece extends NonPawn{

	//Moves for the Knight: 2Left1Up, 2Left1Down,2Up1Left,2Up1Right,2Right1Up,2Right1Down,2Down1Left,2Down1Right
	private final byte[] KnightDir = new byte[]{(byte)14,(byte)-18,(byte)31,(byte)33,(byte)18,(byte)-14,(byte)-33,(byte)-31};
	private final byte[] KingDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17,(byte)16,(byte)-16,(byte)-1,(byte)1};
	
	
	public OnePiece(Colour c, Type t, byte position) throws InvalidPiece {
		super(c, t, position);
		if (!(t == Type.KING || t== Type.KNIGHT)){
			throw new InvalidPiece("OnePiece must have type of either King or Knight");
		}
	}


	@Override
	public byte[] getDirections() {
		Type type = this.getType();
		switch(type){
		case KNIGHT:	return KnightDir;
		default:		return KingDir;
		}
	}
}
