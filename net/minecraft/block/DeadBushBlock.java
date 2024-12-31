package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DeadBushBlock extends PlantBlock {
	protected static final Box field_12640 = new Box(0.099999994F, 0.0, 0.099999994F, 0.9F, 0.8F, 0.9F);

	protected DeadBushBlock() {
		super(Material.REPLACEABLE_PLANT);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12640;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.WOOD;
	}

	@Override
	protected boolean method_11579(BlockState blockState) {
		return blockState.getBlock() == Blocks.SAND
			|| blockState.getBlock() == Blocks.TERRACOTTA
			|| blockState.getBlock() == Blocks.STAINED_TERRACOTTA
			|| blockState.getBlock() == Blocks.DIRT;
	}

	@Override
	public boolean method_8638(BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public int getDropCount(Random rand) {
		return rand.nextInt(3);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.STICK;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		if (!world.isClient && stack != null && stack.getItem() == Items.SHEARS) {
			player.incrementStat(Stats.mined(this));
			onBlockBreak(world, pos, new ItemStack(Blocks.DEADBUSH, 1, 0));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}
}
