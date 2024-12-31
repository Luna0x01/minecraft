package net.minecraft.client.gui;

public interface Selectable extends Narratable {
	Selectable.SelectionType getType();

	default boolean isNarratable() {
		return true;
	}

	public static enum SelectionType {
		NONE,
		HOVERED,
		FOCUSED;

		public boolean isFocused() {
			return this == FOCUSED;
		}
	}
}
