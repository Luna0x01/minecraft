package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4204;
import net.minecraft.class_4206;
import net.minecraft.class_4210;
import net.minecraft.class_4214;
import net.minecraft.class_4342;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.World;

public class ParticleManager {
	private static final Identifier PARTICLE_TEXTURE = new Identifier("textures/particle/particles.png");
	protected World world;
	private final ArrayDeque<Particle>[][] field_13439 = new ArrayDeque[4][];
	private final Queue<EmitterParticle> field_13440 = Queues.newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Int2ObjectMap<ParticleFactory<?>> field_20632 = new Int2ObjectOpenHashMap();
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
		this.method_19016(class_4342.field_21375, new SpellParticle.AmbientMobSpellFactory());
		this.method_19016(class_4342.field_21376, new EmotionParticle.Factory());
		this.method_19016(class_4342.field_21377, new BarrierParticle.Factory());
		this.method_19016(class_4342.BLOCK, new BlockDustParticle.Factory());
		this.method_19016(class_4342.field_21379, new WaterBubbleParticle.Factory());
		this.method_19016(class_4342.field_21380, new class_4204.class_4205());
		this.method_19016(class_4342.field_21370, new class_4206.class_4207());
		this.method_19016(class_4342.field_21381, new CloudParticle.Factory());
		this.method_19016(class_4342.field_21382, new DamageParticle.CritFactory());
		this.method_19016(class_4342.field_21371, new class_4214.class_4215());
		this.method_19016(class_4342.field_21383, new DamageParticle.Factory());
		this.method_19016(class_4342.field_21384, new DragonBreathParticle.Factory());
		this.method_19016(class_4342.field_21374, new VillageParticle.class_4212());
		this.method_19016(class_4342.field_21385, new BlockLeakParticle.LavaDripFactory());
		this.method_19016(class_4342.field_21386, new BlockLeakParticle.WaterDripFactory());
		this.method_19016(class_4342.DUST, new RedstoneParticle.Factory());
		this.method_19016(class_4342.field_21388, new SpellParticle.SpellFactory());
		this.method_19016(class_4342.field_21389, new ElderGuardianAppearanceParticle.Factory());
		this.method_19016(class_4342.field_21390, new DamageParticle.CritMagicFactory());
		this.method_19016(class_4342.field_21391, new EnchantGlyphParticle.Factory());
		this.method_19016(class_4342.field_21392, new EndRodParticle.Factory());
		this.method_19016(class_4342.field_21393, new SpellParticle.MobSpellFactory());
		this.method_19016(class_4342.field_21394, new ExplosionEmitterParticle.Factory());
		this.method_19016(class_4342.field_21395, new LargeExplosionParticle.Factory());
		this.method_19016(class_4342.FALLING_DUST, new FallingDustParticle.Factory());
		this.method_19016(class_4342.field_21397, new FireworksSparkParticle.Factory());
		this.method_19016(class_4342.field_21398, new FishingParticle.Factory());
		this.method_19016(class_4342.field_21399, new FlameParticle.Factory());
		this.method_19016(class_4342.field_21400, new VillageParticle.HappyVillagerFactory());
		this.method_19016(class_4342.field_21351, new EmotionParticle.HealthFactory());
		this.method_19016(class_4342.field_21352, new SpellParticle.InstantSpellFactory());
		this.method_19016(class_4342.ITEM, new SnowballParticle.Factory());
		this.method_19016(class_4342.field_21354, new SnowballParticle.SlimeFactory());
		this.method_19016(class_4342.field_21355, new SnowballParticle.SnowballFactory());
		this.method_19016(class_4342.field_21356, new LargeFireSmokeParticle.Factory());
		this.method_19016(class_4342.field_21357, new LavaEmberParticle.Factory());
		this.method_19016(class_4342.field_21358, new VillageParticle.TownAuraFactory());
		this.method_19016(class_4342.field_21373, new EnchantGlyphParticle.class_4208());
		this.method_19016(class_4342.field_21359, new NoteParticle.Factory());
		this.method_19016(class_4342.field_21360, new ExplosionSmokeParticle.Factory());
		this.method_19016(class_4342.field_21361, new PortalParticle.NetherPortalFactory());
		this.method_19016(class_4342.field_21362, new RainSplashParticle.Factory());
		this.method_19016(class_4342.field_21363, new FireSmokeParticle.Factory());
		this.method_19016(class_4342.field_21364, new SpitParticle.Factory());
		this.method_19016(class_4342.field_21365, new SweepAttackParticle.Factory());
		this.method_19016(class_4342.field_21366, new TotemParticle.Factory());
		this.method_19016(class_4342.field_21372, new class_4210.class_4211());
		this.method_19016(class_4342.field_21367, new SuspendedParticle.Factory());
		this.method_19016(class_4342.field_21368, new WaterSplashParticle.Factory());
		this.method_19016(class_4342.field_21369, new SpellParticle.WitchSpellFactory());
	}

	public <T extends ParticleEffect> void method_19016(ParticleType<T> particleType, ParticleFactory<T> particleFactory) {
		this.field_20632.put(Registry.PARTICLE_TYPE.getRawId(particleType), particleFactory);
	}

	public void method_9707(Entity entity, ParticleEffect particleEffect) {
		this.field_13440.add(new EmitterParticle(this.world, entity, particleEffect));
	}

	public void method_13843(Entity entity, ParticleEffect particleEffect, int i) {
		this.field_13440.add(new EmitterParticle(this.world, entity, particleEffect, i));
	}

	@Nullable
	public Particle method_19015(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
		Particle particle = this.method_19018(particleEffect, d, e, f, g, h, i);
		if (particle != null) {
			this.method_12256(particle);
			return particle;
		} else {
			return null;
		}
	}

	@Nullable
	private <T extends ParticleEffect> Particle method_19018(T particleEffect, double d, double e, double f, double g, double h, double i) {
		ParticleFactory<T> particleFactory = (ParticleFactory<T>)this.field_20632
			.get(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleEffect>)particleEffect.particleType()));
		return particleFactory == null ? null : particleFactory.method_19020(particleEffect, this.world, d, e, f, g, h, i);
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
		this.world.profiler.push(String.valueOf(i));

		for (int j = 0; j < 2; j++) {
			this.world.profiler.push(String.valueOf(j));
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
			int i = particle.getLayer();
			crashReportSection.add("Particle", particle::toString);
			crashReportSection.add("Particle Type", (CrashCallable<String>)(() -> {
				if (i == 0) {
					return "MISC_TEXTURE";
				} else if (i == 1) {
					return "TERRAIN_TEXTURE";
				} else {
					return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
				}
			}));
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
		Particle.field_14949 = entity.getRotationVector(tickDelta);
		GlStateManager.enableBlend();
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(516, 0.003921569F);

		for (int k = 0; k < 3; k++) {
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

					for (Particle particle : this.field_13439[k][l]) {
						try {
							particle.draw(bufferBuilder, entity, tickDelta, f, j, g, h, i);
						} catch (Throwable var18) {
							CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
							CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
							int m = k;
							crashReportSection.add("Particle", particle::toString);
							crashReportSection.add("Particle Type", (CrashCallable<String>)(() -> {
								if (m == 0) {
									return "MISC_TEXTURE";
								} else if (m == 1) {
									return "TERRAIN_TEXTURE";
								} else {
									return m == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + m;
								}
							}));
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
		float f = Camera.getRotationX();
		float g = Camera.getRotationZ();
		float h = Camera.getRotationYZ();
		float i = Camera.getRotationXY();
		float j = Camera.getRotationXZ();
		Particle.field_1722 = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
		Particle.field_1723 = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
		Particle.field_1724 = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
		Particle.field_14949 = entity.getRotationVector(tickDelta);

		for (int k = 0; k < 2; k++) {
			Queue<Particle> queue = this.field_13439[3][k];
			if (!queue.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();

				for (Particle particle : queue) {
					particle.draw(bufferBuilder, entity, tickDelta, f, j, g, h, i);
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
		if (!state.isAir()) {
			VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
			double d = 0.25;
			voxelShape.forEachBox(
				(dx, e, f, g, h, i) -> {
					double j = Math.min(1.0, g - dx);
					double k = Math.min(1.0, h - e);
					double l = Math.min(1.0, i - f);
					int m = Math.max(2, MathHelper.ceil(j / 0.25));
					int n = Math.max(2, MathHelper.ceil(k / 0.25));
					int o = Math.max(2, MathHelper.ceil(l / 0.25));

					for (int p = 0; p < m; p++) {
						for (int q = 0; q < n; q++) {
							for (int r = 0; r < o; r++) {
								double s = ((double)p + 0.5) / (double)m;
								double t = ((double)q + 0.5) / (double)n;
								double u = ((double)r + 0.5) / (double)o;
								double v = s * j + dx;
								double w = t * k + e;
								double x = u * l + f;
								this.method_12256(
									new BlockDustParticle(this.world, (double)pos.getX() + v, (double)pos.getY() + w, (double)pos.getZ() + x, s - 0.5, t - 0.5, u - 0.5, state)
										.method_12260(pos)
								);
							}
						}
					}
				}
			);
		}
	}

	public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			Box box = blockState.getOutlineShape(this.world, pos).getBoundingBox();
			double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - 0.2F) + 0.1F + box.minX;
			double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - 0.2F) + 0.1F + box.minY;
			double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - 0.2F) + 0.1F + box.minZ;
			if (direction == Direction.DOWN) {
				e = (double)j + box.minY - 0.1F;
			}

			if (direction == Direction.UP) {
				e = (double)j + box.maxY + 0.1F;
			}

			if (direction == Direction.NORTH) {
				g = (double)k + box.minZ - 0.1F;
			}

			if (direction == Direction.SOUTH) {
				g = (double)k + box.maxZ + 0.1F;
			}

			if (direction == Direction.WEST) {
				d = (double)i + box.minX - 0.1F;
			}

			if (direction == Direction.EAST) {
				d = (double)i + box.maxX + 0.1F;
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

		return String.valueOf(i);
	}
}
