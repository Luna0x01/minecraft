package net.minecraft.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface PlayerDataHandler {
	void savePlayerData(PlayerEntity player);

	NbtCompound getPlayerData(PlayerEntity player);

	String[] getSavedPlayerIds();
}
