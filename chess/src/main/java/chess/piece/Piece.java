package chess.piece;

import chess.util.Colour;

/**
 * @author Craig Martin
 * @version 0.1 - first piece representation
 * @since 03/12/12
 * 
 *        This passes all tests defined in Piecetest.java
 */

abstract public class Piece {

	/**
	 * Enum storing the Piece type - Can either be Pawn, a knight, a bishop, a
	 * rook, a queen or a king
	 */
	public enum Type {
		PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING, NULL;
	}

	// Variables needed
	private final Colour colour;
	private final Type type;
	private byte position;
	private boolean hasMoved;

	/**
	 * 
	 * @param c
	 *            - colour of the piece
	 * @param p
	 *            - type of the piece
	 * @param position
	 *            - position in 0x88 array
	 */
	public Piece(Colour c, Type t, byte position) {
		this.colour = c;
		this.type = t;
		this.position = position;
		this.hasMoved = false;
	}

	/**
	 * sets the position of this piece
	 * 
	 * @param position
	 */
	public void setPosition(byte position) {
		this.position = position;
		this.hasMoved = true;
	}

	/**
	 * Returns the position of the piece
	 * 
	 * @return position
	 */
	public byte getPosition() {
		return this.position;
	}

	/**
	 * Returns the colour of this piece
	 * 
	 * @return colour
	 */
	public Colour getColour() {
		return colour;
	}

	/**
	 * returns the type of the piece
	 * 
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns true if the piece has has moved durin its life time, false otherwise
	 * 
	 * @return hasMoved
	 */
	public boolean hasMoved() {
		return this.hasMoved;
	}
	
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	/**
	 * Simple to string method. Prints everything out nice
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return "Piece Colour: " + colour.toString() + " type: " + type.toString() + " pos: " + position;
	}

}
