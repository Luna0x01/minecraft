package net.minecraft.client.world;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.MinecartMovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EmptySaveHandler;
import net.minecraft.world.TemporaryStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ClientChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class ClientWorld extends World {
	private ClientPlayNetworkHandler clientNetHandler;
	private ClientChunkProvider clientChunkCache;
	private final Set<Entity> world = Sets.newHashSet();
	private final Set<Entity> entitiesForSpawn = Sets.newHashSet();
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final Set<ChunkPos> previousChunkPos = Sets.newHashSet();

	public ClientWorld(ClientPlayNetworkHandler clientPlayNetworkHandler, LevelInfo levelInfo, int i, Difficulty difficulty, Profiler profiler) {
		super(new EmptySaveHandler(), new LevelProperties(levelInfo, "MpServer"), Dimension.getById(i), profiler, true);
		this.clientNetHandler = clientPlayNetworkHandler;
		this.getLevelProperties().setDifficulty(difficulty);
		this.setSpawnPos(new BlockPos(8, 64, 8));
		this.dimension.copyFromWorld(this);
		this.chunkProvider = this.getChunkCache();
		this.persistentStateManager = new TemporaryStateManager();
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
	}

	@Override
	public void tick() {
		super.tick();
		this.setTime(this.getLastUpdateTime() + 1L);
		if (this.getGameRules().getBoolean("doDaylightCycle")) {
			this.setTimeOfDay(this.getTimeOfDay() + 1L);
		}

		this.profiler.push("reEntryProcessing");

		for (int i = 0; i < 10 && !this.entitiesForSpawn.isEmpty(); i++) {
			Entity entity = (Entity)this.entitiesForSpawn.iterator().next();
			this.entitiesForSpawn.remove(entity);
			if (!this.loadedEntities.contains(entity)) {
				this.spawnEntity(entity);
			}
		}

		this.profiler.swap("chunkCache");
		this.clientChunkCache.tickChunks();
		this.profiler.swap("blocks");
		this.tickBlocks();
		this.profiler.pop();
	}

	public void method_1251(int i, int j, int k, int l, int m, int n) {
	}

	@Override
	protected ChunkProvider getChunkCache() {
		this.clientChunkCache = new ClientChunkProvider(this);
		return this.clientChunkCache;
	}

	@Override
	protected void tickBlocks() {
		super.tickBlocks();
		this.previousChunkPos.retainAll(this.field_4530);
		if (this.previousChunkPos.size() == this.field_4530.size()) {
			this.previousChunkPos.clear();
		}

		int i = 0;

		for (ChunkPos chunkPos : this.field_4530) {
			if (!this.previousChunkPos.contains(chunkPos)) {
				int j = chunkPos.x * 16;
				int k = chunkPos.z * 16;
				this.profiler.push("getChunk");
				Chunk chunk = this.getChunk(chunkPos.x, chunkPos.z);
				this.method_3605(j, k, chunk);
				this.profiler.pop();
				this.previousChunkPos.add(chunkPos);
				if (++i >= 10) {
					return;
				}
			}
		}
	}

	public void handleChunk(int x, int z, boolean load) {
		if (load) {
			this.clientChunkCache.getOrGenerateChunk(x, z);
		} else {
			this.clientChunkCache.unloadChunk(x, z);
		}

		if (!load) {
			this.onRenderRegionUpdate(x * 16, 0, z * 16, x * 16 + 15, 256, z * 16 + 15);
		}
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		boolean bl = super.spawnEntity(entity);
		this.world.add(entity);
		if (!bl) {
			this.entitiesForSpawn.add(entity);
		} else if (entity instanceof AbstractMinecartEntity) {
			this.client.getSoundManager().play(new MinecartMovingSoundInstance((AbstractMinecartEntity)entity));
		}

		return bl;
	}

	@Override
	public void removeEntity(Entity entity) {
		super.removeEntity(entity);
		this.world.remove(entity);
	}

	@Override
	protected void onEntitySpawned(Entity entity) {
		super.onEntitySpawned(entity);
		if (this.entitiesForSpawn.contains(entity)) {
			this.entitiesForSpawn.remove(entity);
		}
	}

	@Override
	protected void onEntityRemoved(Entity entity) {
		super.onEntityRemoved(entity);
		boolean bl = false;
		if (this.world.contains(entity)) {
			if (entity.isAlive()) {
				this.entitiesForSpawn.add(entity);
				bl = true;
			} else {
				this.world.remove(entity);
			}
		}
	}

	public void addEntity(int id, Entity entity) {
		Entity entity2 = this.getEntityById(id);
		if (entity2 != null) {
			this.removeEntity(entity2);
		}

		this.world.add(entity);
		entity.setEntityId(id);
		if (!this.spawnEntity(entity)) {
			this.entitiesForSpawn.add(entity);
		}

		this.idToEntity.set(id, entity);
	}

	@Override
	public Entity getEntityById(int id) {
		return (Entity)(id == this.client.player.getEntityId() ? this.client.player : super.getEntityById(id));
	}

	public Entity removeEntity(int id) {
		Entity entity = this.idToEntity.remove(id);
		if (entity != null) {
			this.world.remove(entity);
			this.removeEntity(entity);
		}

		return entity;
	}

	public boolean setBlockStateWithoutNeighborUpdates(BlockPos pos, BlockState state) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.method_1251(i, j, k, i, j, k);
		return super.setBlockState(pos, state, 3);
	}

	@Override
	public void disconnect() {
		this.clientNetHandler.getClientConnection().disconnect(new LiteralText("Quitting"));
	}

	@Override
	protected void tickWeather() {
	}

	@Override
	protected int getNextMapId() {
		return this.client.options.viewDistance;
	}

	public void spawnRandomParticles(int x, int y, int z) {
		int i = 16;
		Random random = new Random();
		ItemStack itemStack = this.client.player.getStackInHand();
		boolean bl = this.client.interactionManager.getCurrentGameMode() == LevelInfo.GameMode.CREATIVE
			&& itemStack != null
			&& Block.getBlockFromItem(itemStack.getItem()) == Blocks.BARRIER;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < 1000; j++) {
			int k = x + this.random.nextInt(i) - this.random.nextInt(i);
			int l = y + this.random.nextInt(i) - this.random.nextInt(i);
			int m = z + this.random.nextInt(i) - this.random.nextInt(i);
			mutable.setPosition(k, l, m);
			BlockState blockState = this.getBlockState(mutable);
			blockState.getBlock().randomDisplayTick(this, mutable, blockState, random);
			if (bl && blockState.getBlock() == Blocks.BARRIER) {
				this.addParticle(ParticleType.BARRIER, (double)((float)k + 0.5F), (double)((float)l + 0.5F), (double)((float)m + 0.5F), 0.0, 0.0, 0.0, new int[0]);
			}
		}
	}

	public void clearEntities() {
		this.loadedEntities.removeAll(this.unloadedEntities);

		for (int i = 0; i < this.unloadedEntities.size(); i++) {
			Entity entity = (Entity)this.unloadedEntities.get(i);
			int j = entity.chunkX;
			int k = entity.chunkZ;
			if (entity.updateNeeded && this.isChunkLoaded(j, k, true)) {
				this.getChunk(j, k).removeEntity(entity);
			}
		}

		for (int l = 0; l < this.unloadedEntities.size(); l++) {
			this.onEntityRemoved((Entity)this.unloadedEntities.get(l));
		}

		this.unloadedEntities.clear();

		for (int m = 0; m < this.loadedEntities.size(); m++) {
			Entity entity2 = (Entity)this.loadedEntities.get(m);
			if (entity2.vehicle != null) {
				if (!entity2.vehicle.removed && entity2.vehicle.rider == entity2) {
					continue;
				}

				entity2.vehicle.rider = null;
				entity2.vehicle = null;
			}

			if (entity2.removed) {
				int n = entity2.chunkX;
				int o = entity2.chunkZ;
				if (entity2.updateNeeded && this.isChunkLoaded(n, o, true)) {
					this.getChunk(n, o).removeEntity(entity2);
				}

				this.loadedEntities.remove(m--);
				this.onEntityRemoved(entity2);
			}
		}
	}

	@Override
	public CrashReportSection addToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = super.addToCrashReport(report);
		crashReportSection.add("Forced entities", new Callable<String>() {
			public String call() {
				return ClientWorld.this.world.size() + " total; " + ClientWorld.this.world.toString();
			}
		});
		crashReportSection.add("Retry entities", new Callable<String>() {
			public String call() {
				return ClientWorld.this.entitiesForSpawn.size() + " total; " + ClientWorld.this.entitiesForSpawn.toString();
			}
		});
		crashReportSection.add("Server brand", new Callable<String>() {
			public String call() throws Exception {
				return ClientWorld.this.client.player.getServerBrand();
			}
		});
		crashReportSection.add("Server type", new Callable<String>() {
			public String call() throws Exception {
				return ClientWorld.this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
			}
		});
		return crashReportSection;
	}

	public void playSound(BlockPos pos, String sound, float volume, float pitch, boolean useDistance) {
		this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, volume, pitch, useDistance);
	}

	@Override
	public void playSound(double x, double y, double z, String sound, float volume, float pitch, boolean useDistance) {
		double d = this.client.getCameraEntity().squaredDistanceTo(x, y, z);
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(new Identifier(sound), volume, pitch, (float)x, (float)y, (float)z);
		if (useDistance && d > 100.0) {
			double e = Math.sqrt(d) / 40.0;
			this.client.getSoundManager().play(positionedSoundInstance, (int)(e * 20.0));
		} else {
			this.client.getSoundManager().play(positionedSoundInstance);
		}
	}

	@Override
	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, NbtCompound nbt) {
		this.client
			.particleManager
			.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, nbt));
	}

	public void setScoreboard(Scoreboard sb) {
		this.scoreboard = sb;
	}

	@Override
	public void setTimeOfDay(long time) {
		if (time < 0L) {
			time = -time;
			this.getGameRules().setGameRule("doDaylightCycle", "false");
		} else {
			this.getGameRules().setGameRule("doDaylightCycle", "true");
		}

		super.setTimeOfDay(time);
	}
}
