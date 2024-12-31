package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SwampHutGenerator extends StructurePieceWithDimensions {
	private boolean hasWitch;
	private boolean hasCat;

	public SwampHutGenerator(Random random, int i, int j) {
		super(StructurePieceType.SWAMP_HUT, random, i, 64, j, 7, 7, 9);
	}

	public SwampHutGenerator(StructureManager structureManager, CompoundTag compoundTag) {
		super(StructurePieceType.SWAMP_HUT, compoundTag);
		this.hasWitch = compoundTag.getBoolean("Witch");
		this.hasCat = compoundTag.getBoolean("Cat");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		super.toNbt(compoundTag);
		compoundTag.putBoolean("Witch", this.hasWitch);
		compoundTag.putBoolean("Cat", this.hasCat);
	}

	@Override
	public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		if (!this.method_14839(iWorld, blockBox, 0)) {
			return false;
		} else {
			this.fillWithOutline(iWorld, blockBox, 1, 1, 1, 5, 1, 7, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 1, 4, 2, 5, 4, 7, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 2, 1, 0, 4, 1, 0, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 2, 2, 2, 3, 3, 2, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 3, 1, 3, 6, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 5, 2, 3, 5, 3, 6, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 2, 2, 7, 4, 3, 7, Blocks.field_9975.getDefaultState(), Blocks.field_9975.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 1, 0, 2, 1, 3, 2, Blocks.field_10431.getDefaultState(), Blocks.field_10431.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 5, 0, 2, 5, 3, 2, Blocks.field_10431.getDefaultState(), Blocks.field_10431.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 1, 0, 7, 1, 3, 7, Blocks.field_10431.getDefaultState(), Blocks.field_10431.getDefaultState(), false);
			this.fillWithOutline(iWorld, blockBox, 5, 0, 7, 5, 3, 7, Blocks.field_10431.getDefaultState(), Blocks.field_10431.getDefaultState(), false);
			this.addBlock(iWorld, Blocks.field_10620.getDefaultState(), 2, 3, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10620.getDefaultState(), 3, 3, 7, blockBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 3, 4, blockBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 3, 4, blockBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 3, 5, blockBox);
			this.addBlock(iWorld, Blocks.field_10138.getDefaultState(), 1, 3, 5, blockBox);
			this.addBlock(iWorld, Blocks.field_9980.getDefaultState(), 3, 2, 6, blockBox);
			this.addBlock(iWorld, Blocks.field_10593.getDefaultState(), 4, 2, 6, blockBox);
			this.addBlock(iWorld, Blocks.field_10620.getDefaultState(), 1, 2, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10620.getDefaultState(), 5, 2, 1, blockBox);
			BlockState blockState = Blocks.field_10569.getDefaultState().with(StairsBlock.FACING, Direction.field_11043);
			BlockState blockState2 = Blocks.field_10569.getDefaultState().with(StairsBlock.FACING, Direction.field_11034);
			BlockState blockState3 = Blocks.field_10569.getDefaultState().with(StairsBlock.FACING, Direction.field_11039);
			BlockState blockState4 = Blocks.field_10569.getDefaultState().with(StairsBlock.FACING, Direction.field_11035);
			this.fillWithOutline(iWorld, blockBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
			this.fillWithOutline(iWorld, blockBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, blockBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, blockBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);
			this.addBlock(iWorld, blockState.with(StairsBlock.SHAPE, StairShape.field_12709), 0, 4, 1, blockBox);
			this.addBlock(iWorld, blockState.with(StairsBlock.SHAPE, StairShape.field_12708), 6, 4, 1, blockBox);
			this.addBlock(iWorld, blockState4.with(StairsBlock.SHAPE, StairShape.field_12708), 0, 4, 8, blockBox);
			this.addBlock(iWorld, blockState4.with(StairsBlock.SHAPE, StairShape.field_12709), 6, 4, 8, blockBox);

			for (int i = 2; i <= 7; i += 5) {
				for (int j = 1; j <= 5; j += 4) {
					this.method_14936(iWorld, Blocks.field_10431.getDefaultState(), j, -1, i, blockBox);
				}
			}

			if (!this.hasWitch) {
				int k = this.applyXTransform(2, 5);
				int l = this.applyYTransform(2);
				int m = this.applyZTransform(2, 5);
				if (blockBox.contains(new BlockPos(k, l, m))) {
					this.hasWitch = true;
					WitchEntity witchEntity = EntityType.field_6145.create(iWorld.getWorld());
					witchEntity.setPersistent();
					witchEntity.refreshPositionAndAngles((double)k + 0.5, (double)l, (double)m + 0.5, 0.0F, 0.0F);
					witchEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(k, l, m)), SpawnType.field_16474, null, null);
					iWorld.spawnEntity(witchEntity);
				}
			}

			this.method_16181(iWorld, blockBox);
			return true;
		}
	}

	private void method_16181(IWorld iWorld, BlockBox blockBox) {
		if (!this.hasCat) {
			int i = this.applyXTransform(2, 5);
			int j = this.applyYTransform(2);
			int k = this.applyZTransform(2, 5);
			if (blockBox.contains(new BlockPos(i, j, k))) {
				this.hasCat = true;
				CatEntity catEntity = EntityType.field_16281.create(iWorld.getWorld());
				catEntity.setPersistent();
				catEntity.refreshPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
				catEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(i, j, k)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(catEntity);
			}
		}
	}
}
