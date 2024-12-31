package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChickenEntity extends AnimalEntity {
	private static final Ingredient field_16899 = Ingredient.ofItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEED);
	public float flapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float prevFlapProgress;
	public float flapSpeed = 1.0F;
	public int eggLayTime;
	public boolean jockey;

	public ChickenEntity(World world) {
		super(EntityType.CHICKEN, world);
		this.setBounds(0.4F, 0.7F);
		this.eggLayTime = this.random.nextInt(6000) + 6000;
		this.method_13076(LandType.WATER, 0.0F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.4));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(3, new TemptGoal(this, 1.0, false, field_16899));
		this.goals.add(4, new FollowParentGoal(this, 1.1));
		this.goals.add(5, new class_3133(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(7, new LookAroundGoal(this));
	}

	@Override
	public float getEyeHeight() {
		return this.height;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(4.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.prevFlapProgress = this.flapProgress;
		this.prevMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround ? -1 : 4) * 0.3);
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.onGround && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
		if (!this.onGround && this.velocityY < 0.0) {
			this.velocityY *= 0.6;
		}

		this.flapProgress = this.flapProgress + this.flapSpeed * 2.0F;
		if (!this.world.isClient && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
			this.playSound(Sounds.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.method_15560(Items.EGG);
			this.eggLayTime = this.random.nextInt(6000) + 6000;
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_CHICKEN_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_CHICKEN_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_CHICKEN_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.CHICKEN_ENTITIE;
	}

	public ChickenEntity breed(PassiveEntity passiveEntity) {
		return new ChickenEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return field_16899.test(stack);
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return this.hasJockey() ? 10 : super.getXpToDrop(player);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.jockey = nbt.getBoolean("IsChickenJockey");
		if (nbt.contains("EggLayTime")) {
			this.eggLayTime = nbt.getInt("EggLayTime");
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsChickenJockey", this.jockey);
		nbt.putInt("EggLayTime", this.eggLayTime);
	}

	@Override
	public boolean canImmediatelyDespawn() {
		return this.hasJockey() && !this.hasPassengers();
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		super.updatePassengerPosition(passenger);
		float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
		float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
		float h = 0.1F;
		float i = 0.0F;
		passenger.updatePosition(this.x + (double)(0.1F * f), this.y + (double)(this.height * 0.5F) + passenger.getHeightOffset() + 0.0, this.z - (double)(0.1F * g));
		if (passenger instanceof LivingEntity) {
			((LivingEntity)passenger).bodyYaw = this.bodyYaw;
		}
	}

	public boolean hasJockey() {
		return this.jockey;
	}

	public void setHasJockey(boolean hasJockey) {
		this.jockey = hasJockey;
	}
}
