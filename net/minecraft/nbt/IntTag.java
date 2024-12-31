package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class IntTag extends AbstractNumberTag {
	public static final TagReader<IntTag> READER = new TagReader<IntTag>() {
		public IntTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(96L);
			return IntTag.of(dataInput.readInt());
		}

		@Override
		public String getCrashReportName() {
			return "INT";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Int";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final int value;

	private IntTag(int i) {
		this.value = i;
	}

	public static IntTag of(int i) {
		return i >= -128 && i <= 1024 ? IntTag.Cache.VALUES[i + 128] : new IntTag(i);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.value);
	}

	@Override
	public byte getType() {
		return 3;
	}

	@Override
	public TagReader<IntTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	public IntTag copy() {
		return this;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof IntTag && this.value == ((IntTag)object).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
		return new LiteralText(String.valueOf(this.value)).formatted(GOLD);
	}

	@Override
	public long getLong() {
		return (long)this.value;
	}

	@Override
	public int getInt() {
		return this.value;
	}

	@Override
	public short getShort() {
		return (short)(this.value & 65535);
	}

	@Override
	public byte getByte() {
		return (byte)(this.value & 0xFF);
	}

	@Override
	public double getDouble() {
		return (double)this.value;
	}

	@Override
	public float getFloat() {
		return (float)this.value;
	}

	@Override
	public Number getNumber() {
		return this.value;
	}

	static class Cache {
		static final IntTag[] VALUES = new IntTag[1153];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new IntTag(-128 + i);
			}
		}
	}
}
