package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public class NbtNull implements NbtElement {
	private static final int field_33193 = 64;
	public static final NbtType<NbtNull> TYPE = new NbtType<NbtNull>() {
		public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
			nbtTagSizeTracker.add(64L);
			return NbtNull.INSTANCE;
		}

		@Override
		public String getCrashReportName() {
			return "END";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_End";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	public static final NbtNull INSTANCE = new NbtNull();

	private NbtNull() {
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public byte getType() {
		return 0;
	}

	@Override
	public NbtType<NbtNull> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public NbtNull copy() {
		return this;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitNull(this);
	}
}
