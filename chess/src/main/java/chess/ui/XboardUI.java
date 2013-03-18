package chess.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.game.Game;

public class XboardUI implements UI {

	private static Pattern coordinateMovePattern = Pattern.compile(".*([a-h][1-8][\\W]*[a-h][1-8][qrnbQRNB]?)");

	@Override
	public void processInput(String input) {
		if (input.equals("undo")) {
			Game.undo();
			Game.undo();
		} else if (input.equals("remove")) {
			Game.undo();
			Game.undo();
		} else if (input.equals("hard")) {
			Game.setDifficulty(5);
		} else if (input.equals("easy")) {
			Game.setDifficulty(2);
		} else if (input.startsWith("setboard")) {
			String moves = input.substring(9); // After "setboard "
			moves.split(",");
			// TODO
		} else if (input.startsWith("sd")) {
			// TODO
		} else if (input.equalsIgnoreCase("go")) {
			Game.AIMove();
		} else if (coordinateMovePattern.matcher(input).matches()) {
			Matcher m = coordinateMovePattern.matcher(input);
			m.find();
			Game.move(m.group(1));
			Game.AIMove();
		} else {
			// Do nothing - unsupported functions
		}
	}

}
