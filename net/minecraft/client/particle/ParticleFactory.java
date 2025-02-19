package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;

public interface ParticleFactory<T extends ParticleEffect> {
	@Nullable
	Particle createParticle(T parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}
