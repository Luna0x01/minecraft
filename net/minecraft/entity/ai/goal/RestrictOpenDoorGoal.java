package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoor;

public class RestrictOpenDoorGoal extends Goal {
	private final PathAwareEntity entity;
	private VillageDoor door;

	public RestrictOpenDoorGoal(PathAwareEntity pathAwareEntity) {
		this.entity = pathAwareEntity;
		if (!(pathAwareEntity.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (this.entity.world.isDay()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(this.entity);
			Village village = this.entity.world.getVillageState().method_11062(blockPos, 16);
			if (village == null) {
				return false;
			} else {
				this.door = village.method_11055(blockPos);
				return this.door == null ? false : (double)this.door.method_11045(blockPos) < 2.25;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.entity.world.isDay() ? false : !this.door.method_11051() && this.door.method_11046(new BlockPos(this.entity));
	}

	@Override
	public void start() {
		((MobNavigation)this.entity.getNavigation()).setCanPathThroughDoors(false);
		((MobNavigation)this.entity.getNavigation()).setCanEnterOpenDoors(false);
	}

	@Override
	public void stop() {
		((MobNavigation)this.entity.getNavigation()).setCanPathThroughDoors(true);
		((MobNavigation)this.entity.getNavigation()).setCanEnterOpenDoors(true);
		this.door = null;
	}

	@Override
	public void tick() {
		this.door.method_2810();
	}
}
