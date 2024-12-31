package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MeleeAttackGoal extends Goal {
	World world;
	protected PathAwareEntity mob;
	protected int field_3534;
	double speed;
	boolean pauseWhenMobIdle;
	PathMinHeap field_14581;
	private int updateCountdownTicks;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int field_14582 = 20;

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
		} else {
			this.field_14581 = this.mob.getNavigation().method_13109(livingEntity);
			return this.field_14581 != null
				? true
				: this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (!this.pauseWhenMobIdle) {
			return !this.mob.getNavigation().isIdle();
		} else {
			return !this.mob.isInWalkTargetRange(new BlockPos(livingEntity))
				? false
				: !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().method_13107(this.field_14581, this.speed);
		this.updateCountdownTicks = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity instanceof PlayerEntity && (((PlayerEntity)livingEntity).isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
			this.mob.setTarget(null);
		}

		this.mob.getNavigation().stop();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.mob.getTarget();
		this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
		double d = this.mob.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
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
		this.method_13497(livingEntity, d);
	}

	protected void method_13497(LivingEntity livingEntity, double d) {
		double e = this.getSquaredMaxAttackDistance(livingEntity);
		if (d <= e && this.field_3534 <= 0) {
			this.field_3534 = 20;
			this.mob.swingHand(Hand.MAIN_HAND);
			this.mob.tryAttack(livingEntity);
		}
	}

	protected double getSquaredMaxAttackDistance(LivingEntity entity) {
		return (double)(this.mob.width * 2.0F * this.mob.width * 2.0F + entity.width);
	}
}
