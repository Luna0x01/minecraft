package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoor;

public class MoveThroughVillageGoal extends Goal {
	private PathAwareEntity mob;
	private double speed;
	private Path path;
	private VillageDoor target;
	private boolean requiresNighttime;
	private List<VillageDoor> visitedTargets = Lists.newArrayList();

	public MoveThroughVillageGoal(PathAwareEntity pathAwareEntity, double d, boolean bl) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.requiresNighttime = bl;
		this.setCategoryBits(1);
		if (!(pathAwareEntity.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean canStart() {
		this.forgetOldTarget();
		if (this.requiresNighttime && this.mob.world.isDay()) {
			return false;
		} else {
			Village village = this.mob.world.getVillageState().method_11062(new BlockPos(this.mob), 0);
			if (village == null) {
				return false;
			} else {
				this.target = this.method_2761(village);
				if (this.target == null) {
					return false;
				} else {
					MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
					boolean bl = mobNavigation.canEnterOpenDoors();
					mobNavigation.setCanPathThroughDoors(false);
					this.path = mobNavigation.findPathTo(this.target.getPos1());
					mobNavigation.setCanPathThroughDoors(bl);
					if (this.path != null) {
						return true;
					} else {
						Vec3d vec3d = RandomVectorGenerator.method_2800(
							this.mob, 10, 7, new Vec3d((double)this.target.getPos1().getX(), (double)this.target.getPos1().getY(), (double)this.target.getPos1().getZ())
						);
						if (vec3d == null) {
							return false;
						} else {
							mobNavigation.setCanPathThroughDoors(false);
							this.path = this.mob.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z);
							mobNavigation.setCanPathThroughDoors(bl);
							return this.path != null;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.mob.getNavigation().isIdle()) {
			return false;
		} else {
			float f = this.mob.width + 4.0F;
			return this.mob.squaredDistanceTo(this.target.getPos1()) > (double)(f * f);
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingAlong(this.path, this.speed);
	}

	@Override
	public void stop() {
		if (this.mob.getNavigation().isIdle() || this.mob.squaredDistanceTo(this.target.getPos1()) < 16.0) {
			this.visitedTargets.add(this.target);
		}
	}

	private VillageDoor method_2761(Village village) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : village.getDoors()) {
			int j = villageDoor2.method_2806(MathHelper.floor(this.mob.x), MathHelper.floor(this.mob.y), MathHelper.floor(this.mob.z));
			if (j < i && !this.method_2760(villageDoor2)) {
				villageDoor = villageDoor2;
				i = j;
			}
		}

		return villageDoor;
	}

	private boolean method_2760(VillageDoor villageDoor) {
		for (VillageDoor villageDoor2 : this.visitedTargets) {
			if (villageDoor.getPos1().equals(villageDoor2.getPos1())) {
				return true;
			}
		}

		return false;
	}

	private void forgetOldTarget() {
		if (this.visitedTargets.size() > 15) {
			this.visitedTargets.remove(0);
		}
	}
}
