package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FindNearestEntityGoal;
import net.minecraft.entity.ai.goal.FindPlayerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelGeneratorType;

public class SlimeEntity extends MobEntity implements Monster {
	private static final TrackedData<Integer> field_14781 = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public float targetStretch;
	public float stretch;
	public float lastStretch;
	private boolean wasOnGround;

	public SlimeEntity(World world) {
		super(world);
		this.entityMotionHelper = new SlimeEntity.SlimeMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SlimeEntity.SwimmingGoal(this));
		this.goals.add(2, new SlimeEntity.FaceTowardTargetGoal(this));
		this.goals.add(3, new SlimeEntity.RandomLookGoal(this));
		this.goals.add(5, new SlimeEntity.MoveGoal(this));
		this.attackGoals.add(1, new FindPlayerGoal(this));
		this.attackGoals.add(3, new FindNearestEntityGoal(this, IronGolemEntity.class));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14781, 1);
	}

	protected void method_3089(int i, boolean bl) {
		this.dataTracker.set(field_14781, i);
		this.setBounds(0.51000005F * (float)i, 0.51000005F * (float)i);
		this.updatePosition(this.x, this.y, this.z);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)(i * i));
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
		if (bl) {
			this.setHealth(this.getMaxHealth());
		}

		this.experiencePoints = i;
	}

	public int getSize() {
		return this.dataTracker.get(field_14781);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, SlimeEntity.class);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Size", this.getSize() - 1);
		nbt.putBoolean("wasOnGround", this.wasOnGround);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		int i = nbt.getInt("Size");
		if (i < 0) {
			i = 0;
		}

		this.method_3089(i + 1, false);
		this.wasOnGround = nbt.getBoolean("wasOnGround");
	}

	public boolean method_13242() {
		return this.getSize() <= 1;
	}

	protected ParticleType getParticles() {
		return ParticleType.SLIME;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.world.getGlobalDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
			this.removed = true;
		}

		this.stretch = this.stretch + (this.targetStretch - this.stretch) * 0.5F;
		this.lastStretch = this.stretch;
		super.tick();
		if (this.onGround && !this.wasOnGround) {
			int i = this.getSize();

			for (int j = 0; j < i * 8; j++) {
				float f = this.random.nextFloat() * (float) (Math.PI * 2);
				float g = this.random.nextFloat() * 0.5F + 0.5F;
				float h = MathHelper.sin(f) * (float)i * 0.5F * g;
				float k = MathHelper.cos(f) * (float)i * 0.5F * g;
				World var10000 = this.world;
				ParticleType var10001 = this.getParticles();
				double var10002 = this.x + (double)h;
				double var10004 = this.z + (double)k;
				var10000.addParticle(var10001, var10002, this.getBoundingBox().minY, var10004, 0.0, 0.0, 0.0);
			}

			this.playSound(this.method_13240(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.targetStretch = -0.5F;
		} else if (!this.onGround && this.wasOnGround) {
			this.targetStretch = 1.0F;
		}

		this.wasOnGround = this.onGround;
		this.updateStretch();
	}

	protected void updateStretch() {
		this.targetStretch *= 0.6F;
	}

	protected int getTicksUntilNextJump() {
		return this.random.nextInt(20) + 10;
	}

	protected SlimeEntity method_3091() {
		return new SlimeEntity(this.world);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14781.equals(data)) {
			int i = this.getSize();
			this.setBounds(0.51000005F * (float)i, 0.51000005F * (float)i);
			this.yaw = this.headYaw;
			this.bodyYaw = this.headYaw;
			if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
				this.onSwimmingStart();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void remove() {
		int i = this.getSize();
		if (!this.world.isClient && i > 1 && this.getHealth() <= 0.0F) {
			int j = 2 + this.random.nextInt(3);

			for (int k = 0; k < j; k++) {
				float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
				float g = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
				SlimeEntity slimeEntity = this.method_3091();
				if (this.hasCustomName()) {
					slimeEntity.setCustomName(this.getCustomName());
				}

				if (this.isPersistent()) {
					slimeEntity.setPersistent();
				}

				slimeEntity.method_3089(i / 2, true);
				slimeEntity.refreshPositionAndAngles(this.x + (double)f, this.y + 0.5, this.z + (double)g, this.random.nextFloat() * 360.0F, 0.0F);
				this.world.spawnEntity(slimeEntity);
			}
		}

		super.remove();
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		super.pushAwayFrom(entity);
		if (entity instanceof IronGolemEntity && this.isBig()) {
			this.method_11213((LivingEntity)entity);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.isBig()) {
			this.method_11213(player);
		}
	}

	protected void method_11213(LivingEntity livingEntity) {
		int i = this.getSize();
		if (this.canSee(livingEntity)
			&& this.squaredDistanceTo(livingEntity) < 0.6 * (double)i * 0.6 * (double)i
			&& livingEntity.damage(DamageSource.mob(this), (float)this.getDamageAmount())) {
			this.playSound(Sounds.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dealDamage(this, livingEntity);
		}
	}

	@Override
	public float getEyeHeight() {
		return 0.625F * this.height;
	}

	protected boolean isBig() {
		return !this.method_13242();
	}

	protected int getDamageAmount() {
		return this.getSize();
	}

	@Override
	protected Sound method_13048() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_SLIME_HURT : Sounds.ENTITY_SLIME_HURT;
	}

	@Override
	protected Sound deathSound() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_SLIME_DEATH : Sounds.ENTITY_SLIME_DEATH;
	}

	protected Sound method_13240() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_SLIME_SQUISH : Sounds.ENTITY_SLIME_SQUISH;
	}

	@Override
	protected Item getDefaultDrop() {
		return this.getSize() == 1 ? Items.SLIME_BALL : null;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return this.getSize() == 1 ? LootTables.SLIME_ENTITIE : LootTables.EMPTY;
	}

	@Override
	public boolean canSpawn() {
		BlockPos blockPos = new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		Chunk chunk = this.world.getChunk(blockPos);
		if (this.world.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT && this.random.nextInt(4) != 1) {
			return false;
		} else {
			if (this.world.getGlobalDifficulty() != Difficulty.PEACEFUL) {
				Biome biome = this.world.getBiome(blockPos);
				if (biome == Biomes.SWAMP
					&& this.y > 50.0
					&& this.y < 70.0
					&& this.random.nextFloat() < 0.5F
					&& this.random.nextFloat() < this.world.getMoonSize()
					&& this.world.getLightLevelWithNeighbours(new BlockPos(this)) <= this.random.nextInt(8)) {
					return super.canSpawn();
				}

				if (this.random.nextInt(10) == 0 && chunk.getRandom(987234911L).nextInt(10) == 0 && this.y < 40.0) {
					return super.canSpawn();
				}
			}

			return false;
		}
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F * (float)this.getSize();
	}

	@Override
	public int getLookPitchSpeed() {
		return 0;
	}

	protected boolean makesJumpSound() {
		return this.getSize() > 0;
	}

	@Override
	protected void jump() {
		this.velocityY = 0.42F;
		this.velocityDirty = true;
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		int i = this.random.nextInt(3);
		if (i < 2 && this.random.nextFloat() < 0.5F * difficulty.getClampedLocalDifficulty()) {
			i++;
		}

		int j = 1 << i;
		this.method_3089(j, true);
		return super.initialize(difficulty, data);
	}

	protected Sound method_13241() {
		return this.method_13242() ? Sounds.ENTITY_SMALL_SLIME_JUMP : Sounds.ENTITY_SLIME_JUMP;
	}

	static class FaceTowardTargetGoal extends Goal {
		private final SlimeEntity slime;
		private int ticksLeft;

		public FaceTowardTargetGoal(SlimeEntity slimeEntity) {
			this.slime = slimeEntity;
			this.setCategoryBits(2);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else {
				return !livingEntity.isAlive() ? false : !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).abilities.invulnerable;
			}
		}

		@Override
		public void start() {
			this.ticksLeft = 300;
			super.start();
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable ? false : --this.ticksLeft > 0;
			}
		}

		@Override
		public void tick() {
			this.slime.lookAtEntity(this.slime.getTarget(), 10.0F, 10.0F);
			((SlimeEntity.SlimeMoveControl)this.slime.getMotionHelper()).look(this.slime.yaw, this.slime.isBig());
		}
	}

	static class MoveGoal extends Goal {
		private final SlimeEntity slime;

		public MoveGoal(SlimeEntity slimeEntity) {
			this.slime = slimeEntity;
			this.setCategoryBits(5);
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			((SlimeEntity.SlimeMoveControl)this.slime.getMotionHelper()).move(1.0);
		}
	}

	static class RandomLookGoal extends Goal {
		private final SlimeEntity slime;
		private float targetYaw;
		private int timer;

		public RandomLookGoal(SlimeEntity slimeEntity) {
			this.slime = slimeEntity;
			this.setCategoryBits(2);
		}

		@Override
		public boolean canStart() {
			return this.slime.getTarget() == null
				&& (this.slime.onGround || this.slime.isTouchingWater() || this.slime.isTouchingLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION));
		}

		@Override
		public void tick() {
			if (--this.timer <= 0) {
				this.timer = 40 + this.slime.getRandom().nextInt(60);
				this.targetYaw = (float)this.slime.getRandom().nextInt(360);
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMotionHelper()).look(this.targetYaw, false);
		}
	}

	static class SlimeMoveControl extends MoveControl {
		private float targetYaw;
		private int ticksUntilJump;
		private final SlimeEntity slime;
		private boolean jumpOften;

		public SlimeMoveControl(SlimeEntity slimeEntity) {
			super(slimeEntity);
			this.slime = slimeEntity;
			this.targetYaw = 180.0F * slimeEntity.yaw / (float) Math.PI;
		}

		public void look(float targetYaw, boolean jumpOften) {
			this.targetYaw = targetYaw;
			this.jumpOften = jumpOften;
		}

		public void move(double speed) {
			this.speed = speed;
			this.state = MoveControl.MoveStatus.MOVE_TO;
		}

		@Override
		public void updateMovement() {
			this.entity.yaw = this.wrapDegrees(this.entity.yaw, this.targetYaw, 90.0F);
			this.entity.headYaw = this.entity.yaw;
			this.entity.bodyYaw = this.entity.yaw;
			if (this.state != MoveControl.MoveStatus.MOVE_TO) {
				this.entity.setForwardSpeed(0.0F);
			} else {
				this.state = MoveControl.MoveStatus.WAIT;
				if (this.entity.onGround) {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
					if (this.ticksUntilJump-- <= 0) {
						this.ticksUntilJump = this.slime.getTicksUntilNextJump();
						if (this.jumpOften) {
							this.ticksUntilJump /= 3;
						}

						this.slime.getJumpControl().setActive();
						if (this.slime.makesJumpSound()) {
							this.slime
								.playSound(
									this.slime.method_13241(),
									this.slime.getSoundVolume(),
									((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F
								);
						}
					} else {
						this.slime.sidewaysSpeed = 0.0F;
						this.slime.forwardSpeed = 0.0F;
						this.entity.setMovementSpeed(0.0F);
					}
				} else {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue()));
				}
			}
		}
	}

	static class SwimmingGoal extends Goal {
		private final SlimeEntity slime;

		public SwimmingGoal(SlimeEntity slimeEntity) {
			this.slime = slimeEntity;
			this.setCategoryBits(5);
			((MobNavigation)slimeEntity.getNavigation()).setCanSwim(true);
		}

		@Override
		public boolean canStart() {
			return this.slime.isTouchingWater() || this.slime.isTouchingLava();
		}

		@Override
		public void tick() {
			if (this.slime.getRandom().nextFloat() < 0.8F) {
				this.slime.getJumpControl().setActive();
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMotionHelper()).move(1.2);
		}
	}
}
