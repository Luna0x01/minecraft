package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolableFishEntity;

public class FollowGroupLeaderGoal extends Goal {
	private final SchoolableFishEntity field_16852;
	private int field_16853;
	private int field_16854;

	public FollowGroupLeaderGoal(SchoolableFishEntity schoolableFishEntity) {
		this.field_16852 = schoolableFishEntity;
		this.field_16854 = this.method_15681(schoolableFishEntity);
	}

	protected int method_15681(SchoolableFishEntity schoolableFishEntity) {
		return 200 + schoolableFishEntity.getRandom().nextInt(200) % 20;
	}

	@Override
	public boolean canStart() {
		if (this.field_16852.hasOtherFishInGroup()) {
			return false;
		} else if (this.field_16852.hasLeader()) {
			return true;
		} else if (this.field_16854 > 0) {
			this.field_16854--;
			return false;
		} else {
			this.field_16854 = this.method_15681(this.field_16852);
			Predicate<SchoolableFishEntity> predicate = schoolableFishEntityx -> schoolableFishEntityx.canFitMoreFishInGroup() || !schoolableFishEntityx.hasLeader();
			List<SchoolableFishEntity> list = this.field_16852
				.world
				.method_16325(this.field_16852.getClass(), this.field_16852.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
			SchoolableFishEntity schoolableFishEntity = (SchoolableFishEntity)list.stream()
				.filter(SchoolableFishEntity::canFitMoreFishInGroup)
				.findAny()
				.orElse(this.field_16852);
			schoolableFishEntity.bringFishTogether(list.stream().filter(schoolableFishEntityx -> !schoolableFishEntityx.hasLeader()));
			return this.field_16852.hasLeader();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_16852.hasLeader() && this.field_16852.isCloseEnoughToLeader();
	}

	@Override
	public void start() {
		this.field_16853 = 0;
	}

	@Override
	public void stop() {
		this.field_16852.leaveGroup();
	}

	@Override
	public void tick() {
		if (--this.field_16853 <= 0) {
			this.field_16853 = 10;
			this.field_16852.moveToLeader();
		}
	}
}
