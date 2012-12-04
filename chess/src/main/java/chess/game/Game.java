package chess.game;

import java.util.Scanner;

import chess.piece.Piece;

public class Game {

	Board gameBoard;
	Player[] players;

	/**
	 * Player player1;
	 * Player player2;
	 */
	public static void main(String[] args) {
		Board gameBoard = new Board();
		Player[] players = new Player[2];
		players[0] = new Player();
		players[1] = new Player();
		int turn = 0;
		String s;
		Scanner inputScanner = new Scanner(System.in);

		// set player colours
		System.out.print("Player 1 choose colour: ");
		s = inputScanner.next();

		if (s.toLowerCase().equals("white")) {
			players[0].c = Piece.Colour.WHITE;
			players[1].c = Piece.Colour.BLACK;
		} else if (s.toLowerCase().equals("black")) {
			players[1].c = Piece.Colour.WHITE;
			players[0].c = Piece.Colour.BLACK;
			turn = 1;
		} else {
			System.err.println("bad input");
			System.exit(1);
		}

		while (true) {
			// check board for check(mate)/stalemate
			// if checkmate/stalemate, end
			// else if check, impose check rules
			// check player turn
			// if(user input is a single coordinate
			// return content of that coordinate
			// else if(user input follows the pattern (coordinate to coordinate)
			// Check if this move is legal
			// if so, perform the move
			// iterate turn%2
			// else, prompt user for different input
			// else if(user input requests board
			// board to string
			// else if user input equals quit
			// end
			// else
			// error, retry input
		}

	}
}
