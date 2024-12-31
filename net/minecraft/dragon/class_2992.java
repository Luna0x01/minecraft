package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.EndExitPortalFeature;

public class class_2992 extends class_2979 {
	private boolean field_14695;
	private PathMinHeap field_14696;
	private Vec3d field_14697;

	public class_2992(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13183() {
		if (!this.field_14695 && this.field_14696 != null) {
			BlockPos blockPos = this.dragon.world.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, EndExitPortalFeature.ORIGIN);
			double d = this.dragon.squaredDistanceToCenter(blockPos);
			if (d > 100.0) {
				this.dragon.method_13168().method_13203(class_2993.HOLDING_PATTERN);
			}
		} else {
			this.field_14695 = false;
			this.method_13194();
		}
	}

	@Override
	public void method_13184() {
		this.field_14695 = true;
		this.field_14696 = null;
		this.field_14697 = null;
	}

	private void method_13194() {
		int i = this.dragon.method_13171();
		Vec3d vec3d = this.dragon.method_13162(1.0F);
		int j = this.dragon.method_13170(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
		if (this.dragon.method_13169() != null && this.dragon.method_13169().getAliveCrystals() > 0) {
			j %= 12;
			if (j < 0) {
				j += 12;
			}
		} else {
			j -= 12;
			j &= 7;
			j += 12;
		}

		this.field_14696 = this.dragon.method_13164(i, j, null);
		if (this.field_14696 != null) {
			this.field_14696.method_11924();
			this.method_13195();
		}
	}

	private void method_13195() {
		Vec3d vec3d = this.field_14696.method_11938();
		this.field_14696.method_11924();

		double d;
		do {
			d = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
		} while (d < vec3d.y);

		this.field_14697 = new Vec3d(vec3d.x, d, vec3d.z);
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14697;
	}

	@Override
	public class_2993<class_2992> method_13189() {
		return class_2993.TAKEOFF;
	}
}
