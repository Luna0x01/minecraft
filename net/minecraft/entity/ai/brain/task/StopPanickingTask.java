package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class StopPanickingTask extends Task<VillagerEntity> {
	public StopPanickingTask() {
		super(ImmutableMap.of());
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		boolean bl = PanicTask.wasHurt(villagerEntity) || PanicTask.isHostileNearby(villagerEntity) || wasHurtByNearbyEntity(villagerEntity);
		if (!bl) {
			villagerEntity.getBrain().forget(MemoryModuleType.field_18451);
			villagerEntity.getBrain().forget(MemoryModuleType.field_18452);
			villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
		}
	}

	private static boolean wasHurtByNearbyEntity(VillagerEntity villagerEntity) {
		return villagerEntity.getBrain()
			.getOptionalMemory(MemoryModuleType.field_18452)
			.filter(livingEntity -> livingEntity.squaredDistanceTo(villagerEntity) <= 36.0)
			.isPresent();
	}
}
