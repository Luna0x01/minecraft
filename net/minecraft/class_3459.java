package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.math.MathHelper;

public final class class_3459 {
	public static String method_15553(StatusEffectInstance statusEffectInstance, float f) {
		if (statusEffectInstance.isPermanent()) {
			return "**:**";
		} else {
			int i = MathHelper.floor((float)statusEffectInstance.getDuration() * f);
			return ChatUtil.ticksToString(i);
		}
	}

	public static boolean method_15554(LivingEntity livingEntity) {
		return livingEntity.hasStatusEffect(StatusEffects.HASTE) || livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}

	public static int method_15555(LivingEntity livingEntity) {
		int i = 0;
		int j = 0;
		if (livingEntity.hasStatusEffect(StatusEffects.HASTE)) {
			i = livingEntity.getEffectInstance(StatusEffects.HASTE).getAmplifier();
		}

		if (livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
			j = livingEntity.getEffectInstance(StatusEffects.CONDUIT_POWER).getAmplifier();
		}

		return Math.max(i, j);
	}

	public static boolean method_15556(LivingEntity livingEntity) {
		return livingEntity.hasStatusEffect(StatusEffects.WATER_BREATHING) || livingEntity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
	}
}
