package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
	Logger LOGGER = LogManager.getLogger();

	VertexConsumer vertex(double d, double e, double f);

	VertexConsumer color(int i, int j, int k, int l);

	VertexConsumer texture(float f, float g);

	VertexConsumer overlay(int i, int j);

	VertexConsumer light(int i, int j);

	VertexConsumer normal(float f, float g, float h);

	void next();

	default void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
		this.vertex((double)f, (double)g, (double)h);
		this.color(i, j, k, l);
		this.texture(m, n);
		this.overlay(o);
		this.light(p);
		this.normal(q, r, s);
		this.next();
	}

	default VertexConsumer color(float f, float g, float h, float i) {
		return this.color((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
	}

	default VertexConsumer light(int i) {
		return this.light(i & 65535, i >> 16 & 65535);
	}

	default VertexConsumer overlay(int i) {
		return this.overlay(i & 65535, i >> 16 & 65535);
	}

	default void quad(MatrixStack.Entry entry, BakedQuad bakedQuad, float f, float g, float h, int i, int j) {
		this.quad(entry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, g, h, new int[]{i, i, i, i}, j, false);
	}

	default void quad(MatrixStack.Entry entry, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, int i, boolean bl) {
		int[] js = bakedQuad.getVertexData();
		Vec3i vec3i = bakedQuad.getFace().getVector();
		Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		Matrix4f matrix4f = entry.getModel();
		vector3f.transform(entry.getNormal());
		int j = 8;
		int k = js.length / 8;
		MemoryStack memoryStack = MemoryStack.stackPush();
		Throwable var17 = null;

		try {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int l = 0; l < k; l++) {
				intBuffer.clear();
				intBuffer.put(js, l * 8, 8);
				float m = byteBuffer.getFloat(0);
				float n = byteBuffer.getFloat(4);
				float o = byteBuffer.getFloat(8);
				float s;
				float t;
				float u;
				if (bl) {
					float p = (float)(byteBuffer.get(12) & 255) / 255.0F;
					float q = (float)(byteBuffer.get(13) & 255) / 255.0F;
					float r = (float)(byteBuffer.get(14) & 255) / 255.0F;
					s = p * fs[l] * f;
					t = q * fs[l] * g;
					u = r * fs[l] * h;
				} else {
					s = fs[l] * f;
					t = fs[l] * g;
					u = fs[l] * h;
				}

				int y = is[l];
				float z = byteBuffer.getFloat(16);
				float aa = byteBuffer.getFloat(20);
				Vector4f vector4f = new Vector4f(m, n, o, 1.0F);
				vector4f.transform(matrix4f);
				this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), s, t, u, 1.0F, z, aa, i, y, vector3f.getX(), vector3f.getY(), vector3f.getZ());
			}
		} catch (Throwable var38) {
			var17 = var38;
			throw var38;
		} finally {
			if (memoryStack != null) {
				if (var17 != null) {
					try {
						memoryStack.close();
					} catch (Throwable var37) {
						var17.addSuppressed(var37);
					}
				} else {
					memoryStack.close();
				}
			}
		}
	}

	default VertexConsumer vertex(Matrix4f matrix4f, float f, float g, float h) {
		Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
		vector4f.transform(matrix4f);
		return this.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
	}

	default VertexConsumer normal(Matrix3f matrix3f, float f, float g, float h) {
		Vector3f vector3f = new Vector3f(f, g, h);
		vector3f.transform(matrix3f);
		return this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
	}
}
