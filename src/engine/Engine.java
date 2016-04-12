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

package engine;

import java.io.IOException;
import java.util.ArrayList;

import engine.io.IOPlayer;
import tictactoe.TicTacToe;

/**
 * Engine class
 * 
 * DO NOT EDIT THIS FILE
 * 
 * A general engine to implement IO for bot classes All game logic is handled by implemented Logic
 * interfaces.
 * 
 * @author Jackie Xu <jackie@starapple.nl>, Jim van Eeden <jim@starapple.nl>
 */
public class Engine {

	private boolean				isRunning;
	private TicTacToe			logic;
	private ArrayList<IOPlayer>	players;

	public Engine() {
		this.isRunning = false;
		this.players = new ArrayList<IOPlayer>();
	}

	public void addPlayer(String command, String idString) throws IOException {

		Process process = Runtime.getRuntime().exec(command);
		IOPlayer player = new IOPlayer(process, idString);
		this.players.add(player);
		player.run();
	}

	public void setLogic(TicTacToe ttt) {
		this.logic = ttt;
	}

	public boolean hasEnded() {
		return this.logic.isGameOver();
	}

	public ArrayList<IOPlayer> getPlayers() {
		return this.players;
	}

	public void start() throws Exception {
		int round = 0;
		this.isRunning = true;

		this.logic.setupGame(this.players);
		while (this.isRunning) {
			round++;
			this.logic.playRound(round);

			if (this.hasEnded()) {

				// System.out.println("stopping...");
				this.isRunning = false;

				try {
					this.logic.finish();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}