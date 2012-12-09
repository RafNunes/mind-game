package chess.piece;

import chess.util.Colour;

/**
 * 
 * @author Craig Martin, Demian Till
 * These pieces can only move one step in their allowed directions
 */

public class SteppingPiece extends NonPawn{

	public SteppingPiece(Colour c, Type t, byte position) {
		super(c, t, position);
	}
}
