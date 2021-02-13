package com.mc9y.blank038api;

import org.bukkit.Bukkit;

import com.blank038.servermarket.ServerMarket;
import com.mc9y.blank038api.util.inventory.InventoryUtil;

public class Blank038ApiLite {
	private static Blank038ApiLite blank038API;
	private static InventoryUtil inventoryUtil;

	public Blank038ApiLite(ServerMarket plugin, String version) {
		inventoryUtil = new InventoryUtil(version);
		Bukkit.getPluginManager().registerEvents(inventoryUtil, plugin);
		blank038API = this;
	}
	
	public static InventoryUtil getInventoryUtil() {
		return inventoryUtil;
	}

	public static Blank038ApiLite getBlank038API() {
		return blank038API;
	}

}
