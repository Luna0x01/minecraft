package net.minecraft.nbt;

abstract class AbstractNbtNumber extends NbtElement {
	protected AbstractNbtNumber() {
	}

	public abstract long longValue();

	public abstract int intValue();

	public abstract short shortValue();

	public abstract byte byteValue();

	public abstract double doubleValue();

	public abstract float floatValue();
}
