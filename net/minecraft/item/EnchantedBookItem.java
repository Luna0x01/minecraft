package net.minecraft.item;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Rarity;

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
		return this.getEnchantmentNbt(stack).isEmpty() ? super.getRarity(stack) : Rarity.UNCOMMON;
	}

	public NbtList getEnchantmentNbt(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbt();
		return nbtCompound != null && nbtCompound.contains("StoredEnchantments", 9) ? (NbtList)nbtCompound.get("StoredEnchantments") : new NbtList();
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		super.appendTooltip(stack, player, lines, advanced);
		NbtList nbtList = this.getEnchantmentNbt(stack);
		if (nbtList != null) {
			for (int i = 0; i < nbtList.size(); i++) {
				int j = nbtList.getCompound(i).getShort("id");
				int k = nbtList.getCompound(i).getShort("lvl");
				if (Enchantment.byIndex(j) != null) {
					lines.add(Enchantment.byIndex(j).getTranslatedName(k));
				}
			}
		}
	}

	public void addEnchantment(ItemStack itemStack, EnchantmentLevelEntry info) {
		NbtList nbtList = this.getEnchantmentNbt(itemStack);
		boolean bl = true;

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			if (Enchantment.byIndex(nbtCompound.getShort("id")) == info.enchantment) {
				if (nbtCompound.getShort("lvl") < info.level) {
					nbtCompound.putShort("lvl", (short)info.level);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.putShort("id", (short)Enchantment.getId(info.enchantment));
			nbtCompound2.putShort("lvl", (short)info.level);
			nbtList.add(nbtCompound2);
		}

		if (!itemStack.hasNbt()) {
			itemStack.setNbt(new NbtCompound());
		}

		itemStack.getNbt().put("StoredEnchantments", nbtList);
	}

	public ItemStack getAsItemStack(EnchantmentLevelEntry info) {
		ItemStack itemStack = new ItemStack(this);
		this.addEnchantment(itemStack, info);
		return itemStack;
	}

	public void getEnchantments(Enchantment enchantment, List<ItemStack> list) {
		for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
			list.add(this.getAsItemStack(new EnchantmentLevelEntry(enchantment, i)));
		}
	}
}
