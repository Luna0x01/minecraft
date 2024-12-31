package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ParticleManager {
	private static final Identifier PARTICLE_TEXTURE = new Identifier("textures/particle/particles.png");
	protected World world;
	private final ArrayDeque<Particle>[][] field_13439 = new ArrayDeque[4][];
	private final Queue<EmitterParticle> field_13440 = Queues.newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Map<Integer, ParticleFactory> factories = Maps.newHashMap();
	private final Queue<Particle> field_13441 = Queues.newArrayDeque();

	public ParticleManager(World world, TextureManager textureManager) {
		this.world = world;
		this.textureManager = textureManager;

		for (int i = 0; i < 4; i++) {
			this.field_13439[i] = new ArrayDeque[2];

			for (int j = 0; j < 2; j++) {
				this.field_13439[i][j] = Queues.newArrayDeque();
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
		this.registerFactory(ParticleType.DRAGON_BREATH.getId(), new DragonBreathParticle.Factory());
		this.registerFactory(ParticleType.END_ROD.getId(), new EndRodParticle.Factory());
		this.registerFactory(ParticleType.DAMAGE_INDICATOR.getId(), new DamageParticle.Factory());
		this.registerFactory(ParticleType.SWEEP_ATTACK.getId(), new SweepAttackParticle.Factory());
	}

	public void registerFactory(int id, ParticleFactory factory) {
		this.factories.put(id, factory);
	}

	public void addEmitter(Entity entity, ParticleType type) {
		this.field_13440.add(new EmitterParticle(this.world, entity, type));
	}

	@Nullable
	public Particle addParticle(int id, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args) {
		ParticleFactory particleFactory = (ParticleFactory)this.factories.get(id);
		if (particleFactory != null) {
			Particle particle = particleFactory.createParticle(id, this.world, x, y, z, velocityX, velocityY, velocityZ, args);
			if (particle != null) {
				this.method_12256(particle);
				return particle;
			}
		}

		return null;
	}

	public void method_12256(Particle particle) {
		this.field_13441.add(particle);
	}

	public void tick() {
		for (int i = 0; i < 4; i++) {
			this.method_12255(i);
		}

		if (!this.field_13440.isEmpty()) {
			List<EmitterParticle> list = Lists.newArrayList();

			for (EmitterParticle emitterParticle : this.field_13440) {
				emitterParticle.method_12241();
				if (!emitterParticle.method_12253()) {
					list.add(emitterParticle);
				}
			}

			this.field_13440.removeAll(list);
		}

		if (!this.field_13441.isEmpty()) {
			for (Particle particle = (Particle)this.field_13441.poll(); particle != null; particle = (Particle)this.field_13441.poll()) {
				int j = particle.getLayer();
				int k = particle.method_12248() ? 0 : 1;
				if (this.field_13439[j][k].size() >= 16384) {
					this.field_13439[j][k].removeFirst();
				}

				this.field_13439[j][k].add(particle);
			}
		}
	}

	private void method_12255(int i) {
		this.world.profiler.push(i + "");

		for (int j = 0; j < 2; j++) {
			this.world.profiler.push(j + "");
			this.method_12257(this.field_13439[i][j]);
			this.world.profiler.pop();
		}

		this.world.profiler.pop();
	}

	private void method_12257(Queue<Particle> queue) {
		if (!queue.isEmpty()) {
			Iterator<Particle> iterator = queue.iterator();

			while (iterator.hasNext()) {
				Particle particle = (Particle)iterator.next();
				this.tickParticle(particle);
				if (!particle.method_12253()) {
					iterator.remove();
				}
			}
		}
	}

	private void tickParticle(Particle particle) {
		try {
			particle.method_12241();
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking Particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
			final int i = particle.getLayer();
			crashReportSection.add("Particle", new CrashCallable<String>() {
				public String call() throws Exception {
					return particle.toString();
				}
			});
			crashReportSection.add("Particle Type", new CrashCallable<String>() {
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
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(516, 0.003921569F);

		for (final int k = 0; k < 3; k++) {
			for (int l = 0; l < 2; l++) {
				if (!this.field_13439[k][l].isEmpty()) {
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

					for (final Particle particle : this.field_13439[k][l]) {
						try {
							particle.draw(bufferBuilder, entity, tickDelta, f, j, g, h, i);
						} catch (Throwable var18) {
							CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
							CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
							crashReportSection.add("Particle", new CrashCallable<String>() {
								public String call() throws Exception {
									return particle.toString();
								}
							});
							crashReportSection.add("Particle Type", new CrashCallable<String>() {
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
			Queue<Particle> queue = this.field_13439[3][l];
			if (!queue.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();

				for (Particle particle : queue) {
					particle.draw(bufferBuilder, entity, tickDelta, g, k, h, i, j);
				}
			}
		}
	}

	public void setWorld(@Nullable World world) {
		this.world = world;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				this.field_13439[i][j].clear();
			}
		}

		this.field_13440.clear();
	}

	public void addBlockBreakParticles(BlockPos pos, BlockState state) {
		if (state.getMaterial() != Material.AIR) {
			state = state.getBlockState(this.world, pos);
			int i = 4;

			for (int j = 0; j < i; j++) {
				for (int k = 0; k < i; k++) {
					for (int l = 0; l < i; l++) {
						double d = (double)pos.getX() + ((double)j + 0.5) / (double)i;
						double e = (double)pos.getY() + ((double)k + 0.5) / (double)i;
						double f = (double)pos.getZ() + ((double)l + 0.5) / (double)i;
						this.method_12256(
							new BlockDustParticle(this.world, d, e, f, d - (double)pos.getX() - 0.5, e - (double)pos.getY() - 0.5, f - (double)pos.getZ() - 0.5, state)
								.method_12260(pos)
						);
					}
				}
			}
		}
	}

	public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			Box box = blockState.getCollisionBox((BlockView)this.world, pos);
			double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - (double)(f * 2.0F)) + (double)f + box.minX;
			double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - (double)(f * 2.0F)) + (double)f + box.minY;
			double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - (double)(f * 2.0F)) + (double)f + box.minZ;
			if (direction == Direction.DOWN) {
				e = (double)j + box.minY - (double)f;
			}

			if (direction == Direction.UP) {
				e = (double)j + box.maxY + (double)f;
			}

			if (direction == Direction.NORTH) {
				g = (double)k + box.minZ - (double)f;
			}

			if (direction == Direction.SOUTH) {
				g = (double)k + box.maxZ + (double)f;
			}

			if (direction == Direction.WEST) {
				d = (double)i + box.minX - (double)f;
			}

			if (direction == Direction.EAST) {
				d = (double)i + box.maxX + (double)f;
			}

			this.method_12256(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).method_12260(pos).method_12249(0.2F).scale(0.6F));
		}
	}

	public String getDebugString() {
		int i = 0;

		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 2; k++) {
				i += this.field_13439[j][k].size();
			}
		}

		return "" + i;
	}
}
