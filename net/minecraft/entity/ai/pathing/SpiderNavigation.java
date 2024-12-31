package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpiderNavigation extends MobNavigation {
	private BlockPos targetPos;

	public SpiderNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	public Path findPathTo(BlockPos pos) {
		this.targetPos = pos;
		return super.findPathTo(pos);
	}

	@Override
	public Path findPathTo(Entity entity) {
		this.targetPos = new BlockPos(entity);
		return super.findPathTo(entity);
	}

	@Override
	public boolean startMovingTo(Entity entity, double speed) {
		Path path = this.findPathTo(entity);
		if (path != null) {
			return this.startMovingAlong(path, speed);
		} else {
			this.targetPos = new BlockPos(entity);
			this.speed = speed;
			return true;
		}
	}

	@Override
	public void tick() {
		if (!this.isIdle()) {
			super.tick();
		} else {
			if (this.targetPos != null) {
				double d = (double)(this.mob.width * this.mob.width);
				if (!(this.mob.squaredDistanceToCenter(this.targetPos) < d)
					&& (
						!(this.mob.y > (double)this.targetPos.getY())
							|| !(this.mob.squaredDistanceToCenter(new BlockPos(this.targetPos.getX(), MathHelper.floor(this.mob.y), this.targetPos.getZ())) < d)
					)) {
					this.mob.getMotionHelper().moveTo((double)this.targetPos.getX(), (double)this.targetPos.getY(), (double)this.targetPos.getZ(), this.speed);
				} else {
					this.targetPos = null;
				}
			}
		}
	}
}
