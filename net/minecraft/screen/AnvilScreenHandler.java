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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilScreenHandler extends ScreenHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private Inventory resultInventory = new CraftingResultInventory();
	private Inventory inventory = new SimpleInventory("Repair", true, 2) {
		@Override
		public void markDirty() {
			super.markDirty();
			AnvilScreenHandler.this.onContentChanged(this);
		}
	};
	private World world;
	private BlockPos blockPos;
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
				public void onTakeItem(PlayerEntity player, ItemStack stack) {
					if (!player.abilities.creativeMode) {
						player.incrementXp(-AnvilScreenHandler.this.repairCost);
					}

					AnvilScreenHandler.this.inventory.setInvStack(0, null);
					if (AnvilScreenHandler.this.field_5420 > 0) {
						ItemStack itemStack = AnvilScreenHandler.this.inventory.getInvStack(1);
						if (itemStack != null && itemStack.count > AnvilScreenHandler.this.field_5420) {
							itemStack.count = itemStack.count - AnvilScreenHandler.this.field_5420;
							AnvilScreenHandler.this.inventory.setInvStack(1, itemStack);
						} else {
							AnvilScreenHandler.this.inventory.setInvStack(1, null);
						}
					} else {
						AnvilScreenHandler.this.inventory.setInvStack(1, null);
					}

					AnvilScreenHandler.this.repairCost = 0;
					BlockState blockState = world.getBlockState(blockPos);
					if (!player.abilities.creativeMode && !world.isClient && blockState.getBlock() == Blocks.ANVIL && player.getRandom().nextFloat() < 0.12F) {
						int i = (Integer)blockState.get(AnvilBlock.DAMAGE);
						if (++i > 2) {
							world.setAir(blockPos);
							world.syncGlobalEvent(1020, blockPos, 0);
						} else {
							world.setBlockState(blockPos, blockState.with(AnvilBlock.DAMAGE, i), 2);
							world.syncGlobalEvent(1021, blockPos, 0);
						}
					} else if (!world.isClient) {
						world.syncGlobalEvent(1021, blockPos, 0);
					}
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
		int i = 0;
		int j = 1;
		int k = 1;
		int l = 1;
		int m = 2;
		int n = 1;
		int o = 1;
		ItemStack itemStack = this.inventory.getInvStack(0);
		this.repairCost = 1;
		int p = 0;
		int q = 0;
		int r = 0;
		if (itemStack == null) {
			this.resultInventory.setInvStack(0, null);
			this.repairCost = 0;
		} else {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.inventory.getInvStack(1);
			Map<Integer, Integer> map = EnchantmentHelper.get(itemStack2);
			boolean bl = false;
			q += itemStack.getRepairCost() + (itemStack3 == null ? 0 : itemStack3.getRepairCost());
			this.field_5420 = 0;
			if (itemStack3 != null) {
				bl = itemStack3.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.getEnchantmentNbt(itemStack3).size() > 0;
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int s = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (s <= 0) {
						this.resultInventory.setInvStack(0, null);
						this.repairCost = 0;
						return;
					}

					int t;
					for (t = 0; s > 0 && t < itemStack3.count; t++) {
						int u = itemStack2.getDamage() - s;
						itemStack2.setDamage(u);
						p++;
						s = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.field_5420 = t;
				} else {
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
						this.resultInventory.setInvStack(0, null);
						this.repairCost = 0;
						return;
					}

					if (itemStack2.isDamageable() && !bl) {
						int v = itemStack.getMaxDamage() - itemStack.getDamage();
						int w = itemStack3.getMaxDamage() - itemStack3.getDamage();
						int x = w + itemStack2.getMaxDamage() * 12 / 100;
						int y = v + x;
						int z = itemStack2.getMaxDamage() - y;
						if (z < 0) {
							z = 0;
						}

						if (z < itemStack2.getData()) {
							itemStack2.setDamage(z);
							p += 2;
						}
					}

					Map<Integer, Integer> map2 = EnchantmentHelper.get(itemStack3);

					for (int aa : map2.keySet()) {
						Enchantment enchantment = Enchantment.byRawId(aa);
						if (enchantment != null) {
							int ab = map.containsKey(aa) ? (Integer)map.get(aa) : 0;
							int ac = (Integer)map2.get(aa);
							ac = ab == ac ? ++ac : Math.max(ac, ab);
							boolean bl2 = enchantment.isAcceptableItem(itemStack);
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.ENCHANTED_BOOK) {
								bl2 = true;
							}

							for (int ad : map.keySet()) {
								if (ad != aa && !enchantment.differs(Enchantment.byRawId(ad))) {
									bl2 = false;
									p++;
								}
							}

							if (bl2) {
								if (ac > enchantment.getMaximumLevel()) {
									ac = enchantment.getMaximumLevel();
								}

								map.put(aa, ac);
								int ae = 0;
								switch (enchantment.getEnchantmentType()) {
									case 1:
										ae = 8;
										break;
									case 2:
										ae = 4;
									case 3:
									case 4:
									case 6:
									case 7:
									case 8:
									case 9:
									default:
										break;
									case 5:
										ae = 2;
										break;
									case 10:
										ae = 1;
								}

								if (bl) {
									ae = Math.max(1, ae / 2);
								}

								p += ae * ac;
							}
						}
					}
				}
			}

			if (StringUtils.isBlank(this.field_5421)) {
				if (itemStack.hasCustomName()) {
					r = 1;
					p += r;
					itemStack2.removeCustomName();
				}
			} else if (!this.field_5421.equals(itemStack.getCustomName())) {
				r = 1;
				p += r;
				itemStack2.setCustomName(this.field_5421);
			}

			this.repairCost = q + p;
			if (p <= 0) {
				itemStack2 = null;
			}

			if (r == p && r > 0 && this.repairCost >= 40) {
				this.repairCost = 39;
			}

			if (this.repairCost >= 40 && !this.player.abilities.creativeMode) {
				itemStack2 = null;
			}

			if (itemStack2 != null) {
				int af = itemStack2.getRepairCost();
				if (itemStack3 != null && af < itemStack3.getRepairCost()) {
					af = itemStack3.getRepairCost();
				}

				af = af * 2 + 1;
				itemStack2.setRepairCost(af);
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
				if (itemStack != null) {
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
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot != 0 && invSlot != 1) {
				if (invSlot >= 3 && invSlot < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return null;
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
