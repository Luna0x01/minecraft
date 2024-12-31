package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

public abstract class Enchantment {
	public static final SimpleRegistry<Identifier, Enchantment> REGISTRY = new SimpleRegistry<>();
	private final EquipmentSlot[] wearableSlots;
	private final Enchantment.Rarity rarity;
	public EnchantmentTarget target;
	protected String translationKey;

	@Nullable
	public static Enchantment byIndex(int id) {
		return REGISTRY.getByRawId(id);
	}

	public static int getId(Enchantment enchantment) {
		return REGISTRY.getRawId(enchantment);
	}

	@Nullable
	public static Enchantment getByName(String name) {
		return REGISTRY.get(new Identifier(name));
	}

	protected Enchantment(Enchantment.Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) {
		this.rarity = rarity;
		this.target = enchantmentTarget;
		this.wearableSlots = equipmentSlots;
	}

	@Nullable
	public Iterable<ItemStack> method_11445(LivingEntity livingEntity) {
		List<ItemStack> list = Lists.newArrayList();

		for (EquipmentSlot equipmentSlot : this.wearableSlots) {
			ItemStack itemStack = livingEntity.getStack(equipmentSlot);
			if (itemStack != null) {
				list.add(itemStack);
			}
		}

		return list.size() > 0 ? list : null;
	}

	public Enchantment.Rarity getRarity() {
		return this.rarity;
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
		return level == 1 && this.getMaximumLevel() == 1 ? string : string + " " + CommonI18n.translate("enchantment.level." + level);
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.target.isCompatible(stack.getItem());
	}

	public void onDamage(LivingEntity livingEntity, Entity entity, int power) {
	}

	public void onDamaged(LivingEntity livingEntity, Entity entity, int power) {
	}

	public boolean isTreasure() {
		return false;
	}

	public static void register() {
		EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		REGISTRY.add(0, new Identifier("protection"), new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.ProtectionType.ALL, equipmentSlots));
		REGISTRY.add(
			1, new Identifier("fire_protection"), new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.FIRE, equipmentSlots)
		);
		REGISTRY.add(
			2, new Identifier("feather_falling"), new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.FALL, equipmentSlots)
		);
		REGISTRY.add(
			3, new Identifier("blast_protection"), new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.ProtectionType.EXPLOSION, equipmentSlots)
		);
		REGISTRY.add(
			4,
			new Identifier("projectile_protection"),
			new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.PROJECTILE, equipmentSlots)
		);
		REGISTRY.add(5, new Identifier("respiration"), new RespirationEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		REGISTRY.add(6, new Identifier("aqua_affinity"), new AquaAffinityEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		REGISTRY.add(7, new Identifier("thorns"), new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, equipmentSlots));
		REGISTRY.add(8, new Identifier("depth_strider"), new DepthStriderEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		REGISTRY.add(9, new Identifier("frost_walker"), new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.FEET));
		REGISTRY.add(16, new Identifier("sharpness"), new DamageEnchantment(Enchantment.Rarity.COMMON, 0, EquipmentSlot.MAINHAND));
		REGISTRY.add(17, new Identifier("smite"), new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, EquipmentSlot.MAINHAND));
		REGISTRY.add(18, new Identifier("bane_of_arthropods"), new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, EquipmentSlot.MAINHAND));
		REGISTRY.add(19, new Identifier("knockback"), new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
		REGISTRY.add(20, new Identifier("fire_aspect"), new FireAspectEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		REGISTRY.add(21, new Identifier("looting"), new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));
		REGISTRY.add(32, new Identifier("efficiency"), new EfficiencyEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
		REGISTRY.add(33, new Identifier("silk_touch"), new SilkTouchEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
		REGISTRY.add(34, new Identifier("unbreaking"), new UnbreakingEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
		REGISTRY.add(35, new Identifier("fortune"), new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND));
		REGISTRY.add(48, new Identifier("power"), new PowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
		REGISTRY.add(49, new Identifier("punch"), new PunchEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		REGISTRY.add(50, new Identifier("flame"), new FlameEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		REGISTRY.add(51, new Identifier("infinity"), new InfinityEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
		REGISTRY.add(61, new Identifier("luck_of_the_sea"), new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
		REGISTRY.add(62, new Identifier("lure"), new LureEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
		REGISTRY.add(70, new Identifier("mending"), new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.values()));
	}

	public static enum Rarity {
		COMMON(10),
		UNCOMMON(5),
		RARE(2),
		VERY_RARE(1);

		private final int chance;

		private Rarity(int j) {
			this.chance = j;
		}

		public int getChance() {
			return this.chance;
		}
	}
}
