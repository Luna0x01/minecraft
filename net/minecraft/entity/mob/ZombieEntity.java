package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.class_3462;
import net.minecraft.class_3474;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.class_2974;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieEntity extends HostileEntity {
	protected static final EntityAttribute REINFORCEMENTS_ATTRIBUTE = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
		.setName("Spawn Reinforcements Chance");
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier BABY_SPEED_BOOST_MODIFIER = new AttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, 1);
	private static final TrackedData<Boolean> field_14785 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_14786 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_14788 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_17071 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this);
	private boolean canBreakDoors;
	private int field_17072;
	private int field_17073;
	private float boundWidth = -1.0F;
	private float boundHeight;

	public ZombieEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setBounds(0.6F, 1.95F);
	}

	public ZombieEntity(World world) {
		this(EntityType.ZOMBIE, world);
	}

	@Override
	protected void initGoals() {
		this.goals.add(4, new ZombieEntity.class_3527(Blocks.TURTLE_EGG, this, 1.0, 3));
		this.goals.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.initCustomGoals();
	}

	protected void initCustomGoals() {
		this.goals.add(2, new class_2974(this, 1.0, false));
		this.goals.add(6, new MoveThroughVillageGoal(this, 1.0, false));
		this.goals.add(7, new class_3133(this, 1.0));
		this.attackGoals.add(1, new RevengeGoal(this, true, ZombiePigmanEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, false));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.attackGoals.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.field_16957));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(35.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0);
		this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).setBaseValue(2.0);
		this.getAttributeContainer().register(REINFORCEMENTS_ATTRIBUTE).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(field_14785, false);
		this.getDataTracker().startTracking(field_14786, 0);
		this.getDataTracker().startTracking(field_14788, false);
		this.getDataTracker().startTracking(field_17071, false);
	}

	public boolean method_15902() {
		return this.getDataTracker().get(field_17071);
	}

	public void method_13246(boolean bl) {
		this.getDataTracker().set(field_14788, bl);
	}

	public boolean method_13247() {
		return this.getDataTracker().get(field_14788);
	}

	public boolean canBreakDoors() {
		return this.canBreakDoors;
	}

	public void setCanBreakDoors(boolean canBreakDoors) {
		if (this.method_15903()) {
			if (this.canBreakDoors != canBreakDoors) {
				this.canBreakDoors = canBreakDoors;
				((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
				if (canBreakDoors) {
					this.goals.add(1, this.breakDoorsGoal);
				} else {
					this.goals.method_4497(this.breakDoorsGoal);
				}
			}
		} else if (this.canBreakDoors) {
			this.goals.method_4497(this.breakDoorsGoal);
			this.canBreakDoors = false;
		}
	}

	protected boolean method_15903() {
		return true;
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(field_14785);
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.isBaby()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.getXpToDrop(player);
	}

	public void setBaby(boolean baby) {
		this.getDataTracker().set(field_14785, baby);
		if (this.world != null && !this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.method_6193(BABY_SPEED_BOOST_MODIFIER);
			if (baby) {
				entityAttributeInstance.addModifier(BABY_SPEED_BOOST_MODIFIER);
			}
		}

		this.changeType(baby);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14785.equals(data)) {
			this.changeType(this.isBaby());
		}

		super.onTrackedDataSet(data);
	}

	protected boolean method_15900() {
		return true;
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			if (this.method_15902()) {
				this.field_17073--;
				if (this.field_17073 < 0) {
					this.method_15901();
				}
			} else if (this.method_15900()) {
				if (this.method_15567(FluidTags.WATER)) {
					this.field_17072++;
					if (this.field_17072 >= 600) {
						this.method_15896(300);
					}
				} else {
					this.field_17072 = -1;
				}
			}
		}

		super.tick();
	}

	@Override
	public void tickMovement() {
		boolean bl = this.method_13605() && this.method_15656();
		if (bl) {
			ItemStack itemStack = this.getStack(EquipmentSlot.HEAD);
			if (!itemStack.isEmpty()) {
				if (itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
						this.method_6111(itemStack);
						this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
					}
				}

				bl = false;
			}

			if (bl) {
				this.setOnFireFor(8);
			}
		}

		super.tickMovement();
	}

	private void method_15896(int i) {
		this.field_17073 = i;
		this.getDataTracker().set(field_17071, true);
	}

	protected void method_15901() {
		this.method_15897(new DrownedEntity(this.world));
		this.world.syncWorldEvent(null, 1040, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected void method_15897(ZombieEntity zombieEntity) {
		if (!this.world.isClient && !this.removed) {
			zombieEntity.copyPosition(this);
			zombieEntity.method_15898(this.canPickUpLoot(), this.canBreakDoors(), this.isBaby(), this.hasNoAi());

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack = this.getStack(equipmentSlot);
				if (!itemStack.isEmpty()) {
					zombieEntity.equipStack(equipmentSlot, itemStack);
					zombieEntity.method_13077(equipmentSlot, this.method_15655(equipmentSlot));
				}
			}

			if (this.hasCustomName()) {
				zombieEntity.method_15578(this.method_15541());
				zombieEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.method_3686(zombieEntity);
			this.remove();
		}
	}

	protected boolean method_13605() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (super.damage(source, amount)) {
			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
				livingEntity = (LivingEntity)source.getAttacker();
			}

			if (livingEntity != null
				&& this.world.method_16346() == Difficulty.HARD
				&& (double)this.random.nextFloat() < this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).getValue()
				&& this.world.getGameRules().getBoolean("doMobSpawning")) {
				int i = MathHelper.floor(this.x);
				int j = MathHelper.floor(this.y);
				int k = MathHelper.floor(this.z);
				ZombieEntity zombieEntity = new ZombieEntity(this.world);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					if (this.world.getBlockState(new BlockPos(m, n - 1, o)).method_16913() && this.world.method_16358(new BlockPos(m, n, o)) < 10) {
						zombieEntity.updatePosition((double)m, (double)n, (double)o);
						if (!this.world.isPlayerInRange((double)m, (double)n, (double)o, 7.0)
							&& this.world.method_16382(zombieEntity, zombieEntity.getBoundingBox())
							&& this.world.method_16387(zombieEntity, zombieEntity.getBoundingBox())
							&& !this.world.method_16388(zombieEntity.getBoundingBox())) {
							this.world.method_3686(zombieEntity);
							zombieEntity.setTarget(livingEntity);
							zombieEntity.initialize(this.world.method_8482(new BlockPos(zombieEntity)), null, null);
							this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
							zombieEntity.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
							break;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			float f = this.world.method_8482(new BlockPos(this)).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				target.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ZOMBIE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ZOMBIE_DEATH;
	}

	protected Sound getStepSound() {
		return Sounds.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16819;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ZOMBIE_ENTITIE;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		super.initEquipment(difficulty);
		if (this.random.nextFloat() < (this.world.method_16346() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
			} else {
				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}

		nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
		nbt.putInt("InWaterTime", this.isTouchingWater() ? this.field_17072 : -1);
		nbt.putInt("DrownedConversionTime", this.method_15902() ? this.field_17073 : -1);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.getBoolean("IsBaby")) {
			this.setBaby(true);
		}

		this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
		this.field_17072 = nbt.getInt("InWaterTime");
		if (nbt.contains("DrownedConversionTime", 99) && nbt.getInt("DrownedConversionTime") > -1) {
			this.method_15896(nbt.getInt("DrownedConversionTime"));
		}
	}

	@Override
	public void onKilledOther(LivingEntity other) {
		super.onKilledOther(other);
		if ((this.world.method_16346() == Difficulty.NORMAL || this.world.method_16346() == Difficulty.HARD) && other instanceof VillagerEntity) {
			if (this.world.method_16346() != Difficulty.HARD && this.random.nextBoolean()) {
				return;
			}

			VillagerEntity villagerEntity = (VillagerEntity)other;
			ZombieVillagerEntity zombieVillagerEntity = new ZombieVillagerEntity(this.world);
			zombieVillagerEntity.copyPosition(villagerEntity);
			this.world.removeEntity(villagerEntity);
			zombieVillagerEntity.initialize(this.world.method_8482(new BlockPos(zombieVillagerEntity)), new ZombieEntity.Data(false), null);
			zombieVillagerEntity.method_13606(villagerEntity.profession());
			zombieVillagerEntity.setBaby(villagerEntity.isBaby());
			zombieVillagerEntity.setAiDisabled(villagerEntity.hasNoAi());
			if (villagerEntity.hasCustomName()) {
				zombieVillagerEntity.method_15578(villagerEntity.method_15541());
				zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
			}

			this.world.method_3686(zombieVillagerEntity);
			this.world.syncWorldEvent(null, 1026, new BlockPos(this), 0);
		}
	}

	@Override
	public float getEyeHeight() {
		float f = 1.74F;
		if (this.isBaby()) {
			f = (float)((double)f - 0.81);
		}

		return f;
	}

	@Override
	protected boolean canPickupItem(ItemStack stack) {
		return stack.getItem() == Items.EGG && this.isBaby() && this.hasMount() ? false : super.canPickupItem(stack);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		entityData = super.initialize(difficulty, entityData, nbt);
		float f = difficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (entityData == null) {
			entityData = new ZombieEntity.Data(this.world.random.nextFloat() < 0.05F);
		}

		if (entityData instanceof ZombieEntity.Data) {
			ZombieEntity.Data data = (ZombieEntity.Data)entityData;
			if (data.field_15074) {
				this.setBaby(true);
				if ((double)this.world.random.nextFloat() < 0.05) {
					List<ChickenEntity> list = this.world.method_16325(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicate.field_16702);
					if (!list.isEmpty()) {
						ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
						chickenEntity.setHasJockey(true);
						this.ride(chickenEntity);
					}
				} else if ((double)this.world.random.nextFloat() < 0.05) {
					ChickenEntity chickenEntity2 = new ChickenEntity(this.world);
					chickenEntity2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					chickenEntity2.initialize(difficulty, null, null);
					chickenEntity2.setHasJockey(true);
					this.world.method_3686(chickenEntity2);
					this.ride(chickenEntity2);
				}
			}

			this.setCanBreakDoors(this.method_15903() && this.random.nextFloat() < f * 0.1F);
			this.initEquipment(difficulty);
			this.updateEnchantments(difficulty);
		}

		if (this.getStack(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				this.field_14559[EquipmentSlot.HEAD.method_13032()] = 0.0F;
			}
		}

		this.method_15895(f);
		return entityData;
	}

	protected void method_15898(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.setCanPickUpLoot(bl);
		this.setCanBreakDoors(this.method_15903() && bl2);
		this.method_15895(this.world.method_8482(new BlockPos(this)).getClampedLocalDifficulty());
		this.setBaby(bl3);
		this.setAiDisabled(bl4);
	}

	protected void method_15895(float f) {
		this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
			.addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, 0));
		double d = this.random.nextDouble() * 1.5 * (double)f;
		if (d > 1.0) {
			this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", d, 2));
		}

		if (this.random.nextFloat() < f * 0.05F) {
			this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, 0));
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH)
				.addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, 2));
			this.setCanBreakDoors(this.method_15903());
		}
	}

	public void changeType(boolean isBaby) {
		this.increaseBounds(isBaby ? 0.5F : 1.0F);
	}

	@Override
	protected final void setBounds(float width, float height) {
		boolean bl = this.boundWidth > 0.0F && this.boundHeight > 0.0F;
		this.boundWidth = width;
		this.boundHeight = height;
		if (!bl) {
			this.increaseBounds(1.0F);
		}
	}

	protected final void increaseBounds(float multi) {
		super.setBounds(this.boundWidth * multi, this.boundHeight * multi);
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? 0.0 : -0.45;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)source.getAttacker();
			if (creeperEntity.method_3074() && creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				ItemStack itemStack = this.getSkull();
				if (!itemStack.isEmpty()) {
					this.method_15571(itemStack);
				}
			}
		}
	}

	protected ItemStack getSkull() {
		return new ItemStack(Items.ZOMBIE_HEAD);
	}

	public class Data implements EntityData {
		public boolean field_15074;

		private Data(boolean bl) {
			this.field_15074 = bl;
		}
	}

	class class_3527 extends class_3474 {
		class_3527(Block block, PathAwareEntity pathAwareEntity, double d, int i) {
			super(block, pathAwareEntity, d, i);
		}

		@Override
		public void method_15700(IWorld iWorld, BlockPos blockPos) {
			iWorld.playSound(null, blockPos, Sounds.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
		}

		@Override
		public void method_15699(World world, BlockPos blockPos) {
			world.playSound(null, blockPos, Sounds.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		}

		@Override
		public double method_15695() {
			return 1.3;
		}
	}
}
