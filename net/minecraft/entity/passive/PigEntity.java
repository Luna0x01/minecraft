package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
	private static final TrackedData<Boolean> field_14615 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_15485 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient field_16918 = Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
	private boolean field_14617;
	private int field_14613;
	private int field_14614;

	public PigEntity(World world) {
		super(EntityType.PIG, world);
		this.setBounds(0.9F, 0.9F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.25));
		this.goals.add(3, new BreedGoal(this, 1.0));
		this.goals.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
		this.goals.add(4, new TemptGoal(this, 1.2, false, field_16918));
		this.goals.add(5, new FollowParentGoal(this, 1.1));
		this.goals.add(6, new class_3133(this, 1.0));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public boolean canBeControlledByRider() {
		Entity entity = this.getPrimaryPassenger();
		if (!(entity instanceof PlayerEntity)) {
			return false;
		} else {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			return playerEntity.getMainHandStack().getItem() == Items.CARROT_ON_A_STICK || playerEntity.getOffHandStack().getItem() == Items.CARROT_ON_A_STICK;
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_15485.equals(data) && this.world.isClient) {
			this.field_14617 = true;
			this.field_14613 = 0;
			this.field_14614 = this.dataTracker.get(field_15485);
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14615, false);
		this.dataTracker.startTracking(field_15485, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Saddle", this.isSaddled());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSaddled(nbt.getBoolean("Saddle"));
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_PIG_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_PIG_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_PIG_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_PIG_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		if (!super.interactMob(playerEntity, hand)) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.getItem() == Items.NAME_TAG) {
				itemStack.method_6329(playerEntity, this, hand);
				return true;
			} else if (this.isSaddled() && !this.hasPassengers()) {
				if (!this.world.isClient) {
					playerEntity.ride(this);
				}

				return true;
			} else if (itemStack.getItem() == Items.SADDLE) {
				itemStack.method_6329(playerEntity, this, hand);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (!this.world.isClient) {
			if (this.isSaddled()) {
				this.method_15560(Items.SADDLE);
			}
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.PIG_ENTITIE;
	}

	public boolean isSaddled() {
		return this.dataTracker.get(field_14615);
	}

	public void setSaddled(boolean bl) {
		if (bl) {
			this.dataTracker.set(field_14615, true);
		} else {
			this.dataTracker.set(field_14615, false);
		}
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
		if (!this.world.isClient && !this.removed) {
			ZombiePigmanEntity zombiePigmanEntity = new ZombiePigmanEntity(this.world);
			zombiePigmanEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
			zombiePigmanEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			zombiePigmanEntity.setAiDisabled(this.hasNoAi());
			if (this.hasCustomName()) {
				zombiePigmanEntity.method_15578(this.method_15541());
				zombiePigmanEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.method_3686(zombiePigmanEntity);
			this.remove();
		}
	}

	@Override
	public void method_2657(float f, float g, float h) {
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (this.hasPassengers() && this.canBeControlledByRider()) {
			this.yaw = entity.yaw;
			this.prevYaw = this.yaw;
			this.pitch = entity.pitch * 0.5F;
			this.setRotation(this.yaw, this.pitch);
			this.bodyYaw = this.yaw;
			this.headYaw = this.yaw;
			this.stepHeight = 1.0F;
			this.flyingSpeed = this.getMovementSpeed() * 0.1F;
			if (this.field_14617 && this.field_14613++ > this.field_14614) {
				this.field_14617 = false;
			}

			if (this.method_13003()) {
				float i = (float)this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue() * 0.225F;
				if (this.field_14617) {
					i += i * 1.15F * MathHelper.sin((float)this.field_14613 / (float)this.field_14614 * (float) Math.PI);
				}

				this.setMovementSpeed(i);
				super.method_2657(0.0F, 0.0F, 1.0F);
			} else {
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			this.field_6748 = this.field_6749;
			double d = this.x - this.prevX;
			double e = this.z - this.prevZ;
			float j = MathHelper.sqrt(d * d + e * e) * 4.0F;
			if (j > 1.0F) {
				j = 1.0F;
			}

			this.field_6749 = this.field_6749 + (j - this.field_6749) * 0.4F;
			this.field_6750 = this.field_6750 + this.field_6749;
		} else {
			this.stepHeight = 0.5F;
			this.flyingSpeed = 0.02F;
			super.method_2657(f, g, h);
		}
	}

	public boolean method_13117() {
		if (this.field_14617) {
			return false;
		} else {
			this.field_14617 = true;
			this.field_14613 = 0;
			this.field_14614 = this.getRandom().nextInt(841) + 140;
			this.getDataTracker().set(field_15485, this.field_14614);
			return true;
		}
	}

	public PigEntity breed(PassiveEntity passiveEntity) {
		return new PigEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return field_16918.test(stack);
	}
}
