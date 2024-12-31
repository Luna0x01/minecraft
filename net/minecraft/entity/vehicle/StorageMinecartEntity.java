package net.minecraft.entity.vehicle;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_2782;
import net.minecraft.class_2960;
import net.minecraft.class_2964;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.class_2780;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements LockableScreenHandlerFactory, class_2964 {
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
	private boolean field_6145 = true;
	private Identifier lootTableId;
	private long lootSeed;

	public StorageMinecartEntity(World world) {
		super(world);
	}

	public StorageMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemScatterer.spawn(this.world, this, this);
		}
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		this.generateLoot(null);
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.generateLoot(null);
		return class_2960.method_13926(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		this.generateLoot(null);
		ItemStack itemStack = this.inventory.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.inventory.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.generateLoot(null);
		this.inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.removed ? false : !(player.squaredDistanceTo(this) > 64.0);
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
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Nullable
	@Override
	public Entity changeDimension(int newDimension) {
		this.field_6145 = false;
		return super.changeDimension(newDimension);
	}

	@Override
	public void remove() {
		if (this.field_6145) {
			ItemScatterer.spawn(this.world, this, this);
		}

		super.remove();
	}

	@Override
	public void method_12991(boolean bl) {
		this.field_6145 = bl;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer, Class<?> entityClass) {
		AbstractMinecartEntity.registerDataFixes(dataFixer, entityClass);
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemListSchema(entityClass, "Items"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.lootTableId != null) {
			nbt.putString("LootTable", this.lootTableId.toString());
			if (this.lootSeed != 0L) {
				nbt.putLong("LootTableSeed", this.lootSeed);
			}
		} else {
			class_2960.method_13923(nbt, this.inventory);
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		if (nbt.contains("LootTable", 8)) {
			this.lootTableId = new Identifier(nbt.getString("LootTable"));
			this.lootSeed = nbt.getLong("LootTableSeed");
		} else {
			class_2960.method_13927(nbt, this.inventory);
		}
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if (!this.world.isClient) {
			player.openInventory(this);
		}

		return true;
	}

	@Override
	protected void applySlowdown() {
		float f = 0.98F;
		if (this.lootTableId == null) {
			int i = 15 - ScreenHandler.calculateComparatorOutput(this);
			f += (float)i * 0.001F;
		}

		this.velocityX *= (double)f;
		this.velocityY *= 0.0;
		this.velocityZ *= (double)f;
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
	public boolean hasLock() {
		return false;
	}

	@Override
	public void setLock(ScreenHandlerLock lock) {
	}

	@Override
	public ScreenHandlerLock getLock() {
		return ScreenHandlerLock.NONE;
	}

	public void generateLoot(@Nullable PlayerEntity player) {
		if (this.lootTableId != null) {
			class_2780 lv = this.world.method_11487().method_12006(this.lootTableId);
			this.lootTableId = null;
			Random random;
			if (this.lootSeed == 0L) {
				random = new Random();
			} else {
				random = new Random(this.lootSeed);
			}

			class_2782.class_2783 lv2 = new class_2782.class_2783((ServerWorld)this.world);
			if (player != null) {
				lv2.method_11995(player.method_13271());
			}

			lv.method_11983(this, random, lv2.method_11994());
		}
	}

	@Override
	public void clear() {
		this.generateLoot(null);
		this.inventory.clear();
	}

	public void setLootTable(Identifier id, long lootSeed) {
		this.lootTableId = id;
		this.lootSeed = lootSeed;
	}

	@Override
	public Identifier getLootTableId() {
		return this.lootTableId;
	}
}
