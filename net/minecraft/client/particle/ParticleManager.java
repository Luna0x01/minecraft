package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleManager {
	private static final Identifier PARTICLE_TEXTURE = new Identifier("textures/particle/particles.png");
	protected World world;
	private List<Particle>[][] particles = new List[4][];
	private List<EmitterParticle> newEmitterParticles = Lists.newArrayList();
	private TextureManager textureManager;
	private Random random = new Random();
	private Map<Integer, ParticleFactory> factories = Maps.newHashMap();

	public ParticleManager(World world, TextureManager textureManager) {
		this.world = world;
		this.textureManager = textureManager;

		for (int i = 0; i < 4; i++) {
			this.particles[i] = new List[2];

			for (int j = 0; j < 2; j++) {
				this.particles[i][j] = Lists.newArrayList();
			}
		}

		this.registerDefaultFactories();
	}

	private void registerDefaultFactories() {
		this.registerFactory(ParticleType.EXPLOSION.getId(), new ExplosionSmokeParticle.Factory());
		this.registerFactory(ParticleType.BUBBLE.getId(), new WaterBubbleParticle.Factory());
		this.registerFactory(ParticleType.WATER.getId(), new WaterSplashParticle.Factory());
		this.registerFactory(ParticleType.WATER_WAKE.getId(), new FishingParticle.Factory());
		this.registerFactory(ParticleType.WATER_DROP.getId(), new RainSplashParticle.Factory());
		this.registerFactory(ParticleType.SUSPENDED.getId(), new SuspendedParticle.Factory());
		this.registerFactory(ParticleType.SUSPENDED_DEPTH.getId(), new VillageParticle.TownAuraFactory());
		this.registerFactory(ParticleType.CRIT.getId(), new DamageParticle.CritFactory());
		this.registerFactory(ParticleType.CRIT_MAGIC.getId(), new DamageParticle.CritMagicFactory());
		this.registerFactory(ParticleType.SMOKE.getId(), new FireSmokeParticle.Factory());
		this.registerFactory(ParticleType.SMOKE_LARGE.getId(), new LargeFireSmokeParticle.Factory());
		this.registerFactory(ParticleType.SPELL.getId(), new SpellParticle.SpellFactory());
		this.registerFactory(ParticleType.INSTANT_SPELL.getId(), new SpellParticle.InstantSpellFactory());
		this.registerFactory(ParticleType.MOB_SPELL.getId(), new SpellParticle.MobSpellFactory());
		this.registerFactory(ParticleType.AMBIENT_MOB_SPELL.getId(), new SpellParticle.AmbientMobSpellFactory());
		this.registerFactory(ParticleType.WITCH_SPELL.getId(), new SpellParticle.WitchSpellFactory());
		this.registerFactory(ParticleType.WATER_DRIP.getId(), new BlockLeakParticle.WaterDripFactory());
		this.registerFactory(ParticleType.LAVA_DRIP.getId(), new BlockLeakParticle.LavaDripFactory());
		this.registerFactory(ParticleType.ANGRY_VILLAGER.getId(), new EmotionParticle.Factory());
		this.registerFactory(ParticleType.HAPPY_VILLAGER.getId(), new VillageParticle.HappyVillagerFactory());
		this.registerFactory(ParticleType.TOWN_AURA.getId(), new VillageParticle.TownAuraFactory());
		this.registerFactory(ParticleType.NOTE.getId(), new NoteParticle.Factory());
		this.registerFactory(ParticleType.NETHER_PORTAL.getId(), new PortalParticle.NetherPortalFactory());
		this.registerFactory(ParticleType.ENCHANTMENT_TABLE.getId(), new EnchantGlyphParticle.Factory());
		this.registerFactory(ParticleType.FIRE.getId(), new FlameParticle.Factory());
		this.registerFactory(ParticleType.LAVA.getId(), new LavaEmberParticle.Factory());
		this.registerFactory(ParticleType.FOOTSTEP.getId(), new FootstepParticle.Factory());
		this.registerFactory(ParticleType.CLOUD.getId(), new CloudParticle.Factory());
		this.registerFactory(ParticleType.REDSTONE.getId(), new RedstoneParticle.Factory());
		this.registerFactory(ParticleType.SNOWBALL.getId(), new SnowballParticle.SnowballFactory());
		this.registerFactory(ParticleType.SNOW_SHOVEL.getId(), new SnowShovelParticle.Factory());
		this.registerFactory(ParticleType.SLIME.getId(), new SnowballParticle.SlimeFactory());
		this.registerFactory(ParticleType.HEART.getId(), new EmotionParticle.HealthFactory());
		this.registerFactory(ParticleType.BARRIER.getId(), new BarrierParticle.Factory());
		this.registerFactory(ParticleType.ITEM_CRACK.getId(), new SnowballParticle.Factory());
		this.registerFactory(ParticleType.BLOCK_CRACK.getId(), new BlockDustParticle.Factory());
		this.registerFactory(ParticleType.BLOCK_DUST.getId(), new BlockParticle.Factory());
		this.registerFactory(ParticleType.HUGE_EXPLOSION.getId(), new ExplosionEmitterParticle.Factory());
		this.registerFactory(ParticleType.LARGE_EXPLOSION.getId(), new LargeExplosionParticle.Factory());
		this.registerFactory(ParticleType.FIREWORK_SPARK.getId(), new FireworksSparkParticle.Factory());
		this.registerFactory(ParticleType.MOB_APPEARANCE.getId(), new ElderGuardianAppearanceParticle.Factory());
	}

	public void registerFactory(int id, ParticleFactory factory) {
		this.factories.put(id, factory);
	}

	public void addEmitter(Entity entity, ParticleType type) {
		this.newEmitterParticles.add(new EmitterParticle(this.world, entity, type));
	}

	public Particle addParticle(int id, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args) {
		ParticleFactory particleFactory = (ParticleFactory)this.factories.get(id);
		if (particleFactory != null) {
			Particle particle = particleFactory.createParticle(id, this.world, x, y, z, velocityX, velocityY, velocityZ, args);
			if (particle != null) {
				this.addParticle(particle);
				return particle;
			}
		}

		return null;
	}

	public void addParticle(Particle particle) {
		int i = particle.getLayer();
		int j = particle.getAlpha() != 1.0F ? 0 : 1;
		if (this.particles[i][j].size() >= 4000) {
			this.particles[i][j].remove(0);
		}

		this.particles[i][j].add(particle);
	}

	public void tick() {
		for (int i = 0; i < 4; i++) {
			this.updateLayer(i);
		}

		List<EmitterParticle> list = Lists.newArrayList();

		for (EmitterParticle emitterParticle : this.newEmitterParticles) {
			emitterParticle.tick();
			if (emitterParticle.removed) {
				list.add(emitterParticle);
			}
		}

		this.newEmitterParticles.removeAll(list);
	}

	private void updateLayer(int index) {
		for (int i = 0; i < 2; i++) {
			this.updateLayer(this.particles[index][i]);
		}
	}

	private void updateLayer(List<Particle> particles) {
		List<Particle> list = Lists.newArrayList();

		for (int i = 0; i < particles.size(); i++) {
			Particle particle = (Particle)particles.get(i);
			this.tickParticle(particle);
			if (particle.removed) {
				list.add(particle);
			}
		}

		particles.removeAll(list);
	}

	private void tickParticle(Particle particle) {
		try {
			particle.tick();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking Particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
			final int i = particle.getLayer();
			crashReportSection.add("Particle", new Callable<String>() {
				public String call() throws Exception {
					return particle.toString();
				}
			});
			crashReportSection.add("Particle Type", new Callable<String>() {
				public String call() throws Exception {
					if (i == 0) {
						return "MISC_TEXTURE";
					} else if (i == 1) {
						return "TERRAIN_TEXTURE";
					} else {
						return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
					}
				}
			});
			throw new CrashException(crashReport);
		}
	}

	public void renderParticles(Entity entity, float tickDelta) {
		float f = Camera.getRotationX();
		float g = Camera.getRotationZ();
		float h = Camera.getRotationYZ();
		float i = Camera.getRotationXY();
		float j = Camera.getRotationXZ();
		Particle.field_1722 = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
		Particle.field_1723 = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
		Particle.field_1724 = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.alphaFunc(516, 0.003921569F);

		for (final int k = 0; k < 3; k++) {
			for (int l = 0; l < 2; l++) {
				if (!this.particles[k][l].isEmpty()) {
					switch (l) {
						case 0:
							GlStateManager.depthMask(false);
							break;
						case 1:
							GlStateManager.depthMask(true);
					}

					switch (k) {
						case 0:
						default:
							this.textureManager.bindTexture(PARTICLE_TEXTURE);
							break;
						case 1:
							this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
					}

					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					bufferBuilder.begin(7, VertexFormats.PARTICLE);

					for (int m = 0; m < this.particles[k][l].size(); m++) {
						final Particle particle = (Particle)this.particles[k][l].get(m);

						try {
							particle.draw(bufferBuilder, entity, tickDelta, f, j, g, h, i);
						} catch (Throwable var18) {
							CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
							CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
							crashReportSection.add("Particle", new Callable<String>() {
								public String call() throws Exception {
									return particle.toString();
								}
							});
							crashReportSection.add("Particle Type", new Callable<String>() {
								public String call() throws Exception {
									if (k == 0) {
										return "MISC_TEXTURE";
									} else if (k == 1) {
										return "TERRAIN_TEXTURE";
									} else {
										return k == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + k;
									}
								}
							});
							throw new CrashException(crashReport);
						}
					}

					tessellator.draw();
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void method_1299(Entity entity, float tickDelta) {
		float f = (float) (Math.PI / 180.0);
		float g = MathHelper.cos(entity.yaw * (float) (Math.PI / 180.0));
		float h = MathHelper.sin(entity.yaw * (float) (Math.PI / 180.0));
		float i = -h * MathHelper.sin(entity.pitch * (float) (Math.PI / 180.0));
		float j = g * MathHelper.sin(entity.pitch * (float) (Math.PI / 180.0));
		float k = MathHelper.cos(entity.pitch * (float) (Math.PI / 180.0));

		for (int l = 0; l < 2; l++) {
			List<Particle> list = this.particles[3][l];
			if (!list.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();

				for (int m = 0; m < list.size(); m++) {
					Particle particle = (Particle)list.get(m);
					particle.draw(bufferBuilder, entity, tickDelta, g, k, h, i, j);
				}
			}
		}
	}

	public void setWorld(World world) {
		this.world = world;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				this.particles[i][j].clear();
			}
		}

		this.newEmitterParticles.clear();
	}

	public void addBlockBreakParticles(BlockPos pos, BlockState state) {
		if (state.getBlock().getMaterial() != Material.AIR) {
			state = state.getBlock().getBlockState(state, this.world, pos);
			int i = 4;

			for (int j = 0; j < i; j++) {
				for (int k = 0; k < i; k++) {
					for (int l = 0; l < i; l++) {
						double d = (double)pos.getX() + ((double)j + 0.5) / (double)i;
						double e = (double)pos.getY() + ((double)k + 0.5) / (double)i;
						double f = (double)pos.getZ() + ((double)l + 0.5) / (double)i;
						this.addParticle(
							new BlockDustParticle(this.world, d, e, f, d - (double)pos.getX() - 0.5, e - (double)pos.getY() - 0.5, f - (double)pos.getZ() - 0.5, state)
								.setBlockPos(pos)
						);
					}
				}
			}
		}
	}

	public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
		BlockState blockState = this.world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block.getBlockType() != -1) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			double d = (double)i + this.random.nextDouble() * (block.getMaxX() - block.getMinX() - (double)(f * 2.0F)) + (double)f + block.getMinX();
			double e = (double)j + this.random.nextDouble() * (block.getMaxY() - block.getMinY() - (double)(f * 2.0F)) + (double)f + block.getMinY();
			double g = (double)k + this.random.nextDouble() * (block.getMaxZ() - block.getMinZ() - (double)(f * 2.0F)) + (double)f + block.getMinZ();
			if (direction == Direction.DOWN) {
				e = (double)j + block.getMinY() - (double)f;
			}

			if (direction == Direction.UP) {
				e = (double)j + block.getMaxY() + (double)f;
			}

			if (direction == Direction.NORTH) {
				g = (double)k + block.getMinZ() - (double)f;
			}

			if (direction == Direction.SOUTH) {
				g = (double)k + block.getMaxZ() + (double)f;
			}

			if (direction == Direction.WEST) {
				d = (double)i + block.getMinX() - (double)f;
			}

			if (direction == Direction.EAST) {
				d = (double)i + block.getMaxX() + (double)f;
			}

			this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).setBlockPos(pos).move(0.2F).scale(0.6F));
		}
	}

	public void moveToAlphaLayer(Particle particle) {
		this.moveToLayer(particle, 1, 0);
	}

	public void moveToNoAlphaLayer(Particle particle) {
		this.moveToLayer(particle, 0, 1);
	}

	private void moveToLayer(Particle particle, int from, int to) {
		for (int i = 0; i < 4; i++) {
			if (this.particles[i][from].contains(particle)) {
				this.particles[i][from].remove(particle);
				this.particles[i][to].add(particle);
			}
		}
	}

	public String getDebugString() {
		int i = 0;

		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 2; k++) {
				i += this.particles[j][k].size();
			}
		}

		return "" + i;
	}
}
