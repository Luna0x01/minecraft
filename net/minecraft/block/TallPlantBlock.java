package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TallPlantBlock extends PlantBlock implements Growable {
	protected static final VoxelShape field_18526 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected TallPlantBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18526;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return world.random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
	}

	@Override
	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		return 1 + random.nextInt(i * 2 + 1);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.method_15932(Stats.MINED.method_21429(this));
			player.addExhaustion(0.005F);
			onBlockBreak(world, pos, new ItemStack(this));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		DoublePlantBlock doublePlantBlock = (DoublePlantBlock)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
		if (doublePlantBlock.getDefaultState().canPlaceAt(world, pos) && world.method_8579(pos.up())) {
			doublePlantBlock.method_16669(world, pos, 2);
		}
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XYZ;
	}
}
