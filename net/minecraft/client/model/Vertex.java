package net.minecraft.client.model;

import net.minecraft.util.math.Vec3d;

public class Vertex {
	public final Vec3d pos;
	public final float u;
	public final float v;

	public Vertex(float f, float g, float h, float i, float j) {
		this(new Vec3d((double)f, (double)g, (double)h), i, j);
	}

	public Vertex remap(float f, float g) {
		return new Vertex(this, f, g);
	}

	public Vertex(Vertex vertex, float f, float g) {
		this.pos = vertex.pos;
		this.u = f;
		this.v = g;
	}

	public Vertex(Vec3d vec3d, float f, float g) {
		this.pos = vec3d;
		this.u = f;
		this.v = g;
	}
}
