package net.minecraft.text;

public interface Nameable {
	String getTranslationKey();

	boolean hasCustomName();

	Text getName();
}
