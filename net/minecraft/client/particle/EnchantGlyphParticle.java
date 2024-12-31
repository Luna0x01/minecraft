package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class EnchantGlyphParticle extends Particle {
	private final double startX;
	private final double startY;
	private final double startZ;

	protected EnchantGlyphParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.startX = d;
		this.startY = e;
		this.startZ = f;
		this.field_13425 = d + g;
		this.field_13426 = e + h;
		this.field_13427 = f + i;
		this.field_13428 = this.field_13425;
		this.field_13429 = this.field_13426;
		this.field_13430 = this.field_13427;
		float j = this.field_13438.nextFloat() * 0.6F + 0.4F;
		this.scale = this.field_13438.nextFloat() * 0.5F + 0.2F;
		this.red = 0.9F * j;
		this.green = 0.9F * j;
		this.blue = j;
		this.field_14950 = false;
		this.maxAge = (int)(Math.random() * 10.0) + 30;
		this.setMiscTexture((int)(Math.random() * 26.0 + 1.0 + 224.0));
	}

	@Override
	public void method_12242(double d, double e, double f) {
		this.method_12246(this.method_12254().offset(d, e, f));
		this.method_12252();
	}

	@Override
	public int method_12243(float f) {
		int i = super.method_12243(f);
		float g = (float)this.age / (float)this.maxAge;
		g *= g;
		g *= g;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(g * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		float f = (float)this.age / (float)this.maxAge;
		f = 1.0F - f;
		float g = 1.0F - f;
		g *= g;
		g *= g;
		this.field_13428 = this.startX + this.velocityX * (double)f;
		this.field_13429 = this.startY + this.velocityY * (double)f - (double)(g * 1.2F);
		this.field_13430 = this.startZ + this.velocityZ * (double)f;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new EnchantGlyphParticle(world, d, e, f, g, h, i);
		}
	}

	public static class class_4208 implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
			enchantGlyphParticle.setMiscTexture(208);
			return enchantGlyphParticle;
		}
	}
}
