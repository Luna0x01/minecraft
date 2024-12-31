package net.minecraft.world.chunk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.FileIoCallback;
import net.minecraft.util.FileIoThread;
import net.minecraft.util.Identifier;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage implements ChunkStorage, FileIoCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private Map<ChunkPos, NbtCompound> chunksToSave = new ConcurrentHashMap();
	private Set<ChunkPos> chunksBeingSaved = Collections.newSetFromMap(new ConcurrentHashMap());
	private final File saveLocation;
	private final DataFixerUpper field_12919;
	private boolean isSaving = false;

	public ThreadedAnvilChunkStorage(File file, DataFixerUpper dataFixerUpper) {
		this.saveLocation = file;
		this.field_12919 = dataFixerUpper;
	}

	@Nullable
	@Override
	public Chunk loadChunk(World world, int x, int z) throws IOException {
		ChunkPos chunkPos = new ChunkPos(x, z);
		NbtCompound nbtCompound = (NbtCompound)this.chunksToSave.get(chunkPos);
		if (nbtCompound == null) {
			DataInputStream dataInputStream = RegionIo.read(this.saveLocation, x, z);
			if (dataInputStream == null) {
				return null;
			}

			nbtCompound = this.field_12919.update(LevelDataType.CHUNK, NbtIo.read(dataInputStream));
		}

		return this.validateChunk(world, x, z, nbtCompound);
	}

	protected Chunk validateChunk(World world, int chunkX, int chunkZ, NbtCompound nbt) {
		if (!nbt.contains("Level", 10)) {
			LOGGER.error("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
			return null;
		} else {
			NbtCompound nbtCompound = nbt.getCompound("Level");
			if (!nbtCompound.contains("Sections", 9)) {
				LOGGER.error("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
				return null;
			} else {
				Chunk chunk = this.getChunk(world, nbtCompound);
				if (!chunk.isChunkEqual(chunkX, chunkZ)) {
					LOGGER.error(
						"Chunk file at "
							+ chunkX
							+ ","
							+ chunkZ
							+ " is in the wrong location; relocating. (Expected "
							+ chunkX
							+ ", "
							+ chunkZ
							+ ", got "
							+ chunk.chunkX
							+ ", "
							+ chunk.chunkZ
							+ ")"
					);
					nbtCompound.putInt("xPos", chunkX);
					nbtCompound.putInt("zPos", chunkZ);
					chunk = this.getChunk(world, nbtCompound);
				}

				return chunk;
			}
		}
	}

	@Override
	public void writeChunk(World world, Chunk chunk) throws IOException, WorldSaveException {
		world.readSaveLock();

		try {
			NbtCompound nbtCompound = new NbtCompound();
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound.put("Level", nbtCompound2);
			nbtCompound.putInt("DataVersion", 184);
			this.putChunk(chunk, world, nbtCompound2);
			this.registerChunkChecker(chunk.getChunkPos(), nbtCompound);
		} catch (Exception var5) {
			LOGGER.error("Failed to save chunk", var5);
		}
	}

	protected void registerChunkChecker(ChunkPos pos, NbtCompound nbt) {
		if (!this.chunksBeingSaved.contains(pos)) {
			this.chunksToSave.put(pos, nbt);
		}

		FileIoThread.getInstance().registerCallback(this);
	}

	@Override
	public boolean saveNextChunk() {
		if (this.chunksToSave.isEmpty()) {
			if (this.isSaving) {
				LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.saveLocation.getName()});
			}

			return false;
		} else {
			ChunkPos chunkPos = (ChunkPos)this.chunksToSave.keySet().iterator().next();

			boolean exception;
			try {
				this.chunksBeingSaved.add(chunkPos);
				NbtCompound nbtCompound = (NbtCompound)this.chunksToSave.remove(chunkPos);
				if (nbtCompound != null) {
					try {
						this.write(chunkPos, nbtCompound);
					} catch (Exception var7) {
						LOGGER.error("Failed to save chunk", var7);
					}
				}

				exception = true;
			} finally {
				this.chunksBeingSaved.remove(chunkPos);
			}

			return exception;
		}
	}

	private void write(ChunkPos chunkPos, NbtCompound nbt) throws IOException {
		DataOutputStream dataOutputStream = RegionIo.write(this.saveLocation, chunkPos.x, chunkPos.z);
		NbtIo.write(nbt, dataOutputStream);
		dataOutputStream.close();
	}

	@Override
	public void writeEntities(World world, Chunk chunk) throws IOException {
	}

	@Override
	public void method_3950() {
	}

	@Override
	public void save() {
		try {
			this.isSaving = true;

			while (this.saveNextChunk()) {
			}
		} finally {
			this.isSaving = false;
		}
	}

	private void putChunk(Chunk chunk, World world, NbtCompound nbt) {
		nbt.putInt("xPos", chunk.chunkX);
		nbt.putInt("zPos", chunk.chunkZ);
		nbt.putLong("LastUpdate", world.getLastUpdateTime());
		nbt.putIntArray("HeightMap", chunk.getLevelHeightmap());
		nbt.putBoolean("TerrainPopulated", chunk.isTerrainPopulated());
		nbt.putBoolean("LightPopulated", chunk.isLightPopulated());
		nbt.putLong("InhabitedTime", chunk.getInhabitedTime());
		ChunkSection[] chunkSections = chunk.getBlockStorage();
		NbtList nbtList = new NbtList();
		boolean bl = !world.dimension.hasNoSkylight();

		for (ChunkSection chunkSection : chunkSections) {
			if (chunkSection != Chunk.EMPTY) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Y", (byte)(chunkSection.getYOffset() >> 4 & 0xFF));
				byte[] bs = new byte[4096];
				ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray2 = chunkSection.getBlockData().store(bs, chunkNibbleArray);
				nbtCompound.putByteArray("Blocks", bs);
				nbtCompound.putByteArray("Data", chunkNibbleArray.getValue());
				if (chunkNibbleArray2 != null) {
					nbtCompound.putByteArray("Add", chunkNibbleArray2.getValue());
				}

				nbtCompound.putByteArray("BlockLight", chunkSection.getBlockLight().getValue());
				if (bl) {
					nbtCompound.putByteArray("SkyLight", chunkSection.getSkyLight().getValue());
				} else {
					nbtCompound.putByteArray("SkyLight", new byte[chunkSection.getBlockLight().getValue().length]);
				}

				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Sections", nbtList);
		nbt.putByteArray("Biomes", chunk.getBiomeArray());
		chunk.setHasEntities(false);
		NbtList nbtList2 = new NbtList();

		for (int k = 0; k < chunk.getEntities().length; k++) {
			for (Entity entity : chunk.getEntities()[k]) {
				NbtCompound nbtCompound2 = new NbtCompound();
				if (entity.saveToNbt(nbtCompound2)) {
					chunk.setHasEntities(true);
					nbtList2.add(nbtCompound2);
				}
			}
		}

		nbt.put("Entities", nbtList2);
		NbtList nbtList3 = new NbtList();

		for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
			NbtCompound nbtCompound3 = blockEntity.toNbt(new NbtCompound());
			nbtList3.add(nbtCompound3);
		}

		nbt.put("TileEntities", nbtList3);
		List<ScheduledTick> list = world.getScheduledTicks(chunk, false);
		if (list != null) {
			long l = world.getLastUpdateTime();
			NbtList nbtList4 = new NbtList();

			for (ScheduledTick scheduledTick : list) {
				NbtCompound nbtCompound4 = new NbtCompound();
				Identifier identifier = Block.REGISTRY.getIdentifier(scheduledTick.getBlock());
				nbtCompound4.putString("i", identifier == null ? "" : identifier.toString());
				nbtCompound4.putInt("x", scheduledTick.pos.getX());
				nbtCompound4.putInt("y", scheduledTick.pos.getY());
				nbtCompound4.putInt("z", scheduledTick.pos.getZ());
				nbtCompound4.putInt("t", (int)(scheduledTick.time - l));
				nbtCompound4.putInt("p", scheduledTick.priority);
				nbtList4.add(nbtCompound4);
			}

			nbt.put("TileTicks", nbtList4);
		}
	}

	private Chunk getChunk(World world, NbtCompound nbt) {
		int i = nbt.getInt("xPos");
		int j = nbt.getInt("zPos");
		Chunk chunk = new Chunk(world, i, j);
		chunk.setLevelHeightmap(nbt.getIntArray("HeightMap"));
		chunk.setTerrainPopulated(nbt.getBoolean("TerrainPopulated"));
		chunk.setLightPopulated(nbt.getBoolean("LightPopulated"));
		chunk.setInhabitedTime(nbt.getLong("InhabitedTime"));
		NbtList nbtList = nbt.getList("Sections", 10);
		int k = 16;
		ChunkSection[] chunkSections = new ChunkSection[k];
		boolean bl = !world.dimension.hasNoSkylight();

		for (int l = 0; l < nbtList.size(); l++) {
			NbtCompound nbtCompound = nbtList.getCompound(l);
			int m = nbtCompound.getByte("Y");
			ChunkSection chunkSection = new ChunkSection(m << 4, bl);
			byte[] bs = nbtCompound.getByteArray("Blocks");
			ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray(nbtCompound.getByteArray("Data"));
			ChunkNibbleArray chunkNibbleArray2 = nbtCompound.contains("Add", 7) ? new ChunkNibbleArray(nbtCompound.getByteArray("Add")) : null;
			chunkSection.getBlockData().load(bs, chunkNibbleArray, chunkNibbleArray2);
			chunkSection.setBlockLight(new ChunkNibbleArray(nbtCompound.getByteArray("BlockLight")));
			if (bl) {
				chunkSection.setSkyLight(new ChunkNibbleArray(nbtCompound.getByteArray("SkyLight")));
			}

			chunkSection.calculateCounts();
			chunkSections[m] = chunkSection;
		}

		chunk.setLevelChunkSections(chunkSections);
		if (nbt.contains("Biomes", 7)) {
			chunk.setBiomeArray(nbt.getByteArray("Biomes"));
		}

		NbtList nbtList2 = nbt.getList("Entities", 10);
		if (nbtList2 != null) {
			for (int n = 0; n < nbtList2.size(); n++) {
				NbtCompound nbtCompound2 = nbtList2.getCompound(n);
				method_11783(nbtCompound2, world, chunk);
				chunk.setHasEntities(true);
			}
		}

		NbtList nbtList3 = nbt.getList("TileEntities", 10);
		if (nbtList3 != null) {
			for (int o = 0; o < nbtList3.size(); o++) {
				NbtCompound nbtCompound3 = nbtList3.getCompound(o);
				BlockEntity blockEntity = BlockEntity.createFromNbt(nbtCompound3);
				if (blockEntity != null) {
					chunk.addBlockEntity(blockEntity);
				}
			}
		}

		if (nbt.contains("TileTicks", 9)) {
			NbtList nbtList4 = nbt.getList("TileTicks", 10);
			if (nbtList4 != null) {
				for (int p = 0; p < nbtList4.size(); p++) {
					NbtCompound nbtCompound4 = nbtList4.getCompound(p);
					Block block;
					if (nbtCompound4.contains("i", 8)) {
						block = Block.get(nbtCompound4.getString("i"));
					} else {
						block = Block.getById(nbtCompound4.getInt("i"));
					}

					world.scheduleTick(
						new BlockPos(nbtCompound4.getInt("x"), nbtCompound4.getInt("y"), nbtCompound4.getInt("z")), block, nbtCompound4.getInt("t"), nbtCompound4.getInt("p")
					);
				}
			}
		}

		return chunk;
	}

	@Nullable
	public static Entity method_11783(NbtCompound tag, World world, Chunk chunk) {
		Entity entity = method_11781(tag, world);
		if (entity == null) {
			return null;
		} else {
			chunk.addEntity(entity);
			if (tag.contains("Passengers", 9)) {
				NbtList nbtList = tag.getList("Passengers", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					Entity entity2 = method_11783(nbtList.getCompound(i), world, chunk);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}
	}

	@Nullable
	public static Entity method_11782(NbtCompound tag, World world, double x, double y, double z, boolean bl) {
		Entity entity = method_11781(tag, world);
		if (entity == null) {
			return null;
		} else {
			entity.refreshPositionAndAngles(x, y, z, entity.yaw, entity.pitch);
			if (bl && !world.spawnEntity(entity)) {
				return null;
			} else {
				if (tag.contains("Passengers", 9)) {
					NbtList nbtList = tag.getList("Passengers", 10);

					for (int i = 0; i < nbtList.size(); i++) {
						Entity entity2 = method_11782(nbtList.getCompound(i), world, x, y, z, bl);
						if (entity2 != null) {
							entity2.startRiding(entity, true);
						}
					}
				}

				return entity;
			}
		}
	}

	@Nullable
	protected static Entity method_11781(NbtCompound tag, World world) {
		try {
			return EntityType.createInstanceFromNbt(tag, world);
		} catch (RuntimeException var3) {
			return null;
		}
	}

	public static void method_11785(Entity entity, World world) {
		if (world.spawnEntity(entity) && entity.hasPassengers()) {
			for (Entity entity2 : entity.getPassengerList()) {
				method_11785(entity2, world);
			}
		}
	}

	@Nullable
	public static Entity method_11784(NbtCompound tag, World world, boolean bl) {
		Entity entity = method_11781(tag, world);
		if (entity == null) {
			return null;
		} else if (bl && !world.spawnEntity(entity)) {
			return null;
		} else {
			if (tag.contains("Passengers", 9)) {
				NbtList nbtList = tag.getList("Passengers", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					Entity entity2 = method_11784(nbtList.getCompound(i), world, bl);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}
	}
}
