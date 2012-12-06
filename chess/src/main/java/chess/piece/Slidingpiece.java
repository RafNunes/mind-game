package chess.piece;

import chess.util.Colour;
/**
 * 
 * @author 1003264m
 * A sliding piece is a piece which can move multiple squares in a row, and it is not a pawn
 */
public class Slidingpiece extends NonPawn {

	//Moves for the bishop: leftup, rightup, rightdown, leftdown   
		private final byte[] BishDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17};
		//Moves for the rook: Up, Down, Left, Right
		private final byte[] RookDir = new byte[]{(byte)16,(byte)-16,(byte)-1,(byte)1};
		//Moves for the Queen:leftup, rightup, rightdown, leftdown,Up, Down, Left, Right, 
		private final byte[] QueenDir = new byte[]{(byte)15,(byte)17,(byte)-15,(byte)-17,(byte)16,(byte)-16,(byte)-1,(byte)1};
		
	public Slidingpiece(Colour c, Type t, byte coordinate) {
		super(c, t, coordinate);
	}

	public byte[] getDirections() {
		Type type = this.getType();
		switch(type){
		case BISHOP:	return BishDir;
		case ROOK:		return RookDir;
		case QUEEN:		return QueenDir; 
		}
		return null; //should not happen
	}

}
