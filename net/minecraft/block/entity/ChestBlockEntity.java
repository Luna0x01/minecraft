package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.level.storage.LevelDataType;

public class ChestBlockEntity extends class_2737 implements Tickable, Inventory {
	private ItemStack[] inventoryStacks = new ItemStack[27];
	public boolean neighborChestsChecked;
	public ChestBlockEntity neighborChestNorth;
	public ChestBlockEntity neighborChestEast;
	public ChestBlockEntity neighborChestWest;
	public ChestBlockEntity neighborChestSouth;
	public float animationAngle;
	public float animationAnglePrev;
	public int viewerCount;
	private int ticksOpen;
	private ChestBlock.Type field_12843;
	private String translationKey;

	public ChestBlockEntity() {
	}

	public ChestBlockEntity(ChestBlock.Type type) {
		this.field_12843 = type;
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		this.method_11662(null);
		return this.inventoryStacks[slot];
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.method_11662(null);
		ItemStack itemStack = class_2960.method_12933(this.inventoryStacks, slot, amount);
		if (itemStack != null) {
			this.markDirty();
		}

		return itemStack;
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		this.method_11662(null);
		return class_2960.method_12932(this.inventoryStacks, slot);
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.method_11662(null);
		this.inventoryStacks[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}

		this.markDirty();
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.translationKey : "container.chest";
	}

	@Override
	public boolean hasCustomName() {
		return this.translationKey != null && !this.translationKey.isEmpty();
	}

	public void setTranslationKeyName(String name) {
		this.translationKey = name;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.BLOCK_ENTITY, new ItemListSchema("Chest", "Items"));
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.inventoryStacks = new ItemStack[this.getInvSize()];
		if (nbt.contains("CustomName", 8)) {
			this.translationKey = nbt.getString("CustomName");
		}

		if (!this.method_11661(nbt)) {
			NbtList nbtList = nbt.getList("Items", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				int j = nbtCompound.getByte("Slot") & 255;
				if (j >= 0 && j < this.inventoryStacks.length) {
					this.inventoryStacks[j] = ItemStack.fromNbt(nbtCompound);
				}
			}
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_11663(nbt)) {
			NbtList nbtList = new NbtList();

			for (int i = 0; i < this.inventoryStacks.length; i++) {
				if (this.inventoryStacks[i] != null) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putByte("Slot", (byte)i);
					this.inventoryStacks[i].toNbt(nbtCompound);
					nbtList.add(nbtCompound);
				}
			}

