package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;

public class class_4065 extends LandPathNodeMaker {
	private float field_19720;
	private float field_19721;

	@Override
	public void method_11915(BlockView blockView, MobEntity mobEntity) {
		super.method_11915(blockView, mobEntity);
		mobEntity.method_13076(LandType.WATER, 0.0F);
		this.field_19720 = mobEntity.method_13075(LandType.WALKABLE);
		mobEntity.method_13076(LandType.WALKABLE, 6.0F);
		this.field_19721 = mobEntity.method_13075(LandType.WATER_BORDER);
		mobEntity.method_13076(LandType.WATER_BORDER, 4.0F);
	}

	@Override
	public void method_11910() {
		this.field_13076.method_13076(LandType.WALKABLE, this.field_19720);
		this.field_13076.method_13076(LandType.WATER_BORDER, this.field_19721);
		super.method_11910();
	}

	@Override
	public PathNode method_11918() {
		return this.method_11912(
			MathHelper.floor(this.field_13076.getBoundingBox().minX),
			MathHelper.floor(this.field_13076.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.field_13076.getBoundingBox().minZ)
		);
	}

	@Override
	public PathNode method_11911(double d, double e, double f) {
		return this.method_11912(MathHelper.floor(d), MathHelper.floor(e + 0.5), MathHelper.floor(f));
	}

	@Override
	public int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;
		int j = 1;
		BlockPos blockPos = new BlockPos(pathNode.posX, pathNode.posY, pathNode.posZ);
		double d = this.method_17912(blockPos);
		PathNode pathNode3 = this.method_17911(pathNode.posX, pathNode.posY, pathNode.posZ + 1, 1, d);
		PathNode pathNode4 = this.method_17911(pathNode.posX - 1, pathNode.posY, pathNode.posZ, 1, d);
		PathNode pathNode5 = this.method_17911(pathNode.posX + 1, pathNode.posY, pathNode.posZ, 1, d);
		PathNode pathNode6 = this.method_17911(pathNode.posX, pathNode.posY, pathNode.posZ - 1, 1, d);
		PathNode pathNode7 = this.method_17911(pathNode.posX, pathNode.posY + 1, pathNode.posZ, 0, d);
		PathNode pathNode8 = this.method_17911(pathNode.posX, pathNode.posY - 1, pathNode.posZ, 1, d);
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

		if (pathNode7 != null && !pathNode7.visited && pathNode7.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode7;
		}

		if (pathNode8 != null && !pathNode8.visited && pathNode8.getDistance(pathNode2) < f) {
			pathNodes[i++] = pathNode8;
		}

