package chess.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import chess.piece.Pawn;
import chess.piece.Piece;
import chess.piece.PieceList;
import chess.piece.PieceListNode;
import chess.util.Colour;

/**
 * 
 * The AI used in the final version of the product
 * 
 * @author Demian Till
 * 
 */
public class BestAI implements AI {

	// some utility classes:

	/*
	 * Stores values used in the evaluator for weighting all of the different factors considered
	 */
	private class Values {

		public int pawn;
		public int rook;
		public int bishop;
		public int knight;
		public int queen;
		public int valuePerAvailableMove;
		public int valueForPotentialCastle; // will get this twice if king can
											// still castle on both sides
		public int valueForAttackingInnerCenralSquare;
		public int valueForAttackingOuterCenralSquare;
		public int valueForOccupyingInnerCentralSquare;
		public int valueForOccupyingOuterCentralSquare;
		public int sidePawnPenalty;
		public int doubledPawnPenalty;
		public int isolatedPawnPenalty;
		public int passedPawnBonus;
		public int kingOnBackRowDuringMiddleGame; // not relevant during end
													// game
		public int kingClearSideDuringMiddleGame; // not relevant during end
													// game
		public int kingNearSideDuringMiddleGame; // not relevant during end game
		public int kingPawnGuardBonus; // not relevant during end game
		public int kingInInnerCentreDuringEndGame; // not relevant during middle
													// game
		public int kingInOuterCentreDuringEndGame; // not relevant during middle
													// game
		public int penaltyForOpponentAttackingSquareByKing;
		public int bonusForDefendingSquareByKing;
	}

	/*
	 * Allows the search method to pass Moves around with their associated estimated values
	 */
	private class MoveValuePair {
		public MoveValuePair(Move m, int v) {
			move = m;
			value = v;
		}

		public Move move;
		public int value;
	}

	private class MoveValuePairComparator implements Comparator<MoveValuePair> {

		@Override
		public int compare(MoveValuePair mvp1, MoveValuePair mvp2) {

			if (mvp1.value > mvp2.value)
				return -1;
			else if (mvp2.value > mvp1.value)
				return 1;
			else
				return 0;
		}
	}

	// Need value >= to absolute value of any position. Cannot use
	// Integer.MAX_VALUE because we need
	// to be able to negate it and negate the negation.
	private static final int maxVal = 999999999;

	// if the total value of the material on the board is less than this then we
	// are in the end game
	private static final int middleGameMinValue = 15;

	private final Values midGameValues;
	private final Values endGameValues;
	private Values currentValues;

	// first depth we search to for ordering the moves to help alpha-beta make
	// the full depth search quicker
	private int gaugingDepth;
	private int fullDepth;
	private final int fullDepthWidth; // '-1' to consider all possible moves
	// this should not be touched. It is automatically set at the right times
	// depending on which of the above are set
	private int maxDepth;

	private final boolean enableQuiescenceCheckDuringGaugeSearch;
	private final boolean enableQuiescenceCheckDuringFinalSearch;
	// this should not be touched. It is automatically set at the right times
	// depending on which of the above are set
	private boolean quiescenceCheckEnabled;

