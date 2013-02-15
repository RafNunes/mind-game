package chess.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import chess.piece.PieceList;
import chess.piece.PieceListNode;
import chess.util.Colour;

/**
 * 
 * @author craigmartin
 * Basic AI. Current AI should be able to beat. Currently Draws/Stalemates
 * 
 */

public class PlayAgainstAI extends AI{

	private static final int pawnValue = 10;
	private static final int rookValue = 50;
	private static final int bishopValue = 30;
	private static final int knightValue = 30;
	private static final int queenValue = 90;

	private static final int maxDepth = 4;

	// Need value >= to absolute value of any position. Cannot use Integer.MAX_VALUE because we need 
	// to be able to negate it and negate the negation.
	private static final int maxVal = 999999999;

	/**
	 * Used for King position evaluation
	 */

	private boolean endgame = false;


	private class MoveValuePair {
		public MoveValuePair(Move m, int v) {
			move = m;
			value = v;
		}
		public Move move;
		public int value;
	}

	public class MoveValuePairComparator implements Comparator<MoveValuePair> {

		public int compare(MoveValuePair mvp1, MoveValuePair mvp2) {

			if(mvp1.value > mvp2.value) return -1;
			else if(mvp2.value > mvp1.value) return 1;
			else return 0;
		}
	}

	public Move makeMove(Game game) {

		MoveValuePair pair = getBestMove(game.getBoard(), 0, maxVal);
		return pair.move;
	}

	// returns the best move from the current position from the perspective of the player whose
	// turn it is in this position (not necessarily the player that the AI is representing).
	// If it finds a move better than the upperBound, it will stop the search and return that move.
	private MoveValuePair getBestMove(Board board, int currentDepth, int upperBound) {

		if(currentDepth >= maxDepth) {

			int value = evaluateBoard(board);
			return new MoveValuePair(null, value);
		}
		else {

			LinkedList<Move> moves = board.generateMoves();

			// check for checkmate or stalemate.
			// For now, we don't like stalemate. In the future, this should depend on how strong a position we are in.
			if(moves.isEmpty()) return new MoveValuePair(null, -maxVal);
			else {

				// make a list of moveValuePair's for each move, assign a very cheap and rough value to each, and order them accordingly
				LinkedList<MoveValuePair> moveValuePairs = new LinkedList<MoveValuePair>();
				for(Move m : moves) {

					int value = 0;
					if(m.getCapture() != null) {

						switch(m.getCapture().getType()) {

						case PAWN: value += pawnValue; break;
						case ROOK: value += rookValue; break;
						case BISHOP: value += bishopValue; break;
						case KNIGHT: value += knightValue; break;
						case QUEEN: value += queenValue; break;
						default:
							break;
						}
					}
					moveValuePairs.add(new MoveValuePair(m, value));
				}
				Collections.sort(moveValuePairs, new MoveValuePairComparator());

				// this will be overridden unless all moves happen to have value -maxVal
				MoveValuePair bestPair = new MoveValuePair(moves.getFirst(), -maxVal);

				for(MoveValuePair mvp : moveValuePairs) {

					board.tryMove(mvp.move);
					MoveValuePair bestCounter = getBestMove(board, currentDepth + 1, -bestPair.value);
					board.undoMove();

					// if the best counter that the opponent can make to this move is better for us then the previously best move
					// that we could make, then store this move as the best move that we can make.
					if(-bestCounter.value > bestPair.value) {

						bestPair = new MoveValuePair(mvp.move, -bestCounter.value);

						// also, it has a chance of being >= to the upperBound
						if(-bestCounter.value >= upperBound) {

							return bestPair;
						}
					}
				}
				return bestPair;
			}
		}
	}
	// currently the value is calculated from scratch every time, can we carry this across?
	// evaluates the board from the perspective of whoever's turn it is based on the current state of the board.
	private int evaluateBoard(Board board) {
		int total;
		int value = 0;


		if (!endgame){
			total = getMaterialTotalBlack(board) +  getMaterialTotalWhite(board);
			if (total < 15){
				endgame = true;
			}
		}

		// Assigns points material difference. Low cost & MUST
		value += getDifference(board);

		//Assigns points for available moves. High cost & should
		value += getMovesValue(board);

		return value;
	}
	/*
	 * Creating functions for each evaluation. Keeps code separate
	 *  + allows for easier reading
	 *  + allows functions to be added/removed from evaluator without losing code 
	 */

	/**
	 * Gets the material difference. Low cost, essential for basic decision making
	 * @param board
	 * @return
	 */
	protected  int getDifference(Board board){
		if(board.getThisPlayer() == Colour.WHITE) {

			return getMaterialTotalWhite(board) - getMaterialTotalBlack(board);
		}
		else {

			return getMaterialTotalBlack(board) - getMaterialTotalWhite(board);
		}
	}
	/**
	 * Get the number of available moves. High cost, difficult for exact numbers, should have
	 * @param board
	 * @return
	 */
	protected int getMovesValue(Board board){
		// suppose each available move is worth 0.2 pawns
		return board.generateMoves().size() * 2 - board.generateMovesFromOpponentsPerspective().size() * 2; // pawn is worth 10 
	}

	/**
	 * Created by Craig Martin - useful for evaluating endgame
	 * @param pawnValue
	 * @param rookValue
	 * @param bishopValue
	 * @param knightValue
	 * @param queenValue
	 * @return
	 */
	protected int getMaterialTotalWhite(Board b){
		int value = 0;
		PieceList pieces = b.getWhitesList();

		for(PieceListNode node : pieces) {

			switch(node.getPiece().getType()) {

			case PAWN: value += pawnValue; break;
			case ROOK: value += rookValue; break;
			case BISHOP: value += bishopValue; break;
			case KNIGHT: value += knightValue; break;
			case QUEEN: value += queenValue; break;
			default:


			}
		}
		return value;
	}
	/**
	 * Created by Craig Martin, improves collection of material - useful for denoting an endgame
	 * @param pawnValue
	 * @param rookValue
	 * @param bishopValue
	 * @param knightValue
	 * @param queenValue
	 * @return
	 */
	protected int getMaterialTotalBlack(Board b){
		int value = 0;
		PieceList pieces = b.getBlacksList();

		for(PieceListNode node : pieces) {

			switch(node.getPiece().getType()) {

			//black has the exact same thing but in reverse
			case PAWN: value += pawnValue; break;
			case ROOK: value += rookValue; break;
			case BISHOP: value += bishopValue; break;
			case KNIGHT: value += knightValue; break;
			case QUEEN: value += queenValue; break;
			default:
			}
		}
		return value;
	}


}
