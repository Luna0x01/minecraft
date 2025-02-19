package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public abstract class PressableWidget extends ClickableWidget {
	public PressableWidget(int i, int j, int k, int l, Text text) {
		super(i, j, k, l, text);
	}

	public abstract void onPress();

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.onPress();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.active || !this.visible) {
			return false;
		} else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
			return false;
		} else {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			this.onPress();
			return true;
		}
	}
}
