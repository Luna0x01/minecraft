package net.minecraft.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseInput {
	public int x;
	public int y;

	public void lockMouse() {
		Mouse.setGrabbed(true);
		this.x = 0;
		this.y = 0;
	}

	public void grabMouse() {
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(false);
	}

	public void updateMouse() {
		this.x = Mouse.getDX();
		this.y = Mouse.getDY();
	}
}
