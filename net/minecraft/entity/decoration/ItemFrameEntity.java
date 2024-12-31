package net.minecraft.entity.decoration;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameEntity extends AbstractDecorationEntity {
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
		this.getDataTracker().addEntry(8, 5);
		this.getDataTracker().track(9, (byte)0);
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
		d *= 64.0 * this.renderDistanceMultiplier;
		return distance < d * d;
	}

	@Override
	public void onBreak(Entity entity) {
		this.dropHeldStack(entity, true);
	}

	public void dropHeldStack(Entity entity, boolean alwaysDrop) {
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

	public ItemStack getHeldItemStack() {
		return this.getDataTracker().getStack(8);
	}

	public void setHeldItemStack(ItemStack stack) {
		this.setHeldItemStack(stack, true);
	}

	private void setHeldItemStack(ItemStack stack, boolean update) {
		if (stack != null) {
			stack = stack.copy();
			stack.count = 1;
			stack.setInItemFrame(this);
		}

		this.getDataTracker().setProperty(8, stack);
		this.getDataTracker().markDirty(8);
		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
	}

	public int rotation() {
		return this.getDataTracker().getByte(9);
	}

	public void setRotation(int value) {
		this.setRotation(value, true);
	}

	private void setRotation(int value, boolean update) {
		this.getDataTracker().setProperty(9, (byte)(value % 8));
		if (update && this.pos != null) {
			this.world.updateHorizontalAdjacent(this.pos, Blocks.AIR);
		}
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

			if (nbt.contains("Direction")) {
				this.setRotation(this.rotation() * 2, false);
			}
		}

		super.readCustomDataFromNbt(nbt);
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		if (this.getHeldItemStack() == null) {
			ItemStack itemStack = player.getStackInHand();
			if (itemStack != null && !this.world.isClient) {
				this.setHeldItemStack(itemStack);
				if (!player.abilities.creativeMode && --itemStack.count <= 0) {
					player.inventory.setInvStack(player.inventory.selectedSlot, null);
				}
			}
		} else if (!this.world.isClient) {
			this.setRotation(this.rotation() + 1);
		}

		return true;
	}

	public int getComparatorPower() {
		return this.getHeldItemStack() == null ? 0 : this.rotation() % 8 + 1;
	}
}
