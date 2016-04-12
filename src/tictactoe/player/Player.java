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

package tictactoe.player;

import java.io.IOException;

import engine.io.IOPlayer;

public class Player {

	private String	name;
	public IOPlayer	bot;
	private long	timeBank;
	private long	maxTimeBank;
	private long	timePerMove;
	int				mId;
	public long		thinkingTime	= 0;

	public Player(String name, IOPlayer bot, long maxTimeBank, long timePerMove, int id) {
		this.name = name;
		this.bot = bot;
		this.timeBank = maxTimeBank;
		this.maxTimeBank = maxTimeBank;
		this.timePerMove = timePerMove;
		mId = id;
	}

	public void updateTimeBank(long timeElapsed) {
		this.timeBank = Math.max(this.timeBank - timeElapsed, 0);
		this.timeBank = Math.min(this.timeBank + this.timePerMove, this.maxTimeBank);
	}

	public void sendSetting(String type, String value) {
		sendLine(String.format("settings %s %s", type, value));
	}

	public void sendSetting(String type, int value) {
		sendLine(String.format("settings %s %d", type, value));
	}

	public void sendUpdate(String type, Player player, String value) {
		sendLine(String.format("update %s %s %s", player.getName(), type, value));
	}

	public void sendUpdate(String type, Player player, int value) {
		sendLine(String.format("update %s %s %d", player.getName(), type, value));
	}

	public void sendUpdate(String type, String value) {
		sendLine(String.format("update game %s %s", type, value));
	}

	public void sendUpdate(String type, int value) {
		sendLine(String.format("update game %s %d", type, value));
	}

	public String requestMove(String moveType) {
		long startTime = System.currentTimeMillis();

		// write the request to the bot
		sendLine(String.format("action %s %d", moveType, this.timeBank));
		// wait for the bot to return his response
		String response = this.bot.getResponse(this.timeBank);

		// update the timebank
		long timeElapsed = System.currentTimeMillis() - startTime;
		thinkingTime = timeElapsed;
		updateTimeBank(timeElapsed);

		return response;
	}

	private void sendLine(String content) {
		try {
			this.bot.writeToBot(content);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public long getTimeBank() {
		return timeBank;
	}

	public IOPlayer getBot() {
		return bot;
	}

	public void setTimeBank(long time) {
		this.timeBank = time;
	}

	public int getId() {
		return mId;
	}
}
