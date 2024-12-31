package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.BrewingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	private static final int[] inputs = new int[]{3};
	private static final int[] outputs = new int[]{0, 1, 2};
	private ItemStack[] stacks = new ItemStack[4];
	private int brewTime;
	private boolean[] slotsEmptyLastTick;
	private Item itemBrewing;
	private String customName;

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.brewing";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	@Override
	public int getInvSize() {
		return this.stacks.length;
	}

	@Override
	public void tick() {
		if (this.brewTime > 0) {
			this.brewTime--;
			if (this.brewTime == 0) {
				this.brew();
				this.markDirty();
			} else if (!this.canBrew()) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.itemBrewing != this.stacks[3].getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (this.canBrew()) {
			this.brewTime = 400;
			this.itemBrewing = this.stacks[3].getItem();
		}

		if (!this.world.isClient) {
			boolean[] bls = this.getSlotsEmpty();
			if (!Arrays.equals(bls, this.slotsEmptyLastTick)) {
				this.slotsEmptyLastTick = bls;
				BlockState blockState = this.world.getBlockState(this.getPos());
				if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
					return;
				}

				for (int i = 0; i < BrewingStandBlock.HAS_BOTTLES.length; i++) {
					blockState = blockState.with(BrewingStandBlock.HAS_BOTTLES[i], bls[i]);
				}

				this.world.setBlockState(this.pos, blockState, 2);
			}
		}
	}

	private boolean canBrew() {
		if (this.stacks[3] != null && this.stacks[3].count > 0) {
			ItemStack itemStack = this.stacks[3];
			if (!itemStack.getItem().hasStatusEffectString(itemStack)) {
				return false;
			} else {
				boolean bl = false;

				for (int i = 0; i < 3; i++) {
					if (this.stacks[i] != null && this.stacks[i].getItem() == Items.POTION) {
						int j = this.stacks[i].getData();
						int k = this.getBrewEffectData(j, itemStack);
						if (!PotionItem.isThrowable(j) && PotionItem.isThrowable(k)) {
							bl = true;
							break;
						}

						List<StatusEffectInstance> list = Items.POTION.getPotionEffects(j);
						List<StatusEffectInstance> list2 = Items.POTION.getPotionEffects(k);
						if ((j <= 0 || list != list2) && (list == null || !list.equals(list2) && list2 != null) && j != k) {
							bl = true;
							break;
						}
					}
				}

				return bl;
			}
		} else {
			return false;
		}
	}

	private void brew() {
		if (this.canBrew()) {
			ItemStack itemStack = this.stacks[3];

			for (int i = 0; i < 3; i++) {
				if (this.stacks[i] != null && this.stacks[i].getItem() == Items.POTION) {
					int j = this.stacks[i].getData();
					int k = this.getBrewEffectData(j, itemStack);
					List<StatusEffectInstance> list = Items.POTION.getPotionEffects(j);
					List<StatusEffectInstance> list2 = Items.POTION.getPotionEffects(k);
					if (j > 0 && list == list2 || list != null && (list.equals(list2) || list2 == null)) {
						if (!PotionItem.isThrowable(j) && PotionItem.isThrowable(k)) {
							this.stacks[i].setDamage(k);
						}
					} else if (j != k) {
						this.stacks[i].setDamage(k);
					}
				}
			}

			if (itemStack.getItem().isFood()) {
				this.stacks[3] = new ItemStack(itemStack.getItem().getRecipeRemainder());
			} else {
				this.stacks[3].count--;
				if (this.stacks[3].count <= 0) {
					this.stacks[3] = null;
				}
			}
		}
	}

	private int getBrewEffectData(int data, ItemStack stack) {
		if (stack == null) {
			return data;
		} else {
			return stack.getItem().hasStatusEffectString(stack) ? StatusEffectStrings.getStatusEffectData(data, stack.getItem().getStatusEffectString(stack)) : data;
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		NbtList nbtList = nbt.getList("Items", 10);
		this.stacks = new ItemStack[this.getInvSize()];

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot");
			if (j >= 0 && j < this.stacks.length) {
				this.stacks[j] = ItemStack.fromNbt(nbtCompound);
			}
		}

		this.brewTime = nbt.getShort("BrewTime");
		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putShort("BrewTime", (short)this.brewTime);
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.stacks.length; i++) {
			if (this.stacks[i] != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				this.stacks[i].toNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Items", nbtList);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.stacks.length ? this.stacks[slot] : null;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot >= 0 && slot < this.stacks.length) {
			ItemStack itemStack = this.stacks[slot];
			this.stacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (slot >= 0 && slot < this.stacks.length) {
			ItemStack itemStack = this.stacks[slot];
			this.stacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < this.stacks.length) {
			this.stacks[slot] = stack;
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return slot == 3 ? stack.getItem().hasStatusEffectString(stack) : stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE;
	}

	public boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (this.stacks[i] != null) {
				bls[i] = true;
			}
		}

		return bls;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return side == Direction.UP ? inputs : outputs;
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		return this.isValidInvStack(slot, stack);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public String getId() {
		return "minecraft:brewing_stand";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new BrewingScreenHandler(inventory, this);
	}

	@Override
	public int getProperty(int key) {
		switch (key) {
			case 0:
				return this.brewTime;
			default:
				return 0;
		}
	}

	@Override
	public void setProperty(int id, int value) {
		switch (id) {
			case 0:
				this.brewTime = value;
		}
	}

	@Override
	public int getProperties() {
		return 1;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.stacks.length; i++) {
			this.stacks[i] = null;
		}
	}
}
