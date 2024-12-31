package net.minecraft.entity.ai.pathing;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class PathNode {
	public final int posX;
	public final int posY;
	public final int posZ;
	private final int hashCode;
	public int heapIndex = -1;
	public float penalizedPathLength;
	public float distanceToNearestTarget;
	public float heapWeight;
	public PathNode previous;
	public boolean visited;
	public float field_13071 = 0.0F;
	public float field_13072 = 0.0F;
	public float field_13073 = 0.0F;
	public LandType field_13074 = LandType.BLOCKED;

	public PathNode(int i, int j, int k) {
		this.posX = i;
		this.posY = j;
		this.posZ = k;
		this.hashCode = hash(i, j, k);
	}

	public PathNode method_11907(int i, int j, int k) {
		PathNode pathNode = new PathNode(i, j, k);
		pathNode.heapIndex = this.heapIndex;
		pathNode.penalizedPathLength = this.penalizedPathLength;
		pathNode.distanceToNearestTarget = this.distanceToNearestTarget;
		pathNode.heapWeight = this.heapWeight;
		pathNode.previous = this.previous;
		pathNode.visited = this.visited;
		pathNode.field_13071 = this.field_13071;
		pathNode.field_13072 = this.field_13072;
		pathNode.field_13073 = this.field_13073;
		pathNode.field_13074 = this.field_13074;
		return pathNode;
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

	public float method_11909(PathNode pathNode) {
		float f = (float)Math.abs(pathNode.posX - this.posX);
		float g = (float)Math.abs(pathNode.posY - this.posY);
		float h = (float)Math.abs(pathNode.posZ - this.posZ);
		return f + g + h;
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

	public static PathNode method_11908(PacketByteBuf packetByteBuf) {
		PathNode pathNode = new PathNode(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
		pathNode.field_13071 = packetByteBuf.readFloat();
		pathNode.field_13072 = packetByteBuf.readFloat();
		pathNode.field_13073 = packetByteBuf.readFloat();
		pathNode.visited = packetByteBuf.readBoolean();
		pathNode.field_13074 = LandType.values()[packetByteBuf.readInt()];
		pathNode.heapWeight = packetByteBuf.readFloat();
		return pathNode;
	}
}
