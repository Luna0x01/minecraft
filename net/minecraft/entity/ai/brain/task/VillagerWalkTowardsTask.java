package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask extends Task<VillagerEntity> {
	private final MemoryModuleType<GlobalPos> destination;
	private final float speed;
	private final int completionRange;
	private final int maxRange;
	private final int maxRunTime;

	public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j, int k) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_19293,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				memoryModuleType,
				MemoryModuleState.field_18456
			)
		);
		this.destination = memoryModuleType;
		this.speed = f;
		this.completionRange = i;
		this.maxRange = j;
		this.maxRunTime = k;
	}

	private void giveUp(VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		villagerEntity.releaseTicketFor(this.destination);
		brain.forget(this.destination);
		brain.putMemory(MemoryModuleType.field_19293, l);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		brain.getOptionalMemory(this.destination)
			.ifPresent(
				globalPos -> {
					if (this.shouldGiveUp(serverWorld, villagerEntity)) {
						this.giveUp(villagerEntity, l);
					} else if (this.exceedsMaxRange(serverWorld, villagerEntity, globalPos)) {
						Vec3d vec3d = null;
						int i = 0;

						for (int j = 1000;
							i < 1000 && (vec3d == null || this.exceedsMaxRange(serverWorld, villagerEntity, GlobalPos.create(villagerEntity.dimension, new BlockPos(vec3d))));
							i++
						) {
							vec3d = TargetFinder.findTargetTowards(villagerEntity, 15, 7, new Vec3d(globalPos.getPos()));
						}

						if (i == 1000) {
							this.giveUp(villagerEntity, l);
							return;
						}

						brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, this.speed, this.completionRange));
					} else if (!this.reachedDestination(serverWorld, villagerEntity, globalPos)) {
						brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(globalPos.getPos(), this.speed, this.completionRange));
					}
				}
			);
	}

	private boolean shouldGiveUp(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Optional<Long> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_19293);
		return optional.isPresent() ? serverWorld.getTime() - (Long)optional.get() > (long)this.maxRunTime : false;
	}

	private boolean exceedsMaxRange(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
		return globalPos.getDimension() != serverWorld.getDimension().getType()
			|| globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) > this.maxRange;
	}

	private boolean reachedDestination(ServerWorld serverWorld, VillagerEntity villagerEntity, GlobalPos globalPos) {
		return globalPos.getDimension() == serverWorld.getDimension().getType()
			&& globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) <= this.completionRange;
	}
}
