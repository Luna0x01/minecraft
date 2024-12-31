package net.minecraft.client.particle;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
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
		if (material.isFluid() || material.isSolid()) {
			double d;
			if (blockState.getBlock() instanceof AbstractFluidBlock) {
				d = (double)(1.0F - AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL)));
			} else {
				d = blockState.getCollisionBox(this.field_13424, blockPos).maxY;
			}

			double f = (double)MathHelper.floor(this.field_13429) + d;
			if (this.field_13429 < f) {
				this.method_12251();
			}
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new RainSplashParticle(world, x, y, z);
		}
	}
}
