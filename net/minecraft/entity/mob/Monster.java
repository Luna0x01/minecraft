package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategoryProvider;

public interface Monster extends EntityCategoryProvider {
	Predicate<Entity> field_17044 = entity -> entity instanceof Monster;
	Predicate<Entity> field_17045 = entity -> entity instanceof Monster && !entity.isInvisible();
}
