package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.class_3998;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public abstract class StructurePiece {
	protected static final BlockState field_19406 = Blocks.CAVE_AIR.getDefaultState();
	protected BlockBox boundingBox;
	@Nullable
	private Direction facing;
	private BlockMirror field_13013;
	private BlockRotation field_13014;
	protected int chainLength;
	private static final Set<Block> field_19405 = ImmutableSet.builder()
		.add(Blocks.NETHER_BRICK_FENCE)
		.add(Blocks.TORCH)
		.add(Blocks.WALL_TORCH)
		.add(Blocks.OAK_FENCE)
		.add(Blocks.SPRUCE_FENCE)
		.add(Blocks.DARK_OAK_FENCE)
		.add(Blocks.ACACIA_FENCE)
		.add(Blocks.BIRCH_FENCE)
		.add(Blocks.JUNGLE_FENCE)
		.add(Blocks.LADDER)
		.add(Blocks.IRON_BARS)
		.build();

	public StructurePiece() {
	}

	protected StructurePiece(int i) {
		this.chainLength = i;
	}

	public final NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", StructurePieceManager.getId(this));
		nbtCompound.put("BB", this.boundingBox.toNbt());
		Direction direction = this.method_11854();
		nbtCompound.putInt("O", direction == null ? -1 : direction.getHorizontal());
		nbtCompound.putInt("GD", this.chainLength);
		this.serialize(nbtCompound);
		return nbtCompound;
	}

	protected abstract void serialize(NbtCompound structureNbt);

	public void method_5527(IWorld iWorld, NbtCompound nbtCompound) {
		if (nbtCompound.contains("BB")) {
			this.boundingBox = new BlockBox(nbtCompound.getIntArray("BB"));
		}

		int i = nbtCompound.getInt("O");
		this.method_11853(i == -1 ? null : Direction.fromHorizontal(i));
		this.chainLength = nbtCompound.getInt("GD");
		this.method_5530(nbtCompound, iWorld.method_3587().method_11956());
	}

	protected abstract void method_5530(NbtCompound nbtCompound, class_3998 arg);

	public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
	}

	public abstract boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos);

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

	protected boolean method_17651(BlockView blockView, BlockBox blockBox) {
		int i = Math.max(this.boundingBox.minX - 1, blockBox.minX);
		int j = Math.max(this.boundingBox.minY - 1, blockBox.minY);
		int k = Math.max(this.boundingBox.minZ - 1, blockBox.minZ);
		int l = Math.min(this.boundingBox.maxX + 1, blockBox.maxX);
		int m = Math.min(this.boundingBox.maxY + 1, blockBox.maxY);
		int n = Math.min(this.boundingBox.maxZ + 1, blockBox.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= l; o++) {
			for (int p = k; p <= n; p++) {
				if (blockView.getBlockState(mutable.setPosition(o, j, p)).getMaterial().isFluid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.setPosition(o, m, p)).getMaterial().isFluid()) {
					return true;
				}
			}
		}

		for (int q = i; q <= l; q++) {
			for (int r = j; r <= m; r++) {
				if (blockView.getBlockState(mutable.setPosition(q, r, k)).getMaterial().isFluid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.setPosition(q, r, n)).getMaterial().isFluid()) {
					return true;
				}
			}
		}

		for (int s = k; s <= n; s++) {
			for (int t = j; t <= m; t++) {
				if (blockView.getBlockState(mutable.setPosition(i, t, s)).getMaterial().isFluid()) {
					return true;
				}

				if (blockView.getBlockState(mutable.setPosition(l, t, s)).getMaterial().isFluid()) {
					return true;
				}
			}
		}

		return false;
	}

	protected int applyXTransform(int x, int z) {
		Direction direction = this.method_11854();
		if (direction == null) {
			return x;
		} else {
			switch (direction) {
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
		return this.method_11854() == null ? y : y + this.boundingBox.minY;
	}

	protected int applyZTransform(int x, int z) {
		Direction direction = this.method_11854();
		if (direction == null) {
			return z;
		} else {
			switch (direction) {
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

	protected void method_56(IWorld iWorld, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (blockBox.contains(blockPos)) {
			if (this.field_13013 != BlockMirror.NONE) {
				blockState = blockState.mirror(this.field_13013);
			}

			if (this.field_13014 != BlockRotation.NONE) {
				blockState = blockState.rotate(this.field_13014);
			}

			iWorld.setBlockState(blockPos, blockState, 2);
			FluidState fluidState = iWorld.getFluidState(blockPos);
			if (!fluidState.isEmpty()) {
				iWorld.method_16340().schedule(blockPos, fluidState.getFluid(), 0);
			}

			if (field_19405.contains(blockState.getBlock())) {
				iWorld.method_16351(blockPos).method_17005(blockPos);
			}
		}
	}

	protected BlockState method_9273(BlockView blockView, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !blockBox.contains(blockPos) ? Blocks.AIR.getDefaultState() : blockView.getBlockState(blockPos);
	}

	protected boolean method_17657(RenderBlockView renderBlockView, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j + 1);
		int n = this.applyZTransform(i, k);
		BlockPos blockPos = new BlockPos(l, m, n);
		return !blockBox.contains(blockPos) ? false : m < renderBlockView.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, l, n);
	}

	protected void method_60(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					this.method_56(iWorld, Blocks.AIR.getDefaultState(), p, o, q, blockBox);
				}
			}
		}
	}

	protected void method_17653(
		IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState, BlockState blockState2, boolean bl
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.method_9273(iWorld, p, o, q, blockBox).isAir()) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.method_56(iWorld, blockState2, p, o, q, blockBox);
						} else {
							this.method_56(iWorld, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}
	}

	protected void method_17655(
		IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, boolean bl, Random random, StructurePiece.BlockRandomizer blockRandomizer
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!bl || !this.method_9273(iWorld, p, o, q, blockBox).isAir()) {
						blockRandomizer.setBlock(random, p, o, q, o == j || o == m || p == i || p == l || q == k || q == n);
						this.method_56(iWorld, blockRandomizer.getBlock(), p, o, q, blockBox);
					}
				}
			}
		}
	}

	protected void method_17656(
		IWorld iWorld,
		BlockBox blockBox,
		Random random,
		float f,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		BlockState blockState,
		BlockState blockState2,
		boolean bl,
		boolean bl2
	) {
		for (int o = j; o <= m; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = k; q <= n; q++) {
					if (!(random.nextFloat() > f) && (!bl || !this.method_9273(iWorld, p, o, q, blockBox).isAir()) && (!bl2 || this.method_17657(iWorld, p, o, q, blockBox))) {
						if (o != j && o != m && p != i && p != l && q != k && q != n) {
							this.method_56(iWorld, blockState2, p, o, q, blockBox);
						} else {
							this.method_56(iWorld, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}
	}

	protected void method_65(IWorld iWorld, BlockBox blockBox, Random random, float f, int i, int j, int k, BlockState blockState) {
		if (random.nextFloat() < f) {
			this.method_56(iWorld, blockState, i, j, k, blockBox);
		}
	}

	protected void method_17654(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState, boolean bl) {
		float f = (float)(l - i + 1);
		float g = (float)(m - j + 1);
		float h = (float)(n - k + 1);
		float o = (float)i + f / 2.0F;
		float p = (float)k + h / 2.0F;

		for (int q = j; q <= m; q++) {
			float r = (float)(q - j) / g;

			for (int s = i; s <= l; s++) {
				float t = ((float)s - o) / (f * 0.5F);

				for (int u = k; u <= n; u++) {
					float v = ((float)u - p) / (h * 0.5F);
					if (!bl || !this.method_9273(iWorld, s, q, u, blockBox).isAir()) {
						float w = t * t + r * r + v * v;
						if (w <= 1.05F) {
							this.method_56(iWorld, blockState, s, q, u, blockBox);
						}
					}
				}
			}
		}
	}

	protected void method_73(IWorld iWorld, int i, int j, int k, BlockBox blockBox) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (blockBox.contains(blockPos)) {
			while (!iWorld.method_8579(blockPos) && blockPos.getY() < 255) {
				iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
				blockPos = blockPos.up();
			}
		}
	}

	protected void method_72(IWorld iWorld, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
		int l = this.applyXTransform(i, k);
		int m = this.applyYTransform(j);
		int n = this.applyZTransform(i, k);
		if (blockBox.contains(new BlockPos(l, m, n))) {
			while ((iWorld.method_8579(new BlockPos(l, m, n)) || iWorld.getBlockState(new BlockPos(l, m, n)).getMaterial().isFluid()) && m > 1) {
				iWorld.setBlockState(new BlockPos(l, m, n), blockState, 2);
				m--;
			}
		}
	}

	protected boolean method_11852(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		return this.method_13775(iWorld, blockBox, random, blockPos, identifier, null);
	}

	public static BlockState method_17652(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = null;

		for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction2);
			BlockState blockState2 = blockView.getBlockState(blockPos2);
			if (blockState2.getBlock() == Blocks.CHEST) {
				return blockState;
			}

			if (blockState2.isFullOpaque(blockView, blockPos2)) {
				if (direction != null) {
					direction = null;
					break;
				}

				direction = direction2;
			}
		}

		if (direction != null) {
			return blockState.withProperty(HorizontalFacingBlock.FACING, direction.getOpposite());
		} else {
			Direction direction3 = blockState.getProperty(HorizontalFacingBlock.FACING);
			BlockPos blockPos3 = blockPos.offset(direction3);
			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.offset(direction3);
			}

			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.rotateYClockwise();
				blockPos3 = blockPos.offset(direction3);
			}

			if (blockView.getBlockState(blockPos3).isFullOpaque(blockView, blockPos3)) {
				direction3 = direction3.getOpposite();
				blockPos3 = blockPos.offset(direction3);
			}

			return blockState.withProperty(HorizontalFacingBlock.FACING, direction3);
		}
	}

	protected boolean method_13775(IWorld iWorld, BlockBox blockBox, Random random, BlockPos blockPos, Identifier identifier, @Nullable BlockState blockState) {
		if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.CHEST) {
			if (blockState == null) {
				blockState = method_17652(iWorld, blockPos, Blocks.CHEST.getDefaultState());
			}

			iWorld.setBlockState(blockPos, blockState, 2);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).method_11660(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean method_11851(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Direction direction, Identifier identifier) {
		BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
		if (blockBox.contains(blockPos) && iWorld.getBlockState(blockPos).getBlock() != Blocks.DISPENSER) {
			this.method_56(iWorld, Blocks.DISPENSER.getDefaultState().withProperty(DispenserBlock.FACING, direction), i, j, k, blockBox);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).method_11660(identifier, random.nextLong());
			}

			return true;
		} else {
			return false;
		}
	}

	protected void method_13377(IWorld iWorld, BlockBox blockBox, Random random, int i, int j, int k, Direction direction, DoorBlock doorBlock) {
		this.method_56(iWorld, doorBlock.getDefaultState().withProperty(DoorBlock.FACING, direction), i, j, k, blockBox);
		this.method_56(
			iWorld,
			doorBlock.getDefaultState().withProperty(DoorBlock.FACING, direction).withProperty(DoorBlock.field_18296, DoubleBlockHalf.UPPER),
			i,
			j + 1,
			k,
			blockBox
		);
	}

	public void translate(int x, int y, int z) {
		this.boundingBox.move(x, y, z);
	}

	@Nullable
	public Direction method_11854() {
		return this.facing;
	}

	public void method_11853(@Nullable Direction direction) {
		this.facing = direction;
		if (direction == null) {
			this.field_13014 = BlockRotation.NONE;
			this.field_13013 = BlockMirror.NONE;
		} else {
			switch (direction) {
				case SOUTH:
					this.field_13013 = BlockMirror.LEFT_RIGHT;
					this.field_13014 = BlockRotation.NONE;
					break;
				case WEST:
					this.field_13013 = BlockMirror.LEFT_RIGHT;
					this.field_13014 = BlockRotation.CLOCKWISE_90;
					break;
				case EAST:
					this.field_13013 = BlockMirror.NONE;
					this.field_13014 = BlockRotation.CLOCKWISE_90;
					break;
				default:
					this.field_13013 = BlockMirror.NONE;
					this.field_13014 = BlockRotation.NONE;
			}
		}
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
