package chess.piece;

import chess.util.Colour;
/**
 * @author Craig Martin, Demian Till
 * All non-pawn pieces obey the standard ruled for moving (castling aside) in that they can move when the destination
 * square is either empty or occupied by an enemy piece. The NonPawn class implements a method to get an array of 
 * directions in which the particular piece type represented by a NonPawn object may move.
 */


abstract public class NonPawn extends Piece {
	
	// Moves for each non-pawn piece type. Each object holds a reference to the 
	// corresponding shared array of directions of movement.
	private final byte[] directions;
	
	//Moves for the rook: Up, Down, Left, Right
	private static final byte[] rookDir = new byte[]{(byte)16,(byte)-16,(byte)-1,(byte)1};
	//Moves for the Knight: 2Left1Up, 2Left1Down,2Up1Left,2Up1Right,2Right1Up,2Right1Down,2Down1Left,2Down1Right
	private static final byte[] knightDir = new byte[]{(byte)14,(byte)-18,(byte)31,(byte)33,(byte)18,(byte)-14,(byte)-33,(byte)-31};
	//Moves for the bishop: leftup, rightup, rightdown, leftdown   
	private static final byte[] bishDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17};
	//Moves for the Queen:leftup, rightup, rightdown, leftdown,Up, Down, Left, Right, 
	private static final byte[] queenDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17,(byte)16,(byte)-16,(byte)-1,(byte)1};
	private static final byte[] kingDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17,(byte)16,(byte)-16,(byte)-1,(byte)1};
			
	/**
	 * Creates a piece with these attributes
	 * @param c
	 * @param t
	 * @param position
	 */
	public NonPawn(Colour c, Type t, byte position) {
		super(c, t, position);
		switch(t) {
		
		case ROOK: directions = rookDir; break;
		case KNIGHT: directions = knightDir; break;
		case BISHOP: directions = bishDir; break;
		case QUEEN: directions = queenDir; break;
		case KING: directions = kingDir; break;
		default: directions = null; break;
		}
	}

	public byte[] getDirections() {
		
		return directions;
	}
}
