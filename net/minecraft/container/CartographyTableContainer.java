package net.minecraft.container;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableContainer extends Container {
	private final BlockContext context;
	private boolean currentlyTakingItem;
	public final Inventory inventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			CartographyTableContainer.this.onContentChanged(this);
			super.markDirty();
		}
	};
	private final CraftingResultInventory resultSlot = new CraftingResultInventory() {
		@Override
		public void markDirty() {
			CartographyTableContainer.this.onContentChanged(this);
			super.markDirty();
		}
	};

	public CartographyTableContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public CartographyTableContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.field_17343, i);
		this.context = blockContext;
		this.addSlot(new Slot(this.inventory, 0, 15, 15) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() == Items.field_8204;
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 15, 52) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				Item item = itemStack.getItem();
				return item == Items.field_8407 || item == Items.field_8895 || item == Items.GLASS_PANE;
			}
		});
		this.addSlot(
			new Slot(this.resultSlot, 2, 145, 39) {
				@Override
				public boolean canInsert(ItemStack itemStack) {
					return false;
				}

				@Override
				public ItemStack takeStack(int i) {
					ItemStack itemStack = super.takeStack(i);
					ItemStack itemStack2 = (ItemStack)blockContext.run((BiFunction)((world, blockPos) -> {
						if (!CartographyTableContainer.this.currentlyTakingItem && CartographyTableContainer.this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE) {
							ItemStack itemStack2x = FilledMapItem.copyMap(world, CartographyTableContainer.this.inventory.getInvStack(0));
							if (itemStack2x != null) {
								itemStack2x.setCount(1);
								return itemStack2x;
							}
						}

						return itemStack;
					})).orElse(itemStack);
					CartographyTableContainer.this.inventory.takeInvStack(0, 1);
					CartographyTableContainer.this.inventory.takeInvStack(1, 1);
					return itemStack2;
				}

				@Override
				protected void onCrafted(ItemStack itemStack, int i) {
					this.takeStack(i);
					super.onCrafted(itemStack, i);
				}

				@Override
				public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
					itemStack.getItem().onCraft(itemStack, playerEntity.world, playerEntity);
					blockContext.run(
						(BiConsumer<World, BlockPos>)((world, blockPos) -> world.playSound(null, blockPos, SoundEvents.field_17484, SoundCategory.field_15245, 1.0F, 1.0F))
					);
					return super.onTakeItem(playerEntity, itemStack);
				}
			}
		);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.context, playerEntity, Blocks.field_16336);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inventory.getInvStack(0);
		ItemStack itemStack2 = this.inventory.getInvStack(1);
		ItemStack itemStack3 = this.resultSlot.getInvStack(2);
		if (itemStack3.isEmpty() || !itemStack.isEmpty() && !itemStack2.isEmpty()) {
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				this.updateResult(itemStack, itemStack2, itemStack3);
			}
		} else {
			this.resultSlot.removeInvStack(2);
		}
	}

	private void updateResult(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
			Item item = itemStack2.getItem();
			MapState mapState = FilledMapItem.getMapState(itemStack, world);
			if (mapState != null) {
				ItemStack itemStack4;
				if (item == Items.field_8407 && !mapState.locked && mapState.scale < 4) {
					itemStack4 = itemStack.copy();
					itemStack4.setCount(1);
					itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
					this.sendContentUpdates();
				} else if (item == Items.GLASS_PANE && !mapState.locked) {
					itemStack4 = itemStack.copy();
					itemStack4.setCount(1);
					this.sendContentUpdates();
				} else {
					if (item != Items.field_8895) {
						this.resultSlot.removeInvStack(2);
						this.sendContentUpdates();
						return;
					}

					itemStack4 = itemStack.copy();
					itemStack4.setCount(2);
					this.sendContentUpdates();
				}

				if (!ItemStack.areEqualIgnoreDamage(itemStack4, itemStack3)) {
					this.resultSlot.setInvStack(2, itemStack4);
					this.sendContentUpdates();
				}
			}
		}));
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
		return false;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			ItemStack itemStack3 = itemStack2;
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE) {
					itemStack3 = (ItemStack)this.context.run((BiFunction)((world, blockPos) -> {
						ItemStack itemStack2x = FilledMapItem.copyMap(world, this.inventory.getInvStack(0));
						if (itemStack2x != null) {
							itemStack2x.setCount(1);
							return itemStack2x;
						} else {
							return itemStack2;
						}
					})).orElse(itemStack2);
				}

				item.onCraft(itemStack3, playerEntity.world, playerEntity);
				if (!this.insertItem(itemStack3, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack3, itemStack);
			} else if (i != 1 && i != 0) {
				if (item == Items.field_8204) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (item != Items.field_8407 && item != Items.field_8895 && item != Items.GLASS_PANE) {
					if (i >= 3 && i < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack3.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack3.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			this.currentlyTakingItem = true;
			slot.onTakeItem(playerEntity, itemStack3);
			this.currentlyTakingItem = false;
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.resultSlot.removeInvStack(2);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory)));
	}
}
