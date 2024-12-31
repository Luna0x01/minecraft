package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherSkullEntity extends ExplosiveProjectileEntity {
	public WitherSkullEntity(World world) {
		super(world);
		this.setBounds(0.3125F, 0.3125F);
	}

	public WitherSkullEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(world, livingEntity, d, e, f);
		this.setBounds(0.3125F, 0.3125F);
	}

	@Override
	protected float getDrag() {
		return this.isCharged() ? 0.73F : super.getDrag();
	}

	public WitherSkullEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.setBounds(0.3125F, 0.3125F);
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public float getBlastResistance(Explosion explosion, World world, BlockPos pos, BlockState state) {
		float f = super.getBlastResistance(explosion, world, pos, state);
		Block block = state.getBlock();
		if (this.isCharged() && WitherEntity.canDestroy(block)) {
			f = Math.min(0.8F, f);
		}

		return f;
	}

	@Override
	protected void onEntityHit(BlockHitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.entity != null) {
				if (this.target != null) {
					if (hitResult.entity.damage(DamageSource.mob(this.target), 8.0F)) {
						if (!hitResult.entity.isAlive()) {
							this.target.heal(5.0F);
						} else {
							this.dealDamage(this.target, hitResult.entity);
						}
					}
				} else {
					hitResult.entity.damage(DamageSource.MAGIC, 5.0F);
				}

				if (hitResult.entity instanceof LivingEntity) {
					int i = 0;
					if (this.world.getGlobalDifficulty() == Difficulty.NORMAL) {
						i = 10;
					} else if (this.world.getGlobalDifficulty() == Difficulty.HARD) {
						i = 40;
					}

					if (i > 0) {
						((LivingEntity)hitResult.entity).addStatusEffect(new StatusEffectInstance(StatusEffect.WITHER.id, 20 * i, 1));
					}
				}
			}

			this.world.createExplosion(this, this.x, this.y, this.z, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.track(10, (byte)0);
	}

	public boolean isCharged() {
		return this.dataTracker.getByte(10) == 1;
	}

	public void setCharged(boolean charged) {
		this.dataTracker.setProperty(10, Byte.valueOf((byte)(charged ? 1 : 0)));
	}
}
