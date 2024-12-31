package net.minecraft;

import net.minecraft.client.gui.screen.Screen;

public class class_4157 extends Screen {
	private final String field_20275;

	public class_4157(String string) {
		this.field_20275 = string;
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderDirtBackground(0);
		this.drawCenteredString(this.textRenderer, this.field_20275, this.width / 2, 70, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
