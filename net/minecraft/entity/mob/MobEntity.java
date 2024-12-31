package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2782;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.class_2780;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public abstract class MobEntity extends LivingEntity {
	private static final TrackedData<Byte> field_14555 = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
	public int ambientSoundChance;
	protected int experiencePoints;
	private final LookControl lookControl;
	protected MoveControl entityMotionHelper;
	protected JumpControl jumpControl;
	private final BodyControl bodyControl;
	protected EntityNavigation navigation;
	protected final GoalSelector goals;
	protected final GoalSelector attackGoals;
	private LivingEntity attackTarget;
	private final MobVisibilityCache visibilityCache;
	private final DefaultedList<ItemStack> field_15467 = DefaultedList.ofSize(2, ItemStack.EMPTY);
	protected float[] armorDropChances = new float[2];
	private final DefaultedList<ItemStack> field_15468 = DefaultedList.ofSize(4, ItemStack.EMPTY);
	protected float[] field_14559 = new float[4];
	private boolean pickUpLoot;
	private boolean persistent;
	private final Map<LandType, Float> field_14556 = Maps.newEnumMap(LandType.class);
	private Identifier field_14557;
	private long field_14558;
	private boolean leashed;
	private Entity leashOwner;
	private NbtCompound leash;

	public MobEntity(World world) {
		super(world);
		this.goals = new GoalSelector(world != null && world.profiler != null ? world.profiler : null);
		this.attackGoals = new GoalSelector(world != null && world.profiler != null ? world.profiler : null);
		this.lookControl = new LookControl(this);
		this.entityMotionHelper = new MoveControl(this);
		this.jumpControl = new JumpControl(this);
		this.bodyControl = this.method_13088();
		this.navigation = this.createNavigation(world);
		this.visibilityCache = new MobVisibilityCache(this);
		Arrays.fill(this.field_14559, 0.085F);
		Arrays.fill(this.armorDropChances, 0.085F);
		if (world != null && !world.isClient) {
			this.initGoals();
		}
	}

	protected void initGoals() {
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(16.0);
	}

	protected EntityNavigation createNavigation(World world) {
		return new MobNavigation(this, world);
	}

	public float method_13075(LandType landType) {
		Float float_ = (Float)this.field_14556.get(landType);
		return float_ == null ? landType.getWeight() : float_;
	}

	public void method_13076(LandType landType, float f) {
		this.field_14556.put(landType, f);
	}

	protected BodyControl method_13088() {
		return new BodyControl(this);
	}

	public LookControl getLookControl() {
		return this.lookControl;
	}

	public MoveControl getMotionHelper() {
		return this.entityMotionHelper;
	}

	public JumpControl getJumpControl() {
		return this.jumpControl;
	}

	public EntityNavigation getNavigation() {
		return this.navigation;
	}

	public MobVisibilityCache getVisibilityCache() {
		return this.visibilityCache;
	}

	@Nullable
	public LivingEntity getTarget() {
		return this.attackTarget;
	}

	public void setTarget(@Nullable LivingEntity target) {
		this.attackTarget = target;
	}

	public boolean canAttackEntity(Class<? extends LivingEntity> clazz) {
		return clazz != GhastEntity.class;
	}

	public void onEatingGrass() {
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14555, (byte)0);
	}

	public int getMinAmbientSoundDelay() {
		return 80;
	}

	public void playAmbientSound() {
		Sound sound = this.ambientSound();
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.world.profiler.push("mobBaseTick");
		if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
			this.method_13085();
			this.playAmbientSound();
		}

		this.world.profiler.pop();
	}

	@Override
	protected void method_13051(DamageSource damageSource) {
		this.method_13085();
		super.method_13051(damageSource);
	}

	private void method_13085() {
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.experiencePoints > 0) {
			int i = this.experiencePoints;

			for (int j = 0; j < this.field_15468.size(); j++) {
				if (!this.field_15468.get(j).isEmpty() && this.field_14559[j] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			for (int k = 0; k < this.field_15467.size(); k++) {
				if (!this.field_15467.get(k).isEmpty() && this.armorDropChances[k] <= 1.0F) {
					i += 1 + this.random.nextInt(3);
				}
			}

			return i;
		} else {
			return this.experiencePoints;
		}
	}

	public void playSpawnEffects() {
		if (this.world.isClient) {
			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				double g = 10.0;
				this.world
					.addParticle(
						ParticleType.EXPLOSION,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - d * 10.0,
						this.y + (double)(this.random.nextFloat() * this.height) - e * 10.0,
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - f * 10.0,
						d,
						e,
						f
					);
			}
		} else {
			this.world.sendEntityStatus(this, (byte)20);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 20) {
			this.playSpawnEffects();
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			this.updateLeash();
			if (this.ticksAlive % 5 == 0) {
				boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
				boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
				this.goals.method_13096(1, bl);
				this.goals.method_13096(4, bl && bl2);
				this.goals.method_13096(2, bl);
			}
		}
	}

	@Override
	protected float turnHead(float bodyRotation, float headRotation) {
		this.bodyControl.tick();
		return headRotation;
	}

	@Nullable
	protected Sound ambientSound() {
		return null;
	}

	@Nullable
	protected Item getDefaultDrop() {
		return null;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		Item item = this.getDefaultDrop();
		if (item != null) {
			int i = this.random.nextInt(3);
			if (lootingMultiplier > 0) {
				i += this.random.nextInt(lootingMultiplier + 1);
			}

			for (int j = 0; j < i; j++) {
				this.dropItem(item, 1);
			}
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer, Class<?> entityClass) {
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemListSchema(entityClass, "ArmorItems", "HandItems"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("CanPickUpLoot", this.canPickUpLoot());
		nbt.putBoolean("PersistenceRequired", this.persistent);
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.field_15468) {
			NbtCompound nbtCompound = new NbtCompound();
			if (!itemStack.isEmpty()) {
				itemStack.toNbt(nbtCompound);
			}

			nbtList.add(nbtCompound);
		}

		nbt.put("ArmorItems", nbtList);
		NbtList nbtList2 = new NbtList();

		for (ItemStack itemStack2 : this.field_15467) {
			NbtCompound nbtCompound2 = new NbtCompound();
			if (!itemStack2.isEmpty()) {
				itemStack2.toNbt(nbtCompound2);
			}

			nbtList2.add(nbtCompound2);
		}

		nbt.put("HandItems", nbtList2);
		NbtList nbtList3 = new NbtList();

		for (float f : this.field_14559) {
			nbtList3.add(new NbtFloat(f));
		}

		nbt.put("ArmorDropChances", nbtList3);
		NbtList nbtList4 = new NbtList();

		for (float g : this.armorDropChances) {
			nbtList4.add(new NbtFloat(g));
		}

		nbt.put("HandDropChances", nbtList4);
		nbt.putBoolean("Leashed", this.leashed);
		if (this.leashOwner != null) {
			NbtCompound nbtCompound3 = new NbtCompound();
			if (this.leashOwner instanceof LivingEntity) {
				UUID uUID = this.leashOwner.getUuid();
				nbtCompound3.putUuid("UUID", uUID);
			} else if (this.leashOwner instanceof AbstractDecorationEntity) {
				BlockPos blockPos = ((AbstractDecorationEntity)this.leashOwner).getTilePos();
				nbtCompound3.putInt("X", blockPos.getX());
				nbtCompound3.putInt("Y", blockPos.getY());
				nbtCompound3.putInt("Z", blockPos.getZ());
			}

			nbt.put("Leash", nbtCompound3);
		}

		nbt.putBoolean("LeftHanded", this.method_13082());
		if (this.field_14557 != null) {
			nbt.putString("DeathLootTable", this.field_14557.toString());
			if (this.field_14558 != 0L) {
				nbt.putLong("DeathLootTableSeed", this.field_14558);
			}
		}

		if (this.hasNoAi()) {
			nbt.putBoolean("NoAI", this.hasNoAi());
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("CanPickUpLoot", 1)) {
			this.setCanPickUpLoot(nbt.getBoolean("CanPickUpLoot"));
		}

		this.persistent = nbt.getBoolean("PersistenceRequired");
		if (nbt.contains("ArmorItems", 9)) {
			NbtList nbtList = nbt.getList("ArmorItems", 10);

			for (int i = 0; i < this.field_15468.size(); i++) {
				this.field_15468.set(i, new ItemStack(nbtList.getCompound(i)));
			}
		}

		if (nbt.contains("HandItems", 9)) {
			NbtList nbtList2 = nbt.getList("HandItems", 10);

			for (int j = 0; j < this.field_15467.size(); j++) {
				this.field_15467.set(j, new ItemStack(nbtList2.getCompound(j)));
			}
		}

		if (nbt.contains("ArmorDropChances", 9)) {
			NbtList nbtList3 = nbt.getList("ArmorDropChances", 5);

			for (int k = 0; k < nbtList3.size(); k++) {
				this.field_14559[k] = nbtList3.getFloat(k);
			}
		}

		if (nbt.contains("HandDropChances", 9)) {
			NbtList nbtList4 = nbt.getList("HandDropChances", 5);

			for (int l = 0; l < nbtList4.size(); l++) {
				this.armorDropChances[l] = nbtList4.getFloat(l);
			}
		}

		this.leashed = nbt.getBoolean("Leashed");
		if (this.leashed && nbt.contains("Leash", 10)) {
			this.leash = nbt.getCompound("Leash");
		}

		this.method_13084(nbt.getBoolean("LeftHanded"));
		if (nbt.contains("DeathLootTable", 8)) {
			this.field_14557 = new Identifier(nbt.getString("DeathLootTable"));
			this.field_14558 = nbt.getLong("DeathLootTableSeed");
		}

		this.setAiDisabled(nbt.getBoolean("NoAI"));
	}

	@Nullable
	protected Identifier getLootTableId() {
		return null;
	}

	@Override
	protected void method_13044(boolean bl, int i, DamageSource damageSource) {
		Identifier identifier = this.field_14557;
		if (identifier == null) {
			identifier = this.getLootTableId();
		}

		if (identifier != null) {
			class_2780 lv = this.world.method_11487().method_12006(identifier);
			this.field_14557 = null;
			class_2782.class_2783 lv2 = new class_2782.class_2783((ServerWorld)this.world).method_11997(this).method_11996(damageSource);
			if (bl && this.attackingPlayer != null) {
				lv2 = lv2.method_11998(this.attackingPlayer).method_11995(this.attackingPlayer.method_13271());
			}

			for (ItemStack itemStack : lv.method_11981(this.field_14558 == 0L ? this.random : new Random(this.field_14558), lv2.method_11994())) {
				this.dropItem(itemStack, 0.0F);
			}

			this.method_4472(bl, i);
		} else {
			super.method_13044(bl, i, damageSource);
		}
	}

	public void method_15061(float f) {
		this.field_16513 = f;
	}

	public void setForwardSpeed(float forwardSpeed) {
		this.forwardSpeed = forwardSpeed;
	}

	public void method_13086(float f) {
		this.sidewaysSpeed = f;
	}

	@Override
	public void setMovementSpeed(float movementSpeed) {
		super.setMovementSpeed(movementSpeed);
		this.method_15061(movementSpeed);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.world.profiler.push("looting");
		if (!this.world.isClient && this.canPickUpLoot() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
			for (ItemEntity itemEntity : this.world.getEntitiesInBox(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0))) {
				if (!itemEntity.removed && !itemEntity.getItemStack().isEmpty() && !itemEntity.cannotPickup()) {
					this.loot(itemEntity);
				}
			}
		}

		this.world.profiler.pop();
	}

	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getItemStack();
		EquipmentSlot equipmentSlot = method_13083(itemStack);
		boolean bl = true;
		ItemStack itemStack2 = this.getStack(equipmentSlot);
		if (!itemStack2.isEmpty()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
				if (itemStack.getItem() instanceof SwordItem && !(itemStack2.getItem() instanceof SwordItem)) {
					bl = true;
				} else if (itemStack.getItem() instanceof SwordItem && itemStack2.getItem() instanceof SwordItem) {
					SwordItem swordItem = (SwordItem)itemStack.getItem();
					SwordItem swordItem2 = (SwordItem)itemStack2.getItem();
					if (swordItem.getAttackDamage() == swordItem2.getAttackDamage()) {
						bl = itemStack.getData() > itemStack2.getData() || itemStack.hasNbt() && !itemStack2.hasNbt();
					} else {
						bl = swordItem.getAttackDamage() > swordItem2.getAttackDamage();
					}
				} else if (itemStack.getItem() instanceof BowItem && itemStack2.getItem() instanceof BowItem) {
					bl = itemStack.hasNbt() && !itemStack2.hasNbt();
				} else {
					bl = false;
				}
			} else if (itemStack.getItem() instanceof ArmorItem && !(itemStack2.getItem() instanceof ArmorItem)) {
				bl = true;
			} else if (itemStack.getItem() instanceof ArmorItem && itemStack2.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(itemStack2)) {
				ArmorItem armorItem = (ArmorItem)itemStack.getItem();
				ArmorItem armorItem2 = (ArmorItem)itemStack2.getItem();
				if (armorItem.protection == armorItem2.protection) {
					bl = itemStack.getData() > itemStack2.getData() || itemStack.hasNbt() && !itemStack2.hasNbt();
				} else {
					bl = armorItem.protection > armorItem2.protection;
				}
			} else {
				bl = false;
			}
		}

		if (bl && this.canPickupItem(itemStack)) {
			double d;
			switch (equipmentSlot.getType()) {
				case HAND:
					d = (double)this.armorDropChances[equipmentSlot.method_13032()];
					break;
				case ARMOR:
					d = (double)this.field_14559[equipmentSlot.method_13032()];
					break;
				default:
					d = 0.0;
			}

			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.dropItem(itemStack2, 0.0F);
			}

			this.equipStack(equipmentSlot, itemStack);
			switch (equipmentSlot.getType()) {
				case HAND:
					this.armorDropChances[equipmentSlot.method_13032()] = 2.0F;
					break;
				case ARMOR:
					this.field_14559[equipmentSlot.method_13032()] = 2.0F;
			}

			this.persistent = true;
			this.sendPickup(item, itemStack.getCount());
			item.remove();
		}
	}

	protected boolean canPickupItem(ItemStack stack) {
		return true;
	}

	protected boolean canImmediatelyDespawn() {
		return true;
	}

	protected void checkDespawn() {
		if (this.persistent) {
			this.despawnCounter = 0;
		} else {
			Entity entity = this.world.getClosestPlayer(this, -1.0);
			if (entity != null) {
				double d = entity.x - this.x;
				double e = entity.y - this.y;
				double f = entity.z - this.z;
				double g = d * d + e * e + f * f;
				if (this.canImmediatelyDespawn() && g > 16384.0) {
					this.remove();
				}

				if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && g > 1024.0 && this.canImmediatelyDespawn()) {
					this.remove();
				} else if (g < 1024.0) {
					this.despawnCounter = 0;
				}
			}
		}
	}

	@Override
	protected final void tickNewAi() {
		this.despawnCounter++;
		this.world.profiler.push("checkDespawn");
		this.checkDespawn();
		this.world.profiler.pop();
		this.world.profiler.push("sensing");
		this.visibilityCache.clear();
		this.world.profiler.pop();
		this.world.profiler.push("targetSelector");
		this.attackGoals.tick();
		this.world.profiler.pop();
		this.world.profiler.push("goalSelector");
		this.goals.tick();
		this.world.profiler.pop();
		this.world.profiler.push("navigation");
		this.navigation.tick();
		this.world.profiler.pop();
		this.world.profiler.push("mob tick");
		this.mobTick();
		this.world.profiler.pop();
		if (this.hasMount() && this.getVehicle() instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.getVehicle();
			mobEntity.getNavigation().method_13107(this.getNavigation().method_13113(), 1.5);
			mobEntity.getMotionHelper().copyFrom(this.getMotionHelper());
		}

		this.world.profiler.push("controls");
		this.world.profiler.push("move");
		this.entityMotionHelper.updateMovement();
		this.world.profiler.swap("look");
		this.lookControl.tick();
		this.world.profiler.swap("jump");
		this.jumpControl.tick();
		this.world.profiler.pop();
		this.world.profiler.pop();
	}

	protected void mobTick() {
	}

	public int getLookPitchSpeed() {
		return 40;
	}

	public int method_13081() {
		return 10;
	}

	public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
		double d = targetEntity.x - this.x;
		double e = targetEntity.z - this.z;
		double f;
		if (targetEntity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)targetEntity;
			f = livingEntity.y + (double)livingEntity.getEyeHeight() - (this.y + (double)this.getEyeHeight());
		} else {
			f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - (this.y + (double)this.getEyeHeight());
		}

		double h = (double)MathHelper.sqrt(d * d + e * e);
		float i = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float j = (float)(-(MathHelper.atan2(f, h) * 180.0F / (float)Math.PI));
		this.pitch = this.changeAngle(this.pitch, j, maxPitchChange);
		this.yaw = this.changeAngle(this.yaw, i, maxYawChange);
	}

	private float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
		float f = MathHelper.wrapDegrees(newAngle - oldAngle);
		if (f > maxChangeInAngle) {
			f = maxChangeInAngle;
		}

		if (f < -maxChangeInAngle) {
			f = -maxChangeInAngle;
		}

		return oldAngle + f;
	}

	public boolean canSpawn() {
		BlockState blockState = this.world.getBlockState(new BlockPos(this).down());
		return blockState.method_13361(this);
	}

	public boolean hasNoSpawnCollisions() {
		return !this.world.containsFluid(this.getBoundingBox())
			&& this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()
			&& this.world.hasEntityIn(this.getBoundingBox(), this);
	}

	public float method_2638() {
		return 1.0F;
	}

	public int getLimitPerChunk() {
		return 4;
	}

	@Override
	public int getSafeFallDistance() {
		if (this.getTarget() == null) {
			return 3;
		} else {
			int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
			i -= (3 - this.world.getGlobalDifficulty().getId()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.field_15467;
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.field_15468;
	}

	@Override
	public ItemStack getStack(EquipmentSlot slot) {
		switch (slot.getType()) {
			case HAND:
				return this.field_15467.get(slot.method_13032());
			case ARMOR:
				return this.field_15468.get(slot.method_13032());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		switch (slot.getType()) {
			case HAND:
				this.field_15467.set(slot.method_13032(), stack);
				break;
			case ARMOR:
				this.field_15468.set(slot.method_13032(), stack);
		}
	}

	@Override
	protected void method_4472(boolean bl, int i) {
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getStack(equipmentSlot);
			double d;
			switch (equipmentSlot.getType()) {
				case HAND:
					d = (double)this.armorDropChances[equipmentSlot.method_13032()];
					break;
				case ARMOR:
					d = (double)this.field_14559[equipmentSlot.method_13032()];
					break;
				default:
					d = 0.0;
			}

			boolean bl2 = d > 1.0;
			if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (bl || bl2) && (double)(this.random.nextFloat() - (float)i * 0.01F) < d) {
				if (!bl2 && itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
				}

				this.dropItem(itemStack, 0.0F);
			}
		}
	}

	protected void initEquipment(LocalDifficulty difficulty) {
		if (this.random.nextFloat() < 0.15F * difficulty.getClampedLocalDifficulty()) {
			int i = this.random.nextInt(2);
			float f = this.world.getGlobalDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			if (this.random.nextFloat() < 0.095F) {
				i++;
			}

			boolean bl = true;

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
					ItemStack itemStack = this.getStack(equipmentSlot);
					if (!bl && this.random.nextFloat() < f) {
						break;
					}

					bl = false;
					if (itemStack.isEmpty()) {
						Item item = method_13078(equipmentSlot, i);
						if (item != null) {
							this.equipStack(equipmentSlot, new ItemStack(item));
						}
					}
				}
			}
		}
	}

	public static EquipmentSlot method_13083(ItemStack itemStack) {
		if (itemStack.getItem() == Item.fromBlock(Blocks.PUMPKIN) || itemStack.getItem() == Items.SKULL) {
			return EquipmentSlot.HEAD;
		} else if (itemStack.getItem() instanceof ArmorItem) {
			return ((ArmorItem)itemStack.getItem()).field_12275;
		} else if (itemStack.getItem() == Items.ELYTRA) {
			return EquipmentSlot.CHEST;
		} else {
			return itemStack.getItem() == Items.SHIELD ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
		}
	}

	@Nullable
	public static Item method_13078(EquipmentSlot equipmentSlot, int i) {
		switch (equipmentSlot) {
			case HEAD:
				if (i == 0) {
					return Items.LEATHER_HELMET;
				} else if (i == 1) {
					return Items.GOLDEN_HELMET;
				} else if (i == 2) {
					return Items.CHAINMAIL_HELMET;
				} else if (i == 3) {
					return Items.IRON_HELMET;
				} else if (i == 4) {
					return Items.DIAMOND_HELMET;
				}
			case CHEST:
				if (i == 0) {
					return Items.LEATHER_CHESTPLATE;
				} else if (i == 1) {
					return Items.GOLDEN_CHESTPLATE;
				} else if (i == 2) {
					return Items.CHAINMAIL_CHESTPLATE;
				} else if (i == 3) {
					return Items.IRON_CHESTPLATE;
				} else if (i == 4) {
					return Items.DIAMOND_CHESTPLATE;
				}
			case LEGS:
				if (i == 0) {
					return Items.LEATHER_LEGGINGS;
				} else if (i == 1) {
					return Items.GOLDEN_LEGGINGS;
				} else if (i == 2) {
					return Items.CHAINMAIL_LEGGINGS;
				} else if (i == 3) {
					return Items.IRON_LEGGINGS;
				} else if (i == 4) {
					return Items.DIAMOND_LEGGINGS;
				}
			case FEET:
				if (i == 0) {
					return Items.LEATHER_BOOTS;
				} else if (i == 1) {
					return Items.GOLDEN_BOOTS;
				} else if (i == 2) {
					return Items.CHAINMAIL_BOOTS;
				} else if (i == 3) {
					return Items.IRON_BOOTS;
				} else if (i == 4) {
					return Items.DIAMOND_BOOTS;
				}
			default:
				return null;
		}
	}

	protected void updateEnchantments(LocalDifficulty difficulty) {
		float f = difficulty.getClampedLocalDifficulty();
		if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25F * f) {
			this.equipStack(
				EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0F + f * (float)this.random.nextInt(18)), false)
			);
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack itemStack = this.getStack(equipmentSlot);
				if (!itemStack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
					this.equipStack(equipmentSlot, EnchantmentHelper.enchant(this.random, itemStack, (int)(5.0F + f * (float)this.random.nextInt(18)), false));
				}
			}
		}
	}

	@Nullable
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE)
			.addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, 1));
		if (this.random.nextFloat() < 0.05F) {
			this.method_13084(true);
		} else {
			this.method_13084(false);
		}

		return data;
	}

	public boolean canBeControlledByRider() {
		return false;
	}

	public void setPersistent() {
		this.persistent = true;
	}

	public void method_13077(EquipmentSlot equipmentSlot, float f) {
		switch (equipmentSlot.getType()) {
			case HAND:
				this.armorDropChances[equipmentSlot.method_13032()] = f;
				break;
			case ARMOR:
				this.field_14559[equipmentSlot.method_13032()] = f;
		}
	}

	public boolean canPickUpLoot() {
		return this.pickUpLoot;
	}

	public void setCanPickUpLoot(boolean pickUpLoot) {
		this.pickUpLoot = pickUpLoot;
	}

	public boolean isPersistent() {
		return this.persistent;
	}

	@Override
	public final boolean interact(PlayerEntity player, Hand hand) {
		if (this.isLeashed() && this.getLeashOwner() == player) {
			this.detachLeash(true, !player.abilities.creativeMode);
			return true;
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.getItem() == Items.LEAD && this.method_2537(player)) {
				this.attachLeash(player, true);
				itemStack.decrement(1);
				return true;
			} else {
				return this.interactMob(player, hand) ? true : super.interact(player, hand);
			}
		}
	}

	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	protected void updateLeash() {
		if (this.leash != null) {
			this.method_6163();
		}

		if (this.leashed) {
			if (!this.isAlive()) {
				this.detachLeash(true, true);
			}

			if (this.leashOwner == null || this.leashOwner.removed) {
				this.detachLeash(true, true);
			}
		}
	}

	public void detachLeash(boolean sendPacket, boolean bl) {
		if (this.leashed) {
			this.leashed = false;
			this.leashOwner = null;
			if (!this.world.isClient && bl) {
				this.dropItem(Items.LEAD, 1);
			}

			if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
				((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAttachS2CPacket(this, null));
			}
		}
	}

	public boolean method_2537(PlayerEntity playerEntity) {
		return !this.isLeashed() && !(this instanceof Monster);
	}

	public boolean isLeashed() {
		return this.leashed;
	}

	public Entity getLeashOwner() {
		return this.leashOwner;
	}

	public void attachLeash(Entity entity, boolean bl) {
		this.leashed = true;
		this.leashOwner = entity;
		if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
			((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAttachS2CPacket(this, this.leashOwner));
		}

		if (this.hasMount()) {
			this.stopRiding();
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		boolean bl = super.startRiding(entity, force);
		if (bl && this.isLeashed()) {
			this.detachLeash(true, true);
		}

		return bl;
	}

	private void method_6163() {
		if (this.leashed && this.leash != null) {
			if (this.leash.containsUuid("UUID")) {
				UUID uUID = this.leash.getUuid("UUID");

				for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox().expand(10.0))) {
					if (livingEntity.getUuid().equals(uUID)) {
						this.attachLeash(livingEntity, true);
						break;
					}
				}
			} else if (this.leash.contains("X", 99) && this.leash.contains("Y", 99) && this.leash.contains("Z", 99)) {
				BlockPos blockPos = new BlockPos(this.leash.getInt("X"), this.leash.getInt("Y"), this.leash.getInt("Z"));
				LeashKnotEntity leashKnotEntity = LeashKnotEntity.getOrCreate(this.world, blockPos);
				if (leashKnotEntity == null) {
					leashKnotEntity = LeashKnotEntity.create(this.world, blockPos);
				}

				this.attachLeash(leashKnotEntity, true);
			} else {
				this.detachLeash(false, true);
			}
		}

		this.leash = null;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		EquipmentSlot equipmentSlot;
		if (slot == 98) {
			equipmentSlot = EquipmentSlot.MAINHAND;
		} else if (slot == 99) {
			equipmentSlot = EquipmentSlot.OFFHAND;
		} else if (slot == 100 + EquipmentSlot.HEAD.method_13032()) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (slot == 100 + EquipmentSlot.CHEST.method_13032()) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (slot == 100 + EquipmentSlot.LEGS.method_13032()) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else {
			if (slot != 100 + EquipmentSlot.FEET.method_13032()) {
				return false;
			}

			equipmentSlot = EquipmentSlot.FEET;
		}

		if (!item.isEmpty() && !method_13080(equipmentSlot, item) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.equipStack(equipmentSlot, item);
			return true;
		}
	}

	@Override
	public boolean method_13003() {
		return this.canBeControlledByRider() && super.method_13003();
	}

	public static boolean method_13080(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		EquipmentSlot equipmentSlot2 = method_13083(itemStack);
		return equipmentSlot2 == equipmentSlot
			|| equipmentSlot2 == EquipmentSlot.MAINHAND && equipmentSlot == EquipmentSlot.OFFHAND
			|| equipmentSlot2 == EquipmentSlot.OFFHAND && equipmentSlot == EquipmentSlot.MAINHAND;
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.hasNoAi();
	}

	public void setAiDisabled(boolean value) {
		byte b = this.dataTracker.get(field_14555);
		this.dataTracker.set(field_14555, value ? (byte)(b | 1) : (byte)(b & -2));
	}

	public void method_13084(boolean bl) {
		byte b = this.dataTracker.get(field_14555);
		this.dataTracker.set(field_14555, bl ? (byte)(b | 2) : (byte)(b & -3));
	}

	public boolean hasNoAi() {
		return (this.dataTracker.get(field_14555) & 1) != 0;
	}

	public boolean method_13082() {
		return (this.dataTracker.get(field_14555) & 2) != 0;
	}

	@Override
	public HandOption getDurability() {
		return this.method_13082() ? HandOption.LEFT : HandOption.RIGHT;
	}

	public static enum Location {
		ON_GROUND,
		IN_AIR,
		IN_WATER;
	}
}
