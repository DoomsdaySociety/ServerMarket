package com.blank038.servermarket.nms.sub;

import com.blank038.servermarket.nms.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class v1_16_R3 implements NBTBase {

	@Override
	public String get(ItemStack itemStack, String key) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return null;
		}
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack.clone());
		NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
		return nbtTagCompound != null && nbtTagCompound.hasKey(key) ? nbtTagCompound.getString(key) : null;
	}

	@Override
	public boolean contains(ItemStack itemStack, String key) {
		return get(itemStack, key) != null;
	}

	@Override
	public ItemStack addTag(ItemStack itemStack, String key, String value) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack.clone());
		NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
		if (nbtTagCompound != null) {
			nbtTagCompound.setString(key, value);
		}
		nmsItem.setTag(nbtTagCompound);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
}
