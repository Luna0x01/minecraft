package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowGroupLeaderGoal;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class SchoolableFishEntity extends FishEntity {
	private SchoolableFishEntity leader;
	private int groupSize = 1;

	public SchoolableFishEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(5, new FollowGroupLeaderGoal(this));
	}

	@Override
	public int getLimitPerChunk() {
		return this.getMaxGroupSize();
	}

	public int getMaxGroupSize() {
		return super.getLimitPerChunk();
	}

	@Override
	protected boolean isIndependent() {
		return !this.hasLeader();
	}

	public boolean hasLeader() {
		return this.leader != null && this.leader.isAlive();
	}

	public SchoolableFishEntity joinGroupOf(SchoolableFishEntity leaderFish) {
		this.leader = leaderFish;
		leaderFish.growGroupSize();
		return leaderFish;
	}

	public void leaveGroup() {
		this.leader.shrinkGroupSize();
		this.leader = null;
	}

	private void growGroupSize() {
		this.groupSize++;
	}

	private void shrinkGroupSize() {
		this.groupSize--;
	}

	public boolean canFitMoreFishInGroup() {
		return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.hasOtherFishInGroup() && this.world.random.nextInt(200) == 1) {
			List<FishEntity> list = this.world.getEntitiesInBox(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
			if (list.size() <= 1) {
				this.groupSize = 1;
			}
		}
	}

	public boolean hasOtherFishInGroup() {
		return this.groupSize > 1;
	}

	public boolean isCloseEnoughToLeader() {
		return this.squaredDistanceTo(this.leader) <= 121.0;
	}

	public void moveToLeader() {
		if (this.hasLeader()) {
			this.getNavigation().startMovingTo(this.leader, 1.0);
		}
	}

	public void bringFishTogether(Stream<SchoolableFishEntity> stream) {
		stream.limit((long)(this.getMaxGroupSize() - this.groupSize)).filter(fish -> fish != this).forEach(fish -> fish.joinGroupOf(this));
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		super.initialize(difficulty, entityData, nbt);
		if (entityData == null) {
			entityData = new SchoolableFishEntity.FishEntityData(this);
		} else {
			this.joinGroupOf(((SchoolableFishEntity.FishEntityData)entityData).leader);
		}

		return entityData;
	}

	public static class FishEntityData implements EntityData {
		public final SchoolableFishEntity leader;

		public FishEntityData(SchoolableFishEntity schoolableFishEntity) {
			this.leader = schoolableFishEntity;
		}
	}
}
