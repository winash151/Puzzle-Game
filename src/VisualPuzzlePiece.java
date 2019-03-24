import java.awt.Image;
import javax.swing.ImageIcon;
/**
 * 
 * @author Kevin Ren, Ashwin Suresh
 * 
 * VisualPuzzlePiece Class
 * © Ashwin Suresh Inc.
 * © April 2014
 * 
 * Extends PuzzlePiece class with an image attached to it.
 *
 */
public class VisualPuzzlePiece extends PuzzlePiece {

	//The image associated with this puzzle piece
	private Image image;
	
	/**
	 * ctor
	 * 
	 * Similar to PuzzlePiece ctor but also 
	 * takes in image of the piece
	 * @param north
	 * @param east
	 * @param south
	 * @param west
	 * @param imageLocal
	 */
	public VisualPuzzlePiece(int north, int east, int south, int west, String imageLocal) {
		super(north, east, south, west);
		setImage(imageLocal);
	}
	
	/**
	 * Setter for the image
	 * @param location
	 */
	public void setImage(String location){
			image = new ImageIcon(getClass().getResource(location)).getImage();
	}
	
	/**
	 * Getter for the image
	 * @return
	 */
	public Image getImage(){
		return image;
	}
 
}