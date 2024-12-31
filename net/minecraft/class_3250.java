package net.minecraft;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class class_3250 extends LandPathNodeMaker {
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
		} else {
			i = MathHelper.floor(this.field_13076.getBoundingBox().minY + 0.5);
		}

		BlockPos blockPos = new BlockPos(this.field_13076);
		LandType landType = this.method_14444(this.field_13076, blockPos.getX(), i, blockPos.getZ());
		if (this.field_13076.method_13075(landType) < 0.0F) {
			Set<BlockPos> set = Sets.newHashSet();
			set.add(new BlockPos(this.field_13076.getBoundingBox().minX, (double)i, this.field_13076.getBoundingBox().minZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().minX, (double)i, this.field_13076.getBoundingBox().maxZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().maxX, (double)i, this.field_13076.getBoundingBox().minZ));
			set.add(new BlockPos(this.field_13076.getBoundingBox().maxX, (double)i, this.field_13076.getBoundingBox().maxZ));

			for (BlockPos blockPos2 : set) {
				LandType landType2 = this.method_14445(this.field_13076, blockPos2);
				if (this.field_13076.method_13075(landType2) >= 0.0F) {
					return super.method_11912(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
				}
			}
		}

		return super.method_11912(blockPos.getX(), i, blockPos.getZ());
	}

	@Override
	public PathNode method_11911(double d, double e, double f) {
		return super.method_11912(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
	}

	@Override
	public int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;
		PathNode pathNode3 = this.method_11912(pathNode.posX, pathNode.posY, pathNode.posZ + 1);
		PathNode pathNode4 = this.method_11912(pathNode.posX - 1, pathNode.posY, pathNode.posZ);
		PathNode pathNode5 = this.method_11912(pathNode.posX + 1, pathNode.posY, pathNode.posZ);
		PathNode pathNode6 = this.method_11912(pathNode.posX, pathNode.posY, pathNode.posZ - 1);
		PathNode pathNode7 = this.method_11912(pathNode.posX, pathNode.posY + 1, pathNode.posZ);
		PathNode pathNode8 = this.method_11912(pathNode.posX, pathNode.posY - 1, pathNode.posZ);
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

		boolean bl = pathNode6 == null || pathNode6.field_13073 != 0.0F;
		boolean bl2 = pathNode3 == null || pathNode3.field_13073 != 0.0F;
		boolean bl3 = pathNode5 == null || pathNode5.field_13073 != 0.0F;
		boolean bl4 = pathNode4 == null || pathNode4.field_13073 != 0.0F;
		boolean bl5 = pathNode7 == null || pathNode7.field_13073 != 0.0F;
		boolean bl6 = pathNode8 == null || pathNode8.field_13073 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode9 = this.method_11912(pathNode.posX - 1, pathNode.posY, pathNode.posZ - 1);
			if (pathNode9 != null && !pathNode9.visited && pathNode9.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode9;
			}
		}

		if (bl && bl3) {
			PathNode pathNode10 = this.method_11912(pathNode.posX + 1, pathNode.posY, pathNode.posZ - 1);
			if (pathNode10 != null && !pathNode10.visited && pathNode10.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode10;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode11 = this.method_11912(pathNode.posX - 1, pathNode.posY, pathNode.posZ + 1);
			if (pathNode11 != null && !pathNode11.visited && pathNode11.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode11;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode12 = this.method_11912(pathNode.posX + 1, pathNode.posY, pathNode.posZ + 1);
			if (pathNode12 != null && !pathNode12.visited && pathNode12.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode12;
			}
		}

		if (bl && bl5) {
			PathNode pathNode13 = this.method_11912(pathNode.posX, pathNode.posY + 1, pathNode.posZ - 1);
			if (pathNode13 != null && !pathNode13.visited && pathNode13.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode13;
			}
		}

		if (bl2 && bl5) {
			PathNode pathNode14 = this.method_11912(pathNode.posX, pathNode.posY + 1, pathNode.posZ + 1);
			if (pathNode14 != null && !pathNode14.visited && pathNode14.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode14;
			}
		}

		if (bl3 && bl5) {
			PathNode pathNode15 = this.method_11912(pathNode.posX + 1, pathNode.posY + 1, pathNode.posZ);
			if (pathNode15 != null && !pathNode15.visited && pathNode15.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode15;
			}
		}

		if (bl4 && bl5) {
			PathNode pathNode16 = this.method_11912(pathNode.posX - 1, pathNode.posY + 1, pathNode.posZ);
			if (pathNode16 != null && !pathNode16.visited && pathNode16.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode16;
			}
		}

		if (bl && bl6) {
			PathNode pathNode17 = this.method_11912(pathNode.posX, pathNode.posY - 1, pathNode.posZ - 1);
			if (pathNode17 != null && !pathNode17.visited && pathNode17.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode17;
			}
		}

		if (bl2 && bl6) {
			PathNode pathNode18 = this.method_11912(pathNode.posX, pathNode.posY - 1, pathNode.posZ + 1);
			if (pathNode18 != null && !pathNode18.visited && pathNode18.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode18;
			}
		}

		if (bl3 && bl6) {
			PathNode pathNode19 = this.method_11912(pathNode.posX + 1, pathNode.posY - 1, pathNode.posZ);
			if (pathNode19 != null && !pathNode19.visited && pathNode19.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode19;
			}
		}

		if (bl4 && bl6) {
			PathNode pathNode20 = this.method_11912(pathNode.posX - 1, pathNode.posY - 1, pathNode.posZ);
			if (pathNode20 != null && !pathNode20.visited && pathNode20.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode20;
			}
		}

		return i;
	}

	@Nullable
	@Override
	protected PathNode method_11912(int i, int j, int k) {
		PathNode pathNode = null;
		LandType landType = this.method_14444(this.field_13076, i, j, k);
		float f = this.field_13076.method_13075(landType);
		if (f >= 0.0F) {
			pathNode = super.method_11912(i, j, k);
			pathNode.field_13074 = landType;
			pathNode.field_13073 = Math.max(pathNode.field_13073, f);
			if (landType == LandType.WALKABLE) {
				pathNode.field_13073++;
			}
		}

		return landType != LandType.OPEN && landType != LandType.WALKABLE ? pathNode : pathNode;
	}

	@Override
	public LandType method_11914(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<LandType> enumSet = EnumSet.noneOf(LandType.class);
		LandType landType = LandType.BLOCKED;
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

	@Override
	public LandType method_11913(BlockView blockView, int i, int j, int k) {
		LandType landType = this.method_11949(blockView, i, j, k);
		if (landType == LandType.OPEN && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			LandType landType2 = this.method_11949(blockView, i, j - 1, k);
			if (landType2 == LandType.DAMAGE_FIRE || block == Blocks.MAGMA || landType2 == LandType.LAVA) {
				landType = LandType.DAMAGE_FIRE;
			} else if (landType2 == LandType.DAMAGE_CACTUS) {
				landType = LandType.DAMAGE_CACTUS;
			} else {
				landType = landType2 != LandType.WALKABLE && landType2 != LandType.OPEN && landType2 != LandType.WATER ? LandType.WALKABLE : LandType.OPEN;
			}
		}

		return this.method_14447(blockView, i, j, k, landType);
	}

	private LandType method_14445(MobEntity mobEntity, BlockPos blockPos) {
		return this.method_14444(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private LandType method_14444(MobEntity mobEntity, int i, int j, int k) {
		return this.method_11914(this.field_13075, i, j, k, mobEntity, this.field_13078, this.field_13079, this.field_13080, this.method_11922(), this.method_11920());
	}
}
