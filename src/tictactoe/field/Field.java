// Copyright 2016 theaigames.com (developers@theaigames.com)

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// For the full copyright and license information, please view the LICENSE
// file that was distributed with this source code.

package tictactoe.field;

import static java.util.Arrays.asList;

import java.util.ArrayList;

public class Field {

	public int[][]	mBoard;
	public int[][]	mMacroboard;
	private int		mCols		= 9;
	public int		mRows		= 9;
	public String	mLastError	= "";
	public int		lastX		= 0;
	public int		lastY		= 0;

	public Field(int columns, int rows) {
		mBoard = new int[columns][rows];
		mMacroboard = new int[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mMacroboard[i][j] = -1;
		mCols = columns;
		mRows = rows;
		clearBoard();
	}

	public void clearBoard() {
		for (int x = 0; x < mCols; x++)
			for (int y = 0; y < mRows; y++)
				mBoard[x][y] = 0;
	}

	private boolean isInBounds(int x, int y) {
		return x >= 0 && x <= 8 && y >= 0 && y <= 8;
	}

	public Boolean placeMove(int x, int y, int player) {
		mLastError = "";
		lastX = x;
		lastY = y;
		if (isInBounds(x, y)) {
			if (isInActiveMicroboard(x, y)) {
				if (mBoard[x][y] == 0) {
					mBoard[x][y] = player;
					updateMacro();
					return true;
				}
				mLastError = "Move (" + x + "," + y + ") already placed";
				System.err.println(mLastError);
			} else {
				mLastError = "Move (" + x + "," + y + ")" + " is out of macroboard";
				System.err.println(mLastError);
			}
		} else {
			mLastError = "Move (" + x + "," + y + ") is out of bounds.";
			System.err.println(mLastError);
		}
		return false;
	}

	private boolean isBoardFull(int xM, int yM) {
		for (int x = xM * 3; x < xM * 3 + 3; x++) {
			for (int y = yM * 3; y < yM * 3 + 3; y++) {
				if (mBoard[x][y] == 0)
					return false;
			}
		}
		return true;
	}

	public int getMicroWinner(int xP, int yP) {
		int[][] micro = new int[3][3]; // select the microBoard
		int x = xP * 3;
		int y = yP * 3;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				micro[i][j] = mBoard[x + i][y + j];
			}
		}

		int countX, countO;
		ArrayList<ArrayList<Integer>> lines = getLines(micro);

		for (ArrayList<Integer> line : lines) {
			countX = countO = 0;
			for (int i = 0; i < 3; i++)
				if (line.get(i) == 1)
					countO++;
				else if (line.get(i) == 2)
					countX++;

			if (countX == 3)
				return 2;
			else if (countO == 3)
				return 1;
		}

