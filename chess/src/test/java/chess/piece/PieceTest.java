package chess.piece;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import chess.piece.Piece;
import chess.piece.Piece.Colour;
import chess.piece.Piece.Type;

//all tests are currently passed

public class PieceTest {
	@Test
	//test for colour
	public void Whitetest(){
		Piece p = new Piece(Colour.WHITE, Type.PAWN, (byte)0);
		assertEquals("Result", Colour.WHITE, p.getColour());	
	}

	@Test
	//test for colour
	public void Blacktest(){
		Piece p = new Piece(Colour.BLACK, Type.PAWN, (byte)0);
		assertEquals("Result", Colour.BLACK, p.getColour());
	}

	@Test
	//type test for pawn, should be fine
	public void Pawntest(){
		Piece p = new Piece(Colour.BLACK, Type.PAWN, (byte)0);
		assertEquals("Result", Type.PAWN, p.getType());		
	}
	@Test
	//type test for rook, should be fine
	public void Rooktest(){
		Piece p = new Piece(Colour.BLACK, Type.ROOK, (byte)0);
		assertEquals("Result", Type.ROOK, p.getType());		
	}
	@Test
	//type test for Bishop
	public void Bishoptest(){
		Piece p = new Piece(Colour.BLACK, Type.BISHOP, (byte)0);
		assertEquals("Result", Type.BISHOP, p.getType());
	}
	@Test
	//type test for Knight
	public void Knighttest(){
		Piece p = new Piece(Colour.BLACK, Type.KNIGHT, (byte)0);
		assertEquals("Result", Type.KNIGHT, p.getType());
	}
	@Test
	//type test for Queen
	public void Queentest(){
		Piece p = new Piece(Colour.BLACK, Type.QUEEN, (byte)0);
		assertEquals("Result", Type.QUEEN, p.getType());
	}
	@Test
	//type test for King
	public void Kingtest(){
		Piece p = new Piece(Colour.BLACK, Type.KING, (byte)0);
		assertEquals("Result", Type.KING, p.getType());
	}
	@Test
	//test for posistive coordinates (1 - 100)
	public void poscor(){
		int i = 1;
		while (i <= 1000){
			Piece p = new Piece(Colour.BLACK, Type.KING, (byte)i);
			assertEquals("Result", (byte)i, p.getCoordinates());	
			i++;
		}
	}
	@Test
	//test for 0 coordinates
	public void zerocor(){
		Piece p = new Piece(Colour.BLACK, Type.KING, (byte)0);
		assertEquals("Result", (byte)0, p.getCoordinates());
	}
	@Test
	//negative numbers should work however should not be required in the final solution
	public void negacor(){
		int i = -1;
		while (i >= -1000){
			Piece p = new Piece(Colour.BLACK, Type.KING, (byte)i);
			assertEquals("Result", (byte)i, p.getCoordinates());	
			i--;

		}
	}
}
