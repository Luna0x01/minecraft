package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineshaftGenerator {
	static final Logger LOGGER = LogManager.getLogger();
	private static final int field_31551 = 3;
	private static final int field_31552 = 3;
	private static final int field_31553 = 5;
	private static final int field_31554 = 20;
	private static final int field_31555 = 50;
	private static final int field_31556 = 8;

	private static MineshaftGenerator.MineshaftPart pickPiece(
		StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, @Nullable Direction orientation, int chainLength, MineshaftFeature.Type type
	) {
		int i = random.nextInt(100);
		if (i >= 80) {
			BlockBox blockBox = MineshaftGenerator.MineshaftCrossing.getBoundingBox(structurePiecesHolder, random, x, y, z, orientation);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCrossing(chainLength, blockBox, orientation, type);
			}
		} else if (i >= 70) {
			BlockBox blockBox2 = MineshaftGenerator.MineshaftStairs.getBoundingBox(structurePiecesHolder, random, x, y, z, orientation);
			if (blockBox2 != null) {
				return new MineshaftGenerator.MineshaftStairs(chainLength, blockBox2, orientation, type);
			}
		} else {
			BlockBox blockBox3 = MineshaftGenerator.MineshaftCorridor.getBoundingBox(structurePiecesHolder, random, x, y, z, orientation);
			if (blockBox3 != null) {
				return new MineshaftGenerator.MineshaftCorridor(chainLength, random, blockBox3, orientation, type);
			}
		}

		return null;
	}

	static MineshaftGenerator.MineshaftPart pieceGenerator(
		StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (chainLength > 8) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().getMinX()) <= 80 && Math.abs(z - start.getBoundingBox().getMinZ()) <= 80) {
			MineshaftFeature.Type type = ((MineshaftGenerator.MineshaftPart)start).mineshaftType;
			MineshaftGenerator.MineshaftPart mineshaftPart = pickPiece(structurePiecesHolder, random, x, y, z, orientation, chainLength + 1, type);
			if (mineshaftPart != null) {
				structurePiecesHolder.addPiece(mineshaftPart);
				mineshaftPart.fillOpenings(start, structurePiecesHolder, random);
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

		public MineshaftCorridor(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, nbt);
			this.hasRails = nbt.getBoolean("hr");
			this.hasCobwebs = nbt.getBoolean("sc");
			this.hasSpawner = nbt.getBoolean("hps");
			this.length = nbt.getInt("Num");
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putBoolean("hr", this.hasRails);
			nbt.putBoolean("sc", this.hasCobwebs);
			nbt.putBoolean("hps", this.hasSpawner);
			nbt.putInt("Num", this.length);
		}

		public MineshaftCorridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, chainLength, type, boundingBox);
			this.setOrientation(orientation);
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.length = boundingBox.getBlockCountZ() / 5;
			} else {
				this.length = boundingBox.getBlockCountX() / 5;
			}
		}

		@Nullable
		public static BlockBox getBoundingBox(StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation) {
			for (int i = random.nextInt(3) + 2; i > 0; i--) {
				int j = i * 5;

				BlockBox blockBox4 = switch (orientation) {
					default -> new BlockBox(0, 0, -(j - 1), 2, 2, 0);
					case SOUTH -> new BlockBox(0, 0, 0, 2, 2, j - 1);
					case WEST -> new BlockBox(-(j - 1), 0, 0, 0, 2, 2);
					case EAST -> new BlockBox(0, 0, 0, j - 1, 2, 2);
				};
				blockBox4.move(x, y, z);
				if (structurePiecesHolder.getIntersecting(blockBox4) == null) {
					return blockBox4;
				}
			}

			return null;
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			int i = this.getChainLength();
			int j = random.nextInt(4);
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX(),
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ() - 1,
								direction,
								i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX() - 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ(),
								Direction.WEST,
								i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMaxX() + 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ(),
								Direction.EAST,
								i
							);
						}
						break;
					case SOUTH:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX(),
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMaxZ() + 1,
								direction,
								i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX() - 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMaxZ() - 3,
								Direction.WEST,
								i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMaxX() + 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMaxZ() - 3,
								Direction.EAST,
								i
							);
						}
						break;
					case WEST:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX() - 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ(),
								direction,
								i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX(),
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ() - 1,
								Direction.NORTH,
								i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMinX(),
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMaxZ() + 1,
								Direction.SOUTH,
								i
							);
						}
						break;
					case EAST:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMaxX() + 1,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ(),
								direction,
								i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMaxX() - 3,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMinZ() - 1,
								Direction.NORTH,
								i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start,
								structurePiecesHolder,
								random,
								this.boundingBox.getMaxX() - 3,
								this.boundingBox.getMinY() - 1 + random.nextInt(3),
								this.boundingBox.getMaxZ() + 1,
								Direction.SOUTH,
								i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					for (int m = this.boundingBox.getMinX() + 3; m + 3 <= this.boundingBox.getMaxX(); m += 5) {
						int n = random.nextInt(5);
						if (n == 0) {
							MineshaftGenerator.pieceGenerator(
								start, structurePiecesHolder, random, m, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i + 1
							);
						} else if (n == 1) {
							MineshaftGenerator.pieceGenerator(
								start, structurePiecesHolder, random, m, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i + 1
							);
						}
					}
				} else {
					for (int k = this.boundingBox.getMinZ() + 3; k + 3 <= this.boundingBox.getMaxZ(); k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.pieceGenerator(
								start, structurePiecesHolder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), k, Direction.WEST, i + 1
							);
						} else if (l == 1) {
							MineshaftGenerator.pieceGenerator(
								start, structurePiecesHolder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), k, Direction.EAST, i + 1
							);
						}
					}
				}
			}
		}

		@Override
		protected boolean addChest(StructureWorldAccess world, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
			BlockPos blockPos = this.offsetPos(x, y, z);
			if (boundingBox.contains(blockPos) && world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos.down()).isAir()) {
				BlockState blockState = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
				this.addBlock(world, blockState, x, y, z, boundingBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					world.toServerWorld(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5
				);
				chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
				world.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				BlockState blockState = this.mineshaftType.getPlanks();
				this.fillWithOutline(world, boundingBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
				this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.8F, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
				if (this.hasCobwebs) {
					this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.generateSupports(world, boundingBox, 0, 0, o, 2, 2, random);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 0, 2, o - 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 2, 2, o - 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 0, 2, o + 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 2, 2, o + 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 0, 2, o - 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 2, 2, o - 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 0, 2, o + 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.addChest(world, boundingBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (random.nextInt(100) == 0) {
						this.addChest(world, boundingBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = 1;
						int q = o - 1 + random.nextInt(3);
						BlockPos blockPos = this.offsetPos(1, 0, q);
						if (boundingBox.contains(blockPos) && this.isUnderSeaLevel(world, 1, 0, q, boundingBox)) {
							this.hasSpawner = true;
							world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.CAVE_SPIDER);
							}
						}
					}
				}

				for (int r = 0; r <= 2; r++) {
					for (int s = 0; s <= m; s++) {
						this.method_33880(world, boundingBox, blockState, r, -1, s);
					}
				}

				int t = 2;
				this.fillSupportBeam(world, boundingBox, 0, -1, 2);
				if (this.length > 1) {
					int u = m - 2;
					this.fillSupportBeam(world, boundingBox, 0, -1, u);
				}

				if (this.hasRails) {
					BlockState blockState2 = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

					for (int v = 0; v <= m; v++) {
						BlockState blockState3 = this.getBlockAt(world, 1, -1, v, boundingBox);
						if (!blockState3.isAir() && blockState3.isOpaqueFullCube(world, this.offsetPos(1, -1, v))) {
							float f = this.isUnderSeaLevel(world, 1, 0, v, boundingBox) ? 0.7F : 0.9F;
							this.addBlockWithRandomThreshold(world, boundingBox, random, f, 1, 0, v, blockState2);
						}
					}
				}

				return true;
			}
		}

		private void fillSupportBeam(StructureWorldAccess world, BlockBox box, int x, int y, int z) {
			BlockState blockState = this.mineshaftType.getLog();
			BlockState blockState2 = this.mineshaftType.getPlanks();
			if (this.getBlockAt(world, x, y, z, box).isOf(blockState2.getBlock())) {
				this.method_33879(world, blockState, x, y, z, box);
			}

			if (this.getBlockAt(world, x + 2, y, z, box).isOf(blockState2.getBlock())) {
				this.method_33879(world, blockState, x + 2, y, z, box);
			}
		}

		@Override
		protected void fillDownwards(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
			BlockPos.Mutable mutable = this.offsetPos(x, y, z);
			if (box.contains(mutable)) {
				int i = mutable.getY();

				while (this.canReplace(world.getBlockState(mutable)) && mutable.getY() > world.getBottomY() + 1) {
					mutable.move(Direction.DOWN);
				}

				if (this.isNotRailOrLava(world.getBlockState(mutable))) {
					while (mutable.getY() < i) {
						mutable.move(Direction.UP);
						world.setBlockState(mutable, state, 2);
					}
				}
			}
		}

		protected void method_33879(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
			BlockPos.Mutable mutable = this.offsetPos(x, y, z);
			if (box.contains(mutable)) {
				int i = mutable.getY();
				int j = 1;
				boolean bl = true;

				for (boolean bl2 = true; bl || bl2; j++) {
					if (bl) {
						mutable.setY(i - j);
						BlockState blockState = world.getBlockState(mutable);
						boolean bl3 = this.canReplace(blockState) && !blockState.isOf(Blocks.LAVA);
						if (!bl3 && this.isNotRailOrLava(blockState)) {
							fillColumn(world, state, mutable, i - j + 1, i);
							return;
						}

						bl = j <= 20 && bl3 && mutable.getY() > world.getBottomY() + 1;
					}

					if (bl2) {
						mutable.setY(i + j);
						BlockState blockState2 = world.getBlockState(mutable);
						boolean bl4 = this.canReplace(blockState2);
						if (!bl4 && this.sideCoversSmallSquare(world, mutable, blockState2)) {
							world.setBlockState(mutable.setY(i + 1), this.mineshaftType.getFence(), 2);
							fillColumn(world, Blocks.CHAIN.getDefaultState(), mutable, i + 2, i + j);
							return;
						}

						bl2 = j <= 50 && bl4 && mutable.getY() < world.getTopY() - 1;
					}
				}
			}
		}

		private static void fillColumn(StructureWorldAccess world, BlockState state, BlockPos.Mutable pos, int startY, int endY) {
			for (int i = startY; i < endY; i++) {
				world.setBlockState(pos.setY(i), state, 2);
			}
		}

		private boolean isNotRailOrLava(BlockState state) {
			return !state.isOf(Blocks.RAIL) && !state.isOf(Blocks.LAVA);
		}

		private boolean sideCoversSmallSquare(WorldView world, BlockPos pos, BlockState state) {
			return Block.sideCoversSmallSquare(world, pos, Direction.DOWN) && !(state.getBlock() instanceof FallingBlock);
		}

		private void generateSupports(StructureWorldAccess world, BlockBox boundingBox, int minX, int minY, int z, int maxY, int maxX, Random random) {
			if (this.isSolidCeiling(world, boundingBox, minX, maxX, maxY, z)) {
				BlockState blockState = this.mineshaftType.getPlanks();
				BlockState blockState2 = this.mineshaftType.getFence();
				this.fillWithOutline(world, boundingBox, minX, minY, z, minX, maxY - 1, z, blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)), AIR, false);
				this.fillWithOutline(world, boundingBox, maxX, minY, z, maxX, maxY - 1, z, blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)), AIR, false);
				if (random.nextInt(4) == 0) {
					this.fillWithOutline(world, boundingBox, minX, maxY, z, minX, maxY, z, blockState, AIR, false);
					this.fillWithOutline(world, boundingBox, maxX, maxY, z, maxX, maxY, z, blockState, AIR, false);
				} else {
					this.fillWithOutline(world, boundingBox, minX, maxY, z, maxX, maxY, z, blockState, AIR, false);
					this.addBlockWithRandomThreshold(
						world, boundingBox, random, 0.05F, minX + 1, maxY, z - 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH)
					);
					this.addBlockWithRandomThreshold(
						world, boundingBox, random, 0.05F, minX + 1, maxY, z + 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH)
					);
				}
			}
		}

		private void addCobwebsUnderground(StructureWorldAccess world, BlockBox box, Random random, float threshold, int x, int y, int z) {
			if (this.isUnderSeaLevel(world, x, y, z, box) && random.nextFloat() < threshold && this.method_36422(world, box, x, y, z, 2)) {
				this.addBlock(world, Blocks.COBWEB.getDefaultState(), x, y, z, box);
			}
		}

		private boolean method_36422(StructureWorldAccess world, BlockBox box, int x, int y, int z, int count) {
			BlockPos.Mutable mutable = this.offsetPos(x, y, z);
			int i = 0;

			for (Direction direction : Direction.values()) {
				mutable.move(direction);
				if (box.contains(mutable) && world.getBlockState(mutable).isSideSolidFullSquare(world, mutable, direction.getOpposite())) {
					if (++i >= count) {
						return true;
					}
				}

				mutable.move(direction.getOpposite());
			}

			return false;
		}
	}

	public static class MineshaftCrossing extends MineshaftGenerator.MineshaftPart {
		private final Direction direction;
		private final boolean twoFloors;

		public MineshaftCrossing(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.MINESHAFT_CROSSING, nbt);
			this.twoFloors = nbt.getBoolean("tf");
			this.direction = Direction.fromHorizontal(nbt.getInt("D"));
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putBoolean("tf", this.twoFloors);
			nbt.putInt("D", this.direction.getHorizontal());
		}

		public MineshaftCrossing(int chainLength, BlockBox boundingBox, @Nullable Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CROSSING, chainLength, type, boundingBox);
			this.direction = orientation;
			this.twoFloors = boundingBox.getBlockCountY() > 3;
		}

		@Nullable
		public static BlockBox getBoundingBox(StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation) {
			int i;
			if (random.nextInt(4) == 0) {
				i = 6;
			} else {
				i = 2;
			}
			BlockBox blockBox4 = switch (orientation) {
				default -> new BlockBox(-1, 0, -4, 3, i, 0);
				case SOUTH -> new BlockBox(-1, 0, 0, 3, i, 4);
				case WEST -> new BlockBox(-4, 0, -1, 0, i, 3);
				case EAST -> new BlockBox(0, 0, -1, 4, i, 3);
			};
			blockBox4.move(x, y, z);
			return structurePiecesHolder.getIntersecting(blockBox4) != null ? null : blockBox4;
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			int i = this.getChainLength();
			switch (this.direction) {
				case NORTH:
				default:
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i
					);
					break;
				case SOUTH:
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i
					);
					break;
				case WEST:
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i
					);
					break;
				case EAST:
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.pieceGenerator(
						start, structurePiecesHolder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i
					);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start,
						structurePiecesHolder,
						random,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMinY() + 3 + 1,
						this.boundingBox.getMinZ() - 1,
						Direction.NORTH,
						i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start,
						structurePiecesHolder,
						random,
						this.boundingBox.getMinX() - 1,
						this.boundingBox.getMinY() + 3 + 1,
						this.boundingBox.getMinZ() + 1,
						Direction.WEST,
						i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start,
						structurePiecesHolder,
						random,
						this.boundingBox.getMaxX() + 1,
						this.boundingBox.getMinY() + 3 + 1,
						this.boundingBox.getMinZ() + 1,
						Direction.EAST,
						i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start,
						structurePiecesHolder,
						random,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMinY() + 3 + 1,
						this.boundingBox.getMaxZ() + 1,
						Direction.SOUTH,
						i
					);
				}
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				BlockState blockState = this.mineshaftType.getPlanks();
				if (this.twoFloors) {
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMinY(),
						this.boundingBox.getMinZ(),
						this.boundingBox.getMaxX() - 1,
						this.boundingBox.getMinY() + 3 - 1,
						this.boundingBox.getMaxZ(),
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX(),
						this.boundingBox.getMinY(),
						this.boundingBox.getMinZ() + 1,
						this.boundingBox.getMaxX(),
						this.boundingBox.getMinY() + 3 - 1,
						this.boundingBox.getMaxZ() - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMaxY() - 2,
						this.boundingBox.getMinZ(),
						this.boundingBox.getMaxX() - 1,
						this.boundingBox.getMaxY(),
						this.boundingBox.getMaxZ(),
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX(),
						this.boundingBox.getMaxY() - 2,
						this.boundingBox.getMinZ() + 1,
						this.boundingBox.getMaxX(),
						this.boundingBox.getMaxY(),
						this.boundingBox.getMaxZ() - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMinY() + 3,
						this.boundingBox.getMinZ() + 1,
						this.boundingBox.getMaxX() - 1,
						this.boundingBox.getMinY() + 3,
						this.boundingBox.getMaxZ() - 1,
						AIR,
						AIR,
						false
					);
				} else {
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX() + 1,
						this.boundingBox.getMinY(),
						this.boundingBox.getMinZ(),
						this.boundingBox.getMaxX() - 1,
						this.boundingBox.getMaxY(),
						this.boundingBox.getMaxZ(),
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						world,
						boundingBox,
						this.boundingBox.getMinX(),
						this.boundingBox.getMinY(),
						this.boundingBox.getMinZ() + 1,
						this.boundingBox.getMaxX(),
						this.boundingBox.getMaxY(),
						this.boundingBox.getMaxZ() - 1,
						AIR,
						AIR,
						false
					);
				}

				this.generateCrossingPillar(
					world, boundingBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY()
				);
				this.generateCrossingPillar(
					world, boundingBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY()
				);
				this.generateCrossingPillar(
					world, boundingBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY()
				);
				this.generateCrossingPillar(
					world, boundingBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY()
				);
				int i = this.boundingBox.getMinY() - 1;

				for (int j = this.boundingBox.getMinX(); j <= this.boundingBox.getMaxX(); j++) {
					for (int k = this.boundingBox.getMinZ(); k <= this.boundingBox.getMaxZ(); k++) {
						this.method_33880(world, boundingBox, blockState, j, i, k);
					}
				}

				return true;
			}
		}

		private void generateCrossingPillar(StructureWorldAccess world, BlockBox boundingBox, int x, int minY, int z, int maxY) {
			if (!this.getBlockAt(world, x, maxY + 1, z, boundingBox).isAir()) {
				this.fillWithOutline(world, boundingBox, x, minY, z, x, maxY, z, this.mineshaftType.getPlanks(), AIR, false);
			}
		}
	}

	abstract static class MineshaftPart extends StructurePiece {
		protected MineshaftFeature.Type mineshaftType;

		public MineshaftPart(StructurePieceType structurePieceType, int chainLength, MineshaftFeature.Type type, BlockBox box) {
			super(structurePieceType, chainLength, box);
			this.mineshaftType = type;
		}

		public MineshaftPart(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
			this.mineshaftType = MineshaftFeature.Type.byIndex(nbtCompound.getInt("MST"));
		}

		@Override
		protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
			BlockState blockState = this.getBlockAt(world, x, y, z, box);
			return !blockState.isOf(this.mineshaftType.getPlanks().getBlock())
				&& !blockState.isOf(this.mineshaftType.getLog().getBlock())
				&& !blockState.isOf(this.mineshaftType.getFence().getBlock())
				&& !blockState.isOf(Blocks.CHAIN);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			nbt.putInt("MST", this.mineshaftType.ordinal());
		}

		protected boolean isSolidCeiling(BlockView world, BlockBox boundingBox, int minX, int maxX, int y, int z) {
			for (int i = minX; i <= maxX; i++) {
				if (this.getBlockAt(world, i, y + 1, z, boundingBox).isAir()) {
					return false;
				}
			}

			return true;
		}

		protected boolean method_33999(BlockView world, BlockBox box) {
			int i = Math.max(this.boundingBox.getMinX() - 1, box.getMinX());
			int j = Math.max(this.boundingBox.getMinY() - 1, box.getMinY());
			int k = Math.max(this.boundingBox.getMinZ() - 1, box.getMinZ());
			int l = Math.min(this.boundingBox.getMaxX() + 1, box.getMaxX());
			int m = Math.min(this.boundingBox.getMaxY() + 1, box.getMaxY());
			int n = Math.min(this.boundingBox.getMaxZ() + 1, box.getMaxZ());
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (world.getBlockState(mutable.set(o, j, p)).getMaterial().isLiquid()) {
						return true;
					}

					if (world.getBlockState(mutable.set(o, m, p)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			for (int q = i; q <= l; q++) {
				for (int r = j; r <= m; r++) {
					if (world.getBlockState(mutable.set(q, r, k)).getMaterial().isLiquid()) {
						return true;
					}

					if (world.getBlockState(mutable.set(q, r, n)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			for (int s = k; s <= n; s++) {
				for (int t = j; t <= m; t++) {
					if (world.getBlockState(mutable.set(i, t, s)).getMaterial().isLiquid()) {
						return true;
					}

					if (world.getBlockState(mutable.set(l, t, s)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			return false;
		}

		protected void method_33880(StructureWorldAccess world, BlockBox box, BlockState state, int x, int y, int z) {
			if (this.isUnderSeaLevel(world, x, y, z, box)) {
				BlockPos blockPos = this.offsetPos(x, y, z);
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.isAir() || blockState.isOf(Blocks.CHAIN)) {
					world.setBlockState(blockPos, state, 2);
				}
			}
		}
	}

	public static class MineshaftRoom extends MineshaftGenerator.MineshaftPart {
		private final List<BlockBox> entrances = Lists.newLinkedList();

		public MineshaftRoom(int chainLength, Random random, int x, int z, MineshaftFeature.Type type) {
			super(
				StructurePieceType.MINESHAFT_ROOM, chainLength, type, new BlockBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6))
			);
			this.mineshaftType = type;
		}

		public MineshaftRoom(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.MINESHAFT_ROOM, nbt);
			BlockBox.CODEC
				.listOf()
				.parse(NbtOps.INSTANCE, nbt.getList("Entrances", 11))
				.resultOrPartial(MineshaftGenerator.LOGGER::error)
				.ifPresent(this.entrances::addAll);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
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

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.pieceGenerator(
					start,
					structurePiecesHolder,
					random,
					this.boundingBox.getMinX() + k,
					this.boundingBox.getMinY() + random.nextInt(j) + 1,
					this.boundingBox.getMinZ() - 1,
					Direction.NORTH,
					i
				);
				if (mineshaftPart != null) {
					BlockBox blockBox = mineshaftPart.getBoundingBox();
					this.entrances
						.add(
							new BlockBox(blockBox.getMinX(), blockBox.getMinY(), this.boundingBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), this.boundingBox.getMinZ() + 1)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountX()) {
				k += random.nextInt(this.boundingBox.getBlockCountX());
				if (k + 3 > this.boundingBox.getBlockCountX()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart2 = MineshaftGenerator.pieceGenerator(
					start,
					structurePiecesHolder,
					random,
					this.boundingBox.getMinX() + k,
					this.boundingBox.getMinY() + random.nextInt(j) + 1,
					this.boundingBox.getMaxZ() + 1,
					Direction.SOUTH,
					i
				);
				if (mineshaftPart2 != null) {
					BlockBox blockBox2 = mineshaftPart2.getBoundingBox();
					this.entrances
						.add(
							new BlockBox(
								blockBox2.getMinX(), blockBox2.getMinY(), this.boundingBox.getMaxZ() - 1, blockBox2.getMaxX(), blockBox2.getMaxY(), this.boundingBox.getMaxZ()
							)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart3 = MineshaftGenerator.pieceGenerator(
					start,
					structurePiecesHolder,
					random,
					this.boundingBox.getMinX() - 1,
					this.boundingBox.getMinY() + random.nextInt(j) + 1,
					this.boundingBox.getMinZ() + k,
					Direction.WEST,
					i
				);
				if (mineshaftPart3 != null) {
					BlockBox blockBox3 = mineshaftPart3.getBoundingBox();
					this.entrances
						.add(
							new BlockBox(
								this.boundingBox.getMinX(), blockBox3.getMinY(), blockBox3.getMinZ(), this.boundingBox.getMinX() + 1, blockBox3.getMaxY(), blockBox3.getMaxZ()
							)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				StructurePiece structurePiece = MineshaftGenerator.pieceGenerator(
					start,
					structurePiecesHolder,
					random,
					this.boundingBox.getMaxX() + 1,
					this.boundingBox.getMinY() + random.nextInt(j) + 1,
					this.boundingBox.getMinZ() + k,
					Direction.EAST,
					i
				);
				if (structurePiece != null) {
					BlockBox blockBox4 = structurePiece.getBoundingBox();
					this.entrances
						.add(
							new BlockBox(
								this.boundingBox.getMaxX() - 1, blockBox4.getMinY(), blockBox4.getMinZ(), this.boundingBox.getMaxX(), blockBox4.getMaxY(), blockBox4.getMaxZ()
							)
						);
				}

				k += 4;
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.getMinX(),
					this.boundingBox.getMinY(),
					this.boundingBox.getMinZ(),
					this.boundingBox.getMaxX(),
					this.boundingBox.getMinY(),
					this.boundingBox.getMaxZ(),
					Blocks.DIRT.getDefaultState(),
					AIR,
					true
				);
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.getMinX(),
					this.boundingBox.getMinY() + 1,
					this.boundingBox.getMinZ(),
					this.boundingBox.getMaxX(),
					Math.min(this.boundingBox.getMinY() + 3, this.boundingBox.getMaxY()),
					this.boundingBox.getMaxZ(),
					AIR,
					AIR,
					false
				);

				for (BlockBox blockBox : this.entrances) {
					this.fillWithOutline(
						world,
						boundingBox,
						blockBox.getMinX(),
						blockBox.getMaxY() - 2,
						blockBox.getMinZ(),
						blockBox.getMaxX(),
						blockBox.getMaxY(),
						blockBox.getMaxZ(),
						AIR,
						AIR,
						false
					);
				}

				this.fillHalfEllipsoid(
					world,
					boundingBox,
					this.boundingBox.getMinX(),
					this.boundingBox.getMinY() + 4,
					this.boundingBox.getMinZ(),
					this.boundingBox.getMaxX(),
					this.boundingBox.getMaxY(),
					this.boundingBox.getMaxZ(),
					AIR,
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
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			BlockBox.CODEC
				.listOf()
				.encodeStart(NbtOps.INSTANCE, this.entrances)
				.resultOrPartial(MineshaftGenerator.LOGGER::error)
				.ifPresent(nbtElement -> nbt.put("Entrances", nbtElement));
		}
	}

	public static class MineshaftStairs extends MineshaftGenerator.MineshaftPart {
		public MineshaftStairs(int chainLength, BlockBox boundingBox, Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_STAIRS, chainLength, type, boundingBox);
			this.setOrientation(orientation);
		}

		public MineshaftStairs(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.MINESHAFT_STAIRS, nbt);
		}

		@Nullable
		public static BlockBox getBoundingBox(StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox4 = switch (orientation) {
				default -> new BlockBox(0, -5, -8, 2, 2, 0);
				case SOUTH -> new BlockBox(0, -5, 0, 2, 2, 8);
				case WEST -> new BlockBox(-8, -5, 0, 0, 2, 2);
				case EAST -> new BlockBox(0, -5, 0, 8, 2, 2);
			};
			blockBox4.move(x, y, z);
			return structurePiecesHolder.getIntersecting(blockBox4) != null ? null : blockBox4;
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			int i = this.getChainLength();
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						MineshaftGenerator.pieceGenerator(
							start, structurePiecesHolder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i
						);
						break;
					case SOUTH:
						MineshaftGenerator.pieceGenerator(
							start, structurePiecesHolder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i
						);
						break;
					case WEST:
						MineshaftGenerator.pieceGenerator(
							start, structurePiecesHolder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.WEST, i
						);
						break;
					case EAST:
						MineshaftGenerator.pieceGenerator(
							start, structurePiecesHolder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.EAST, i
						);
				}
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(world, boundingBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
				this.fillWithOutline(world, boundingBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(world, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
				}

				return true;
			}
		}
	}
}
