package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends Entity> extends Goal {
	private final Predicate<Entity> field_16838 = new Predicate<Entity>() {
		public boolean test(@Nullable Entity entity) {
			return entity.isAlive() && FleeEntityGoal.this.mob.getVisibilityCache().canSee(entity) && !FleeEntityGoal.this.mob.isTeammate(entity);
		}
	};
	protected PathAwareEntity mob;
	private final double slowSpeed;
	private final double fastSpeed;
	protected T targetEntity;
	private final float fleeDistance;
	private PathMinHeap field_14575;
	private final EntityNavigation fleeingEntityNavigation;
	private final Class<T> classToFleeFrom;
	private final Predicate<? super Entity> field_16839;
	private final Predicate<? super Entity> field_16840;

	public FleeEntityGoal(PathAwareEntity pathAwareEntity, Class<T> class_, float f, double d, double e) {
		this(pathAwareEntity, class_, entity -> true, f, d, e, EntityPredicate.field_16704);
	}

	public FleeEntityGoal(
		PathAwareEntity pathAwareEntity, Class<T> class_, Predicate<? super Entity> predicate, float f, double d, double e, Predicate<Entity> predicate2
	) {
		this.mob = pathAwareEntity;
		this.classToFleeFrom = class_;
		this.field_16839 = predicate;
		this.fleeDistance = f;
		this.slowSpeed = d;
		this.fastSpeed = e;
		this.field_16840 = predicate2;
		this.fleeingEntityNavigation = pathAwareEntity.getNavigation();
		this.setCategoryBits(1);
	}

	public FleeEntityGoal(PathAwareEntity pathAwareEntity, Class<T> class_, float f, double d, double e, Predicate<Entity> predicate) {
		this(pathAwareEntity, class_, entity -> true, f, d, e, predicate);
	}

	@Override
	public boolean canStart() {
		List<T> list = this.mob
			.world
			.method_16325(
				this.classToFleeFrom,
				this.mob.getBoundingBox().expand((double)this.fleeDistance, 3.0, (double)this.fleeDistance),
				entity -> this.field_16840.test(entity) && this.field_16838.test(entity) && this.field_16839.test(entity)
			);
		if (list.isEmpty()) {
			return false;
		} else {
			this.targetEntity = (T)list.get(0);
			Vec3d vec3d = RandomVectorGenerator.method_2801(this.mob, 16, 7, new Vec3d(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));
			if (vec3d == null) {
				return false;
			} else if (this.targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.targetEntity.squaredDistanceTo(this.mob)) {
				return false;
			} else {
				this.field_14575 = this.fleeingEntityNavigation.method_2772(vec3d.x, vec3d.y, vec3d.z);
				return this.field_14575 != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.fleeingEntityNavigation.isIdle();
	}

	@Override
	public void start() {
		this.fleeingEntityNavigation.method_13107(this.field_14575, this.slowSpeed);
	}

	@Override
	public void stop() {
		this.targetEntity = null;
	}

	@Override
	public void tick() {
		if (this.mob.squaredDistanceTo(this.targetEntity) < 49.0) {
			this.mob.getNavigation().setSpeed(this.fastSpeed);
		} else {
			this.mob.getNavigation().setSpeed(this.slowSpeed);
		}
	}
}
