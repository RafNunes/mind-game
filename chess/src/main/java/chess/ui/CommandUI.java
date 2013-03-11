package chess.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.game.Move;
import chess.piece.Piece;
import chess.util.Colour;

/**
 * 
 * @author RafaelNunes
 * 
 */
public class CommandUI implements UI {
	private final Scanner in;

	/**
	 * Constructor
	 */
	public CommandUI() {
		in = new Scanner(System.in);
	}

	/**
	 * Get the move input from stdin. It checks if it is a valid square using
	 * regex.
	 * 
	 * @return - a string to be used to construct the move class.
	 */
	public String getMoveInput() {
		Matcher matcher;
		Pattern pattern = Pattern.compile("[a-h][1-8][ ]*[a-h][1-8](\\([q,n]\\))?");
		System.out.print("Please chose next move: ");
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(System.in));

		while (true) {
			matcher = pattern.matcher(in.nextLine().toLowerCase());
			if (matcher.find()) {

				return matcher.group();
			}
			// try {
			// matcher = pattern.matcher(in.readLine().toLowerCase());
			// if (matcher.find()) {
			// return matcher.group();
			// }
			// }catch (IOException ignore) {
			// } finally {
			// try {
			// in.close();
			// } catch (IOException ignore) {
			// }
			// }
			System.out.print("Invalid move, please try again : ");
		}
	}

	public void displayLegalMoves(LinkedList<Move> legalMoves) {

		System.out.println("legal moves from this position: ");
		for (Move m : legalMoves) {

			System.out.print(m.toString() + ", ");
		}
		System.out.print("\n\n");
	}

	public void displayWinner(int player) {

		System.out.println("\n\nGame Over, Player " + (player + 1) + " wins\n\n");
	}

	public void displayStalemate() {

		System.out.println("\n\n Game Over, Stalemate.\n\n");
	}

	/**
	 * Gets a player name by the colour.
	 * 
	 * @param colour
	 * @return
	 */
	public String getPlayerName(Colour colour) {
		System.out.print("Enter player name (" + colour + "): ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				return in.readLine();
			} catch (IOException ignore) {
			} finally {
				try {
					in.close();
				} catch (IOException ignore) {
				}
			}
			System.out.print("Error, please try again : ");
		}
	}

	/**
	 * Outputs the board to stdout
	 * 
	 * @param pieces
	 */
	public void displayBoard(List<Piece> pieces) {
		String[] board = getBoardRepresentation(pieces);
		String square = " | ";
		String edge = "  +---+---+---+---+---+---+---+---+\n";
		StringBuilder builder = new StringBuilder();
		builder.append("    A   B   C   D   E   F   G   H\n");
		builder.append(edge);
		for (int i = 7; i >= 0; i--) {
			builder.append(i + 1).append(square);
			for (int j = 0; j < 8; j++) {
				builder.append((board[i * 8 + j] == null ? " " : board[i * 8 + j]));
				builder.append(square);
			}
			builder.append("\n").append(edge);
		}
		System.out.print(new String(builder));
	}

	/**
	 * constructs a array of string using the list of pieces
	 * 
	 * @param pieces
	 * @return
	 */
	private String[] getBoardRepresentation(List<Piece> pieces) {
		String[] board = new String[64];
		for (Piece piece : pieces) {
			board[convertPositionToArrayIndex(piece)] = getPieceRepresentation(piece);
		}
		return board;
	}

	/**
	 * returns a string representing the piece
	 * 
	 * @param piece
	 * @return
	 */
	private String getPieceRepresentation(Piece piece) {
		boolean isWhite = piece.getColour().equals(Colour.WHITE);
		String str;
		if (piece.getType().equals(Piece.Type.KNIGHT)) {
			str = "N";
		} else {
			str = piece.getType().name().substring(0, 1);
		}
		if (isWhite) {
			return str.toLowerCase();
		}
		return str;
	}

	/**
	 * returns the 64 cell index corresponding to the 0x88 index stored in piece
	 * 
	 * @param piece
	 * @return
	 */
	private int convertPositionToArrayIndex(Piece piece) {

		int file07 = piece.getPosition() & 7;
		int rank07 = piece.getPosition() >> 4;
		return (8 * rank07) + file07;
	}

	@Override
	public void processInput(String input) {
		// TODO Auto-generated method stub

	}

}
