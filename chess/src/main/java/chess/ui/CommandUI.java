package chess.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.piece.Piece;
import chess.util.Colour;

/**
 * 
 * @author RafaelNunes
 * 
 */
public class CommandUI implements UI {

	private static Pattern coordinateMovePattern = Pattern.compile(".*([a-h][1-8][\\W]*[a-h][1-8][qrnbQRNB]?)");

	private final BufferedReader stdin;

	public CommandUI() {
		stdin = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public String readInput() {
		String input;
		do {
			try {
				input = stdin.readLine();
				if (coordinateMovePattern.matcher(input).matches()) {
					Matcher matcher = coordinateMovePattern.matcher(input);
					if (matcher.find())
						return matcher.group(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (true);
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
	public void write(String move) {
		System.out.println(move);
		System.out.flush();
	}

}
