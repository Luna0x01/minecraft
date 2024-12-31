package net.minecraft.client.gui.screen.multiplayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;

public class LanScanWidget extends MultiplayerServerListWidget.class_4169 {
	private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_18403() + j / 2 - this.client.textRenderer.fontHeight / 2;
		this.client
			.textRenderer
			.method_18355(
				I18n.translate("lanServer.scanning"),
				(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
				(float)m,
				16777215
			);
		String string;
		switch ((int)(Util.method_20227() / 300L % 4L)) {
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
			.method_18355(
				string,
				(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2),
				(float)(m + this.client.textRenderer.fontHeight),
				8421504
			);
	}
}
