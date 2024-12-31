package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PathNodeNavigator {
	private PathMinHeap minHeap = new PathMinHeap();
	private PathNode[] nodes = new PathNode[32];
	private PathNodeMaker nodeMaker;

	public PathNodeNavigator(PathNodeMaker pathNodeMaker) {
		this.nodeMaker = pathNodeMaker;
	}

	public Path findPathToAny(BlockView world, Entity entity, Entity target, float maxDistance) {
		return this.findPathToAny(world, entity, target.x, target.getBoundingBox().minY, target.z, maxDistance);
	}

	public Path findPathToAny(BlockView world, Entity entity, BlockPos pos, float maxDistance) {
		return this.findPathToAny(
			world, entity, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), maxDistance
		);
	}

	private Path findPathToAny(BlockView world, Entity entity, double x, double y, double z, float maxDistance) {
		this.minHeap.clear();
		this.nodeMaker.init(world, entity);
		PathNode pathNode = this.nodeMaker.getStart(entity);
		PathNode pathNode2 = this.nodeMaker.getNode(entity, x, y, z);
		Path path = this.findPathToAny(entity, pathNode, pathNode2, maxDistance);
		this.nodeMaker.clear();
		return path;
	}

	private Path findPathToAny(Entity entity, PathNode startNode, PathNode endNode, float maxDistance) {
		startNode.penalizedPathLength = 0.0F;
		startNode.distanceToNearestTarget = startNode.getSquaredDistance(endNode);
		startNode.heapWeight = startNode.distanceToNearestTarget;
		this.minHeap.clear();
		this.minHeap.push(startNode);
		PathNode pathNode = startNode;

		while (!this.minHeap.isEmpty()) {
			PathNode pathNode2 = this.minHeap.pop();
			if (pathNode2.equals(endNode)) {
				return this.createPath(startNode, endNode);
			}

			if (pathNode2.getSquaredDistance(endNode) < pathNode.getSquaredDistance(endNode)) {
				pathNode = pathNode2;
			}

			pathNode2.visited = true;
			int i = this.nodeMaker.getSuccessors(this.nodes, entity, pathNode2, endNode, maxDistance);

			for (int j = 0; j < i; j++) {
				PathNode pathNode3 = this.nodes[j];
				float f = pathNode2.penalizedPathLength + pathNode2.getSquaredDistance(pathNode3);
				if (f < maxDistance * 2.0F && (!pathNode3.isInHeap() || f < pathNode3.penalizedPathLength)) {
					pathNode3.previous = pathNode2;
					pathNode3.penalizedPathLength = f;
					pathNode3.distanceToNearestTarget = pathNode3.getSquaredDistance(endNode);
					if (pathNode3.isInHeap()) {
						this.minHeap.setNodeWeight(pathNode3, pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget);
					} else {
						pathNode3.heapWeight = pathNode3.penalizedPathLength + pathNode3.distanceToNearestTarget;
						this.minHeap.push(pathNode3);
					}
				}
			}
		}

		return pathNode == startNode ? null : this.createPath(startNode, pathNode);
	}

	private Path createPath(PathNode startNode, PathNode endNode) {
		int i = 1;

		for (PathNode pathNode = endNode; pathNode.previous != null; pathNode = pathNode.previous) {
			i++;
		}

		PathNode[] pathNodes = new PathNode[i];
		PathNode var7 = endNode;
		i--;

		for (pathNodes[i] = endNode; var7.previous != null; pathNodes[i] = var7) {
			var7 = var7.previous;
			i--;
		}

		return new Path(pathNodes);
	}
}
