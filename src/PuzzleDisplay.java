import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.net.URL;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Kevin Ren, Ashwin Suresh
 * 
 * PuzzleDisplay Class
 * © Ashwin Suresh Inc.
 * © April 2014
 * 
 * Shows a JFrame GUI for a puzzle game. 
 * Features a drag and drop interface.
 * Right click on a piece to rotate.
 * Solve and reset capabilities do exist.
 * Animated transitions do exist.
 *
 */

public class PuzzleDisplay {
	//The player object.
	private Player player;

	//Our piece bank for display
	private PieceHolder[] pieceHolderBank;
	
	private JPanel[] pHBorders;

	// The main JPanel
	private JPanel mainPanel;

	//The pieceHolder that shows the piece that is currently being dragged
	private PieceHolder handHolder = new PieceHolder(null);

	//The JFrame
	private JFrame frame = new JFrame();

	//The original piece holder the dragging piece should snap to if it is dropped over an invalid location
	private PieceHolder originalBankHolder = null;

	//The original grid location the dragging piece should snap to if it is dropped over an invalid location
	private Point originalGridCell = null;

	//The grid component that displays the grid
	private GridComponent gridComponent;
	
	//The border for the grid component
	//This was n
	private JPanel gridCompBorder = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));

	//The panel that holds all of the bank piece holders
	private JPanel bankPanel;
	
	//The JPanel that holds all the buttons and stuff
	private JPanel buttonPanel;
	
	//JLayeredPane that puts the handHolder on an upper level
	private JLayeredPane pane;
	
	//The background image for the main panel. Skeumorphism. In honor of Steve Jobs.
	private Image woodBg;
	
	//Whether or not the puzzle is currently solved. Goes to true when solve button is pressed.
	private boolean wasSolved = false;
	
	//The dimension of the bank piece
	private int bankPieceDimension;
	
	//Handles rotation of a piece
	private Timer rotateTimer = new Timer(1, null);
	
	//Handles the solving
	private Timer solveTimer = new Timer(1,null);
	
	//Handles the floating of the hand from one location to the next
	private Timer floatTimer = new Timer(1,null);
	
	//The timer is called once after the puzzle is solved to end the solve song
	private Timer djTimer = new Timer(30000, null);
	
	//The interval at which all timers are called
	private int timerInterval = 1;
	
	//The number percentage the piece travels each time the action is performed in the float listener
	private int floatInterval = 2;
	
	//The URL for the song that plays when the puzzle is solved
	private URL solveSongURL = getClass().getResource("champion.mp3");
	
	//the media that stores the solve song
	private Media solveSong;
	
	//The URL of the calming song so you solve puzzles with minimal stress
	private URL calmSongURL = getClass().getResource("peaceful3.mp3");
	
	//The media that stores the calm song
	private Media calmSong;
	
	//the player of the background music
	private MediaPlayer bgPlayer;
	
	//The player of the music when the puzzle is solved
	private MediaPlayer victoryPlayer;
	
	//The dimensions of your average JButton
	private Dimension buttonDimension;
	
	//The JLabel that shows the sound image
	private JLabel soundLabel;
	
	//the icon that stores the sound image
	private ImageIcon soundIcon;
	
	//The sound image
	private Image soundImage;
	
	//THE volume
	private double theVolume = 1.0;
	
//	The volume slider
	private JSlider volSlider;
	
