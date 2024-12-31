package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.class_4157;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final Text message;

	public DeathScreen(@Nullable Text text) {
		this.message = text;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		String string;
		String string2;
		if (this.client.world.method_3588().isHardcore()) {
			string = I18n.translate("deathScreen.spectate");
			string2 = I18n.translate("deathScreen." + (this.client.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"));
		} else {
			string = I18n.translate("deathScreen.respawn");
			string2 = I18n.translate("deathScreen.titleScreen");
		}

		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 72, string) {
			@Override
			public void method_18374(double d, double e) {
				DeathScreen.this.client.player.requestRespawn();
				DeathScreen.this.client.setScreen(null);
			}
		});
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 96, string2) {
				@Override
				public void method_18374(double d, double e) {
					if (DeathScreen.this.client.world.method_3588().isHardcore()) {
						DeathScreen.this.client.setScreen(new TitleScreen());
					} else {
						ConfirmScreen confirmScreen = new ConfirmScreen(
							DeathScreen.this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
						);
						DeathScreen.this.client.setScreen(confirmScreen);
						confirmScreen.disableButtons(20);
					}
				}
			}
		);
		if (!this.client.world.method_3588().isHardcore() && this.client.getSession() == null) {
			buttonWidget.active = false;
		}

		for (ButtonWidget buttonWidget2 : this.buttons) {
			buttonWidget2.active = false;
		}
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			super.confirmResult(bl, i);
		} else if (bl) {
			if (this.client.world != null) {
				this.client.world.disconnect();
			}

			this.client.method_18206(null, new class_4157(I18n.translate("menu.savingLevel")));
			this.client.setScreen(new TitleScreen());
		} else {
			this.client.player.requestRespawn();
			this.client.setScreen(null);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		boolean bl = this.client.world.method_3588().isHardcore();
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
	public boolean mouseClicked(double d, double e, int i) {
		if (this.message != null && e > 85.0 && e < (double)(85 + this.textRenderer.fontHeight)) {
			Text text = this.method_12181((int)d);
			if (text != null && text.getStyle().getClickEvent() != null && text.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleTextClick(text);
				return false;
			}
		}

		return super.mouseClicked(d, e, i);
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
