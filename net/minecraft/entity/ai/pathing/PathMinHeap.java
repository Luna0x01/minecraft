package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class PathMinHeap {
	private final PathNode[] field_13084;
	private PathNode[] field_13085 = new PathNode[0];
	private PathNode[] field_13086 = new PathNode[0];
	private PathNode field_13087;
	private int field_13088;
	private int field_13089;

	public PathMinHeap(PathNode[] pathNodes) {
		this.field_13084 = pathNodes;
		this.field_13089 = pathNodes.length;
	}

	public void method_11924() {
		this.field_13088++;
	}

	public boolean method_11930() {
		return this.field_13088 >= this.field_13089;
	}

	@Nullable
	public PathNode method_11934() {
		return this.field_13089 > 0 ? this.field_13084[this.field_13089 - 1] : null;
	}

	public PathNode method_11925(int i) {
		return this.field_13084[i];
	}

	public void method_11926(int i, PathNode pathNode) {
		this.field_13084[i] = pathNode;
	}

	public int method_11936() {
		return this.field_13089;
	}

	public void method_11931(int i) {
		this.field_13089 = i;
	}

	public int method_11937() {
		return this.field_13088;
	}

	public void method_11935(int i) {
		this.field_13088 = i;
	}

	public Vec3d method_11929(Entity entity, int i) {
		double d = (double)this.field_13084[i].posX + (double)((int)(entity.width + 1.0F)) * 0.5;
		double e = (double)this.field_13084[i].posY;
		double f = (double)this.field_13084[i].posZ + (double)((int)(entity.width + 1.0F)) * 0.5;
		return new Vec3d(d, e, f);
	}

	public Vec3d method_11928(Entity entity) {
		return this.method_11929(entity, this.field_13088);
	}

	public Vec3d method_11938() {
		PathNode pathNode = this.field_13084[this.field_13088];
		return new Vec3d((double)pathNode.posX, (double)pathNode.posY, (double)pathNode.posZ);
	}

	public boolean method_11927(PathMinHeap pathMinHeap) {
		if (pathMinHeap == null) {
			return false;
		} else if (pathMinHeap.field_13084.length != this.field_13084.length) {
			return false;
		} else {
			for (int i = 0; i < this.field_13084.length; i++) {
				if (this.field_13084[i].posX != pathMinHeap.field_13084[i].posX
					|| this.field_13084[i].posY != pathMinHeap.field_13084[i].posY
					|| this.field_13084[i].posZ != pathMinHeap.field_13084[i].posZ) {
					return false;
				}
			}

			return true;
		}
	}

	public PathNode[] method_13397() {
		return this.field_13085;
	}

	public PathNode[] method_13398() {
		return this.field_13086;
	}

	public PathNode method_13399() {
		return this.field_13087;
	}

	public static PathMinHeap read(PacketByteBuf packet) {
		int i = packet.readInt();
		PathNode pathNode = PathNode.method_11908(packet);
		PathNode[] pathNodes = new PathNode[packet.readInt()];

		for (int j = 0; j < pathNodes.length; j++) {
			pathNodes[j] = PathNode.method_11908(packet);
		}

		PathNode[] pathNodes2 = new PathNode[packet.readInt()];

		for (int k = 0; k < pathNodes2.length; k++) {
			pathNodes2[k] = PathNode.method_11908(packet);
		}

		PathNode[] pathNodes3 = new PathNode[packet.readInt()];

		for (int l = 0; l < pathNodes3.length; l++) {
			pathNodes3[l] = PathNode.method_11908(packet);
		}

		PathMinHeap pathMinHeap = new PathMinHeap(pathNodes);
		pathMinHeap.field_13085 = pathNodes2;
		pathMinHeap.field_13086 = pathNodes3;
		pathMinHeap.field_13087 = pathNode;
		pathMinHeap.field_13088 = i;
		return pathMinHeap;
	}
}
