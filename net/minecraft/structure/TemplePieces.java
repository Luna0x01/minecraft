package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TemplePieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(TemplePieces.DesertPyramid.class, "TeDP");
		StructurePieceManager.registerPiece(TemplePieces.JunglePyramid.class, "TeJP");
		StructurePieceManager.registerPiece(TemplePieces.SwampHut.class, "TeSH");
	}

	abstract static class AbstractPiece extends StructurePiece {
		protected int width;
		protected int height;
		protected int depth;
		protected int hPos = -1;

		public AbstractPiece() {
		}

		protected AbstractPiece(Random random, int i, int j, int k, int l, int m, int n) {
			super(0);
			this.width = l;
			this.height = m;
			this.depth = n;
			this.facing = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
			switch (this.facing) {
				case NORTH:
				case SOUTH:
					this.boundingBox = new BlockBox(i, j, k, i + l - 1, j + m - 1, k + n - 1);
					break;
				default:
					this.boundingBox = new BlockBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putInt("Width", this.width);
			structureNbt.putInt("Height", this.height);
			structureNbt.putInt("Depth", this.depth);
			structureNbt.putInt("HPos", this.hPos);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			this.width = structureNbt.getInt("Width");
			this.height = structureNbt.getInt("Height");
			this.depth = structureNbt.getInt("Depth");
			this.hPos = structureNbt.getInt("HPos");
		}

		protected boolean adjustToAverageHeight(World world, BlockBox boundingBox, int deltaY) {
			if (this.hPos >= 0) {
				return true;
			} else {
				int i = 0;
				int j = 0;
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++) {
					for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; l++) {
						mutable.setPosition(l, 64, k);
						if (boundingBox.contains(mutable)) {
							i += Math.max(world.getTopPosition(mutable).getY(), world.dimension.getAverageYLevel());
							j++;
						}
					}
				}

				if (j == 0) {
					return false;
				} else {
					this.hPos = i / j;
					this.boundingBox.move(0, this.hPos - this.boundingBox.minY + deltaY, 0);
					return true;
				}
			}
		}
	}

	public static class DesertPyramid extends TemplePieces.AbstractPiece {
		private boolean[] chestsPlaced = new boolean[4];
		private static final List<WeightedRandomChestContent> CHEST_LOOT_TABLE = Lists.newArrayList(
			new WeightedRandomChestContent[]{
				new WeightedRandomChestContent(Items.DIAMOND, 0, 1, 3, 3),
				new WeightedRandomChestContent(Items.IRON_INGOT, 0, 1, 5, 10),
				new WeightedRandomChestContent(Items.GOLD_INGOT, 0, 2, 7, 15),
				new WeightedRandomChestContent(Items.EMERALD, 0, 1, 3, 2),
				new WeightedRandomChestContent(Items.BONE, 0, 4, 6, 20),
				new WeightedRandomChestContent(Items.ROTTEN_FLESH, 0, 3, 7, 16),
				new WeightedRandomChestContent(Items.SADDLE, 0, 1, 1, 3),
				new WeightedRandomChestContent(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
			}
		);

		public DesertPyramid() {
		}

		public DesertPyramid(Random random, int i, int j) {
			super(random, i, 64, j, 21, 15, 21);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("hasPlacedChest0", this.chestsPlaced[0]);
			structureNbt.putBoolean("hasPlacedChest1", this.chestsPlaced[1]);
			structureNbt.putBoolean("hasPlacedChest2", this.chestsPlaced[2]);
			structureNbt.putBoolean("hasPlacedChest3", this.chestsPlaced[3]);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.chestsPlaced[0] = structureNbt.getBoolean("hasPlacedChest0");
			this.chestsPlaced[1] = structureNbt.getBoolean("hasPlacedChest1");
			this.chestsPlaced[2] = structureNbt.getBoolean("hasPlacedChest2");
			this.chestsPlaced[3] = structureNbt.getBoolean("hasPlacedChest3");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.fillWithOutline(
				world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);

			for (int i = 1; i <= 9; i++) {
				this.fillWithOutline(
					world, boundingBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
				);
				this.fillWithOutline(
					world, boundingBox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false
				);
			}

			for (int j = 0; j < this.width; j++) {
				for (int k = 0; k < this.depth; k++) {
					int l = -5;
					this.fillAirAndLiquidsDownwards(world, Blocks.SANDSTONE.getDefaultState(), j, l, k, boundingBox);
				}
			}

			int m = this.getData(Blocks.SANDSTONE_STAIRS, 3);
			int n = this.getData(Blocks.SANDSTONE_STAIRS, 2);
			int o = this.getData(Blocks.SANDSTONE_STAIRS, 0);
			int p = this.getData(Blocks.SANDSTONE_STAIRS, 1);
			int q = ~DyeColor.ORANGE.getSwappedId() & 15;
			int r = ~DyeColor.BLUE.getSwappedId() & 15;
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), 2, 10, 0, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(n), 2, 10, 4, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(o), 0, 10, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(p), 4, 10, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), this.width - 3, 10, 0, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(n), this.width - 3, 10, 4, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(o), this.width - 5, 10, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(p), this.width - 1, 10, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 1, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 1, 1, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				1,
				8,
				8,
				3,
				8,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				12,
				1,
				8,
				12,
				3,
				8,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				1,
				12,
				8,
				3,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				12,
				1,
				12,
				12,
				3,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(world, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				5,
				5,
				9,
				5,
				7,
				11,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				this.width - 6,
				5,
				9,
				this.width - 6,
				7,
				11,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 5, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 6, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), 2, 4, 5, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), 2, 3, 4, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), this.width - 3, 4, 5, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(m), this.width - 3, 3, 4, boundingBox);
			this.fillWithOutline(world, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getDefaultState(), 1, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getDefaultState(), this.width - 2, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.SANDSTONE.getId()), 1, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.SANDSTONE.getId()), this.width - 2, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(p), 2, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE_STAIRS.stateFromData(o), this.width - 3, 1, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

			for (int s = 5; s <= 17; s += 2) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 4, 1, s, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 4, 2, s, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), this.width - 5, 1, s, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), this.width - 5, 2, s, boundingBox);
			}

			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 10, 0, 7, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 10, 0, 8, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 9, 0, 9, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 11, 0, 9, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 8, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 12, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 7, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 13, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 9, 0, 11, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 11, 0, 11, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 10, 0, 12, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 10, 0, 13, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(r), 10, 0, 10, boundingBox);

			for (int t = 0; t <= this.width - 1; t += this.width - 1) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 2, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 2, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 3, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 3, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 4, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), t, 4, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 4, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 5, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 5, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 5, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 6, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), t, 6, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 6, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 7, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 7, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), t, 7, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 8, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 8, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), t, 8, 3, boundingBox);
			}

			for (int u = 2; u <= this.width - 3; u += this.width - 3 - 2) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u - 1, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u + 1, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u - 1, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u + 1, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u - 1, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), u, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u + 1, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u - 1, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u + 1, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u - 1, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), u, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u + 1, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u - 1, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), u + 1, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u - 1, 8, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u, 8, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), u + 1, 8, 0, boundingBox);
			}

			this.fillWithOutline(
				world,
				boundingBox,
				8,
				4,
				0,
				12,
				6,
				0,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, 6, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, 6, 0, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 9, 5, 0, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, 5, 0, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(q), 11, 5, 0, boundingBox);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-14,
				8,
				12,
				-11,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-10,
				8,
				12,
				-10,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-9,
				8,
				12,
				-9,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(world, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 7, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 7, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 13, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 13, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -11, 8, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -10, 8, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, -10, 7, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, -11, 7, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -11, 12, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -10, 12, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, -10, 13, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, -11, 13, boundingBox);

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (!this.chestsPlaced[direction.getHorizontal()]) {
					int v = direction.getOffsetX() * 2;
					int w = direction.getOffsetZ() * 2;
					this.chestsPlaced[direction.getHorizontal()] = this.placeChest(
						world,
						boundingBox,
						random,
						10 + v,
						-11,
						10 + w,
						WeightedRandomChestContent.combineLootTables(CHEST_LOOT_TABLE, Items.ENCHANTED_BOOK.getLootTable(random)),
						2 + random.nextInt(5)
					);
				}
			}

			return true;
		}
	}

	public static class JunglePyramid extends TemplePieces.AbstractPiece {
		private boolean mainChest;
		private boolean hiddenChest;
		private boolean placedTrap1;
		private boolean placedTrap2;
		private static final List<WeightedRandomChestContent> CHEST_LOOT_TABLE = Lists.newArrayList(
			new WeightedRandomChestContent[]{
				new WeightedRandomChestContent(Items.DIAMOND, 0, 1, 3, 3),
				new WeightedRandomChestContent(Items.IRON_INGOT, 0, 1, 5, 10),
				new WeightedRandomChestContent(Items.GOLD_INGOT, 0, 2, 7, 15),
				new WeightedRandomChestContent(Items.EMERALD, 0, 1, 3, 2),
				new WeightedRandomChestContent(Items.BONE, 0, 4, 6, 20),
				new WeightedRandomChestContent(Items.ROTTEN_FLESH, 0, 3, 7, 16),
				new WeightedRandomChestContent(Items.SADDLE, 0, 1, 1, 3),
				new WeightedRandomChestContent(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
			}
		);
		private static final List<WeightedRandomChestContent> DISPENSER_LOOT_TABLE = Lists.newArrayList(
			new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.ARROW, 0, 2, 7, 30)}
		);
		private static TemplePieces.JunglePyramid.Randomizer randomizer = new TemplePieces.JunglePyramid.Randomizer();

		public JunglePyramid() {
		}

		public JunglePyramid(Random random, int i, int j) {
			super(random, i, 64, j, 12, 10, 15);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("placedMainChest", this.mainChest);
			structureNbt.putBoolean("placedHiddenChest", this.hiddenChest);
			structureNbt.putBoolean("placedTrap1", this.placedTrap1);
			structureNbt.putBoolean("placedTrap2", this.placedTrap2);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.mainChest = structureNbt.getBoolean("placedMainChest");
			this.hiddenChest = structureNbt.getBoolean("placedHiddenChest");
			this.placedTrap1 = structureNbt.getBoolean("placedTrap1");
			this.placedTrap2 = structureNbt.getBoolean("placedTrap2");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (!this.adjustToAverageHeight(world, boundingBox, 0)) {
				return false;
			} else {
				int i = this.getData(Blocks.STONE_STAIRS, 3);
				int j = this.getData(Blocks.STONE_STAIRS, 2);
				int k = this.getData(Blocks.STONE_STAIRS, 0);
				int l = this.getData(Blocks.STONE_STAIRS, 1);
				this.fillRandomized(world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 2, 9, 2, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 12, 9, 2, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 3, 2, 2, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 1, 3, 9, 2, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 1, 10, 6, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 13, 10, 6, 13, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 2, 1, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 10, 3, 2, 10, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 3, 2, 9, 3, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 6, 2, 9, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 3, 7, 3, 8, 7, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 8, 4, 7, 8, 10, false, random, randomizer);
				this.setAir(world, boundingBox, 3, 1, 3, 8, 2, 11);
				this.setAir(world, boundingBox, 4, 3, 6, 7, 3, 9);
				this.setAir(world, boundingBox, 2, 4, 2, 9, 5, 12);
				this.setAir(world, boundingBox, 4, 6, 5, 7, 6, 9);
				this.setAir(world, boundingBox, 5, 7, 6, 6, 7, 8);
				this.setAir(world, boundingBox, 5, 1, 2, 6, 2, 2);
				this.setAir(world, boundingBox, 5, 2, 12, 6, 2, 12);
				this.setAir(world, boundingBox, 5, 5, 1, 6, 5, 1);
				this.setAir(world, boundingBox, 5, 5, 13, 6, 5, 13);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 5, 5, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, 5, 5, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 5, 9, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, 5, 9, boundingBox);

				for (int m = 0; m <= 14; m += 14) {
					this.fillRandomized(world, boundingBox, 2, 4, m, 2, 5, m, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 4, 4, m, 4, 5, m, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 7, 4, m, 7, 5, m, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 9, 4, m, 9, 5, m, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 5, 6, 0, 6, 6, 0, false, random, randomizer);

				for (int n = 0; n <= 11; n += 11) {
					for (int o = 2; o <= 12; o += 2) {
						this.fillRandomized(world, boundingBox, n, 4, o, n, 5, o, false, random, randomizer);
					}

					this.fillRandomized(world, boundingBox, n, 6, 5, n, 6, 5, false, random, randomizer);
					this.fillRandomized(world, boundingBox, n, 6, 9, n, 6, 9, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 2, 7, 2, 2, 9, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 7, 2, 9, 9, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 7, 12, 2, 9, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 7, 12, 9, 9, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 9, 4, 4, 9, 4, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 9, 4, 7, 9, 4, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 9, 10, 4, 9, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 9, 10, 7, 9, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 5, 9, 7, 6, 9, 7, false, random, randomizer);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 5, 9, 6, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 6, 9, 6, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(j), 5, 9, 8, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(j), 6, 9, 8, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 4, 0, 0, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 5, 0, 0, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 6, 0, 0, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 7, 0, 0, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 4, 1, 8, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 4, 2, 9, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 4, 3, 10, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 7, 1, 8, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 7, 2, 9, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 7, 3, 10, boundingBox);
				this.fillRandomized(world, boundingBox, 4, 1, 9, 4, 1, 9, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 1, 9, 7, 1, 9, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 1, 10, 7, 2, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 5, 4, 5, 6, 4, 5, false, random, randomizer);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(k), 4, 4, 5, boundingBox);
				this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(l), 7, 4, 5, boundingBox);

				for (int p = 0; p < 4; p++) {
					this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(j), 5, 0 - p, 6 + p, boundingBox);
					this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(j), 6, 0 - p, 6 + p, boundingBox);
					this.setAir(world, boundingBox, 5, 0 - p, 7 + p, 6, 0 - p, 9 + p);
				}

				this.setAir(world, boundingBox, 1, -3, 12, 10, -1, 13);
				this.setAir(world, boundingBox, 1, -3, 1, 3, -1, 13);
				this.setAir(world, boundingBox, 1, -3, 1, 9, -1, 5);

				for (int q = 1; q <= 13; q += 2) {
					this.fillRandomized(world, boundingBox, 1, -3, q, 1, -2, q, false, random, randomizer);
				}

				for (int r = 2; r <= 12; r += 2) {
					this.fillRandomized(world, boundingBox, 1, -1, r, 3, -1, r, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 2, -2, 1, 5, -2, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, -2, 1, 9, -2, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 6, -3, 1, 6, -3, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 6, -1, 1, 6, -1, 1, false, random, randomizer);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.stateFromData(this.getData(Blocks.TRIPWIRE_HOOK, Direction.EAST.getHorizontal())).with(TripwireHookBlock.ATTACHED, true),
					1,
					-3,
					8,
					boundingBox
				);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.stateFromData(this.getData(Blocks.TRIPWIRE_HOOK, Direction.WEST.getHorizontal())).with(TripwireHookBlock.ATTACHED, true),
					4,
					-3,
					8,
					boundingBox
				);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 2, -3, 8, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 3, -3, 8, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 7, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 3, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 4, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, boundingBox);
				if (!this.placedTrap1) {
					this.placedTrap1 = this.placeDispenser(world, boundingBox, random, 3, -2, 1, Direction.NORTH.getId(), DISPENSER_LOOT_TABLE, 2);
				}

				this.setBlockState(world, Blocks.VINE.stateFromData(15), 3, -2, 2, boundingBox);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.stateFromData(this.getData(Blocks.TRIPWIRE_HOOK, Direction.NORTH.getHorizontal())).with(TripwireHookBlock.ATTACHED, true),
					7,
					-3,
					1,
					boundingBox
				);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.stateFromData(this.getData(Blocks.TRIPWIRE_HOOK, Direction.SOUTH.getHorizontal())).with(TripwireHookBlock.ATTACHED, true),
					7,
					-3,
					5,
					boundingBox
				);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 3, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -2, 4, boundingBox);
				if (!this.placedTrap2) {
					this.placedTrap2 = this.placeDispenser(world, boundingBox, random, 9, -2, 3, Direction.WEST.getId(), DISPENSER_LOOT_TABLE, 2);
				}

				this.setBlockState(world, Blocks.VINE.stateFromData(15), 8, -1, 3, boundingBox);
				this.setBlockState(world, Blocks.VINE.stateFromData(15), 8, -2, 3, boundingBox);
				if (!this.mainChest) {
					this.mainChest = this.placeChest(
						world,
						boundingBox,
						random,
						8,
						-3,
						3,
						WeightedRandomChestContent.combineLootTables(CHEST_LOOT_TABLE, Items.ENCHANTED_BOOK.getLootTable(random)),
						2 + random.nextInt(5)
					);
				}

				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, boundingBox);
				this.fillRandomized(world, boundingBox, 9, -1, 1, 9, -1, 5, false, random, randomizer);
				this.setAir(world, boundingBox, 8, -3, 8, 10, -1, 10);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 8, -2, 11, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 9, -2, 11, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 10, -2, 11, boundingBox);
				this.setBlockState(
					world,
					Blocks.LEVER.stateFromData(LeverBlock.getDataFromDirection(Direction.getById(this.getData(Blocks.LEVER, Direction.NORTH.getId())))),
					8,
					-2,
					12,
					boundingBox
				);
				this.setBlockState(
					world,
					Blocks.LEVER.stateFromData(LeverBlock.getDataFromDirection(Direction.getById(this.getData(Blocks.LEVER, Direction.NORTH.getId())))),
					9,
					-2,
					12,
					boundingBox
				);
				this.setBlockState(
					world,
					Blocks.LEVER.stateFromData(LeverBlock.getDataFromDirection(Direction.getById(this.getData(Blocks.LEVER, Direction.NORTH.getId())))),
					10,
					-2,
					12,
					boundingBox
				);
				this.fillRandomized(world, boundingBox, 8, -3, 8, 8, -3, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 10, -3, 8, 10, -3, 10, false, random, randomizer);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 9, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 10, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.stateFromData(Direction.UP.getId()), 9, -2, 8, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.stateFromData(this.getData(Blocks.STICKY_PISTON, Direction.WEST.getId())), 10, -2, 8, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.stateFromData(this.getData(Blocks.STICKY_PISTON, Direction.WEST.getId())), 10, -1, 8, boundingBox);
				this.setBlockState(
					world, Blocks.UNPOWERED_REPEATER.stateFromData(this.getData(Blocks.UNPOWERED_REPEATER, Direction.NORTH.getHorizontal())), 10, -2, 10, boundingBox
				);
				if (!this.hiddenChest) {
					this.hiddenChest = this.placeChest(
						world,
						boundingBox,
						random,
						9,
						-3,
						10,
						WeightedRandomChestContent.combineLootTables(CHEST_LOOT_TABLE, Items.ENCHANTED_BOOK.getLootTable(random)),
						2 + random.nextInt(5)
					);
				}

				return true;
			}
		}

		static class Randomizer extends StructurePiece.BlockRandomizer {
			private Randomizer() {
			}

			@Override
			public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
				if (random.nextFloat() < 0.4F) {
					this.block = Blocks.COBBLESTONE.getDefaultState();
				} else {
					this.block = Blocks.MOSSY_COBBLESTONE.getDefaultState();
				}
			}
		}
	}

	public static class SwampHut extends TemplePieces.AbstractPiece {
		private boolean witch;

		public SwampHut() {
		}

		public SwampHut(Random random, int i, int j) {
			super(random, i, 64, j, 7, 7, 9);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Witch", this.witch);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.witch = structureNbt.getBoolean("Witch");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (!this.adjustToAverageHeight(world, boundingBox, 0)) {
				return false;
			} else {
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					1,
					1,
					5,
					1,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					4,
					2,
					5,
					4,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					1,
					0,
					4,
					1,
					0,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					2,
					2,
					3,
					3,
					2,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					2,
					3,
					1,
					3,
					6,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					5,
					2,
					3,
					5,
					3,
					6,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					2,
					7,
					4,
					3,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(world, boundingBox, 1, 0, 2, 1, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 0, 2, 5, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 1, 0, 7, 1, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 0, 7, 5, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 3, 4, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 3, 4, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 3, 5, boundingBox);
				this.setBlockState(
					world, Blocks.FLOWER_POT.getDefaultState().with(FlowerPotBlock.CONTENTS, FlowerPotBlock.PottablePlantType.RED_MUSHROOM), 1, 3, 5, boundingBox
				);
				this.setBlockState(world, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, boundingBox);
				this.setBlockState(world, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, boundingBox);
				int i = this.getData(Blocks.WOODEN_STAIRS, 3);
				int j = this.getData(Blocks.WOODEN_STAIRS, 1);
				int k = this.getData(Blocks.WOODEN_STAIRS, 0);
				int l = this.getData(Blocks.WOODEN_STAIRS, 2);
				this.fillWithOutline(world, boundingBox, 0, 4, 1, 6, 4, 1, Blocks.SPRUCE_STAIRS.stateFromData(i), Blocks.SPRUCE_STAIRS.stateFromData(i), false);
				this.fillWithOutline(world, boundingBox, 0, 4, 2, 0, 4, 7, Blocks.SPRUCE_STAIRS.stateFromData(k), Blocks.SPRUCE_STAIRS.stateFromData(k), false);
				this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 7, Blocks.SPRUCE_STAIRS.stateFromData(j), Blocks.SPRUCE_STAIRS.stateFromData(j), false);
				this.fillWithOutline(world, boundingBox, 0, 4, 8, 6, 4, 8, Blocks.SPRUCE_STAIRS.stateFromData(l), Blocks.SPRUCE_STAIRS.stateFromData(l), false);

				for (int m = 2; m <= 7; m += 5) {
					for (int n = 1; n <= 5; n += 4) {
						this.fillAirAndLiquidsDownwards(world, Blocks.LOG.getDefaultState(), n, -1, m, boundingBox);
					}
				}

				if (!this.witch) {
					int o = this.applyXTransform(2, 5);
					int p = this.applyYTransform(2);
					int q = this.applyZTransform(2, 5);
					if (boundingBox.contains(new BlockPos(o, p, q))) {
						this.witch = true;
						WitchEntity witchEntity = new WitchEntity(world);
						witchEntity.refreshPositionAndAngles((double)o + 0.5, (double)p, (double)q + 0.5, 0.0F, 0.0F);
						witchEntity.initialize(world.getLocalDifficulty(new BlockPos(o, p, q)), null);
						world.spawnEntity(witchEntity);
					}
				}

				return true;
			}
		}
	}
}
