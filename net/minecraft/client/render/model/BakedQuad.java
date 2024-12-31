package net.minecraft.client.render.model;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class BakedQuad {
	protected final int[] vertexData;
	protected final int colorIndex;
	protected final Direction direction;
	protected final Sprite field_13552;

	public BakedQuad(int[] is, int i, Direction direction, Sprite sprite) {
		this.vertexData = is;
		this.colorIndex = i;
		this.direction = direction;
		this.field_13552 = sprite;
	}

	public Sprite method_12351() {
		return this.field_13552;
	}

	public int[] getVertexData() {
		return this.vertexData;
	}

	public boolean hasColor() {
		return this.colorIndex != -1;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public Direction getFace() {
		return this.direction;
	}
}
