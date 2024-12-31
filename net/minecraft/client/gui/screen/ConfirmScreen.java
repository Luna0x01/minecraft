package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class ConfirmScreen extends Screen {
	protected IdentifiableBooleanConsumer consumer;
	protected String title;
	private final String subtitle;
	private final List<String> lines = Lists.newArrayList();
	protected String yesText;
	protected String noText;
	protected int identifier;
	private int buttonEnableTimer;

	public ConfirmScreen(IdentifiableBooleanConsumer identifiableBooleanConsumer, String string, String string2, int i) {
		this.consumer = identifiableBooleanConsumer;
		this.title = string;
		this.subtitle = string2;
		this.identifier = i;
		this.yesText = I18n.translate("gui.yes");
		this.noText = I18n.translate("gui.no");
	}

	public ConfirmScreen(IdentifiableBooleanConsumer identifiableBooleanConsumer, String string, String string2, String string3, String string4, int i) {
		this.consumer = identifiableBooleanConsumer;
		this.title = string;
		this.subtitle = string2;
		this.yesText = string3;
		this.noText = string4;
		this.identifier = i;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new OptionButtonWidget(0, this.width / 2 - 155, this.height / 6 + 96, this.yesText) {
			@Override
			public void method_18374(double d, double e) {
				ConfirmScreen.this.consumer.confirmResult(true, ConfirmScreen.this.identifier);
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.noText) {
			@Override
			public void method_18374(double d, double e) {
				ConfirmScreen.this.consumer.confirmResult(false, ConfirmScreen.this.identifier);
			}
		});
		this.lines.clear();
		this.lines.addAll(this.textRenderer.wrapLines(this.subtitle, this.width - 50));
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 70, 16777215);
		int i = 90;

		for (String string : this.lines) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	public void disableButtons(int duration) {
		this.buttonEnableTimer = duration;

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.active = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.buttonEnableTimer == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.consumer.confirmResult(false, this.identifier);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}
}
