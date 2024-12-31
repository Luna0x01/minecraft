package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindNearestEntityGoal extends Goal {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MobEntity mob;
	private final Predicate<LivingEntity> targetPredicate;
	private final FollowTargetGoal.DistanceComparator field_11953;
	private LivingEntity target;
	private final Class<? extends LivingEntity> targetClass;

	public FindNearestEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_) {
		this.mob = mobEntity;
		this.targetClass = class_;
		if (mobEntity instanceof PathAwareEntity) {
			LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.targetPredicate = new Predicate<LivingEntity>() {
			public boolean apply(@Nullable LivingEntity livingEntity) {
				double d = FindNearestEntityGoal.this.method_11019();
				if (livingEntity.isSneaking()) {
					d *= 0.8F;
				}

				if (livingEntity.isInvisible()) {
					return false;
				} else {
					return (double)livingEntity.distanceTo(FindNearestEntityGoal.this.mob) > d
						? false
						: TrackTargetGoal.method_11025(FindNearestEntityGoal.this.mob, livingEntity, false, true);
				}
			}
		};
		this.field_11953 = new FollowTargetGoal.DistanceComparator(mobEntity);
	}

	@Override
	public boolean canStart() {
		double d = this.method_11019();
		List<LivingEntity> list = this.mob.world.getEntitiesInBox(this.targetClass, this.mob.getBoundingBox().expand(d, 4.0, d), this.targetPredicate);
		Collections.sort(list, this.field_11953);
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
		} else {
			double d = this.method_11019();
			return this.mob.squaredDistanceTo(livingEntity) > d * d
				? false
				: !(livingEntity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)livingEntity).interactionManager.isCreative();
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

	protected double method_11019() {
		EntityAttributeInstance entityAttributeInstance = this.mob.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}
}
