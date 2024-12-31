package net.minecraft.client.particle;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
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
		this.setMiscTexture(19 + this.random.nextInt(4));
		this.setBounds(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.remove();
		}

		if (this.onGround) {
			if (Math.random() < 0.5) {
				this.remove();
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this);
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		block.setBoundingBox(this.world, blockPos);
		Material material = blockState.getBlock().getMaterial();
		if (material.isFluid() || material.isSolid()) {
			double d = 0.0;
			if (blockState.getBlock() instanceof AbstractFluidBlock) {
				d = (double)(1.0F - AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL)));
			} else {
				d = block.getMaxY();
			}

			double e = (double)MathHelper.floor(this.y) + d;
			if (this.y < e) {
				this.remove();
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
