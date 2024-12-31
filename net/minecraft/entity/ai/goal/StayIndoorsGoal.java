package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoor;

public class StayIndoorsGoal extends Goal {
	private final PathAwareEntity entity;
	private VillageDoor door;
	private int x = -1;
	private int y = -1;

	public StayIndoorsGoal(PathAwareEntity pathAwareEntity) {
		this.entity = pathAwareEntity;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		BlockPos blockPos = new BlockPos(this.entity);
		if ((!this.entity.world.isDay() || this.entity.world.isRaining() && !this.entity.world.getBiome(blockPos).method_3830())
			&& !this.entity.world.dimension.hasNoSkylight()) {
			if (this.entity.getRandom().nextInt(50) != 0) {
				return false;
			} else if (this.x != -1 && this.entity.squaredDistanceTo((double)this.x, this.entity.y, (double)this.y) < 4.0) {
				return false;
			} else {
				Village village = this.entity.world.getVillageState().method_11062(blockPos, 14);
				if (village == null) {
					return false;
				} else {
					this.door = village.method_11056(blockPos);
					return this.door != null;
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.entity.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.x = -1;
		BlockPos blockPos = this.door.getPos2();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (this.entity.squaredDistanceTo(blockPos) > 256.0) {
			Vec3d vec3d = RandomVectorGenerator.method_2800(this.entity, 14, 3, new Vec3d((double)i + 0.5, (double)j, (double)k + 0.5));
			if (vec3d != null) {
				this.entity.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			}
		} else {
			this.entity.getNavigation().startMovingTo((double)i + 0.5, (double)j, (double)k + 0.5, 1.0);
		}
	}

	@Override
	public void stop() {
		this.x = this.door.getPos2().getX();
		this.y = this.door.getPos2().getZ();
		this.door = null;
	}
}
