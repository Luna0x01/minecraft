package net.minecraft.entity.ai;

import net.minecraft.entity.ai.pathing.PathNode;

public class class_2769 {
	private PathNode[] field_13050 = new PathNode[128];
	private int field_13051;

	public PathNode method_11901(PathNode pathNode) {
		if (pathNode.heapIndex >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.field_13051 == this.field_13050.length) {
				PathNode[] pathNodes = new PathNode[this.field_13051 << 1];
				System.arraycopy(this.field_13050, 0, pathNodes, 0, this.field_13051);
				this.field_13050 = pathNodes;
			}

			this.field_13050[this.field_13051] = pathNode;
			pathNode.heapIndex = this.field_13051;
			this.method_11900(this.field_13051++);
			return pathNode;
		}
	}

	public void method_11899() {
		this.field_13051 = 0;
	}

	public PathNode method_11904() {
		PathNode pathNode = this.field_13050[0];
		this.field_13050[0] = this.field_13050[--this.field_13051];
		this.field_13050[this.field_13051] = null;
		if (this.field_13051 > 0) {
			this.method_11903(0);
		}

		pathNode.heapIndex = -1;
		return pathNode;
	}

	public void method_11902(PathNode pathNode, float f) {
		float g = pathNode.heapWeight;
		pathNode.heapWeight = f;
		if (f < g) {
			this.method_11900(pathNode.heapIndex);
		} else {
			this.method_11903(pathNode.heapIndex);
		}
	}

	private void method_11900(int i) {
		PathNode pathNode = this.field_13050[i];
		float f = pathNode.heapWeight;

		while (i > 0) {
			int j = i - 1 >> 1;
			PathNode pathNode2 = this.field_13050[j];
			if (!(f < pathNode2.heapWeight)) {
				break;
			}

			this.field_13050[i] = pathNode2;
			pathNode2.heapIndex = i;
			i = j;
		}

		this.field_13050[i] = pathNode;
		pathNode.heapIndex = i;
	}

	private void method_11903(int i) {
		PathNode pathNode = this.field_13050[i];
		float f = pathNode.heapWeight;

		while (true) {
			int j = 1 + (i << 1);
			int k = j + 1;
			if (j >= this.field_13051) {
				break;
			}

			PathNode pathNode2 = this.field_13050[j];
			float g = pathNode2.heapWeight;
			PathNode pathNode3;
			float h;
			if (k >= this.field_13051) {
				pathNode3 = null;
				h = Float.POSITIVE_INFINITY;
			} else {
				pathNode3 = this.field_13050[k];
				h = pathNode3.heapWeight;
			}

			if (g < h) {
				if (!(g < f)) {
					break;
				}

				this.field_13050[i] = pathNode2;
				pathNode2.heapIndex = i;
				i = j;
			} else {
				if (!(h < f)) {
					break;
				}

				this.field_13050[i] = pathNode3;
				pathNode3.heapIndex = i;
				i = k;
			}
		}

		this.field_13050[i] = pathNode;
		pathNode.heapIndex = i;
	}

	public boolean method_11905() {
		return this.field_13051 == 0;
	}
}
