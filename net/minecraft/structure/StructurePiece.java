package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.WoodenDoorItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class StructurePiece {
	protected BlockBox boundingBox;
	protected Direction facing;
	protected int chainLength;

	public StructurePiece() {
	}

	protected StructurePiece(int i) {
		this.chainLength = i;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", StructurePieceManager.getId(this));
		nbtCompound.put("BB", this.boundingBox.toNbt());
		nbtCompound.putInt("O", this.facing == null ? -1 : this.facing.getHorizontal());
		nbtCompound.putInt("GD", this.chainLength);
		this.serialize(nbtCompound);
		return nbtCompound;
	}

	protected abstract void serialize(NbtCompound structureNbt);

	public void fromNbt(World world, NbtCompound nbt) {
		if (nbt.contains("BB")) {
			this.boundingBox = new BlockBox(nbt.getIntArray("BB"));
		}

		int i = nbt.getInt("O");
		this.facing = i == -1 ? null : Direction.fromHorizontal(i);
		this.chainLength = nbt.getInt("GD");
		this.deserialize(nbt);
	}

	protected abstract void deserialize(NbtCompound structureNbt);

	public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
	}

	public abstract boolean generate(World world, Random random, BlockBox boundingBox);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public int getChainLength() {
		return this.chainLength;
	}

	public static StructurePiece getOverlappingPiece(List<StructurePiece> pieces, BlockBox box) {
		for (StructurePiece structurePiece : pieces) {
			if (structurePiece.getBoundingBox() != null && structurePiece.getBoundingBox().intersects(box)) {
				return structurePiece;
			}
		}

		return null;
	}

	public BlockPos getCenterBlockPos() {
		return new BlockPos(this.boundingBox.getCenter());
	}

	protected boolean isTouchingLiquid(World world, BlockBox box) {
		int i = Math.max(this.boundingBox.minX - 1, box.minX);
		int j = Math.max(this.boundingBox.minY - 1, box.minY);
		int k = Math.max(this.boundingBox.minZ - 1, box.minZ);
		int l = Math.min(this.boundingBox.maxX + 1, box.maxX);
		int m = Math.min(this.boundingBox.maxY + 1, box.maxY);
		int n = Math.min(this.boundingBox.maxZ + 1, box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (world.getBlockState(mutable.setPosition(o, j, p)).getBlock().getMaterial().isFluid()) {
					return true;
				}

				if (world.getBlockState(mutable.setPosition(o, m, p)).getBlock().getMaterial().isFluid()) {
					return true;
				}
			}
		}

		for (int q = i; q <= l; q++) {
			for (int r = j; r <= m; r++) {
				if (world.getBlockState(mutable.setPosition(q, r, k)).getBlock().getMaterial().isFluid()) {
					return true;
				}

				if (world.getBlockState(mutable.setPosition(q, r, n)).getBlock().getMaterial().isFluid()) {
					return true;
				}
			}
		}

		for (int s = k; s <= n; s++) {
			for (int t = j; t <= m; t++) {
				if (world.getBlockState(mutable.setPosition(i, t, s)).getBlock().getMaterial().isFluid()) {
					return true;
				}

				if (world.getBlockState(mutable.setPosition(l, t, s)).getBlock().getMaterial().isFluid()) {
					return true;
				}
			}
		}

		return false;
	}

	protected int applyXTransform(int x, int z) {
		if (this.facing == null) {
			return x;
		} else {
			switch (this.facing) {
				case NORTH:
				case SOUTH:
					return this.boundingBox.minX + x;
				case WEST:
					return this.boundingBox.maxX - z;
				case EAST:
					return this.boundingBox.minX + z;
				default:
					return x;
			}
		}
	}

	protected int applyYTransform(int y) {
		return this.facing == null ? y : y + this.boundingBox.minY;
	}

	protected int applyZTransform(int x, int z) {
		if (this.facing == null) {
			return z;
		} else {
			switch (this.facing) {
				case NORTH:
					return this.boundingBox.maxZ - z;
				case SOUTH:
					return this.boundingBox.minZ + z;
				case WEST:
				case EAST:
					return this.boundingBox.minZ + x;
				default:
					return z;
			}
		}
	}

	protected int getData(Block block, int type) {
		if (block == Blocks.RAIL) {
			if (this.facing == Direction.WEST || this.facing == Direction.EAST) {
				if (type == 1) {
					return 0;
				}

				return 1;
			}
		} else if (block instanceof DoorBlock) {
			if (this.facing == Direction.SOUTH) {
				if (type == 0) {
					return 2;
				}

				if (type == 2) {
					return 0;
				}
			} else {
				if (this.facing == Direction.WEST) {
					return type + 1 & 3;
				}

				if (this.facing == Direction.EAST) {
					return type + 3 & 3;
				}
			}
		} else if (block != Blocks.STONE_STAIRS
			&& block != Blocks.WOODEN_STAIRS
			&& block != Blocks.NETHER_BRICK_STAIRS
			&& block != Blocks.STONE_BRICK_STAIRS
			&& block != Blocks.SANDSTONE_STAIRS) {
			if (block == Blocks.LADDER) {
				if (this.facing == Direction.SOUTH) {
					if (type == Direction.NORTH.getId()) {
						return Direction.SOUTH.getId();
					}

					if (type == Direction.SOUTH.getId()) {
						return Direction.NORTH.getId();
					}
				} else if (this.facing == Direction.WEST) {
					if (type == Direction.NORTH.getId()) {
						return Direction.WEST.getId();
					}

					if (type == Direction.SOUTH.getId()) {
						return Direction.EAST.getId();
					}

					if (type == Direction.WEST.getId()) {
						return Direction.NORTH.getId();
					}

					if (type == Direction.EAST.getId()) {
						return Direction.SOUTH.getId();
					}
				} else if (this.facing == Direction.EAST) {
					if (type == Direction.NORTH.getId()) {
						return Direction.EAST.getId();
					}

					if (type == Direction.SOUTH.getId()) {
						return Direction.WEST.getId();
					}

					if (type == Direction.WEST.getId()) {
						return Direction.NORTH.getId();
					}

					if (type == Direction.EAST.getId()) {
						return Direction.SOUTH.getId();
					}
				}
			} else if (block == Blocks.STONE_BUTTON) {
				if (this.facing == Direction.SOUTH) {
					if (type == 3) {
						return 4;
					}

					if (type == 4) {
						return 3;
					}
				} else if (this.facing == Direction.WEST) {
					if (type == 3) {
						return 1;
					}

					if (type == 4) {
						return 2;
					}

					if (type == 2) {
						return 3;
					}

					if (type == 1) {
						return 4;
					}
				} else if (this.facing == Direction.EAST) {
					if (type == 3) {
						return 2;
					}

					if (type == 4) {
						return 1;
					}

					if (type == 2) {
						return 3;
					}

					if (type == 1) {
						return 4;
					}
				}
			} else if (block == Blocks.TRIPWIRE_HOOK || block instanceof FacingBlock) {
				Direction direction = Direction.fromHorizontal(type);
				if (this.facing == Direction.SOUTH) {
					if (direction == Direction.SOUTH || direction == Direction.NORTH) {
						return direction.getOpposite().getHorizontal();
					}
				} else if (this.facing == Direction.WEST) {
					if (direction == Direction.NORTH) {
						return Direction.WEST.getHorizontal();
					}

					if (direction == Direction.SOUTH) {
						return Direction.EAST.getHorizontal();
					}

					if (direction == Direction.WEST) {
						return Direction.NORTH.getHorizontal();
					}

					if (direction == Direction.EAST) {
						return Direction.SOUTH.getHorizontal();
					}
				} else if (this.facing == Direction.EAST) {
					if (direction == Direction.NORTH) {
						return Direction.EAST.getHorizontal();
					}

					if (direction == Direction.SOUTH) {
						return Direction.WEST.getHorizontal();
					}

					if (direction == Direction.WEST) {
						return Direction.NORTH.getHorizontal();
					}

					if (direction == Direction.EAST) {
						return Direction.SOUTH.getHorizontal();
					}
				}
			} else if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.LEVER || block == Blocks.DISPENSER) {
				if (this.facing == Direction.SOUTH) {
					if (type == Direction.NORTH.getId() || type == Direction.SOUTH.getId()) {
						return Direction.getById(type).getOpposite().getId();
					}
				} else if (this.facing == Direction.WEST) {
					if (type == Direction.NORTH.getId()) {
						return Direction.WEST.getId();
					}

					if (type == Direction.SOUTH.getId()) {
						return Direction.EAST.getId();
					}

					if (type == Direction.WEST.getId()) {
						return Direction.NORTH.getId();
					}

					if (type == Direction.EAST.getId()) {
						return Direction.SOUTH.getId();
					}
				} else if (this.facing == Direction.EAST) {
					if (type == Direction.NORTH.getId()) {
						return Direction.EAST.getId();
					}

					if (type == Direction.SOUTH.getId()) {
						return Direction.WEST.getId();
					}

					if (type == Direction.WEST.getId()) {
						return Direction.NORTH.getId();
					}

					if (type == Direction.EAST.getId()) {
						return Direction.SOUTH.getId();
					}
				}
			}
		} else if (this.facing == Direction.SOUTH) {
			if (type == 2) {
				return 3;
			}

			if (type == 3) {
				return 2;
			}
		} else if (this.facing == Direction.WEST) {
			if (type == 0) {
				return 2;
			}

			if (type == 1) {
				return 3;
			}

			if (type == 2) {
				return 0;
			}

			if (type == 3) {
				return 1;
			}
		} else if (this.facing == Direction.EAST) {
			if (type == 0) {
				return 2;
			}

			if (type == 1) {
				return 3;
			}

			if (type == 2) {
				return 1;
			}

			if (type == 3) {
				return 0;
			}
		}

		return type;
	}

	protected void setBlockState(World world, BlockState state, int x, int y, int z, BlockBox box) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (box.contains(blockPos)) {
			world.setBlockState(blockPos, state, 2);
		}
	}

	protected BlockState getBlockAt(World world, int x, int y, int z, BlockBox box) {
		int i = this.applyXTransform(x, z);
		int j = this.applyYTransform(y);
		int k = this.applyZTransform(x, z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return !box.contains(blockPos) ? Blocks.AIR.getDefaultState() : world.getBlockState(blockPos);
	}

	protected void setAir(World world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					this.setBlockState(world, Blocks.AIR.getDefaultState(), j, i, k, box);
				}
			}
		}
	}

	protected void fillWithOutline(
		World world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState outline, BlockState inside, boolean cantReplaceAir
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!cantReplaceAir || this.getBlockAt(world, j, i, k, box).getBlock().getMaterial() != Material.AIR) {
						if (i != minY && i != maxY && j != minX && j != maxX && k != minZ && k != maxZ) {
							this.setBlockState(world, inside, j, i, k, box);
						} else {
							this.setBlockState(world, outline, j, i, k, box);
						}
					}
				}
			}
		}
	}

	protected void fillRandomized(
		World world,
		BlockBox box,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		boolean cantReplaceAir,
		Random random,
		StructurePiece.BlockRandomizer randomizer
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!cantReplaceAir || this.getBlockAt(world, j, i, k, box).getBlock().getMaterial() != Material.AIR) {
						randomizer.setBlock(random, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
						this.setBlockState(world, randomizer.getBlock(), j, i, k, box);
					}
				}
			}
		}
	}

	protected void fillWithOutlineUnderSeaLevel(
		World world,
		BlockBox box,
		Random random,
		float blockChance,
		int minX,
		int minY,
		int minZ,
		int maxX,
		int maxY,
		int maxZ,
		BlockState outline,
		BlockState inside,
		boolean cantReplaceAir
	) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					if (!(random.nextFloat() > blockChance) && (!cantReplaceAir || this.getBlockAt(world, j, i, k, box).getBlock().getMaterial() != Material.AIR)) {
						if (i != minY && i != maxY && j != minX && j != maxX && k != minZ && k != maxZ) {
							this.setBlockState(world, inside, j, i, k, box);
						} else {
							this.setBlockState(world, outline, j, i, k, box);
						}
					}
				}
			}
		}
	}

	protected void addBlockWithRandomThreshold(World world, BlockBox box, Random random, float blockChance, int x, int y, int z, BlockState block) {
		if (random.nextFloat() < blockChance) {
			this.setBlockState(world, block, x, y, z, box);
		}
	}

	protected void fillHalfEllipsoid(
		World world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block, boolean cantReplaceAir
	) {
		float f = (float)(maxX - minX + 1);
		float g = (float)(maxY - minY + 1);
		float h = (float)(maxZ - minZ + 1);
		float i = (float)minX + f / 2.0F;
		float j = (float)minZ + h / 2.0F;

		for (int k = minY; k <= maxY; k++) {
			float l = (float)(k - minY) / g;

			for (int m = minX; m <= maxX; m++) {
				float n = ((float)m - i) / (f * 0.5F);

				for (int o = minZ; o <= maxZ; o++) {
					float p = ((float)o - j) / (h * 0.5F);
					if (!cantReplaceAir || this.getBlockAt(world, m, k, o, box).getBlock().getMaterial() != Material.AIR) {
						float q = n * n + l * l + p * p;
						if (q <= 1.05F) {
							this.setBlockState(world, block, m, k, o, box);
						}
					}
				}
			}
		}
	}

	protected void clearBlocksUpwards(World world, int x, int y, int z, BlockBox box) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (box.contains(blockPos)) {
			while (!world.isAir(blockPos) && blockPos.getY() < 255) {
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
				blockPos = blockPos.up();
			}
		}
	}

	protected void fillAirAndLiquidsDownwards(World world, BlockState block, int x, int y, int z, BlockBox box) {
		int i = this.applyXTransform(x, z);
		int j = this.applyYTransform(y);
		int k = this.applyZTransform(x, z);
		if (box.contains(new BlockPos(i, j, k))) {
			while ((world.isAir(new BlockPos(i, j, k)) || world.getBlockState(new BlockPos(i, j, k)).getBlock().getMaterial().isFluid()) && j > 1) {
				world.setBlockState(new BlockPos(i, j, k), block, 2);
				j--;
			}
		}
	}

	protected boolean placeChest(World world, BlockBox box, Random random, int x, int y, int z, List<WeightedRandomChestContent> chestContent, int i) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (box.contains(blockPos) && world.getBlockState(blockPos).getBlock() != Blocks.CHEST) {
			BlockState blockState = Blocks.CHEST.getDefaultState();
			world.setBlockState(blockPos, Blocks.CHEST.changeFacing(world, blockPos, blockState), 2);
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				WeightedRandomChestContent.fillInventory(random, chestContent, (ChestBlockEntity)blockEntity, i);
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean placeDispenser(
		World world, BlockBox box, Random random, int x, int y, int z, int type, List<WeightedRandomChestContent> dispenserContent, int i
	) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (box.contains(blockPos) && world.getBlockState(blockPos).getBlock() != Blocks.DISPENSER) {
			world.setBlockState(blockPos, Blocks.DISPENSER.stateFromData(this.getData(Blocks.DISPENSER, type)), 2);
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				WeightedRandomChestContent.fillDispenser(random, dispenserContent, (DispenserBlockEntity)blockEntity, i);
			}

			return true;
		} else {
			return false;
		}
	}

	protected void placeDoor(World world, BlockBox box, Random random, int x, int y, int z, Direction facing) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
		if (box.contains(blockPos)) {
			WoodenDoorItem.place(world, blockPos, facing.rotateYCounterclockwise(), Blocks.OAK_DOOR);
		}
	}

	public void translate(int x, int y, int z) {
		this.boundingBox.move(x, y, z);
	}

	public abstract static class BlockRandomizer {
		protected BlockState block = Blocks.AIR.getDefaultState();

		protected BlockRandomizer() {
		}

		public abstract void setBlock(Random random, int x, int y, int z, boolean placeBlock);

		public BlockState getBlock() {
			return this.block;
		}
	}
}
