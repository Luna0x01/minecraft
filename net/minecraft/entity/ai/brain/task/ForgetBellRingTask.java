package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class ForgetBellRingTask extends Task<LivingEntity> {
	private final int distance;
	private final int maxHiddenTicks;
	private int hiddenTicks;

	public ForgetBellRingTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_19008, MemoryModuleState.field_18456, MemoryModuleType.field_19009, MemoryModuleState.field_18456));
		this.maxHiddenTicks = i * 20;
		this.hiddenTicks = 0;
		this.distance = j;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.field_19009);
		boolean bl = (Long)optional.get() + 300L <= l;
		if (this.hiddenTicks <= this.maxHiddenTicks && !bl) {
			BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.field_19008).get()).getPos();
			if (blockPos.isWithinDistance(new BlockPos(livingEntity), (double)(this.distance + 1))) {
				this.hiddenTicks++;
			}
		} else {
			brain.forget(MemoryModuleType.field_19009);
			brain.forget(MemoryModuleType.field_19008);
			brain.refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
			this.hiddenTicks = 0;
		}
	}
}
