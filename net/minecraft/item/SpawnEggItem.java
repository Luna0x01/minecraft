package net.minecraft.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
	public SpawnEggItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String string = ("" + CommonI18n.translate(this.getTranslationKey() + ".name")).trim();
		String string2 = method_11407(stack);
		if (string2 != null) {
			string = string + " " + CommonI18n.translate("entity." + string2 + ".name");
		}

		return string;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else if (!playerEntity.canModify(blockPos.offset(direction), direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.SPAWNER) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					SpawnerBlockEntityBehavior spawnerBlockEntityBehavior = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					spawnerBlockEntityBehavior.setEntityId(method_11407(itemStack));
					blockEntity.markDirty();
					world.method_11481(blockPos, blockState, blockState, 3);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.count--;
					}

					return ActionResult.SUCCESS;
				}
			}

			blockPos = blockPos.offset(direction);
			double d = 0.0;
			if (direction == Direction.UP && blockState instanceof FenceBlock) {
				d = 0.5;
			}

			Entity entity = method_4628(world, method_11407(itemStack), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5);
			if (entity != null) {
				if (entity instanceof LivingEntity && itemStack.hasCustomName()) {
					entity.setCustomName(itemStack.getCustomName());
				}

				method_11406(world, playerEntity, itemStack, entity);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.count--;
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	public static void method_11406(World world, @Nullable PlayerEntity player, ItemStack stack, @Nullable Entity entity) {
		MinecraftServer minecraftServer = world.getServer();
		if (minecraftServer != null && entity != null) {
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
				if (!world.isClient && entity.entityDataRequiresOperator() && (player == null || !minecraftServer.getPlayerManager().isOperator(player.getGameProfile()))) {
					return;
				}

				NbtCompound nbtCompound2 = entity.toNbt(new NbtCompound());
				UUID uUID = entity.getUuid();
				nbtCompound2.copyFrom(nbtCompound.getCompound("EntityTag"));
				entity.setUuid(uUID);
				entity.fromNbt(nbtCompound2);
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			BlockHitResult blockHitResult = this.onHit(world, playerEntity, true);
			if (blockHitResult != null && blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!(world.getBlockState(blockPos).getBlock() instanceof AbstractFluidBlock)) {
					return new TypedActionResult<>(ActionResult.PASS, itemStack);
				} else if (world.canPlayerModifyAt(playerEntity, blockPos) && playerEntity.canModify(blockPos, blockHitResult.direction, itemStack)) {
					Entity entity = method_4628(world, method_11407(itemStack), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
					if (entity == null) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					} else {
						if (entity instanceof LivingEntity && itemStack.hasCustomName()) {
							entity.setCustomName(itemStack.getCustomName());
						}

						method_11406(world, playerEntity, itemStack, entity);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.count--;
						}

						playerEntity.incrementStat(Stats.used(this));
						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
					}
				} else {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}

	@Nullable
	public static Entity method_4628(World world, @Nullable String string, double d, double e, double f) {
		if (string != null && EntityType.SPAWN_EGGS.containsKey(string)) {
			Entity entity = null;

			for (int i = 0; i < 1; i++) {
				entity = EntityType.method_13023(string, world);
				if (entity instanceof LivingEntity) {
					MobEntity mobEntity = (MobEntity)entity;
					entity.refreshPositionAndAngles(d, e, f, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
					mobEntity.headYaw = mobEntity.yaw;
					mobEntity.bodyYaw = mobEntity.yaw;
					mobEntity.initialize(world.getLocalDifficulty(new BlockPos(mobEntity)), null);
					world.spawnEntity(entity);
					mobEntity.playAmbientSound();
				}
			}

			return entity;
		} else {
			return null;
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (EntityType.SpawnEggData spawnEggData : EntityType.SPAWN_EGGS.values()) {
			ItemStack itemStack = new ItemStack(item, 1);
			method_11405(itemStack, spawnEggData.name);
			list.add(itemStack);
		}
	}

	public static void method_11405(ItemStack itemStack, String string) {
		NbtCompound nbtCompound = itemStack.hasNbt() ? itemStack.getNbt() : new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putString("id", string);
		nbtCompound.put("EntityTag", nbtCompound2);
		itemStack.setNbt(nbtCompound);
	}

	@Nullable
	public static String method_11407(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbt();
		if (nbtCompound == null) {
			return null;
		} else if (!nbtCompound.contains("EntityTag", 10)) {
			return null;
		} else {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("EntityTag");
			return !nbtCompound2.contains("id", 8) ? null : nbtCompound2.getString("id");
		}
	}
}
