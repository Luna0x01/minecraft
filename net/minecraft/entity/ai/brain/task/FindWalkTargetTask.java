package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask extends Task<MobEntityWithAi> {
	private final float walkSpeed;
	private final int maxHorizontalDistance;
	private final int maxVerticalDistance;

	public FindWalkTargetTask(float f) {
		this(f, 10, 7);
	}

	public FindWalkTargetTask(float f, int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.walkSpeed = f;
		this.maxHorizontalDistance = i;
		this.maxVerticalDistance = j;
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		BlockPos blockPos = new BlockPos(mobEntityWithAi);
		if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
			this.updateWalkTarget(mobEntityWithAi);
		} else {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
			ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
			if (chunkSectionPos2 != chunkSectionPos) {
				this.updateWalkTarget(mobEntityWithAi, chunkSectionPos2);
			} else {
				this.updateWalkTarget(mobEntityWithAi);
			}
		}
	}

	private void updateWalkTarget(MobEntityWithAi mobEntityWithAi, ChunkSectionPos chunkSectionPos) {
		Optional<Vec3d> optional = Optional.ofNullable(
			TargetFinder.findTargetTowards(mobEntityWithAi, this.maxHorizontalDistance, this.maxVerticalDistance, new Vec3d(chunkSectionPos.getCenterPos()))
		);
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}

	private void updateWalkTarget(MobEntityWithAi mobEntityWithAi) {
		Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(mobEntityWithAi, this.maxHorizontalDistance, this.maxVerticalDistance));
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
	}
}
