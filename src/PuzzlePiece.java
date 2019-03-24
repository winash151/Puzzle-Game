import java.util.Arrays;

/**
 * @author Nick Duncan, Josh Xiong
 * PuzzlePiece.java
 * 
 * Class to represent PuzzlePieces, contains integer constants
 * for each direction and all types of PuzzlePiece sides, Pieces can also
 * be rotated.
 */
public class PuzzlePiece {
	
	//int constants for puzzle sides
	public static final int CLUBS_OUT = 1;
	public static final int CLUBS_IN = -1;
	public static final int DIAMONDS_OUT = 2;
	public static final int DIAMONDS_IN = -2;
	public static final int HEARTS_OUT = 3;
	public static final int HEARTS_IN = -3;
	public static final int SPADES_OUT = 4;
	public static final int SPADES_IN = -4;
	
	//int constants for direction
	public static final int NORTH = 0;
	public static final int EAST = 90;
	public static final int SOUTH = 180;
	public static final int WEST = 270;
	
	private int[][] sides = new int[4][4];
	private int orientation = 0;

	/**
	 * ctor to create a PuzzlePiece object
	 * @param north requires int constant for top
	 * @param east requires int constant for rigth 
	 * @param south requires int constant for bottom
	 * @param west requires int constant for left
	 */
	public PuzzlePiece(int north, int east, int south, int west) {
		setSides(north, east, south, west);
	}
	/**
	 * rotates the piece 90 degrees clockwise returns void
	 */
	public void rotate() {
		orientation = (orientation + 90) % 360;
	}
	/**
	 * rotates the piece 90 degrees counter-clockwise returns void
	 */
	public void rotateTheWayOppositeOfTheOtherRotateMethod() {
		orientation = (orientation + 270) % 360;
	}
	/**
	 * @param direction requires a direction constant that corresponds to desired side
	 * @return Side constant at given direction
	 */
	public int getSide(int direction) {
		return sides[orientation / 90][direction / 90];
	}
	/**
	 * @return rotation value for the piece
	 */
	public int getRotation() {
		return orientation;
	}
	/**
	 * Private to set all sides at once
	 * @param north requires int constant for top
	 * @param east requires int constant for rigth 
	 * @param south requires int constant for bottom
	 * @param west requires int constant for left
	 */
	private void setSides(int north, int east, int south, int west) {
		sides[0][0] = sides[1][1] = sides[2][2] = sides[3][3] = north;
		sides[0][1] = sides[1][2] = sides[2][3] = sides[3][0] = east;
		sides[0][2] = sides[1][3] = sides[2][0] = sides[3][1] = south;
		sides[0][3] = sides[1][0] = sides[2][1] = sides[3][2] = west;
	}
	/**
	 * @return String representation of the PuzzlePiece
	 */
	public String toString() {
		return Arrays.toString(sides[orientation / 90]);
	}
	

}