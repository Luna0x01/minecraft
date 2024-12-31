package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GoToNearbyEntityTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<? extends Entity> entityMemory;
	private final float speed;

	public GoToNearbyEntityTask(MemoryModuleType<? extends Entity> memoryModuleType, float f) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457, memoryModuleType, MemoryModuleState.field_18456));
		this.entityMemory = memoryModuleType;
		this.speed = f;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Entity entity = (Entity)mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
		return mobEntityWithAi.squaredDistanceTo(entity) < 36.0;
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Entity entity = (Entity)mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
		setWalkTarget(mobEntityWithAi, entity, this.speed);
	}

	public static void setWalkTarget(MobEntityWithAi mobEntityWithAi, Entity entity, float f) {
		for (int i = 0; i < 10; i++) {
			Vec3d vec3d = TargetFinder.findGroundTargetAwayFrom(mobEntityWithAi, 16, 7, entity.getPos());
			if (vec3d != null) {
				mobEntityWithAi.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, f, 0));
				return;
			}
		}
	}
}
