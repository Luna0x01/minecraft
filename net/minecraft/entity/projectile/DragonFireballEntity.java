package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.class_4342;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DragonFireballEntity extends ExplosiveProjectileEntity {
	public DragonFireballEntity(World world) {
		super(EntityType.DRAGON_FIREBALL, world, 1.0F, 1.0F);
	}

	public DragonFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.DRAGON_FIREBALL, d, e, f, g, h, i, world, 1.0F, 1.0F);
	}

	public DragonFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.DRAGON_FIREBALL, livingEntity, d, e, f, world, 1.0F, 1.0F);
	}

	@Override
	protected void onEntityHit(BlockHitResult hitResult) {
		if (hitResult.entity == null || !hitResult.entity.isPartOf(this.target)) {
			if (!this.world.isClient) {
				List<LivingEntity> list = this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0));
				AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
				areaEffectCloudEntity.method_12954(this.target);
				areaEffectCloudEntity.method_12952(class_4342.field_21384);
				areaEffectCloudEntity.setRadius(3.0F);
				areaEffectCloudEntity.setDuration(600);
				areaEffectCloudEntity.method_12958((7.0F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
				areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));
				if (!list.isEmpty()) {
					for (LivingEntity livingEntity : list) {
						double d = this.squaredDistanceTo(livingEntity);
						if (d < 16.0) {
							areaEffectCloudEntity.updatePosition(livingEntity.x, livingEntity.y, livingEntity.z);
							break;
						}
					}
				}

				this.world.syncGlobalEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
				this.world.method_3686(areaEffectCloudEntity);
				this.remove();
			}
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
	protected ParticleEffect method_13283() {
		return class_4342.field_21384;
	}

	@Override
	protected boolean isBurning() {
		return false;
	}
}
