/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

import chess.ui.CommandUI;
import chess.ui.UI;
import chess.ui.XboardUI;

public class Game {

	public static final String AI_MOVE = "AI";
	public static final String COMMAND_LINE = "CL";

	private static UI ui;
	private static Board gameBoard;
	private static AI ai;

	public static void main(String[] args) {
		if (System.getProperty("xboard") != null) {
			ui = new XboardUI();
		} else {
			ui = new CommandUI();
		}
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
		String input;
		Move move;
		do {
			outputBoard();
			input = ui.readInput();
			if (input.equalsIgnoreCase(AI_MOVE)) {
				move = ai.makeMove(gameBoard);
				gameBoard.makeMove(move);
				ui.write("move " + move.toString());
			} else {
				for (Move availableMoves : gameBoard.generateMoves()) {
					if (availableMoves.matches(input)) {
						gameBoard.makeMove(availableMoves);
						move = ai.makeMove(gameBoard);
						gameBoard.makeMove(move);
						ui.write("move " + move.toString());
						break;
					}
				}
			}
		} while (true);
	}

	private static void outputBoard() {
		if (ui instanceof CommandUI) {
			((CommandUI) ui).displayBoard(gameBoard.getPieces());
			ui.write(gameBoard.generateMoves().toString());
		}
	}

}
