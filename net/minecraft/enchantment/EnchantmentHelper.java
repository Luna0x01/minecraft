package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.class_3462;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
			NbtList nbtList = stack.getEnchantments();

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				Identifier identifier2 = Identifier.fromString(nbtCompound.getString("id"));
				if (identifier2 != null && identifier2.equals(identifier)) {
					return nbtCompound.getInt("lvl");
				}
			}

			return 0;
		}
	}

	public static Map<Enchantment, Integer> get(ItemStack stack) {
		Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
		NbtList nbtList = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments();

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(Identifier.fromString(nbtCompound.getString("id")));
			if (enchantment != null) {
				map.put(enchantment, nbtCompound.getInt("lvl"));
			}
		}

		return map;
	}

	public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		NbtList nbtList = new NbtList();

		for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (enchantment != null) {
				int i = (Integer)entry.getValue();
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
				nbtCompound.putShort("lvl", (short)i);
				nbtList.add((NbtElement)nbtCompound);
				if (stack.getItem() == Items.ENCHANTED_BOOK) {
					EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
				}
			}
		}

		if (nbtList.isEmpty()) {
			stack.removeNbt("Enchantments");
		} else if (stack.getItem() != Items.ENCHANTED_BOOK) {
			stack.addNbt("Enchantments", nbtList);
		}
	}

	private static void method_16261(EnchantmentHelper.Consumer consumer, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			NbtList nbtList = itemStack.getEnchantments();

			for (int i = 0; i < nbtList.size(); i++) {
				String string = nbtList.getCompound(i).getString("id");
				int j = nbtList.getCompound(i).getInt("lvl");
				Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(Identifier.fromString(string));
				if (enchantment != null) {
					consumer.accept(enchantment, j);
				}
			}
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			method_16261(consumer, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> stacks, DamageSource source) {
		MutableInt mutableInt = new MutableInt();
		forEachEnchantment((enchantment, i) -> mutableInt.add(enchantment.getProtectionAmount(i, source)), stacks);
		return mutableInt.intValue();
	}

	public static float method_16260(ItemStack itemStack, class_3462 arg) {
		MutableFloat mutableFloat = new MutableFloat();
		method_16261((enchantment, i) -> mutableFloat.add(enchantment.method_5489(i, arg)), itemStack);
		return mutableFloat.floatValue();
	}

	public static float getSweepingMultiplier(LivingEntity entity) {
		int i = getEquipmentLevel(Enchantments.SWEEPING, entity);
		return i > 0 ? SweepingEnchantment.getMultiplier(i) : 0.0F;
	}

	public static void onUserDamaged(LivingEntity user, Entity attacker) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onDamaged(user, attacker, i);
		if (user != null) {
			forEachEnchantment(consumer, user.getItemsEquipped());
		}

		if (attacker instanceof PlayerEntity) {
			method_16261(consumer, user.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity attacker) {
		EnchantmentHelper.Consumer consumer = (enchantment, i) -> enchantment.onDamage(user, attacker, i);
		if (user != null) {
			forEachEnchantment(consumer, user.getItemsEquipped());
		}

		if (user instanceof PlayerEntity) {
			method_16261(consumer, user.getMainHandStack());
		}
	}

	public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.method_13673(entity);
		if (iterable == null) {
			return 0;
		} else {
			int i = 0;

			for (ItemStack itemStack : iterable) {
				int j = getLevel(enchantment, itemStack);
				if (j > i) {
					i = j;
				}
			}

			return i;
		}
	}

	public static int getKnockback(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.KNOCKBACK, entity);
	}

	public static int getFireAspect(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.FIRE_ASPECT, entity);
	}

	public static int getRespiration(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.RESPIRATION, entity);
	}

	public static int getDepthStrider(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.DEPTH_STRIDER, entity);
	}

	public static int getEfficiency(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.EFFICIENCY, entity);
	}

	public static int getLuckOfTheSea(ItemStack stack) {
		return getLevel(Enchantments.LUCK_OF_THE_SEA, stack);
	}

	public static int getLure(ItemStack stack) {
		return getLevel(Enchantments.LURE, stack);
	}

	public static int getLooting(LivingEntity stack) {
		return getEquipmentLevel(Enchantments.LOOTING, stack);
	}

	public static boolean hasAquaAffinity(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
	}

	public static boolean hasFrostWalker(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
	}

	public static boolean hasBindingCurse(ItemStack stack) {
		return getLevel(Enchantments.BINDING_CURSE, stack) > 0;
	}

	public static boolean hasVanishingCurse(ItemStack stack) {
		return getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
	}

	public static int method_16266(ItemStack itemStack) {
		return getLevel(Enchantments.LOYALTY, itemStack);
	}

	public static int method_16267(ItemStack itemStack) {
		return getLevel(Enchantments.RIPTIDE, itemStack);
	}

	public static boolean method_16268(ItemStack itemStack) {
		return getLevel(Enchantments.CHANNELING, itemStack) > 0;
	}

	public static ItemStack chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
		List<ItemStack> list = enchantment.method_13673(entity);
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			List<ItemStack> list2 = Lists.newArrayList();

			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0) {
					list2.add(itemStack);
				}
			}

			return list2.isEmpty() ? ItemStack.EMPTY : (ItemStack)list2.get(entity.getRandom().nextInt(list2.size()));
		}
	}

	public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return 0;
		} else {
			if (bookshelfCount > 15) {
				bookshelfCount = 15;
			}

			int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
			if (slotIndex == 0) {
				return Math.max(j / 3, 1);
			} else {
				return slotIndex == 1 ? j * 2 / 3 + 1 : Math.max(j, bookshelfCount * 2);
			}
		}
	}

	public static ItemStack enchant(Random random, ItemStack stack, int level, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = generateEnchantments(random, stack, level, treasureAllowed);
		boolean bl = stack.getItem() == Items.BOOK;
		if (bl) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
		}

		for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
			if (bl) {
				EnchantedBookItem.addEnchantment(stack, enchantmentLevelEntry);
			} else {
				stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
			}
		}

		return stack;
	}

	public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.newArrayList();
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return list;
		} else {
			level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
			List<EnchantmentLevelEntry> list2 = getPossibleEntries(level, stack, treasureAllowed);
			if (!list2.isEmpty()) {
				list.add(Weighting.getRandom(random, list2));

				while (random.nextInt(50) <= level) {
					removeConflicts(list2, Util.getLast(list));
					if (list2.isEmpty()) {
						break;
					}

					list.add(Weighting.getRandom(random, list2));
					level /= 2;
				}
			}

			return list;
		}
	}

	public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
		Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();

		while (iterator.hasNext()) {
			if (!pickedEntry.enchantment.isDifferent(((EnchantmentLevelEntry)iterator.next()).enchantment)) {
				iterator.remove();
			}
		}
	}

	public static boolean method_16262(Collection<Enchantment> collection, Enchantment enchantment) {
		for (Enchantment enchantment2 : collection) {
			if (!enchantment2.isDifferent(enchantment)) {
				return false;
			}
		}

		return true;
	}

	public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.newArrayList();
		Item item = stack.getItem();
		boolean bl = stack.getItem() == Items.BOOK;

		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if ((!enchantment.isTreasure() || treasureAllowed) && (enchantment.target.isCompatible(item) || bl)) {
				for (int i = enchantment.getMaximumLevel(); i > enchantment.getMinimumLevel() - 1; i--) {
					if (power >= enchantment.getMinimumPower(i) && power <= enchantment.getMaximumPower(i)) {
						list.add(new EnchantmentLevelEntry(enchantment, i));
						break;
					}
				}
			}
		}

		return list;
	}

	@FunctionalInterface
	interface Consumer {
		void accept(Enchantment enchantment, int i);
	}
}
