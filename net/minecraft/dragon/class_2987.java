package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface class_2987 {
	boolean method_13179();

	void method_13182();

	void method_13183();

	void method_13181(EndCrystalEntity endCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity);

	void method_13184();

	void method_13185();

	float method_13186();

	float method_13188();

	class_2993<? extends class_2987> method_13189();

	@Nullable
	Vec3d method_13187();

	float method_13180(EnderDragonPart enderDragonPart, DamageSource damageSource, float f);
}
