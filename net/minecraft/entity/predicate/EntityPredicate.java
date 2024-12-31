package net.minecraft.entity.predicate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;

public final class EntityPredicate {
	public static final Predicate<Entity> VALID_ENTITY = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity.isAlive();
		}
	};
	public static final Predicate<Entity> NOT_MOUNTED = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity.isAlive() && !entity.hasPassengers() && !entity.hasMount();
		}
	};
	public static final Predicate<Entity> VALID_INVENTORY = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof Inventory && entity.isAlive();
		}
	};
	public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return !(entity instanceof PlayerEntity) || !((PlayerEntity)entity).isSpectator() && !((PlayerEntity)entity).isCreative();
		}
	};
	public static final Predicate<Entity> EXCEPT_SPECTATOR = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return !(entity instanceof PlayerEntity) || !((PlayerEntity)entity).isSpectator();
		}
	};
	public static final Predicate<Entity> field_14516 = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof ShulkerEntity && entity.isAlive();
		}
	};

	public static <T extends Entity> Predicate<T> method_13024(double d, double e, double f, double g) {
		final double h = g * g;
		return new Predicate<T>() {
			public boolean apply(@Nullable T entity) {
				return entity != null && entity.squaredDistanceTo(d, e, f) <= h;
			}
		};
	}

	public static <T extends Entity> Predicate<T> method_13025(Entity entity) {
		final AbstractTeam abstractTeam = entity.getScoreboardTeam();
		final AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.method_12129();
		return collisionRule == AbstractTeam.CollisionRule.NEVER
			? Predicates.alwaysFalse()
			: Predicates.and(
				EXCEPT_SPECTATOR,
				new Predicate<Entity>() {
					public boolean apply(@Nullable Entity entity) {
						if (!entity.isPushable()) {
							return false;
						} else if (!entity.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).isMainPlayer()) {
							AbstractTeam abstractTeam = entity.getScoreboardTeam();
							AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.method_12129();
							if (collisionRule == AbstractTeam.CollisionRule.NEVER) {
								return false;
							} else {
								boolean bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam);
								return (collisionRule == AbstractTeam.CollisionRule.HIDE_FOR_OWN_TEAM || collisionRule == AbstractTeam.CollisionRule.HIDE_FOR_OWN_TEAM) && bl
									? false
									: collisionRule != AbstractTeam.CollisionRule.HIDE_FOR_OTHER_TEAMS && collisionRule != AbstractTeam.CollisionRule.HIDE_FOR_OTHER_TEAMS || bl;
							}
						} else {
							return false;
						}
					}
				}
			);
	}

	public static class Armored implements Predicate<Entity> {
		private final ItemStack stack;

		public Armored(ItemStack itemStack) {
			this.stack = itemStack;
		}

		public boolean apply(@Nullable Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (livingEntity.getStack(MobEntity.method_13083(this.stack)) != null) {
					return false;
				} else if (livingEntity instanceof MobEntity) {
					return ((MobEntity)livingEntity).canPickUpLoot();
				} else {
					return livingEntity instanceof ArmorStandEntity ? true : livingEntity instanceof PlayerEntity;
				}
			}
		}
	}
}
