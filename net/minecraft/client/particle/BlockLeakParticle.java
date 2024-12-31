package net.minecraft.client.particle;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockLeakParticle extends Particle {
	private Material material;
	private int ticks;

	protected BlockLeakParticle(World world, double d, double e, double f, Material material) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = this.velocityY = this.velocityZ = 0.0;
		if (material == Material.WATER) {
			this.red = 0.0F;
			this.green = 0.0F;
			this.blue = 1.0F;
		} else {
			this.red = 1.0F;
			this.green = 0.0F;
			this.blue = 0.0F;
		}

		this.setMiscTexture(113);
		this.setBounds(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.material = material;
		this.ticks = 40;
		this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		this.velocityX = this.velocityY = this.velocityZ = 0.0;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return this.material == Material.WATER ? super.getLightmapCoordinates(f) : 257;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return this.material == Material.WATER ? super.getBrightnessAtEyes(f) : 1.0F;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.material == Material.WATER) {
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

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.remove();
		}

		if (this.onGround) {
			if (this.material == Material.WATER) {
				this.remove();
				this.world.addParticle(ParticleType.WATER, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			} else {
				this.setMiscTexture(114);
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this);
		BlockState blockState = this.world.getBlockState(blockPos);
		Material material = blockState.getBlock().getMaterial();
		if (material.isFluid() || material.isSolid()) {
			double d = 0.0;
			if (blockState.getBlock() instanceof AbstractFluidBlock) {
				d = (double)AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL));
			}

			double e = (double)(MathHelper.floor(this.y) + 1) - d;
			if (this.y < e) {
				this.remove();
			}
		}
	}

	public static class LavaDripFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new BlockLeakParticle(world, x, y, z, Material.LAVA);
		}
	}

	public static class WaterDripFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new BlockLeakParticle(world, x, y, z, Material.WATER);
		}
	}
}
