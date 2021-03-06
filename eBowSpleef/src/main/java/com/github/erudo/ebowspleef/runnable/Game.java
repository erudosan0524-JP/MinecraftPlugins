package com.github.erudo.ebowspleef.runnable;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;

import com.github.erudo.ebowspleef.Main;
import com.github.erudo.ebowspleef.enums.GameState;
import com.github.erudo.ebowspleef.enums.Teams;
import com.github.erudo.ebowspleef.utils.MessageManager;

public class Game extends BukkitRunnable {

	private final Main plg;
	private BukkitTask task;
	private int count;

	public Game(Main _plg, int _count) {
		this.plg = _plg;
		this.count = _count;
	}

	public void run() {

		if (plg.getGameState().equals(GameState.END)) {
			MessageManager.broadcastMessage("ゲーム終了！");
			count = 0;
			plg.getServer().getScheduler().cancelTask(task.getTaskId());
		}

		if (plg.getGameState().equals(GameState.GAMING)) {
			if (count > 0) {
				Score TimeScore = plg.getObj().getScore(ChatColor.GOLD + "残り時間: ");
				TimeScore.setScore(count);
				Score RedPoint = plg.getObj().getScore(ChatColor.RED + "赤ポイント");
				RedPoint.setScore(plg.getRedPoint());
				Score BluePoint = plg.getObj().getScore(ChatColor.BLUE + "青ポイント");
				BluePoint.setScore(plg.getBluePoint());

				if(plg.getTeam(Teams.BLUE).getEntries().size() == 0 || plg.getTeam(Teams.RED).getEntries().size() == 0) {
					MessageManager.messageAll("プレイヤーが十分に揃っていなかったためゲームが終了しました。");
					plg.setGameState(GameState.END);
				}

			} else {
				plg.setGameState(GameState.END);
			}
			count--;
		}
	}

	public void setTask(BukkitTask task) {
		this.task = task;
	}

}
