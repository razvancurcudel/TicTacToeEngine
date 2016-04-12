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

package tictactoe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tictactoe.field.Field;
import tictactoe.moves.Move;
import tictactoe.player.Player;

public class Processor {

	private int				mRoundNumber					= 1;
	private List<Player>	mPlayers;
	private List<Move>		mMoves;
	private Field			mField;
	private int				mGameOverByPlayerErrorPlayerId	= 0;

	public Processor(List<Player> players, Field field) {
		mPlayers = players;
		mField = field;
		mMoves = new ArrayList<Move>();

		/* Create first move with empty field */
		Move move = new Move(mPlayers.get(0), mField, -1, -1);
		// MoveResult moveResult = new MoveResult(mPlayers.get(0), mField, mPlayers.get(0).getId());
		mMoves.add(move);
		// mMoveResults.add(moveResult);
	}

	int moveNumber = 1;

	public void playRound(int roundNumber) {
		for (Player player : mPlayers) {
			player.sendUpdate("round", roundNumber);
			player.sendUpdate("move", moveNumber++);
			player.sendUpdate("field", mField.toString());
			player.sendUpdate("macroboard", mField.macroToString());
			if (getWinner() == null && !mField.isFull()) {
				String response = player.requestMove("move");
				if (parseResponse(response, player)) {
					mMoves.add(new Move(player, mField, mField.lastX, mField.lastY));
				} else {
					player.sendUpdate("field", mField.toString());
					player.sendUpdate("macroboard", mField.macroToString());
					response = player.requestMove("move");
					if (parseResponse(response, player)) {
						// do nothing
					} else {
						player.sendUpdate("field", mField.toString());
						player.sendUpdate("macroboard", mField.macroToString());
						response = player.requestMove("move");
						if (parseResponse(response, player)) {
							// do nothing
						} else {
							/* Too many errors, other player wins */
							mGameOverByPlayerErrorPlayerId = player.getId();
						}
					}
				}
				player.sendUpdate("field", mField.toString());
				player.sendUpdate("macroboard", mField.macroToString());
			}
		}
	}

	/**
	 * Parses player response and inserts disc in field
	 * 
	 * @param args
	 *            : command line arguments passed on running of application
	 * @return : true if valid move, otherwise false
	 */
	private Boolean parseResponse(String r, Player player) {
		String[] parts = r.split(" ");
		if (parts.length >= 2 && parts[0].equals("place_move")) {
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			if (mField.placeMove(x, y, player.getId()))
				return true;
		}
		mField.mLastError = "Unknown command";
		return false;
	}

	public int getRoundNumber() {
		return this.mRoundNumber;
	}

	public Player getWinner() {
		int winner = mField.getWinner();
		/*
		 * Game over due to too many player errors. Look up the other player, which became the
		 * winner
		 */
		if (mGameOverByPlayerErrorPlayerId > 0) {
			for (Player player : mPlayers) {
				if (player.getId() != mGameOverByPlayerErrorPlayerId) {
					return player;
				}
			}
		}
		if (winner != 0) {
			for (Player player : mPlayers) {
				if (player.getId() == winner) {
					return player;
				}
			}
		}
		return null;
	}

	public String getPlayedGame() {
		BufferedWriter bw = null;
		Player winner = getWinner();
		try {
			bw = new BufferedWriter(new FileWriter("moves.txt"));
			for (int i = 0; i < mMoves.size(); i++) {
				Move move = mMoves.get(i);
				System.err.println("    Move took " + mMoves.get(i).getThinkingTime() + " ms");
				bw.write(move.getPlayer().getName() + " " + move.getX() + " " + move.getY()
						+ " took " + move.getThinkingTime() + " ms");
				System.err.println(move); // print board
				bw.flush();
				bw.newLine();
			}
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.err.println(mMoves.get(mMoves.size() - 1).getmMacroboard());
		if (winner == null) {
			System.err.println("DRAW");
			return "0";
		}
		System.err.println(winner.getName() + " WON");
		return winner.getId() + "";
	}

	public List<Move> getMoves() {
		return mMoves;
	}

	public Field getField() {
		return mField;
	}

	public boolean isGameOver() {
		return (getWinner() != null || mField.isFull());
	}
}
