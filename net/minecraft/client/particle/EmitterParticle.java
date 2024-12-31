package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;

public class EmitterParticle extends Particle {
	private final Entity entity;
	private int emitterAge;
	private final int maxEmitterAge;
	private final ParticleEffect field_10622;

	public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect) {
		this(world, entity, particleEffect, 3);
	}

	public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i) {
		super(world, entity.x, entity.getBoundingBox().minY + (double)(entity.height / 2.0F), entity.z, entity.velocityX, entity.velocityY, entity.velocityZ);
		this.entity = entity;
		this.maxEmitterAge = i;
		this.field_10622 = particleEffect;
		this.method_12241();
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
	}

	@Override
	public void method_12241() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.field_13438.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.field_13438.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.field_13438.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.entity.x + d * (double)this.entity.width / 4.0;
				double h = this.entity.getBoundingBox().minY + (double)(this.entity.height / 2.0F) + e * (double)this.entity.height / 4.0;
				double j = this.entity.z + f * (double)this.entity.width / 4.0;
				this.field_13424.method_16323(this.field_10622, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.emitterAge++;
		if (this.emitterAge >= this.maxEmitterAge) {
			this.method_12251();
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}
}
