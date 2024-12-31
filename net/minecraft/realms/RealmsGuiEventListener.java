package net.minecraft.realms;

import net.minecraft.class_4122;

public abstract class RealmsGuiEventListener {
	public boolean mouseClicked(double d, double e, int i) {
		return false;
	}

	public boolean mouseReleased(double d, double e, int i) {
		return false;
	}

	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return false;
	}

	public boolean mouseScrolled(double d) {
		return false;
	}

	public boolean keyPressed(int i, int j, int k) {
		return false;
	}

	public boolean keyReleased(int i, int j, int k) {
		return false;
	}

	public boolean charTyped(char c, int i) {
		return false;
	}

	public abstract class_4122 getProxy();
}
