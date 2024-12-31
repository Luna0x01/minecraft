package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class class_2979 implements class_2987 {
	protected final EnderDragonEntity dragon;

	public class_2979(EnderDragonEntity enderDragonEntity) {
		this.dragon = enderDragonEntity;
	}

	@Override
	public boolean method_13179() {
		return false;
	}

	@Override
	public void method_13182() {
	}

	@Override
	public void method_13183() {
	}

	@Override
	public void method_13181(EndCrystalEntity endCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity) {
	}

	@Override
	public void method_13184() {
	}

	@Override
	public void method_13185() {
	}

	@Override
	public float method_13186() {
		return 0.6F;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return null;
	}

	@Override
	public float method_13180(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
		return f;
	}

	@Override
	public float method_13188() {
		float f = MathHelper.sqrt(this.dragon.velocityX * this.dragon.velocityX + this.dragon.velocityZ * this.dragon.velocityZ) + 1.0F;
		float g = Math.min(f, 40.0F);
		return 0.7F / g / f;
	}
}
