package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface NbtType<T extends NbtElement> {
	T read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException;

	default boolean isImmutable() {
		return false;
	}

	String getCrashReportName();

	String getCommandFeedbackName();

	static NbtType<NbtNull> createInvalid(int type) {
		return new NbtType<NbtNull>() {
			public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
				throw new IllegalArgumentException("Invalid tag id: " + type);
			}

			@Override
			public String getCrashReportName() {
				return "INVALID[" + type + "]";
			}

			@Override
			public String getCommandFeedbackName() {
				return "UNKNOWN_" + type;
			}
		};
	}
}
