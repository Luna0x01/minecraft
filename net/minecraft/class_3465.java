package net.minecraft;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class class_3465 extends LookControl {
	private final int field_16831;

	public class_3465(MobEntity mobEntity, int i) {
		super(mobEntity);
		this.field_16831 = i;
	}

	@Override
	public void tick() {
		if (this.active) {
			this.active = false;
			double d = this.lookX - this.entity.x;
			double e = this.lookY - (this.entity.y + (double)this.entity.getEyeHeight());
			double f = this.lookZ - this.entity.z;
			double g = (double)MathHelper.sqrt(d * d + f * f);
			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F + 20.0F;
			float i = (float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)) + 10.0F;
			this.entity.pitch = this.clampAndWrapAngle(this.entity.pitch, i, this.pitch);
			this.entity.headYaw = this.clampAndWrapAngle(this.entity.headYaw, h, this.yaw);
		} else {
			if (this.entity.getNavigation().isIdle()) {
				this.entity.pitch = this.clampAndWrapAngle(this.entity.pitch, 0.0F, 5.0F);
			}

			this.entity.headYaw = this.clampAndWrapAngle(this.entity.headYaw, this.entity.bodyYaw, this.yaw);
		}

		float j = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.bodyYaw);
		if (j < (float)(-this.field_16831)) {
			this.entity.bodyYaw -= 4.0F;
		} else if (j > (float)this.field_16831) {
			this.entity.bodyYaw += 4.0F;
		}
	}
}
