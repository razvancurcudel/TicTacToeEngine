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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.io.IOPlayer;
import tictactoe.field.Field;
import tictactoe.player.Player;

public class TicTacToe {

	private final int		TIMEBANK_MAX	= 2000;
	private final int		TIME_PER_MOVE	= 500;
	private final int		FIELD_COLUMNS	= 9;
	private final int		FIELD_ROWS		= 9;
	private List<Player>	players;
	private Field			mField;
	private int				mBotId			= 1;

	public Engine			engine;
	public Processor		processor;

	public int				maxRounds		= 81;

	public boolean			DEV_MODE		= false;	// turn this on for local testing
	public String			TEST_BOT;					// command for the test bot in DEV_MODE
	public int				NUM_TEST_BOTS;				// number of bots for this game

	public void setupEngine(String args[]) throws IOException, RuntimeException {

		this.engine = new Engine();

		if (DEV_MODE) {
			if (TEST_BOT.isEmpty()) {
				throw new RuntimeException(
						"DEV_MODE: Please provide a command to start the test bot by setting 'TEST_BOT' in your main class.");
			}
			if (NUM_TEST_BOTS <= 0) {
				throw new RuntimeException(
						"DEV_MODE: Please provide the number of bots in this game by setting 'NUM_TEST_BOTS' in your main class.");
			}

			for (int i = 0; i < NUM_TEST_BOTS; i++) {
				this.engine.addPlayer(TEST_BOT, "ID_" + i);
			}

			return;
		}

		List<String> botDirs = new ArrayList<String>();
		List<String> botIds = new ArrayList<String>();

		if (args.length <= 0) {
			throw new RuntimeException("No arguments provided.");
		}

		for (int i = 0; i < args.length; i++) {
			botIds.add(i + "");
			botDirs.add(args[i]);
		}

		if (botIds.isEmpty() || botDirs.isEmpty() || botIds.size() != botDirs.size())
			throw new RuntimeException("Missing some arguments.");

		for (int i = 0; i < botIds.size(); i++) {
			this.engine.addPlayer(botDirs.get(i), botIds.get(i));
		}
	}

	public boolean isGameOver() {
		if (this.processor.isGameOver()
				|| (this.maxRounds >= 0 && this.processor.getRoundNumber() > this.maxRounds)) {

			return true;
		}
		return false;
	}

	public void playRound(int roundNumber) {
		// for (IOPlayer ioPlayer : this.engine.getPlayers())
		// ioPlayer.addToDump(String.format("Round %d", roundNumber));
		IOPlayer.addToDump(String.format("Round %d", roundNumber));

		this.processor.playRound(roundNumber);
	}

	/**
	 * close the bot processes, save, exit program
	 */
	public void finish() throws Exception {
		for (IOPlayer ioPlayer : this.engine.getPlayers())
			ioPlayer.finish();
		Thread.sleep(100);

		if (DEV_MODE) { // print the game file when in DEV_MODE
			String playedGame = this.processor.getPlayedGame();
			System.out.println(playedGame);
		} else { // save game
			try {
				this.saveGame();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		System.err.println("Done.");

		System.exit(0);
	}

	public void saveGame() {

		// save results to file here
		String playedGame = this.processor.getPlayedGame();
		System.out.print(playedGame);
	}

	public void setupGame(ArrayList<IOPlayer> ioPlayers) throws Exception {
		this.players = new ArrayList<Player>();

		this.mField = new Field(FIELD_COLUMNS, FIELD_ROWS);

		for (int i = 0; i < ioPlayers.size(); i++) {
			String playerName = String.format("player%d", i + 1);
			Player player = new Player(playerName, ioPlayers.get(i), TIMEBANK_MAX, TIME_PER_MOVE,
					i + 1);
			this.players.add(player);

		}

		for (Player player : this.players) {
			sendSettings(player);
		}

		processor = new Processor(this.players, this.mField);
	}

	public void sendSettings(Player player) {
		player.sendSetting("timebank", TIMEBANK_MAX);
		player.sendSetting("time_per_move", TIME_PER_MOVE);
		player.sendSetting("player_names",
				this.players.get(0).getName() + "," + this.players.get(1).getName());
		player.sendSetting("your_bot", player.getName());
		player.sendSetting("your_botid", mBotId);
		mBotId++;
	}

	protected void runEngine() throws Exception {
		engine.setLogic(this);
		engine.start();
	}

	public static void main(String args[]) throws Exception {
		TicTacToe game = new TicTacToe();

		// DEV_MODE settings
		game.TEST_BOT = "java -cp \"E:\\java projects\\TTT\\bin\" bot.BotStarter";
		game.NUM_TEST_BOTS = 2;
		game.DEV_MODE = false;

		game.setupEngine(args);
		game.runEngine();
	}
}
