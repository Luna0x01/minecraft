package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractNbtList<T extends NbtElement> extends AbstractList<T> implements NbtElement {
	public abstract int size();

	public T get(int i) {
		return this.getElement(i);
	}

	public T set(int i, T nbtElement) {
		T nbtElement2 = this.get(i);
		this.setElement(i, nbtElement);
		return nbtElement2;
	}

	public abstract T getElement(int index);

	public abstract void setElement(int index, NbtElement nbt);

	public abstract void remove(int index);
}
