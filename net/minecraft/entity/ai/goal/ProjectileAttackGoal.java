package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal extends Goal {
	private final MobEntity entity;
	private final RangedAttackMob rangedAttackMob;
	private LivingEntity target;
	private int updateCountdownTicks = -1;
	private final double mobSpeed;
	private int seenTargetTicks;
	private final int minIntervalTicks;
	private final int maxIntervalTicks;
	private final float maxShootRange;
	private final float squaredMaxShootRange;

	public ProjectileAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
		this(rangedAttackMob, d, i, i, f);
	}

	public ProjectileAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, int j, float f) {
		if (!(rangedAttackMob instanceof LivingEntity)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.rangedAttackMob = rangedAttackMob;
			this.entity = (MobEntity)rangedAttackMob;
			this.mobSpeed = d;
			this.minIntervalTicks = i;
			this.maxIntervalTicks = j;
			this.maxShootRange = f;
			this.squaredMaxShootRange = f * f;
			this.setCategoryBits(3);
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			return false;
		} else {
			this.target = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart() || !this.entity.getNavigation().isIdle();
	}

	@Override
	public void stop() {
		this.target = null;
		this.seenTargetTicks = 0;
		this.updateCountdownTicks = -1;
	}

	@Override
	public void tick() {
		double d = this.entity.squaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
		boolean bl = this.entity.getVisibilityCache().canSee(this.target);
		if (bl) {
			this.seenTargetTicks++;
		} else {
			this.seenTargetTicks = 0;
		}

		if (!(d > (double)this.squaredMaxShootRange) && this.seenTargetTicks >= 20) {
			this.entity.getNavigation().stop();
		} else {
			this.entity.getNavigation().startMovingTo(this.target, this.mobSpeed);
		}

		this.entity.getLookControl().lookAt(this.target, 30.0F, 30.0F);
		if (--this.updateCountdownTicks == 0) {
			if (!bl) {
				return;
			}

			float f = MathHelper.sqrt(d) / this.maxShootRange;
			float g = MathHelper.clamp(f, 0.1F, 1.0F);
			this.rangedAttackMob.rangedAttack(this.target, g);
			this.updateCountdownTicks = MathHelper.floor(f * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
		} else if (this.updateCountdownTicks < 0) {
			float h = MathHelper.sqrt(d) / this.maxShootRange;
			this.updateCountdownTicks = MathHelper.floor(h * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
		}
	}
}
