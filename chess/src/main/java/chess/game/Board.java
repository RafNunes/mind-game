package chess.game;

import java.util.Stack;

import chess.piece.*;
import chess.util.*;

import java.util.LinkedList;

/**
 * 
 * @author Demian Till
 * 
 */
public class Board {

	private PieceList whitesPieceList;
	private PieceList blacksPieceList;
	private PieceListNode[] boardArray;		// 0x88 board array. Each element points to null or to a PieceListNode which 
	// is in the list of white's or black's pieces.
	private Colour thisPlayer; // colour of player whose turn it is now
	private Colour otherPlayer; // colour of player whose turn it is NOT now
	private Stack<Move> previousMoves;
	private byte whiteKingPos; // so we don't have to search to find kings every time we check for 'check'
	private byte blackKingPos;

	/**
	 * Constructor
	 */
	public Board() {

		thisPlayer = Colour.WHITE;
		otherPlayer = Colour.BLACK;
		previousMoves = new Stack<Move>();
		whitesPieceList = new PieceList();
		blacksPieceList = new PieceList();
		whiteKingPos = (byte)4;
		blackKingPos = (byte)116;
		Piece.Type[] list_pieces = {Piece.Type.ROOK,Piece.Type.KNIGHT,Piece.Type.BISHOP,Piece.Type.QUEEN,Piece.Type.KING,Piece.Type.BISHOP,Piece.Type.KNIGHT,Piece.Type.ROOK};

		for(int i=0;i<8;i++){

			whitesPieceList.addPiece(new Pawn(Colour.WHITE, (byte)(i+16)));
			blacksPieceList.addPiece(new Pawn(Colour.BLACK, (byte)(i+96)));
			if(list_pieces[i] == Piece.Type.ROOK || list_pieces[i] == Piece.Type.BISHOP|| list_pieces[i] == Piece.Type.QUEEN) {

				whitesPieceList.addPiece(new SlidingPiece(Colour.WHITE, list_pieces[i], (byte)i));
				blacksPieceList.addPiece(new SlidingPiece(Colour.BLACK, list_pieces[i], (byte)(i+112)));
			}
			else { // must be knight or king

				whitesPieceList.addPiece(new SteppingPiece(Colour.WHITE, list_pieces[i], (byte)i));
				blacksPieceList.addPiece(new SteppingPiece(Colour.BLACK, list_pieces[i], (byte)(i+112)));
			}
		}

		boardArray = new PieceListNode[120]; // no need for the last 8 cells which are off the board
		for(int i = 0; i < boardArray.length; i++) {
			boardArray[i] = null;
		}		
		for(PieceListNode node : whitesPieceList){
			boardArray[node.getPiece().getPosition()]=node;
		}
		for(PieceListNode node : blacksPieceList){
			boardArray[node.getPiece().getPosition()]=node;
		}
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
	protected int getMaterialTotalWhite(int pawnValue,int rookValue, int bishopValue, int knightValue, int queenValue){
		int value = 0;
		PieceList pieces = whitesPieceList;

		for(PieceListNode node : pieces) {

			switch(node.getPiece().getType()) {

			case PAWN: value += pawnValue; 
			if ((node.getPiece().getPosition() % 16) == 0 || node.getPiece().getPosition() % 16 == 7){
				value -= (pawnValue /100) * 15;
			}
			if ((node.getPiece().getPosition() > 79)){
				value += pawnValue/2; //temp value - must confirm with David Watt
			}
			if ((node.getPiece().getPosition() > 95)){
				value += queenValue/2; //temp value -must confirm with David Watt
			}
			break;
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
	protected int getMaterialTotalBlack(int pawnValue,int rookValue, int bishopValue, int knightValue, int queenValue){
		int value = 0;
		PieceList pieces = blacksPieceList;

		for(PieceListNode node : pieces) {

			switch(node.getPiece().getType()) {

			//black has the exact same thing but in reverse
			case PAWN: value += pawnValue; 
			if ((node.getPiece().getPosition() % 16) == 0 || node.getPiece().getPosition() % 16 == 7){
				value -= (pawnValue /100) * 15;
			}
			if ((node.getPiece().getPosition() < 79)){
				value += pawnValue/2; //temp value - must confirm with David Watt
			}
			if ((node.getPiece().getPosition() < 95)){
				value += queenValue/2; //temp value -must confirm with David Watt
			}
			break;
			case ROOK: value += rookValue; break;
			case BISHOP: value += bishopValue; break;
			case KNIGHT: value += knightValue; break;
			case QUEEN: value += queenValue; break;
			default:
			}
		}
		return value;
	}

	// positive if player whose turn it is has more material, negative if other player has more material


/**
 * @return - LinkedList<Piece> containing all of the pieces on the board
 */
public LinkedList<Piece> getPieces() {

	LinkedList<Piece> pieces = new LinkedList<Piece>();
	for(PieceListNode node : whitesPieceList) {

		pieces.add(node.getPiece());
	}
	for(PieceListNode node : blacksPieceList) {

		pieces.add(node.getPiece());
	}
	return pieces;
}
/*
 * Used in AI class
 */
public Colour getThisPlayer(){
	return thisPlayer;
}

/**
 * updates the board and the pieces to reflect move m having been made.
 * 
 * @param - Move m
 */
public void makeMove(Move m) {

	tryMove(m);
}

/**
 * updates the board and the pieces to reflect move m having been made.
 * 
 * @param - Move m
 */
public void tryMove(Move m) {

	if(boardArray[m.getEndpos()] != null) {

		boardArray[m.getEndpos()].detachSelfFromList();
	}
	if(boardArray[m.getStartpos()].getPiece().hasMoved() == false) m.setFirstTimeMoving(true);
	boardArray[m.getEndpos()] = boardArray[m.getStartpos()];
	boardArray[m.getStartpos()] = null;
	boardArray[m.getEndpos()].getPiece().setPosition(m.getEndpos());
	if(m.getPromotion() != null) {

		m.setPromotedPawn(boardArray[m.getEndpos()].getPiece());

		if(m.getPromotion() == Piece.Type.QUEEN) {

			boardArray[m.getEndpos()].setPiece(new SlidingPiece(thisPlayer, Piece.Type.QUEEN, m.getEndpos()));
		}
		else {

			boardArray[m.getEndpos()].setPiece(new SteppingPiece(thisPlayer, Piece.Type.KNIGHT, m.getEndpos()));
		}
	}
	if(boardArray[m.getEndpos()].getPiece().getType() == Piece.Type.KING) {

		// track king position
		if(thisPlayer == Colour.WHITE) whiteKingPos = m.getEndpos();
		else blackKingPos = m.getEndpos();

		// if castling to the left, we must move the left rook
		if(m.getStartpos() - m.getEndpos() == 2) {

			boardArray[m.getStartpos() - 1] = boardArray[m.getStartpos() - 4];
			boardArray[m.getStartpos() - 4] = null;
			boardArray[m.getStartpos() - 1].getPiece().setPosition((byte)(m.getStartpos() - 1));
		}
		// if castling to the right, we must move the right rook
		if(m.getStartpos() - m.getEndpos() == -2) {

			boardArray[m.getStartpos() + 1] = boardArray[m.getStartpos() + 3];
			boardArray[m.getStartpos() + 3] = null;
			boardArray[m.getStartpos() + 1].getPiece().setPosition((byte)(m.getStartpos() + 1));
		}
	}

	Colour temp = thisPlayer;
	thisPlayer = otherPlayer;
	otherPlayer = temp;
	previousMoves.push(m);
}

/**
 * reverts the state of the board and pieces to how they were before the most recent move was made.
 */
public void undoMove() {

	Colour temp = thisPlayer;
	thisPlayer = otherPlayer;
	otherPlayer = temp;
	Move m = previousMoves.pop();
	boardArray[m.getStartpos()] = boardArray[m.getEndpos()];
	boardArray[m.getStartpos()].getPiece().setPosition(m.getStartpos());
	if(m.getFirstTimeMoving()) boardArray[m.getStartpos()].getPiece().setHasMoved(false);
	if(boardArray[m.getStartpos()].getPiece() instanceof Pawn) {

		// set hasMoved to false if starting position == current position
		boardArray[m.getStartpos()].getPiece().setHasMoved(!(((Pawn)(boardArray[m.getStartpos()].getPiece())).getStartingPos() == 
				boardArray[m.getStartpos()].getPiece().getPosition()));
	}
	if(m.getCapture() != null) {

		boardArray[m.getEndpos()] = new PieceListNode(m.getCapture());
		if(otherPlayer == Colour.WHITE) whitesPieceList.addNode(boardArray[m.getEndpos()]);
		else blacksPieceList.addNode(boardArray[m.getEndpos()]);
	}
	else boardArray[m.getEndpos()] = null;

	if(m.getPromotion() != null) {

		boardArray[m.getStartpos()].setPiece(m.getPromotedPawn());
		boardArray[m.getStartpos()].getPiece().setPosition(m.getStartpos());
	}
	if(boardArray[m.getStartpos()].getPiece().getType() == Piece.Type.KING) {

		// track king position
		if(thisPlayer == Colour.WHITE) whiteKingPos = m.getStartpos();
		else blackKingPos = m.getStartpos();

		// if left castle we must move the left rook
		if(m.getStartpos() - m.getEndpos() == 2) {

			boardArray[m.getStartpos()].getPiece().setHasMoved(false);
			boardArray[m.getStartpos() - 4] = boardArray[m.getStartpos() - 1];
			boardArray[m.getStartpos() - 1] = null;
			boardArray[m.getStartpos() - 4].getPiece().setPosition((byte)(m.getStartpos() - 4));
			boardArray[m.getStartpos() - 4].getPiece().setHasMoved(false);
		}

		// if right castle we must move the left rook
		if(m.getStartpos() - m.getEndpos() == -2) {

			boardArray[m.getStartpos()].getPiece().setHasMoved(false);
			boardArray[m.getStartpos() + 3] = boardArray[m.getStartpos() + 1];
			boardArray[m.getStartpos() + 1] = null;
			boardArray[m.getStartpos() + 3].getPiece().setPosition((byte)(m.getStartpos() + 3));
			boardArray[m.getStartpos() + 3].getPiece().setHasMoved(false);
		}
	}
}

/**
 * @param - player who's king needs to be checked for 'check'.
 * 
 * @return - true if player's king is in check, false otherwise.
 */
public boolean inCheck(Colour player) {

	byte kingPos;
	Colour enemy;
	if(player == Colour.WHITE) {

		kingPos = whiteKingPos;
		enemy = Colour.BLACK;
	}
	else {

		kingPos = blackKingPos;
		enemy = Colour.WHITE;
	}

	// check for attacking stepping pieces
	for(byte dir : NonPawn.getKnightDir()) {

		if(((byte)(kingPos + dir) & 0x88) == 0) {

			if(boardArray[kingPos + dir] != null && boardArray[kingPos + dir].getPiece().getType() == Piece.Type.KNIGHT &&
					boardArray[kingPos + dir].getPiece().getColour() == enemy) return true;
		}
	}
	for(byte dir : NonPawn.getKingDir()) {

		if(((byte)(kingPos + dir) & 0x88) == 0) {

			if(boardArray[kingPos + dir] != null && boardArray[kingPos + dir].getPiece().getType() == Piece.Type.KING &&
					boardArray[kingPos + dir].getPiece().getColour() == enemy) return true;
		}
	}

	// check for attacking sliding pieces
	byte[][] dirSets = new byte[2][];
	dirSets[0] = NonPawn.getRookDir();
	dirSets[1] = NonPawn.getBishDir();

	for(int i = 0; i < 2; i++) {

		for(byte dir : dirSets[i]) {

			byte nextPosition = (byte)(kingPos + dir);
			boolean finished = false;
			while(!finished) {

				// test whether off the board
				if((nextPosition & 0x88) != 0) {

					finished = true;
				}
				else {

					// test whether next square is occupied
					if(boardArray[nextPosition] != null) {

						// this code is disgusting, sorry
						Piece.Type dangerType;
						if(i == 0) dangerType = Piece.Type.ROOK;
						else dangerType = Piece.Type.BISHOP;

						if((boardArray[nextPosition].getPiece().getColour() == enemy) &&
								(boardArray[nextPosition].getPiece().getType() == dangerType ||
								boardArray[nextPosition].getPiece().getType() == Piece.Type.QUEEN)) return true;

						finished = true;
					}
					else {

						nextPosition = (byte)(nextPosition + dir);
					}
				}
			}
		}
	}

	// check for attacking pawns
	byte[] dirs = Pawn.getCaptureDirs(enemy);
	for(byte dir : dirs) {

		byte newPosition = (byte)(kingPos - dir); // '-' because dir is from pawns perspective
		if(((byte)(newPosition) & 0x88) == 0) {

			if(boardArray[newPosition] != null && boardArray[newPosition].getPiece().getType() == Piece.Type.PAWN &&
					boardArray[newPosition].getPiece().getColour() == enemy) return true;
		}
	}

	// if we get here then the king is not in check
	return false;
}

/**
 * @return - true if player whose turn it is is in checkmate, false otherwise.
 */
public boolean checkMate() {

	if(inCheck(thisPlayer)) {

		LinkedList<Move> moves = generateMoves();
		if(moves.isEmpty()) return true;
	}
	return false;
}

/**
 * Checks if an input move is illegal due to the king being put in check
 * 
 * @param - move to be checked for legality
 * 
 * @return - true if player who's turn it is is not in check after the move has been made, false otherwise.
 */
private boolean legal(Move m) {

	boolean legal = true;
	tryMove(m);
	if(inCheck(otherPlayer)) legal = false; // since the board has progressed a turn at this point, 'we' are the 'otherPlayer'
	undoMove();
	return legal;
}

/**
 * @return - LinkedList<Move> of all moves which can be made from this position by the player whose turn it is.
 */
public LinkedList<Move> generateMoves() {

	PieceList pieces;
	if(thisPlayer == Colour.WHITE) pieces = whitesPieceList;
	else pieces = blacksPieceList;

	LinkedList<Move> moves = new LinkedList<Move>();

	for(PieceListNode node : pieces) {

		Piece piece = node.getPiece();

		if(piece instanceof NonPawn) {

			byte[] normalMoves = ((NonPawn)piece).getDirections();
			if(piece instanceof SlidingPiece) {

				for(byte move : normalMoves) {

					byte nextPosition = (byte)(piece.getPosition() + move);
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
								if(boardArray[nextPosition].getPiece().getColour() == otherPlayer) {

									Move m = new Move(piece.getPosition(), nextPosition, boardArray[nextPosition].getPiece());
									if(legal(m)) moves.add(m);
								}

								finished = true;
							}
							else {

								Move m = new Move(piece.getPosition(), nextPosition);
								if(legal(m)) moves.add(m);
							}

							nextPosition = (byte)(nextPosition + move);
						}
					}
				}
			}
			if(piece instanceof SteppingPiece) {

				for(byte move : normalMoves) {

					byte newPosition = (byte)(piece.getPosition() + move);
					if((newPosition & 0x88) == 0) {

						if(boardArray[newPosition] == null) {

							Move m = new Move(piece.getPosition(), newPosition);
							if(legal(m)) moves.add(m);
						}
						// test whether new square is occupied by one of the enemy's pieces pieces
						else if(boardArray[newPosition].getPiece().getColour() == otherPlayer) {

							Move m = new Move(piece.getPosition(), newPosition, boardArray[newPosition].getPiece());
							if(legal(m)) moves.add(m);
						}
					}
				}

				// while king's normal moves are covered in 'SteppingPieces', we need to see if we can castle
				if(piece.getType() == Piece.Type.KING) {

					if(!piece.hasMoved() && !inCheck(thisPlayer)) {

						// check whether left rook is unmoved
						if((boardArray[piece.getPosition() - 4] != null) && 
								(boardArray[piece.getPosition() - 4].getPiece().getType() == Piece.Type.ROOK) &&
								(!boardArray[piece.getPosition() - 4].getPiece().hasMoved())) {

							// check whether spaces in between are empty
							byte nextPosition = (byte)(piece.getPosition() - 1);
							boolean violated = false;
							while((nextPosition > (piece.getPosition() - 4)) && !violated) {

								if(boardArray[nextPosition] != null) {

									violated = true;
								}
								nextPosition--;
							}
							if(!violated) {

								// then move onto the next stage of testing.
								// construct a move moving the king one square just to check that that square is not attacked
								// this move will not be added to the move list
								Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() - 1));
								if(!legal(m)) violated = true;
							}
							if(!violated) {

								Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() - 2));
								if(legal(m)) moves.add(m);
							}
						}

						// check whether right rook is unmoved
						if((boardArray[piece.getPosition() + 3] != null) && 
								(boardArray[piece.getPosition() + 3].getPiece().getType() == Piece.Type.ROOK) &&
								(!boardArray[piece.getPosition() + 3].getPiece().hasMoved())) {

							// check whether spaces in between are empty
							byte nextPosition = (byte)(piece.getPosition() + 1);
							boolean violated = false;
							while((nextPosition < (piece.getPosition() + 3)) && !violated) {

								if(boardArray[nextPosition] != null) {

									violated = true;
								}
								nextPosition++;
							}
							if(!violated) {

								// then move onto the next stage of testing.
								// construct a move moving the king one square just to check that that square is not attacked
								// this move will not be added to the move list
								Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + 1));
								if(!legal(m)) violated = true;
							}
							if(!violated) {

								Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + 2));
								if(legal(m)) moves.add(m);
							}
						}
					}
				}
			}
		}
		else { // must be pawn

			byte forwardMove = ((Pawn)piece).getForwardDir();

			if(boardArray[piece.getPosition() + forwardMove] == null) {

				if(((Pawn)piece).oneOffFinalRow()) {

					Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove), Piece.Type.QUEEN);
					if(legal(m)) { 

						moves.add(m);
						// it should also be legal for knight as well (no point in the others)
						m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove), Piece.Type.KNIGHT);
						moves.add(m);
					}
				}
				else {

					Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove));
					if(legal(m)) moves.add(m);
				}

				if(!((Pawn)piece).hasMoved()) {

					if(boardArray[piece.getPosition() + (2 * forwardMove)] == null) {

						Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + (2 * forwardMove)));
						if(legal(m)) moves.add(m);
					}
				}
			}

			byte[] diagonalMoves = ((Pawn)piece).getCaptureDirs();
			for(byte move : diagonalMoves) {

				if(((piece.getPosition() + move) & 0x88) == 0) {

					if((boardArray[piece.getPosition() + move] != null) && (boardArray[piece.getPosition() + move].getPiece().getColour() == otherPlayer)) {

						if(((Pawn)piece).oneOffFinalRow()) {

							Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + move), Piece.Type.QUEEN, boardArray[piece.getPosition() + move].getPiece());
							if(legal(m)) { 

								moves.add(m);
								// it should also be legal for knight as well (no point in the others)
								m = new Move(piece.getPosition(), (byte)(piece.getPosition() + move), Piece.Type.KNIGHT, boardArray[piece.getPosition() + move].getPiece());
								moves.add(m);
							}
						}
						else {

							Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + move), boardArray[piece.getPosition() + move].getPiece());
							if(legal(m)) moves.add(m);
						}
					}
				}
			}
		}
	}

	return moves;
}

public LinkedList<Move> generateMovesFromOpponentsPerspective() {

	Colour temp = thisPlayer;
	thisPlayer = otherPlayer;
	otherPlayer = temp;

	LinkedList<Move> moves = generateMoves();

	temp = thisPlayer;
	thisPlayer = otherPlayer;
	otherPlayer = temp;

	return moves;
}
}



















