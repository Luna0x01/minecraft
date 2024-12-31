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
		this.setBounds(0.01F, 0.01F);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.0;
		this.velocityY = h * 0.0;
		this.velocityZ = i * 0.0;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.world.getBlockState(new BlockPos(this)).getBlock().getMaterial() != Material.WATER) {
			this.remove();
		}

		if (this.maxAge-- <= 0) {
			this.remove();
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new SuspendedParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
