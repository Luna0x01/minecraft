package net.minecraft.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;

public class CrackParticle extends SpriteBillboardParticle {
	private final float sampleU;
	private final float sampleV;

	CrackParticle(ClientWorld world, double x, double y, double z, double d, double e, double f, ItemStack stack) {
		this(world, x, y, z, stack);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += d;
		this.velocityY += e;
		this.velocityZ += f;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.TERRAIN_SHEET;
	}

	protected CrackParticle(ClientWorld world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, 0.0, 0.0, 0.0);
		this.setSprite(MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(stack, world, null, 0).getSprite());
		this.gravityStrength = 1.0F;
		this.scale /= 2.0F;
		this.sampleU = this.random.nextFloat() * 3.0F;
		this.sampleV = this.random.nextFloat() * 3.0F;
	}

	@Override
	protected float getMinU() {
		return this.sprite.getFrameU((double)((this.sampleU + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxU() {
		return this.sprite.getFrameU((double)(this.sampleU / 4.0F * 16.0F));
	}

	@Override
	protected float getMinV() {
		return this.sprite.getFrameV((double)(this.sampleV / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxV() {
		return this.sprite.getFrameV((double)((this.sampleV + 1.0F) / 4.0F * 16.0F));
	}

	public static class ItemFactory implements ParticleFactory<ItemStackParticleEffect> {
		public Particle createParticle(
			ItemStackParticleEffect itemStackParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
		) {
			return new CrackParticle(clientWorld, d, e, f, g, h, i, itemStackParticleEffect.getItemStack());
		}
	}

	public static class SlimeballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(clientWorld, d, e, f, new ItemStack(Items.SLIME_BALL));
		}
	}

	public static class SnowballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(clientWorld, d, e, f, new ItemStack(Items.SNOWBALL));
		}
	}
}
