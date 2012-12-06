package chess.piece;

import chess.util.Colour;
/**
 * @author Craig Martin
 * Any Piece that is not a Pawn follows a set of rules
 */


abstract public class NonPawn extends Piece {
	
	//Moves for the Knight: 2Left1Up, 2Left1Down,2Up1Left,2Up1Right,2Right1Up,2Right1Down,2Down1Left,2Down1Right
	private final byte[] KnightDir = new byte[]{(byte)14,(byte)-18,(byte)31,(byte)33,(byte)18,(byte)-14,(byte)-33,(byte)-31};
	
	/**
	 * Creates a piece with these attributes
	 * @param c
	 * @param t
	 * @param position
	 */
	public NonPawn(Colour c, Type t, byte position) {
		super(c, t, position);
	}

	public abstract byte[] getDirections();
}
