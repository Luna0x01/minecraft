package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal extends Goal {
	private final MobEntity mob;
	private final RangedAttackMob owner;
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
			this.owner = rangedAttackMob;
			this.mob = (MobEntity)rangedAttackMob;
			this.mobSpeed = d;
			this.minIntervalTicks = i;
			this.maxIntervalTicks = j;
			this.maxShootRange = f;
			this.squaredMaxShootRange = f * f;
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity != null && livingEntity.isAlive()) {
			this.target = livingEntity;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart() || !this.mob.getNavigation().isIdle();
	}

	@Override
	public void stop() {
		this.target = null;
		this.seenTargetTicks = 0;
		this.updateCountdownTicks = -1;
	}

	@Override
	public void tick() {
		double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
		boolean bl = this.mob.getVisibilityCache().canSee(this.target);
		if (bl) {
			this.seenTargetTicks++;
		} else {
			this.seenTargetTicks = 0;
		}

		if (!(d > (double)this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
		}

		this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
		if (--this.updateCountdownTicks == 0) {
			if (!bl) {
				return;
			}

			float f = MathHelper.sqrt(d) / this.maxShootRange;
			float g = MathHelper.clamp(f, 0.1F, 1.0F);
			this.owner.attack(this.target, g);
			this.updateCountdownTicks = MathHelper.floor(f * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
		} else if (this.updateCountdownTicks < 0) {
			float h = MathHelper.sqrt(d) / this.maxShootRange;
			this.updateCountdownTicks = MathHelper.floor(h * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
		}
	}
}
