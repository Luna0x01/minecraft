package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;

public class DeathScreen extends Screen implements IdentifiableBooleanConsumer {
	private int ticksSinceDeath;
	private boolean isHardcore = false;

	@Override
	public void init() {
		this.buttons.clear();
		if (this.client.world.getLevelProperties().isHardcore()) {
			if (this.client.isIntegratedServerRunning()) {
				this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.deleteWorld")));
			} else {
				this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.leaveServer")));
			}
		} else {
			this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 72, I18n.translate("deathScreen.respawn")));
			this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.titleScreen")));
			if (this.client.getSession() == null) {
				((ButtonWidget)this.buttons.get(1)).active = false;
			}
		}

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.active = false;
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				this.client.player.requestRespawn();
				this.client.setScreen(null);
				break;
			case 1:
				if (this.client.world.getLevelProperties().isHardcore()) {
					this.client.setScreen(new TitleScreen());
				} else {
					ConfirmScreen confirmScreen = new ConfirmScreen(
						this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
					);
					this.client.setScreen(confirmScreen);
					confirmScreen.disableButtons(20);
				}
		}
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		if (confirmed) {
			this.client.world.disconnect();
			this.client.connect(null);
			this.client.setScreen(new TitleScreen());
		} else {
			this.client.player.requestRespawn();
			this.client.setScreen(null);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		boolean bl = this.client.world.getLevelProperties().isHardcore();
		String string = bl ? I18n.translate("deathScreen.title.hardcore") : I18n.translate("deathScreen.title");
		this.drawCenteredString(this.textRenderer, string, this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (bl) {
			this.drawCenteredString(this.textRenderer, I18n.translate("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
		}

		this.drawCenteredString(
			this.textRenderer, I18n.translate("deathScreen.score") + ": " + Formatting.YELLOW + this.client.player.getScore(), this.width / 2, 100, 16777215
		);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.ticksSinceDeath++;
		if (this.ticksSinceDeath == 20) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
	}
}
