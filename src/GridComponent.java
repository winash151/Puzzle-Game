import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;
/**
 * 
 * @author Kevin Ren, Ashwin Suresh
 * 
 * GridComponent Class
 * © Ashwin Suresh Inc.
 * © April 2014
 * 
 * Shows a Grid of VisualPuzzlePieces.
 * Handles for rotation and shows empty cells as rectangles.
 *
 */
public class GridComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//The grid that this will be displaying
	private Grid grid;
	
	//The width and height of the png image
	private final int imageDimension = 118;
	
	//The length of the jutting out part or jutting in part
	private final int suitDimension = 24;
	
	//The dimension of the inside square
	private final int insideDimension = 70;
	
	//Matrix of ints that stores all the rotations
	private int[][] rotationMatrix;
	
	/**Returns the rotation, as an int in degrees, of the piece at the given coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getRotation(int x, int y){
		return rotationMatrix[y][x];
	}
	
	/**Sets the rotation of the piece at the given coordinates with the given angle
	 * 
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public void setRotation(int x, int y, int rotation){
		rotation%=360;
		rotationMatrix[y][x] = rotation;
	}
	
	/**Rotates the piece of the given coordinates by the given angle
	 * 
	 * @param x
	 * @param y
	 * @param degree
	 */
	public void rotate(int x, int y, int degree){
		int rotation = getRotation(x,y);
		rotation+=degree;
		rotation%=360;
		
		rotationMatrix[y][x] = rotation;
	}
	
	/**Ctor that takes in a Grid of PuzzlePieces and sets all the rotations to the default of 0
	 * 
	 * @param grid
	 */
	public GridComponent(Grid grid){
		this.grid = grid;
		rotationMatrix = new int[grid.getHeight()][grid.getWidth()];
	}


	/**Paints the grid
	 * 
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D canvas = (Graphics2D) g;
		
		//This is the color that slighty distinguishes the board from th rest of the frame
		canvas.setColor(new Color(255,191,126,90));
		
		//Fill the board with this color
		canvas.fillRect(0,0,getWidth(),getHeight());
		
		//Stepping through the grid
		for(int x = 0;x<grid.getWidth();x++){
			for(int y = 0;y<grid.getHeight();y++){
				//if a Piece is there, paint it
				if(grid.isOccupied(x, y)){
					VisualPuzzlePiece piece = (VisualPuzzlePiece) grid.getCell(x, y);
					
					AffineTransform andRollout = new AffineTransform();
					
					andRollout.setToScale(getWidthScale(), getHeightScale());
					
					//Rotate the piece by its given rotation
					andRollout.rotate(Math.toRadians(rotationMatrix[y][x]),
							suitDimension+x*insideDimension+insideDimension/2, 
							suitDimension+y*insideDimension+insideDimension/2);//Rotate about the center
					
					//translate by the inside dimension so everything lines up
					andRollout.translate(x*insideDimension, y*insideDimension);
					
					//Lastly, draw the image
					canvas.drawImage(piece.getImage(), andRollout, null);
				}
				//if that spot isn't occupied, draw a dot
				else{
					//This color and stroke are used to draw the markers that show ehere the pieces should go
					canvas.setStroke(new BasicStroke(1));
					canvas.setColor(new Color(107,77,51));
					
					//Have each marker be a circle has a diameter that is a seventh the width
					//of the inside dimension
					canvas.draw(new Ellipse2D.Double(
							(int) (
								(
								(suitDimension+x*insideDimension)+(double)3/7*insideDimension
								)*getWidthScale()), 
							(int) (
								(
								(suitDimension+y*insideDimension)+(double)3/7*insideDimension
								)*getHeightScale()),
							(int) ((double)1/7*insideDimension*getWidthScale()), 
							(int) ((double)1/7*insideDimension*getHeightScale())));
				}
			}
		}
	}
	
	/**
	 * Receives x coordinate and y coordinate of click on this component
	 * Returns the grid cell that was clicked as a Point object
	 */
	public Point getCellFromClick(double x, double y) {
		double xC = (x-suitDimension*getWidthScale())/(insideDimension*getWidthScale());
		double yC = (y-suitDimension*getHeightScale())/(insideDimension*getHeightScale());
		if(grid.isValid((int) xC, (int) yC))
			return new Point((int)xC, (int)yC);
		return null;
	}
	
	/**
	 * Gets the coordinates of the center of the passed cell 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getCenter(double x, double y){
		int xCoord = (int) (suitDimension*getWidthScale() + insideDimension*getWidthScale()*(x+0.5));
		int yCoord = (int) (suitDimension*getHeightScale() + insideDimension*getHeightScale()*(y+0.5));
		
		return new Point(xCoord, yCoord);
	}
	
	/**
	 * Returns, as a double, the width scale:
	 * the component width/the two outside buffers and total inside widths
	 * 
	 */
	public double getWidthScale(){
		return (double)getWidth()/(2*suitDimension+grid.getWidth()*insideDimension);
	}
	
	/**
	 * Returns, as a double, the height scale:
	 * the component height/the two outside buffers and total inside widths
	 */
	public double getHeightScale(){
		return (double)getHeight()/(2*suitDimension+grid.getHeight()*insideDimension);
	}
	
	/**
	 * Returns the width of an entire piece image to scale
	 * @return
	 */
	public int getPieceWidth(){
		return (int) (getWidthScale()*imageDimension);
	}
	
	/**
	 * Returns the height of an entire piece image to scale
	 * @return
	 */
	public int getPieceHeight(){
		return (int) (getHeightScale()*imageDimension);
	}
}