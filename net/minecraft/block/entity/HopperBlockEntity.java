package net.minecraft.block.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.HopperProvider;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.World;

public class HopperBlockEntity extends class_2737 implements HopperProvider, Tickable {
	private DefaultedList<ItemStack> field_15155 = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long field_15156;

	public HopperBlockEntity() {
		super(BlockEntityType.HOPPER);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15155 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (!this.method_11661(nbt)) {
			class_2960.method_13927(nbt, this.field_15155);
		}

		if (nbt.contains("CustomName", 8)) {
			this.method_16835(Text.Serializer.deserializeText(nbt.getString("CustomName")));
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
		Text text = this.method_15541();
		if (text != null) {
			nbt.putString("CustomName", Text.Serializer.serialize(text));
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
	public Text method_15540() {
		return (Text)(this.field_18643 != null ? this.field_18643 : new TranslatableText("container.hopper"));
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
				this.method_16825(() -> extract(this));
			}
		}
	}

	private boolean method_16825(Supplier<Boolean> supplier) {
		if (this.world != null && !this.world.isClient) {
			if (!this.needsCooldown() && (Boolean)this.method_16783().getProperty(HopperBlock.field_18354)) {
				boolean bl = false;
				if (!this.isHopperEmpty()) {
					bl = this.insert();
				}

				if (!this.isFull()) {
					bl |= supplier.get();
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
			Direction direction = ((Direction)this.method_16783().getProperty(HopperBlock.field_18353)).getOpposite();
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
			for (ItemEntity itemEntity : method_16827(provider)) {
				if (method_13728(provider, itemEntity)) {
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

	public static boolean method_13728(Inventory inventory, ItemEntity itemEntity) {
		boolean bl = false;
		ItemStack itemStack = itemEntity.getItemStack().copy();
		ItemStack itemStack2 = method_13727(null, inventory, itemStack, null);
		if (itemStack2.isEmpty()) {
			bl = true;
			itemEntity.remove();
		} else {
			itemEntity.setItemStack(itemStack2);
		}

		return bl;
	}

	public static ItemStack method_13727(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, @Nullable Direction direction) {
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

	private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
		return !inventory.isValidInvStack(slot, stack)
			? false
			: !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side);
	}

	private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
		return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
	}

	private static ItemStack method_13726(@Nullable Inventory inventory, Inventory inventory2, ItemStack itemStack, int i, @Nullable Direction direction) {
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
						if (inventory instanceof HopperBlockEntity) {
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

	@Nullable
	private Inventory getOutputInventory() {
		Direction direction = this.method_16783().getProperty(HopperBlock.field_18353);
		return method_16823(this.getEntityWorld(), this.pos.offset(direction));
	}

	@Nullable
	public static Inventory getInputInventory(HopperProvider provider) {
		return getInventoryAt(provider.getEntityWorld(), provider.getX(), provider.getY() + 1.0, provider.getZ());
	}

	public static List<ItemEntity> method_16827(HopperProvider hopperProvider) {
		return (List<ItemEntity>)hopperProvider.method_16820()
			.getBoundingBoxes()
			.stream()
			.flatMap(
				box -> hopperProvider.getEntityWorld()
						.method_16325(
							ItemEntity.class, box.offset(hopperProvider.getX() - 0.5, hopperProvider.getY() - 0.5, hopperProvider.getZ() - 0.5), EntityPredicate.field_16700
						)
						.stream()
			)
			.collect(Collectors.toList());
	}

	@Nullable
	public static Inventory method_16823(World world, BlockPos blockPos) {
		return getInventoryAt(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
	}

	@Nullable
	public static Inventory getInventoryAt(World world, double x, double y, double z) {
		Inventory inventory = null;
		BlockPos blockPos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				inventory = (Inventory)blockEntity;
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ((ChestBlock)block).getInventory(blockState, world, blockPos, true);
				}
			}
		}

		if (inventory == null) {
			List<Entity> list = world.method_16288(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicate.field_16703);
			if (!list.isEmpty()) {
				inventory = (Inventory)list.get(world.random.nextInt(list.size()));
			}
		}

		return inventory;
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamage() != second.getDamage()) {
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

	@Override
	protected void method_16834(DefaultedList<ItemStack> defaultedList) {
		this.field_15155 = defaultedList;
	}

	public void method_16822(Entity entity) {
		if (entity instanceof ItemEntity) {
			BlockPos blockPos = this.getPos();
			if (VoxelShapes.matchesAnywhere(
				VoxelShapes.method_18049(entity.getBoundingBox().offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))),
				this.method_16820(),
				BooleanBiFunction.AND
			)) {
				this.method_16825(() -> method_13728(this, (ItemEntity)entity));
			}
		}
	}
}
