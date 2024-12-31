package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.class_2762;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3986 {
	private static final Identifier[] field_19382 = new Identifier[]{
		new Identifier("underwater_ruin/warm_1"),
		new Identifier("underwater_ruin/warm_2"),
		new Identifier("underwater_ruin/warm_3"),
		new Identifier("underwater_ruin/warm_4"),
		new Identifier("underwater_ruin/warm_5"),
		new Identifier("underwater_ruin/warm_6"),
		new Identifier("underwater_ruin/warm_7"),
		new Identifier("underwater_ruin/warm_8")
	};
	private static final Identifier[] field_19383 = new Identifier[]{
		new Identifier("underwater_ruin/brick_1"),
		new Identifier("underwater_ruin/brick_2"),
		new Identifier("underwater_ruin/brick_3"),
		new Identifier("underwater_ruin/brick_4"),
		new Identifier("underwater_ruin/brick_5"),
		new Identifier("underwater_ruin/brick_6"),
		new Identifier("underwater_ruin/brick_7"),
		new Identifier("underwater_ruin/brick_8")
	};
	private static final Identifier[] field_19384 = new Identifier[]{
		new Identifier("underwater_ruin/cracked_1"),
		new Identifier("underwater_ruin/cracked_2"),
		new Identifier("underwater_ruin/cracked_3"),
		new Identifier("underwater_ruin/cracked_4"),
		new Identifier("underwater_ruin/cracked_5"),
		new Identifier("underwater_ruin/cracked_6"),
		new Identifier("underwater_ruin/cracked_7"),
		new Identifier("underwater_ruin/cracked_8")
	};
	private static final Identifier[] field_19385 = new Identifier[]{
		new Identifier("underwater_ruin/mossy_1"),
		new Identifier("underwater_ruin/mossy_2"),
		new Identifier("underwater_ruin/mossy_3"),
		new Identifier("underwater_ruin/mossy_4"),
		new Identifier("underwater_ruin/mossy_5"),
		new Identifier("underwater_ruin/mossy_6"),
		new Identifier("underwater_ruin/mossy_7"),
		new Identifier("underwater_ruin/mossy_8")
	};
	private static final Identifier[] field_19386 = new Identifier[]{
		new Identifier("underwater_ruin/big_brick_1"),
		new Identifier("underwater_ruin/big_brick_2"),
		new Identifier("underwater_ruin/big_brick_3"),
		new Identifier("underwater_ruin/big_brick_8")
	};
	private static final Identifier[] field_19387 = new Identifier[]{
		new Identifier("underwater_ruin/big_mossy_1"),
		new Identifier("underwater_ruin/big_mossy_2"),
		new Identifier("underwater_ruin/big_mossy_3"),
		new Identifier("underwater_ruin/big_mossy_8")
	};
	private static final Identifier[] field_19388 = new Identifier[]{
		new Identifier("underwater_ruin/big_cracked_1"),
		new Identifier("underwater_ruin/big_cracked_2"),
		new Identifier("underwater_ruin/big_cracked_3"),
		new Identifier("underwater_ruin/big_cracked_8")
	};
	private static final Identifier[] field_19389 = new Identifier[]{
		new Identifier("underwater_ruin/big_warm_4"),
		new Identifier("underwater_ruin/big_warm_5"),
		new Identifier("underwater_ruin/big_warm_6"),
		new Identifier("underwater_ruin/big_warm_7")
	};

	public static void method_17628() {
		StructurePieceManager.registerPiece(class_3986.class_3987.class, "ORP");
	}

	private static Identifier method_17632(Random random) {
		return field_19382[random.nextInt(field_19382.length)];
	}

	private static Identifier method_17634(Random random) {
		return field_19389[random.nextInt(field_19389.length)];
	}

	public static void method_17629(class_3998 arg, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random, class_3874 arg2) {
		boolean bl = random.nextFloat() <= arg2.field_19224;
		float f = bl ? 0.9F : 0.8F;
		method_17630(arg, blockPos, blockRotation, list, random, arg2, bl, f);
		if (bl && random.nextFloat() <= arg2.field_19225) {
			method_17631(arg, random, blockRotation, blockPos, arg2, list);
		}
	}

	private static void method_17631(class_3998 arg, Random random, BlockRotation blockRotation, BlockPos blockPos, class_3874 arg2, List<StructurePiece> list) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		BlockPos blockPos2 = Structure.method_11889(new BlockPos(15, 0, 15), BlockMirror.NONE, blockRotation, new BlockPos(0, 0, 0)).add(i, 0, j);
		BlockBox blockBox = BlockBox.create(i, 0, j, blockPos2.getX(), 0, blockPos2.getZ());
		BlockPos blockPos3 = new BlockPos(Math.min(i, blockPos2.getX()), 0, Math.min(j, blockPos2.getZ()));
		List<BlockPos> list2 = method_17633(random, blockPos3.getX(), blockPos3.getZ());
		int k = MathHelper.nextInt(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list2.isEmpty()) {
				int m = random.nextInt(list2.size());
				BlockPos blockPos4 = (BlockPos)list2.remove(m);
				int n = blockPos4.getX();
				int o = blockPos4.getZ();
				BlockRotation blockRotation2 = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
				BlockPos blockPos5 = Structure.method_11889(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation2, new BlockPos(0, 0, 0)).add(n, 0, o);
				BlockBox blockBox2 = BlockBox.create(n, 0, o, blockPos5.getX(), 0, blockPos5.getZ());
				if (!blockBox2.intersects(blockBox)) {
					method_17630(arg, blockPos4, blockRotation2, list, random, arg2, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> method_17633(Random random, int i, int j) {
		List<BlockPos> list = Lists.newArrayList();
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 6)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 3, 8)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		return list;
	}

	private static void method_17630(
		class_3998 arg, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random, class_3874 arg2, boolean bl, float f
	) {
		if (arg2.field_19223 == class_3983.class_3985.WARM) {
			Identifier identifier = bl ? method_17634(random) : method_17632(random);
			list.add(new class_3986.class_3987(arg, identifier, blockPos, blockRotation, f, arg2.field_19223, bl));
		} else if (arg2.field_19223 == class_3983.class_3985.COLD) {
			Identifier[] identifiers = bl ? field_19386 : field_19383;
			Identifier[] identifiers2 = bl ? field_19388 : field_19384;
			Identifier[] identifiers3 = bl ? field_19387 : field_19385;
			int i = random.nextInt(identifiers.length);
			list.add(new class_3986.class_3987(arg, identifiers[i], blockPos, blockRotation, f, arg2.field_19223, bl));
			list.add(new class_3986.class_3987(arg, identifiers2[i], blockPos, blockRotation, 0.7F, arg2.field_19223, bl));
			list.add(new class_3986.class_3987(arg, identifiers3[i], blockPos, blockRotation, 0.5F, arg2.field_19223, bl));
		}
	}

	public static class class_3987 extends class_2762 {
		private class_3983.class_3985 field_19390;
		private float field_19391;
		private Identifier field_19392;
		private BlockRotation field_19393;
		private boolean field_19394;

		public class_3987() {
		}

		public class_3987(class_3998 arg, Identifier identifier, BlockPos blockPos, BlockRotation blockRotation, float f, class_3983.class_3985 arg2, boolean bl) {
			super(0);
			this.field_19392 = identifier;
			this.field_13018 = blockPos;
			this.field_19393 = blockRotation;
			this.field_19391 = f;
			this.field_19390 = arg2;
			this.field_19394 = bl;
			this.method_17635(arg);
		}

		private void method_17635(class_3998 arg) {
			Structure structure = arg.method_17682(this.field_19392);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.method_11868(this.field_19393)
				.method_11867(BlockMirror.NONE)
				.method_11866(Blocks.AIR);
			this.method_11856(structure, this.field_13018, structurePlacementData);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putString("Template", this.field_19392.toString());
			structureNbt.putString("Rot", this.field_19393.name());
			structureNbt.putFloat("Integrity", this.field_19391);
			structureNbt.putString("BiomeType", this.field_19390.toString());
			structureNbt.putBoolean("IsLarge", this.field_19394);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_19392 = new Identifier(nbtCompound.getString("Template"));
			this.field_19393 = BlockRotation.valueOf(nbtCompound.getString("Rot"));
			this.field_19391 = nbtCompound.getFloat("Integrity");
			this.field_19390 = class_3983.class_3985.valueOf(nbtCompound.getString("BiomeType"));
			this.field_19394 = nbtCompound.getBoolean("IsLarge");
			this.method_17635(arg);
		}

		@Override
		protected void method_11857(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(
					blockPos, Blocks.CHEST.getDefaultState().withProperty(ChestBlock.WATERLOGGED, Boolean.valueOf(iWorld.getFluidState(blockPos).matches(FluidTags.WATER))), 2
				);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity)
						.method_11660(this.field_19394 ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
				}
			} else if ("drowned".equals(string)) {
				DrownedEntity drownedEntity = new DrownedEntity(iWorld.method_16348());
				drownedEntity.setPersistent();
				drownedEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
				drownedEntity.initialize(iWorld.method_8482(blockPos), null, null);
				iWorld.method_3686(drownedEntity);
				if (blockPos.getY() > iWorld.method_8483()) {
					iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
				} else {
					iWorld.setBlockState(blockPos, Blocks.WATER.getDefaultState(), 2);
				}
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.field_13017.method_13385(this.field_19391);
			int i = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, this.field_13018.getX(), this.field_13018.getZ());
			this.field_13018 = new BlockPos(this.field_13018.getX(), i, this.field_13018.getZ());
			BlockPos blockPos = Structure.method_11889(
					new BlockPos(this.field_13016.getSize().getX() - 1, 0, this.field_13016.getSize().getZ() - 1), BlockMirror.NONE, this.field_19393, new BlockPos(0, 0, 0)
				)
				.add(this.field_13018);
			this.field_13018 = new BlockPos(this.field_13018.getX(), this.method_17636(this.field_13018, iWorld, blockPos), this.field_13018.getZ());
			return super.method_58(iWorld, random, blockBox, chunkPos);
		}

		private int method_17636(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
			int i = blockPos.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
				int m = blockPos3.getX();
				int n = blockPos3.getZ();
				int o = blockPos.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = blockView.getBlockState(mutable);

				for (FluidState fluidState = blockView.getFluidState(mutable);
					(blockState.isAir() || fluidState.matches(FluidTags.WATER) || blockState.getBlock().isIn(BlockTags.ICE)) && o > 1;
					fluidState = blockView.getFluidState(mutable)
				) {
					mutable.setPosition(m, --o, n);
					blockState = blockView.getBlockState(mutable);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(blockPos.getX() - blockPos2.getX());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
