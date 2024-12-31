package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TeleportToSpecificPlayerSpectatorCommand implements SpectatorMenuCommand {
	private final GameProfile gameProfile;
	private final Identifier skinId;

	public TeleportToSpecificPlayerSpectatorCommand(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
		this.skinId = AbstractClientPlayerEntity.getSkinId(gameProfile.getName());
		AbstractClientPlayerEntity.loadSkin(this.skinId, gameProfile.getName());
	}

	@Override
	public void use(SpectatorMenu menu) {
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(this.gameProfile.getId()));
	}

	@Override
	public Text getName() {
		return new LiteralText(this.gameProfile.getName());
	}

	@Override
	public void renderIcon(float brightness, int alpha) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
		GlStateManager.color(1.0F, 1.0F, 1.0F, (float)alpha / 255.0F);
		DrawableHelper.drawTexture(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		DrawableHelper.drawTexture(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
