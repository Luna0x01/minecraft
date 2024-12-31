package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WalkHomeTask extends Task<LivingEntity> {
	private final float speed;
	private final Long2LongMap positionToExpiry = new Long2LongOpenHashMap();
	private int tries;
	private long expiryTimeLimit;

	public WalkHomeTask(float f) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457, MemoryModuleType.field_18438, MemoryModuleState.field_18457));
		this.speed = f;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		if (serverWorld.getTime() - this.expiryTimeLimit < 20L) {
			return false;
		} else {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.field_18517.getCompletionCondition(),
				blockPos -> true,
				new BlockPos(livingEntity),
				48,
				PointOfInterestStorage.OccupationStatus.field_18489
			);
			return optional.isPresent() && !(((BlockPos)optional.get()).getSquaredDistance(new BlockPos(mobEntityWithAi)) <= 4.0);
		}
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		this.tries = 0;
		this.expiryTimeLimit = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPosx -> {
			long lx = blockPosx.asLong();
			if (this.positionToExpiry.containsKey(lx)) {
				return false;
			} else if (++this.tries >= 5) {
				return false;
			} else {
				this.positionToExpiry.put(lx, this.expiryTimeLimit + 40L);
				return true;
			}
		};
		Stream<BlockPos> stream = pointOfInterestStorage.getPositions(
			PointOfInterestType.field_18517.getCompletionCondition(), predicate, new BlockPos(livingEntity), 48, PointOfInterestStorage.OccupationStatus.field_18489
		);
		Path path = mobEntityWithAi.getNavigation().findPathToAny(stream, PointOfInterestType.field_18517.getSearchDistance());
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos);
			if (optional.isPresent()) {
				livingEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(blockPos, this.speed, 1));
				DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos);
			}
		} else if (this.tries < 5) {
			this.positionToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTimeLimit);
		}
	}
}
