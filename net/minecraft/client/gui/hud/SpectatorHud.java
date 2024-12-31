package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class SpectatorHud extends DrawableHelper implements SpectatorMenuCloseCallback {
	private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
	public static final Identifier SPECTATOR_TEXTURE = new Identifier("textures/gui/spectator_widgets.png");
	private final MinecraftClient client;
	private long lastInteractionTime;
	private SpectatorMenu spectatorMenu;

	public SpectatorHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void selectSlot(int slot) {
		this.lastInteractionTime = Util.method_20227();
		if (this.spectatorMenu != null) {
			this.spectatorMenu.useCommand(slot);
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

	private float getSpectatorMenuHeight() {
		long l = this.lastInteractionTime - Util.method_20227() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void method_9534(float f) {
		if (this.spectatorMenu != null) {
			float g = this.getSpectatorMenuHeight();
			if (g <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = this.client.field_19944.method_18321() / 2;
				float h = this.zOffset;
				this.zOffset = -90.0F;
				float j = (float)this.client.field_19944.method_18322() - 22.0F * g;
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.method_9535(g, i, j, spectatorMenuState);
				this.zOffset = h;
			}
		}
	}

	protected void method_9535(float f, int i, float g, SpectatorMenuState spectatorMenuState) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.color(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
		this.drawTexture((float)(i - 91), g, 0, 0, 182, 22);
		if (spectatorMenuState.getSelectedSlot() >= 0) {
			this.drawTexture((float)(i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20), g - 1.0F, 0, 22, 24, 22);
		}

		DiffuseLighting.enable();

		for (int j = 0; j < 9; j++) {
			this.renderSpectatorCommand(j, this.client.field_19944.method_18321() / 2 - 90 + j * 20 + 2, g + 3.0F, f, spectatorMenuState.getCommand(j));
		}

		DiffuseLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void renderSpectatorCommand(int slot, int x, float y, float alpha, SpectatorMenuCommand command) {
		this.client.getTextureManager().bindTexture(SPECTATOR_TEXTURE);
		if (command != SpectatorMenu.BLANK_COMMAND) {
			int i = (int)(alpha * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, y, 0.0F);
			float f = command.isEnabled() ? 1.0F : 0.25F;
			GlStateManager.color(f, f, f, alpha);
			command.renderIcon(f, i);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.client.options.hotbarKeys[slot].method_18174());
			if (i > 3 && command.isEnabled()) {
				this.client
					.textRenderer
					.drawWithShadow(string, (float)(x + 19 - 2 - this.client.textRenderer.getStringWidth(string)), y + 6.0F + 3.0F, 16777215 + (i << 24));
			}
		}
	}

	public void method_18429() {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			String string = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND
				? this.spectatorMenu.getCurrentGroup().getPrompt().asFormattedString()
				: spectatorMenuCommand.getName().asFormattedString();
			if (string != null) {
				int j = (this.client.field_19944.method_18321() - this.client.textRenderer.getStringWidth(string)) / 2;
				int k = this.client.field_19944.method_18322() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.method_12288(
					GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
				);
				this.client.textRenderer.drawWithShadow(string, (float)j, (float)k, 16777215 + (i << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void close(SpectatorMenu menu) {
		this.spectatorMenu = null;
		this.lastInteractionTime = 0L;
	}

	public boolean isOpen() {
		return this.spectatorMenu != null;
	}

	public void method_18430(double d) {
		int i = this.spectatorMenu.getSelectedSlot() + (int)d;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.getCommand(i) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(i).isEnabled())) {
			i = (int)((double)i + d);
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.useCommand(i);
			this.lastInteractionTime = Util.method_20227();
		}
	}

	public void useSelectedCommand() {
		this.lastInteractionTime = Util.method_20227();
		if (this.isOpen()) {
			int i = this.spectatorMenu.getSelectedSlot();
			if (i != -1) {
				this.spectatorMenu.useCommand(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}
}
