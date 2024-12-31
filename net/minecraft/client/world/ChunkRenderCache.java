package net.minecraft.client.world;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;

public class ChunkRenderCache extends ChunkCache {
	private static final BlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
	private final BlockPos startPos;
	private int[] field_10663;
	private BlockState[] field_10664;

	public ChunkRenderCache(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		super(world, blockPos, blockPos2, i);
		this.startPos = blockPos.subtract(new Vec3i(i, i, i));
		int j = 8000;
		this.field_10663 = new int[8000];
		Arrays.fill(this.field_10663, -1);
		this.field_10664 = new BlockState[8000];
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		int i = (pos.getX() >> 4) - this.minX;
		int j = (pos.getZ() >> 4) - this.minZ;
		return this.chunks[i][j].getBlockEntity(pos, Chunk.Status.QUEUED);
	}

	@Override
	public int getLight(BlockPos pos, int minBlockLight) {
		int i = this.method_9765(pos);
		int j = this.field_10663[i];
		if (j == -1) {
			j = super.getLight(pos, minBlockLight);
			this.field_10663[i] = j;
		}

		return j;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = this.method_9765(pos);
		BlockState blockState = this.field_10664[i];
		if (blockState == null) {
			blockState = this.method_9764(pos);
			this.field_10664[i] = blockState;
		}

		return blockState;
	}

	private BlockState method_9764(BlockPos blockPos) {
		if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
			int i = (blockPos.getX() >> 4) - this.minX;
			int j = (blockPos.getZ() >> 4) - this.minZ;
			return this.chunks[i][j].method_9154(blockPos);
		} else {
			return DEFAULT_STATE;
		}
	}

	private int method_9765(BlockPos blockPos) {
		int i = blockPos.getX() - this.startPos.getX();
		int j = blockPos.getY() - this.startPos.getY();
		int k = blockPos.getZ() - this.startPos.getZ();
		return i * 400 + k * 20 + j;
	}
}
