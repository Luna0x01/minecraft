package net.minecraft.client.particle;

import net.minecraft.world.World;

public interface ParticleFactory {
	Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr);
}
