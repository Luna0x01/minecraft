package net.minecraft.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FireworksSparkParticle {
	public static class Explosion extends Particle {
		private int textureIndex = 160;
		private boolean trail;
		private boolean flicker;
		private final ParticleManager manager;
		private float fadeRed;
		private float fadeGreen;
		private float fadeBlue;
		private boolean fades;

		public Explosion(World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager) {
			super(world, d, e, f);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.manager = particleManager;
			this.scale *= 0.75F;
			this.maxAge = 48 + this.random.nextInt(12);
			this.noClip = false;
		}

		public void setTrail(boolean trail) {
			this.trail = trail;
		}

		public void setFlicker(boolean flicker) {
			this.flicker = flicker;
		}

		public void setColor(int color) {
			float f = (float)((color & 0xFF0000) >> 16) / 255.0F;
			float g = (float)((color & 0xFF00) >> 8) / 255.0F;
			float h = (float)((color & 0xFF) >> 0) / 255.0F;
			float i = 1.0F;
			this.setColor(f * i, g * i, h * i);
		}

		public void fade(int color) {
			this.fadeRed = (float)((color & 0xFF0000) >> 16) / 255.0F;
			this.fadeGreen = (float)((color & 0xFF00) >> 8) / 255.0F;
			this.fadeBlue = (float)((color & 0xFF) >> 0) / 255.0F;
			this.fades = true;
		}

		@Override
		public Box getBox() {
			return null;
		}

		@Override
		public boolean isPushable() {
			return false;
		}

		@Override
		public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
			if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
				super.draw(builder, entity, tickDelta, g, h, i, j, k);
			}
		}

		@Override
		public void tick() {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			if (this.age++ >= this.maxAge) {
				this.remove();
			}

			if (this.age > this.maxAge / 2) {
				this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
				if (this.fades) {
					this.red = this.red + (this.fadeRed - this.red) * 0.2F;
					this.green = this.green + (this.fadeGreen - this.green) * 0.2F;
					this.blue = this.blue + (this.fadeBlue - this.blue) * 0.2F;
				}
			}

			this.setMiscTexture(this.textureIndex + (7 - this.age * 8 / this.maxAge));
			this.velocityY -= 0.004;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.91F;
			this.velocityY *= 0.91F;
			this.velocityZ *= 0.91F;
			if (this.onGround) {
				this.velocityX *= 0.7F;
				this.velocityZ *= 0.7F;
			}

			if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
				FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.manager);
				explosion.setColorAlpha(0.99F);
				explosion.setColor(this.red, this.green, this.blue);
				explosion.age = explosion.maxAge / 2;
				if (this.fades) {
					explosion.fades = true;
					explosion.fadeRed = this.fadeRed;
					explosion.fadeGreen = this.fadeGreen;
					explosion.fadeBlue = this.fadeBlue;
				}

				explosion.flicker = this.flicker;
				this.manager.addParticle(explosion);
			}
		}

		@Override
		public int getLightmapCoordinates(float f) {
			return 15728880;
		}

		@Override
		public float getBrightnessAtEyes(float f) {
			return 1.0F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(
				world, x, y, z, velocityX, velocityY, velocityZ, MinecraftClient.getInstance().particleManager
			);
			explosion.setColorAlpha(0.99F);
			return explosion;
		}
	}

	public static class FireworkParticle extends Particle {
		private int age;
		private final ParticleManager manager;
		private NbtList explosions;
		boolean flicker;

		public FireworkParticle(World world, double d, double e, double f, double g, double h, double i, ParticleManager particleManager, NbtCompound nbtCompound) {
			super(world, d, e, f, 0.0, 0.0, 0.0);
			this.velocityX = g;
			this.velocityY = h;
			this.velocityZ = i;
			this.manager = particleManager;
			this.maxAge = 8;
			if (nbtCompound != null) {
				this.explosions = nbtCompound.getList("Explosions", 10);
				if (this.explosions.size() == 0) {
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
		public void tick() {
			if (this.age == 0 && this.explosions != null) {
				boolean bl = this.isFar();
				boolean bl2 = false;
				if (this.explosions.size() >= 3) {
					bl2 = true;
				} else {
					for (int i = 0; i < this.explosions.size(); i++) {
						NbtCompound nbtCompound = this.explosions.getCompound(i);
						if (nbtCompound.getByte("Type") == 1) {
							bl2 = true;
							break;
						}
					}
				}

				String string = "fireworks." + (bl2 ? "largeBlast" : "blast") + (bl ? "_far" : "");
				this.world.playSound(this.x, this.y, this.z, string, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
			}

			if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
				int j = this.age / 2;
				NbtCompound nbtCompound2 = this.explosions.getCompound(j);
				int k = nbtCompound2.getByte("Type");
				boolean bl3 = nbtCompound2.getBoolean("Trail");
				boolean bl4 = nbtCompound2.getBoolean("Flicker");
				int[] is = nbtCompound2.getIntArray("Colors");
				int[] js = nbtCompound2.getIntArray("FadeColors");
				if (is.length == 0) {
					is = new int[]{DyeItem.COLORS[0]};
				}

				if (k == 1) {
					this.explodeBall(0.5, 4, is, js, bl3, bl4);
				} else if (k == 2) {
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
				} else if (k == 3) {
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
				} else if (k == 4) {
					this.explodeBurst(is, js, bl3, bl4);
				} else {
					this.explodeBall(0.25, 2, is, js, bl3, bl4);
				}

				int l = is[0];
				float f = (float)((l & 0xFF0000) >> 16) / 255.0F;
				float g = (float)((l & 0xFF00) >> 8) / 255.0F;
				float h = (float)((l & 0xFF) >> 0) / 255.0F;
				FireworksSparkParticle.Flash flash = new FireworksSparkParticle.Flash(this.world, this.x, this.y, this.z);
				flash.setColor(f, g, h);
				this.manager.addParticle(flash);
			}

			this.age++;
			if (this.age > this.maxAge) {
				if (this.flicker) {
					boolean bl5 = this.isFar();
					String string2 = "fireworks." + (bl5 ? "twinkle_far" : "twinkle");
					this.world.playSound(this.x, this.y, this.z, string2, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
				}

				this.remove();
			}
		}

		private boolean isFar() {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			return minecraftClient == null
				|| minecraftClient.getCameraEntity() == null
				|| !(minecraftClient.getCameraEntity().squaredDistanceTo(this.x, this.y, this.z) < 256.0);
		}

		private void addExplosionParticle(
			double x, double y, double z, double velocityX, double velocityY, double velocityZ, int[] colors, int[] fadeColors, boolean trail, boolean flicker
		) {
			FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(this.world, x, y, z, velocityX, velocityY, velocityZ, this.manager);
			explosion.setColorAlpha(0.99F);
			explosion.setTrail(trail);
			explosion.setFlicker(flicker);
			int i = this.random.nextInt(colors.length);
			explosion.setColor(colors[i]);
			if (fadeColors != null && fadeColors.length > 0) {
				explosion.fade(fadeColors[this.random.nextInt(fadeColors.length)]);
			}

			this.manager.addParticle(explosion);
		}

		private void explodeBall(double size, int amount, int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
			double d = this.x;
			double e = this.y;
			double f = this.z;

			for (int i = -amount; i <= amount; i++) {
				for (int j = -amount; j <= amount; j++) {
					for (int k = -amount; k <= amount; k++) {
						double g = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double h = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double l = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
						double m = (double)MathHelper.sqrt(g * g + h * h + l * l) / size + this.random.nextGaussian() * 0.05;
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
			this.addExplosionParticle(this.x, this.y, this.z, d * size, e * size, 0.0, colors, fadeColors, trail, flicker);
			float f = this.random.nextFloat() * (float) Math.PI;
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
							this.addExplosionParticle(this.x, this.y, this.z, p * s, q, r * s, colors, fadeColors, trail, flicker);
						}
					}

					j = m;
					k = n;
				}
			}
		}

		private void explodeBurst(int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
			double d = this.random.nextGaussian() * 0.05;
			double e = this.random.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + d;
				double g = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + e;
				double h = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
				this.addExplosionParticle(this.x, this.y, this.z, f, h, g, colors, fadeColors, trail, flicker);
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
			this.alpha = 0.6F - ((float)this.age + tickDelta - 1.0F) * 0.25F * 0.5F;
			float p = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - field_1722);
			float q = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - field_1723);
			float r = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - field_1724);
			int s = this.getLightmapCoordinates(tickDelta);
			int t = s >> 16 & 65535;
			int u = s & 65535;
			builder.vertex((double)(p - g * o - j * o), (double)(q - h * o), (double)(r - i * o - k * o))
				.texture(0.5, 0.375)
				.color(this.red, this.green, this.blue, this.alpha)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p - g * o + j * o), (double)(q + h * o), (double)(r - i * o + k * o))
				.texture(0.5, 0.125)
				.color(this.red, this.green, this.blue, this.alpha)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p + g * o + j * o), (double)(q + h * o), (double)(r + i * o + k * o))
				.texture(0.25, 0.125)
				.color(this.red, this.green, this.blue, this.alpha)
				.texture2(t, u)
				.next();
			builder.vertex((double)(p + g * o - j * o), (double)(q - h * o), (double)(r + i * o - k * o))
				.texture(0.25, 0.375)
				.color(this.red, this.green, this.blue, this.alpha)
				.texture2(t, u)
				.next();
		}
	}
}
