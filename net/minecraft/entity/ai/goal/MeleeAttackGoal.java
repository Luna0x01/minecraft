package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MeleeAttackGoal extends Goal {
	World world;
	protected PathAwareEntity mob;
	int field_3534;
	double speed;
	boolean pauseWhenMobIdle;
	Path path;
	Class<? extends Entity> targetClass;
	private int updateCountdownTicks;
	private double targetX;
	private double targetY;
	private double targetZ;

	public MeleeAttackGoal(PathAwareEntity pathAwareEntity, Class<? extends Entity> class_, double d, boolean bl) {
		this(pathAwareEntity, d, bl);
		this.targetClass = class_;
	}

	public MeleeAttackGoal(PathAwareEntity pathAwareEntity, double d, boolean bl) {
		this.mob = pathAwareEntity;
		this.world = pathAwareEntity.world;
		this.speed = d;
		this.pauseWhenMobIdle = bl;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (this.targetClass != null && !this.targetClass.isAssignableFrom(livingEntity.getClass())) {
			return false;
		} else {
			this.path = this.mob.getNavigation().findPathTo(livingEntity);
			return this.path != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else {
			return !this.pauseWhenMobIdle ? !this.mob.getNavigation().isIdle() : this.mob.isInWalkTargetRange(new BlockPos(livingEntity));
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingAlong(this.path, this.speed);
		this.updateCountdownTicks = 0;
	}

	@Override
	public void stop() {
		this.mob.getNavigation().stop();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.mob.getTarget();
		this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
		double d = this.mob.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
		double e = this.getSquaredMaxAttackDistance(livingEntity);
		this.updateCountdownTicks--;
		if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity))
			&& this.updateCountdownTicks <= 0
			&& (
				this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
					|| livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
					|| this.mob.getRandom().nextFloat() < 0.05F
			)) {
			this.targetX = livingEntity.x;
			this.targetY = livingEntity.getBoundingBox().minY;
			this.targetZ = livingEntity.z;
			this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
			if (d > 1024.0) {
				this.updateCountdownTicks += 10;
			} else if (d > 256.0) {
				this.updateCountdownTicks += 5;
			}

			if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
				this.updateCountdownTicks += 15;
			}
		}

		this.field_3534 = Math.max(this.field_3534 - 1, 0);
		if (d <= e && this.field_3534 <= 0) {
			this.field_3534 = 20;
			if (this.mob.getStackInHand() != null) {
				this.mob.swingHand();
			}

			this.mob.tryAttack(livingEntity);
		}
	}

	protected double getSquaredMaxAttackDistance(LivingEntity entity) {
		return (double)(this.mob.width * 2.0F * this.mob.width * 2.0F + entity.width);
	}
}
