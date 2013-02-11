package chess.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import chess.piece.*;
import chess.piece.Piece.Type;
import chess.util.Colour;


/**
 * 
 * @author craigmartin
 * Edited to add several new features to improve evaluation.
 * 
 */

public class AI extends Player{

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

	private boolean middlegame = false;
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

		//check for middle game/ end game

		if (!middlegame){
			if (board.getHasCastled() && board.getMinorDeveloped() > 2){
				middlegame = true;
			}
		}
		else{
			if (!endgame){
				total = getMaterialTotalBlack(board) +  getMaterialTotalWhite(board);
				if (total < 15){
					endgame = true;
					middlegame = false;
				}
			}
		}

		// Assigns points material difference. Low cost & MUST
		// Based on Pawn Protection, center control, int values and development order
		value += getDifference(board);

		//Assigns points for available moves. High cost & should
		value += getMovesValue(board);

		//Assigns points based on how well protected the King is in start game/middle game
		value += getKingProtectionValue(board);

		//Detracts points if castled or unable to castle. However is balanced out by King protection
		value += hasCastled(board);

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
		return board.generateMoves().size() * (pawnValue/10 * 2) - board.generateMovesFromOpponentsPerspective().size() * (pawnValue/10 *2); // pawn is worth 10 
	}




	/**
	 * Created by Craig Martin - useful for evaluating endgame
	 * @param pawnValue
	 * @param rookValue
	 * @param bishopValue
	 * @param knightValue
	 * @param queenValue
	 * @return
	 * 
	 * Numbers for central control provided by David McKenna
	 */                            

	private int getMaterialTotalWhite(Board b){

		int value = 0;
		PieceList pieces = b.getWhitesList();
		PieceListNode[] boardArray = b.getBoardArray();

		for(PieceListNode node : pieces) {
			byte pos = node.getPiece().getPosition();
			switch(node.getPiece().getType()) {
			case PAWN: value += pawnValue;
			if ((pos % 16) == 0 || node.getPiece().getPosition() % (byte)16 == 7){
				value -= (pawnValue /100) * 15;
			}
			if ((pos > (byte)79)){
				value += (pawnValue/10); //temp value - must confirm with David Watt
			}
			if ((pos > (byte)95)){
				value += (pawnValue/10); //temp value -must confirm with David Watt
			}
			/*
			 * Pawn Protection
			 */
			if (b.inRange((byte)(pos - 17))){
				if(!(boardArray[(byte) pos - 17]== null)){
					if(boardArray[(byte) pos - 17].getPiece().getType() == Type.PAWN){
						value += (pawnValue/10);
					}
				}
			}
			if (b.inRange((byte)(pos - 15))){
				if(!(boardArray[(byte) pos - 15] == null)){
					if(boardArray[(byte) pos - 15].getPiece().getType() == Type.PAWN){
						value += (pawnValue/10);
					}
				}
			}
			/*
			 * Central Control
			 */
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*4;
			}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			break;
			case ROOK: value += rookValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// +50% of pawnScore for saving Rook till after two other minor pieces have moved
			if ((!node.getPiece().hasMoved()) && b.getMinorDeveloped() <= 2){
				value += (pawnValue/5);
			}


			break;
			case BISHOP: value += bishopValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;
			}else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			break;
			case KNIGHT: value += knightValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;
			}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// 20% of pawnScore for developing knight before bishop
			if (!node.getPiece().hasMoved()){
				value += getKnightOrderScore(b.getWhitesList());
			}
			break;
			case QUEEN: value += queenValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*3;
			}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// +33% of pawnScore for saving Queen till after two other minor pieces have moved
			if ((!node.getPiece().hasMoved()) && b.getMinorDeveloped() < 3){
				value += (pawnValue/3);
			}
			break;
			default:


			}
		}
		return value;
	}




	/**
	 * Created by Craig Martin, improves collection of material - useful for denoting an endgame
	 * @param board
	 * @return the total value of the black pieces
	 * Numbers for central control provided by David McKenna
	 */
	private int getMaterialTotalBlack(Board b){
		int value = 0;
		PieceList pieces = b.getBlacksList();
		PieceListNode[] boardArray = b.getBoardArray();

		for(PieceListNode node : pieces) {
			byte pos = node.getPiece().getPosition();
			switch(node.getPiece().getType()) {
			case PAWN: value += pawnValue;
			if ((pos % 16) == 0 || node.getPiece().getPosition() % (byte)16 == 7){
				value -= (pawnValue /100) * 15;
			}
			if ((pos < (byte)39)){
				value += (pawnValue/10); //temp value - must confirm with David Watt - must be extended to watch for blocking/attacking pieces
			}
			if ((pos < (byte)23)){
				value += (pawnValue/10); //temp value -must confirm with David Watt  - must be extended to watch for blocking/attacking pieces
			}
			/*
			 * Pawn Protection
			 */
			if (b.inRange((byte)(pos + 17))){ // I.e. not out of bounds
				if(!(boardArray[(byte) pos + 17] == null)){
					if(boardArray[(byte) pos + 17].getPiece().getType() == Type.PAWN){
						value += (pawnValue/10);
					}
				}
			}
			if(b.inRange((byte) (pos + 15))){
				if(!(boardArray[(byte) pos + 15] == null)){
					if(boardArray[(byte) pos + 15].getPiece().getType() == Type.PAWN){
						value += (pawnValue/10);
					}
				}
			}
			/*
			 * Is the piece covering the central squares?
			 */
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*4;
			}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			break;
			case ROOK: value += rookValue;
			/*
			 * Is the piece covering the central squares?
			 */
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;
			}
			else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// +50% of pawnScore for saving Rook till after two other minor pieces have moved
			if ((!node.getPiece().hasMoved()) && b.getMinorDeveloped() <= 2){
				value += (pawnValue/2);
			}
			break;
			case BISHOP: value += bishopValue;
			/*
			 * Is the piece covering the central squares?
			 */
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;
			}else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			break;

			case KNIGHT: value += knightValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*2;
			}else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// 20% of pawnScore for developing knight before bishop
			if (!node.getPiece().hasMoved()){
				value += getKnightOrderScore(b.getBlacksList());
			}
			break;
			/*
			 * Is the piece covering the central squares?
			 */
			case QUEEN: value += queenValue;
			if ((pos == (byte)51 || pos ==(byte)52 || pos == (byte)67 || pos == (byte)68)){
				value += (pawnValue/10)*3;
			}else if ((canAttack(node.getPiece(), (byte) 51, b)) ||(canAttack(node.getPiece(), (byte) 52, b)) || (canAttack(node.getPiece(), (byte) 67, b)) || (canAttack(node.getPiece(), (byte) 51, b))){
				value += (pawnValue/10);
			}
			/*
			 * Wider centre control
			 */
			if (((pos > (byte)33 && pos < (byte) 38) || pos ==(byte)50 || pos == (byte)53 || pos == (byte)66 || pos == (byte)69 || (pos > 81 && pos < 86))){
				value += (pawnValue/10);
			}
			// +33% of pawnScore for saving Queen till after two other minor pieces have moved
			if ((!node.getPiece().hasMoved()) && b.getMinorDeveloped() < 3){
				value += (pawnValue/3);	
			}
			break;
			default:
			}
		}
		return value;
	}




	private int getKnightOrderScore(PieceList pieceList) {
		for(PieceListNode node : pieceList){
			if(node.getPiece().hasMoved() && node.getPiece().getType() == Type.BISHOP)
				return (-(pawnValue/10 * 2));
		}
		return 0;
	}



	private int hasCastled(Board board) {
		if(board.getThisPlayer() == Colour.BLACK){
			if (board.getHasCastled() && board.getBlackKingPos() == (byte)116){
				return -(2*pawnValue);
			}
			else{
				return 0;
			}
		}
		else{
			if (board.getHasCastled() && board.getWhiteKingPos() == (byte)4){
				return -(2*pawnValue);
			}
			else{
				return 0;
			}
		}
	}

	/**
	 * 
	 * @param p the chosen piece
	 * @param pos the position to be checked
	 * @param board
	 * @return true if the piece can attack that square on the board. False if otherwise
	 */
	private boolean canAttack(Piece p, byte pos, Board board){

		PieceListNode[] boardArray = board.getBoardArray();

		//test for white pawn
		if (p.getColour() == Colour.WHITE && p.getType() == Type.PAWN){
			if(pos == ((byte) p.getPosition()+17)||pos == ((byte) p.getPosition()+15)){
				return true;
			}
			return false;
		}

		//test for black pawn
		if (p.getColour() == Colour.BLACK && p.getType() == Type.PAWN){
			if(pos == ((byte) p.getPosition()-17)||pos == ((byte) p.getPosition()-15)){
				return true;
			}
			else{
				return false;
			}
		}

		//test for sliding piece
		byte[] normalMoves = ((NonPawn)p).getDirections();
		if(p instanceof SlidingPiece) {

			for(byte move : normalMoves) {

				byte nextPosition = (byte)(p.getPosition() + move);
				boolean finished = false;
				while(!finished) {

					// test whether off the board
					if((nextPosition & 0x88) != 0) {

						finished = true;
					}
					else {

						// test whether next square is occupied
						if(boardArray[nextPosition] != null) {

							// test whether next square is occupied by one of the enemy's pieces
							if(boardArray[nextPosition].getPiece().getColour() == board.getOtherPlayer()) {

								Move m = new Move(p.getPosition(), nextPosition, boardArray[nextPosition].getPiece());
								if(board.legal(m)) return true;
							}

							finished = true;
						}
						else {

							Move m = new Move(p.getPosition(), nextPosition);
							if(board.legal(m)) return true;
						}

						nextPosition = (byte)(nextPosition + move);
					}
				}
			}
		}
		if(p instanceof SteppingPiece) {

			for(byte move : normalMoves) {

				byte newPosition = (byte)(p.getPosition() + move);
				if((newPosition & 0x88) == 0) {

					if(boardArray[newPosition] == null) {

						Move m = new Move(p.getPosition(), newPosition);
						if(board.legal(m)) return true;
					}
					// test whether new square is occupied by one of the enemy's pieces pieces
					else if(boardArray[newPosition].getPiece().getColour() == board.getOtherPlayer()) {

						Move m = new Move(p.getPosition(), newPosition, boardArray[newPosition].getPiece());
						if(board.legal(m)) return true;
					}
				}
			}
		}
		return false;
	}
	// adds a Pawn for each Piece next to the King, unless you've allowed it only 1 open space. In which case you should lose a lot of points
	/**
	 * 
	 * @param board
	 * @return a value based on how well protected the king is. You should also be heavily docked points of leaving the King only one place to move
	 */
	private int getKingProtectionValue(Board board) {
		int value = 0;
		byte kingPos;
		Colour mine;
		PieceListNode[] boardArray = board.getBoardArray();
		mine = board.getThisPlayer();
		if (mine == Colour.WHITE){
			kingPos = board.getWhiteKingPos();
		}
		else{
			kingPos = board.getBlackKingPos();
		}
		if(board.inRange((byte) (kingPos + 15))){
			if(!(boardArray[(byte) kingPos + 15] == null)){
				if(boardArray[(byte) kingPos + 15].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos - 15))){
			if(!(boardArray[(byte) kingPos - 15] == null)){
				if(boardArray[(byte) kingPos - 15].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos + 16))){
			if(!(boardArray[(byte) kingPos + 16] == null)){
				if(boardArray[(byte) kingPos + 16].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos - 16))){
			if(!(boardArray[(byte) kingPos - 16] == null)){
				if(boardArray[(byte) kingPos - 16].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos + 17))){
			if(!(boardArray[(byte) kingPos + 17] == null)){
				if(boardArray[(byte) kingPos + 17].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos - 17))){
			if(!(boardArray[(byte) kingPos - 17] == null)){
				if(boardArray[(byte) kingPos - 17].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos + 1))){
			if(!(boardArray[(byte) kingPos + 1] == null)){
				if(boardArray[(byte) kingPos + 1].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		if(board.inRange((byte) (kingPos - 1))){
			if(!(boardArray[(byte) kingPos - 1] == null)){
				if(boardArray[(byte) kingPos - 1].getPiece().getColour() == mine){
					value += (pawnValue/10);
				}
			}
			value += (pawnValue/10); //against side of board. Still good
		}
		return value;
	}
}




















