package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WolfEntity extends TameableEntity {
	private static final TrackedData<Float> field_14625 = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Boolean> field_14626 = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_14627 = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private float field_3727;
	private float field_3728;
	private boolean field_3729;
	private boolean field_3730;
	private float field_3731;
	private float field_3732;

	public WolfEntity(World world) {
		super(world);
		this.setBounds(0.6F, 0.85F);
		this.setTamed(false);
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, this.sitGoal);
		this.goals.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goals.add(4, new MeleeAttackGoal(this, 1.0, true));
		this.goals.add(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
		this.goals.add(6, new BreedGoal(this, 1.0));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
		this.goals.add(8, new WolfBegGoal(this, 8.0F));
		this.goals.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(9, new LookAroundGoal(this));
		this.attackGoals.add(1, new TrackOwnerAttackerGoal(this));
		this.attackGoals.add(2, new AttackWithOwnerGoal(this));
		this.attackGoals.add(3, new RevengeGoal(this, true));
		this.attackGoals.add(4, new FollowTargetIfTamedGoal(this, AnimalEntity.class, false, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				return entity instanceof SheepEntity || entity instanceof RabbitEntity;
			}
		}));
		this.attackGoals.add(5, new FollowTargetGoal(this, SkeletonEntity.class, false));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
		if (this.isTamed()) {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		} else {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		}

		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		if (target == null) {
			this.updateAnger(false);
		} else if (!this.isTamed()) {
			this.updateAnger(true);
		}
	}

	@Override
	protected void mobTick() {
		this.dataTracker.set(field_14625, this.getHealth());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14625, this.getHealth());
		this.dataTracker.startTracking(field_14626, false);
		this.dataTracker.startTracking(field_14627, DyeColor.RED.getSwappedId());
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Wolf");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Angry", this.isAngry());
		nbt.putByte("CollarColor", (byte)this.getCollarColor().getSwappedId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.updateAnger(nbt.getBoolean("Angry"));
		if (nbt.contains("CollarColor", 99)) {
			this.setCollarColor(DyeColor.getById(nbt.getByte("CollarColor")));
		}
	}

	@Override
	protected Sound ambientSound() {
		if (this.isAngry()) {
			return Sounds.ENTITY_WOLF_GROWL;
		} else if (this.random.nextInt(3) == 0) {
			return this.isTamed() && this.dataTracker.get(field_14625) < 10.0F ? Sounds.ENTITY_WOLF_WHINE : Sounds.ENTITY_WOLF_PANT;
		} else {
			return Sounds.ENTITY_WOLF_AMBIENT;
		}
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_WOLF_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.WOLF_ENTITIE;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient && this.field_3729 && !this.field_3730 && !this.shouldContinue() && this.onGround) {
			this.field_3730 = true;
			this.field_3731 = 0.0F;
			this.field_3732 = 0.0F;
			this.world.sendEntityStatus(this, (byte)8);
		}

		if (!this.world.isClient && this.getTarget() == null && this.isAngry()) {
			this.updateAnger(false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.field_3728 = this.field_3727;
		if (this.method_2875()) {
			this.field_3727 = this.field_3727 + (1.0F - this.field_3727) * 0.4F;
		} else {
			this.field_3727 = this.field_3727 + (0.0F - this.field_3727) * 0.4F;
		}

		if (this.tickFire()) {
			this.field_3729 = true;
			this.field_3730 = false;
			this.field_3731 = 0.0F;
			this.field_3732 = 0.0F;
		} else if ((this.field_3729 || this.field_3730) && this.field_3730) {
			if (this.field_3731 == 0.0F) {
				this.playSound(Sounds.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.field_3732 = this.field_3731;
			this.field_3731 += 0.05F;
			if (this.field_3732 >= 2.0F) {
				this.field_3729 = false;
				this.field_3730 = false;
				this.field_3732 = 0.0F;
				this.field_3731 = 0.0F;
			}

			if (this.field_3731 > 0.4F) {
				float f = (float)this.getBoundingBox().minY;
				int i = (int)(MathHelper.sin((this.field_3731 - 0.4F) * (float) Math.PI) * 7.0F);

				for (int j = 0; j < i; j++) {
					float g = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.world.addParticle(ParticleType.WATER, this.x + (double)g, (double)(f + 0.8F), this.z + (double)h, this.velocityX, this.velocityY, this.velocityZ);
				}
			}
		}
	}

	public boolean method_2881() {
		return this.field_3729;
	}

	public float method_2879(float f) {
		return 0.75F + (this.field_3732 + (this.field_3731 - this.field_3732) * f) / 2.0F * 0.25F;
	}

	public float method_2876(float f, float g) {
		float h = (this.field_3732 + (this.field_3731 - this.field_3732) * f + g) / 1.8F;
		if (h < 0.0F) {
			h = 0.0F;
		} else if (h > 1.0F) {
			h = 1.0F;
		}

		return MathHelper.sin(h * (float) Math.PI) * MathHelper.sin(h * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}

	public float method_2880(float f) {
		return (this.field_3728 + (this.field_3727 - this.field_3728) * f) * 0.15F * (float) Math.PI;
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.8F;
	}

	@Override
	public int getLookPitchSpeed() {
		return this.isSitting() ? 20 : super.getLookPitchSpeed();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getAttacker();
			if (this.sitGoal != null) {
				this.sitGoal.setEnabledWithOwner(false);
			}

			if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.damage(source, amount);
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, target);
		}

		return bl;
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
		if (tamed) {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		} else {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		}

		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	public boolean method_13079(PlayerEntity playerEntity, Hand hand, @Nullable ItemStack itemStack) {
		if (this.isTamed()) {
			if (itemStack != null) {
				if (itemStack.getItem() instanceof FoodItem) {
					FoodItem foodItem = (FoodItem)itemStack.getItem();
					if (foodItem.isMeat() && this.dataTracker.get(field_14625) < 20.0F) {
						if (!playerEntity.abilities.creativeMode) {
							itemStack.count--;
						}

						this.heal((float)foodItem.getHungerPoints(itemStack));
						return true;
					}
				} else if (itemStack.getItem() == Items.DYE) {
					DyeColor dyeColor = DyeColor.getById(itemStack.getData());
					if (dyeColor != this.getCollarColor()) {
						this.setCollarColor(dyeColor);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.count--;
						}

						return true;
					}
				}
			}

			if (this.isOwner(playerEntity) && !this.world.isClient && !this.isBreedingItem(itemStack)) {
				this.sitGoal.setEnabledWithOwner(!this.isSitting());
				this.jumping = false;
				this.navigation.stop();
				this.setTarget(null);
			}
		} else if (itemStack != null && itemStack.getItem() == Items.BONE && !this.isAngry()) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.count--;
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setTamed(true);
					this.navigation.stop();
					this.setTarget(null);
					this.sitGoal.setEnabledWithOwner(true);
					this.setHealth(20.0F);
					this.method_13092(playerEntity.getUuid());
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return true;
		}

		return super.method_13079(playerEntity, hand, itemStack);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 8) {
			this.field_3730 = true;
			this.field_3731 = 0.0F;
			this.field_3732 = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	public float method_2882() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.dataTracker.get(field_14625)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
		}
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		} else {
			return !(stack.getItem() instanceof FoodItem) ? false : ((FoodItem)stack.getItem()).isMeat();
		}
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	public boolean isAngry() {
		return (this.dataTracker.get(field_14566) & 2) != 0;
	}

	public void updateAnger(boolean angry) {
		byte b = this.dataTracker.get(field_14566);
		if (angry) {
			this.dataTracker.set(field_14566, (byte)(b | 2));
		} else {
			this.dataTracker.set(field_14566, (byte)(b & -3));
		}
	}

	public DyeColor getCollarColor() {
		return DyeColor.getById(this.dataTracker.get(field_14627) & 15);
	}

	public void setCollarColor(DyeColor color) {
		this.dataTracker.set(field_14627, color.getSwappedId());
	}

	public WolfEntity breed(PassiveEntity passiveEntity) {
		WolfEntity wolfEntity = new WolfEntity(this.world);
		UUID uUID = this.method_2719();
		if (uUID != null) {
			wolfEntity.method_13092(uUID);
			wolfEntity.setTamed(true);
		}

		return wolfEntity;
	}

	public void setBegging(boolean begging) {
		this.dataTracker.set(field_14626, begging);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(other instanceof WolfEntity)) {
			return false;
		} else {
			WolfEntity wolfEntity = (WolfEntity)other;
			if (!wolfEntity.isTamed()) {
				return false;
			} else {
				return wolfEntity.isSitting() ? false : this.isInLove() && wolfEntity.isInLove();
			}
		}
	}

	public boolean method_2875() {
		return this.dataTracker.get(field_14626);
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
			if (target instanceof WolfEntity) {
				WolfEntity wolfEntity = (WolfEntity)target;
				if (wolfEntity.isTamed() && wolfEntity.getOwner() == owner) {
					return false;
				}
			}

			return target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)
				? false
				: !(target instanceof HorseBaseEntity) || !((HorseBaseEntity)target).isTame();
		} else {
			return false;
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return !this.isAngry() && super.method_2537(playerEntity);
	}
}
