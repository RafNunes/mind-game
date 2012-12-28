package chess.game;

import java.util.LinkedList;

public class AI extends Player{
	
	private static final int pawnValue = 1;
	private static final int rookValue = 5;
	private static final int bishopValue = 3;
	private static final int knightValue = 3;
	private static final int queenValue = 9;
	
	private static final int maxDepth = 4;
	
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
		return pair.move;
	}
	
	// returns the best move from the current position from the perspective of the player whose
	// turn it is in this position (not necessarily the player that the AI is representing).
	private MoveValuePair getBestMove(Board board, int currentDepth) {
		
		if(currentDepth >= maxDepth) {
			
			int value = board.getMaterialDifference(pawnValue, rookValue, bishopValue, knightValue, queenValue);
			return new MoveValuePair(null, value);
		}
		else {
			
			LinkedList<Move> moves = board.generateMoves();
			
			// check for check mate or stalemate
			if(moves.isEmpty()) return new MoveValuePair(null, Integer.MIN_VALUE);
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
}