	/*
	 * Constructor - sets all values to be used during searching and evaluating.
	 * Keeps all such value assignments in the same place.
	 */
	public BestAI() {

		fullDepth = Integer.valueOf(System.getProperty("depth", "6"));
		gaugingDepth = (fullDepth == 1 ? 1 : fullDepth - 1);
		fullDepthWidth = -1;
		enableQuiescenceCheckDuringGaugeSearch = false;
		enableQuiescenceCheckDuringFinalSearch = false;

		midGameValues = new Values();
		midGameValues.pawn = 100;
		midGameValues.rook = 500;
		midGameValues.knight = 300;
		midGameValues.bishop = 330;
		midGameValues.queen = 900;
		midGameValues.valueForAttackingInnerCenralSquare = 7;
		midGameValues.valueForAttackingOuterCenralSquare = 3;
		midGameValues.valueForOccupyingInnerCentralSquare = 20;
		midGameValues.valueForOccupyingOuterCentralSquare = 10;
		midGameValues.valueForPotentialCastle = 20;
		midGameValues.valuePerAvailableMove = 6;
		midGameValues.kingOnBackRowDuringMiddleGame = 20;
		midGameValues.kingClearSideDuringMiddleGame = 20;
		midGameValues.kingPawnGuardBonus = 20;
		midGameValues.penaltyForOpponentAttackingSquareByKing = -50;
		midGameValues.bonusForDefendingSquareByKing = 50;
		midGameValues.sidePawnPenalty = -30;
		midGameValues.doubledPawnPenalty = -20;
		midGameValues.isolatedPawnPenalty = 10;
		midGameValues.passedPawnBonus = 40;

		endGameValues = new Values();
		endGameValues.pawn = 100;
		endGameValues.rook = 500;
		endGameValues.knight = 300;
		endGameValues.bishop = 330;
		endGameValues.queen = 900;
		endGameValues.valueForAttackingInnerCenralSquare = 7;
		endGameValues.valueForAttackingOuterCenralSquare = 3;
		endGameValues.valueForOccupyingInnerCentralSquare = 20;
		endGameValues.valueForOccupyingOuterCentralSquare = 10;
		endGameValues.valueForPotentialCastle = 0;
		endGameValues.valuePerAvailableMove = 6;
		endGameValues.penaltyForOpponentAttackingSquareByKing = -50;
		endGameValues.bonusForDefendingSquareByKing = 50;
		endGameValues.sidePawnPenalty = -30;
		endGameValues.doubledPawnPenalty = -20;
		endGameValues.isolatedPawnPenalty = 10;
		endGameValues.passedPawnBonus = 40;
		endGameValues.kingInInnerCentreDuringEndGame = 40;
		endGameValues.kingInOuterCentreDuringEndGame = 30;

		currentValues = midGameValues;
	}

	@Override
	public Move makeMove(Board board) {
		LinkedList<Move> moves = board.generateMoves();

		if (moves.isEmpty())
			return null;

		// make a MoveValuePair for each move
		LinkedList<MoveValuePair> moveValuePairs = new LinkedList<MoveValuePair>();
		for (Move m : moves) {

			moveValuePairs.add(new MoveValuePair(m, 0)); // this value will not
															// be used
		}

		// initial 'gauge' search

		maxDepth = gaugingDepth;
		quiescenceCheckEnabled = enableQuiescenceCheckDuringGaugeSearch;
		for (MoveValuePair mvp : moveValuePairs) {

			board.tryMove(mvp.move);
			MoveValuePair bestCounter = getBestMove(board, 1, maxVal);
			board.undoMove();
			mvp.value = -bestCounter.value;
		}

		// sort based on these values to help alpha-beta pruning when we do the
		// next search
		Collections.sort(moveValuePairs, new MoveValuePairComparator());

		// narrow the search down to the n most promising.
		int width;
		if (fullDepthWidth == -1)
			width = moveValuePairs.size();
		else
			width = fullDepthWidth;
		LinkedList<MoveValuePair> bestPairs = new LinkedList<MoveValuePair>();
		for (int i = 0; i < width; i++) {

			bestPairs.addLast(moveValuePairs.get(i));
		}

		// final full depth search

		maxDepth = fullDepth;
		quiescenceCheckEnabled = enableQuiescenceCheckDuringFinalSearch;

		// this will be overridden unless all moves happen to have value -maxVal
		MoveValuePair bestPair = new MoveValuePair(moves.getFirst(), -maxVal);

		for (MoveValuePair mvp : bestPairs) {

			board.tryMove(mvp.move);
			MoveValuePair bestCounter = getBestMove(board, 1, -bestPair.value);
			board.undoMove();

			// if the best counter that the opponent can make to this move is
			// better for us then the previously best move
			// that we could make, then store this move as the best move that we
			// can make.
			if (-bestCounter.value > bestPair.value) {

				bestPair = new MoveValuePair(mvp.move, -bestCounter.value);
			}
		}

		return bestPair.move;
	}

