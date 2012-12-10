package chess.game;

import java.util.ArrayList;

import org.junit.Test;

import chess.piece.Piece;

public class CommandUITest {

	private final CommandUI commandUI = new CommandUI();

	@Test
	public void testPrintBoard() {
		commandUI.outputBoard(new ArrayList<Piece>());
	}

}
