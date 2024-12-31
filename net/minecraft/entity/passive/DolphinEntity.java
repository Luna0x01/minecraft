package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3465;
import net.minecraft.class_3468;
import net.minecraft.class_3469;
import net.minecraft.class_3471;
import net.minecraft.class_3473;
import net.minecraft.class_3475;
import net.minecraft.class_4342;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class DolphinEntity extends WaterCreatureEntity {
	private static final TrackedData<BlockPos> field_16902 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_16903 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_16901 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Predicate<ItemEntity> field_16900 = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive() && itemEntity.isTouchingWater();

	public DolphinEntity(World world) {
		super(EntityType.DOLPHIN, world);
		this.setBounds(0.9F, 0.6F);
		this.entityMotionHelper = new DolphinEntity.class_3483(this);
		this.lookControl = new class_3465(this, 10);
		this.setCanPickUpLoot(true);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.setAir(this.method_15585());
		this.pitch = 0.0F;
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	public boolean method_2607() {
		return false;
	}

	@Override
	protected void method_15826(int i) {
	}

	public void method_15751(BlockPos blockPos) {
		this.dataTracker.set(field_16902, blockPos);
	}

	public BlockPos method_15752() {
		return this.dataTracker.get(field_16902);
	}

	public boolean method_15748() {
		return this.dataTracker.get(field_16903);
	}

	public void method_15744(boolean bl) {
		this.dataTracker.set(field_16903, bl);
	}

	public int method_15749() {
		return this.dataTracker.get(field_16901);
	}

	public void method_15745(int i) {
		this.dataTracker.set(field_16901, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_16902, BlockPos.ORIGIN);
		this.dataTracker.startTracking(field_16903, false);
		this.dataTracker.startTracking(field_16901, 2400);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("TreasurePosX", this.method_15752().getX());
		nbt.putInt("TreasurePosY", this.method_15752().getY());
		nbt.putInt("TreasurePosZ", this.method_15752().getZ());
		nbt.putBoolean("GotFish", this.method_15748());
		nbt.putInt("Moistness", this.method_15749());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		int i = nbt.getInt("TreasurePosX");
		int j = nbt.getInt("TreasurePosY");
		int k = nbt.getInt("TreasurePosZ");
		this.method_15751(new BlockPos(i, j, k));
		super.readCustomDataFromNbt(nbt);
		this.method_15744(nbt.getBoolean("GotFish"));
		this.method_15745(nbt.getInt("Moistness"));
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new class_3468(this));
		this.goals.add(0, new class_3475(this));
		this.goals.add(1, new DolphinEntity.class_3484(this));
		this.goals.add(2, new DolphinEntity.class_3485(this, 4.0));
		this.goals.add(4, new class_3473(this, 1.0, 10));
		this.goals.add(4, new LookAroundGoal(this));
		this.goals.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(5, new class_3471(this, 10));
		this.goals.add(6, new MeleeAttackGoal(this, 1.2F, true));
		this.goals.add(8, new DolphinEntity.class_3486());
		this.goals.add(8, new class_3469(this));
		this.goals.add(9, new FleeEntityGoal(this, GuardianEntity.class, 8.0F, 1.0, 1.0));
		this.attackGoals.add(1, new RevengeGoal(this, true, GuardianEntity.class));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(1.2F);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, target);
			this.playSound(Sounds.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public int method_15585() {
		return 4800;
	}

	@Override
	protected int method_15648(int i) {
		return this.method_15585();
	}

	@Override
	public float getEyeHeight() {
		return 0.3F;
	}

	@Override
	public int getLookPitchSpeed() {
		return 1;
	}

	@Override
	public int method_13081() {
		return 1;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return true;
	}

	@Override
	protected void loot(ItemEntity item) {
		if (this.getStack(EquipmentSlot.MAINHAND).isEmpty()) {
			ItemStack itemStack = item.getItemStack();
			if (this.canPickupItem(itemStack)) {
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
				this.armorDropChances[EquipmentSlot.MAINHAND.method_13032()] = 2.0F;
				this.sendPickup(item, itemStack.getCount());
				item.remove();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.hasNoAi()) {
			if (this.method_15574()) {
				this.method_15745(2400);
			} else {
				this.method_15745(this.method_15749() - 1);
				if (this.method_15749() <= 0) {
					this.damage(DamageSource.DRYOUT, 1.0F);
				}

				if (this.onGround) {
					this.velocityY += 0.5;
					this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
					this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
					this.yaw = this.random.nextFloat() * 360.0F;
					this.onGround = false;
					this.velocityDirty = true;
				}
			}

			if (this.world.isClient
				&& this.isTouchingWater()
				&& this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ > 0.03) {
				Vec3d vec3d = this.getRotationVector(0.0F);
				float f = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float g = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.random.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.world
						.method_16343(class_4342.field_21374, this.x - vec3d.x * (double)h + (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h + (double)g, 0.0, 0.0, 0.0);
					this.world
						.method_16343(class_4342.field_21374, this.x - vec3d.x * (double)h - (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h - (double)g, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 38) {
			this.method_15743(class_4342.field_21400);
		} else {
			super.handleStatus(status);
		}
	}

	private void method_15743(ParticleEffect particleEffect) {
		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.01;
			double e = this.random.nextGaussian() * 0.01;
			double f = this.random.nextGaussian() * 0.01;
			this.world
				.method_16343(
					particleEffect,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 0.2F + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!itemStack.isEmpty() && itemStack.getItem().method_16075(ItemTags.FISHES)) {
			if (!this.world.isClient) {
				this.playSound(Sounds.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
			}

			this.method_15744(true);
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Nullable
	public ItemEntity method_15750(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return null;
		} else {
			double d = this.y - 0.3F + (double)this.getEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, itemStack);
			itemEntity.setPickupDelay(40);
			itemEntity.method_15848(this.getUuid());
			float f = 0.3F;
			itemEntity.velocityX = (double)(-MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
			itemEntity.velocityY = (double)(MathHelper.sin(this.pitch * (float) (Math.PI / 180.0)) * f * 1.5F);
			itemEntity.velocityZ = (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(this.pitch * (float) (Math.PI / 180.0)) * f);
			float g = this.random.nextFloat() * (float) (Math.PI * 2);
			f = 0.02F * this.random.nextFloat();
			itemEntity.velocityX = itemEntity.velocityX + (double)(MathHelper.cos(g) * f);
			itemEntity.velocityZ = itemEntity.velocityZ + (double)(MathHelper.sin(g) * f);
			this.world.method_3686(itemEntity);
			return itemEntity;
		}
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return this.y > 45.0 && this.y < (double)iWorld.method_8483() && iWorld.method_8577(new BlockPos(this)) != Biomes.OCEAN
			|| iWorld.method_8577(new BlockPos(this)) != Biomes.DEEP_OCEAN && super.method_15652(iWorld, bl);
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_DOLPHIN_HURT;
	}

	@Nullable
	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_DOLPHIN_DEATH;
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		return this.isTouchingWater() ? Sounds.ENTITY_DOLPHIN_AMBIENT_WATER : Sounds.ENTITY_DOLPHIN_AMBIENT;
	}

	@Override
	protected Sound method_12985() {
		return Sounds.ENTITY_DOLPHIN_SPLASH;
	}

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_DOLPHIN_SWIM;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.DOLPHIN_ENTITIE;
	}

	protected boolean method_15747() {
		BlockPos blockPos = this.getNavigation().method_15710();
		return blockPos != null ? this.squaredDistanceTo(blockPos) < 144.0 : false;
	}

	@Override
	public void method_2657(float f, float g, float h) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.method_2492(f, g, h, this.getMovementSpeed());
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
	public boolean method_2537(PlayerEntity playerEntity) {
		return true;
	}

	static class class_3483 extends MoveControl {
		private final DolphinEntity field_16904;

		public class_3483(DolphinEntity dolphinEntity) {
			super(dolphinEntity);
			this.field_16904 = dolphinEntity;
		}

		@Override
		public void updateMovement() {
			if (this.field_16904.isTouchingWater()) {
				this.field_16904.velocityY += 0.005;
			}

			if (this.state == MoveControl.MoveStatus.MOVE_TO && !this.field_16904.getNavigation().isIdle()) {
				double d = this.targetX - this.field_16904.x;
				double e = this.targetY - this.field_16904.y;
				double f = this.targetZ - this.field_16904.z;
				double g = d * d + e * e + f * f;
				if (g < 2.5000003E-7F) {
					this.entity.method_15061(0.0F);
				} else {
					float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
					this.field_16904.yaw = this.wrapDegrees(this.field_16904.yaw, h, 10.0F);
					this.field_16904.bodyYaw = this.field_16904.yaw;
					this.field_16904.headYaw = this.field_16904.yaw;
					float i = (float)(this.speed * this.field_16904.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
					if (this.field_16904.isTouchingWater()) {
						this.field_16904.setMovementSpeed(i * 0.02F);
						float j = -((float)(MathHelper.atan2(e, (double)MathHelper.sqrt(d * d + f * f)) * 180.0F / (float)Math.PI));
						j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0F, 85.0F);
						this.field_16904.pitch = this.wrapDegrees(this.field_16904.pitch, j, 5.0F);
						float k = MathHelper.cos(this.field_16904.pitch * (float) (Math.PI / 180.0));
						float l = MathHelper.sin(this.field_16904.pitch * (float) (Math.PI / 180.0));
						this.field_16904.field_16513 = k * i;
						this.field_16904.forwardSpeed = -l * i;
					} else {
						this.field_16904.setMovementSpeed(i * 0.1F);
					}
				}
			} else {
				this.field_16904.setMovementSpeed(0.0F);
				this.field_16904.method_13086(0.0F);
				this.field_16904.setForwardSpeed(0.0F);
				this.field_16904.method_15061(0.0F);
			}
		}
	}

	static class class_3484 extends Goal {
		private final DolphinEntity field_16905;
		private boolean field_16906;

		class_3484(DolphinEntity dolphinEntity) {
			this.field_16905 = dolphinEntity;
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public boolean canStart() {
			return this.field_16905.method_15748() && this.field_16905.getAir() >= 100;
		}

		@Override
		public boolean shouldContinue() {
			BlockPos blockPos = this.field_16905.method_15752();
			return this.field_16905.squaredDistanceTo(new BlockPos((double)blockPos.getX(), this.field_16905.y, (double)blockPos.getZ())) > 16.0
				&& !this.field_16906
				&& this.field_16905.getAir() >= 100;
		}

		@Override
		public void start() {
			this.field_16906 = false;
			this.field_16905.getNavigation().stop();
			World world = this.field_16905.world;
			BlockPos blockPos = new BlockPos(this.field_16905);
			String string = (double)world.random.nextFloat() >= 0.5 ? "Ocean_Ruin" : "Shipwreck";
			BlockPos blockPos2 = world.method_13688(string, blockPos, 50, false);
			if (blockPos2 == null) {
				BlockPos blockPos3 = world.method_13688(string.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockPos, 50, false);
				if (blockPos3 == null) {
					this.field_16906 = true;
					return;
				}

				this.field_16905.method_15751(blockPos3);
			} else {
				this.field_16905.method_15751(blockPos2);
			}

			world.sendEntityStatus(this.field_16905, (byte)38);
		}

		@Override
		public void stop() {
			BlockPos blockPos = this.field_16905.method_15752();
			if (this.field_16905.squaredDistanceTo(new BlockPos((double)blockPos.getX(), this.field_16905.y, (double)blockPos.getZ())) <= 16.0 || this.field_16906) {
				this.field_16905.method_15744(false);
			}
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_16905.method_15752();
			World world = this.field_16905.world;
			if (this.field_16905.method_15747() || this.field_16905.getNavigation().isIdle()) {
				Vec3d vec3d = RandomVectorGenerator.method_15715(
					this.field_16905, 16, 1, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 8)
				);
				if (vec3d == null) {
					vec3d = RandomVectorGenerator.method_2800(this.field_16905, 8, 4, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					BlockPos blockPos2 = new BlockPos(vec3d);
					if (!world.getFluidState(blockPos2).matches(FluidTags.WATER)
						|| !world.getBlockState(blockPos2).canPlaceAtSide(world, blockPos2, BlockPlacementEnvironment.WATER)) {
						vec3d = RandomVectorGenerator.method_2800(this.field_16905, 8, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
					}
				}

				if (vec3d == null) {
					this.field_16906 = true;
					return;
				}

				this.field_16905
					.getLookControl()
					.lookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.field_16905.method_13081() + 20), (float)this.field_16905.getLookPitchSpeed());
				this.field_16905.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.3);
				if (world.random.nextInt(80) == 0) {
					world.sendEntityStatus(this.field_16905, (byte)38);
				}
			}
		}
	}

	static class class_3485 extends Goal {
		private final DolphinEntity field_16907;
		private final double field_16908;
		private PlayerEntity field_16909;

		class_3485(DolphinEntity dolphinEntity, double d) {
			this.field_16907 = dolphinEntity;
			this.field_16908 = d;
			this.setCategoryBits(3);
		}

		@Override
		public boolean canStart() {
			this.field_16909 = this.field_16907.world.method_16364(this.field_16907, 10.0);
			return this.field_16909 == null ? false : this.field_16909.method_15584();
		}

		@Override
		public boolean shouldContinue() {
			return this.field_16909 != null && this.field_16909.method_15584() && this.field_16907.squaredDistanceTo(this.field_16909) < 256.0;
		}

		@Override
		public void start() {
			this.field_16909.method_2654(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
		}

		@Override
		public void stop() {
			this.field_16909 = null;
			this.field_16907.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.field_16907.getLookControl().lookAt(this.field_16909, (float)(this.field_16907.method_13081() + 20), (float)this.field_16907.getLookPitchSpeed());
			if (this.field_16907.squaredDistanceTo(this.field_16909) < 6.25) {
				this.field_16907.getNavigation().stop();
			} else {
				this.field_16907.getNavigation().startMovingTo(this.field_16909, this.field_16908);
			}

			if (this.field_16909.method_15584() && this.field_16909.world.random.nextInt(6) == 0) {
				this.field_16909.method_2654(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
			}
		}
	}

	class class_3486 extends Goal {
		private int field_16911;

		private class_3486() {
		}

		@Override
		public boolean canStart() {
			if (this.field_16911 > DolphinEntity.this.ticksAlive) {
				return false;
			} else {
				List<ItemEntity> list = DolphinEntity.this.world
					.method_16325(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_16900);
				return !list.isEmpty() || !DolphinEntity.this.getStack(EquipmentSlot.MAINHAND).isEmpty();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = DolphinEntity.this.world
				.method_16325(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_16900);
			if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				DolphinEntity.this.playSound(Sounds.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
			}

			this.field_16911 = 0;
		}

		@Override
		public void stop() {
			ItemStack itemStack = DolphinEntity.this.getStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				DolphinEntity.this.method_15750(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				this.field_16911 = DolphinEntity.this.ticksAlive + DolphinEntity.this.random.nextInt(100);
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = DolphinEntity.this.world
				.method_16325(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_16900);
			ItemStack itemStack = DolphinEntity.this.getStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				DolphinEntity.this.method_15750(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			} else if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}
	}
}
