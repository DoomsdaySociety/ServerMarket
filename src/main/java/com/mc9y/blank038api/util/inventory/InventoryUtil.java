package com.mc9y.blank038api.util.inventory;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryUtil implements Listener {
	public List<GuiModel> guiModels = new ArrayList<GuiModel>();
	String version;
	public InventoryUtil(String version) {
		this.version = version;
	}
	
	public void setListener(GuiModel guiModel, boolean listener) {
		if (listener) {
			if (!this.guiModels.contains(guiModel)) {
				this.guiModels.add(guiModel);
			}
		} else {
			this.guiModels.remove(guiModel);
		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof GuiModel) {
			((GuiModel) e.getInventory().getHolder()).run(e);
		}

	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof GuiModel) {
			GuiModel guiModel = (GuiModel) e.getInventory().getHolder();
			if (guiModel.closeRemove()) {
				this.guiModels.remove(guiModel);
			}

			guiModel.onGuiClose(e);
		}
	}
}
