package net.minecraft.client.gui.screen;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;

public class DownloadingTerrainScreen extends Screen {
	public DownloadingTerrainScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
