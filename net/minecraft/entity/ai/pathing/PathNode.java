package net.minecraft.entity.ai.pathing;

import net.minecraft.util.math.MathHelper;

public class PathNode {
	public final int posX;
	public final int posY;
	public final int posZ;
	private final int hashCode;
	int heapIndex = -1;
	float penalizedPathLength;
	float distanceToNearestTarget;
	float heapWeight;
	PathNode previous;
	public boolean visited;

	public PathNode(int i, int j, int k) {
		this.posX = i;
		this.posY = j;
		this.posZ = k;
		this.hashCode = hash(i, j, k);
	}

	public static int hash(int x, int y, int z) {
		return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
	}

	public float getDistance(PathNode node) {
		float f = (float)(node.posX - this.posX);
		float g = (float)(node.posY - this.posY);
		float h = (float)(node.posZ - this.posZ);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public float getSquaredDistance(PathNode node) {
		float f = (float)(node.posX - this.posX);
		float g = (float)(node.posY - this.posY);
		float h = (float)(node.posZ - this.posZ);
		return f * f + g * g + h * h;
	}

	public boolean equals(Object object) {
		if (!(object instanceof PathNode)) {
			return false;
		} else {
			PathNode pathNode = (PathNode)object;
			return this.hashCode == pathNode.hashCode && this.posX == pathNode.posX && this.posY == pathNode.posY && this.posZ == pathNode.posZ;
		}
	}

	public int hashCode() {
		return this.hashCode;
	}

	public boolean isInHeap() {
		return this.heapIndex >= 0;
	}

	public String toString() {
		return this.posX + ", " + this.posY + ", " + this.posZ;
	}
}
