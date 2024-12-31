package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Growable;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DyeItem extends Item {
	public static final int[] COLORS = new int[]{
		1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
	};

	public DyeItem() {
		this.setUnbreakable(true);
		this.setMaxDamage(0);
		this.setItemGroup(ItemGroup.MATERIALS);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		int i = stack.getData();
		return super.getTranslationKey() + "." + DyeColor.getById(i).getTranslationKey();
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!playerEntity.canModify(blockPos.offset(direction), direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			DyeColor dyeColor = DyeColor.getById(itemStack.getData());
			if (dyeColor == DyeColor.WHITE) {
				if (fertilize(itemStack, world, blockPos)) {
					if (!world.isClient) {
						world.syncGlobalEvent(2005, blockPos, 0);
					}

					return ActionResult.SUCCESS;
				}
			} else if (dyeColor == DyeColor.BROWN) {
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block == Blocks.LOG && blockState.get(Log1Block.VARIANT) == PlanksBlock.WoodType.JUNGLE) {
					if (direction != Direction.DOWN && direction != Direction.UP) {
						blockPos = blockPos.offset(direction);
						if (world.isAir(blockPos)) {
							BlockState blockState2 = Blocks.COCOA.getStateFromData(world, blockPos, direction, f, g, h, 0, playerEntity);
							world.setBlockState(blockPos, blockState2, 10);
							if (!playerEntity.abilities.creativeMode) {
								itemStack.count--;
							}
						}

						return ActionResult.SUCCESS;
					}

					return ActionResult.FAIL;
				}

				return ActionResult.FAIL;
			}

			return ActionResult.PASS;
		}
	}

	public static boolean fertilize(ItemStack stack, World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() instanceof Growable) {
			Growable growable = (Growable)blockState.getBlock();
			if (growable.canGrow(world, pos, blockState, world.isClient)) {
				if (!world.isClient) {
					if (growable.canBeFertilized(world, world.random, pos, blockState)) {
						growable.grow(world, world.random, pos, blockState);
					}

					stack.count--;
				}

				return true;
			}
		}

		return false;
	}

	public static void spawnParticles(World world, BlockPos pos, int i) {
		if (i == 0) {
			i = 15;
		}

		BlockState blockState = world.getBlockState(pos);
		if (blockState.getMaterial() != Material.AIR) {
			for (int j = 0; j < i; j++) {
				double d = RANDOM.nextGaussian() * 0.02;
				double e = RANDOM.nextGaussian() * 0.02;
				double f = RANDOM.nextGaussian() * 0.02;
				world.addParticle(
					ParticleType.HAPPY_VILLAGER,
					(double)((float)pos.getX() + RANDOM.nextFloat()),
					(double)pos.getY() + (double)RANDOM.nextFloat() * blockState.getCollisionBox((BlockView)world, pos).maxY,
					(double)((float)pos.getZ() + RANDOM.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}

	@Override
	public boolean method_3353(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof SheepEntity) {
			SheepEntity sheepEntity = (SheepEntity)livingEntity;
			DyeColor dyeColor = DyeColor.getById(itemStack.getData());
			if (!sheepEntity.isSheared() && sheepEntity.getColor() != dyeColor) {
				sheepEntity.setColor(dyeColor);
				itemStack.count--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (int i = 0; i < 16; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
}
