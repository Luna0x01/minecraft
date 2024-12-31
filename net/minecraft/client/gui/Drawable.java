package net.minecraft.client.gui;

import net.minecraft.client.util.math.MatrixStack;

public interface Drawable {
	void render(MatrixStack matrices, int mouseX, int mouseY, float delta);
}
