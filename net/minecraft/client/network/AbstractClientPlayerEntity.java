package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DownloadedSkinParser;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	private PlayerListEntry cachedScoreboardEntry;
	public float field_13449;
	public float field_13450;
	public float field_13451;

	public AbstractClientPlayerEntity(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}

	@Override
	public boolean isSpectator() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getGameProfile().getId());
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.CREATIVE;
	}

	public boolean canRenderCapeTexture() {
		return this.getPlayerListEntry() != null;
	}

	@Nullable
	protected PlayerListEntry getPlayerListEntry() {
		if (this.cachedScoreboardEntry == null) {
			this.cachedScoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
		}

		return this.cachedScoreboardEntry;
	}

	public boolean hasSkinTexture() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.hasSkinTexture();
	}

	public Identifier getCapeId() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry.getSkinTexture();
	}

	@Nullable
	public Identifier getSkinId() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? null : playerListEntry.getElytraTexture();
	}

	public boolean method_12263() {
		return this.getPlayerListEntry() != null;
	}

	@Nullable
	public Identifier method_12264() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? null : playerListEntry.method_12240();
	}

	public static PlayerSkinTexture loadSkin(Identifier id, String playerName) {
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		Texture texture = textureManager.getTexture(id);
		if (texture == null) {
			texture = new PlayerSkinTexture(
				null,
				String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", ChatUtil.stripTextFormat(playerName)),
				DefaultSkinHelper.getTexture(getOfflinePlayerUuid(playerName)),
				new DownloadedSkinParser()
			);
			textureManager.loadTexture(id, texture);
		}

		return (PlayerSkinTexture)texture;
	}

	public static Identifier getSkinId(String playerName) {
		return new Identifier("skins/" + ChatUtil.stripTextFormat(playerName));
	}

	public String getModel() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getModel(this.getUuid()) : playerListEntry.getModel();
	}

	public float getSpeed() {
		float f = 1.0F;
		if (this.abilities.flying) {
			f *= 1.1F;
		}

		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		f = (float)((double)f * ((entityAttributeInstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
		if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.method_13061() && this.method_13064() != null && this.method_13064().getItem() == Items.BOW) {
			int i = this.method_13066();
			float g = (float)i / 20.0F;
			if (g > 1.0F) {
				g = 1.0F;
			} else {
				g *= g;
			}

			f *= 1.0F - g * 0.15F;
		}

		return f;
	}
}