	/*
	 * returns the best move from the current position from the perspective of the player whose
	 * turn it is in this position (not necessarily the player that the AI is representing).
	 * If it finds a move better than the upperBound, it will stop the search and return that move.
	 * 
	 * @param - the board containing the state of the game in which a move must be chosen
	 * @param - depth of the current position
	 * @param - upperbound, if we find a better move, we will stop and return that move
	 */
	private MoveValuePair getBestMove(Board board, int currentDepth, int upperBound) {

		LinkedList<Move> moves = board.generateMoves();

		if (currentDepth >= maxDepth) {

			// this should be left 'true' if either the position is quiescent or
			// we are not checking for quiescence.
			// Otherwise 'moves' will be filtered to leave only the dangerous
			// moves and will be searched further.
			boolean stopHereAndEvaluate = true;

			if (quiescenceCheckEnabled) {

				LinkedList<Move> dangerousMoves = filterDangerousMoves(moves);
				if (dangerousMoves.size() != 0) {

					stopHereAndEvaluate = false;
					moves = dangerousMoves;
				}
			}

			if (stopHereAndEvaluate) {

				int value = evaluate(board, moves);
				return new MoveValuePair(null, value);
			}
		}

		// check for checkmate or stalemate.
		// For now, we don't like stalemate. In the future, this should depend
		// on how strong a position we are in.
		if (moves.isEmpty())
			return new MoveValuePair(null, -maxVal);
		else {

			// make a list of moveValuePair's for each move, assign a very cheap
			// and rough value to each, and order them accordingly
			LinkedList<MoveValuePair> moveValuePairs = new LinkedList<MoveValuePair>();
			for (Move m : moves) {

				int value = 0;
				if (m.getCapture() != null) {

					switch (m.getCapture().getType()) {

					case PAWN:
						value += currentValues.pawn;
						break;
					case ROOK:
						value += currentValues.rook;
						break;
					case BISHOP:
						value += currentValues.bishop;
						break;
					case KNIGHT:
						value += currentValues.knight;
						break;
					case QUEEN:
						value += currentValues.queen;
						break;
					default:
					}
				}

				moveValuePairs.add(new MoveValuePair(m, value));
			}
			Collections.sort(moveValuePairs, new MoveValuePairComparator());

			// this will be overridden unless all moves happen to have value
			// -maxVal
			MoveValuePair bestPair = new MoveValuePair(moves.getFirst(), -maxVal);

			for (MoveValuePair mvp : moveValuePairs) {

				board.tryMove(mvp.move);
				MoveValuePair bestCounter = getBestMove(board, currentDepth + 1, -bestPair.value);
				board.undoMove();

				// if the best counter that the opponent can make to this move
				// is better for us then the previously best move
				// that we could make, then store this move as the best move
				// that we can make.
				if (-bestCounter.value > bestPair.value) {

					bestPair = new MoveValuePair(mvp.move, -bestCounter.value);

					// also, it has a chance of being >= to the upperBound
					if (-bestCounter.value >= upperBound) {

						return bestPair;
					}
				}
			}
			return bestPair;
		}
	}

	/*
	 * @param - list of moves that should be filtered
	 * 
	 * @return - provided list filtered leaving only 'dangerous' moves
	 */
	private LinkedList<Move> filterDangerousMoves(LinkedList<Move> moves) {

		LinkedList<Move> dangerousMoves = new LinkedList<Move>();

		for (Move m : moves) {

			if (m.getCapture() != null && !(m.getCapture() instanceof Pawn)) {

				dangerousMoves.add(m);
			}
		}

		return dangerousMoves;
	}

