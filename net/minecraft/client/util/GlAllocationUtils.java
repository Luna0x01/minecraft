package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlAllocationUtils {
	public static synchronized int genLists(int i) {
		int j = GlStateManager.method_12319(i);
		if (j == 0) {
			int k = GlStateManager.method_12271();
			String string = "No error code reported";
			if (k != 0) {
				string = GLX.method_19690(k);
			}

			throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + k + "): " + string);
		} else {
			return j;
		}
	}

	public static synchronized void deleteLists(int i, int j) {
		GlStateManager.method_12310(i, j);
	}

	public static synchronized void deleteSingletonList(int i) {
		deleteLists(i, 1);
	}

	public static synchronized ByteBuffer allocateByteBuffer(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer allocateFloatBuffer(int size) {
		return allocateByteBuffer(size << 2).asFloatBuffer();
	}
}
