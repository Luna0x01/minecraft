package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;

public class ConfirmChatLinkScreen extends ConfirmScreen {
	private final String warning;
	private final String copy;
	private final String link;
	private boolean drawWarning = true;

	public ConfirmChatLinkScreen(IdentifiableBooleanConsumer identifiableBooleanConsumer, String string, int i, boolean bl) {
		super(identifiableBooleanConsumer, I18n.translate(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), string, i);
		this.yesText = I18n.translate(bl ? "chat.link.open" : "gui.yes");
		this.noText = I18n.translate(bl ? "gui.cancel" : "gui.no");
		this.copy = I18n.translate("chat.copy");
		this.warning = I18n.translate("chat.link.warning");
		this.link = string;
	}

	@Override
	public void init() {
		super.init();
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesText));
		this.buttons.add(new ButtonWidget(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy));
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noText));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 2) {
			this.copyToClipboard();
		}

		this.consumer.confirmResult(button.id == 0, this.identifier);
	}

	public void copyToClipboard() {
		setClipboard(this.link);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
		if (this.drawWarning) {
			this.drawCenteredString(this.textRenderer, this.warning, this.width / 2, 110, 16764108);
		}
	}

	public void disableWarning() {
		this.drawWarning = false;
	}
}
