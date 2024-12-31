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
		this.m00 = this.m01 = this.m02 = this.m03 = this.m10 = this.m11 = this.m12 = this.m13 = this.m20 = this.m21 = this.m22 = this.m23 = this.m30 = this.m31 = this.m32 = this.m33 = 0.0F;
	}
}
