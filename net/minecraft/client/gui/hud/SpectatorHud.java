package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
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
		this.lastInteractionTime = MinecraftClient.getTime();
		if (this.spectatorMenu != null) {
			this.spectatorMenu.useCommand(slot);
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

	private float getSpectatorMenuHeight() {
		long l = this.lastInteractionTime - MinecraftClient.getTime() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void render(Window window, float tickDelta) {
		if (this.spectatorMenu != null) {
			float f = this.getSpectatorMenuHeight();
			if (f <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = window.getWidth() / 2;
				float g = this.zOffset;
				this.zOffset = -90.0F;
				float h = (float)window.getHeight() - 22.0F * f;
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.renderSpectatorMenu(window, f, i, h, spectatorMenuState);
				this.zOffset = g;
			}
		}
	}

	protected void renderSpectatorMenu(Window window, float height, int x, float y, SpectatorMenuState state) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, height);
		this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
		this.drawTexture((float)(x - 91), y, 0, 0, 182, 22);
		if (state.getSelectedSlot() >= 0) {
			this.drawTexture((float)(x - 91 - 1 + state.getSelectedSlot() * 20), y - 1.0F, 0, 22, 24, 22);
		}

		DiffuseLighting.enable();

		for (int i = 0; i < 9; i++) {
			this.renderSpectatorCommand(i, window.getWidth() / 2 - 90 + i * 20 + 2, y + 3.0F, height, state.getCommand(i));
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
			String string = String.valueOf(GameOptions.getFormattedNameForKeyCode(this.client.options.hotbarKeys[slot].getCode()));
			if (i > 3 && command.isEnabled()) {
				this.client
					.textRenderer
					.drawWithShadow(string, (float)(x + 19 - 2 - this.client.textRenderer.getStringWidth(string)), y + 6.0F + 3.0F, 16777215 + (i << 24));
			}
		}
	}

	public void render(Window window) {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			String string = spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND
				? spectatorMenuCommand.getName().asFormattedString()
				: this.spectatorMenu.getCurrentGroup().getPrompt().asFormattedString();
			if (string != null) {
				int j = (window.getWidth() - this.client.textRenderer.getStringWidth(string)) / 2;
				int k = window.getHeight() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
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

	public void cycleSlot(int offset) {
		int i = this.spectatorMenu.getSelectedSlot() + offset;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.getCommand(i) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(i).isEnabled())) {
			i += offset;
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.useCommand(i);
			this.lastInteractionTime = MinecraftClient.getTime();
		}
	}

	public void useSelectedCommand() {
		this.lastInteractionTime = MinecraftClient.getTime();
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
