package net.minecraft.entity.decoration;

import com.google.common.base.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class ItemFrameEntity extends AbstractDecorationEntity {
	private static final TrackedData<Optional<ItemStack>> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private float setDropChance = 1.0F;

	public ItemFrameEntity(World world) {
		super(world);
	}

	public ItemFrameEntity(World world, BlockPos blockPos, Direction direction) {
		super(world, blockPos);
		this.setDirection(direction);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, Optional.absent());
		this.getDataTracker().startTracking(ROTATION, 0);
	}

	@Override
	public float getTargetingMargin() {
		return 0.0F;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!source.isExplosive() && this.getHeldItemStack() != null) {
			if (!this.world.isClient) {
				this.dropHeldStack(source.getAttacker(), false);
				this.playSound(Sounds.ENTITY_ITEMFRAME_REMOVE_ITEM, 1.0F, 1.0F);
				this.setHeldItemStack(null);
			}

			return true;
		} else {
			return super.damage(source, amount);
		}
	}

	@Override
	public int getWidth() {
		return 12;
	}

	@Override
	public int getHeight() {
		return 12;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = 16.0;
		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		this.playSound(Sounds.ENTITY_ITEMFRAME_BREAK, 1.0F, 1.0F);
		this.dropHeldStack(entity, true);
	}

	@Override
	public void onPlace() {
		this.playSound(Sounds.ENTITY_ITEMFRAME_PLACE, 1.0F, 1.0F);
	}

	public void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = this.getHeldItemStack();
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					this.removeFromFrame(itemStack);
					return;
				}
			}

			if (alwaysDrop) {
				this.dropItem(new ItemStack(Items.ITEM_FRAME), 0.0F);
			}

			if (itemStack != null && this.random.nextFloat() < this.setDropChance) {
				itemStack = itemStack.copy();
				this.removeFromFrame(itemStack);
				this.dropItem(itemStack, 0.0F);
			}
		}
	}

	private void removeFromFrame(ItemStack map) {
		if (map != null) {
			if (map.getItem() == Items.FILLED_MAP) {
				MapState mapState = ((FilledMapItem)map.getItem()).getMapState(map, this.world);
				mapState.icons.remove("frame-" + this.getEntityId());
			}

			map.setInItemFrame(null);
		}
	}

	@Nullable
	public ItemStack getHeldItemStack() {
		return (ItemStack)this.getDataTracker().get(ITEM_STACK).orNull();
	}

	public void setHeldItemStack(@Nullable ItemStack stack) {
		this.setHeldItemStack(stack, true);
	}

	private void setHeldItemStack(@Nullable ItemStack stack, boolean update) {
		if (stack != null) {
			stack = stack.copy();
			stack.count = 1;
			stack.setInItemFrame(this);
		}

		this.getDataTracker().set(ITEM_STACK, Optional.fromNullable(stack));
		this.getDataTracker().method_12754(ITEM_STACK);
		if (stack != null) {
			this.playSound(Sounds.ENTITY_ITEMFRAME_ADD_ITEM, 1.0F, 1.0F);
		}

		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (data.equals(ITEM_STACK)) {
			ItemStack itemStack = this.getHeldItemStack();
			if (itemStack != null && itemStack.getItemFrame() != this) {
				itemStack.setInItemFrame(this);
			}
		}
	}

	public int rotation() {
		return this.getDataTracker().get(ROTATION);
	}

	public void setRotation(int value) {
		this.setRotation(value, true);
	}

	private void setRotation(int value, boolean update) {
		this.getDataTracker().set(ROTATION, value % 8);
		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema("ItemFrame", "Item"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.getHeldItemStack() != null) {
			nbt.put("Item", this.getHeldItemStack().toNbt(new NbtCompound()));
			nbt.putByte("ItemRotation", (byte)this.rotation());
			nbt.putFloat("ItemDropChance", this.setDropChance);
		}

		super.writeCustomDataToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("Item");
		if (nbtCompound != null && !nbtCompound.isEmpty()) {
			this.setHeldItemStack(ItemStack.fromNbt(nbtCompound), false);
			this.setRotation(nbt.getByte("ItemRotation"), false);
			if (nbt.contains("ItemDropChance", 99)) {
				this.setDropChance = nbt.getFloat("ItemDropChance");
			}
		}

		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		if (this.getHeldItemStack() == null) {
			if (itemStack != null && !this.world.isClient) {
				this.setHeldItemStack(itemStack);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.count--;
				}
			}
		} else if (!this.world.isClient) {
			this.playSound(Sounds.ENTITY_ITEMFRAME_ROTATE_ITEM, 1.0F, 1.0F);
			this.setRotation(this.rotation() + 1);
		}

		return true;
	}

	public int getComparatorPower() {
		return this.getHeldItemStack() == null ? 0 : this.rotation() % 8 + 1;
	}
}
