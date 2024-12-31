package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.class_4342;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.EndExitPortalFeature;

public class class_2986 extends class_2979 {
	private Vec3d field_14683;

	public class_2986(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13182() {
		Vec3d vec3d = this.dragon.method_13162(1.0F).normalize();
		vec3d.rotateY((float) (-Math.PI / 4));
		double d = this.dragon.partHead.x;
		double e = this.dragon.partHead.y + (double)(this.dragon.partHead.height / 2.0F);
		double f = this.dragon.partHead.z;

		for (int i = 0; i < 8; i++) {
			double g = d + this.dragon.getRandom().nextGaussian() / 2.0;
			double h = e + this.dragon.getRandom().nextGaussian() / 2.0;
			double j = f + this.dragon.getRandom().nextGaussian() / 2.0;
			this.dragon
				.world
				.method_16343(
					class_4342.field_21384,
					g,
					h,
					j,
					-vec3d.x * 0.08F + this.dragon.velocityX,
					-vec3d.y * 0.3F + this.dragon.velocityY,
					-vec3d.z * 0.08F + this.dragon.velocityZ
				);
			vec3d.rotateY((float) (Math.PI / 16));
		}
	}

	@Override
	public void method_13183() {
		if (this.field_14683 == null) {
			this.field_14683 = new Vec3d(this.dragon.world.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, EndExitPortalFeature.ORIGIN));
		}

		if (this.field_14683.method_12126(this.dragon.x, this.dragon.y, this.dragon.z) < 1.0) {
			this.dragon.method_13168().method_13204(class_2993.SITTING_FLAMING).method_13190();
			this.dragon.method_13168().method_13203(class_2993.SITTING_SCANNING);
		}
	}

	@Override
	public float method_13186() {
		return 1.5F;
	}

	@Override
	public float method_13188() {
		float f = MathHelper.sqrt(this.dragon.velocityX * this.dragon.velocityX + this.dragon.velocityZ * this.dragon.velocityZ) + 1.0F;
		float g = Math.min(f, 40.0F);
		return g / f;
	}

	@Override
	public void method_13184() {
		this.field_14683 = null;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14683;
	}

	@Override
	public class_2993<class_2986> method_13189() {
		return class_2993.LANDING;
	}
}
