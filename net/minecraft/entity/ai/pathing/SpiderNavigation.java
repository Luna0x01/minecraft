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
	public PathMinHeap method_13108(BlockPos blockPos) {
		this.targetPos = blockPos;
		return super.method_13108(blockPos);
	}

	@Override
	public PathMinHeap method_13109(Entity entity) {
		this.targetPos = new BlockPos(entity);
		return super.method_13109(entity);
	}

	@Override
	public boolean startMovingTo(Entity entity, double speed) {
		PathMinHeap pathMinHeap = this.method_13109(entity);
		if (pathMinHeap != null) {
			return this.method_13107(pathMinHeap, speed);
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
