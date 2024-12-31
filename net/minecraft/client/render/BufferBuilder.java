package net.minecraft.client.render;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder {
	private static final Logger field_13461 = LogManager.getLogger();
	private ByteBuffer buffer;
	private IntBuffer intBuffer;
	private ShortBuffer shortBuffer;
	private FloatBuffer floatBuffer;
	private int vertexCount;
	private VertexFormatElement currentElement;
	private int currentElementId;
	private boolean textured;
	private int drawMode;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private VertexFormat format;
	private boolean building;

	public BufferBuilder(int i) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(i * 4);
		this.intBuffer = this.buffer.asIntBuffer();
		this.shortBuffer = this.buffer.asShortBuffer();
		this.floatBuffer = this.buffer.asFloatBuffer();
	}

	private void grow(int size) {
		int i = (this.vertexCount + 1) * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
		if (size > this.intBuffer.remaining() || i >= this.buffer.capacity()) {
			int j = this.buffer.capacity();
			int k = j % 2097152;
			int l = k + (((this.intBuffer.position() + size) * 4 - k) / 2097152 + 1) * 2097152;
			field_13461.debug("Needed to grow BufferBuilder buffer: Old size " + j + " bytes, new size " + l + " bytes.");
			int m = this.intBuffer.position();
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(l);
			this.buffer.position(0);
			byteBuffer.put(this.buffer);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
			this.floatBuffer = this.buffer.asFloatBuffer().asReadOnlyBuffer();
			this.intBuffer = this.buffer.asIntBuffer();
			this.intBuffer.position(m);
			this.shortBuffer = this.buffer.asShortBuffer();
			this.shortBuffer.position(m << 1);
		}
	}

	public void sortQuads(float cameraX, float cameraY, float cameraZ) {
		int i = this.vertexCount / 4;
		final float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = getDistanceSq(
				this.floatBuffer,
				(float)((double)cameraX + this.offsetX),
				(float)((double)cameraY + this.offsetY),
				(float)((double)cameraZ + this.offsetZ),
				this.format.getVertexSizeInteger(),
				j * this.format.getVertexSize()
			);
		}

		Integer[] integers = new Integer[i];

		for (int k = 0; k < integers.length; k++) {
			integers[k] = k;
		}

		Arrays.sort(integers, new Comparator<Integer>() {
			public int compare(Integer integer, Integer integer2) {
				return Floats.compare(fs[integer2], fs[integer]);
			}
		});
		BitSet bitSet = new BitSet();
		int l = this.format.getVertexSize();
		int[] is = new int[l];

		for (int m = 0; (m = bitSet.nextClearBit(m)) < integers.length; m++) {
			int n = integers[m];
			if (n != m) {
				this.intBuffer.limit(n * l + l);
				this.intBuffer.position(n * l);
				this.intBuffer.get(is);
				int o = n;

				for (int p = integers[n]; o != m; p = integers[p]) {
					this.intBuffer.limit(p * l + l);
					this.intBuffer.position(p * l);
					IntBuffer intBuffer = this.intBuffer.slice();
					this.intBuffer.limit(o * l + l);
					this.intBuffer.position(o * l);
					this.intBuffer.put(intBuffer);
					bitSet.set(o);
					o = p;
				}

				this.intBuffer.limit(m * l + l);
				this.intBuffer.position(m * l);
				this.intBuffer.put(is);
			}

			bitSet.set(m);
		}
	}

	public BufferBuilder.DrawArrayParameters method_9727() {
		this.intBuffer.rewind();
		int i = this.method_9757();
		this.intBuffer.limit(i);
		int[] is = new int[i];
		this.intBuffer.get(is);
		this.intBuffer.limit(this.intBuffer.capacity());
		this.intBuffer.position(i);
		return new BufferBuilder.DrawArrayParameters(is, new VertexFormat(this.format));
	}

	private int method_9757() {
		return this.vertexCount * this.format.getVertexSizeInteger();
	}

	private static float getDistanceSq(FloatBuffer buffer, float x, float y, float z, int i, int j) {
		float f = buffer.get(j + i * 0 + 0);
		float g = buffer.get(j + i * 0 + 1);
		float h = buffer.get(j + i * 0 + 2);
		float k = buffer.get(j + i * 1 + 0);
		float l = buffer.get(j + i * 1 + 1);
		float m = buffer.get(j + i * 1 + 2);
		float n = buffer.get(j + i * 2 + 0);
		float o = buffer.get(j + i * 2 + 1);
		float p = buffer.get(j + i * 2 + 2);
		float q = buffer.get(j + i * 3 + 0);
		float r = buffer.get(j + i * 3 + 1);
		float s = buffer.get(j + i * 3 + 2);
		float t = (f + k + n + q) * 0.25F - x;
		float u = (g + l + o + r) * 0.25F - y;
		float v = (h + m + p + s) * 0.25F - z;
		return t * t + u * u + v * v;
	}

	public void restoreState(BufferBuilder.DrawArrayParameters drawArrayParameters) {
		this.intBuffer.clear();
		this.grow(drawArrayParameters.method_9760().length);
		this.intBuffer.put(drawArrayParameters.method_9760());
		this.vertexCount = drawArrayParameters.method_9761();
		this.format = new VertexFormat(drawArrayParameters.getFormat());
	}

	public void reset() {
		this.vertexCount = 0;
		this.currentElement = null;
		this.currentElementId = 0;
	}

	public void begin(int drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.reset();
			this.drawMode = drawMode;
			this.format = format;
			this.currentElement = format.get(this.currentElementId);
			this.textured = false;
			this.buffer.limit(this.buffer.capacity());
		}
	}

	public BufferBuilder texture(double u, double v) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)u);
				this.buffer.putFloat(i + 4, (float)v);
				break;
			case UNSIGNED_INT:
			case INT:
				this.buffer.putInt(i, (int)u);
				this.buffer.putInt(i + 4, (int)v);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)v));
				this.buffer.putShort(i + 2, (short)((int)u));
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)v));
				this.buffer.put(i + 1, (byte)((int)u));
		}

		this.nextElement();
		return this;
	}

	public BufferBuilder texture2(int u, int v) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)u);
				this.buffer.putFloat(i + 4, (float)v);
				break;
			case UNSIGNED_INT:
			case INT:
				this.buffer.putInt(i, u);
				this.buffer.putInt(i + 4, v);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.buffer.putShort(i, (short)v);
				this.buffer.putShort(i + 2, (short)u);
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.buffer.put(i, (byte)v);
				this.buffer.put(i + 1, (byte)u);
		}

		this.nextElement();
		return this;
	}

	public void faceTexture2(int i, int j, int k, int l) {
		int m = (this.vertexCount - 4) * this.format.getVertexSizeInteger() + this.format.getUvIndex(1) / 4;
		int n = this.format.getVertexSize() >> 2;
		this.intBuffer.put(m, i);
		this.intBuffer.put(m + n, j);
		this.intBuffer.put(m + n * 2, k);
		this.intBuffer.put(m + n * 3, l);
	}

	public void postProcessFacePosition(double d, double e, double f) {
		int i = this.format.getVertexSizeInteger();
		int j = (this.vertexCount - 4) * i;

		for (int k = 0; k < 4; k++) {
			int l = j + k * i;
			int m = l + 1;
			int n = m + 1;
			this.intBuffer.put(l, Float.floatToRawIntBits((float)(d + this.offsetX) + Float.intBitsToFloat(this.intBuffer.get(l))));
			this.intBuffer.put(m, Float.floatToRawIntBits((float)(e + this.offsetY) + Float.intBitsToFloat(this.intBuffer.get(m))));
			this.intBuffer.put(n, Float.floatToRawIntBits((float)(f + this.offsetZ) + Float.intBitsToFloat(this.intBuffer.get(n))));
		}
	}

	private int method_1308(int i) {
		return ((this.vertexCount - i) * this.format.getVertexSize() + this.format.getColorIndex()) / 4;
	}

	public void faceTint(float f, float g, float h, int i) {
		int j = this.method_1308(i);
		int k = -1;
		if (!this.textured) {
			k = this.intBuffer.get(j);
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				int l = (int)((float)(k & 0xFF) * f);
				int m = (int)((float)(k >> 8 & 0xFF) * g);
				int n = (int)((float)(k >> 16 & 0xFF) * h);
				k &= -16777216;
				k |= n << 16 | m << 8 | l;
			} else {
				int o = (int)((float)(k >> 24 & 0xFF) * f);
				int p = (int)((float)(k >> 16 & 0xFF) * g);
				int q = (int)((float)(k >> 8 & 0xFF) * h);
				k &= 255;
				k |= o << 24 | p << 16 | q << 8;
			}
		}

		this.intBuffer.put(j, k);
	}

	private void putColor(int color, int i) {
		int j = this.method_1308(i);
		int k = color >> 16 & 0xFF;
		int l = color >> 8 & 0xFF;
		int m = color & 0xFF;
		int n = color >> 24 & 0xFF;
		this.putColor(j, k, l, m, n);
	}

	public void putColor(float red, float green, float blue, int i) {
		int j = this.method_1308(i);
		int k = MathHelper.clamp((int)(red * 255.0F), 0, 255);
		int l = MathHelper.clamp((int)(green * 255.0F), 0, 255);
		int m = MathHelper.clamp((int)(blue * 255.0F), 0, 255);
		this.putColor(j, k, l, m, 255);
	}

	private void putColor(int index, int red, int green, int blue, int alpha) {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			this.intBuffer.put(index, alpha << 24 | blue << 16 | green << 8 | red);
		} else {
			this.intBuffer.put(index, red << 24 | green << 16 | blue << 8 | alpha);
		}
	}

	public void enableTexture() {
		this.textured = true;
	}

	public BufferBuilder color(float red, float green, float blue, float alpha) {
		return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
	}

	public BufferBuilder color(int red, int green, int blue, int alpha) {
		if (this.textured) {
			return this;
		} else {
			int i = this.vertexCount * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
			switch (this.currentElement.getFormat()) {
				case FLOAT:
					this.buffer.putFloat(i, (float)red / 255.0F);
					this.buffer.putFloat(i + 4, (float)green / 255.0F);
					this.buffer.putFloat(i + 8, (float)blue / 255.0F);
					this.buffer.putFloat(i + 12, (float)alpha / 255.0F);
					break;
				case UNSIGNED_INT:
				case INT:
					this.buffer.putFloat(i, (float)red);
					this.buffer.putFloat(i + 4, (float)green);
					this.buffer.putFloat(i + 8, (float)blue);
					this.buffer.putFloat(i + 12, (float)alpha);
					break;
				case UNSIGNED_SHORT:
				case SHORT:
					this.buffer.putShort(i, (short)red);
					this.buffer.putShort(i + 2, (short)green);
					this.buffer.putShort(i + 4, (short)blue);
					this.buffer.putShort(i + 6, (short)alpha);
					break;
				case UNSIGNED_BYTE:
				case BYTE:
					if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
						this.buffer.put(i, (byte)red);
						this.buffer.put(i + 1, (byte)green);
						this.buffer.put(i + 2, (byte)blue);
						this.buffer.put(i + 3, (byte)alpha);
					} else {
						this.buffer.put(i, (byte)alpha);
						this.buffer.put(i + 1, (byte)blue);
						this.buffer.put(i + 2, (byte)green);
						this.buffer.put(i + 3, (byte)red);
					}
			}

			this.nextElement();
			return this;
		}
	}

	public void putArray(int[] data) {
		this.grow(data.length);
		this.intBuffer.position(this.method_9757());
		this.intBuffer.put(data);
		this.vertexCount = this.vertexCount + data.length / this.format.getVertexSizeInteger();
	}

	public void next() {
		this.vertexCount++;
		this.grow(this.format.getVertexSizeInteger());
	}

	public BufferBuilder vertex(double x, double y, double z) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)(x + this.offsetX));
				this.buffer.putFloat(i + 4, (float)(y + this.offsetY));
				this.buffer.putFloat(i + 8, (float)(z + this.offsetZ));
				break;
			case UNSIGNED_INT:
			case INT:
				this.buffer.putInt(i, Float.floatToRawIntBits((float)(x + this.offsetX)));
				this.buffer.putInt(i + 4, Float.floatToRawIntBits((float)(y + this.offsetY)));
				this.buffer.putInt(i + 8, Float.floatToRawIntBits((float)(z + this.offsetZ)));
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)(x + this.offsetX)));
				this.buffer.putShort(i + 2, (short)((int)(y + this.offsetY)));
				this.buffer.putShort(i + 4, (short)((int)(z + this.offsetZ)));
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)(x + this.offsetX)));
				this.buffer.put(i + 1, (byte)((int)(y + this.offsetY)));
				this.buffer.put(i + 2, (byte)((int)(z + this.offsetZ)));
		}

		this.nextElement();
		return this;
	}

	public void putNormal(float x, float y, float z) {
		int i = (byte)((int)(x * 127.0F)) & 255;
		int j = (byte)((int)(y * 127.0F)) & 255;
		int k = (byte)((int)(z * 127.0F)) & 255;
		int l = i | j << 8 | k << 16;
		int m = this.format.getVertexSize() >> 2;
		int n = (this.vertexCount - 4) * m + this.format.getNormalIndex() / 4;
		this.intBuffer.put(n, l);
		this.intBuffer.put(n + m, l);
		this.intBuffer.put(n + m * 2, l);
		this.intBuffer.put(n + m * 3, l);
	}

	private void nextElement() {
		this.currentElementId++;
		this.currentElementId = this.currentElementId % this.format.getSize();
		this.currentElement = this.format.get(this.currentElementId);
		if (this.currentElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}
	}

	public BufferBuilder normal(float x, float y, float z) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getIndex(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, x);
				this.buffer.putFloat(i + 4, y);
				this.buffer.putFloat(i + 8, z);
				break;
			case UNSIGNED_INT:
			case INT:
				this.buffer.putInt(i, (int)x);
				this.buffer.putInt(i + 4, (int)y);
				this.buffer.putInt(i + 8, (int)z);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)x * 32767 & 65535));
				this.buffer.putShort(i + 2, (short)((int)y * 32767 & 65535));
				this.buffer.putShort(i + 4, (short)((int)z * 32767 & 65535));
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)x * 127 & 0xFF));
				this.buffer.put(i + 1, (byte)((int)y * 127 & 0xFF));
				this.buffer.put(i + 2, (byte)((int)z * 127 & 0xFF));
		}

		this.nextElement();
		return this;
	}

	public void offset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}

	public void end() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		} else {
			this.building = false;
			this.buffer.position(0);
			this.buffer.limit(this.method_9757() * 4);
		}
	}

	public ByteBuffer getByteBuffer() {
		return this.buffer;
	}

	public VertexFormat getFormat() {
		return this.format;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public int getDrawMode() {
		return this.drawMode;
	}

	public void putQuadColor(int color) {
		for (int i = 0; i < 4; i++) {
			this.putColor(color, i + 1);
		}
	}

	public void putQuadColor(float red, float green, float blue) {
		for (int i = 0; i < 4; i++) {
			this.putColor(red, green, blue, i + 1);
		}
	}

	public class DrawArrayParameters {
		private final int[] field_10658;
		private final VertexFormat format;

		public DrawArrayParameters(int[] is, VertexFormat vertexFormat) {
			this.field_10658 = is;
			this.format = vertexFormat;
		}

		public int[] method_9760() {
			return this.field_10658;
		}

		public int method_9761() {
			return this.field_10658.length / this.format.getVertexSizeInteger();
		}

		public VertexFormat getFormat() {
			return this.format;
		}
	}
}
