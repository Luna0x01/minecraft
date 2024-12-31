package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DeadBushBlock extends PlantBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected DeadBushBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		Block block = state.getBlock();
		return block == Blocks.SAND
			|| block == Blocks.RED_SAND
			|| block == Blocks.TERRACOTTA
			|| block == Blocks.WHITE_TERRACOTTA
			|| block == Blocks.ORANGE_TERRACOTTA
			|| block == Blocks.MAGENTA_TERRACOTTA
			|| block == Blocks.LIGHT_BLUE_TERRACOTTA
			|| block == Blocks.YELLOW_TERRACOTTA
			|| block == Blocks.LIME_TERRACOTTA
			|| block == Blocks.PINK_TERRACOTTA
			|| block == Blocks.GRAY_TERRACOTTA
			|| block == Blocks.LIGHT_GRAY_TERRACOTTA
			|| block == Blocks.CYAN_TERRACOTTA
			|| block == Blocks.PURPLE_TERRACOTTA
			|| block == Blocks.BLUE_TERRACOTTA
			|| block == Blocks.BROWN_TERRACOTTA
			|| block == Blocks.GREEN_TERRACOTTA
			|| block == Blocks.RED_TERRACOTTA
			|| block == Blocks.BLACK_TERRACOTTA
			|| block == Blocks.DIRT
			|| block == Blocks.COARSE_DIRT
			|| block == Blocks.PODZOL;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return random.nextInt(3);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.STICK;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		boolean bl = !world.isClient && stack.getItem() == Items.SHEARS;
		if (bl) {
			onBlockBreak(world, pos, new ItemStack(Blocks.DEAD_BUSH));
		}

		super.method_8651(world, player, pos, bl ? Blocks.AIR.getDefaultState() : state, blockEntity, stack);
	}
}
