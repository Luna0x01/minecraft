package net.minecraft.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
		String string2 = EntityType.getEntityName(getEntityIdentifierFromStack(stack));
		if (string2 != null) {
			string = string + " " + CommonI18n.translate("entity." + string2 + ".name");
		}

		return string;
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block == Blocks.SPAWNER) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					SpawnerBlockEntityBehavior spawnerBlockEntityBehavior = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					spawnerBlockEntityBehavior.setSpawnedEntity(getEntityIdentifierFromStack(itemStack));
					blockEntity.markDirty();
					world.method_11481(pos, blockState, blockState, 3);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return ActionResult.SUCCESS;
				}
			}

			BlockPos blockPos = pos.offset(direction);
			double d = this.method_13666(world, blockPos);
			Entity entity = createEntity(
				world, getEntityIdentifierFromStack(itemStack), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5
			);
			if (entity != null) {
				if (entity instanceof LivingEntity && itemStack.hasCustomName()) {
					entity.setCustomName(itemStack.getCustomName());
				}

				method_11406(world, player, itemStack, entity);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	protected double method_13666(World world, BlockPos blockPos) {
		Box box = new Box(blockPos).stretch(0.0, -1.0, 0.0);
		List<Box> list = world.doesBoxCollide(null, box);
		if (list.isEmpty()) {
			return 0.0;
		} else {
			double d = box.minY;

			for (Box box2 : list) {
				d = Math.max(box2.maxY, d);
			}

			return d - (double)blockPos.getY();
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
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			BlockHitResult blockHitResult = this.onHit(world, player, true);
			if (blockHitResult != null && blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!(world.getBlockState(blockPos).getBlock() instanceof AbstractFluidBlock)) {
					return new TypedActionResult<>(ActionResult.PASS, itemStack);
				} else if (world.canPlayerModifyAt(player, blockPos) && player.canModify(blockPos, blockHitResult.direction, itemStack)) {
					Entity entity = createEntity(
						world, getEntityIdentifierFromStack(itemStack), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5
					);
					if (entity == null) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					} else {
						if (entity instanceof LivingEntity && itemStack.hasCustomName()) {
							entity.setCustomName(itemStack.getCustomName());
						}

						method_11406(world, player, itemStack, entity);
						if (!player.abilities.creativeMode) {
							itemStack.decrement(1);
						}

						player.incrementStat(Stats.used(this));
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
	public static Entity createEntity(World world, @Nullable Identifier entityId, double x, double y, double z) {
		if (entityId != null && EntityType.SPAWN_EGGS.containsKey(entityId)) {
			Entity entity = null;

			for (int i = 0; i < 1; i++) {
				entity = EntityType.createInstanceFromId(entityId, world);
				if (entity instanceof MobEntity) {
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
		} else {
			return null;
		}
	}

	@Override
	public void method_13648(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (EntityType.SpawnEggData spawnEggData : EntityType.SPAWN_EGGS.values()) {
			ItemStack itemStack = new ItemStack(item, 1);
			setEggEntity(itemStack, spawnEggData.identifier);
			defaultedList.add(itemStack);
		}
	}

	public static void setEggEntity(ItemStack stack, Identifier entityId) {
		NbtCompound nbtCompound = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putString("id", entityId.toString());
		nbtCompound.put("EntityTag", nbtCompound2);
		stack.setNbt(nbtCompound);
	}

	@Nullable
	public static Identifier getEntityIdentifierFromStack(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound == null) {
			return null;
		} else if (!nbtCompound.contains("EntityTag", 10)) {
			return null;
		} else {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("EntityTag");
			if (!nbtCompound2.contains("id", 8)) {
				return null;
			} else {
				String string = nbtCompound2.getString("id");
				Identifier identifier = new Identifier(string);
				if (!string.contains(":")) {
					nbtCompound2.putString("id", identifier.toString());
				}

				return identifier;
			}
		}
	}
}
