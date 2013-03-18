/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

import java.util.List;

import chess.piece.Piece;
import chess.ui.UI;
import chess.ui.XboardUI;

public class Game {

	public static final String AI_MOVE = "AI";

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

		// Create new game and AI engine
		gameBoard = new Board();
		ai = new BestAI();
		// By default, use the xboard UI
		ui = new XboardUI();
		String input;
		Move move;
		do {
			input = ui.readInput();
			for (Move availableMoves : gameBoard.generateMoves()) {
				if (availableMoves.matches(input)) {
					gameBoard.makeMove(availableMoves);
					break;
				}
			}
			move = ai.makeMove(gameBoard);
			gameBoard.makeMove(move);
			ui.write(move.toString());
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
		Game.write("st 30");
	}

	public static List<Piece> getPieces() {
		return gameBoard.getPieces();
	}
}
