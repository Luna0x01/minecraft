package net.minecraft.entity.projectile;

import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class FireballEntity extends ExplosiveProjectileEntity {
	public int explosionPower = 1;

	public FireballEntity(World world) {
		super(world);
	}

	public FireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	public FireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(world, livingEntity, d, e, f);
	}

	@Override
	protected void onEntityHit(BlockHitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.entity != null) {
				hitResult.entity.damage(DamageSource.fire(this, this.target), 6.0F);
				this.dealDamage(this.target, hitResult.entity);
			}

			boolean bl = this.world.getGameRules().getBoolean("mobGriefing");
			this.world.createExplosion(null, this.x, this.y, this.z, (float)this.explosionPower, bl, bl);
			this.remove();
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		ExplosiveProjectileEntity.registerDataFixes(dataFixer, "Fireball");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("ExplosionPower", this.explosionPower);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("ExplosionPower", 99)) {
			this.explosionPower = nbt.getInt("ExplosionPower");
		}
	}
}
