package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker extends PathNodeMaker {
	@Override
	public void init(BlockView blockView, Entity entity) {
		super.init(blockView, entity);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public PathNode getStart(Entity entity) {
		return this.getNode(
			MathHelper.floor(entity.getBoundingBox().minX), MathHelper.floor(entity.getBoundingBox().minY + 0.5), MathHelper.floor(entity.getBoundingBox().minZ)
		);
	}

	@Override
	public PathNode getNode(Entity entity, double x, double y, double z) {
		return this.getNode(MathHelper.floor(x - (double)(entity.width / 2.0F)), MathHelper.floor(y + 0.5), MathHelper.floor(z - (double)(entity.width / 2.0F)));
	}

	@Override
	public int getSuccessors(PathNode[] nodes, Entity entity, PathNode currentNode, PathNode endNode, float maxDistance) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode = this.getNodeInWater(
				entity, currentNode.posX + direction.getOffsetX(), currentNode.posY + direction.getOffsetY(), currentNode.posZ + direction.getOffsetZ()
			);
			if (pathNode != null && !pathNode.visited && pathNode.getDistance(endNode) < maxDistance) {
				nodes[i++] = pathNode;
			}
		}

		return i;
	}

	private PathNode getNodeInWater(Entity entity, int x, int y, int z) {
		int i = this.getNodeType(entity, x, y, z);
		return i == -1 ? this.getNode(x, y, z) : null;
	}

	private int getNodeType(Entity entity, int x, int y, int z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = x; i < x + this.entityBlockXSize; i++) {
			for (int j = y; j < y + this.entityBlockYSize; j++) {
				for (int k = z; k < z + this.entityBlockZSize; k++) {
					Block block = this.blockView.getBlockState(mutable.setPosition(i, j, k)).getBlock();
					if (block.getMaterial() != Material.WATER) {
						return 0;
					}
				}
			}
		}

		return -1;
	}
}
