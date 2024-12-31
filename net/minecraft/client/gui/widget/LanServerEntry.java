package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.resource.language.I18n;

public class LanServerEntry implements EntryListWidget.Entry {
	private final MultiplayerScreen parent;
	protected final MinecraftClient client;
	protected final net.minecraft.client.network.ServerEntry field_7827;
	private long time;

	protected LanServerEntry(MultiplayerScreen multiplayerScreen, net.minecraft.client.network.ServerEntry serverEntry) {
		this.parent = multiplayerScreen;
		this.field_7827 = serverEntry;
		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
		this.client.textRenderer.draw(I18n.translate("lanServer.title"), x + 32 + 3, y + 1, 16777215);
		this.client.textRenderer.draw(this.field_7827.getName(), x + 32 + 3, y + 12, 8421504);
		if (this.client.options.hideServerAddress) {
			this.client.textRenderer.draw(I18n.translate("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, 3158064);
		} else {
			this.client.textRenderer.draw(this.field_7827.getAddress(), x + 32 + 3, y + 12 + 11, 3158064);
		}
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		this.parent.selectEntry(index);
		if (MinecraftClient.getTime() - this.time < 250L) {
			this.parent.connect();
		}

		this.time = MinecraftClient.getTime();
		return false;
	}

	@Override
	public void updatePosition(int index, int x, int y) {
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
	}

	public net.minecraft.client.network.ServerEntry method_6786() {
		return this.field_7827;
	}
}
