/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, 6/12/12
 */

package chess.game;

import chess.piece.Move;
import chess.util.Colour;

public class Game {

	Board gameBoard = new Board();
	Player[] players;

	/**
	 * Player player1;
	 * Player player2;
	 */
	public Game() {
		gameBoard = new Board();
		Player[] players = new Player[2];
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
	 * Indicates an AI turn. Makes its own move, all it needs to know is which
	 * side it's playing.
	 * 
	 * @param n
	 *            the player whose turn it is.
	 */
	public void AITurn(int n) {
		// Call board class(Send colour of players[n])
		// Board class does move generation, evaluation, updates itself
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
		// TODO run the game.
	}

}
