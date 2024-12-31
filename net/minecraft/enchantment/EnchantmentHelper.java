package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
	private static final String ID_KEY = "id";
	private static final String LEVEL_KEY = "lvl";

	public static NbtCompound createNbt(@Nullable Identifier id, int lvl) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", String.valueOf(id));
		nbtCompound.putShort("lvl", (short)lvl);
		return nbtCompound;
	}

	public static void writeLevelToNbt(NbtCompound nbt, int lvl) {
		nbt.putShort("lvl", (short)lvl);
	}

	public static int getLevelFromNbt(NbtCompound nbt) {
		return MathHelper.clamp(nbt.getInt("lvl"), 0, 255);
	}

	@Nullable
	public static Identifier getIdFromNbt(NbtCompound nbt) {
		return Identifier.tryParse(nbt.getString("id"));
	}

	@Nullable
	public static Identifier getEnchantmentId(Enchantment enchantment) {
		return Registry.ENCHANTMENT.getId(enchantment);
	}

	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Identifier identifier = getEnchantmentId(enchantment);
			NbtList nbtList = stack.getEnchantments();

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				Identifier identifier2 = getIdFromNbt(nbtCompound);
				if (identifier2 != null && identifier2.equals(identifier)) {
					return getLevelFromNbt(nbtCompound);
				}
			}

			return 0;
		}
	}

	public static Map<Enchantment, Integer> get(ItemStack stack) {
		NbtList nbtList = stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments();
		return fromNbt(nbtList);
	}

	public static Map<Enchantment, Integer> fromNbt(NbtList list) {
		Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

		for (int i = 0; i < list.size(); i++) {
			NbtCompound nbtCompound = list.getCompound(i);
			Registry.ENCHANTMENT.getOrEmpty(getIdFromNbt(nbtCompound)).ifPresent(enchantment -> map.put(enchantment, getLevelFromNbt(nbtCompound)));
		}

		return map;
	}

	public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		NbtList nbtList = new NbtList();

		for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			Enchantment enchantment = (Enchantment)entry.getKey();
			if (enchantment != null) {
				int i = (Integer)entry.getValue();
				nbtList.add(createNbt(getEnchantmentId(enchantment), i));
				if (stack.isOf(Items.ENCHANTED_BOOK)) {
					EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
				}
			}
		}

		if (nbtList.isEmpty()) {
			stack.removeSubTag("Enchantments");
		} else if (!stack.isOf(Items.ENCHANTED_BOOK)) {
			stack.putSubTag("Enchantments", nbtList);
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, ItemStack stack) {
		if (!stack.isEmpty()) {
			NbtList nbtList = stack.getEnchantments();

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				Registry.ENCHANTMENT.getOrEmpty(getIdFromNbt(nbtCompound)).ifPresent(enchantment -> consumer.accept(enchantment, getLevelFromNbt(nbtCompound)));
			}
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			forEachEnchantment(consumer, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
		MutableInt mutableInt = new MutableInt();
		forEachEnchantment((enchantment, level) -> mutableInt.add(enchantment.getProtectionAmount(level, source)), equipment);
		return mutableInt.intValue();
	}

	public static float getAttackDamage(ItemStack stack, EntityGroup group) {
		MutableFloat mutableFloat = new MutableFloat();
		forEachEnchantment((enchantment, level) -> mutableFloat.add(enchantment.getAttackDamage(level, group)), stack);
		return mutableFloat.floatValue();
	}

	public static float getSweepingMultiplier(LivingEntity entity) {
		int i = getEquipmentLevel(Enchantments.SWEEPING, entity);
		return i > 0 ? SweepingEnchantment.getMultiplier(i) : 0.0F;
	}

	public static void onUserDamaged(LivingEntity user, Entity attacker) {
		EnchantmentHelper.Consumer consumer = (enchantment, level) -> enchantment.onUserDamaged(user, attacker, level);
		if (user != null) {
			forEachEnchantment(consumer, user.getItemsEquipped());
		}

		if (attacker instanceof PlayerEntity) {
			forEachEnchantment(consumer, user.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity target) {
		EnchantmentHelper.Consumer consumer = (enchantment, level) -> enchantment.onTargetDamaged(user, target, level);
		if (user != null) {
			forEachEnchantment(consumer, user.getItemsEquipped());
		}

		if (user instanceof PlayerEntity) {
			forEachEnchantment(consumer, user.getMainHandStack());
		}
	}

	public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.getEquipment(entity).values();
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

	public static int getLooting(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.LOOTING, entity);
	}

	public static boolean hasAquaAffinity(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
	}

	public static boolean hasFrostWalker(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
	}

	public static boolean hasSoulSpeed(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
	}

	public static boolean hasBindingCurse(ItemStack stack) {
		return getLevel(Enchantments.BINDING_CURSE, stack) > 0;
	}

	public static boolean hasVanishingCurse(ItemStack stack) {
		return getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
	}

	public static int getLoyalty(ItemStack stack) {
		return getLevel(Enchantments.LOYALTY, stack);
	}

	public static int getRiptide(ItemStack stack) {
		return getLevel(Enchantments.RIPTIDE, stack);
	}

	public static boolean hasChanneling(ItemStack stack) {
		return getLevel(Enchantments.CHANNELING, stack) > 0;
	}

	@Nullable
	public static Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
		return chooseEquipmentWith(enchantment, entity, stack -> true);
	}

	@Nullable
	public static Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
		Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
		if (map.isEmpty()) {
			return null;
		} else {
			List<Entry<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

			for (Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
				ItemStack itemStack = (ItemStack)entry.getValue();
				if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0 && condition.test(itemStack)) {
					list.add(entry);
				}
			}

			return list.isEmpty() ? null : (Entry)list.get(entity.getRandom().nextInt(list.size()));
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

	public static ItemStack enchant(Random random, ItemStack target, int level, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = generateEnchantments(random, target, level, treasureAllowed);
		boolean bl = target.isOf(Items.BOOK);
		if (bl) {
			target = new ItemStack(Items.ENCHANTED_BOOK);
		}

		for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
			if (bl) {
				EnchantedBookItem.addEnchantment(target, enchantmentLevelEntry);
			} else {
				target.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
			}
		}

		return target;
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
				Weighting.getRandom(random, list2).ifPresent(list::add);

				while (random.nextInt(50) <= level) {
					if (!list.isEmpty()) {
						removeConflicts(list2, Util.getLast(list));
					}

					if (list2.isEmpty()) {
						break;
					}

					Weighting.getRandom(random, list2).ifPresent(list::add);
					level /= 2;
				}
			}

			return list;
		}
	}

	public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
		Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();

		while (iterator.hasNext()) {
			if (!pickedEntry.enchantment.canCombine(((EnchantmentLevelEntry)iterator.next()).enchantment)) {
				iterator.remove();
			}
		}
	}

	public static boolean isCompatible(Collection<Enchantment> existing, Enchantment candidate) {
		for (Enchantment enchantment : existing) {
			if (!enchantment.canCombine(candidate)) {
				return false;
			}
		}

		return true;
	}

	public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.newArrayList();
		Item item = stack.getItem();
		boolean bl = stack.isOf(Items.BOOK);

		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if ((!enchantment.isTreasure() || treasureAllowed) && enchantment.isAvailableForRandomSelection() && (enchantment.type.isAcceptableItem(item) || bl)) {
				for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
					if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
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
		void accept(Enchantment enchantment, int level);
	}
}
