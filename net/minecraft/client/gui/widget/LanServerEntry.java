package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;

public class LanServerEntry extends MultiplayerServerListWidget.class_4169 {
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
	public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_18404();
		int n = this.method_18403();
		this.client.textRenderer.method_18355(I18n.translate("lanServer.title"), (float)(m + 32 + 3), (float)(n + 1), 16777215);
		this.client.textRenderer.method_18355(this.field_7827.getName(), (float)(m + 32 + 3), (float)(n + 12), 8421504);
		if (this.client.options.hideServerAddress) {
			this.client.textRenderer.method_18355(I18n.translate("selectServer.hiddenAddress"), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		} else {
			this.client.textRenderer.method_18355(this.field_7827.getAddress(), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.parent.selectEntry(this.method_18402());
		if (Util.method_20227() - this.time < 250L) {
			this.parent.connect();
		}

		this.time = Util.method_20227();
		return false;
	}

	public net.minecraft.client.network.ServerEntry method_6786() {
		return this.field_7827;
	}
}
