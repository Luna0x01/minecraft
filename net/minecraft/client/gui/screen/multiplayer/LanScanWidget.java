package net.minecraft.client.gui.screen.multiplayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;

public class LanScanWidget implements EntryListWidget.Entry {
	private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
		int i = y + rowHeight / 2 - this.client.textRenderer.fontHeight / 2;
		this.client
			.textRenderer
			.draw(
				I18n.translate("lanServer.scanning"),
				this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2,
				i,
				16777215
			);
		String string;
		switch ((int)(MinecraftClient.getTime() / 300L % 4L)) {
			case 0:
			default:
				string = "O o o";
				break;
			case 1:
			case 3:
				string = "o O o";
				break;
			case 2:
				string = "o o O";
		}

		this.client
			.textRenderer
			.draw(string, this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2, i + this.client.textRenderer.fontHeight, 8421504);
	}

	@Override
	public void updatePosition(int index, int x, int y) {
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		return false;
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}
}
