package net.minecraft.client.render;

import net.minecraft.client.texture.Sprite;

public class SpriteTexturedVertexConsumer implements VertexConsumer {
	private final VertexConsumer parent;
	private final Sprite sprite;

	public SpriteTexturedVertexConsumer(VertexConsumer vertexConsumer, Sprite sprite) {
		this.parent = vertexConsumer;
		this.sprite = sprite;
	}

	@Override
	public VertexConsumer vertex(double d, double e, double f) {
		return this.parent.vertex(d, e, f);
	}

	@Override
	public VertexConsumer color(int i, int j, int k, int l) {
		return this.parent.color(i, j, k, l);
	}

	@Override
	public VertexConsumer texture(float f, float g) {
		return this.parent.texture(this.sprite.getFrameU((double)(f * 16.0F)), this.sprite.getFrameV((double)(g * 16.0F)));
	}

	@Override
	public VertexConsumer overlay(int i, int j) {
		return this.parent.overlay(i, j);
	}

	@Override
	public VertexConsumer light(int i, int j) {
		return this.parent.light(i, j);
	}

	@Override
	public VertexConsumer normal(float f, float g, float h) {
		return this.parent.normal(f, g, h);
	}

	@Override
	public void next() {
		this.parent.next();
	}

	@Override
	public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
		this.parent.vertex(f, g, h, i, j, k, l, this.sprite.getFrameU((double)(m * 16.0F)), this.sprite.getFrameV((double)(n * 16.0F)), o, p, q, r, s);
	}
}
