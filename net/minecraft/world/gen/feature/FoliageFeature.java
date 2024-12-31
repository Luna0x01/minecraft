package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public abstract class FoliageFeature<T extends class_3845> extends class_3844<T> {
	public FoliageFeature(boolean bl) {
		super(bl);
	}

	protected boolean isBlockReplaceable(Block block) {
		BlockState blockState = block.getDefaultState();
		return blockState.isAir()
			|| blockState.isIn(BlockTags.LEAVES)
			|| block == Blocks.GRASS_BLOCK
			|| Block.method_16588(block)
			|| block.isIn(BlockTags.LOGS)
			|| block.isIn(BlockTags.SAPLINGS)
			|| block == Blocks.VINE;
	}

	protected void method_17292(IWorld iWorld, BlockPos blockPos) {
		if (!Block.method_16588(iWorld.getBlockState(blockPos).getBlock())) {
			this.method_17344(iWorld, blockPos, Blocks.DIRT.getDefaultState());
		}
	}

	@Override
	protected void method_17344(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_17295(iWorld, blockPos, blockState);
	}

	protected final void method_17293(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_17295(iWorld, blockPos, blockState);
		if (BlockTags.LOGS.contains(blockState.getBlock())) {
			set.add(blockPos.toImmutable());
		}
	}

	private void method_17295(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if (this.field_19153) {
			iWorld.setBlockState(blockPos, blockState, 19);
		} else {
			iWorld.setBlockState(blockPos, blockState, 18);
		}
	}

	@Override
	public final boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, T arg) {
		Set<BlockPos> set = Sets.newHashSet();
		boolean bl = this.method_17294(set, iWorld, random, blockPos);
		List<Set<BlockPos>> list = Lists.newArrayList();
		int i = 6;

		for (int j = 0; j < 6; j++) {
			list.add(Sets.newHashSet());
		}

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			if (bl && !set.isEmpty()) {
				for (BlockPos blockPos2 : Lists.newArrayList(set)) {
					for (Direction direction : Direction.values()) {
						pooled.set(blockPos2).move(direction);
						if (!set.contains(pooled)) {
							BlockState blockState = iWorld.getBlockState(pooled);
							if (blockState.method_16933(Properties.DISTANCE_1_7)) {
								((Set)list.get(0)).add(pooled.toImmutable());
								this.method_17295(iWorld, pooled, blockState.withProperty(Properties.DISTANCE_1_7, Integer.valueOf(1)));
							}
						}
					}
				}
			}

			for (int k = 1; k < 6; k++) {
				Set<BlockPos> set2 = (Set<BlockPos>)list.get(k - 1);
				Set<BlockPos> set3 = (Set<BlockPos>)list.get(k);

				for (BlockPos blockPos3 : set2) {
					for (Direction direction2 : Direction.values()) {
						pooled.set(blockPos3).move(direction2);
						if (!set2.contains(pooled) && !set3.contains(pooled)) {
							BlockState blockState2 = iWorld.getBlockState(pooled);
							if (blockState2.method_16933(Properties.DISTANCE_1_7)) {
								int l = (Integer)blockState2.getProperty(Properties.DISTANCE_1_7);
								if (l > k + 1) {
									BlockState blockState3 = blockState2.withProperty(Properties.DISTANCE_1_7, Integer.valueOf(k + 1));
									this.method_17295(iWorld, pooled, blockState3);
									set3.add(pooled.toImmutable());
								}
							}
						}
					}
				}
			}
		}

		return bl;
	}

	protected abstract boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos);
}