//	Whether or not the volume is muted
	private boolean isMuted = false;

	/**
	 * Default ctor
	 * Creates a 3 by 3 grid with the default VisualPuzzlePieces
	 */
	public PuzzleDisplay(){this(new Player(new Grid(3,3), VisualPuzzlePieces.pieceList));}

	/**
	 * ctor
	 * Takes in a player
	 */
	public PuzzleDisplay(Player player){

		//Create the player
		this.player=player;

		//Set layout to border
		frame.setLayout(new BorderLayout());
		
		//Set the title
		frame.setTitle("Puzzle Game");
		
		//So the frame is not in the corner
		frame.setLocationByPlatform(true);
		
		//Have the program end when the window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add the resize listener to the frame.
		frame.addComponentListener(new MasterOfTheResize());
		frame.addWindowFocusListener(new FocusEavesDropper());

		//Have the main panel be a grid bag. The main panel will hold the grid and the pieces.
		mainPanel = new BoardPanel(new GridBagLayout());

		//Add the event listeners to the mainPanel
		mainPanel.addMouseListener(new DragAndDropDawg());
		mainPanel.addMouseMotionListener(new SmoothMotionBoss());
		mainPanel.addMouseListener(new RotationRevolution());

		//Create a JLayeredPane that puts the handHolder on an upper level
		pane = new JLayeredPane();

		//Add the hand holder to the upper level of the layered pane
		pane.add(handHolder,JLayeredPane.DRAG_LAYER);

		//Create the grid component
		gridComponent = new GridComponent(player.getGrid());
		
		//Set the border of the grid component
		gridCompBorder.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(160,120,79), new Color(91,66,44)));
		
		//Have this be transparent
		gridCompBorder.setOpaque(false);
		
		//Add the grid component to the border
		gridCompBorder.add(gridComponent);

		//Annoying constraining stuff. We'll use the same object for all are two.
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=1;//Set position in the middle.
		c.gridy=0;//Top
		c.weighty=0.5;//Take half the height.
		c.insets = new Insets(5, 5, 5, 5);//Make the insets 5 pixels

		//Add the grid to the main panel.
		mainPanel.add(gridCompBorder, c);

		//Create the piecebank
		pieceHolderBank = new PieceHolder[player.get$Bank().length];
		pHBorders = new JPanel[player.get$Bank().length];
		
		//Create the bank panel. By default it will have a flow layout.
		bankPanel = new JPanel();
		
		//Set the layout so there are no gaps
		bankPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		//Cycle through the piece bank to create the piece holders inside it
		for(int i = 0;i<pieceHolderBank.length;i++){
			//Create a new piece holder object
			PieceHolder h = new PieceHolder();
			
			//Set the corresponding piece
			h.setPiece((VisualPuzzlePiece) player.get$Bank()[i]);
			
			//add the piece holder to the bank
			pieceHolderBank[i] = h;
			
			//Initialize the border
			pHBorders[i] = new JPanel();
			
			//Create a local variable for convenience
			JPanel pHBorder = pHBorders[i];
			
			//add the border to the bank panel
			bankPanel.add(pHBorder);
			
			//Set the border to look caved in
			pHBorder.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(184,133,88), new Color(134,97,64)));
			
			//Make it transparent
			pHBorder.setOpaque(false);
			
			//Set the layout so there are no gaps
			pHBorders[i].setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
			
			//Add the piece holder to the border
			pHBorder.add(h);
		}

		c.gridx = 0;
		c.gridy = 1;//So it comes below the grid
		c.gridwidth=2;//So it takes up more width
		c.insets = new Insets(0,0,0,0);//No insets
		mainPanel.add(bankPanel, c);//Add the bank panel to the main panel with the proper constraints
		
		c.gridy = 2;//So it comes below the bank Panel
		c.insets = new Insets(0, 2, 2, 2);//Buffer
		c.weighty = .3;//Set the height to be less
		
		//This panel will hold the buttons
		buttonPanel = new JPanel();
		
		buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(160,120,79), new Color(91,66,44)));
		
		//buttonPanel.setBackground(new Color(255,191,126,90));
		
		//The solve button
		JButton solveButton = new JButton("Solve");
		//Add the solve listener
		solveButton.addActionListener(new SolveGod());
		
		//Add the rotation instructions
		buttonPanel.add(new JLabel("Right Click to Rotate"));
		
		//add the solve button to the button panel
		buttonPanel.add(solveButton);
		
		//create a reset button
		JButton resetButton = new JButton("Reset");
		
		//add the reset listener to the reset button
		resetButton.addActionListener(new ResetReformist());
		
		//add the reset button to the button panel
		buttonPanel.add(resetButton);
		
		//Create the sound icon
		soundIcon = new ImageIcon(getClass().getResource("on.png"));
		
		//Get the image from the icon
		soundImage = soundIcon.getImage();
		
		//Get the size of the button. The sound label will share the same height
		buttonDimension = resetButton.getPreferredSize();
		
		//Scale the image so it has the same height as its fellow JButtons
		//Setting the width to -1 will maintain the same aspect ratio
		soundImage = soundImage.getScaledInstance(-1, buttonDimension.height, Image.SCALE_SMOOTH);
		
		//Reset the icon to the newly sized image
		soundIcon = new ImageIcon(soundImage);
		
//		create a new JLabel using the icon 
		soundLabel = new JLabel(soundIcon);
		
		soundLabel.addMouseListener(new SoundClerk());
		
		//Add the sound label to the button panel
		buttonPanel.add(soundLabel);
		
		//Create the volume slider set at max volume
		volSlider = new JSlider(0,100,100);
		
//		add the change listener to the slider
		volSlider.addChangeListener(new VolumePatrol());

