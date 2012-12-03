
package chess.game;

import chess.piece.Piece;



public class Player {
	

	
	public Piece.Colour c;
	public Player(){
		
	}
	
	public void setColour(String s){
		if(s.equals("white")){
			c = Piece.Colour.WHITE;
		}
		else{
			c = Piece.Colour.BLACK;
		}
	}
	
	
}
