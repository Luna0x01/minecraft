package net.minecraft.client.world;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.class_3592;
import net.minecraft.class_3604;
import net.minecraft.class_4342;
import net.minecraft.class_4488;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.sound.MinecartMovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EmptySaveHandler;
import net.minecraft.world.GameMode;
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
	private final ClientPlayNetworkHandler clientNetHandler;
	private ClientChunkProvider clientChunkCache;
	private final Set<Entity> world = Sets.newHashSet();
	private final Set<Entity> entitiesForSpawn = Sets.newHashSet();
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final Set<ChunkPos> previousChunkPos = Sets.newHashSet();
	private int field_13407 = this.random.nextInt(12000);
	protected Set<ChunkPos> field_13408 = Sets.newHashSet();
	private Scoreboard field_20626 = new Scoreboard();

	public ClientWorld(
		ClientPlayNetworkHandler clientPlayNetworkHandler, LevelInfo levelInfo, DimensionType dimensionType, Difficulty difficulty, Profiler profiler
	) {
		super(new EmptySaveHandler(), new TemporaryStateManager(), new LevelProperties(levelInfo, "MpServer"), dimensionType.method_17203(), profiler, true);
		this.clientNetHandler = clientPlayNetworkHandler;
		this.method_3588().setDifficulty(difficulty);
		this.setSpawnPos(new BlockPos(8, 64, 8));
		this.dimension.copyFromWorld(this);
		this.chunkProvider = this.getChunkCache();
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
	}

	@Override
	public void method_16327(BooleanSupplier booleanSupplier) {
		super.method_16327(booleanSupplier);
		this.setTime(this.getLastUpdateTime() + 1L);
		if (this.getGameRules().getBoolean("doDaylightCycle")) {
			this.setTimeOfDay(this.getTimeOfDay() + 1L);
		}

		this.profiler.push("reEntryProcessing");

		for (int i = 0; i < 10 && !this.entitiesForSpawn.isEmpty(); i++) {
			Entity entity = (Entity)this.entitiesForSpawn.iterator().next();
			this.entitiesForSpawn.remove(entity);
			if (!this.loadedEntities.contains(entity)) {
				this.method_3686(entity);
			}
		}

		this.profiler.swap("chunkCache");
		this.clientChunkCache.method_17045(booleanSupplier);
		this.profiler.swap("blocks");
		this.tickBlocks();
		this.profiler.pop();
	}

	@Override
	protected ChunkProvider getChunkCache() {
		this.clientChunkCache = new ClientChunkProvider(this);
		return this.clientChunkCache;
	}

	@Override
	public boolean method_8487(int i, int j, boolean bl) {
		return bl || this.method_3586().method_17044(i, j, true, false) != null;
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
				Chunk chunk = this.method_16347(chunkPos.x, chunkPos.z);
				this.method_3605(j, k, chunk);
				this.profiler.pop();
				this.previousChunkPos.add(chunkPos);
				if (++i >= 10) {
					return;
				}
			}
		}
	}

	@Override
	public boolean method_3686(Entity entity) {
		boolean bl = super.method_3686(entity);
		this.world.add(entity);
		if (bl) {
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundManager().play(new MinecartMovingSoundInstance((AbstractMinecartEntity)entity));
			}
		} else {
			this.entitiesForSpawn.add(entity);
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
		if (this.world.contains(entity)) {
			if (entity.isAlive()) {
				this.entitiesForSpawn.add(entity);
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
		if (!this.method_3686(entity)) {
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

	public void method_18973(BlockPos blockPos, BlockState blockState) {
		this.setBlockState(blockPos, blockState, 19);
	}

	@Override
	public void disconnect() {
		this.clientNetHandler.getClientConnection().disconnect(new TranslatableText("multiplayer.status.quitting"));
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
			if (blockState.isAir() && this.method_16379(blockPos, 0) <= this.random.nextInt(8) && this.method_16370(LightType.SKY, blockPos) <= 0) {
				double d = this.client.player.squaredDistanceTo((double)l + 0.5, (double)n + 0.5, (double)m + 0.5);
				if (this.client.player != null && d > 4.0 && d < 256.0) {
					this.playSound(
						(double)l + 0.5, (double)n + 0.5, (double)m + 0.5, Sounds.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.random.nextFloat() * 0.2F, false
					);
					this.field_13407 = this.random.nextInt(12000) + 6000;
				}
			}
		}
	}

	public void spawnRandomParticles(int x, int y, int z) {
		int i = 32;
		Random random = new Random();
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.client.interactionManager.method_9667() == GameMode.CREATIVE && !itemStack.isEmpty() && itemStack.getItem() == Blocks.BARRIER.getItem();
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
		FluidState fluidState = this.getFluidState(mutable);
		if (!fluidState.isEmpty()) {
			fluidState.method_17802(this, mutable, random);
			ParticleEffect particleEffect = fluidState.getParticle();
			if (particleEffect != null && this.random.nextInt(10) == 0) {
				boolean bl2 = blockState.getRenderLayer(this, mutable, Direction.DOWN) == BlockRenderLayer.SOLID;
				BlockPos blockPos = mutable.down();
				this.method_18971(blockPos, this.getBlockState(blockPos), particleEffect, bl2);
			}
		}

		if (bl && blockState.getBlock() == Blocks.BARRIER) {
			this.method_16343(class_4342.field_21377, (double)((float)m + 0.5F), (double)((float)n + 0.5F), (double)((float)o + 0.5F), 0.0, 0.0, 0.0);
		}
	}

	private void method_18971(BlockPos blockPos, BlockState blockState, ParticleEffect particleEffect, boolean bl) {
		if (blockState.getFluidState().isEmpty()) {
			VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.Y);
			if (d < 1.0) {
				if (bl) {
					this.method_18970(
						(double)blockPos.getX(),
						(double)(blockPos.getX() + 1),
						(double)blockPos.getZ(),
						(double)(blockPos.getZ() + 1),
						(double)(blockPos.getY() + 1) - 0.05,
						particleEffect
					);
				}
			} else if (!blockState.isIn(BlockTags.IMPERMEABLE)) {
				double e = voxelShape.getMinimum(Direction.Axis.Y);
				if (e > 0.0) {
					this.method_18972(blockPos, particleEffect, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.down();
					BlockState blockState2 = this.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.Y);
					if (f < 1.0 && blockState2.getFluidState().isEmpty()) {
						this.method_18972(blockPos, particleEffect, voxelShape, (double)blockPos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void method_18972(BlockPos blockPos, ParticleEffect particleEffect, VoxelShape voxelShape, double d) {
		this.method_18970(
			(double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.X),
			(double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.X),
			(double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.Z),
			(double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.Z),
			d,
			particleEffect
		);
	}

	private void method_18970(double d, double e, double f, double g, double h, ParticleEffect particleEffect) {
		this.method_16343(particleEffect, d + (e - d) * this.random.nextDouble(), h, f + (g - f) * this.random.nextDouble(), 0.0, 0.0, 0.0);
	}

	public void clearEntities() {
		this.loadedEntities.removeAll(this.unloadedEntities);

		for (int i = 0; i < this.unloadedEntities.size(); i++) {
			Entity entity = (Entity)this.unloadedEntities.get(i);
			int j = entity.chunkX;
			int k = entity.chunkZ;
			if (entity.updateNeeded && this.method_8487(j, k, true)) {
				this.method_16347(j, k).removeEntity(entity);
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
				if (entity2.updateNeeded && this.method_8487(n, o, true)) {
					this.method_16347(n, o).removeEntity(entity2);
				}

				this.loadedEntities.remove(m--);
				this.onEntityRemoved(entity2);
			}
		}
	}

	@Override
	public CrashReportSection addToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = super.addToCrashReport(report);
		crashReportSection.add("Forced entities", (CrashCallable<String>)(() -> this.world.size() + " total; " + this.world));
		crashReportSection.add("Retry entities", (CrashCallable<String>)(() -> this.entitiesForSpawn.size() + " total; " + this.entitiesForSpawn));
		crashReportSection.add("Server brand", (CrashCallable<String>)(() -> this.client.player.getServerBrand()));
		crashReportSection.add(
			"Server type", (CrashCallable<String>)(() -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server")
		);
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

	@Override
	public RecipeDispatcher method_16313() {
		return this.clientNetHandler.method_18962();
	}

	public void setScoreboard(Scoreboard sb) {
		this.field_20626 = sb;
	}

	@Override
	public void setTimeOfDay(long time) {
		if (time < 0L) {
			time = -time;
			this.getGameRules().method_16297("doDaylightCycle", "false", null);
		} else {
			this.getGameRules().method_16297("doDaylightCycle", "true", null);
		}

		super.setTimeOfDay(time);
	}

	@Override
	public class_3604<Block> getBlockTickScheduler() {
		return class_3592.method_16285();
	}

	@Override
	public class_3604<Fluid> method_16340() {
		return class_3592.method_16285();
	}

	public ClientChunkProvider method_3586() {
		return (ClientChunkProvider)super.method_3586();
	}

	@Override
	public Scoreboard getScoreboard() {
		return this.field_20626;
	}

	@Override
	public class_4488 method_16314() {
		return this.clientNetHandler.method_18965();
	}
}
