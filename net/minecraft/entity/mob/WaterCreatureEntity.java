package net.minecraft.entity.mob;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity extends MobEntity implements EntityCategoryProvider {
	public WaterCreatureEntity(World world) {
		super(world);
	}

	@Override
	public boolean method_2607() {
		return true;
	}

	@Override
	public boolean canSpawn() {
		return true;
	}

	@Override
	public boolean hasNoSpawnCollisions() {
		return this.world.hasEntityIn(this.getBoundingBox(), this);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return true;
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return 1 + this.world.random.nextInt(3);
	}

	@Override
	public void baseTick() {
		int i = this.getAir();
		super.baseTick();
		if (this.isAlive() && !this.isTouchingWater()) {
			this.setAir(--i);
			if (this.getAir() == -20) {
				this.setAir(0);
				this.damage(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setAir(300);
		}
	}

	@Override
	public boolean canFly() {
		return false;
	}
}
