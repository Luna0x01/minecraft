package net.minecraft.container;

import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilContainer extends Container {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Inventory result = new CraftingResultInventory();
	private final Inventory inventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			AnvilContainer.this.onContentChanged(this);
		}
	};
	private final Property levelCost = Property.create();
	private final BlockContext context;
	private int repairItemUsage;
	private String newItemName;
	private final PlayerEntity player;

	public AnvilContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public AnvilContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.field_17329, i);
		this.context = blockContext;
		this.player = playerInventory.player;
		this.addProperty(this.levelCost);
		this.addSlot(new Slot(this.inventory, 0, 27, 47));
		this.addSlot(new Slot(this.inventory, 1, 76, 47));
		this.addSlot(
			new Slot(this.result, 2, 134, 47) {
				@Override
				public boolean canInsert(ItemStack itemStack) {
					return false;
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					return (playerEntity.abilities.creativeMode || playerEntity.experienceLevel >= AnvilContainer.this.levelCost.get())
						&& AnvilContainer.this.levelCost.get() > 0
						&& this.hasStack();
				}

				@Override
				public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.addExperienceLevels(-AnvilContainer.this.levelCost.get());
					}

					AnvilContainer.this.inventory.setInvStack(0, ItemStack.EMPTY);
					if (AnvilContainer.this.repairItemUsage > 0) {
						ItemStack itemStack2 = AnvilContainer.this.inventory.getInvStack(1);
						if (!itemStack2.isEmpty() && itemStack2.getCount() > AnvilContainer.this.repairItemUsage) {
							itemStack2.decrement(AnvilContainer.this.repairItemUsage);
							AnvilContainer.this.inventory.setInvStack(1, itemStack2);
						} else {
							AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
						}
					} else {
						AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
					}

					AnvilContainer.this.levelCost.set(0);
					blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
						BlockState blockState = world.getBlockState(blockPos);
						if (!playerEntity.abilities.creativeMode && blockState.matches(BlockTags.field_15486) && playerEntity.getRandom().nextFloat() < 0.12F) {
							BlockState blockState2 = AnvilBlock.getLandingState(blockState);
							if (blockState2 == null) {
								world.removeBlock(blockPos, false);
								world.playLevelEvent(1029, blockPos, 0);
							} else {
								world.setBlockState(blockPos, blockState2, 2);
								world.playLevelEvent(1030, blockPos, 0);
							}
						} else {
							world.playLevelEvent(1030, blockPos, 0);
						}
					}));
					return itemStack;
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
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.inventory) {
			this.updateResult();
		}
	}

	public void updateResult() {
		ItemStack itemStack = this.inventory.getInvStack(0);
		this.levelCost.set(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemStack.isEmpty()) {
			this.result.setInvStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
		} else {
			ItemStack itemStack2 = itemStack.copy();
			ItemStack itemStack3 = this.inventory.getInvStack(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
			j += itemStack.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
			this.repairItemUsage = 0;
			if (!itemStack3.isEmpty()) {
				boolean bl = itemStack3.getItem() == Items.field_8598 && !EnchantedBookItem.getEnchantmentTag(itemStack3).isEmpty();
				if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
					int l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					if (l <= 0) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}

					int m;
					for (m = 0; l > 0 && m < itemStack3.getCount(); m++) {
						int n = itemStack2.getDamage() - l;
						itemStack2.setDamage(n);
						i++;
						l = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);
					}

					this.repairItemUsage = m;
				} else {
					if (!bl && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.isDamageable())) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
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

						if (s < itemStack2.getDamage()) {
							itemStack2.setDamage(s);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemStack3);
					boolean bl2 = false;
					boolean bl3 = false;

					for (Enchantment enchantment : map2.keySet()) {
						if (enchantment != null) {
							int t = map.containsKey(enchantment) ? (Integer)map.get(enchantment) : 0;
							int u = (Integer)map2.get(enchantment);
							u = t == u ? u + 1 : Math.max(u, t);
							boolean bl4 = enchantment.isAcceptableItem(itemStack);
							if (this.player.abilities.creativeMode || itemStack.getItem() == Items.field_8598) {
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
								switch (enchantment.getWeight()) {
									case field_9087:
										v = 1;
										break;
									case field_9090:
										v = 2;
										break;
									case field_9088:
										v = 4;
										break;
									case field_9091:
										v = 8;
								}

								if (bl) {
									v = Math.max(1, v / 2);
								}

								i += v * u;
								if (itemStack.getCount() > 1) {
									i = 40;
								}
							}
						}
					}

					if (bl3 && !bl2) {
						this.result.setInvStack(0, ItemStack.EMPTY);
						this.levelCost.set(0);
						return;
					}
				}
			}

			if (StringUtils.isBlank(this.newItemName)) {
				if (itemStack.hasCustomName()) {
					k = 1;
					i += k;
					itemStack2.removeCustomName();
				}
			} else if (!this.newItemName.equals(itemStack.getName().getString())) {
				k = 1;
				i += k;
				itemStack2.setCustomName(new LiteralText(this.newItemName));
			}

			this.levelCost.set(j + i);
			if (i <= 0) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && this.levelCost.get() >= 40) {
				this.levelCost.set(39);
			}

			if (this.levelCost.get() >= 40 && !this.player.abilities.creativeMode) {
				itemStack2 = ItemStack.EMPTY;
			}

			if (!itemStack2.isEmpty()) {
				int w = itemStack2.getRepairCost();
				if (!itemStack3.isEmpty() && w < itemStack3.getRepairCost()) {
					w = itemStack3.getRepairCost();
				}

				if (k != i || k == 0) {
					w = getNextCost(w);
				}

				itemStack2.setRepairCost(w);
				EnchantmentHelper.set(map, itemStack2);
			}

			this.result.setInvStack(0, itemStack2);
			this.sendContentUpdates();
		}
	}

	public static int getNextCost(int i) {
		return i * 2 + 1;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(playerEntity, world, this.inventory)));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.context
			.run(
				(world, blockPos) -> !world.getBlockState(blockPos).matches(BlockTags.field_15486)
						? false
						: playerEntity.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
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

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	public void setNewItemName(String string) {
		this.newItemName = string;
		if (this.getSlot(2).hasStack()) {
			ItemStack itemStack = this.getSlot(2).getStack();
			if (StringUtils.isBlank(string)) {
				itemStack.removeCustomName();
			} else {
				itemStack.setCustomName(new LiteralText(this.newItemName));
			}
		}

		this.updateResult();
	}

	public int getLevelCost() {
		return this.levelCost.get();
	}
}
