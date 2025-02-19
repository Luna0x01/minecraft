package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class StriderEntity extends AnimalEntity implements ItemSteerable, Saddleable {
	private static final float COLD_SADDLED_SPEED = 0.23F;
	private static final float COLD_SPEED = 0.66F;
	private static final float DEFAULT_SADDLED_SPEED = 0.55F;
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS);
	private static final Ingredient ATTRACTING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
	private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> COLD = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final SaddledComponent saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
	private TemptGoal temptGoal;
	private EscapeDangerGoal escapeDangerGoal;

	public StriderEntity(EntityType<? extends StriderEntity> entityType, World world) {
		super(entityType, world);
		this.inanimate = true;
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
	}

	public static boolean canSpawn(EntityType<StriderEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		do {
			mutable.move(Direction.UP);
		} while (world.getFluidState(mutable).isIn(FluidTags.LAVA));

		return world.getBlockState(mutable).isAir();
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (BOOST_TIME.equals(data) && this.world.isClient) {
			this.saddledComponent.boost();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BOOST_TIME, 0);
		this.dataTracker.startTracking(COLD, false);
		this.dataTracker.startTracking(SADDLED, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.saddledComponent.writeNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.saddledComponent.readNbt(nbt);
	}

	@Override
	public boolean isSaddled() {
		return this.saddledComponent.isSaddled();
	}

	@Override
	public boolean canBeSaddled() {
		return this.isAlive() && !this.isBaby();
	}

	@Override
	public void saddle(@Nullable SoundCategory sound) {
		this.saddledComponent.setSaddled(true);
		if (sound != null) {
			this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_STRIDER_SADDLE, sound, 0.5F, 1.0F);
		}
	}

	@Override
	protected void initGoals() {
		this.escapeDangerGoal = new EscapeDangerGoal(this, 1.65);
		this.goalSelector.add(1, this.escapeDangerGoal);
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.temptGoal = new TemptGoal(this, 1.4, ATTRACTING_INGREDIENT, false);
		this.goalSelector.add(3, this.temptGoal);
		this.goalSelector.add(4, new StriderEntity.GoBackToLavaGoal(this, 1.5));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.goalSelector.add(9, new LookAtEntityGoal(this, StriderEntity.class, 8.0F));
	}

	public void setCold(boolean cold) {
		this.dataTracker.set(COLD, cold);
	}

	public boolean isCold() {
		return this.getVehicle() instanceof StriderEntity ? ((StriderEntity)this.getVehicle()).isCold() : this.dataTracker.get(COLD);
	}

	@Override
	public boolean canWalkOnFluid(Fluid fluid) {
		return fluid.isIn(FluidTags.LAVA);
	}

	@Override
	public double getMountedHeightOffset() {
		float f = Math.min(0.25F, this.limbDistance);
		float g = this.limbAngle;
		return (double)this.getHeight() - 0.19 + (double)(0.12F * MathHelper.cos(g * 1.5F) * 2.0F * f);
	}

	@Override
	public boolean canBeControlledByRider() {
		return !(this.getPrimaryPassenger() instanceof PlayerEntity playerEntity)
			? false
			: playerEntity.getMainHandStack().isOf(Items.WARPED_FUNGUS_ON_A_STICK) || playerEntity.getOffHandStack().isOf(Items.WARPED_FUNGUS_ON_A_STICK);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getFirstPassenger();
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Vec3d[] vec3ds = new Vec3d[]{
			getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw()),
			getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() - 22.5F),
			getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() + 22.5F),
			getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() - 45.0F),
			getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() + 45.0F)
		};
		Set<BlockPos> set = Sets.newLinkedHashSet();
		double d = this.getBoundingBox().maxY;
		double e = this.getBoundingBox().minY - 0.5;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Vec3d vec3d : vec3ds) {
			mutable.set(this.getX() + vec3d.x, d, this.getZ() + vec3d.z);

			for (double f = d; f > e; f--) {
				set.add(mutable.toImmutable());
				mutable.move(Direction.DOWN);
			}
		}

		for (BlockPos blockPos : set) {
			if (!this.world.getFluidState(blockPos).isIn(FluidTags.LAVA)) {
				double g = this.world.getDismountHeight(blockPos);
				if (Dismounting.canDismountInBlock(g)) {
					Vec3d vec3d2 = Vec3d.ofCenter(blockPos, g);
					UnmodifiableIterator var14 = passenger.getPoses().iterator();

					while (var14.hasNext()) {
						EntityPose entityPose = (EntityPose)var14.next();
						Box box = passenger.getBoundingBox(entityPose);
						if (Dismounting.canPlaceEntityAt(this.world, passenger, box.offset(vec3d2))) {
							passenger.setPose(entityPose);
							return vec3d2;
						}
					}
				}
			}
		}

		return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
	}

	@Override
	public void travel(Vec3d movementInput) {
		this.setMovementSpeed(this.getSpeed());
		this.travel(this, this.saddledComponent, movementInput);
	}

	public float getSpeed() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.66F : 1.0F);
	}

	@Override
	public float getSaddledSpeed() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.23F : 0.55F);
	}

	@Override
	public void setMovementInput(Vec3d movementInput) {
		super.travel(movementInput);
	}

	@Override
	protected float calculateNextStepSoundDistance() {
		return this.distanceTraveled + 0.6F;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0F, 1.0F);
	}

	@Override
	public boolean consumeOnAStickItem() {
		return this.saddledComponent.boost(this.getRandom());
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
		this.checkBlockCollision();
		if (this.isInLava()) {
			this.fallDistance = 0.0F;
		} else {
			super.fall(heightDifference, onGround, landedState, landedPosition);
		}
	}

	@Override
	public void tick() {
		if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0F, this.getSoundPitch());
		} else if (this.isEscapingDanger() && this.random.nextInt(60) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0F, this.getSoundPitch());
		}

		BlockState blockState = this.world.getBlockState(this.getBlockPos());
		BlockState blockState2 = this.getLandingBlockState();
		boolean bl = blockState.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.isIn(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
		this.setCold(!bl);
		super.tick();
		this.updateFloating();
		this.checkBlockCollision();
	}

	private boolean isEscapingDanger() {
		return this.escapeDangerGoal != null && this.escapeDangerGoal.isActive();
	}

	private boolean isBeingTempted() {
		return this.temptGoal != null && this.temptGoal.isActive();
	}

	@Override
	protected boolean movesIndependently() {
		return true;
	}

	private void updateFloating() {
		if (this.isInLava()) {
			ShapeContext shapeContext = ShapeContext.of(this);
			if (shapeContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.world.getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) {
				this.onGround = true;
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5).add(0.0, 0.05, 0.0));
			}
		}
	}

	public static DefaultAttributeContainer.Builder createStriderAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return !this.isEscapingDanger() && !this.isBeingTempted() ? SoundEvents.ENTITY_STRIDER_AMBIENT : null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_STRIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_STRIDER_DEATH;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return !this.hasPassengers() && !this.isSubmergedIn(FluidTags.LAVA);
	}

	@Override
	public boolean hurtByWater() {
		return true;
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new StriderEntity.Navigation(this, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		if (world.getBlockState(pos).getFluidState().isIn(FluidTags.LAVA)) {
			return 10.0F;
		} else {
			return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
		}
	}

	public StriderEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.STRIDER.create(serverWorld);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.isSaddled()) {
			this.dropItem(Items.SADDLE);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		boolean bl = this.isBreedingItem(player.getStackInHand(hand));
		if (!bl && this.isSaddled() && !this.hasPassengers() && !player.shouldCancelInteraction()) {
			if (!this.world.isClient) {
				player.startRiding(this);
			}

			return ActionResult.success(this.world.isClient);
		} else {
			ActionResult actionResult = super.interactMob(player, hand);
			if (!actionResult.isAccepted()) {
				ItemStack itemStack = player.getStackInHand(hand);
				return itemStack.isOf(Items.SADDLE) ? itemStack.useOnEntity(player, this, hand) : ActionResult.PASS;
			} else {
				if (bl && !this.isSilent()) {
					this.world
						.playSound(
							null,
							this.getX(),
							this.getY(),
							this.getZ(),
							SoundEvents.ENTITY_STRIDER_EAT,
							this.getSoundCategory(),
							1.0F,
							1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
						);
				}

				return actionResult;
			}
		}
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (this.isBaby()) {
			return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
		} else {
			Object var7;
			if (this.random.nextInt(30) == 0) {
				MobEntity mobEntity = EntityType.ZOMBIFIED_PIGLIN.create(world.toServerWorld());
				var7 = this.initializeRider(world, difficulty, mobEntity, new ZombieEntity.ZombieData(ZombieEntity.shouldBeBaby(this.random), false));
				mobEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
				this.saddle(null);
			} else if (this.random.nextInt(10) == 0) {
				PassiveEntity passiveEntity = EntityType.STRIDER.create(world.toServerWorld());
				passiveEntity.setBreedingAge(-24000);
				var7 = this.initializeRider(world, difficulty, passiveEntity, null);
			} else {
				var7 = new PassiveEntity.PassiveData(0.5F);
			}

			return super.initialize(world, difficulty, spawnReason, (EntityData)var7, entityNbt);
		}
	}

	private EntityData initializeRider(ServerWorldAccess world, LocalDifficulty difficulty, MobEntity rider, @Nullable EntityData entityData) {
		rider.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
		rider.initialize(world, difficulty, SpawnReason.JOCKEY, entityData, null);
		rider.startRiding(this, true);
		return new PassiveEntity.PassiveData(0.0F);
	}

	static class GoBackToLavaGoal extends MoveToTargetPosGoal {
		private final StriderEntity strider;

		GoBackToLavaGoal(StriderEntity striderEntity, double d) {
			super(striderEntity, d, 8, 2);
			this.strider = striderEntity;
		}

		@Override
		public BlockPos getTargetPos() {
			return this.targetPos;
		}

		@Override
		public boolean shouldContinue() {
			return !this.strider.isInLava() && this.isTargetPos(this.strider.world, this.targetPos);
		}

		@Override
		public boolean canStart() {
			return !this.strider.isInLava() && super.canStart();
		}

		@Override
		public boolean shouldResetPath() {
			return this.tryingTime % 20 == 0;
		}

		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			return world.getBlockState(pos).isOf(Blocks.LAVA) && world.getBlockState(pos.up()).canPathfindThrough(world, pos, NavigationType.LAND);
		}
	}

	static class Navigation extends MobNavigation {
		Navigation(StriderEntity entity, World world) {
			super(entity, world);
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int range) {
			this.nodeMaker = new LandPathNodeMaker();
			return new PathNodeNavigator(this.nodeMaker, range);
		}

		@Override
		protected boolean canWalkOnPath(PathNodeType pathType) {
			return pathType != PathNodeType.LAVA && pathType != PathNodeType.DAMAGE_FIRE && pathType != PathNodeType.DANGER_FIRE ? super.canWalkOnPath(pathType) : true;
		}

		@Override
		public boolean isValidPosition(BlockPos pos) {
			return this.world.getBlockState(pos).isOf(Blocks.LAVA) || super.isValidPosition(pos);
		}
	}
}
