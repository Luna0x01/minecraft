package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class GlUniform {
	private static final Logger LOGGER = LogManager.getLogger();
	private int loc;
	private final int count;
	private final int dataType;
	private final IntBuffer intData;
	private final FloatBuffer floatData;
	private final String name;
	private boolean stateDirty;
	private final JsonGlProgram program;

	public GlUniform(String string, int i, int j, JsonGlProgram jsonGlProgram) {
		this.name = string;
		this.count = j;
		this.dataType = i;
		this.program = jsonGlProgram;
		if (i <= 3) {
			this.intData = BufferUtils.createIntBuffer(j);
			this.floatData = null;
		} else {
			this.intData = null;
			this.floatData = BufferUtils.createFloatBuffer(j);
		}

		this.loc = -1;
		this.markStateDirty();
	}

	private void markStateDirty() {
		this.stateDirty = true;
		if (this.program != null) {
			this.program.markUniformsDirty();
		}
	}

	public static int getTypeIndex(String typeName) {
		int i = -1;
		if ("int".equals(typeName)) {
			i = 0;
		} else if ("float".equals(typeName)) {
			i = 4;
		} else if (typeName.startsWith("matrix")) {
			if (typeName.endsWith("2x2")) {
				i = 8;
			} else if (typeName.endsWith("3x3")) {
				i = 9;
			} else if (typeName.endsWith("4x4")) {
				i = 10;
			}
		}

		return i;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public String getName() {
		return this.name;
	}

	public void set(float value1) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.markStateDirty();
	}

	public void set(float value1, float value2) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.markStateDirty();
	}

	public void set(float value1, float value2, float value3) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.markStateDirty();
	}

	public void set(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		this.floatData.put(value1);
		this.floatData.put(value2);
		this.floatData.put(value3);
		this.floatData.put(value4);
		this.floatData.flip();
		this.markStateDirty();
	}

	public void setForDataType(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		if (this.dataType >= 4) {
			this.floatData.put(0, value1);
		}

		if (this.dataType >= 5) {
			this.floatData.put(1, value2);
		}

		if (this.dataType >= 6) {
			this.floatData.put(2, value3);
		}

		if (this.dataType >= 7) {
			this.floatData.put(3, value4);
		}

		this.markStateDirty();
	}

	public void set(int value1, int value2, int value3, int value4) {
		this.intData.position(0);
		if (this.dataType >= 0) {
			this.intData.put(0, value1);
		}

		if (this.dataType >= 1) {
			this.intData.put(1, value2);
		}

		if (this.dataType >= 2) {
			this.intData.put(2, value3);
		}

		if (this.dataType >= 3) {
			this.intData.put(3, value4);
		}

		this.markStateDirty();
	}

	public void set(float[] values) {
		if (values.length < this.count) {
			LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", new Object[]{this.count, values.length});
		} else {
			this.floatData.position(0);
			this.floatData.put(values);
			this.floatData.position(0);
			this.markStateDirty();
		}
	}

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
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.floatData.put(8, value9);
		this.floatData.put(9, value10);
		this.floatData.put(10, value11);
		this.floatData.put(11, value12);
		this.floatData.put(12, value13);
		this.floatData.put(13, value14);
		this.floatData.put(14, value15);
		this.floatData.put(15, value16);
		this.markStateDirty();
	}

	public void set(Matrix4f values) {
		this.set(
			values.m00,
			values.m01,
			values.m02,
			values.m03,
			values.m10,
			values.m11,
			values.m12,
			values.m13,
			values.m20,
			values.m21,
			values.m22,
			values.m23,
			values.m30,
			values.m31,
			values.m32,
			values.m33
		);
	}

	public void upload() {
		if (!this.stateDirty) {
		}

		this.stateDirty = false;
		if (this.dataType <= 3) {
			this.uploadInts();
		} else if (this.dataType <= 7) {
			this.uploadFloats();
		} else {
			if (this.dataType > 10) {
				LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", new Object[]{this.dataType});
				return;
			}

			this.uploadMatrix();
		}
	}

	private void uploadInts() {
		switch (this.dataType) {
			case 0:
				GLX.gl20Uniform1(this.loc, this.intData);
				break;
			case 1:
				GLX.gl20Uniform2(this.loc, this.intData);
				break;
			case 2:
				GLX.gl20Uniform3(this.loc, this.intData);
				break;
			case 3:
				GLX.gl20Uniform4(this.loc, this.intData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", new Object[]{this.count});
		}
	}

	private void uploadFloats() {
		switch (this.dataType) {
			case 4:
				GLX.gl20Uniform(this.loc, this.floatData);
				break;
			case 5:
				GLX.gl20Uniform2(this.loc, this.floatData);
				break;
			case 6:
				GLX.gl20Uniform3(this.loc, this.floatData);
				break;
			case 7:
				GLX.gl20Uniform4(this.loc, this.floatData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", new Object[]{this.count});
		}
	}

	private void uploadMatrix() {
		switch (this.dataType) {
			case 8:
				GLX.gl20UniformMatrix2(this.loc, true, this.floatData);
				break;
			case 9:
				GLX.gl20UniformMatrix3(this.loc, true, this.floatData);
				break;
			case 10:
				GLX.gl20UniformMatrix4(this.loc, true, this.floatData);
		}
	}
}
