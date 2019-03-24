import java.util.Arrays;
/**
 * @author Nick Duncan,Josh Xiong
 * Grid.java
 * 
 * Holds PuzzlePiece object in any rectangular
 * format, allows pieces to be placed wherever no 
 * matter if it fits or not
 */
public class Grid {

	PuzzlePiece[][] grid;
	/**
	 * ctor to create the grid
	 * @param width is for the x distance of the desired grid 
	 * @param height is the y distance of the desired grid
	 */
	public Grid(int width, int height) {
		grid = new PuzzlePiece[height][width];
	}
	/**
	 * checks to see if a location is on the grid
	 * @param x is the x location to be checked
	 * @param y is the y location to be checked
	 * @return boolean based on whether the location exists
	 */
	public boolean isValid(int x, int y){
		return(grid != null && x < grid[0].length && y < grid.length && x > -1 && y > -1);
	}
	/**
	 * Sets a cell on the grid 
	 * @param x is the x axis location for the piece
	 * @param y is the y axis location for the piece
	 * @param piece is the new PuzzlePiece to be placed
	 * @return the old PuzzlePiece, null if the location was not valid
	 */
	public PuzzlePiece setCell(int x, int y, PuzzlePiece piece){
		if(grid == null || !isValid(x,y)) return null;
		PuzzlePiece old = grid[y][x];
		grid[y][x] = piece;
		return old;
	}
	/**
	 * Gets the value stored at a spot on the grid
	 * @param x = x axis location
	 * @param y = y axis location
	 * @return PuzzlePiece stored at given location
	 */
	public PuzzlePiece getCell(int x, int y){
		if(isValid(x,y)) return grid[y][x];
		return null;
	}
	/**
	 * clears the board of all pieces returns void
	 */
	public void clear(){
		grid = new PuzzlePiece[grid.length][grid[0].length];
	}
	/**
	 * @return the y axis length of the grid
	 */
	public int getHeight(){
		return grid.length;
	}
	/**
	 * @return the x axis length of the grid
	 */
	public int getWidth(){
		return grid[0].length;
	}
	/**
	 * checks a location to see if a PuzzlePiece is there	
	 * @param x = x location
	 * @param y = y location
	 * @return boolean whether or not something exists in that spot
	 */
	public boolean isOccupied(int x, int y){
		return getCell(x,y) != null;
	}
	/**
	 * @return boolean, whether or not the board is full of pieces
	 */
	public boolean isFull(){
		for(int i = 0; i < grid.length; i ++){
			for(int j = 0; j < grid[i].length; j++){
				if(!isOccupied(j,i)) return false;
			}
		}
		return true;
	}
	/**
	 * @return boolean whether the board has no pieces on it
	 */
	public boolean isEmpty(){
		for(int i = 0; i < grid.length; i ++){
			for(int j = 0; j < grid[i].length; j++){
				if(isOccupied(j,i)) return false;
			}
		}
		return true;
	}
	/**
	 * @return the grid represented as a String
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < grid.length; i ++){
			s += Arrays.toString(grid[i]) + "\n";
		}
		return s;
	}
}