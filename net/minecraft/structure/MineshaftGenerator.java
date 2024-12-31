package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftGenerator {
	private static MineshaftGenerator.MineshaftPart method_14712(
		List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, MineshaftFeature.Type type
	) {
		int m = random.nextInt(100);
		if (m >= 80) {
			BlockBox blockBox = MineshaftGenerator.MineshaftCrossing.method_14717(list, random, i, j, k, direction);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCrossing(l, blockBox, direction, type);
			}
		} else if (m >= 70) {
			BlockBox blockBox2 = MineshaftGenerator.MineshaftStairs.method_14720(list, random, i, j, k, direction);
			if (blockBox2 != null) {
				return new MineshaftGenerator.MineshaftStairs(l, blockBox2, direction, type);
			}
		} else {
			BlockBox blockBox3 = MineshaftGenerator.MineshaftCorridor.method_14714(list, random, i, j, k, direction);
			if (blockBox3 != null) {
				return new MineshaftGenerator.MineshaftCorridor(l, random, blockBox3, direction, type);
			}
		}

		return null;
	}

	private static MineshaftGenerator.MineshaftPart method_14711(
		StructurePiece structurePiece, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (l > 8) {
			return null;
		} else if (Math.abs(i - structurePiece.getBoundingBox().minX) <= 80 && Math.abs(k - structurePiece.getBoundingBox().minZ) <= 80) {
			MineshaftFeature.Type type = ((MineshaftGenerator.MineshaftPart)structurePiece).mineshaftType;
			MineshaftGenerator.MineshaftPart mineshaftPart = method_14712(list, random, i, j, k, direction, l + 1, type);
			if (mineshaftPart != null) {
				list.add(mineshaftPart);
				mineshaftPart.method_14918(structurePiece, list, random);
			}

			return mineshaftPart;
		} else {
			return null;
		}
	}

	public static class MineshaftCorridor extends MineshaftGenerator.MineshaftPart {
		private final boolean hasRails;
		private final boolean hasCobwebs;
		private boolean hasSpawner;
		private final int length;

		public MineshaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, compoundTag);
			this.hasRails = compoundTag.getBoolean("hr");
			this.hasCobwebs = compoundTag.getBoolean("sc");
			this.hasSpawner = compoundTag.getBoolean("hps");
			this.length = compoundTag.getInt("Num");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("hr", this.hasRails);
			compoundTag.putBoolean("sc", this.hasCobwebs);
			compoundTag.putBoolean("hps", this.hasSpawner);
			compoundTag.putInt("Num", this.length);
		}

		public MineshaftCorridor(int i, Random random, BlockBox blockBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, i, type);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.getFacing().getAxis() == Direction.Axis.field_11051) {
				this.length = blockBox.getBlockCountZ() / 5;
			} else {
				this.length = blockBox.getBlockCountX() / 5;
			}
		}

		public static BlockBox method_14714(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			BlockBox blockBox = new BlockBox(i, j, k, i, j + 3 - 1, k);

			int l;
			for (l = random.nextInt(3) + 2; l > 0; l--) {
				int m = l * 5;
				switch (direction) {
					case field_11043:
					default:
						blockBox.maxX = i + 3 - 1;
						blockBox.minZ = k - (m - 1);
						break;
					case field_11035:
						blockBox.maxX = i + 3 - 1;
						blockBox.maxZ = k + m - 1;
						break;
					case field_11039:
						blockBox.minX = i - (m - 1);
						blockBox.maxZ = k + 3 - 1;
						break;
					case field_11034:
						blockBox.maxX = i + m - 1;
						blockBox.maxZ = k + 3 - 1;
				}

				if (StructurePiece.method_14932(list, blockBox) == null) {
					break;
				}
			}

			return l > 0 ? blockBox : null;
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.method_14923();
			int j = random.nextInt(4);
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case field_11043:
					default:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.field_11039, i
							);
						} else {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.field_11034, i
							);
						}
						break;
					case field_11035:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								structurePiece,
								list,
								random,
								this.boundingBox.minX - 1,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ - 3,
								Direction.field_11039,
								i
							);
						} else {
							MineshaftGenerator.method_14711(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX + 1,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ - 3,
								Direction.field_11034,
								i
							);
						}
						break;
					case field_11039:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.field_11043, i
							);
						} else {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.field_11035, i
							);
						}
						break;
					case field_11034:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX - 3,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.minZ - 1,
								Direction.field_11043,
								i
							);
						} else {
							MineshaftGenerator.method_14711(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX - 3,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ + 1,
								Direction.field_11035,
								i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.field_11043 && direction != Direction.field_11035) {
					for (int m = this.boundingBox.minX + 3; m + 3 <= this.boundingBox.maxX; m += 5) {
						int n = random.nextInt(5);
						if (n == 0) {
							MineshaftGenerator.method_14711(structurePiece, list, random, m, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i + 1);
						} else if (n == 1) {
							MineshaftGenerator.method_14711(structurePiece, list, random, m, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i + 1);
						}
					}
				} else {
					for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.field_11039, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.field_11034, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean addChest(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Identifier identifier) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
			if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).isAir() && !iWorld.getBlockState(blockPos.down()).isAir()) {
				BlockState blockState = Blocks.field_10167.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.field_12665 : RailShape.field_12674);
				this.addBlock(iWorld, blockState, i, j, k, blockBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					iWorld.getWorld(), (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F)
				);
				chestMinecartEntity.setLootTable(identifier, random.nextLong());
				iWorld.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, blockBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				BlockState blockState = this.method_16443();
				this.fillWithOutline(iWorld, blockBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
				this.fillWithOutlineUnderSealevel(iWorld, blockBox, random, 0.8F, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
				if (this.hasCobwebs) {
					this.fillWithOutlineUnderSealevel(iWorld, blockBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.field_10343.getDefaultState(), AIR, false, true);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.method_14713(iWorld, blockBox, 0, 0, o, 2, 2, random);
					this.method_14715(iWorld, blockBox, random, 0.1F, 0, 2, o - 1);
					this.method_14715(iWorld, blockBox, random, 0.1F, 2, 2, o - 1);
					this.method_14715(iWorld, blockBox, random, 0.1F, 0, 2, o + 1);
					this.method_14715(iWorld, blockBox, random, 0.1F, 2, 2, o + 1);
					this.method_14715(iWorld, blockBox, random, 0.05F, 0, 2, o - 2);
					this.method_14715(iWorld, blockBox, random, 0.05F, 2, 2, o - 2);
					this.method_14715(iWorld, blockBox, random, 0.05F, 0, 2, o + 2);
					this.method_14715(iWorld, blockBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.addChest(iWorld, blockBox, random, 2, 0, o - 1, LootTables.field_472);
					}

					if (random.nextInt(100) == 0) {
						this.addChest(iWorld, blockBox, random, 0, 0, o + 1, LootTables.field_472);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						int s = this.applyZTransform(1, q);
						BlockPos blockPos = new BlockPos(r, p, s);
						if (blockBox.contains(blockPos) && this.isUnderSeaLevel(iWorld, 1, 0, q, blockBox)) {
							this.hasSpawner = true;
							iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.field_6084);
							}
						}
					}
				}

				for (int t = 0; t <= 2; t++) {
					for (int u = 0; u <= m; u++) {
						int v = -1;
						BlockState blockState2 = this.getBlockAt(iWorld, t, -1, u, blockBox);
						if (blockState2.isAir() && this.isUnderSeaLevel(iWorld, t, -1, u, blockBox)) {
							int w = -1;
							this.addBlock(iWorld, blockState, t, -1, u, blockBox);
						}
					}
				}

				if (this.hasRails) {
					BlockState blockState3 = Blocks.field_10167.getDefaultState().with(RailBlock.SHAPE, RailShape.field_12665);

					for (int x = 0; x <= m; x++) {
						BlockState blockState4 = this.getBlockAt(iWorld, 1, -1, x, blockBox);
						if (!blockState4.isAir()
							&& blockState4.isFullOpaque(iWorld, new BlockPos(this.applyXTransform(1, x), this.applyYTransform(-1), this.applyZTransform(1, x)))) {
							float f = this.isUnderSeaLevel(iWorld, 1, 0, x, blockBox) ? 0.7F : 0.9F;
							this.addBlockWithRandomThreshold(iWorld, blockBox, random, f, 1, 0, x, blockState3);
						}
					}
				}

				return true;
			}
		}

		private void method_14713(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, Random random) {
			if (this.method_14719(iWorld, blockBox, i, m, l, k)) {
				BlockState blockState = this.method_16443();
				BlockState blockState2 = this.method_14718();
				this.fillWithOutline(iWorld, blockBox, i, j, k, i, l - 1, k, blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)), AIR, false);
				this.fillWithOutline(iWorld, blockBox, m, j, k, m, l - 1, k, blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)), AIR, false);
				if (random.nextInt(4) == 0) {
					this.fillWithOutline(iWorld, blockBox, i, l, k, i, l, k, blockState, AIR, false);
					this.fillWithOutline(iWorld, blockBox, m, l, k, m, l, k, blockState, AIR, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, i, l, k, m, l, k, blockState, AIR, false);
					this.addBlockWithRandomThreshold(
						iWorld, blockBox, random, 0.05F, i + 1, l, k - 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.field_11043)
					);
					this.addBlockWithRandomThreshold(
						iWorld, blockBox, random, 0.05F, i + 1, l, k + 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.field_11035)
					);
				}
			}
		}

		private void method_14715(IWorld iWorld, BlockBox blockBox, Random random, float f, int i, int j, int k) {
			if (this.isUnderSeaLevel(iWorld, i, j, k, blockBox)) {
				this.addBlockWithRandomThreshold(iWorld, blockBox, random, f, i, j, k, Blocks.field_10343.getDefaultState());
			}
		}
	}

	public static class MineshaftCrossing extends MineshaftGenerator.MineshaftPart {
		private final Direction direction;
		private final boolean twoFloors;

		public MineshaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_CROSSING, compoundTag);
			this.twoFloors = compoundTag.getBoolean("tf");
			this.direction = Direction.fromHorizontal(compoundTag.getInt("D"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("tf", this.twoFloors);
			compoundTag.putInt("D", this.direction.getHorizontal());
		}

		public MineshaftCrossing(int i, BlockBox blockBox, @Nullable Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CROSSING, i, type);
			this.direction = direction;
			this.boundingBox = blockBox;
			this.twoFloors = blockBox.getBlockCountY() > 3;
		}

		public static BlockBox method_14717(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			BlockBox blockBox = new BlockBox(i, j, k, i, j + 3 - 1, k);
			if (random.nextInt(4) == 0) {
				blockBox.maxY += 4;
			}

			switch (direction) {
				case field_11043:
				default:
					blockBox.minX = i - 1;
					blockBox.maxX = i + 3;
					blockBox.minZ = k - 4;
					break;
				case field_11035:
					blockBox.minX = i - 1;
					blockBox.maxX = i + 3;
					blockBox.maxZ = k + 3 + 1;
					break;
				case field_11039:
					blockBox.minX = i - 4;
					blockBox.minZ = k - 1;
					blockBox.maxZ = k + 3;
					break;
				case field_11034:
					blockBox.maxX = i + 3 + 1;
					blockBox.minZ = k - 1;
					blockBox.maxZ = k + 3;
			}

			return StructurePiece.method_14932(list, blockBox) != null ? null : blockBox;
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.method_14923();
			switch (this.direction) {
				case field_11043:
				default:
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
					break;
				case field_11035:
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
					break;
				case field_11039:
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					break;
				case field_11034:
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, blockBox)) {
				return false;
			} else {
				BlockState blockState = this.method_16443();
				if (this.twoFloors) {
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY + 3,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
				} else {
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						iWorld,
						blockBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
				}

				this.method_14716(iWorld, blockBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_14716(iWorld, blockBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
				this.method_14716(iWorld, blockBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_14716(iWorld, blockBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

				for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
					for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
						if (this.getBlockAt(iWorld, i, this.boundingBox.minY - 1, j, blockBox).isAir() && this.isUnderSeaLevel(iWorld, i, this.boundingBox.minY - 1, j, blockBox)
							)
						 {
							this.addBlock(iWorld, blockState, i, this.boundingBox.minY - 1, j, blockBox);
						}
					}
				}

				return true;
			}
		}

		private void method_14716(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l) {
			if (!this.getBlockAt(iWorld, i, l + 1, k, blockBox).isAir()) {
				this.fillWithOutline(iWorld, blockBox, i, j, k, i, l, k, this.method_16443(), AIR, false);
			}
		}
	}

	abstract static class MineshaftPart extends StructurePiece {
		protected MineshaftFeature.Type mineshaftType;

		public MineshaftPart(StructurePieceType structurePieceType, int i, MineshaftFeature.Type type) {
			super(structurePieceType, i);
			this.mineshaftType = type;
		}

		public MineshaftPart(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.mineshaftType = MineshaftFeature.Type.byIndex(compoundTag.getInt("MST"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			compoundTag.putInt("MST", this.mineshaftType.ordinal());
		}

		protected BlockState method_16443() {
			switch (this.mineshaftType) {
				case field_13692:
				default:
					return Blocks.field_10161.getDefaultState();
				case field_13691:
					return Blocks.field_10075.getDefaultState();
			}
		}

		protected BlockState method_14718() {
			switch (this.mineshaftType) {
				case field_13692:
				default:
					return Blocks.field_10620.getDefaultState();
				case field_13691:
					return Blocks.field_10132.getDefaultState();
			}
		}

		protected boolean method_14719(BlockView blockView, BlockBox blockBox, int i, int j, int k, int l) {
			for (int m = i; m <= j; m++) {
				if (this.getBlockAt(blockView, m, k + 1, l, blockBox).isAir()) {
					return false;
				}
			}

			return true;
		}
	}

	public static class MineshaftRoom extends MineshaftGenerator.MineshaftPart {
		private final List<BlockBox> entrances = Lists.newLinkedList();

		public MineshaftRoom(int i, Random random, int j, int k, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_ROOM, i, type);
			this.mineshaftType = type;
			this.boundingBox = new BlockBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
		}

		public MineshaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_ROOM, compoundTag);
			ListTag listTag = compoundTag.getList("Entrances", 11);

			for (int i = 0; i < listTag.size(); i++) {
				this.entrances.add(new BlockBox(listTag.getIntArray(i)));
			}
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.method_14923();
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

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.method_14711(
					structurePiece,
					list,
					random,
					this.boundingBox.minX + k,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ - 1,
					Direction.field_11043,
					i
				);
				if (mineshaftPart != null) {
					BlockBox blockBox = mineshaftPart.getBoundingBox();
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

				MineshaftGenerator.MineshaftPart mineshaftPart2 = MineshaftGenerator.method_14711(
					structurePiece,
					list,
					random,
					this.boundingBox.minX + k,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.maxZ + 1,
					Direction.field_11035,
					i
				);
				if (mineshaftPart2 != null) {
					BlockBox blockBox2 = mineshaftPart2.getBoundingBox();
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

				MineshaftGenerator.MineshaftPart mineshaftPart3 = MineshaftGenerator.method_14711(
					structurePiece,
					list,
					random,
					this.boundingBox.minX - 1,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ + k,
					Direction.field_11039,
					i
				);
				if (mineshaftPart3 != null) {
					BlockBox blockBox3 = mineshaftPart3.getBoundingBox();
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

				StructurePiece structurePiece2 = MineshaftGenerator.method_14711(
					structurePiece,
					list,
					random,
					this.boundingBox.maxX + 1,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ + k,
					Direction.field_11034,
					i
				);
				if (structurePiece2 != null) {
					BlockBox blockBox4 = structurePiece2.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, blockBox4.minY, blockBox4.minZ, this.boundingBox.maxX, blockBox4.maxY, blockBox4.maxZ));
				}

				k += 4;
			}
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, blockBox)) {
				return false;
			} else {
				this.fillWithOutline(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.minY,
					this.boundingBox.maxZ,
					Blocks.field_10566.getDefaultState(),
					AIR,
					true
				);
				this.fillWithOutline(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 1,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
					this.boundingBox.maxZ,
					AIR,
					AIR,
					false
				);

				for (BlockBox blockBox2 : this.entrances) {
					this.fillWithOutline(iWorld, blockBox, blockBox2.minX, blockBox2.maxY - 2, blockBox2.minZ, blockBox2.maxX, blockBox2.maxY, blockBox2.maxZ, AIR, AIR, false);
				}

				this.method_14919(
					iWorld,
					blockBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 4,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.maxY,
					this.boundingBox.maxZ,
					AIR,
					false
				);
				return true;
			}
		}

		@Override
		public void translate(int i, int j, int k) {
			super.translate(i, j, k);

			for (BlockBox blockBox : this.entrances) {
				blockBox.offset(i, j, k);
			}
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			ListTag listTag = new ListTag();

			for (BlockBox blockBox : this.entrances) {
				listTag.add(blockBox.toNbt());
			}

			compoundTag.put("Entrances", listTag);
		}
	}

	public static class MineshaftStairs extends MineshaftGenerator.MineshaftPart {
		public MineshaftStairs(int i, BlockBox blockBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_STAIRS, i, type);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public MineshaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_STAIRS, compoundTag);
		}

		public static BlockBox method_14720(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			BlockBox blockBox = new BlockBox(i, j - 5, k, i, j + 3 - 1, k);
			switch (direction) {
				case field_11043:
				default:
					blockBox.maxX = i + 3 - 1;
					blockBox.minZ = k - 8;
					break;
				case field_11035:
					blockBox.maxX = i + 3 - 1;
					blockBox.maxZ = k + 8;
					break;
				case field_11039:
					blockBox.minX = i - 8;
					blockBox.maxZ = k + 3 - 1;
					break;
				case field_11034:
					blockBox.maxX = i + 8;
					blockBox.maxZ = k + 3 - 1;
			}

			return StructurePiece.method_14932(list, blockBox) != null ? null : blockBox;
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.method_14923();
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case field_11043:
					default:
						MineshaftGenerator.method_14711(
							structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
						);
						break;
					case field_11035:
						MineshaftGenerator.method_14711(
							structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
						);
						break;
					case field_11039:
						MineshaftGenerator.method_14711(
							structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.field_11039, i
						);
						break;
					case field_11034:
						MineshaftGenerator.method_14711(
							structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.field_11034, i
						);
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, blockBox)) {
				return false;
			} else {
				this.fillWithOutline(iWorld, blockBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
				this.fillWithOutline(iWorld, blockBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(iWorld, blockBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
				}

				return true;
			}
		}
	}
}
