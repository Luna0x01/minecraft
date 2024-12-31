package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class class_2984 extends class_2979 {
	private Vec3d field_14680;

	public class_2984(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13183() {
		if (this.field_14680 == null) {
			this.field_14680 = new Vec3d(this.dragon.x, this.dragon.y, this.dragon.z);
		}
	}

	@Override
	public boolean method_13179() {
		return true;
	}

	@Override
	public void method_13184() {
		this.field_14680 = null;
	}

	@Override
	public float method_13186() {
		return 1.0F;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14680;
	}

	@Override
	public class_2993<class_2984> method_13189() {
		return class_2993.HOVER;
	}
}
