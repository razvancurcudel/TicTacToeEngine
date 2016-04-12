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

package tictactoe.moves;

import tictactoe.field.Field;
import tictactoe.player.Player;

public class Move {

	protected Player	player;			// player that did this move
	private String		illegalMove;	// gets the value of the error message if move is
										// illegal, else remains empty
	private int			x	= 0;
	private int			y	= 0;
	private String		mBoard;
	private String		mMacroboard;
	private long		thinkingTime;

	public Move(Player player, Field field, int x, int y) {
		this.player = player;
		this.illegalMove = "";
		this.mBoard = field.dumpBoard();
		this.mMacroboard = field.dumpMacro();
		this.x = x;
		this.y = y;
		this.thinkingTime = player.thinkingTime;
	}

	public boolean isIllegal() {
		if (illegalMove.isEmpty())
			return false;
		return true;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setIllegalMove(String illegalMove) {
		this.illegalMove = illegalMove;
	}

	public String getIllegalMove() {
		return illegalMove;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public String getmBoard() {
		return mBoard;
	}

	public String getmMacroboard() {
		return mMacroboard;
	}

	public long getThinkingTime() {
		return thinkingTime;
	}

	public void setThinkingTime(long thinkingTime) {
		this.thinkingTime = thinkingTime;
	}

	public String toString() {
		return mBoard;
	}
}
