package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.class_2771;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class MoveControl {
	protected final MobEntity entity;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected double speed;
	protected float field_14568;
	protected float field_14569;
	public MoveControl.MoveStatus state = MoveControl.MoveStatus.WAIT;

	public MoveControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public boolean isMoving() {
		return this.state == MoveControl.MoveStatus.MOVE_TO;
	}

	public double getSpeed() {
		return this.speed;
	}

	public void moveTo(double x, double y, double z, double speed) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.speed = speed;
		this.state = MoveControl.MoveStatus.MOVE_TO;
	}

	public void method_13094(float f, float g) {
		this.state = MoveControl.MoveStatus.STRAFE;
		this.field_14568 = f;
		this.field_14569 = g;
		this.speed = 0.25;
	}

	public void copyFrom(MoveControl control) {
		this.state = control.state;
		this.targetX = control.targetX;
		this.targetY = control.targetY;
		this.targetZ = control.targetZ;
		this.speed = Math.max(control.speed, 1.0);
		this.field_14568 = control.field_14568;
		this.field_14569 = control.field_14569;
	}

	public void updateMovement() {
		if (this.state == MoveControl.MoveStatus.STRAFE) {
			float f = (float)this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue();
			float g = (float)this.speed * f;
			float h = this.field_14568;
			float i = this.field_14569;
			float j = MathHelper.sqrt(h * h + i * i);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = MathHelper.sin(this.entity.yaw * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.entity.yaw * (float) (Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			EntityNavigation entityNavigation = this.entity.getNavigation();
			if (entityNavigation != null) {
				class_2771 lv = entityNavigation.method_13114();
				if (lv != null
					&& lv.method_11913(
							this.entity.world, MathHelper.floor(this.entity.x + (double)m), MathHelper.floor(this.entity.y), MathHelper.floor(this.entity.z + (double)n)
						)
						!= LandType.WALKABLE) {
					this.field_14568 = 1.0F;
					this.field_14569 = 0.0F;
					g = f;
				}
			}

			this.entity.setMovementSpeed(g);
			this.entity.setForwardSpeed(this.field_14568);
			this.entity.method_13086(this.field_14569);
			this.state = MoveControl.MoveStatus.WAIT;
		} else if (this.state == MoveControl.MoveStatus.MOVE_TO) {
			this.state = MoveControl.MoveStatus.WAIT;
			double d = this.targetX - this.entity.x;
			double e = this.targetZ - this.entity.z;
			double o = this.targetY - this.entity.y;
			double p = d * d + o * o + e * e;
			if (p < 2.5000003E-7F) {
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float q = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.yaw = this.wrapDegrees(this.entity.yaw, q, 90.0F);
			this.entity.setMovementSpeed((float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
			if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0F, this.entity.width)) {
				this.entity.getJumpControl().setActive();
				this.state = MoveControl.MoveStatus.JUMPING;
			}
		} else if (this.state == MoveControl.MoveStatus.JUMPING) {
			this.entity.setMovementSpeed((float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
			if (this.entity.onGround) {
				this.state = MoveControl.MoveStatus.WAIT;
			}
		} else {
			this.entity.setForwardSpeed(0.0F);
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

	public static enum MoveStatus {
		WAIT,
		MOVE_TO,
		STRAFE,
		JUMPING;
	}
}
