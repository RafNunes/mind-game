package chess.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.game.Game;

public class XboardUI implements UI {

	private static Pattern coordinateMovePattern = Pattern.compile(".*([a-h][1-8][\\W]*[a-h][1-8][qrnbQRNB]?)");
	private final BufferedReader stdin;

	public XboardUI() {
		stdin = new BufferedReader(new InputStreamReader(System.in));
		write("feature usermove=1");
		write("feature option=NAME -button");
		write("feature done=1");
		write("st 30");
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
				} else if (input.equalsIgnoreCase("go")) {
					return Game.AI_MOVE;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	@Override
	public void write(String output) {
		System.out.println(output);
		System.out.flush();
	}

}
