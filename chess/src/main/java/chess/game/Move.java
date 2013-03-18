package chess.game;

import chess.piece.Piece;
import chess.piece.Piece.Type;

/**
 * 
 * @author Craig Martin, Demian Till
 * @version 0.1 - first move representation
 * @since 03/12/12
 */

public class Move {
	private final byte startpos;
	private final byte endpos;
	private Piece.Type promotion;
	private Piece capture;
	private Piece promotedPawn;
	private boolean firstTimeMoving; // this should be set to true if it is the
										// first time the moving piece has moved
										// so that
										// it can be set back to unmoved if and
										// when the move is undone.

	public Move(byte start, byte end) {
		this.startpos = start;
		this.endpos = end;
	}

	public Move(byte start, byte end, Piece.Type promotion) {
		this.startpos = start;
		this.endpos = end;
		this.promotion = promotion;
	}

	public Move(byte start, byte end, Piece capture) {
		this.startpos = start;
		this.endpos = end;
		this.capture = capture;
	}

	public Move(byte start, byte end, Piece.Type promotion, Piece capture) {
		this.startpos = start;
		this.endpos = end;
		this.promotion = promotion;
		this.capture = capture;
	}

	// /**
	// * Current test method, used for updating the coordinates and activating
	// the promotion method
	// * setting the coordinates will not be done from here permanently
	// * @param p
	// * @param promotion
	// * @throws InvalidPiece
	// */
	// public Piece makeMove(Piece p,boolean promotion, Type t) throws
	// InvalidPiece{
	// p.setPosition(endpos);
	// if (promotion){
	// p = promotion(p, t);
	//
	// }
	// return p;
	// }

	// getters and setters

	public void setFirstTimeMoving(boolean f) {

		firstTimeMoving = f;
	}

	public boolean getFirstTimeMoving() {

		return firstTimeMoving;
	}

	public byte getStartpos() {
		return startpos;
	}

	public byte getEndpos() {
		return endpos;
	}

	public Piece.Type getPromotion() {

		return promotion;
	}

	public void setPromotion(Piece.Type promotion) {

		this.promotion = promotion;
	}

	public Piece getCapture() {

		return capture;
	}

	public void setCapture(Piece capture) {

		this.capture = capture;
	}

	public Piece getPromotedPawn() {

		return promotedPawn;
	}

	public void setPromotedPawn(Piece promotedPawn) {

		this.promotedPawn = promotedPawn;
	}

	/**
	 * Checks to see if the current instance of move matches the input move.
	 * 
	 * @param moveInput
	 *            the move that will be compared to this move
	 * @return true if moves match, false otherwise
	 */
	public boolean matches(String moveInput) {

		int startFile07 = Character.getNumericValue(moveInput.charAt(0)) - Character.getNumericValue('a');
		int startRank07 = Integer.valueOf(moveInput.charAt(1)) - 1;
		int nextMoveCharacterIndex = 2;
		while (moveInput.charAt(nextMoveCharacterIndex) == ' ')
			nextMoveCharacterIndex++;
		int endFile07 = Character.getNumericValue(moveInput.charAt(nextMoveCharacterIndex))
				- Character.getNumericValue('a');
		int endRank07 = Integer.valueOf(moveInput.charAt(nextMoveCharacterIndex + 1)) - 1;
		byte inputStartPos = (byte) (startFile07 + (16 * startRank07));
		byte inputEndPos = (byte) (endFile07 + (16 * endRank07));
		if (inputStartPos == startpos && inputEndPos == endpos) {

			if (promotion == null) {

				return true;
			} else {

				if (moveInput.charAt(nextMoveCharacterIndex + 2) == 'q' && promotion == Piece.Type.QUEEN)
					return true;
				if (moveInput.charAt(nextMoveCharacterIndex + 2) == 'n' && promotion == Piece.Type.KNIGHT)
					return true;
				else
					return false;
			}
		} else
			return false;
	}

	@Override
	public String toString() {

		String[] files = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };
		int startFile07 = startpos & 7;
		int startRank07 = startpos >> 4;
		int endFile07 = endpos & 7;
		int endRank07 = endpos >> 4;

		return files[startFile07] + (startRank07 + 1) + files[endFile07] + (endRank07 + 1)
				+ (promotion == null ? "" : (promotion == Type.QUEEN ? "q" : "n"));
	}
}
