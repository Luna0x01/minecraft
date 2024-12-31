package net.minecraft.entity.mob;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(World world) {
		super(world);
		this.isFireImmune = true;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	public boolean canSpawn() {
		return this.world.getGlobalDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean hasNoSpawnCollisions() {
		return this.world.hasEntityIn(this.getBoundingBox(), this)
			&& this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()
			&& !this.world.containsFluid(this.getBoundingBox());
	}

	@Override
	public int getArmorProtectionValue() {
		return this.getSize() * 3;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	protected ParticleType getParticles() {
		return ParticleType.FIRE;
	}

	@Override
	protected SlimeEntity method_3091() {
		return new MagmaCubeEntity(this.world);
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.MAGMA_CREAM;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		Item item = this.getDefaultDrop();
		if (item != null && this.getSize() > 1) {
			int i = this.random.nextInt(4) - 2;
			if (lootingMultiplier > 0) {
				i += this.random.nextInt(lootingMultiplier + 1);
			}

			for (int j = 0; j < i; j++) {
				this.dropItem(item, 1);
			}
		}
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	protected int getTicksUntilNextJump() {
		return super.getTicksUntilNextJump() * 4;
	}

	@Override
	protected void updateStretch() {
		this.targetStretch *= 0.9F;
	}

	@Override
	protected void jump() {
		this.velocityY = (double)(0.42F + (float)this.getSize() * 0.1F);
		this.velocityDirty = true;
	}

	@Override
	protected void method_10979() {
		this.velocityY = (double)(0.22F + (float)this.getSize() * 0.05F);
		this.velocityDirty = true;
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected boolean isBig() {
		return true;
	}

	@Override
	protected int getDamageAmount() {
		return super.getDamageAmount() + 2;
	}

	@Override
	protected String getSound() {
		return this.getSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
	}

	@Override
	protected boolean method_3097() {
		return true;
	}
}
