package net.minecraft.entity.mob;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity implements EntityCategoryProvider {
	protected AmbientEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}
}
