package com.mc9y.blank038api.util.inventory;

import com.mc9y.blank038api.Blank038ApiLite;
import com.mc9y.blank038api.interfaces.GuiCloseInterface;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuiModel implements InventoryHolder {
	private final Inventory inventory;
	private ExecuteInterface executeInterface;
	private GuiCloseInterface guiCloseInterface;
	private boolean closeRemove;

	public GuiModel(String title, int size) {
		this.inventory = Bukkit.createInventory(this, size, title.replace("&", "\u00a7"));
		this.closeRemove = true;
	}

	public void setItem(HashMap<Integer, ItemStack> itemMap) {
		for (Integer key : itemMap.keySet()) {
			this.setItem(key, itemMap.get(key));
		}
	}

	public void openInventory(Player player) {
		//////////////////// MrXiaoM 添加 START ////////////////////
		// 据说打开Gui之前要先关掉再打开更安全？我听说的
		player.closeInventory();
		//////////////////// MrXiaoM 添加 END ////////////////////
		player.openInventory(this.getInventory());
	}

	public boolean closeRemove() {
		return this.closeRemove;
	}

	public void setCloseRemove(boolean remove) {
		this.closeRemove = remove;
	}

	public void setItem(Integer slot, ItemStack itemStack) {
		this.inventory.setItem(slot.intValue(), itemStack);
	}

	public void setListener(boolean listener) {
		Blank038ApiLite.getInventoryUtil().setListener(this, listener);
	}

	public String getTitle() {
		return getInvTitle(Blank038ApiLite.getInventoryUtil().version, this.inventory);
		// return this.inventory.getTitle();
	}

	public void execute(ExecuteInterface executeInterface) {
		this.executeInterface = executeInterface;
	}

	public void setCloseInterface(GuiCloseInterface guiCloseInterface) {
		this.guiCloseInterface = guiCloseInterface;
	}

	public void onGuiClose(InventoryCloseEvent event) {
		if (this.guiCloseInterface != null) {
			this.guiCloseInterface.execute(event);
		}
	}

	public void run(InventoryClickEvent e) {
		if (this.executeInterface != null) {
			this.executeInterface.execute(e);
		}
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void removeGuiModel() {
		Blank038ApiLite.getInventoryUtil().guiModels.remove(this);
	}

	public static String getInvTitle(String version, Inventory inv) {
		String result = "";
		try {
			Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftInventoryCrafting");
			Class<?> clazz1 = clazz.getDeclaredClasses()[0];
			Field titleField = clazz1.getDeclaredField("title");
			titleField.setAccessible(true);
			result = (String) titleField.get(inv);
		} catch (Throwable t) {
		}
		return result;
	}

}
