package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_3473 extends WanderAroundGoal {
	public class_3473(PathAwareEntity pathAwareEntity, double d, int i) {
		super(pathAwareEntity, d, i);
	}

	@Nullable
	@Override
	protected Vec3d method_13954() {
		Vec3d vec3d = RandomVectorGenerator.method_2799(this.mob, 10, 7);
		int i = 0;

		while (
			vec3d != null
				&& !this.mob.world.getBlockState(new BlockPos(vec3d)).canPlaceAtSide(this.mob.world, new BlockPos(vec3d), BlockPlacementEnvironment.WATER)
				&& i++ < 10
		) {
			vec3d = RandomVectorGenerator.method_2799(this.mob, 10, 7);
		}

		return vec3d;
	}
}
