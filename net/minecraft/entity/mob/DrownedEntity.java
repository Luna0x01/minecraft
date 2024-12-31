package net.minecraft.entity.mob;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.class_2974;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements RangedAttackMob {
	private boolean field_17027;
	protected final SwimNavigation field_17026;
	protected final MobNavigation field_17028;

	public DrownedEntity(World world) {
		super(EntityType.DROWNED, world);
		this.stepHeight = 1.0F;
		this.entityMotionHelper = new DrownedEntity.class_3513(this);
		this.method_13076(LandType.WATER, 0.0F);
		this.field_17026 = new SwimNavigation(this, world);
		this.field_17028 = new MobNavigation(this, world);
	}

	@Override
	protected void initCustomGoals() {
		this.goals.add(1, new DrownedEntity.class_3512(this, 1.0));
		this.goals.add(2, new DrownedEntity.class_3515(this, 1.0, 40, 10.0F));
		this.goals.add(2, new DrownedEntity.class_3509(this, 1.0, false));
		this.goals.add(5, new DrownedEntity.class_3511(this, 1.0));
		this.goals.add(6, new DrownedEntity.class_3514(this, 1.0, this.world.method_8483()));
		this.goals.add(7, new WanderAroundGoal(this, 1.0));
		this.attackGoals.add(1, new RevengeGoal(this, true, DrownedEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, new DrownedEntity.class_3510(this)));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, false));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.attackGoals.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.field_16957));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return super.createNavigation(world);
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		entityData = super.initialize(difficulty, entityData, nbt);
		if (this.getStack(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
			this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
			this.armorDropChances[EquipmentSlot.OFFHAND.method_13032()] = 2.0F;
		}

		return entityData;
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		Biome biome = iWorld.method_8577(new BlockPos(this.x, this.y, this.z));
		return biome != Biomes.RIVER && biome != Biomes.FROZEN_RIVER
			? this.random.nextInt(40) == 0 && this.method_15855() && super.method_15652(iWorld, bl)
			: this.random.nextInt(15) == 0 && super.method_15652(iWorld, bl);
	}

	private boolean method_15855() {
		return this.getBoundingBox().minY < (double)(this.world.method_8483() - 5);
	}

	@Override
	protected boolean method_15903() {
		return false;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.DROWNED_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return this.isTouchingWater() ? Sounds.ENTITY_DROWNED_AMBIENT_WATER : Sounds.ENTITY_DROWNED_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return this.isTouchingWater() ? Sounds.ENTITY_DROWNED_HURT_WATER : Sounds.ENTITY_DROWNED_HURT;
	}

	@Override
	protected Sound deathSound() {
		return this.isTouchingWater() ? Sounds.ENTITY_DROWNED_DEATH_WATER : Sounds.ENTITY_DROWNED_DEATH;
	}

	@Override
	protected Sound getStepSound() {
		return Sounds.ENTITY_DROWNED_STEP;
	}

	@Override
	protected Sound method_12984() {
		return Sounds.ENTITY_DROWNED_SWIM;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		if ((double)this.random.nextFloat() > 0.9) {
			int i = this.random.nextInt(16);
			if (i < 10) {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
			} else {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
			}
		}
	}

	@Override
	protected boolean method_15651(ItemStack itemStack, ItemStack itemStack2, EquipmentSlot equipmentSlot) {
		if (itemStack2.getItem() == Items.NAUTILUS_SHELL) {
			return false;
		} else if (itemStack2.getItem() == Items.TRIDENT) {
			return itemStack.getItem() == Items.TRIDENT ? itemStack.getDamage() < itemStack2.getDamage() : false;
		} else {
			return itemStack.getItem() == Items.TRIDENT ? true : super.method_15651(itemStack, itemStack2, equipmentSlot);
		}
	}

	@Override
	protected boolean method_15900() {
		return false;
	}

	@Override
	public boolean method_15653(RenderBlockView renderBlockView) {
		return renderBlockView.method_16382(this, this.getBoundingBox()) && renderBlockView.method_16387(this, this.getBoundingBox());
	}

	public boolean method_15857(@Nullable LivingEntity livingEntity) {
		return livingEntity != null ? !this.world.isDay() || livingEntity.isTouchingWater() : false;
	}

	@Override
	public boolean canFly() {
		return !this.method_15584();
	}

	private boolean method_15856() {
		if (this.field_17027) {
			return true;
		} else {
			LivingEntity livingEntity = this.getTarget();
			return livingEntity != null && livingEntity.isTouchingWater();
		}
	}

	@Override
	public void method_2657(float f, float g, float h) {
		if (this.canMoveVoluntarily() && this.isTouchingWater() && this.method_15856()) {
			this.method_2492(f, g, h, 0.01F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
		} else {
			super.method_2657(f, g, h);
		}
	}

	@Override
	public void method_15577() {
		if (!this.world.isClient) {
			if (this.canMoveVoluntarily() && this.isTouchingWater() && this.method_15856()) {
				this.navigation = this.field_17026;
				this.method_15590(true);
			} else {
				this.navigation = this.field_17028;
				this.method_15590(false);
			}
		}
	}

	protected boolean method_15854() {
		PathMinHeap pathMinHeap = this.getNavigation().method_13113();
		if (pathMinHeap != null) {
			PathNode pathNode = pathMinHeap.method_13399();
			if (pathNode != null) {
				double d = this.squaredDistanceTo((double)pathNode.posX, (double)pathNode.posY, (double)pathNode.posZ);
				if (d < 4.0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		TridentEntity tridentEntity = new TridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
		double d = target.x - this.x;
		double e = target.getBoundingBox().minY + (double)(target.height / 3.0F) - tridentEntity.y;
		double f = target.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		tridentEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.method_16346().getId() * 4));
		this.playSound(Sounds.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.method_3686(tridentEntity);
	}

	public void method_15852(boolean bl) {
		this.field_17027 = bl;
	}

	static class class_3509 extends class_2974 {
		private final DrownedEntity field_17029;

		public class_3509(DrownedEntity drownedEntity, double d, boolean bl) {
			super(drownedEntity, d, bl);
			this.field_17029 = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.field_17029.method_15857(this.field_17029.getTarget());
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.field_17029.method_15857(this.field_17029.getTarget());
		}
	}

	static class class_3510 implements Predicate<PlayerEntity> {
		private final DrownedEntity field_17030;

		public class_3510(DrownedEntity drownedEntity) {
			this.field_17030 = drownedEntity;
		}

		public boolean test(@Nullable PlayerEntity playerEntity) {
			return this.field_17030.method_15857(playerEntity);
		}
	}

	static class class_3511 extends MoveToTargetPosGoal {
		private final DrownedEntity field_17031;

		public class_3511(DrownedEntity drownedEntity, double d) {
			super(drownedEntity, d, 8, 2);
			this.field_17031 = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart()
				&& !this.field_17031.world.isDay()
				&& this.field_17031.isTouchingWater()
				&& this.field_17031.y >= (double)(this.field_17031.world.method_8483() - 3);
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue();
		}

		@Override
		protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
			BlockPos blockPos2 = blockPos.up();
			return renderBlockView.method_8579(blockPos2) && renderBlockView.method_8579(blockPos2.up())
				? renderBlockView.getBlockState(blockPos).method_16913()
				: false;
		}

		@Override
		public void start() {
			this.field_17031.method_15852(false);
			this.field_17031.navigation = this.field_17031.field_17028;
			super.start();
		}

		@Override
		public void stop() {
			super.stop();
		}
	}

	static class class_3512 extends Goal {
		private final PathAwareEntity field_17032;
		private double field_17033;
		private double field_17034;
		private double field_17035;
		private final double field_17036;
		private final World field_17037;

		public class_3512(PathAwareEntity pathAwareEntity, double d) {
			this.field_17032 = pathAwareEntity;
			this.field_17036 = d;
			this.field_17037 = pathAwareEntity.world;
			this.setCategoryBits(1);
		}

		@Override
		public boolean canStart() {
			if (!this.field_17037.isDay()) {
				return false;
			} else if (this.field_17032.isTouchingWater()) {
				return false;
			} else {
				Vec3d vec3d = this.method_15859();
				if (vec3d == null) {
					return false;
				} else {
					this.field_17033 = vec3d.x;
					this.field_17034 = vec3d.y;
					this.field_17035 = vec3d.z;
					return true;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_17032.getNavigation().isIdle();
		}

		@Override
		public void start() {
			this.field_17032.getNavigation().startMovingTo(this.field_17033, this.field_17034, this.field_17035, this.field_17036);
		}

		@Nullable
		private Vec3d method_15859() {
			Random random = this.field_17032.getRandom();
			BlockPos blockPos = new BlockPos(this.field_17032.x, this.field_17032.getBoundingBox().minY, this.field_17032.z);

			for (int i = 0; i < 10; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
				if (this.field_17037.getBlockState(blockPos2).getBlock() == Blocks.WATER) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}

			return null;
		}
	}

	static class class_3513 extends MoveControl {
		private final DrownedEntity field_17038;

		public class_3513(DrownedEntity drownedEntity) {
			super(drownedEntity);
			this.field_17038 = drownedEntity;
		}

		@Override
		public void updateMovement() {
			LivingEntity livingEntity = this.field_17038.getTarget();
			if (this.field_17038.method_15856() && this.field_17038.isTouchingWater()) {
				if (livingEntity != null && livingEntity.y > this.field_17038.y || this.field_17038.field_17027) {
					this.field_17038.velocityY += 0.002;
				}

				if (this.state != MoveControl.MoveStatus.MOVE_TO || this.field_17038.getNavigation().isIdle()) {
					this.field_17038.setMovementSpeed(0.0F);
					return;
				}

				double d = this.targetX - this.field_17038.x;
				double e = this.targetY - this.field_17038.y;
				double f = this.targetZ - this.field_17038.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.field_17038.yaw = this.wrapDegrees(this.field_17038.yaw, h, 90.0F);
				this.field_17038.bodyYaw = this.field_17038.yaw;
				float i = (float)(this.speed * this.field_17038.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				this.field_17038.setMovementSpeed(this.field_17038.getMovementSpeed() + (i - this.field_17038.getMovementSpeed()) * 0.125F);
				this.field_17038.velocityY = this.field_17038.velocityY + (double)this.field_17038.getMovementSpeed() * e * 0.1;
				this.field_17038.velocityX = this.field_17038.velocityX + (double)this.field_17038.getMovementSpeed() * d * 0.005;
				this.field_17038.velocityZ = this.field_17038.velocityZ + (double)this.field_17038.getMovementSpeed() * f * 0.005;
			} else {
				if (!this.field_17038.onGround) {
					this.field_17038.velocityY -= 0.008;
				}

				super.updateMovement();
			}
		}
	}

	static class class_3514 extends Goal {
		private final DrownedEntity field_17039;
		private final double field_17040;
		private final int field_17041;
		private boolean field_17042;

		public class_3514(DrownedEntity drownedEntity, double d, int i) {
			this.field_17039 = drownedEntity;
			this.field_17040 = d;
			this.field_17041 = i;
		}

		@Override
		public boolean canStart() {
			return !this.field_17039.world.isDay() && this.field_17039.isTouchingWater() && this.field_17039.y < (double)(this.field_17041 - 2);
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart() && !this.field_17042;
		}

		@Override
		public void tick() {
			if (this.field_17039.y < (double)(this.field_17041 - 1) && (this.field_17039.getNavigation().isIdle() || this.field_17039.method_15854())) {
				Vec3d vec3d = RandomVectorGenerator.method_2800(this.field_17039, 4, 8, new Vec3d(this.field_17039.x, (double)(this.field_17041 - 1), this.field_17039.z));
				if (vec3d == null) {
					this.field_17042 = true;
					return;
				}

				this.field_17039.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_17040);
			}
		}

		@Override
		public void start() {
			this.field_17039.method_15852(true);
			this.field_17042 = false;
		}

		@Override
		public void stop() {
			this.field_17039.method_15852(false);
		}
	}

	static class class_3515 extends ProjectileAttackGoal {
		private final DrownedEntity field_17043;

		public class_3515(RangedAttackMob rangedAttackMob, double d, int i, float f) {
			super(rangedAttackMob, d, i, f);
			this.field_17043 = (DrownedEntity)rangedAttackMob;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.field_17043.getMainHandStack().getItem() == Items.TRIDENT;
		}

		@Override
		public void start() {
			super.start();
			this.field_17043.method_13246(true);
		}

		@Override
		public void stop() {
			super.stop();
			this.field_17043.method_13246(false);
		}
	}
}
