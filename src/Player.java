/**
 * Player.java: A class that allows for PuzzlePieces to be placed
 * on a Grid. 
 * 
 * Authors: Nicholas Duncan and Joshua Xiong
 * 
 */


import java.util.ArrayList;

public class Player {

	private PuzzlePiece[] bank;
	private Grid g;

	/**
	 * Removes a PuzzlePiece from the bank by searching for
	 * 	an identical reference, and then setting the slot to null.
	 * @param p The PuzzlePiece to be removed from the bank
	 */
	public void removeFromBank(PuzzlePiece p) {
		if (p == null)
			return;
		for (int i = 0; i < bank.length; i++) {
			if (bank[i] != null && bank[i].equals(p)) {
				bank[i] = null;
				return;
			}
		}
	}

	/**
	 * Returns a PuzzlePiece to the bank by searching for the first
	 * 	empty slot and placing the piece there.
	 * @param p The PuzzlePiece to be added to the bank
	 */
	private void addToBank(PuzzlePiece p) {
		if (p == null)
			return;
		for (int i = 0; i < bank.length; i++) {
			if (bank[i] == null) {
				bank[i] = p;
				return;
			}
		}
	}

	/**
	 * Constructor that takes a grid and an array of pieces to be used
	 * 	for the puzzle.
	 * @param grid The grid to be used.
	 * @param pieces The set of pieces to be used.
	 */
	public Player(Grid grid, PuzzlePiece[] pieces) {
		bank = pieces;
		g = grid;
	}

	/**
	 * Removes the piece that is currently at the specified
	 * 	coordinates. The piece, if there is a piece at the location,
	 * 	will then be returned to the bank
	 * @param x The x-coordinate of the slot to have a piece removed
	 * @param y The y-coordinate of the slot to have a piece removed
	 * @return Whether there was a piece at the slot (note that if there
	 * 	is a piece there, it will always be returned to the bank.
	 */
	public boolean remove(int x, int y) {
		boolean occupied = g.isOccupied(x, y);
		addToBank(g.setCell(x, y, null));
		return occupied;
	}

	/**
	 * Checks whether a certain piece can be placed at a certain slot on the grid.
	 * @param x The x-coordinate of the slot that we want to place it at
	 * @param y The y-coordinate of the slot that we want to place it at
	 * @param p The piece that we want to place at the location
	 * @return Whether the piece fits at the location specified. A piece is said
	 * 	to "fit" if and only if each side is the complement of the side that it is
	 * 	to be adjoined with, of if the side that it is to be adjoined with contains no
	 * 	other piece.
	 */
	public boolean canPlace(int x, int y, PuzzlePiece p) {
		return !(g.isOccupied(x, y))
				&& ((g.getCell(x - 1, y) == null || (g.getCell(x - 1, y)
						.getSide(PuzzlePiece.EAST) == -p
						.getSide(PuzzlePiece.WEST))))
				&& ((g.getCell(x, y - 1) == null || (g.getCell(x, y - 1)
						.getSide(PuzzlePiece.SOUTH) == -p
						.getSide(PuzzlePiece.NORTH))))
				&& ((g.getCell(x + 1, y) == null || (g.getCell(x + 1, y)
						.getSide(PuzzlePiece.WEST) == -p
						.getSide(PuzzlePiece.EAST))))
				&& ((g.getCell(x, y + 1) == null || (g.getCell(x, y + 1)
						.getSide(PuzzlePiece.NORTH) == -p
						.getSide(PuzzlePiece.SOUTH))));
	}

	/**
	 * Attempts to place the piece, with its current orientation at the
	 * location specified.
	 * @param x The x-coordinate of the slot that we want to place it at
	 * @param y The y-coordinate of the slot that we want to place it at
	 * @param p The piece that we want to place at the location
	 * @return Whether the piece was placed at the location
	 */
	public boolean place(int x, int y, PuzzlePiece p) {
		if (p != null && canPlace(x, y, p)) {
			removeFromBank(p);
			g.setCell(x, y, p);
			return true;
		}
		return false;
	}

	/**
	 * Removes all pieces from the grid and returns them to the bank.
	 * Does not return anything.
	 */
	public void returnAllPieces() {
		for (int i = 0; i < g.getWidth(); i++) {
			for (int j = 0; j < g.getHeight(); j++) {
				remove(j, i);
			}
		}
	}

