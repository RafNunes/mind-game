package chess.game;

import chess.util.*;

public class Human extends Player{
	
	public Human(Colour c){
		super(c);
	}
	
	public Human(){
		
	}
	public int getPlayerType(){
		return 0;
	}
}
