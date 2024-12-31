package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MineshaftPieces {
	private static final List<WeightedRandomChestContent> LOOT_TABLES = Lists.newArrayList(
		new WeightedRandomChestContent[]{
			new WeightedRandomChestContent(Items.IRON_INGOT, 0, 1, 5, 10),
			new WeightedRandomChestContent(Items.GOLD_INGOT, 0, 1, 3, 5),
			new WeightedRandomChestContent(Items.REDSTONE, 0, 4, 9, 5),
			new WeightedRandomChestContent(Items.DYE, DyeColor.BLUE.getSwappedId(), 4, 9, 5),
			new WeightedRandomChestContent(Items.DIAMOND, 0, 1, 2, 3),
			new WeightedRandomChestContent(Items.COAL, 0, 3, 8, 10),
			new WeightedRandomChestContent(Items.BREAD, 0, 1, 3, 15),
			new WeightedRandomChestContent(Items.IRON_PICKAXE, 0, 1, 1, 1),
			new WeightedRandomChestContent(Item.fromBlock(Blocks.RAIL), 0, 4, 8, 1),
			new WeightedRandomChestContent(Items.MELON_SEEDS, 0, 2, 4, 10),
			new WeightedRandomChestContent(Items.PUMPKIN_SEEDS, 0, 2, 4, 10),
			new WeightedRandomChestContent(Items.SADDLE, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1)
		}
	);

	public static void registerPieces() {
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftCorridor.class, "MSCorridor");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftCrossing.class, "MSCrossing");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftRoom.class, "MSRoom");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftStairs.class, "MSStairs");
	}

	private static StructurePiece pickPiece(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
		int i = random.nextInt(100);
		if (i >= 80) {
			BlockBox blockBox = MineshaftPieces.MineshaftCrossing.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox != null) {
				return new MineshaftPieces.MineshaftCrossing(chainLength, random, blockBox, orientation);
			}
		} else if (i >= 70) {
			BlockBox blockBox2 = MineshaftPieces.MineshaftStairs.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox2 != null) {
				return new MineshaftPieces.MineshaftStairs(chainLength, random, blockBox2, orientation);
			}
		} else {
			BlockBox blockBox3 = MineshaftPieces.MineshaftCorridor.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox3 != null) {
				return new MineshaftPieces.MineshaftCorridor(chainLength, random, blockBox3, orientation);
			}
		}

		return null;
	}

	private static StructurePiece pieceGenerator(
		StructurePiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (chainLength > 8) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().minX) <= 80 && Math.abs(z - start.getBoundingBox().minZ) <= 80) {
			StructurePiece structurePiece = pickPiece(pieces, random, x, y, z, orientation, chainLength + 1);
			if (structurePiece != null) {
				pieces.add(structurePiece);
				structurePiece.fillOpenings(start, pieces, random);
			}

			return structurePiece;
		} else {
			return null;
		}
	}

	public static class MineshaftCorridor extends StructurePiece {
		private boolean hasRails;
		private boolean hasCobwebs;
		private boolean hasSpawner;
		private int length;

		public MineshaftCorridor() {
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putBoolean("hr", this.hasRails);
			structureNbt.putBoolean("sc", this.hasCobwebs);
			structureNbt.putBoolean("hps", this.hasSpawner);
			structureNbt.putInt("Num", this.length);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			this.hasRails = structureNbt.getBoolean("hr");
			this.hasCobwebs = structureNbt.getBoolean("sc");
			this.hasSpawner = structureNbt.getBoolean("hps");
			this.length = structureNbt.getInt("Num");
		}

		public MineshaftCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.boundingBox = blockBox;
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.facing != Direction.NORTH && this.facing != Direction.SOUTH) {
				this.length = blockBox.getBlockCountX() / 5;
			} else {
				this.length = blockBox.getBlockCountZ() / 5;
			}
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 2, z);

			int i;
			for (i = random.nextInt(3) + 2; i > 0; i--) {
				int j = i * 5;
				switch (orientation) {
					case NORTH:
						blockBox.maxX = x + 2;
						blockBox.minZ = z - (j - 1);
						break;
					case SOUTH:
						blockBox.maxX = x + 2;
						blockBox.maxZ = z + (j - 1);
						break;
					case WEST:
						blockBox.minX = x - (j - 1);
						blockBox.maxZ = z + 2;
						break;
					case EAST:
						blockBox.maxX = x + (j - 1);
						blockBox.maxZ = z + 2;
				}

				if (StructurePiece.getOverlappingPiece(pieces, blockBox) == null) {
					break;
				}
			}

			return i > 0 ? blockBox : null;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			int j = random.nextInt(4);
			if (this.facing != null) {
				switch (this.facing) {
					case NORTH:
						if (j <= 1) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, this.facing, i
							);
						} else if (j == 2) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, i
							);
						} else {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, i
							);
						}
						break;
					case SOUTH:
						if (j <= 1) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, this.facing, i
							);
						} else if (j == 2) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i
							);
						} else {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i
							);
						}
						break;
					case WEST:
						if (j <= 1) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, this.facing, i
							);
						} else if (j == 2) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
						break;
					case EAST:
						if (j <= 1) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, this.facing, i
							);
						} else if (j == 2) {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftPieces.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
				}
			}

			if (i < 8) {
				if (this.facing != Direction.NORTH && this.facing != Direction.SOUTH) {
					for (int m = this.boundingBox.minX + 3; m + 3 <= this.boundingBox.maxX; m += 5) {
						int n = random.nextInt(5);
						if (n == 0) {
							MineshaftPieces.pieceGenerator(start, pieces, random, m, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
						} else if (n == 1) {
							MineshaftPieces.pieceGenerator(start, pieces, random, m, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
						}
					}
				} else {
					for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
						} else if (l == 1) {
							MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean placeChest(World world, BlockBox box, Random random, int x, int y, int z, List<WeightedRandomChestContent> chestContent, int i) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
			if (box.contains(blockPos) && world.getBlockState(blockPos).getBlock().getMaterial() == Material.AIR) {
				int j = random.nextBoolean() ? 1 : 0;
				world.setBlockState(blockPos, Blocks.RAIL.stateFromData(this.getData(Blocks.RAIL, j)), 2);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F)
				);
				WeightedRandomChestContent.fillInventory(random, chestContent, chestMinecartEntity, i);
				world.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				this.fillWithOutline(world, boundingBox, 0, 0, 0, 2, 1, m, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.8F, 0, 2, 0, 2, 2, m, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				if (this.hasCobwebs) {
					this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.fillWithOutline(world, boundingBox, 0, 0, o, 0, 1, o, Blocks.OAK_FENCE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 2, 0, o, 2, 1, o, Blocks.OAK_FENCE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
					if (random.nextInt(4) == 0) {
						this.fillWithOutline(world, boundingBox, 0, 2, o, 0, 2, o, Blocks.PLANKS.getDefaultState(), Blocks.AIR.getDefaultState(), false);
						this.fillWithOutline(world, boundingBox, 2, 2, o, 2, 2, o, Blocks.PLANKS.getDefaultState(), Blocks.AIR.getDefaultState(), false);
					} else {
						this.fillWithOutline(world, boundingBox, 0, 2, o, 2, 2, o, Blocks.PLANKS.getDefaultState(), Blocks.AIR.getDefaultState(), false);
					}

					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 0, 2, o - 1, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 2, 2, o - 1, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 0, 2, o + 1, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 2, 2, o + 1, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 0, 2, o - 2, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 2, 2, o - 2, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 0, 2, o + 2, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 2, 2, o + 2, Blocks.COBWEB.getDefaultState());
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 1, 2, o - 1, Blocks.TORCH.stateFromData(Direction.UP.getId()));
					this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05F, 1, 2, o + 1, Blocks.TORCH.stateFromData(Direction.UP.getId()));
					if (random.nextInt(100) == 0) {
						this.placeChest(
							world,
							boundingBox,
							random,
							2,
							0,
							o - 1,
							WeightedRandomChestContent.combineLootTables(MineshaftPieces.LOOT_TABLES, Items.ENCHANTED_BOOK.getLootTable(random)),
							3 + random.nextInt(4)
						);
					}

					if (random.nextInt(100) == 0) {
						this.placeChest(
							world,
							boundingBox,
							random,
							0,
							0,
							o + 1,
							WeightedRandomChestContent.combineLootTables(MineshaftPieces.LOOT_TABLES, Items.ENCHANTED_BOOK.getLootTable(random)),
							3 + random.nextInt(4)
						);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						q = this.applyZTransform(1, q);
						BlockPos blockPos = new BlockPos(r, p, q);
						if (boundingBox.contains(blockPos)) {
							this.hasSpawner = true;
							world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId("CaveSpider");
							}
						}
					}
				}

				for (int s = 0; s <= 2; s++) {
					for (int t = 0; t <= m; t++) {
						int u = -1;
						BlockState blockState = this.getBlockAt(world, s, u, t, boundingBox);
						if (blockState.getBlock().getMaterial() == Material.AIR) {
							int v = -1;
							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), s, v, t, boundingBox);
						}
					}
				}

				if (this.hasRails) {
					for (int w = 0; w <= m; w++) {
						BlockState blockState2 = this.getBlockAt(world, 1, -1, w, boundingBox);
						if (blockState2.getBlock().getMaterial() != Material.AIR && blockState2.getBlock().isFullBlock()) {
							this.addBlockWithRandomThreshold(world, boundingBox, random, 0.7F, 1, 0, w, Blocks.RAIL.stateFromData(this.getData(Blocks.RAIL, 0)));
						}
					}
				}

				return true;
			}
		}
	}

	public static class MineshaftCrossing extends StructurePiece {
		private Direction orientation;
		private boolean twoFloors;

		public MineshaftCrossing() {
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putBoolean("tf", this.twoFloors);
			structureNbt.putInt("D", this.orientation.getHorizontal());
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			this.twoFloors = structureNbt.getBoolean("tf");
			this.orientation = Direction.fromHorizontal(structureNbt.getInt("D"));
		}

		public MineshaftCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.orientation = direction;
			this.boundingBox = blockBox;
			this.twoFloors = blockBox.getBlockCountY() > 3;
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 2, z);
			if (random.nextInt(4) == 0) {
				blockBox.maxY += 4;
			}

			switch (orientation) {
				case NORTH:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.minZ = z - 4;
					break;
				case SOUTH:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.maxZ = z + 4;
					break;
				case WEST:
					blockBox.minX = x - 4;
					blockBox.minZ = z - 1;
					blockBox.maxZ = z + 3;
					break;
				case EAST:
					blockBox.maxX = x + 4;
					blockBox.minZ = z - 1;
					blockBox.maxZ = z + 3;
			}

			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			switch (this.orientation) {
				case NORTH:
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case SOUTH:
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case WEST:
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					break;
				case EAST:
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftPieces.pieceGenerator(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.pieceGenerator(
						start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.pieceGenerator(
						start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.pieceGenerator(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
					);
				}
			}
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				if (this.twoFloors) {
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ - 1,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY + 3,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3,
						this.boundingBox.maxZ - 1,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
				} else {
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
				}

				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.minX + 1,
					this.boundingBox.minY,
					this.boundingBox.minZ + 1,
					this.boundingBox.minX + 1,
					this.boundingBox.maxY,
					this.boundingBox.minZ + 1,
					Blocks.PLANKS.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.minX + 1,
					this.boundingBox.minY,
					this.boundingBox.maxZ - 1,
					this.boundingBox.minX + 1,
					this.boundingBox.maxY,
					this.boundingBox.maxZ - 1,
					Blocks.PLANKS.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.maxX - 1,
					this.boundingBox.minY,
					this.boundingBox.minZ + 1,
					this.boundingBox.maxX - 1,
					this.boundingBox.maxY,
					this.boundingBox.minZ + 1,
					Blocks.PLANKS.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.maxX - 1,
					this.boundingBox.minY,
					this.boundingBox.maxZ - 1,
					this.boundingBox.maxX - 1,
					this.boundingBox.maxY,
					this.boundingBox.maxZ - 1,
					Blocks.PLANKS.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					false
				);

				for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
					for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
						if (this.getBlockAt(world, i, this.boundingBox.minY - 1, j, boundingBox).getBlock().getMaterial() == Material.AIR) {
							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), i, this.boundingBox.minY - 1, j, boundingBox);
						}
					}
				}

				return true;
			}
		}
	}

	public static class MineshaftRoom extends StructurePiece {
		private List<BlockBox> entrances = Lists.newLinkedList();

		public MineshaftRoom() {
		}

		public MineshaftRoom(int i, Random random, int j, int k) {
			super(i);
			this.boundingBox = new BlockBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			int j = this.boundingBox.getBlockCountY() - 3 - 1;
			if (j <= 0) {
				j = 1;
			}

			int k = 0;

			while (k < this.boundingBox.getBlockCountX()) {
				k += random.nextInt(this.boundingBox.getBlockCountX());
				if (k + 3 > this.boundingBox.getBlockCountX()) {
					break;
				}

				StructurePiece structurePiece = MineshaftPieces.pieceGenerator(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
				);
				if (structurePiece != null) {
					BlockBox blockBox = structurePiece.getBoundingBox();
					this.entrances.add(new BlockBox(blockBox.minX, blockBox.minY, this.boundingBox.minZ, blockBox.maxX, blockBox.maxY, this.boundingBox.minZ + 1));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountX()) {
				k += random.nextInt(this.boundingBox.getBlockCountX());
				if (k + 3 > this.boundingBox.getBlockCountX()) {
					break;
				}

				StructurePiece structurePiece2 = MineshaftPieces.pieceGenerator(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
				);
				if (structurePiece2 != null) {
					BlockBox blockBox2 = structurePiece2.getBoundingBox();
					this.entrances.add(new BlockBox(blockBox2.minX, blockBox2.minY, this.boundingBox.maxZ - 1, blockBox2.maxX, blockBox2.maxY, this.boundingBox.maxZ));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				StructurePiece structurePiece3 = MineshaftPieces.pieceGenerator(
					start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i
				);
				if (structurePiece3 != null) {
					BlockBox blockBox3 = structurePiece3.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.minX, blockBox3.minY, blockBox3.minZ, this.boundingBox.minX + 1, blockBox3.maxY, blockBox3.maxZ));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				StructurePiece structurePiece4 = MineshaftPieces.pieceGenerator(
					start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i
				);
				if (structurePiece4 != null) {
					BlockBox blockBox4 = structurePiece4.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, blockBox4.minY, blockBox4.minZ, this.boundingBox.maxX, blockBox4.maxY, blockBox4.maxZ));
				}

				k += 4;
			}
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.minY,
					this.boundingBox.maxZ,
					Blocks.DIRT.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					true
				);
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 1,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
					this.boundingBox.maxZ,
					Blocks.AIR.getDefaultState(),
					Blocks.AIR.getDefaultState(),
					false
				);

				for (BlockBox blockBox : this.entrances) {
					this.fillWithOutline(
						world,
						boundingBox,
						blockBox.minX,
						blockBox.maxY - 2,
						blockBox.minZ,
						blockBox.maxX,
						blockBox.maxY,
						blockBox.maxZ,
						Blocks.AIR.getDefaultState(),
						Blocks.AIR.getDefaultState(),
						false
					);
				}

				this.fillHalfEllipsoid(
					world,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 4,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.maxY,
					this.boundingBox.maxZ,
					Blocks.AIR.getDefaultState(),
					false
				);
				return true;
			}
		}

		@Override
		public void translate(int x, int y, int z) {
			super.translate(x, y, z);

			for (BlockBox blockBox : this.entrances) {
				blockBox.move(x, y, z);
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			NbtList nbtList = new NbtList();

			for (BlockBox blockBox : this.entrances) {
				nbtList.add(blockBox.toNbt());
			}

			structureNbt.put("Entrances", nbtList);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			NbtList nbtList = structureNbt.getList("Entrances", 11);

			for (int i = 0; i < nbtList.size(); i++) {
				this.entrances.add(new BlockBox(nbtList.getIntArray(i)));
			}
		}
	}

	public static class MineshaftStairs extends StructurePiece {
		public MineshaftStairs() {
		}

		public MineshaftStairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y - 5, z, x, y + 2, z);
			switch (orientation) {
				case NORTH:
					blockBox.maxX = x + 2;
					blockBox.minZ = z - 8;
					break;
				case SOUTH:
					blockBox.maxX = x + 2;
					blockBox.maxZ = z + 8;
					break;
				case WEST:
					blockBox.minX = x - 8;
					blockBox.maxZ = z + 2;
					break;
				case EAST:
					blockBox.maxX = x + 8;
					blockBox.maxZ = z + 2;
			}

			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			if (this.facing != null) {
				switch (this.facing) {
					case NORTH:
						MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
						break;
					case SOUTH:
						MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
						break;
					case WEST:
						MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
						break;
					case EAST:
						MineshaftPieces.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
				}
			}
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(world, boundingBox, 0, 5, 0, 2, 7, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 0, 0, 7, 2, 2, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(
						world, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false
					);
				}

				return true;
			}
		}
	}
}
