package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnchantingTableBlock extends BlockWithEntity {
	protected static final VoxelShape field_18304 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected EnchantingTableBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18304;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (random.nextInt(16) == 0) {
					for (int k = 0; k <= 1; k++) {
						BlockPos blockPos = pos.add(i, k, j);
						if (world.getBlockState(blockPos).getBlock() == Blocks.BOOKSHELF) {
							if (!world.method_8579(pos.add(i / 2, 0, j / 2))) {
								break;
							}

							world.method_16343(
								class_4342.field_21391,
								(double)pos.getX() + 0.5,
								(double)pos.getY() + 2.0,
								(double)pos.getZ() + 0.5,
								(double)((float)i + random.nextFloat()) - 0.5,
								(double)((float)k - random.nextFloat() - 1.0F),
								(double)((float)j + random.nextFloat()) - 0.5
							);
						}
					}
				}
			}
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EnchantingTableBlockEntity();
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				player.openHandledScreen((EnchantingTableBlockEntity)blockEntity);
			}

			return true;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof EnchantingTableBlockEntity) {
				((EnchantingTableBlockEntity)blockEntity).method_16811(itemStack.getName());
			}
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
