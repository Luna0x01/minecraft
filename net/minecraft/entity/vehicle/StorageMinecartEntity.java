package net.minecraft.entity.vehicle;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_2782;
import net.minecraft.class_2960;
import net.minecraft.class_2964;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.class_2780;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements LockableScreenHandlerFactory, class_2964 {
	private ItemStack[] stacks = new ItemStack[36];
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

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		this.generateLoot(null);
		return this.stacks[slot];
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.generateLoot(null);
		return class_2960.method_12933(this.stacks, slot, amount);
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		this.generateLoot(null);
		if (this.stacks[slot] != null) {
			ItemStack itemStack = this.stacks[slot];
			this.stacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.generateLoot(null);
		this.stacks[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
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

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.lootTableId != null) {
			nbt.putString("LootTable", this.lootTableId.toString());
			if (this.lootSeed != 0L) {
				nbt.putLong("LootTableSeed", this.lootSeed);
			}
		} else {
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
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.stacks = new ItemStack[this.getInvSize()];
		if (nbt.contains("LootTable", 8)) {
			this.lootTableId = new Identifier(nbt.getString("LootTable"));
			this.lootSeed = nbt.getLong("LootTableSeed");
		} else {
			NbtList nbtList = nbt.getList("Items", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				int j = nbtCompound.getByte("Slot") & 255;
				if (j >= 0 && j < this.stacks.length) {
					this.stacks[j] = ItemStack.fromNbt(nbtCompound);
				}
			}
		}
	}

	@Override
	public boolean method_6100(PlayerEntity playerEntity, @Nullable ItemStack itemStack, Hand hand) {
		if (!this.world.isClient) {
			playerEntity.openInventory(this);
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

		for (int i = 0; i < this.stacks.length; i++) {
			this.stacks[i] = null;
		}
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
