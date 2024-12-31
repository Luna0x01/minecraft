package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class OpenToLanScreen extends Screen {
	private final Screen parent;
	private ButtonWidget allowCommandsButton;
	private ButtonWidget gameModeButton;
	private String gameMode = "survival";
	private boolean allowCommands;

	public OpenToLanScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void init() {
		this.addButton(
			new ButtonWidget(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start")) {
				@Override
				public void method_18374(double d, double e) {
					OpenToLanScreen.this.client.setScreen(null);
					int i = NetworkUtils.getFreePort();
					Text text;
					if (OpenToLanScreen.this.client
						.getServer()
						.method_20311(GameMode.setGameModeWithString(OpenToLanScreen.this.gameMode), OpenToLanScreen.this.allowCommands, i)) {
						text = new TranslatableText("commands.publish.started", i);
					} else {
						text = new TranslatableText("commands.publish.failed");
					}

					OpenToLanScreen.this.client.inGameHud.getChatHud().addMessage(text);
				}
			}
		);
		this.addButton(new ButtonWidget(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				OpenToLanScreen.this.client.setScreen(OpenToLanScreen.this.parent);
			}
		});
		this.gameModeButton = this.addButton(new ButtonWidget(104, this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void method_18374(double d, double e) {
				if ("spectator".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "creative";
				} else if ("creative".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "adventure";
				} else if ("adventure".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "survival";
				} else {
					OpenToLanScreen.this.gameMode = "spectator";
				}

				OpenToLanScreen.this.updateButtonTexts();
			}
		});
		this.allowCommandsButton = this.addButton(new ButtonWidget(103, this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void method_18374(double d, double e) {
				OpenToLanScreen.this.allowCommands = !OpenToLanScreen.this.allowCommands;
				OpenToLanScreen.this.updateButtonTexts();
			}
		});
		this.updateButtonTexts();
	}

	private void updateButtonTexts() {
		this.gameModeButton.message = I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode);
		this.allowCommandsButton.message = I18n.translate("selectWorld.allowCommands") + " ";
		if (this.allowCommands) {
			this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.on");
		} else {
			this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.off");
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("lanServer.title"), this.width / 2, 50, 16777215);
		this.drawCenteredString(this.textRenderer, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
