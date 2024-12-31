package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.class_2762;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

public class class_3976 {
	private static final Identifier field_19349 = new Identifier("igloo/top");
	private static final Identifier field_19350 = new Identifier("igloo/middle");
	private static final Identifier field_19351 = new Identifier("igloo/bottom");
	private static final Map<Identifier, BlockPos> field_19352 = ImmutableMap.of(
		field_19349, new BlockPos(3, 5, 5), field_19350, new BlockPos(1, 3, 1), field_19351, new BlockPos(3, 6, 7)
	);
	private static final Map<Identifier, BlockPos> field_19353 = ImmutableMap.of(
		field_19349, new BlockPos(0, 0, 0), field_19350, new BlockPos(2, -3, 4), field_19351, new BlockPos(0, -3, -2)
	);

	public static void method_17601() {
		StructurePieceManager.registerPiece(class_3976.class_3977.class, "Iglu");
	}

	public static void method_17602(class_3998 arg, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random, class_3855 arg2) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			list.add(new class_3976.class_3977(arg, field_19351, blockPos, blockRotation, i * 3));

			for (int j = 0; j < i - 1; j++) {
				list.add(new class_3976.class_3977(arg, field_19350, blockPos, blockRotation, j * 3));
			}
		}

		list.add(new class_3976.class_3977(arg, field_19349, blockPos, blockRotation, 0));
	}

	public static class class_3977 extends class_2762 {
		private Identifier field_19354;
		private BlockRotation field_19355;

		public class_3977() {
		}

		public class_3977(class_3998 arg, Identifier identifier, BlockPos blockPos, BlockRotation blockRotation, int i) {
			super(0);
			this.field_19354 = identifier;
			BlockPos blockPos2 = (BlockPos)class_3976.field_19353.get(identifier);
			this.field_13018 = blockPos.add(blockPos2.getX(), blockPos2.getY() - i, blockPos2.getZ());
			this.field_19355 = blockRotation;
			this.method_17606(arg);
		}

		private void method_17606(class_3998 arg) {
			Structure structure = arg.method_17682(this.field_19354);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.method_11868(this.field_19355)
				.method_11867(BlockMirror.NONE)
				.method_17691((BlockPos)class_3976.field_19352.get(this.field_19354));
			this.method_11856(structure, this.field_13018, structurePlacementData);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putString("Template", this.field_19354.toString());
			structureNbt.putString("Rot", this.field_19355.name());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_19354 = new Identifier(nbtCompound.getString("Template"));
			this.field_19355 = BlockRotation.valueOf(nbtCompound.getString("Rot"));
			this.method_17606(arg);
		}

		@Override
		protected void method_11857(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).method_11660(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
				}
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.method_11868(this.field_19355)
				.method_11867(BlockMirror.NONE)
				.method_17691((BlockPos)class_3976.field_19352.get(this.field_19354));
			BlockPos blockPos = (BlockPos)class_3976.field_19353.get(this.field_19354);
			BlockPos blockPos2 = this.field_13018.add(Structure.method_11886(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, 0 - blockPos.getZ())));
			int i = iWorld.method_16372(class_3804.class_3805.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.field_13018;
			this.field_13018 = this.field_13018.add(0, i - 90 - 1, 0);
			boolean bl = super.method_58(iWorld, random, blockBox, chunkPos);
			if (this.field_19354.equals(class_3976.field_19349)) {
				BlockPos blockPos4 = this.field_13018.add(Structure.method_11886(structurePlacementData, new BlockPos(3, 0, 5)));
				BlockState blockState = iWorld.getBlockState(blockPos4.down());
				if (!blockState.isAir() && blockState.getBlock() != Blocks.LADDER) {
					iWorld.setBlockState(blockPos4, Blocks.SNOW_BLOCK.getDefaultState(), 3);
				}
			}

			this.field_13018 = blockPos3;
			return bl;
		}
	}
}
