package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadItem extends BlockItem {
	public LilyPadItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		BlockHitResult blockHitResult = this.onHit(world, player, true);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!world.canPlayerModifyAt(player, blockPos) || !player.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				Material material = blockState.getMaterial();
				FluidState fluidState = world.getFluidState(blockPos);
				if ((fluidState.getFluid() == Fluids.WATER || material == Material.ICE) && world.method_8579(blockPos2)) {
					world.setBlockState(blockPos2, Blocks.LILY_PAD.getDefaultState(), 11);
					if (player instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, blockPos2, itemStack);
					}

					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					player.method_15932(Stats.USED.method_21429(this));
					world.playSound(player, blockPos, Sounds.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
