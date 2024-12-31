package net.minecraft.entity.predicate;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public final class EntityPredicate {
	public static final Predicate<Entity> VALID_ENTITY = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity.isAlive();
		}
	};
	public static final Predicate<Entity> NOT_MOUNTED = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity.isAlive() && entity.rider == null && entity.vehicle == null;
		}
	};
	public static final Predicate<Entity> VALID_INVENTORY = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity instanceof Inventory && entity.isAlive();
		}
	};
	public static final Predicate<Entity> EXCEPT_SPECTATOR = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return !(entity instanceof PlayerEntity) || !((PlayerEntity)entity).isSpectator();
		}
	};

	public static class Armored implements Predicate<Entity> {
		private final ItemStack stack;

		public Armored(ItemStack itemStack) {
			this.stack = itemStack;
		}

		public boolean apply(Entity entity) {
			if (!entity.isAlive()) {
				return false;
			} else if (!(entity instanceof LivingEntity)) {
				return false;
			} else {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (livingEntity.getMainSlot(MobEntity.getEquipableSlot(this.stack)) != null) {
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
