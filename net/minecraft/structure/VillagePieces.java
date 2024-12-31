package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;

public class VillagePieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(VillagePieces.BookHouse.class, "ViBH");
		StructurePieceManager.registerPiece(VillagePieces.LargeFarmField.class, "ViDF");
		StructurePieceManager.registerPiece(VillagePieces.FarmField.class, "ViF");
		StructurePieceManager.registerPiece(VillagePieces.LampPost.class, "ViL");
		StructurePieceManager.registerPiece(VillagePieces.PeasantHouse.class, "ViPH");
		StructurePieceManager.registerPiece(VillagePieces.SingleHouse.class, "ViSH");
		StructurePieceManager.registerPiece(VillagePieces.SingleMultifunctionalHouse.class, "ViSmH");
		StructurePieceManager.registerPiece(VillagePieces.Church.class, "ViST");
		StructurePieceManager.registerPiece(VillagePieces.BlacksmithHouse.class, "ViS");
		StructurePieceManager.registerPiece(VillagePieces.StartPiece.class, "ViStart");
		StructurePieceManager.registerPiece(VillagePieces.StraightRoad.class, "ViSR");
		StructurePieceManager.registerPiece(VillagePieces.TinyHouse.class, "ViTRH");
		StructurePieceManager.registerPiece(VillagePieces.Well.class, "ViW");
	}

	public static List<VillagePieces.PieceData> getPieceData(Random random, int size) {
		List<VillagePieces.PieceData> list = Lists.newArrayList();
		list.add(new VillagePieces.PieceData(VillagePieces.SingleHouse.class, 4, MathHelper.nextInt(random, 2 + size, 4 + size * 2)));
		list.add(new VillagePieces.PieceData(VillagePieces.Church.class, 20, MathHelper.nextInt(random, 0 + size, 1 + size)));
		list.add(new VillagePieces.PieceData(VillagePieces.BookHouse.class, 20, MathHelper.nextInt(random, 0 + size, 2 + size)));
		list.add(new VillagePieces.PieceData(VillagePieces.SingleMultifunctionalHouse.class, 3, MathHelper.nextInt(random, 2 + size, 5 + size * 3)));
		list.add(new VillagePieces.PieceData(VillagePieces.PeasantHouse.class, 15, MathHelper.nextInt(random, 0 + size, 2 + size)));
		list.add(new VillagePieces.PieceData(VillagePieces.LargeFarmField.class, 3, MathHelper.nextInt(random, 1 + size, 4 + size)));
		list.add(new VillagePieces.PieceData(VillagePieces.FarmField.class, 3, MathHelper.nextInt(random, 2 + size, 4 + size * 2)));
		list.add(new VillagePieces.PieceData(VillagePieces.BlacksmithHouse.class, 15, MathHelper.nextInt(random, 0, 1 + size)));
		list.add(new VillagePieces.PieceData(VillagePieces.TinyHouse.class, 8, MathHelper.nextInt(random, 0 + size, 3 + size * 2)));
		Iterator<VillagePieces.PieceData> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (((VillagePieces.PieceData)iterator.next()).limit == 0) {
				iterator.remove();
			}
		}

		return list;
	}

	private static int getTotalWeight(List<VillagePieces.PieceData> pieces) {
		boolean bl = false;
		int i = 0;

		for (VillagePieces.PieceData pieceData : pieces) {
			if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
				bl = true;
			}

			i += pieceData.weight;
		}

		return bl ? i : -1;
	}

	private static VillagePieces.AbstractPiece createPiece(
		VillagePieces.StartPiece start,
		VillagePieces.PieceData chance,
		List<StructurePiece> pieces,
		Random random,
		int x,
		int y,
		int z,
		Direction orientation,
		int chainLength
	) {
		Class<? extends VillagePieces.AbstractPiece> class_ = chance.pieceType;
		VillagePieces.AbstractPiece abstractPiece = null;
		if (class_ == VillagePieces.SingleHouse.class) {
			abstractPiece = VillagePieces.SingleHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.Church.class) {
			abstractPiece = VillagePieces.Church.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.BookHouse.class) {
			abstractPiece = VillagePieces.BookHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.SingleMultifunctionalHouse.class) {
			abstractPiece = VillagePieces.SingleMultifunctionalHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.PeasantHouse.class) {
			abstractPiece = VillagePieces.PeasantHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.LargeFarmField.class) {
			abstractPiece = VillagePieces.LargeFarmField.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.FarmField.class) {
			abstractPiece = VillagePieces.FarmField.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.BlacksmithHouse.class) {
			abstractPiece = VillagePieces.BlacksmithHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == VillagePieces.TinyHouse.class) {
			abstractPiece = VillagePieces.TinyHouse.create(start, pieces, random, x, y, z, orientation, chainLength);
		}

		return abstractPiece;
	}

	private static VillagePieces.AbstractPiece pickPiece(
		VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		int i = getTotalWeight(start.field_6245);
		if (i <= 0) {
			return null;
		} else {
			int j = 0;

			while (j < 5) {
				j++;
				int k = random.nextInt(i);

				for (VillagePieces.PieceData pieceData : start.field_6245) {
					k -= pieceData.weight;
					if (k < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.field_95 && start.field_6245.size() > 1) {
							break;
						}

						VillagePieces.AbstractPiece abstractPiece = createPiece(start, pieceData, pieces, random, x, y, z, orientation, chainLength);
						if (abstractPiece != null) {
							pieceData.generatedCount++;
							start.field_95 = pieceData;
							if (!pieceData.canGenerate()) {
								start.field_6245.remove(pieceData);
							}

							return abstractPiece;
						}
					}
				}
			}

			BlockBox blockBox = VillagePieces.LampPost.create(start, pieces, random, x, y, z, orientation);
			return blockBox != null ? new VillagePieces.LampPost(start, chainLength, random, blockBox, orientation) : null;
		}
	}

	private static StructurePiece generate(
		VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (chainLength > 50) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
			StructurePiece structurePiece = pickPiece(start, pieces, random, x, y, z, orientation, chainLength + 1);
			if (structurePiece != null) {
				int i = (structurePiece.boundingBox.minX + structurePiece.boundingBox.maxX) / 2;
				int j = (structurePiece.boundingBox.minZ + structurePiece.boundingBox.maxZ) / 2;
				int k = structurePiece.boundingBox.maxX - structurePiece.boundingBox.minX;
				int l = structurePiece.boundingBox.maxZ - structurePiece.boundingBox.minZ;
				int m = k > l ? k : l;
				if (start.method_105().method_3854(i, j, m / 2 + 4, VillageStructure.BIOMES)) {
					pieces.add(structurePiece);
					start.field_6246.add(structurePiece);
					return structurePiece;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	private static StructurePiece method_93(
		VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (chainLength > 3 + start.field_94) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
			BlockBox blockBox = VillagePieces.StraightRoad.method_106(start, pieces, random, x, y, z, orientation);
			if (blockBox != null && blockBox.minY > 10) {
				StructurePiece structurePiece = new VillagePieces.StraightRoad(start, chainLength, random, blockBox, orientation);
				int i = (structurePiece.boundingBox.minX + structurePiece.boundingBox.maxX) / 2;
				int j = (structurePiece.boundingBox.minZ + structurePiece.boundingBox.maxZ) / 2;
				int k = structurePiece.boundingBox.maxX - structurePiece.boundingBox.minX;
				int l = structurePiece.boundingBox.maxZ - structurePiece.boundingBox.minZ;
				int m = k > l ? k : l;
				if (start.method_105().method_3854(i, j, m / 2 + 4, VillageStructure.BIOMES)) {
					pieces.add(structurePiece);
					start.field_6247.add(structurePiece);
					return structurePiece;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	abstract static class AbstractPiece extends StructurePiece {
		protected int hPos = -1;
		private int villagers;
		private boolean desert;

		public AbstractPiece() {
		}

		protected AbstractPiece(VillagePieces.StartPiece startPiece, int i) {
			super(i);
			if (startPiece != null) {
				this.desert = startPiece.isDesert;
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putInt("HPos", this.hPos);
			structureNbt.putInt("VCount", this.villagers);
			structureNbt.putBoolean("Desert", this.desert);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			this.hPos = structureNbt.getInt("HPos");
			this.villagers = structureNbt.getInt("VCount");
			this.desert = structureNbt.getBoolean("Desert");
		}

		protected StructurePiece fillNWOpening(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case SOUTH:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case WEST:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
					case EAST:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		protected StructurePiece fillSEOpening(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case SOUTH:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case WEST:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
					case EAST:
						return VillagePieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		protected int method_113(World world, BlockBox box) {
			int i = 0;
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++) {
				for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; l++) {
					mutable.setPosition(l, 64, k);
					if (box.contains(mutable)) {
						i += Math.max(world.getTopPosition(mutable).getY(), world.dimension.getAverageYLevel());
						j++;
					}
				}
			}

			return j == 0 ? -1 : i / j;
		}

		protected static boolean isInbounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.minY > 10;
		}

		protected void method_109(World world, BlockBox box, int x, int y, int z, int i) {
			if (this.villagers < i) {
				for (int j = this.villagers; j < i; j++) {
					int k = this.applyXTransform(x + j, z);
					int l = this.applyYTransform(y);
					int m = this.applyZTransform(x + j, z);
					if (!box.contains(new BlockPos(k, l, m))) {
						break;
					}

					this.villagers++;
					VillagerEntity villagerEntity = new VillagerEntity(world);
					villagerEntity.refreshPositionAndAngles((double)k + 0.5, (double)l, (double)m + 0.5, 0.0F, 0.0F);
					villagerEntity.initialize(world.getLocalDifficulty(new BlockPos(villagerEntity)), null);
					villagerEntity.setProfession(this.method_111(j, villagerEntity.profession()));
					world.spawnEntity(villagerEntity);
				}
			}
		}

		protected int method_111(int i, int j) {
			return j;
		}

		protected BlockState checkSandStone(BlockState state) {
			if (this.desert) {
				if (state.getBlock() == Blocks.LOG || state.getBlock() == Blocks.LOG2) {
					return Blocks.SANDSTONE.getDefaultState();
				}

				if (state.getBlock() == Blocks.COBBLESTONE) {
					return Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.DEFAULT.getId());
				}

				if (state.getBlock() == Blocks.PLANKS) {
					return Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId());
				}

				if (state.getBlock() == Blocks.WOODEN_STAIRS) {
					return Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, state.get(StairsBlock.FACING));
				}

				if (state.getBlock() == Blocks.STONE_STAIRS) {
					return Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, state.get(StairsBlock.FACING));
				}

				if (state.getBlock() == Blocks.GRAVEL) {
					return Blocks.SANDSTONE.getDefaultState();
				}
			}

			return state;
		}

		@Override
		protected void setBlockState(World world, BlockState state, int x, int y, int z, BlockBox box) {
			BlockState blockState = this.checkSandStone(state);
			super.setBlockState(world, blockState, x, y, z, box);
		}

		@Override
		protected void fillWithOutline(
			World world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState outline, BlockState inside, boolean cantReplaceAir
		) {
			BlockState blockState = this.checkSandStone(outline);
			BlockState blockState2 = this.checkSandStone(inside);
			super.fillWithOutline(world, box, minX, minY, minZ, maxX, maxY, maxZ, blockState, blockState2, cantReplaceAir);
		}

		@Override
		protected void fillAirAndLiquidsDownwards(World world, BlockState block, int x, int y, int z, BlockBox box) {
			BlockState blockState = this.checkSandStone(block);
			super.fillAirAndLiquidsDownwards(world, blockState, x, y, z, box);
		}

		protected void setDesert(boolean isDesert) {
			this.desert = isDesert;
		}
	}

	public static class BlacksmithHouse extends VillagePieces.AbstractPiece {
		private boolean hasChest;

		public BlacksmithHouse() {
		}

		public BlacksmithHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static VillagePieces.BlacksmithHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 10, 6, 7, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.BlacksmithHouse(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Chest", this.hasChest);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.hasChest = structureNbt.getBoolean("Chest");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 9, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 9, 0, 6, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 0, 9, 4, 6, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 1, 8, 5, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 4, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 1, 0, 3, 4, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 6, 0, 4, 6, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 3, 3, 1, boundingBox);
			this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 3, 2, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 1, 3, 5, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 6, 5, 3, 6, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 1, 0, 5, 3, 0, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 1, 0, 9, 3, 0, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 1, 4, 9, 4, 6, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.setBlockState(world, Blocks.FLOWING_LAVA.getDefaultState(), 7, 1, 5, boundingBox);
			this.setBlockState(world, Blocks.FLOWING_LAVA.getDefaultState(), 8, 1, 5, boundingBox);
			this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 9, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 9, 2, 4, boundingBox);
			this.fillWithOutline(world, boundingBox, 7, 2, 4, 8, 2, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 3, boundingBox);
			this.setBlockState(world, Blocks.FURNACE.getDefaultState(), 6, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.FURNACE.getDefaultState(), 6, 3, 3, boundingBox);
			this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 8, 1, 1, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 6, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 6, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 1, 4, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 2, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 1, 1, 5, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), 2, 1, 5, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST), 1, 1, 4, boundingBox);
			if (!this.hasChest && boundingBox.contains(new BlockPos(this.applyXTransform(5, 5), this.applyYTransform(1), this.applyZTransform(5, 5)))) {
				this.hasChest = true;
				this.method_11852(world, boundingBox, random, 5, 1, 5, LootTables.VILLAGE_BLACKSMITH_CHEST);
			}

			for (int i = 6; i <= 8; i++) {
				if (this.getBlockAt(world, i, 0, -1, boundingBox).getMaterial() == Material.AIR
					&& this.getBlockAt(world, i, -1, -1, boundingBox).getMaterial() != Material.AIR) {
					this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), i, 0, -1, boundingBox);
				}
			}

			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 10; k++) {
					this.clearBlocksUpwards(world, k, 6, j, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), k, -1, j, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 7, 1, 1, 1);
			return true;
		}

		@Override
		protected int method_111(int i, int j) {
			return 3;
		}
	}

	public static class BookHouse extends VillagePieces.AbstractPiece {
		public BookHouse() {
		}

		public BookHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static VillagePieces.BookHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 9, 9, 6, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.BookHouse(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 9 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 8, 0, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 8, 5, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 6, 1, 8, 6, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 7, 2, 8, 7, 3, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.setBlockState(world, Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), j, 6 + i, i, boundingBox);
					this.setBlockState(world, Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH), j, 6 + i, 5 - i, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 5, 8, 1, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 1, 0, 8, 1, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 1, 0, 7, 1, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 4, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 4, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 2, 5, 8, 4, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 2, 0, 8, 4, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 5, 7, 4, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 2, 1, 8, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 4, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 5, boundingBox);
			this.fillWithOutline(world, boundingBox, 1, 4, 1, 7, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 4, 7, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 7, 1, 4, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST), 7, 1, 3, boundingBox);
			BlockState blockState = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.setBlockState(world, blockState, 6, 1, 4, boundingBox);
			this.setBlockState(world, blockState, 5, 1, 4, boundingBox);
			this.setBlockState(world, blockState, 4, 1, 4, boundingBox);
			this.setBlockState(world, blockState, 3, 1, 4, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 3, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 1, 3, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.CRAFTING_TABLE.getDefaultState(), 7, 1, 1, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, boundingBox);
			this.placeDoor(world, boundingBox, random, 1, 1, 0, Direction.NORTH);
			if (this.getBlockAt(world, 1, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 1, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), 1, 0, -1, boundingBox);
			}

			for (int k = 0; k < 6; k++) {
				for (int l = 0; l < 9; l++) {
					this.clearBlocksUpwards(world, l, 9, k, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), l, -1, k, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 2, 1, 2, 1);
			return true;
		}

		@Override
		protected int method_111(int i, int j) {
			return 1;
		}
	}

	public static class Church extends VillagePieces.AbstractPiece {
		public Church() {
		}

		public Church(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static VillagePieces.Church create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 5, 12, 9, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.Church(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 12 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 3, 3, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 1, 3, 9, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 3, 0, 8, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 3, 10, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 10, 3, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 10, 3, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 4, 0, 4, 7, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 4, 4, 4, 7, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 8, 3, 4, 8, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 4, 3, 10, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 5, 3, 5, 7, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 9, 0, 4, 9, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 0, 4, 4, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 11, 2, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 11, 2, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, 11, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, 11, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 1, 1, 6, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 1, 1, 7, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 2, 1, 7, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 6, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 3, 1, 7, boundingBox);
			BlockState blockState = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			BlockState blockState3 = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
			this.setBlockState(world, blockState, 1, 1, 5, boundingBox);
			this.setBlockState(world, blockState, 2, 1, 6, boundingBox);
			this.setBlockState(world, blockState, 3, 1, 5, boundingBox);
			this.setBlockState(world, blockState2, 1, 2, 7, boundingBox);
			this.setBlockState(world, blockState3, 3, 2, 7, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 3, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 6, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 7, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 6, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 7, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 6, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 7, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 6, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 7, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 6, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 3, 6, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 3, 8, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.SOUTH), 2, 4, 7, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.EAST), 1, 4, 6, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.WEST), 3, 4, 6, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH), 2, 4, 5, boundingBox);
			BlockState blockState4 = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);

			for (int i = 1; i <= 9; i++) {
				this.setBlockState(world, blockState4, 3, i, 3, boundingBox);
			}

			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 2, 0, boundingBox);
			this.placeDoor(world, boundingBox, random, 2, 1, 0, Direction.NORTH);
			if (this.getBlockAt(world, 2, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 2, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, blockState, 2, 0, -1, boundingBox);
			}

			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 5; k++) {
					this.clearBlocksUpwards(world, k, 12, j, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), k, -1, j, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 2, 1, 2, 1);
			return true;
		}

		@Override
		protected int method_111(int i, int j) {
			return 2;
		}
	}

	public abstract static class DelegatingPiece extends VillagePieces.AbstractPiece {
		public DelegatingPiece() {
		}

		protected DelegatingPiece(VillagePieces.StartPiece startPiece, int i) {
			super(startPiece, i);
		}
	}

	public static class FarmField extends VillagePieces.AbstractPiece {
		private Block cropA;
		private Block cropB;

		public FarmField() {
		}

		public FarmField(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.cropA = this.getCrop(random);
			this.cropB = this.getCrop(random);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("CA", Block.REGISTRY.getRawId(this.cropA));
			structureNbt.putInt("CB", Block.REGISTRY.getRawId(this.cropB));
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.cropA = Block.getById(structureNbt.getInt("CA"));
			this.cropB = Block.getById(structureNbt.getInt("CB"));
		}

		private Block getCrop(Random random) {
			switch (random.nextInt(10)) {
				case 0:
				case 1:
					return Blocks.CARROTS;
				case 2:
				case 3:
					return Blocks.POTATOES;
				case 4:
					return Blocks.BEETROOTS;
				default:
					return Blocks.WHEAT;
			}
		}

		public static VillagePieces.FarmField create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 7, 4, 9, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.FarmField(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 6, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 0, 0, 6, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 5, 0, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 8, 5, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				int j = ((CropBlock)this.cropA).getMaxAge();
				int k = j / 3;
				this.setBlockState(world, this.cropA.stateFromData(MathHelper.nextInt(random, k, j)), 1, 1, i, boundingBox);
				this.setBlockState(world, this.cropA.stateFromData(MathHelper.nextInt(random, k, j)), 2, 1, i, boundingBox);
				int l = ((CropBlock)this.cropB).getMaxAge();
				int m = l / 3;
				this.setBlockState(world, this.cropB.stateFromData(MathHelper.nextInt(random, m, l)), 4, 1, i, boundingBox);
				this.setBlockState(world, this.cropB.stateFromData(MathHelper.nextInt(random, m, l)), 5, 1, i, boundingBox);
			}

			for (int n = 0; n < 9; n++) {
				for (int o = 0; o < 7; o++) {
					this.clearBlocksUpwards(world, o, 4, n, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.DIRT.getDefaultState(), o, -1, n, boundingBox);
				}
			}

			return true;
		}
	}

	public static class LampPost extends VillagePieces.AbstractPiece {
		public LampPost() {
		}

		public LampPost(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static BlockBox create(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 3, 4, 2, orientation);
			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 0, 0, 0, 2, 3, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 0, 0, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.WOOL.stateFromData(DyeColor.WHITE.getSwappedId()), 1, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.EAST), 2, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH), 1, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.WEST), 0, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.SOUTH), 1, 3, -1, boundingBox);
			return true;
		}
	}

	public static class LargeFarmField extends VillagePieces.AbstractPiece {
		private Block cropA;
		private Block cropB;
		private Block cropC;
		private Block cropD;

		public LargeFarmField() {
		}

		public LargeFarmField(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.cropA = this.getCrop(random);
			this.cropB = this.getCrop(random);
			this.cropC = this.getCrop(random);
			this.cropD = this.getCrop(random);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("CA", Block.REGISTRY.getRawId(this.cropA));
			structureNbt.putInt("CB", Block.REGISTRY.getRawId(this.cropB));
			structureNbt.putInt("CC", Block.REGISTRY.getRawId(this.cropC));
			structureNbt.putInt("CD", Block.REGISTRY.getRawId(this.cropD));
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.cropA = Block.getById(structureNbt.getInt("CA"));
			this.cropB = Block.getById(structureNbt.getInt("CB"));
			this.cropC = Block.getById(structureNbt.getInt("CC"));
			this.cropD = Block.getById(structureNbt.getInt("CD"));
			if (!(this.cropA instanceof CropBlock)) {
				this.cropA = Blocks.WHEAT;
			}

			if (!(this.cropB instanceof CropBlock)) {
				this.cropB = Blocks.CARROTS;
			}

			if (!(this.cropC instanceof CropBlock)) {
				this.cropC = Blocks.POTATOES;
			}

			if (!(this.cropD instanceof CropBlock)) {
				this.cropD = Blocks.BEETROOTS;
			}
		}

		private Block getCrop(Random random) {
			switch (random.nextInt(10)) {
				case 0:
				case 1:
					return Blocks.CARROTS;
				case 2:
				case 3:
					return Blocks.POTATOES;
				case 4:
					return Blocks.BEETROOTS;
				default:
					return Blocks.WHEAT;
			}
		}

		public static VillagePieces.LargeFarmField create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 13, 4, 9, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.LargeFarmField(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 12, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 0, 0, 6, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 12, 0, 0, 12, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 11, 0, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 8, 11, 0, 8, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 0, 1, 9, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				int j = ((CropBlock)this.cropA).getMaxAge();
				int k = j / 3;
				this.setBlockState(world, this.cropA.stateFromData(MathHelper.nextInt(random, k, j)), 1, 1, i, boundingBox);
				this.setBlockState(world, this.cropA.stateFromData(MathHelper.nextInt(random, k, j)), 2, 1, i, boundingBox);
				int l = ((CropBlock)this.cropB).getMaxAge();
				int m = l / 3;
				this.setBlockState(world, this.cropB.stateFromData(MathHelper.nextInt(random, m, l)), 4, 1, i, boundingBox);
				this.setBlockState(world, this.cropB.stateFromData(MathHelper.nextInt(random, m, l)), 5, 1, i, boundingBox);
				int n = ((CropBlock)this.cropC).getMaxAge();
				int o = n / 3;
				this.setBlockState(world, this.cropC.stateFromData(MathHelper.nextInt(random, o, n)), 7, 1, i, boundingBox);
				this.setBlockState(world, this.cropC.stateFromData(MathHelper.nextInt(random, o, n)), 8, 1, i, boundingBox);
				int p = ((CropBlock)this.cropD).getMaxAge();
				int q = p / 3;
				this.setBlockState(world, this.cropD.stateFromData(MathHelper.nextInt(random, q, p)), 10, 1, i, boundingBox);
				this.setBlockState(world, this.cropD.stateFromData(MathHelper.nextInt(random, q, p)), 11, 1, i, boundingBox);
			}

			for (int r = 0; r < 9; r++) {
				for (int s = 0; s < 13; s++) {
					this.clearBlocksUpwards(world, s, 4, r, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.DIRT.getDefaultState(), s, -1, r, boundingBox);
				}
			}

			return true;
		}
	}

	public static class PeasantHouse extends VillagePieces.AbstractPiece {
		public PeasantHouse() {
		}

		public PeasantHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static VillagePieces.PeasantHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 9, 7, 11, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.PeasantHouse(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 0, 6, 8, 0, 10, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, 0, 6, boundingBox);
			this.fillWithOutline(world, boundingBox, 2, 1, 6, 2, 1, 10, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 1, 6, 8, 1, 10, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 1, 10, 7, 1, 10, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 1, 7, 0, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 0, 0, 8, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 7, 1, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 5, 7, 1, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 5, 7, 3, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 1, 8, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 4, 8, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 2, 8, 5, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 0, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 0, 4, 3, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 4, 3, boundingBox);
			BlockState blockState = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState3 = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.setBlockState(world, blockState, j, 4 + i, i, boundingBox);
					this.setBlockState(world, blockState2, j, 4 + i, 5 - i, boundingBox);
				}
			}

			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 1, 3, boundingBox);
			this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 2, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 1, 1, 4, boundingBox);
			this.setBlockState(world, blockState, 2, 1, 4, boundingBox);
			this.setBlockState(world, blockState3, 1, 1, 3, boundingBox);
			this.fillWithOutline(world, boundingBox, 5, 0, 1, 7, 0, 3, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
			this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 6, 1, 1, boundingBox);
			this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 6, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH), 2, 3, 1, boundingBox);
			this.placeDoor(world, boundingBox, random, 2, 1, 0, Direction.NORTH);
			if (this.getBlockAt(world, 2, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 2, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, blockState, 2, 0, -1, boundingBox);
			}

			this.setBlockState(world, Blocks.AIR.getDefaultState(), 6, 1, 5, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 6, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.SOUTH), 6, 3, 4, boundingBox);
			this.placeDoor(world, boundingBox, random, 6, 1, 5, Direction.SOUTH);

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					this.clearBlocksUpwards(world, l, 7, k, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), l, -1, k, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 4, 1, 2, 2);
			return true;
		}

		@Override
		protected int method_111(int i, int j) {
			return i == 0 ? 4 : super.method_111(i, j);
		}
	}

	public static class PieceData {
		public Class<? extends VillagePieces.AbstractPiece> pieceType;
		public final int weight;
		public int generatedCount;
		public int limit;

		public PieceData(Class<? extends VillagePieces.AbstractPiece> class_, int i, int j) {
			this.pieceType = class_;
			this.weight = i;
			this.limit = j;
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class SingleHouse extends VillagePieces.AbstractPiece {
		private boolean terrace;

		public SingleHouse() {
		}

		public SingleHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.terrace = random.nextBoolean();
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Terrace", this.terrace);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.terrace = structureNbt.getBoolean("Terrace");
		}

		public static VillagePieces.SingleHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction direction, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 5, 6, 5, direction);
			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : new VillagePieces.SingleHouse(start, chainLength, random, blockBox, direction);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 0, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 0, 4, 4, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 1, 3, 4, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 1, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 0, 3, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 4, boundingBox);
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 4, 3, 3, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 1, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 1, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 1, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 2, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 3, 3, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 3, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 3, 1, 0, boundingBox);
			if (this.getBlockAt(world, 2, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 2, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), 2, 0, -1, boundingBox);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 3, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			if (this.terrace) {
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 5, 4, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 5, 4, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 5, 4, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 5, 4, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 5, 4, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 5, 1, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 5, 2, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 5, 3, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 5, 1, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 5, 2, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 0, 5, 3, boundingBox);
			}

			if (this.terrace) {
				BlockState blockState = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
				this.setBlockState(world, blockState, 3, 1, 3, boundingBox);
				this.setBlockState(world, blockState, 3, 2, 3, boundingBox);
				this.setBlockState(world, blockState, 3, 3, 3, boundingBox);
				this.setBlockState(world, blockState, 3, 4, 3, boundingBox);
			}

			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH), 2, 3, 1, boundingBox);

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					this.clearBlocksUpwards(world, j, 6, i, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 1, 1, 2, 1);
			return true;
		}
	}

	public static class SingleMultifunctionalHouse extends VillagePieces.AbstractPiece {
		private boolean tall;
		private int tablePosition;

		public SingleMultifunctionalHouse() {
		}

		public SingleMultifunctionalHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.tall = random.nextBoolean();
			this.tablePosition = random.nextInt(3);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("T", this.tablePosition);
			structureNbt.putBoolean("C", this.tall);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.tablePosition = structureNbt.getInt("T");
			this.tall = structureNbt.getBoolean("C");
		}

		public static VillagePieces.SingleMultifunctionalHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int type
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 4, 6, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.SingleMultifunctionalHouse(start, type, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 3, 0, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
			if (this.tall) {
				this.fillWithOutline(world, boundingBox, 1, 4, 1, 2, 4, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			} else {
				this.fillWithOutline(world, boundingBox, 1, 5, 1, 2, 5, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			}

			this.setBlockState(world, Blocks.LOG.getDefaultState(), 1, 4, 0, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 4, 0, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 1, 4, 4, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 4, 4, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 1, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 3, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 1, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 3, boundingBox);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 3, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 1, 0, 3, 3, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 4, 0, 3, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 1, 4, 3, 3, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 1, 1, 3, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 4, 2, 3, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 2, boundingBox);
			if (this.tablePosition > 0) {
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), this.tablePosition, 1, 3, boundingBox);
				this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, boundingBox);
			}

			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, boundingBox);
			this.placeDoor(world, boundingBox, random, 1, 1, 0, Direction.NORTH);
			if (this.getBlockAt(world, 1, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 1, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH), 1, 0, -1, boundingBox);
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					this.clearBlocksUpwards(world, j, 6, i, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 1, 1, 2, 1);
			return true;
		}
	}

	public static class StartPiece extends VillagePieces.Well {
		public SingletonBiomeSource field_13021;
		public boolean isDesert;
		public int field_94;
		public VillagePieces.PieceData field_95;
		public List<VillagePieces.PieceData> field_6245;
		public List<StructurePiece> field_6246 = Lists.newArrayList();
		public List<StructurePiece> field_6247 = Lists.newArrayList();

		public StartPiece() {
		}

		public StartPiece(SingletonBiomeSource singletonBiomeSource, int i, Random random, int j, int k, List<VillagePieces.PieceData> list, int l) {
			super(null, 0, random, j, k);
			this.field_13021 = singletonBiomeSource;
			this.field_6245 = list;
			this.field_94 = l;
			Biome biome = singletonBiomeSource.method_11536(new BlockPos(j, 0, k), Biomes.DEFAULT);
			this.isDesert = biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS;
			this.setDesert(this.isDesert);
		}

		public SingletonBiomeSource method_105() {
			return this.field_13021;
		}
	}

	public static class StraightRoad extends VillagePieces.DelegatingPiece {
		private int length;

		public StraightRoad() {
		}

		public StraightRoad(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.length = Math.max(blockBox.getBlockCountX(), blockBox.getBlockCountZ());
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("Length", this.length);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.length = structureNbt.getInt("Length");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			boolean bl = false;

			for (int i = random.nextInt(5); i < this.length - 8; i += 2 + random.nextInt(5)) {
				StructurePiece structurePiece = this.fillNWOpening((VillagePieces.StartPiece)start, pieces, random, 0, i);
				if (structurePiece != null) {
					i += Math.max(structurePiece.boundingBox.getBlockCountX(), structurePiece.boundingBox.getBlockCountZ());
					bl = true;
				}
			}

			for (int var7 = random.nextInt(5); var7 < this.length - 8; var7 += 2 + random.nextInt(5)) {
				StructurePiece structurePiece2 = this.fillSEOpening((VillagePieces.StartPiece)start, pieces, random, 0, var7);
				if (structurePiece2 != null) {
					var7 += Math.max(structurePiece2.boundingBox.getBlockCountX(), structurePiece2.boundingBox.getBlockCountZ());
					bl = true;
				}
			}

			Direction direction = this.method_11854();
			if (bl && random.nextInt(3) > 0 && direction != null) {
				switch (direction) {
					case NORTH:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY,
							this.boundingBox.minZ,
							Direction.WEST,
							this.getChainLength()
						);
						break;
					case SOUTH:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY,
							this.boundingBox.maxZ - 2,
							Direction.WEST,
							this.getChainLength()
						);
						break;
					case WEST:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.minX,
							this.boundingBox.minY,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
						break;
					case EAST:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.maxX - 2,
							this.boundingBox.minY,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
				}
			}

			if (bl && random.nextInt(3) > 0 && direction != null) {
				switch (direction) {
					case NORTH:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY,
							this.boundingBox.minZ,
							Direction.EAST,
							this.getChainLength()
						);
						break;
					case SOUTH:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY,
							this.boundingBox.maxZ - 2,
							Direction.EAST,
							this.getChainLength()
						);
						break;
					case WEST:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.minX,
							this.boundingBox.minY,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
						break;
					case EAST:
						VillagePieces.method_93(
							(VillagePieces.StartPiece)start,
							pieces,
							random,
							this.boundingBox.maxX - 2,
							this.boundingBox.minY,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
				}
			}
		}

		public static BlockBox method_106(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction direction) {
			for (int i = 7 * MathHelper.nextInt(random, 3, 5); i >= 7; i -= 7) {
				BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 3, 3, i, direction);
				if (StructurePiece.getOverlappingPiece(pieces, blockBox) == null) {
					return blockBox;
				}
			}

			return null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			BlockState blockState = this.checkSandStone(Blocks.GRAVEL.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());

			for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
				for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
					BlockPos blockPos = new BlockPos(i, 64, j);
					if (boundingBox.contains(blockPos)) {
						blockPos = world.getTopPosition(blockPos).down();
						world.setBlockState(blockPos, blockState, 2);
						world.setBlockState(blockPos.down(), blockState2, 2);
					}
				}
			}

			return true;
		}
	}

	public static class TinyHouse extends VillagePieces.AbstractPiece {
		public TinyHouse() {
		}

		public TinyHouse(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static VillagePieces.TinyHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 9, 7, 12, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new VillagePieces.TinyHouse(start, chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 0, 5, 8, 0, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 1, 7, 0, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 0, 0, 8, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 7, 2, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 5, 2, 1, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 0, 6, 2, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 0, 10, 7, 3, 10, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 5, 2, 3, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 1, 8, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 4, 4, 3, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 2, 8, 5, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 0, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 0, 4, 3, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 4, 2, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 4, 3, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 4, 4, boundingBox);
			BlockState blockState = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState3 = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			BlockState blockState4 = Blocks.WOODEN_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.setBlockState(world, blockState, j, 4 + i, i, boundingBox);
					if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
						this.setBlockState(world, blockState2, j, 4 + i, 5 - i, boundingBox);
					}
				}
			}

			this.fillWithOutline(world, boundingBox, 3, 4, 5, 3, 4, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 4, 2, 7, 4, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 5, 4, 4, 5, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 5, 4, 6, 5, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 6, 3, 5, 6, 10, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);

			for (int k = 4; k >= 1; k--) {
				this.setBlockState(world, Blocks.PLANKS.getDefaultState(), k, 2 + k, 7 - k, boundingBox);

				for (int l = 8 - k; l <= 10; l++) {
					this.setBlockState(world, blockState4, k, 2 + k, l, boundingBox);
				}
			}

			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 6, 6, 3, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 7, 5, 4, boundingBox);
			this.setBlockState(world, blockState3, 6, 6, 4, boundingBox);

			for (int m = 6; m <= 8; m++) {
				for (int n = 5; n <= 10; n++) {
					this.setBlockState(world, blockState3, m, 12 - m, n, boundingBox);
				}
			}

			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 4, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 6, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 4, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 2, 5, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 6, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 7, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 8, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 8, 2, 9, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 2, 6, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 7, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 8, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 2, 9, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 4, 4, 10, boundingBox);
			this.setBlockState(world, Blocks.GLASS_PANE.getDefaultState(), 5, 4, 10, boundingBox);
			this.setBlockState(world, Blocks.LOG.getDefaultState(), 6, 4, 10, boundingBox);
			this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 5, 5, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 1, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 2, 0, boundingBox);
			this.setBlockState(world, Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, Direction.NORTH), 2, 3, 1, boundingBox);
			this.placeDoor(world, boundingBox, random, 2, 1, 0, Direction.NORTH);
			this.fillWithOutline(world, boundingBox, 1, 0, -1, 3, 2, -1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			if (this.getBlockAt(world, 2, 0, -1, boundingBox).getMaterial() == Material.AIR
				&& this.getBlockAt(world, 2, -1, -1, boundingBox).getMaterial() != Material.AIR) {
				this.setBlockState(world, blockState, 2, 0, -1, boundingBox);
			}

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < 9; p++) {
					this.clearBlocksUpwards(world, p, 7, o, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), p, -1, o, boundingBox);
				}
			}

			for (int q = 5; q < 11; q++) {
				for (int r = 2; r < 9; r++) {
					this.clearBlocksUpwards(world, r, 7, q, boundingBox);
					this.fillAirAndLiquidsDownwards(world, Blocks.COBBLESTONE.getDefaultState(), r, -1, q, boundingBox);
				}
			}

			this.method_109(world, boundingBox, 4, 1, 2, 2);
			return true;
		}
	}

	public static class Well extends VillagePieces.AbstractPiece {
		public Well() {
		}

		public Well(VillagePieces.StartPiece startPiece, int i, Random random, int j, int k) {
			super(startPiece, i);
			this.method_11853(Direction.DirectionType.HORIZONTAL.getRandomDirection(random));
			if (this.method_11854().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
			} else {
				this.boundingBox = new BlockBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
			}
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			VillagePieces.method_93(
				(VillagePieces.StartPiece)start,
				pieces,
				random,
				this.boundingBox.minX - 1,
				this.boundingBox.maxY - 4,
				this.boundingBox.minZ + 1,
				Direction.WEST,
				this.getChainLength()
			);
			VillagePieces.method_93(
				(VillagePieces.StartPiece)start,
				pieces,
				random,
				this.boundingBox.maxX + 1,
				this.boundingBox.maxY - 4,
				this.boundingBox.minZ + 1,
				Direction.EAST,
				this.getChainLength()
			);
			VillagePieces.method_93(
				(VillagePieces.StartPiece)start,
				pieces,
				random,
				this.boundingBox.minX + 1,
				this.boundingBox.maxY - 4,
				this.boundingBox.minZ - 1,
				Direction.NORTH,
				this.getChainLength()
			);
			VillagePieces.method_93(
				(VillagePieces.StartPiece)start,
				pieces,
				random,
				this.boundingBox.minX + 1,
				this.boundingBox.maxY - 4,
				this.boundingBox.maxZ + 1,
				Direction.SOUTH,
				this.getChainLength()
			);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.hPos < 0) {
				this.hPos = this.method_113(world, boundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 3, 0);
			}

			this.fillWithOutline(world, boundingBox, 1, 0, 1, 4, 12, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.FLOWING_WATER.getDefaultState(), false);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 12, 2, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 12, 2, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, 12, 3, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 12, 3, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 13, 1, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 14, 1, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 13, 1, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 14, 1, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 13, 4, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 14, 4, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 13, 4, boundingBox);
			this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 14, 4, boundingBox);
			this.fillWithOutline(world, boundingBox, 1, 15, 1, 4, 15, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 5; j++) {
					if (j == 0 || j == 5 || i == 0 || i == 5) {
						this.setBlockState(world, Blocks.GRAVEL.getDefaultState(), j, 11, i, boundingBox);
						this.clearBlocksUpwards(world, j, 12, i, boundingBox);
					}
				}
			}

			return true;
		}
	}
}
