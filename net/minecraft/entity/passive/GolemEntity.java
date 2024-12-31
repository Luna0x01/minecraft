package net.minecraft.entity.passive;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.world.World;

public abstract class GolemEntity extends PathAwareEntity implements EntityCategoryProvider {
	public GolemEntity(World world) {
		super(world);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected String getAmbientSound() {
		return "none";
	}

	@Override
	protected String getHurtSound() {
		return "none";
	}

	@Override
	protected String getDeathSound() {
		return "none";
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return false;
	}
}
