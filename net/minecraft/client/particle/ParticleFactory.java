package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;

public interface ParticleFactory<T extends ParticleEffect> {
	@Nullable
	Particle method_19020(T particleEffect, World world, double d, double e, double f, double g, double h, double i);
}
