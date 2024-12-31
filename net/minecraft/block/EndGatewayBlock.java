package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndGatewayBlock extends BlockWithEntity {
	protected EndGatewayBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EndGatewayBlockEntity();
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			int i = ((EndGatewayBlockEntity)blockEntity).method_11697();

			for (int j = 0; j < i; j++) {
				double d = (double)((float)pos.getX() + random.nextFloat());
				double e = (double)((float)pos.getY() + random.nextFloat());
				double f = (double)((float)pos.getZ() + random.nextFloat());
				double g = ((double)random.nextFloat() - 0.5) * 0.5;
				double h = ((double)random.nextFloat() - 0.5) * 0.5;
				double k = ((double)random.nextFloat() - 0.5) * 0.5;
				int l = random.nextInt(2) * 2 - 1;
				if (random.nextBoolean()) {
					f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
					k = (double)(random.nextFloat() * 2.0F * (float)l);
				} else {
					d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
					g = (double)(random.nextFloat() * 2.0F * (float)l);
				}

				world.method_16343(class_4342.field_21361, d, e, f, g, h, k);
			}
		}
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
