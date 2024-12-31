package net.minecraft.block.entity;

import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.item.BoatItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.WoodenDoorItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.storage.LevelDataType;

public class FurnaceBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	private static final int[] inputs = new int[]{0};
	private static final int[] outputs = new int[]{2, 1};
	private static final int[] fuelInputs = new int[]{1};
	private DefaultedList<ItemStack> field_15154 = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private int fuelTime;
	private int totalFuelTime;
	private int cookTime;
	private int totalCookTime;
	private String customName;

	@Override
	public int getInvSize() {
		return this.field_15154.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15154) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.field_15154.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_13926(this.field_15154, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15154, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		ItemStack itemStack = this.field_15154.get(slot);
		boolean bl = !stack.isEmpty() && stack.equalsIgnoreNbt(itemStack) && ItemStack.equalsIgnoreDamage(stack, itemStack);
		this.field_15154.set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema(FurnaceBlockEntity.class, "Items"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15154 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		class_2960.method_13927(nbt, this.field_15154);
		this.fuelTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.totalCookTime = nbt.getShort("CookTimeTotal");
		this.totalFuelTime = getBurnTime(this.field_15154.get(1));
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
		class_2960.method_13923(nbt, this.field_15154);
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
			ItemStack itemStack = this.field_15154.get(1);
			if (this.isFueled() || !itemStack.isEmpty() && !this.field_15154.get(0).isEmpty()) {
				if (!this.isFueled() && this.canAcceptRecipeOutput()) {
					this.fuelTime = getBurnTime(itemStack);
					this.totalFuelTime = this.fuelTime;
					if (this.isFueled()) {
						bl2 = true;
						if (!itemStack.isEmpty()) {
							Item item = itemStack.getItem();
							itemStack.decrement(1);
							if (itemStack.isEmpty()) {
								Item item2 = item.getRecipeRemainder();
								this.field_15154.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
							}
						}
					}
				}

				if (this.isFueled() && this.canAcceptRecipeOutput()) {
					this.cookTime++;
					if (this.cookTime == this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.getStackCookTime(this.field_15154.get(0));
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

	public int getStackCookTime(ItemStack stack) {
		return 200;
	}

	private boolean canAcceptRecipeOutput() {
		if (this.field_15154.get(0).isEmpty()) {
			return false;
		} else {
			ItemStack itemStack = SmeltingRecipeRegistry.getInstance().getResult(this.field_15154.get(0));
			if (itemStack.isEmpty()) {
				return false;
			} else {
				ItemStack itemStack2 = this.field_15154.get(2);
				if (itemStack2.isEmpty()) {
					return true;
				} else if (!itemStack2.equalsIgnoreNbt(itemStack)) {
					return false;
				} else {
					return itemStack2.getCount() < this.getInvMaxStackAmount() && itemStack2.getCount() < itemStack2.getMaxCount()
						? true
						: itemStack2.getCount() < itemStack.getMaxCount();
				}
			}
		}
	}

	public void craftRecipe() {
		if (this.canAcceptRecipeOutput()) {
			ItemStack itemStack = this.field_15154.get(0);
			ItemStack itemStack2 = SmeltingRecipeRegistry.getInstance().getResult(itemStack);
			ItemStack itemStack3 = this.field_15154.get(2);
			if (itemStack3.isEmpty()) {
				this.field_15154.set(2, itemStack2.copy());
			} else if (itemStack3.getItem() == itemStack2.getItem()) {
				itemStack3.increment(1);
			}

			if (itemStack.getItem() == Item.fromBlock(Blocks.SPONGE)
				&& itemStack.getData() == 1
				&& !this.field_15154.get(1).isEmpty()
				&& this.field_15154.get(1).getItem() == Items.BUCKET) {
				this.field_15154.set(1, new ItemStack(Items.WATER_BUCKET));
			}

			itemStack.decrement(1);
		}
	}

	public static int getBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();
			if (item == Item.fromBlock(Blocks.WOODEN_SLAB)) {
				return 150;
			} else if (item == Item.fromBlock(Blocks.WOOL)) {
				return 100;
			} else if (item == Item.fromBlock(Blocks.CARPET)) {
				return 67;
			} else if (item == Item.fromBlock(Blocks.LADDER)) {
				return 300;
			} else if (item == Item.fromBlock(Blocks.WOODEN_BUTTON)) {
				return 100;
			} else if (Block.getBlockFromItem(item).getDefaultState().getMaterial() == Material.WOOD) {
				return 300;
			} else if (item == Item.fromBlock(Blocks.COAL_BLOCK)) {
				return 16000;
			} else if (item instanceof ToolItem && "WOOD".equals(((ToolItem)item).getMaterialAsString())) {
				return 200;
			} else if (item instanceof SwordItem && "WOOD".equals(((SwordItem)item).getToolMaterial())) {
				return 200;
			} else if (item instanceof HoeItem && "WOOD".equals(((HoeItem)item).getAsString())) {
				return 200;
			} else if (item == Items.STICK) {
				return 100;
			} else if (item == Items.BOW || item == Items.FISHING_ROD) {
				return 300;
			} else if (item == Items.SIGN) {
				return 200;
			} else if (item == Items.COAL) {
				return 1600;
			} else if (item == Items.LAVA_BUCKET) {
				return 20000;
			} else if (item == Item.fromBlock(Blocks.SAPLING) || item == Items.BOWL) {
				return 100;
			} else if (item == Items.BLAZE_ROD) {
				return 2400;
			} else if (item instanceof WoodenDoorItem && item != Items.IRON_DOOR) {
				return 200;
			} else {
				return item instanceof BoatItem ? 400 : 0;
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
			ItemStack itemStack = this.field_15154.get(1);
			return isFuel(stack) || FurnaceFuelSlot.isBucket(stack) && itemStack.getItem() != Items.BUCKET;
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
		this.field_15154.clear();
	}
}
