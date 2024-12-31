package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherSkullEntity extends ExplosiveProjectileEntity {
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(WitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public WitherSkullEntity(World world) {
		super(EntityType.WITHER_SKULL, world, 0.3125F, 0.3125F);
	}

	public WitherSkullEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.WITHER_SKULL, livingEntity, d, e, f, world, 0.3125F, 0.3125F);
	}

	public WitherSkullEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.WITHER_SKULL, d, e, f, g, h, i, world, 0.3125F, 0.3125F);
	}

	@Override
	protected float getDrag() {
		return this.isCharged() ? 0.73F : super.getDrag();
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public float method_10932(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return this.isCharged() && WitherEntity.canDestroy(blockState.getBlock()) ? Math.min(0.8F, f) : f;
	}

	@Override
	protected void onEntityHit(BlockHitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.entity != null) {
				if (this.target != null) {
					if (hitResult.entity.damage(DamageSource.mob(this.target), 8.0F)) {
						if (hitResult.entity.isAlive()) {
							this.dealDamage(this.target, hitResult.entity);
						} else {
							this.target.heal(5.0F);
						}
					}
				} else {
					hitResult.entity.damage(DamageSource.MAGIC, 5.0F);
				}

				if (hitResult.entity instanceof LivingEntity) {
					int i = 0;
					if (this.world.method_16346() == Difficulty.NORMAL) {
						i = 10;
					} else if (this.world.method_16346() == Difficulty.HARD) {
						i = 40;
					}

					if (i > 0) {
						((LivingEntity)hitResult.entity).method_2654(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1));
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
		this.dataTracker.startTracking(CHARGED, false);
	}

	public boolean isCharged() {
		return this.dataTracker.get(CHARGED);
	}

	public void setCharged(boolean charged) {
		this.dataTracker.set(CHARGED, charged);
	}

	@Override
	protected boolean isBurning() {
		return false;
	}
}
