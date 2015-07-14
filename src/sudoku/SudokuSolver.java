package sudoku;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 
 * @author shawila
 */
public class SudokuSolver {
	private ArrayList<Integer>[][] grid;
	private ArrayList<Integer>[] countRow, countColumn;
	private Queue<String> queue;
	private static StringTokenizer tok;
	private static int counter;
	private boolean solvable;
	private final int SIZE = 9;

	/** Creates a new instance of SudokuSolver */
	@SuppressWarnings("unchecked")
	public SudokuSolver() {
		grid = new ArrayList[SIZE][SIZE];
		countRow = new ArrayList[SIZE];
		countColumn = new ArrayList[SIZE];

		queue = new LinkedList<String>();
		counter = 0;
		solvable = true;

		// set up the square domains
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++) {
				grid[i][j] = new ArrayList<Integer>();
				for (int k = 1; k <= SIZE; k++)
					grid[i][j].add(new Integer(k));
			}

		// set up the value counters for rows, columns and boxes
		for (int i = 0; i < SIZE; i++) {
			countRow[i] = new ArrayList<Integer>();
			countColumn[i] = new ArrayList<Integer>();

			countRow[i].add(new Integer(0));
			countColumn[i].add(new Integer(0));

			for (int j = 0; j < SIZE; j++) {
				countRow[i].add(new Integer(9));
				countColumn[i].add(new Integer(9));
			}
		}

		/*
		 * / set up a moderate sudoku puzzle setVal(0, 2, 9); setVal(0, 5, 3); setVal(0, 8, 7); setVal(1, 1, 8);
		 * setVal(1, 3, 4); setVal(1, 6, 5); setVal(2, 0, 4); setVal(2, 3, 5); setVal(3, 0, 9); setVal(3, 1, 3);
		 * setVal(3, 3, 6); setVal(3, 6, 7); setVal(4, 0, 2); setVal(4, 3, 1); setVal(4, 5, 7); setVal(4, 8, 6);
		 * setVal(5, 2, 5); setVal(5, 5, 9); setVal(5, 7, 1); setVal(5, 8, 2); setVal(6, 5, 2); setVal(6, 8, 8);
		 * setVal(7, 2, 4); setVal(7, 5, 1); setVal(7, 7, 7); setVal(8, 0, 1); setVal(8, 3, 3); setVal(8, 6, 6);
		 */

		/*
		 * / sets up a diabolical sudoku puzzle setVal(0, 0, 8); setVal(0, 3, 2); setVal(0, 5, 9); setVal(0, 8, 7);
		 * setVal(1, 2, 9); setVal(1, 3, 3); setVal(1, 5, 6); setVal(1, 6, 2); setVal(2, 0, 3); setVal(2, 8, 6);
		 * setVal(3, 2, 7); setVal(3, 3, 6); setVal(3, 5, 1); setVal(3, 6, 3); setVal(4, 1, 3); setVal(4, 7, 5);
		 * setVal(5, 2, 8); setVal(5, 3, 9); setVal(5, 5, 3); setVal(5, 6, 6); setVal(6, 0, 1); setVal(6, 8, 8);
		 * setVal(7, 2, 3); setVal(7, 3, 1); setVal(7, 5, 8); setVal(7, 6, 7); setVal(8, 0, 9); setVal(8, 3, 4);
		 * setVal(8, 5, 7); setVal(8, 8, 3);
		 */

