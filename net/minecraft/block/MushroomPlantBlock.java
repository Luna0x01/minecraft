package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3871;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class MushroomPlantBlock extends PlantBlock implements Growable {
	protected static final VoxelShape field_18408 = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

	public MushroomPlantBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18408;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (BlockPos blockPos : BlockPos.mutableIterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
				if (world.getBlockState(blockPos).getBlock() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (world.method_8579(blockPos2) && state.canPlaceAt(world, blockPos2)) {
					pos = blockPos2;
				}

				blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			}

			if (world.method_8579(blockPos2) && state.canPlaceAt(world, blockPos2)) {
				world.setBlockState(blockPos2, state, 2);
			}
		}
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return state.isFullOpaque(world, pos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block != Blocks.MYCELIUM && block != Blocks.PODZOL ? world.method_16379(pos, 0) < 13 && this.canPlantOnTop(blockState, world, blockPos) : true;
	}

	public boolean method_16703(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		iWorld.method_8553(blockPos);
		class_3844<class_3871> lv = null;
		if (this == Blocks.BROWN_MUSHROOM) {
			lv = class_3844.field_19141;
		} else if (this == Blocks.RED_MUSHROOM) {
			lv = class_3844.field_19140;
		}

		if (lv != null
			&& lv.method_17343(iWorld, (ChunkGenerator<? extends class_3798>)iWorld.method_3586().method_17046(), random, blockPos, class_3845.field_19203)) {
			return true;
		} else {
			iWorld.setBlockState(blockPos, blockState, 3);
			return false;
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.method_16703(world, pos, state, random);
	}

	@Override
	public boolean method_16592(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
