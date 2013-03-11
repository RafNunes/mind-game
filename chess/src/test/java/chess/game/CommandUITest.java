package chess.game;

import java.util.ArrayList;

import org.junit.Test;

import chess.piece.Piece;
import chess.ui.CommandUI;

public class CommandUITest {

	private final CommandUI commandUI = new CommandUI();

	@Test
	public void testPrintBoard() {
		commandUI.displayBoard(new ArrayList<Piece>());
	}

}
