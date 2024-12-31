package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class FatalErrorScreen extends Screen {
	private final String title;
	private final String message;

	public FatalErrorScreen(String string, String string2) {
		this.title = string;
		this.message = string2;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, 140, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				FatalErrorScreen.this.client.setScreen(null);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 90, 16777215);
		this.drawCenteredString(this.textRenderer, this.message, this.width / 2, 110, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean method_18607() {
		return false;
	}
}
