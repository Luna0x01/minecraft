package net.minecraft.client.network;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelInfo;

public class PlayerListEntry {
	private final GameProfile profile;
	private LevelInfo.GameMode gameMode;
	private int latency;
	private boolean texturesLoaded = false;
	private Identifier getSkinTexture;
	private Identifier getElytraTexture;
	private String model;
	private Text displayName;
	private int field_10602 = 0;
	private int field_10603 = 0;
	private long field_10604 = 0L;
	private long field_10605 = 0L;
	private long field_10606 = 0L;

	public PlayerListEntry(GameProfile gameProfile) {
		this.profile = gameProfile;
	}

	public PlayerListEntry(PlayerListS2CPacket.Entry entry) {
		this.profile = entry.getProfile();
		this.gameMode = entry.getGameMode();
		this.latency = entry.getLatency();
		this.displayName = entry.getDisplayName();
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
	}

	public int getLatency() {
		return this.latency;
	}

	protected void setGameMode(LevelInfo.GameMode gameMode) {
		this.gameMode = gameMode;
	}

	protected void setLatency(int latency) {
		this.latency = latency;
	}

	public boolean hasSkinTexture() {
		return this.getSkinTexture != null;
	}

	public String getModel() {
		return this.model == null ? DefaultSkinHelper.getModel(this.profile.getId()) : this.model;
	}

	public Identifier getSkinTexture() {
		if (this.getSkinTexture == null) {
			this.loadTextures();
		}

		return (Identifier)Objects.firstNonNull(this.getSkinTexture, DefaultSkinHelper.getTexture(this.profile.getId()));
	}

	public Identifier getElytraTexture() {
		if (this.getElytraTexture == null) {
			this.loadTextures();
		}

		return this.getElytraTexture;
	}

	public Team getScoreboardTeam() {
		return MinecraftClient.getInstance().world.getScoreboard().getPlayerTeam(this.getProfile().getName());
	}

	protected void loadTextures() {
		synchronized (this) {
			if (!this.texturesLoaded) {
				this.texturesLoaded = true;
				MinecraftClient.getInstance().getSkinProvider().loadProfileSkin(this.profile, new PlayerSkinProvider.class_1890() {
					@Override
					public void method_7047(Type type, Identifier identifier, MinecraftProfileTexture minecraftProfileTexture) {
						switch (type) {
							case SKIN:
								PlayerListEntry.this.getSkinTexture = identifier;
								PlayerListEntry.this.model = minecraftProfileTexture.getMetadata("model");
								if (PlayerListEntry.this.model == null) {
									PlayerListEntry.this.model = "default";
								}
								break;
							case CAPE:
								PlayerListEntry.this.getElytraTexture = identifier;
						}
					}
				}, true);
			}
		}
	}

	public void setDisplayName(Text displayName) {
		this.displayName = displayName;
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public int getLastHealth() {
		return this.field_10602;
	}

	public void setLastHealth(int i) {
		this.field_10602 = i;
	}

	public int getHealth() {
		return this.field_10603;
	}

	public void setHealth(int i) {
		this.field_10603 = i;
	}

	public long getLastHealthTime() {
		return this.field_10604;
	}

	public void setLastHealthTime(long l) {
		this.field_10604 = l;
	}

	public long getBlinkingHeartTime() {
		return this.field_10605;
	}

	public void setBlinkingHeartTime(long l) {
		this.field_10605 = l;
	}

	public long getShowTime() {
		return this.field_10606;
	}

	public void setShowTime(long l) {
		this.field_10606 = l;
	}
}
