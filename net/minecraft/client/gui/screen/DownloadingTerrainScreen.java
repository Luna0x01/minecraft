package net.minecraft.client.gui.screen;

import net.minecraft.client.resource.language.I18n;

public class DownloadingTerrainScreen extends Screen {
	@Override
	public void init() {
		this.buttons.clear();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}
}
