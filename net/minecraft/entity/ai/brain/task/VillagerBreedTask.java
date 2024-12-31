package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask extends Task<VillagerEntity> {
	private long breedEndTime;

	public VillagerBreedTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18448, MemoryModuleState.field_18456, MemoryModuleType.field_18442, MemoryModuleState.field_18456), 350, 350);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.isReadyToBreed(villagerEntity);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return l <= this.breedEndTime && this.isReadyToBreed(villagerEntity);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.getBreedTarget(villagerEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
		serverWorld.sendEntityStatus(villagerEntity2, (byte)18);
		serverWorld.sendEntityStatus(villagerEntity, (byte)18);
		int i = 275 + villagerEntity.getRandom().nextInt(50);
		this.breedEndTime = l + (long)i;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.getBreedTarget(villagerEntity);
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
			if (l >= this.breedEndTime) {
				villagerEntity.eatForBreeding();
				villagerEntity2.eatForBreeding();
				this.goHome(serverWorld, villagerEntity, villagerEntity2);
			} else if (villagerEntity.getRandom().nextInt(35) == 0) {
				serverWorld.sendEntityStatus(villagerEntity2, (byte)12);
				serverWorld.sendEntityStatus(villagerEntity, (byte)12);
			}
		}
	}

	private void goHome(ServerWorld serverWorld, VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		Optional<BlockPos> optional = this.getReachableHome(serverWorld, villagerEntity);
		if (!optional.isPresent()) {
			serverWorld.sendEntityStatus(villagerEntity2, (byte)13);
			serverWorld.sendEntityStatus(villagerEntity, (byte)13);
		} else {
			Optional<VillagerEntity> optional2 = this.createChild(villagerEntity, villagerEntity2);
			if (optional2.isPresent()) {
				this.setChildHome(serverWorld, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
			} else {
				serverWorld.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
				DebugRendererInfoManager.sendPointOfInterest(serverWorld, (BlockPos)optional.get());
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18448);
	}

	private VillagerEntity getBreedTarget(VillagerEntity villagerEntity) {
		return (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18448).get();
	}

	private boolean isReadyToBreed(VillagerEntity villagerEntity) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.field_18448).isPresent()) {
			return false;
		} else {
			VillagerEntity villagerEntity2 = this.getBreedTarget(villagerEntity);
			return LookTargetUtil.canSee(brain, MemoryModuleType.field_18448, EntityType.field_6077)
				&& villagerEntity.isReadyToBreed()
				&& villagerEntity2.isReadyToBreed();
		}
	}

	private Optional<BlockPos> getReachableHome(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return serverWorld.getPointOfInterestStorage()
			.getPosition(
				PointOfInterestType.field_18517.getCompletionCondition(), blockPos -> this.canReachHome(villagerEntity, blockPos), new BlockPos(villagerEntity), 48
			);
	}

	private boolean canReachHome(VillagerEntity villagerEntity, BlockPos blockPos) {
		Path path = villagerEntity.getNavigation().findPathTo(blockPos, PointOfInterestType.field_18517.getSearchDistance());
		return path != null && path.reachesTarget();
	}

	private Optional<VillagerEntity> createChild(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		VillagerEntity villagerEntity3 = villagerEntity.createChild(villagerEntity2);
		if (villagerEntity3 == null) {
			return Optional.empty();
		} else {
			villagerEntity.setBreedingAge(6000);
			villagerEntity2.setBreedingAge(6000);
			villagerEntity3.setBreedingAge(-24000);
			villagerEntity3.refreshPositionAndAngles(villagerEntity.getX(), villagerEntity.getY(), villagerEntity.getZ(), 0.0F, 0.0F);
			villagerEntity.world.spawnEntity(villagerEntity3);
			villagerEntity.world.sendEntityStatus(villagerEntity3, (byte)12);
			return Optional.of(villagerEntity3);
		}
	}

	private void setChildHome(ServerWorld serverWorld, VillagerEntity villagerEntity, BlockPos blockPos) {
		GlobalPos globalPos = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
		villagerEntity.getBrain().putMemory(MemoryModuleType.field_18438, globalPos);
	}
}
