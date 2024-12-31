package net.minecraft.client.util.math;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f {
	public Matrix4f(float[] fs) {
		this.m00 = fs[0];
		this.m01 = fs[1];
		this.m02 = fs[2];
		this.m03 = fs[3];
		this.m10 = fs[4];
		this.m11 = fs[5];
		this.m12 = fs[6];
		this.m13 = fs[7];
		this.m20 = fs[8];
		this.m21 = fs[9];
		this.m22 = fs[10];
		this.m23 = fs[11];
		this.m30 = fs[12];
		this.m31 = fs[13];
		this.m32 = fs[14];
		this.m33 = fs[15];
	}

	public Matrix4f() {
		this.m00 = 0.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 0.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 0.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 0.0F;
	}
}
