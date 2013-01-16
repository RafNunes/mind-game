package chess.game;

import java.util.LinkedList;

public class AI extends Player{

	private static final int pawnValue = 10;
	private static final int rookValue = 50;
	private static final int bishopValue = 30;
	private static final int knightValue = 30;
	private static final int queenValue = 90;

	/**
	 * Edited by Craig Martin 11/01/13, Testing for minimax, 
	 * Changes undone 16/01/13, minimax is all good
	 */

	private static final int maxDepth = 5;

	private class MoveValuePair {
		public MoveValuePair(Move m, int v) {
			move = m;
			value = v;
		}
		public Move move;
		public int value;
	}

	public Move makeMove(Game game) {
		MoveValuePair pair = getBestMove(game.getBoard(), 0);
		System.out.println("Chosen Move   " + pair.value + pair.move);
		return pair.move;
	}

	// returns the best move from the current position from the perspective of the player whose
	// turn it is in this position (not necessarily the player that the AI is representing).
	private MoveValuePair getBestMove(Board board, int currentDepth) {

		LinkedList<Move> moves = board.generateMoves();

		// check for checkmate or stalemate.
		// For now, we don't like stalemate. In the future, this should depend on how strong a position we are in.
		if(moves.isEmpty()) return new MoveValuePair(null, Integer.MIN_VALUE);
		else {

			if(currentDepth >= maxDepth) {

				int value = evaluateBoard(board);
				return new MoveValuePair(null, value);
			}
			else {
				
				// this will be overidden unless all moves happen to have value Integer.MIN_VALUE
				MoveValuePair bestPair = new MoveValuePair(moves.getFirst(), Integer.MIN_VALUE);

				for(Move m : moves) {
					
					board.tryMove(m);
					MoveValuePair bestCounter = getBestMove(board, currentDepth + 1);

					// if the best counter that the opponent can make to this move is better for us then the previously best move
					// that we could make, then store this move as the best move that we can make.
					if(-bestCounter.value > bestPair.value) {

						bestPair = new MoveValuePair(m, -bestCounter.value);
					}
					board.undoMove();
				}
				return bestPair;

			}
		}
	}

	// evaluates the board from the perspective of whoever's turn it is based on the current state of the board.
	private int evaluateBoard(Board board) {

		int value = board.getMaterialDifference(pawnValue, rookValue, bishopValue, knightValue, queenValue);

		// suppose each available move is worth 0.2 pawns
		value += board.generateMoves().size() * 2; // pawn is worth 10, not 1...
		value -= board.generateMovesFromOpponentsPerspective().size() * 2;
		return value;
	}
}























