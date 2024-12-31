package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.class_3146;
import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
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
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
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
		super(EntityType.WOLF, world);
		this.setBounds(0.6F, 0.85F);
		this.setTamed(false);
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, this.sitGoal);
		this.goals.add(3, new WolfEntity.class_3134(this, LlamaEntity.class, 24.0F, 1.5, 1.5));
		this.goals.add(4, new PounceAtTargetGoal(this, 0.4F));
		this.goals.add(5, new MeleeAttackGoal(this, 1.0, true));
		this.goals.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
		this.goals.add(7, new BreedGoal(this, 1.0));
		this.goals.add(8, new class_3133(this, 1.0));
		this.goals.add(9, new WolfBegGoal(this, 8.0F));
		this.goals.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(10, new LookAroundGoal(this));
		this.attackGoals.add(1, new TrackOwnerAttackerGoal(this));
		this.attackGoals.add(2, new AttackWithOwnerGoal(this));
		this.attackGoals.add(3, new RevengeGoal(this, true));
		this.attackGoals
			.add(
				4,
				new FollowTargetIfTamedGoal(this, AnimalEntity.class, false, animalEntity -> animalEntity instanceof SheepEntity || animalEntity instanceof RabbitEntity)
			);
		this.attackGoals.add(4, new FollowTargetIfTamedGoal(this, TurtleEntity.class, false, TurtleEntity.field_16957));
		this.attackGoals.add(5, new FollowTargetGoal(this, class_3146.class, false));
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
		this.dataTracker.startTracking(field_14627, DyeColor.RED.getId());
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Angry", this.isAngry());
		nbt.putByte("CollarColor", (byte)this.getCollarColor().getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.updateAnger(nbt.getBoolean("Angry"));
		if (nbt.contains("CollarColor", 99)) {
			this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
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
	protected Sound getHurtSound(DamageSource damageSource) {
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

		if (this.method_15574()) {
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
					this.world
						.method_16343(class_4342.field_21368, this.x + (double)g, (double)(f + 0.8F), this.z + (double)h, this.velocityX, this.velocityY, this.velocityZ);
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
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.isTamed()) {
			if (!itemStack.isEmpty()) {
				if (item instanceof FoodItem) {
					FoodItem foodItem = (FoodItem)item;
					if (foodItem.isMeat() && this.dataTracker.get(field_14625) < 20.0F) {
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
						}

						this.heal((float)foodItem.getHungerPoints(itemStack));
						return true;
					}
				} else if (item instanceof DyeItem) {
					DyeColor dyeColor = ((DyeItem)item).method_16047();
					if (dyeColor != this.getCollarColor()) {
						this.setCollarColor(dyeColor);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
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
		} else if (item == Items.BONE && !this.isAngry()) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.method_15070(playerEntity);
					this.navigation.stop();
					this.setTarget(null);
					this.sitGoal.setEnabledWithOwner(true);
					this.setHealth(20.0F);
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return true;
		}

		return super.interactMob(playerEntity, hand);
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
	public boolean isBreedingItem(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof FoodItem && ((FoodItem)item).isMeat();
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
		return DyeColor.byId(this.dataTracker.get(field_14627));
	}

	public void setCollarColor(DyeColor color) {
		this.dataTracker.set(field_14627, color.getId());
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
				: !(target instanceof AbstractHorseEntity) || !((AbstractHorseEntity)target).method_13990();
		} else {
			return false;
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return !this.isAngry() && super.method_2537(playerEntity);
	}

	class class_3134<T extends Entity> extends FleeEntityGoal<T> {
		private final WolfEntity field_15487;

		public class_3134(WolfEntity wolfEntity2, Class<T> class_, float f, double d, double e) {
			super(wolfEntity2, class_, f, d, e);
			this.field_15487 = wolfEntity2;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.targetEntity instanceof LlamaEntity
				? !this.field_15487.isTamed() && this.method_13961((LlamaEntity)this.targetEntity)
				: false;
		}

		private boolean method_13961(LlamaEntity llamaEntity) {
			return llamaEntity.getStrength() >= WolfEntity.this.random.nextInt(5);
		}

		@Override
		public void start() {
			WolfEntity.this.setTarget(null);
			super.start();
		}

		@Override
		public void tick() {
			WolfEntity.this.setTarget(null);
			super.tick();
		}
	}
}
