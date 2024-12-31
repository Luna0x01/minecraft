package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker extends class_2771 {
	private final boolean field_19719;

	public WaterPathNodeMaker(boolean bl) {
		this.field_19719 = bl;
	}

	@Override
	public PathNode method_11918() {
		return super.method_11912(
			MathHelper.floor(this.field_13076.getBoundingBox().minX),
			MathHelper.floor(this.field_13076.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.field_13076.getBoundingBox().minZ)
		);
	}

	@Override
	public PathNode method_11911(double d, double e, double f) {
		return super.method_11912(
			MathHelper.floor(d - (double)(this.field_13076.width / 2.0F)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.field_13076.width / 2.0F))
		);
	}

	@Override
	public int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode3 = this.method_11944(
				pathNode.posX + direction.getOffsetX(), pathNode.posY + direction.getOffsetY(), pathNode.posZ + direction.getOffsetZ()
			);
			if (pathNode3 != null && !pathNode3.visited && pathNode3.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode3;
			}
		}

		return i;
	}

	@Override
	public LandType method_11914(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		return this.method_11913(blockView, i, j, k);
	}

	@Override
	public LandType method_11913(BlockView blockView, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		FluidState fluidState = blockView.getFluidState(blockPos);
		BlockState blockState = blockView.getBlockState(blockPos);
		if (fluidState.isEmpty() && blockState.canPlaceAtSide(blockView, blockPos.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
			return LandType.BREACH;
		} else {
			return fluidState.matches(FluidTags.WATER) && blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.WATER)
				? LandType.WATER
				: LandType.BLOCKED;
		}
	}

	@Nullable
	private PathNode method_11944(int i, int j, int k) {
		LandType landType = this.method_11945(i, j, k);
		return (!this.field_19719 || landType != LandType.BREACH) && landType != LandType.WATER ? null : this.method_11912(i, j, k);
	}

	@Nullable
	@Override
	protected PathNode method_11912(int i, int j, int k) {
		PathNode pathNode = null;
		LandType landType = this.method_11913(this.field_13076.world, i, j, k);
		float f = this.field_13076.method_13075(landType);
		if (f >= 0.0F) {
			pathNode = super.method_11912(i, j, k);
			pathNode.field_13074 = landType;
			pathNode.field_13073 = Math.max(pathNode.field_13073, f);
			if (this.field_13075.getFluidState(new BlockPos(i, j, k)).isEmpty()) {
				pathNode.field_13073 += 8.0F;
			}
		}

		return landType == LandType.OPEN ? pathNode : pathNode;
	}

	private LandType method_11945(int i, int j, int k) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = i; l < i + this.field_13078; l++) {
			for (int m = j; m < j + this.field_13079; m++) {
				for (int n = k; n < k + this.field_13080; n++) {
					FluidState fluidState = this.field_13075.getFluidState(mutable.setPosition(l, m, n));
					BlockState blockState = this.field_13075.getBlockState(mutable.setPosition(l, m, n));
					if (fluidState.isEmpty() && blockState.canPlaceAtSide(this.field_13075, mutable.down(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
						return LandType.BREACH;
					}

					if (!fluidState.matches(FluidTags.WATER)) {
						return LandType.BLOCKED;
					}
				}
			}
		}

		BlockState blockState2 = this.field_13075.getBlockState(mutable);
		return blockState2.canPlaceAtSide(this.field_13075, mutable, BlockPlacementEnvironment.WATER) ? LandType.WATER : LandType.BLOCKED;
	}
}
