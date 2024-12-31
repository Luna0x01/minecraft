package net.minecraft.entity.ai.goal;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3039;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> targetClass;
	private final int reciprocalChance;
	protected final FollowTargetGoal.DistanceComparator field_3629;
	protected final Predicate<? super T> targetPredicate;
	protected T target;

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, boolean bl) {
		this(pathAwareEntity, class_, bl, false);
	}

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, boolean bl, boolean bl2) {
		this(pathAwareEntity, class_, 10, bl, bl2, null);
	}

	public FollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<? super T> predicate) {
		super(pathAwareEntity, bl, bl2);
		this.targetClass = class_;
		this.reciprocalChance = i;
		this.field_3629 = new FollowTargetGoal.DistanceComparator(pathAwareEntity);
		this.setCategoryBits(1);
		this.targetPredicate = new Predicate<T>() {
			public boolean apply(@Nullable T livingEntity) {
				if (livingEntity == null) {
					return false;
				} else if (predicate != null && !predicate.apply(livingEntity)) {
					return false;
				} else {
					return !EntityPredicate.EXCEPT_SPECTATOR.apply(livingEntity) ? false : FollowTargetGoal.this.canTrack(livingEntity, false);
				}
			}
		};
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			List<T> list = this.mob.world.getEntitiesInBox(this.targetClass, this.method_13104(this.getFollowRange()), this.targetPredicate);
			if (list.isEmpty()) {
				return false;
			} else {
				Collections.sort(list, this.field_3629);
				this.target = (T)list.get(0);
				return true;
			}
		} else {
			this.target = (T)this.mob
				.world
				.method_11477(
					this.mob.x,
					this.mob.y + (double)this.mob.getEyeHeight(),
					this.mob.z,
					this.getFollowRange(),
					this.getFollowRange(),
					new Function<PlayerEntity, Double>() {
						@Nullable
						public Double apply(@Nullable PlayerEntity playerEntity) {
							ItemStack itemStack = playerEntity.getStack(EquipmentSlot.HEAD);
							if (itemStack != null && itemStack.getItem() == Items.SKULL) {
								int i = itemStack.getDamage();
								boolean bl = FollowTargetGoal.this.mob instanceof SkeletonEntity
									&& ((SkeletonEntity)FollowTargetGoal.this.mob).method_13539() == class_3039.NORMAL
									&& i == 0;
								boolean bl2 = FollowTargetGoal.this.mob instanceof ZombieEntity && i == 2;
								boolean bl3 = FollowTargetGoal.this.mob instanceof CreeperEntity && i == 4;
								if (bl || bl2 || bl3) {
									return 0.5;
								}
							}

							return 1.0;
						}
					},
					this.targetPredicate
				);
			return this.target != null;
		}
	}

	protected Box method_13104(double d) {
		return this.mob.getBoundingBox().expand(d, 4.0, d);
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
