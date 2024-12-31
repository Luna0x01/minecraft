package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnderEyeItem extends Item {
	public EnderEyeItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		BlockState blockState = world.getBlockState(pos);
		if (!player.canModify(pos.offset(direction), direction, itemStack)
			|| blockState.getBlock() != Blocks.END_PORTAL_FRAME
			|| (Boolean)blockState.get(EndPortalFrameBlock.EYE)) {
			return false;
		} else if (world.isClient) {
			return true;
		} else {
			world.setBlockState(pos, blockState.with(EndPortalFrameBlock.EYE, true), 2);
			world.updateHorizontalAdjacent(pos, Blocks.END_PORTAL_FRAME);
			itemStack.count--;

			for (int i = 0; i < 16; i++) {
				double d = (double)((float)pos.getX() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double e = (double)((float)pos.getY() + 0.8125F);
				double f = (double)((float)pos.getZ() + (5.0F + RANDOM.nextFloat() * 6.0F) / 16.0F);
				double g = 0.0;
				double h = 0.0;
				double j = 0.0;
				world.addParticle(ParticleType.SMOKE, d, e, f, g, h, j);
			}

			Direction direction2 = blockState.get(EndPortalFrameBlock.FACING);
			int k = 0;
			int l = 0;
			boolean bl = false;
			boolean bl2 = true;
			Direction direction3 = direction2.rotateYClockwise();

			for (int m = -2; m <= 2; m++) {
				BlockPos blockPos = pos.offset(direction3, m);
				BlockState blockState2 = world.getBlockState(blockPos);
				if (blockState2.getBlock() == Blocks.END_PORTAL_FRAME) {
					if (!(Boolean)blockState2.get(EndPortalFrameBlock.EYE)) {
						bl2 = false;
						break;
					}

					l = m;
					if (!bl) {
						k = m;
						bl = true;
					}
				}
			}

			if (bl2 && l == k + 2) {
				BlockPos blockPos2 = pos.offset(direction2, 4);

				for (int n = k; n <= l; n++) {
					BlockPos blockPos3 = blockPos2.offset(direction3, n);
					BlockState blockState3 = world.getBlockState(blockPos3);
					if (blockState3.getBlock() != Blocks.END_PORTAL_FRAME || !(Boolean)blockState3.get(EndPortalFrameBlock.EYE)) {
						bl2 = false;
						break;
					}
				}

				for (int o = k - 1; o <= l + 1; o += 4) {
					blockPos2 = pos.offset(direction3, o);

					for (int p = 1; p <= 3; p++) {
						BlockPos blockPos4 = blockPos2.offset(direction2, p);
						BlockState blockState4 = world.getBlockState(blockPos4);
						if (blockState4.getBlock() != Blocks.END_PORTAL_FRAME || !(Boolean)blockState4.get(EndPortalFrameBlock.EYE)) {
							bl2 = false;
							break;
						}
					}
				}

				if (bl2) {
					for (int q = k; q <= l; q++) {
						blockPos2 = pos.offset(direction3, q);

						for (int r = 1; r <= 3; r++) {
							BlockPos blockPos5 = blockPos2.offset(direction2, r);
							world.setBlockState(blockPos5, Blocks.END_PORTAL.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		BlockHitResult blockHitResult = this.onHit(world, player, false);
		if (blockHitResult != null
			&& blockHitResult.type == BlockHitResult.Type.BLOCK
			&& world.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
			return stack;
		} else {
			if (!world.isClient) {
				BlockPos blockPos = world.getNearestStructurePos("Stronghold", new BlockPos(player));
				if (blockPos != null) {
					EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, player.x, player.y, player.z);
					eyeOfEnderEntity.initTargetPos(blockPos);
					world.spawnEntity(eyeOfEnderEntity);
					world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
					world.syncWorldEvent(null, 1002, new BlockPos(player), 0);
					if (!player.abilities.creativeMode) {
						stack.count--;
					}

					player.incrementStat(Stats.USED[Item.getRawId(this)]);
				}
			}

			return stack;
		}
	}
}
