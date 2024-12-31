package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsBridge;

public class GameMenuScreen extends Screen {
	private int saveStep;
	private int ticks;

	@Override
	public void init() {
		this.saveStep = 0;
		this.buttons.clear();
		int i = -16;
		int j = 98;
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.translate("menu.returnToMenu")));
		if (!this.client.isIntegratedServerRunning()) {
			((ButtonWidget)this.buttons.get(0)).message = I18n.translate("menu.disconnect");
		}

		this.buttons.add(new ButtonWidget(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.translate("menu.returnToGame")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.options")));
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan")));
		buttonWidget.active = this.client.isInSingleplayer() && !this.client.getServer().isPublished();
		this.buttons.add(new ButtonWidget(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.advancements")));
		this.buttons.add(new ButtonWidget(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.translate("gui.stats")));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				this.client.setScreen(new SettingsScreen(this, this.client.options));
				break;
			case 1:
				boolean bl = this.client.isIntegratedServerRunning();
				boolean bl2 = this.client.isConnectedToRealms();
				button.active = false;
				this.client.world.disconnect();
				this.client.connect(null);
				if (bl) {
					this.client.setScreen(new TitleScreen());
				} else if (bl2) {
					RealmsBridge realmsBridge = new RealmsBridge();
					realmsBridge.switchToRealms(new TitleScreen());
				} else {
					this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
				}
			case 2:
			case 3:
			default:
				break;
			case 4:
				this.client.setScreen(null);
				this.client.closeScreen();
				break;
			case 5:
				this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.method_14672()));
				break;
			case 6:
				this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
				break;
			case 7:
				this.client.setScreen(new OpenToLanScreen(this));
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.ticks++;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("menu.game"), this.width / 2, 40, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
