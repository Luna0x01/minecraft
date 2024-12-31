package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.EndExitPortalFeature;

public class class_2985 extends class_2979 {
	private PathMinHeap field_14681;
	private Vec3d field_14682;

	public class_2985(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public class_2993<class_2985> method_13189() {
		return class_2993.LANDING_APPROACH;
	}

	@Override
	public void method_13184() {
		this.field_14681 = null;
		this.field_14682 = null;
	}

	@Override
	public void method_13183() {
		double d = this.field_14682 == null ? 0.0 : this.field_14682.method_12126(this.dragon.x, this.dragon.y, this.dragon.z);
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.method_13177();
		}
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14682;
	}

	private void method_13177() {
		if (this.field_14681 == null || this.field_14681.method_11930()) {
			int i = this.dragon.method_13171();
			BlockPos blockPos = this.dragon.world.getTopPosition(EndExitPortalFeature.ORIGIN);
			PlayerEntity playerEntity = this.dragon.world.method_11480(blockPos, 128.0, 128.0);
			int j;
			if (playerEntity != null) {
				Vec3d vec3d = new Vec3d(playerEntity.x, 0.0, playerEntity.z).normalize();
				j = this.dragon.method_13170(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
			} else {
				j = this.dragon.method_13170(40.0, (double)blockPos.getY(), 0.0);
			}

			PathNode pathNode = new PathNode(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.field_14681 = this.dragon.method_13164(i, j, pathNode);
			if (this.field_14681 != null) {
				this.field_14681.method_11924();
			}
		}

		this.method_13178();
		if (this.field_14681 != null && this.field_14681.method_11930()) {
			this.dragon.method_13168().method_13203(class_2993.LANDING);
		}
	}

	private void method_13178() {
		if (this.field_14681 != null && !this.field_14681.method_11930()) {
			Vec3d vec3d = this.field_14681.method_11938();
			this.field_14681.method_11924();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_14682 = new Vec3d(d, f, e);
		}
	}
}
