
package chess.piece;

import chess.util.Colour;
/**
 * 
 * @author Craig Martin, Demian Till
 * A sliding piece is a piece that can take multiple steps in it's allowed directions in a single turn
 */
public class SlidingPiece extends NonPawn {

	public SlidingPiece(Colour c, Type t, byte position) {
		super(c, t, position);
	}
}
