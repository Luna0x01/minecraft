package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class class_3133 extends WanderAroundGoal {
	protected final float field_15483;

	public class_3133(PathAwareEntity pathAwareEntity, double d) {
		this(pathAwareEntity, d, 0.001F);
	}

	public class_3133(PathAwareEntity pathAwareEntity, double d, float f) {
		super(pathAwareEntity, d);
		this.field_15483 = f;
	}

	@Nullable
	@Override
	protected Vec3d method_13954() {
		if (this.mob.isTouchingWater()) {
			Vec3d vec3d = RandomVectorGenerator.method_13959(this.mob, 15, 7);
			return vec3d == null ? super.method_13954() : vec3d;
		} else {
			return this.mob.getRandom().nextFloat() >= this.field_15483 ? RandomVectorGenerator.method_13959(this.mob, 10, 7) : super.method_13954();
		}
	}
}
