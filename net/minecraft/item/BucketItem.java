package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BucketItem extends Item {
	private final Fluid field_17157;

	public BucketItem(Fluid fluid, Item.Settings settings) {
		super(settings);
		this.field_17157 = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		BlockHitResult blockHitResult = this.onHit(world, player, this.field_17157 == Fluids.EMPTY);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!world.canPlayerModifyAt(player, blockPos) || !player.canModify(blockPos, blockHitResult.direction, itemStack)) {
				return new TypedActionResult<>(ActionResult.FAIL, itemStack);
			} else if (this.field_17157 == Fluids.EMPTY) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
					if (fluid != Fluids.EMPTY) {
						player.method_15932(Stats.USED.method_21429(this));
						player.playSound(fluid.method_17786(FluidTags.LAVA) ? Sounds.ITEM_BUCKET_FILL_LAVA : Sounds.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						ItemStack itemStack2 = this.fill(itemStack, player, fluid.method_17787());
						if (!world.isClient) {
							AchievementsAndCriterions.field_21658.method_15967((ServerPlayerEntity)player, new ItemStack(fluid.method_17787()));
						}

						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
					}
				}

				return new TypedActionResult<>(ActionResult.FAIL, itemStack);
			} else {
				BlockState blockState2 = world.getBlockState(blockPos);
				BlockPos blockPos2 = this.method_16032(blockState2, blockPos, blockHitResult);
				if (this.method_16028(player, world, blockPos2, blockHitResult)) {
					this.method_16031(world, itemStack, blockPos2);
					if (player instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, blockPos2, itemStack);
					}

					player.method_15932(Stats.USED.method_21429(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, this.method_16030(itemStack, player));
				} else {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}
			}
		} else {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}

	private BlockPos method_16032(BlockState blockState, BlockPos blockPos, BlockHitResult blockHitResult) {
		return blockState.getBlock() instanceof FluidFillable ? blockPos : blockHitResult.getBlockPos().offset(blockHitResult.direction);
	}

	protected ItemStack method_16030(ItemStack itemStack, PlayerEntity playerEntity) {
		return !playerEntity.abilities.creativeMode ? new ItemStack(Items.BUCKET) : itemStack;
	}

	public void method_16031(World world, ItemStack itemStack, BlockPos blockPos) {
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

	public boolean method_16028(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(this.field_17157 instanceof FlowableFluid)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			Material material = blockState.getMaterial();
			boolean bl = !material.isSolid();
			boolean bl2 = material.isReplaceable();
			if (world.method_8579(blockPos)
				|| bl
				|| bl2
				|| blockState.getBlock() instanceof FluidFillable && ((FluidFillable)blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, this.field_17157)
				)
			 {
				if (world.dimension.doesWaterVaporize() && this.field_17157.method_17786(FluidTags.WATER)) {
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					world.playSound(
						playerEntity, blockPos, Sounds.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int l = 0; l < 8; l++) {
						world.method_16343(class_4342.field_21356, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
					}
				} else if (blockState.getBlock() instanceof FluidFillable) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, ((FlowableFluid)this.field_17157).getStill(false))) {
						this.method_16029(playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && (bl || bl2) && !material.isFluid()) {
						world.method_8535(blockPos, true);
					}

					this.method_16029(playerEntity, world, blockPos);
					world.setBlockState(blockPos, this.field_17157.getDefaultState().method_17813(), 11);
				}

				return true;
			} else {
				return blockHitResult == null ? false : this.method_16028(playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.direction), null);
			}
		}
	}

	protected void method_16029(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		Sound sound = this.field_17157.method_17786(FluidTags.LAVA) ? Sounds.ITEM_BUCKET_EMPTY_LAVA : Sounds.ITEM_BUCKET_EMPTY;
		iWorld.playSound(playerEntity, blockPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