	/**
	 * Sets all of the grid locations to null, effectively removing all of the pieces.
	 * Does not return anything.
	 */
	private void clear() {
		for (int i = 0; i < g.getWidth(); i++)
			for (int j = 0; j < g.getHeight(); j++)
				g.setCell(i, j, null);
	}

	/**
	 * Creates an ArrayList of all of the possible permutations of the pieces in the bank.
	 * See solve() for a description of usefulness.
	 * @param pieces The array of pieces to create the list of permutations from.
	 * @return An ArrayList of the permutations of the pieces
	 */
	private static ArrayList<PuzzlePiece[]> permutation(PuzzlePiece[] pieces) {
		ArrayList<PuzzlePiece[]> perms = new ArrayList<PuzzlePiece[]>();
		if (pieces.length == 1) {
			perms.add(pieces);
			return perms;
		}
		for (int i = 0; i < pieces.length; i++) {
			PuzzlePiece[] smallPieces = new PuzzlePiece[pieces.length - 1];
			for (int j = 0; j < i; j++)
				smallPieces[j] = pieces[j];
			for (int j = i; j < pieces.length - 1; j++)
				smallPieces[j] = pieces[j + 1];

			ArrayList<PuzzlePiece[]> smallPerms = permutation(smallPieces);
			for (PuzzlePiece[] pieceArray : smallPerms) {
				PuzzlePiece[] pieceArray2 = new PuzzlePiece[pieceArray.length + 1];
				System.arraycopy(pieceArray, 0, pieceArray2, 1, pieceArray.length);
				pieceArray2[0] = pieces[i];
				perms.add(pieceArray2);
			}
		}
		return perms;
	}

	/**
	 * Solves the puzzle. Using the list of permutations of the pieces,
	 * 	we assign index 0 to the top-left corner and then each successive
	 * 	index will be assigned from left to right and then top to bottom.
	 * 	Thus each of the arrays that are in the ArrayList force the
	 * 	pieces to go in a certain order. If, at any point, a piece cannot
	 * 	be placed in this successive order, we will go to the next array
	 * 	and try again.
	 * @return Whether the puzzle was solved.
	 */
	public boolean solve() {
		returnAllPieces();
		ArrayList<PuzzlePiece[]> allPermutations = permutation(bank);
		for (int i = allPermutations.size()-1;i >-1; i--) {
			PuzzlePiece[] p = allPermutations.get(i);
			boolean isSuccessful = false;
			for (int j = 0; j < 4; j++) {
				place(0, 0, p[0]);
				for (int k = 1; k < p.length; k++) {
					if (!tryToPlace(k % g.getWidth(), k / g.getHeight(), p[k])) {
						isSuccessful = false;
						clear();
						break;
					} else
						isSuccessful = true;
				}
				if (isSuccessful)
					return true;
				p[0].rotate();
			}
		}
		bank = allPermutations.get(0);
		return false;
	}

	/**
	 * Tries to place a piece at a certain location of the grid by trying
	 * to place it using any of its four orientations.
	 * @param x The x-coordinate of the slot that we want to place it at
	 * @param y The y-coordinate of the slot that we want to place it at
	 * @param p The piece that we want to place at the location
	 * @return Whether the piece could be placed at the location
	 */
	private boolean tryToPlace(int x, int y, PuzzlePiece p) {
		boolean hasPlaced = false;
		if (p == null)
			return false;
		for (int j = 0; j < 4; j++) {
			if (place(x, y, p)) {
				hasPlaced = true;
				break;
			}
			p.rotate();
		}
		return hasPlaced;
	}
	
	/**
	 * Randomizes the order and the orientation of the bank.
	 * Does not return anything.
	 */
	public void randomizeBank() {
		returnAllPieces();
		ArrayList<PuzzlePiece> pieces = new ArrayList<PuzzlePiece>();
		for (int i = 0; i< bank.length; i++)
			pieces.add(bank[i]);
		for (int i = bank.length - 1; i > -1; i--) {
			int index = (int) (Math.random() * i);
			bank[i] = pieces.remove(index);
		}
		
		for (int i = 0; i < bank.length; i++) {
			int rotate = (int) (Math.random() * 4);
			for (int j = 0; j < rotate; j++)
				bank[i].rotate();
		}
	}
	
	/**
	 * Returns a reference to the bank of PuzzlePieces
	 * @return The current bank of PuzzlePieces
	 */
	public PuzzlePiece[] get$Bank() {
		return bank;
	}

	/**
	 * Returns a reference to the current grid of PuzzlePieces.
	 * @return The current grid
	 */
	public Grid getGrid() {
		return g;
	}


}