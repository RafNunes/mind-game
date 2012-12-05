package chess.game;

import java.util.Scanner;

import chess.piece.Piece;

public class Game {

	Board gameBoard;
	Player[] players;

	/**
	 * Player player1;
	 * Player player2;
	 */
	public static void main(String[] args) {
		Board gameBoard = new Board();
		Player[] players = new Player[2];
		players[0] = new Player();
		players[1] = new Player();
		int turn = 0;
		Coord point1 = new Coord();
		Coord point2 = new Coord();
		String s;
		Scanner inputScanner = new Scanner(System.in);

		
		String validRanks = "abcdefh";
		int n = 0;
		
		//set player colours


		// set player colours

		System.out.print("Player 1 choose colour: ");
		s = inputScanner.next();

		if (s.toLowerCase().equals("white")) {
			players[0].c = Piece.Colour.WHITE;
			players[1].c = Piece.Colour.BLACK;
		} else if (s.toLowerCase().equals("black")) {
			players[1].c = Piece.Colour.WHITE;
			players[0].c = Piece.Colour.BLACK;
			turn = 1;
		} else {
			System.err.println("bad input");
			System.exit(1);
		}

				
		while(true){
			//check board for check(mate)/stalemate
			//if checkmate/stalemate, end
			//else if check, impose check rules
			//check player turn
			s = inputScanner.next();
			if(s.length() == 2){
				
				try{
					n = Character.getNumericValue(s.charAt(1));
				}catch(Exception e){
					System.err.print("Bad input, please try again");
					continue;
				}
				
				if(validRanks.indexOf(Character.toLowerCase(s.charAt(0))) != -1){ //If valid file
					point1 = new Coord(validRanks.indexOf(Character.toLowerCase(s.charAt(0)))+1, n);
				}
				
				if(inputScanner.hasNext()){//If the user has inputted two coordinates, it may be a move.
					inputScanner.next();  //Another coordinate is created and the move is checked for legality.
					if(inputScanner.hasNext()){
						if(s.length() == 2){
							
							try{
								n = Character.getNumericValue(s.charAt(1));
							}catch(Exception e){
								System.err.print("Bad input, please try again");
								continue;
							}
							
							if(validRanks.indexOf(Character.toLowerCase(s.charAt(0))) != -1){ //If valid file
								point2 = new Coord(validRanks.indexOf(Character.toLowerCase(s.charAt(0)))+1, n);
							}
						}
						//From here, calculate if the move is legal
						//if so, continue and iterate turn%2
					}
				}
				
				else{//Input is just a single coordinate.
					//Print contents of point1.
				}
				
			}
			
			else if(s.equals("Board")){
				//Print the state of the board. (What notation?)
			}

			else if(s.equals("quit")){
				break;
			}
			else{
				System.err.print("Bad input, try again");
				continue;
			}

		}
	}
}
