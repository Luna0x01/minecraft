package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.class_3732;
import net.minecraft.class_4065;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class TurtleEntity extends AnimalEntity {
	private static final TrackedData<BlockPos> field_16958 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_16959 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_16960 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<BlockPos> field_16961 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_16962 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_16963 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_16964;
	public static final Predicate<Entity> field_16957 = entity -> !(entity instanceof LivingEntity)
			? false
			: ((LivingEntity)entity).isBaby() && !entity.isTouchingWater();

	public TurtleEntity(World world) {
		super(EntityType.TURTLE, world);
		this.setBounds(1.2F, 0.4F);
		this.entityMotionHelper = new TurtleEntity.class_3500(this);
		this.field_11973 = Blocks.SAND;
		this.stepHeight = 1.0F;
	}

	public void method_15817(BlockPos blockPos) {
		this.dataTracker.set(field_16958, blockPos);
	}

	private BlockPos method_15808() {
		return this.dataTracker.get(field_16958);
	}

	private void method_15819(BlockPos blockPos) {
		this.dataTracker.set(field_16961, blockPos);
	}

	private BlockPos method_15809() {
		return this.dataTracker.get(field_16961);
	}

	public boolean method_15812() {
		return this.dataTracker.get(field_16959);
	}

	private void method_15820(boolean bl) {
		this.dataTracker.set(field_16959, bl);
	}

	public boolean method_15813() {
		return this.dataTracker.get(field_16960);
	}

	private void method_15821(boolean bl) {
		this.field_16964 = bl ? 1 : 0;
		this.dataTracker.set(field_16960, bl);
	}

	private boolean method_15810() {
		return this.dataTracker.get(field_16962);
	}

	private void method_15822(boolean bl) {
		this.dataTracker.set(field_16962, bl);
	}

	private boolean method_15811() {
		return this.dataTracker.get(field_16963);
	}

	private void method_15823(boolean bl) {
		this.dataTracker.set(field_16963, bl);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_16958, BlockPos.ORIGIN);
		this.dataTracker.startTracking(field_16959, false);
		this.dataTracker.startTracking(field_16961, BlockPos.ORIGIN);
		this.dataTracker.startTracking(field_16962, false);
		this.dataTracker.startTracking(field_16963, false);
		this.dataTracker.startTracking(field_16960, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("HomePosX", this.method_15808().getX());
		nbt.putInt("HomePosY", this.method_15808().getY());
		nbt.putInt("HomePosZ", this.method_15808().getZ());
		nbt.putBoolean("HasEgg", this.method_15812());
		nbt.putInt("TravelPosX", this.method_15809().getX());
		nbt.putInt("TravelPosY", this.method_15809().getY());
		nbt.putInt("TravelPosZ", this.method_15809().getZ());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		int i = nbt.getInt("HomePosX");
		int j = nbt.getInt("HomePosY");
		int k = nbt.getInt("HomePosZ");
		this.method_15817(new BlockPos(i, j, k));
		super.readCustomDataFromNbt(nbt);
		this.method_15820(nbt.getBoolean("HasEgg"));
		int l = nbt.getInt("TravelPosX");
		int m = nbt.getInt("TravelPosY");
		int n = nbt.getInt("TravelPosZ");
		this.method_15819(new BlockPos(l, m, n));
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.method_15817(new BlockPos(this.x, this.y, this.z));
		this.method_15819(BlockPos.ORIGIN);
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		return blockPos.getY() < iWorld.method_8483() + 4 && super.method_15652(iWorld, bl);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new TurtleEntity.class_3501(this, 1.2));
		this.goals.add(1, new TurtleEntity.class_3496(this, 1.0));
		this.goals.add(1, new TurtleEntity.class_3499(this, 1.0));
		this.goals.add(2, new TurtleEntity.class_3504(this, 1.1, Blocks.SEAGRASS.getItem()));
		this.goals.add(3, new TurtleEntity.class_3498(this, 1.0));
		this.goals.add(4, new TurtleEntity.class_3497(this, 1.0));
		this.goals.add(7, new TurtleEntity.class_3505(this, 1.0));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(9, new TurtleEntity.class_3503(this, 1.0, 100));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(30.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean method_2607() {
		return true;
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16822;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 200;
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		return !this.isTouchingWater() && this.onGround && !this.isBaby() ? Sounds.ENTITY_TURTLE_AMBIENT_LAND : super.ambientSound();
	}

	@Override
	protected void method_15588(float f) {
		super.method_15588(f * 1.5F);
	}

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_TURTLE_SWIM;
	}

	@Nullable
	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return this.isBaby() ? Sounds.ENTITY_TURTLE_HURT_BABY : Sounds.ENTITY_TURTLE_HURT;
	}

	@Nullable
	@Override
	protected Sound deathSound() {
		return this.isBaby() ? Sounds.ENTITY_TURTLE_DEATH_BABY : Sounds.ENTITY_TURTLE_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		Sound sound = this.isBaby() ? Sounds.ENTITY_TURTLE_SHAMBLE_BABY : Sounds.ENTITY_TURTLE_SHAMBLE;
		this.playSound(sound, 0.15F, 1.0F);
	}

	@Override
	public boolean method_15741() {
		return super.method_15741() && !this.method_15812();
	}

	@Override
	protected float method_15572() {
		return this.distanceTraveled + 0.15F;
	}

	@Override
	public void method_5377(boolean bl) {
		this.method_5378(bl ? 0.3F : 1.0F);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new TurtleEntity.class_3502(this, world);
	}

	@Nullable
	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		return new TurtleEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Blocks.SEAGRASS.getItem();
	}

	@Override
	public float method_15657(BlockPos blockPos, RenderBlockView renderBlockView) {
		return !this.method_15810() && renderBlockView.getFluidState(blockPos).matches(FluidTags.WATER) ? 10.0F : super.method_15657(blockPos, renderBlockView);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.method_15813() && this.field_16964 >= 1 && this.field_16964 % 5 == 0) {
			BlockPos blockPos = new BlockPos(this);
			if (this.world.getBlockState(blockPos.down()).getBlock() == Blocks.SAND) {
				this.world.syncGlobalEvent(2001, blockPos, Block.getRawIdFromState(Blocks.SAND.getDefaultState()));
			}
		}
	}

	@Override
	protected void method_10926() {
		super.method_10926();
		if (this.world.getGameRules().getBoolean("doMobLoot")) {
			this.method_15561(Items.SCUTE, 1);
		}
	}

	@Override
	public void method_2657(float f, float g, float h) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.method_2492(f, g, h, 0.1F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (this.getTarget() == null && (!this.method_15810() || !(this.squaredDistanceTo(this.method_15808()) < 400.0))) {
				this.velocityY -= 0.005;
			}
		} else {
			super.method_2657(f, g, h);
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.TURTLE_ENTITIE;
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
		this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source == DamageSource.LIGHTNING_BOLT) {
			this.dropItem(new ItemStack(Items.BOWL, 1), 0.0F);
		}
	}

	static class class_3496 extends BreedGoal {
		private final TurtleEntity field_16965;

		class_3496(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d);
			this.field_16965 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.field_16965.method_15812();
		}

		@Override
		protected void breed() {
			ServerPlayerEntity serverPlayerEntity = this.animal.method_15103();
			if (serverPlayerEntity == null && this.mate.method_15103() != null) {
				serverPlayerEntity = this.mate.method_15103();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.method_15928(Stats.ANIMALS_BRED);
				AchievementsAndCriterions.field_16342.method_15041(serverPlayerEntity, this.animal, this.mate, null);
			}

			this.field_16965.method_15820(true);
			this.animal.resetLoveTicks();
			this.mate.resetLoveTicks();
			Random random = this.animal.getRandom();
			if (this.world.getGameRules().getBoolean("doMobLoot")) {
				this.world.method_3686(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, random.nextInt(7) + 1));
			}
		}
	}

	static class class_3497 extends Goal {
		private final TurtleEntity field_16966;
		private final double field_16967;
		private boolean field_16968;
		private int field_16969;

		class_3497(TurtleEntity turtleEntity, double d) {
			this.field_16966 = turtleEntity;
			this.field_16967 = d;
		}

		@Override
		public boolean canStart() {
			if (this.field_16966.isBaby()) {
				return false;
			} else if (this.field_16966.method_15812()) {
				return true;
			} else {
				return this.field_16966.getRandom().nextInt(700) != 0 ? false : this.field_16966.squaredDistanceTo(this.field_16966.method_15808()) >= 4096.0;
			}
		}

		@Override
		public void start() {
			this.field_16966.method_15822(true);
			this.field_16968 = false;
			this.field_16969 = 0;
		}

		@Override
		public void stop() {
			this.field_16966.method_15822(false);
		}

		@Override
		public boolean shouldContinue() {
			return this.field_16966.squaredDistanceTo(this.field_16966.method_15808()) >= 49.0 && !this.field_16968 && this.field_16969 <= 600;
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_16966.method_15808();
			boolean bl = this.field_16966.squaredDistanceTo(blockPos) <= 256.0;
			if (bl) {
				this.field_16969++;
			}

			if (this.field_16966.getNavigation().isIdle()) {
				Vec3d vec3d = RandomVectorGenerator.method_15715(
					this.field_16966, 16, 3, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 10)
				);
				if (vec3d == null) {
					vec3d = RandomVectorGenerator.method_2800(this.field_16966, 8, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null && !bl && this.field_16966.world.getBlockState(new BlockPos(vec3d)).getBlock() != Blocks.WATER) {
					vec3d = RandomVectorGenerator.method_2800(this.field_16966, 16, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d == null) {
					this.field_16968 = true;
					return;
				}

				this.field_16966.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_16967);
			}
		}
	}

	static class class_3498 extends MoveToTargetPosGoal {
		private final TurtleEntity field_16970;

		private class_3498(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, turtleEntity.isBaby() ? 2.0 : d, 24);
			this.field_16970 = turtleEntity;
			this.field_16880 = -1;
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_16970.isTouchingWater() && this.tryingTime <= 1200 && this.method_11012(this.field_16970.world, this.targetPos);
		}

		@Override
		public boolean canStart() {
			if (this.field_16970.isBaby() && !this.field_16970.isTouchingWater()) {
				return super.canStart();
			} else {
				return !this.field_16970.method_15810() && !this.field_16970.isTouchingWater() && !this.field_16970.method_15812() ? super.canStart() : false;
			}
		}

		@Override
		public int method_15697() {
			return 1;
		}

		@Override
		public boolean method_15696() {
			return this.tryingTime % 160 == 0;
		}

		@Override
		protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
			Block block = renderBlockView.getBlockState(blockPos).getBlock();
			return block == Blocks.WATER;
		}
	}

	static class class_3499 extends MoveToTargetPosGoal {
		private final TurtleEntity field_16971;

		class_3499(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d, 16);
			this.field_16971 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_16971.method_15812() && this.field_16971.squaredDistanceTo(this.field_16971.method_15808()) < 81.0 ? super.canStart() : false;
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.field_16971.method_15812() && this.field_16971.squaredDistanceTo(this.field_16971.method_15808()) < 81.0;
		}

		@Override
		public void tick() {
			super.tick();
			BlockPos blockPos = new BlockPos(this.field_16971);
			if (!this.field_16971.isTouchingWater() && this.hasReached()) {
				if (this.field_16971.field_16964 < 1) {
					this.field_16971.method_15821(true);
				} else if (this.field_16971.field_16964 > 200) {
					World world = this.field_16971.world;
					world.playSound(null, blockPos, Sounds.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
					world.setBlockState(
						this.targetPos.up(), Blocks.TURTLE_EGG.getDefaultState().withProperty(class_3732.field_18560, Integer.valueOf(this.field_16971.random.nextInt(4) + 1)), 3
					);
					this.field_16971.method_15820(false);
					this.field_16971.method_15821(false);
					this.field_16971.method_15740(600);
				}

				if (this.field_16971.method_15813()) {
					this.field_16971.field_16964++;
				}
			}
		}

		@Override
		protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
			if (!renderBlockView.method_8579(blockPos.up())) {
				return false;
			} else {
				Block block = renderBlockView.getBlockState(blockPos).getBlock();
				return block == Blocks.SAND;
			}
		}
	}

	static class class_3500 extends MoveControl {
		private final TurtleEntity field_16972;

		class_3500(TurtleEntity turtleEntity) {
			super(turtleEntity);
			this.field_16972 = turtleEntity;
		}

		private void method_15824() {
			if (this.field_16972.isTouchingWater()) {
				this.field_16972.velocityY += 0.005;
				if (this.field_16972.squaredDistanceTo(this.field_16972.method_15808()) > 256.0) {
					this.field_16972.setMovementSpeed(Math.max(this.field_16972.getMovementSpeed() / 2.0F, 0.08F));
				}

				if (this.field_16972.isBaby()) {
					this.field_16972.setMovementSpeed(Math.max(this.field_16972.getMovementSpeed() / 3.0F, 0.06F));
				}
			} else if (this.field_16972.onGround) {
				this.field_16972.setMovementSpeed(Math.max(this.field_16972.getMovementSpeed() / 2.0F, 0.06F));
			}
		}

		@Override
		public void updateMovement() {
			this.method_15824();
			if (this.state == MoveControl.MoveStatus.MOVE_TO && !this.field_16972.getNavigation().isIdle()) {
				double d = this.targetX - this.field_16972.x;
				double e = this.targetY - this.field_16972.y;
				double f = this.targetZ - this.field_16972.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_16972.yaw = this.wrapDegrees(this.field_16972.yaw, h, 90.0F);
				this.field_16972.bodyYaw = this.field_16972.yaw;
				float i = (float)(this.speed * this.field_16972.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				this.field_16972.setMovementSpeed(this.field_16972.getMovementSpeed() + (i - this.field_16972.getMovementSpeed()) * 0.125F);
				this.field_16972.velocityY = this.field_16972.velocityY + (double)this.field_16972.getMovementSpeed() * e * 0.1;
			} else {
				this.field_16972.setMovementSpeed(0.0F);
			}
		}
	}

	static class class_3501 extends EscapeDangerGoal {
		class_3501(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d);
		}

		@Override
		public boolean canStart() {
			if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.method_15698(this.mob.world, this.mob, 7, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				} else {
					return this.method_13953();
				}
			}
		}
	}

	static class class_3502 extends SwimNavigation {
		class_3502(TurtleEntity turtleEntity, World world) {
			super(turtleEntity, world);
		}

		@Override
		protected boolean isAtValidPosition() {
			return true;
		}

		@Override
		protected PathNodeNavigator createNavigator() {
			return new PathNodeNavigator(new class_4065());
		}

		@Override
		public boolean method_13110(BlockPos blockPos) {
			if (this.mob instanceof TurtleEntity) {
				TurtleEntity turtleEntity = (TurtleEntity)this.mob;
				if (turtleEntity.method_15811()) {
					return this.world.getBlockState(blockPos).getBlock() == Blocks.WATER;
				}
			}

			return !this.world.getBlockState(blockPos.down()).isAir();
		}
	}

	static class class_3503 extends WanderAroundGoal {
		private final TurtleEntity field_16973;

		private class_3503(TurtleEntity turtleEntity, double d, int i) {
			super(turtleEntity, d, i);
			this.field_16973 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return !this.mob.isTouchingWater() && !this.field_16973.method_15810() && !this.field_16973.method_15812() ? super.canStart() : false;
		}
	}

	static class class_3504 extends Goal {
		private final TurtleEntity field_16974;
		private final double field_16975;
		private PlayerEntity field_16976;
		private int field_16977;
		private final Set<Item> field_16978;

		class_3504(TurtleEntity turtleEntity, double d, Item item) {
			this.field_16974 = turtleEntity;
			this.field_16975 = d;
			this.field_16978 = Sets.newHashSet(new Item[]{item});
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			if (this.field_16977 > 0) {
				this.field_16977--;
				return false;
			} else {
				this.field_16976 = this.field_16974.world.method_16364(this.field_16974, 10.0);
				return this.field_16976 == null ? false : this.method_15825(this.field_16976.getMainHandStack()) || this.method_15825(this.field_16976.getOffHandStack());
			}
		}

		private boolean method_15825(ItemStack itemStack) {
			return this.field_16978.contains(itemStack.getItem());
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart();
		}

		@Override
		public void stop() {
			this.field_16976 = null;
			this.field_16974.getNavigation().stop();
			this.field_16977 = 100;
		}

		@Override
		public void tick() {
			this.field_16974.getLookControl().lookAt(this.field_16976, (float)(this.field_16974.method_13081() + 20), (float)this.field_16974.getLookPitchSpeed());
			if (this.field_16974.squaredDistanceTo(this.field_16976) < 6.25) {
				this.field_16974.getNavigation().stop();
			} else {
				this.field_16974.getNavigation().startMovingTo(this.field_16976, this.field_16975);
			}
		}
	}

	static class class_3505 extends Goal {
		private final TurtleEntity field_16979;
		private final double field_16980;
		private boolean field_16981;

		class_3505(TurtleEntity turtleEntity, double d) {
			this.field_16979 = turtleEntity;
			this.field_16980 = d;
		}

		@Override
		public boolean canStart() {
			return !this.field_16979.method_15810() && !this.field_16979.method_15812() && this.field_16979.isTouchingWater();
		}

		@Override
		public void start() {
			int i = 512;
			int j = 4;
			Random random = this.field_16979.random;
			int k = random.nextInt(1025) - 512;
			int l = random.nextInt(9) - 4;
			int m = random.nextInt(1025) - 512;
			if ((double)l + this.field_16979.y > (double)(this.field_16979.world.method_8483() - 1)) {
				l = 0;
			}

			BlockPos blockPos = new BlockPos((double)k + this.field_16979.x, (double)l + this.field_16979.y, (double)m + this.field_16979.z);
			this.field_16979.method_15819(blockPos);
			this.field_16979.method_15823(true);
			this.field_16981 = false;
		}

		@Override
		public void tick() {
			if (this.field_16979.getNavigation().isIdle()) {
				BlockPos blockPos = this.field_16979.method_15809();
				Vec3d vec3d = RandomVectorGenerator.method_15715(
					this.field_16979, 16, 3, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 10)
				);
				if (vec3d == null) {
					vec3d = RandomVectorGenerator.method_2800(this.field_16979, 8, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					int i = MathHelper.floor(vec3d.x);
					int j = MathHelper.floor(vec3d.z);
					int k = 34;
					BlockBox blockBox = new BlockBox(i - 34, 0, j - 34, i + 34, 0, j + 34);
					if (!this.field_16979.world.method_16374(blockBox)) {
						vec3d = null;
					}
				}

				if (vec3d == null) {
					this.field_16981 = true;
					return;
				}

				this.field_16979.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_16980);
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_16979.getNavigation().isIdle()
				&& !this.field_16981
				&& !this.field_16979.method_15810()
				&& !this.field_16979.isInLove()
				&& !this.field_16979.method_15812();
		}

		@Override
		public void stop() {
			this.field_16979.method_15823(false);
			super.stop();
		}
	}
}
