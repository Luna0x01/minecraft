package net.minecraft.entity.mob;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class HostileEntity extends PathAwareEntity implements Monster {
	protected HostileEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	public void tickMovement() {
		this.tickHandSwing();
		float f = this.getBrightnessAtEyes();
		if (f > 0.5F) {
			this.despawnCounter += 2;
		}

		super.tickMovement();
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.world.method_16346() == Difficulty.PEACEFUL) {
			this.remove();
		}
	}

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected Sound method_12985() {
		return Sounds.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_HOSTILE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_HOSTILE_DEATH;
	}

	@Override
	protected Sound getLandSound(int height) {
		return height > 4 ? Sounds.ENTITY_HOSTILE_BIG_FALL : Sounds.ENTITY_HOSTILE_SMALL_FALL;
	}

	@Override
	public float method_15657(BlockPos blockPos, RenderBlockView renderBlockView) {
		return 0.5F - renderBlockView.method_16356(blockPos);
	}

	protected boolean method_3087() {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (this.world.method_16370(LightType.SKY, blockPos) > this.random.nextInt(32)) {
			return false;
		} else {
			int i = this.world.isThundering() ? this.world.method_16389(blockPos, 10) : this.world.method_16358(blockPos);
			return i <= this.random.nextInt(8);
		}
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return iWorld.method_16346() != Difficulty.PEACEFUL && this.method_3087() && super.method_15652(iWorld, bl);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	protected boolean shouldDropXp() {
		return true;
	}

	public boolean method_14129(PlayerEntity playerEntity) {
		return true;
	}
}
