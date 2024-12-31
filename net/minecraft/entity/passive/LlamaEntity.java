package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class LlamaEntity extends AbstractDonkeyEntity implements RangedAttackMob {
	private static final TrackedData<Integer> ATTR_STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CARPET_COLOR = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean spit;
	@Nullable
	private LlamaEntity following;
	@Nullable
	private LlamaEntity follower;

	public LlamaEntity(EntityType<? extends LlamaEntity> entityType, World world) {
		super(entityType, world);
	}

	public boolean isTrader() {
		return false;
	}

	private void setStrength(int i) {
		this.dataTracker.set(ATTR_STRENGTH, Math.max(1, Math.min(5, i)));
	}

	private void initializeStrength() {
		int i = this.random.nextFloat() < 0.04F ? 5 : 3;
		this.setStrength(1 + this.random.nextInt(i));
	}

	public int getStrength() {
		return this.dataTracker.get(ATTR_STRENGTH);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
		compoundTag.putInt("Strength", this.getStrength());
		if (!this.items.getInvStack(1).isEmpty()) {
			compoundTag.put("DecorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.setStrength(compoundTag.getInt("Strength"));
		super.readCustomDataFromTag(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
		if (compoundTag.contains("DecorItem", 10)) {
			this.items.setInvStack(1, ItemStack.fromTag(compoundTag.getCompound("DecorItem")));
		}

		this.updateSaddle();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goalSelector.add(2, new FormCaravanGoal(this, 2.1F));
		this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
		this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new LlamaEntity.SpitRevengeGoal(this));
		this.targetSelector.add(2, new LlamaEntity.ChaseWolvesGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ATTR_STRENGTH, 0);
		this.dataTracker.startTracking(CARPET_COLOR, -1);
		this.dataTracker.startTracking(ATTR_VARIANT, 0);
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 3);
	}

	public void setVariant(int i) {
		this.dataTracker.set(ATTR_VARIANT, i);
	}

	@Override
	protected int getInventorySize() {
		return this.hasChest() ? 2 + 3 * this.method_6702() : super.getInventorySize();
	}

	@Override
	public void updatePassengerPosition(Entity entity) {
		if (this.hasPassenger(entity)) {
			float f = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.3F;
			entity.updatePosition(
				this.getX() + (double)(0.3F * g), this.getY() + this.getMountedHeightOffset() + entity.getHeightOffset(), this.getZ() - (double)(0.3F * f)
			);
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.67;
	}

	@Override
	public boolean canBeControlledByRider() {
		return false;
	}

	@Override
	protected boolean receiveFood(PlayerEntity playerEntity, ItemStack itemStack) {
		int i = 0;
		int j = 0;
		float f = 0.0F;
		boolean bl = false;
		Item item = itemStack.getItem();
		if (item == Items.field_8861) {
			i = 10;
			j = 3;
			f = 2.0F;
		} else if (item == Blocks.field_10359.asItem()) {
			i = 90;
			j = 6;
			f = 10.0F;
			if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
				bl = true;
				this.lovePlayer(playerEntity);
			}
		}

		if (this.getHealth() < this.getMaximumHealth() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
			this.world.addParticle(ParticleTypes.field_11211, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.growUp(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
			bl = true;
			if (!this.world.isClient) {
				this.addTemper(j);
			}
		}

		if (bl && !this.isSilent()) {
			this.world
				.playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.field_14884,
					this.getSoundCategory(),
					1.0F,
					1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}

		return bl;
	}

	@Override
	protected boolean isImmobile() {
		return this.getHealth() <= 0.0F || this.isEatingGrass();
	}

	@Nullable
	@Override
	public net.minecraft.entity.EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable net.minecraft.entity.EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.initializeStrength();
		int i;
		if (entityData instanceof LlamaEntity.EntityData) {
			i = ((LlamaEntity.EntityData)entityData).variant;
		} else {
			i = this.random.nextInt(4);
			entityData = new LlamaEntity.EntityData(i);
		}

		this.setVariant(i);
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected SoundEvent getAngrySound() {
		return SoundEvents.field_14586;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14682;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15031;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15189;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14795, 0.15F, 1.0F);
	}

	@Override
	protected void playAddChestSound() {
		this.playSound(SoundEvents.field_15097, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void playAngrySound() {
		SoundEvent soundEvent = this.getAngrySound();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public int method_6702() {
		return this.getStrength();
	}

	@Override
	public boolean canEquip() {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return ItemTags.field_15542.contains(item);
	}

	@Override
	public boolean canBeSaddled() {
		return false;
	}

	@Override
	public void onInvChange(Inventory inventory) {
		DyeColor dyeColor = this.getCarpetColor();
		super.onInvChange(inventory);
		DyeColor dyeColor2 = this.getCarpetColor();
		if (this.age > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
			this.playSound(SoundEvents.field_14554, 0.5F, 1.0F);
		}
	}

	@Override
	protected void updateSaddle() {
		if (!this.world.isClient) {
			super.updateSaddle();
			this.setCarpetColor(getColorFromCarpet(this.items.getInvStack(1)));
		}
	}

	private void setCarpetColor(@Nullable DyeColor dyeColor) {
		this.dataTracker.set(CARPET_COLOR, dyeColor == null ? -1 : dyeColor.getId());
	}

	@Nullable
	private static DyeColor getColorFromCarpet(ItemStack itemStack) {
		Block block = Block.getBlockFromItem(itemStack.getItem());
		return block instanceof CarpetBlock ? ((CarpetBlock)block).getColor() : null;
	}

	@Nullable
	public DyeColor getCarpetColor() {
		int i = this.dataTracker.get(CARPET_COLOR);
		return i == -1 ? null : DyeColor.byId(i);
	}

	@Override
	public int getMaxTemper() {
		return 30;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return animalEntity != this && animalEntity instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)animalEntity).canBreed();
	}

	public LlamaEntity createChild(PassiveEntity passiveEntity) {
		LlamaEntity llamaEntity = this.createChild();
		this.setChildAttributes(passiveEntity, llamaEntity);
		LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
		int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
		if (this.random.nextFloat() < 0.03F) {
			i++;
		}

		llamaEntity.setStrength(i);
		llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
		return llamaEntity;
	}

	protected LlamaEntity createChild() {
		return EntityType.field_6074.create(this.world);
	}

	private void spitAt(LivingEntity livingEntity) {
		LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
		double d = livingEntity.getX() - this.getX();
		double e = livingEntity.getBodyY(0.3333333333333333) - llamaSpitEntity.getY();
		double f = livingEntity.getZ() - this.getZ();
		float g = MathHelper.sqrt(d * d + f * f) * 0.2F;
		llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5F, 10.0F);
		this.world
			.playSound(
				null,
				this.getX(),
				this.getY(),
				this.getZ(),
				SoundEvents.field_14789,
				this.getSoundCategory(),
				1.0F,
				1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
			);
		this.world.spawnEntity(llamaSpitEntity);
		this.spit = true;
	}

	private void setSpit(boolean bl) {
		this.spit = bl;
	}

	@Override
	public boolean handleFallDamage(float f, float g) {
		int i = this.computeFallDamage(f, g);
		if (i <= 0) {
			return false;
		} else {
			if (f >= 6.0F) {
				this.damage(DamageSource.FALL, (float)i);
				if (this.hasPassengers()) {
					for (Entity entity : this.getPassengersDeep()) {
						entity.damage(DamageSource.FALL, (float)i);
					}
				}
			}

			this.playBlockFallSound();
			return true;
		}
	}

	public void stopFollowing() {
		if (this.following != null) {
			this.following.follower = null;
		}

		this.following = null;
	}

	public void follow(LlamaEntity llamaEntity) {
		this.following = llamaEntity;
		this.following.follower = this;
	}

	public boolean hasFollower() {
		return this.follower != null;
	}

	public boolean isFollowing() {
		return this.following != null;
	}

	@Nullable
	public LlamaEntity getFollowing() {
		return this.following;
	}

	@Override
	protected double getRunFromLeashSpeed() {
		return 2.0;
	}

	@Override
	protected void walkToParent() {
		if (!this.isFollowing() && this.isBaby()) {
			super.walkToParent();
		}
	}

	@Override
	public boolean eatsGrass() {
		return false;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		this.spitAt(livingEntity);
	}

	static class ChaseWolvesGoal extends FollowTargetGoal<WolfEntity> {
		public ChaseWolvesGoal(LlamaEntity llamaEntity) {
			super(llamaEntity, WolfEntity.class, 16, false, true, livingEntity -> !((WolfEntity)livingEntity).isTamed());
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.25;
		}
	}

	static class EntityData extends PassiveEntity.EntityData {
		public final int variant;

		private EntityData(int i) {
			this.variant = i;
		}
	}

	static class SpitRevengeGoal extends RevengeGoal {
		public SpitRevengeGoal(LlamaEntity llamaEntity) {
			super(llamaEntity);
		}

		@Override
		public boolean shouldContinue() {
			if (this.mob instanceof LlamaEntity) {
				LlamaEntity llamaEntity = (LlamaEntity)this.mob;
				if (llamaEntity.spit) {
					llamaEntity.setSpit(false);
					return false;
				}
			}

			return super.shouldContinue();
		}
	}
}
