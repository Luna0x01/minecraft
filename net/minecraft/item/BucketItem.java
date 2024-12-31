package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private final Block fluid;

	public BucketItem(Block block) {
		this.maxCount = 1;
		this.fluid = block;
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		boolean bl = this.fluid == Blocks.AIR;
		ItemStack itemStack = player.getStackInHand(hand);
		BlockHitResult blockHitResult = this.onHit(world, player, bl);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else if (blockHitResult.type != BlockHitResult.Type.BLOCK) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!world.canPlayerModifyAt(player, blockPos)) {
				return new TypedActionResult<>(ActionResult.FAIL, itemStack);
			} else if (bl) {
				if (!player.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				} else {
					BlockState blockState = world.getBlockState(blockPos);
					Material material = blockState.getMaterial();
					if (material == Material.WATER && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
						world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
						player.incrementStat(Stats.used(this));
						player.playSound(Sounds.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						return new TypedActionResult<>(ActionResult.SUCCESS, this.fill(itemStack, player, Items.WATER_BUCKET));
					} else if (material == Material.LAVA && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
						player.playSound(Sounds.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
						world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
						player.incrementStat(Stats.used(this));
						return new TypedActionResult<>(ActionResult.SUCCESS, this.fill(itemStack, player, Items.LAVA_BUCKET));
					} else {
						return new TypedActionResult<>(ActionResult.FAIL, itemStack);
					}
				}
			} else {
				boolean bl2 = world.getBlockState(blockPos).getBlock().method_8638(world, blockPos);
				BlockPos blockPos2 = bl2 && blockHitResult.direction == Direction.UP ? blockPos : blockPos.offset(blockHitResult.direction);
				if (!player.canModify(blockPos2, blockHitResult.direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				} else if (this.method_11365(player, world, blockPos2)) {
					player.incrementStat(Stats.used(this));
					return !player.abilities.creativeMode
						? new TypedActionResult<>(ActionResult.SUCCESS, new ItemStack(Items.BUCKET))
						: new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				} else {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}
			}
		}
	}

	private ItemStack fill(ItemStack stack, PlayerEntity player, Item item) {
		if (player.abilities.creativeMode) {
			return stack;
		} else {
			stack.decrement(1);
			if (stack.isEmpty()) {
				return new ItemStack(item);
			} else {
				if (!player.inventory.insertStack(new ItemStack(item))) {
					player.dropItem(new ItemStack(item), false);
				}

				return stack;
			}
		}
	}

	public boolean method_11365(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos) {
		if (this.fluid == Blocks.AIR) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			boolean bl = !material.isSolid();
			boolean bl2 = blockState.getBlock().method_8638(world, blockPos);
			if (!world.isAir(blockPos) && !bl && !bl2) {
				return false;
			} else {
				if (world.dimension.doesWaterVaporize() && this.fluid == Blocks.FLOWING_WATER) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.method_11486(
						playerEntity, blockPos, Sounds.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleType.SMOKE_LARGE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else {
					if (!world.isClient && (bl || bl2) && !material.isFluid()) {
						world.removeBlock(blockPos, true);
					}

					Sound sound = this.fluid == Blocks.FLOWING_LAVA ? Sounds.ITEM_BUCKET_EMPTY_LAVA : Sounds.ITEM_BUCKET_EMPTY;
					world.method_11486(playerEntity, blockPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
					world.setBlockState(blockPos, this.fluid.getDefaultState(), 11);
				}

				return true;
			}
		}
	}
}
