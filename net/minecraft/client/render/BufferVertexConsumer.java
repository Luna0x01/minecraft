package net.minecraft.client.render;

import net.minecraft.util.math.MathHelper;

public interface BufferVertexConsumer extends VertexConsumer {
	VertexFormatElement getCurrentElement();

	void nextElement();

	void putByte(int i, byte b);

	void putShort(int i, short s);

	void putFloat(int i, float f);

	@Override
	default VertexConsumer vertex(double d, double e, double f) {
		if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.field_1623) {
			throw new IllegalStateException();
		} else {
			this.putFloat(0, (float)d);
			this.putFloat(4, (float)e);
			this.putFloat(8, (float)f);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer color(int i, int j, int k, int l) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.field_1632) {
			return this;
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.field_1624) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, (byte)i);
			this.putByte(1, (byte)j);
			this.putByte(2, (byte)k);
			this.putByte(3, (byte)l);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer texture(float f, float g) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() == VertexFormatElement.Type.field_1636 && vertexFormatElement.getIndex() == 0) {
			if (vertexFormatElement.getFormat() != VertexFormatElement.Format.field_1623) {
				throw new IllegalStateException();
			} else {
				this.putFloat(0, f);
				this.putFloat(4, g);
				this.nextElement();
				return this;
			}
		} else {
			return this;
		}
	}

	@Override
	default VertexConsumer overlay(int i, int j) {
		return this.texture((short)i, (short)j, 1);
	}

	@Override
	default VertexConsumer light(int i, int j) {
		return this.texture((short)i, (short)j, 2);
	}

	default VertexConsumer texture(short s, short t, int i) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.field_1636 || vertexFormatElement.getIndex() != i) {
			return this;
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.field_1625) {
			throw new IllegalStateException();
		} else {
			this.putShort(0, s);
			this.putShort(2, t);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer normal(float f, float g, float h) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.field_1635) {
			return this;
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.field_1621) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, method_24212(f));
			this.putByte(1, method_24212(g));
			this.putByte(2, method_24212(h));
			this.nextElement();
			return this;
		}
	}

	static byte method_24212(float f) {
		return (byte)((int)(MathHelper.clamp(f, -1.0F, 1.0F) * 127.0F) & 0xFF);
	}
}
