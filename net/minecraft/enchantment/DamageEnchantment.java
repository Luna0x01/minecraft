package net.minecraft.enchantment;

import net.minecraft.class_3462;
import net.minecraft.entity.Entity;
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
	public float method_5489(int i, class_3462 arg) {
		if (this.typeIndex == 0) {
			return 1.0F + (float)Math.max(0, i - 1) * 0.5F;
		} else if (this.typeIndex == 1 && arg == class_3462.field_16819) {
			return (float)i * 2.5F;
		} else {
			return this.typeIndex == 2 && arg == class_3462.field_16820 ? (float)i * 2.5F : 0.0F;
		}
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
			if (this.typeIndex == 2 && livingEntity2.method_2647() == class_3462.field_16820) {
				int i = 20 + livingEntity.getRandom().nextInt(10 * power);
				livingEntity2.method_2654(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
			}
		}
	}
}
