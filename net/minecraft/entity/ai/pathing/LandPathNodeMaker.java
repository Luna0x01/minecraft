package net.minecraft.entity.ai.pathing;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class LandPathNodeMaker extends PathNodeMaker {
	private boolean canEnterOpenDoors;
	private boolean canOpenDoors;
	private boolean field_10243;
	private boolean canSwim;
	private boolean field_10245;

	@Override
	public void init(BlockView blockView, Entity entity) {
		super.init(blockView, entity);
		this.field_10245 = this.field_10243;
	}

	@Override
	public void clear() {
		super.clear();
		this.field_10243 = this.field_10245;
	}

	@Override
	public PathNode getStart(Entity entity) {
		int i;
		if (this.canSwim && entity.isTouchingWater()) {
			i = (int)entity.getBoundingBox().minY;
			BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(entity.x), i, MathHelper.floor(entity.z));

			for (Block block = this.blockView.getBlockState(mutable).getBlock();
				block == Blocks.FLOWING_WATER || block == Blocks.WATER;
				block = this.blockView.getBlockState(mutable).getBlock()
			) {
				mutable.setPosition(MathHelper.floor(entity.x), ++i, MathHelper.floor(entity.z));
			}

			this.field_10243 = false;
		} else {
			i = MathHelper.floor(entity.getBoundingBox().minY + 0.5);
		}

		return this.getNode(MathHelper.floor(entity.getBoundingBox().minX), i, MathHelper.floor(entity.getBoundingBox().minZ));
	}

	@Override
	public PathNode getNode(Entity entity, double x, double y, double z) {
		return this.getNode(MathHelper.floor(x - (double)(entity.width / 2.0F)), MathHelper.floor(y), MathHelper.floor(z - (double)(entity.width / 2.0F)));
	}

	@Override
	public int getSuccessors(PathNode[] nodes, Entity entity, PathNode currentNode, PathNode endNode, float maxDistance) {
		int i = 0;
		int j = 0;
		if (this.getNodeType(entity, currentNode.posX, currentNode.posY + 1, currentNode.posZ) == 1) {
			j = 1;
		}

		PathNode pathNode = this.getPathNode(entity, currentNode.posX, currentNode.posY, currentNode.posZ + 1, j);
		PathNode pathNode2 = this.getPathNode(entity, currentNode.posX - 1, currentNode.posY, currentNode.posZ, j);
		PathNode pathNode3 = this.getPathNode(entity, currentNode.posX + 1, currentNode.posY, currentNode.posZ, j);
		PathNode pathNode4 = this.getPathNode(entity, currentNode.posX, currentNode.posY, currentNode.posZ - 1, j);
		if (pathNode != null && !pathNode.visited && pathNode.getDistance(endNode) < maxDistance) {
			nodes[i++] = pathNode;
		}

		if (pathNode2 != null && !pathNode2.visited && pathNode2.getDistance(endNode) < maxDistance) {
			nodes[i++] = pathNode2;
		}

		if (pathNode3 != null && !pathNode3.visited && pathNode3.getDistance(endNode) < maxDistance) {
			nodes[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.visited && pathNode4.getDistance(endNode) < maxDistance) {
			nodes[i++] = pathNode4;
		}

		return i;
	}

	private PathNode getPathNode(Entity entity, int x, int y, int z, int maxYStep) {
		PathNode pathNode = null;
		int i = this.getNodeType(entity, x, y, z);
		if (i == 2) {
			return this.getNode(x, y, z);
		} else {
			if (i == 1) {
				pathNode = this.getNode(x, y, z);
			}

			if (pathNode == null && maxYStep > 0 && i != -3 && i != -4 && this.getNodeType(entity, x, y + maxYStep, z) == 1) {
				pathNode = this.getNode(x, y + maxYStep, z);
				y += maxYStep;
			}

			if (pathNode != null) {
				int j = 0;

				int k;
				for (k = 0; y > 0; pathNode = this.getNode(x, y, z)) {
					k = this.getNodeType(entity, x, y - 1, z);
					if (this.field_10243 && k == -1) {
						return null;
					}

					if (k != 1) {
						break;
					}

					if (j++ >= entity.getSafeFallDistance()) {
						return null;
					}

					if (--y <= 0) {
						return null;
					}
				}

				if (k == -2) {
					return null;
				}
			}

			return pathNode;
		}
	}

	private int getNodeType(Entity entity, int x, int y, int z) {
		return getNodeType(
			this.blockView,
			entity,
			x,
			y,
			z,
			this.entityBlockXSize,
			this.entityBlockYSize,
			this.entityBlockZSize,
			this.field_10243,
			this.canOpenDoors,
			this.canEnterOpenDoors
		);
	}

	public static int getNodeType(
		BlockView blockView, Entity entity, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean bl, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		boolean bl2 = false;
		BlockPos blockPos = new BlockPos(entity);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = x; i < x + sizeX; i++) {
			for (int j = y; j < y + sizeY; j++) {
				for (int k = z; k < z + sizeZ; k++) {
					mutable.setPosition(i, j, k);
					Block block = blockView.getBlockState(mutable).getBlock();
					if (block.getMaterial() != Material.AIR) {
						if (block == Blocks.TRAPDOOR || block == Blocks.IRON_TRAPDOOR) {
							bl2 = true;
						} else if (block != Blocks.FLOWING_WATER && block != Blocks.WATER) {
							if (!canEnterOpenDoors && block instanceof DoorBlock && block.getMaterial() == Material.WOOD) {
								return 0;
							}
						} else {
							if (bl) {
								return -1;
							}

							bl2 = true;
						}

						if (entity.world.getBlockState(mutable).getBlock() instanceof AbstractRailBlock) {
							if (!(entity.world.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
								&& !(entity.world.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
								return -3;
							}
						} else if (!block.blocksMovement(blockView, mutable) && (!canOpenDoors || !(block instanceof DoorBlock) || block.getMaterial() != Material.WOOD)) {
							if (block instanceof FenceBlock || block instanceof FenceGateBlock || block instanceof WallBlock) {
								return -3;
							}

							if (block == Blocks.TRAPDOOR || block == Blocks.IRON_TRAPDOOR) {
								return -4;
							}

							Material material = block.getMaterial();
							if (material != Material.LAVA) {
								return 0;
							}

							if (!entity.isTouchingLava()) {
								return -2;
							}
						}
					}
				}
			}
		}

		return bl2 ? 2 : 1;
	}

	public void setCanEnterOpenDoors(boolean value) {
		this.canEnterOpenDoors = value;
	}

	public void setCanOpenDoors(boolean value) {
		this.canOpenDoors = value;
	}

	public void method_9300(boolean value) {
		this.field_10243 = value;
	}

	public void setCanSwim(boolean value) {
		this.canSwim = value;
	}

	public boolean canEnterOpenDoors() {
		return this.canEnterOpenDoors;
	}

	public boolean canSwim() {
		return this.canSwim;
	}

	public boolean method_9303() {
		return this.field_10243;
	}
}
