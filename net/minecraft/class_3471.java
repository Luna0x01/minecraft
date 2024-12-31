package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_3471 extends Goal {
	private static final int[] field_16857 = new int[]{0, 1, 4, 5, 6, 7};
	private final DolphinEntity field_16858;
	private final int field_16859;
	private boolean field_16860;

	public class_3471(DolphinEntity dolphinEntity, int i) {
		this.field_16858 = dolphinEntity;
		this.field_16859 = i;
		this.setCategoryBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.field_16858.getRandom().nextInt(this.field_16859) != 0) {
			return false;
		} else {
			Direction direction = this.field_16858.getMovementDirection();
			int i = direction.getOffsetX();
			int j = direction.getOffsetZ();
			BlockPos blockPos = new BlockPos(this.field_16858);

			for (int k : field_16857) {
				if (!this.method_15686(blockPos, i, j, k) || !this.method_15687(blockPos, i, j, k)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_15686(BlockPos blockPos, int i, int j, int k) {
		BlockPos blockPos2 = blockPos.add(i * k, 0, j * k);
		return this.field_16858.world.getFluidState(blockPos2).matches(FluidTags.WATER)
			&& !this.field_16858.world.getBlockState(blockPos2).getMaterial().blocksMovement();
	}

	private boolean method_15687(BlockPos blockPos, int i, int j, int k) {
		return this.field_16858.world.getBlockState(blockPos.add(i * k, 1, j * k)).isAir()
			&& this.field_16858.world.getBlockState(blockPos.add(i * k, 2, j * k)).isAir();
	}

	@Override
	public boolean shouldContinue() {
		return (
				!(this.field_16858.velocityY * this.field_16858.velocityY < 0.03F)
					|| this.field_16858.pitch == 0.0F
					|| !(Math.abs(this.field_16858.pitch) < 10.0F)
					|| !this.field_16858.isTouchingWater()
			)
			&& !this.field_16858.onGround;
	}

	@Override
	public boolean canStop() {
		return false;
	}

	@Override
	public void start() {
		Direction direction = this.field_16858.getMovementDirection();
		this.field_16858.velocityX = this.field_16858.velocityX + (double)direction.getOffsetX() * 0.6;
		this.field_16858.velocityY += 0.7;
		this.field_16858.velocityZ = this.field_16858.velocityZ + (double)direction.getOffsetZ() * 0.6;
		this.field_16858.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.field_16858.pitch = 0.0F;
	}

	@Override
	public void tick() {
		boolean bl = this.field_16860;
		if (!bl) {
			FluidState fluidState = this.field_16858.world.getFluidState(new BlockPos(this.field_16858));
			this.field_16860 = fluidState.matches(FluidTags.WATER);
		}

		if (this.field_16860 && !bl) {
			this.field_16858.playSound(Sounds.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
		}

		if (this.field_16858.velocityY * this.field_16858.velocityY < 0.03F && this.field_16858.pitch != 0.0F) {
			this.field_16858.pitch = this.method_15685(this.field_16858.pitch, 0.0F, 0.2F);
		} else {
			double d = Math.sqrt(
				this.field_16858.velocityX * this.field_16858.velocityX
					+ this.field_16858.velocityY * this.field_16858.velocityY
					+ this.field_16858.velocityZ * this.field_16858.velocityZ
			);
			double e = Math.sqrt(this.field_16858.velocityX * this.field_16858.velocityX + this.field_16858.velocityZ * this.field_16858.velocityZ);
			double f = Math.signum(-this.field_16858.velocityY) * Math.acos(e / d) * 180.0F / (float)Math.PI;
			this.field_16858.pitch = (float)f;
		}
	}

	protected float method_15685(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
