package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.class_4343;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.FireworkItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FireworksSparkParticle {
	public static class Explosion extends class_2864 {
		private boolean trail;
		private boolean flicker;
		private final ParticleManager manager;
		private float field_13418;
		private float field_13419;
		private float field_13420;
		private boolean fades;

		public Explosion(World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager) {
			super(world, d, e, f, 160, 8, -0.004F);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.manager = particleManager;
			this.scale *= 0.75F;
			this.maxAge = 48 + this.field_13438.nextInt(12);
		}

		public void setTrail(boolean trail) {
			this.trail = trail;
		}

		public void setFlicker(boolean flicker) {
			this.flicker = flicker;
		}

		@Override
		public boolean method_12248() {
			return true;
		}

		@Override
		public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
			if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
				super.draw(builder, entity, tickDelta, g, h, i, j, k);
			}
		}

		@Override
		public void method_12241() {
			super.method_12241();
			if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
				FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(
					this.field_13424, this.field_13428, this.field_13429, this.field_13430, 0.0, 0.0, 0.0, this.manager
				);
				explosion.method_12250(0.99F);
				explosion.setColor(this.red, this.green, this.blue);
				explosion.age = explosion.maxAge / 2;
				if (this.fades) {
					explosion.fades = true;
					explosion.field_13418 = this.field_13418;
					explosion.field_13419 = this.field_13419;
					explosion.field_13420 = this.field_13420;
				}

				explosion.flicker = this.flicker;
				this.manager.method_12256(explosion);
			}
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(world, d, e, f, g, h, i, MinecraftClient.getInstance().particleManager);
			explosion.method_12250(0.99F);
			return explosion;
		}
	}

	public static class FireworkParticle extends Particle {
		private int age;
		private final ParticleManager manager;
		private NbtList explosions;
		private boolean flicker;

		public FireworkParticle(
			World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, @Nullable NbtCompound nbtCompound
		) {
			super(world, d, e, f, 0.0, 0.0, 0.0);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.manager = particleManager;
			this.maxAge = 8;
			if (nbtCompound != null) {
				this.explosions = nbtCompound.getList("Explosions", 10);
				if (this.explosions.isEmpty()) {
					this.explosions = null;
				} else {
					this.maxAge = this.explosions.size() * 2 - 1;

					for (int j = 0; j < this.explosions.size(); j++) {
						NbtCompound nbtCompound2 = this.explosions.getCompound(j);
						if (nbtCompound2.getBoolean("Flicker")) {
							this.flicker = true;
							this.maxAge += 15;
							break;
						}
					}
				}
			}
		}

		@Override
		public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		}

		@Override
		public void method_12241() {
			if (this.age == 0 && this.explosions != null) {
				boolean bl = this.isFar();
				boolean bl2 = false;
				if (this.explosions.size() >= 3) {
					bl2 = true;
				} else {
					for (int i = 0; i < this.explosions.size(); i++) {
						NbtCompound nbtCompound = this.explosions.getCompound(i);
						if (FireworkItem.class_3551.method_16054(nbtCompound.getByte("Type")) == FireworkItem.class_3551.LARGE_BALL) {
							bl2 = true;
							break;
						}
					}
				}

				Sound sound;
				if (bl2) {
					sound = bl ? Sounds.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : Sounds.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
				} else {
					sound = bl ? Sounds.ENTITY_FIREWORK_ROCKET_BLAST_FAR : Sounds.ENTITY_FIREWORK_ROCKET_BLAST;
				}

				this.field_13424
					.playSound(this.field_13428, this.field_13429, this.field_13430, sound, SoundCategory.AMBIENT, 20.0F, 0.95F + this.field_13438.nextFloat() * 0.1F, true);
			}

			if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
				int j = this.age / 2;
				NbtCompound nbtCompound2 = this.explosions.getCompound(j);
				FireworkItem.class_3551 lv = FireworkItem.class_3551.method_16054(nbtCompound2.getByte("Type"));
				boolean bl3 = nbtCompound2.getBoolean("Trail");
				boolean bl4 = nbtCompound2.getBoolean("Flicker");
				int[] is = nbtCompound2.getIntArray("Colors");
				int[] js = nbtCompound2.getIntArray("FadeColors");
				if (is.length == 0) {
					is = new int[]{DyeColor.BLACK.getSwappedId()};
				}

				switch (lv) {
					case SMALL_BALL:
					default:
						this.explodeBall(0.25, 2, is, js, bl3, bl4);
						break;
					case LARGE_BALL:
						this.explodeBall(0.5, 4, is, js, bl3, bl4);
						break;
					case STAR:
						this.explodeStar(
							0.5,
							new double[][]{
								{0.0, 1.0},
								{0.3455, 0.309},
								{0.9511, 0.309},
								{0.3795918367346939, -0.12653061224489795},
								{0.6122448979591837, -0.8040816326530612},
								{0.0, -0.35918367346938773}
							},
							is,
							js,
							bl3,
							bl4,
							false
						);
						break;
					case CREEPER:
						this.explodeStar(
							0.5,
							new double[][]{
								{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}
							},
							is,
							js,
							bl3,
							bl4,
							true
						);
						break;
					case BURST:
						this.explodeBurst(is, js, bl3, bl4);
				}

				int k = is[0];
				float f = (float)((k & 0xFF0000) >> 16) / 255.0F;
				float g = (float)((k & 0xFF00) >> 8) / 255.0F;
				float h = (float)((k & 0xFF) >> 0) / 255.0F;
				FireworksSparkParticle.Flash flash = new FireworksSparkParticle.Flash(this.field_13424, this.field_13428, this.field_13429, this.field_13430);
				flash.setColor(f, g, h);
				this.manager.method_12256(flash);
			}

			this.age++;
			if (this.age > this.maxAge) {
				if (this.flicker) {
					boolean bl5 = this.isFar();
					Sound sound3 = bl5 ? Sounds.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : Sounds.ENTITY_FIREWORK_ROCKET_TWINKLE;
					this.field_13424
						.playSound(this.field_13428, this.field_13429, this.field_13430, sound3, SoundCategory.AMBIENT, 20.0F, 0.9F + this.field_13438.nextFloat() * 0.15F, true);
				}

				this.method_12251();
			}
		}

		private boolean isFar() {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return minecraftClient.getCameraEntity() == null
				|| !(minecraftClient.getCameraEntity().squaredDistanceTo(this.field_13428, this.field_13429, this.field_13430) < 256.0);
		}

		private void addExplosionParticle(
			double x, double y, double z, double velocityX, double velocityY, double velocityZ, int[] colors, int[] fadeColors, boolean trail, boolean flicker
		) {
			FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(this.field_13424, x, y, z, velocityX, velocityY, velocityZ, this.manager);
			explosion.method_12250(0.99F);
			explosion.setTrail(trail);
			explosion.setFlicker(flicker);
			int i = this.field_13438.nextInt(colors.length);
			explosion.method_12258(colors[i]);
			if (fadeColors.length > 0) {
				explosion.method_12259(fadeColors[this.field_13438.nextInt(fadeColors.length)]);
			}

			this.manager.method_12256(explosion);
		}

		private void explodeBall(double size, int amount, int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
			double d = this.field_13428;
			double e = this.field_13429;
			double f = this.field_13430;

			for (int i = -amount; i <= amount; i++) {
				for (int j = -amount; j <= amount; j++) {
					for (int k = -amount; k <= amount; k++) {
						double g = (double)j + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 0.5;
						double h = (double)i + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 0.5;
						double l = (double)k + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 0.5;
						double m = (double)MathHelper.sqrt(g * g + h * h + l * l) / size + this.field_13438.nextGaussian() * 0.05;
						this.addExplosionParticle(d, e, f, g / m, h / m, l / m, colors, fadeColors, trail, flicker);
						if (i != -amount && i != amount && j != -amount && j != amount) {
							k += amount * 2 - 1;
						}
					}
				}
			}
		}

		private void explodeStar(double size, double[][] pattern, int[] colors, int[] fadeColors, boolean trail, boolean flicker, boolean keepShape) {
			double d = pattern[0][0];
			double e = pattern[0][1];
			this.addExplosionParticle(this.field_13428, this.field_13429, this.field_13430, d * size, e * size, 0.0, colors, fadeColors, trail, flicker);
			float f = this.field_13438.nextFloat() * (float) Math.PI;
			double g = keepShape ? 0.034 : 0.34;

			for (int i = 0; i < 3; i++) {
				double h = (double)f + (double)((float)i * (float) Math.PI) * g;
				double j = d;
				double k = e;

				for (int l = 1; l < pattern.length; l++) {
					double m = pattern[l][0];
					double n = pattern[l][1];

					for (double o = 0.25; o <= 1.0; o += 0.25) {
						double p = (j + (m - j) * o) * size;
						double q = (k + (n - k) * o) * size;
						double r = p * Math.sin(h);
						p *= Math.cos(h);

						for (double s = -1.0; s <= 1.0; s += 2.0) {
							this.addExplosionParticle(this.field_13428, this.field_13429, this.field_13430, p * s, q, r * s, colors, fadeColors, trail, flicker);
						}
					}

					j = m;
					k = n;
				}
			}
		}

		private void explodeBurst(int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
			double d = this.field_13438.nextGaussian() * 0.05;
			double e = this.field_13438.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.velocityX * 0.5 + this.field_13438.nextGaussian() * 0.15 + d;
				double g = this.velocityZ * 0.5 + this.field_13438.nextGaussian() * 0.15 + e;
				double h = this.velocityY * 0.5 + this.field_13438.nextDouble() * 0.5;
				this.addExplosionParticle(this.field_13428, this.field_13429, this.field_13430, f, h, g, colors, fadeColors, trail, flicker);
			}
		}

		@Override
		public int getLayer() {
			return 0;
		}
	}

	public static class Flash extends Particle {
		protected Flash(World world, double d, double e, double f) {
			super(world, d, e, f);
			this.maxAge = 4;
		}

		@Override
		public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
			float f = 0.25F;
			float l = 0.5F;
			float m = 0.125F;
			float n = 0.375F;
			float o = 7.1F * MathHelper.sin(((float)this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
			this.method_12250(0.6F - ((float)this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
			float p = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
			float q = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
			float r = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
			int s = this.method_12243(tickDelta);
			int t = s >> 16 & 65535;
			int u = s & 65535;
			builder.vertex((double)(p - g * o - j * o), (double)(q - h * o), (double)(r - i * o - k * o))
				.texture(0.5, 0.375)
				.color(this.red, this.green, this.blue, this.field_13421)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p - g * o + j * o), (double)(q + h * o), (double)(r - i * o + k * o))
				.texture(0.5, 0.125)
				.color(this.red, this.green, this.blue, this.field_13421)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p + g * o + j * o), (double)(q + h * o), (double)(r + i * o + k * o))
				.texture(0.25, 0.125)
				.color(this.red, this.green, this.blue, this.field_13421)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p + g * o - j * o), (double)(q - h * o), (double)(r + i * o - k * o))
				.texture(0.25, 0.375)
				.color(this.red, this.green, this.blue, this.field_13421)
				.texture2(t, u)
				.next();
		}
	}
}
