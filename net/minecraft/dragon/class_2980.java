package net.minecraft.dragon;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractArrowEntity;

public abstract class class_2980 extends class_2979 {
	public class_2980(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public boolean method_13179() {
		return true;
	}

	@Override
	public float method_13180(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
		if (damageSource.getSource() instanceof AbstractArrowEntity) {
			damageSource.getSource().setOnFireFor(1);
			return 0.0F;
		} else {
			return super.method_13180(enderDragonPart, damageSource, f);
		}
	}
}
