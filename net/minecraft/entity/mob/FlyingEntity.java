package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
	public FlyingEntity(World world) {
		super(world);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
	}

	@Override
	public void travel(float f, float g) {
		if (this.isTouchingWater()) {
			this.updateVelocity(f, g, 0.02F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.8F;
			this.velocityY *= 0.8F;
			this.velocityZ *= 0.8F;
		} else if (this.isTouchingLava()) {
			this.updateVelocity(f, g, 0.02F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.5;
			this.velocityY *= 0.5;
			this.velocityZ *= 0.5;
		} else {
			float h = 0.91F;
			if (this.onGround) {
				h = this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z))).getBlock().slipperiness
					* 0.91F;
			}

			float i = 0.16277136F / (h * h * h);
			this.updateVelocity(f, g, this.onGround ? 0.1F * i : 0.02F);
			h = 0.91F;
			if (this.onGround) {
				h = this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z))).getBlock().slipperiness
					* 0.91F;
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= (double)h;
			this.velocityY *= (double)h;
			this.velocityZ *= (double)h;
		}

		this.field_6748 = this.field_6749;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float j = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (j > 1.0F) {
			j = 1.0F;
		}

		this.field_6749 = this.field_6749 + (j - this.field_6749) * 0.4F;
		this.field_6750 = this.field_6750 + this.field_6749;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}
}
