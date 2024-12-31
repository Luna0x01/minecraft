package net.minecraft.dragon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_2990 extends class_2980 {
	private int field_14688;

	public class_2990(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13183() {
		this.field_14688++;
		LivingEntity livingEntity = this.dragon.world.method_11484(this.dragon, 20.0, 10.0);
		if (livingEntity != null) {
			if (this.field_14688 > 25) {
				this.dragon.method_13168().method_13203(class_2993.SITTING_ATTACKING);
			} else {
				Vec3d vec3d = new Vec3d(livingEntity.x - this.dragon.x, 0.0, livingEntity.z - this.dragon.z).normalize();
				Vec3d vec3d2 = new Vec3d(
						(double)MathHelper.sin(this.dragon.yaw * (float) (Math.PI / 180.0)), 0.0, (double)(-MathHelper.cos(this.dragon.yaw * (float) (Math.PI / 180.0)))
					)
					.normalize();
				float f = (float)vec3d2.dotProduct(vec3d);
				float g = (float)(Math.acos((double)f) * 180.0F / (float)Math.PI) + 0.5F;
				if (g < 0.0F || g > 10.0F) {
					double d = livingEntity.x - this.dragon.partHead.x;
					double e = livingEntity.z - this.dragon.partHead.z;
					double h = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d, e) * 180.0F / (float)Math.PI - (double)this.dragon.yaw), -100.0, 100.0);
					this.dragon.field_6782 *= 0.8F;
					float i = MathHelper.sqrt(d * d + e * e) + 1.0F;
					float j = i;
					if (i > 40.0F) {
						i = 40.0F;
					}

					this.dragon.field_6782 = (float)((double)this.dragon.field_6782 + h * (double)(0.7F / i / j));
					this.dragon.yaw = this.dragon.yaw + this.dragon.field_6782;
				}
			}
		} else if (this.field_14688 >= 100) {
			livingEntity = this.dragon.world.method_11484(this.dragon, 150.0, 150.0);
			this.dragon.method_13168().method_13203(class_2993.TAKEOFF);
			if (livingEntity != null) {
				this.dragon.method_13168().method_13203(class_2993.CHARGING_PLAYER);
				this.dragon.method_13168().method_13204(class_2993.CHARGING_PLAYER).method_13173(new Vec3d(livingEntity.x, livingEntity.y, livingEntity.z));
			}
		}
	}

	@Override
	public void method_13184() {
		this.field_14688 = 0;
	}

	@Override
	public class_2993<class_2990> method_13189() {
		return class_2993.SITTING_SCANNING;
	}
}
