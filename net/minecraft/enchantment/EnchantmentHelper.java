package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;

public class EnchantmentHelper {
	private static final Random RANDOM = new Random();
	private static final EnchantmentHelper.ProtectionModifier PROTECTION_CONSUMER = new EnchantmentHelper.ProtectionModifier();
	private static final EnchantmentHelper.DamageModifier DAMAGE_CONSUMER = new EnchantmentHelper.DamageModifier();
	private static final EnchantmentHelper.OnDamagedWith ON_DAMAGE_CONSUMER = new EnchantmentHelper.OnDamagedWith();
	private static final EnchantmentHelper.OnAttackedWith ON_ATTACK_CONSUMER = new EnchantmentHelper.OnAttackedWith();

	public static int getLevel(Enchantment enchantment, @Nullable ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			NbtList nbtList = stack.getEnchantments();
			if (nbtList == null) {
				return 0;
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					Enchantment enchantment2 = Enchantment.byIndex(nbtList.getCompound(i).getShort("id"));
					int j = nbtList.getCompound(i).getShort("lvl");
					if (enchantment2 == enchantment) {
						return j;
					}
				}

				return 0;
			}
		}
	}

	public static Map<Enchantment, Integer> get(ItemStack stack) {
		Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
		NbtList nbtList = stack.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.getEnchantmentNbt(stack) : stack.getEnchantments();
		if (nbtList != null) {
			for (int i = 0; i < nbtList.size(); i++) {
				Enchantment enchantment = Enchantment.byIndex(nbtList.getCompound(i).getShort("id"));
				int j = nbtList.getCompound(i).getShort("lvl");
				map.put(enchantment, j);
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
				nbtCompound.putShort("id", (short)Enchantment.getId(enchantment));
				nbtCompound.putShort("lvl", (short)i);
				nbtList.add(nbtCompound);
				if (stack.getItem() == Items.ENCHANTED_BOOK) {
					Items.ENCHANTED_BOOK.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, i));
				}
			}
		}

		if (nbtList.isEmpty()) {
			if (stack.hasNbt()) {
				stack.getNbt().remove("ench");
			}
		} else if (stack.getItem() != Items.ENCHANTED_BOOK) {
			stack.putSubNbt("ench", nbtList);
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, ItemStack stack) {
		if (stack != null) {
			NbtList nbtList = stack.getEnchantments();
			if (nbtList != null) {
				for (int i = 0; i < nbtList.size(); i++) {
					int j = nbtList.getCompound(i).getShort("id");
					int k = nbtList.getCompound(i).getShort("lvl");
					if (Enchantment.byIndex(j) != null) {
						consumer.accept(Enchantment.byIndex(j), k);
					}
				}
			}
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			forEachEnchantment(consumer, itemStack);
		}
	}

	public static int getProtectionAmount(Iterable<ItemStack> stacks, DamageSource source) {
		PROTECTION_CONSUMER.protection = 0;
		PROTECTION_CONSUMER.source = source;
		forEachEnchantment(PROTECTION_CONSUMER, stacks);
		return PROTECTION_CONSUMER.protection;
	}

	public static float getAttackDamage(ItemStack stack, EntityGroup group) {
		DAMAGE_CONSUMER.damage = 0.0F;
		DAMAGE_CONSUMER.target = group;
		forEachEnchantment(DAMAGE_CONSUMER, stack);
		return DAMAGE_CONSUMER.damage;
	}

	public static void onUserDamaged(LivingEntity user, Entity attacker) {
		ON_DAMAGE_CONSUMER.attackedEntity = attacker;
		ON_DAMAGE_CONSUMER.livingEntity = user;
		if (user != null) {
			forEachEnchantment(ON_DAMAGE_CONSUMER, user.getItemsEquipped());
		}

		if (attacker instanceof PlayerEntity) {
			forEachEnchantment(ON_DAMAGE_CONSUMER, user.getMainHandStack());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity attacker) {
		ON_ATTACK_CONSUMER.livingEntity = user;
		ON_ATTACK_CONSUMER.attackedEntity = attacker;
		if (user != null) {
			forEachEnchantment(ON_ATTACK_CONSUMER, user.getItemsEquipped());
		}

		if (user instanceof PlayerEntity) {
			forEachEnchantment(ON_ATTACK_CONSUMER, user.getMainHandStack());
		}
	}

	public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.method_11445(entity);
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

	public static int getLuckOfTheSea(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.LUCK_OF_THE_SEA, entity);
	}

	public static int getLure(LivingEntity entity) {
		return getEquipmentLevel(Enchantments.LURE, entity);
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

	@Nullable
	public static ItemStack chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.method_11445(entity);
		if (iterable == null) {
			return null;
		} else {
			List<ItemStack> list = Lists.newArrayList();

			for (ItemStack itemStack : iterable) {
				if (itemStack != null && getLevel(enchantment, itemStack) > 0) {
					list.add(itemStack);
				}
			}

			return list.isEmpty() ? null : (ItemStack)list.get(entity.getRandom().nextInt(list.size()));
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
		boolean bl = stack.getItem() == Items.BOOK;
		List<EnchantmentLevelEntry> list = generateEnchantments(random, stack, level, treasureAllowed);
		if (bl) {
			stack.setItem(Items.ENCHANTED_BOOK);
		}

		for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
			if (bl) {
				Items.ENCHANTED_BOOK.addEnchantment(stack, enchantmentLevelEntry);
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
			if (!pickedEntry.enchantment.differs(((EnchantmentLevelEntry)iterator.next()).enchantment)) {
				iterator.remove();
			}
		}
	}

	public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
		List<EnchantmentLevelEntry> list = Lists.newArrayList();
		Item item = stack.getItem();
		boolean bl = stack.getItem() == Items.BOOK;

		for (Enchantment enchantment : Enchantment.REGISTRY) {
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

	interface Consumer {
		void accept(Enchantment enchantment, int level);
	}

	static final class DamageModifier implements EnchantmentHelper.Consumer {
		public float damage;
		public EntityGroup target;

		private DamageModifier() {
		}

		@Override
		public void accept(Enchantment enchantment, int level) {
			this.damage = this.damage + enchantment.getDamageModifier(level, this.target);
		}
	}

	static final class OnAttackedWith implements EnchantmentHelper.Consumer {
		public LivingEntity livingEntity;
		public Entity attackedEntity;

		private OnAttackedWith() {
		}

		@Override
		public void accept(Enchantment enchantment, int level) {
			enchantment.onDamage(this.livingEntity, this.attackedEntity, level);
		}
	}

	static final class OnDamagedWith implements EnchantmentHelper.Consumer {
		public LivingEntity livingEntity;
		public Entity attackedEntity;

		private OnDamagedWith() {
		}

		@Override
		public void accept(Enchantment enchantment, int level) {
			enchantment.onDamaged(this.livingEntity, this.attackedEntity, level);
		}
	}

	static final class ProtectionModifier implements EnchantmentHelper.Consumer {
		public int protection;
		public DamageSource source;

		private ProtectionModifier() {
		}

		@Override
		public void accept(Enchantment enchantment, int level) {
			this.protection = this.protection + enchantment.getProtectionAmount(level, this.source);
		}
	}
}
