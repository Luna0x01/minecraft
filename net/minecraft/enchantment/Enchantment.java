package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public abstract class Enchantment {
	private final EquipmentSlot[] slotTypes;
	private final Enchantment.Rarity rarity;
	public final EnchantmentTarget type;
	@Nullable
	protected String translationKey;

	@Nullable
	public static Enchantment byRawId(int id) {
		return Registry.ENCHANTMENT.get(id);
	}

	protected Enchantment(Enchantment.Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
		this.rarity = weight;
		this.type = type;
		this.slotTypes = slotTypes;
	}

	public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
		Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

		for (EquipmentSlot equipmentSlot : this.slotTypes) {
			ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				map.put(equipmentSlot, itemStack);
			}
		}

		return map;
	}

	public Enchantment.Rarity getRarity() {
		return this.rarity;
	}

	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return 1;
	}

	public int getMinPower(int level) {
		return 1 + level * 10;
	}

	public int getMaxPower(int level) {
		return this.getMinPower(level) + 5;
	}

	public int getProtectionAmount(int level, DamageSource source) {
		return 0;
	}

	public float getAttackDamage(int level, EntityGroup group) {
		return 0.0F;
	}

	public final boolean canCombine(Enchantment other) {
		return this.canAccept(other) && other.canAccept(this);
	}

	protected boolean canAccept(Enchantment other) {
		return this != other;
	}

	protected String getOrCreateTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public Text getName(int level) {
		MutableText mutableText = new TranslatableText(this.getTranslationKey());
		if (this.isCursed()) {
			mutableText.formatted(Formatting.RED);
		} else {
			mutableText.formatted(Formatting.GRAY);
		}

		if (level != 1 || this.getMaxLevel() != 1) {
			mutableText.append(" ").append(new TranslatableText("enchantment.level." + level));
		}

		return mutableText;
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.type.isAcceptableItem(stack.getItem());
	}

	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
	}

	public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
	}

	public boolean isTreasure() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public boolean isAvailableForEnchantedBookOffer() {
		return true;
	}

	public boolean isAvailableForRandomSelection() {
		return true;
	}

	public static enum Rarity {
		COMMON(10),
		UNCOMMON(5),
		RARE(2),
		VERY_RARE(1);

		private final int weight;

		private Rarity(int weight) {
			this.weight = weight;
		}

		public int getWeight() {
			return this.weight;
		}
	}
}
