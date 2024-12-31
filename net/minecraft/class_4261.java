package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface class_4261 {
	Vec3d method_19411(class_3915 arg);

	Vec2f method_19413(class_3915 arg);

	default BlockPos method_19415(class_3915 arg) {
		return new BlockPos(this.method_19411(arg));
	}

	boolean method_19410();

	boolean method_19412();

	boolean method_19414();
}
