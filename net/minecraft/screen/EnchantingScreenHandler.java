package net.minecraft.screen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchantingScreenHandler extends ScreenHandler {
	public Inventory inventory = new SimpleInventory("Enchant", true, 2) {
		@Override
		public int getInvMaxStackAmount() {
			return 64;
		}

		@Override
		public void markDirty() {
			super.markDirty();
			EnchantingScreenHandler.this.onContentChanged(this);
		}
	};
	private World world;
	private BlockPos pos;
	private Random random = new Random();
	public int enchantmentPower;
	public int[] enchantmentId = new int[3];
	public int[] enchantmentLevel = new int[]{-1, -1, -1};

	public EnchantingScreenHandler(PlayerInventory playerInventory, World world) {
		this(playerInventory, world, BlockPos.ORIGIN);
	}

	public EnchantingScreenHandler(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
		this.enchantmentPower = playerInventory.player.getEnchantmentTableSeed();
		this.addSlot(new Slot(this.inventory, 0, 15, 47) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return true;
			}

			@Override
			public int getMaxStackAmount() {
				return 1;
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 35, 47) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == Items.DYE && DyeColor.getById(stack.getData()) == DyeColor.BLUE;
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		listener.onScreenHandlerPropertyUpdate(this, 0, this.enchantmentId[0]);
		listener.onScreenHandlerPropertyUpdate(this, 1, this.enchantmentId[1]);
		listener.onScreenHandlerPropertyUpdate(this, 2, this.enchantmentId[2]);
		listener.onScreenHandlerPropertyUpdate(this, 3, this.enchantmentPower & -16);
		listener.onScreenHandlerPropertyUpdate(this, 4, this.enchantmentLevel[0]);
		listener.onScreenHandlerPropertyUpdate(this, 5, this.enchantmentLevel[1]);
		listener.onScreenHandlerPropertyUpdate(this, 6, this.enchantmentLevel[2]);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (int i = 0; i < this.listeners.size(); i++) {
			ScreenHandlerListener screenHandlerListener = (ScreenHandlerListener)this.listeners.get(i);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 0, this.enchantmentId[0]);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 1, this.enchantmentId[1]);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 2, this.enchantmentId[2]);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 3, this.enchantmentPower & -16);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 4, this.enchantmentLevel[0]);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 5, this.enchantmentLevel[1]);
			screenHandlerListener.onScreenHandlerPropertyUpdate(this, 6, this.enchantmentLevel[2]);
		}
	}

	@Override
	public void setProperty(int id, int value) {
		if (id >= 0 && id <= 2) {
			this.enchantmentId[id] = value;
		} else if (id == 3) {
			this.enchantmentPower = value;
		} else if (id >= 4 && id <= 6) {
			this.enchantmentLevel[id - 4] = value;
		} else {
			super.setProperty(id, value);
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack itemStack = inventory.getInvStack(0);
			if (itemStack != null && itemStack.isEnchantable()) {
				if (!this.world.isClient) {
					int j = 0;

					for (int k = -1; k <= 1; k++) {
						for (int l = -1; l <= 1; l++) {
							if ((k != 0 || l != 0) && this.world.isAir(this.pos.add(l, 0, k)) && this.world.isAir(this.pos.add(l, 1, k))) {
								if (this.world.getBlockState(this.pos.add(l * 2, 0, k * 2)).getBlock() == Blocks.BOOKSHELF) {
									j++;
								}

								if (this.world.getBlockState(this.pos.add(l * 2, 1, k * 2)).getBlock() == Blocks.BOOKSHELF) {
									j++;
								}

								if (l != 0 && k != 0) {
									if (this.world.getBlockState(this.pos.add(l * 2, 0, k)).getBlock() == Blocks.BOOKSHELF) {
										j++;
									}

									if (this.world.getBlockState(this.pos.add(l * 2, 1, k)).getBlock() == Blocks.BOOKSHELF) {
										j++;
									}

									if (this.world.getBlockState(this.pos.add(l, 0, k * 2)).getBlock() == Blocks.BOOKSHELF) {
										j++;
									}

									if (this.world.getBlockState(this.pos.add(l, 1, k * 2)).getBlock() == Blocks.BOOKSHELF) {
										j++;
									}
								}
							}
						}
					}

					this.random.setSeed((long)this.enchantmentPower);

					for (int m = 0; m < 3; m++) {
						this.enchantmentId[m] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, m, j, itemStack);
						this.enchantmentLevel[m] = -1;
						if (this.enchantmentId[m] < m + 1) {
							this.enchantmentId[m] = 0;
						}
					}

					for (int n = 0; n < 3; n++) {
						if (this.enchantmentId[n] > 0) {
							List<EnchantmentLevelEntry> list = this.getRandomEnchantments(itemStack, n, this.enchantmentId[n]);
							if (list != null && !list.isEmpty()) {
								EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
								this.enchantmentLevel[n] = enchantmentLevelEntry.enchantment.id | enchantmentLevelEntry.level << 8;
							}
						}
					}

					this.sendContentUpdates();
				}
			} else {
				for (int i = 0; i < 3; i++) {
					this.enchantmentId[i] = 0;
					this.enchantmentLevel[i] = -1;
				}
			}
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		ItemStack itemStack = this.inventory.getInvStack(0);
		ItemStack itemStack2 = this.inventory.getInvStack(1);
		int i = id + 1;
		if ((itemStack2 == null || itemStack2.count < i) && !player.abilities.creativeMode) {
			return false;
		} else if (this.enchantmentId[id] > 0
			&& itemStack != null
			&& (player.experienceLevel >= i && player.experienceLevel >= this.enchantmentId[id] || player.abilities.creativeMode)) {
			if (!this.world.isClient) {
				List<EnchantmentLevelEntry> list = this.getRandomEnchantments(itemStack, id, this.enchantmentId[id]);
				boolean bl = itemStack.getItem() == Items.BOOK;
				if (list != null) {
					player.decrementXp(i);
					if (bl) {
						itemStack.setItem(Items.ENCHANTED_BOOK);
					}

					for (int j = 0; j < list.size(); j++) {
						EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(j);
						if (bl) {
							Items.ENCHANTED_BOOK.addEnchantment(itemStack, enchantmentLevelEntry);
						} else {
							itemStack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
						}
					}

					if (!player.abilities.creativeMode) {
						itemStack2.count -= i;
						if (itemStack2.count <= 0) {
							this.inventory.setInvStack(1, null);
						}
					}

					player.incrementStat(Stats.ITEM_ENCHANTED);
					this.inventory.markDirty();
					this.enchantmentPower = player.getEnchantmentTableSeed();
					this.onContentChanged(this.inventory);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private List<EnchantmentLevelEntry> getRandomEnchantments(ItemStack stack, int modifier, int i) {
		this.random.setSeed((long)(this.enchantmentPower + modifier));
		List<EnchantmentLevelEntry> list = EnchantmentHelper.getEnchantmentInfoEntries(this.random, stack, i);
		if (stack.getItem() == Items.BOOK && list != null && list.size() > 1) {
			list.remove(this.random.nextInt(list.size()));
		}

		return list;
	}

	public int getLapisCount() {
		ItemStack itemStack = this.inventory.getInvStack(1);
		return itemStack == null ? 0 : itemStack.count;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (!this.world.isClient) {
			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.removeInvStack(i);
				if (itemStack != null) {
					player.dropItem(itemStack, false);
				}
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.ENCHANTING_TABLE
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return null;
				}
			} else if (invSlot == 1) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return null;
				}
			} else if (itemStack2.getItem() == Items.DYE && DyeColor.getById(itemStack2.getData()) == DyeColor.BLUE) {
				if (!this.insertItem(itemStack2, 1, 2, true)) {
					return null;
				}
			} else {
				if (((Slot)this.slots.get(0)).hasStack() || !((Slot)this.slots.get(0)).canInsert(itemStack2)) {
					return null;
				}

				if (itemStack2.hasNbt() && itemStack2.count == 1) {
					((Slot)this.slots.get(0)).setStack(itemStack2.copy());
					itemStack2.count = 0;
				} else if (itemStack2.count >= 1) {
					((Slot)this.slots.get(0)).setStack(new ItemStack(itemStack2.getItem(), 1, itemStack2.getData()));
					itemStack2.count--;
				}
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}

			if (itemStack2.count == itemStack.count) {
				return null;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}
}
