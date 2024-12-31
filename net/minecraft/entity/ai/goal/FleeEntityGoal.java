package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends Entity> extends Goal {
	private final Predicate<Entity> inclusionSelector = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity.isAlive() && FleeEntityGoal.this.mob.getVisibilityCache().canSee(entity);
		}
	};
	protected PathAwareEntity mob;
	private double slowSpeed;
	private double fastSpeed;
	protected T targetEntity;
	private float fleeDistance;
	private Path fleePath;
	private EntityNavigation fleeingEntityNavigation;
	private Class<T> classToFleeFrom;
	private Predicate<? super T> extraInclusionSelector;

	public FleeEntityGoal(PathAwareEntity pathAwareEntity, Class<T> class_, float f, double d, double e) {
		this(pathAwareEntity, class_, Predicates.alwaysTrue(), f, d, e);
	}

	public FleeEntityGoal(PathAwareEntity pathAwareEntity, Class<T> class_, Predicate<? super T> predicate, float f, double d, double e) {
		this.mob = pathAwareEntity;
		this.classToFleeFrom = class_;
		this.extraInclusionSelector = predicate;
		this.fleeDistance = f;
		this.slowSpeed = d;
		this.fastSpeed = e;
		this.fleeingEntityNavigation = pathAwareEntity.getNavigation();
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		List<T> list = this.mob
			.world
			.getEntitiesInBox(
				this.classToFleeFrom,
				this.mob.getBoundingBox().expand((double)this.fleeDistance, 3.0, (double)this.fleeDistance),
				Predicates.and(new Predicate[]{EntityPredicate.EXCEPT_SPECTATOR, this.inclusionSelector, this.extraInclusionSelector})
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
				this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z);
				return this.fleePath == null ? false : this.fleePath.equalsEndPos(vec3d);
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.fleeingEntityNavigation.isIdle();
	}

	@Override
	public void start() {
		this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.slowSpeed);
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
