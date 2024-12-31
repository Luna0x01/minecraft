package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.HopperProvider;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HopperBlockEntity extends LockableContainerBlockEntity implements HopperProvider, Tickable {
	private ItemStack[] items = new ItemStack[5];
	private String customName;
	private int transferCooldown = -1;

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		NbtList nbtList = nbt.getList("Items", 10);
		this.items = new ItemStack[this.getInvSize()];
		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}

		this.transferCooldown = nbt.getInt("TransferCooldown");

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot");
			if (j >= 0 && j < this.items.length) {
				this.items[j] = ItemStack.fromNbt(nbtCompound);
			}
		}
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i] != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				this.items[i].toNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Items", nbtList);
		nbt.putInt("TransferCooldown", this.transferCooldown);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public int getInvSize() {
		return this.items.length;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.items[slot];
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (this.items[slot] != null) {
			if (this.items[slot].count <= amount) {
				ItemStack itemStack = this.items[slot];
				this.items[slot] = null;
				return itemStack;
			} else {
				ItemStack itemStack2 = this.items[slot].split(amount);
				if (this.items[slot].count == 0) {
					this.items[slot] = null;
				}

				return itemStack2;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (this.items[slot] != null) {
			ItemStack itemStack = this.items[slot];
			this.items[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.items[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.hopper";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
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
		return true;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			this.transferCooldown--;
			if (!this.needsCooldown()) {
				this.setCooldown(0);
				this.insertAndExtract();
			}
		}
	}

	public boolean insertAndExtract() {
		if (this.world != null && !this.world.isClient) {
			if (!this.needsCooldown() && HopperBlock.isEnabled(this.getDataValue())) {
				boolean bl = false;
				if (!this.isHopperEmpty()) {
					bl = this.insert();
				}

				if (!this.isFull()) {
					bl = extract(this) || bl;
				}

				if (bl) {
					this.setCooldown(8);
					this.markDirty();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	private boolean isHopperEmpty() {
		for (ItemStack itemStack : this.items) {
			if (itemStack != null) {
				return false;
			}
		}

		return true;
	}

	private boolean isFull() {
		for (ItemStack itemStack : this.items) {
			if (itemStack == null || itemStack.count != itemStack.getMaxCount()) {
				return false;
			}
		}

		return true;
	}

	private boolean insert() {
		Inventory inventory = this.getOutputInventory();
		if (inventory == null) {
			return false;
		} else {
			Direction direction = HopperBlock.getDirection(this.getDataValue()).getOpposite();
			if (this.isInventoryFull(inventory, direction)) {
				return false;
			} else {
				for (int i = 0; i < this.getInvSize(); i++) {
					if (this.getInvStack(i) != null) {
						ItemStack itemStack = this.getInvStack(i).copy();
						ItemStack itemStack2 = transfer(inventory, this.takeInvStack(i, 1), direction);
						if (itemStack2 == null || itemStack2.count == 0) {
							inventory.markDirty();
							return true;
						}

						this.setInvStack(i, itemStack);
					}
				}

				return false;
			}
		}
	}

	private boolean isInventoryFull(Inventory inventory, Direction dir) {
		if (inventory instanceof SidedInventory) {
			SidedInventory sidedInventory = (SidedInventory)inventory;
			int[] is = sidedInventory.getAvailableSlots(dir);

			for (int i = 0; i < is.length; i++) {
				ItemStack itemStack = sidedInventory.getInvStack(is[i]);
				if (itemStack == null || itemStack.count != itemStack.getMaxCount()) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				ItemStack itemStack2 = inventory.getInvStack(k);
				if (itemStack2 == null || itemStack2.count != itemStack2.getMaxCount()) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean isInventoryEmpty(Inventory inventory, Direction dir) {
		if (inventory instanceof SidedInventory) {
			SidedInventory sidedInventory = (SidedInventory)inventory;
			int[] is = sidedInventory.getAvailableSlots(dir);

			for (int i = 0; i < is.length; i++) {
				if (sidedInventory.getInvStack(is[i]) != null) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				if (inventory.getInvStack(k) != null) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean extract(HopperProvider provider) {
		Inventory inventory = getInputInventory(provider);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			if (isInventoryEmpty(inventory, direction)) {
				return false;
			}

			if (inventory instanceof SidedInventory) {
				SidedInventory sidedInventory = (SidedInventory)inventory;
				int[] is = sidedInventory.getAvailableSlots(direction);

				for (int i = 0; i < is.length; i++) {
					if (extract(provider, inventory, is[i], direction)) {
						return true;
					}
				}
			} else {
				int j = inventory.getInvSize();

				for (int k = 0; k < j; k++) {
					if (extract(provider, inventory, k, direction)) {
						return true;
					}
				}
			}
		} else {
			for (ItemEntity itemEntity : getInputItemEntities(provider.getEntityWorld(), provider.getX(), provider.getY() + 1.0, provider.getZ())) {
				if (extract(provider, itemEntity)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean extract(HopperProvider provider, Inventory inventory, int slot, Direction dir) {
		ItemStack itemStack = inventory.getInvStack(slot);
		if (itemStack != null && canExtract(inventory, itemStack, slot, dir)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = transfer(provider, inventory.takeInvStack(slot, 1), null);
			if (itemStack3 == null || itemStack3.count == 0) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(slot, itemStack2);
		}

		return false;
	}

	public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		if (itemEntity == null) {
			return false;
		} else {
			ItemStack itemStack = itemEntity.getItemStack().copy();
			ItemStack itemStack2 = transfer(inventory, itemStack, null);
			if (itemStack2 != null && itemStack2.count != 0) {
				itemEntity.setItemStack(itemStack2);
			} else {
				bl = true;
				itemEntity.remove();
			}

			return bl;
		}
	}

	public static ItemStack transfer(Inventory inventory, ItemStack stack, Direction dir) {
		if (inventory instanceof SidedInventory && dir != null) {
			SidedInventory sidedInventory = (SidedInventory)inventory;
			int[] is = sidedInventory.getAvailableSlots(dir);

			for (int i = 0; i < is.length && stack != null && stack.count > 0; i++) {
				stack = transfer(inventory, stack, is[i], dir);
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j && stack != null && stack.count > 0; k++) {
				stack = transfer(inventory, stack, k, dir);
			}
		}

		if (stack != null && stack.count == 0) {
			stack = null;
		}

		return stack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
		return !inventory.isValidInvStack(slot, stack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
	}

	private static ItemStack transfer(Inventory inventory, ItemStack stack, int slot, Direction dir) {
		ItemStack itemStack = inventory.getInvStack(slot);
		if (canInsert(inventory, stack, slot, dir)) {
			boolean bl = false;
			if (itemStack == null) {
				inventory.setInvStack(slot, stack);
				stack = null;
				bl = true;
			} else if (canMergeItems(itemStack, stack)) {
				int i = stack.getMaxCount() - itemStack.count;
				int j = Math.min(stack.count, i);
				stack.count -= j;
				itemStack.count += j;
				bl = j > 0;
			}

			if (bl) {
				if (inventory instanceof HopperBlockEntity) {
					HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)inventory;
					if (hopperBlockEntity.isReady()) {
						hopperBlockEntity.setCooldown(8);
					}

					inventory.markDirty();
				}

				inventory.markDirty();
			}
		}

		return stack;
	}

	private Inventory getOutputInventory() {
		Direction direction = HopperBlock.getDirection(this.getDataValue());
		return getInventoryAt(
			this.getEntityWorld(),
			(double)(this.pos.getX() + direction.getOffsetX()),
			(double)(this.pos.getY() + direction.getOffsetY()),
			(double)(this.pos.getZ() + direction.getOffsetZ())
		);
	}

	public static Inventory getInputInventory(HopperProvider provider) {
		return getInventoryAt(provider.getEntityWorld(), provider.getX(), provider.getY() + 1.0, provider.getZ());
	}

	public static List<ItemEntity> getInputItemEntities(World world, double posX, double posY, double posZ) {
		return world.getEntitiesInBox(ItemEntity.class, new Box(posX - 0.5, posY - 0.5, posZ - 0.5, posX + 0.5, posY + 0.5, posZ + 0.5), EntityPredicate.VALID_ENTITY);
	}

	public static Inventory getInventoryAt(World world, double x, double y, double z) {
		Inventory inventory = null;
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = world.getBlockState(blockPos).getBlock();
		if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ((ChestBlock)block).createScreenHandlerFactory(world, blockPos);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.getEntitiesIn(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicate.VALID_INVENTORY);
			if (list.size() > 0) {
				inventory = (Inventory)list.get(world.random.nextInt(list.size()));
			}
		}

		return inventory;
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getData() != second.getData()) {
			return false;
		} else {
			return first.count > first.getMaxCount() ? false : ItemStack.equalsIgnoreDamage(first, second);
		}
	}

	@Override
	public double getX() {
		return (double)this.pos.getX() + 0.5;
	}

	@Override
	public double getY() {
		return (double)this.pos.getY() + 0.5;
	}

	@Override
	public double getZ() {
		return (double)this.pos.getZ() + 0.5;
	}

	public void setCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	public boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	public boolean isReady() {
		return this.transferCooldown <= 1;
	}

	@Override
	public String getId() {
		return "minecraft:hopper";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new HopperScreenHandler(inventory, this, player);
	}

	@Override
	public int getProperty(int key) {
		return 0;
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public int getProperties() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.items.length; i++) {
			this.items[i] = null;
		}
	}
}
