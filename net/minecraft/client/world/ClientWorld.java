package net.minecraft.client.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class ClientWorld extends World {
	private final List<Entity> globalEntities = Lists.newArrayList();
	private final Int2ObjectMap<Entity> regularEntities = new Int2ObjectOpenHashMap();
	private final ClientPlayNetworkHandler netHandler;
	private final WorldRenderer worldRenderer;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
	private int ticksUntilCaveAmbientSound = this.random.nextInt(12000);
	private Scoreboard scoreboard = new Scoreboard();
	private final Map<String, MapState> mapStates = Maps.newHashMap();
	private int lightningTicksLeft;
	private final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = Util.make(new Object2ObjectArrayMap(3), object2ObjectArrayMap -> {
		object2ObjectArrayMap.put(BiomeColors.GRASS_COLOR, new BiomeColorCache());
		object2ObjectArrayMap.put(BiomeColors.FOLIAGE_COLOR, new BiomeColorCache());
		object2ObjectArrayMap.put(BiomeColors.WATER_COLOR, new BiomeColorCache());
	});

	public ClientWorld(
		ClientPlayNetworkHandler clientPlayNetworkHandler, LevelInfo levelInfo, DimensionType dimensionType, int i, Profiler profiler, WorldRenderer worldRenderer
	) {
		super(new LevelProperties(levelInfo, "MpServer"), dimensionType, (world, dimension) -> new ClientChunkManager((ClientWorld)world, i), profiler, true);
		this.netHandler = clientPlayNetworkHandler;
		this.worldRenderer = worldRenderer;
		this.setSpawnPos(new BlockPos(8, 64, 8));
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
	}

	public void tick(BooleanSupplier booleanSupplier) {
		this.getWorldBorder().tick();
		this.tickTime();
		this.getProfiler().push("blocks");
		this.chunkManager.tick(booleanSupplier);
		this.tickCaveAmbientSound();
		this.getProfiler().pop();
	}

	public Iterable<Entity> getEntities() {
		return Iterables.concat(this.regularEntities.values(), this.globalEntities);
	}

	public void tickEntities() {
		Profiler profiler = this.getProfiler();
		profiler.push("entities");
		profiler.push("global");

		for (int i = 0; i < this.globalEntities.size(); i++) {
			Entity entity = (Entity)this.globalEntities.get(i);
			this.tickEntity(entityx -> {
				entityx.age++;
				entityx.tick();
			}, entity);
			if (entity.removed) {
				this.globalEntities.remove(i--);
			}
		}

		profiler.swap("regular");
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity2 = (Entity)entry.getValue();
			if (!entity2.hasVehicle()) {
				profiler.push("tick");
				if (!entity2.removed) {
					this.tickEntity(this::tickEntity, entity2);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity2.removed) {
					objectIterator.remove();
					this.finishRemovingEntity(entity2);
				}

				profiler.pop();
			}
		}

		profiler.pop();
		this.tickBlockEntities();
		profiler.pop();
	}

	public void tickEntity(Entity entity) {
		if (entity instanceof PlayerEntity || this.getChunkManager().shouldTickEntity(entity)) {
			entity.resetPosition(entity.getX(), entity.getY(), entity.getZ());
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (entity.updateNeeded || entity.isSpectator()) {
				entity.age++;
				this.getProfiler().push((Supplier<String>)(() -> Registry.field_11145.getId(entity.getType()).toString()));
				entity.tick();
				this.getProfiler().pop();
			}

			this.checkChunk(entity);
			if (entity.updateNeeded) {
				for (Entity entity2 : entity.getPassengerList()) {
					this.tickPassenger(entity, entity2);
				}
			}
		}
	}

	public void tickPassenger(Entity entity, Entity entity2) {
		if (entity2.removed || entity2.getVehicle() != entity) {
			entity2.stopRiding();
		} else if (entity2 instanceof PlayerEntity || this.getChunkManager().shouldTickEntity(entity2)) {
			entity2.resetPosition(entity2.getX(), entity2.getY(), entity2.getZ());
			entity2.prevYaw = entity2.yaw;
			entity2.prevPitch = entity2.pitch;
			if (entity2.updateNeeded) {
				entity2.age++;
				entity2.tickRiding();
			}

			this.checkChunk(entity2);
			if (entity2.updateNeeded) {
				for (Entity entity3 : entity2.getPassengerList()) {
					this.tickPassenger(entity2, entity3);
				}
			}
		}
	}

	public void checkChunk(Entity entity) {
		this.getProfiler().push("chunkCheck");
		int i = MathHelper.floor(entity.getX() / 16.0);
		int j = MathHelper.floor(entity.getY() / 16.0);
		int k = MathHelper.floor(entity.getZ() / 16.0);
		if (!entity.updateNeeded || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
			if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
				this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
			}

			if (!entity.teleportRequested() && !this.isChunkLoaded(i, k)) {
				entity.updateNeeded = false;
			} else {
				this.getChunk(i, k).addEntity(entity);
			}
		}

		this.getProfiler().pop();
	}

	public void unloadBlockEntities(WorldChunk worldChunk) {
		this.unloadedBlockEntities.addAll(worldChunk.getBlockEntities().values());
		this.chunkManager.getLightingProvider().setLightEnabled(worldChunk.getPos(), false);
	}

	public void resetChunkColor(int i, int j) {
		this.colorCache.forEach((colorResolver, biomeColorCache) -> biomeColorCache.reset(i, j));
	}

	public void reloadColor() {
		this.colorCache.forEach((colorResolver, biomeColorCache) -> biomeColorCache.reset());
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return true;
	}

	private void tickCaveAmbientSound() {
		if (this.client.player != null) {
			if (this.ticksUntilCaveAmbientSound > 0) {
				this.ticksUntilCaveAmbientSound--;
			} else {
				BlockPos blockPos = new BlockPos(this.client.player);
				BlockPos blockPos2 = blockPos.add(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
				double d = blockPos.getSquaredDistance(blockPos2);
				if (d >= 4.0 && d <= 256.0) {
					BlockState blockState = this.getBlockState(blockPos2);
					if (blockState.isAir() && this.getBaseLightLevel(blockPos2, 0) <= this.random.nextInt(8) && this.getLightLevel(LightType.field_9284, blockPos2) <= 0) {
						this.playSound(
							(double)blockPos2.getX() + 0.5,
							(double)blockPos2.getY() + 0.5,
							(double)blockPos2.getZ() + 0.5,
							SoundEvents.field_14564,
							SoundCategory.field_15256,
							0.7F,
							0.8F + this.random.nextFloat() * 0.2F,
							false
						);
						this.ticksUntilCaveAmbientSound = this.random.nextInt(12000) + 6000;
					}
				}
			}
		}
	}

	public int getRegularEntityCount() {
		return this.regularEntities.size();
	}

	public void addLightning(LightningEntity lightningEntity) {
		this.globalEntities.add(lightningEntity);
	}

	public void addPlayer(int i, AbstractClientPlayerEntity abstractClientPlayerEntity) {
		this.addEntityPrivate(i, abstractClientPlayerEntity);
		this.players.add(abstractClientPlayerEntity);
	}

	public void addEntity(int i, Entity entity) {
		this.addEntityPrivate(i, entity);
	}

	private void addEntityPrivate(int i, Entity entity) {
		this.removeEntity(i);
		this.regularEntities.put(i, entity);
		this.getChunkManager()
			.getChunk(MathHelper.floor(entity.getX() / 16.0), MathHelper.floor(entity.getZ() / 16.0), ChunkStatus.field_12803, true)
			.addEntity(entity);
	}

	public void removeEntity(int i) {
		Entity entity = (Entity)this.regularEntities.remove(i);
		if (entity != null) {
			entity.remove();
			this.finishRemovingEntity(entity);
		}
	}

	private void finishRemovingEntity(Entity entity) {
		entity.detach();
		if (entity.updateNeeded) {
			this.getChunk(entity.chunkX, entity.chunkZ).remove(entity);
		}

		this.players.remove(entity);
	}

	public void addEntitiesToChunk(WorldChunk worldChunk) {
		ObjectIterator var2 = this.regularEntities.int2ObjectEntrySet().iterator();

		while (var2.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)var2.next();
			Entity entity = (Entity)entry.getValue();
			int i = MathHelper.floor(entity.getX() / 16.0);
			int j = MathHelper.floor(entity.getZ() / 16.0);
			if (i == worldChunk.getPos().x && j == worldChunk.getPos().z) {
				worldChunk.addEntity(entity);
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityById(int i) {
		return (Entity)this.regularEntities.get(i);
	}

	public void setBlockStateWithoutNeighborUpdates(BlockPos blockPos, BlockState blockState) {
		this.setBlockState(blockPos, blockState, 19);
	}

	@Override
	public void disconnect() {
		this.netHandler.getConnection().disconnect(new TranslatableText("multiplayer.status.quitting"));
	}

	public void doRandomBlockDisplayTicks(int i, int j, int k) {
		int l = 32;
		Random random = new Random();
		boolean bl = false;
		if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9220) {
			for (ItemStack itemStack : this.client.player.getItemsHand()) {
				if (itemStack.getItem() == Blocks.field_10499.asItem()) {
					bl = true;
					break;
				}
			}
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < 667; m++) {
			this.randomBlockDisplayTick(i, j, k, 16, random, bl, mutable);
			this.randomBlockDisplayTick(i, j, k, 32, random, bl, mutable);
		}
	}

	public void randomBlockDisplayTick(int i, int j, int k, int l, Random random, boolean bl, BlockPos.Mutable mutable) {
		int m = i + this.random.nextInt(l) - this.random.nextInt(l);
		int n = j + this.random.nextInt(l) - this.random.nextInt(l);
		int o = k + this.random.nextInt(l) - this.random.nextInt(l);
		mutable.set(m, n, o);
		BlockState blockState = this.getBlockState(mutable);
		blockState.getBlock().randomDisplayTick(blockState, this, mutable, random);
		FluidState fluidState = this.getFluidState(mutable);
		if (!fluidState.isEmpty()) {
			fluidState.randomDisplayTick(this, mutable, random);
			ParticleEffect particleEffect = fluidState.getParticle();
			if (particleEffect != null && this.random.nextInt(10) == 0) {
				boolean bl2 = blockState.isSideSolidFullSquare(this, mutable, Direction.field_11033);
				BlockPos blockPos = mutable.down();
				this.addParticle(blockPos, this.getBlockState(blockPos), particleEffect, bl2);
			}
		}

		if (bl && blockState.getBlock() == Blocks.field_10499) {
			this.addParticle(ParticleTypes.field_11235, (double)m + 0.5, (double)n + 0.5, (double)o + 0.5, 0.0, 0.0, 0.0);
		}
	}

	private void addParticle(BlockPos blockPos, BlockState blockState, ParticleEffect particleEffect, boolean bl) {
		if (blockState.getFluidState().isEmpty()) {
			VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.field_11052);
			if (d < 1.0) {
				if (bl) {
					this.addParticle(
						(double)blockPos.getX(),
						(double)(blockPos.getX() + 1),
						(double)blockPos.getZ(),
						(double)(blockPos.getZ() + 1),
						(double)(blockPos.getY() + 1) - 0.05,
						particleEffect
					);
				}
			} else if (!blockState.matches(BlockTags.field_15490)) {
				double e = voxelShape.getMinimum(Direction.Axis.field_11052);
				if (e > 0.0) {
					this.addParticle(blockPos, particleEffect, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.down();
					BlockState blockState2 = this.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.field_11052);
					if (f < 1.0 && blockState2.getFluidState().isEmpty()) {
						this.addParticle(blockPos, particleEffect, voxelShape, (double)blockPos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void addParticle(BlockPos blockPos, ParticleEffect particleEffect, VoxelShape voxelShape, double d) {
		this.addParticle(
			(double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.field_11048),
			(double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.field_11048),
			(double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.field_11051),
			(double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.field_11051),
			d,
			particleEffect
		);
	}

	private void addParticle(double d, double e, double f, double g, double h, ParticleEffect particleEffect) {
		this.addParticle(particleEffect, MathHelper.lerp(this.random.nextDouble(), d, e), h, MathHelper.lerp(this.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
	}

	public void finishRemovingEntities() {
		ObjectIterator<Entry<Entity>> objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<Entity> entry = (Entry<Entity>)objectIterator.next();
			Entity entity = (Entity)entry.getValue();
			if (entity.removed) {
				objectIterator.remove();
				this.finishRemovingEntity(entity);
			}
		}
	}

	@Override
	public CrashReportSection addDetailsToCrashReport(CrashReport crashReport) {
		CrashReportSection crashReportSection = super.addDetailsToCrashReport(crashReport);
		crashReportSection.add("Server brand", (CrashCallable<String>)(() -> this.client.player.getServerBrand()));
		crashReportSection.add(
			"Server type", (CrashCallable<String>)(() -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server")
		);
		return crashReportSection;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h) {
		if (playerEntity == this.client.player) {
			this.playSound(d, e, f, soundEvent, soundCategory, g, h, false);
		}
	}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		if (playerEntity == this.client.player) {
			this.client.getSoundManager().play(new EntityTrackingSoundInstance(soundEvent, soundCategory, entity));
		}
	}

	public void playSound(BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, boolean bl) {
		this.playSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundCategory, f, g, bl);
	}

	@Override
	public void playSound(double d, double e, double f, SoundEvent soundEvent, SoundCategory soundCategory, float g, float h, boolean bl) {
		double i = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(d, e, f);
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(soundEvent, soundCategory, g, h, (float)d, (float)e, (float)f);
		if (bl && i > 100.0) {
			double j = Math.sqrt(i) / 40.0;
			this.client.getSoundManager().play(positionedSoundInstance, (int)(j * 20.0));
		} else {
			this.client.getSoundManager().play(positionedSoundInstance);
		}
	}

	@Override
	public void addFireworkParticle(double d, double e, double f, double g, double h, double i, @Nullable CompoundTag compoundTag) {
		this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, d, e, f, g, h, i, this.client.particleManager, compoundTag));
	}

	@Override
	public void sendPacket(Packet<?> packet) {
		this.netHandler.sendPacket(packet);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.netHandler.getRecipeManager();
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	@Override
	public void setTimeOfDay(long l) {
		if (l < 0L) {
			l = -l;
			this.getGameRules().get(GameRules.field_19396).set(false, null);
		} else {
			this.getGameRules().get(GameRules.field_19396).set(true, null);
		}

		super.setTimeOfDay(l);
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return DummyClientTickScheduler.get();
	}

	public ClientChunkManager getChunkManager() {
		return (ClientChunkManager)super.getChunkManager();
	}

	@Nullable
	@Override
	public MapState getMapState(String string) {
		return (MapState)this.mapStates.get(string);
	}

	@Override
	public void putMapState(MapState mapState) {
		this.mapStates.put(mapState.getId(), mapState);
	}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	@Override
	public RegistryTagManager getTagManager() {
		return this.netHandler.getTagManager();
	}

	@Override
	public void updateListeners(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.worldRenderer.updateBlock(this, blockPos, blockState, blockState2, i);
	}

	@Override
	public void checkBlockRerender(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		this.worldRenderer.checkBlockRerender(blockPos, blockState, blockState2);
	}

	public void scheduleBlockRenders(int i, int j, int k) {
		this.worldRenderer.scheduleBlockRenders(i, j, k);
	}

	@Override
	public void setBlockBreakingInfo(int i, BlockPos blockPos, int j) {
		this.worldRenderer.setBlockBreakingInfo(i, blockPos, j);
	}

	@Override
	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
		this.worldRenderer.playGlobalEvent(i, blockPos, j);
	}

	@Override
	public void playLevelEvent(@Nullable PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		try {
			this.worldRenderer.playLevelEvent(playerEntity, i, blockPos, j);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Playing level event");
			CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
			crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(blockPos));
			crashReportSection.add("Event source", playerEntity);
			crashReportSection.add("Event type", i);
			crashReportSection.add("Event data", j);
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void addParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), d, e, f, g, h, i);
	}

	@Override
	public void addParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn() || bl, d, e, f, g, h, i);
	}

	@Override
	public void addImportantParticle(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleEffect, false, true, d, e, f, g, h, i);
	}

	@Override
	public void addImportantParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.worldRenderer.addParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn() || bl, true, d, e, f, g, h, i);
	}

	@Override
	public List<AbstractClientPlayerEntity> getPlayers() {
		return this.players;
	}

	@Override
	public Biome getGeneratorStoredBiome(int i, int j, int k) {
		return Biomes.field_9451;
	}

	public float method_23783(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	public Vec3d method_23777(BlockPos blockPos, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		Biome biome = this.getBiome(blockPos);
		int i = biome.getSkyColor();
		float j = (float)(i >> 16 & 0xFF) / 255.0F;
		float k = (float)(i >> 8 & 0xFF) / 255.0F;
		float l = (float)(i & 0xFF) / 255.0F;
		j *= h;
		k *= h;
		l *= h;
		float m = this.getRainGradient(f);
		if (m > 0.0F) {
			float n = (j * 0.3F + k * 0.59F + l * 0.11F) * 0.6F;
			float o = 1.0F - m * 0.75F;
			j = j * o + n * (1.0F - o);
			k = k * o + n * (1.0F - o);
			l = l * o + n * (1.0F - o);
		}

		float p = this.getThunderGradient(f);
		if (p > 0.0F) {
			float q = (j * 0.3F + k * 0.59F + l * 0.11F) * 0.2F;
			float r = 1.0F - p * 0.75F;
			j = j * r + q * (1.0F - r);
			k = k * r + q * (1.0F - r);
			l = l * r + q * (1.0F - r);
		}

		if (this.lightningTicksLeft > 0) {
			float s = (float)this.lightningTicksLeft - f;
			if (s > 1.0F) {
				s = 1.0F;
			}

			s *= 0.45F;
			j = j * (1.0F - s) + 0.8F * s;
			k = k * (1.0F - s) + 0.8F * s;
			l = l * (1.0F - s) + 1.0F * s;
		}

		return new Vec3d((double)j, (double)k, (double)l);
	}

	public Vec3d getCloudsColor(float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		float i = 1.0F;
		float j = 1.0F;
		float k = 1.0F;
		float l = this.getRainGradient(f);
		if (l > 0.0F) {
			float m = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.6F;
			float n = 1.0F - l * 0.95F;
			i = i * n + m * (1.0F - n);
			j = j * n + m * (1.0F - n);
			k = k * n + m * (1.0F - n);
		}

		i *= h * 0.9F + 0.1F;
		j *= h * 0.9F + 0.1F;
		k *= h * 0.85F + 0.15F;
		float o = this.getThunderGradient(f);
		if (o > 0.0F) {
			float p = (i * 0.3F + j * 0.59F + k * 0.11F) * 0.2F;
			float q = 1.0F - o * 0.95F;
			i = i * q + p * (1.0F - q);
			j = j * q + p * (1.0F - q);
			k = k * q + p * (1.0F - q);
		}

		return new Vec3d((double)i, (double)j, (double)k);
	}

	public Vec3d getFogColor(float f) {
		float g = this.getSkyAngle(f);
		return this.dimension.getFogColor(g, f);
	}

	public float method_23787(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public double getSkyDarknessHeight() {
		return this.properties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public int getLightningTicksLeft() {
		return this.lightningTicksLeft;
	}

	@Override
	public void setLightningTicksLeft(int i) {
		this.lightningTicksLeft = i;
	}

	@Override
	public int getColor(BlockPos blockPos, ColorResolver colorResolver) {
		BiomeColorCache biomeColorCache = (BiomeColorCache)this.colorCache.get(colorResolver);
		return biomeColorCache.getBiomeColor(blockPos, () -> this.calculateColor(blockPos, colorResolver));
	}

	public int calculateColor(BlockPos blockPos, ColorResolver colorResolver) {
		int i = MinecraftClient.getInstance().options.biomeBlendRadius;
		if (i == 0) {
			return colorResolver.getColor(this.getBiome(blockPos), (double)blockPos.getX(), (double)blockPos.getZ());
		} else {
			int j = (i * 2 + 1) * (i * 2 + 1);
			int k = 0;
			int l = 0;
			int m = 0;
			CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(
				blockPos.getX() - i, blockPos.getY(), blockPos.getZ() - i, blockPos.getX() + i, blockPos.getY(), blockPos.getZ() + i
			);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			while (cuboidBlockIterator.step()) {
				mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
				int n = colorResolver.getColor(this.getBiome(mutable), (double)mutable.getX(), (double)mutable.getZ());
				k += (n & 0xFF0000) >> 16;
				l += (n & 0xFF00) >> 8;
				m += n & 0xFF;
			}

			return (k / j & 0xFF) << 16 | (l / j & 0xFF) << 8 | m / j & 0xFF;
		}
	}
}
