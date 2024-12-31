package net.minecraft.screen;

import java.util.Map;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilScreenHandler extends ScreenHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Inventory resultInventory = new CraftingResultInventory();
	private final Inventory inventory = new SimpleInventory("Repair", true, 2) {
		@Override
		public void markDirty() {
			super.markDirty();
			AnvilScreenHandler.this.onContentChanged(this);
		}
	};
	private final World world;
	private final BlockPos blockPos;
	public int repairCost;
	private int field_5420;
	private String field_5421;
	private final PlayerEntity player;

	public AnvilScreenHandler(PlayerInventory playerInventory, World world, PlayerEntity playerEntity) {
		this(playerInventory, world, BlockPos.ORIGIN, playerEntity);
	}

	public AnvilScreenHandler(PlayerInventory playerInventory, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.blockPos = blockPos;
		this.world = world;
		this.player = playerEntity;
		this.addSlot(new Slot(this.inventory, 0, 27, 47));
		this.addSlot(new Slot(this.inventory, 1, 76, 47));
		this.addSlot(
			new Slot(this.resultInventory, 2, 134, 47) {
				@Override
				public boolean canInsert(ItemStack stack) {
					return false;
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					return (playerEntity.abilities.creativeMode || playerEntity.experienceLevel >= AnvilScreenHandler.this.repairCost)
						&& AnvilScreenHandler.this.repairCost > 0
						&& this.hasStack();
				}

				@Override
				public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.incrementXp(-AnvilScreenHandler.this.repairCost);
					}

					ItemStack itemStack2 = AnvilScreenHandler.this.inventory.getInvStack(0);
					if (itemStack2.getCount() != 1 && !playerEntity.abilities.creativeMode && !(itemStack2.getItem() instanceof NameTagItem)) {
						itemStack2.setCount(itemStack2.getCount() - 1);
					} else {
						AnvilScreenHandler.this.inventory.setInvStack(0, ItemStack.EMPTY);
					}

					if (AnvilScreenHandler.this.field_5420 > 0) {
						ItemStack itemStack3 = AnvilScreenHandler.this.inventory.getInvStack(1);
						if (!itemStack3.isEmpty() && itemStack3.getCount() > AnvilScreenHandler.this.field_5420) {
							itemStack3.decrement(AnvilScreenHandler.this.field_5420);
							AnvilScreenHandler.this.inventory.setInvStack(1, itemStack3);
						} else {
							AnvilScreenHandler.this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					} else {
						AnvilScreenHandler.this.inventory.setInvStack(1, ItemStack.EMPTY);
					}

					AnvilScreenHandler.this.repairCost = 0;
					BlockState blockState = world.getBlockState(blockPos);
					if (!playerEntity.abilities.creativeMode && !world.isClient && blockState.getBlock() == Blocks.ANVIL && playerEntity.getRandom().nextFloat() < 0.12F) {
						int i = (Integer)blockState.get(AnvilBlock.DAMAGE);
						if (++i > 2) {
							world.setAir(blockPos);
							world.syncGlobalEvent(1029, blockPos, 0);
						} else {
							world.setBlockState(blockPos, blockState.with(AnvilBlock.DAMAGE, i), 2);
							world.syncGlobalEvent(1030, blockPos, 0);
						}
					} else if (!world.isClient) {
						world.syncGlobalEvent(1030, blockPos, 0);
					}

					return itemStack;
				}
			}
		);

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
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.inventory) {
			this.updateResult();
		}
	}

	public void updateResult() {
		ItemStack itemStack = this.inventory.getInvStack(0);
		this.repairCost = 1;
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemStack.isEmpty()) {
			this.resultInventory.setInvStack(0, ItemStack.EMPTY);
			this.repairCost = 0;
		} else {
			ItemStack itemStack2 = itemStack.copy();
			if (itemStack2.getCount() > 1 && !this.player.abilities.creativeMode && !(itemStack2.getItem() instanceof NameTagItem)) {
				itemStack2.setCount(1);
			}

			ItemStack itemStack3 = this.inventory.getInvStack(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack2);
			j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
			this.field_5420 = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.getEnchantmentNbt(itemStack3).isEmpty();
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (l <= 0) {
						this.resultInventory.setInvStack(0, ItemStack.EMPTY);
						this.repairCost = 0;
						return;
					}

					int m;
					for (m = 0; l > 0 && m < itemStack3.getCount(); m++) {
						int n = itemStack2.getDamage() - l;
						itemStack2.setDamage(n);
						i++;
						l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.field_5420 = m;
				} else {
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
						this.resultInventory.setInvStack(0, ItemStack.EMPTY);
						this.repairCost = 0;
						return;
					}

					if (itemStack2.isDamageable() && !bl) {
						int o = itemStack.getMaxDamage() - itemStack.getDamage();
						int p = itemStack3.getMaxDamage() - itemStack3.getDamage();
						int q = p + itemStack2.getMaxDamage() * 12 / 100;
						int r = o + q;
						int s = itemStack2.getMaxDamage() - r;
						if (s < 0) {
							s = 0;
						}

						if (s < itemStack2.getData()) {
							itemStack2.setDamage(s);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map2 = EnchantmentHelper.get(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (Enchantment enchantment : map2.keySet()) {
						if (enchantment != null) {
							int t = map.containsKey(enchantment) ? (Integer)map.get(enchantment) : 0;
							int u = (Integer)map2.get(enchantment);
							u = t == u ? u + 1 : Math.max(u, t);
							boolean bl4 = enchantment.isAcceptableItem(itemStack);
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.ENCHANTED_BOOK) {
								bl4 = true;
							}

							for (Enchantment enchantment2 : map.keySet()) {
								if (enchantment2 != enchantment && !enchantment.isDifferent(enchantment2)) {
									bl4 = false;
									i++;
								}
							}

							if (!bl4) {
								bl3 = true;
							} else {
								bl2 = true;
								if (u > enchantment.getMaximumLevel()) {
									u = enchantment.getMaximumLevel();
								}

								map.put(enchantment, u);
								int v = 0;
								switch (enchantment.getRarity()) {
									case COMMON:
										v = 1;
										break;
									case UNCOMMON:
										v = 2;
										break;
									case RARE:
										v = 4;
										break;
									case VERY_RARE:
										v = 8;
								}

								if (bl) {
									v = Math.max(1, v / 2);
								}

								i += v * u;
							}
						}
					}

					if (bl3 && !bl2) {
						this.resultInventory.setInvStack(0, ItemStack.EMPTY);
						this.repairCost = 0;
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.field_5421)) {
				if (itemStack.hasCustomName()) {
					k = 1;
					i += k;
					itemStack2.removeCustomName();
				}
			} else if (!this.field_5421.equals(itemStack.getCustomName())) {
				k = 1;
				i += k;
				itemStack2.setCustomName(this.field_5421);
			}

			this.repairCost = j + i;
			if (i <= 0) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && this.repairCost >= 40) {
				this.repairCost = 39;
			}

			if (this.repairCost >= 40 && !this.player.abilities.creativeMode) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (!itemStack2.isEmpty()) {
				int w = itemStack2.getRepairCost();
				if (!itemStack3.isEmpty() && w < itemStack3.getRepairCost()) {
					w = itemStack3.getRepairCost();
				}

				if (k != i || k == 0) {
					w = w * 2 + 1;
				}

				itemStack2.setRepairCost(w);
				EnchantmentHelper.set(map, itemStack2);
			}

			this.resultInventory.setInvStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		listener.onScreenHandlerPropertyUpdate(this, 0, this.repairCost);
	}

	@Override
	public void setProperty(int id, int value) {
		if (id == 0) {
			this.repairCost = value;
		}
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
		return this.world.getBlockState(this.blockPos).getBlock() != Blocks.ANVIL
			? false
			: !(player.squaredDistanceTo((double)this.blockPos.getX() + 0.5, (double)this.blockPos.getY() + 0.5, (double)this.blockPos.getZ() + 0.5) > 64.0);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot != 0 && invSlot != 1) {
				if (invSlot >= 3 && invSlot < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
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

	public void rename(String customName) {
		this.field_5421 = customName;
		if (this.getSlot(2).hasStack()) {
			ItemStack itemStack = this.getSlot(2).getStack();
			if (StringUtils.isBlank(customName)) {
				itemStack.removeCustomName();
			} else {
				itemStack.setCustomName(this.field_5421);
			}
		}

		this.updateResult();
	}
}
