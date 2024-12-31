package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerManager extends PlayerManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public DedicatedPlayerManager(MinecraftDedicatedServer minecraftDedicatedServer) {
		super(minecraftDedicatedServer);
		this.setViewDistance(minecraftDedicatedServer.getIntOrDefault("view-distance", 10));
		this.maxPlayers = minecraftDedicatedServer.getIntOrDefault("max-players", 20);
		this.setWhitelistEnabled(minecraftDedicatedServer.getBooleanOrDefault("white-list", false));
		if (!minecraftDedicatedServer.isSinglePlayer()) {
			this.getUserBanList().setEnabled(true);
			this.getIpBanList().setEnabled(true);
		}

		this.loadUserBanList();
		this.saveUserBanList();
		this.loadIpBanList();
		this.saveIpBanList();
		this.loadOpList();
		this.loadWhitelist();
		this.saveOpList();
		if (!this.getWhitelist().getFile().exists()) {
			this.saveWhitelist();
		}
	}

	@Override
	public void setWhitelistEnabled(boolean whitelistEnabled) {
		super.setWhitelistEnabled(whitelistEnabled);
		this.getServer().setProperty("white-list", whitelistEnabled);
		this.getServer().saveAbstractPropertiesHandler();
	}

	@Override
	public void op(GameProfile profile) {
		super.op(profile);
		this.saveOpList();
	}

	@Override
	public void deop(GameProfile profile) {
		super.deop(profile);
		this.saveOpList();
	}

	@Override
	public void reloadWhitelist() {
		this.loadWhitelist();
	}

	private void saveIpBanList() {
		try {
			this.getIpBanList().save();
		} catch (IOException var2) {
			LOGGER.warn("Failed to save ip banlist: ", var2);
		}
	}

	private void saveUserBanList() {
		try {
			this.getUserBanList().save();
		} catch (IOException var2) {
			LOGGER.warn("Failed to save user banlist: ", var2);
		}
	}

	private void loadIpBanList() {
		try {
			this.getIpBanList().load();
		} catch (IOException var2) {
			LOGGER.warn("Failed to load ip banlist: ", var2);
		}
	}

	private void loadUserBanList() {
		try {
			this.getUserBanList().load();
		} catch (IOException var2) {
			LOGGER.warn("Failed to load user banlist: ", var2);
		}
	}

	private void loadOpList() {
		try {
			this.getOpList().load();
		} catch (Exception var2) {
			LOGGER.warn("Failed to load operators list: ", var2);
		}
	}

	private void saveOpList() {
		try {
			this.getOpList().save();
		} catch (Exception var2) {
			LOGGER.warn("Failed to save operators list: ", var2);
		}
	}

	private void loadWhitelist() {
		try {
			this.getWhitelist().load();
		} catch (Exception var2) {
			LOGGER.warn("Failed to load white-list: ", var2);
		}
	}

	private void saveWhitelist() {
		try {
			this.getWhitelist().save();
		} catch (Exception var2) {
			LOGGER.warn("Failed to save white-list: ", var2);
		}
	}

	@Override
	public boolean isWhitelisted(GameProfile profile) {
		return !this.isWhitelistEnabled() || this.isOperator(profile) || this.getWhitelist().isAllowed(profile);
	}

	public MinecraftDedicatedServer getServer() {
		return (MinecraftDedicatedServer)super.getServer();
	}

	@Override
	public boolean canBypassPlayerLimit(GameProfile profile) {
		return this.getOpList().isOp(profile);
	}
}
