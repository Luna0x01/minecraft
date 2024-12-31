package net.minecraft.item;

import java.util.List;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SpawnerBlockEntityBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
	public SpawnEggItem() {
		this.setUnbreakable(true);
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String string = ("" + CommonI18n.translate(this.getTranslationKey() + ".name")).trim();
		String string2 = EntityType.getEntityName(stack.getData());
		if (string2 != null) {
			string = string + " " + CommonI18n.translate("entity." + string2 + ".name");
		}

		return string;
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(stack.getData());
		if (spawnEggData != null) {
			return color == 0 ? spawnEggData.foregroundColor : spawnEggData.backgroundColor;
		} else {
			return 16777215;
		}
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (world.isClient) {
			return true;
		} else if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() == Blocks.SPAWNER) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					SpawnerBlockEntityBehavior spawnerBlockEntityBehavior = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					spawnerBlockEntityBehavior.setEntityId(EntityType.getEntityName(itemStack.getData()));
					blockEntity.markDirty();
					world.onBlockUpdate(pos);
					if (!player.abilities.creativeMode) {
						itemStack.count--;
					}

					return true;
				}
			}

			pos = pos.offset(direction);
			double d = 0.0;
			if (direction == Direction.UP && blockState instanceof FenceBlock) {
				d = 0.5;
			}

			Entity entity = spawnEntity(world, itemStack.getData(), (double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5);
			if (entity != null) {
				if (entity instanceof LivingEntity && itemStack.hasCustomName()) {
					entity.setCustomName(itemStack.getCustomName());
				}

				if (!player.abilities.creativeMode) {
					itemStack.count--;
				}
			}

			return true;
		}
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (world.isClient) {
			return stack;
		} else {
			BlockHitResult blockHitResult = this.onHit(world, player, true);
			if (blockHitResult == null) {
				return stack;
			} else {
				if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					if (!world.canPlayerModifyAt(player, blockPos)) {
						return stack;
					}

					if (!player.canModify(blockPos, blockHitResult.direction, stack)) {
						return stack;
					}

					if (world.getBlockState(blockPos).getBlock() instanceof AbstractFluidBlock) {
						Entity entity = spawnEntity(world, stack.getData(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
						if (entity != null) {
							if (entity instanceof LivingEntity && stack.hasCustomName()) {
								((MobEntity)entity).setCustomName(stack.getCustomName());
							}

							if (!player.abilities.creativeMode) {
								stack.count--;
							}

							player.incrementStat(Stats.USED[Item.getRawId(this)]);
						}
					}
				}

				return stack;
			}
		}
	}

	public static Entity spawnEntity(World world, int id, double x, double y, double z) {
		if (!EntityType.SPAWN_EGGS.containsKey(id)) {
			return null;
		} else {
			Entity entity = null;

			for (int i = 0; i < 1; i++) {
				entity = EntityType.createInstanceFromRawId(id, world);
				if (entity instanceof LivingEntity) {
					MobEntity mobEntity = (MobEntity)entity;
					entity.refreshPositionAndAngles(x, y, z, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
					mobEntity.headYaw = mobEntity.yaw;
					mobEntity.bodyYaw = mobEntity.yaw;
					mobEntity.initialize(world.getLocalDifficulty(new BlockPos(mobEntity)), null);
					world.spawnEntity(entity);
					mobEntity.playAmbientSound();
				}
			}

			return entity;
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (EntityType.SpawnEggData spawnEggData : EntityType.SPAWN_EGGS.values()) {
			list.add(new ItemStack(item, 1, spawnEggData.id));
		}
	}
}
