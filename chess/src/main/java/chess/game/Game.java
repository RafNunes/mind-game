/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import chess.piece.Piece;
import chess.ui.CommandUI;
import chess.ui.UI;
import chess.ui.XboardUI;

public class Game {

	private static UI ui;
	private static Board gameBoard;
	private static AI ai;

	public static void main(String[] args) {
		Game.run();
	}

	public Board getBoard() {
		return gameBoard;
	}

	public static void run() {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		// Create new game and AI engine
		gameBoard = new Board();
		ai = new BestAI();
		// By default, use the command line UI
		ui = new CommandUI();
		String input;
		write("Type help for list of commands");
		do {
			try {
				input = stdin.readLine();

				if (input.equalsIgnoreCase("new")) {
					// Start a new game
					ai = new BestAI();
					gameBoard = new Board();
					break;
				} else if (input.equalsIgnoreCase("xboard")) {
					ui = new XboardUI();
				} else {
					ui.processInput(input);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (true);
	}

	/**
	 * Writes to System.out.
	 * 
	 * @param output
	 *            String to be written.
	 */
	public static void write(String output) {
		System.out.println(output);
		System.out.flush();
	}

	/**
	 * Reverts the board to the last move in the stack.
	 */
	public static void undo() {
		gameBoard.undoMove();
	}

	public static void setDifficulty(Integer depth) {
		ai = new BestAI();
	}

	/**
	 * Checks if mvoe is legal, then acts it on the board.
	 * 
	 * @param input
	 *            The move to be made
	 * @return Whether or not the move has been made: true if it has, false
	 *         otherwise
	 */
	public static boolean move(String input) {
		Move thisMove = null;
		for (Move move : gameBoard.generateMoves()) {
			if (move.matches(input)) {
				thisMove = move;
			}
		}
		if (thisMove != null) {
			gameBoard.makeMove(thisMove);
			return true;
		}
		return false;
	}

	/**
	 * Called to make the AI generate a move and act it upon the board.
	 */
	public static void AIMove() {
		Move aiMove = ai.makeMove(gameBoard);
		gameBoard.makeMove(aiMove);
		write("move " + aiMove.toString().replaceAll("[\\W]", ""));
	}

	public static List<Piece> getPieces() {
		return gameBoard.getPieces();
	}
}
