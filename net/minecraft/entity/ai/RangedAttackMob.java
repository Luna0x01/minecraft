package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;

public interface RangedAttackMob {
	void rangedAttack(LivingEntity target, float pullProgress);

	void method_14057(boolean bl);
}
