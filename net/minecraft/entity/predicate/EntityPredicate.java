package net.minecraft.entity.predicate;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;

public final class EntityPredicate {
	public static final Predicate<Entity> field_16700 = Entity::isAlive;
	public static final Predicate<LivingEntity> field_16701 = LivingEntity::isAlive;
	public static final Predicate<Entity> field_16702 = entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasMount();
	public static final Predicate<Entity> field_16703 = entity -> entity instanceof Inventory && entity.isAlive();
	public static final Predicate<Entity> field_16704 = entity -> !(entity instanceof PlayerEntity)
			|| !((PlayerEntity)entity).isSpectator() && !((PlayerEntity)entity).isCreative();
	public static final Predicate<Entity> field_16705 = entity -> !(entity instanceof PlayerEntity) || !((PlayerEntity)entity).isSpectator();

	public static Predicate<Entity> method_15603(double d, double e, double f, double g) {
		double h = g * g;
		return entity -> entity != null && entity.squaredDistanceTo(d, e, f) <= h;
	}

	public static Predicate<Entity> method_15605(Entity entity) {
		AbstractTeam abstractTeam = entity.getScoreboardTeam();
		AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.method_12129();
		return (Predicate<Entity>)(collisionRule == AbstractTeam.CollisionRule.NEVER
			? Predicates.alwaysFalse()
			: field_16705.and(
				entity2 -> {
					if (!entity2.isPushable()) {
						return false;
					} else if (!entity.world.isClient || entity2 instanceof PlayerEntity && ((PlayerEntity)entity2).isMainPlayer()) {
						AbstractTeam abstractTeam2 = entity2.getScoreboardTeam();
						AbstractTeam.CollisionRule collisionRule2 = abstractTeam2 == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam2.method_12129();
						if (collisionRule2 == AbstractTeam.CollisionRule.NEVER) {
							return false;
						} else {
							boolean bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam2);
							return (collisionRule == AbstractTeam.CollisionRule.PUSH_OWN_TEAM || collisionRule2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM) && bl
								? false
								: collisionRule != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS || bl;
						}
					} else {
						return false;
					}
				}
			));
	}

	public static Predicate<Entity> method_15608(Entity entity) {
		return entity2 -> {
			while (entity2.hasMount()) {
				entity2 = entity2.getVehicle();
				if (entity2 == entity) {
					return false;
				}
			}

			return true;
		};
	}

	public static class Armored implements Predicate<Entity> {
		private final ItemStack stack;

		public Armored(ItemStack itemStack) {
			this.stack = itemStack;
		}

		public boolean test(@Nullable Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				EquipmentSlot equipmentSlot = MobEntity.method_13083(this.stack);
				if (!livingEntity.getStack(equipmentSlot).isEmpty()) {
					return false;
				} else if (livingEntity instanceof MobEntity) {
					return ((MobEntity)livingEntity).canPickUpLoot();
				} else {
					return livingEntity instanceof ArmorStandEntity ? !((ArmorStandEntity)livingEntity).method_13207(equipmentSlot) : livingEntity instanceof PlayerEntity;
				}
			}
		}
	}
}
