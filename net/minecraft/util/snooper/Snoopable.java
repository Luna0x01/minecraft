package net.minecraft.util.snooper;

public interface Snoopable {
	void addSnooperInfo(Snooper snooper);

	void addSnooper(Snooper snooper);

	boolean isSnooperEnabled();
}
