package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FollowCustomerTask extends Task<VillagerEntity> {
	private final float speed;

	public FollowCustomerTask(float f) {
		super(
			ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458, MemoryModuleType.field_18446, MemoryModuleState.field_18458), Integer.MAX_VALUE
		);
		this.speed = f;
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		PlayerEntity playerEntity = villagerEntity.getCurrentCustomer();
		return villagerEntity.isAlive()
			&& playerEntity != null
			&& !villagerEntity.isTouchingWater()
			&& !villagerEntity.velocityModified
			&& villagerEntity.squaredDistanceTo(playerEntity) <= 16.0
			&& playerEntity.container != null;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.shouldRun(serverWorld, villagerEntity);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.update(villagerEntity);
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		brain.forget(MemoryModuleType.field_18445);
		brain.forget(MemoryModuleType.field_18446);
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.update(villagerEntity);
	}

	@Override
	protected boolean isTimeLimitExceeded(long l) {
		return false;
	}

	private void update(VillagerEntity villagerEntity) {
		EntityPosWrapper entityPosWrapper = new EntityPosWrapper(villagerEntity.getCurrentCustomer());
		Brain<?> brain = villagerEntity.getBrain();
		brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(entityPosWrapper, this.speed, 2));
		brain.putMemory(MemoryModuleType.field_18446, entityPosWrapper);
	}
}
