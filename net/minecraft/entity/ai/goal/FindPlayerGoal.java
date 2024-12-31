package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindPlayerGoal extends Goal {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MobEntity mob;
	private final Predicate<Entity> targetPredicate;
	private final FollowTargetGoal.DistanceComparator field_11960;
	private LivingEntity target;

	public FindPlayerGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		if (mobEntity instanceof PathAwareEntity) {
			LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.targetPredicate = new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				if (!(entity instanceof PlayerEntity)) {
					return false;
				} else if (((PlayerEntity)entity).abilities.invulnerable) {
					return false;
				} else {
					double d = FindPlayerGoal.this.method_11022();
					if (entity.isSneaking()) {
						d *= 0.8F;
					}

					if (entity.isInvisible()) {
						float f = ((PlayerEntity)entity).method_4575();
						if (f < 0.1F) {
							f = 0.1F;
						}

						d *= (double)(0.7F * f);
					}

					return (double)entity.distanceTo(FindPlayerGoal.this.mob) > d
						? false
						: TrackTargetGoal.method_11025(FindPlayerGoal.this.mob, (LivingEntity)entity, false, true);
				}
			}
		};
		this.field_11960 = new FollowTargetGoal.DistanceComparator(mobEntity);
	}

	@Override
	public boolean canStart() {
		double d = this.method_11022();
		List<PlayerEntity> list = this.mob.world.getEntitiesInBox(PlayerEntity.class, this.mob.getBoundingBox().expand(d, 4.0, d), this.targetPredicate);
		Collections.sort(list, this.field_11960);
		if (list.isEmpty()) {
			return false;
		} else {
			this.target = (LivingEntity)list.get(0);
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.mob.getScoreboardTeam();
			AbstractTeam abstractTeam2 = livingEntity.getScoreboardTeam();
			if (abstractTeam != null && abstractTeam2 == abstractTeam) {
				return false;
			} else {
				double d = this.method_11022();
				return this.mob.squaredDistanceTo(livingEntity) > d * d
					? false
					: !(livingEntity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)livingEntity).interactionManager.isCreative();
			}
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.target);
		super.start();
	}

	@Override
	public void stop() {
		this.mob.setTarget(null);
		super.start();
	}

	protected double method_11022() {
		EntityAttributeInstance entityAttributeInstance = this.mob.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}
}
