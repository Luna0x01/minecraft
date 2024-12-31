package net.minecraft.client.network;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class PlayerListEntry {
	private final GameProfile profile;
	Map<Type, Identifier> field_13409 = Maps.newEnumMap(Type.class);
	private GameMode gameMode;
	private int latency;
	private boolean texturesLoaded;
	private String model;
	private Text displayName;
	private int field_10602;
	private int field_10603;
	private long field_10604;
	private long field_10605;
	private long field_10606;

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

	public GameMode getGameMode() {
		return this.gameMode;
	}

	protected void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public int getLatency() {
		return this.latency;
	}

	protected void setLatency(int latency) {
		this.latency = latency;
	}

	public boolean hasSkinTexture() {
		return this.getSkinTexture() != null;
	}

	public String getModel() {
		return this.model == null ? DefaultSkinHelper.getModel(this.profile.getId()) : this.model;
	}

	public Identifier getSkinTexture() {
		this.loadTextures();
		return (Identifier)Objects.firstNonNull(this.field_13409.get(Type.SKIN), DefaultSkinHelper.getTexture(this.profile.getId()));
	}

	@Nullable
	public Identifier getElytraTexture() {
		this.loadTextures();
		return (Identifier)this.field_13409.get(Type.CAPE);
	}

	@Nullable
	public Identifier method_12240() {
		this.loadTextures();
		return (Identifier)this.field_13409.get(Type.ELYTRA);
	}

	@Nullable
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
								PlayerListEntry.this.field_13409.put(Type.SKIN, identifier);
								PlayerListEntry.this.model = minecraftProfileTexture.getMetadata("model");
								if (PlayerListEntry.this.model == null) {
									PlayerListEntry.this.model = "default";
								}
								break;
							case CAPE:
								PlayerListEntry.this.field_13409.put(Type.CAPE, identifier);
								break;
							case ELYTRA:
								PlayerListEntry.this.field_13409.put(Type.ELYTRA, identifier);
						}
					}
				}, true);
			}
		}
	}

	public void setDisplayName(@Nullable Text displayName) {
		this.displayName = displayName;
	}

	@Nullable
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
