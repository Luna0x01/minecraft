package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RainSplashParticle extends Particle {
	protected RainSplashParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.3F;
		this.velocityY = Math.random() * 0.2F + 0.1F;
		this.velocityZ *= 0.3F;
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.setMiscTexture(19 + this.field_13438.nextInt(4));
		this.method_12244(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.method_12251();
		}

		if (this.field_13434) {
			if (Math.random() < 0.5) {
				this.method_12251();
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this.field_13428, this.field_13429, this.field_13430);
		BlockState blockState = this.field_13424.getBlockState(blockPos);
		Material material = blockState.getMaterial();
		FluidState fluidState = this.field_13424.getFluidState(blockPos);
		if (!fluidState.isEmpty() || material.isSolid()) {
			double d;
			if (fluidState.method_17810() > 0.0F) {
				d = (double)fluidState.method_17810();
			} else {
				d = blockState.getCollisionShape(this.field_13424, blockPos)
					.method_18084(Direction.Axis.Y, this.field_13428 - Math.floor(this.field_13428), this.field_13430 - Math.floor(this.field_13430));
			}

			double f = (double)MathHelper.floor(this.field_13429) + d;
			if (this.field_13429 < f) {
				this.method_12251();
			}
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new RainSplashParticle(world, d, e, f);
		}
	}
}