		boolean bl = pathNode6 == null || pathNode6.field_13074 == LandType.OPEN || pathNode6.field_13073 != 0.0F;
		boolean bl2 = pathNode3 == null || pathNode3.field_13074 == LandType.OPEN || pathNode3.field_13073 != 0.0F;
		boolean bl3 = pathNode5 == null || pathNode5.field_13074 == LandType.OPEN || pathNode5.field_13073 != 0.0F;
		boolean bl4 = pathNode4 == null || pathNode4.field_13074 == LandType.OPEN || pathNode4.field_13073 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode9 = this.method_17911(pathNode.posX - 1, pathNode.posY, pathNode.posZ - 1, 1, d);
			if (pathNode9 != null && !pathNode9.visited && pathNode9.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode9;
			}
		}

		if (bl && bl3) {
			PathNode pathNode10 = this.method_17911(pathNode.posX + 1, pathNode.posY, pathNode.posZ - 1, 1, d);
			if (pathNode10 != null && !pathNode10.visited && pathNode10.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode10;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode11 = this.method_17911(pathNode.posX - 1, pathNode.posY, pathNode.posZ + 1, 1, d);
			if (pathNode11 != null && !pathNode11.visited && pathNode11.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode11;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode12 = this.method_17911(pathNode.posX + 1, pathNode.posY, pathNode.posZ + 1, 1, d);
			if (pathNode12 != null && !pathNode12.visited && pathNode12.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode12;
			}
		}

		return i;
	}

	private double method_17912(BlockPos blockPos) {
		if (!this.field_13076.isTouchingWater()) {
			BlockPos blockPos2 = blockPos.down();
			VoxelShape voxelShape = this.field_13075.getBlockState(blockPos2).getCollisionShape(this.field_13075, blockPos2);
			return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
		} else {
			return (double)blockPos.getY() + 0.5;
		}
	}

	@Nullable
	private PathNode method_17911(int i, int j, int k, int l, double d) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(i, j, k);
		double e = this.method_17912(blockPos);
		if (e - d > 1.125) {
			return null;
		} else {
			LandType landType = this.method_11914(this.field_13075, i, j, k, this.field_13076, this.field_13078, this.field_13079, this.field_13080, false, false);
			float f = this.field_13076.method_13075(landType);
			double g = (double)this.field_13076.width / 2.0;
			if (f >= 0.0F) {
				pathNode = this.method_11912(i, j, k);
				pathNode.field_13074 = landType;
				pathNode.field_13073 = Math.max(pathNode.field_13073, f);
			}

			if (landType != LandType.WATER && landType != LandType.WALKABLE) {
				if (pathNode == null && l > 0 && landType != LandType.FENCE && landType != LandType.TRAPDOOR) {
					pathNode = this.method_17911(i, j + 1, k, l - 1, d);
				}

				if (landType == LandType.OPEN) {
					Box box = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.field_13076.height), (double)k + g + 0.5
					);
					if (!this.field_13076.world.method_16387(null, box)) {
						return null;
					}

					LandType landType2 = this.method_11914(this.field_13075, i, j - 1, k, this.field_13076, this.field_13078, this.field_13079, this.field_13080, false, false);
					if (landType2 == LandType.BLOCKED) {
						pathNode = this.method_11912(i, j, k);
						pathNode.field_13074 = LandType.WALKABLE;
						pathNode.field_13073 = Math.max(pathNode.field_13073, f);
						return pathNode;
					}

					if (landType2 == LandType.WATER) {
						pathNode = this.method_11912(i, j, k);
						pathNode.field_13074 = LandType.WATER;
						pathNode.field_13073 = Math.max(pathNode.field_13073, f);
						return pathNode;
					}

					int m = 0;

					while (j > 0 && landType == LandType.OPEN) {
						j--;
						if (m++ >= this.field_13076.getSafeFallDistance()) {
							return null;
						}

						landType = this.method_11914(this.field_13075, i, j, k, this.field_13076, this.field_13078, this.field_13079, this.field_13080, false, false);
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
			} else {
				if (j < this.field_13076.world.method_8483() - 10 && pathNode != null) {
					pathNode.field_13073++;
				}

				return pathNode;
			}
		}
	}

	@Override
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
					if (landType2 == LandType.RAIL
						&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
						&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
						landType2 = LandType.FENCE;
					}

					if (landType2 == LandType.DOOR_OPEN || landType2 == LandType.DOOR_WOOD_CLOSED || landType2 == LandType.DOOR_IRON_CLOSED) {
						landType2 = LandType.BLOCKED;
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

	@Override
	public LandType method_11913(BlockView blockView, int i, int j, int k) {
		LandType landType = this.method_11949(blockView, i, j, k);
		if (landType == LandType.WATER) {
			for (Direction direction : Direction.values()) {
				LandType landType2 = this.method_11949(blockView, i + direction.getOffsetX(), j + direction.getOffsetY(), k + direction.getOffsetZ());
				if (landType2 == LandType.BLOCKED) {
					return LandType.WATER_BORDER;
				}
			}

			return LandType.WATER;
		} else {
			if (landType == LandType.OPEN && j >= 1) {
				Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
				LandType landType3 = this.method_11949(blockView, i, j - 1, k);
				if (landType3 != LandType.WALKABLE && landType3 != LandType.OPEN && landType3 != LandType.LAVA) {
					landType = LandType.WALKABLE;
				} else {
					landType = LandType.OPEN;
				}

				if (landType3 == LandType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK) {
					landType = LandType.DAMAGE_FIRE;
				}

				if (landType3 == LandType.DAMAGE_CACTUS) {
					landType = LandType.DAMAGE_CACTUS;
				}
			}

			return this.method_14447(blockView, i, j, k, landType);
		}
	}
}
