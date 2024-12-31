package net.minecraft.item;

import net.minecraft.class_4342;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderEyeItem extends Item {
	public EnderEyeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.END_PORTAL_FRAME || (Boolean)blockState.getProperty(EndPortalFrameBlock.field_18306)) {
			return ActionResult.PASS;
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockState blockState2 = blockState.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(true));
			Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, blockPos);
			world.setBlockState(blockPos, blockState2, 2);
			world.updateHorizontalAdjacent(blockPos, Blocks.END_PORTAL_FRAME);
			itemUsageContext.getItemStack().decrement(1);

			for (int i = 0; i < 16; i++) {
				double d = (double)((float)blockPos.getX() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double e = (double)((float)blockPos.getY() + 0.8125F);
				double f = (double)((float)blockPos.getZ() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double g = 0.0;
				double h = 0.0;
				double j = 0.0;
				world.method_16343(class_4342.field_21363, d, e, f, 0.0, 0.0, 0.0);
			}

			world.playSound(null, blockPos, Sounds.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
			BlockPattern.Result result = EndPortalFrameBlock.method_11610().method_16938(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						world.setBlockState(blockPos2.add(k, 0, l), Blocks.END_PORTAL.getDefaultState(), 2);
					}
				}

				world.method_4689(1038, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		BlockHitResult blockHitResult = this.onHit(world, player, false);
		if (blockHitResult != null
			&& blockHitResult.type == BlockHitResult.Type.BLOCK
			&& world.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			player.method_13050(hand);
			if (!world.isClient) {
				BlockPos blockPos = ((ServerWorld)world).method_3586().method_12773(world, "Stronghold", new BlockPos(player), 100, false);
				if (blockPos != null) {
					EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, player.x, player.y + (double)(player.height / 2.0F), player.z);
					eyeOfEnderEntity.initTargetPos(blockPos);
					world.method_3686(eyeOfEnderEntity);
					if (player instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16340.method_14427((ServerPlayerEntity)player, blockPos);
					}

					world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
					world.syncWorldEvent(null, 1003, new BlockPos(player), 0);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					player.method_15932(Stats.USED.method_21429(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}
}
