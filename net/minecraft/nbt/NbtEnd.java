package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtEnd implements NbtElement {
	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(64L);
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public byte getType() {
		return 0;
	}

	@Override
	public String toString() {
		return "END";
	}

	public NbtEnd copy() {
		return new NbtEnd();
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		return new LiteralText("");
	}

	public boolean equals(Object o) {
		return o instanceof NbtEnd;
	}

	public int hashCode() {
		return this.getType();
	}
}