//		add the slider to the button panel
		buttonPanel.add(volSlider);
		
		//Make the background transparent
		bankPanel.setOpaque(false);
		volSlider.setOpaque(false);
		buttonPanel.setOpaque(false);
		
		//add the button panel, containing the buttons to the main panel
		mainPanel.add(buttonPanel,c);

		pane.add(mainPanel,JLayeredPane.DEFAULT_LAYER);//Add the main panel to the lower layer of the JLayeredPane

		frame.getContentPane().add(pane);//Add the layered pane to the frame
		
		//Set the sizes of all the components relative to a frame size of 800 by 800 pixels
		setSizes(800, 800);
		
		
		//This is necessary to avoid an error, in javafx
		new JFXPanel();
		
		//create the solve song
		solveSong = new Media(solveSongURL.toString());
		
		//create the calm song
		calmSong = new Media(calmSongURL.toString());
		
		//have the media player play the solve song
		bgPlayer = new MediaPlayer(calmSong);
		
		//Have the bgPlayer loop
		bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		
		//The player for when the user solves the puzzle
		victoryPlayer = new MediaPlayer(solveSong);
		
		//Reset the board to guarantee a randomized bank each time the program is run
		reset();
	}
	
	/**
	 * ctor
	 * Takes in a Grid and Puzzle Pieces
	 * Constructs a player from these and then delegates to other constructor
	 */
	public PuzzleDisplay(Grid grid, PuzzlePiece[] pieces){
		this(new Player(grid,pieces));
	}
	
	/**
	 * Sets the sizes of the components relative to the passed dimensions
	 */
	public void setSizes(int frameWidth, int frameHeight){
		
		//Make sure that the grid and the hand holder is a square
		if(frameWidth<frameHeight){//If the width is smaller set the sizes relative to the width
			gridCompBorder.setPreferredSize(new Dimension(frameWidth/2, frameWidth/2));
			
			//Set the size of the grid to exclude the borders
			gridComponent.setPreferredSize(
					new Dimension(frameWidth/2 - gridCompBorder.getInsets().left - gridCompBorder.getInsets().right,
							frameWidth/2 - gridCompBorder.getInsets().top - gridCompBorder.getInsets().bottom)
					);
			
			//Have the handholder be one tenth the size of the screen
			handHolder.setBounds(0, 0, frameWidth/10, frameWidth/10);
		}
		else{//Otherwise set the sizes relative to the height
			gridCompBorder.setPreferredSize(new Dimension(frameHeight/2,frameHeight/2));
			
			//Set the size of the grid to exclude the borders
			gridComponent.setPreferredSize(
					new Dimension(frameHeight/2 - gridCompBorder.getInsets().left - gridCompBorder.getInsets().right,
							frameHeight/2 - gridCompBorder.getInsets().top - gridCompBorder.getInsets().bottom)
					);
			
			//Have the handholder be one tenth the size of the screen
			handHolder.setBounds(0, 0, frameHeight/10, frameHeight/10);
		}
		
		//Set the location in the top left, just outside of the borders
		gridComponent.setLocation(gridCompBorder.getInsets().left, gridCompBorder.getInsets().top);
		
		//Set the width and height of the bank panel to these dimensions
		int bankWidth = frameWidth*4/5;
		int bankHeight = frameHeight/3;
		
		//Set the size of the bank panel
		bankPanel.setPreferredSize(new Dimension(bankWidth, bankHeight));
		
		//Get the area of the bank panel
		int bankArea = bankWidth*bankHeight;
		
		//Theoretically each piece holder in the bank would get this area. 
		//This calculation is a starting point
		int areaPerPiece = bankArea/(player.get$Bank().length);
		
		//So the bank pieces are squares we take the square root of the area
		bankPieceDimension = (int) Math.sqrt(areaPerPiece);
		
		
		while(true){
			//Reduce the bank piece dimension until all the pieces can realistically fit in the bank
			if((bankWidth/bankPieceDimension)*(bankHeight/bankPieceDimension)<player.get$Bank().length)
				bankPieceDimension--;
			else
				break;
		}
		
		//Finally set the sizes for each piece holder
		for(int i = 0; i<pHBorders.length;i++){
			
			//Get the JPanel
			JPanel pHBorder = pHBorders[i];
			
			//Set the size to the bankPieceDimension
			pHBorder.setPreferredSize(new Dimension(bankPieceDimension, bankPieceDimension));
			
			//Set the piece holder size to exclude the borders
			pieceHolderBank[i].setPreferredSize(new Dimension(
					bankPieceDimension - pHBorder.getInsets().left - pHBorder.getInsets().right
					, bankPieceDimension - pHBorder.getInsets().top - pHBorder.getInsets().bottom)
			);
			
			//Set the location at the top left, just outside the border
			pieceHolderBank[i].setLocation(pHBorder.getInsets().left, pHBorder.getInsets().top);
		}
		
		//Reduce the bank piece dimension to the actual size of the piece holder
		bankPieceDimension = bankPieceDimension - pHBorders[0].getInsets().left - pHBorders[0].getInsets().right;
		
		//Let the whole main panel show
		mainPanel.setBounds(0, 0, frameWidth, frameHeight);
	
		//Set the size of the layered pane to the size of the frame
		pane.setPreferredSize(new Dimension(frameWidth, frameHeight));

		//Set the location of the handholder so it is off the frame
		handHolder.setLocation(new Point(-handHolder.getWidth(),-handHolder.getHeight()));
		
		//Finally, pack the frame
		frame.pack();
		
	}

	/**
	 * Displays the frame
	 */
	public void display(){
		frame.setVisible(true);
		//Start the media player
		bgPlayer.play();
	}

	/**
	 * Takes in the coordinates of a click relative to the mainPanel
	 * Returns the grid cell clicked from the grid as a point 
	 * returns null if no grid cell was clicked
	 */
	public Point getGridCellFromClick(int x, int y){
		
		//Get the coordinates relative to the location of the grid component
		int xCoordGrid = (int) (x-gridComponent.getX()-gridCompBorder.getX());
		int yCoordGrid = (int) (y-gridComponent.getY()-gridCompBorder.getY());
		
		//This should return null if no grid cell was actually clicked
		return gridComponent.getCellFromClick(xCoordGrid, yCoordGrid);
	}

	/**
	 * Takes in the coordinates of a click relative to the mainPanel
	 * Returns the piece holder in the bank that was clicked
	 * Returns null if no piece holder was clicked
	 */
	public PieceHolder getBankPieceHolder(int x, int y){
		
		//Let's see if the bank panel was clicked
		JPanel bankMaybePanel = (JPanel) mainPanel.getComponentAt(x, y);
		
		//If it was clicked
		if(bankMaybePanel!=null && bankMaybePanel.equals(bankPanel)){
			//Get the coordinates relative to the bankPanel
			x = x - bankPanel.getX();
			y = y - bankPanel.getY();
			
			//See if a border JPanel was clicked
			JPanel borderPanel = (JPanel) bankPanel.getComponentAt(x, y);
			
			//If one was
			if(borderPanel!=null && !borderPanel.equals(bankPanel)){
				//Get the child which is the piece holder that was clicked
				PieceHolder ph = (PieceHolder) borderPanel.getComponent(0);
				return ph;//Return the piece holder
			}
		}
		
		//If no pieceholder contained the click then return null
		return null;
	}
	
	/**
	 * Resets the board
	 * Randomizes the bank
	 */
	public void reset(){
		
		//Let the user get credit now
		wasSolved = false;
		
		//Deactivate all the timers
		floatTimer.stop();
		
		rotateTimer.stop();
		
		solveTimer.stop();
		
		//Reset the hand holder
		setHand(null);
		
		//Stop the victory song
		victoryPlayer.stop();
		
		//And continue with the background music
		bgPlayer.play();
		
		//Update the hand location now that it is empty
		setHandLocation(0,0);
		
		//Return all pieces from the grid to the bank
		player.returnAllPieces();
		
		//Randomize the bank
		player.randomizeBank();
		
		//Get the bank
		PuzzlePiece[] pBank = player.get$Bank();
		
		//Set the visual bank to the one in the player class
		for(int i = 0;i<pBank.length;i++)
			pieceHolderBank[i].setPiece((VisualPuzzlePiece) pBank[i]);
		
	}
	
	/**
	 * Takes in the coordinates of a click relative to the mainPanel
	 * Sets which piece holder the mouse is hovering over
	 */
	public void setHover(int x, int y){
		//Get the possible piece holder
		PieceHolder ph = getBankPieceHolder(x,y);
		if(ph!=null){//If it is legit
			for(PieceHolder cyclePH: pieceHolderBank)
				cyclePH.setHover(false);//Set all the other piece holders to not hover
			ph.setHover(true);//And set the right one to hover
		}
		else//If it is not legit
			for(PieceHolder cyclePH: pieceHolderBank)
				cyclePH.setHover(false);//Set all the piece holders to not hover
		frame.repaint();
	}
	
	/**
	 * Sets the location of the hand hand holder, so that the center is where the passed coordinate is
	 * Positions the hand holder outside of the frame when it is empty
	 * 
	 * Returns true if the hand holder is occupied. Otherwise returns false
	 */
	public boolean setHandLocation(int x, int y){
		if(handHolder.isOccupied()){//If the hand holder is carrying a piece
			//Then set the center of the hand to the coordinate
			handHolder.setLocation(new Point(x-handHolder.getWidth()/2,y-handHolder.getHeight()/2));
			return true;
		}
		else{//Otherwise place the hand holder outside of the screen
			handHolder.setLocation(new Point(-handHolder.getWidth(),-handHolder.getHeight()));
			return false;
		}
	}
	
	/**
	 * Calls the float timer to send the hand to the passed piece holder
	 */
	public void moveHandTo(PieceHolder ph){
		if(handHolder.isOccupied()){//If the hand holder is occupied
			floatTimer.stop();//Stop the float timer. Just in case.
			
			//Set the timer to call every millisecond with an interval of 2 out of 100
			//This should be fast but fluid
			floatTimer = new Timer(timerInterval, new FlotationGuru(ph,floatInterval)); 
			floatTimer.start();//Start the timer
		}
	}
	
	/**
	 * Moves the hand to the passed grid cell
	 * @param gridCell
	 */
	public void moveHandTo(Point gridCell){
		if(handHolder.isOccupied()){//If the hand holder is occupied
			floatTimer.stop();//Stop the timer just in case
			
			//Set the timer to call every millisecond with an interval of 2 out of 100
			//This should be fast but fluid
			floatTimer = new Timer(timerInterval, new FlotationGuru(gridCell,floatInterval));
			floatTimer.start();//Start the timer
		}
	}
	
	/**
	 * Takes in a VisualPuzzlePiece
	 * Handles the setting of the hand holder.
	 * Returns the previous piece that was in the hand holder
	 */
	public VisualPuzzlePiece setHand(VisualPuzzlePiece piece){
		VisualPuzzlePiece temp = handHolder.getPiece();//Get the original piece
		if(piece==null && temp!=null)//If a null piece is being sent. Meaning the hand has been sent to the bank or the grid.
			if(handHolder.getRotation()!=temp.getRotation())//And the hand holder was in the middle of a rotation
				rotateTimer.stop();//stop the timer
			
		
		handHolder.setPiece(piece);//Set the hand holder to the passed piece
		return temp;//Return the old piece
	}
	
	/**
	 * Takes in coordinates and decides where to drop off the hand
	 * 
	 * @param x
	 * @param y
	 */
	public void dropHand(int x, int y){
		//Get the possible piece holder in the bank
		PieceHolder ph = getBankPieceHolder(x, y);
		
		//If the piece holder is legit and empty
		if(ph!=null && ph.isEmpty())
			moveHandTo(ph);
		else{
			//Get the possible grid cell
			Point p = getGridCellFromClick(x, y);
			
			//If the grid cell is legit and the puzzle piece can be placed there
			if(p!=null && player.canPlace(p.x, p.y, handHolder.getPiece()))
				moveHandTo(p);
			else{
				if(originalBankHolder!=null)//If there is an original piece holder
					moveHandTo(originalBankHolder);//Place the piece back there
				//If there is an original grid cell, send the hand there
				else if(originalGridCell!=null && player.canPlace(originalGridCell.x,originalGridCell.y, handHolder.getPiece()))
					moveHandTo(originalGridCell);//
				else{//other wise look for a last resort in the bank
					for(PieceHolder lastResort: pieceHolderBank){
						if(lastResort.isEmpty()){
							moveHandTo(lastResort);
							break;
						}
					}
				}
			}
		}
		//no more originals
		originalGridCell=null;
		originalBankHolder=null;
		
		frame.repaint();
	}
	
	/**
	 * Sets the cursor type according to the passed coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public void setCursor(int x, int y){
		
		//If the puzzle is being solved or the solve button was clicked
		//Then go back to the default cursor
		if(solveTimer.isRunning() || wasSolved){
			mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		
		//If a bank piece is hovered over
		//and is occupied
		//then set the cursor to a hand
		if(getBankPieceHolder(x,y)!=null && getBankPieceHolder(x,y).isOccupied())
			mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		//if a grid cell was hovered over
		//and is occupied
		//then set the cursor to a hand
		else if(getGridCellFromClick(x,y)!=null 
				&& player.getGrid().isOccupied(getGridCellFromClick(x,y).x
						,getGridCellFromClick(x,y).y))
			mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		//Otherwise set the cursor to default
		else
			mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		frame.repaint();
	}
	
	/**
	 * Listens to the solve button
	 * Runs the solve animation when the button is pressed
	 * if there is nothing going on already in terms of animation
	 */
	private class SolveGod implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			if(rotateTimer.isRunning() ||//If a piece is rotating
					floatTimer.isRunning() || //If a piece is floating
					solveTimer.isRunning() || //If the puzzle is already in the middle of a solve animation
					handHolder.isOccupied()//or if a piece is being dragged
					) return;//Get out and don't do nada
			
			djTimer.stop();
			victoryPlayer.stop();
			bgPlayer.play();
			
			if(wasSolved)
				reset();
			else
				player.returnAllPieces();//Start out by resetting the board
			
			//Get the bank
			PuzzlePiece[] pBank = player.get$Bank();
			
			//Set the visual bank to the one in the player class
			for(int i = 0;i<pBank.length;i++)
				pieceHolderBank[i].setPiece((VisualPuzzlePiece) pBank[i]);

			wasSolved = true;//The solve button was clicked
			
			boolean a = player.solve();//Solve the puzzle
			
			if(!a){//If the puzzle was not solved. Show a message and reset
				JOptionPane.showMessageDialog(frame, "The puzzle could not be solved. Tough luck kid.");
				reset();
			}
			else{
				solveTimer.stop();//Stop the solve timer just in case
				solveTimer = new Timer(1, new SolveAnimationEinstein());//Have the solve timer call every millisecond
				solveTimer.start();//start the solve timer
			}
			frame.repaint();
		}
	}
	 
	/**
	 * Handles the solving animation
	 */
	private class SolveAnimationEinstein implements ActionListener{

		//Our copy of the grid
		private Grid myGrid;
		
		//the current piece in the bank that we are looking at
		private int bankIndex = 0;
		
		/**
		 * default ctor
		 */
		public SolveAnimationEinstein(){
			
			//Initialize our copy of the grid
			myGrid = new Grid(player.getGrid().getWidth(), player.getGrid().getHeight());
			
			//Get the grid that has been solved
			Grid grid = player.getGrid();
			
			for(int x = 0; x<grid.getWidth(); x++)
				for(int y =0;y<grid.getHeight();y++)
					myGrid.setCell(x, y, grid.setCell(x, y, null));//Copy over into our grid, whilst emptying the real rid
			
		}
		
		public void actionPerformed(ActionEvent arg0) {
			if(handHolder.isEmpty() && !floatTimer.isRunning()){//Only do something if the piece before has been moved
				if(bankIndex<player.get$Bank().length){//Check for whether we are done with all the bank pieces
					
					PieceHolder ph = pieceHolderBank[bankIndex];//Get the current piece holder that we are looking at
					
					//Get the piece from that piece holder
					VisualPuzzlePiece piece = ph.getPiece();
					
					for(int x = 0; x<myGrid.getWidth(); x++){
						for(int y =0;y<myGrid.getHeight();y++){//Cycle through our grid
							if(myGrid.getCell(x,y).equals(piece)){//If we find the same piece
								
								setHand(piece);//Set the hand to that piece
								
								//But set the hand to the original rotation of the piece holder
								handHolder.setRotation(ph.getRotation());
								
								//Get the coordinates relative to the main panel of the center of the piece holder
								int xPH = ph.getParent().getX() + ph.getX()+mainPanel.getX()+bankPanel.getX()+bankPieceDimension/2;
								int yPH = ph.getParent().getY() + ph.getY() +mainPanel.getY()+bankPanel.getY()+bankPieceDimension/2;
								
								//Set the hand to the starting coordinates
								setHandLocation(xPH,yPH);
								
								//Stop the rotation timer
								rotateTimer.stop();
								
								//The theoretical degree that the hand must rotate to
								int theoryRot = piece.getRotation();
								
								//If this is less than the visual rotation of the hand holder
								if(theoryRot<handHolder.getRotation())
									theoryRot+=360;//Add 360
								
								//Get the difference of the two rotations
								//By now the theoretical should be greater than or equal to the visual rotation
								double difference = theoryRot - handHolder.getRotation();
								
								
								//Try to have the rotation complete by the end of the floating
								//100/floatInterval is the number of times floatTimer event will occur
								//Since all animations occur on the same timer frequency
								//We need to calculate the degrees we should turn each time the rotate event is called
								double interval = Math.ceil(difference/(100.0/floatInterval));
								
								//Create the timer
								rotateTimer = new Timer(timerInterval, new RotationPHD(handHolder, (int) interval));
								
								//If the difference is not 0 then actually start the timer
								if(difference!=0)
									rotateTimer.start();
								
								//Move the hand to this point on the actual visual grid
								moveHandTo(new Point(x,y));
								
								//remove the piece from the piece holder
								ph.removePiece();
							}
						}
					}
				}
				else{
					
					//Tell the guy how lazy he is
					JOptionPane.showMessageDialog(frame, "You get an F for effort.");
					
					//Stop the timer
					solveTimer.stop();
				}
				
				//after all the stuff has been done move on to the next bank piece
				bankIndex++;
			}
			
		}
		
	}
	
	/**
	 * starts the calm song after the solve song has finished
	 */
	private class DJ implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			victoryPlayer.stop();
			bgPlayer.play();
			djTimer.stop();
		}
	}
	
	/**
	 * The listener that floats the hand from one location to another
	 */
	private class FlotationGuru implements ActionListener{
		
		//Where the hand needs to get to. The center of the hand.
		private int xDestination;
		private int yDestination;
		
		//The size that the hand needs to reach
		private int widthDestination;
		private int heightDestination;
		
		//The starting size
		private int startWidth;
		private int startHeight;
		
		//How close the hand is to reaching its destination
		private double percentage = 0;
		
		//The starting location. This is the center of the hand
		private int startX;
		private int startY;
		
		//The piece holder or grid cell that the hand needs to get to
		private PieceHolder ph = null;
		private Point gridCell = null;
		
		//The interval by which the percentage is increased
		private int interval;
		
		/**
		 * PieceHolder ctor
		 * 
		 * @param ph
		 * @param interval
		 */
		public FlotationGuru(PieceHolder ph, int interval){
			if(ph==null){//If no piece holder was sent
				//stop the timer and get out
				floatTimer.stop();
				return;
			}
			
			//Get the destination relative to the frame
			this.xDestination = ph.getParent().getX() + ph.getX()+bankPanel.getX()+mainPanel.getX() + bankPieceDimension/2;
			this.yDestination = ph.getParent().getY() + ph.getY()+bankPanel.getY()+mainPanel.getY() + bankPieceDimension/2;
			
			//The width destinations are the bankPieceDimensions
			this.widthDestination = bankPieceDimension;
			this.heightDestination = bankPieceDimension;
			
			//the starting location is the center of where the hand holder currently is
			startX = handHolder.getX()+handHolder.getWidth()/2;
			startY = handHolder.getY()+handHolder.getHeight()/2;
			
			//the starting size
			startWidth = handHolder.getWidth();
			startHeight = handHolder.getHeight();
			
			this.ph=ph;
			this.interval = interval;
		}
		
		/**
		 * GridCell ctor
		 * 
		 * @param gridCell
		 * @param interval
		 */
		public FlotationGuru(Point gridCell, int interval){
			//If no grid cell was sent or it is not valid
			if(gridCell==null || !player.getGrid().isValid(gridCell.x, (int) gridCell.getY())){
				//then stop the timer and get out
				floatTimer.stop();
				return;
			}
			
			//set the grid cell point
			this.gridCell = gridCell;
			
			//Get the destination point by getting the center coordinate of the grid cell
			Point destPoint = gridComponent.getCenter(gridCell.x, gridCell.y);
			
			//Get the destination relative to the frame
			this.xDestination = destPoint.x + gridCompBorder.getX() + gridComponent.getX()+mainPanel.getX();
			this.yDestination = destPoint.y + gridCompBorder.getY() + gridComponent.getY() + mainPanel.getY();
			
			//Get the dimensions of a piece on the grid for the destination
			this.widthDestination = gridComponent.getPieceWidth();
			this.heightDestination = gridComponent.getPieceHeight();
			
			//the starting location is the center of where the hand holder currently is
			startX = handHolder.getX()+handHolder.getWidth()/2;
			startY = handHolder.getY()+handHolder.getHeight()/2;
			
			//the starting size
			startWidth = handHolder.getWidth();
			startHeight = handHolder.getHeight();
			
			this.interval = interval;
			
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			if(percentage==100){//If the floating is complete
				
				if(ph!=null){//If the piece holder is where we snap to
					//set the piece holder to the hand
					ph.setPiece(handHolder.getPiece());
				}
				else{
					//Set the actual grid cell to the hand
					player.place(gridCell.x,gridCell.y, handHolder.getPiece());//Then place the piece there
					
					//Set the rotation matrix of the grid cell to the proper rotation
					gridComponent.setRotation(gridCell.x,gridCell.y, handHolder.getPiece().getRotation());
					
					
					frame.repaint();
					
					//checks if puzzle has been solved
					if (player.getGrid().isFull()){
						if(!wasSolved){//If it is being solved through actual effort
							wasSolved = true;
							//Play the music
							bgPlayer.pause();
							victoryPlayer.play();
							djTimer.stop();
							djTimer = new Timer(39000, new DJ());
							djTimer.setInitialDelay(39000);
							djTimer.setRepeats(false);
							djTimer.start();
							
							//Congratulate the person
							JOptionPane.showMessageDialog(frame, "WAY TO GO CHAMP! The puzzle has been solved!");
						}
					}
					
				}
				//Empty the hand
				setHand(null);
				
				//Set sizes so the hand holder returns back to normal size
				setSizes(mainPanel.getWidth(),mainPanel.getHeight());
				
				floatTimer.stop();//Stop the timer
				
				frame.repaint();
				return;
			}
			
			//Increase the percentage by the interval
			percentage+=interval;
			
			//cap off at 100 percent
			if(percentage>100)
				percentage = 100;
			
			//Weighted averages
			double coordX = (percentage*xDestination + (100-percentage)*startX)/100;
			double coordY = (percentage*yDestination + (100-percentage)*startY)/100;
			
			double newWidth = (percentage*widthDestination + (100-percentage)*startWidth)/100;
			double newHeight = (percentage*heightDestination + (100-percentage)*startHeight)/100;
			
			//Set the size of the hand holder
			handHolder.setSize((int)newWidth, (int)newHeight);
			
			//Set the location of the hand
			setHandLocation((int)coordX, (int)coordY);
			
			frame.repaint();
		}
		
	}

	/**
	 * Listens to the reset button
	 */
	private class ResetReformist implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(rotateTimer.isRunning() || //If a piece is rotating
					solveTimer.isRunning() || //If the puzzle is solving itself
					floatTimer.isRunning() || //If a piece is floating
					handHolder.isOccupied()//If a piece is being dragged
					) return;//Get out
			djTimer.stop();
			reset();//reset
			wasSolved = false;//Solve is no longer clicked. and the user can get credit.
			
			frame.repaint();
		}
	}
	
	/**
	 * Handles the resizing of the window
	 */
	private class MasterOfTheResize implements ComponentListener{
		public void componentHidden(ComponentEvent e) {}
		public void componentMoved(ComponentEvent e) {}
		public void componentResized(ComponentEvent e) {
			
			int width = frame.getContentPane().getWidth();//Get the actual width
			int height = frame.getContentPane().getHeight();
			
			//Don't let the size be too small
			if(width<20)
				width = 20;
			if(height<20)
				height=20;
		
			//Just set the new sizes
			setSizes(width, height);
		}
		public void componentShown(ComponentEvent e) {}
	}
	
	/**
	 * Animated Rotation of a piece holder
	 */
	private class RotationPHD implements ActionListener{
		
		//The piece holder that is being rotated
		private PieceHolder pH;
		
		//the interval that the degree increases by
		private int interval;
		
		//the x coordinate of the cell
		private int x;
				
		//the y coordinate of the cell
		private int y;
		
		/**
		 * PieceHolder rotater
		 * 
		 * @param pH
		 * @param interval
		 */
		public RotationPHD(PieceHolder pH, int interval){
			//If null piece holder was sent
			if(pH==null){
				//stop the timer and get out
				rotateTimer.stop();
				return;
			}
			this.pH = pH;
			this.interval = interval;
		}
		
		/**
		 * The ctor for a piece on the grid
		 * 
		 * @param x
		 * @param y
		 * @param interval
		 */
		public RotationPHD(int x, int y, int interval){
			
			//If the passed cell is invalid
			if(!player.getGrid().isValid(x, y)){
				//Then stop the timer and get out
				rotateTimer.stop();
				return;
			}
			this.x = x;
			this.y = y;
			this.interval = interval;
		}
		
		
		public void actionPerformed(ActionEvent arg0) {
			
			if(pH==null){//If there is no piece holder then, there must be a grid cell that was passed
				if(!player.getGrid().isOccupied(x, y)){
					rotateTimer.stop();//If the grid cell is empty then stop the timer and get out
					return;
				}
				
				//The goal rotation is what the piece in the grid says it is
				int goalRotation = player.getGrid().getCell(x, y).getRotation();
				
				//The visual rotation is what the grid component says it is
				int gridRotation = gridComponent.getRotation(x, y);
				
				//If the goal is zero. We'll have it be 360 for comparison' sake.
				if(goalRotation==0)
					goalRotation=360;
				
				//If the visual rotation surpasses the real goal or if the goal is 360 and the visual rotation has not exceed beyond the interval
				if(gridRotation>=goalRotation || (goalRotation == 360 && gridRotation<=interval)){
					
					gridComponent.setRotation(x,y,goalRotation);//Correct the visual rotation to be exact
					rotateTimer.stop();//Stop the timer
				}
				else//Otherwise rotate by the interval
					gridComponent.rotate(x,y,interval);
			}
			
			//If there is a piece holder
			else{
				if(pH.isEmpty()){ //If the piece holder is empty stop and get out
					rotateTimer.stop();
					return;
				}
				
				//The goal rotation. This is the rotation of the actual piece in the piece holder.
				int goalRotation = pH.getPiece().getRotation();
				
				//the rotation of the visual piece holder
				int pHRotation = pH.getRotation();
				
				//If the goal is zero. We'll have it be 360 for comparison' sake.
				if(goalRotation==0)
					goalRotation=360;
				
				//If the visual rotation surpasses the real goal or if the goal is 360 
				//and the visual rotation has not exceed beyond the interval
				if(pHRotation>=goalRotation || (goalRotation == 360 && pHRotation<=interval)){
					pH.setRotation(goalRotation);//Correct the visual rotation to be exact
					rotateTimer.stop();//Stop the timer
				}
				else//Otherwise rotate by the interval
					pH.rotate(interval);
			}
			
			frame.repaint();
			
		}
	}
	
	/**
	 * Handles the rotation of the pieces
	 */
	private class RotationRevolution implements MouseListener{
		public void mousePressed(MouseEvent e) {
			if (wasSolved || //If the solve button was clicked
					rotateTimer.isRunning() || //If a piece is already rotating
					floatTimer.isRunning() || //If a piece is floating
					solveTimer.isRunning() ||//If the puzzle is solving 
					e.getButton()!=3//If the right mouse button is not clicked
					) return;//Then don't do nada
			

			if(handHolder.isOccupied()){//If the hand holder is being dragged
				handHolder.rotate();//rotate it
				
				//Do timer animation stuff
				rotateTimer.stop();
				rotateTimer = new Timer(timerInterval, new RotationPHD(handHolder,1));
				rotateTimer.start();
			}//Otherwise see if a piece holder was clicked
			else{
				//Get the pieceHolder that may have been clicked
				PieceHolder ph = getBankPieceHolder(e.getX(), e.getY());
				
				if(ph!=null && ph.isOccupied()){//If a piece holder was actually clicked then rotate that
					ph.rotate();
					
					//Do timer animation stuff
					rotateTimer.stop();
					rotateTimer = new Timer(timerInterval, new RotationPHD(ph,1));
					rotateTimer.start();
				}
				else{//Otherwise see if a grid cell was clicked
					Point p = getGridCellFromClick(e.getX(), e.getY());//Get the grid cell that may have been clicked
					
					if(p!=null){//If its legit
						
						//convert the coordinates to ints
						int x = p.x;
						int y = p.y;
						
						//If it is valid and occupied, then rotate that
						if (player.getGrid().isValid(x, y) && player.getGrid().isOccupied(x, y)){
							PuzzlePiece piece = player.getGrid().getCell(x, y);//Get the piece that was clicked
							piece.rotate();//rotate it
							player.remove(x, y);//take it out of the grid
							//If we can place it back
							if(player.canPlace(x, y, piece)){
								player.place(x, y, piece);//Then place it there
								
								//Do timer animation stuff
								rotateTimer.stop();
								rotateTimer = new Timer(timerInterval, new RotationPHD(x,y,1));
								rotateTimer.start();
							}
							else{//If you can't place it back, that means you couldn't rotate it in the first place
								//Rotate back
								piece.rotateTheWayOppositeOfTheOtherRotateMethod();//This is Hunter's fault
								player.place(x, y, piece);//Place the piece back
							}
						}
					}//If nothing of significance was clicked then nothing happens
				}
			}
			frame.repaint();
		}

		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent arg0) {}
	}

	/**
	 * Handles the dragging of the hand holder and the hovering
	 */
	private class SmoothMotionBoss implements MouseMotionListener{
		public void mouseDragged(MouseEvent e) {
			if(floatTimer.isRunning() || //If a piece is floating
					solveTimer.isRunning() || //If the puzzle is solving itself
					!SwingUtilities.isLeftMouseButton(e)//If the left mouse button was not pressed
					) return;//Don't do nada
			
			//Get the coordinates
			int x = e.getX();
			int y = e.getY();
			
			//So the piece is never dragged out of bounds
			if(x>frame.getContentPane().getWidth()-handHolder.getWidth()/2)
				x = frame.getContentPane().getWidth()-handHolder.getWidth()/2;
			else if(x<handHolder.getWidth()/2)
				x=handHolder.getWidth()/2;
			if(y>frame.getContentPane().getHeight()-handHolder.getHeight()/2)
				y = frame.getContentPane().getHeight()-handHolder.getHeight()/2;
			else if(y<handHolder.getHeight()/2)
				y=handHolder.getHeight()/2;
			setHandLocation(x,y);//Set the hand location and
			setHover(e.getX(),e.getY());//Set the hover
		}
		public void mouseMoved(MouseEvent e) {
			setCursor(e.getX(), e.getY());//Set the cursor
			setHover(e.getX(),e.getY());//set the hover
		}
	}
	
	/**
	 * If the window goes out of focus, the hand goes back to the grid or the bank
	 */
	private class FocusEavesDropper implements WindowFocusListener{
		public void windowGainedFocus(WindowEvent e) {
			if(solveTimer.isRunning() || floatTimer.isRunning()) return;
			dropHand(0,0);
		}
		public void windowLostFocus(WindowEvent e) {
			if(solveTimer.isRunning() || floatTimer.isRunning()) return;
			dropHand(0,0);
		}
	}
	
	/**
	 * Handles the drag and drop stuff
	 */
	private class DragAndDropDawg implements MouseListener{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {
			
			if (wasSolved || //If the solve button was clicked
					rotateTimer.isRunning() || //If a piece is rotating
					floatTimer.isRunning() || //If a piece is floating
					solveTimer.isRunning() || //If the puzzle is solving itself
					handHolder.isOccupied() || //If a piece is being dragged
					e.getButton()!=1//If the left mouse button wasn't clicked
					) return;//Then do nada
			
			

			//Get the possible piece holder in the bank
			PieceHolder ph = getBankPieceHolder(e.getX(), e.getY());
				
			//If the piece holder is legit
			if(ph!=null){
				
				originalBankHolder = ph;//Set the original piece holder to this piece holder in case the piece is dropped over lava
				setHand(originalBankHolder.removePiece());//set the hand whilst removing the piece from the piece holder
			}
			else{
				Point p = getGridCellFromClick(e.getX(), e.getY());//Get the possible grid cell that was clicked
				if(p!=null){//If the cell is legit
					
					setHand((VisualPuzzlePiece) player.getGrid().getCell((int) p.getX(), (int) p.getY()));//set the hand
					originalGridCell = p;//Set the original grid cell to this grid cell in case the piece is dropped over lava
					player.remove(p.x, p.y);//remove the piece from the grid cell
				}
			}
			
			//The hand is being moved
			if(handHolder.isOccupied())
				mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			
			setHandLocation(e.getX(), e.getY());//Set the hand location
			frame.repaint();
		}

		public void mouseReleased(MouseEvent e) {
			if (wasSolved || //If the solve button was clicked
					floatTimer.isRunning() || //If a piece is floating
					solveTimer.isRunning() || //If the puzzle is solving itself
					handHolder.isEmpty() || //If there is nothing in the hand
					e.getButton()!=1//If it was not the left mouse button that was released
					) return;//Then don't do nada
			
			//Otherwise drop the hand where it needs to go
			dropHand(e.getX(),e.getY());
			
			//Set the cursor type according to the mouse location
			setCursor(e.getX(), e.getY());
		}
	}
	
	/**
	 * The listener of the volume slider
	 */
	private class VolumePatrol implements ChangeListener{
		public void stateChanged(ChangeEvent e) {
			
//			Get the new volume
			double volume = 1.0 *volSlider.getValue()/volSlider.getMaximum();
			
			//If the volume was brought down to zero
			if(volume==0){
				soundIcon = new ImageIcon(getClass().getResource("off.png"));
				soundImage = soundIcon.getImage();
				soundImage = soundImage.getScaledInstance(-1, buttonDimension.height, Image.SCALE_SMOOTH);
				soundIcon = new ImageIcon(soundImage);
				soundLabel.setIcon(soundIcon);
				//Change the icon
				
				//The sound is now muted
				isMuted = true;
			}
//			If the sound is already muted and the slider is being set to audible
			else if(isMuted){
				soundIcon = new ImageIcon(getClass().getResource("on.png"));
				soundImage = soundIcon.getImage();
				soundImage = soundImage.getScaledInstance(-1, buttonDimension.height, Image.SCALE_SMOOTH);
				soundIcon = new ImageIcon(soundImage);
				soundLabel.setIcon(soundIcon);
//				Change the icon
				
//				Change the volume we keep
				theVolume = volume;
				
//				The sound is no longer muted
				isMuted = false;
			}
			else//Otherwise we just change the private volume data
				theVolume = volume;
			
//			Change the volume oof the players
			bgPlayer.setVolume(volume);
			victoryPlayer.setVolume(volume);
		}
	}

	/**
	 * This listens to clicks on the sound icon
	 */
	private class SoundClerk implements MouseListener{
		public void mousePressed(MouseEvent e) {
			if(isMuted)//If there is no volume
				volSlider.setValue((int) (theVolume*100));//set the volume to whatever the volume was before there was no volume
			else//Otherwise mute it
				volSlider.setValue(0);
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		
	}
	
	/**
	 * This class is necessary for us to change the background of the main panel.
	 */
	private class BoardPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int imgWidth = 1280;
		private int imgHeight = 960;
		
		
		public BoardPanel(GridBagLayout gridBagLayout) {
			super(gridBagLayout);
			
			woodBg = new ImageIcon(getClass().getResource("HONDURAS MAHOGANY.jpg")).getImage();
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D canvas = (Graphics2D) g;
			
			double widthRatio = 1;
			double heightRatio = 1;
			
			if(imgWidth<getWidth())
				widthRatio = (double)getWidth()/imgWidth;
			if(imgHeight<getHeight())
				heightRatio = (double)getHeight()/imgHeight;
			
			AffineTransform andRollout = new AffineTransform();
			andRollout.setToScale(widthRatio, heightRatio);
			
			canvas.drawImage(woodBg, andRollout, null);
		}
	}
}