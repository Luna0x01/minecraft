package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class IntegratedPlayerManager extends PlayerManager {
	private NbtCompound userData;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		if (player.method_15540().getString().equals(this.getServer().getUserName())) {
			this.userData = player.toNbt(new NbtCompound());
		}

		super.savePlayerData(player);
	}

	@Override
	public Text method_21386(SocketAddress socketAddress, GameProfile gameProfile) {
		return (Text)(gameProfile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(gameProfile.getName()) != null
			? new TranslatableText("multiplayer.disconnect.name_taken")
			: super.method_21386(socketAddress, gameProfile));
	}

	public IntegratedServer getServer() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public NbtCompound getUserData() {
		return this.userData;
	}
}
