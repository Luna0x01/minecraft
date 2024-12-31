package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Identifier;

public abstract class Enchantment {
	private static final Enchantment[] ENCHANTMENTS = new Enchantment[256];
	public static final Enchantment[] ALL_ENCHANTMENTS;
	private static final Map<Identifier, Enchantment> ENCHANTMENT_MAP = Maps.newHashMap();
	public static final Enchantment PROTECTION = new ProtectionEnchantment(0, new Identifier("protection"), 10, 0);
	public static final Enchantment FIRE_PROTECTION = new ProtectionEnchantment(1, new Identifier("fire_protection"), 5, 1);
	public static final Enchantment FEATHER_FALLING = new ProtectionEnchantment(2, new Identifier("feather_falling"), 5, 2);
	public static final Enchantment BLAST_PROTECTION = new ProtectionEnchantment(3, new Identifier("blast_protection"), 2, 3);
	public static final Enchantment PROJECTILE_PROTECTION = new ProtectionEnchantment(4, new Identifier("projectile_protection"), 5, 4);
	public static final Enchantment RESPIRATION = new RespirationEnchantment(5, new Identifier("respiration"), 2);
	public static final Enchantment AQUA_AFFINITY = new AquaAffinityEnchantment(6, new Identifier("aqua_affinity"), 2);
	public static final Enchantment THORNS = new ThornsEnchantment(7, new Identifier("thorns"), 1);
	public static final Enchantment DEPTH_STRIDER = new DepthStriderEnchantment(8, new Identifier("depth_strider"), 2);
	public static final Enchantment SHARPNESS = new DamageEnchantment(16, new Identifier("sharpness"), 10, 0);
	public static final Enchantment SMITE = new DamageEnchantment(17, new Identifier("smite"), 5, 1);
	public static final Enchantment BANE_OF_ARTHROPODS = new DamageEnchantment(18, new Identifier("bane_of_arthropods"), 5, 2);
	public static final Enchantment KNOCKBACK = new KnockbackEnchantment(19, new Identifier("knockback"), 5);
	public static final Enchantment FIRE_ASPECT = new FireAspectEnchantment(20, new Identifier("fire_aspect"), 2);
	public static final Enchantment LOOTING = new BetterLootEnchantment(21, new Identifier("looting"), 2, EnchantmentTarget.WEAPON);
	public static final Enchantment EFFICIENCY = new EfficiencyEnchantment(32, new Identifier("efficiency"), 10);
	public static final Enchantment SILK_TOUCH = new SilkTouchEnchantment(33, new Identifier("silk_touch"), 1);
	public static final Enchantment UNBREAKING = new UnbreakingEnchantment(34, new Identifier("unbreaking"), 5);
	public static final Enchantment FORTUNE = new BetterLootEnchantment(35, new Identifier("fortune"), 2, EnchantmentTarget.DIGGER);
	public static final Enchantment POWER = new PowerEnchantment(48, new Identifier("power"), 10);
	public static final Enchantment PUNCH = new PunchEnchantment(49, new Identifier("punch"), 2);
	public static final Enchantment FLAME = new FlameEnchantment(50, new Identifier("flame"), 2);
	public static final Enchantment INFINITY = new InfinityEnchantment(51, new Identifier("infinity"), 1);
	public static final Enchantment LUCK_OF_THE_SEA = new BetterLootEnchantment(61, new Identifier("luck_of_the_sea"), 2, EnchantmentTarget.FISHING_ROD);
	public static final Enchantment LURE = new LureEnchantment(62, new Identifier("lure"), 2, EnchantmentTarget.FISHING_ROD);
	public final int id;
	private final int enchantmentType;
	public EnchantmentTarget target;
	protected String translationKey;

	public static Enchantment byRawId(int id) {
		return id >= 0 && id < ENCHANTMENTS.length ? ENCHANTMENTS[id] : null;
	}

	protected Enchantment(int i, Identifier identifier, int j, EnchantmentTarget enchantmentTarget) {
		this.id = i;
		this.enchantmentType = j;
		this.target = enchantmentTarget;
		if (ENCHANTMENTS[i] != null) {
			throw new IllegalArgumentException("Duplicate enchantment id!");
		} else {
			ENCHANTMENTS[i] = this;
			ENCHANTMENT_MAP.put(identifier, this);
		}
	}

	public static Enchantment getByName(String name) {
		return (Enchantment)ENCHANTMENT_MAP.get(new Identifier(name));
	}

	public static Set<Identifier> getSet() {
		return ENCHANTMENT_MAP.keySet();
	}

	public int getEnchantmentType() {
		return this.enchantmentType;
	}

	public int getMinimumLevel() {
		return 1;
	}

	public int getMaximumLevel() {
		return 1;
	}

	public int getMinimumPower(int level) {
		return 1 + level * 10;
	}

	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 5;
	}

	public int getProtectionAmount(int level, DamageSource source) {
		return 0;
	}

	public float getDamageModifier(int index, EntityGroup target) {
		return 0.0F;
	}

	public boolean differs(Enchantment other) {
		return this != other;
	}

	public Enchantment setName(String translationKey) {
		this.translationKey = translationKey;
		return this;
	}

	public String getTranslationKey() {
		return "enchantment." + this.translationKey;
	}

	public String getTranslatedName(int level) {
		String string = CommonI18n.translate(this.getTranslationKey());
		return string + " " + CommonI18n.translate("enchantment.level." + level);
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.target.isCompatible(stack.getItem());
	}

	public void onDamage(LivingEntity livingEntity, Entity entity, int power) {
	}

	public void onDamaged(LivingEntity livingEntity, Entity entity, int power) {
	}

	static {
		List<Enchantment> list = Lists.newArrayList();

		for (Enchantment enchantment : ENCHANTMENTS) {
			if (enchantment != null) {
				list.add(enchantment);
			}
		}

		ALL_ENCHANTMENTS = (Enchantment[])list.toArray(new Enchantment[list.size()]);
	}
}
