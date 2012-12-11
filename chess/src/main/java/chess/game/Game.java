/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, 6/12/12
 */

package chess.game;

import chess.piece.Move;
import chess.util.Colour;
import java.util.LinkedList;

public class Game {

	Board gameBoard = new Board();
	Player[] players;

	public static void main(String[] args) {

		Game game = new Game();
		game.run();
	}

	public Game() {
		gameBoard = new Board();
		players = new Player[2];
		players[0] = new Human();
		players[1] = new Human();
	}

	public Board getBoard() {
		return gameBoard;
	}

	public void setPlayerColour(int n, Colour c) {
		if (n < 0 || n > 1) {
			players[n] = new Human(c);// this will be changed later.
		}
	}

	public int getPlayerType(int n) {
		if (n != 0 || n != 1) {
			System.err.println("Bad player number");
		}
		return players[n].getPlayerType();

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
		ui.displayBoard(gameBoard.getPieces());
		while(!gameBoard.checkMate()) {
			
			LinkedList<Move> legalMoves = gameBoard.generateMoves();
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
				// have a beer :)
			}
			gameBoard.makeMove(move);
			ui.displayBoard(gameBoard.getPieces());
			
			if(turn == 0) turn = 1;
			else turn = 0;
		}
	}

}
