package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class TrackOwnerAttackerGoal extends TrackTargetGoal {
	TameableEntity tameable;
	LivingEntity attacker;
	private int lastAttackedTime;

	public TrackOwnerAttackerGoal(TameableEntity tameableEntity) {
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
				this.attacker = livingEntity.getAttacker();
				int i = livingEntity.getLastHurtTimestamp();
				return i != this.lastAttackedTime && this.canTrack(this.attacker, false) && this.tameable.canAttackWithOwner(this.attacker, livingEntity);
			}
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.attacker);
		LivingEntity livingEntity = this.tameable.getOwner();
		if (livingEntity != null) {
			this.lastAttackedTime = livingEntity.getLastHurtTimestamp();
		}

		super.start();
	}
}
