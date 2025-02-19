package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private final boolean penaliseDeepWater;
	private float oldWalkablePenalty;
	private float oldWaterBorderPenalty;

	public AmphibiousPathNodeMaker(boolean penaliseDeepWater) {
		this.penaliseDeepWater = penaliseDeepWater;
	}

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		entity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.oldWalkablePenalty = entity.getPathfindingPenalty(PathNodeType.WALKABLE);
		entity.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0F);
		this.oldWaterBorderPenalty = entity.getPathfindingPenalty(PathNodeType.WATER_BORDER);
		entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0F);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WALKABLE, this.oldWalkablePenalty);
		this.entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.oldWaterBorderPenalty);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		return this.getNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y + 0.5), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = super.getSuccessors(successors, node);
		PathNodeType pathNodeType = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
		PathNodeType pathNodeType2 = this.getNodeType(this.entity, node.x, node.y, node.z);
		int j;
		if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F && pathNodeType2 != PathNodeType.STICKY_HONEY) {
			j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		} else {
			j = 0;
		}

		double d = this.method_37003(new BlockPos(node.x, node.y, node.z));
		PathNode pathNode = this.getPathNode(node.x, node.y + 1, node.z, Math.max(0, j - 1), d, Direction.UP, pathNodeType2);
		PathNode pathNode2 = this.getPathNode(node.x, node.y - 1, node.z, j, d, Direction.DOWN, pathNodeType2);
		if (this.isValidAdjacentSuccessor(pathNode, node)) {
			successors[i++] = pathNode;
		}

		if (this.isValidAdjacentSuccessor(pathNode2, node) && pathNodeType2 != PathNodeType.TRAPDOOR) {
			successors[i++] = pathNode2;
		}

		for (int l = 0; l < i; l++) {
			PathNode pathNode3 = successors[l];
			if (pathNode3.type == PathNodeType.WATER && this.penaliseDeepWater && pathNode3.y < this.entity.world.getSeaLevel() - 10) {
				pathNode3.penalty++;
			}
		}

		return i;
	}

	@Override
	protected double method_37003(BlockPos blockPos) {
		return this.entity.isTouchingWater() ? (double)blockPos.getY() + 0.5 : super.method_37003(blockPos);
	}

	@Override
	protected boolean method_37004() {
		return true;
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		PathNodeType pathNodeType = getCommonNodeType(world, mutable.set(x, y, z));
		if (pathNodeType == PathNodeType.WATER) {
			for (Direction direction : Direction.values()) {
				PathNodeType pathNodeType2 = getCommonNodeType(world, mutable.set(x, y, z).move(direction));
				if (pathNodeType2 == PathNodeType.BLOCKED) {
					return PathNodeType.WATER_BORDER;
				}
			}

			return PathNodeType.WATER;
		} else {
			return getLandNodeType(world, mutable);
		}
	}
}