	/*
	 * evaluates the board from the perspective of whoever's turn it is based 
	 * on the current state of the board. It takes the list of legal moves from this
	 * position. This is for efficiency. It is very demanding to compute this, and since
	 * it is likely it will have been computed anyway before this method is called, it should
	 * be passed in so that we don't have to generate them again.
	 * 
	 * @param - game board
	 * @param - legal moves from this position
	 */
	private int evaluate(Board b, LinkedList<Move> moves) {

		int whitesMaterial = getMaterial(b, Colour.WHITE);
		int blacksMaterial = getMaterial(b, Colour.BLACK);

		int totalMaterial = whitesMaterial + blacksMaterial;
		if (totalMaterial > middleGameMinValue)
			currentValues = midGameValues;
		else {
			currentValues = endGameValues;
			gaugingDepth = 5;
			fullDepth = 6;
		}
		int value = 0;
		Colour ourColour = b.getThisPlayer();
		Colour theirColour = b.getOtherPlayer();

		// consider material value
		if (ourColour == Colour.WHITE) {

			value += whitesMaterial - blacksMaterial;
		} else {

			value += blacksMaterial - whitesMaterial;
		}

		// consider num available moves
		value += moves.size() * currentValues.valuePerAvailableMove;
		value -= b.generateMovesFromOpponentsPerspective().size() * currentValues.valuePerAvailableMove;

		// consider whether or not castling is still a possibility
		if (b.isLeftCastlePossible(ourColour))
			value += currentValues.valueForPotentialCastle;
		if (b.isRightCastlePossible(ourColour))
			value += currentValues.valueForPotentialCastle;
		if (b.isLeftCastlePossible(theirColour))
			value -= currentValues.valueForPotentialCastle;
		if (b.isRightCastlePossible(theirColour))
			value -= currentValues.valueForPotentialCastle;

		// consider pieces attacking central squares
		value += evaluateCentralControl(b);

		// consider pawn structure
		value += evaluatePawnStructure(b);

		if (currentValues == midGameValues) {

			// consider king safety
			value += evaluateKingSafety(b, ourColour);
			value -= evaluateKingSafety(b, theirColour);
		} else {

			// consider king mobility
			value += evaluateKingMobility(b, ourColour);
			value -= evaluateKingMobility(b, theirColour);
		}

		return value;
	}

