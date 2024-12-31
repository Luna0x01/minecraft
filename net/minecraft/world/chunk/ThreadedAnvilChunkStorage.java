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
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage implements ChunkStorage, FileIoCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private Map<ChunkPos, NbtCompound> chunksToSave = new ConcurrentHashMap();
	private Set<ChunkPos> chunksBeingSaved = Collections.newSetFromMap(new ConcurrentHashMap());
	private final File saveLocation;
	private boolean isSaving = false;

	public ThreadedAnvilChunkStorage(File file) {
		this.saveLocation = file;
	}

	@Override
	public Chunk loadChunk(World world, int x, int z) throws IOException {
		ChunkPos chunkPos = new ChunkPos(x, z);
		NbtCompound nbtCompound = (NbtCompound)this.chunksToSave.get(chunkPos);
		if (nbtCompound == null) {
			DataInputStream dataInputStream = RegionIo.read(this.saveLocation, x, z);
			if (dataInputStream == null) {
				return null;
			}

			nbtCompound = NbtIo.read(dataInputStream);
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
		nbt.putByte("V", (byte)1);
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
			if (chunkSection != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Y", (byte)(chunkSection.getYOffset() >> 4 & 0xFF));
				byte[] bs = new byte[chunkSection.getBlockStates().length];
				ChunkNibbleArray chunkNibbleArray = new ChunkNibbleArray();
				ChunkNibbleArray chunkNibbleArray2 = null;

				for (int k = 0; k < chunkSection.getBlockStates().length; k++) {
					char c = chunkSection.getBlockStates()[k];
					int l = k & 15;
					int m = k >> 8 & 15;
					int n = k >> 4 & 15;
					if (c >> '\f' != 0) {
						if (chunkNibbleArray2 == null) {
							chunkNibbleArray2 = new ChunkNibbleArray();
						}

						chunkNibbleArray2.set(l, m, n, c >> '\f');
					}

					bs[k] = (byte)(c >> 4 & 0xFF);
					chunkNibbleArray.set(l, m, n, c & 15);
				}

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

		for (int o = 0; o < chunk.getEntities().length; o++) {
			for (Entity entity : chunk.getEntities()[o]) {
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
			NbtCompound nbtCompound3 = new NbtCompound();
			blockEntity.toNbt(nbtCompound3);
			nbtList3.add(nbtCompound3);
		}

		nbt.put("TileEntities", nbtList3);
		List<ScheduledTick> list = world.getScheduledTicks(chunk, false);
		if (list != null) {
			long p = world.getLastUpdateTime();
			NbtList nbtList4 = new NbtList();

			for (ScheduledTick scheduledTick : list) {
				NbtCompound nbtCompound4 = new NbtCompound();
				Identifier identifier = Block.REGISTRY.getIdentifier(scheduledTick.getBlock());
				nbtCompound4.putString("i", identifier == null ? "" : identifier.toString());
				nbtCompound4.putInt("x", scheduledTick.pos.getX());
				nbtCompound4.putInt("y", scheduledTick.pos.getY());
				nbtCompound4.putInt("z", scheduledTick.pos.getZ());
				nbtCompound4.putInt("t", (int)(scheduledTick.time - p));
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
			char[] cs = new char[bs.length];

			for (int n = 0; n < cs.length; n++) {
				int o = n & 15;
				int p = n >> 8 & 15;
				int q = n >> 4 & 15;
				int r = chunkNibbleArray2 != null ? chunkNibbleArray2.get(o, p, q) : 0;
				cs[n] = (char)(r << 12 | (bs[n] & 255) << 4 | chunkNibbleArray.get(o, p, q));
			}

			chunkSection.setBlockStates(cs);
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
			for (int s = 0; s < nbtList2.size(); s++) {
				NbtCompound nbtCompound2 = nbtList2.getCompound(s);
				Entity entity = EntityType.createInstanceFromNbt(nbtCompound2, world);
				chunk.setHasEntities(true);
				if (entity != null) {
					chunk.addEntity(entity);
					Entity entity2 = entity;

					for (NbtCompound nbtCompound3 = nbtCompound2; nbtCompound3.contains("Riding", 10); nbtCompound3 = nbtCompound3.getCompound("Riding")) {
						Entity entity3 = EntityType.createInstanceFromNbt(nbtCompound3.getCompound("Riding"), world);
						if (entity3 != null) {
							chunk.addEntity(entity3);
							entity2.startRiding(entity3);
						}

						entity2 = entity3;
					}
				}
			}
		}

		NbtList nbtList3 = nbt.getList("TileEntities", 10);
		if (nbtList3 != null) {
			for (int t = 0; t < nbtList3.size(); t++) {
				NbtCompound nbtCompound4 = nbtList3.getCompound(t);
				BlockEntity blockEntity = BlockEntity.createFromNbt(nbtCompound4);
				if (blockEntity != null) {
					chunk.addBlockEntity(blockEntity);
				}
			}
		}

		if (nbt.contains("TileTicks", 9)) {
			NbtList nbtList4 = nbt.getList("TileTicks", 10);
			if (nbtList4 != null) {
				for (int u = 0; u < nbtList4.size(); u++) {
					NbtCompound nbtCompound5 = nbtList4.getCompound(u);
					Block block;
					if (nbtCompound5.contains("i", 8)) {
						block = Block.get(nbtCompound5.getString("i"));
					} else {
						block = Block.getById(nbtCompound5.getInt("i"));
					}

					world.scheduleTick(
						new BlockPos(nbtCompound5.getInt("x"), nbtCompound5.getInt("y"), nbtCompound5.getInt("z")), block, nbtCompound5.getInt("t"), nbtCompound5.getInt("p")
					);
				}
			}
		}

		return chunk;
	}
}
