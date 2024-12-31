package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public abstract class Enchantment {
	private final EquipmentSlot[] wearableSlots;
	private final Enchantment.Rarity rarity;
	@Nullable
	public EnchantmentTarget target;
	@Nullable
	protected String translationKey;

	@Nullable
	public static Enchantment byIndex(int id) {
		return Registry.ENCHANTMENT.getByRawId(id);
	}

	protected Enchantment(Enchantment.Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) {
		this.rarity = rarity;
		this.target = enchantmentTarget;
		this.wearableSlots = equipmentSlots;
	}

	public List<ItemStack> method_13673(LivingEntity livingEntity) {
		List<ItemStack> list = Lists.newArrayList();

		for (EquipmentSlot equipmentSlot : this.wearableSlots) {
			ItemStack itemStack = livingEntity.getStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
			}
		}

		return list;
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

	public float method_5489(int i, class_3462 arg) {
		return 0.0F;
	}

	public final boolean isDifferent(Enchantment other) {
		return this.differs(other) && other.differs(this);
	}

	protected boolean differs(Enchantment other) {
		return this != other;
	}

	protected String method_16258() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.method_16258();
	}

	public Text method_16257(int i) {
		Text text = new TranslatableText(this.getTranslationKey());
		if (this.isCursed()) {
			text.formatted(Formatting.RED);
		} else {
			text.formatted(Formatting.GRAY);
		}

		if (i != 1 || this.getMaximumLevel() != 1) {
			text.append(" ").append(new TranslatableText("enchantment.level." + i));
		}

		return text;
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

	public boolean isCursed() {
		return false;
	}

	public static void register() {
		EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		register("protection", new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.ProtectionType.ALL, equipmentSlots));
		register("fire_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.FIRE, equipmentSlots));
		register("feather_falling", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.FALL, equipmentSlots));
		register("blast_protection", new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.ProtectionType.EXPLOSION, equipmentSlots));
		register("projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.ProtectionType.PROJECTILE, equipmentSlots));
		register("respiration", new RespirationEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		register("thorns", new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, equipmentSlots));
		register("depth_strider", new DepthStriderEnchantment(Enchantment.Rarity.RARE, equipmentSlots));
		register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.FEET));
		register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.VERY_RARE, equipmentSlots));
		register("sharpness", new DamageEnchantment(Enchantment.Rarity.COMMON, 0, EquipmentSlot.MAINHAND));
		register("smite", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, EquipmentSlot.MAINHAND));
		register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, EquipmentSlot.MAINHAND));
		register("knockback", new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
		register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("looting", new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));
		register("sweeping", new SweepingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("efficiency", new EfficiencyEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
		register("silk_touch", new SilkTouchEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
		register("unbreaking", new UnbreakingEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
		register("fortune", new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND));
		register("power", new PowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
		register("punch", new PunchEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("flame", new FlameEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("infinity", new InfinityEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
		register("luck_of_the_sea", new BetterLootEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
		register("lure", new LureEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
		register("loyalty", new LoyaltyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
		register("impaling", new ImpalingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("riptide", new RiptideEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
		register("channeling", new ChannelingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
		register("mending", new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.values()));
		register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.values()));
	}

	private static void register(String identifier, Enchantment enchantment) {
		Registry.ENCHANTMENT.add(new Identifier(identifier), enchantment);
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
