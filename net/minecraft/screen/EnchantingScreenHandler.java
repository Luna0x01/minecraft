package net.minecraft.screen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
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
	private final World world;
	private final BlockPos pos;
	private final Random random = new Random();
	public int enchantmentPower;
	public int[] enchantmentId = new int[3];
	public int[] enchantmentLevel = new int[]{-1, -1, -1};
	public int[] field_12271 = new int[]{-1, -1, -1};

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

	protected void method_11351(ScreenHandlerListener screenHandlerListener) {
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 0, this.enchantmentId[0]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 1, this.enchantmentId[1]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 2, this.enchantmentId[2]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 3, this.enchantmentPower & -16);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 4, this.enchantmentLevel[0]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 5, this.enchantmentLevel[1]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 6, this.enchantmentLevel[2]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 7, this.field_12271[0]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 8, this.field_12271[1]);
		screenHandlerListener.onScreenHandlerPropertyUpdate(this, 9, this.field_12271[2]);
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		this.method_11351(listener);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (int i = 0; i < this.listeners.size(); i++) {
			ScreenHandlerListener screenHandlerListener = (ScreenHandlerListener)this.listeners.get(i);
			this.method_11351(screenHandlerListener);
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
		} else if (id >= 7 && id <= 9) {
			this.field_12271[id - 7] = value;
		} else {
			super.setProperty(id, value);
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack itemStack = inventory.getInvStack(0);
			if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
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
						this.field_12271[m] = -1;
						if (this.enchantmentId[m] < m + 1) {
							this.enchantmentId[m] = 0;
						}
					}

					for (int n = 0; n < 3; n++) {
						if (this.enchantmentId[n] > 0) {
							List<EnchantmentLevelEntry> list = this.getRandomEnchantments(itemStack, n, this.enchantmentId[n]);
							if (list != null && !list.isEmpty()) {
								EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
								this.enchantmentLevel[n] = Enchantment.getId(enchantmentLevelEntry.enchantment);
								this.field_12271[n] = enchantmentLevelEntry.level;
							}
						}
					}

					this.sendContentUpdates();
				}
			} else {
				for (int i = 0; i < 3; i++) {
					this.enchantmentId[i] = 0;
					this.enchantmentLevel[i] = -1;
					this.field_12271[i] = -1;
				}
			}
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		ItemStack itemStack = this.inventory.getInvStack(0);
		ItemStack itemStack2 = this.inventory.getInvStack(1);
		int i = id + 1;
		if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.abilities.creativeMode) {
			return false;
		} else if (this.enchantmentId[id] > 0
			&& !itemStack.isEmpty()
			&& (player.experienceLevel >= i && player.experienceLevel >= this.enchantmentId[id] || player.abilities.creativeMode)) {
			if (!this.world.isClient) {
				List<EnchantmentLevelEntry> list = this.getRandomEnchantments(itemStack, id, this.enchantmentId[id]);
				if (!list.isEmpty()) {
					player.decrementXp(i);
					boolean bl = itemStack.getItem() == Items.BOOK;
					if (bl) {
						itemStack = new ItemStack(Items.ENCHANTED_BOOK);
						this.inventory.setInvStack(0, itemStack);
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
						itemStack2.decrement(i);
						if (itemStack2.isEmpty()) {
							this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					}

					player.incrementStat(Stats.ITEM_ENCHANTED);
					this.inventory.markDirty();
					this.enchantmentPower = player.getEnchantmentTableSeed();
					this.onContentChanged(this.inventory);
					this.world.method_11486(null, this.pos, Sounds.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private List<EnchantmentLevelEntry> getRandomEnchantments(ItemStack stack, int modifier, int i) {
		this.random.setSeed((long)(this.enchantmentPower + modifier));
		List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, i, false);
		if (stack.getItem() == Items.BOOK && list.size() > 1) {
			list.remove(this.random.nextInt(list.size()));
		}

		return list;
	}

	public int getLapisCount() {
		ItemStack itemStack = this.inventory.getInvStack(1);
		return itemStack.isEmpty() ? 0 : itemStack.getCount();
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (!this.world.isClient) {
			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.removeInvStack(i);
				if (!itemStack.isEmpty()) {
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
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot == 1) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (itemStack2.getItem() == Items.DYE && DyeColor.getById(itemStack2.getData()) == DyeColor.BLUE) {
				if (!this.insertItem(itemStack2, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (((Slot)this.slots.get(0)).hasStack() || !((Slot)this.slots.get(0)).canInsert(itemStack2)) {
					return ItemStack.EMPTY;
				}

				if (itemStack2.hasNbt() && itemStack2.getCount() == 1) {
					((Slot)this.slots.get(0)).setStack(itemStack2.copy());
					itemStack2.setCount(0);
				} else if (!itemStack2.isEmpty()) {
					((Slot)this.slots.get(0)).setStack(new ItemStack(itemStack2.getItem(), 1, itemStack2.getData()));
					itemStack2.decrement(1);
				}
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.method_3298(player, itemStack2);
		}

		return itemStack;
	}
}
