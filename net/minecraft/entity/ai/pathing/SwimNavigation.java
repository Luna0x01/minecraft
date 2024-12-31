package net.minecraft.entity.ai.pathing;

import net.minecraft.class_4079;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimNavigation extends EntityNavigation {
	private boolean field_16891;

	public SwimNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createNavigator() {
		this.field_16891 = this.mob instanceof DolphinEntity;
		this.field_14600 = new WaterPathNodeMaker(this.field_16891);
		return new PathNodeNavigator(this.field_14600);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.field_16891 || this.isInLiquid();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.mob.x, this.mob.y + (double)this.mob.height * 0.5, this.mob.z);
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
	protected void continueFollowingPath() {
		if (this.field_14599 != null) {
			Vec3d vec3d = this.getPos();
			float f = this.mob.width > 0.75F ? this.mob.width / 2.0F : 0.75F - this.mob.width / 2.0F;
			if ((double)MathHelper.abs((float)this.mob.velocityX) > 0.2 || (double)MathHelper.abs((float)this.mob.velocityZ) > 0.2) {
				f *= MathHelper.sqrt(this.mob.velocityX * this.mob.velocityX + this.mob.velocityY * this.mob.velocityY + this.mob.velocityZ * this.mob.velocityZ) * 6.0F;
			}

			int i = 6;
			Vec3d vec3d2 = this.field_14599.method_11938();
			if (MathHelper.abs((float)(this.mob.x - (vec3d2.x + 0.5))) < f
				&& MathHelper.abs((float)(this.mob.z - (vec3d2.z + 0.5))) < f
				&& Math.abs(this.mob.y - vec3d2.y) < (double)(f * 2.0F)) {
				this.field_14599.method_11924();
			}

			for (int j = Math.min(this.field_14599.method_11937() + 6, this.field_14599.method_11936() - 1); j > this.field_14599.method_11937(); j--) {
				vec3d2 = this.field_14599.method_11929(this.mob, j);
				if (!(vec3d2.squaredDistanceTo(vec3d) > 36.0) && this.canPathDirectlyThrough(vec3d, vec3d2, 0, 0, 0)) {
					this.field_14599.method_11935(j);
					break;
				}
			}

			this.checkTimeouts(vec3d);
		}
	}

	@Override
	protected void checkTimeouts(Vec3d currentPos) {
		if (this.tickCount - this.pathStartTime > 100) {
			if (currentPos.squaredDistanceTo(this.pathStartPos) < 2.25) {
				this.stop();
			}

			this.pathStartTime = this.tickCount;
			this.pathStartPos = currentPos;
		}

		if (this.field_14599 != null && !this.field_14599.method_11930()) {
			Vec3d vec3d = this.field_14599.method_11938();
			if (vec3d.equals(this.field_14602)) {
				this.field_14603 = this.field_14603 + (Util.method_20227() - this.field_14604);
			} else {
				this.field_14602 = vec3d;
				double d = currentPos.distanceTo(this.field_14602);
				this.field_14605 = this.mob.getMovementSpeed() > 0.0F ? d / (double)this.mob.getMovementSpeed() * 100.0 : 0.0;
			}

			if (this.field_14605 > 0.0 && (double)this.field_14603 > this.field_14605 * 2.0) {
				this.field_14602 = Vec3d.ZERO;
				this.field_14603 = 0L;
				this.field_14605 = 0.0;
				this.stop();
			}

			this.field_14604 = Util.method_20227();
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		BlockHitResult blockHitResult = this.world
			.method_3615(origin, new Vec3d(target.x, target.y + (double)this.mob.height * 0.5, target.z), class_4079.NEVER, true, false);
		return blockHitResult == null || blockHitResult.type == BlockHitResult.Type.MISS;
	}

	@Override
	public boolean method_13110(BlockPos blockPos) {
		return !this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos);
	}

	@Override
	public void method_15709(boolean bl) {
	}
}
