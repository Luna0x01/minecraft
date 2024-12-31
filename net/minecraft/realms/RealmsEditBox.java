package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class RealmsEditBox {
	private final TextFieldWidget editBox;

	public RealmsEditBox(int i, int j, int k, int l, int m) {
		this.editBox = new TextFieldWidget(i, MinecraftClient.getInstance().textRenderer, j, k, l, m);
	}

	public String getValue() {
		return this.editBox.getText();
	}

	public void tick() {
		this.editBox.tick();
	}

	public void setFocus(boolean focused) {
		this.editBox.setFocused(focused);
	}

	public void setValue(String text) {
		this.editBox.setText(text);
	}

	public void keyPressed(char character, int code) {
		this.editBox.keyPressed(character, code);
	}

	public boolean isFocused() {
		return this.editBox.isFocused();
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		this.editBox.mouseClicked(mouseX, mouseY, button);
	}

	public void render() {
		this.editBox.render();
	}

	public void setMaxLength(int maximumLength) {
		this.editBox.setMaxLength(maximumLength);
	}

	public void setIsEditable(boolean editable) {
		this.editBox.setEditable(editable);
	}
}
