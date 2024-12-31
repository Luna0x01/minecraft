package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import net.minecraft.util.collection.Weighting;

public class EnchantmentHelper {
	private static final Random RANDOM = new Random();
	private static final EnchantmentHelper.ProtectionModifier PROTECTION_CONSUMER = new EnchantmentHelper.ProtectionModifier();
	private static final EnchantmentHelper.DamageModifier DAMAGE_CONSUMER = new EnchantmentHelper.DamageModifier();
	private static final EnchantmentHelper.OnDamagedWith ON_DAMAGE_CONSUMER = new EnchantmentHelper.OnDamagedWith();
	private static final EnchantmentHelper.OnAttackedWith ON_ATTACK_CONSUMER = new EnchantmentHelper.OnAttackedWith();

	public static int getLevel(int id, ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			NbtList nbtList = stack.getEnchantments();
			if (nbtList == null) {
				return 0;
			} else {
				for (int i = 0; i < nbtList.size(); i++) {
					int j = nbtList.getCompound(i).getShort("id");
					int k = nbtList.getCompound(i).getShort("lvl");
					if (j == id) {
						return k;
					}
				}

				return 0;
			}
		}
	}

	public static Map<Integer, Integer> get(ItemStack stack) {
		Map<Integer, Integer> map = Maps.newLinkedHashMap();
		NbtList nbtList = stack.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.getEnchantmentNbt(stack) : stack.getEnchantments();
		if (nbtList != null) {
			for (int i = 0; i < nbtList.size(); i++) {
				int j = nbtList.getCompound(i).getShort("id");
				int k = nbtList.getCompound(i).getShort("lvl");
				map.put(j, k);
			}
		}

		return map;
	}

	public static void set(Map<Integer, Integer> enchantments, ItemStack stack) {
		NbtList nbtList = new NbtList();

		for (int i : enchantments.keySet()) {
			Enchantment enchantment = Enchantment.byRawId(i);
			if (enchantment != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putShort("id", (short)i);
				nbtCompound.putShort("lvl", (short)((Integer)enchantments.get(i)).intValue());
				nbtList.add(nbtCompound);
				if (stack.getItem() == Items.ENCHANTED_BOOK) {
					Items.ENCHANTED_BOOK.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, (Integer)enchantments.get(i)));
				}
			}
		}

		if (nbtList.size() > 0) {
			if (stack.getItem() != Items.ENCHANTED_BOOK) {
				stack.putSubNbt("ench", nbtList);
			}
		} else if (stack.hasNbt()) {
			stack.getNbt().remove("ench");
		}
	}

	public static int getLevel(int enchantmentId, ItemStack[] stacks) {
		if (stacks == null) {
			return 0;
		} else {
			int i = 0;

			for (ItemStack itemStack : stacks) {
				int l = getLevel(enchantmentId, itemStack);
				if (l > i) {
					i = l;
				}
			}

			return i;
		}
	}

	private static void forEachEnchantment(EnchantmentHelper.Consumer consumer, ItemStack stack) {
		if (stack != null) {
			NbtList nbtList = stack.getEnchantments();
			if (nbtList != null) {
				for (int i = 0; i < nbtList.size(); i++) {
					int j = nbtList.getCompound(i).getShort("id");
					int k = nbtList.getCompound(i).getShort("lvl");
					if (Enchantment.byRawId(j) != null) {
						consumer.accept(Enchantment.byRawId(j), k);
					}
				}
			}
		}
	}

	private static void method_3527(EnchantmentHelper.Consumer consumer, ItemStack[] stacks) {
		for (ItemStack itemStack : stacks) {
			forEachEnchantment(consumer, itemStack);
		}
	}

	public static int method_3524(ItemStack[] stacks, DamageSource source) {
		PROTECTION_CONSUMER.protection = 0;
		PROTECTION_CONSUMER.source = source;
		method_3527(PROTECTION_CONSUMER, stacks);
		if (PROTECTION_CONSUMER.protection > 25) {
			PROTECTION_CONSUMER.protection = 25;
		} else if (PROTECTION_CONSUMER.protection < 0) {
			PROTECTION_CONSUMER.protection = 0;
		}

		return (PROTECTION_CONSUMER.protection + 1 >> 1) + RANDOM.nextInt((PROTECTION_CONSUMER.protection >> 1) + 1);
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
			method_3527(ON_DAMAGE_CONSUMER, user.getArmorStacks());
		}

		if (attacker instanceof PlayerEntity) {
			forEachEnchantment(ON_DAMAGE_CONSUMER, user.getStackInHand());
		}
	}

	public static void onTargetDamaged(LivingEntity user, Entity attacker) {
		ON_ATTACK_CONSUMER.livingEntity = user;
		ON_ATTACK_CONSUMER.attackedEntity = attacker;
		if (user != null) {
			method_3527(ON_ATTACK_CONSUMER, user.getArmorStacks());
		}

		if (user instanceof PlayerEntity) {
			forEachEnchantment(ON_ATTACK_CONSUMER, user.getStackInHand());
		}
	}

	public static int getKnockback(LivingEntity entity) {
		return getLevel(Enchantment.KNOCKBACK.id, entity.getStackInHand());
	}

	public static int getFireAspect(LivingEntity entity) {
		return getLevel(Enchantment.FIRE_ASPECT.id, entity.getStackInHand());
	}

	public static int getRespiration(Entity entity) {
		return getLevel(Enchantment.RESPIRATION.id, entity.getArmorStacks());
	}

	public static int getDepthStrider(Entity entity) {
		return getLevel(Enchantment.DEPTH_STRIDER.id, entity.getArmorStacks());
	}

	public static int getEfficiency(LivingEntity entity) {
		return getLevel(Enchantment.EFFICIENCY.id, entity.getStackInHand());
	}

	public static boolean hasSilkTouch(LivingEntity entity) {
		return getLevel(Enchantment.SILK_TOUCH.id, entity.getStackInHand()) > 0;
	}

	public static int getFortune(LivingEntity entity) {
		return getLevel(Enchantment.FORTUNE.id, entity.getStackInHand());
	}

	public static int getLuckOfTheSea(LivingEntity entity) {
		return getLevel(Enchantment.LUCK_OF_THE_SEA.id, entity.getStackInHand());
	}

	public static int getLure(LivingEntity entity) {
		return getLevel(Enchantment.LURE.id, entity.getStackInHand());
	}

	public static int getLooting(LivingEntity entity) {
		return getLevel(Enchantment.LOOTING.id, entity.getStackInHand());
	}

	public static boolean hasAquaAffinity(LivingEntity entity) {
		return getLevel(Enchantment.AQUA_AFFINITY.id, entity.getArmorStacks()) > 0;
	}

	public static ItemStack chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
		for (ItemStack itemStack : entity.getArmorStacks()) {
			if (itemStack != null && getLevel(enchantment.id, itemStack) > 0) {
				return itemStack;
			}
		}

		return null;
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

	public static ItemStack addRandomEnchantment(Random random, ItemStack stack, int i) {
		List<EnchantmentLevelEntry> list = getEnchantmentInfoEntries(random, stack, i);
		boolean bl = stack.getItem() == Items.BOOK;
		if (bl) {
			stack.setItem(Items.ENCHANTED_BOOK);
		}

		if (list != null) {
			for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
				if (bl) {
					Items.ENCHANTED_BOOK.addEnchantment(stack, enchantmentLevelEntry);
				} else {
					stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
				}
			}
		}

		return stack;
	}

	public static List<EnchantmentLevelEntry> getEnchantmentInfoEntries(Random random, ItemStack stack, int i) {
		Item item = stack.getItem();
		int j = item.getEnchantability();
		if (j <= 0) {
			return null;
		} else {
			j /= 2;
			j = 1 + random.nextInt((j >> 1) + 1) + random.nextInt((j >> 1) + 1);
			int k = j + i;
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			int l = (int)((float)k * (1.0F + f) + 0.5F);
			if (l < 1) {
				l = 1;
			}

			List<EnchantmentLevelEntry> list = null;
			Map<Integer, EnchantmentLevelEntry> map = method_3528(l, stack);
			if (map != null && !map.isEmpty()) {
				EnchantmentLevelEntry enchantmentLevelEntry = Weighting.rand(random, map.values());
				if (enchantmentLevelEntry != null) {
					list = Lists.newArrayList();
					list.add(enchantmentLevelEntry);

					for (int m = l; random.nextInt(50) <= m; m >>= 1) {
						Iterator<Integer> iterator = map.keySet().iterator();

						while (iterator.hasNext()) {
							Integer integer = (Integer)iterator.next();
							boolean bl = true;

							for (EnchantmentLevelEntry enchantmentLevelEntry2 : list) {
								if (!enchantmentLevelEntry2.enchantment.differs(Enchantment.byRawId(integer))) {
									bl = false;
									break;
								}
							}

							if (!bl) {
								iterator.remove();
							}
						}

						if (!map.isEmpty()) {
							EnchantmentLevelEntry enchantmentLevelEntry3 = Weighting.rand(random, map.values());
							list.add(enchantmentLevelEntry3);
						}
					}
				}
			}

			return list;
		}
	}

	public static Map<Integer, EnchantmentLevelEntry> method_3528(int power, ItemStack stack) {
		Item item = stack.getItem();
		Map<Integer, EnchantmentLevelEntry> map = null;
		boolean bl = stack.getItem() == Items.BOOK;

		for (Enchantment enchantment : Enchantment.ALL_ENCHANTMENTS) {
			if (enchantment != null && (enchantment.target.isCompatible(item) || bl)) {
				for (int k = enchantment.getMinimumLevel(); k <= enchantment.getMaximumLevel(); k++) {
					if (power >= enchantment.getMinimumPower(k) && power <= enchantment.getMaximumPower(k)) {
						if (map == null) {
							map = Maps.newHashMap();
						}

						map.put(enchantment.id, new EnchantmentLevelEntry(enchantment, k));
					}
				}
			}
		}

		return map;
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
