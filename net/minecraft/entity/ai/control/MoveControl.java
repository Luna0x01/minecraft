package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class MoveControl {
	protected MobEntity entity;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected double speed;
	protected boolean moving;

	public MoveControl(MobEntity mobEntity) {
		this.entity = mobEntity;
		this.targetX = mobEntity.x;
		this.targetY = mobEntity.y;
		this.targetZ = mobEntity.z;
	}

	public boolean isMoving() {
		return this.moving;
	}

	public double getSpeed() {
		return this.speed;
	}

	public void moveTo(double x, double y, double z, double speed) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.speed = speed;
		this.moving = true;
	}

	public void updateMovement() {
		this.entity.setForwardSpeed(0.0F);
		if (this.moving) {
			this.moving = false;
			int i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
			double d = this.targetX - this.entity.x;
			double e = this.targetZ - this.entity.z;
			double f = this.targetY - (double)i;
			double g = d * d + f * f + e * e;
			if (!(g < 2.5000003E-7F)) {
				float h = (float)(MathHelper.atan2(e, d) * 180.0 / (float) Math.PI) - 90.0F;
				this.entity.yaw = this.wrapDegrees(this.entity.yaw, h, 30.0F);
				this.entity.setMovementSpeed((float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
				if (f > 0.0 && d * d + e * e < 1.0) {
					this.entity.getJumpControl().setActive();
				}
			}
		}
	}

	protected float wrapDegrees(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(to - from);
		if (f > max) {
			f = max;
		}

		if (f < -max) {
			f = -max;
		}

		float g = from + f;
		if (g < 0.0F) {
			g += 360.0F;
		} else if (g > 360.0F) {
			g -= 360.0F;
		}

		return g;
	}

	public double getTargetX() {
		return this.targetX;
	}

	public double getTargetY() {
		return this.targetY;
	}

	public double getTargetZ() {
		return this.targetZ;
	}
}
