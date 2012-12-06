package chess.piece;

public class InvalidPiece extends Exception {

	/**
	 * Put in here to keep eclipse happy
	 */
	private static final long serialVersionUID = 131507155746858938L;

	public InvalidPiece(String message){
		super(message);
	}

}
