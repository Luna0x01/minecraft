package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(World world) {
		super(world);
		this.isFireImmune = true;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "LavaSlime");
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
	protected void setSize(int size) {
		super.setSize(size);
		this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue((double)(size * 3));
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

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return this.method_13242() ? LootTables.EMPTY : LootTables.MAGMA_CUBE_ENTITIE;
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
	protected Sound method_13048() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_MAGMACUBE_HURT : Sounds.ENTITY_MAGMACUBE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_MAGMACUBE_DEATH : Sounds.ENTITY_MAGMACUBE_DEATH;
	}

	@Override
	protected Sound method_13240() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_MAGMACUBE_SQUISH : Sounds.ENTITY_MAGMACUBE_SQUISH;
	}

	@Override
	protected Sound method_13241() {
		return Sounds.ENTITY_MAGMACUBE_JUMP;
	}
}
