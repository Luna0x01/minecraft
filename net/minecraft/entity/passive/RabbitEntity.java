package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.client.particle.ParticleType;
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
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class RabbitEntity extends AnimalEntity {
	private RabbitEntity.FleeGoal<WolfEntity> fleeFromWolfGoal;
	private int jumpTicks = 0;
	private int jumpDuration = 0;
	private boolean field_11980 = false;
	private boolean lastOnGround = false;
	private int ticksUntilJump = 0;
	private RabbitEntity.Action field_11983 = RabbitEntity.Action.HOP;
	private int moreCarrotTicks = 0;
	private PlayerEntity field_11985 = null;

	public RabbitEntity(World world) {
		super(world);
		this.setBounds(0.6F, 0.7F);
		this.jumpControl = new RabbitEntity.RabbitJumpControl(this);
		this.entityMotionHelper = new RabbitEntity.RabbitMoveControl(this);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.navigation.method_11038(2.5F);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(1, new RabbitEntity.EscapeDangerGoal(this, 1.33));
		this.goals.add(2, new TemptGoal(this, 1.0, Items.CARROT, false));
		this.goals.add(2, new TemptGoal(this, 1.0, Items.GOLDEN_CARROT, false));
		this.goals.add(2, new TemptGoal(this, 1.0, Item.fromBlock(Blocks.YELLOW_FLOWER), false));
		this.goals.add(3, new BreedGoal(this, 0.8));
		this.goals.add(5, new RabbitEntity.EatCarrotCropGoal(this));
		this.goals.add(5, new WanderAroundGoal(this, 0.6));
		this.goals.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.fleeFromWolfGoal = new RabbitEntity.FleeGoal<>(this, WolfEntity.class, 16.0F, 1.33, 1.33);
		this.goals.add(4, this.fleeFromWolfGoal);
		this.setSpeed(0.0);
	}

	@Override
	protected float getJumpVelocity() {
		return this.entityMotionHelper.isMoving() && this.entityMotionHelper.getTargetY() > this.y + 0.5 ? 0.5F : this.field_11983.method_11094();
	}

	public void method_11074(RabbitEntity.Action action) {
		this.field_11983 = action;
	}

	public float getJumpProgress(float delta) {
		return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + delta) / (float)this.jumpDuration;
	}

	public void setSpeed(double speed) {
		this.getNavigation().setSpeed(speed);
		this.entityMotionHelper.moveTo(this.entityMotionHelper.getTargetX(), this.entityMotionHelper.getTargetY(), this.entityMotionHelper.getTargetZ(), speed);
	}

	public void method_11077(boolean bl, RabbitEntity.Action action) {
		super.setJumping(bl);
		if (!bl) {
			if (this.field_11983 == RabbitEntity.Action.ATTACK) {
				this.field_11983 = RabbitEntity.Action.HOP;
			}
		} else {
			this.setSpeed(1.5 * (double)action.getSpeed());
			this.playSound(this.getHopSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}

		this.field_11980 = bl;
	}

	public void method_11080(RabbitEntity.Action action) {
		this.method_11077(true, action);
		this.jumpDuration = action.getJumpDuration();
		this.jumpTicks = 0;
	}

	public boolean method_11081() {
		return this.field_11980;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(18, (byte)0);
	}

	@Override
	public void mobTick() {
		if (this.entityMotionHelper.getSpeed() > 0.8) {
			this.method_11074(RabbitEntity.Action.SPRINT);
		} else if (this.field_11983 != RabbitEntity.Action.ATTACK) {
			this.method_11074(RabbitEntity.Action.HOP);
		}

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
				this.method_11077(false, RabbitEntity.Action.NONE);
				this.method_11089();
			}

			if (this.getRabbitType() == 99 && this.ticksUntilJump == 0) {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
					this.lookTowards(livingEntity.x, livingEntity.z);
					this.entityMotionHelper.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, this.entityMotionHelper.getSpeed());
					this.method_11080(RabbitEntity.Action.ATTACK);
					this.lastOnGround = true;
				}
			}

			RabbitEntity.RabbitJumpControl rabbitJumpControl = (RabbitEntity.RabbitJumpControl)this.jumpControl;
			if (!rabbitJumpControl.isActive()) {
				if (this.entityMotionHelper.isMoving() && this.ticksUntilJump == 0) {
					Path path = this.navigation.getCurrentPath();
					Vec3d vec3d = new Vec3d(this.entityMotionHelper.getTargetX(), this.entityMotionHelper.getTargetY(), this.entityMotionHelper.getTargetZ());
					if (path != null && path.getCurrentNode() < path.getNodeCount()) {
						vec3d = path.getCurrentPosition(this);
					}

					this.lookTowards(vec3d.x, vec3d.z);
					this.method_11080(this.field_11983);
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
		this.yaw = (float)(MathHelper.atan2(z - this.z, x - this.x) * 180.0 / (float) Math.PI) - 90.0F;
	}

	private void method_11086() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_11097(true);
	}

	private void method_11087() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_11097(false);
	}

	private void method_11088() {
		this.ticksUntilJump = this.method_11084();
	}

	private void method_11089() {
		this.method_11088();
		this.method_11087();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.jumpTicks != this.jumpDuration) {
			if (this.jumpTicks == 0 && !this.world.isClient) {
				this.world.sendEntityStatus(this, (byte)1);
			}

			this.jumpTicks++;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
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

	protected String getHopSound() {
		return "mob.rabbit.hop";
	}

	@Override
	protected String getAmbientSound() {
		return "mob.rabbit.idle";
	}

	@Override
	protected String getHurtSound() {
		return "mob.rabbit.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.rabbit.death";
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (this.getRabbitType() == 99) {
			this.playSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			return target.damage(DamageSource.mob(this), 8.0F);
		} else {
			return target.damage(DamageSource.mob(this), 3.0F);
		}
	}

	@Override
	public int getArmorProtectionValue() {
		return this.getRabbitType() == 99 ? 8 : super.getArmorProtectionValue();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	@Override
	protected void method_4473() {
		this.dropItem(new ItemStack(Items.RABBIT_FOOT, 1), 0.0F);
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(2) + this.random.nextInt(1 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			this.dropItem(Items.RABBIT_HIDE, 1);
		}

		i = this.random.nextInt(2);

		for (int k = 0; k < i; k++) {
			if (this.isOnFire()) {
				this.dropItem(Items.COOKED_RABBIT, 1);
			} else {
				this.dropItem(Items.RAW_RABBIT, 1);
			}
		}
	}

	private boolean method_11076(Item item) {
		return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Item.fromBlock(Blocks.YELLOW_FLOWER);
	}

	public RabbitEntity breed(PassiveEntity passiveEntity) {
		RabbitEntity rabbitEntity = new RabbitEntity(this.world);
		if (passiveEntity instanceof RabbitEntity) {
			rabbitEntity.setRabbitType(this.random.nextBoolean() ? this.getRabbitType() : ((RabbitEntity)passiveEntity).getRabbitType());
		}

		return rabbitEntity;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && this.method_11076(stack.getItem());
	}

	public int getRabbitType() {
		return this.dataTracker.getByte(18);
	}

	public void setRabbitType(int type) {
		if (type == 99) {
			this.goals.method_4497(this.fleeFromWolfGoal);
			this.goals.add(4, new RabbitEntity.RabbitAttackGoal(this));
			this.attackGoals.add(1, new RevengeGoal(this, false));
			this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
			this.attackGoals.add(2, new FollowTargetGoal(this, WolfEntity.class, true));
			if (!this.hasCustomName()) {
				this.setCustomName(CommonI18n.translate("entity.KillerBunny.name"));
			}
		}

		this.dataTracker.setProperty(18, (byte)type);
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		data = super.initialize(difficulty, data);
		int i = this.random.nextInt(6);
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

	private boolean wantsCarrots() {
		return this.moreCarrotTicks == 0;
	}

	protected int method_11084() {
		return this.field_11983.method_11095();
	}

	protected void method_11085() {
		this.world
			.addParticle(
				ParticleType.BLOCK_DUST,
				this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
				this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
				this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
				0.0,
				0.0,
				0.0,
				Block.getByBlockState(Blocks.CARROTS.stateFromData(7))
			);
		this.moreCarrotTicks = 100;
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

	static enum Action {
		NONE(0.0F, 0.0F, 30, 1),
		HOP(0.8F, 0.2F, 20, 10),
		STEP(1.0F, 0.45F, 14, 14),
		SPRINT(1.75F, 0.4F, 1, 8),
		ATTACK(2.0F, 0.7F, 7, 8);

		private final float speed;
		private final float field_11992;
		private final int field_11993;
		private final int field_11994;

		private Action(float f, float g, int j, int k) {
			this.speed = f;
			this.field_11992 = g;
			this.field_11993 = j;
			this.field_11994 = k;
		}

		public float getSpeed() {
			return this.speed;
		}

		public float method_11094() {
			return this.field_11992;
		}

		public int method_11095() {
			return this.field_11993;
		}

		public int getJumpDuration() {
			return this.field_11994;
		}
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
		private boolean hasTarget = false;

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
				if (this.hasTarget && block instanceof CarrotsBlock && (Integer)blockState.get(CarrotsBlock.AGE) == 7) {
					world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
					world.removeBlock(blockPos, true);
					this.rabbit.method_11085();
				}

				this.hasTarget = false;
				this.cooldown = 10;
			}
		}

		@Override
		protected boolean isTargetPos(World world, BlockPos pos) {
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.FARMLAND) {
				pos = pos.up();
				BlockState blockState = world.getBlockState(pos);
				block = blockState.getBlock();
				if (block instanceof CarrotsBlock && (Integer)blockState.get(CarrotsBlock.AGE) == 7 && this.wantsCarrots && !this.hasTarget) {
					this.hasTarget = true;
					return true;
				}
			}

			return false;
		}
	}

	static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
		private RabbitEntity rabbit;

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
		private RabbitEntity rabbit;

		public FleeGoal(RabbitEntity rabbitEntity, Class<T> class_, float f, double d, double e) {
			super(rabbitEntity, class_, f, d, e);
			this.rabbit = rabbitEntity;
		}

		@Override
		public void tick() {
			super.tick();
		}
	}

	static class RabbitAttackGoal extends MeleeAttackGoal {
		public RabbitAttackGoal(RabbitEntity rabbitEntity) {
			super(rabbitEntity, LivingEntity.class, 1.4, true);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.width);
		}
	}

	public class RabbitJumpControl extends JumpControl {
		private RabbitEntity rabbit;
		private boolean field_12000 = false;

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
				this.rabbit.method_11080(RabbitEntity.Action.STEP);
				this.active = false;
			}
		}
	}

	static class RabbitMoveControl extends MoveControl {
		private RabbitEntity rabbit;

		public RabbitMoveControl(RabbitEntity rabbitEntity) {
			super(rabbitEntity);
			this.rabbit = rabbitEntity;
		}

		@Override
		public void updateMovement() {
			if (this.rabbit.onGround && !this.rabbit.method_11081()) {
				this.rabbit.setSpeed(0.0);
			}

			super.updateMovement();
		}
	}
}
