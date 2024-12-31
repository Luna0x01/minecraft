package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ThornsEnchantment extends Enchantment {
	public ThornsEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.TORSO);
		this.setName("thorns");
	}

	@Override
	public int getMinimumPower(int level) {
		return 10 + 20 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem ? true : super.isAcceptableItem(stack);
	}

	@Override
	public void onDamaged(LivingEntity livingEntity, Entity entity, int power) {
		Random random = livingEntity.getRandom();
		ItemStack itemStack = EnchantmentHelper.chooseEquipmentWith(Enchantment.THORNS, livingEntity);
		if (shouldDamageAttacker(power, random)) {
			if (entity != null) {
				entity.damage(DamageSource.thorns(livingEntity), (float)getDamageAmount(power, random));
				entity.playSound("damage.thorns", 0.5F, 1.0F);
			}

			if (itemStack != null) {
				itemStack.damage(3, livingEntity);
			}
		} else if (itemStack != null) {
			itemStack.damage(1, livingEntity);
		}
	}

	public static boolean shouldDamageAttacker(int level, Random random) {
		return level <= 0 ? false : random.nextFloat() < 0.15F * (float)level;
	}

	public static int getDamageAmount(int level, Random random) {
		return level > 10 ? level - 10 : 1 + random.nextInt(4);
	}
}
