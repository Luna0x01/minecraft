package net.minecraft;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class class_3369 extends MoveControl {
	public class_3369(MobEntity mobEntity) {
		super(mobEntity);
	}

	@Override
	public void updateMovement() {
		if (this.state == MoveControl.MoveStatus.MOVE_TO) {
			this.state = MoveControl.MoveStatus.WAIT;
			this.entity.setNoGravity(true);
			double d = this.targetX - this.entity.x;
			double e = this.targetY - this.entity.y;
			double f = this.targetZ - this.entity.z;
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.entity.setForwardSpeed(0.0F);
				this.entity.method_15061(0.0F);
				return;
			}

			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.yaw = this.wrapDegrees(this.entity.yaw, h, 10.0F);
			float i;
			if (this.entity.onGround) {
				i = (float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
			} else {
				i = (float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_FLYING_SPEED).getValue());
			}

			this.entity.setMovementSpeed(i);
			double k = (double)MathHelper.sqrt(d * d + f * f);
			float l = (float)(-(MathHelper.atan2(e, k) * 180.0F / (float)Math.PI));
			this.entity.pitch = this.wrapDegrees(this.entity.pitch, l, 10.0F);
			this.entity.setForwardSpeed(e > 0.0 ? i : -i);
		} else {
			this.entity.setNoGravity(false);
			this.entity.setForwardSpeed(0.0F);
			this.entity.method_15061(0.0F);
		}
	}
}
