package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static ProtoChunk deserialize(
		ServerWorld serverWorld, StructureManager structureManager, PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, CompoundTag compoundTag
	) {
		ChunkGenerator<?> chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		ChunkPos chunkPos2 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
		if (!Objects.equals(chunkPos, chunkPos2)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, chunkPos2);
		}

		BiomeArray biomeArray = new BiomeArray(chunkPos, biomeSource, compoundTag2.contains("Biomes", 11) ? compoundTag2.getIntArray("Biomes") : null);
		UpgradeData upgradeData = compoundTag2.contains("UpgradeData", 10) ? new UpgradeData(compoundTag2.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.getDefaultState().isAir(), chunkPos, compoundTag2.getList("ToBeTicked", 9)
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.field_15906, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9)
		);
		boolean bl = compoundTag2.getBoolean("isLightOn");
		ListTag listTag = compoundTag2.getList("Sections", 10);
		int i = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = serverWorld.getDimension().hasSkyLight();
		ChunkManager chunkManager = serverWorld.getChunkManager();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		if (bl) {
			lightingProvider.setRetainData(chunkPos, true);
		}

		for (int j = 0; j < listTag.size(); j++) {
			CompoundTag compoundTag3 = listTag.getCompound(j);
			int k = compoundTag3.getByte("Y");
			if (compoundTag3.contains("Palette", 9) && compoundTag3.contains("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(k << 4);
				chunkSection.getContainer().read(compoundTag3.getList("Palette", 10), compoundTag3.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				if (!chunkSection.isEmpty()) {
					chunkSections[k] = chunkSection;
				}

				pointOfInterestStorage.initForPalette(chunkPos, chunkSection);
			}

			if (bl) {
				if (compoundTag3.contains("BlockLight", 7)) {
					lightingProvider.queueData(LightType.field_9282, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(compoundTag3.getByteArray("BlockLight")));
				}

				if (bl2 && compoundTag3.contains("SkyLight", 7)) {
					lightingProvider.queueData(LightType.field_9284, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(compoundTag3.getByteArray("SkyLight")));
				}
			}
		}

		long l = compoundTag2.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = getChunkType(compoundTag);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			TickScheduler<Block> tickScheduler;
			if (compoundTag2.contains("TileTicks", 9)) {
				tickScheduler = SimpleTickScheduler.fromNbt(compoundTag2.getList("TileTicks", 10), Registry.field_11146::getId, Registry.field_11146::get);
			} else {
				tickScheduler = chunkTickScheduler;
			}

			TickScheduler<Fluid> tickScheduler3;
			if (compoundTag2.contains("LiquidTicks", 9)) {
				tickScheduler3 = SimpleTickScheduler.fromNbt(compoundTag2.getList("LiquidTicks", 10), Registry.field_11154::getId, Registry.field_11154::get);
			} else {
				tickScheduler3 = chunkTickScheduler2;
			}

			chunk = new WorldChunk(
				serverWorld.getWorld(),
				chunkPos,
				biomeArray,
				upgradeData,
				tickScheduler,
				tickScheduler3,
				l,
				chunkSections,
				worldChunk -> writeEntities(compoundTag2, worldChunk)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
			protoChunk.method_22405(biomeArray);
			chunk = protoChunk;
			protoChunk.setInhabitedTime(l);
			protoChunk.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
			if (protoChunk.getStatus().isAtLeast(ChunkStatus.field_12795)) {
				protoChunk.setLightingProvider(lightingProvider);
			}

			if (!bl && protoChunk.getStatus().isAtLeast(ChunkStatus.field_12805)) {
				for (BlockPos blockPos : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
					if (chunk.getBlockState(blockPos).getLuminance() != 0) {
						protoChunk.addLightSource(blockPos);
					}
				}
			}
		}

		chunk.setLightOn(bl);
		CompoundTag compoundTag4 = compoundTag2.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
			String string = type.getName();
			if (compoundTag4.contains(string, 12)) {
				chunk.setHeightmap(type, compoundTag4.getLongArray(string));
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		CompoundTag compoundTag5 = compoundTag2.getCompound("Structures");
		chunk.setStructureStarts(readStructureStarts(chunkGenerator, structureManager, compoundTag5));
		chunk.setStructureReferences(readStructureReferences(chunkPos, compoundTag5));
		if (compoundTag2.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		ListTag listTag2 = compoundTag2.getList("PostProcessing", 9);

		for (int m = 0; m < listTag2.size(); m++) {
			ListTag listTag3 = listTag2.getList(m);

			for (int n = 0; n < listTag3.size(); n++) {
				chunk.markBlockForPostProcessing(listTag3.getShort(n), m);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			return new ReadOnlyChunk((WorldChunk)chunk);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			ListTag listTag4 = compoundTag2.getList("Entities", 10);

			for (int o = 0; o < listTag4.size(); o++) {
				protoChunk2.addEntity(listTag4.getCompound(o));
			}

			ListTag listTag5 = compoundTag2.getList("TileEntities", 10);

			for (int p = 0; p < listTag5.size(); p++) {
				CompoundTag compoundTag6 = listTag5.getCompound(p);
				chunk.addPendingBlockEntityTag(compoundTag6);
			}

			ListTag listTag6 = compoundTag2.getList("Lights", 9);

			for (int q = 0; q < listTag6.size(); q++) {
				ListTag listTag7 = listTag6.getList(q);

				for (int r = 0; r < listTag7.size(); r++) {
					protoChunk2.addLightSource(listTag7.getShort(r), q);
				}
			}

			CompoundTag compoundTag7 = compoundTag2.getCompound("CarvingMasks");

			for (String string2 : compoundTag7.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.setCarvingMask(carver, BitSet.valueOf(compoundTag7.getByteArray(string2)));
			}

			return protoChunk2;
		}
	}

	public static CompoundTag serialize(ServerWorld serverWorld, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Level", compoundTag2);
		compoundTag2.putInt("xPos", chunkPos.x);
		compoundTag2.putInt("zPos", chunkPos.z);
		compoundTag2.putLong("LastUpdate", serverWorld.getTime());
		compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
		compoundTag2.putString("Status", chunk.getStatus().getId());
		UpgradeData upgradeData = chunk.getUpgradeData();
		if (!upgradeData.isDone()) {
			compoundTag2.put("UpgradeData", upgradeData.toTag());
		}

		ChunkSection[] chunkSections = chunk.getSectionArray();
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = serverWorld.getChunkManager().getLightingProvider();
		boolean bl = chunk.isLightOn();

		for (int i = -1; i < 17; i++) {
			int j = i;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == j)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.field_9282).getLightArray(ChunkSectionPos.from(chunkPos, j));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.field_9284).getLightArray(ChunkSectionPos.from(chunkPos, j));
			if (chunkSection != WorldChunk.EMPTY_SECTION || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				CompoundTag compoundTag3 = new CompoundTag();
				compoundTag3.putByte("Y", (byte)(j & 0xFF));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().write(compoundTag3, "Palette", "BlockStates");
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					compoundTag3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					compoundTag3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				listTag.add(compoundTag3);
			}
		}

		compoundTag2.put("Sections", listTag);
		if (bl) {
			compoundTag2.putBoolean("isLightOn", true);
		}

		BiomeArray biomeArray = chunk.getBiomeArray();
		if (biomeArray != null) {
			compoundTag2.putIntArray("Biomes", biomeArray.toIntArray());
		}

		ListTag listTag2 = new ListTag();

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			CompoundTag compoundTag4 = chunk.method_20598(blockPos);
			if (compoundTag4 != null) {
				listTag2.add(compoundTag4);
			}
		}

		compoundTag2.put("TileEntities", listTag2);
		ListTag listTag3 = new ListTag();
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
			WorldChunk worldChunk = (WorldChunk)chunk;
			worldChunk.setUnsaved(false);

			for (int k = 0; k < worldChunk.getEntitySectionArray().length; k++) {
				for (Entity entity : worldChunk.getEntitySectionArray()[k]) {
					CompoundTag compoundTag5 = new CompoundTag();
					if (entity.saveToTag(compoundTag5)) {
						worldChunk.setUnsaved(true);
						listTag3.add(compoundTag5);
					}
				}
			}
		} else {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			listTag3.addAll(protoChunk.getEntities());
			compoundTag2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			CompoundTag compoundTag6 = new CompoundTag();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				compoundTag6.putByteArray(carver.toString(), chunk.getCarvingMask(carver).toByteArray());
			}

			compoundTag2.put("CarvingMasks", compoundTag6);
		}

		compoundTag2.put("Entities", listTag3);
		TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
		if (tickScheduler instanceof ChunkTickScheduler) {
			compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
		} else if (tickScheduler instanceof SimpleTickScheduler) {
			compoundTag2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toNbt(serverWorld.getTime()));
		} else {
			compoundTag2.put("TileTicks", serverWorld.getBlockTickScheduler().toTag(chunkPos));
		}

		TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
		if (tickScheduler2 instanceof ChunkTickScheduler) {
			compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
		} else if (tickScheduler2 instanceof SimpleTickScheduler) {
			compoundTag2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toNbt(serverWorld.getTime()));
		} else {
			compoundTag2.put("LiquidTicks", serverWorld.getFluidTickScheduler().toTag(chunkPos));
		}

		compoundTag2.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
		CompoundTag compoundTag7 = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				compoundTag7.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		compoundTag2.put("Heightmaps", compoundTag7);
		compoundTag2.put("Structures", writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return compoundTag;
	}

	public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			ChunkStatus chunkStatus = ChunkStatus.get(compoundTag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.field_12808;
	}

	private static void writeEntities(CompoundTag compoundTag, WorldChunk worldChunk) {
		ListTag listTag = compoundTag.getList("Entities", 10);
		World world = worldChunk.getWorld();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompound(i);
			EntityType.loadEntityWithPassengers(compoundTag2, world, entity -> {
				worldChunk.addEntity(entity);
				return entity;
			});
			worldChunk.setUnsaved(true);
		}

		ListTag listTag2 = compoundTag.getList("TileEntities", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			CompoundTag compoundTag3 = listTag2.getCompound(j);
			boolean bl = compoundTag3.getBoolean("keepPacked");
			if (bl) {
				worldChunk.addPendingBlockEntityTag(compoundTag3);
			} else {
				BlockEntity blockEntity = BlockEntity.createFromTag(compoundTag3);
				if (blockEntity != null) {
					worldChunk.addBlockEntity(blockEntity);
				}
			}
		}
	}

	private static CompoundTag writeStructures(ChunkPos chunkPos, Map<String, StructureStart> map, Map<String, LongSet> map2) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<String, StructureStart> entry : map.entrySet()) {
			compoundTag2.put((String)entry.getKey(), ((StructureStart)entry.getValue()).toTag(chunkPos.x, chunkPos.z));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			compoundTag3.put((String)entry2.getKey(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private static Map<String, StructureStart> readStructureStarts(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, CompoundTag compoundTag) {
		Map<String, StructureStart> map = Maps.newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("Starts");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, StructureFeatures.readStructureStart(chunkGenerator, structureManager, compoundTag2.getCompound(string)));
		}

		return map;
	}

	private static Map<String, LongSet> readStructureReferences(ChunkPos chunkPos, CompoundTag compoundTag) {
		Map<String, LongSet> map = Maps.newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("References");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, new LongOpenHashSet(Arrays.stream(compoundTag2.getLongArray(string)).filter(l -> {
				ChunkPos chunkPos2 = new ChunkPos(l);
				if (chunkPos2.method_24022(chunkPos) > 8) {
					LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", string, chunkPos2, chunkPos);
					return false;
				} else {
					return true;
				}
			}).toArray()));
		}

		return map;
	}

	public static ListTag toNbt(ShortList[] shortLists) {
		ListTag listTag = new ListTag();

		for (ShortList shortList : shortLists) {
			ListTag listTag2 = new ListTag();
			if (shortList != null) {
				ShortListIterator var7 = shortList.iterator();

				while (var7.hasNext()) {
					Short short_ = (Short)var7.next();
					listTag2.add(ShortTag.of(short_));
				}
			}

			listTag.add(listTag2);
		}

		return listTag;
	}
}