	/*
	 * calculates the net gain/loss resulting from pawn structure from the perspective 
	 * of the player whose turn it is. considers doubled pawns, side pawns, passed pawns and isolated pawns
	 */
	private int evaluatePawnStructure(Board b) {

		// each array has an entry for each column on the board. The entry
		// contains
		// the row number of the corresponding pawn (0 - 7) or -1 if there is no
		// corresponding pawn. For each colour we need to note the farthest
		// forward and
		// Farthest back on each column for passed pawn determination.
		int[] whitePawnsFarthestForwardOnEachCol = new int[] { -1, -1, -1, -1, -1, -1, -1, -1 };
		int[] whitePawnsFarthestBackOnEachCol = new int[] { -1, -1, -1, -1, -1, -1, -1, -1 };
		int[] blackPawnsFarthestForwardOnEachCol = new int[] { -1, -1, -1, -1, -1, -1, -1, -1 };
		int[] blackPawnsFarthestBackOnEachCol = new int[] { -1, -1, -1, -1, -1, -1, -1, -1 };

		int whitesNumDoubled = 0;
		int whitesNumSide = 0;
		int blacksNumDoubled = 0;
		int blacksNumSide = 0;

		PieceList whitesPieceList = b.getWhitesList();
		PieceList blacksPieceList = b.getBlacksList();

		for (PieceListNode node : whitesPieceList) {

			if (node.getPiece().getType() == Piece.Type.PAWN) {

				int column = node.getPiece().getPosition() & 7;
				int row = node.getPiece().getPosition() >> 4;

				if (column == 0 || column == 7)
					whitesNumSide++;

				// if none on this column yet, then this is both the farthest
				// forward and back
				if (whitePawnsFarthestForwardOnEachCol[column] == -1 && whitePawnsFarthestBackOnEachCol[column] == -1) {

					whitePawnsFarthestForwardOnEachCol[column] = row;
					whitePawnsFarthestBackOnEachCol[column] = row;
				} else {

					// may need to update farthest forward or back
					whitePawnsFarthestForwardOnEachCol[column] = Math.max(whitePawnsFarthestForwardOnEachCol[column],
							row);
					whitePawnsFarthestBackOnEachCol[column] = Math.min(whitePawnsFarthestBackOnEachCol[column], row);

					// certainly need to note down another doubled pawn
					whitesNumDoubled++;
				}
			}
		}

		for (PieceListNode node : blacksPieceList) {

			if (node.getPiece().getType() == Piece.Type.PAWN) {

				int column = node.getPiece().getPosition() & 7;
				int row = node.getPiece().getPosition() >> 4;

				if (column == 0 || column == 7)
					blacksNumSide++;

				// if none on this column yet, then this is both the farthest
				// forward and back
				if (blackPawnsFarthestForwardOnEachCol[column] == -1 && blackPawnsFarthestBackOnEachCol[column] == -1) {

					blackPawnsFarthestForwardOnEachCol[column] = row;
					blackPawnsFarthestBackOnEachCol[column] = row;
				} else {

					// may need to update farthest forward or back
					blackPawnsFarthestForwardOnEachCol[column] = Math.min(blackPawnsFarthestForwardOnEachCol[column],
							row);
					blackPawnsFarthestBackOnEachCol[column] = Math.max(blackPawnsFarthestBackOnEachCol[column], row);

					// certainly need to note down another doubled pawn
					blacksNumDoubled++;
				}
			}
		}

		int whitesNumIsolated = 0;
		int blacksNumIsolated = 0;

		// to keep the complexity down, you only get penalised once for each
		// column with an isolated pawn
		for (int col = 0; col < 8; col++) {

			// first check that we have a pawn on this column
			if (whitePawnsFarthestForwardOnEachCol[col] != -1) {

				if (col != 0) {

					if (whitePawnsFarthestForwardOnEachCol[col - 1] != -1) {

						// then there is a pawn on col - 1
						break;
					}
				}
				if (col != 7) {

					if (whitePawnsFarthestForwardOnEachCol[col + 1] != -1) {

						break;
					}
				}
				// if we got here then we have no neighbours
				whitesNumIsolated++;
			}
		}
		for (int col = 0; col < 8; col++) {

			// first check that we have a pawn on this column
			if (whitePawnsFarthestForwardOnEachCol[col] != -1) {

				if (col != 0) {

					if (blackPawnsFarthestForwardOnEachCol[col - 1] != -1) {

						// then there is a pawn on col - 1
						break;
					}
				}
				if (col != 7) {

					if (blackPawnsFarthestForwardOnEachCol[col + 1] != -1) {

						break;
					}
				}
				// if we got here then we have no neighbours
				blacksNumIsolated++;
			}
		}

		int whitesNumPassed = 0;
		int blacksNumPassed = 0;

		// similarly, only benefit once for each column with a passed pawn
		for (int col = 0; col < 8; col++) {

			int ourRow = whitePawnsFarthestForwardOnEachCol[col];

			if (ourRow != -1) {

				if (blackPawnsFarthestBackOnEachCol[col] != -1 && blackPawnsFarthestBackOnEachCol[col] > ourRow) {

					// black pawn blocking, not passed
					break;
				}
				if ((col != 0) && (blackPawnsFarthestBackOnEachCol[col - 1] != -1)
						&& (blackPawnsFarthestBackOnEachCol[col - 1] > ourRow)) {

					break;
				}
				if ((col != 7) && (blackPawnsFarthestBackOnEachCol[col + 1] != -1)
						&& (blackPawnsFarthestBackOnEachCol[col + 1] > ourRow)) {

					break;
				}
				// if we got here then there are no pawns blocking our way
				whitesNumPassed++;
			}
		}
		for (int col = 0; col < 8; col++) {

			int ourRow = blackPawnsFarthestForwardOnEachCol[col];

			if (ourRow != -1) {

				if (whitePawnsFarthestBackOnEachCol[col] != -1 && whitePawnsFarthestBackOnEachCol[col] < ourRow) {

					// white pawn blocking, not passed
					break;
				}
				if ((col != 0) && (whitePawnsFarthestBackOnEachCol[col - 1] != -1)
						&& (whitePawnsFarthestBackOnEachCol[col - 1] < ourRow)) {

					break;
				}
				if ((col != 7) && (whitePawnsFarthestBackOnEachCol[col + 1] != -1)
						&& (whitePawnsFarthestBackOnEachCol[col + 1] < ourRow)) {

					break;
				}
				// if we got here then there are no pawns blocking our way
				blacksNumPassed++;
			}
		}

		int score = 0;
		// finally we have all of the required information so we can assign a
		// score
		if (b.getThisPlayer() == Colour.WHITE) {

			score -= whitesNumSide * currentValues.sidePawnPenalty;
			score -= whitesNumDoubled * currentValues.doubledPawnPenalty;
			score -= whitesNumIsolated * currentValues.isolatedPawnPenalty;
			score += whitesNumPassed * currentValues.passedPawnBonus;

			score += blacksNumSide * currentValues.sidePawnPenalty;
			score += blacksNumDoubled * currentValues.doubledPawnPenalty;
			score += blacksNumIsolated * currentValues.isolatedPawnPenalty;
			score -= blacksNumPassed * currentValues.passedPawnBonus;
		} else {

			score -= blacksNumSide * currentValues.sidePawnPenalty;
			score -= blacksNumDoubled * currentValues.doubledPawnPenalty;
			score -= blacksNumIsolated * currentValues.isolatedPawnPenalty;
			score += blacksNumPassed * currentValues.passedPawnBonus;

			score += whitesNumSide * currentValues.sidePawnPenalty;
			score += whitesNumDoubled * currentValues.doubledPawnPenalty;
			score += whitesNumIsolated * currentValues.isolatedPawnPenalty;
			score -= whitesNumPassed * currentValues.passedPawnBonus;
		}

		return score;
	}

