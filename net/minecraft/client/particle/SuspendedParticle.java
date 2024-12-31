package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SuspendedParticle extends Particle {
	protected SuspendedParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e - 0.125, f, g, h, i);
		this.red = 0.4F;
		this.green = 0.4F;
		this.blue = 0.7F;
		this.setMiscTexture(0);
		this.method_12244(0.01F, 0.01F);
		this.scale = this.scale * (this.field_13438.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.0;
		this.velocityY = h * 0.0;
		this.velocityZ = i * 0.0;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		if (this.field_13424.getBlockState(new BlockPos(this.field_13428, this.field_13429, this.field_13430)).getMaterial() != Material.WATER) {
			this.method_12251();
		}

		if (this.maxAge-- <= 0) {
			this.method_12251();
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new SuspendedParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
