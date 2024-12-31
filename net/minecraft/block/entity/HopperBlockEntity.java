package net.minecraft.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.HopperProvider;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class HopperBlockEntity extends class_2737 implements HopperProvider, Tickable {
	private DefaultedList<ItemStack> field_15155 = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long field_15156;

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema(HopperBlockEntity.class, "Items"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15155 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11661(nbt)) {
			class_2960.method_13927(nbt, this.field_15155);
		}

		if (nbt.contains("CustomName", 8)) {
			this.name = nbt.getString("CustomName");
		}

		this.transferCooldown = nbt.getInt("TransferCooldown");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_11663(nbt)) {
			class_2960.method_13923(nbt, this.field_15155);
		}

		nbt.putInt("TransferCooldown", this.transferCooldown);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.name);
		}

		return nbt;
	}

	@Override
	public int getInvSize() {
		return this.field_15155.size();
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.method_11662(null);
		return class_2960.method_13926(this.method_13730(), slot, amount);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.method_11662(null);
		this.method_13730().set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.name : "container.hopper";
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			this.transferCooldown--;
			this.field_15156 = this.world.getLastUpdateTime();
			if (!this.needsCooldown()) {
				this.setCooldown(0);
				this.insertAndExtract();
			}
		}
	}

	private boolean insertAndExtract() {
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
		for (ItemStack itemStack : this.field_15155) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.isHopperEmpty();
	}

	private boolean isFull() {
		for (ItemStack itemStack : this.field_15155) {
			if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
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
					if (!this.getInvStack(i).isEmpty()) {
						ItemStack itemStack = this.getInvStack(i).copy();
						ItemStack itemStack2 = method_13727(this, inventory, this.takeInvStack(i, 1), direction);
						if (itemStack2.isEmpty()) {
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

			for (int i : is) {
				ItemStack itemStack = sidedInventory.getInvStack(i);
				if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				ItemStack itemStack2 = inventory.getInvStack(k);
				if (itemStack2.isEmpty() || itemStack2.getCount() != itemStack2.getMaxCount()) {
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

			for (int i : is) {
				if (!sidedInventory.getInvStack(i).isEmpty()) {
					return false;
				}
			}
		} else {
			int j = inventory.getInvSize();

			for (int k = 0; k < j; k++) {
				if (!inventory.getInvStack(k).isEmpty()) {
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

				for (int i : is) {
					if (extract(provider, inventory, i, direction)) {
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
			for (ItemEntity itemEntity : getInputItemEntities(provider.getEntityWorld(), provider.getX(), provider.getY(), provider.getZ())) {
				if (method_13728(null, provider, itemEntity)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean extract(HopperProvider provider, Inventory inventory, int slot, Direction dir) {
		ItemStack itemStack = inventory.getInvStack(slot);
		if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, dir)) {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = method_13727(inventory, provider, inventory.takeInvStack(slot, 1), null);
			if (itemStack3.isEmpty()) {
				inventory.markDirty();
				return true;
			}

			inventory.setInvStack(slot, itemStack2);
		}

		return false;
	}

	public static boolean method_13728(Inventory inventory, Inventory inventory2, ItemEntity itemEntity) {
		boolean bl = false;
		if (itemEntity == null) {
			return false;
		} else {
			ItemStack itemStack = itemEntity.getItemStack().copy();
			ItemStack itemStack2 = method_13727(inventory, inventory2, itemStack, null);
			if (itemStack2.isEmpty()) {
				bl = true;
				itemEntity.remove();
			} else {
				itemEntity.setItemStack(itemStack2);
			}

			return bl;
		}
	}

	public static ItemStack method_13727(Inventory inventory, Inventory inventory2, ItemStack itemStack, @Nullable Direction direction) {
		if (inventory2 instanceof SidedInventory && direction != null) {
			SidedInventory sidedInventory = (SidedInventory)inventory2;
			int[] is = sidedInventory.getAvailableSlots(direction);

			for (int i = 0; i < is.length && !itemStack.isEmpty(); i++) {
				itemStack = method_13726(inventory, inventory2, itemStack, is[i], direction);
			}
		} else {
			int j = inventory2.getInvSize();

			for (int k = 0; k < j && !itemStack.isEmpty(); k++) {
				itemStack = method_13726(inventory, inventory2, itemStack, k, direction);
			}
		}

		return itemStack;
	}

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction side) {
		return !inventory.isValidInvStack(slot, stack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
	}

	private static ItemStack method_13726(Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, Direction direction) {
		ItemStack itemStack2 = inventory2.getInvStack(i);
		if (canInsert(inventory2, itemStack, i, direction)) {
			boolean bl = false;
			boolean bl2 = inventory2.isEmpty();
			if (itemStack2.isEmpty()) {
				inventory2.setInvStack(i, itemStack);
				itemStack = ItemStack.EMPTY;
				bl = true;
			} else if (canMergeItems(itemStack2, itemStack)) {
				int j = itemStack.getMaxCount() - itemStack2.getCount();
				int k = Math.min(itemStack.getCount(), j);
				itemStack.decrement(k);
				itemStack2.increment(k);
				bl = k > 0;
			}

			if (bl) {
				if (bl2 && inventory2 instanceof HopperBlockEntity) {
					HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)inventory2;
					if (!hopperBlockEntity.isReady()) {
						int l = 0;
						if (inventory != null && inventory instanceof HopperBlockEntity) {
							HopperBlockEntity hopperBlockEntity2 = (HopperBlockEntity)inventory;
							if (hopperBlockEntity.field_15156 >= hopperBlockEntity2.field_15156) {
								l = 1;
							}
						}

						hopperBlockEntity.setCooldown(8 - l);
					}
				}

				inventory2.markDirty();
			}
		}

		return itemStack;
	}

	private Inventory getOutputInventory() {
		Direction direction = HopperBlock.getDirection(this.getDataValue());
		return getInventoryAt(
			this.getEntityWorld(),
			this.getX() + (double)direction.getOffsetX(),
			this.getY() + (double)direction.getOffsetY(),
			this.getZ() + (double)direction.getOffsetZ()
		);
	}

	public static Inventory getInputInventory(HopperProvider provider) {
		return getInventoryAt(provider.getEntityWorld(), provider.getX(), provider.getY() + 1.0, provider.getZ());
	}

	public static List<ItemEntity> getInputItemEntities(World world, double posX, double posY, double posZ) {
		return world.getEntitiesInBox(ItemEntity.class, new Box(posX - 0.5, posY, posZ - 0.5, posX + 0.5, posY + 1.5, posZ + 0.5), EntityPredicate.VALID_ENTITY);
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
					inventory = ((ChestBlock)block).method_8702(world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.getEntitiesIn(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicate.VALID_INVENTORY);
			if (!list.isEmpty()) {
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
			return first.getCount() > first.getMaxCount() ? false : ItemStack.equalsIgnoreDamage(first, second);
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

	private void setCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	private boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	private boolean isReady() {
		return this.transferCooldown > 8;
	}

	@Override
	public String getId() {
		return "minecraft:hopper";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.method_11662(player);
		return new HopperScreenHandler(inventory, this, player);
	}

	@Override
	protected DefaultedList<ItemStack> method_13730() {
		return this.field_15155;
	}
}
