package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EnchantedBookItem extends Item {
	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return getEnchantmentNbt(stack).isEmpty() ? super.getRarity(stack) : Rarity.UNCOMMON;
	}

	public static NbtList getEnchantmentNbt(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? nbtCompound.getList("StoredEnchantments", 10) : new NbtList();
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		super.appendTooltips(stack, world, tooltip, tooltipContext);
		NbtList nbtList = getEnchantmentNbt(stack);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getShort("id");
			Enchantment enchantment = Enchantment.byIndex(j);
			if (enchantment != null) {
				tooltip.add(enchantment.getTranslatedName(nbtCompound.getShort("lvl")));
			}
		}
	}

	public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry itemStack) {
		NbtList nbtList = getEnchantmentNbt(stack);
		boolean bl = true;

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			if (Enchantment.byIndex(nbtCompound.getShort("id")) == itemStack.enchantment) {
				if (nbtCompound.getShort("lvl") < itemStack.level) {
					nbtCompound.putShort("lvl", (short)itemStack.level);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.putShort("id", (short)Enchantment.getId(itemStack.enchantment));
			nbtCompound2.putShort("lvl", (short)itemStack.level);
			nbtList.add(nbtCompound2);
		}

		if (!stack.hasNbt()) {
			stack.setNbt(new NbtCompound());
		}

		stack.getNbt().put("StoredEnchantments", nbtList);
	}

	public static ItemStack getAsItemStack(EnchantmentLevelEntry enchantmentLevelEntry) {
		ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
		addEnchantment(itemStack, enchantmentLevelEntry);
		return itemStack;
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (group == ItemGroup.SEARCH) {
			for (Enchantment enchantment : Enchantment.REGISTRY) {
				if (enchantment.target != null) {
					for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
						stacks.add(getAsItemStack(new EnchantmentLevelEntry(enchantment, i)));
					}
				}
			}
		} else if (group.getEnchantments().length != 0) {
			for (Enchantment enchantment2 : Enchantment.REGISTRY) {
				if (group.containsEnchantments(enchantment2.target)) {
					stacks.add(getAsItemStack(new EnchantmentLevelEntry(enchantment2, enchantment2.getMaximumLevel())));
				}
			}
		}
	}
}
