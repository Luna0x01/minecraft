package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class IntegratedPlayerManager extends PlayerManager {
	private CompoundTag userData;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer, 8);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.getName().getString().equals(this.getServer().getUserName())) {
			this.userData = serverPlayerEntity.toTag(new CompoundTag());
		}

		super.savePlayerData(serverPlayerEntity);
	}

	@Override
	public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		return (Text)(gameProfile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(gameProfile.getName()) != null
			? new TranslatableText("multiplayer.disconnect.name_taken")
			: super.checkCanJoin(socketAddress, gameProfile));
	}

	public IntegratedServer getServer() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public CompoundTag getUserData() {
		return this.userData;
	}
}