		/*
		 * / sets up a 16x16 sudoku puzzle setVal(0, 0, 1); setVal(0, 3, 7); setVal(0, 4, 11); setVal(0, 7, 15);
		 * setVal(0, 9, 9); setVal(0, 10, 10); setVal(0, 11, 6); setVal(0, 12, 12); setVal(0, 15, 5); setVal(1, 1, 15);
		 * setVal(1, 5, 13); setVal(1, 6, 7); setVal(1, 8, 2); setVal(1, 9, 1); setVal(1, 10, 12); setVal(1, 14, 4);
		 * setVal(2, 2, 3); setVal(2, 5, 1); setVal(2, 6, 14); setVal(2, 8, 4); setVal(2, 11, 5); setVal(2, 13, 9);
		 * setVal(3, 0, 4); setVal(3, 3, 10); setVal(3, 4, 5); setVal(3, 7, 6); setVal(3, 9, 3); setVal(3, 10, 13);
		 * setVal(3, 12, 11); setVal(3, 15, 1); setVal(4, 0, 10); setVal(4, 2, 11); setVal(4, 5, 4); setVal(4, 6, 12);
		 * setVal(4, 9, 2); setVal(4, 10, 16); setVal(4, 13, 8); setVal(4, 15, 7); setVal(5, 1, 6); setVal(5, 3, 8);
		 * setVal(5, 4, 15); setVal(5, 7, 1); setVal(5, 8, 10); setVal(5, 11, 7); setVal(5, 12, 13); setVal(5, 14, 14);
		 * setVal(6, 2, 1); setVal(6, 3, 16); setVal(6, 4, 7); setVal(6, 7, 2); setVal(6, 8, 6); setVal(6, 11, 12);
		 * setVal(6, 12, 10); setVal(6, 13, 11); setVal(7, 0, 14); setVal(7, 2, 15); setVal(7, 5, 10); setVal(7, 6, 6);
		 * setVal(7, 9, 13); setVal(7, 10, 3); setVal(7, 13, 12); setVal(7, 15, 4); setVal(8, 0, 16); setVal(8, 2, 4);
		 * setVal(8, 5, 15); setVal(8, 6, 8); setVal(8, 9, 6); setVal(8, 10, 2); setVal(8, 13, 5); setVal(8, 15, 10);
		 * setVal(9, 2, 2); setVal(9, 3, 6); setVal(9, 4, 1); setVal(9, 7, 5); setVal(9, 7, 13); setVal(9, 11, 8);
		 * setVal(9, 12, 9); setVal(9, 13, 4); setVal(10, 1, 13); setVal(10, 3, 12); setVal(10, 4, 9); setVal(10, 7, 3);
		 * setVal(10, 8, 15); setVal(10, 11, 16); setVal(10, 12, 1); setVal(10, 14, 2); setVal(11, 0, 11); setVal(11, 2,
		 * 8); setVal(11, 5, 6); setVal(11, 6, 16); setVal(11, 9, 5); setVal(11, 10, 9); setVal(11, 13, 13); setVal(11,
		 * 15, 11); setVal(12, 0, 13); setVal(12, 3, 14); setVal(12, 5, 9); setVal(12, 6, 15); setVal(12, 8, 5);
		 * setVal(12, 11, 2); setVal(12, 12, 4); setVal(12, 15, 11); setVal(13, 2, 6); setVal(13, 4, 4); setVal(13, 7,
		 * 10); setVal(13, 9, 16); setVal(13, 10, 7); setVal(13, 13, 1); setVal(14, 1, 8); setVal(14, 5, 16); setVal(14,
		 * 6, 1); setVal(14, 7, 11); setVal(14, 9, 15); setVal(14, 10, 6); setVal(14, 14, 13); setVal(15, 0, 2);
		 * setVal(15, 3, 4); setVal(15, 4, 12); setVal(15, 5, 8); setVal(15, 6, 5); setVal(15, 8, 14); setVal(15, 11,
		 * 3); setVal(15, 12, 15); setVal(15, 15, 9);
		 */
	}

	/**
	 * asserts value in square of coordinates row and column and cascades results onto appropriate row, column and
	 * square while updating domains of respective squares, check if domain becomes a singleton in which case we assert
	 * that value by adding that assert to queue
	 */

	private void assertVal(int row, int column, int value) {
		// if domain of row and column does not contain value return
		if (!grid[row][column].contains(new Integer(value)))
			return;

		// check row
		for (int i = 0; i < SIZE; i++)
			if (i != column) {
				/**
				 * if the value to be asserted is present in square domain remove it 1- check if the domain contains
				 * only one element left, if so assert it by adding assert command to queue 2- decrement counter of
				 * value in corresponding column
				 */
				if (grid[row][i].remove(new Integer(value))) {
					if (grid[row][i].size() == 1)
						queue.add(new String("assertVal:" + row + ":" + i + ":" + grid[row][i]));
					fixColumn(i, value);

					if (grid[row][i].size() == 0)
						solvable = false;
				}
			}

		// check column similar to row
		for (int i = 0; i < SIZE; i++)
			if (i != row) {
				if (grid[i][column].remove(new Integer(value))) {
					if (grid[i][column].size() == 1)
						queue.add(new String("assertVal:" + i + ":" + column + ":" + grid[i][column]));
					fixRow(i, value);

					if (grid[i][column].size() == 0)
						solvable = false;
				}
			}

		// check box similar to row and column
		int sqrt = ((int) Math.sqrt(SIZE));
		int boxRow = (int) row / sqrt;
		int boxColumn = (int) column / sqrt;
		for (int i = boxRow * sqrt; i < boxRow * sqrt + sqrt; i++)
			for (int j = boxColumn * sqrt; j < boxColumn * sqrt + sqrt; j++) {
				if (i != row && j != column) {
					if (grid[i][j].remove(new Integer(value))) {
						if (grid[i][j].size() == 1)
							queue.add(new String("assertVal:" + i + ":" + j + ":" + grid[i][j]));
						fixColumn(j, value);
						fixRow(i, value);

						if (grid[i][j].size() == 0)
							solvable = false;
					}
				}
			}
		/*
		 * try { Thread.sleep(100); } catch (InterruptedException e) {};
		 */
	}

	// method to remove a value from domain of given square
	private void disassertVal(int row, int column, int value) {
		// if value not in domain don't do anything
		if (grid[row][column].remove(new Integer(value))) {
			if (grid[row][column].size() == 1)
				queue.add(new String("assertVal:" + row + ":" + column + ":" + grid[row][column]));
			fixRow(row, value);
			fixColumn(column, value);
		}
	}

	// method to set value in given squares
	public void setVal(int row, int column, int value) {
		// if domain of row and column doesn't contain value return
		if (!grid[row][column].contains(new Integer(value)))
			return;

		Iterator<Integer> iterator = grid[row][column].iterator();
		while (iterator.hasNext()) {
			Integer tempInteger = iterator.next();
			int tempInt = tempInteger.intValue();
			if (tempInt != value) {
				/**
				 * set the counter of the value asserted corresponding to the row, column and box of square asserted to
				 * 0
				 */
				countRow[row].set(new Integer(0), value);
				countColumn[column].set(new Integer(0), value);
				fixRow(row, tempInt);
				fixColumn(column, tempInt);
			}
		}

		grid[row][column] = new ArrayList<Integer>();
		grid[row][column].add(new Integer(value));
		assertVal(row, column, value);
	}

	// method to update counters for row and do appropriate changes
	private void fixRow(int row, int value) {
		ArrayList<Integer> tempVector = countRow[row];
		Integer tempInteger = tempVector.get(value);
		if (tempInteger != 0)
			tempVector.set(--tempInteger, value);

		if (tempInteger.intValue() == 1)
			queue.add(new String("checkRow:" + row + ":" + value));
	}

	// method to update counters for column and do appropriate changes
	private void fixColumn(int column, int value) {
		ArrayList<Integer> tempVector = countColumn[column];
		Integer tempInteger = tempVector.get(value);
		if (tempInteger != 0)
			tempVector.set(--tempInteger, value);

		/**
		 * if there remains only 1 occurence of value in domains of whole column look for it and assert it by adding
		 * checkColumn commmand to queue
		 */
		if (tempInteger.intValue() == 1)
			queue.add(new String("checkColumn:" + column + ":" + value));
	}

	// method to find value to assert in given row
	private void checkRow(int row, int value) {
		for (int i = 0; i < SIZE; i++)
			if (grid[row][i].contains(new Integer(value)))
				setVal(row, i, value);
	}

	// method to find value to assert in given column
	private void checkColumn(int column, int value) {
		for (int i = 0; i < SIZE; i++)
			if (grid[i][column].contains(new Integer(value)))
				setVal(i, column, value);
	}

	// method to find value to assert in given box
	private void checkBox(int box, int value) {
		int sqrt = (int) Math.sqrt(SIZE);
		int intRow = (int) box / sqrt;
		int intColumn = box % value;

		for (int i = intRow * sqrt; i < intRow * sqrt + sqrt; i++)
			for (int j = intColumn * sqrt; j < intColumn * sqrt + sqrt; j++)
				if (grid[intRow][intColumn].contains(new Integer(value)))
					setVal(intRow, intColumn, value);
	}

	// method to find next choice to make
	private int[] findNext() {
		int[] result = new int[3];
		int constrainedMost = 1000;
		int constrainingMost = 1000;
		int constrainingLeast = 1000;

		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++) {

				// check for most constrained variable
				ArrayList<Integer> tempVector = grid[i][j];
				if (tempVector.size() <= constrainedMost && tempVector.size() != 1) {
					if (tempVector.size() < constrainedMost) {
						constrainedMost = tempVector.size();
						constrainingMost = 1000;
						constrainingLeast = 1000;
					}

					// check for most constraining variable
					int tempConstrainingMost = -2;
					Iterator<Integer> iterator = countRow[i].iterator();
					while (iterator.hasNext()) {
						Integer tempInteger = iterator.next();
						if (tempInteger.intValue() == 0)
							tempConstrainingMost++;
					}
					iterator = countColumn[j].iterator();
					while (iterator.hasNext()) {
						Integer tempInteger = iterator.next();
						if (tempInteger.intValue() == 0)
							tempConstrainingMost++;
					}

					if (tempConstrainingMost <= constrainingMost) {
						if (tempConstrainingMost < constrainingMost)
							constrainingLeast = 1000;
						constrainingMost = tempConstrainingMost;

						// check for least constraining value
						iterator = grid[i][j].iterator();
						while (iterator.hasNext()) {
							int tempConstrainingLeast = 0;
							Integer integer = iterator.next();
							int tempInt = integer.intValue();
							Integer tempInteger = countRow[i].get(tempInt);
							tempConstrainingLeast += tempInteger;
							tempInteger = countColumn[j].get(tempInt);
							tempConstrainingLeast += tempInteger;

							if (tempConstrainingLeast < constrainingLeast) {
								constrainingLeast = tempConstrainingLeast;

								result[0] = i;
								result[1] = j;
								result[2] = tempInt;
							}
						}
					}
				}
			}
		return result;
	}

	private void process() {
		// process commands in queue
		while (queue.size() != 1) {
			tok = new StringTokenizer(queue.peek(), ":");
			String action = tok.nextToken();

			if (action.equals("assertVal")) {
				int tempRow = Integer.parseInt(tok.nextToken());
				int tempColumn = Integer.parseInt(tok.nextToken());

				action = tok.nextToken();
				action = action.substring(1, action.length() - 1);
				int tempVal = Integer.parseInt(action);

				assertVal(tempRow, tempColumn, tempVal);
			} else if (action.equals("checkRow")) {
				int tempRow = Integer.parseInt(tok.nextToken());
				int tempVal = Integer.parseInt(tok.nextToken());

				checkRow(tempRow, tempVal);
			} else if (action.equals("checkColumn")) {
				int tempColumn = Integer.parseInt(tok.nextToken());
				int tempVal = Integer.parseInt(tok.nextToken());

				checkColumn(tempColumn, tempVal);
			} else if (action.equals("checkBox")) {
				int tempBox = Integer.parseInt(tok.nextToken());
				int tempVal = Integer.parseInt(tok.nextToken());

				checkBox(tempBox, tempVal);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void solve(int[] choice) {
		ArrayList<Integer>[][] oldGrid = new ArrayList[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				oldGrid[i][j] = (ArrayList<Integer>) grid[i][j].clone();
		ArrayList<Integer>[] oldCountRow = new ArrayList[SIZE];
		for (int i = 0; i < SIZE; i++)
			oldCountRow[i] = (ArrayList<Integer>) countRow[i].clone();
		ArrayList<Integer>[] oldCountColumn = new ArrayList[SIZE];
		for (int i = 0; i < SIZE; i++)
			oldCountColumn[i] = (ArrayList<Integer>) countColumn[i].clone();

		setVal(choice[0], choice[1], choice[2]);
		process();

		if (!isSolved() && solvable) {
			int[] newChoice = findNext();
			solve(newChoice);
		}
		if (isSolved())
			return;
		if (!solvable) {
			grid = oldGrid;
			countRow = oldCountRow;
			countColumn = oldCountColumn;

			solvable = true;
			counter++;
			disassertVal(choice[0], choice[1], choice[2]);
			process();
		}
	}

	public boolean isSolved() {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (grid[i][j].size() != 1)
					return false;
		return true;
	}

	private void display() {
		// display the grid
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Iterator<Integer> iterator = grid[i][j].iterator();
				while (iterator.hasNext())
					System.out.print(iterator.next() + " ");
				System.out.print("\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public ArrayList<Integer>[][] getGrid() {
		return grid;
	}

	public void start() {
		process();
		int[] choice = findNext();
		solve(choice);
		display();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		SudokuSolver sudoku = new SudokuSolver();

		sudoku.display();
		System.out.println("Number of backtracks: " + ++counter);
		/*
		 * display the counters for (int i = 0; i < grid.length; i++) { Iterator iterator = countRow[i].iterator();
		 * while (iterator.hasNext()) System.out.print(iterator.next() + " "); System.out.print("\t\t");
		 * 
		 * System.out.println(); } System.out.println();
		 * 
		 * // display the counters for (int i = 0; i < grid.length; i++) { Iterator iterator =
		 * countColumn[i].iterator(); while (iterator.hasNext()) System.out.print(iterator.next() + " ");
		 * System.out.print("\t\t");
		 * 
		 * System.out.println(); } System.out.println();
		 */

		// sudoku.process();

		// sudoku.display();

		/*
		 * display the counters for (int i = 0; i < grid.length; i++) { Iterator iterator = countRow[i].iterator();
		 * while (iterator.hasNext()) System.out.print(iterator.next() + " "); System.out.print("\t\t");
		 * 
		 * System.out.println(); } System.out.println();
		 * 
		 * // display the counters for (int i = 0; i < grid.length; i++) { Iterator iterator =
		 * countColumn[i].iterator(); while (iterator.hasNext()) System.out.print(iterator.next() + " ");
		 * System.out.print("\t\t");
		 * 
		 * System.out.println(); } System.out.println();
		 */

		// int[] choice = sudoku.findNext();
		// sudoku.solve(choice);
		// sudoku.display();
	}
}