package net.minecraft.client.world;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.MinecartMovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.LiteralText;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EmptySaveHandler;
import net.minecraft.world.LightType;
import net.minecraft.world.TemporaryStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ClientChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class ClientWorld extends World {
	private ClientPlayNetworkHandler clientNetHandler;
	private ClientChunkProvider clientChunkCache;
	private final Set<Entity> world = Sets.newHashSet();
	private final Set<Entity> entitiesForSpawn = Sets.newHashSet();
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final Set<ChunkPos> previousChunkPos = Sets.newHashSet();
	private int field_13407 = this.random.nextInt(12000);
	protected Set<ChunkPos> field_13408 = Sets.newHashSet();

	public ClientWorld(ClientPlayNetworkHandler clientPlayNetworkHandler, LevelInfo levelInfo, int i, Difficulty difficulty, Profiler profiler) {
		super(new EmptySaveHandler(), new LevelProperties(levelInfo, "MpServer"), DimensionType.fromId(i).create(), profiler, true);
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
	protected boolean isChunkLoaded(int chunkX, int chunkZ, boolean canBeEmpty) {
		return canBeEmpty || !this.getChunkProvider().getOrGenerateChunks(chunkX, chunkZ).isEmpty();
	}

	protected void method_12237() {
		this.field_13408.clear();
		int i = this.client.options.viewDistance;
		this.profiler.push("buildList");
		int j = MathHelper.floor(this.client.player.x / 16.0);
		int k = MathHelper.floor(this.client.player.z / 16.0);

		for (int l = -i; l <= i; l++) {
			for (int m = -i; m <= i; m++) {
				this.field_13408.add(new ChunkPos(l + j, m + k));
			}
		}

		this.profiler.pop();
	}

	@Override
	protected void tickBlocks() {
		this.method_12237();
		if (this.field_13407 > 0) {
			this.field_13407--;
		}

		this.previousChunkPos.retainAll(this.field_13408);
		if (this.previousChunkPos.size() == this.field_13408.size()) {
			this.previousChunkPos.clear();
		}

		int i = 0;

		for (ChunkPos chunkPos : this.field_13408) {
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

	@Nullable
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

	@Deprecated
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
	protected void method_3605(int i, int j, Chunk chunk) {
		super.method_3605(i, j, chunk);
		if (this.field_13407 == 0) {
			this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
			int k = this.lcgBlockSeed >> 2;
			int l = k & 15;
			int m = k >> 8 & 15;
			int n = k >> 16 & 0xFF;
			BlockPos blockPos = new BlockPos(l + i, n, m + j);
			BlockState blockState = chunk.getBlockState(blockPos);
			l += i;
			m += j;
			if (blockState.getMaterial() == Material.AIR
				&& this.getLightLevel(blockPos) <= this.random.nextInt(8)
				&& this.getLightAtPos(LightType.SKY, blockPos) <= 0
				&& this.client.player != null
				&& this.client.player.squaredDistanceTo((double)l + 0.5, (double)n + 0.5, (double)m + 0.5) > 4.0) {
				this.playSound(
					(double)l + 0.5, (double)n + 0.5, (double)m + 0.5, Sounds.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.random.nextFloat() * 0.2F, false
				);
				this.field_13407 = this.random.nextInt(12000) + 6000;
			}
		}
	}

	public void spawnRandomParticles(int x, int y, int z) {
		int i = 32;
		Random random = new Random();
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.client.interactionManager.getCurrentGameMode() == LevelInfo.GameMode.CREATIVE
			&& itemStack != null
			&& Block.getBlockFromItem(itemStack.getItem()) == Blocks.BARRIER;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < 667; j++) {
			this.method_12238(x, y, z, 16, random, bl, mutable);
			this.method_12238(x, y, z, 32, random, bl, mutable);
		}
	}

	public void method_12238(int i, int j, int k, int l, Random random, boolean bl, BlockPos.Mutable mutable) {
		int m = i + this.random.nextInt(l) - this.random.nextInt(l);
		int n = j + this.random.nextInt(l) - this.random.nextInt(l);
		int o = k + this.random.nextInt(l) - this.random.nextInt(l);
		mutable.setPosition(m, n, o);
		BlockState blockState = this.getBlockState(mutable);
		blockState.getBlock().randomDisplayTick(blockState, this, mutable, random);
		if (bl && blockState.getBlock() == Blocks.BARRIER) {
			this.addParticle(ParticleType.BARRIER, (double)((float)m + 0.5F), (double)((float)n + 0.5F), (double)((float)o + 0.5F), 0.0, 0.0, 0.0, new int[0]);
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
			Entity entity3 = entity2.getVehicle();
			if (entity3 != null) {
				if (!entity3.removed && entity3.hasPassenger(entity2)) {
					continue;
				}

				entity2.stopRiding();
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
		crashReportSection.add("Forced entities", new CrashCallable<String>() {
			public String call() {
				return ClientWorld.this.world.size() + " total; " + ClientWorld.this.world.toString();
			}
		});
		crashReportSection.add("Retry entities", new CrashCallable<String>() {
			public String call() {
				return ClientWorld.this.entitiesForSpawn.size() + " total; " + ClientWorld.this.entitiesForSpawn.toString();
			}
		});
		crashReportSection.add("Server brand", new CrashCallable<String>() {
			public String call() throws Exception {
				return ClientWorld.this.client.player.getServerBrand();
			}
		});
		crashReportSection.add("Server type", new CrashCallable<String>() {
			public String call() throws Exception {
				return ClientWorld.this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
			}
		});
		return crashReportSection;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, double d, double e, double f, Sound sound, SoundCategory soundCategory, float g, float h) {
		if (playerEntity == this.client.player) {
			this.playSound(d, e, f, sound, soundCategory, g, h, false);
		}
	}

	public void method_9669(BlockPos blockPos, Sound sound, SoundCategory soundCategory, float f, float g, boolean bl) {
		this.playSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, sound, soundCategory, f, g, bl);
	}

	@Override
	public void playSound(double d, double e, double f, Sound sound, SoundCategory soundCategory, float g, float h, boolean bl) {
		double i = this.client.getCameraEntity().squaredDistanceTo(d, e, f);
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(sound, soundCategory, g, h, (float)d, (float)e, (float)f);
		if (bl && i > 100.0) {
			double j = Math.sqrt(i) / 40.0;
			this.client.getSoundManager().play(positionedSoundInstance, (int)(j * 20.0));
		} else {
			this.client.getSoundManager().play(positionedSoundInstance);
		}
	}

	@Override
	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
		this.client
			.particleManager
			.method_12256(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, nbt));
	}

	@Override
	public void method_11483(Packet<?> packet) {
		this.clientNetHandler.sendPacket(packet);
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

	public ClientChunkProvider getChunkProvider() {
		return (ClientChunkProvider)super.getChunkProvider();
	}
}
