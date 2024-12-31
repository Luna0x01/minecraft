package net.minecraft.client.gui.screen;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class DisconnectedScreen extends Screen {
	private final String name;
	private final Text reason;
	private List<String> reasonFormatted;
	private final Screen parent;
	private int reasonHeight;

	public DisconnectedScreen(Screen screen, String string, Text text) {
		this.parent = screen;
		this.name = I18n.translate(string);
		this.reason = text;
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	protected void init() {
		this.reasonFormatted = this.textRenderer.wrapLines(this.reason.asFormattedString(), this.width - 50);
		this.reasonHeight = this.reasonFormatted.size() * this.textRenderer.fontHeight;
		this.addButton(
			new ButtonWidget(
				0, this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + this.textRenderer.fontHeight, this.height - 30), I18n.translate("gui.toMenu")
			) {
				@Override
				public void method_18374(double d, double e) {
					DisconnectedScreen.this.client.setScreen(DisconnectedScreen.this.parent);
				}
			}
		);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.name, this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.textRenderer.fontHeight * 2, 11184810);
		int i = this.height / 2 - this.reasonHeight / 2;
		if (this.reasonFormatted != null) {
			for (String string : this.reasonFormatted) {
				this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
				i += this.textRenderer.fontHeight;
			}
		}

		super.render(mouseX, mouseY, tickDelta);
	}
}
