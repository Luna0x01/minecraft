package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal extends TrackTargetGoal {
	private final TameableEntity tameable;
	private LivingEntity attacking;
	private int lastAttackTime;

	public AttackWithOwnerGoal(TameableEntity tameableEntity) {
		super(tameableEntity, false);
		this.tameable = tameableEntity;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.tameable.isTamed()) {
			return false;
		} else {
			LivingEntity livingEntity = this.tameable.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.attacking = livingEntity.getAttacking();
				int i = livingEntity.getLastAttackTime();
				return i != this.lastAttackTime && this.canTrack(this.attacking, false) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
			}
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.attacking);
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity != null) {
			this.lastAttackTime = livingEntity.getLastAttackTime();
		}

		super.start();
	}
}
