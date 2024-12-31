package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class DamageUtils {
	public static float getDamageAfterProtection(float damage, float armorProtection, float f) {
		float g = 2.0F + f / 4.0F;
		float h = MathHelper.clamp(armorProtection - damage / g, armorProtection * 0.2F, 20.0F);
		return damage * (1.0F - h / 25.0F);
	}

	public static float method_12937(float f, float g) {
		float h = MathHelper.clamp(g, 0.0F, 20.0F);
		return f * (1.0F - h / 25.0F);
	}
}
