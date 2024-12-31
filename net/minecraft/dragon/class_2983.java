package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.EndExitPortalFeature;

public class class_2983 extends class_2979 {
	private PathMinHeap field_14677;
	private Vec3d field_14678;
	private boolean field_14679;

	public class_2983(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public class_2993<class_2983> method_13189() {
		return class_2993.HOLDING_PATTERN;
	}

	@Override
	public void method_13183() {
		double d = this.field_14678 == null ? 0.0 : this.field_14678.method_12126(this.dragon.x, this.dragon.y, this.dragon.z);
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.method_13175();
		}
	}

	@Override
	public void method_13184() {
		this.field_14677 = null;
		this.field_14678 = null;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14678;
	}

	private void method_13175() {
		if (this.field_14677 != null && this.field_14677.method_11930()) {
			BlockPos blockPos = this.dragon.world.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndExitPortalFeature.ORIGIN));
			int i = this.dragon.method_13169() == null ? 0 : this.dragon.method_13169().getAliveCrystals();
			if (this.dragon.getRandom().nextInt(i + 3) == 0) {
				this.dragon.method_13168().method_13203(class_2993.LANDING_APPROACH);
				return;
			}

			double d = 64.0;
			PlayerEntity playerEntity = this.dragon.world.method_11480(blockPos, d, d);
			if (playerEntity != null) {
				d = playerEntity.squaredDistanceToCenter(blockPos) / 512.0;
			}

			if (playerEntity != null && (this.dragon.getRandom().nextInt(MathHelper.abs((int)d) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
				this.method_13174(playerEntity);
				return;
			}
		}

		if (this.field_14677 == null || this.field_14677.method_11930()) {
			int j = this.dragon.method_13171();
			int k = j;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.field_14679 = !this.field_14679;
				k = j + 6;
			}

			if (this.field_14679) {
				k++;
			} else {
				k--;
			}

			if (this.dragon.method_13169() != null && this.dragon.method_13169().getAliveCrystals() >= 0) {
				k %= 12;
				if (k < 0) {
					k += 12;
				}
			} else {
				k -= 12;
				k &= 7;
				k += 12;
			}

			this.field_14677 = this.dragon.method_13164(j, k, null);
			if (this.field_14677 != null) {
				this.field_14677.method_11924();
			}
		}

		this.method_13176();
	}

	private void method_13174(PlayerEntity playerEntity) {
		this.dragon.method_13168().method_13203(class_2993.STRAFE_PLAYER);
		this.dragon.method_13168().method_13204(class_2993.STRAFE_PLAYER).method_13191(playerEntity);
	}

	private void method_13176() {
		if (this.field_14677 != null && !this.field_14677.method_11930()) {
			Vec3d vec3d = this.field_14677.method_11938();
			this.field_14677.method_11924();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_14678 = new Vec3d(d, f, e);
		}
	}

	@Override
	public void method_13181(EndCrystalEntity endCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity) {
		if (playerEntity != null && !playerEntity.abilities.invulnerable) {
			this.method_13174(playerEntity);
		}
	}
}
