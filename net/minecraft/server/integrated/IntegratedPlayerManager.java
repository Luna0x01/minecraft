package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;

public class IntegratedPlayerManager extends PlayerManager {
	private NbtCompound userData;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		if (player.getTranslationKey().equals(this.getServer().getUserName())) {
			this.userData = new NbtCompound();
			player.writePlayerData(this.userData);
		}

		super.savePlayerData(player);
	}

	@Override
	public String checkCanJoin(SocketAddress address, GameProfile profile) {
		return profile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(profile.getName()) != null
			? "That name is already taken."
			: super.checkCanJoin(address, profile);
	}

	public IntegratedServer getServer() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public NbtCompound getUserData() {
		return this.userData;
	}
}
