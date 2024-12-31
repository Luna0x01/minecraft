package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface PlayerDataHandler {
	void savePlayerData(PlayerEntity player);

	@Nullable
	NbtCompound getPlayerData(PlayerEntity player);

	String[] getSavedPlayerIds();
}
