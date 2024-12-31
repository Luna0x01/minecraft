package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantedBookItem extends Item {
	public EnchantedBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static NbtList getEnchantmentNbt(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? nbtCompound.getList("StoredEnchantments", 10) : new NbtList();
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		super.appendTooltips(stack, world, tooltip, tooltipContext);
		NbtList nbtList = getEnchantmentNbt(stack);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(Identifier.fromString(nbtCompound.getString("id")));
			if (enchantment != null) {
				tooltip.add(enchantment.method_16257(nbtCompound.getInt("lvl")));
			}
		}
	}

	public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry itemStack) {
		NbtList nbtList = getEnchantmentNbt(stack);
		boolean bl = true;
		Identifier identifier = Registry.ENCHANTMENT.getId(itemStack.enchantment);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Identifier identifier2 = Identifier.fromString(nbtCompound.getString("id"));
			if (identifier2 != null && identifier2.equals(identifier)) {
				if (nbtCompound.getInt("lvl") < itemStack.level) {
					nbtCompound.putShort("lvl", (short)itemStack.level);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.putString("id", String.valueOf(identifier));
			nbtCompound2.putShort("lvl", (short)itemStack.level);
			nbtList.add((NbtElement)nbtCompound2);
		}

		stack.getOrCreateNbt().put("StoredEnchantments", nbtList);
	}

	public static ItemStack getAsItemStack(EnchantmentLevelEntry enchantmentLevelEntry) {
		ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
		addEnchantment(itemStack, enchantmentLevelEntry);
		return itemStack;
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (group == ItemGroup.SEARCH) {
			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (enchantment.target != null) {
					for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
						stacks.add(getAsItemStack(new EnchantmentLevelEntry(enchantment, i)));
					}
				}
			}
		} else if (group.getEnchantments().length != 0) {
			for (Enchantment enchantment2 : Registry.ENCHANTMENT) {
				if (group.containsEnchantments(enchantment2.target)) {
					stacks.add(getAsItemStack(new EnchantmentLevelEntry(enchantment2, enchantment2.getMaximumLevel())));
				}
			}
		}
	}
}
