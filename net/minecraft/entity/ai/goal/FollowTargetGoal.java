package net.minecraft.entity.ai.goal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
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
	protected final Predicate<? super T> field_16890;
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
		this.field_16890 = livingEntity -> {
			if (livingEntity == null) {
				return false;
			} else if (predicate != null && !predicate.test(livingEntity)) {
				return false;
			} else {
				return !EntityPredicate.field_16705.test(livingEntity) ? false : this.canTrack(livingEntity, false);
			}
		};
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			List<T> list = this.mob.world.method_16325(this.targetClass, this.method_13104(this.getFollowRange()), this.field_16890);
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
				.method_16319(
					this.mob.x,
					this.mob.y + (double)this.mob.getEyeHeight(),
					this.mob.z,
					this.getFollowRange(),
					this.getFollowRange(),
					new Function<PlayerEntity, Double>() {
						@Nullable
						public Double apply(@Nullable PlayerEntity playerEntity) {
							ItemStack itemStack = playerEntity.getStack(EquipmentSlot.HEAD);
							return (!(FollowTargetGoal.this.mob instanceof SkeletonEntity) || itemStack.getItem() != Items.SKELETON_SKULL)
									&& (!(FollowTargetGoal.this.mob instanceof ZombieEntity) || itemStack.getItem() != Items.ZOMBIE_HEAD)
									&& (!(FollowTargetGoal.this.mob instanceof CreeperEntity) || itemStack.getItem() != Items.CREEPER_HEAD)
								? 1.0
								: 0.5;
						}
					},
					this.field_16890
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
