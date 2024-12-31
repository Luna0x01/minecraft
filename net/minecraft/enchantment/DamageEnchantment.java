package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class DamageEnchantment extends Enchantment {
	private static final String[] TYPE_NAMES = new String[]{"all", "undead", "arthropods"};
	private static final int[] field_4449 = new int[]{1, 5, 5};
	private static final int[] field_4450 = new int[]{11, 8, 8};
	private static final int[] field_4451 = new int[]{20, 20, 20};
	public final int typeIndex;

	public DamageEnchantment(Enchantment.Rarity rarity, int i, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.WEAPON, equipmentSlots);
		this.typeIndex = i;
	}

	@Override
	public int getMinimumPower(int level) {
		return field_4449[this.typeIndex] + (level - 1) * field_4450[this.typeIndex];
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + field_4451[this.typeIndex];
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public float getDamageModifier(int index, EntityGroup target) {
		if (this.typeIndex == 0) {
			return 1.0F + (float)Math.max(0, index - 1) * 0.5F;
		} else if (this.typeIndex == 1 && target == EntityGroup.UNDEAD) {
			return (float)index * 2.5F;
		} else {
			return this.typeIndex == 2 && target == EntityGroup.ARTHROPOD ? (float)index * 2.5F : 0.0F;
		}
	}

	@Override
	public String getTranslationKey() {
		return "enchantment.damage." + TYPE_NAMES[this.typeIndex];
	}

	@Override
	public boolean differs(Enchantment other) {
		return !(other instanceof DamageEnchantment);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof AxeItem ? true : super.isAcceptableItem(stack);
	}

	@Override
	public void onDamage(LivingEntity livingEntity, Entity entity, int power) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)entity;
			if (this.typeIndex == 2 && livingEntity2.getGroup() == EntityGroup.ARTHROPOD) {
				int i = 20 + livingEntity.getRandom().nextInt(10 * power);
				livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
			}
		}
	}
}
