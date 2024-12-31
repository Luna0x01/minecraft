package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class SleepingChatScreen extends ChatScreen {
	@Override
	public void init() {
		super.init();
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height - 40, I18n.translate("multiplayer.stopSleeping")));
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == 1) {
			this.stopSleeping();
		} else if (code != 28 && code != 156) {
			super.keyPressed(id, code);
		} else {
			String string = this.chatField.getText().trim();
			if (!string.isEmpty()) {
				this.client.player.sendChatMessage(string);
			}

			this.chatField.setText("");
			this.client.inGameHud.getChatHud().resetScroll();
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 1) {
			this.stopSleeping();
		} else {
			super.buttonClicked(button);
		}
	}

	private void stopSleeping() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
	}
}
