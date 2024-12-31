package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimNavigation extends EntityNavigation {
	public SwimNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createNavigator() {
		return new PathNodeNavigator(new WaterPathNodeMaker());
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.isInLiquid();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.mob.x, this.mob.y + (double)this.mob.height * 0.5, this.mob.z);
	}

	@Override
	protected void continueFollowingPath() {
		Vec3d vec3d = this.getPos();
		float f = this.mob.width * this.mob.width;
		int i = 6;
		if (vec3d.squaredDistanceTo(this.currentPath.getNodePosition(this.mob, this.currentPath.getCurrentNode())) < (double)f) {
			this.currentPath.next();
		}

		for (int j = Math.min(this.currentPath.getCurrentNode() + i, this.currentPath.getNodeCount() - 1); j > this.currentPath.getCurrentNode(); j--) {
			Vec3d vec3d2 = this.currentPath.getNodePosition(this.mob, j);
			if (!(vec3d2.squaredDistanceTo(vec3d) > 36.0) && this.canPathDirectlyThrough(vec3d, vec3d2, 0, 0, 0)) {
				this.currentPath.setCurrentNode(j);
				break;
			}
		}

		this.checkTimeouts(vec3d);
	}

	@Override
	protected void adjustPath() {
		super.adjustPath();
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		BlockHitResult blockHitResult = this.world.rayTrace(origin, new Vec3d(target.x, target.y + (double)this.mob.height * 0.5, target.z), false, true, false);
		return blockHitResult == null || blockHitResult.type == BlockHitResult.Type.MISS;
	}
}
