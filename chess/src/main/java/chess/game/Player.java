
package chess.game;

import chess.piece.Piece;
import chess.util.Colour;



public abstract class Player {
	
public boolean getTrue(){return true;}
	
	public Colour c;
	
	public Player(){
		
	}
	
	public Player(Colour c){
		this.c = c;
	}
	
	public abstract int getPlayerType();
		
	
	
	
	
	
	
}
