package net.minecraft.entity.ai.pathing;

public class PathMinHeap {
	private PathNode[] nodes = new PathNode[1024];
	private int count;

	public PathNode push(PathNode node) {
		if (node.heapIndex >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.count == this.nodes.length) {
				PathNode[] pathNodes = new PathNode[this.count << 1];
				System.arraycopy(this.nodes, 0, pathNodes, 0, this.count);
				this.nodes = pathNodes;
			}

			this.nodes[this.count] = node;
			node.heapIndex = this.count;
			this.shiftUp(this.count++);
			return node;
		}
	}

	public void clear() {
		this.count = 0;
	}

	public PathNode pop() {
		PathNode pathNode = this.nodes[0];
		this.nodes[0] = this.nodes[--this.count];
		this.nodes[this.count] = null;
		if (this.count > 0) {
			this.shiftDown(0);
		}

		pathNode.heapIndex = -1;
		return pathNode;
	}

	public void setNodeWeight(PathNode node, float weight) {
		float f = node.heapWeight;
		node.heapWeight = weight;
		if (weight < f) {
			this.shiftUp(node.heapIndex);
		} else {
			this.shiftDown(node.heapIndex);
		}
	}

	private void shiftUp(int index) {
		PathNode pathNode = this.nodes[index];
		float f = pathNode.heapWeight;

		while (index > 0) {
			int i = index - 1 >> 1;
			PathNode pathNode2 = this.nodes[i];
			if (!(f < pathNode2.heapWeight)) {
				break;
			}

			this.nodes[index] = pathNode2;
			pathNode2.heapIndex = index;
			index = i;
		}

		this.nodes[index] = pathNode;
		pathNode.heapIndex = index;
	}

	private void shiftDown(int index) {
		PathNode pathNode = this.nodes[index];
		float f = pathNode.heapWeight;

		while (true) {
			int i = 1 + (index << 1);
			int j = i + 1;
			if (i >= this.count) {
				break;
			}

			PathNode pathNode2 = this.nodes[i];
			float g = pathNode2.heapWeight;
			PathNode pathNode3;
			float h;
			if (j >= this.count) {
				pathNode3 = null;
				h = Float.POSITIVE_INFINITY;
			} else {
				pathNode3 = this.nodes[j];
				h = pathNode3.heapWeight;
			}

			if (g < h) {
				if (!(g < f)) {
					break;
				}

				this.nodes[index] = pathNode2;
				pathNode2.heapIndex = index;
				index = i;
			} else {
				if (!(h < f)) {
					break;
				}

				this.nodes[index] = pathNode3;
				pathNode3.heapIndex = index;
				index = j;
			}
		}

		this.nodes[index] = pathNode;
		pathNode.heapIndex = index;
	}

	public boolean isEmpty() {
		return this.count == 0;
	}
}
