package net.minecraft.world.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3600;
import net.minecraft.class_3804;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;

public class ChunkCache implements class_3600 {
	protected int minX;
	protected int minZ;
	protected Chunk[][] chunks;
	protected boolean field_17511;
	protected World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		this.world = world;
		this.minX = blockPos.getX() - i >> 4;
		this.minZ = blockPos.getZ() - i >> 4;
		int j = blockPos2.getX() + i >> 4;
		int k = blockPos2.getZ() + i >> 4;
		this.chunks = new Chunk[j - this.minX + 1][k - this.minZ + 1];
		this.field_17511 = true;

		for (int l = this.minX; l <= j; l++) {
			for (int m = this.minZ; m <= k; m++) {
				this.chunks[l - this.minX][m - this.minZ] = world.method_16347(l, m);
			}
		}

		for (int n = blockPos.getX() >> 4; n <= blockPos2.getX() >> 4; n++) {
			for (int o = blockPos.getZ() >> 4; o <= blockPos2.getZ() >> 4; o++) {
				Chunk chunk = this.chunks[n - this.minX][o - this.minZ];
				if (chunk != null && !chunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) {
					this.field_17511 = false;
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.method_13314(pos, Chunk.Status.IMMEDIATE);
	}

	@Nullable
	public BlockEntity method_13314(BlockPos blockPos, Chunk.Status status) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].getBlockEntity(blockPos, status);
	}

	@Override
	public int method_8578(BlockPos blockPos, int i) {
		int j = this.getLightAtPos(LightType.SKY, blockPos);
		int k = this.getLightAtPos(LightType.BLOCK, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}

	@Override
	public float method_16356(BlockPos blockPos) {
		return this.world.dimension.getLightLevelToBrightness()[this.method_16358(blockPos)];
	}

	@Override
	public int method_16389(BlockPos blockPos, int i) {
		if (this.getBlockState(blockPos).method_16889(this, blockPos)) {
			int j = 0;

			for (Direction direction : Direction.values()) {
				int k = this.method_16379(blockPos.offset(direction), i);
				if (k > j) {
					j = k;
				}

				if (j >= 15) {
					return j;
				}
			}

			return j;
		} else {
			return this.method_16379(blockPos, i);
		}
	}

	@Override
	public Dimension method_16393() {
		return this.world.method_16393();
	}

	@Override
	public int method_16379(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() > 30000000) {
			return 15;
		} else if (blockPos.getY() < 0) {
			return 0;
		} else if (blockPos.getY() >= 256) {
			int j = 15 - i;
			if (j < 0) {
				j = 0;
			}

			return j;
		} else {
			int k = (blockPos.getX() >> 4) - this.minX;
			int l = (blockPos.getZ() >> 4) - this.minZ;
			return this.chunks[k][l].getLightLevel(blockPos, i);
		}
	}

	@Override
	public boolean method_8487(int i, int j, boolean bl) {
		return this.method_16408(i, j);
	}

	@Override
	public boolean method_8555(BlockPos blockPos) {
		return false;
	}

	public boolean method_16408(int i, int j) {
		int k = i - this.minX;
		int l = j - this.minZ;
		return k >= 0 && k < this.chunks.length && l >= 0 && l < this.chunks[k].length;
	}

	@Override
	public int method_16372(class_3804.class_3805 arg, int i, int j) {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public WorldBorder method_8524() {
		return this.world.method_8524();
	}

	@Override
	public boolean method_16368(@Nullable Entity entity, VoxelShape voxelShape) {
		throw new RuntimeException("This method should never be called here. No entity logic inside Region");
	}

	@Nullable
	@Override
	public PlayerEntity method_16360(double d, double e, double f, double g, Predicate<Entity> predicate) {
		throw new RuntimeException("This method should never be called here. No entity logic inside Region");
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.minX;
			int j = (pos.getZ() >> 4) - this.minZ;
			if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
				Chunk chunk = this.chunks[i][j];
				if (chunk != null) {
					return chunk.getBlockState(pos);
				}
			}
		}

		return Blocks.AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.minX;
			int j = (pos.getZ() >> 4) - this.minZ;
			if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
				Chunk chunk = this.chunks[i][j];
				if (chunk != null) {
					return chunk.getFluidState(pos);
				}
			}
		}

		return Fluids.EMPTY.getDefaultState();
	}

	@Override
	public int method_8520() {
		return 0;
	}

	@Override
	public Biome method_8577(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.minX;
		int j = (blockPos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].method_17088(blockPos);
	}

	private int getLightAtPos(LightType type, BlockPos pos) {
		if (type == LightType.SKY && !this.world.method_16393().isOverworld()) {
			return 0;
		} else if (pos.getY() >= 0 && pos.getY() < 256) {
			if (this.getBlockState(pos).method_16889(this, pos)) {
				int i = 0;

				for (Direction direction : Direction.values()) {
					int j = this.method_16370(type, pos.offset(direction));
					if (j > i) {
						i = j;
					}

					if (i >= 15) {
						return i;
					}
				}

				return i;
			} else {
				int k = (pos.getX() >> 4) - this.minX;
				int l = (pos.getZ() >> 4) - this.minZ;
				return this.chunks[k][l].method_17071(type, pos);
			}
		} else {
			return type.defaultValue;
		}
	}

	@Override
	public boolean method_8579(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	@Override
	public int method_16370(LightType lightType, BlockPos blockPos) {
		if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
			int i = (blockPos.getX() >> 4) - this.minX;
			int j = (blockPos.getZ() >> 4) - this.minZ;
			return this.chunks[i][j].method_17071(lightType, blockPos);
		} else {
			return lightType.defaultValue;
		}
	}

	@Override
	public int method_8576(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	@Override
	public boolean method_16390() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public int method_8483() {
		throw new RuntimeException("Not yet implemented");
	}
}
