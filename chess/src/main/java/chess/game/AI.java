package chess.game;

/**
 * Common interface that must be implemented by all AIs
 * 
 * @author Demian Till
 * 
 */
public interface AI {

	/*
	 * @param - the board containing the state of the game in which a move must be chosen
	 * 
	 * @return - the chosen move
	 */
	Move makeMove(Board board);
}