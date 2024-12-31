package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelGeneratorType;

public class ChunkCache implements BlockView {
	protected int minX;
	protected int minZ;
	protected Chunk[][] chunks;
	protected boolean empty;
	protected World world;

	public ChunkCache(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		this.world = world;
		this.minX = blockPos.getX() - i >> 4;
		this.minZ = blockPos.getZ() - i >> 4;
		int j = blockPos2.getX() + i >> 4;
		int k = blockPos2.getZ() + i >> 4;
		this.chunks = new Chunk[j - this.minX + 1][k - this.minZ + 1];
		this.empty = true;

		for (int l = this.minX; l <= j; l++) {
			for (int m = this.minZ; m <= k; m++) {
				this.chunks[l - this.minX][m - this.minZ] = world.getChunk(l, m);
			}
		}

		for (int n = blockPos.getX() >> 4; n <= blockPos2.getX() >> 4; n++) {
			for (int o = blockPos.getZ() >> 4; o <= blockPos2.getZ() >> 4; o++) {
				Chunk chunk = this.chunks[n - this.minX][o - this.minZ];
				if (chunk != null && !chunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) {
					this.empty = false;
				}
			}
		}
	}

	public boolean method_3772() {
		return this.empty;
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
	public int getLight(BlockPos pos, int minBlockLight) {
		int i = this.getLightAtPos(LightType.SKY, pos);
		int j = this.getLightAtPos(LightType.BLOCK, pos);
		if (j < minBlockLight) {
			j = minBlockLight;
		}

		return i << 20 | j << 4;
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
	public Biome getBiome(BlockPos pos) {
		int i = (pos.getX() >> 4) - this.minX;
		int j = (pos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].method_11771(pos, this.world.method_3726());
	}

	private int getLightAtPos(LightType type, BlockPos pos) {
		if (type == LightType.SKY && this.world.dimension.hasNoSkylight()) {
			return 0;
		} else if (pos.getY() >= 0 && pos.getY() < 256) {
			if (this.getBlockState(pos).useNeighbourLight()) {
				int i = 0;

				for (Direction direction : Direction.values()) {
					int l = this.method_8586(type, pos.offset(direction));
					if (l > i) {
						i = l;
					}

					if (i >= 15) {
						return i;
					}
				}

				return i;
			} else {
				int m = (pos.getX() >> 4) - this.minX;
				int n = (pos.getZ() >> 4) - this.minZ;
				return this.chunks[m][n].getLightAtPos(type, pos);
			}
		} else {
			return type.defaultValue;
		}
	}

	@Override
	public boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).getMaterial() == Material.AIR;
	}

	public int method_8586(LightType type, BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.minX;
			int j = (pos.getZ() >> 4) - this.minZ;
			return this.chunks[i][j].getLightAtPos(type, pos);
		} else {
			return type.defaultValue;
		}
	}

	@Override
	public int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.world.getGeneratorType();
	}
}
