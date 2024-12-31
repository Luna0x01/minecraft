package net.minecraft.client.gui.screen;

import net.minecraft.class_4157;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;

public class GameMenuScreen extends Screen {
	@Override
	protected void init() {
		int i = -16;
		int j = 98;
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.translate("menu.returnToMenu")) {
			@Override
			public void method_18374(double d, double e) {
				boolean bl = GameMenuScreen.this.client.isIntegratedServerRunning();
				boolean bl2 = GameMenuScreen.this.client.isConnectedToRealms();
				this.active = false;
				GameMenuScreen.this.client.world.disconnect();
				if (bl) {
					GameMenuScreen.this.client.method_18206(null, new class_4157(I18n.translate("menu.savingLevel")));
				} else {
					GameMenuScreen.this.client.connect(null);
				}

				if (bl) {
					GameMenuScreen.this.client.setScreen(new TitleScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new TitleScreen());
				} else {
					GameMenuScreen.this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
				}
			}
		});
		if (!this.client.isIntegratedServerRunning()) {
			buttonWidget.message = I18n.translate("menu.disconnect");
		}

		this.addButton(new ButtonWidget(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.translate("menu.returnToGame")) {
			@Override
			public void method_18374(double d, double e) {
				GameMenuScreen.this.client.setScreen(null);
				GameMenuScreen.this.client.field_19945.method_18253();
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.options")) {
			@Override
			public void method_18374(double d, double e) {
				GameMenuScreen.this.client.setScreen(new SettingsScreen(GameMenuScreen.this, GameMenuScreen.this.client.options));
			}
		});
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan")) {
			@Override
			public void method_18374(double d, double e) {
				GameMenuScreen.this.client.setScreen(new OpenToLanScreen(GameMenuScreen.this));
			}
		});
		buttonWidget2.active = this.client.isInSingleplayer() && !this.client.getServer().shouldBroadcastConsoleToIps();
		this.addButton(new ButtonWidget(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements")) {
			@Override
			public void method_18374(double d, double e) {
				GameMenuScreen.this.client.setScreen(new AdvancementsScreen(GameMenuScreen.this.client.player.networkHandler.method_14672()));
			}
		});
		this.addButton(new ButtonWidget(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats")) {
			@Override
			public void method_18374(double d, double e) {
				GameMenuScreen.this.client.setScreen(new StatsScreen(GameMenuScreen.this, GameMenuScreen.this.client.player.getStatHandler()));
			}
		});
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("menu.game"), this.width / 2, 40, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
