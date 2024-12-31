package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.loot.LootTables;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(World world) {
		super(EntityType.MAGMA_CUBE, world);
		this.isFireImmune = true;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return iWorld.method_16346() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean method_15653(RenderBlockView renderBlockView) {
		return renderBlockView.method_16382(this, this.getBoundingBox())
			&& renderBlockView.method_16387(this, this.getBoundingBox())
			&& !renderBlockView.method_16388(this.getBoundingBox());
	}

	@Override
	protected void method_3089(int i, boolean bl) {
		super.method_3089(i, bl);
		this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue((double)(i * 3));
	}

	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	protected ParticleEffect method_11214() {
		return class_4342.field_21399;
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
	protected void method_15645(Tag<Fluid> tag) {
		if (tag == FluidTags.LAVA) {
			this.velocityY = (double)(0.22F + (float)this.getSize() * 0.05F);
			this.velocityDirty = true;
		} else {
			super.method_15645(tag);
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected boolean isBig() {
		return this.canMoveVoluntarily();
	}

	@Override
	protected int getDamageAmount() {
		return super.getDamageAmount() + 2;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return this.method_13242() ? Sounds.ENTITY_MAGMA_CUBE_HURT_SMALL : Sounds.ENTITY_MAGMA_CUBE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return this.method_13242() ? Sounds.ENTITY_MAGMA_CUBE_DEATH_SMALL : Sounds.ENTITY_MAGMA_CUBE_DEATH;
	}

	@Override
	protected Sound method_13240() {
		return this.method_13242() ? Sounds.ENTITY_MAGMA_CUBE_SQUISH_SMALL : Sounds.ENTITY_MAGMA_CUBE_SQUISH;
	}

	@Override
	protected Sound method_13241() {
		return Sounds.ENTITY_MAGMA_CUBE_JUMP;
	}
}
