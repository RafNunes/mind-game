/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, 6/12/12
 */


package chess.game;

import java.util.Scanner;

import chess.piece.Piece;
import chess.util.*;

public class Game {

	Board gameBoard = new Board();
	Player[] players;
	/**
	 * Player player1;
	 * Player player2;
	 */
	public Game(){
		gameBoard = new Board();
		Player[] players = new Player[2];
		players[0] = new Player();
		players[1] = new Player();
	}
	
	public Board getBoard(){
		return gameBoard;
	}
	
	public int getPlayerType(int n){
		if(n!=0 || n!=1){
			System.err.println("Bad player number");
			return -1;
		}
		//if players[n] is human
			//Return some representation of human
		//if players[n] is AI
			//Return some representation of AI
		return 0;
	}
	
	/**
	 * Indicates an AI turn. Makes its own move, all it needs to know is which side it's playing.
	 *
	 * @param n the player whose turn it is.
	 */
	public void AITurn(int n){
		//Call board class(Send colour of players[n])
		//Board class does move generation, evaluation, updates itself
	}
	/**
	 * This calls the board for when a human player makes a move.
	 * In the final version this should throw an exception for bad moves to the UI.
	 * 
	 * @param n the player whose turn it is.
	 */
	public void playerTurn(int n/**, some representation of a move */){
		//check which player's turn it is(players[n])
		//check if move is legal
		//update board
	}
	
	
	
		
		

		
		
	
}
