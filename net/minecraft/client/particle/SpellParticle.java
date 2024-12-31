package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.class_4343;
import net.minecraft.world.World;

public class SpellParticle extends Particle {
	private static final Random spellRandom = new Random();
	private int textureIndex = 128;

	protected SpellParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.5 - spellRandom.nextDouble(), h, 0.5 - spellRandom.nextDouble());
		this.velocityY *= 0.2F;
		if (g == 0.0 && i == 0.0) {
			this.velocityX *= 0.1F;
			this.velocityZ *= 0.1F;
		}

		this.scale *= 0.75F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_14950 = false;
	}

	@Override
	public boolean method_12248() {
		return true;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.setMiscTexture(this.textureIndex + 7 - this.age * 8 / this.maxAge);
		this.velocityY += 0.004;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		if (this.field_13429 == this.field_13426) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public void setTextureIndex(int index) {
		this.textureIndex = index;
	}

	public static class AmbientMobSpellFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			particle.method_12250(0.15F);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	public static class InstantSpellFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			((SpellParticle)particle).setTextureIndex(144);
			return particle;
		}
	}

	public static class MobSpellFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			particle.setColor((float)g, (float)h, (float)i);
			return particle;
		}
	}

	public static class SpellFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new SpellParticle(world, d, e, f, g, h, i);
		}
	}

	public static class WitchSpellFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new SpellParticle(world, d, e, f, g, h, i);
			((SpellParticle)particle).setTextureIndex(144);
			float j = world.random.nextFloat() * 0.5F + 0.35F;
			particle.setColor(1.0F * j, 0.0F * j, 1.0F * j);
			return particle;
		}
	}
}
