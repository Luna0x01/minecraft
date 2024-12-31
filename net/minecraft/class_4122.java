package net.minecraft;

public interface class_4122 {
	default boolean mouseClicked(double d, double e, int i) {
		return false;
	}

	default boolean mouseReleased(double d, double e, int i) {
		return false;
	}

	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return false;
	}

	default boolean mouseScrolled(double d) {
		return false;
	}

	default boolean keyPressed(int i, int j, int k) {
		return false;
	}

	default boolean keyReleased(int i, int j, int k) {
		return false;
	}

	default boolean charTyped(char c, int i) {
		return false;
	}

	default void method_18428(boolean bl) {
	}

	default boolean method_18427() {
		return false;
	}
}
