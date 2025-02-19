package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NoPenaltyTargeting {
	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
			return tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end, double angleRange) {
		Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, angleRange);
			return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	@Nullable
	public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d direction) {
		Vec3d vec3d = entity.getPos().subtract(direction);
		boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
		return FuzzyPositions.guessBestPathTarget(entity, () -> {
			BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, (float) (Math.PI / 2));
			return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
		});
	}

	@Nullable
	private static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos fuzz) {
		BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), fuzz);
		return !NavigationConditions.isHeightInvalid(blockPos, entity)
				&& !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos)
				&& !NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)
				&& !NavigationConditions.hasPathfindingPenalty(entity, blockPos)
			? blockPos
			: null;
	}
}
