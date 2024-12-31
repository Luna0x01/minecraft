package net.minecraft.entity.ai.control;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
	private LivingEntity entity;
	private int activeTicks;
	private float lastHeadYaw;

	public BodyControl(LivingEntity livingEntity) {
		this.entity = livingEntity;
	}

	public void tick() {
		double d = this.entity.x - this.entity.prevX;
		double e = this.entity.z - this.entity.prevZ;
		if (d * d + e * e > 2.5000003E-7F) {
			this.entity.bodyYaw = this.entity.yaw;
			this.entity.headYaw = this.clampAndWrapAngle(this.entity.bodyYaw, this.entity.headYaw, 75.0F);
			this.lastHeadYaw = this.entity.headYaw;
			this.activeTicks = 0;
		} else {
			if (this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity)) {
				float f = 75.0F;
				if (Math.abs(this.entity.headYaw - this.lastHeadYaw) > 15.0F) {
					this.activeTicks = 0;
					this.lastHeadYaw = this.entity.headYaw;
				} else {
					this.activeTicks++;
					int i = 10;
					if (this.activeTicks > 10) {
						f = Math.max(1.0F - (float)(this.activeTicks - 10) / 10.0F, 0.0F) * 75.0F;
					}
				}

				this.entity.bodyYaw = this.clampAndWrapAngle(this.entity.headYaw, this.entity.bodyYaw, f);
			}
		}
	}

	private float clampAndWrapAngle(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(from - to);
		if (f < -max) {
			f = -max;
		}

		if (f >= max) {
			f = max;
		}

		return from - f;
	}
}
