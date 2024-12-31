package net.minecraft.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
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
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.BrewingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.level.storage.LevelDataType;

public class BrewingStandBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	private static final int[] inputs = new int[]{3};
	private static final int[] field_12841 = new int[]{0, 1, 2, 3};
	private static final int[] outputs = new int[]{0, 1, 2, 4};
	private ItemStack[] stacks = new ItemStack[5];
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
		return this.stacks.length;
	}

	@Override
	public void tick() {
		if (this.field_12842 <= 0 && this.stacks[4] != null && this.stacks[4].getItem() == Items.BLAZE_POWDER) {
			this.field_12842 = 20;
			this.stacks[4].count--;
			if (this.stacks[4].count <= 0) {
				this.stacks[4] = null;
			}

			this.markDirty();
		}

		boolean bl = this.canBrew();
		boolean bl2 = this.brewTime > 0;
		if (bl2) {
			this.brewTime--;
			boolean bl3 = this.brewTime == 0;
			if (bl3 && bl) {
				this.brew();
				this.markDirty();
			} else if (!bl) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.itemBrewing != this.stacks[3].getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (bl && this.field_12842 > 0) {
			this.field_12842--;
			this.brewTime = 400;
			this.itemBrewing = this.stacks[3].getItem();
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
			if (this.stacks[i] != null) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private boolean canBrew() {
		if (this.stacks[3] != null && this.stacks[3].count > 0) {
			ItemStack itemStack = this.stacks[3];
			if (!StatusEffectStrings.method_11417(itemStack)) {
				return false;
			} else {
				for (int i = 0; i < 3; i++) {
					ItemStack itemStack2 = this.stacks[i];
					if (itemStack2 != null && StatusEffectStrings.method_11418(itemStack2, itemStack)) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	private void brew() {
		ItemStack itemStack = this.stacks[3];

		for (int i = 0; i < 3; i++) {
			this.stacks[i] = StatusEffectStrings.method_11427(itemStack, this.stacks[i]);
		}

		itemStack.count--;
		BlockPos blockPos = this.getPos();
		if (itemStack.getItem().isFood()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.count <= 0) {
				itemStack = itemStack2;
			} else {
				ItemScatterer.spawnItemStack(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
			}
		}

		if (itemStack.count <= 0) {
			itemStack = null;
		}

		this.stacks[3] = itemStack;
		this.world.syncGlobalEvent(1035, blockPos, 0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Cauldron", "Items"));
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

		this.field_12842 = nbt.getByte("Fuel");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
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

		nbt.putByte("Fuel", (byte)this.field_12842);
		return nbt;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.stacks.length ? this.stacks[slot] : null;
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_12933(this.stacks, slot, amount);
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_12932(this.stacks, slot);
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
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
		if (slot == 3) {
			return StatusEffectStrings.method_11417(stack);
		} else {
			Item item = stack.getItem();
			return slot == 4
				? item == Items.BLAZE_POWDER
				: item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
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
		Arrays.fill(this.stacks, null);
	}
}
