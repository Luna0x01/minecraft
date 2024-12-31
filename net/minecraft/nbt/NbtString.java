package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtString extends NbtElement {
	private String value;

	public NbtString() {
		this.value = "";
	}

	public NbtString(String string) {
		this.value = string;
		if (string == null) {
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(288L);
		this.value = input.readUTF();
		tracker.add((long)(16 * this.value.length()));
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public String toString() {
		return "\"" + this.value.replace("\"", "\\\"") + "\"";
	}

	public NbtString copy() {
		return new NbtString(this.value);
	}

	@Override
	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else {
			NbtString nbtString = (NbtString)object;
			return this.value == null && nbtString.value == null || this.value != null && this.value.equals(nbtString.value);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}
}
