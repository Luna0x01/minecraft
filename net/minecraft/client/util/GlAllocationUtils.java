package net.minecraft.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GlAllocationUtils {
	public static synchronized int genLists(int i) {
		int j = GL11.glGenLists(i);
		if (j == 0) {
			int k = GL11.glGetError();
			String string = "No error code reported";
			if (k != 0) {
				string = GLU.gluErrorString(k);
			}

			throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + k + "): " + string);
		} else {
			return j;
		}
	}

	public static synchronized void deleteLists(int i, int j) {
		GL11.glDeleteLists(i, j);
	}

	public static synchronized void deleteSingletonList(int i) {
		GL11.glDeleteLists(i, 1);
	}

	public static synchronized ByteBuffer allocateByteBuffer(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public static IntBuffer allocateIntBuffer(int size) {
		return allocateByteBuffer(size << 2).asIntBuffer();
	}

	public static FloatBuffer allocateFloatBuffer(int size) {
		return allocateByteBuffer(size << 2).asFloatBuffer();
	}
}