			nbt.put("Items", nbtList);
		}

		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.translationKey);
		}

		return nbt;
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
	public void resetBlock() {
		super.resetBlock();
		this.neighborChestsChecked = false;
	}

	private void neighborChestSanityCheck(ChestBlockEntity chest, Direction dir) {
		if (chest.isRemoved()) {
			this.neighborChestsChecked = false;
		} else if (this.neighborChestsChecked) {
			switch (dir) {
				case NORTH:
					if (this.neighborChestNorth != chest) {
						this.neighborChestsChecked = false;
					}
					break;
				case SOUTH:
					if (this.neighborChestSouth != chest) {
						this.neighborChestsChecked = false;
					}
					break;
				case EAST:
					if (this.neighborChestEast != chest) {
						this.neighborChestsChecked = false;
					}
					break;
				case WEST:
					if (this.neighborChestWest != chest) {
						this.neighborChestsChecked = false;
					}
			}
		}
	}

	public void checkNeighborChests() {
		if (!this.neighborChestsChecked) {
			this.neighborChestsChecked = true;
			this.neighborChestWest = this.getNeighborChest(Direction.WEST);
			this.neighborChestEast = this.getNeighborChest(Direction.EAST);
			this.neighborChestNorth = this.getNeighborChest(Direction.NORTH);
			this.neighborChestSouth = this.getNeighborChest(Direction.SOUTH);
		}
	}

	@Nullable
	protected ChestBlockEntity getNeighborChest(Direction dir) {
		BlockPos blockPos = this.pos.offset(dir);
		if (this.isChest(blockPos)) {
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;
				chestBlockEntity.neighborChestSanityCheck(this, dir.getOpposite());
				return chestBlockEntity;
			}
		}

		return null;
	}

	private boolean isChest(BlockPos pos) {
		if (this.world == null) {
			return false;
		} else {
			Block block = this.world.getBlockState(pos).getBlock();
			return block instanceof ChestBlock && ((ChestBlock)block).field_12621 == this.method_4806();
		}
	}

	@Override
	public void tick() {
		this.checkNeighborChests();
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.ticksOpen++;
		if (!this.world.isClient && this.viewerCount != 0 && (this.ticksOpen + i + j + k) % 200 == 0) {
			this.viewerCount = 0;
			float f = 5.0F;

			for (PlayerEntity playerEntity : this.world
				.getEntitiesInBox(
					PlayerEntity.class,
					new Box(
						(double)((float)i - 5.0F),
						(double)((float)j - 5.0F),
						(double)((float)k - 5.0F),
						(double)((float)(i + 1) + 5.0F),
						(double)((float)(j + 1) + 5.0F),
						(double)((float)(k + 1) + 5.0F)
					)
				)) {
				if (playerEntity.openScreenHandler instanceof ChestScreenHandler) {
					Inventory inventory = ((ChestScreenHandler)playerEntity.openScreenHandler).getInventory();
					if (inventory == this || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(this)) {
						this.viewerCount++;
					}
				}
			}
		}

		this.animationAnglePrev = this.animationAngle;
		float g = 0.1F;
		if (this.viewerCount > 0 && this.animationAngle == 0.0F && this.neighborChestNorth == null && this.neighborChestWest == null) {
			double d = (double)i + 0.5;
			double e = (double)k + 0.5;
			if (this.neighborChestSouth != null) {
				e += 0.5;
			}

			if (this.neighborChestEast != null) {
				d += 0.5;
			}

			this.world.playSound(null, d, (double)j + 0.5, e, Sounds.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (this.viewerCount == 0 && this.animationAngle > 0.0F || this.viewerCount > 0 && this.animationAngle < 1.0F) {
			float h = this.animationAngle;
			if (this.viewerCount > 0) {
				this.animationAngle += 0.1F;
			} else {
				this.animationAngle -= 0.1F;
			}

			if (this.animationAngle > 1.0F) {
				this.animationAngle = 1.0F;
			}

			float l = 0.5F;
			if (this.animationAngle < 0.5F && h >= 0.5F && this.neighborChestNorth == null && this.neighborChestWest == null) {
				double m = (double)i + 0.5;
				double n = (double)k + 0.5;
				if (this.neighborChestSouth != null) {
					n += 0.5;
				}

				if (this.neighborChestEast != null) {
					m += 0.5;
				}

				this.world.playSound(null, m, (double)j + 0.5, n, Sounds.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (this.animationAngle < 0.0F) {
				this.animationAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.viewerCount = data;
			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.world.addBlockAction(this.pos, this.getBlock(), 1, this.viewerCount);
			this.world.updateNeighborsAlways(this.pos, this.getBlock());
			this.world.updateNeighborsAlways(this.pos.down(), this.getBlock());
		}
	}

	@Override
	public void onInvClose(PlayerEntity player) {
		if (!player.isSpectator() && this.getBlock() instanceof ChestBlock) {
			this.viewerCount--;
			this.world.addBlockAction(this.pos, this.getBlock(), 1, this.viewerCount);
			this.world.updateNeighborsAlways(this.pos, this.getBlock());
			this.world.updateNeighborsAlways(this.pos.down(), this.getBlock());
		}
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		this.resetBlock();
		this.checkNeighborChests();
	}

	public ChestBlock.Type method_4806() {
		if (this.field_12843 == null) {
			if (this.world == null || !(this.getBlock() instanceof ChestBlock)) {
				return ChestBlock.Type.BASIC;
			}

			this.field_12843 = ((ChestBlock)this.getBlock()).field_12621;
		}

		return this.field_12843;
	}

	@Override
	public String getId() {
		return "minecraft:chest";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.method_11662(player);
		return new ChestScreenHandler(inventory, this, player);
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
		this.method_11662(null);

		for (int i = 0; i < this.inventoryStacks.length; i++) {
			this.inventoryStacks[i] = null;
		}
	}
}
