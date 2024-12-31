package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class SleepingChatScreen extends ChatScreen {
	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height - 40, I18n.translate("multiplayer.stopSleeping")) {
			@Override
			public void method_18374(double d, double e) {
				SleepingChatScreen.this.stopSleeping();
			}
		});
	}

	@Override
	public void method_18608() {
		this.stopSleeping();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.stopSleeping();
		} else if (i == 257 || i == 335) {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.client.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.client.inGameHud.getChatHud().resetScroll();
			return true;
		}

		return super.keyPressed(i, j, k);
	}

	private void stopSleeping() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
	}
}
