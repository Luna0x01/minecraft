package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnderEyeItem extends Item {
	public EnderEyeItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		ItemStack itemStack = player.getStackInHand(hand);
		if (!player.canModify(pos.offset(direction), direction, itemStack)
			|| blockState.getBlock() != Blocks.END_PORTAL_FRAME
			|| (Boolean)blockState.get(EndPortalFrameBlock.EYE)) {
			return ActionResult.FAIL;
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			world.setBlockState(pos, blockState.with(EndPortalFrameBlock.EYE, true), 2);
			world.updateHorizontalAdjacent(pos, Blocks.END_PORTAL_FRAME);
			itemStack.decrement(1);

			for (int i = 0; i < 16; i++) {
				double d = (double)((float)pos.getX() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double e = (double)((float)pos.getY() + 0.8125F);
				double f = (double)((float)pos.getZ() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double g = 0.0;
				double h = 0.0;
				double j = 0.0;
				world.addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			}

			world.method_11486(null, pos, Sounds.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
			BlockPattern.Result result = EndPortalFrameBlock.method_11610().searchAround(world, pos);
			if (result != null) {
				BlockPos blockPos = result.getFrontTopLeft().add(-3, 0, -3);

				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						world.setBlockState(blockPos.add(k, 0, l), Blocks.END_PORTAL.getDefaultState(), 2);
					}
				}

				world.method_4689(1038, blockPos.add(1, 0, 1), 0);
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
				BlockPos blockPos = ((ServerWorld)world).getChunkProvider().method_12773(world, "Stronghold", new BlockPos(player), false);
				if (blockPos != null) {
					EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, player.x, player.y + (double)(player.height / 2.0F), player.z);
					eyeOfEnderEntity.initTargetPos(blockPos);
					world.spawnEntity(eyeOfEnderEntity);
					if (player instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16340.method_14427((ServerPlayerEntity)player, blockPos);
					}

					world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
					world.syncWorldEvent(null, 1003, new BlockPos(player), 0);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					player.incrementStat(Stats.used(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}
}
