package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
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
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
		player.addExhaustion(0.025F);
		if (this.requiresSilkTouch() && EnchantmentHelper.hasSilkTouch(player)) {
			ItemStack itemStack = this.createStackFromBlock(state);
			if (itemStack != null) {
				onBlockBreak(world, pos, itemStack);
			}
		} else {
			if (world.dimension.doesWaterVaporize()) {
				world.setAir(pos);
				return;
			}

			int i = EnchantmentHelper.getFortune(player);
			this.dropAsItem(world, pos, state, i);
			Material material = world.getBlockState(pos.down()).getBlock().getMaterial();
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
		if (world.getLightAtPos(LightType.BLOCK, pos) > 11 - this.getOpacity()) {
			if (world.dimension.doesWaterVaporize()) {
				world.setAir(pos);
			} else {
				this.dropAsItem(world, pos, world.getBlockState(pos), 0);
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
			}
		}
	}

	@Override
	public int getPistonInteractionType() {
		return 0;
	}
}
