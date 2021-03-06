package com.github.erudo.advancedec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.github.erudo.advancedec.listener.OnChestClose;
import com.github.erudo.advancedec.listener.OnChestOpen;
import com.github.erudo.advancedec.listener.OnJoinLeave;
import com.github.erudo.advancedec.utils.Config;
import com.github.erudo.advancedec.utils.OutputYaml;

public class Main extends JavaPlugin {

	public static final String CHESTNAME = "AdvancedEnderChest";

	//Config関連
	private Config config;
	private OutputYaml outputConfig;

	//インベントリ登録済みプレイヤー
	public static List<UUID> players = new ArrayList<UUID>();
	//データを紐づけするMAP
	public static HashMap<UUID, String> inventories = new HashMap<UUID, String>();



	@Override
	public void onDisable() {
		getLogger().info("プラグインが停止しました");


		for (UUID uuid : players) {
			if (inventories.containsKey(uuid)) {
				//アウトプット用データ
				outputConfig.setContent(uuid, inventories.get(uuid));
			}
		}

		////////////////////////
		///		Config 		///
		///////////////////////
		outputConfig.save();

	}

	@Override
	public void onEnable() {
		getLogger().info("プラグインが読み込まれました");

		////////////////////////
		///		Listener 	///
		///////////////////////
		new OnChestOpen(this);
		new OnChestClose(this);
		new OnJoinLeave(this);

		////////////////////////
		///		Config 		///
		///////////////////////
		config = new Config(this);

		outputConfig = new OutputYaml(this);
	}

	//セーブ関連
	private String InventorytoBase64(Inventory inventory) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataoutput = new BukkitObjectOutputStream(outputStream);

			dataoutput.writeInt(inventory.getSize());

			for (int i = 0; i < inventory.getSize(); i++) {
				dataoutput.writeObject(inventory.getItem(i));
			}

			dataoutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Could not convert inventory to base64.", e);
		}
	}

	private Inventory InventoryfromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream datainput = new BukkitObjectInputStream(inputStream);

			CraftInventoryCustom inventory = new CraftInventoryCustom(null, datainput.readInt(), CHESTNAME);

			for (int i = 0; i < inventory.getSize(); i++) {
				inventory.setItem(i, (ItemStack) datainput.readObject());
			}

			datainput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException("Could not decode inventory.", e);
		}

	}

	public void saveInventory(Player p, Inventory inventory) {
		String data = this.InventorytoBase64(inventory);

		if (Main.inventories.containsKey(p.getUniqueId())) {
			Main.inventories.replace(p.getUniqueId(), data);
//			System.out.println("replace");
		} else {
			Main.inventories.put(p.getUniqueId(), data);
			if (!players.contains(p.getUniqueId())) {
				players.add(p.getUniqueId());
			}
//			System.out.println("put");
		}
	}

	public Inventory getSavedInventory(Player p) throws IOException {
//		System.out.println(inventories);
		if (Main.inventories.containsKey(p.getUniqueId())) {
			return this.InventoryfromBase64(inventories.get(p.getUniqueId()));
		} else {
			return Bukkit.getServer().createInventory(null, 9 * this.getChestRow(), CHESTNAME);
		}
	}

	public int getChestRow() {
		return config.getChestRow();
	}

	public String getContent(UUID uuid) {
		return outputConfig.getContent(uuid.toString());
	}

}
