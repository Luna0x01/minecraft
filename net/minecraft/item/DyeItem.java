package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Growable;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return false;
		} else {
			DyeColor dyeColor = DyeColor.getById(itemStack.getData());
			if (dyeColor == DyeColor.WHITE) {
				if (fertilize(itemStack, world, pos)) {
					if (!world.isClient) {
						world.syncGlobalEvent(2005, pos, 0);
					}

					return true;
				}
			} else if (dyeColor == DyeColor.BROWN) {
				BlockState blockState = world.getBlockState(pos);
				Block block = blockState.getBlock();
				if (block == Blocks.LOG && blockState.get(PlanksBlock.VARIANT) == PlanksBlock.WoodType.JUNGLE) {
					if (direction == Direction.DOWN) {
						return false;
					}

					if (direction == Direction.UP) {
						return false;
					}

					pos = pos.offset(direction);
					if (world.isAir(pos)) {
						BlockState blockState2 = Blocks.COCOA.getStateFromData(world, pos, direction, facingX, facingY, facingZ, 0, player);
						world.setBlockState(pos, blockState2, 2);
						if (!player.abilities.creativeMode) {
							itemStack.count--;
						}
					}

					return true;
				}
			}

			return false;
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

		Block block = world.getBlockState(pos).getBlock();
		if (block.getMaterial() != Material.AIR) {
			block.setBoundingBox(world, pos);

			for (int j = 0; j < i; j++) {
				double d = RANDOM.nextGaussian() * 0.02;
				double e = RANDOM.nextGaussian() * 0.02;
				double f = RANDOM.nextGaussian() * 0.02;
				world.addParticle(
					ParticleType.HAPPY_VILLAGER,
					(double)((float)pos.getX() + RANDOM.nextFloat()),
					(double)pos.getY() + (double)RANDOM.nextFloat() * block.getMaxY(),
					(double)((float)pos.getZ() + RANDOM.nextFloat()),
					d,
					e,
					f
				);
			}
		}
	}

	@Override
	public boolean canUseOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity) {
		if (entity instanceof SheepEntity) {
			SheepEntity sheepEntity = (SheepEntity)entity;
			DyeColor dyeColor = DyeColor.getById(stack.getData());
			if (!sheepEntity.isSheared() && sheepEntity.getColor() != dyeColor) {
				sheepEntity.setColor(dyeColor);
				stack.count--;
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
