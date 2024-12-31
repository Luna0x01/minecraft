package net.minecraft.entity.passive;

import net.minecraft.class_3473;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity implements EntityCategoryProvider {
	private static final TrackedData<Boolean> SPAWNED_FROM_BUCKET = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public FishEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.entityMotionHelper = new FishEntity.class_3478(this);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.65F;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(3.0);
	}

	@Override
	public boolean isPersistent() {
		return this.method_15722() || super.isPersistent();
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		BlockPos blockPos = new BlockPos(this);
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.WATER && iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.WATER
			? super.method_15652(iWorld, bl)
			: false;
	}

	@Override
	public boolean canImmediatelyDespawn() {
		return !this.method_15722() && !this.hasCustomName();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SPAWNED_FROM_BUCKET, false);
	}

	private boolean method_15722() {
		return this.dataTracker.get(SPAWNED_FROM_BUCKET);
	}

	public void method_15721(boolean bl) {
		this.dataTracker.set(SPAWNED_FROM_BUCKET, bl);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("FromBucket", this.method_15722());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_15721(nbt.getBoolean("FromBucket"));
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new EscapeDangerGoal(this, 1.25));
		this.goals.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, EntityPredicate.field_16705));
		this.goals.add(4, new FishEntity.class_3008(this));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public void method_2657(float f, float g, float h) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.method_2492(f, g, h, 0.01F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (this.getTarget() == null) {
				this.velocityY -= 0.005;
			}
		} else {
			super.method_2657(f, g, h);
		}
	}

	@Override
	public void tickMovement() {
		if (!this.isTouchingWater() && this.onGround && this.verticalCollision) {
			this.velocityY += 0.4F;
			this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
			this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
			this.onGround = false;
			this.velocityDirty = true;
			this.playSound(this.method_15724(), this.getSoundVolume(), this.getSoundPitch());
		}

		super.tickMovement();
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
			this.playSound(Sounds.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
			itemStack.decrement(1);
			ItemStack itemStack2 = this.method_15726();
			this.method_15725(itemStack2);
			if (!this.world.isClient) {
				AchievementsAndCriterions.field_21658.method_15967((ServerPlayerEntity)playerEntity, itemStack2);
			}

			if (itemStack.isEmpty()) {
				playerEntity.equipStack(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			this.remove();
			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	protected void method_15725(ItemStack itemStack) {
		if (this.hasCustomName()) {
			itemStack.setCustomName(this.method_15541());
		}
	}

	protected abstract ItemStack method_15726();

	protected boolean isIndependent() {
		return true;
	}

	protected abstract Sound method_15724();

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_FISH_SWIM;
	}

	static class class_3008 extends class_3473 {
		private final FishEntity field_16895;

		public class_3008(FishEntity fishEntity) {
			super(fishEntity, 1.0, 40);
			this.field_16895 = fishEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_16895.isIndependent() && super.canStart();
		}
	}

	static class class_3478 extends MoveControl {
		private final FishEntity field_16894;

		class_3478(FishEntity fishEntity) {
			super(fishEntity);
			this.field_16894 = fishEntity;
		}

		@Override
		public void updateMovement() {
			if (this.field_16894.method_15567(FluidTags.WATER)) {
				this.field_16894.velocityY += 0.005;
			}

			if (this.state == MoveControl.MoveStatus.MOVE_TO && !this.field_16894.getNavigation().isIdle()) {
				double d = this.targetX - this.field_16894.x;
				double e = this.targetY - this.field_16894.y;
				double f = this.targetZ - this.field_16894.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_16894.yaw = this.wrapDegrees(this.field_16894.yaw, h, 90.0F);
				this.field_16894.bodyYaw = this.field_16894.yaw;
				float i = (float)(this.speed * this.field_16894.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				this.field_16894.setMovementSpeed(this.field_16894.getMovementSpeed() + (i - this.field_16894.getMovementSpeed()) * 0.125F);
				this.field_16894.velocityY = this.field_16894.velocityY + (double)this.field_16894.getMovementSpeed() * e * 0.1;
			} else {
				this.field_16894.setMovementSpeed(0.0F);
			}
		}
	}
}
