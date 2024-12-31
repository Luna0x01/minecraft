package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.sound.Sound;
import net.minecraft.world.World;

public abstract class GolemEntity extends PathAwareEntity implements EntityCategoryProvider {
	public GolemEntity(World world) {
		super(world);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		return null;
	}

	@Nullable
	@Override
	protected Sound method_13048() {
		return null;
	}

	@Nullable
	@Override
	protected Sound deathSound() {
		return null;
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
