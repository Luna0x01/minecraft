package net.minecraft.client.particle;

import net.minecraft.class_4342;
import net.minecraft.class_4343;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockLeakParticle extends Particle {
	private final Fluid field_20628;
	private int ticks;

	protected BlockLeakParticle(World world, double d, double e, double f, Fluid fluid) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		if (fluid.method_17786(FluidTags.WATER)) {
			this.red = 0.0F;
			this.green = 0.0F;
			this.blue = 1.0F;
		} else {
			this.red = 1.0F;
			this.green = 0.0F;
			this.blue = 0.0F;
		}

		this.setMiscTexture(113);
		this.method_12244(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.field_20628 = fluid;
		this.ticks = 40;
		this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
	}

	@Override
	public int method_12243(float f) {
		return this.field_20628.method_17786(FluidTags.WATER) ? super.method_12243(f) : 257;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.field_20628.method_17786(FluidTags.WATER)) {
			this.red = 0.2F;
			this.green = 0.3F;
			this.blue = 1.0F;
		} else {
			this.red = 1.0F;
			this.green = 16.0F / (float)(40 - this.ticks + 16);
			this.blue = 4.0F / (float)(40 - this.ticks + 8);
		}

		this.velocityY = this.velocityY - (double)this.gravityStrength;
		if (this.ticks-- > 0) {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
			this.setMiscTexture(113);
		} else {
			this.setMiscTexture(112);
		}

		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.method_12251();
		}

		if (this.field_13434) {
			if (this.field_20628.method_17786(FluidTags.WATER)) {
				this.method_12251();
				this.field_13424.method_16343(class_4342.field_21368, this.field_13428, this.field_13429, this.field_13430, 0.0, 0.0, 0.0);
			} else {
				this.setMiscTexture(114);
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this.field_13428, this.field_13429, this.field_13430);
		FluidState fluidState = this.field_13424.getFluidState(blockPos);
		if (fluidState.getFluid() == this.field_20628) {
			double d = (double)((float)MathHelper.floor(this.field_13429) + fluidState.method_17810());
			if (this.field_13429 < d) {
				this.method_12251();
			}
		}
	}

	public static class LavaDripFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.LAVA);
		}
	}

	public static class WaterDripFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new BlockLeakParticle(world, d, e, f, Fluids.WATER);
		}
	}
}
