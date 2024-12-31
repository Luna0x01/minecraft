package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_2759 {
	private static final StructurePlacementData field_12996 = new StructurePlacementData().method_11870(true);
	private static final StructurePlacementData field_12997 = new StructurePlacementData().method_11870(true).method_11866(Blocks.AIR);
	private static final class_2759.class_1926 field_12998 = new class_2759.class_1926() {
		@Override
		public void method_10465() {
		}

		@Override
		public boolean method_11849(class_2763 arg, int i, class_2759.class_2760 arg2, BlockPos blockPos, List<StructurePiece> list, Random random) {
			if (i > 8) {
				return false;
			} else {
				BlockRotation blockRotation = arg2.field_13017.method_11874();
				class_2759.class_2760 lv = class_2759.method_13366(list, class_2759.method_11839(arg, arg2, blockPos, "base_floor", blockRotation, true));
				int j = random.nextInt(3);
				if (j == 0) {
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true));
				} else if (j == 1) {
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false));
					class_2759.method_11840(arg, class_2759.field_13000, i + 1, lv, null, list, random);
				} else if (j == 2) {
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 4, -1), "third_floor_c", blockRotation, false));
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
					class_2759.method_11840(arg, class_2759.field_13000, i + 1, lv, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> field_12999 = Lists.newArrayList(
		new Pair[]{
			new Pair<>(BlockRotation.NONE, new BlockPos(1, -1, 0)),
			new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(6, -1, 1)),
			new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)),
			new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(5, -1, 6))
		}
	);
	private static final class_2759.class_1926 field_13000 = new class_2759.class_1926() {
		@Override
		public void method_10465() {
		}

		@Override
		public boolean method_11849(class_2763 arg, int i, class_2759.class_2760 arg2, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = arg2.field_13017.method_11874();
			class_2759.class_2760 lv = class_2759.method_13366(
				list, class_2759.method_11839(arg, arg2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true)
			);
			lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
			class_2759.class_2760 lv2 = random.nextInt(3) == 0 ? lv : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
				if (k < j - 1 && random.nextBoolean()) {
					lv2 = lv;
				}
			}

			if (lv2 != null) {
				for (Pair<BlockRotation, BlockPos> pair : class_2759.field_12999) {
					if (random.nextBoolean()) {
						class_2759.class_2760 lv3 = class_2759.method_13366(
							list, class_2759.method_11839(arg, lv2, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						class_2759.method_11840(arg, class_2759.field_13001, i + 1, lv3, null, list, random);
					}
				}

				lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			} else {
				if (i != 7) {
					return class_2759.method_11840(arg, class_2759.field_13003, i + 1, lv, null, list, random);
				}

				lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			}

			return true;
		}
	};
	private static final class_2759.class_1926 field_13001 = new class_2759.class_1926() {
		public boolean field_13004;

		@Override
		public void method_10465() {
			this.field_13004 = false;
		}

		@Override
		public boolean method_11849(class_2763 arg, int i, class_2759.class_2760 arg2, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = arg2.field_13017.method_11874();
			int j = random.nextInt(4) + 1;
			class_2759.class_2760 lv = class_2759.method_13366(list, class_2759.method_11839(arg, arg2, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true));
			lv.chainLength = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, k, -4), "bridge_piece", blockRotation, true));
					k = 0;
				} else {
					if (random.nextBoolean()) {
						lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, k, -4), "bridge_steep_stairs", blockRotation, true));
					} else {
						lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, k, -8), "bridge_gentle_stairs", blockRotation, true));
					}

					k = 4;
				}
			}

			if (!this.field_13004 && random.nextInt(10 - i) == 0) {
				class_2759.method_13366(
					list, class_2759.method_11839(arg, lv, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", blockRotation, true)
				);
				this.field_13004 = true;
			} else if (!class_2759.method_11840(arg, class_2759.field_12998, i + 1, lv, new BlockPos(-3, k + 1, -11), list, random)) {
				return false;
			}

			lv = class_2759.method_13366(
				list, class_2759.method_11839(arg, lv, new BlockPos(4, k, 0), "bridge_end", blockRotation.rotate(BlockRotation.CLOCKWISE_180), true)
			);
			lv.chainLength = -1;
			return true;
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> field_13002 = Lists.newArrayList(
		new Pair[]{
			new Pair<>(BlockRotation.NONE, new BlockPos(4, -1, 0)),
			new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(12, -1, 4)),
			new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)),
			new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(8, -1, 12))
		}
	);
	private static final class_2759.class_1926 field_13003 = new class_2759.class_1926() {
		@Override
		public void method_10465() {
		}

		@Override
		public boolean method_11849(class_2763 arg, int i, class_2759.class_2760 arg2, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = arg2.field_13017.method_11874();
			class_2759.class_2760 lv = class_2759.method_13366(list, class_2759.method_11839(arg, arg2, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true));
			lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true));

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true));

				for (Pair<BlockRotation, BlockPos> pair : class_2759.field_13002) {
					if (random.nextBoolean()) {
						class_2759.class_2760 lv2 = class_2759.method_13366(
							list, class_2759.method_11839(arg, lv, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						class_2759.method_11840(arg, class_2759.field_13001, i + 1, lv2, null, list, random);
					}
				}
			}

			lv = class_2759.method_13366(list, class_2759.method_11839(arg, lv, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true));
			return true;
		}
	};

	public static void registerPieces() {
		StructurePieceManager.registerPiece(class_2759.class_2760.class, "ECP");
	}

	private static class_2759.class_2760 method_11839(
		class_2763 arg, class_2759.class_2760 arg2, BlockPos blockPos, String string, BlockRotation blockRotation, boolean bl
	) {
		class_2759.class_2760 lv = new class_2759.class_2760(arg, string, arg2.field_13018, blockRotation, bl);
		BlockPos blockPos2 = arg2.field_13016.method_11887(arg2.field_13017, blockPos, lv.field_13017, BlockPos.ORIGIN);
		lv.translate(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
		return lv;
	}

	public static void method_11837(class_2763 arg, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random) {
		field_13003.method_10465();
		field_12998.method_10465();
		field_13001.method_10465();
		field_13000.method_10465();
		class_2759.class_2760 lv = method_13366(list, new class_2759.class_2760(arg, "base_floor", blockPos, blockRotation, true));
		lv = method_13366(list, method_11839(arg, lv, new BlockPos(-1, 0, -1), "second_floor", blockRotation, false));
		lv = method_13366(list, method_11839(arg, lv, new BlockPos(-1, 4, -1), "third_floor", blockRotation, false));
		lv = method_13366(list, method_11839(arg, lv, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
		method_11840(arg, field_13000, 1, lv, null, list, random);
	}

	private static class_2759.class_2760 method_13366(List<StructurePiece> list, class_2759.class_2760 arg) {
		list.add(arg);
		return arg;
	}

	private static boolean method_11840(
		class_2763 arg, class_2759.class_1926 arg2, int i, class_2759.class_2760 arg3, BlockPos blockPos, List<StructurePiece> list, Random random
	) {
		if (i > 8) {
			return false;
		} else {
			List<StructurePiece> list2 = Lists.newArrayList();
			if (arg2.method_11849(arg, i, arg3, blockPos, list2, random)) {
				boolean bl = false;
				int j = random.nextInt();

				for (StructurePiece structurePiece : list2) {
					structurePiece.chainLength = j;
					StructurePiece structurePiece2 = StructurePiece.getOverlappingPiece(list, structurePiece.getBoundingBox());
					if (structurePiece2 != null && structurePiece2.chainLength != arg3.chainLength) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					list.addAll(list2);
					return true;
				}
			}

			return false;
		}
	}

	interface class_1926 {
		void method_10465();

		boolean method_11849(class_2763 arg, int i, class_2759.class_2760 arg2, BlockPos blockPos, List<StructurePiece> list, Random random);
	}

	public static class class_2760 extends class_2762 {
		private String field_13005;
		private BlockRotation field_13006;
		private boolean field_13007;

		public class_2760() {
		}

		public class_2760(class_2763 arg, String string, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
			super(0);
			this.field_13005 = string;
			this.field_13018 = blockPos;
			this.field_13006 = blockRotation;
			this.field_13007 = bl;
			this.method_13773(arg);
		}

		private void method_13773(class_2763 arg) {
			Structure structure = arg.method_11861(null, new Identifier("endcity/" + this.field_13005));
			StructurePlacementData structurePlacementData = (this.field_13007 ? class_2759.field_12996 : class_2759.field_12997)
				.method_11864()
				.method_11868(this.field_13006);
			this.method_11856(structure, this.field_13018, structurePlacementData);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putString("Template", this.field_13005);
			structureNbt.putString("Rot", this.field_13006.name());
			structureNbt.putBoolean("OW", this.field_13007);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_2763 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_13005 = nbtCompound.getString("Template");
			this.field_13006 = BlockRotation.valueOf(nbtCompound.getString("Rot"));
			this.field_13007 = nbtCompound.getBoolean("OW");
			this.method_13773(arg);
		}

		@Override
		protected void method_11857(String string, BlockPos blockPos, World world, Random random, BlockBox blockBox) {
			if (string.startsWith("Chest")) {
				BlockPos blockPos2 = blockPos.down();
				if (blockBox.contains(blockPos2)) {
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof ChestBlockEntity) {
						((ChestBlockEntity)blockEntity).method_11660(LootTables.END_CITY_TREASURE_CHEST, random.nextLong());
					}
				}
			} else if (string.startsWith("Sentry")) {
				ShulkerEntity shulkerEntity = new ShulkerEntity(world);
				shulkerEntity.updatePosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
				shulkerEntity.method_13234(blockPos);
				world.spawnEntity(shulkerEntity);
			} else if (string.startsWith("Elytra")) {
				ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world, blockPos, this.field_13006.rotate(Direction.SOUTH));
				itemFrameEntity.setHeldItemStack(new ItemStack(Items.ELYTRA));
				world.spawnEntity(itemFrameEntity);
			}
		}
	}
}
