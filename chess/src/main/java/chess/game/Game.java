/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

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
			if (!input.equalsIgnoreCase(AI_MOVE)) {
				for (Move availableMoves : gameBoard.generateMoves()) {
					if (availableMoves.matches(input)) {
						gameBoard.makeMove(availableMoves);
						break;
					}
				}
			}
			move = ai.makeMove(gameBoard);
			gameBoard.makeMove(move);
			ui.write("move " + move.toString());
		} while (true);
	}

}
