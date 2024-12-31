package net.minecraft.client.render.model;

import net.minecraft.util.math.Direction;

public class BakedQuad {
	protected final int[] vertexData;
	protected final int colorIndex;
	protected final Direction direction;

	public BakedQuad(int[] is, int i, Direction direction) {
		this.vertexData = is;
		this.colorIndex = i;
		this.direction = direction;
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
