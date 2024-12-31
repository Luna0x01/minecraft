package net.minecraft.entity.mob;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategoryProvider;

public interface Monster extends EntityCategoryProvider {
	Predicate<Entity> MONSTER_PREDICATE = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity instanceof Monster;
		}
	};
	Predicate<Entity> VISIBLE_MONSTER_PREDICATE = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity instanceof Monster && !entity.isInvisible();
		}
	};
}
