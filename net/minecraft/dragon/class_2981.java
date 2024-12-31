package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2981 extends class_2979 {
	private static final Logger field_14672 = LogManager.getLogger();
	private Vec3d field_14673;
	private int field_14674 = 0;

	public class_2981(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13183() {
		if (this.field_14673 == null) {
			field_14672.warn("Aborting charge player as no target was set.");
			this.dragon.method_13168().method_13203(class_2993.HOLDING_PATTERN);
		} else if (this.field_14674 > 0 && this.field_14674++ >= 10) {
			this.dragon.method_13168().method_13203(class_2993.HOLDING_PATTERN);
		} else {
			double d = this.field_14673.method_12126(this.dragon.x, this.dragon.y, this.dragon.z);
			if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
				this.field_14674++;
			}
		}
	}

	@Override
	public void method_13184() {
		this.field_14673 = null;
		this.field_14674 = 0;
	}

	public void method_13173(Vec3d vec3d) {
		this.field_14673 = vec3d;
	}

	@Override
	public float method_13186() {
		return 3.0F;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14673;
	}

	@Override
	public class_2993<class_2981> method_13189() {
		return class_2993.CHARGING_PLAYER;
	}
}
