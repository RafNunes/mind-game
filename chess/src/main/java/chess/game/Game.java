/**
 * Game class, acts as intermediary between the board and the UI
 * Current version by David McKenna, Demian Till
 */

package chess.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
		do {
			try {
				input = stdin.readLine();

				if (input.equals("new")) {
					// Start a new game
					ai = new BestAI();
					gameBoard = new Board();
				} else if (input.equals("quit")) {
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

	public static void write(String output) {
		System.out.println(output);
		System.out.flush();
	}

	public static void undo() {
		gameBoard.undoMove();
	}

	public static void setToHard() {
		// TODO Auto-generated method stub

	}

	public static void setToEasy() {
		// TODO Auto-generated method stub

	}

	public static void move(String input) {
		Move thisMove = null;
		Move nextMove = null;
		for (Move move : gameBoard.generateMoves()) {
			if (move.matches(input)) {
				thisMove = move;
			}
		}
		if (thisMove != null) {
			gameBoard.makeMove(thisMove);
			nextMove = ai.makeMove(gameBoard);
			write("move " + nextMove.toString().replaceAll("][- ", ""));
		}
	}
}
