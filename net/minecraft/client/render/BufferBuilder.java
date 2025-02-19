package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilder extends FixedColorVertexConsumer implements BufferVertexConsumer {
	private static final int MAX_BUFFER_SIZE = 2097152;
	private static final Logger LOGGER = LogManager.getLogger();
	private ByteBuffer buffer;
	private final List<BufferBuilder.DrawArrayParameters> parameters = Lists.newArrayList();
	private int lastParameterIndex;
	private int buildStart;
	private int elementOffset;
	private int nextDrawStart;
	private int vertexCount;
	@Nullable
	private VertexFormatElement currentElement;
	private int currentElementId;
	private VertexFormat format;
	private VertexFormat.DrawMode drawMode;
	private boolean textured;
	private boolean hasOverlay;
	private boolean building;
	@Nullable
	private Vec3f[] currentParameters;
	private float cameraX = Float.NaN;
	private float cameraY = Float.NaN;
	private float cameraZ = Float.NaN;
	private boolean cameraOffset;

	public BufferBuilder(int initialCapacity) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 6);
	}

	private void grow() {
		this.grow(this.format.getVertexSize());
	}

	private void grow(int size) {
		if (this.elementOffset + size > this.buffer.capacity()) {
			int i = this.buffer.capacity();
			int j = i + roundBufferSize(size);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
			ByteBuffer byteBuffer = GlAllocationUtils.resizeByteBuffer(this.buffer, j);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
		}
	}

	private static int roundBufferSize(int amount) {
		int i = 2097152;
		if (amount == 0) {
			return i;
		} else {
			if (amount < 0) {
				i *= -1;
			}

			int j = amount % i;
			return j == 0 ? amount : amount + i - j;
		}
	}

	public void setCameraPosition(float cameraX, float cameraY, float cameraZ) {
		if (this.drawMode == VertexFormat.DrawMode.QUADS) {
			if (this.cameraX != cameraX || this.cameraY != cameraY || this.cameraZ != cameraZ) {
				this.cameraX = cameraX;
				this.cameraY = cameraY;
				this.cameraZ = cameraZ;
				if (this.currentParameters == null) {
					this.currentParameters = this.buildParameterVector();
				}
			}
		}
	}

	public BufferBuilder.State popState() {
		return new BufferBuilder.State(this.drawMode, this.vertexCount, this.currentParameters, this.cameraX, this.cameraY, this.cameraZ);
	}

	public void restoreState(BufferBuilder.State state) {
		this.buffer.clear();
		this.drawMode = state.drawMode;
		this.vertexCount = state.vertexCount;
		this.elementOffset = this.buildStart;
		this.currentParameters = state.currentParameters;
		this.cameraX = state.cameraX;
		this.cameraY = state.cameraY;
		this.cameraZ = state.cameraZ;
		this.cameraOffset = true;
	}

	public void begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.drawMode = drawMode;
			this.setFormat(format);
			this.currentElement = (VertexFormatElement)format.getElements().get(0);
			this.currentElementId = 0;
			this.buffer.clear();
		}
	}

	private void setFormat(VertexFormat format) {
		if (this.format != format) {
			this.format = format;
			boolean bl = format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
			boolean bl2 = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			this.textured = bl || bl2;
			this.hasOverlay = bl;
		}
	}

	private IntConsumer createConsumer(VertexFormat.IntType elementFormat) {
		switch (elementFormat) {
			case BYTE:
				return value -> this.buffer.put((byte)value);
			case SHORT:
				return value -> this.buffer.putShort((short)value);
			case INT:
			default:
				return value -> this.buffer.putInt(value);
		}
	}

	private Vec3f[] buildParameterVector() {
		FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
		int i = this.buildStart / 4;
		int j = this.format.getVertexSizeInteger();
		int k = j * this.drawMode.size;
		int l = this.vertexCount / this.drawMode.size;
		Vec3f[] vec3fs = new Vec3f[l];

		for (int m = 0; m < l; m++) {
			float f = floatBuffer.get(i + m * k + 0);
			float g = floatBuffer.get(i + m * k + 1);
			float h = floatBuffer.get(i + m * k + 2);
			float n = floatBuffer.get(i + m * k + j * 2 + 0);
			float o = floatBuffer.get(i + m * k + j * 2 + 1);
			float p = floatBuffer.get(i + m * k + j * 2 + 2);
			float q = (f + n) / 2.0F;
			float r = (g + o) / 2.0F;
			float s = (h + p) / 2.0F;
			vec3fs[m] = new Vec3f(q, r, s);
		}

		return vec3fs;
	}

	private void writeCameraOffset(VertexFormat.IntType elementFormat) {
		float[] fs = new float[this.currentParameters.length];
		int[] is = new int[this.currentParameters.length];

		for (int i = 0; i < this.currentParameters.length; is[i] = i++) {
			float f = this.currentParameters[i].getX() - this.cameraX;
			float g = this.currentParameters[i].getY() - this.cameraY;
			float h = this.currentParameters[i].getZ() - this.cameraZ;
			fs[i] = f * f + g * g + h * h;
		}

		IntArrays.mergeSort(is, (i, jx) -> Floats.compare(fs[jx], fs[i]));
		IntConsumer intConsumer = this.createConsumer(elementFormat);
		this.buffer.position(this.elementOffset);

		for (int j : is) {
			intConsumer.accept(j * this.drawMode.size + 0);
			intConsumer.accept(j * this.drawMode.size + 1);
			intConsumer.accept(j * this.drawMode.size + 2);
			intConsumer.accept(j * this.drawMode.size + 2);
			intConsumer.accept(j * this.drawMode.size + 3);
			intConsumer.accept(j * this.drawMode.size + 0);
		}
	}

	public void end() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		} else {
			int i = this.drawMode.getSize(this.vertexCount);
			VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(i);
			boolean bl;
			if (this.currentParameters != null) {
				int j = MathHelper.roundUpToMultiple(i * intType.size, 4);
				this.grow(j);
				this.writeCameraOffset(intType);
				bl = false;
				this.elementOffset += j;
				this.buildStart = this.buildStart + this.vertexCount * this.format.getVertexSize() + j;
			} else {
				bl = true;
				this.buildStart = this.buildStart + this.vertexCount * this.format.getVertexSize();
			}

			this.building = false;
			this.parameters.add(new BufferBuilder.DrawArrayParameters(this.format, this.vertexCount, i, this.drawMode, intType, this.cameraOffset, bl));
			this.vertexCount = 0;
			this.currentElement = null;
			this.currentElementId = 0;
			this.currentParameters = null;
			this.cameraX = Float.NaN;
			this.cameraY = Float.NaN;
			this.cameraZ = Float.NaN;
			this.cameraOffset = false;
		}
	}

	@Override
	public void putByte(int index, byte value) {
		this.buffer.put(this.elementOffset + index, value);
	}

	@Override
	public void putShort(int index, short value) {
		this.buffer.putShort(this.elementOffset + index, value);
	}

	@Override
	public void putFloat(int index, float value) {
		this.buffer.putFloat(this.elementOffset + index, value);
	}

	@Override
	public void next() {
		if (this.currentElementId != 0) {
			throw new IllegalStateException("Not filled all elements of the vertex");
		} else {
			this.vertexCount++;
			this.grow();
			if (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP) {
				int i = this.format.getVertexSize();
				this.buffer.position(this.elementOffset);
				ByteBuffer byteBuffer = this.buffer.duplicate();
				byteBuffer.position(this.elementOffset - i).limit(this.elementOffset);
				this.buffer.put(byteBuffer);
				this.elementOffset += i;
				this.vertexCount++;
				this.grow();
			}
		}
	}

	@Override
	public void nextElement() {
		ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
		this.currentElementId = (this.currentElementId + 1) % immutableList.size();
		this.elementOffset = this.elementOffset + this.currentElement.getByteLength();
		VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
		this.currentElement = vertexFormatElement;
		if (vertexFormatElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}

		if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
			BufferVertexConsumer.super.color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha);
		}
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else {
			return BufferVertexConsumer.super.color(red, green, blue, alpha);
		}
	}

	@Override
	public void vertex(
		float x,
		float y,
		float z,
		float red,
		float green,
		float blue,
		float alpha,
		float u,
		float v,
		int overlay,
		int light,
		float normalX,
		float normalY,
		float normalZ
	) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else if (this.textured) {
			this.putFloat(0, x);
			this.putFloat(4, y);
			this.putFloat(8, z);
			this.putByte(12, (byte)((int)(red * 255.0F)));
			this.putByte(13, (byte)((int)(green * 255.0F)));
			this.putByte(14, (byte)((int)(blue * 255.0F)));
			this.putByte(15, (byte)((int)(alpha * 255.0F)));
			this.putFloat(16, u);
			this.putFloat(20, v);
			int i;
			if (this.hasOverlay) {
				this.putShort(24, (short)(overlay & 65535));
				this.putShort(26, (short)(overlay >> 16 & 65535));
				i = 28;
			} else {
				i = 24;
			}

			this.putShort(i + 0, (short)(light & 65535));
			this.putShort(i + 2, (short)(light >> 16 & 65535));
			this.putByte(i + 4, BufferVertexConsumer.packByte(normalX));
			this.putByte(i + 5, BufferVertexConsumer.packByte(normalY));
			this.putByte(i + 6, BufferVertexConsumer.packByte(normalZ));
			this.elementOffset += i + 8;
			this.next();
		} else {
			super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
		}
	}

	public Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> popData() {
		BufferBuilder.DrawArrayParameters drawArrayParameters = (BufferBuilder.DrawArrayParameters)this.parameters.get(this.lastParameterIndex++);
		this.buffer.position(this.nextDrawStart);
		this.nextDrawStart = this.nextDrawStart + MathHelper.roundUpToMultiple(drawArrayParameters.getDrawStart(), 4);
		this.buffer.limit(this.nextDrawStart);
		if (this.lastParameterIndex == this.parameters.size() && this.vertexCount == 0) {
			this.clear();
		}

		ByteBuffer byteBuffer = this.buffer.slice();
		this.buffer.clear();
		return Pair.of(drawArrayParameters, byteBuffer);
	}

	public void clear() {
		if (this.buildStart != this.nextDrawStart) {
			LOGGER.warn("Bytes mismatch {} {}", this.buildStart, this.nextDrawStart);
		}

		this.reset();
	}

	public void reset() {
		this.buildStart = 0;
		this.nextDrawStart = 0;
		this.elementOffset = 0;
		this.parameters.clear();
		this.lastParameterIndex = 0;
	}

	@Override
	public VertexFormatElement getCurrentElement() {
		if (this.currentElement == null) {
			throw new IllegalStateException("BufferBuilder not started");
		} else {
			return this.currentElement;
		}
	}

	public boolean isBuilding() {
		return this.building;
	}

	public static final class DrawArrayParameters {
		private final VertexFormat vertexFormat;
		private final int count;
		private final int vertexCount;
		private final VertexFormat.DrawMode mode;
		private final VertexFormat.IntType elementFormat;
		private final boolean cameraOffset;
		private final boolean textured;

		DrawArrayParameters(
			VertexFormat vertexFormat,
			int count,
			int vertexCount,
			VertexFormat.DrawMode mode,
			VertexFormat.IntType elementFormat,
			boolean cameraOffset,
			boolean textured
		) {
			this.vertexFormat = vertexFormat;
			this.count = count;
			this.vertexCount = vertexCount;
			this.mode = mode;
			this.elementFormat = elementFormat;
			this.cameraOffset = cameraOffset;
			this.textured = textured;
		}

		public VertexFormat getVertexFormat() {
			return this.vertexFormat;
		}

		public int getCount() {
			return this.count;
		}

		public int getVertexCount() {
			return this.vertexCount;
		}

		public VertexFormat.DrawMode getMode() {
			return this.mode;
		}

		public VertexFormat.IntType getElementFormat() {
			return this.elementFormat;
		}

		public int getLimit() {
			return this.count * this.vertexFormat.getVertexSize();
		}

		private int getDrawLength() {
			return this.textured ? 0 : this.vertexCount * this.elementFormat.size;
		}

		public int getDrawStart() {
			return this.getLimit() + this.getDrawLength();
		}

		public boolean isCameraOffset() {
			return this.cameraOffset;
		}

		public boolean isTextured() {
			return this.textured;
		}
	}

	public static class State {
		final VertexFormat.DrawMode drawMode;
		final int vertexCount;
		@Nullable
		final Vec3f[] currentParameters;
		final float cameraX;
		final float cameraY;
		final float cameraZ;

		State(VertexFormat.DrawMode drawMode, int i, @Nullable Vec3f[] vec3fs, float f, float g, float h) {
			this.drawMode = drawMode;
			this.vertexCount = i;
			this.currentParameters = vec3fs;
			this.cameraX = f;
			this.cameraY = g;
			this.cameraZ = h;
		}
	}
}
