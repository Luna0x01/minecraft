package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private final Fluid fluid;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.fluid = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = rayTrace(
			world, playerEntity, this.fluid == Fluids.field_15906 ? RayTraceContext.FluidHandling.field_1345 : RayTraceContext.FluidHandling.field_1348
		);
		if (hitResult.getType() == HitResult.Type.field_1333) {
			return TypedActionResult.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.field_1332) {
			return TypedActionResult.pass(itemStack);
		} else {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos2 = blockPos.offset(direction);
			if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceOn(blockPos2, direction, itemStack)) {
				return TypedActionResult.fail(itemStack);
			} else if (this.fluid == Fluids.field_15906) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
					if (fluid != Fluids.field_15906) {
						playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
						playerEntity.playSound(fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15202 : SoundEvents.field_15126, 1.0F, 1.0F);
						ItemStack itemStack2 = this.getFilledStack(itemStack, playerEntity, fluid.getBucketItem());
						if (!world.isClient) {
							Criterions.FILLED_BUCKET.trigger((ServerPlayerEntity)playerEntity, new ItemStack(fluid.getBucketItem()));
						}

						return TypedActionResult.success(itemStack2);
					}
				}

				return TypedActionResult.fail(itemStack);
			} else {
				BlockState blockState2 = world.getBlockState(blockPos);
				BlockPos blockPos3 = blockState2.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
				if (this.placeFluid(playerEntity, world, blockPos3, blockHitResult)) {
					this.onEmptied(world, itemStack, blockPos3);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos3, itemStack);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return TypedActionResult.success(this.getEmptiedStack(itemStack, playerEntity));
				} else {
					return TypedActionResult.fail(itemStack);
				}
			}
		}
	}

	protected ItemStack getEmptiedStack(ItemStack itemStack, PlayerEntity playerEntity) {
		return !playerEntity.abilities.creativeMode ? new ItemStack(Items.field_8550) : itemStack;
	}

	public void onEmptied(World world, ItemStack itemStack, BlockPos blockPos) {
	}

	private ItemStack getFilledStack(ItemStack itemStack, PlayerEntity playerEntity, Item item) {
		if (playerEntity.abilities.creativeMode) {
			return itemStack;
		} else {
			itemStack.decrement(1);
			if (itemStack.isEmpty()) {
				return new ItemStack(item);
			} else {
				if (!playerEntity.inventory.insertStack(new ItemStack(item))) {
					playerEntity.dropItem(new ItemStack(item), false);
				}

				return itemStack;
			}
		}
	}

	public boolean placeFluid(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(this.fluid instanceof BaseFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			boolean bl = blockState.canBucketPlace(this.fluid);
			if (blockState.isAir()
				|| bl
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, this.fluid)) {
				if (world.dimension.doesWaterVaporize() && this.fluid.matches(FluidTags.field_15517)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.playSound(
						playerEntity, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.addParticle(ParticleTypes.field_11237, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, ((BaseFluid)this.fluid).getStill(false))) {
						this.playEmptyingSound(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && bl && !material.isLiquid()) {
						world.breakBlock(blockPos, true);
					}

					this.playEmptyingSound(playerEntity, world, blockPos);
					world.setBlockState(blockPos, this.fluid.getDefaultState().getBlockState(), 11);
				}

				return true;
			} else {
				return blockHitResult == null ? false : this.placeFluid(playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
			}
		}
	}

	protected void playEmptyingSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		SoundEvent soundEvent = this.fluid.matches(FluidTags.field_15518) ? SoundEvents.field_15010 : SoundEvents.field_14834;
		iWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
