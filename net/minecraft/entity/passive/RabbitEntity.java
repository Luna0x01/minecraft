package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DesertBiome;

public class RabbitEntity extends AnimalEntity {
	private static final TrackedData<Integer> field_14618 = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int jumpTicks;
	private int jumpDuration;
	private boolean lastOnGround;
	private int ticksUntilJump;
	private int moreCarrotTicks;

	public RabbitEntity(World world) {
		super(world);
		this.setBounds(0.4F, 0.5F);
		this.jumpControl = new RabbitEntity.RabbitJumpControl(this);
		this.entityMotionHelper = new RabbitEntity.RabbitMoveControl(this);
		this.setSpeed(0.0);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(1, new RabbitEntity.EscapeDangerGoal(this, 2.2));
		this.goals.add(2, new BreedGoal(this, 0.8));
		this.goals.add(3, new TemptGoal(this, 1.0, Items.CARROT, false));
		this.goals.add(3, new TemptGoal(this, 1.0, Items.GOLDEN_CARROT, false));
		this.goals.add(3, new TemptGoal(this, 1.0, Item.fromBlock(Blocks.YELLOW_FLOWER), false));
		this.goals.add(4, new RabbitEntity.FleeGoal(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
		this.goals.add(4, new RabbitEntity.FleeGoal(this, WolfEntity.class, 10.0F, 2.2, 2.2));
		this.goals.add(4, new RabbitEntity.FleeGoal(this, HostileEntity.class, 4.0F, 2.2, 2.2));
		this.goals.add(5, new RabbitEntity.EatCarrotCropGoal(this));
		this.goals.add(6, new WanderAroundGoal(this, 0.6));
		this.goals.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
	}

	@Override
	protected float getJumpVelocity() {
		if (!this.horizontalCollision && (!this.entityMotionHelper.isMoving() || !(this.entityMotionHelper.getTargetY() > this.y + 0.5))) {
			PathMinHeap pathMinHeap = this.navigation.method_13113();
			if (pathMinHeap != null && pathMinHeap.method_11937() < pathMinHeap.method_11936()) {
				Vec3d vec3d = pathMinHeap.method_11928(this);
				if (vec3d.y > this.y) {
					return 0.5F;
				}
			}

			return this.entityMotionHelper.getSpeed() <= 0.6 ? 0.2F : 0.3F;
		} else {
			return 0.5F;
		}
	}

	@Override
	protected void jump() {
		super.jump();
		double d = this.entityMotionHelper.getSpeed();
		if (d > 0.0) {
			double e = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
			if (e < 0.010000000000000002) {
				this.updateVelocity(0.0F, 1.0F, 0.1F);
			}
		}

		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)1);
		}
	}

	public float getJumpProgress(float delta) {
		return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + delta) / (float)this.jumpDuration;
	}

	public void setSpeed(double speed) {
		this.getNavigation().setSpeed(speed);
		this.entityMotionHelper.moveTo(this.entityMotionHelper.getTargetX(), this.entityMotionHelper.getTargetY(), this.entityMotionHelper.getTargetZ(), speed);
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			this.playSound(this.method_13121(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	public void method_13120() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14618, 0);
	}

	@Override
	public void mobTick() {
		if (this.ticksUntilJump > 0) {
			this.ticksUntilJump--;
		}

		if (this.moreCarrotTicks > 0) {
			this.moreCarrotTicks = this.moreCarrotTicks - this.random.nextInt(3);
			if (this.moreCarrotTicks < 0) {
				this.moreCarrotTicks = 0;
			}
		}

		if (this.onGround) {
			if (!this.lastOnGround) {
				this.setJumping(false);
				this.method_11089();
			}

			if (this.getRabbitType() == 99 && this.ticksUntilJump == 0) {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
					this.lookTowards(livingEntity.x, livingEntity.z);
					this.entityMotionHelper.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, this.entityMotionHelper.getSpeed());
					this.method_13120();
					this.lastOnGround = true;
				}
			}

			RabbitEntity.RabbitJumpControl rabbitJumpControl = (RabbitEntity.RabbitJumpControl)this.jumpControl;
			if (!rabbitJumpControl.isActive()) {
				if (this.entityMotionHelper.isMoving() && this.ticksUntilJump == 0) {
					PathMinHeap pathMinHeap = this.navigation.method_13113();
					Vec3d vec3d = new Vec3d(this.entityMotionHelper.getTargetX(), this.entityMotionHelper.getTargetY(), this.entityMotionHelper.getTargetZ());
					if (pathMinHeap != null && pathMinHeap.method_11937() < pathMinHeap.method_11936()) {
						vec3d = pathMinHeap.method_11928(this);
					}

					this.lookTowards(vec3d.x, vec3d.z);
					this.method_13120();
				}
			} else if (!rabbitJumpControl.method_11099()) {
				this.method_11086();
			}
		}

		this.lastOnGround = this.onGround;
	}

	@Override
	public void attemptSprintingParticles() {
	}

	private void lookTowards(double x, double z) {
		this.yaw = (float)(MathHelper.atan2(z - this.z, x - this.x) * 180.0F / (float)Math.PI) - 90.0F;
	}

	private void method_11086() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_11097(true);
	}

	private void method_11087() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_11097(false);
	}

	private void method_11088() {
		if (this.entityMotionHelper.getSpeed() < 2.2) {
			this.ticksUntilJump = 10;
		} else {
			this.ticksUntilJump = 1;
		}
	}

	private void method_11089() {
		this.method_11088();
		this.method_11087();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.jumpTicks != this.jumpDuration) {
			this.jumpTicks++;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(3.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Rabbit");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("RabbitType", this.getRabbitType());
		nbt.putInt("MoreCarrotTicks", this.moreCarrotTicks);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setRabbitType(nbt.getInt("RabbitType"));
		this.moreCarrotTicks = nbt.getInt("MoreCarrotTicks");
	}

	protected Sound method_13121() {
		return Sounds.ENTITY_RABBIT_JUMP;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_RABBIT_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_RABBIT_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_RABBIT_DEATH;
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (this.getRabbitType() == 99) {
			this.playSound(Sounds.ENTITY_RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			return target.damage(DamageSource.mob(this), 8.0F);
		} else {
			return target.damage(DamageSource.mob(this), 3.0F);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.RABBIT_ENTITIE;
	}

	private boolean method_11076(Item item) {
		return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Item.fromBlock(Blocks.YELLOW_FLOWER);
	}

	public RabbitEntity breed(PassiveEntity passiveEntity) {
		RabbitEntity rabbitEntity = new RabbitEntity(this.world);
		int i = this.method_13122();
		if (this.random.nextInt(20) != 0) {
			if (passiveEntity instanceof RabbitEntity && this.random.nextBoolean()) {
				i = ((RabbitEntity)passiveEntity).getRabbitType();
			} else {
				i = this.getRabbitType();
			}
		}

		rabbitEntity.setRabbitType(i);
		return rabbitEntity;
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack != null && this.method_11076(stack.getItem());
	}

	public int getRabbitType() {
		return this.dataTracker.get(field_14618);
	}

	public void setRabbitType(int type) {
		if (type == 99) {
			this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue(8.0);
			this.goals.add(4, new RabbitEntity.RabbitAttackGoal(this));
			this.attackGoals.add(1, new RevengeGoal(this, false));
			this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
			this.attackGoals.add(2, new FollowTargetGoal(this, WolfEntity.class, true));
			if (!this.hasCustomName()) {
				this.setCustomName(CommonI18n.translate("entity.KillerBunny.name"));
			}
		}

		this.dataTracker.set(field_14618, type);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		int i = this.method_13122();
		boolean bl = false;
		if (data instanceof RabbitEntity.Data) {
			i = ((RabbitEntity.Data)data).type;
			bl = true;
		} else {
			data = new RabbitEntity.Data(i);
		}

		this.setRabbitType(i);
		if (bl) {
			this.setAge(-24000);
		}

		return data;
	}

	private int method_13122() {
		Biome biome = this.world.getBiome(new BlockPos(this));
		int i = this.random.nextInt(100);
		if (biome.isMutatedBiome()) {
			return i < 80 ? 1 : 3;
		} else if (biome instanceof DesertBiome) {
			return 4;
		} else {
			return i < 50 ? 0 : (i < 90 ? 5 : 2);
		}
	}

	private boolean wantsCarrots() {
		return this.moreCarrotTicks == 0;
	}

	protected void method_11085() {
		CarrotsBlock carrotsBlock = (CarrotsBlock)Blocks.CARROTS;
		BlockState blockState = carrotsBlock.withAge(carrotsBlock.getMaxAge());
		this.world
			.addParticle(
				ParticleType.BLOCK_DUST,
				this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
				this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
				this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
				0.0,
				0.0,
				0.0,
				Block.getByBlockState(blockState)
			);
		this.moreCarrotTicks = 40;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 1) {
			this.spawnSprintingParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
	}

	public static class Data implements EntityData {
		public int type;

		public Data(int i) {
			this.type = i;
		}
	}

	static class EatCarrotCropGoal extends MoveToTargetPosGoal {
		private final RabbitEntity rabbit;
		private boolean wantsCarrots;
		private boolean hasTarget;

		public EatCarrotCropGoal(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 0.7F, 16);
			this.rabbit = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			if (this.cooldown <= 0) {
				if (!this.rabbit.world.getGameRules().getBoolean("mobGriefing")) {
					return false;
				}

				this.hasTarget = false;
				this.wantsCarrots = this.rabbit.wantsCarrots();
				this.wantsCarrots = true;
			}

			return super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return this.hasTarget && super.shouldContinue();
		}

		@Override
		public void start() {
			super.start();
		}

		@Override
		public void stop() {
			super.stop();
		}

		@Override
		public void tick() {
			super.tick();
			this.rabbit
				.getLookControl()
				.lookAt(
					(double)this.targetPos.getX() + 0.5,
					(double)(this.targetPos.getY() + 1),
					(double)this.targetPos.getZ() + 0.5,
					10.0F,
					(float)this.rabbit.getLookPitchSpeed()
				);
			if (this.hasReached()) {
				World world = this.rabbit.world;
				BlockPos blockPos = this.targetPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (this.hasTarget && block instanceof CarrotsBlock) {
					Integer integer = blockState.get(CarrotsBlock.AGE);
					if (integer == 0) {
						world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
						world.removeBlock(blockPos, true);
					} else {
						world.setBlockState(blockPos, blockState.with(CarrotsBlock.AGE, integer - 1), 2);
						world.syncGlobalEvent(2001, blockPos, Block.getByBlockState(blockState));
					}

					this.rabbit.method_11085();
				}

				this.hasTarget = false;
				this.cooldown = 10;
			}
		}

		@Override
		protected boolean isTargetPos(World world, BlockPos pos) {
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.FARMLAND && this.wantsCarrots && !this.hasTarget) {
				pos = pos.up();
				BlockState blockState = world.getBlockState(pos);
				block = blockState.getBlock();
				if (block instanceof CarrotsBlock && ((CarrotsBlock)block).isMature(blockState)) {
					this.hasTarget = true;
					return true;
				}
			}

			return false;
		}
	}

	static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
		private final RabbitEntity rabbit;

		public EscapeDangerGoal(RabbitEntity rabbitEntity, double d) {
			super(rabbitEntity, d);
			this.rabbit = rabbitEntity;
		}

		@Override
		public void tick() {
			super.tick();
			this.rabbit.setSpeed(this.speed);
		}
	}

	static class FleeGoal<T extends Entity> extends FleeEntityGoal<T> {
		private final RabbitEntity rabbit;

		public FleeGoal(RabbitEntity rabbitEntity, Class<T> class_, float f, double d, double e) {
			super(rabbitEntity, class_, f, d, e);
			this.rabbit = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			return this.rabbit.getRabbitType() != 99 && super.canStart();
		}
	}

	static class RabbitAttackGoal extends MeleeAttackGoal {
		public RabbitAttackGoal(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 1.4, true);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.width);
		}
	}

	public class RabbitJumpControl extends JumpControl {
		private final RabbitEntity rabbit;
		private boolean field_12000;

		public RabbitJumpControl(RabbitEntity rabbitEntity2) {
			super(rabbitEntity2);
			this.rabbit = rabbitEntity2;
		}

		public boolean isActive() {
			return this.active;
		}

		public boolean method_11099() {
			return this.field_12000;
		}

		public void method_11097(boolean bl) {
			this.field_12000 = bl;
		}

		@Override
		public void tick() {
			if (this.active) {
				this.rabbit.method_13120();
				this.active = false;
			}
		}
	}

	static class RabbitMoveControl extends MoveControl {
		private final RabbitEntity rabbit;
		private double field_14619;

		public RabbitMoveControl(RabbitEntity rabbitEntity) {
			super(rabbitEntity);
			this.rabbit = rabbitEntity;
		}

		@Override
		public void updateMovement() {
			if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitEntity.RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
				this.rabbit.setSpeed(0.0);
			} else if (this.isMoving()) {
				this.rabbit.setSpeed(this.field_14619);
			}

			super.updateMovement();
		}

		@Override
		public void moveTo(double x, double y, double z, double speed) {
			if (this.rabbit.isTouchingWater()) {
				speed = 1.5;
			}

			super.moveTo(x, y, z, speed);
			if (speed > 0.0) {
				this.field_14619 = speed;
			}
		}
	}
}
