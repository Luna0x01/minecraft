package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PiglinEntity extends AbstractPiglinEntity implements CrossbowUser, InventoryOwner {
	private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> DANCING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
	private static final EntityAttributeModifier BABY_SPEED_BOOST = new EntityAttributeModifier(
		BABY_SPEED_BOOST_ID, "Baby speed boost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE
	);
	private static final int field_30548 = 16;
	private static final float field_30549 = 0.35F;
	private static final int field_30550 = 5;
	private static final float field_30551 = 1.6F;
	private static final float field_30552 = 0.1F;
	private static final int field_30553 = 3;
	private static final float field_30554 = 0.2F;
	private static final float field_30555 = 0.81F;
	private static final double field_30556 = 0.5;
	private final SimpleInventory inventory = new SimpleInventory(8);
	private boolean cannotHunt;
	protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> SENSOR_TYPES = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR
	);
	protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.DOORS_TO_CLOSE,
		MemoryModuleType.MOBS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.NEAREST_VISIBLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
		MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
		MemoryModuleType.NEARBY_ADULT_PIGLINS,
		MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
		MemoryModuleType.HURT_BY,
		MemoryModuleType.HURT_BY_ENTITY,
		MemoryModuleType.WALK_TARGET,
		new MemoryModuleType[]{
			MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
			MemoryModuleType.ATTACK_TARGET,
			MemoryModuleType.ATTACK_COOLING_DOWN,
			MemoryModuleType.INTERACTION_TARGET,
			MemoryModuleType.PATH,
			MemoryModuleType.ANGRY_AT,
			MemoryModuleType.UNIVERSAL_ANGER,
			MemoryModuleType.AVOID_TARGET,
			MemoryModuleType.ADMIRING_ITEM,
			MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM,
			MemoryModuleType.ADMIRING_DISABLED,
			MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM,
			MemoryModuleType.CELEBRATE_LOCATION,
			MemoryModuleType.DANCING,
			MemoryModuleType.HUNTED_RECENTLY,
			MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
			MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
			MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED,
			MemoryModuleType.RIDE_TARGET,
			MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
			MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
			MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
			MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
			MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
			MemoryModuleType.ATE_RECENTLY,
			MemoryModuleType.NEAREST_REPELLENT
		}
	);

	public PiglinEntity(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.isBaby()) {
			nbt.putBoolean("IsBaby", true);
		}

		if (this.cannotHunt) {
			nbt.putBoolean("CannotHunt", true);
		}

		nbt.put("Inventory", this.inventory.toNbtList());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setBaby(nbt.getBoolean("IsBaby"));
		this.setCannotHunt(nbt.getBoolean("CannotHunt"));
		this.inventory.readNbtList(nbt.getList("Inventory", 10));
	}

	@Debug
	@Override
	public Inventory getInventory() {
		return this.inventory;
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		this.inventory.clearToList().forEach(this::dropStack);
	}

	protected ItemStack addItem(ItemStack stack) {
		return this.inventory.addStack(stack);
	}

	protected boolean canInsertIntoInventory(ItemStack stack) {
		return this.inventory.canInsert(stack);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BABY, false);
		this.dataTracker.startTracking(CHARGING, false);
		this.dataTracker.startTracking(DANCING, false);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (BABY.equals(data)) {
			this.calculateDimensions();
		}
	}

	public static DefaultAttributeContainer.Builder createPiglinAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
	}

	public static boolean canSpawn(EntityType<PiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (spawnReason != SpawnReason.STRUCTURE) {
			if (world.getRandom().nextFloat() < 0.2F) {
				this.setBaby(true);
			} else if (this.isAdult()) {
				this.equipStack(EquipmentSlot.MAINHAND, this.makeInitialWeapon());
			}
		}

		PiglinBrain.setHuntedRecently(this);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return false;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isPersistent();
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		if (this.isAdult()) {
			this.equipAtChance(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
			this.equipAtChance(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
			this.equipAtChance(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
			this.equipAtChance(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
		}
	}

	private void equipAtChance(EquipmentSlot slot, ItemStack stack) {
		if (this.world.random.nextFloat() < 0.1F) {
			this.equipStack(slot, stack);
		}
	}

	@Override
	protected Brain.Profile<PiglinEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
	}

	@Override
	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return PiglinBrain.create(this, this.createBrainProfile().deserialize(dynamic));
	}

	@Override
	public Brain<PiglinEntity> getBrain() {
		return (Brain<PiglinEntity>)super.getBrain();
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ActionResult actionResult = super.interactMob(player, hand);
		if (actionResult.isAccepted()) {
			return actionResult;
		} else if (!this.world.isClient) {
			return PiglinBrain.playerInteract(this, player, hand);
		} else {
			boolean bl = PiglinBrain.isWillingToTrade(this, player.getStackInHand(hand)) && this.getActivity() != PiglinActivity.ADMIRING_ITEM;
			return bl ? ActionResult.SUCCESS : ActionResult.PASS;
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.92;
	}

	@Override
	public void setBaby(boolean baby) {
		this.getDataTracker().set(BABY, baby);
		if (!this.world.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			entityAttributeInstance.removeModifier(BABY_SPEED_BOOST);
			if (baby) {
				entityAttributeInstance.addTemporaryModifier(BABY_SPEED_BOOST);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return this.getDataTracker().get(BABY);
	}

	private void setCannotHunt(boolean cannotHunt) {
		this.cannotHunt = cannotHunt;
	}

	@Override
	protected boolean canHunt() {
		return !this.cannotHunt;
	}

	@Override
	protected void mobTick() {
		this.world.getProfiler().push("piglinBrain");
		this.getBrain().tick((ServerWorld)this.world, this);
		this.world.getProfiler().pop();
		PiglinBrain.tickActivities(this);
		super.mobTick();
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return this.experiencePoints;
	}

	@Override
	protected void zombify(ServerWorld world) {
		PiglinBrain.pickupItemWithOffHand(this);
		this.inventory.clearToList().forEach(this::dropStack);
		super.zombify(world);
	}

	private ItemStack makeInitialWeapon() {
		return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
	}

	private boolean isCharging() {
		return this.dataTracker.get(CHARGING);
	}

	@Override
	public void setCharging(boolean charging) {
		this.dataTracker.set(CHARGING, charging);
	}

	@Override
	public void postShoot() {
		this.despawnCounter = 0;
	}

	@Override
	public PiglinActivity getActivity() {
		if (this.isDancing()) {
			return PiglinActivity.DANCING;
		} else if (PiglinBrain.isGoldenItem(this.getOffHandStack())) {
			return PiglinActivity.ADMIRING_ITEM;
		} else if (this.isAttacking() && this.isHoldingTool()) {
			return PiglinActivity.ATTACKING_WITH_MELEE_WEAPON;
		} else if (this.isCharging()) {
			return PiglinActivity.CROSSBOW_CHARGE;
		} else {
			return this.isAttacking() && this.isHolding(Items.CROSSBOW) ? PiglinActivity.CROSSBOW_HOLD : PiglinActivity.DEFAULT;
		}
	}

	public boolean isDancing() {
		return this.dataTracker.get(DANCING);
	}

	public void setDancing(boolean dancing) {
		this.dataTracker.set(DANCING, dancing);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean bl = super.damage(source, amount);
		if (this.world.isClient) {
			return false;
		} else {
			if (bl && source.getAttacker() instanceof LivingEntity) {
				PiglinBrain.onAttacked(this, (LivingEntity)source.getAttacker());
			}

			return bl;
		}
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		this.shoot(this, 1.6F);
	}

	@Override
	public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
		this.shoot(this, target, projectile, multiShotSpray, 1.6F);
	}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return weapon == Items.CROSSBOW;
	}

	protected void equipToMainHand(ItemStack stack) {
		this.equipLootStack(EquipmentSlot.MAINHAND, stack);
	}

	protected void equipToOffHand(ItemStack stack) {
		if (stack.isOf(PiglinBrain.BARTERING_ITEM)) {
			this.equipStack(EquipmentSlot.OFFHAND, stack);
			this.updateDropChances(EquipmentSlot.OFFHAND);
		} else {
			this.equipLootStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.canPickUpLoot() && PiglinBrain.canGather(this, stack);
	}

	protected boolean canEquipStack(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		ItemStack itemStack = this.getEquippedStack(equipmentSlot);
		return this.prefersNewEquipment(stack, itemStack);
	}

	@Override
	protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
		if (EnchantmentHelper.hasBindingCurse(oldStack)) {
			return false;
		} else {
			boolean bl = PiglinBrain.isGoldenItem(newStack) || newStack.isOf(Items.CROSSBOW);
			boolean bl2 = PiglinBrain.isGoldenItem(oldStack) || oldStack.isOf(Items.CROSSBOW);
			if (bl && !bl2) {
				return true;
			} else if (!bl && bl2) {
				return false;
			} else {
				return this.isAdult() && !newStack.isOf(Items.CROSSBOW) && oldStack.isOf(Items.CROSSBOW) ? false : super.prefersNewEquipment(newStack, oldStack);
			}
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		this.triggerItemPickedUpByEntityCriteria(item);
		PiglinBrain.loot(this, item);
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.isBaby() && entity.getType() == EntityType.HOGLIN) {
			entity = this.getTopMostPassenger(entity, 3);
		}

		return super.startRiding(entity, force);
	}

	private Entity getTopMostPassenger(Entity entity, int maxLevel) {
		List<Entity> list = entity.getPassengerList();
		return maxLevel != 1 && !list.isEmpty() ? this.getTopMostPassenger((Entity)list.get(0), maxLevel - 1) : entity;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.world.isClient ? null : (SoundEvent)PiglinBrain.getCurrentActivitySound(this).orElse(null);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PIGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PIGLIN_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
	}

	protected void playSound(SoundEvent sound) {
		this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected void playZombificationSound() {
		this.playSound(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
	}
}
