package sudoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author shawila
 */
public class SuperSudokuSolver {
	private ArrayList<Integer>[][] grids;
	private ArrayList<Integer>[] countRow, countColumn;
	private LinkedList<String> queue;

	private static StringTokenizer tok;
	private boolean solvable;

	private final int SIZE = 25;
	private final String fileName = "puzzle-25_5423.txt";

	public static void main(String[] args) {
		SuperSudokuSolver sudoku = new SuperSudokuSolver();

		long t1 = System.currentTimeMillis();
		sudoku.start();
		sudoku.display();
		long t2 = System.currentTimeMillis();
		System.out.print((t2 - t1) / 1);
	}

	/** Creates a new instance of SudokuSolver */
	@SuppressWarnings("unchecked")
	public SuperSudokuSolver() {
		grids = new ArrayList[SIZE][SIZE];
		countRow = new ArrayList[SIZE];
		countColumn = new ArrayList[SIZE];

		queue = new LinkedList<String>();
		solvable = true;

		// set up the square domains
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				grids[i][j] = new ArrayList<Integer>();
				for (int k = 1; k <= SIZE; k++) {
					grids[i][j].add(new Integer(k));
				}
			}
		}

		// set up the value counters for rows, columns and boxes
		for (int i = 0; i < SIZE; i++) {
			countRow[i] = new ArrayList<Integer>();
			countColumn[i] = new ArrayList<Integer>();

			// these are buffer values so that the vector will have value SIZE
			// for indices 1->SIZE
			countRow[i].add(new Integer(0));
			countColumn[i].add(new Integer(0));

			for (int j = 0; j < SIZE; j++) {
				countRow[i].add(new Integer(SIZE));
				countColumn[i].add(new Integer(SIZE));
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		try {
			File file = new File(fileName);

			BufferedReader bufRdr = new BufferedReader(new FileReader(file));
			String line = null;
			int row = 0;
			int col = 0;

			// read each line of text file
			while ((line = bufRdr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");
				col = 0;
				while (st.hasMoreTokens()) {
					// get next token and store it in the array if not empty
					String str = st.nextToken();

					// set insets to make squares more visible
					int top = 0;
					int left = 0;
					int squareSize = (int)Math.sqrt(SIZE);
					if (row != 0 && row % squareSize == 0) {
						top += 2;
					}
					if (col != 0 && col % squareSize == 0) {
						left += 2;
					}
					
					GridBagConstraints constraints = new GridBagConstraints(col, row, 1, 1, 1, 1,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(top, left, 0, 0), 0, 0);
					JTextField field = new JTextField();
					field.setMinimumSize(new Dimension(20, 20));
					field.setEditable(false);
					if (!str.equals("0")) {
						field.setText(str);
					} else {
						field.setText(" ");
					}
					panel.add(field, constraints);

					if (!str.equals("0")) {
						setVal(row, col, Integer.parseInt(str));
					}
					col++;
				}
				row++;
			}

			// close the file
			bufRdr.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 800));
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/*
	 * if update is true asserts value in box
	 * else returns number of boxes that would be affected if we assert (boxes that contain the value in possible choices
	 */
	private int assertBox(int row, int column, int value, boolean update) {
		int count = 0;

		int sqrt = ((int) Math.sqrt(SIZE));
		int boxRow = (int) row / sqrt;
		int boxColumn = (int) column / sqrt;
		for (int i = boxRow * sqrt; i < boxRow * sqrt + sqrt; i++) {
			for (int j = boxColumn * sqrt; j < boxColumn * sqrt + sqrt; j++) {
				if (i != row && j != column) {
					if (!update && grids[i][j].contains(new Integer(value))) {
						count++;
					} else if (grids[i][j].remove(new Integer(value))) {
						if (grids[i][j].size() == 1) {
							queue.add(new String("assertVal:" + i + ":" + j + ":" + grids[i][j]));
						}
						fixColumn(j, value);
						fixRow(i, value);

						if (grids[i][j].size() == 0) {
							solvable = false;
						}
					}
				}
			}
		}
		return count;
	}

	/*
	 * same as assert box but for column
	 */
	private int assertColumn(int row, int column, int value, boolean update) {
		int count = 0;

		for (int i = 0; i < SIZE; i++) {
			if (i != row) {
				if (!update && grids[i][column].contains(new Integer(value))) {
					count++;
				} else if (grids[i][column].remove(new Integer(value))) {
					if (grids[i][column].size() == 1) {
						queue.add(new String("assertVal:" + i + ":" + column + ":" + grids[i][column]));
					}
					fixRow(i, value);

					if (grids[i][column].size() == 0) {
						solvable = false;
					}
				}
			}
		}

		return count;
	}

	/*
	 * same as assert box but for row
	 */
	private int assertRow(int row, int column, int value, boolean update) {
		int count = 0;

		for (int i = 0; i < SIZE; i++) {
			if (i != column) {
				/**
				 * if the value to be asserted is present in square domain remove it
				 * 1- check if the domain contains only one element left, if so assert it by adding assert command to queue
				 * 2- decrement counter of value in corresponding column
				 */
				if (!update && grids[row][i].contains(new Integer(value))) {
					count++;
				} else if (grids[row][i].remove(new Integer(value))) {
					if (grids[row][i].size() == 1) {
						queue.add(new String("assertVal:" + row + ":" + i + ":" + grids[row][i]));
					}
					fixColumn(i, value);

					if (grids[row][i].size() == 0) {
						solvable = false;
					}
				}
			}
		}

		return count;
	}

	/**
	 * asserts value in square of coordinates row and column and cascades results onto appropriate row, column and
	 * square while updating domains of respective squares, check if domain becomes a singleton in which case we assert
	 * that value by adding that assert to queue
	 */
	private void assertVal(int row, int column, int value) {
		// if domain of row and column does not contain value return
		if (!grids[row][column].contains(new Integer(value)))
			return;

		// check row
		assertRow(row, column, value, true);

		// check column similar to row
		assertColumn(row, column, value, true);

		// check box similar to row and column
		assertBox(row, column, value, true);
	}

	// method to find value to assert in given box
	private void checkBox(int box, int value) {
		int sqrt = (int) Math.sqrt(SIZE);
		int intRow = (int) box / sqrt;
		int intColumn = box % value;

		for (int i = intRow * sqrt; i < intRow * sqrt + sqrt; i++) {
			for (int j = intColumn * sqrt; j < intColumn * sqrt + sqrt; j++) {
				if (grids[intRow][intColumn].contains(new Integer(value))) {
					setVal(intRow, intColumn, value);
				}
			}
		}
	}

	// method to find value to assert in given column
	private void checkColumn(int column, int value) {
		for (int i = 0; i < SIZE; i++) {
			if (grids[i][column].contains(new Integer(value))) {
				setVal(i, column, value);
			}
		}
	}

	// method to find value to assert in given row
	private void checkRow(int row, int value) {
		for (int i = 0; i < SIZE; i++) {
			if (grids[row][i].contains(new Integer(value))) {
				setVal(row, i, value);
			}
		}
	}

	private void display() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				// set insets to make squares more visible
				int top = 0;
				int left = 0;
				int squareSize = (int)Math.sqrt(SIZE);
				if (i != 0 && i % squareSize == 0) {
					top += 2;
				}
				if (j != 0 && j % squareSize == 0) {
					left += 2;
				}

				GridBagConstraints constraints = new GridBagConstraints(j, i, 1, 1, 1, 1, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(top, left, 0, 0), 0, 0);
				JTextField field = new JTextField();
				field.setMinimumSize(new Dimension(20, 20));
				field.setEditable(false);
				if (grids[i][j].size() == 1) {
					field.setText(grids[i][j].get(0).toString());
				} else {
					field.setBackground(Color.RED);
				}
				panel.add(field, constraints);
			}
		}

		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 800));
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	// method to find next choice to make
	private Grid findNext() {
		int constrainedMost = Integer.MAX_VALUE;
		int constrainingMost = Integer.MIN_VALUE;

		ArrayList<Grid> choices = null;

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {

				// check for most constrained variable
				ArrayList<Integer> tempVector = grids[i][j];
				if (tempVector.size() <= constrainedMost && tempVector.size() != 1) {
					if (tempVector.size() < constrainedMost) {
						constrainedMost = tempVector.size();
						// clear points and add new point
						choices = new ArrayList<Grid>();
						choices.add(new Grid(i, j));
					} else {
						// add point since it's equal
						choices.add(new Grid(i, j));
					}
				}
			}
		}
		if (choices == null) {
			return null;
		}
		if (choices.size() == 1) {
			return choices.get(0);
		}

		Grid bestChoice = null;
		for (Grid choice: choices) {
			int tempConstrainingMost = -1;
			ArrayList<Integer> values = grids[choice.row][choice.column];
			for (int i = 0; i < values.size(); i++) {
				tempConstrainingMost += assertBox(choice.row, choice.column, values.get(i), false);
				tempConstrainingMost += assertColumn(choice.row, choice.column, values.get(i), false);
				tempConstrainingMost += assertRow(choice.row, choice.column, values.get(i), false);
				if (tempConstrainingMost > constrainingMost) {
					constrainingMost = tempConstrainingMost;
					choice.index = i;
					bestChoice = choice;
				}
			}
		}
		return bestChoice;
	}

	// method to update counters for column and do appropriate changes
	private void fixColumn(int column, int value) {
		ArrayList<Integer> tempVector = countColumn[column];
		Integer tempInteger = tempVector.get(value);
		if (tempInteger != 0) {
			tempVector.set(value, --tempInteger);
		}

		/**
		 * if there remains only 1 occurence of value in domains of whole column
		 * look for it and assert it by adding checkColumn commmand to queue
		 */
		if (tempInteger.intValue() == 1) {
			queue.add(new String("checkColumn:" + column + ":" + value));
		}
	}

	// method to update counters for row and do appropriate changes
	private void fixRow(int row, int value) {
		ArrayList<Integer> tempVector = countRow[row];
		Integer tempInteger = tempVector.get(value);
		if (tempInteger.intValue() != 0) {
			tempVector.set(value, --tempInteger);
		}

		if (tempInteger.intValue() == 1) {
			queue.add(new String("checkRow:" + row + ":" + value));
		}
	}

	private boolean isSolved() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (grids[i][j].size() != 1) {
					return false;
				}
			}
		}
		return true;
	}

	private void process() {
		// process commands in queue
		while (!queue.isEmpty()) {
			tok = new StringTokenizer(queue.remove(), ":");
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

	// method to set value in given squares
	private void setVal(int row, int column, int value) {
		// if domain of row and column doesn't contain value return
		if (!grids[row][column].contains(new Integer(value)) || grids[row][column].size() == 1) {
			return;
		}

		System.out.println("Set value: " + value + " at (" + row + ", " + column + ")");
		// set the value to 0 to show that we already got this value
		countRow[row].set(value, new Integer(0));
		countColumn[column].set(value, new Integer(0));

		Iterator<Integer> iterator = grids[row][column].iterator();
		while (iterator.hasNext()) {
			Integer tempInteger = iterator.next();
			int tempInt = tempInteger.intValue();
			if (tempInt != value) {
				/**
				 * set the counter of the value asserted corresponding to the
				 * row, column and box of square asserted to 0
				 */
				fixRow(row, tempInt);
				fixColumn(column, tempInt);
			}
		}

		grids[row][column] = new ArrayList<Integer>();
		grids[row][column].add(new Integer(value));
		assertVal(row, column, value);
	}

	@SuppressWarnings("unchecked")
	private void solve(Grid choice) {
		ArrayList<Integer>[][] oldGrids = new ArrayList[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				oldGrids[i][j] = (ArrayList<Integer>)grids[i][j].clone();
			}
		}
		ArrayList<Integer>[] oldCountRow = new ArrayList[SIZE];
		for (int i = 0; i < SIZE; i++) {
			oldCountRow[i] = (ArrayList<Integer>)countRow[i].clone();
		}
		ArrayList<Integer>[] oldCountColumn = new ArrayList[SIZE];
		for (int i = 0; i < SIZE; i++) {
			oldCountColumn[i] = (ArrayList<Integer>)countColumn[i].clone();
		}

		ArrayList<Integer> choices = grids[choice.row][choice.column];
		while (!isSolved()) {
			System.out.println("\nChoice");
			// first try the value we assumed and process
			setVal(choice.row, choice.column, choices.get((choice.index + choice.increment) % choices.size()));
			process();

			// if still solvable but not solved yet we need to make another guess
			if (solvable) {
				Grid newChoice = findNext();
				if (newChoice == null) {
					// finished
					return;
				}
				solve(newChoice);
			} else {
				// next we remove the choice we made by incrementing the increment variable and try the next one
				if (++choice.increment == choices.size()) {
					// return and let the solve method that called this one try another choice
					return;
				}
				// we guessed wrong and need to backtrack
				grids = oldGrids;
				countRow = oldCountRow;
				countColumn = oldCountColumn;
				solvable = true;

				oldGrids = new ArrayList[SIZE][SIZE];
				for (int i = 0; i < SIZE; i++) {
					for (int j = 0; j < SIZE; j++) {
						oldGrids[i][j] = (ArrayList<Integer>)grids[i][j].clone();
					}
				}
				oldCountRow = new ArrayList[SIZE];
				for (int i = 0; i < SIZE; i++) {
					oldCountRow[i] = (ArrayList<Integer>)countRow[i].clone();
				}
				oldCountColumn = new ArrayList[SIZE];
				for (int i = 0; i < SIZE; i++) {
					oldCountColumn[i] = (ArrayList<Integer>)countColumn[i].clone();
				}
				System.out.println("\nBacktrack");
			}
		}
	}

	private void start() {
		process();
		Grid choice = findNext();
		if (choice == null) {
			// done
			return;
		}
		solve(choice);
	}

	private class Grid {
		private int row, column, index, increment;

		// value is set to 0 if there isn't a preferred value yet
		private Grid(int row, int col) {
			this.row = row;
			this.column = col;
			index = increment = 0;
		}
	}
}