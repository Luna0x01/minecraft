package net.minecraft.screen;

import java.util.List;
import java.util.Random;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.registry.Registry;

public class EnchantmentScreenHandler extends ScreenHandler {
	private final Inventory inventory = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			EnchantmentScreenHandler.this.onContentChanged(this);
		}
	};
	private final ScreenHandlerContext context;
	private final Random random = new Random();
	private final Property seed = Property.create();
	public final int[] enchantmentPower = new int[3];
	public final int[] enchantmentId = new int[]{-1, -1, -1};
	public final int[] enchantmentLevel = new int[]{-1, -1, -1};

	public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.ENCHANTMENT, syncId);
		this.context = context;
		this.addSlot(new Slot(this.inventory, 0, 15, 47) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return true;
			}

			@Override
			public int getMaxItemCount() {
				return 1;
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 35, 47) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Items.LAPIS_LAZULI);
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

		this.addProperty(Property.create(this.enchantmentPower, 0));
		this.addProperty(Property.create(this.enchantmentPower, 1));
		this.addProperty(Property.create(this.enchantmentPower, 2));
		this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
		this.addProperty(Property.create(this.enchantmentId, 0));
		this.addProperty(Property.create(this.enchantmentId, 1));
		this.addProperty(Property.create(this.enchantmentId, 2));
		this.addProperty(Property.create(this.enchantmentLevel, 0));
		this.addProperty(Property.create(this.enchantmentLevel, 1));
		this.addProperty(Property.create(this.enchantmentLevel, 2));
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack itemStack = inventory.getStack(0);
			if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
				this.context.run((world, pos) -> {
					int ix = 0;

					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if ((j != 0 || k != 0) && world.isAir(pos.add(k, 0, j)) && world.isAir(pos.add(k, 1, j))) {
								if (world.getBlockState(pos.add(k * 2, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
									ix++;
								}

								if (world.getBlockState(pos.add(k * 2, 1, j * 2)).isOf(Blocks.BOOKSHELF)) {
									ix++;
								}

								if (k != 0 && j != 0) {
									if (world.getBlockState(pos.add(k * 2, 0, j)).isOf(Blocks.BOOKSHELF)) {
										ix++;
									}

									if (world.getBlockState(pos.add(k * 2, 1, j)).isOf(Blocks.BOOKSHELF)) {
										ix++;
									}

									if (world.getBlockState(pos.add(k, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
										ix++;
									}

									if (world.getBlockState(pos.add(k, 1, j * 2)).isOf(Blocks.BOOKSHELF)) {
										ix++;
									}
								}
							}
						}
					}

					this.random.setSeed((long)this.seed.get());

					for (int l = 0; l < 3; l++) {
						this.enchantmentPower[l] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, l, ix, itemStack);
						this.enchantmentId[l] = -1;
						this.enchantmentLevel[l] = -1;
						if (this.enchantmentPower[l] < l + 1) {
							this.enchantmentPower[l] = 0;
						}
					}

					for (int m = 0; m < 3; m++) {
						if (this.enchantmentPower[m] > 0) {
							List<EnchantmentLevelEntry> list = this.generateEnchantments(itemStack, m, this.enchantmentPower[m]);
							if (list != null && !list.isEmpty()) {
								EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
								this.enchantmentId[m] = Registry.ENCHANTMENT.getRawId(enchantmentLevelEntry.enchantment);
								this.enchantmentLevel[m] = enchantmentLevelEntry.level;
							}
						}
					}

					this.sendContentUpdates();
				});
			} else {
				for (int i = 0; i < 3; i++) {
					this.enchantmentPower[i] = 0;
					this.enchantmentId[i] = -1;
					this.enchantmentLevel[i] = -1;
				}
			}
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		ItemStack itemStack = this.inventory.getStack(0);
		ItemStack itemStack2 = this.inventory.getStack(1);
		int i = id + 1;
		if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.getAbilities().creativeMode) {
			return false;
		} else if (this.enchantmentPower[id] <= 0
			|| itemStack.isEmpty()
			|| (player.experienceLevel < i || player.experienceLevel < this.enchantmentPower[id]) && !player.getAbilities().creativeMode) {
			return false;
		} else {
			this.context.run((world, pos) -> {
				ItemStack itemStack3 = itemStack;
				List<EnchantmentLevelEntry> list = this.generateEnchantments(itemStack, id, this.enchantmentPower[id]);
				if (!list.isEmpty()) {
					player.applyEnchantmentCosts(itemStack, i);
					boolean bl = itemStack.isOf(Items.BOOK);
					if (bl) {
						itemStack3 = new ItemStack(Items.ENCHANTED_BOOK);
						NbtCompound nbtCompound = itemStack.getTag();
						if (nbtCompound != null) {
							itemStack3.setTag(nbtCompound.copy());
						}

						this.inventory.setStack(0, itemStack3);
					}

					for (int k = 0; k < list.size(); k++) {
						EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(k);
						if (bl) {
							EnchantedBookItem.addEnchantment(itemStack3, enchantmentLevelEntry);
						} else {
							itemStack3.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
						}
					}

					if (!player.getAbilities().creativeMode) {
						itemStack2.decrement(i);
						if (itemStack2.isEmpty()) {
							this.inventory.setStack(1, ItemStack.EMPTY);
						}
					}

					player.incrementStat(Stats.ENCHANT_ITEM);
					if (player instanceof ServerPlayerEntity) {
						Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
					}

					this.inventory.markDirty();
					this.seed.set(player.getEnchantmentTableSeed());
					this.onContentChanged(this.inventory);
					world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
				}
			});
			return true;
		}
	}

	private List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level) {
		this.random.setSeed((long)(this.seed.get() + slot));
		List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, false);
		if (stack.isOf(Items.BOOK) && list.size() > 1) {
			list.remove(this.random.nextInt(list.size()));
		}

		return list;
	}

	public int getLapisCount() {
		ItemStack itemStack = this.inventory.getStack(1);
		return itemStack.isEmpty() ? 0 : itemStack.getCount();
	}

	public int getSeed() {
		return this.seed.get();
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.ENCHANTING_TABLE);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 0) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index == 1) {
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (itemStack2.isOf(Items.LAPIS_LAZULI)) {
				if (!this.insertItem(itemStack2, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (this.slots.get(0).hasStack() || !this.slots.get(0).canInsert(itemStack2)) {
					return ItemStack.EMPTY;
				}

				ItemStack itemStack3 = itemStack2.copy();
				itemStack3.setCount(1);
				itemStack2.decrement(1);
				this.slots.get(0).setStack(itemStack3);
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