		return 0;
	}

	private void updateMacro() {
		int nxM = lastX - lastX / 3 * 3;
		int nyM = lastY - lastY / 3 * 3;
		int currX = lastX / 3;
		int currY = lastY / 3;

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (mMacroboard[x][y] == -1) {
					mMacroboard[x][y] = 0;
				}
			}
		}

		int winner = getMicroWinner(currX, currY);
		if (winner > 0) {
			mMacroboard[currX][currY] = winner;
		}

		if (getWinner() > 0) {
			return;
		}
		if (getMicroWinner(nxM, nyM) > 0 || isBoardFull(nxM, nyM)) {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					if (isBoardFull(x, y)) {
						mMacroboard[x][y] = 0;
						continue;
					}
					int mWinner = getMicroWinner(x, y);
					if (mWinner != 0) {
						mMacroboard[x][y] = mWinner;
						continue;
					}
					mMacroboard[x][y] = -1;
				}
			}
		} else {
			mMacroboard[nxM][nyM] = -1;
		}
	}

	private Boolean isInActiveMicroboard(int x, int y) {
		return mMacroboard[(int) x / 3][(int) y / 3] == -1;
	}

	private ArrayList<ArrayList<Integer>> getLines(int[][] board) {
		ArrayList<ArrayList<Integer>> lines = new ArrayList<>();

		// Lines
		lines.add(new ArrayList<>(asList(board[0][0], board[0][1], board[0][2])));
		lines.add(new ArrayList<>(asList(board[1][0], board[1][1], board[1][2])));
		lines.add(new ArrayList<>(asList(board[2][0], board[2][1], board[2][2])));

		// Columns
		lines.add(new ArrayList<>(asList(board[0][0], board[1][0], board[2][0])));
		lines.add(new ArrayList<>(asList(board[0][1], board[1][1], board[2][1])));
		lines.add(new ArrayList<>(asList(board[0][2], board[1][2], board[2][2])));

		// Diagonals
		lines.add(new ArrayList<>(asList(board[0][0], board[1][1], board[2][2])));
		lines.add(new ArrayList<>(asList(board[0][2], board[1][1], board[2][0])));

		return lines;
	}

	public boolean isFull() {
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				if (mMacroboard[x][y] == -1)
					return false;
		return true;
	}

	public int getWinner() {
		int countX, countO;
		ArrayList<ArrayList<Integer>> lines = getLines(mMacroboard);

		for (ArrayList<Integer> line : lines) {
			countX = countO = 0;
			for (int i = 0; i < 3; i++)
				if (line.get(i) == 1)
					countO++;
				else if (line.get(i) == 2)
					countX++;

			if (countX == 3)
				return 2;
			else if (countO == 3)
				return 1;
		}

		return 0;
	}

	public String toString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (counter > 0) {
					r += ",";
				}
				r += mBoard[x][y];
				counter++;
			}
		}
		return r;
	}

	public String macroToString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (counter > 0) {
					r += ",";
				}
				r += mMacroboard[x][y];
				counter++;
			}
		}
		return r;
	}

	public String dumpBoard() {
		String[] x = toString().split(",");
		String ret = "";
		ret += "    0 " + "1 " + "2 | " + "3 " + "4 " + "5 | " + "6 " + "7 " + "8 \n";
		for (int i = 0; i < x.length / 9; i++) {
			if (i == 0 || i == 3 || i == 6) {
				ret += "  -------------------------\n";
			}
			for (int j = 0; j < x.length / 9; j++) {
				if (j == 0) {
					ret += i + " ";
				}

				if (j == 0 || j == 3 || j == 6) {
					ret += "| ";
				}

				if (x[j + i * 9].equals("0")) {
					ret += "- ";
				} else if (x[j + i * 9].equals("1")) {
					ret += "0 ";
				} else if (x[j + i * 9].equals("2")) {
					ret += "X ";
				}
				if (j == 8) {
					ret += "|";
				}
			}
			ret += "\n";
		}
		ret += ("  -------------------------\n");

		return ret;

	}

	public String dumpBoard2() {
		String[] x = toString().split(",");
		String ret = "";
		for (int i = 0; i < x.length / 9; i++) {
			if (i == 0 || i == 3 || i == 6)
				ret += "-------------------------\n";
			for (int j = 0; j < x.length / 9; j++) {
				if (j == 0 || j == 3 || j == 6)
					ret += "| ";
				if (x[j + i * 9].equals("0"))
					ret += "- ";
				else if (x[j + i * 9].equals("1"))
					ret += "O ";
				else if (x[j + i * 9].equals("2"))
					ret += "X ";
				if (j == 8)
					ret += "|";
			}
			ret += "\n";
		}
		ret += "-------------------------\n";
		return ret;
	}

	public String dumpMacro() {
		String r = "";
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				r += mMacroboard[x][y] + "  ";
			}
			r += "\n";
		}
		return r;
	}

	public String getLastError() {
		return mLastError;
	}

	public int getCols() {
		return mCols;
	}

	public int getRows() {
		return mRows;
	}
}
