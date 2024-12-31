package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class LandPathNodeMaker extends class_2771 {
	protected float field_13093;

	@Override
	public void method_11915(BlockView blockView, MobEntity mobEntity) {
		super.method_11915(blockView, mobEntity);
		this.field_13093 = mobEntity.method_13075(LandType.WATER);
	}

	@Override
	public void method_11910() {
		this.field_13076.method_13076(LandType.WATER, this.field_13093);
		super.method_11910();
	}

	@Override
	public PathNode method_11918() {
		int i;
		if (this.method_11923() && this.field_13076.isTouchingWater()) {
			i = (int)this.field_13076.getBoundingBox().minY;
			BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.field_13076.x), i, MathHelper.floor(this.field_13076.z));

			for (Block block = this.field_13075.getBlockState(mutable).getBlock();
				block == Blocks.FLOWING_WATER || block == Blocks.WATER;
				block = this.field_13075.getBlockState(mutable).getBlock()
			) {
				mutable.setPosition(MathHelper.floor(this.field_13076.x), ++i, MathHelper.floor(this.field_13076.z));
			}
		} else if (this.field_13076.onGround) {
			i = MathHelper.floor(this.field_13076.getBoundingBox().minY + 0.5);
		} else {
			BlockPos blockPos = new BlockPos(this.field_13076);

			while (
				(
						this.field_13075.getBlockState(blockPos).getMaterial() == Material.AIR
							|| this.field_13075.getBlockState(blockPos).getBlock().blocksMovement(this.field_13075, blockPos)
					)
					&& blockPos.getY() > 0
			) {
				blockPos = blockPos.down();
			}

			i = blockPos.up().getY();
		}

		BlockPos blockPos2 = new BlockPos(this.field_13076);
		LandType landType = this.method_11947(this.field_13076, blockPos2.getX(), i, blockPos2.getZ());
		if (this.field_13076.method_13075(landType) < 0.0F) {
			Set<BlockPos> set = Sets.newHashSet();
			set.add(new BlockPos(this.field_13076.getBoundingBox().minX, (double)i, this.field_13076.getBoundingBox().minZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().minX, (double)i, this.field_13076.getBoundingBox().maxZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().maxX, (double)i, this.field_13076.getBoundingBox().minZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().maxX, (double)i, this.field_13076.getBoundingBox().maxZ));

			for (BlockPos blockPos3 : set) {
				LandType landType2 = this.method_11948(this.field_13076, blockPos3);
				if (this.field_13076.method_13075(landType2) >= 0.0F) {
					return this.method_11912(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
				}
			}
		}

		return this.method_11912(blockPos2.getX(), i, blockPos2.getZ());
	}

	@Override
	public PathNode method_11911(double d, double e, double f) {
		return this.method_11912(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
	}

	@Override
	public int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;
		int j = 0;
		LandType landType = this.method_11947(this.field_13076, pathNode.posX, pathNode.posY + 1, pathNode.posZ);
		if (this.field_13076.method_13075(landType) >= 0.0F) {
			j = MathHelper.floor(Math.max(1.0F, this.field_13076.stepHeight));
		}

		BlockPos blockPos = new BlockPos(pathNode.posX, pathNode.posY, pathNode.posZ).down();
		double d = (double)pathNode.posY - (1.0 - this.field_13075.getBlockState(blockPos).getCollisionBox(this.field_13075, blockPos).maxY);
		PathNode pathNode3 = this.method_11946(pathNode.posX, pathNode.posY, pathNode.posZ + 1, j, d, Direction.SOUTH);
		PathNode pathNode4 = this.method_11946(pathNode.posX - 1, pathNode.posY, pathNode.posZ, j, d, Direction.WEST);
		PathNode pathNode5 = this.method_11946(pathNode.posX + 1, pathNode.posY, pathNode.posZ, j, d, Direction.EAST);
		PathNode pathNode6 = this.method_11946(pathNode.posX, pathNode.posY, pathNode.posZ - 1, j, d, Direction.NORTH);
		if (pathNode3 != null && !pathNode3.visited && pathNode3.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.visited && pathNode4.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode4;
		}

		if (pathNode5 != null && !pathNode5.visited && pathNode5.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode5;
		}

		if (pathNode6 != null && !pathNode6.visited && pathNode6.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode6;
		}

		boolean bl = pathNode6 == null || pathNode6.field_13074 == LandType.OPEN || pathNode6.field_13073 != 0.0F;
		boolean bl2 = pathNode3 == null || pathNode3.field_13074 == LandType.OPEN || pathNode3.field_13073 != 0.0F;
		boolean bl3 = pathNode5 == null || pathNode5.field_13074 == LandType.OPEN || pathNode5.field_13073 != 0.0F;
		boolean bl4 = pathNode4 == null || pathNode4.field_13074 == LandType.OPEN || pathNode4.field_13073 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode7 = this.method_11946(pathNode.posX - 1, pathNode.posY, pathNode.posZ - 1, j, d, Direction.NORTH);
			if (pathNode7 != null && !pathNode7.visited && pathNode7.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode7;
			}
		}

		if (bl && bl3) {
			PathNode pathNode8 = this.method_11946(pathNode.posX + 1, pathNode.posY, pathNode.posZ - 1, j, d, Direction.NORTH);
			if (pathNode8 != null && !pathNode8.visited && pathNode8.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode9 = this.method_11946(pathNode.posX - 1, pathNode.posY, pathNode.posZ + 1, j, d, Direction.SOUTH);
			if (pathNode9 != null && !pathNode9.visited && pathNode9.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode9;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode10 = this.method_11946(pathNode.posX + 1, pathNode.posY, pathNode.posZ + 1, j, d, Direction.SOUTH);
			if (pathNode10 != null && !pathNode10.visited && pathNode10.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode10;
			}
		}

		return i;
	}

	@Nullable
	private PathNode method_11946(int i, int j, int k, int l, double d, Direction direction) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockPos blockPos2 = blockPos.down();
		double e = (double)j - (1.0 - this.field_13075.getBlockState(blockPos2).getCollisionBox(this.field_13075, blockPos2).maxY);
		if (e - d > 1.125) {
			return null;
		} else {
			LandType landType = this.method_11947(this.field_13076, i, j, k);
			float f = this.field_13076.method_13075(landType);
			double g = (double)this.field_13076.width / 2.0;
			if (f >= 0.0F) {
				pathNode = this.method_11912(i, j, k);
				pathNode.field_13074 = landType;
				pathNode.field_13073 = Math.max(pathNode.field_13073, f);
			}

			if (landType == LandType.WALKABLE) {
				return pathNode;
			} else {
				if (pathNode == null && l > 0 && landType != LandType.FENCE && landType != LandType.TRAPDOOR) {
					pathNode = this.method_11946(i, j + 1, k, l - 1, d, direction);
					if (pathNode != null && (pathNode.field_13074 == LandType.OPEN || pathNode.field_13074 == LandType.WALKABLE) && this.field_13076.width < 1.0F) {
						double h = (double)(i - direction.getOffsetX()) + 0.5;
						double m = (double)(k - direction.getOffsetZ()) + 0.5;
						Box box = new Box(h - g, (double)j + 0.001, m - g, h + g, (double)((float)j + this.field_13076.height), m + g);
						Box box2 = this.field_13075.getBlockState(blockPos).getCollisionBox(this.field_13075, blockPos);
						Box box3 = box.stretch(0.0, box2.maxY - 0.002, 0.0);
						if (this.field_13076.world.method_11488(box3)) {
							pathNode = null;
						}
					}
				}

				if (landType == LandType.OPEN) {
					Box box4 = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.field_13076.height), (double)k + g + 0.5
					);
					if (this.field_13076.world.method_11488(box4)) {
						return null;
					}

					if (this.field_13076.width >= 1.0F) {
						LandType landType2 = this.method_11947(this.field_13076, i, j - 1, k);
						if (landType2 == LandType.BLOCKED) {
							pathNode = this.method_11912(i, j, k);
							pathNode.field_13074 = LandType.WALKABLE;
							pathNode.field_13073 = Math.max(pathNode.field_13073, f);
							return pathNode;
						}
					}

					int n = 0;

					while (j > 0 && landType == LandType.OPEN) {
						j--;
						if (n++ >= this.field_13076.getSafeFallDistance()) {
							return null;
						}

						landType = this.method_11947(this.field_13076, i, j, k);
						f = this.field_13076.method_13075(landType);
						if (landType != LandType.OPEN && f >= 0.0F) {
							pathNode = this.method_11912(i, j, k);
							pathNode.field_13074 = landType;
							pathNode.field_13073 = Math.max(pathNode.field_13073, f);
							break;
						}

						if (f < 0.0F) {
							return null;
						}
					}
				}

				return pathNode;
			}
		}
	}

	@Override
	public LandType method_11914(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<LandType> enumSet = EnumSet.noneOf(LandType.class);
		LandType landType = LandType.BLOCKED;
		double d = (double)mobEntity.width / 2.0;
		BlockPos blockPos = new BlockPos(mobEntity);
		landType = this.method_14446(blockView, i, j, k, l, m, n, bl, bl2, enumSet, landType, blockPos);
		if (enumSet.contains(LandType.FENCE)) {
			return LandType.FENCE;
		} else {
			LandType landType2 = LandType.BLOCKED;

			for (LandType landType3 : enumSet) {
				if (mobEntity.method_13075(landType3) < 0.0F) {
					return landType3;
				}

				if (mobEntity.method_13075(landType3) >= mobEntity.method_13075(landType2)) {
					landType2 = landType3;
				}
			}

			return landType == LandType.OPEN && mobEntity.method_13075(landType2) == 0.0F ? LandType.OPEN : landType2;
		}
	}

	public LandType method_14446(
		BlockView blockView, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, EnumSet<LandType> enumSet, LandType landType, BlockPos blockPos
	) {
		for (int o = 0; o < l; o++) {
			for (int p = 0; p < m; p++) {
				for (int q = 0; q < n; q++) {
					int r = o + i;
					int s = p + j;
					int t = q + k;
					LandType landType2 = this.method_11913(blockView, r, s, t);
					if (landType2 == LandType.DOOR_WOOD_CLOSED && bl && bl2) {
						landType2 = LandType.WALKABLE;
					}

					if (landType2 == LandType.DOOR_OPEN && !bl2) {
						landType2 = LandType.BLOCKED;
					}

					if (landType2 == LandType.RAIL
						&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
						&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
						landType2 = LandType.FENCE;
					}

					if (o == 0 && p == 0 && q == 0) {
						landType = landType2;
					}

					enumSet.add(landType2);
				}
			}
		}

		return landType;
	}

	private LandType method_11948(MobEntity mobEntity, BlockPos blockPos) {
		return this.method_11947(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private LandType method_11947(MobEntity mobEntity, int i, int j, int k) {
		return this.method_11914(this.field_13075, i, j, k, mobEntity, this.field_13078, this.field_13079, this.field_13080, this.method_11922(), this.method_11920());
	}

	@Override
	public LandType method_11913(BlockView blockView, int i, int j, int k) {
		LandType landType = this.method_11949(blockView, i, j, k);
		if (landType == LandType.OPEN && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			LandType landType2 = this.method_11949(blockView, i, j - 1, k);
			landType = landType2 != LandType.WALKABLE && landType2 != LandType.OPEN && landType2 != LandType.WATER && landType2 != LandType.LAVA
				? LandType.WALKABLE
				: LandType.OPEN;
			if (landType2 == LandType.DAMAGE_FIRE || block == Blocks.MAGMA) {
				landType = LandType.DAMAGE_FIRE;
			}

			if (landType2 == LandType.DAMAGE_CACTUS) {
				landType = LandType.DAMAGE_CACTUS;
			}
		}

		return this.method_14447(blockView, i, j, k, landType);
	}

	public LandType method_14447(BlockView blockView, int i, int j, int k, LandType landType) {
		BlockPos.Pooled pooled = BlockPos.Pooled.get();
		if (landType == LandType.WALKABLE) {
			for (int l = -1; l <= 1; l++) {
				for (int m = -1; m <= 1; m++) {
					if (l != 0 || m != 0) {
						Block block = blockView.getBlockState(pooled.setPosition(l + i, j, m + k)).getBlock();
						if (block == Blocks.CACTUS) {
							landType = LandType.DANGER_CACTUS;
						} else if (block == Blocks.FIRE) {
							landType = LandType.DANGER_FIRE;
						}
					}
				}
			}
		}

		pooled.method_12576();
		return landType;
	}

	protected LandType method_11949(BlockView blockView, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Material material = blockState.getMaterial();
		if (material == Material.AIR) {
			return LandType.OPEN;
		} else if (block == Blocks.TRAPDOOR || block == Blocks.IRON_TRAPDOOR || block == Blocks.LILY_PAD) {
			return LandType.TRAPDOOR;
		} else if (block == Blocks.FIRE) {
			return LandType.DAMAGE_FIRE;
		} else if (block == Blocks.CACTUS) {
			return LandType.DAMAGE_CACTUS;
		} else if (block instanceof DoorBlock && material == Material.WOOD && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return LandType.DOOR_WOOD_CLOSED;
		} else if (block instanceof DoorBlock && material == Material.IRON && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return LandType.DOOR_IRON_CLOSED;
		} else if (block instanceof DoorBlock && (Boolean)blockState.get(DoorBlock.OPEN)) {
			return LandType.DOOR_OPEN;
		} else if (block instanceof AbstractRailBlock) {
			return LandType.RAIL;
		} else if (!(block instanceof FenceBlock)
			&& !(block instanceof WallBlock)
			&& (!(block instanceof FenceGateBlock) || (Boolean)blockState.get(FenceGateBlock.OPEN))) {
			if (material == Material.WATER) {
				return LandType.WATER;
			} else if (material == Material.LAVA) {
				return LandType.LAVA;
			} else {
				return block.blocksMovement(blockView, blockPos) ? LandType.OPEN : LandType.BLOCKED;
			}
		} else {
			return LandType.FENCE;
		}
	}
}
