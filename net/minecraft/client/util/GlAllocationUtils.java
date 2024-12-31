package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlAllocationUtils {
	public static synchronized ByteBuffer allocateByteBuffer(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer allocateFloatBuffer(int size) {
		return allocateByteBuffer(size << 2).asFloatBuffer();
	}
}
