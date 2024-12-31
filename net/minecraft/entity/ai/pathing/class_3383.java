package net.minecraft.entity.ai.pathing;

import net.minecraft.class_3250;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_3383 extends EntityNavigation {
	public class_3383(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createNavigator() {
		this.field_14600 = new class_3250();
		this.field_14600.method_11916(true);
		return new PathNodeNavigator(this.field_14600);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.method_15711() && this.isInLiquid() || !this.mob.hasMount();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.mob.x, this.mob.y, this.mob.z);
	}

	@Override
	public PathMinHeap method_13109(Entity entity) {
		return this.method_13108(new BlockPos(entity));
	}

	@Override
	public void tick() {
		this.tickCount++;
		if (this.field_14606) {
			this.method_13112();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.continueFollowingPath();
			} else if (this.field_14599 != null && this.field_14599.method_11937() < this.field_14599.method_11936()) {
				Vec3d vec3d = this.field_14599.method_11929(this.mob, this.field_14599.method_11937());
				if (MathHelper.floor(this.mob.x) == MathHelper.floor(vec3d.x)
					&& MathHelper.floor(this.mob.y) == MathHelper.floor(vec3d.y)
					&& MathHelper.floor(this.mob.z) == MathHelper.floor(vec3d.z)) {
					this.field_14599.method_11935(this.field_14599.method_11937() + 1);
				}
			}

			this.method_15102();
			if (!this.isIdle()) {
				Vec3d vec3d2 = this.field_14599.method_11928(this.mob);
				this.mob.getMotionHelper().moveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor(origin.x);
		int j = MathHelper.floor(origin.y);
		int k = MathHelper.floor(origin.z);
		double d = target.x - origin.x;
		double e = target.y - origin.y;
		double f = target.z - origin.z;
		double g = d * d + e * e + f * f;
		if (g < 1.0E-8) {
			return false;
		} else {
			double h = 1.0 / Math.sqrt(g);
			d *= h;
			e *= h;
			f *= h;
			double l = 1.0 / Math.abs(d);
			double m = 1.0 / Math.abs(e);
			double n = 1.0 / Math.abs(f);
			double o = (double)i - origin.x;
			double p = (double)j - origin.y;
			double q = (double)k - origin.z;
			if (d >= 0.0) {
				o++;
			}

			if (e >= 0.0) {
				p++;
			}

			if (f >= 0.0) {
				q++;
			}

			o /= d;
			p /= e;
			q /= f;
			int r = d < 0.0 ? -1 : 1;
			int s = e < 0.0 ? -1 : 1;
			int t = f < 0.0 ? -1 : 1;
			int u = MathHelper.floor(target.x);
			int v = MathHelper.floor(target.y);
			int w = MathHelper.floor(target.z);
			int x = u - i;
			int y = v - j;
			int z = w - k;

			while (x * r > 0 || y * s > 0 || z * t > 0) {
				if (o < q && o <= p) {
					o += l;
					i += r;
					x = u - i;
				} else if (p < o && p <= q) {
					p += m;
					j += s;
					y = v - j;
				} else {
					q += n;
					k += t;
					z = w - k;
				}
			}

			return true;
		}
	}

	public void method_15098(boolean bl) {
		this.field_14600.method_11919(bl);
	}

	public void method_15099(boolean bl) {
		this.field_14600.method_11916(bl);
	}

	@Override
	public boolean method_13110(BlockPos blockPos) {
		return this.world.getBlockState(blockPos).method_16913();
	}
}
