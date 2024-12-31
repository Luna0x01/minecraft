package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final Text message;

	public DeathScreen(@Nullable Text text) {
		this.message = text;
	}

	@Override
	public void init() {
		this.buttons.clear();
		this.ticksSinceDeath = 0;
		if (this.client.world.getLevelProperties().isHardcore()) {
			this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 72, I18n.translate("deathScreen.spectate")));
			this.buttons
				.add(
					new ButtonWidget(
						1, this.width / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen." + (this.client.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"))
					)
				);
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
			if (this.client.world != null) {
				this.client.world.disconnect();
			}

			this.client.connect(null);
			this.client.setScreen(new TitleScreen());
		} else {
			this.client.player.requestRespawn();
			this.client.setScreen(null);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		boolean bl = this.client.world.getLevelProperties().isHardcore();
		this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		this.drawCenteredString(this.textRenderer, I18n.translate(bl ? "deathScreen.title.hardcore" : "deathScreen.title"), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.message != null) {
			this.drawCenteredString(this.textRenderer, this.message.asFormattedString(), this.width / 2, 85, 16777215);
		}

		this.drawCenteredString(
			this.textRenderer, I18n.translate("deathScreen.score") + ": " + Formatting.YELLOW + this.client.player.getScore(), this.width / 2, 100, 16777215
		);
		if (this.message != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
			Text text = this.method_12181(mouseX);
			if (text != null && text.getStyle().getHoverEvent() != null) {
				this.renderTextHoverEffect(text, mouseX, mouseY);
			}
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Nullable
	public Text method_12181(int i) {
		if (this.message == null) {
			return null;
		} else {
			int j = this.client.textRenderer.getStringWidth(this.message.asFormattedString());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (Text text : this.message) {
					m += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(text.computeValue(), false));
					if (m > i) {
						return text;
					}
				}

				return null;
			} else {
				return null;
			}
		}
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
