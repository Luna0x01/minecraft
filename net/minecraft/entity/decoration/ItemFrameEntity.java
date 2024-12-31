package net.minecraft.entity.decoration;

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
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
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
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
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
		} else if (!source.isExplosive() && !this.getHeldItemStack().isEmpty()) {
			if (!this.world.isClient) {
				this.dropHeldStack(source.getAttacker(), false);
				this.playSound(Sounds.ENTITY_ITEMFRAME_REMOVE_ITEM, 1.0F, 1.0F);
				this.setHeldItemStack(ItemStack.EMPTY);
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

			if (!itemStack.isEmpty() && this.random.nextFloat() < this.setDropChance) {
				itemStack = itemStack.copy();
				this.removeFromFrame(itemStack);
				this.dropItem(itemStack, 0.0F);
			}
		}
	}

	private void removeFromFrame(ItemStack map) {
		if (!map.isEmpty()) {
			if (map.getItem() == Items.FILLED_MAP) {
				MapState mapState = ((FilledMapItem)map.getItem()).getMapState(map, this.world);
				mapState.icons.remove("frame-" + this.getEntityId());
			}

			map.setInItemFrame(null);
		}
	}

	public ItemStack getHeldItemStack() {
		return this.getDataTracker().get(ITEM_STACK);
	}

	public void setHeldItemStack(ItemStack stack) {
		this.setHeldItemStack(stack, true);
	}

	private void setHeldItemStack(ItemStack stack, boolean update) {
		if (!stack.isEmpty()) {
			stack = stack.copy();
			stack.setCount(1);
			stack.setInItemFrame(this);
		}

		this.getDataTracker().set(ITEM_STACK, stack);
		this.getDataTracker().method_12754(ITEM_STACK);
		if (!stack.isEmpty()) {
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
			if (!itemStack.isEmpty() && itemStack.getItemFrame() != this) {
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
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema(ItemFrameEntity.class, "Item"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		if (!this.getHeldItemStack().isEmpty()) {
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
			this.setHeldItemStack(new ItemStack(nbtCompound), false);
			this.setRotation(nbt.getByte("ItemRotation"), false);
			if (nbt.contains("ItemDropChance", 99)) {
				this.setDropChance = nbt.getFloat("ItemDropChance");
			}
		}

		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.world.isClient) {
			if (this.getHeldItemStack().isEmpty()) {
				if (!itemStack.isEmpty()) {
					this.setHeldItemStack(itemStack);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}
				}
			} else {
				this.playSound(Sounds.ENTITY_ITEMFRAME_ROTATE_ITEM, 1.0F, 1.0F);
				this.setRotation(this.rotation() + 1);
			}
		}

		return true;
	}

	public int getComparatorPower() {
		return this.getHeldItemStack().isEmpty() ? 0 : this.rotation() % 8 + 1;
	}
}