	/*
	 * gets value of material for player of colour c
	 */
	private int getMaterial(Board b, Colour c) {

		PieceList pieces;
		if (c == Colour.WHITE)
			pieces = b.getWhitesList();
		else
			pieces = b.getBlacksList();

		int value = 0;
		for (PieceListNode node : pieces) {
			switch (node.getPiece().getType()) {

			case PAWN:
				value += currentValues.pawn;
				break;

			case ROOK:
				value += currentValues.rook;
				break;

			case BISHOP:
				value += currentValues.bishop;
				break;

			case KNIGHT:
				value += currentValues.knight;
				break;

			case QUEEN:
				value += currentValues.queen;
				break;
			default:
			}
		}

		return value;
	}

	/*
	 * calculates the net gain/loss resulting from central control from the perspective 
	 * of the player whose turn it is
	 */
	private int evaluateCentralControl(Board b) {

		Colour ourColour = b.getThisPlayer();

		int value = 0;

		byte[] innerCentralSquares = new byte[] { 51, 52, 67, 68 };
		byte[] outerCentralSquares = new byte[] { 35, 36, 37, 38, 50, 53, 66, 69, 82, 83, 84, 85 };

		for (byte square : innerCentralSquares) {

			LinkedList<Piece> attackingPieces = b.getPiecesAttackingSquare(square);
			for (Piece piece : attackingPieces) {

				if (piece.getColour() == ourColour)
					value += currentValues.valueForAttackingInnerCenralSquare;
				else
					value -= currentValues.valueForAttackingInnerCenralSquare;
			}

			// also consider pieces occupying central squares
			Piece p = b.getPieceAt(square);
			if (p != null) {

				if (p.getColour() == ourColour)
					value += currentValues.valueForOccupyingInnerCentralSquare;
				else
					value -= currentValues.valueForOccupyingInnerCentralSquare;
			}
		}

		for (byte square : outerCentralSquares) {

			LinkedList<Piece> attackingPieces = b.getPiecesAttackingSquare(square);
			for (Piece piece : attackingPieces) {

				if (piece.getColour() == ourColour)
					value += currentValues.valueForAttackingOuterCenralSquare;
				else
					value -= currentValues.valueForAttackingOuterCenralSquare;
			}

			// also consider pieces occupying central squares
			Piece p = b.getPieceAt(square);
			if (p != null) {

				if (p.getColour() == ourColour)
					value += currentValues.valueForOccupyingOuterCentralSquare;
				else
					value -= currentValues.valueForOccupyingOuterCentralSquare;
			}
		}
		return value;
	}

