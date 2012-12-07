package chess.piece;

import chess.util.Colour;
/**
 * 
 * @author Craig Martin
 * A sliding piece is a piece which can move multiple squares in a row, and it is not a pawn
 */
public class SlidingPiece extends NonPawn {

	//Moves for the bishop: leftup, rightup, rightdown, leftdown   
		private final byte[] BishDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17};
		//Moves for the rook: Up, Down, Left, Right
		private final byte[] RookDir = new byte[]{(byte)16,(byte)-16,(byte)-1,(byte)1};
		//Moves for the Queen:leftup, rightup, rightdown, leftdown,Up, Down, Left, Right, 
		private final byte[] QueenDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17,(byte)16,(byte)-16,(byte)-1,(byte)1};
		
	public SlidingPiece(Colour c, Type t, byte coordinate) throws InvalidPiece {
		super(c, t, coordinate);
		if (!(t == Type.QUEEN || t == Type.ROOK || t == Type.BISHOP)){
			throw new InvalidPiece("Sliding piece must be of type Bishop,Queen or Rook");
		}
	}

	public byte[] getDirections() {
		Type type = this.getType();
		switch(type){
		case BISHOP:	return BishDir;
		case ROOK:		return RookDir;
		default:		return QueenDir; 
		}
	}

}
