package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureStart<C extends FeatureConfig> implements StructurePiecesHolder {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String INVALID = "INVALID";
	public static final StructureStart<?> DEFAULT = new StructureStart<MineshaftFeatureConfig>(null, new ChunkPos(0, 0), 0, 0L) {
		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			MineshaftFeatureConfig mineshaftFeatureConfig,
			HeightLimitView heightLimitView
		) {
		}

		@Override
		public boolean hasChildren() {
			return false;
		}
	};
	private final StructureFeature<C> feature;
	protected final List<StructurePiece> children = Lists.newArrayList();
	private final ChunkPos pos;
	private int references;
	protected final ChunkRandom random;
	@Nullable
	private BlockBox boundingBox;

	public StructureStart(StructureFeature<C> feature, ChunkPos pos, int references, long seed) {
		this.feature = feature;
		this.pos = pos;
		this.references = references;
		this.random = new ChunkRandom();
		this.random.setCarverSeed(seed, pos.x, pos.z);
	}

	public abstract void init(
		DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, ChunkPos pos, Biome biome, C config, HeightLimitView world
	);

	public final BlockBox setBoundingBoxFromChildren() {
		if (this.boundingBox == null) {
			this.boundingBox = this.calculateBoundingBox();
		}

		return this.boundingBox;
	}

	protected BlockBox calculateBoundingBox() {
		synchronized (this.children) {
			return (BlockBox)BlockBox.encompass(this.children.stream().map(StructurePiece::getBoundingBox)::iterator)
				.orElseThrow(() -> new IllegalStateException("Unable to calculate boundingbox without pieces"));
		}
	}

	public List<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(
		StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos
	) {
		synchronized (this.children) {
			if (!this.children.isEmpty()) {
				BlockBox blockBox = ((StructurePiece)this.children.get(0)).boundingBox;
				BlockPos blockPos = blockBox.getCenter();
				BlockPos blockPos2 = new BlockPos(blockPos.getX(), blockBox.getMinY(), blockPos.getZ());
				Iterator<StructurePiece> iterator = this.children.iterator();

				while (iterator.hasNext()) {
					StructurePiece structurePiece = (StructurePiece)iterator.next();
					if (structurePiece.getBoundingBox().intersects(box)
						&& !structurePiece.generate(world, structureAccessor, chunkGenerator, random, box, chunkPos, blockPos2)) {
						iterator.remove();
					}
				}
			}
		}
	}

	public NbtCompound toNbt(ServerWorld world, ChunkPos chunkPos) {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.hasChildren()) {
			nbtCompound.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
			nbtCompound.putInt("ChunkX", chunkPos.x);
			nbtCompound.putInt("ChunkZ", chunkPos.z);
			nbtCompound.putInt("references", this.references);
			NbtList nbtList = new NbtList();
			synchronized (this.children) {
				for (StructurePiece structurePiece : this.children) {
					nbtList.add(structurePiece.toNbt(world));
				}
			}

			nbtCompound.put("Children", nbtList);
			return nbtCompound;
		} else {
			nbtCompound.putString("id", "INVALID");
			return nbtCompound;
		}
	}

	protected void randomUpwardTranslation(int seaLevel, int i, Random random, int j) {
		int k = seaLevel - j;
		BlockBox blockBox = this.setBoundingBoxFromChildren();
		int l = blockBox.getBlockCountY() + i + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - blockBox.getMaxY();
		this.translateUpward(m);
	}

	protected void randomUpwardTranslation(Random random, int minY, int maxY) {
		BlockBox blockBox = this.setBoundingBoxFromChildren();
		int i = maxY - minY + 1 - blockBox.getBlockCountY();
		int j;
		if (i > 1) {
			j = minY + random.nextInt(i);
		} else {
			j = minY;
		}

		int l = j - blockBox.getMinY();
		this.translateUpward(l);
	}

	protected void translateUpward(int amount) {
		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, amount, 0);
		}

		this.resetBoundingBox();
	}

	private void resetBoundingBox() {
		this.boundingBox = null;
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return new BlockPos(this.pos.getStartX(), 0, this.pos.getStartZ());
	}

	public boolean isInExistingChunk() {
		return this.references < this.getReferenceCountToBeInExistingChunk();
	}

	public void incrementReferences() {
		this.references++;
	}

	public int getReferences() {
		return this.references;
	}

	protected int getReferenceCountToBeInExistingChunk() {
		return 1;
	}

	public StructureFeature<?> getFeature() {
		return this.feature;
	}

	@Override
	public void addPiece(StructurePiece piece) {
		this.children.add(piece);
		this.resetBoundingBox();
	}

	@Nullable
	@Override
	public StructurePiece getIntersecting(BlockBox box) {
		return getIntersecting(this.children, box);
	}

	public void clearChildren() {
		this.children.clear();
		this.resetBoundingBox();
	}

	public boolean hasNoChildren() {
		return this.children.isEmpty();
	}

	@Nullable
	public static StructurePiece getIntersecting(List<StructurePiece> pieces, BlockBox box) {
		for (StructurePiece structurePiece : pieces) {
			if (structurePiece.getBoundingBox().intersects(box)) {
				return structurePiece;
			}
		}

		return null;
	}

	protected boolean contains(BlockPos pos) {
		for (StructurePiece structurePiece : this.children) {
			if (structurePiece.getBoundingBox().contains(pos)) {
				return true;
			}
		}

		return false;
	}
}
