/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

import chess.util.Colour;
import java.util.LinkedList;

public class Game {

	Board gameBoard = new Board();
	Player[] players;

	public static void main(String[] args) {
		Game game;
		if(args.length == 0){
			game = new Game("");
		}
		else{
			game = new Game(args[0]);
		}
		game.run();
	}

	public Game(String args) {
		gameBoard = new Board();
		players = new Player[2];
		players[1] = new AI();
		if(args.contentEquals("AI")){
			players[0] = new PlayAgainstAI();}
		else{
			players[0] = new Human();
		}
	}

	public Board getBoard() {
		return gameBoard;
	}

	/**
	 * This calls the board for when a human player makes a move.
	 * In the final version this should throw an exception for bad moves to the
	 * UI.
	 * 
	 * @param n
	 *            the player whose turn it is.
	 */
	public boolean playerTurn(Move m) {
		boolean successful = false;
		gameBoard.tryMove(m);// Needs a check to see whether move was successful
		return successful;
	}

	public void run() {
		
		CommandUI ui = new CommandUI();
		int turn = 0;
		boolean gameOver = false;
		ui.displayBoard(gameBoard.getPieces());
		while(!gameOver) {
			
			LinkedList<Move> legalMoves = gameBoard.generateMoves();
			if(legalMoves.isEmpty()) gameOver = true;
			else {
				
				Move move = null;

				if(players[turn] instanceof Human) {

					ui.displayLegalMoves(legalMoves);
					while(move == null) {

						String moveInput = ui.getMoveInput();
						for(Move m : legalMoves) {

							if(m.matches(moveInput)) move = m;
						}
					}
				}
				else {
					
					move = ((AI)players[turn]).makeMove(this);
					System.out.println(move);
				}
				gameBoard.makeMove(move);
				ui.displayBoard(gameBoard.getPieces());

				turn = (turn + 1) % 2;
			}
		}
		if(gameBoard.inCheck((turn == 0) ? Colour.WHITE : Colour.BLACK)) {
			
			ui.displayWinner((turn + 1) % 2);
		}
		else {
			
			ui.displayStalemate();
		}
	}
}
