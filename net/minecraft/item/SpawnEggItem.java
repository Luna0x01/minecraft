package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
	private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
	private final int primaryColor;
	private final int secondaryColor;
	private final EntityType<?> type;

	public SpawnEggItem(EntityType<?> entityType, int i, int j, Item.Settings settings) {
		super(settings);
		this.type = entityType;
		this.primaryColor = i;
		this.secondaryColor = j;
		SPAWN_EGGS.put(entityType, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			ItemStack itemStack = itemUsageContext.getStack();
			BlockPos blockPos = itemUsageContext.getBlockPos();
			Direction direction = itemUsageContext.getSide();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.field_10260) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					EntityType<?> entityType = this.getEntityType(itemStack.getTag());
					mobSpawnerLogic.setEntityId(entityType);
					blockEntity.markDirty();
					world.updateListeners(blockPos, blockState, blockState, 3);
					itemStack.decrement(1);
					return ActionResult.field_5812;
				}
			}

			BlockPos blockPos2;
			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			EntityType<?> entityType2 = this.getEntityType(itemStack.getTag());
			if (entityType2.spawnFromItemStack(
					world,
					itemStack,
					itemUsageContext.getPlayer(),
					blockPos2,
					SpawnType.field_16465,
					true,
					!Objects.equals(blockPos, blockPos2) && direction == Direction.field_11036
				)
				!= null) {
				itemStack.decrement(1);
			}

			return ActionResult.field_5812;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else {
			HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.field_1345);
			if (hitResult.getType() != HitResult.Type.field_1332) {
				return new TypedActionResult<>(ActionResult.field_5811, itemStack);
			} else {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
					return new TypedActionResult<>(ActionResult.field_5811, itemStack);
				} else if (world.canPlayerModifyAt(playerEntity, blockPos) && playerEntity.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
					EntityType<?> entityType = this.getEntityType(itemStack.getTag());
					if (entityType.spawnFromItemStack(world, itemStack, playerEntity, blockPos, SpawnType.field_16465, false, false) == null) {
						return new TypedActionResult<>(ActionResult.field_5811, itemStack);
					} else {
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
						}

						playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
						return new TypedActionResult<>(ActionResult.field_5812, itemStack);
					}
				} else {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				}
			}
		}
	}

	public boolean isOfSameEntityType(@Nullable CompoundTag compoundTag, EntityType<?> entityType) {
		return Objects.equals(this.getEntityType(compoundTag), entityType);
	}

	public int getColor(int i) {
		return i == 0 ? this.primaryColor : this.secondaryColor;
	}

	public static SpawnEggItem forEntity(@Nullable EntityType<?> entityType) {
		return (SpawnEggItem)SPAWN_EGGS.get(entityType);
	}

	public static Iterable<SpawnEggItem> getAll() {
		return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
	}

	public EntityType<?> getEntityType(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("EntityTag");
			if (compoundTag2.containsKey("id", 8)) {
				return (EntityType<?>)EntityType.get(compoundTag2.getString("id")).orElse(this.type);
			}
		}

		return this.type;
	}
}
