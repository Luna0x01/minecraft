package net.minecraft.client.util.math;

import net.minecraft.util.math.Vec3d;

public class TexturePosition {
	public Vec3d position;
	public float u;
	public float v;

	public TexturePosition(float f, float g, float h, float i, float j) {
		this(new Vec3d((double)f, (double)g, (double)h), i, j);
	}

	public TexturePosition withUv(float u, float v) {
		return new TexturePosition(this, u, v);
	}

	public TexturePosition(TexturePosition texturePosition, float f, float g) {
		this.position = texturePosition.position;
		this.u = f;
		this.v = g;
	}

	public TexturePosition(Vec3d vec3d, float f, float g) {
		this.position = vec3d;
		this.u = f;
		this.v = g;
	}
}
