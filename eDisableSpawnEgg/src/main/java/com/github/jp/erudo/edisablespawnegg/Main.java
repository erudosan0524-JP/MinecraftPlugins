package com.github.jp.erudo.edisablespawnegg;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

 	@Override
	public void onDisable() {
		getLogger().info("プラグインが停止しました");
	}

	@Override
	public void onEnable() {
		getLogger().info("プラグインが起動しました");
		new EventListener(this);
	}

}
