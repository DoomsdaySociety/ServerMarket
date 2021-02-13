package com.blank038.servermarket.bridge;

import org.bukkit.OfflinePlayer;

public interface IBridge {

	double balance(OfflinePlayer player);

	void give(OfflinePlayer player, double amount);

	boolean take(OfflinePlayer player, double amount);
}
