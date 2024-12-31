package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock {
	public IceBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return Blocks.WATER.getDefaultState().method_16885(world, pos);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		player.method_15932(Stats.MINED.method_21429(this));
		player.addExhaustion(0.005F);
		if (this.requiresSilkTouch() && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			onBlockBreak(world, pos, this.createStackFromBlock(state));
		} else {
			if (world.dimension.doesWaterVaporize()) {
				world.method_8553(pos);
				return;
			}

			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			state.method_16867(world, pos, i);
			Material material = world.getBlockState(pos.down()).getMaterial();
			if (material.blocksMovement() || material.isFluid()) {
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
			}
		}
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.method_16370(LightType.BLOCK, pos) > 11 - state.method_16885(world, pos)) {
			this.method_11617(state, world, pos);
		}
	}

	protected void method_11617(BlockState blockState, World world, BlockPos blockPos) {
		if (world.dimension.doesWaterVaporize()) {
			world.method_8553(blockPos);
		} else {
			blockState.method_16867(world, blockPos, 0);
			world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
			world.updateNeighbor(blockPos, Blocks.WATER, blockPos);
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}
}
