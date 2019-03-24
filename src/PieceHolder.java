import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;

/**
 * 
 * @author Kevin Ren, Ashwin Suresh
 * 
 * PieceHolder Class
 * © Ashwin Suresh Inc.
 * © April 2014
 * 
 * Shows a VisualPuzzlePiece. 
 * Handles for rotation and can even portray if the piece is being hovered over or selected.
 *
 */
public class PieceHolder extends JComponent {

	private static final long serialVersionUID = 1L;
	
	//The piece this piece holder holds
	private VisualPuzzlePiece piece;
	
	
	//The width and height of the png image
	private final int imageDimension = 118;
	
	//Whether or not this is currently being hovered over by the mouse
	private boolean hover = false;
	
	//The color for when the mouse is hovering over the piece holder
	private Color hoverColor = new Color(102,204,255,40);
	
	//The visual rotation
	private int rotation;
	
	/**creates an empty PieceHolder
	 * 
	 */
	public PieceHolder(){
		this.piece = null;
	}
	
	/**creates a PieceHolder that contains the given piece
	 * 
	 * @param piece
	 */
	public PieceHolder(VisualPuzzlePiece piece){
		this.piece = piece;
		if(piece!=null)
			rotation = piece.getRotation();
	}
	
	/**returns the piece this PieceHolder holds (null if there is no piece)
	 * 
	 * @return
	 */
	public VisualPuzzlePiece getPiece(){
		return piece;
	}
	
	/**returns true if this PieceHolder has a piece (the PuzzlePiece reference isn't null)
	 * 
	 * @return
	 */
	public boolean isOccupied(){
		return !isEmpty();
	}
	
	/**returns true if this PieceHolder doesn't have a piece (the PuzzlePiece is null)
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		if(piece==null) return true;
		return false;
	}
	
	/**
	 * Sets puzzle piece to passed puzzle piece, and returns old one
	 */
	public VisualPuzzlePiece setPiece(VisualPuzzlePiece piece){
		VisualPuzzlePiece temp = this.piece;
		this.piece=piece;
			if(piece!=null)
		rotation = piece.getRotation();
		return temp;
	}
	
	/**
	 * Removes the puzzle piece and returns it
	 */
	public VisualPuzzlePiece removePiece(){
		VisualPuzzlePiece temp = this.piece;
		this.piece=null;
		return temp;
	}
	/**sets the hover variable to the given boolean
	 * 
	 * @param a
	 */
	public void setHover(boolean a){
		hover=a;
	}
	
	/**returns the hover boolean
	 * 
	 * @return
	 */
	public boolean isHovered(){
		return hover;
	}
	
	/**
	 * Rotates the piece that is being held.
	 * Does not do anything if there is no piece.
	 */
	public void rotate(){
		if(isOccupied()){
			piece.rotate();
		}
	}
	
	/**returns, as an int (degrees), the rotation of the PieceHolder
	 * 
	 * @return
	 */
	public int getRotation(){
		return rotation;
	}
	
	/**sets the rotation to the given int (in degrees)
	 * 
	 * @param rotation
	 */
	public void setRotation(int rotation){
		this.rotation = rotation;
		this.rotation%=360;
	}
	
	/**rotates this PieceHolder by the given int degrees
	 * 
	 * @param degree
	 */
	public void rotate(int degree){
		rotation+=degree;
		rotation%=360;
	}
	
	/**paints
	 * 
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D canvas= (Graphics2D) g;//Call it canvas like in HTML5
		
		//If there is a piece
		if(isOccupied()){
			
			AffineTransform andRollout = new AffineTransform();
			
			//Set the scale to full
			andRollout.setToScale((double)getWidth()/imageDimension,(double)getHeight()/imageDimension);
			
			andRollout.rotate(Math.toRadians(rotation), imageDimension/2, imageDimension/2);//Rotate about the center
			
			//FInally, draw the image
			canvas.drawImage(piece.getImage(), andRollout, null);
		}
		
		//fills the PieceHolder with the hoverColor when hovered over
		if(isHovered()){
			canvas.setColor(hoverColor);
			canvas.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}