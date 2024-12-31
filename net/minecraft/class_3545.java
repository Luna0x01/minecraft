package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Growable;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3545 extends DyeItem {
	public class_3545(DyeColor dyeColor, Item.Settings settings) {
		super(dyeColor, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(itemUsageContext.method_16151());
		if (method_16022(itemUsageContext.getItemStack(), world, blockPos)) {
			if (!world.isClient) {
				world.syncGlobalEvent(2005, blockPos, 0);
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = blockState.getRenderLayer(world, blockPos, itemUsageContext.method_16151()) == BlockRenderLayer.SOLID;
			if (bl && method_16023(itemUsageContext.getItemStack(), world, blockPos2, itemUsageContext.method_16151())) {
				if (!world.isClient) {
					world.syncGlobalEvent(2005, blockPos2, 0);
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public static boolean method_16022(ItemStack itemStack, World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof Growable) {
			Growable growable = (Growable)blockState.getBlock();
			if (growable.isFertilizable(world, blockPos, blockState, world.isClient)) {
				if (!world.isClient) {
					if (growable.canBeFertilized(world, world.random, blockPos, blockState)) {
						growable.grow(world, world.random, blockPos, blockState);
					}

					itemStack.decrement(1);
				}

				return true;
			}
		}

		return false;
	}

	public static boolean method_16023(ItemStack itemStack, World world, BlockPos blockPos, @Nullable Direction direction) {
		if (world.getBlockState(blockPos).getBlock() == Blocks.WATER && world.getFluidState(blockPos).method_17811() == 8) {
			if (!world.isClient) {
				label79:
				for (int i = 0; i < 128; i++) {
					BlockPos blockPos2 = blockPos;
					Biome biome = world.method_8577(blockPos);
					BlockState blockState = Blocks.SEAGRASS.getDefaultState();

					for (int j = 0; j < i / 16; j++) {
						blockPos2 = blockPos2.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
						biome = world.method_8577(blockPos2);
						if (world.getBlockState(blockPos2).method_16905()) {
							continue label79;
						}
					}

					if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
						if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
							blockState = BlockTags.WALL_CORALS.getRandom(world.random).getDefaultState().withProperty(DeadCoralWallFanBlock.FACING, direction);
						} else if (RANDOM.nextInt(4) == 0) {
							blockState = BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM).getDefaultState();
						}
					}

					if (blockState.getBlock().isIn(BlockTags.WALL_CORALS)) {
						for (int k = 0; !blockState.canPlaceAt(world, blockPos2) && k < 4; k++) {
							blockState = blockState.withProperty(DeadCoralWallFanBlock.FACING, Direction.DirectionType.HORIZONTAL.getRandomDirection(RANDOM));
						}
					}

					if (blockState.canPlaceAt(world, blockPos2)) {
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.getBlock() == Blocks.WATER && world.getFluidState(blockPos2).method_17811() == 8) {
							world.setBlockState(blockPos2, blockState, 3);
						} else if (blockState2.getBlock() == Blocks.SEAGRASS && RANDOM.nextInt(10) == 0) {
							((Growable)Blocks.SEAGRASS).grow(world, RANDOM, blockPos2, blockState2);
						}
					}
				}

				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}

	public static void method_16024(IWorld iWorld, BlockPos blockPos, int i) {
		if (i == 0) {
			i = 15;
		}

		BlockState blockState = iWorld.getBlockState(blockPos);
		if (!blockState.isAir()) {
			for (int j = 0; j < i; j++) {
				double d = RANDOM.nextGaussian() * 0.02;
				double e = RANDOM.nextGaussian() * 0.02;
				double f = RANDOM.nextGaussian() * 0.02;
				iWorld.method_16343(
					class_4342.field_21400,
					(double)((float)blockPos.getX() + RANDOM.nextFloat()),
					(double)blockPos.getY() + (double)RANDOM.nextFloat() * blockState.getOutlineShape(iWorld, blockPos).getMaximum(Direction.Axis.Y),
					(double)((float)blockPos.getZ() + RANDOM.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}
}