	/*
	 * Generates a value based on the safety of the king of colour c.
	 * For use during early/mid game only
	 */
	int evaluateKingSafety(Board b, Colour c) {

		int backRow;
		int forwards;
		byte kingPos;
		if (c == Colour.WHITE) {

			backRow = 0;
			forwards = 16;
			kingPos = b.getWhiteKingPos();
		} else {

			backRow = 7;
			forwards = -16;
			kingPos = b.getBlackKingPos();
		}

		int value = 0;

		int kingCol = kingPos & 7;
		int kingRow = kingPos >> 4;

		byte towardsSide;
		if (kingCol <= 3)
			towardsSide = -1;
		else
			towardsSide = 1;

		// assign bonus for being on back row
		if (kingRow == backRow)
			value += currentValues.kingOnBackRowDuringMiddleGame;

		// assign bonus for being near the side
		if (kingCol <= 1 || kingCol >= 6)
			value += currentValues.kingClearSideDuringMiddleGame;

		// check whether squares between king and side are empty
		boolean empty = true;
		for (byte nextSquare = (byte) (towardsSide + kingPos); (nextSquare & 0x88) == 0; nextSquare += towardsSide) {

			if (b.getPieceAt(nextSquare) != null) {

				empty = false;
				break;
			}
		}
		if (empty) {

			value += currentValues.kingNearSideDuringMiddleGame;
		}

		// now check for pawns in front of king. Only check if king is on one of
		// back two rows.
		if (kingRow == backRow || (kingRow - forwards) == backRow) {

			Piece forwardOne = b.getPieceAt((byte) (kingPos + forwards));
			if (forwardOne != null && forwardOne.getColour() == c && forwardOne.getType() == Piece.Type.PAWN) {

				value += currentValues.kingPawnGuardBonus;
			}
			if (kingCol != 0) {

				Piece forwardLeft = b.getPieceAt((byte) (kingPos + forwards - 1));
				if (forwardLeft != null && forwardLeft.getColour() == c && forwardLeft.getType() == Piece.Type.PAWN) {

					value += currentValues.kingPawnGuardBonus;
				}
			}
			if (kingCol != 7) {

				Piece forwardRight = b.getPieceAt((byte) (kingPos + forwards + 1));
				if (forwardRight != null && forwardRight.getColour() == c && forwardRight.getType() == Piece.Type.PAWN) {

					value += currentValues.kingPawnGuardBonus;
				}
			}
		}

		// now check for pieces attacking squares around king
		byte[] squaresAroundKing = new byte[] { (byte) (kingPos + 15), (byte) (kingPos + 16), (byte) (kingPos + 17),
				(byte) (kingPos - 1), /* kingPos */(byte) (kingPos + 1), (byte) (kingPos - 17), (byte) (kingPos - 16),
				(byte) (kingPos - 15) };

		for (byte square : squaresAroundKing) {

			if ((square & 0x88) == 0) {

				LinkedList<Piece> piecesAttackingSquare = b.getPiecesAttackingSquare(square);
				for (Piece p : piecesAttackingSquare) {

					if (p.getColour() == c) {

						value += currentValues.bonusForDefendingSquareByKing;
					} else {

						value += currentValues.penaltyForOpponentAttackingSquareByKing;
					}
				}
			}
		}

		return value;
	}

	/*
	 * generates a value based on the mobility of the king of colour c.
	 * For use during end game only
	 */
	int evaluateKingMobility(Board b, Colour c) {

		byte kingPos;
		if (c == Colour.WHITE) {

			kingPos = b.getWhiteKingPos();
		} else {

			kingPos = b.getBlackKingPos();
		}

		int kingRow = kingPos >> 4;
		int kingCol = kingPos & 7;

		int value = 0;

		// assign bonus if in inner centre
		if ((kingRow == 3 || kingRow == 4) && (kingCol == 3 || kingCol == 4)) {

			value += currentValues.kingInInnerCentreDuringEndGame;
		} else if (kingRow >= 2 && kingRow <= 5 && kingCol >= 2 && kingRow <= 5) {

			value += currentValues.kingInOuterCentreDuringEndGame;
		}

		return value;
	}
}
