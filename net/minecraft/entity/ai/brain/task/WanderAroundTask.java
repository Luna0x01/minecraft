package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderAroundTask extends Task<MobEntity> {
	@Nullable
	private Path path;
	@Nullable
	private BlockPos lookTargetPos;
	private float speed;
	private int pathUpdateCountdownTicks;

	public WanderAroundTask(int i) {
		super(ImmutableMap.of(MemoryModuleType.field_18449, MemoryModuleState.field_18457, MemoryModuleType.field_18445, MemoryModuleState.field_18456), i);
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		Brain<?> brain = mobEntity.getBrain();
		WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.field_18445).get();
		if (!this.hasReached(mobEntity, walkTarget) && this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
			this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
			return true;
		} else {
			brain.forget(MemoryModuleType.field_18445);
			return false;
		}
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (this.path != null && this.lookTargetPos != null) {
			Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18445);
			EntityNavigation entityNavigation = mobEntity.getNavigation();
			return !entityNavigation.isIdle() && optional.isPresent() && !this.hasReached(mobEntity, (WalkTarget)optional.get());
		} else {
			return false;
		}
	}

	protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getNavigation().stop();
		mobEntity.getBrain().forget(MemoryModuleType.field_18445);
		mobEntity.getBrain().forget(MemoryModuleType.field_18449);
		this.path = null;
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().putMemory(MemoryModuleType.field_18449, this.path);
		mobEntity.getNavigation().startMovingAlong(this.path, (double)this.speed);
		this.pathUpdateCountdownTicks = serverWorld.getRandom().nextInt(10);
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		this.pathUpdateCountdownTicks--;
		if (this.pathUpdateCountdownTicks <= 0) {
			Path path = mobEntity.getNavigation().getCurrentPath();
			Brain<?> brain = mobEntity.getBrain();
			if (this.path != path) {
				this.path = path;
				brain.putMemory(MemoryModuleType.field_18449, path);
			}

			if (path != null && this.lookTargetPos != null) {
				WalkTarget walkTarget = (WalkTarget)brain.getOptionalMemory(MemoryModuleType.field_18445).get();
				if (walkTarget.getLookTarget().getBlockPos().getSquaredDistance(this.lookTargetPos) > 4.0
					&& this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
					this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
					this.run(serverWorld, mobEntity, l);
				}
			}
		}
	}

	private boolean hasFinishedPath(MobEntity mobEntity, WalkTarget walkTarget, long l) {
		BlockPos blockPos = walkTarget.getLookTarget().getBlockPos();
		this.path = mobEntity.getNavigation().findPathTo(blockPos, 0);
		this.speed = walkTarget.getSpeed();
		if (!this.hasReached(mobEntity, walkTarget)) {
			Brain<?> brain = mobEntity.getBrain();
			boolean bl = this.path != null && this.path.reachesTarget();
			if (bl) {
				brain.setMemory(MemoryModuleType.field_19293, Optional.empty());
			} else if (!brain.hasMemoryModule(MemoryModuleType.field_19293)) {
				brain.putMemory(MemoryModuleType.field_19293, l);
			}

			if (this.path != null) {
				return true;
			}

			Vec3d vec3d = TargetFinder.findTargetTowards((MobEntityWithAi)mobEntity, 10, 7, new Vec3d(blockPos));
			if (vec3d != null) {
				this.path = mobEntity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
				return this.path != null;
			}
		}

		return false;
	}

	private boolean hasReached(MobEntity mobEntity, WalkTarget walkTarget) {
		return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(new BlockPos(mobEntity)) <= walkTarget.getCompletionRange();
	}
}
