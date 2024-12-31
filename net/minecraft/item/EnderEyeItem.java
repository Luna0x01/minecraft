package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
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
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		if (!playerEntity.canModify(blockPos.offset(direction), direction, itemStack)
			|| blockState.getBlock() != Blocks.END_PORTAL_FRAME
			|| (Boolean)blockState.get(EndPortalFrameBlock.EYE)) {
			return ActionResult.FAIL;
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			world.setBlockState(blockPos, blockState.with(EndPortalFrameBlock.EYE, true), 2);
			world.updateHorizontalAdjacent(blockPos, Blocks.END_PORTAL_FRAME);
			itemStack.count--;

			for (int i = 0; i < 16; i++) {
				double d = (double)((float)blockPos.getX() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double e = (double)((float)blockPos.getY() + 0.8125F);
				double j = (double)((float)blockPos.getZ() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double k = 0.0;
				double l = 0.0;
				double m = 0.0;
				world.addParticle(ParticleType.SMOKE, d, e, j, 0.0, 0.0, 0.0);
			}

			Direction direction2 = blockState.get(EndPortalFrameBlock.FACING);
			BlockPattern.Result result = EndPortalFrameBlock.method_11610().searchAround(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int n = 0; n < 3; n++) {
					for (int o = 0; o < 3; o++) {
						world.setBlockState(blockPos2.add(n, 0, o), Blocks.END_PORTAL.getDefaultState(), 2);
					}
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		BlockHitResult blockHitResult = this.onHit(world, playerEntity, false);
		if (blockHitResult != null
			&& blockHitResult.type == BlockHitResult.Type.BLOCK
			&& world.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (!world.isClient) {
				BlockPos blockPos = ((ServerWorld)world).getChunkProvider().method_12773(world, "Stronghold", new BlockPos(playerEntity));
				if (blockPos != null) {
					EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, playerEntity.x, playerEntity.y + (double)(playerEntity.height / 2.0F), playerEntity.z);
					eyeOfEnderEntity.initTargetPos(blockPos);
					world.spawnEntity(eyeOfEnderEntity);
					world.playSound(
						null,
						playerEntity.x,
						playerEntity.y,
						playerEntity.z,
						Sounds.ENTITY_ENDEREYE_LAUNCH,
						SoundCategory.NEUTRAL,
						0.5F,
						0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
					);
					world.syncWorldEvent(null, 1003, new BlockPos(playerEntity), 0);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.count--;
					}

					playerEntity.incrementStat(Stats.used(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
