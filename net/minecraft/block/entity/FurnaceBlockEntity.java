package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class FurnaceBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	private static final int[] inputs = new int[]{0};
	private static final int[] outputs = new int[]{2, 1};
	private static final int[] fuelInputs = new int[]{1};
	private ItemStack[] stacks = new ItemStack[3];
	private int fuelTime;
	private int totalFuelTime;
	private int cookTime;
	private int totalCookTime;
	private String customName;

	@Override
	public int getInvSize() {
		return this.stacks.length;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return this.stacks[slot];
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
		boolean bl = stack != null && stack.equalsIgnoreNbt(this.stacks[slot]) && ItemStack.equalsIgnoreDamage(stack, this.stacks[slot]);
		this.stacks[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}

		if (slot == 0 && !bl) {
			this.totalCookTime = this.getStackCookTime(stack);
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.furnace";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomName(String name) {
		this.customName = name;
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

		this.fuelTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.totalCookTime = nbt.getShort("CookTimeTotal");
		this.totalFuelTime = getBurnTime(this.stacks[1]);
		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putShort("BurnTime", (short)this.fuelTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("CookTimeTotal", (short)this.totalCookTime);
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

		return nbt;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	public boolean isFueled() {
		return this.fuelTime > 0;
	}

	public static boolean isLit(Inventory inventory) {
		return inventory.getProperty(0) > 0;
	}

	@Override
	public void tick() {
		boolean bl = this.isFueled();
		boolean bl2 = false;
		if (this.isFueled()) {
			this.fuelTime--;
		}

		if (!this.world.isClient) {
			if (this.isFueled() || this.stacks[1] != null && this.stacks[0] != null) {
				if (!this.isFueled() && this.canAcceptRecipeOutput()) {
					this.totalFuelTime = this.fuelTime = getBurnTime(this.stacks[1]);
					if (this.isFueled()) {
						bl2 = true;
						if (this.stacks[1] != null) {
							this.stacks[1].count--;
							if (this.stacks[1].count == 0) {
								Item item = this.stacks[1].getItem().getRecipeRemainder();
								this.stacks[1] = item != null ? new ItemStack(item) : null;
							}
						}
					}
				}

				if (this.isFueled() && this.canAcceptRecipeOutput()) {
					this.cookTime++;
					if (this.cookTime == this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.getStackCookTime(this.stacks[0]);
						this.craftRecipe();
						bl2 = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isFueled() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
			}

			if (bl != this.isFueled()) {
				bl2 = true;
				FurnaceBlock.setBlockState(this.isFueled(), this.world, this.pos);
			}
		}

		if (bl2) {
			this.markDirty();
		}
	}

	public int getStackCookTime(@Nullable ItemStack stack) {
		return 200;
	}

	private boolean canAcceptRecipeOutput() {
		if (this.stacks[0] == null) {
			return false;
		} else {
			ItemStack itemStack = SmeltingRecipeRegistry.getInstance().getResult(this.stacks[0]);
			if (itemStack == null) {
				return false;
			} else if (this.stacks[2] == null) {
				return true;
			} else if (!this.stacks[2].equalsIgnoreNbt(itemStack)) {
				return false;
			} else {
				return this.stacks[2].count < this.getInvMaxStackAmount() && this.stacks[2].count < this.stacks[2].getMaxCount()
					? true
					: this.stacks[2].count < itemStack.getMaxCount();
			}
		}
	}

	public void craftRecipe() {
		if (this.canAcceptRecipeOutput()) {
			ItemStack itemStack = SmeltingRecipeRegistry.getInstance().getResult(this.stacks[0]);
			if (this.stacks[2] == null) {
				this.stacks[2] = itemStack.copy();
			} else if (this.stacks[2].getItem() == itemStack.getItem()) {
				this.stacks[2].count++;
			}

			if (this.stacks[0].getItem() == Item.fromBlock(Blocks.SPONGE)
				&& this.stacks[0].getData() == 1
				&& this.stacks[1] != null
				&& this.stacks[1].getItem() == Items.BUCKET) {
				this.stacks[1] = new ItemStack(Items.WATER_BUCKET);
			}

			this.stacks[0].count--;
			if (this.stacks[0].count <= 0) {
				this.stacks[0] = null;
			}
		}
	}

	public static int getBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();
			if (item instanceof BlockItem && Block.getBlockFromItem(item) != Blocks.AIR) {
				Block block = Block.getBlockFromItem(item);
				if (block == Blocks.WOODEN_SLAB) {
					return 150;
				}

				if (block.getDefaultState().getMaterial() == Material.WOOD) {
					return 300;
				}

				if (block == Blocks.COAL_BLOCK) {
					return 16000;
				}
			}

			if (item instanceof ToolItem && ((ToolItem)item).getMaterialAsString().equals("WOOD")) {
				return 200;
			} else if (item instanceof SwordItem && ((SwordItem)item).getToolMaterial().equals("WOOD")) {
				return 200;
			} else if (item instanceof HoeItem && ((HoeItem)item).getAsString().equals("WOOD")) {
				return 200;
			} else if (item == Items.STICK) {
				return 100;
			} else if (item == Items.COAL) {
				return 1600;
			} else if (item == Items.LAVA_BUCKET) {
				return 20000;
			} else if (item == Item.fromBlock(Blocks.SAPLING)) {
				return 100;
			} else {
				return item == Items.BLAZE_ROD ? 2400 : 0;
			}
		}
	}

	public static boolean isFuel(ItemStack stack) {
		return getBurnTime(stack) > 0;
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
		if (slot == 2) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			ItemStack itemStack = this.stacks[1];
			return isFuel(stack) || FurnaceFuelSlot.isBucket(stack) && (itemStack == null || itemStack.getItem() != Items.BUCKET);
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return outputs;
		} else {
			return side == Direction.UP ? inputs : fuelInputs;
		}
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		return this.isValidInvStack(slot, stack);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN && slot == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getId() {
		return "minecraft:furnace";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new FurnaceScreenHandler(inventory, this);
	}

	@Override
	public int getProperty(int key) {
		switch (key) {
			case 0:
				return this.fuelTime;
			case 1:
				return this.totalFuelTime;
			case 2:
				return this.cookTime;
			case 3:
				return this.totalCookTime;
			default:
				return 0;
		}
	}

	@Override
	public void setProperty(int id, int value) {
		switch (id) {
			case 0:
				this.fuelTime = value;
				break;
			case 1:
				this.totalFuelTime = value;
				break;
			case 2:
				this.cookTime = value;
				break;
			case 3:
				this.totalCookTime = value;
		}
	}

	@Override
	public int getProperties() {
		return 4;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.stacks.length; i++) {
			this.stacks[i] = null;
		}
	}
}
