package com.blank038.servermarket.command;

import com.blank038.servermarket.ServerMarket;
import com.blank038.servermarket.config.I18n;
import com.blank038.servermarket.data.gui.MarketContainer;
import com.blank038.servermarket.data.gui.SaleItem;
import com.blank038.servermarket.data.gui.StoreContainer;
import com.blank038.servermarket.enums.PayType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MainCommand implements CommandExecutor {
	private final ServerMarket main;

	public MainCommand(ServerMarket serverMarket) {
		main = serverMarket;
	}

	/**
	 * 命令执行器
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (main.getConfig().getBoolean("short-command")) {
				// 打开全球市场
				openServerMarket(sender);
			} else {
				sendHelp(sender, label);
			}
		} else {
			switch (args[0]) {
			case "listings":
				openServerMarket(sender);
				break;
			case "create":
				create(sender, args);
				break;
			case "mail":
				if (sender instanceof Player) {
					new StoreContainer((Player) sender, 1).open(1);
				}
				break;
			case "reload":
				if (sender.hasPermission("servermarket.admin")) {
					main.loadConfig();
					sender.sendMessage(I18n.trans("reload", true));
				}
				break;
			default:
				sendHelp(sender, label);
				break;
			}
		}
		return true;
	}

	/**
	 * 打开全球市场
	 */
	private void openServerMarket(CommandSender sender) {
		if (sender instanceof Player) {
			new MarketContainer((Player) sender).openGui(1);
		}
	}

	/**
	 * 玩家出售物品
	 *
	 * @param sender 命令执行者
	 * @param args   命令参数
	 */
	public void create(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				sender.sendMessage(I18n.trans("price-null", true));
				return;
			}
			Player player = (Player) sender;
			ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
			if (itemStack == null || itemStack.getType() == Material.AIR) {
				sender.sendMessage(I18n.trans("hand-air", true));
				return;
			}
			
			boolean has = false;
			if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
				for (String l : itemStack.getItemMeta().getLore()) {
					if (main.getConfig().getStringList("black-list.lore").contains(l.replace("§", "&"))) {
						has = true;
						break;
					}
				}
			}
			if (main.getConfig().getStringList("black-list.type").contains(itemStack.getType().name()) || has) {
				sender.sendMessage(I18n.trans("deny-item", true));
				return;
			}
			int price;
			try {
				price = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(I18n.trans("wrong-number", true));
				return;
			}
			int min = main.getConfig().getInt("price.min"), max = main.getConfig().getInt("price.max");
			if (price < min) {
				sender.sendMessage(
						I18n.trans("min-price", true).replace("%min%", String.valueOf(min)));
				return;
			}
			if (price > max) {
				sender.sendMessage(
						I18n.trans("max-price", true).replace("%max%", String.valueOf(max)));
				return;
			}
			

			//////////////////// MrXiaoM 增加 START ////////////////////
			// 将要出售的物品数量
			int amount = itemStack.getAmount();
			// 记录原数量，后面需要用来判断
			int itemAmount = amount;
			// 参数2为可选参数
			if(args.length == 3) {
				try {
					amount = Integer.parseInt(args[2]);
				} catch (Exception e) {
					sender.sendMessage(I18n.trans("wrong-number", true));
					return;
				}
			}
			if(amount > itemStack.getAmount()) {
				sender.sendMessage(I18n.trans("amount-too-big", true));
				return;
			}
			else if(amount < 1) {
				sender.sendMessage(I18n.trans("amount-too-small", true));
				return;
			}
			itemStack.setAmount(amount);
			// 如果出售手里所有物品
			if(amount == itemAmount) {
				// 设置玩家手中物品为空
				player.getInventory().setItemInMainHand(null);
			}
			else {
				ItemStack newItemStack = player.getInventory().getItemInMainHand();
				newItemStack.setAmount(itemAmount - amount);
				player.getInventory().setItemInMainHand(newItemStack);
			}
			//////////////////// MrXiaoM 增加 END ////////////////////
			
			// 上架物品
			String saleUUID = UUID.randomUUID().toString();
			SaleItem saleItem = new SaleItem(saleUUID, player.getUniqueId().toString(), player.getName(), itemStack,
					PayType.VAULT, price, System.currentTimeMillis());
			main.sales.put(saleUUID, saleItem);
			player.sendMessage(I18n.trans("sell", true));
			// 判断是否公告
			if (main.getConfig().getBoolean("sale-broadcast")) {
				String displayMmae = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()
						? itemStack.getItemMeta().getDisplayName()
						: itemStack.getType().name();
				player.sendMessage(I18n.trans("broadcast", true).replace("%item%", displayMmae)
						.replace("%amount%", String.valueOf(itemStack.getAmount()))
						.replace("%player%", player.getName()));
			}
		}
	}

	/**
	 * 发送命令帮助
	 */
	private void sendHelp(CommandSender sender, String label) {
		for (String text : I18n
				.getStringList("help." + (sender.hasPermission("servermarket.admin") ? "admin" : "default"))) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text).replace("%c", label));
		}
	}
}