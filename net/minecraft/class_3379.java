package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_3379 extends class_3133 {
	public class_3379(PathAwareEntity pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@Nullable
	@Override
	protected Vec3d method_13954() {
		Vec3d vec3d = null;
		if (this.mob.isTouchingWater() || this.mob.method_15052()) {
			vec3d = RandomVectorGenerator.method_13959(this.mob, 15, 15);
		}

		if (this.mob.getRandom().nextFloat() >= this.field_15483) {
			vec3d = this.method_15089();
		}

		return vec3d == null ? super.method_13954() : vec3d;
	}

	@Nullable
	private Vec3d method_15089() {
		BlockPos blockPos = new BlockPos(this.mob);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (BlockPos blockPos2 : BlockPos.Mutable.mutableIterate(
			MathHelper.floor(this.mob.x - 3.0),
			MathHelper.floor(this.mob.y - 6.0),
			MathHelper.floor(this.mob.z - 3.0),
			MathHelper.floor(this.mob.x + 3.0),
			MathHelper.floor(this.mob.y + 6.0),
			MathHelper.floor(this.mob.z + 3.0)
		)) {
			if (!blockPos.equals(blockPos2)) {
				Block block = this.mob.world.getBlockState(mutable2.set(blockPos2).move(Direction.DOWN)).getBlock();
				boolean bl = block instanceof LeavesBlock || block == Blocks.LOG || block == Blocks.LOG2;
				if (bl && this.mob.world.isAir(blockPos2) && this.mob.world.isAir(mutable.set(blockPos2).move(Direction.UP))) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}
		}

		return null;
	}
}
