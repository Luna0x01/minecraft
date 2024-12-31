package net.minecraft.realms;

public interface RealmsAbstractButtonProxy<T extends AbstractRealmsButton<?>> {
	T getButton();

	boolean active();

	void active(boolean bl);

	boolean isVisible();

	void setVisible(boolean bl);
}
