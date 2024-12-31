package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Path {
	private final PathNode[] nodes;
	private int currentNodeIndex;
	private int nodeCount;

	public Path(PathNode[] pathNodes) {
		this.nodes = pathNodes;
		this.nodeCount = pathNodes.length;
	}

	public void next() {
		this.currentNodeIndex++;
	}

	public boolean isFinished() {
		return this.currentNodeIndex >= this.nodeCount;
	}

	public PathNode getEnd() {
		return this.nodeCount > 0 ? this.nodes[this.nodeCount - 1] : null;
	}

	public PathNode getNode(int index) {
		return this.nodes[index];
	}

	public int getNodeCount() {
		return this.nodeCount;
	}

	public void setNodeCount(int index) {
		this.nodeCount = index;
	}

	public int getCurrentNode() {
		return this.currentNodeIndex;
	}

	public void setCurrentNode(int index) {
		this.currentNodeIndex = index;
	}

	public Vec3d getNodePosition(Entity entity, int index) {
		double d = (double)this.nodes[index].posX + (double)((int)(entity.width + 1.0F)) * 0.5;
		double e = (double)this.nodes[index].posY;
		double f = (double)this.nodes[index].posZ + (double)((int)(entity.width + 1.0F)) * 0.5;
		return new Vec3d(d, e, f);
	}

	public Vec3d getCurrentPosition(Entity entity) {
		return this.getNodePosition(entity, this.currentNodeIndex);
	}

	public boolean equalsPath(Path path) {
		if (path == null) {
			return false;
		} else if (path.nodes.length != this.nodes.length) {
			return false;
		} else {
			for (int i = 0; i < this.nodes.length; i++) {
				if (this.nodes[i].posX != path.nodes[i].posX || this.nodes[i].posY != path.nodes[i].posY || this.nodes[i].posZ != path.nodes[i].posZ) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean equalsEndPos(Vec3d pos) {
		PathNode pathNode = this.getEnd();
		return pathNode == null ? false : pathNode.posX == (int)pos.x && pathNode.posZ == (int)pos.z;
	}
}
