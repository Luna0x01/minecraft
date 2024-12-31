package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2991 extends class_2979 {
	private static final Logger field_14689 = LogManager.getLogger();
	private int field_14690;
	private PathMinHeap field_14691;
	private Vec3d field_14692;
	private LivingEntity field_14693;
	private boolean field_14694;

	public class_2991(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13183() {
		if (this.field_14693 == null) {
			field_14689.warn("Skipping player strafe phase because no player was found");
			this.dragon.method_13168().method_13203(class_2993.HOLDING_PATTERN);
		} else {
			if (this.field_14691 != null && this.field_14691.method_11930()) {
				double d = this.field_14693.x;
				double e = this.field_14693.z;
				double f = d - this.dragon.x;
				double g = e - this.dragon.z;
				double h = (double)MathHelper.sqrt(f * f + g * g);
				double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
				this.field_14692 = new Vec3d(d, this.field_14693.y + i, e);
			}

			double j = this.field_14692 == null ? 0.0 : this.field_14692.method_12126(this.dragon.x, this.dragon.y, this.dragon.z);
			if (j < 100.0 || j > 22500.0) {
				this.method_13192();
			}

			double k = 64.0;
			if (this.field_14693.squaredDistanceTo(this.dragon) < 4096.0) {
				if (this.dragon.canSee(this.field_14693)) {
					this.field_14690++;
					Vec3d vec3d = new Vec3d(this.field_14693.x - this.dragon.x, 0.0, this.field_14693.z - this.dragon.z).normalize();
					Vec3d vec3d2 = new Vec3d(
							(double)MathHelper.sin(this.dragon.yaw * (float) (Math.PI / 180.0)), 0.0, (double)(-MathHelper.cos(this.dragon.yaw * (float) (Math.PI / 180.0)))
						)
						.normalize();
					float l = (float)vec3d2.dotProduct(vec3d);
					float m = (float)(Math.acos((double)l) * 180.0F / (float)Math.PI);
					m += 0.5F;
					if (this.field_14690 >= 5 && m >= 0.0F && m < 10.0F) {
						double n = 1.0;
						Vec3d vec3d3 = this.dragon.getRotationVector(1.0F);
						double o = this.dragon.partHead.x - vec3d3.x * 1.0;
						double p = this.dragon.partHead.y + (double)(this.dragon.partHead.height / 2.0F) + 0.5;
						double q = this.dragon.partHead.z - vec3d3.z * 1.0;
						double r = this.field_14693.x - o;
						double s = this.field_14693.y + (double)(this.field_14693.height / 2.0F) - (p + (double)(this.dragon.partHead.height / 2.0F));
						double t = this.field_14693.z - q;
						this.dragon.world.syncWorldEvent(null, 1017, new BlockPos(this.dragon), 0);
						DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.world, this.dragon, r, s, t);
						dragonFireballEntity.refreshPositionAndAngles(o, p, q, 0.0F, 0.0F);
						this.dragon.world.method_3686(dragonFireballEntity);
						this.field_14690 = 0;
						if (this.field_14691 != null) {
							while (!this.field_14691.method_11930()) {
								this.field_14691.method_11924();
							}
						}

						this.dragon.method_13168().method_13203(class_2993.HOLDING_PATTERN);
					}
				} else if (this.field_14690 > 0) {
					this.field_14690--;
				}
			} else if (this.field_14690 > 0) {
				this.field_14690--;
			}
		}
	}

	private void method_13192() {
		if (this.field_14691 == null || this.field_14691.method_11930()) {
			int i = this.dragon.method_13171();
			int j = i;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.field_14694 = !this.field_14694;
				j = i + 6;
			}

			if (this.field_14694) {
				j++;
			} else {
				j--;
			}

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

			this.field_14691 = this.dragon.method_13164(i, j, null);
			if (this.field_14691 != null) {
				this.field_14691.method_11924();
			}
		}

		this.method_13193();
	}

	private void method_13193() {
		if (this.field_14691 != null && !this.field_14691.method_11930()) {
			Vec3d vec3d = this.field_14691.method_11938();
			this.field_14691.method_11924();
			double d = vec3d.x;
			double e = vec3d.z;

			double f;
			do {
				f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < vec3d.y);

			this.field_14692 = new Vec3d(d, f, e);
		}
	}

	@Override
	public void method_13184() {
		this.field_14690 = 0;
		this.field_14692 = null;
		this.field_14691 = null;
		this.field_14693 = null;
	}

	public void method_13191(LivingEntity livingEntity) {
		this.field_14693 = livingEntity;
		int i = this.dragon.method_13171();
		int j = this.dragon.method_13170(this.field_14693.x, this.field_14693.y, this.field_14693.z);
		int k = MathHelper.floor(this.field_14693.x);
		int l = MathHelper.floor(this.field_14693.z);
		double d = (double)k - this.dragon.x;
		double e = (double)l - this.dragon.z;
		double f = (double)MathHelper.sqrt(d * d + e * e);
		double g = Math.min(0.4F + f / 80.0 - 1.0, 10.0);
		int m = MathHelper.floor(this.field_14693.y + g);
		PathNode pathNode = new PathNode(k, m, l);
		this.field_14691 = this.dragon.method_13164(i, j, pathNode);
		if (this.field_14691 != null) {
			this.field_14691.method_11924();
			this.method_13193();
		}
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14692;
	}

	@Override
	public class_2993<class_2991> method_13189() {
		return class_2993.STRAFE_PLAYER;
	}
}
