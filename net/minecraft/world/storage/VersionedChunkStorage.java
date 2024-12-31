package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;

public class VersionedChunkStorage implements AutoCloseable {
	private final StorageIoWorker worker;
	protected final DataFixer dataFixer;
	@Nullable
	private FeatureUpdater featureUpdater;

	public VersionedChunkStorage(File file, DataFixer dataFixer) {
		this.dataFixer = dataFixer;
		this.worker = new StorageIoWorker(new RegionBasedStorage(file), "chunk");
	}

	public CompoundTag updateChunkTag(DimensionType dimensionType, Supplier<PersistentStateManager> supplier, CompoundTag compoundTag) {
		int i = getDataVersion(compoundTag);
		int j = 1493;
		if (i < 1493) {
			compoundTag = NbtHelper.update(this.dataFixer, DataFixTypes.field_19214, compoundTag, i, 1493);
			if (compoundTag.getCompound("Level").getBoolean("hasLegacyStructureData")) {
				if (this.featureUpdater == null) {
					this.featureUpdater = FeatureUpdater.create(dimensionType, (PersistentStateManager)supplier.get());
				}

				compoundTag = this.featureUpdater.getUpdatedReferences(compoundTag);
			}
		}

		compoundTag = NbtHelper.update(this.dataFixer, DataFixTypes.field_19214, compoundTag, Math.max(1493, i));
		if (i < SharedConstants.getGameVersion().getWorldVersion()) {
			compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		}

		return compoundTag;
	}

	public static int getDataVersion(CompoundTag compoundTag) {
		return compoundTag.contains("DataVersion", 99) ? compoundTag.getInt("DataVersion") : -1;
	}

	@Nullable
	public CompoundTag getNbt(ChunkPos chunkPos) throws IOException {
		return this.worker.getNbt(chunkPos);
	}

	public void setTagAt(ChunkPos chunkPos, CompoundTag compoundTag) {
		this.worker.setResult(chunkPos, compoundTag);
		if (this.featureUpdater != null) {
			this.featureUpdater.markResolved(chunkPos.toLong());
		}
	}

	public void completeAll() {
		this.worker.completeAll().join();
	}

	public void close() throws IOException {
		this.worker.close();
	}
}
