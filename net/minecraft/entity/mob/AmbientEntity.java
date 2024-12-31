package net.minecraft.entity.mob;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity implements EntityCategoryProvider {
	public AmbientEntity(World world) {
		super(world);
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}
}
