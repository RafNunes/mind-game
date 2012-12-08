package chess.piece;

import chess.util.Colour;
/**
 * 
 * @author 1003264m, Demian Till
 * A sliding piece is a piece that can take multiple steps in it's allowed directions in a single turn
 */
public class SlidingPiece extends NonPawn {

	public SlidingPiece(Colour c, Type t, byte coordinate) {
		super(c, t, coordinate);
	}
}
