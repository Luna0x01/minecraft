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
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock {
	public IceBlock() {
		super(Material.ICE, false);
		this.slipperiness = 0.98F;
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		player.incrementStat(Stats.mined(this));
		player.addExhaustion(0.025F);
		if (this.requiresSilkTouch() && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			ItemStack itemStack = this.createStackFromBlock(state);
			if (itemStack != null) {
				onBlockBreak(world, pos, itemStack);
			}
		} else {
			if (world.dimension.doesWaterVaporize()) {
				world.setAir(pos);
				return;
			}

			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			this.dropAsItem(world, pos, state, i);
			Material material = world.getBlockState(pos.down()).getMaterial();
			if (material.blocksMovement() || material.isFluid()) {
				world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.getLightAtPos(LightType.BLOCK, pos) > 11 - this.getDefaultState().getOpacity()) {
			this.method_11617(world, pos);
		}
	}

	protected void method_11617(World world, BlockPos blockPos) {
		if (world.dimension.doesWaterVaporize()) {
			world.setAir(blockPos);
		} else {
			this.dropAsItem(world, blockPos, world.getBlockState(blockPos), 0);
			world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
			world.neighbourUpdate(blockPos, Blocks.WATER);
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}
}
