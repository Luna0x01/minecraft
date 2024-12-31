package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SpawnerBlockEntityBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_3558 extends Item {
	private static final Map<EntityType<?>, class_3558> field_17380 = Maps.newIdentityHashMap();
	private final int field_17381;
	private final int field_17382;
	private final EntityType<?> field_17383;

	public class_3558(EntityType<?> entityType, int i, int j, Item.Settings settings) {
		super(settings);
		this.field_17383 = entityType;
		this.field_17381 = i;
		this.field_17382 = j;
		field_17380.put(entityType, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = itemUsageContext.getItemStack();
			BlockPos blockPos = itemUsageContext.getBlockPos();
			Direction direction = itemUsageContext.method_16151();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.SPAWNER) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					SpawnerBlockEntityBehavior spawnerBlockEntityBehavior = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					EntityType<?> entityType = this.method_16128(itemStack.getNbt());
					if (entityType != null) {
						spawnerBlockEntityBehavior.method_16278(entityType);
						blockEntity.markDirty();
						world.method_11481(blockPos, blockState, blockState, 3);
					}

					itemStack.decrement(1);
					return ActionResult.SUCCESS;
				}
			}

			BlockPos blockPos2;
			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			EntityType<?> entityType2 = this.method_16128(itemStack.getNbt());
			if (entityType2 == null
				|| entityType2.method_15619(
						world, itemStack, itemUsageContext.getPlayer(), blockPos2, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP
					)
					!= null) {
				itemStack.decrement(1);
			}

			return ActionResult.SUCCESS;
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
				if (!(world.getBlockState(blockPos).getBlock() instanceof class_3710)) {
					return new TypedActionResult<>(ActionResult.PASS, itemStack);
				} else if (world.canPlayerModifyAt(player, blockPos) && player.canModify(blockPos, blockHitResult.direction, itemStack)) {
					EntityType<?> entityType = this.method_16128(itemStack.getNbt());
					if (entityType != null && entityType.method_15619(world, itemStack, player, blockPos, false, false) != null) {
						if (!player.abilities.creativeMode) {
							itemStack.decrement(1);
						}

						player.method_15932(Stats.USED.method_21429(this));
						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
					} else {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}
				} else {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}

	public boolean method_16127(@Nullable NbtCompound nbtCompound, EntityType<?> entityType) {
		return Objects.equals(this.method_16128(nbtCompound), entityType);
	}

	public int method_16125(int i) {
		return i == 0 ? this.field_17381 : this.field_17382;
	}

	public static class_3558 method_16126(@Nullable EntityType<?> entityType) {
		return (class_3558)field_17380.get(entityType);
	}

	public static Iterable<class_3558> method_16129() {
		return Iterables.unmodifiableIterable(field_17380.values());
	}

	@Nullable
	public EntityType<?> method_16128(@Nullable NbtCompound nbtCompound) {
		if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("EntityTag");
			if (nbtCompound2.contains("id", 8)) {
				return EntityType.getById(nbtCompound2.getString("id"));
			}
		}

		return this.field_17383;
	}
}
