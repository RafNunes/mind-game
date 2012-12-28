
package chess.game;

import chess.util.Colour;



public abstract class Player {
	
	public Colour c;
	
	public Player(){
		
	}
	
	public Player(Colour c){
		this.c = c;
	}
}
