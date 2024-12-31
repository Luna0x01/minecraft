package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.Sound;
import net.minecraft.world.World;

public abstract class GolemEntity extends PathAwareEntity implements EntityCategoryProvider {
	protected GolemEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
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
	protected Sound getHurtSound(DamageSource damageSource) {
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
	public boolean canImmediatelyDespawn() {
		return false;
	}
}
