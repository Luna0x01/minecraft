package net.minecraft.block.entity;

import java.util.Arrays;
import net.minecraft.class_2960;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.BrewingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.level.storage.LevelDataType;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	private static final int[] inputs = new int[]{3};
	private static final int[] field_12841 = new int[]{0, 1, 2, 3};
	private static final int[] outputs = new int[]{0, 1, 2, 4};
	private DefaultedList<ItemStack> field_15151 = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int brewTime;
	private boolean[] slotsEmptyLastTick;
	private Item itemBrewing;
	private String customName;
	private int field_12842;

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.brewing";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomName(String name) {
		this.customName = name;
	}

	@Override
	public int getInvSize() {
		return this.field_15151.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15151) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void tick() {
		ItemStack itemStack = this.field_15151.get(4);
		if (this.field_12842 <= 0 && itemStack.getItem() == Items.BLAZE_POWDER) {
			this.field_12842 = 20;
			itemStack.decrement(1);
			this.markDirty();
		}

		boolean bl = this.canBrew();
		boolean bl2 = this.brewTime > 0;
		ItemStack itemStack2 = this.field_15151.get(3);
		if (bl2) {
			this.brewTime--;
			boolean bl3 = this.brewTime == 0;
			if (bl3 && bl) {
				this.brew();
				this.markDirty();
			} else if (!bl) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.itemBrewing != itemStack2.getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (bl && this.field_12842 > 0) {
			this.field_12842--;
			this.brewTime = 400;
			this.itemBrewing = itemStack2.getItem();
			this.markDirty();
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

	public boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (!this.field_15151.get(i).isEmpty()) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private boolean canBrew() {
		ItemStack itemStack = this.field_15151.get(3);
		if (itemStack.isEmpty()) {
			return false;
		} else if (!StatusEffectStrings.method_11417(itemStack)) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				ItemStack itemStack2 = this.field_15151.get(i);
				if (!itemStack2.isEmpty() && StatusEffectStrings.method_11418(itemStack2, itemStack)) {
					return true;
				}
			}

			return false;
		}
	}

	private void brew() {
		ItemStack itemStack = this.field_15151.get(3);

		for (int i = 0; i < 3; i++) {
			this.field_15151.set(i, StatusEffectStrings.method_11427(itemStack, this.field_15151.get(i)));
		}

		itemStack.decrement(1);
		BlockPos blockPos = this.getPos();
		if (itemStack.getItem().isFood()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else {
				ItemScatterer.spawnItemStack(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
			}
		}

		this.field_15151.set(3, itemStack);
		this.world.syncGlobalEvent(1035, blockPos, 0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema(BrewingStandBlockEntity.class, "Items"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15151 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		class_2960.method_13927(nbt, this.field_15151);
		this.brewTime = nbt.getShort("BrewTime");
		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}

		this.field_12842 = nbt.getByte("Fuel");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putShort("BrewTime", (short)this.brewTime);
		class_2960.method_13923(nbt, this.field_15151);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}

		nbt.putByte("Fuel", (byte)this.field_12842);
		return nbt;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.field_15151.size() ? this.field_15151.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_13926(this.field_15151, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15151, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < this.field_15151.size()) {
			this.field_15151.set(slot, stack);
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
		if (slot == 3) {
			return StatusEffectStrings.method_11417(stack);
		} else {
			Item item = stack.getItem();
			return slot == 4
				? item == Items.BLAZE_POWDER
				: (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE) && this.getInvStack(slot).isEmpty();
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return inputs;
		} else {
			return side == Direction.DOWN ? field_12841 : outputs;
		}
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		return this.isValidInvStack(slot, stack);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return slot == 3 ? stack.getItem() == Items.GLASS_BOTTLE : true;
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
			case 1:
				return this.field_12842;
			default:
				return 0;
		}
	}

	@Override
	public void setProperty(int id, int value) {
		switch (id) {
			case 0:
				this.brewTime = value;
				break;
			case 1:
				this.field_12842 = value;
		}
	}

	@Override
	public int getProperties() {
		return 2;
	}

	@Override
	public void clear() {
		this.field_15151.clear();
	}
}
