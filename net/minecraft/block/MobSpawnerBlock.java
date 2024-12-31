package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MobSpawnerBlock extends BlockWithEntity {
	protected MobSpawnerBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new MobSpawnerBlockEntity();
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		int j = 15 + world.random.nextInt(15) + world.random.nextInt(15);
		this.dropExperience(world, blockPos, j);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
