package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.class_2769;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PathNodeNavigator {
	private final class_2769 field_13090 = new class_2769();
	private final Set<PathNode> field_13091 = Sets.newHashSet();
	private final PathNode[] nodes = new PathNode[32];
	private class_2771 field_13092;

	public PathNodeNavigator(class_2771 arg) {
		this.field_13092 = arg;
	}

	@Nullable
	public PathMinHeap method_11941(BlockView blockView, MobEntity mobEntity, Entity entity, float f) {
		return this.method_11939(blockView, mobEntity, entity.x, entity.getBoundingBox().minY, entity.z, f);
	}

	@Nullable
	public PathMinHeap method_11940(BlockView blockView, MobEntity mobEntity, BlockPos blockPos, float f) {
		return this.method_11939(
			blockView, mobEntity, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), f
		);
	}

	@Nullable
	private PathMinHeap method_11939(BlockView blockView, MobEntity mobEntity, double d, double e, double f, float g) {
		this.field_13090.method_11899();
		this.field_13092.method_11915(blockView, mobEntity);
		PathNode pathNode = this.field_13092.method_11918();
		PathNode pathNode2 = this.field_13092.method_11911(d, e, f);
		PathMinHeap pathMinHeap = this.method_11943(pathNode, pathNode2, g);
		this.field_13092.method_11910();
		return pathMinHeap;
	}

	@Nullable
	private PathMinHeap method_11943(PathNode pathNode, PathNode pathNode2, float f) {
		pathNode.penalizedPathLength = 0.0F;
		pathNode.distanceToNearestTarget = pathNode.method_11909(pathNode2);
		pathNode.heapWeight = pathNode.distanceToNearestTarget;
		this.field_13090.method_11899();
		this.field_13091.clear();
		this.field_13090.method_11901(pathNode);
		PathNode pathNode3 = pathNode;
		int i = 0;

		while (!this.field_13090.method_11905()) {
			if (++i >= 200) {
				break;
			}

			PathNode pathNode4 = this.field_13090.method_11904();
			if (pathNode4.equals(pathNode2)) {
				pathNode3 = pathNode2;
				break;
			}

			if (pathNode4.method_11909(pathNode2) < pathNode3.method_11909(pathNode2)) {
				pathNode3 = pathNode4;
			}

			pathNode4.visited = true;
			int j = this.field_13092.method_11917(this.nodes, pathNode4, pathNode2, f);

			for (int k = 0; k < j; k++) {
				PathNode pathNode5 = this.nodes[k];
				float g = pathNode4.method_11909(pathNode5);
				pathNode5.field_13071 = pathNode4.field_13071 + g;
				pathNode5.field_13072 = g + pathNode5.field_13073;
				float h = pathNode4.penalizedPathLength + pathNode5.field_13072;
				if (pathNode5.field_13071 < f && (!pathNode5.isInHeap() || h < pathNode5.penalizedPathLength)) {
					pathNode5.previous = pathNode4;
					pathNode5.penalizedPathLength = h;
					pathNode5.distanceToNearestTarget = pathNode5.method_11909(pathNode2) + pathNode5.field_13073;
					if (pathNode5.isInHeap()) {
						this.field_13090.method_11902(pathNode5, pathNode5.penalizedPathLength + pathNode5.distanceToNearestTarget);
					} else {
						pathNode5.heapWeight = pathNode5.penalizedPathLength + pathNode5.distanceToNearestTarget;
						this.field_13090.method_11901(pathNode5);
					}
				}
			}
		}

		return pathNode3 == pathNode ? null : this.method_11942(pathNode, pathNode3);
	}

	private PathMinHeap method_11942(PathNode pathNode, PathNode pathNode2) {
		int i = 1;

		for (PathNode pathNode3 = pathNode2; pathNode3.previous != null; pathNode3 = pathNode3.previous) {
			i++;
		}

		PathNode[] pathNodes = new PathNode[i];
		PathNode var7 = pathNode2;
		i--;

		for (pathNodes[i] = pathNode2; var7.previous != null; pathNodes[i] = var7) {
			var7 = var7.previous;
			i--;
		}

		return new PathMinHeap(pathNodes);
	}
}
