package net.minecraft.screen;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrindstoneScreenHandler extends ScreenHandler {
	private final Inventory result = new CraftingResultInventory();
	private final Inventory input = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			GrindstoneScreenHandler.this.onContentChanged(this);
		}
	};
	private final ScreenHandlerContext context;

	public GrindstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public GrindstoneScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.GRINDSTONE, syncId);
		this.context = context;
		this.addSlot(new Slot(this.input, 0, 49, 19) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.input, 1, 49, 40) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.hasEnchantments();
			}
		});
		this.addSlot(new Slot(this.result, 2, 129, 34) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
				context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					int i = this.getExperience(world);

					while (i > 0) {
						int j = ExperienceOrbEntity.roundToOrbSize(i);
						i -= j;
						world.spawnEntity(new ExperienceOrbEntity(world, (double)blockPos.getX(), (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
					}

					world.syncWorldEvent(1042, blockPos, 0);
				}));
				GrindstoneScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
				GrindstoneScreenHandler.this.input.setStack(1, ItemStack.EMPTY);
				return stack;
			}

			private int getExperience(World world) {
				int i = 0;
				i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(0));
				i += this.getExperience(GrindstoneScreenHandler.this.input.getStack(1));
				if (i > 0) {
					int j = (int)Math.ceil((double)i / 2.0);
					return j + world.random.nextInt(j);
				} else {
					return 0;
				}
			}

			private int getExperience(ItemStack stack) {
				int i = 0;
				Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);

				for (Entry<Enchantment, Integer> entry : map.entrySet()) {
					Enchantment enchantment = (Enchantment)entry.getKey();
					Integer integer = (Integer)entry.getValue();
					if (!enchantment.isCursed()) {
						i += enchantment.getMinPower(integer);
					}
				}

				return i;
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
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
		if (inventory == this.input) {
			this.updateResult();
		}
	}

	private void updateResult() {
		ItemStack itemStack = this.input.getStack(0);
		ItemStack itemStack2 = this.input.getStack(1);
		boolean bl = !itemStack.isEmpty() || !itemStack2.isEmpty();
		boolean bl2 = !itemStack.isEmpty() && !itemStack2.isEmpty();
		if (!bl) {
			this.result.setStack(0, ItemStack.EMPTY);
		} else {
			boolean bl3 = !itemStack.isEmpty() && itemStack.getItem() != Items.ENCHANTED_BOOK && !itemStack.hasEnchantments()
				|| !itemStack2.isEmpty() && itemStack2.getItem() != Items.ENCHANTED_BOOK && !itemStack2.hasEnchantments();
			if (itemStack.getCount() > 1 || itemStack2.getCount() > 1 || !bl2 && bl3) {
				this.result.setStack(0, ItemStack.EMPTY);
				this.sendContentUpdates();
				return;
			}

			int i = 1;
			int m;
			ItemStack itemStack3;
			if (bl2) {
				if (itemStack.getItem() != itemStack2.getItem()) {
					this.result.setStack(0, ItemStack.EMPTY);
					this.sendContentUpdates();
					return;
				}

				Item item = itemStack.getItem();
				int j = item.getMaxDamage() - itemStack.getDamage();
				int k = item.getMaxDamage() - itemStack2.getDamage();
				int l = j + k + item.getMaxDamage() * 5 / 100;
				m = Math.max(item.getMaxDamage() - l, 0);
				itemStack3 = this.transferEnchantments(itemStack, itemStack2);
				if (!itemStack3.isDamageable()) {
					if (!ItemStack.areEqual(itemStack, itemStack2)) {
						this.result.setStack(0, ItemStack.EMPTY);
						this.sendContentUpdates();
						return;
					}

					i = 2;
				}
			} else {
				boolean bl4 = !itemStack.isEmpty();
				m = bl4 ? itemStack.getDamage() : itemStack2.getDamage();
				itemStack3 = bl4 ? itemStack : itemStack2;
			}

			this.result.setStack(0, this.grind(itemStack3, m, i));
		}

		this.sendContentUpdates();
	}

	private ItemStack transferEnchantments(ItemStack target, ItemStack source) {
		ItemStack itemStack = target.copy();
		Map<Enchantment, Integer> map = EnchantmentHelper.get(source);

		for (Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (!enchantment.isCursed() || EnchantmentHelper.getLevel(enchantment, itemStack) == 0) {
				itemStack.addEnchantment(enchantment, (Integer)entry.getValue());
			}
		}

		return itemStack;
	}

	private ItemStack grind(ItemStack item, int damage, int amount) {
		ItemStack itemStack = item.copy();
		itemStack.removeSubTag("Enchantments");
		itemStack.removeSubTag("StoredEnchantments");
		if (damage > 0) {
			itemStack.setDamage(damage);
		} else {
			itemStack.removeSubTag("Damage");
		}

		itemStack.setCount(amount);
		Map<Enchantment, Integer> map = (Map<Enchantment, Integer>)EnchantmentHelper.get(item)
			.entrySet()
			.stream()
			.filter(entry -> ((Enchantment)entry.getKey()).isCursed())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		EnchantmentHelper.set(map, itemStack);
		itemStack.setRepairCost(0);
		if (itemStack.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
			itemStack = new ItemStack(Items.BOOK);
			if (item.hasCustomName()) {
				itemStack.setCustomName(item.getName());
			}
		}

		for (int i = 0; i < map.size(); i++) {
			itemStack.setRepairCost(AnvilScreenHandler.getNextCost(itemStack.getRepairCost()));
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(player, world, this.input)));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.GRINDSTONE);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			ItemStack itemStack3 = this.input.getStack(0);
			ItemStack itemStack4 = this.input.getStack(1);
			if (index == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (index != 0 && index != 1) {
				if (!itemStack3.isEmpty() && !itemStack4.isEmpty()) {
					if (index >= 3 && index < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 0, 2, false)) {
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

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}
}
