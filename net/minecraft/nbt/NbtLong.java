package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public class NbtLong extends AbstractNbtNumber {
	private static final int field_33201 = 128;
	public static final NbtType<NbtLong> TYPE = new NbtType<NbtLong>() {
		public NbtLong read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(128L);
			return NbtLong.of(dataInput.readLong());
		}

		@Override
		public String getCrashReportName() {
			return "LONG";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Long";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final long value;

	NbtLong(long l) {
		this.value = l;
	}

	public static NbtLong of(long value) {
		return value >= -128L && value <= 1024L ? NbtLong.Cache.VALUES[(int)value - -128] : new NbtLong(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public NbtType<NbtLong> getNbtType() {
		return TYPE;
	}

	public NbtLong copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtLong && this.value == ((NbtLong)o).value;
	}

	public int hashCode() {
		return (int)(this.value ^ this.value >>> 32);
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitLong(this);
	}

	@Override
	public long longValue() {
		return this.value;
	}

	@Override
	public int intValue() {
		return (int)(this.value & -1L);
	}

	@Override
	public short shortValue() {
		return (short)((int)(this.value & 65535L));
	}

	@Override
	public byte byteValue() {
		return (byte)((int)(this.value & 255L));
	}

	@Override
	public double doubleValue() {
		return (double)this.value;
	}

	@Override
	public float floatValue() {
		return (float)this.value;
	}

	@Override
	public Number numberValue() {
		return this.value;
	}

	static class Cache {
		private static final int field_33202 = 1024;
		private static final int field_33203 = -128;
		static final NbtLong[] VALUES = new NbtLong[1153];

		private Cache() {
		}

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtLong((long)(-128 + i));
			}
		}
	}
}
