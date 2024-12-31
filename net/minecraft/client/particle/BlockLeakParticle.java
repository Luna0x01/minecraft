package net.minecraft.client.particle;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockLeakParticle extends Particle {
	private final Material material;
	private int ticks;

	protected BlockLeakParticle(World world, double d, double e, double f, Material material) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
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
		this.method_12244(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.material = material;
		this.ticks = 40;
		this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
	}

	@Override
	public int method_12243(float f) {
		return this.material == Material.WATER ? super.method_12243(f) : 257;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
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

		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.maxAge-- <= 0) {
			this.method_12251();
		}

		if (this.field_13434) {
			if (this.material == Material.WATER) {
				this.method_12251();
				this.field_13424.addParticle(ParticleType.WATER, this.field_13428, this.field_13429, this.field_13430, 0.0, 0.0, 0.0);
			} else {
				this.setMiscTexture(114);
			}

			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}

		BlockPos blockPos = new BlockPos(this.field_13428, this.field_13429, this.field_13430);
		BlockState blockState = this.field_13424.getBlockState(blockPos);
		Material material = blockState.getMaterial();
		if (material.isFluid() || material.isSolid()) {
			double d = 0.0;
			if (blockState.getBlock() instanceof AbstractFluidBlock) {
				d = (double)AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL));
			}

			double e = (double)(MathHelper.floor(this.field_13429) + 1) - d;
			if (this.field_13429 < e) {
				this.method_12251();
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
