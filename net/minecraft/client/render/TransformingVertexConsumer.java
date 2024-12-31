package net.minecraft.client.render;

import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

public class TransformingVertexConsumer extends FixedColorVertexConsumer {
	private final VertexConsumer vertexConsumer;
	private final Matrix4f textureMatrix;
	private final Matrix3f normalMatrix;
	private float x;
	private float y;
	private float z;
	private int u1;
	private int v1;
	private int light;
	private float normalX;
	private float normalY;
	private float normalZ;

	public TransformingVertexConsumer(VertexConsumer vertexConsumer, MatrixStack.Entry entry) {
		this.vertexConsumer = vertexConsumer;
		this.textureMatrix = entry.getModel().copy();
		this.textureMatrix.invert();
		this.normalMatrix = entry.getNormal().copy();
		this.normalMatrix.invert();
		this.init();
	}

	private void init() {
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
		this.u1 = 0;
		this.v1 = 10;
		this.light = 15728880;
		this.normalX = 0.0F;
		this.normalY = 1.0F;
		this.normalZ = 0.0F;
	}

	@Override
	public void next() {
		Vector3f vector3f = new Vector3f(this.normalX, this.normalY, this.normalZ);
		vector3f.transform(this.normalMatrix);
		Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ());
		Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0F);
		vector4f.transform(this.textureMatrix);
		vector4f.rotate(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		vector4f.rotate(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
		vector4f.rotate(direction.getRotationQuaternion());
		float f = -vector4f.getX();
		float g = -vector4f.getY();
		this.vertexConsumer
			.vertex((double)this.x, (double)this.y, (double)this.z)
			.color(1.0F, 1.0F, 1.0F, 1.0F)
			.texture(f, g)
			.overlay(this.u1, this.v1)
			.light(this.light)
			.normal(this.normalX, this.normalY, this.normalZ)
			.next();
		this.init();
	}

	@Override
	public VertexConsumer vertex(double d, double e, double f) {
		this.x = (float)d;
		this.y = (float)e;
		this.z = (float)f;
		return this;
	}

	@Override
	public VertexConsumer color(int i, int j, int k, int l) {
		return this;
	}

	@Override
	public VertexConsumer texture(float f, float g) {
		return this;
	}

	@Override
	public VertexConsumer overlay(int i, int j) {
		this.u1 = i;
		this.v1 = j;
		return this;
	}

	@Override
	public VertexConsumer light(int i, int j) {
		this.light = i | j << 16;
		return this;
	}

	@Override
	public VertexConsumer normal(float f, float g, float h) {
		this.normalX = f;
		this.normalY = g;
		this.normalZ = h;
		return this;
	}
}
