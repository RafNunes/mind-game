package chess.game;

import java.util.List;

import chess.piece.*;
import chess.util.*;

import java.util.LinkedList;
import java.util.Iterator;

public class Board {
	
	private PieceList whitesPieceList;
	private PieceList blacksPieceList;
	private PieceListNode[] boardArray;		// 0x88 board array. Each element points to null or to a PieceListNode which 
											// is in the list of white's or black's pieces.
	private Colour thisPlayer; // colour of player whose turn it is now
	private Colour otherPlayer; // colour of player whose turn it is NOT now
	
	public static void main(String[] args) {
		
		Board b = new Board();
		System.out.println("yay");
	}

	public Board() {
		
		whitesPieceList = new PieceList();
		blacksPieceList = new PieceList();
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
		
		System.out.println("check each position in boardArray");
		for(int i=7;i>=0;i--) {
			for(int j=0;j<8;j++){
				if(boardArray[(i*16)+j] == null) System.out.print("Empty, ");
				else System.out.print(boardArray[(i*16)+j].getPiece().getType() + ", ");
			}
			System.out.print('\n');
		}
		
		thisPlayer = Colour.WHITE;
		otherPlayer = Colour.BLACK;
	}
	
	public void tryMove(Move m) {
		
		
	}
	
	public void undoMove() {
		
		
	}
	
	private boolean inCheck(Colour player) {
		
		return false;
	}
	
	private boolean legal(Move m) {
		
		boolean legal = true;
		tryMove(m);
		if(inCheck(otherPlayer)) legal = false; // since the board has progressed a turn at this point, 'we' are the 'otherPlayer'
		undoMove();
		return legal;
	}
	
	public LinkedList<Move> generateMoves() {
		
		PieceList pieces;
		if(thisPlayer == Colour.WHITE) pieces = whitesPieceList;
		else pieces = blacksPieceList;
		
		LinkedList<Move> moves = new LinkedList<Move>();
		
		for(PieceListNode node : pieces) {
			
			Piece piece = node.getPiece();
			
			// if piece that can move many squares per turn
			if(piece instanceof NonPawn) {
				
				byte[] normalMoves = ((NonPawn)piece).getDirections();
				if(piece instanceof SlidingPiece) {
					
					for(byte move : normalMoves) {
						
						byte nextPosition = (byte)(piece.getPosition() + move);
						boolean finished = false;
						while(!finished) {
						
							// test whether off the board
							if((nextPosition & 0x88) != 0) finished = true;
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
				if(piece instanceof SteppingPiece) {
					
					for(byte move : normalMoves) {
						
						byte newPosition = (byte)(piece.getPosition() + move);
						if((newPosition & 0x88) != 0) break;
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
					
					// while king's normal moves are covered in 'SteppingPieces', we need to see if we can castle
					if(piece.getType() == Piece.Type.KING) {
						
						if(!piece.hasMoved()) {
							
							// check whether left rook is unmoved
							if((boardArray[piece.getPosition() - 11111] != null) && 
									(boardArray[piece.getPosition() - 111111].getPiece().getType() == Piece.Type.ROOK) &&
									(!boardArray[piece.getPosition() - 111111].getPiece().hasMoved())) {
								
								// check whether spaces in between are empty and not attacked
								byte nextPosition = (byte)(piece.getPosition() - 1);
								boolean violated = false;
								while((nextPosition > (piece.getPosition() - 11111)) && !violated) { // !!!!!! check rules, maybe not all need to be unattacked
									
									if(boardArray[nextPosition] == null) {
										
										Move m = new Move(piece.getPosition(), nextPosition);
										if(!legal(m)) violated = true;
									}
									else violated = true;
									nextPosition--;
								}
								if(!violated) {
									
									Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() - 2));
									moves.add(m);
								}
							}
							
							// check whether right rook is unmoved
							if((boardArray[piece.getPosition() + 11111] != null) && 
									(boardArray[piece.getPosition() + 111111].getPiece().getType() == Piece.Type.ROOK) &&
									(!boardArray[piece.getPosition() + 111111].getPiece().hasMoved())) {
								
								// check whether spaces in between are empty and not attacked
								byte nextPosition = (byte)(piece.getPosition() + 1);
								boolean violated = false;
								while((nextPosition < (piece.getPosition() + 11111)) && !violated) { // !!!!!! check rules, maybe not all need to be unattacked
									
									if(boardArray[nextPosition] == null) {
										
										Move m = new Move(piece.getPosition(), nextPosition);
										if(!legal(m)) violated = true;
									}
									else violated = true;
									nextPosition++;
								}
								if(!violated) {
									
									Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + 2));
									moves.add(m);
								}
							}
						}
					}
				}
			}
			else { // must be pawn
				
				byte forwardMove = ((Pawn)piece).getForwardDir();
				
				if(!((Pawn)piece).hasMoved()) {
					
					if(boardArray[piece.getPosition() + forwardMove] == null) {
						
						Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove));
						if(legal(m)) moves.add(m);
						if(boardArray[piece.getPosition() + (2 * forwardMove)] == null) {
							
							m = new Move(piece.getPosition(), (byte)(piece.getPosition() + (2 * forwardMove)));
							if(legal(m)) moves.add(m);
						}
					}
				}
				else if(((Pawn)piece).oneOffFinalRow()) {
					
					if(boardArray[piece.getPosition() + forwardMove] == null) {
						
						Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove), Piece.Type.QUEEN);
						if(legal(m)) { 
							
							moves.add(m);
							// it should also be legal for knight as well (no point in the others)
							m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove), Piece.Type.KNIGHT);
							moves.add(m);
						}
					}
				}
				else {
					
					if(boardArray[piece.getPosition() + forwardMove] == null) {
						
						Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + forwardMove));
						if(legal(m)) moves.add(m);
					}
				}
				
				byte[] diagonalMoves = ((Pawn)piece).getCaptureDirs();
				for(byte move : diagonalMoves) {
					
					if(((piece.getPosition() + move) & 0x88) != 0) break;
					if((boardArray[piece.getPosition() + move] != null) && (boardArray[piece.getPosition() + move].getPiece().getColour() == otherPlayer)) {
						
						Move m = new Move(piece.getPosition(), (byte)(piece.getPosition() + move), boardArray[piece.getPosition() + move].getPiece());
						if(legal(m)) moves.add(m);
					}
				}
			}
		}
		
		return moves;
	}
}



















