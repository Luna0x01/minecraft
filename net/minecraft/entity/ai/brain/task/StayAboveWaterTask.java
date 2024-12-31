package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask extends Task<MobEntity> {
	private final float minWaterHeight;
	private final float chance;

	public StayAboveWaterTask(float f, float g) {
		super(ImmutableMap.of());
		this.minWaterHeight = f;
		this.chance = g;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isTouchingWater() && mobEntity.getWaterHeight() > (double)this.minWaterHeight || mobEntity.isInLava();
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.shouldRun(serverWorld, mobEntity);
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRandom().nextFloat() < this.chance) {
			mobEntity.getJumpControl().setActive();
		}
	}
}
