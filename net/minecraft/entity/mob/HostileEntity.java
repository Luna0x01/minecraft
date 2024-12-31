package net.minecraft.entity.mob;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
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
		if (!this.world.isClient && this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
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
	public boolean tryAttack(Entity target) {
		float f = (float)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
		int i = 0;
		if (target instanceof LivingEntity) {
			f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
			i += EnchantmentHelper.getKnockback(this);
		}

		boolean bl = target.damage(DamageSource.mob(this), f);
		if (bl) {
			if (i > 0 && target instanceof LivingEntity) {
				((LivingEntity)target)
					.method_6109(
						this, (float)i * 0.5F, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
					);
				this.velocityX *= 0.6;
				this.velocityZ *= 0.6;
			}

			int j = EnchantmentHelper.getFireAspect(this);
			if (j > 0) {
				target.setOnFireFor(j * 4);
			}

			if (target instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)target;
				ItemStack itemStack = this.getMainHandStack();
				ItemStack itemStack2 = playerEntity.method_13061() ? playerEntity.method_13064() : ItemStack.EMPTY;
				if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.getItem() == Items.SHIELD) {
					float g = 0.25F + (float)EnchantmentHelper.getEfficiency(this) * 0.05F;
					if (this.random.nextFloat() < g) {
						playerEntity.getItemCooldownManager().method_11384(Items.SHIELD, 100);
						this.world.sendEntityStatus(playerEntity, (byte)30);
					}
				}
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

	public boolean method_14129(PlayerEntity playerEntity) {
		return true;
	}
}
