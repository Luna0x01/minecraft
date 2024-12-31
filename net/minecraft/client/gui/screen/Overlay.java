package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

public abstract class Overlay extends DrawableHelper implements Drawable {
	public boolean pausesGame() {
		return true;
	}
}
