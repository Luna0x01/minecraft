package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> targetClass;
	private final int reciprocalChance;
	protected final FollowTargetGoal.DistanceComparator field_3629;
	protected Predicate<? super T> targetPredicate;
	protected LivingEntity target;

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, boolean bl) {
		this(pathAwareEntity, class_, bl, false);
	}

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, boolean bl, boolean bl2) {
		this(pathAwareEntity, class_, 10, bl, bl2, null);
	}

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, int i, boolean bl, boolean bl2, Predicate<? super T> predicate) {
		super(pathAwareEntity, bl, bl2);
		this.targetClass = class_;
		this.reciprocalChance = i;
		this.field_3629 = new FollowTargetGoal.DistanceComparator(pathAwareEntity);
		this.setCategoryBits(1);
		this.targetPredicate = new Predicate<T>() {
			public boolean apply(T livingEntity) {
				if (predicate != null && !predicate.apply(livingEntity)) {
					return false;
				} else {
					if (livingEntity instanceof PlayerEntity) {
						double d = FollowTargetGoal.this.getFollowRange();
						if (livingEntity.isSneaking()) {
							d *= 0.8F;
						}

						if (livingEntity.isInvisible()) {
							float f = ((PlayerEntity)livingEntity).method_4575();
							if (f < 0.1F) {
								f = 0.1F;
							}

							d *= (double)(0.7F * f);
						}

						if ((double)livingEntity.distanceTo(FollowTargetGoal.this.mob) > d) {
							return false;
						}
					}

					return FollowTargetGoal.this.canTrack(livingEntity, false);
				}
			}
		};
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else {
			double d = this.getFollowRange();
			List<T> list = this.mob
				.world
				.getEntitiesInBox(this.targetClass, this.mob.getBoundingBox().expand(d, 4.0, d), Predicates.and(this.targetPredicate, EntityPredicate.EXCEPT_SPECTATOR));
			Collections.sort(list, this.field_3629);
			if (list.isEmpty()) {
				return false;
			} else {
				this.target = (LivingEntity)list.get(0);
				return true;
			}
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.target);
		super.start();
	}

	public static class DistanceComparator implements Comparator<Entity> {
		private final Entity entity;

		public DistanceComparator(Entity entity) {
			this.entity = entity;
		}

		public int compare(Entity entity, Entity entity2) {
			double d = this.entity.squaredDistanceTo(entity);
			double e = this.entity.squaredDistanceTo(entity2);
			if (d < e) {
				return -1;
			} else {
				return d > e ? 1 : 0;
			}
		}
	}
}
