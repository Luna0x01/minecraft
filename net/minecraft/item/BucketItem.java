package net.minecraft.item;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private Block fluid;

	public BucketItem(Block block) {
		this.maxCount = 1;
		this.fluid = block;
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		boolean bl = this.fluid == Blocks.AIR;
		BlockHitResult blockHitResult = this.onHit(world, player, bl);
		if (blockHitResult == null) {
			return stack;
		} else {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!world.canPlayerModifyAt(player, blockPos)) {
					return stack;
				}

				if (bl) {
					if (!player.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, stack)) {
						return stack;
					}

					BlockState blockState = world.getBlockState(blockPos);
					Material material = blockState.getBlock().getMaterial();
					if (material == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
						world.setAir(blockPos);
						player.incrementStat(Stats.USED[Item.getRawId(this)]);
						return this.fill(stack, player, Items.WATER_BUCKET);
					}

					if (material == Material.LAVA && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
						world.setAir(blockPos);
						player.incrementStat(Stats.USED[Item.getRawId(this)]);
						return this.fill(stack, player, Items.LAVA_BUCKET);
					}
				} else {
					if (this.fluid == Blocks.AIR) {
						return new ItemStack(Items.BUCKET);
					}

					BlockPos blockPos2 = blockPos.offset(blockHitResult.direction);
					if (!player.canModify(blockPos2, blockHitResult.direction, stack)) {
						return stack;
					}

					if (this.empty(world, blockPos2) && !player.abilities.creativeMode) {
						player.incrementStat(Stats.USED[Item.getRawId(this)]);
						return new ItemStack(Items.BUCKET);
					}
				}
			}

			return stack;
		}
	}

	private ItemStack fill(ItemStack stack, PlayerEntity player, Item item) {
		if (player.abilities.creativeMode) {
			return stack;
		} else if (--stack.count <= 0) {
			return new ItemStack(item);
		} else {
			if (!player.inventory.insertStack(new ItemStack(item))) {
				player.dropItem(new ItemStack(item, 1, 0), false);
			}

			return stack;
		}
	}

	public boolean empty(World world, BlockPos pos) {
		if (this.fluid == Blocks.AIR) {
			return false;
		} else {
			Material material = world.getBlockState(pos).getBlock().getMaterial();
			boolean bl = !material.isSolid();
			if (!world.isAir(pos) && !bl) {
				return false;
			} else {
				if (world.dimension.doesWaterVaporize() && this.fluid == Blocks.FLOWING_WATER) {
					int i = pos.getX();
					int j = pos.getY();
					int k = pos.getZ();
					world.playSound(
						(double)((float)i + 0.5F),
						(double)((float)j + 0.5F),
						(double)((float)k + 0.5F),
						"random.fizz",
						0.5F,
						2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleType.SMOKE_LARGE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else {
					if (!world.isClient && bl && !material.isFluid()) {
						world.removeBlock(pos, true);
					}

					world.setBlockState(pos, this.fluid.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
