package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class class_4018 extends class_4017 {
	@Override
	public LightType method_17742() {
		return LightType.BLOCK;
	}

	public void method_17741(class_4441 arg, class_3781 arg2) {
		for (BlockPos blockPos : arg2.method_17010()) {
			this.method_17734(arg, blockPos, this.method_17737(arg, blockPos));
			this.method_17731(arg2.method_3920(), blockPos, this.method_17733(arg, blockPos));
		}

		this.method_17732(arg, arg2.method_3920());
	}
}
