package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3706;
import net.minecraft.class_3735;
import net.minecraft.class_3804;
import net.minecraft.class_3911;
import net.minecraft.class_3998;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

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
				pieces.add(structurePiece);
				start.field_6246.add(structurePiece);
				return structurePiece;
			} else {
				return null;
			}
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
				pieces.add(structurePiece);
				start.field_6247.add(structurePiece);
				return structurePiece;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	abstract static class AbstractPiece extends StructurePiece {
		protected int hPos = -1;
		private int villagers;
		protected VillagePieces.class_3996 field_19427;
		protected boolean field_14872;

		public AbstractPiece() {
		}

		protected AbstractPiece(VillagePieces.StartPiece startPiece, int i) {
			super(i);
			if (startPiece != null) {
				this.field_19427 = startPiece.field_19427;
				this.field_14872 = startPiece.field_14872;
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putInt("HPos", this.hPos);
			structureNbt.putInt("VCount", this.villagers);
			structureNbt.putByte("Type", (byte)this.field_19427.method_17674());
			structureNbt.putBoolean("Zombie", this.field_14872);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			this.hPos = nbtCompound.getInt("HPos");
			this.villagers = nbtCompound.getInt("VCount");
			this.field_19427 = VillagePieces.class_3996.method_17675(nbtCompound.getByte("Type"));
			if (nbtCompound.getBoolean("Desert")) {
				this.field_19427 = VillagePieces.class_3996.SANDSTONE;
			}

			this.field_14872 = nbtCompound.getBoolean("Zombie");
		}

		@Nullable
		protected StructurePiece fillNWOpening(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
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
			} else {
				return null;
			}
		}

		@Nullable
		protected StructurePiece fillSEOpening(VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
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
			} else {
				return null;
			}
		}

		protected int method_17676(IWorld iWorld, BlockBox blockBox) {
			int i = 0;
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++) {
				for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; l++) {
					mutable.setPosition(l, 64, k);
					if (blockBox.contains(mutable)) {
						i += iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						j++;
					}
				}
			}

			return j == 0 ? -1 : i / j;
		}

		protected static boolean isInbounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.minY > 10;
		}

		protected void method_109(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l) {
			if (this.villagers < l) {
				for (int m = this.villagers; m < l; m++) {
					int n = this.applyXTransform(i + m, k);
					int o = this.applyYTransform(j);
					int p = this.applyZTransform(i + m, k);
					if (!blockBox.contains(new BlockPos(n, o, p))) {
						break;
					}

					this.villagers++;
					if (this.field_14872) {
						ZombieVillagerEntity zombieVillagerEntity = new ZombieVillagerEntity(iWorld.method_16348());
						zombieVillagerEntity.refreshPositionAndAngles((double)n + 0.5, (double)o, (double)p + 0.5, 0.0F, 0.0F);
						zombieVillagerEntity.initialize(iWorld.method_8482(new BlockPos(zombieVillagerEntity)), null, null);
						zombieVillagerEntity.method_13606(this.method_111(m, 0));
						zombieVillagerEntity.setPersistent();
						iWorld.method_3686(zombieVillagerEntity);
					} else {
						VillagerEntity villagerEntity = new VillagerEntity(iWorld.method_16348());
						villagerEntity.refreshPositionAndAngles((double)n + 0.5, (double)o, (double)p + 0.5, 0.0F, 0.0F);
						villagerEntity.setProfession(this.method_111(m, iWorld.getRandom().nextInt(6)));
						villagerEntity.method_13613(iWorld.method_8482(new BlockPos(villagerEntity)), null, null, false);
						iWorld.method_3686(villagerEntity);
					}
				}
			}
		}

		protected int method_111(int i, int j) {
			return j;
		}

		protected BlockState checkSandStone(BlockState state) {
			Block block = state.getBlock();
			if (this.field_19427 == VillagePieces.class_3996.SANDSTONE) {
				if (block.isIn(BlockTags.LOGS) || block == Blocks.COBBLESTONE) {
					return Blocks.SANDSTONE.getDefaultState();
				}

				if (block.isIn(BlockTags.PLANKS)) {
					return Blocks.CUT_SANDSTONE.getDefaultState();
				}

				if (block == Blocks.WOODEN_STAIRS) {
					return Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, state.getProperty(StairsBlock.FACING));
				}

				if (block == Blocks.COBBLESTONE_STAIRS) {
					return Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, state.getProperty(StairsBlock.FACING));
				}

				if (block == Blocks.GRAVEL) {
					return Blocks.SANDSTONE.getDefaultState();
				}

				if (block == Blocks.OAK_PRESSURE_PLATE) {
					return Blocks.BIRCH_PRESSURE_PLATE.getDefaultState();
				}
			} else if (this.field_19427 == VillagePieces.class_3996.SPRUCE) {
				if (block.isIn(BlockTags.LOGS)) {
					return Blocks.SPRUCE_LOG.getDefaultState().withProperty(LogBlock.PILLAR_AXIS, state.getProperty(LogBlock.PILLAR_AXIS));
				}

				if (block.isIn(BlockTags.PLANKS)) {
					return Blocks.SPRUCE_PLANKS.getDefaultState();
				}

				if (block == Blocks.WOODEN_STAIRS) {
					return Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, state.getProperty(StairsBlock.FACING));
				}

				if (block == Blocks.OAK_FENCE) {
					return Blocks.SPRUCE_FENCE.getDefaultState();
				}

				if (block == Blocks.OAK_PRESSURE_PLATE) {
					return Blocks.SPRUCE_PRESSURE_PLATE.getDefaultState();
				}
			} else if (this.field_19427 == VillagePieces.class_3996.ACACIA) {
				if (block.isIn(BlockTags.LOGS)) {
					return Blocks.ACACIA_LOG.getDefaultState().withProperty(LogBlock.PILLAR_AXIS, state.getProperty(LogBlock.PILLAR_AXIS));
				}

				if (block.isIn(BlockTags.PLANKS)) {
					return Blocks.ACACIA_PLANKS.getDefaultState();
				}

				if (block == Blocks.WOODEN_STAIRS) {
					return Blocks.ACACIA_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, state.getProperty(StairsBlock.FACING));
				}

				if (block == Blocks.COBBLESTONE) {
					return Blocks.ACACIA_LOG.getDefaultState().withProperty(LogBlock.PILLAR_AXIS, Direction.Axis.Y);
				}

				if (block == Blocks.OAK_FENCE) {
					return Blocks.ACACIA_FENCE.getDefaultState();
				}

				if (block == Blocks.OAK_PRESSURE_PLATE) {
					return Blocks.ACACIA_PRESSURE_PLATE.getDefaultState();
				}
			}

			return state;
		}

		protected DoorBlock method_13382() {
			if (this.field_19427 == VillagePieces.class_3996.ACACIA) {
				return (DoorBlock)Blocks.ACACIA_DOOR;
			} else {
				return this.field_19427 == VillagePieces.class_3996.SPRUCE ? (DoorBlock)Blocks.SPRUCE_DOOR : (DoorBlock)Blocks.OAK_DOOR;
			}
		}

		protected void method_13380(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Direction direction) {
			if (!this.field_14872) {
				this.method_13377(iWorld, blockBox, random, i, j, k, Direction.NORTH, this.method_13382());
			}
		}

		protected void method_17677(IWorld iWorld, Direction direction, int i, int j, int k, BlockBox blockBox) {
			if (!this.field_14872) {
				this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, direction), i, j, k, blockBox);
			}
		}

		@Override
		protected void method_72(IWorld iWorld, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
			BlockState blockState2 = this.checkSandStone(blockState);
			super.method_72(iWorld, blockState2, i, j, k, blockBox);
		}

		protected void method_17678(VillagePieces.class_3996 arg) {
			this.field_19427 = arg;
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
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.hasChest = nbtCompound.getBoolean("Chest");
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			BlockState blockState = Blocks.COBBLESTONE.getDefaultState();
			BlockState blockState2 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST));
			BlockState blockState4 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState5 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState6 = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			BlockState blockState7 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 0, 1, 0, 9, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 9, 0, 6, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 4, 0, 9, 4, 6, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 5, 1, 8, 5, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 1, 0, 2, 3, 0, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 0, 1, 0, 0, 4, 0, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 3, 1, 0, 3, 4, 0, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 0, 1, 6, 0, 4, 6, blockState6, blockState6, false);
			this.method_56(iWorld, blockState4, 3, 3, 1, blockBox);
			this.method_17653(iWorld, blockBox, 3, 1, 2, 3, 3, 2, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 4, 1, 3, 5, 3, 3, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 3, 5, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 1, 1, 6, 5, 3, 6, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 5, 1, 0, 5, 3, 0, blockState7, blockState7, false);
			this.method_17653(iWorld, blockBox, 9, 1, 0, 9, 3, 0, blockState7, blockState7, false);
			this.method_17653(iWorld, blockBox, 6, 1, 4, 9, 4, 6, blockState, blockState, false);
			this.method_56(iWorld, Blocks.LAVA.getDefaultState(), 7, 1, 5, blockBox);
			this.method_56(iWorld, Blocks.LAVA.getDefaultState(), 8, 1, 5, blockBox);
			this.method_56(
				iWorld,
				Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)).withProperty(PaneBlock.field_18267, Boolean.valueOf(true)),
				9,
				2,
				5,
				blockBox
			);
			this.method_56(iWorld, Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)), 9, 2, 4, blockBox);
			this.method_17653(iWorld, blockBox, 7, 2, 4, 8, 2, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_56(iWorld, blockState, 6, 1, 3, blockBox);
			this.method_56(iWorld, Blocks.FURNACE.getDefaultState().withProperty(FurnaceBlock.FACING, Direction.SOUTH), 6, 2, 3, blockBox);
			this.method_56(iWorld, Blocks.FURNACE.getDefaultState().withProperty(FurnaceBlock.FACING, Direction.SOUTH), 6, 3, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState().withProperty(SlabBlock.field_18486, SlabType.DOUBLE), 8, 1, 1, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				4,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				2,
				6,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				4,
				2,
				6,
				blockBox
			);
			this.method_56(iWorld, blockState7, 2, 1, 4, blockBox);
			this.method_56(iWorld, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 4, blockBox);
			this.method_56(iWorld, blockState4, 1, 1, 5, blockBox);
			this.method_56(iWorld, blockState2, 2, 1, 5, blockBox);
			this.method_56(iWorld, blockState3, 1, 1, 4, blockBox);
			if (!this.hasChest && blockBox.contains(new BlockPos(this.applyXTransform(5, 5), this.applyYTransform(1), this.applyZTransform(5, 5)))) {
				this.hasChest = true;
				this.method_11852(iWorld, blockBox, random, 5, 1, 5, LootTables.VILLAGE_BLACKSMITH_CHEST);
			}

			for (int i = 6; i <= 8; i++) {
				if (this.method_9273(iWorld, i, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, i, -1, -1, blockBox).isAir()) {
					this.method_56(iWorld, blockState5, i, 0, -1, blockBox);
					if (this.method_9273(iWorld, i, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
						this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), i, -1, -1, blockBox);
					}
				}
			}

			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 10; k++) {
					this.method_73(iWorld, k, 6, j, blockBox);
					this.method_72(iWorld, blockState, k, -1, j, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 7, 1, 1, 1);
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 9 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST));
			BlockState blockState5 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState6 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState7 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 8, 0, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 8, 5, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 6, 1, 8, 6, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 7, 2, 8, 7, 3, blockState, blockState, false);

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_56(iWorld, blockState2, j, 6 + i, i, blockBox);
					this.method_56(iWorld, blockState3, j, 6 + i, 5 - i, blockBox);
				}
			}

			this.method_17653(iWorld, blockBox, 0, 1, 0, 0, 1, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 1, 5, 8, 1, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 1, 0, 8, 1, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 2, 1, 0, 7, 1, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 4, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 2, 5, 0, 4, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 2, 5, 8, 4, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 2, 0, 8, 4, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 2, 1, 0, 4, 4, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 1, 2, 5, 7, 4, 5, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 8, 2, 1, 8, 4, 4, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 1, 2, 0, 7, 4, 0, blockState5, blockState5, false);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				4,
				2,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				2,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				6,
				2,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				4,
				3,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				3,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				6,
				3,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				3,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				3,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				2,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				3,
				2,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				2,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				6,
				2,
				5,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 1, 4, 1, 7, 4, 1, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 1, 4, 4, 7, 4, 4, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			this.method_56(iWorld, blockState5, 7, 1, 4, blockBox);
			this.method_56(iWorld, blockState4, 7, 1, 3, blockBox);
			this.method_56(iWorld, blockState2, 6, 1, 4, blockBox);
			this.method_56(iWorld, blockState2, 5, 1, 4, blockBox);
			this.method_56(iWorld, blockState2, 4, 1, 4, blockBox);
			this.method_56(iWorld, blockState2, 3, 1, 4, blockBox);
			this.method_56(iWorld, blockState7, 6, 1, 3, blockBox);
			this.method_56(iWorld, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, blockBox);
			this.method_56(iWorld, blockState7, 4, 1, 3, blockBox);
			this.method_56(iWorld, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, blockBox);
			this.method_56(iWorld, Blocks.CRAFTING_TABLE.getDefaultState(), 7, 1, 1, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 1, 0, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 2, 0, blockBox);
			this.method_13380(iWorld, blockBox, random, 1, 1, 0, Direction.NORTH);
			if (this.method_9273(iWorld, 1, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 1, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState6, 1, 0, -1, blockBox);
				if (this.method_9273(iWorld, 1, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, blockBox);
				}
			}

			for (int k = 0; k < 6; k++) {
				for (int l = 0; l < 9; l++) {
					this.method_73(iWorld, l, 9, k, blockBox);
					this.method_72(iWorld, blockState, l, -1, k, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 2, 1, 2, 1);
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 12 - 1, 0);
			}

			BlockState blockState = Blocks.COBBLESTONE.getDefaultState();
			BlockState blockState2 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST));
			BlockState blockState4 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST));
			this.method_17653(iWorld, blockBox, 1, 1, 1, 3, 3, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 5, 1, 3, 9, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 3, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 1, 0, 3, 10, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 10, 3, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 4, 1, 1, 4, 10, 3, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 0, 4, 0, 4, 7, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 4, 0, 4, 4, 4, 7, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 1, 8, 3, 4, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 5, 4, 3, 10, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 5, 5, 3, 5, 7, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 9, 0, 4, 9, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 4, 0, 4, 4, 4, blockState, blockState, false);
			this.method_56(iWorld, blockState, 0, 11, 2, blockBox);
			this.method_56(iWorld, blockState, 4, 11, 2, blockBox);
			this.method_56(iWorld, blockState, 2, 11, 0, blockBox);
			this.method_56(iWorld, blockState, 2, 11, 4, blockBox);
			this.method_56(iWorld, blockState, 1, 1, 6, blockBox);
			this.method_56(iWorld, blockState, 1, 1, 7, blockBox);
			this.method_56(iWorld, blockState, 2, 1, 7, blockBox);
			this.method_56(iWorld, blockState, 3, 1, 6, blockBox);
			this.method_56(iWorld, blockState, 3, 1, 7, blockBox);
			this.method_56(iWorld, blockState2, 1, 1, 5, blockBox);
			this.method_56(iWorld, blockState2, 2, 1, 6, blockBox);
			this.method_56(iWorld, blockState2, 3, 1, 5, blockBox);
			this.method_56(iWorld, blockState3, 1, 2, 7, blockBox);
			this.method_56(iWorld, blockState4, 3, 2, 7, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				6,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				7,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				6,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				7,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				6,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				7,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				6,
				4,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				7,
				4,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				3,
				6,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				3,
				6,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				3,
				8,
				blockBox
			);
			this.method_17677(iWorld, Direction.SOUTH, 2, 4, 7, blockBox);
			this.method_17677(iWorld, Direction.EAST, 1, 4, 6, blockBox);
			this.method_17677(iWorld, Direction.WEST, 3, 4, 6, blockBox);
			this.method_17677(iWorld, Direction.NORTH, 2, 4, 5, blockBox);
			BlockState blockState5 = Blocks.LADDER.getDefaultState().withProperty(LadderBlock.FACING, Direction.WEST);

			for (int i = 1; i <= 9; i++) {
				this.method_56(iWorld, blockState5, 3, i, 3, blockBox);
			}

			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 1, 0, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 2, 0, blockBox);
			this.method_13380(iWorld, blockBox, random, 2, 1, 0, Direction.NORTH);
			if (this.method_9273(iWorld, 2, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 2, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState2, 2, 0, -1, blockBox);
				if (this.method_9273(iWorld, 2, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, blockBox);
				}
			}

			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 5; k++) {
					this.method_73(iWorld, k, 12, j, blockBox);
					this.method_72(iWorld, blockState, k, -1, j, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 2, 1, 2, 1);
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
		private BlockState field_19419;
		private BlockState field_19420;

		public FarmField() {
		}

		public FarmField(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.field_19419 = method_17673(random);
			this.field_19420 = method_17673(random);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.put("CA", NbtHelper.method_20139(this.field_19419));
			structureNbt.put("CB", NbtHelper.method_20139(this.field_19420));
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_19419 = NbtHelper.toBlockState(nbtCompound.getCompound("CA"));
			this.field_19420 = NbtHelper.toBlockState(nbtCompound.getCompound("CB"));
		}

		private static BlockState method_17673(Random random) {
			switch (random.nextInt(10)) {
				case 0:
				case 1:
					return Blocks.CARROTS.getDefaultState();
				case 2:
				case 3:
					return Blocks.POTATOES.getDefaultState();
				case 4:
					return Blocks.BEETROOTS.getDefaultState();
				default:
					return Blocks.WHEAT.getDefaultState();
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			this.method_17653(iWorld, blockBox, 0, 1, 0, 6, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 0, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 6, 0, 0, 6, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 5, 0, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 8, 5, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				CropBlock cropBlock = (CropBlock)this.field_19419.getBlock();
				int j = cropBlock.getMaxAge();
				int k = j / 3;
				this.method_56(iWorld, this.field_19419.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 1, 1, i, blockBox);
				this.method_56(iWorld, this.field_19419.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 2, 1, i, blockBox);
				cropBlock = (CropBlock)this.field_19420.getBlock();
				int l = cropBlock.getMaxAge();
				int m = l / 3;
				this.method_56(iWorld, this.field_19420.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 4, 1, i, blockBox);
				this.method_56(iWorld, this.field_19420.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 5, 1, i, blockBox);
			}

			for (int n = 0; n < 9; n++) {
				for (int o = 0; o < 7; o++) {
					this.method_73(iWorld, o, 4, n, blockBox);
					this.method_72(iWorld, Blocks.DIRT.getDefaultState(), o, -1, n, blockBox);
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 0, 0, 0, 2, 3, 1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_56(iWorld, blockState, 1, 0, 0, blockBox);
			this.method_56(iWorld, blockState, 1, 1, 0, blockBox);
			this.method_56(iWorld, blockState, 1, 2, 0, blockBox);
			this.method_56(iWorld, Blocks.BLACK_WOOL.getDefaultState(), 1, 3, 0, blockBox);
			this.method_17677(iWorld, Direction.EAST, 2, 3, 0, blockBox);
			this.method_17677(iWorld, Direction.NORTH, 1, 3, 1, blockBox);
			this.method_17677(iWorld, Direction.WEST, 0, 3, 0, blockBox);
			this.method_17677(iWorld, Direction.SOUTH, 1, 3, -1, blockBox);
			return true;
		}
	}

	public static class LargeFarmField extends VillagePieces.AbstractPiece {
		private BlockState field_19415;
		private BlockState field_19416;
		private BlockState field_19417;
		private BlockState field_19418;

		public LargeFarmField() {
		}

		public LargeFarmField(VillagePieces.StartPiece startPiece, int i, Random random, BlockBox blockBox, Direction direction) {
			super(startPiece, i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.field_19415 = VillagePieces.FarmField.method_17673(random);
			this.field_19416 = VillagePieces.FarmField.method_17673(random);
			this.field_19417 = VillagePieces.FarmField.method_17673(random);
			this.field_19418 = VillagePieces.FarmField.method_17673(random);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.put("CA", NbtHelper.method_20139(this.field_19415));
			structureNbt.put("CB", NbtHelper.method_20139(this.field_19416));
			structureNbt.put("CC", NbtHelper.method_20139(this.field_19417));
			structureNbt.put("CD", NbtHelper.method_20139(this.field_19418));
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_19415 = NbtHelper.toBlockState(nbtCompound.getCompound("CA"));
			this.field_19416 = NbtHelper.toBlockState(nbtCompound.getCompound("CB"));
			this.field_19417 = NbtHelper.toBlockState(nbtCompound.getCompound("CC"));
			this.field_19418 = NbtHelper.toBlockState(nbtCompound.getCompound("CD"));
			if (!(this.field_19415.getBlock() instanceof CropBlock)) {
				this.field_19415 = Blocks.WHEAT.getDefaultState();
			}

			if (!(this.field_19416.getBlock() instanceof CropBlock)) {
				this.field_19416 = Blocks.CARROTS.getDefaultState();
			}

			if (!(this.field_19417.getBlock() instanceof CropBlock)) {
				this.field_19417 = Blocks.POTATOES.getDefaultState();
			}

			if (!(this.field_19418.getBlock() instanceof CropBlock)) {
				this.field_19418 = Blocks.BEETROOTS.getDefaultState();
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			this.method_17653(iWorld, blockBox, 0, 1, 0, 12, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 0, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 6, 0, 0, 6, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 12, 0, 0, 12, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 11, 0, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 8, 11, 0, 8, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 0, 1, 9, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				CropBlock cropBlock = (CropBlock)this.field_19415.getBlock();
				int j = cropBlock.getMaxAge();
				int k = j / 3;
				this.method_56(iWorld, this.field_19415.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 1, 1, i, blockBox);
				this.method_56(iWorld, this.field_19415.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 2, 1, i, blockBox);
				cropBlock = (CropBlock)this.field_19416.getBlock();
				int l = cropBlock.getMaxAge();
				int m = l / 3;
				this.method_56(iWorld, this.field_19416.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 4, 1, i, blockBox);
				this.method_56(iWorld, this.field_19416.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 5, 1, i, blockBox);
				cropBlock = (CropBlock)this.field_19417.getBlock();
				int n = cropBlock.getMaxAge();
				int o = n / 3;
				this.method_56(iWorld, this.field_19417.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, o, n))), 7, 1, i, blockBox);
				this.method_56(iWorld, this.field_19417.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, o, n))), 8, 1, i, blockBox);
				cropBlock = (CropBlock)this.field_19418.getBlock();
				int p = cropBlock.getMaxAge();
				int q = p / 3;
				this.method_56(iWorld, this.field_19418.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, q, p))), 10, 1, i, blockBox);
				this.method_56(iWorld, this.field_19418.withProperty(cropBlock.getAge(), Integer.valueOf(MathHelper.nextInt(random, q, p))), 11, 1, i, blockBox);
			}

			for (int r = 0; r < 9; r++) {
				for (int s = 0; s < 13; s++) {
					this.method_73(iWorld, s, 4, r, blockBox);
					this.method_72(iWorld, Blocks.DIRT.getDefaultState(), s, -1, r, blockBox);
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST));
			BlockState blockState5 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState6 = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			BlockState blockState7 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 0, 6, 8, 0, 10, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
			this.method_56(iWorld, blockState, 6, 0, 6, blockBox);
			BlockState blockState8 = blockState7.withProperty(FenceBlock.field_18265, Boolean.valueOf(true)).withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			BlockState blockState9 = blockState7.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)).withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 2, 1, 6, 2, 1, 9, blockState8, blockState8, false);
			this.method_56(
				iWorld,
				blockState7.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)).withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
				2,
				1,
				10,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 8, 1, 6, 8, 1, 9, blockState8, blockState8, false);
			this.method_56(
				iWorld,
				blockState7.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)).withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
				8,
				1,
				10,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 3, 1, 10, 7, 1, 10, blockState9, blockState9, false);
			this.method_17653(iWorld, blockBox, 1, 0, 1, 7, 0, 4, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 0, 3, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 0, 0, 8, 3, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 7, 1, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 5, 7, 1, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 2, 0, 7, 3, 0, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 1, 2, 5, 7, 3, 5, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 0, 4, 1, 8, 4, 1, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 0, 4, 4, 8, 4, 4, blockState5, blockState5, false);
			this.method_17653(iWorld, blockBox, 0, 5, 2, 8, 5, 3, blockState5, blockState5, false);
			this.method_56(iWorld, blockState5, 0, 4, 2, blockBox);
			this.method_56(iWorld, blockState5, 0, 4, 3, blockBox);
			this.method_56(iWorld, blockState5, 8, 4, 2, blockBox);
			this.method_56(iWorld, blockState5, 8, 4, 3, blockBox);
			BlockState blockState10 = blockState2;
			BlockState blockState11 = blockState3;

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_56(iWorld, blockState10, j, 4 + i, i, blockBox);
					this.method_56(iWorld, blockState11, j, 4 + i, 5 - i, blockBox);
				}
			}

			this.method_56(iWorld, blockState6, 0, 2, 1, blockBox);
			this.method_56(iWorld, blockState6, 0, 2, 4, blockBox);
			this.method_56(iWorld, blockState6, 8, 2, 1, blockBox);
			this.method_56(iWorld, blockState6, 8, 2, 4, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				2,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				3,
				2,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				2,
				0,
				blockBox
			);
			this.method_56(iWorld, blockState7, 2, 1, 3, blockBox);
			this.method_56(iWorld, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), 2, 2, 3, blockBox);
			this.method_56(iWorld, blockState5, 1, 1, 4, blockBox);
			this.method_56(iWorld, blockState10, 2, 1, 4, blockBox);
			this.method_56(iWorld, blockState4, 1, 1, 3, blockBox);
			BlockState blockState13 = Blocks.STONE_SLAB.getDefaultState().withProperty(SlabBlock.field_18486, SlabType.DOUBLE);
			this.method_17653(iWorld, blockBox, 5, 0, 1, 7, 0, 3, blockState13, blockState13, false);
			this.method_56(iWorld, blockState13, 6, 1, 1, blockBox);
			this.method_56(iWorld, blockState13, 6, 1, 2, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 1, 0, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 2, 0, blockBox);
			this.method_17677(iWorld, Direction.NORTH, 2, 3, 1, blockBox);
			this.method_13380(iWorld, blockBox, random, 2, 1, 0, Direction.NORTH);
			if (this.method_9273(iWorld, 2, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 2, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState10, 2, 0, -1, blockBox);
				if (this.method_9273(iWorld, 2, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, blockBox);
				}
			}

			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 6, 1, 5, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 6, 2, 5, blockBox);
			this.method_17677(iWorld, Direction.SOUTH, 6, 3, 4, blockBox);
			this.method_13380(iWorld, blockBox, random, 6, 1, 5, Direction.SOUTH);

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					this.method_73(iWorld, l, 7, k, blockBox);
					this.method_72(iWorld, blockState, l, -1, k, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 4, 1, 2, 2);
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
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.terrace = nbtCompound.getBoolean("Terrace");
		}

		public static VillagePieces.SingleHouse create(
			VillagePieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction direction, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, 0, 0, 0, 5, 6, 5, direction);
			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : new VillagePieces.SingleHouse(start, chainLength, random, blockBox, direction);
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState3 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState4 = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			BlockState blockState5 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 0, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 4, 0, 4, 4, 4, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 1, 4, 1, 3, 4, 3, blockState2, blockState2, false);
			this.method_56(iWorld, blockState, 0, 1, 0, blockBox);
			this.method_56(iWorld, blockState, 0, 2, 0, blockBox);
			this.method_56(iWorld, blockState, 0, 3, 0, blockBox);
			this.method_56(iWorld, blockState, 4, 1, 0, blockBox);
			this.method_56(iWorld, blockState, 4, 2, 0, blockBox);
			this.method_56(iWorld, blockState, 4, 3, 0, blockBox);
			this.method_56(iWorld, blockState, 0, 1, 4, blockBox);
			this.method_56(iWorld, blockState, 0, 2, 4, blockBox);
			this.method_56(iWorld, blockState, 0, 3, 4, blockBox);
			this.method_56(iWorld, blockState, 4, 1, 4, blockBox);
			this.method_56(iWorld, blockState, 4, 2, 4, blockBox);
			this.method_56(iWorld, blockState, 4, 3, 4, blockBox);
			this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 3, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 4, 1, 1, 4, 3, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 1, 1, 4, 3, 3, 4, blockState2, blockState2, false);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				2,
				2,
				4,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				4,
				2,
				2,
				blockBox
			);
			this.method_56(iWorld, blockState2, 1, 1, 0, blockBox);
			this.method_56(iWorld, blockState2, 1, 2, 0, blockBox);
			this.method_56(iWorld, blockState2, 1, 3, 0, blockBox);
			this.method_56(iWorld, blockState2, 2, 3, 0, blockBox);
			this.method_56(iWorld, blockState2, 3, 3, 0, blockBox);
			this.method_56(iWorld, blockState2, 3, 2, 0, blockBox);
			this.method_56(iWorld, blockState2, 3, 1, 0, blockBox);
			if (this.method_9273(iWorld, 2, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 2, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState3, 2, 0, -1, blockBox);
				if (this.method_9273(iWorld, 2, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, blockBox);
				}
			}

			this.method_17653(iWorld, blockBox, 1, 1, 1, 3, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			if (this.terrace) {
				int i = 0;
				int j = 4;

				for (int k = 0; k <= 4; k++) {
					for (int l = 0; l <= 4; l++) {
						boolean bl = k == 0 || k == 4;
						boolean bl2 = l == 0 || l == 4;
						if (bl || bl2) {
							boolean bl3 = k == 0 || k == 4;
							boolean bl4 = l == 0 || l == 4;
							BlockState blockState6 = blockState5.withProperty(FenceBlock.field_18267, Boolean.valueOf(bl3 && l != 0))
								.withProperty(FenceBlock.field_18265, Boolean.valueOf(bl3 && l != 4))
								.withProperty(FenceBlock.field_18268, Boolean.valueOf(bl4 && k != 0))
								.withProperty(FenceBlock.field_18266, Boolean.valueOf(bl4 && k != 4));
							this.method_56(iWorld, blockState6, k, 5, l, blockBox);
						}
					}
				}
			}

			if (this.terrace) {
				BlockState blockState7 = Blocks.LADDER.getDefaultState().withProperty(LadderBlock.FACING, Direction.SOUTH);
				this.method_56(iWorld, blockState7, 3, 1, 3, blockBox);
				this.method_56(iWorld, blockState7, 3, 2, 3, blockBox);
				this.method_56(iWorld, blockState7, 3, 3, 3, blockBox);
				this.method_56(iWorld, blockState7, 3, 4, 3, blockBox);
			}

			this.method_17677(iWorld, Direction.NORTH, 2, 3, 1, blockBox);

			for (int m = 0; m < 5; m++) {
				for (int n = 0; n < 5; n++) {
					this.method_73(iWorld, n, 6, m, blockBox);
					this.method_72(iWorld, blockState, n, -1, m, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 1, 1, 2, 1);
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
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.tablePosition = nbtCompound.getInt("T");
			this.tall = nbtCompound.getBoolean("C");
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState3 = this.checkSandStone(Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState4 = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			BlockState blockState5 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 3, 0, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
			if (this.tall) {
				this.method_17653(iWorld, blockBox, 1, 4, 1, 2, 4, 3, blockState4, blockState4, false);
			} else {
				this.method_17653(iWorld, blockBox, 1, 5, 1, 2, 5, 3, blockState4, blockState4, false);
			}

			this.method_56(iWorld, blockState4, 1, 4, 0, blockBox);
			this.method_56(iWorld, blockState4, 2, 4, 0, blockBox);
			this.method_56(iWorld, blockState4, 1, 4, 4, blockBox);
			this.method_56(iWorld, blockState4, 2, 4, 4, blockBox);
			this.method_56(iWorld, blockState4, 0, 4, 1, blockBox);
			this.method_56(iWorld, blockState4, 0, 4, 2, blockBox);
			this.method_56(iWorld, blockState4, 0, 4, 3, blockBox);
			this.method_56(iWorld, blockState4, 3, 4, 1, blockBox);
			this.method_56(iWorld, blockState4, 3, 4, 2, blockBox);
			this.method_56(iWorld, blockState4, 3, 4, 3, blockBox);
			this.method_17653(iWorld, blockBox, 0, 1, 0, 0, 3, 0, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 3, 1, 0, 3, 3, 0, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 0, 1, 4, 0, 3, 4, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 3, 1, 4, 3, 3, 4, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 3, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 3, 1, 1, 3, 3, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 1, 1, 0, 2, 3, 0, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 1, 1, 4, 2, 3, 4, blockState2, blockState2, false);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				3,
				2,
				2,
				blockBox
			);
			if (this.tablePosition > 0) {
				this.method_56(
					iWorld,
					blockState5.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
						.withProperty(this.tablePosition == 1 ? FenceBlock.field_18268 : FenceBlock.field_18266, Boolean.valueOf(true)),
					this.tablePosition,
					1,
					3,
					blockBox
				);
				this.method_56(iWorld, Blocks.OAK_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, blockBox);
			}

			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 1, 0, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 2, 0, blockBox);
			this.method_13380(iWorld, blockBox, random, 1, 1, 0, Direction.NORTH);
			if (this.method_9273(iWorld, 1, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 1, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState3, 1, 0, -1, blockBox);
				if (this.method_9273(iWorld, 1, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, blockBox);
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					this.method_73(iWorld, j, 6, i, blockBox);
					this.method_72(iWorld, blockState, j, -1, i, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 1, 1, 2, 1);
			return true;
		}
	}

	public static class StartPiece extends VillagePieces.Well {
		public int field_94;
		public VillagePieces.PieceData field_95;
		public List<VillagePieces.PieceData> field_6245;
		public List<StructurePiece> field_6246 = Lists.newArrayList();
		public List<StructurePiece> field_6247 = Lists.newArrayList();

		public StartPiece() {
		}

		public StartPiece(int i, Random random, int j, int k, List<VillagePieces.PieceData> list, class_3911 arg) {
			super(null, 0, random, j, k);
			this.field_6245 = list;
			this.field_94 = arg.field_19272;
			this.field_19427 = arg.field_19273;
			this.method_17678(this.field_19427);
			this.field_14872 = random.nextInt(50) == 0;
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
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.length = nbtCompound.getInt("Length");
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
					default:
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
					default:
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			BlockState blockState = this.checkSandStone(Blocks.GRASS_PATH.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState3 = this.checkSandStone(Blocks.GRAVEL.getDefaultState());
			BlockState blockState4 = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			this.boundingBox.minY = 1000;
			this.boundingBox.maxY = 0;

			for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
				for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
					mutable.setPosition(i, 64, j);
					if (blockBox.contains(mutable)) {
						int k = iWorld.method_16372(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, mutable.getX(), mutable.getZ());
						mutable.setPosition(mutable.getX(), k, mutable.getZ()).move(Direction.DOWN);
						if (mutable.getY() < iWorld.method_8483()) {
							mutable.setY(iWorld.method_8483() - 1);
						}

						while (mutable.getY() >= iWorld.method_8483() - 1) {
							BlockState blockState5 = iWorld.getBlockState(mutable);
							Block block = blockState5.getBlock();
							if (block == Blocks.GRASS_BLOCK && iWorld.method_8579(mutable.up())) {
								iWorld.setBlockState(mutable, blockState, 2);
								break;
							}

							if (blockState5.getMaterial().isFluid()) {
								iWorld.setBlockState(new BlockPos(mutable), blockState2, 2);
								break;
							}

							if (block == Blocks.SAND
								|| block == Blocks.RED_SAND
								|| block == Blocks.SANDSTONE
								|| block == Blocks.CHISELED_SANDSTONE
								|| block == Blocks.CUT_SANDSTONE
								|| block == Blocks.RED_SANDSTONE
								|| block == Blocks.CHISELED_SANDSTONE
								|| block == Blocks.CUT_SANDSTONE) {
								iWorld.setBlockState(mutable, blockState3, 2);
								iWorld.setBlockState(mutable.down(), blockState4, 2);
								break;
							}

							mutable.move(Direction.DOWN);
						}

						this.boundingBox.minY = Math.min(this.boundingBox.minY, mutable.getY());
						this.boundingBox.maxY = Math.max(this.boundingBox.maxY, mutable.getY());
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST));
			BlockState blockState5 = this.checkSandStone(Blocks.WOODEN_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST));
			BlockState blockState6 = this.checkSandStone(Blocks.OAK_PLANKS.getDefaultState());
			BlockState blockState7 = this.checkSandStone(Blocks.OAK_LOG.getDefaultState());
			this.method_17653(iWorld, blockBox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 0, 5, 8, 0, 10, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 1, 0, 1, 7, 0, 4, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 0, 3, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 0, 0, 8, 3, 10, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 7, 2, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 0, 5, 2, 1, 5, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 2, 0, 6, 2, 3, 10, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 3, 0, 10, 7, 3, 10, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 1, 2, 0, 7, 3, 0, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 1, 2, 5, 2, 3, 5, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 0, 4, 1, 8, 4, 1, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 0, 4, 4, 3, 4, 4, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 0, 5, 2, 8, 5, 3, blockState6, blockState6, false);
			this.method_56(iWorld, blockState6, 0, 4, 2, blockBox);
			this.method_56(iWorld, blockState6, 0, 4, 3, blockBox);
			this.method_56(iWorld, blockState6, 8, 4, 2, blockBox);
			this.method_56(iWorld, blockState6, 8, 4, 3, blockBox);
			this.method_56(iWorld, blockState6, 8, 4, 4, blockBox);
			BlockState blockState8 = blockState2;
			BlockState blockState9 = blockState3;
			BlockState blockState10 = blockState5;
			BlockState blockState11 = blockState4;

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_56(iWorld, blockState8, j, 4 + i, i, blockBox);
					if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
						this.method_56(iWorld, blockState9, j, 4 + i, 5 - i, blockBox);
					}
				}
			}

			this.method_17653(iWorld, blockBox, 3, 4, 5, 3, 4, 10, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 7, 4, 2, 7, 4, 10, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 4, 5, 4, 4, 5, 10, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 6, 5, 4, 6, 5, 10, blockState6, blockState6, false);
			this.method_17653(iWorld, blockBox, 5, 6, 3, 5, 6, 10, blockState6, blockState6, false);

			for (int k = 4; k >= 1; k--) {
				this.method_56(iWorld, blockState6, k, 2 + k, 7 - k, blockBox);

				for (int l = 8 - k; l <= 10; l++) {
					this.method_56(iWorld, blockState11, k, 2 + k, l, blockBox);
				}
			}

			this.method_56(iWorld, blockState6, 6, 6, 3, blockBox);
			this.method_56(iWorld, blockState6, 7, 5, 4, blockBox);
			this.method_56(iWorld, blockState5, 6, 6, 4, blockBox);

			for (int m = 6; m <= 8; m++) {
				for (int n = 5; n <= 10; n++) {
					this.method_56(iWorld, blockState10, m, 12 - m, n, blockBox);
				}
			}

			this.method_56(iWorld, blockState7, 0, 2, 1, blockBox);
			this.method_56(iWorld, blockState7, 0, 2, 4, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				0,
				2,
				3,
				blockBox
			);
			this.method_56(iWorld, blockState7, 4, 2, 0, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				2,
				0,
				blockBox
			);
			this.method_56(iWorld, blockState7, 6, 2, 0, blockBox);
			this.method_56(iWorld, blockState7, 8, 2, 1, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				3,
				blockBox
			);
			this.method_56(iWorld, blockState7, 8, 2, 4, blockBox);
			this.method_56(iWorld, blockState6, 8, 2, 5, blockBox);
			this.method_56(iWorld, blockState7, 8, 2, 6, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				7,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				8,
				2,
				8,
				blockBox
			);
			this.method_56(iWorld, blockState7, 8, 2, 9, blockBox);
			this.method_56(iWorld, blockState7, 2, 2, 6, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				2,
				2,
				7,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18267, Boolean.valueOf(true)).withProperty(class_3706.field_18265, Boolean.valueOf(true)),
				2,
				2,
				8,
				blockBox
			);
			this.method_56(iWorld, blockState7, 2, 2, 9, blockBox);
			this.method_56(iWorld, blockState7, 4, 4, 10, blockBox);
			this.method_56(
				iWorld,
				Blocks.GLASS_PANE.getDefaultState().withProperty(class_3706.field_18266, Boolean.valueOf(true)).withProperty(class_3706.field_18268, Boolean.valueOf(true)),
				5,
				4,
				10,
				blockBox
			);
			this.method_56(iWorld, blockState7, 6, 4, 10, blockBox);
			this.method_56(iWorld, blockState6, 5, 5, 10, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 1, 0, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 2, 0, blockBox);
			this.method_17677(iWorld, Direction.NORTH, 2, 3, 1, blockBox);
			this.method_13380(iWorld, blockBox, random, 2, 1, 0, Direction.NORTH);
			this.method_17653(iWorld, blockBox, 1, 0, -1, 3, 2, -1, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			if (this.method_9273(iWorld, 2, 0, -1, blockBox).isAir() && !this.method_9273(iWorld, 2, -1, -1, blockBox).isAir()) {
				this.method_56(iWorld, blockState8, 2, 0, -1, blockBox);
				if (this.method_9273(iWorld, 2, -1, -1, blockBox).getBlock() == Blocks.GRASS_PATH) {
					this.method_56(iWorld, Blocks.GRASS_BLOCK.getDefaultState(), 2, -1, -1, blockBox);
				}
			}

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < 9; p++) {
					this.method_73(iWorld, p, 7, o, blockBox);
					this.method_72(iWorld, blockState, p, -1, o, blockBox);
				}
			}

			for (int q = 5; q < 11; q++) {
				for (int r = 2; r < 9; r++) {
					this.method_73(iWorld, r, 7, q, blockBox);
					this.method_72(iWorld, blockState, r, -1, q, blockBox);
				}
			}

			this.method_109(iWorld, blockBox, 4, 1, 2, 2);
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
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_17676(iWorld, blockBox);
				if (this.hPos < 0) {
					return true;
				}

				this.boundingBox.move(0, this.hPos - this.boundingBox.maxY + 3, 0);
			}

			BlockState blockState = this.checkSandStone(Blocks.COBBLESTONE.getDefaultState());
			BlockState blockState2 = this.checkSandStone(Blocks.OAK_FENCE.getDefaultState());
			this.method_17653(iWorld, blockBox, 1, 0, 1, 4, 12, 4, blockState, Blocks.WATER.getDefaultState(), false);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 12, 2, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 3, 12, 2, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 2, 12, 3, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 3, 12, 3, blockBox);
			this.method_56(iWorld, blockState2, 1, 13, 1, blockBox);
			this.method_56(iWorld, blockState2, 1, 14, 1, blockBox);
			this.method_56(iWorld, blockState2, 4, 13, 1, blockBox);
			this.method_56(iWorld, blockState2, 4, 14, 1, blockBox);
			this.method_56(iWorld, blockState2, 1, 13, 4, blockBox);
			this.method_56(iWorld, blockState2, 1, 14, 4, blockBox);
			this.method_56(iWorld, blockState2, 4, 13, 4, blockBox);
			this.method_56(iWorld, blockState2, 4, 14, 4, blockBox);
			this.method_17653(iWorld, blockBox, 1, 15, 1, 4, 15, 4, blockState, blockState, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 5; j++) {
					if (j == 0 || j == 5 || i == 0 || i == 5) {
						this.method_56(iWorld, blockState, j, 11, i, blockBox);
						this.method_73(iWorld, j, 12, i, blockBox);
					}
				}
			}

			return true;
		}
	}

	public static enum class_3996 {
		OAK(0),
		SANDSTONE(1),
		ACACIA(2),
		SPRUCE(3);

		private final int field_19425;

		private class_3996(int j) {
			this.field_19425 = j;
		}

		public int method_17674() {
			return this.field_19425;
		}

		public static VillagePieces.class_3996 method_17675(int i) {
			VillagePieces.class_3996[] lvs = values();
			return i >= 0 && i < lvs.length ? lvs[i] : OAK;
		}
	}
}
