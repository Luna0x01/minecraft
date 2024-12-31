package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart {
	public static final StructureStart DEFAULT = new StructureStart(Feature.MINESHAFT, 0, 0, BlockBox.empty(), 0, 0L) {
		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
		}
	};
	private final StructureFeature<?> feature;
	protected final List<StructurePiece> children = Lists.newArrayList();
	protected BlockBox boundingBox;
	private final int chunkX;
	private final int chunkZ;
	private int references;
	protected final ChunkRandom random;

	public StructureStart(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
		this.feature = structureFeature;
		this.chunkX = i;
		this.chunkZ = j;
		this.references = k;
		this.random = new ChunkRandom();
		this.random.setStructureSeed(l, i, j);
		this.boundingBox = blockBox;
	}

	public abstract void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public List<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		synchronized (this.children) {
			Iterator<StructurePiece> iterator = this.children.iterator();

			while (iterator.hasNext()) {
				StructurePiece structurePiece = (StructurePiece)iterator.next();
				if (structurePiece.getBoundingBox().intersects(blockBox) && !structurePiece.generate(iWorld, chunkGenerator, random, blockBox, chunkPos)) {
					iterator.remove();
				}
			}

			this.setBoundingBoxFromChildren();
		}
	}

	protected void setBoundingBoxFromChildren() {
		this.boundingBox = BlockBox.empty();

		for (StructurePiece structurePiece : this.children) {
			this.boundingBox.encompass(structurePiece.getBoundingBox());
		}
	}

	public CompoundTag toTag(int i, int j) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.hasChildren()) {
			compoundTag.putString("id", Registry.field_16644.getId(this.getFeature()).toString());
			compoundTag.putInt("ChunkX", i);
			compoundTag.putInt("ChunkZ", j);
			compoundTag.putInt("references", this.references);
			compoundTag.put("BB", this.boundingBox.toNbt());
			ListTag listTag = new ListTag();
			synchronized (this.children) {
				for (StructurePiece structurePiece : this.children) {
					listTag.add(structurePiece.getTag());
				}
			}

			compoundTag.put("Children", listTag);
			return compoundTag;
		} else {
			compoundTag.putString("id", "INVALID");
			return compoundTag;
		}
	}

	protected void method_14978(int i, Random random, int j) {
		int k = i - j;
		int l = this.boundingBox.getBlockCountY() + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - this.boundingBox.maxY;
		this.boundingBox.offset(0, m, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, m, 0);
		}
	}

	protected void method_14976(Random random, int i, int j) {
		int k = j - i + 1 - this.boundingBox.getBlockCountY();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int n = l - this.boundingBox.minY;
		this.boundingBox.offset(0, n, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, n, 0);
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public BlockPos getPos() {
		return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
	}

	public boolean isInExistingChunk() {
		return this.references < this.getReferenceCountToBeInExistingChunk();
	}

	public void incrementReferences() {
		this.references++;
	}

	public int method_23676() {
		return this.references;
	}

	protected int getReferenceCountToBeInExistingChunk() {
		return 1;
	}

	public StructureFeature<?> getFeature() {
		return this.feature;
	}
}
