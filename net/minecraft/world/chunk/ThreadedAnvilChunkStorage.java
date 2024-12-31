package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_3603;
import net.minecraft.class_3781;
import net.minecraft.class_3786;
import net.minecraft.class_3788;
import net.minecraft.class_3789;
import net.minecraft.class_3790;
import net.minecraft.class_3801;
import net.minecraft.class_3804;
import net.minecraft.class_3979;
import net.minecraft.class_3992;
import net.minecraft.class_4070;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.util.FileIoCallback;
import net.minecraft.util.FileIoThread;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedAnvilChunkStorage implements ChunkStorage, FileIoCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<ChunkPos, NbtCompound> chunksToSave = Maps.newHashMap();
	private final File saveLocation;
	private final DataFixer field_18950;
	private class_3979 field_18951;
	private boolean isSaving;

	public ThreadedAnvilChunkStorage(File file, DataFixer dataFixer) {
		this.saveLocation = file;
		this.field_18950 = dataFixer;
	}

	@Nullable
	private NbtCompound method_17167(IWorld iWorld, int i, int j) throws IOException {
		return this.method_17174(iWorld.method_16393().method_11789(), iWorld.method_16399(), i, j);
	}

	@Nullable
	private NbtCompound method_17174(DimensionType dimensionType, @Nullable class_4070 arg, int i, int j) throws IOException {
		NbtCompound nbtCompound = (NbtCompound)this.chunksToSave.get(new ChunkPos(i, j));
		if (nbtCompound != null) {
			return nbtCompound;
		} else {
			DataInputStream dataInputStream = RegionIo.read(this.saveLocation, i, j);
			if (dataInputStream == null) {
				return null;
			} else {
				NbtCompound nbtCompound2 = NbtIo.read(dataInputStream);
				dataInputStream.close();
				int k = nbtCompound2.contains("DataVersion", 99) ? nbtCompound2.getInt("DataVersion") : -1;
				if (k < 1493) {
					nbtCompound2 = NbtHelper.method_20142(this.field_18950, DataFixTypes.CHUNK, nbtCompound2, k, 1493);
					if (nbtCompound2.getCompound("Level").getBoolean("hasLegacyStructureData")) {
						this.method_17173(dimensionType, arg);
						nbtCompound2 = this.field_18951.method_17614(nbtCompound2);
					}
				}

				nbtCompound2 = NbtHelper.method_20141(this.field_18950, DataFixTypes.CHUNK, nbtCompound2, Math.max(1493, k));
				if (k < 1631) {
					nbtCompound2.putInt("DataVersion", 1631);
					this.registerChunkChecker(new ChunkPos(i, j), nbtCompound2);
				}

				return nbtCompound2;
			}
		}
	}

	public void method_17173(DimensionType dimensionType, @Nullable class_4070 arg) {
		if (this.field_18951 == null) {
			this.field_18951 = class_3979.method_17612(dimensionType, arg);
		}
	}

	@Nullable
	@Override
	public Chunk method_17186(IWorld iWorld, int i, int j, Consumer<Chunk> consumer) throws IOException {
		NbtCompound nbtCompound = this.method_17167(iWorld, i, j);
		if (nbtCompound == null) {
			return null;
		} else {
			Chunk chunk = this.method_3972(iWorld, i, j, nbtCompound);
			if (chunk != null) {
				consumer.accept(chunk);
				this.method_17178(nbtCompound.getCompound("Level"), chunk);
			}

			return chunk;
		}
	}

	@Nullable
	@Override
	public ChunkBlockStateStorage method_17187(IWorld iWorld, int i, int j, Consumer<class_3781> consumer) throws IOException {
		NbtCompound nbtCompound;
		try {
			nbtCompound = this.method_17167(iWorld, i, j);
		} catch (CrashException var7) {
			if (var7.getCause() instanceof IOException) {
				throw (IOException)var7.getCause();
			}

			throw var7;
		}

		if (nbtCompound == null) {
			return null;
		} else {
			ChunkBlockStateStorage chunkBlockStateStorage = this.method_17181(iWorld, i, j, nbtCompound);
			if (chunkBlockStateStorage != null) {
				consumer.accept(chunkBlockStateStorage);
			}

			return chunkBlockStateStorage;
		}
	}

	@Nullable
	protected Chunk method_3972(IWorld iWorld, int i, int j, NbtCompound nbtCompound) {
		if (nbtCompound.contains("Level", 10) && nbtCompound.getCompound("Level").contains("Status", 8)) {
			class_3786.class_3787 lv = this.method_17176(nbtCompound);
			if (lv != class_3786.class_3787.LEVELCHUNK) {
				return null;
			} else {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
				if (!nbtCompound2.contains("Sections", 9)) {
					LOGGER.error("Chunk file at {},{} is missing block data, skipping", i, j);
					return null;
				} else {
					Chunk chunk = this.method_17168(iWorld, nbtCompound2);
					if (!chunk.isChunkEqual(i, j)) {
						LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", i, j, i, j, chunk.chunkX, chunk.chunkZ);
						nbtCompound2.putInt("xPos", i);
						nbtCompound2.putInt("zPos", j);
						chunk = this.method_17168(iWorld, nbtCompound2);
					}

					return chunk;
				}
			}
		} else {
			LOGGER.error("Chunk file at {},{} is missing level data, skipping", i, j);
			return null;
		}
	}

	@Nullable
	protected ChunkBlockStateStorage method_17181(IWorld iWorld, int i, int j, NbtCompound nbtCompound) {
		if (nbtCompound.contains("Level", 10) && nbtCompound.getCompound("Level").contains("Status", 8)) {
			class_3786.class_3787 lv = this.method_17176(nbtCompound);
			if (lv == class_3786.class_3787.LEVELCHUNK) {
				return new class_3788(this.method_3972(iWorld, i, j, nbtCompound));
			} else {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
				return this.method_17182(iWorld, nbtCompound2);
			}
		} else {
			LOGGER.error("Chunk file at {},{} is missing level data, skipping", i, j);
			return null;
		}
	}

	@Override
	public void method_17185(World world, class_3781 arg) throws IOException, WorldSaveException {
		world.readSaveLock();

		try {
			NbtCompound nbtCompound = new NbtCompound();
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound.putInt("DataVersion", 1631);
			ChunkPos chunkPos = arg.method_3920();
			nbtCompound.put("Level", nbtCompound2);
			if (arg.method_17009().method_17054() == class_3786.class_3787.LEVELCHUNK) {
				this.putChunk((Chunk)arg, world, nbtCompound2);
			} else {
				NbtCompound nbtCompound3 = this.method_17167(world, chunkPos.x, chunkPos.z);
				if (nbtCompound3 != null && this.method_17176(nbtCompound3) == class_3786.class_3787.LEVELCHUNK) {
					return;
				}

				this.method_17172((ChunkBlockStateStorage)arg, world, nbtCompound2);
			}

			this.registerChunkChecker(chunkPos, nbtCompound);
		} catch (Exception var7) {
			LOGGER.error("Failed to save chunk", var7);
		}
	}

	protected void registerChunkChecker(ChunkPos pos, NbtCompound nbt) {
		this.chunksToSave.put(pos, nbt);
		FileIoThread.getInstance().registerCallback(this);
	}

	@Override
	public boolean saveNextChunk() {
		Iterator<Entry<ChunkPos, NbtCompound>> iterator = this.chunksToSave.entrySet().iterator();
		if (!iterator.hasNext()) {
			if (this.isSaving) {
				LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.saveLocation.getName());
			}

			return false;
		} else {
			Entry<ChunkPos, NbtCompound> entry = (Entry<ChunkPos, NbtCompound>)iterator.next();
			iterator.remove();
			ChunkPos chunkPos = (ChunkPos)entry.getKey();
			NbtCompound nbtCompound = (NbtCompound)entry.getValue();
			if (nbtCompound == null) {
				return true;
			} else {
				try {
					DataOutputStream dataOutputStream = RegionIo.write(this.saveLocation, chunkPos.x, chunkPos.z);
					NbtIo.write(nbtCompound, dataOutputStream);
					dataOutputStream.close();
					if (this.field_18951 != null) {
						this.field_18951.method_17611(chunkPos.method_16281());
					}
				} catch (Exception var6) {
					LOGGER.error("Failed to save chunk", var6);
				}

				return true;
			}
		}
	}

	private class_3786.class_3787 method_17176(@Nullable NbtCompound nbtCompound) {
		if (nbtCompound != null) {
			class_3786 lv = class_3786.method_17050(nbtCompound.getCompound("Level").getString("Status"));
			if (lv != null) {
				return lv.method_17054();
			}
		}

		return class_3786.class_3787.PROTOCHUNK;
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

	private void method_17172(ChunkBlockStateStorage chunkBlockStateStorage, World world, NbtCompound nbtCompound) {
		int i = chunkBlockStateStorage.method_3920().x;
		int j = chunkBlockStateStorage.method_3920().z;
		nbtCompound.putInt("xPos", i);
		nbtCompound.putInt("zPos", j);
		nbtCompound.putLong("LastUpdate", world.getLastUpdateTime());
		nbtCompound.putLong("InhabitedTime", chunkBlockStateStorage.method_17136());
		nbtCompound.putString("Status", chunkBlockStateStorage.method_17009().method_17052());
		class_3790 lv = chunkBlockStateStorage.method_17145();
		if (!lv.method_17151()) {
			nbtCompound.put("UpgradeData", lv.method_17156());
		}

		ChunkSection[] chunkSections = chunkBlockStateStorage.method_17003();
		NbtList nbtList = this.method_17166(world, chunkSections);
		nbtCompound.put("Sections", nbtList);
		Biome[] biomes = chunkBlockStateStorage.method_17007();
		int[] is = biomes != null ? new int[biomes.length] : new int[0];
		if (biomes != null) {
			for (int k = 0; k < biomes.length; k++) {
				is[k] = Registry.BIOME.getRawId(biomes[k]);
			}
		}

		nbtCompound.putIntArray("Biomes", is);
		NbtList nbtList2 = new NbtList();

		for (NbtCompound nbtCompound2 : chunkBlockStateStorage.method_17142()) {
			nbtList2.add((NbtElement)nbtCompound2);
		}

		nbtCompound.put("Entities", nbtList2);
		NbtList nbtList3 = new NbtList();

		for (BlockPos blockPos : chunkBlockStateStorage.method_17140()) {
			BlockEntity blockEntity = chunkBlockStateStorage.getBlockEntity(blockPos);
			if (blockEntity != null) {
				NbtCompound nbtCompound3 = new NbtCompound();
				blockEntity.toNbt(nbtCompound3);
				nbtList3.add((NbtElement)nbtCompound3);
			} else {
				nbtList3.add((NbtElement)chunkBlockStateStorage.method_17008(blockPos));
			}
		}

		nbtCompound.put("TileEntities", nbtList3);
		nbtCompound.put("Lights", method_17180(chunkBlockStateStorage.method_17139()));
		nbtCompound.put("PostProcessing", method_17180(chunkBlockStateStorage.method_17144()));
		nbtCompound.put("ToBeTicked", chunkBlockStateStorage.method_17011().method_17148());
		nbtCompound.put("LiquidsToBeTicked", chunkBlockStateStorage.method_17012().method_17148());
		NbtCompound nbtCompound4 = new NbtCompound();

		for (class_3804.class_3805 lv2 : chunkBlockStateStorage.method_17143()) {
			nbtCompound4.put(lv2.method_17250(), new NbtLongArray(chunkBlockStateStorage.method_17123(lv2).method_17245()));
		}

		nbtCompound.put("Heightmaps", nbtCompound4);
		NbtCompound nbtCompound5 = new NbtCompound();

		for (class_3801.class_3802 lv3 : class_3801.class_3802.values()) {
			nbtCompound5.putByteArray(lv3.toString(), chunkBlockStateStorage.method_16991(lv3).toByteArray());
		}

		nbtCompound.put("CarvingMasks", nbtCompound5);
		nbtCompound.put("Structures", this.method_17164(i, j, chunkBlockStateStorage.method_17004(), chunkBlockStateStorage.method_17006()));
	}

	private void putChunk(Chunk chunk, World world, NbtCompound nbt) {
		nbt.putInt("xPos", chunk.chunkX);
		nbt.putInt("zPos", chunk.chunkZ);
		nbt.putLong("LastUpdate", world.getLastUpdateTime());
		nbt.putLong("InhabitedTime", chunk.getInhabitedTime());
		nbt.putString("Status", chunk.method_17009().method_17052());
		class_3790 lv = chunk.method_17065();
		if (!lv.method_17151()) {
			nbt.put("UpgradeData", lv.method_17156());
		}

		ChunkSection[] chunkSections = chunk.method_17003();
		NbtList nbtList = this.method_17166(world, chunkSections);
		nbt.put("Sections", nbtList);
		Biome[] biomes = chunk.method_17007();
		int[] is = new int[biomes.length];

		for (int i = 0; i < biomes.length; i++) {
			is[i] = Registry.BIOME.getRawId(biomes[i]);
		}

		nbt.putIntArray("Biomes", is);
		chunk.setHasEntities(false);
		NbtList nbtList2 = new NbtList();

		for (int j = 0; j < chunk.getEntities().length; j++) {
			for (Entity entity : chunk.getEntities()[j]) {
				NbtCompound nbtCompound = new NbtCompound();
				if (entity.saveToNbt(nbtCompound)) {
					chunk.setHasEntities(true);
					nbtList2.add((NbtElement)nbtCompound);
				}
			}
		}

		nbt.put("Entities", nbtList2);
		NbtList nbtList3 = new NbtList();

		for (BlockPos blockPos : chunk.method_17091()) {
			BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
			if (blockEntity != null) {
				NbtCompound nbtCompound2 = new NbtCompound();
				blockEntity.toNbt(nbtCompound2);
				nbtCompound2.putBoolean("keepPacked", false);
				nbtList3.add((NbtElement)nbtCompound2);
			} else {
				NbtCompound nbtCompound3 = chunk.method_17008(blockPos);
				if (nbtCompound3 != null) {
					nbtCompound3.putBoolean("keepPacked", true);
					nbtList3.add((NbtElement)nbtCompound3);
				}
			}
		}

		nbt.put("TileEntities", nbtList3);
		if (world.getBlockTickScheduler() instanceof class_3603) {
			nbt.put("TileTicks", ((class_3603)world.getBlockTickScheduler()).method_16410(chunk));
		}

		if (world.method_16340() instanceof class_3603) {
			nbt.put("LiquidTicks", ((class_3603)world.method_16340()).method_16410(chunk));
		}

		nbt.put("PostProcessing", method_17180(chunk.method_17066()));
		if (chunk.method_17011() instanceof class_3789) {
			nbt.put("ToBeTicked", ((class_3789)chunk.method_17011()).method_17148());
		}

		if (chunk.method_17012() instanceof class_3789) {
			nbt.put("LiquidsToBeTicked", ((class_3789)chunk.method_17012()).method_17148());
		}

		NbtCompound nbtCompound4 = new NbtCompound();

		for (class_3804.class_3805 lv2 : chunk.method_17063()) {
			if (lv2.method_17251() == class_3804.class_3806.LIVE_WORLD) {
				nbtCompound4.put(lv2.method_17250(), new NbtLongArray(chunk.method_17079(lv2).method_17245()));
			}
		}

		nbt.put("Heightmaps", nbtCompound4);
		nbt.put("Structures", this.method_17164(chunk.chunkX, chunk.chunkZ, chunk.method_17004(), chunk.method_17006()));
	}

	private Chunk method_17168(IWorld iWorld, NbtCompound nbtCompound) {
		int i = nbtCompound.getInt("xPos");
		int j = nbtCompound.getInt("zPos");
		Biome[] biomes = new Biome[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (nbtCompound.contains("Biomes", 11)) {
			int[] is = nbtCompound.getIntArray("Biomes");

			for (int k = 0; k < is.length; k++) {
				biomes[k] = Registry.BIOME.getByRawId(is[k]);
				if (biomes[k] == null) {
					biomes[k] = iWorld.method_3586()
						.method_17046()
						.method_17020()
						.method_16480(mutable.setPosition((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
				}
			}
		} else {
			for (int l = 0; l < biomes.length; l++) {
				biomes[l] = iWorld.method_3586()
					.method_17046()
					.method_17020()
					.method_16480(mutable.setPosition((l & 15) + (i << 4), 0, (l >> 4 & 15) + (j << 4)), Biomes.PLAINS);
			}
		}

		class_3790 lv = nbtCompound.contains("UpgradeData", 10) ? new class_3790(nbtCompound.getCompound("UpgradeData")) : class_3790.field_18935;
		class_3789<Block> lv2 = new class_3789<>(block -> block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, new ChunkPos(i, j));
		class_3789<Fluid> lv3 = new class_3789<>(fluid -> fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, new ChunkPos(i, j));
		long m = nbtCompound.getLong("InhabitedTime");
		Chunk chunk = new Chunk(iWorld.method_16348(), i, j, biomes, lv, lv2, lv3, m);
		chunk.method_17083(nbtCompound.getString("Status"));
		NbtList nbtList = nbtCompound.getList("Sections", 10);
		chunk.setLevelChunkSections(this.method_17169(iWorld, nbtList));
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Heightmaps");

		for (class_3804.class_3805 lv4 : class_3804.class_3805.values()) {
			if (lv4.method_17251() == class_3804.class_3806.LIVE_WORLD) {
				String string = lv4.method_17250();
				if (nbtCompound2.contains(string, 12)) {
					chunk.method_17073(lv4, nbtCompound2.getLongArray(string));
				} else {
					chunk.method_17079(lv4).method_17238();
				}
			}
		}

		NbtCompound nbtCompound3 = nbtCompound.getCompound("Structures");
		chunk.method_17076(this.method_17184(iWorld, nbtCompound3));
		chunk.method_17080(this.method_17183(nbtCompound3));
		NbtList nbtList2 = nbtCompound.getList("PostProcessing", 9);

		for (int n = 0; n < nbtList2.size(); n++) {
			NbtList nbtList3 = nbtList2.getList(n);

			for (int o = 0; o < nbtList3.size(); o++) {
				chunk.method_17077(nbtList3.getShort(o), n);
			}
		}

		lv2.method_17150(nbtCompound.getList("ToBeTicked", 9));
		lv3.method_17150(nbtCompound.getList("LiquidsToBeTicked", 9));
		if (nbtCompound.getBoolean("shouldSave")) {
			chunk.setModified(true);
		}

		return chunk;
	}

	private void method_17178(NbtCompound nbtCompound, Chunk chunk) {
		NbtList nbtList = nbtCompound.getList("Entities", 10);
		World world = chunk.getWorld();

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound2 = nbtList.getCompound(i);
			method_11783(nbtCompound2, world, chunk);
			chunk.setHasEntities(true);
		}

		NbtList nbtList2 = nbtCompound.getList("TileEntities", 10);

		for (int j = 0; j < nbtList2.size(); j++) {
			NbtCompound nbtCompound3 = nbtList2.getCompound(j);
			boolean bl = nbtCompound3.getBoolean("keepPacked");
			if (bl) {
				chunk.method_16995(nbtCompound3);
			} else {
				BlockEntity blockEntity = BlockEntity.method_16781(nbtCompound3);
				if (blockEntity != null) {
					chunk.addBlockEntity(blockEntity);
				}
			}
		}

		if (nbtCompound.contains("TileTicks", 9) && world.getBlockTickScheduler() instanceof class_3603) {
			((class_3603)world.getBlockTickScheduler()).method_16414(nbtCompound.getList("TileTicks", 10));
		}

		if (nbtCompound.contains("LiquidTicks", 9) && world.method_16340() instanceof class_3603) {
			((class_3603)world.method_16340()).method_16414(nbtCompound.getList("LiquidTicks", 10));
		}
	}

	private ChunkBlockStateStorage method_17182(IWorld iWorld, NbtCompound nbtCompound) {
		int i = nbtCompound.getInt("xPos");
		int j = nbtCompound.getInt("zPos");
		Biome[] biomes = new Biome[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (nbtCompound.contains("Biomes", 11)) {
			int[] is = nbtCompound.getIntArray("Biomes");

			for (int k = 0; k < is.length; k++) {
				biomes[k] = Registry.BIOME.getByRawId(is[k]);
				if (biomes[k] == null) {
					biomes[k] = iWorld.method_3586()
						.method_17046()
						.method_17020()
						.method_16480(mutable.setPosition((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
				}
			}
		} else {
			for (int l = 0; l < biomes.length; l++) {
				biomes[l] = iWorld.method_3586()
					.method_17046()
					.method_17020()
					.method_16480(mutable.setPosition((l & 15) + (i << 4), 0, (l >> 4 & 15) + (j << 4)), Biomes.PLAINS);
			}
		}

		class_3790 lv = nbtCompound.contains("UpgradeData", 10) ? new class_3790(nbtCompound.getCompound("UpgradeData")) : class_3790.field_18935;
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage(i, j, lv);
		chunkBlockStateStorage.method_16999(biomes);
		chunkBlockStateStorage.method_17121(nbtCompound.getLong("InhabitedTime"));
		chunkBlockStateStorage.method_17129(nbtCompound.getString("Status"));
		NbtList nbtList = nbtCompound.getList("Sections", 10);
		chunkBlockStateStorage.method_17118(this.method_17169(iWorld, nbtList));
		NbtList nbtList2 = nbtCompound.getList("Entities", 10);

		for (int m = 0; m < nbtList2.size(); m++) {
			chunkBlockStateStorage.method_17124(nbtList2.getCompound(m));
		}

		NbtList nbtList3 = nbtCompound.getList("TileEntities", 10);

		for (int n = 0; n < nbtList3.size(); n++) {
			NbtCompound nbtCompound2 = nbtList3.getCompound(n);
			chunkBlockStateStorage.method_16995(nbtCompound2);
		}

		NbtList nbtList4 = nbtCompound.getList("Lights", 9);

		for (int o = 0; o < nbtList4.size(); o++) {
			NbtList nbtList5 = nbtList4.getList(o);

			for (int p = 0; p < nbtList5.size(); p++) {
				chunkBlockStateStorage.method_17115(nbtList5.getShort(p), o);
			}
		}

		NbtList nbtList6 = nbtCompound.getList("PostProcessing", 9);

		for (int q = 0; q < nbtList6.size(); q++) {
			NbtList nbtList7 = nbtList6.getList(q);

			for (int r = 0; r < nbtList7.size(); r++) {
				chunkBlockStateStorage.method_17126(nbtList7.getShort(r), q);
			}
		}

		chunkBlockStateStorage.method_17011().method_17150(nbtCompound.getList("ToBeTicked", 9));
		chunkBlockStateStorage.method_17012().method_17150(nbtCompound.getList("LiquidsToBeTicked", 9));
		NbtCompound nbtCompound3 = nbtCompound.getCompound("Heightmaps");

		for (String string : nbtCompound3.getKeys()) {
			chunkBlockStateStorage.method_17112(class_3804.class_3805.method_17248(string), nbtCompound3.getLongArray(string));
		}

		NbtCompound nbtCompound4 = nbtCompound.getCompound("Structures");
		chunkBlockStateStorage.method_17114(this.method_17184(iWorld, nbtCompound4));
		chunkBlockStateStorage.method_17125(this.method_17183(nbtCompound4));
		NbtCompound nbtCompound5 = nbtCompound.getCompound("CarvingMasks");

		for (String string2 : nbtCompound5.getKeys()) {
			class_3801.class_3802 lv2 = class_3801.class_3802.valueOf(string2);
			chunkBlockStateStorage.method_17111(lv2, BitSet.valueOf(nbtCompound5.getByteArray(string2)));
		}

		return chunkBlockStateStorage;
	}

	private NbtList method_17166(World world, ChunkSection[] chunkSections) {
		NbtList nbtList = new NbtList();
		boolean bl = world.dimension.isOverworld();

		for (ChunkSection chunkSection : chunkSections) {
			if (chunkSection != Chunk.EMPTY) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Y", (byte)(chunkSection.getYOffset() >> 4 & 0xFF));
				chunkSection.getBlockData().method_17107(nbtCompound, "Palette", "BlockStates");
				nbtCompound.putByteArray("BlockLight", chunkSection.getBlockLight().getValue());
				if (bl) {
					nbtCompound.putByteArray("SkyLight", chunkSection.getSkyLight().getValue());
				} else {
					nbtCompound.putByteArray("SkyLight", new byte[chunkSection.getBlockLight().getValue().length]);
				}

				nbtList.add((NbtElement)nbtCompound);
			}
		}

		return nbtList;
	}

	private ChunkSection[] method_17169(RenderBlockView renderBlockView, NbtList nbtList) {
		int i = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl = renderBlockView.method_16393().isOverworld();

		for (int j = 0; j < nbtList.size(); j++) {
			NbtCompound nbtCompound = nbtList.getCompound(j);
			int k = nbtCompound.getByte("Y");
			ChunkSection chunkSection = new ChunkSection(k << 4, bl);
			chunkSection.getBlockData().method_17103(nbtCompound, "Palette", "BlockStates");
			chunkSection.setBlockLight(new ChunkNibbleArray(nbtCompound.getByteArray("BlockLight")));
			if (bl) {
				chunkSection.setSkyLight(new ChunkNibbleArray(nbtCompound.getByteArray("SkyLight")));
			}

			chunkSection.calculateCounts();
			chunkSections[k] = chunkSection;
		}

		return chunkSections;
	}

	private NbtCompound method_17164(int i, int j, Map<String, class_3992> map, Map<String, LongSet> map2) {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();

		for (Entry<String, class_3992> entry : map.entrySet()) {
			nbtCompound2.put((String)entry.getKey(), ((class_3992)entry.getValue()).method_17659(i, j));
		}

		nbtCompound.put("Starts", nbtCompound2);
		NbtCompound nbtCompound3 = new NbtCompound();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			nbtCompound3.put((String)entry2.getKey(), new NbtLongArray((LongSet)entry2.getValue()));
		}

		nbtCompound.put("References", nbtCompound3);
		return nbtCompound;
	}

	private Map<String, class_3992> method_17184(IWorld iWorld, NbtCompound nbtCompound) {
		Map<String, class_3992> map = Maps.newHashMap();
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Starts");

		for (String string : nbtCompound2.getKeys()) {
			map.put(string, StructurePieceManager.method_17641(nbtCompound2.getCompound(string), iWorld));
		}

		return map;
	}

	private Map<String, LongSet> method_17183(NbtCompound nbtCompound) {
		Map<String, LongSet> map = Maps.newHashMap();
		NbtCompound nbtCompound2 = nbtCompound.getCompound("References");

		for (String string : nbtCompound2.getKeys()) {
			map.put(string, new LongOpenHashSet(nbtCompound2.getLongArray(string)));
		}

		return map;
	}

	public static NbtList method_17180(ShortList[] shortLists) {
		NbtList nbtList = new NbtList();

		for (ShortList shortList : shortLists) {
			NbtList nbtList2 = new NbtList();
			if (shortList != null) {
				ShortListIterator var7 = shortList.iterator();

				while (var7.hasNext()) {
					Short short_ = (Short)var7.next();
					nbtList2.add((NbtElement)(new NbtShort(short_)));
				}
			}

			nbtList.add((NbtElement)nbtList2);
		}

		return nbtList;
	}

	@Nullable
	private static Entity method_17177(NbtCompound nbtCompound, World world, Function<Entity, Entity> function) {
		Entity entity = method_11781(nbtCompound, world);
		if (entity == null) {
			return null;
		} else {
			entity = (Entity)function.apply(entity);
			if (entity != null && nbtCompound.contains("Passengers", 9)) {
				NbtList nbtList = nbtCompound.getList("Passengers", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					Entity entity2 = method_17177(nbtList.getCompound(i), world, function);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}
	}

	@Nullable
	public static Entity method_11783(NbtCompound tag, World world, Chunk chunk) {
		return method_17177(tag, world, entity -> {
			chunk.method_3887(entity);
			return entity;
		});
	}

	@Nullable
	public static Entity method_11782(NbtCompound tag, World world, double x, double y, double z, boolean bl) {
		return method_17177(tag, world, entity -> {
			entity.refreshPositionAndAngles(x, y, z, entity.yaw, entity.pitch);
			return bl && !world.method_3686(entity) ? null : entity;
		});
	}

	@Nullable
	public static Entity method_11784(NbtCompound tag, World world, boolean bl) {
		return method_17177(tag, world, entity -> bl && !world.method_3686(entity) ? null : entity);
	}

	@Nullable
	protected static Entity method_11781(NbtCompound tag, World world) {
		try {
			return EntityType.method_15623(tag, world);
		} catch (RuntimeException var3) {
			LOGGER.warn("Exception loading entity: ", var3);
			return null;
		}
	}

	public static void method_11785(Entity entity, IWorld iWorld) {
		if (iWorld.method_3686(entity) && entity.hasPassengers()) {
			for (Entity entity2 : entity.getPassengerList()) {
				method_11785(entity2, iWorld);
			}
		}
	}

	public boolean method_17165(ChunkPos chunkPos, DimensionType dimensionType, class_4070 arg) {
		boolean bl = false;

		try {
			this.method_17174(dimensionType, arg, chunkPos.x, chunkPos.z);

			while (this.saveNextChunk()) {
				bl = true;
			}
		} catch (IOException var6) {
		}

		return bl;
	}
}
