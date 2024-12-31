package net.minecraft.entity.mob;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public abstract class HostileEntity extends PathAwareEntity implements Monster {
	public HostileEntity(World world) {
		super(world);
		this.experiencePoints = 5;
	}

	@Override
	public void tickMovement() {
		this.tickHandSwing();
		float f = this.getBrightnessAtEyes(1.0F);
		if (f > 0.5F) {
			this.despawnCounter += 2;
		}

		super.tickMovement();
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
			this.remove();
		}
	}

	@Override
	protected String getSwimSound() {
		return "game.hostile.swim";
	}

	@Override
	protected String getSplashSound() {
		return "game.hostile.swim.splash";
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (super.damage(source, amount)) {
			Entity entity = source.getAttacker();
			return this.rider != entity && this.vehicle != entity ? true : true;
		} else {
			return false;
		}
	}

	@Override
	protected String getHurtSound() {
		return "game.hostile.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "game.hostile.die";
	}

	@Override
	protected String getFallSound(int distance) {
		return distance > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
	}

	@Override
	public boolean tryAttack(Entity target) {
		float f = (float)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
		int i = 0;
		if (target instanceof LivingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.getStackInHand(), ((LivingEntity)target).getGroup());
			i += EnchantmentHelper.getKnockback(this);
		}

		boolean bl = target.damage(DamageSource.mob(this), f);
		if (bl) {
			if (i > 0) {
				target.addVelocity(
					(double)(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * (float)i * 0.5F),
					0.1,
					(double)(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * (float)i * 0.5F)
				);
				this.velocityX *= 0.6;
				this.velocityZ *= 0.6;
			}

			int j = EnchantmentHelper.getFireAspect(this);
			if (j > 0) {
				target.setOnFireFor(j * 4);
			}

			this.dealDamage(this, target);
		}

		return bl;
	}

	@Override
	public float getPathfindingFavor(BlockPos pos) {
		return 0.5F - this.world.getBrightness(pos);
	}

	protected boolean method_3087() {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (this.world.getLightAtPos(LightType.SKY, blockPos) > this.random.nextInt(32)) {
			return false;
		} else {
			int i = this.world.getLightLevelWithNeighbours(blockPos);
			if (this.world.isThundering()) {
				int j = this.world.getAmbientDarkness();
				this.world.setAmbientDarkness(10);
				i = this.world.getLightLevelWithNeighbours(blockPos);
				this.world.setAmbientDarkness(j);
			}

			return i <= this.random.nextInt(8);
		}
	}

	@Override
	public boolean canSpawn() {
		return this.world.getGlobalDifficulty() != Difficulty.PEACEFUL && this.method_3087() && super.canSpawn();
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
}
