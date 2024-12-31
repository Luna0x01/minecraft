package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NbtElement {
	public static final String[] TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"};

	abstract void write(DataOutput output) throws IOException;

	abstract void read(DataInput input, int depth, PositionTracker tracker) throws IOException;

	public abstract String toString();

	public abstract byte getType();

	protected NbtElement() {
	}

	protected static NbtElement createFromType(byte id) {
		switch (id) {
			case 0:
				return new NbtEnd();
			case 1:
				return new NbtByte();
			case 2:
				return new NbtShort();
			case 3:
				return new NbtInt();
			case 4:
				return new NbtLong();
			case 5:
				return new NbtFloat();
			case 6:
				return new NbtDouble();
			case 7:
				return new NbtByteArray();
			case 8:
				return new NbtString();
			case 9:
				return new NbtList();
			case 10:
				return new NbtCompound();
			case 11:
				return new NbtIntArray();
			default:
				return null;
		}
	}

	public abstract NbtElement copy();

	public boolean isEmpty() {
		return false;
	}

	public boolean equals(Object other) {
		if (!(other instanceof NbtElement)) {
			return false;
		} else {
			NbtElement nbtElement = (NbtElement)other;
			return this.getType() == nbtElement.getType();
		}
	}

	public int hashCode() {
		return this.getType();
	}

	protected String asString() {
		return this.toString();
	}
}
