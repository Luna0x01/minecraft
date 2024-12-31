package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class LookControl {
	private MobEntity entity;
	private float yaw;
	private float pitch;
	private boolean active;
	private double lookX;
	private double lookY;
	private double lookZ;

	public LookControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public void lookAt(Entity entity, float yaw, float pitch) {
		this.lookX = entity.x;
		if (entity instanceof LivingEntity) {
			this.lookY = entity.y + (double)entity.getEyeHeight();
		} else {
			this.lookY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
		}

		this.lookZ = entity.z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.active = true;
	}

	public void lookAt(double x, double y, double z, float yaw, float pitch) {
		this.lookX = x;
		this.lookY = y;
		this.lookZ = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.active = true;
	}

	public void tick() {
		this.entity.pitch = 0.0F;
		if (this.active) {
			this.active = false;
			double d = this.lookX - this.entity.x;
			double e = this.lookY - (this.entity.y + (double)this.entity.getEyeHeight());
			double f = this.lookZ - this.entity.z;
			double g = (double)MathHelper.sqrt(d * d + f * f);
			float h = (float)(MathHelper.atan2(f, d) * 180.0 / (float) Math.PI) - 90.0F;
			float i = (float)(-(MathHelper.atan2(e, g) * 180.0 / (float) Math.PI));
			this.entity.pitch = this.clampAndWrapAngle(this.entity.pitch, i, this.pitch);
			this.entity.headYaw = this.clampAndWrapAngle(this.entity.headYaw, h, this.yaw);
		} else {
			this.entity.headYaw = this.clampAndWrapAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
		}

		float j = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.bodyYaw);
		if (!this.entity.getNavigation().isIdle()) {
			if (j < -75.0F) {
				this.entity.headYaw = this.entity.bodyYaw - 75.0F;
			}

			if (j > 75.0F) {
				this.entity.headYaw = this.entity.bodyYaw + 75.0F;
			}
		}
	}

	private float clampAndWrapAngle(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(to - from);
		if (f > max) {
			f = max;
		}

		if (f < -max) {
			f = -max;
		}

		return from + f;
	}

	public boolean isActive() {
		return this.active;
	}

	public double getLookX() {
		return this.lookX;
	}

	public double getLookY() {
		return this.lookY;
	}

	public double getLookZ() {
		return this.lookZ;
	}
}
