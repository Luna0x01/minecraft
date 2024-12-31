package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneLampBlock extends Block {
	private final boolean powered;

	public RedstoneLampBlock(boolean bl) {
		super(Material.REDSTONE_LAMP);
		this.powered = bl;
		if (bl) {
			this.setLightLevel(1.0F);
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			if (this.powered && !world.isReceivingRedstonePower(pos)) {
				world.setBlockState(pos, Blocks.REDSTONE_LAMP.getDefaultState(), 2);
			} else if (!this.powered && world.isReceivingRedstonePower(pos)) {
				world.setBlockState(pos, Blocks.LIT_REDSTONE_LAMP.getDefaultState(), 2);
			}
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			if (this.powered && !world.isReceivingRedstonePower(blockPos)) {
				world.createAndScheduleBlockTick(blockPos, this, 4);
			} else if (!this.powered && world.isReceivingRedstonePower(blockPos)) {
				world.setBlockState(blockPos, Blocks.LIT_REDSTONE_LAMP.getDefaultState(), 2);
			}
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if (this.powered && !world.isReceivingRedstonePower(pos)) {
				world.setBlockState(pos, Blocks.REDSTONE_LAMP.getDefaultState(), 2);
			}
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.REDSTONE_LAMP);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.REDSTONE_LAMP);
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(Blocks.REDSTONE_LAMP);
	}
}
