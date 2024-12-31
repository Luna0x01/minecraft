package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
	private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.field_13195, Heightmap.Type.field_13194);
	private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(
		Heightmap.Type.field_13200, Heightmap.Type.field_13202, Heightmap.Type.field_13197, Heightmap.Type.field_13203
	);
	private static final ChunkStatus.NoGenTask STATUS_BUMP_NO_GEN_TASK = (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> {
		if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(chunkStatus)) {
			((ProtoChunk)chunk).setStatus(chunkStatus);
		}

		return CompletableFuture.completedFuture(Either.left(chunk));
	};
	public static final ChunkStatus field_12798 = register(
		"empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus field_16423 = register(
		"structure_starts",
		field_12798,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				if (serverWorld.getLevelProperties().hasStructures()) {
					chunkGenerator.setStructureStarts(serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, chunkGenerator, structureManager);
				}

				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus field_16422 = register(
		"structure_references",
		field_16423,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus field_12794 = register(
		"biomes",
		field_16422,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateBiomes(chunk)
	);
	public static final ChunkStatus field_12804 = register(
		"noise",
		field_12794,
		8,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus field_12796 = register(
		"surface",
		field_12804,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.buildSurface(new ChunkRegion(serverWorld, list), chunk)
	);
	public static final ChunkStatus field_12801 = register(
		"carvers",
		field_12796,
		0,
		PRE_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.field_13169
			)
	);
	public static final ChunkStatus field_12790 = register(
		"liquid_carvers",
		field_12801,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.carve(
				serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.field_13166
			)
	);
	public static final ChunkStatus field_12795 = register(
		"features",
		field_12790,
		8,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			protoChunk.setLightingProvider(serverLightingProvider);
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				Heightmap.populateHeightmaps(
					chunk, EnumSet.of(Heightmap.Type.field_13197, Heightmap.Type.field_13203, Heightmap.Type.field_13200, Heightmap.Type.field_13202)
				);
				chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list));
				protoChunk.setStatus(chunkStatus);
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}
	);
	public static final ChunkStatus field_12805 = register(
		"light",
		field_12795,
		1,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> method_20610(
				chunkStatus, serverLightingProvider, chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> method_20610(chunkStatus, serverLightingProvider, chunk)
	);
	public static final ChunkStatus field_12786 = register(
		"spawn",
		field_12805,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12808,
		(serverWorld, chunkGenerator, list, chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list))
	);
	public static final ChunkStatus field_12800 = register(
		"heightmaps", field_12786, 0, POST_CARVER_HEIGHTMAPS, ChunkStatus.ChunkType.field_12808, (serverWorld, chunkGenerator, list, chunk) -> {
		}
	);
	public static final ChunkStatus field_12803 = register(
		"full",
		field_12800,
		0,
		POST_CARVER_HEIGHTMAPS,
		ChunkStatus.ChunkType.field_12807,
		(chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			),
		(chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)function.apply(
				chunk
			)
	);
	private static final List<ChunkStatus> DISTANCE_TO_TARGET_GENERATION_STATUS = ImmutableList.of(
		field_12803, field_12795, field_12790, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423
	);
	private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS = Util.make(new IntArrayList(createOrderedList().size()), intArrayList -> {
		int i = 0;

		for (int j = createOrderedList().size() - 1; j >= 0; j--) {
			while (i + 1 < DISTANCE_TO_TARGET_GENERATION_STATUS.size() && j <= ((ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(i + 1)).getIndex()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String id;
	private final int index;
	private final ChunkStatus previous;
	private final ChunkStatus.Task task;
	private final ChunkStatus.NoGenTask noGenTask;
	private final int taskMargin;
	private final ChunkStatus.ChunkType chunkType;
	private final EnumSet<Heightmap.Type> heightMapTypes;

	private static CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> method_20610(
		ChunkStatus chunkStatus, ServerLightingProvider serverLightingProvider, Chunk chunk
	) {
		boolean bl = method_20608(chunkStatus, chunk);
		if (!chunk.getStatus().isAtLeast(chunkStatus)) {
			((ProtoChunk)chunk).setStatus(chunkStatus);
		}

		return serverLightingProvider.light(chunk, bl).thenApply(Either::left);
	}

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkStatus.ChunkType chunkType, ChunkStatus.SimpleTask simpleTask
	) {
		return register(string, chunkStatus, i, enumSet, chunkType, (ChunkStatus.Task)simpleTask);
	}

	private static ChunkStatus register(
		String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkStatus.ChunkType chunkType, ChunkStatus.Task task
	) {
		return register(string, chunkStatus, i, enumSet, chunkType, task, STATUS_BUMP_NO_GEN_TASK);
	}

	private static ChunkStatus register(
		String string,
		@Nullable ChunkStatus chunkStatus,
		int i,
		EnumSet<Heightmap.Type> enumSet,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.Task task,
		ChunkStatus.NoGenTask noGenTask
	) {
		return Registry.register(Registry.field_16643, string, new ChunkStatus(string, chunkStatus, i, enumSet, chunkType, task, noGenTask));
	}

	public static List<ChunkStatus> createOrderedList() {
		List<ChunkStatus> list = Lists.newArrayList();

		ChunkStatus chunkStatus;
		for (chunkStatus = field_12803; chunkStatus.getPrevious() != chunkStatus; chunkStatus = chunkStatus.getPrevious()) {
			list.add(chunkStatus);
		}

		list.add(chunkStatus);
		Collections.reverse(list);
		return list;
	}

	private static boolean method_20608(ChunkStatus chunkStatus, Chunk chunk) {
		return chunk.getStatus().isAtLeast(chunkStatus) && chunk.isLightOn();
	}

	public static ChunkStatus getTargetGenerationStatus(int i) {
		if (i >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
			return field_12798;
		} else {
			return i < 0 ? field_12803 : (ChunkStatus)DISTANCE_TO_TARGET_GENERATION_STATUS.get(i);
		}
	}

	public static int getMaxTargetGenerationRadius() {
		return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
	}

	public static int getTargetGenerationRadius(ChunkStatus chunkStatus) {
		return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(chunkStatus.getIndex());
	}

	ChunkStatus(
		String string,
		@Nullable ChunkStatus chunkStatus,
		int i,
		EnumSet<Heightmap.Type> enumSet,
		ChunkStatus.ChunkType chunkType,
		ChunkStatus.Task task,
		ChunkStatus.NoGenTask noGenTask
	) {
		this.id = string;
		this.previous = chunkStatus == null ? this : chunkStatus;
		this.task = task;
		this.noGenTask = noGenTask;
		this.taskMargin = i;
		this.chunkType = chunkType;
		this.heightMapTypes = enumSet;
		this.index = chunkStatus == null ? 0 : chunkStatus.getIndex() + 1;
	}

	public int getIndex() {
		return this.index;
	}

	public String getId() {
		return this.id;
	}

	public ChunkStatus getPrevious() {
		return this.previous;
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(
		ServerWorld serverWorld,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		List<Chunk> list
	) {
		return this.task.doWork(this, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, (Chunk)list.get(list.size() / 2));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runNoGenTask(
		ServerWorld serverWorld,
		StructureManager structureManager,
		ServerLightingProvider serverLightingProvider,
		Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
		Chunk chunk
	) {
		return this.noGenTask.doWork(this, serverWorld, structureManager, serverLightingProvider, function, chunk);
	}

	public int getTaskMargin() {
		return this.taskMargin;
	}

	public ChunkStatus.ChunkType getChunkType() {
		return this.chunkType;
	}

	public static ChunkStatus get(String string) {
		return Registry.field_16643.get(Identifier.tryParse(string));
	}

	public EnumSet<Heightmap.Type> getHeightmapTypes() {
		return this.heightMapTypes;
	}

	public boolean isAtLeast(ChunkStatus chunkStatus) {
		return this.getIndex() >= chunkStatus.getIndex();
	}

	public String toString() {
		return Registry.field_16643.getId(this).toString();
	}

	public static enum ChunkType {
		field_12808,
		field_12807;
	}

	interface NoGenTask {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			Chunk chunk
		);
	}

	interface SimpleTask extends ChunkStatus.Task {
		@Override
		default CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk
		) {
			if (!chunk.getStatus().isAtLeast(chunkStatus)) {
				this.doWork(serverWorld, chunkGenerator, list, chunk);
				if (chunk instanceof ProtoChunk) {
					((ProtoChunk)chunk).setStatus(chunkStatus);
				}
			}

			return CompletableFuture.completedFuture(Either.left(chunk));
		}

		void doWork(ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk);
	}

	interface Task {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(
			ChunkStatus chunkStatus,
			ServerWorld serverWorld,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			ServerLightingProvider serverLightingProvider,
			Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function,
			List<Chunk> list,
			Chunk chunk
		);
	}
}
