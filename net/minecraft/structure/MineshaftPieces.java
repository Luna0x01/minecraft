package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3735;
import net.minecraft.class_3867;
import net.minecraft.class_3998;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class MineshaftPieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftCorridor.class, "MSCorridor");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftCrossing.class, "MSCrossing");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftRoom.class, "MSRoom");
		StructurePieceManager.registerPiece(MineshaftPieces.MineshaftStairs.class, "MSStairs");
	}

	private static MineshaftPieces.class_3015 method_13369(
		List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, class_3867.class_3014 arg
	) {
		int m = random.nextInt(100);
		if (m >= 80) {
			BlockBox blockBox = MineshaftPieces.MineshaftCrossing.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox != null) {
				return new MineshaftPieces.MineshaftCrossing(l, random, blockBox, direction, arg);
			}
		} else if (m >= 70) {
			BlockBox blockBox2 = MineshaftPieces.MineshaftStairs.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox2 != null) {
				return new MineshaftPieces.MineshaftStairs(l, random, blockBox2, direction, arg);
			}
		} else {
			BlockBox blockBox3 = MineshaftPieces.MineshaftCorridor.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox3 != null) {
				return new MineshaftPieces.MineshaftCorridor(l, random, blockBox3, direction, arg);
			}
		}

		return null;
	}

	private static MineshaftPieces.class_3015 method_13370(
		StructurePiece structurePiece, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (l > 8) {
			return null;
		} else if (Math.abs(i - structurePiece.getBoundingBox().minX) <= 80 && Math.abs(k - structurePiece.getBoundingBox().minZ) <= 80) {
			class_3867.class_3014 lv = ((MineshaftPieces.class_3015)structurePiece).field_14868;
			MineshaftPieces.class_3015 lv2 = method_13369(list, random, i, j, k, direction, l + 1, lv);
			if (lv2 != null) {
				list.add(lv2);
				lv2.fillOpenings(structurePiece, list, random);
			}

			return lv2;
		} else {
			return null;
		}
	}

	public static class MineshaftCorridor extends MineshaftPieces.class_3015 {
		private boolean hasRails;
		private boolean hasCobwebs;
		private boolean hasSpawner;
		private int length;

		public MineshaftCorridor() {
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("hr", this.hasRails);
			structureNbt.putBoolean("sc", this.hasCobwebs);
			structureNbt.putBoolean("hps", this.hasSpawner);
			structureNbt.putInt("Num", this.length);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.hasRails = nbtCompound.getBoolean("hr");
			this.hasCobwebs = nbtCompound.getBoolean("sc");
			this.hasSpawner = nbtCompound.getBoolean("hps");
			this.length = nbtCompound.getInt("Num");
		}

		public MineshaftCorridor(int i, Random random, BlockBox blockBox, Direction direction, class_3867.class_3014 arg) {
			super(i, arg);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.method_11854().getAxis() == Direction.Axis.Z) {
				this.length = blockBox.getBlockCountZ() / 5;
			} else {
				this.length = blockBox.getBlockCountX() / 5;
			}
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 3 - 1, z);

			int i;
			for (i = random.nextInt(3) + 2; i > 0; i--) {
				int j = i * 5;
				switch (orientation) {
					case NORTH:
					default:
						blockBox.maxX = x + 3 - 1;
						blockBox.minZ = z - (j - 1);
						break;
					case SOUTH:
						blockBox.maxX = x + 3 - 1;
						blockBox.maxZ = z + j - 1;
						break;
					case WEST:
						blockBox.minX = x - (j - 1);
						blockBox.maxZ = z + 3 - 1;
						break;
					case EAST:
						blockBox.maxX = x + j - 1;
						blockBox.maxZ = z + 3 - 1;
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
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						if (j <= 1) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction, i
							);
						} else if (j == 2) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, i
							);
						} else {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, i
							);
						}
						break;
					case SOUTH:
						if (j <= 1) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction, i
							);
						} else if (j == 2) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i
							);
						} else {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i
							);
						}
						break;
					case WEST:
						if (j <= 1) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
						break;
					case EAST:
						if (j <= 1) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftPieces.method_13370(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					for (int m = this.boundingBox.minX + 3; m + 3 <= this.boundingBox.maxX; m += 5) {
						int n = random.nextInt(5);
						if (n == 0) {
							MineshaftPieces.method_13370(start, pieces, random, m, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
						} else if (n == 1) {
							MineshaftPieces.method_13370(start, pieces, random, m, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
						}
					}
				} else {
					for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
						} else if (l == 1) {
							MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean method_11852(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Identifier identifier) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
			if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).isAir() && !iWorld.getBlockState(blockPos.down()).isAir()) {
				BlockState blockState = Blocks.RAIL
					.getDefaultState()
					.withProperty(RailBlock.field_18435, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
				this.method_56(iWorld, blockState, i, j, k, blockBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					iWorld.method_16348(), (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F)
				);
				chestMinecartEntity.setLootTable(identifier, random.nextLong());
				iWorld.method_3686(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_17651(iWorld, blockBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				BlockState blockState = this.method_13374();
				this.method_17653(iWorld, blockBox, 0, 0, 0, 2, 1, m, field_19406, field_19406, false);
				this.method_17656(iWorld, blockBox, random, 0.8F, 0, 2, 0, 2, 2, m, field_19406, field_19406, false, false);
				if (this.hasCobwebs) {
					this.method_17656(iWorld, blockBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), field_19406, false, true);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.method_13371(iWorld, blockBox, 0, 0, o, 2, 2, random);
					this.method_13372(iWorld, blockBox, random, 0.1F, 0, 2, o - 1);
					this.method_13372(iWorld, blockBox, random, 0.1F, 2, 2, o - 1);
					this.method_13372(iWorld, blockBox, random, 0.1F, 0, 2, o + 1);
					this.method_13372(iWorld, blockBox, random, 0.1F, 2, 2, o + 1);
					this.method_13372(iWorld, blockBox, random, 0.05F, 0, 2, o - 2);
					this.method_13372(iWorld, blockBox, random, 0.05F, 2, 2, o - 2);
					this.method_13372(iWorld, blockBox, random, 0.05F, 0, 2, o + 2);
					this.method_13372(iWorld, blockBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.method_11852(iWorld, blockBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (random.nextInt(100) == 0) {
						this.method_11852(iWorld, blockBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						int s = this.applyZTransform(1, q);
						BlockPos blockPos = new BlockPos(r, p, s);
						if (blockBox.contains(blockPos) && this.method_17657(iWorld, 1, 0, q, blockBox)) {
							this.hasSpawner = true;
							iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().method_16278(EntityType.CAVE_SPIDER);
							}
						}
					}
				}

				for (int t = 0; t <= 2; t++) {
					for (int u = 0; u <= m; u++) {
						int v = -1;
						BlockState blockState2 = this.method_9273(iWorld, t, -1, u, blockBox);
						if (blockState2.isAir() && this.method_17657(iWorld, t, -1, u, blockBox)) {
							int w = -1;
							this.method_56(iWorld, blockState, t, -1, u, blockBox);
						}
					}
				}

				if (this.hasRails) {
					BlockState blockState3 = Blocks.RAIL.getDefaultState().withProperty(RailBlock.field_18435, RailShape.NORTH_SOUTH);

					for (int x = 0; x <= m; x++) {
						BlockState blockState4 = this.method_9273(iWorld, 1, -1, x, blockBox);
						if (!blockState4.isAir()
							&& blockState4.isFullOpaque(iWorld, new BlockPos(this.applyXTransform(1, x), this.applyYTransform(-1), this.applyZTransform(1, x)))) {
							float f = this.method_17657(iWorld, 1, 0, x, blockBox) ? 0.7F : 0.9F;
							this.method_65(iWorld, blockBox, random, f, 1, 0, x, blockState3);
						}
					}
				}

				return true;
			}
		}

		private void method_13371(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, Random random) {
			if (this.method_17621(iWorld, blockBox, i, m, l, k)) {
				BlockState blockState = this.method_13374();
				BlockState blockState2 = this.method_13376();
				this.method_17653(iWorld, blockBox, i, j, k, i, l - 1, k, blockState2.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)), field_19406, false);
				this.method_17653(iWorld, blockBox, m, j, k, m, l - 1, k, blockState2.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)), field_19406, false);
				if (random.nextInt(4) == 0) {
					this.method_17653(iWorld, blockBox, i, l, k, i, l, k, blockState, field_19406, false);
					this.method_17653(iWorld, blockBox, m, l, k, m, l, k, blockState, field_19406, false);
				} else {
					this.method_17653(iWorld, blockBox, i, l, k, m, l, k, blockState, field_19406, false);
					this.method_65(iWorld, blockBox, random, 0.05F, i + 1, l, k - 1, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.NORTH));
					this.method_65(iWorld, blockBox, random, 0.05F, i + 1, l, k + 1, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.SOUTH));
				}
			}
		}

		private void method_13372(IWorld iWorld, BlockBox blockBox, Random random, float f, int i, int j, int k) {
			if (this.method_17657(iWorld, i, j, k, blockBox)) {
				this.method_65(iWorld, blockBox, random, f, i, j, k, Blocks.COBWEB.getDefaultState());
			}
		}
	}

	public static class MineshaftCrossing extends MineshaftPieces.class_3015 {
		private Direction orientation;
		private boolean twoFloors;

		public MineshaftCrossing() {
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("tf", this.twoFloors);
			structureNbt.putInt("D", this.orientation.getHorizontal());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.twoFloors = nbtCompound.getBoolean("tf");
			this.orientation = Direction.fromHorizontal(nbtCompound.getInt("D"));
		}

		public MineshaftCrossing(int i, Random random, BlockBox blockBox, @Nullable Direction direction, class_3867.class_3014 arg) {
			super(i, arg);
			this.orientation = direction;
			this.boundingBox = blockBox;
			this.twoFloors = blockBox.getBlockCountY() > 3;
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 3 - 1, z);
			if (random.nextInt(4) == 0) {
				blockBox.maxY += 4;
			}

			switch (orientation) {
				case NORTH:
				default:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.minZ = z - 4;
					break;
				case SOUTH:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.maxZ = z + 3 + 1;
					break;
				case WEST:
					blockBox.minX = x - 4;
					blockBox.minZ = z - 1;
					blockBox.maxZ = z + 3;
					break;
				case EAST:
					blockBox.maxX = x + 3 + 1;
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
				default:
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case SOUTH:
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case WEST:
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					break;
				case EAST:
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftPieces.method_13370(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i);
				}

				if (random.nextBoolean()) {
					MineshaftPieces.method_13370(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
					);
				}
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_17651(iWorld, blockBox)) {
				return false;
			} else {
				BlockState blockState = this.method_13374();
				if (this.twoFloors) {
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ,
						field_19406,
						field_19406,
						false
					);
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ - 1,
						field_19406,
						field_19406,
						false
					);
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						field_19406,
						field_19406,
						false
					);
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						field_19406,
						field_19406,
						false
					);
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY + 3,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3,
						this.boundingBox.maxZ - 1,
						field_19406,
						field_19406,
						false
					);
				} else {
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						field_19406,
						field_19406,
						false
					);
					this.method_17653(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						field_19406,
						field_19406,
						false
					);
				}

				this.method_17620(iWorld, blockBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_17620(iWorld, blockBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
				this.method_17620(iWorld, blockBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_17620(iWorld, blockBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

				for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
					for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
						if (this.method_9273(iWorld, i, this.boundingBox.minY - 1, j, blockBox).isAir() && this.method_17657(iWorld, i, this.boundingBox.minY - 1, j, blockBox)) {
							this.method_56(iWorld, blockState, i, this.boundingBox.minY - 1, j, blockBox);
						}
					}
				}

				return true;
			}
		}

		private void method_17620(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l) {
			if (!this.method_9273(iWorld, i, l + 1, k, blockBox).isAir()) {
				this.method_17653(iWorld, blockBox, i, j, k, i, l, k, this.method_13374(), field_19406, false);
			}
		}
	}

	public static class MineshaftRoom extends MineshaftPieces.class_3015 {
		private final List<BlockBox> entrances = Lists.newLinkedList();

		public MineshaftRoom() {
		}

		public MineshaftRoom(int i, Random random, int j, int k, class_3867.class_3014 arg) {
			super(i, arg);
			this.field_14868 = arg;
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

				MineshaftPieces.class_3015 lv = MineshaftPieces.method_13370(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
				);
				if (lv != null) {
					BlockBox blockBox = lv.getBoundingBox();
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

				MineshaftPieces.class_3015 lv2 = MineshaftPieces.method_13370(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
				);
				if (lv2 != null) {
					BlockBox blockBox2 = lv2.getBoundingBox();
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

				MineshaftPieces.class_3015 lv3 = MineshaftPieces.method_13370(
					start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i
				);
				if (lv3 != null) {
					BlockBox blockBox3 = lv3.getBoundingBox();
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

				StructurePiece structurePiece = MineshaftPieces.method_13370(
					start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i
				);
				if (structurePiece != null) {
					BlockBox blockBox4 = structurePiece.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, blockBox4.minY, blockBox4.minZ, this.boundingBox.maxX, blockBox4.maxY, blockBox4.maxZ));
				}

				k += 4;
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_17651(iWorld, blockBox)) {
				return false;
			} else {
				this.method_17653(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.minY,
					this.boundingBox.maxZ,
					Blocks.DIRT.getDefaultState(),
					field_19406,
					true
				);
				this.method_17653(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 1,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
					this.boundingBox.maxZ,
					field_19406,
					field_19406,
					false
				);

				for (BlockBox blockBox2 : this.entrances) {
					this.method_17653(
						iWorld, blockBox, blockBox2.minX, blockBox2.maxY - 2, blockBox2.minZ, blockBox2.maxX, blockBox2.maxY, blockBox2.maxZ, field_19406, field_19406, false
					);
				}

				this.method_17654(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 4,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.maxY,
					this.boundingBox.maxZ,
					field_19406,
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
			super.serialize(structureNbt);
			NbtList nbtList = new NbtList();

			for (BlockBox blockBox : this.entrances) {
				nbtList.add((NbtElement)blockBox.toNbt());
			}

			structureNbt.put("Entrances", nbtList);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			NbtList nbtList = nbtCompound.getList("Entrances", 11);

			for (int i = 0; i < nbtList.size(); i++) {
				this.entrances.add(new BlockBox(nbtList.getIntArray(i)));
			}
		}
	}

	public static class MineshaftStairs extends MineshaftPieces.class_3015 {
		public MineshaftStairs() {
		}

		public MineshaftStairs(int i, Random random, BlockBox blockBox, Direction direction, class_3867.class_3014 arg) {
			super(i, arg);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y - 5, z, x, y + 3 - 1, z);
			switch (orientation) {
				case NORTH:
				default:
					blockBox.maxX = x + 3 - 1;
					blockBox.minZ = z - 8;
					break;
				case SOUTH:
					blockBox.maxX = x + 3 - 1;
					blockBox.maxZ = z + 8;
					break;
				case WEST:
					blockBox.minX = x - 8;
					blockBox.maxZ = z + 3 - 1;
					break;
				case EAST:
					blockBox.maxX = x + 8;
					blockBox.maxZ = z + 3 - 1;
			}

			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
						break;
					case SOUTH:
						MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
						break;
					case WEST:
						MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
						break;
					case EAST:
						MineshaftPieces.method_13370(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
				}
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_17651(iWorld, blockBox)) {
				return false;
			} else {
				this.method_17653(iWorld, blockBox, 0, 5, 0, 2, 7, 1, field_19406, field_19406, false);
				this.method_17653(iWorld, blockBox, 0, 0, 7, 2, 2, 8, field_19406, field_19406, false);

				for (int i = 0; i < 5; i++) {
					this.method_17653(iWorld, blockBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, field_19406, field_19406, false);
				}

				return true;
			}
		}
	}

	abstract static class class_3015 extends StructurePiece {
		protected class_3867.class_3014 field_14868;

		public class_3015() {
		}

		public class_3015(int i, class_3867.class_3014 arg) {
			super(i);
			this.field_14868 = arg;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putInt("MST", this.field_14868.ordinal());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			this.field_14868 = class_3867.class_3014.method_13368(nbtCompound.getInt("MST"));
		}

		protected BlockState method_13374() {
			switch (this.field_14868) {
				case NORMAL:
				default:
					return Blocks.OAK_PLANKS.getDefaultState();
				case MESA:
					return Blocks.DARK_OAK_PLANKS.getDefaultState();
			}
		}

		protected BlockState method_13376() {
			switch (this.field_14868) {
				case NORMAL:
				default:
					return Blocks.OAK_FENCE.getDefaultState();
				case MESA:
					return Blocks.DARK_OAK_FENCE.getDefaultState();
			}
		}

		protected boolean method_17621(BlockView blockView, BlockBox blockBox, int i, int j, int k, int l) {
			for (int m = i; m <= j; m++) {
				if (this.method_9273(blockView, m, k + 1, l, blockBox).isAir()) {
					return false;
				}
			}

			return true;
		}
	}
}
