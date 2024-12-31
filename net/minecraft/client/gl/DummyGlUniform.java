package net.minecraft.client.gl;

import org.lwjgl.util.vector.Matrix4f;

public class DummyGlUniform extends GlUniform {
	public DummyGlUniform() {
		super("dummy", 4, 1, null);
	}

	@Override
	public void set(float value1) {
	}

	@Override
	public void set(float value1, float value2) {
	}

	@Override
	public void set(float value1, float value2, float value3) {
	}

	@Override
	public void set(float value1, float value2, float value3, float value4) {
	}

	@Override
	public void setForDataType(float value1, float value2, float value3, float value4) {
	}

	@Override
	public void set(int value1, int value2, int value3, int value4) {
	}

	@Override
	public void set(float[] values) {
	}

	@Override
	public void set(
		float value1,
		float value2,
		float value3,
		float value4,
		float value5,
		float value6,
		float value7,
		float value8,
		float value9,
		float value10,
		float value11,
		float value12,
		float value13,
		float value14,
		float value15,
		float value16
	) {
	}

	@Override
	public void set(Matrix4f values) {
	}
}
