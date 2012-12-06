package chess.piece;

import chess.util.Colour;


/**
 * @author Craig Martin
 * @version 0.1 - first piece representation
 * @since 03/12/12
 * 
 * This passes all tests defined in Piecetest.java
 */


abstract public class Piece {


	/**
	 *Enum storing the Piece type - Can either be Pawn, a knight, a bishop, a rook, a queen or a king
	 *The pieces are assigned values relative to their class - these values are taken from http://en.wikipedia.org/wiki/Chess_piece_relative_value
	 *with reference to (Capablanca & de Firmian 2006:24–25), (Soltis 2004:6), (Silman 1998:340), (Polgar & Truong 2005:11)
	 */
	public enum Type{
		PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;


	}

	// Variables needed
	private final Colour colour;
	private final Type type;
	private byte posistion;

	/**
	 * 
	 * @param c - colour of the piece
	 * @param p	- type of the piece
	 */
	public Piece(Colour c, Type t, byte coordinate) {
		this.colour = c;
		this.type = t;
		this.posistion = coordinate;
	}

	/**
	 * Get coordinates of this piece
	 * @return coordinates
	 */
	public byte getCoordinates() {
		return posistion;
	}
	/**
	 * sets the coordinates of this piece
	 * @param coordinates
	 */
	public void setCoordinates(byte coordinates) {
		this.posistion = coordinates;
	}
	/**
	 * Returns the colour of this piece
	 * @return colour
	 */
	public Colour getColour() {
		return colour;
	}

	/**
	 * returns the type of the piece
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Simple to string method. Prints everything out nice
	 * @return string
	 */
	public String toString(){
		return "Piece Colour: " + colour.toString() + " type: " + type.toString() +  " pos: " + posistion;
	}

}
