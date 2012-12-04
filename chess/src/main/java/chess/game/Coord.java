package chess.game;

public class Coord {
	
	public int rank; 
	private int file;
	
	public Coord(int f, int r){
		if(rank < 1 || rank > 8 || file < 1 || file > 8){
			System.err.print("Bad coordinates");
			System.exit(1);
		}
		rank = r;
		file = f;
	}
	
	public Coord(){
		
	}
	
	public String toString(){
		switch(file){
		case(1):return "a"+rank;
		case(2):return "b"+rank;
		case(3):return "c"+rank;
		case(4):return "d"+rank;
		case(5):return "e"+rank;
		case(6):return "f"+rank;
		case(7):return "g"+rank;
		case(8):return "h"+rank;
		default: return"";
		}
	}
}
