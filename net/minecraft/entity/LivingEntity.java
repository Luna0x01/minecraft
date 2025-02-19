package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class LivingEntity extends Entity {
	private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final UUID SOUL_SPEED_BOOST_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
	private static final UUID POWDER_SNOW_SLOW_ID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");
	private static final EntityAttributeModifier SPRINTING_SPEED_BOOST = new EntityAttributeModifier(
		SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
	);
	public static final int field_30069 = 2;
	public static final int field_30070 = 4;
	public static final int field_30071 = 98;
	public static final int field_30072 = 100;
	public static final int field_30073 = 6;
	public static final int field_30074 = 100;
	private static final int field_30078 = 40;
	public static final double field_30075 = 0.003;
	public static final double field_30076 = 0.08;
	public static final int field_30077 = 20;
	private static final int field_30079 = 7;
	private static final int field_30080 = 10;
	private static final int field_30081 = 2;
	public static final int field_30063 = 4;
	private static final double field_33908 = 128.0;
	protected static final int USING_ITEM_FLAG = 1;
	protected static final int OFF_HAND_ACTIVE_FLAG = 2;
	protected static final int USING_RIPTIDE_FLAG = 4;
	protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(
		LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS
	);
	protected static final float field_30067 = 1.74F;
	protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F);
	public static final float field_30068 = 0.5F;
	private final AttributeContainer attributes;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
	private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public boolean handSwinging;
	private boolean noDrag = false;
	public Hand preferredHand;
	public int handSwingTicks;
	public int stuckArrowTimer;
	public int stuckStingerTimer;
	public int hurtTime;
	public int maxHurtTime;
	public float knockbackVelocity;
	public int deathTime;
	public float lastHandSwingProgress;
	public float handSwingProgress;
	protected int lastAttackedTicks;
	public float lastLimbDistance;
	public float limbDistance;
	public float limbAngle;
	public final int defaultMaxHealth = 20;
	public final float randomLargeSeed;
	public final float randomSmallSeed;
	public float bodyYaw;
	public float prevBodyYaw;
	public float headYaw;
	public float prevHeadYaw;
	public float flyingSpeed = 0.02F;
	@Nullable
	protected PlayerEntity attackingPlayer;
	protected int playerHitTimer;
	protected boolean dead;
	protected int despawnCounter;
	protected float prevStepBobbingAmount;
	protected float stepBobbingAmount;
	protected float lookDirection;
	protected float prevLookDirection;
	protected float field_6215;
	protected int scoreAmount;
	protected float lastDamageTaken;
	protected boolean jumping;
	public float sidewaysSpeed;
	public float upwardSpeed;
	public float forwardSpeed;
	protected int bodyTrackingIncrements;
	protected double serverX;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverPitch;
	protected double serverHeadYaw;
	protected int headTrackingIncrements;
	private boolean effectsChanged = true;
	@Nullable
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int jumpingCooldown;
	private float absorptionAmount;
	protected ItemStack activeItemStack = ItemStack.EMPTY;
	protected int itemUseTimeLeft;
	protected int roll;
	private BlockPos lastBlockPos;
	private Optional<BlockPos> climbingPos = Optional.empty();
	@Nullable
	private DamageSource lastDamageSource;
	private long lastDamageTime;
	protected int riptideTicks;
	private float leaningPitch;
	private float lastLeaningPitch;
	protected Brain<?> brain;

	protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		this.attributes = new AttributeContainer(DefaultAttributeRegistry.get(entityType));
		this.setHealth(this.getMaxHealth());
		this.inanimate = true;
		this.randomSmallSeed = (float)((Math.random() + 1.0) * 0.01F);
		this.refreshPosition();
		this.randomLargeSeed = (float)Math.random() * 12398.0F;
		this.setYaw((float)(Math.random() * (float) (Math.PI * 2)));
		this.headYaw = this.getYaw();
		this.stepHeight = 0.6F;
		NbtOps nbtOps = NbtOps.INSTANCE;
		this.brain = this.deserializeBrain(
			new Dynamic(nbtOps, (NbtElement)nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), (NbtElement)nbtOps.emptyMap())))
		);
	}

	public Brain<?> getBrain() {
		return this.brain;
	}

	protected Brain.Profile<?> createBrainProfile() {
		return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
	}

	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return this.createBrainProfile().deserialize(dynamic);
	}

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public boolean canTarget(EntityType<?> type) {
		return true;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(LIVING_FLAGS, (byte)0);
		this.dataTracker.startTracking(POTION_SWIRLS_COLOR, 0);
		this.dataTracker.startTracking(POTION_SWIRLS_AMBIENT, false);
		this.dataTracker.startTracking(STUCK_ARROW_COUNT, 0);
		this.dataTracker.startTracking(STINGER_COUNT, 0);
		this.dataTracker.startTracking(HEALTH, 1.0F);
		this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
	}

	public static DefaultAttributeContainer.Builder createLivingAttributes() {
		return DefaultAttributeContainer.builder()
			.add(EntityAttributes.GENERIC_MAX_HEALTH)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED)
			.add(EntityAttributes.GENERIC_ARMOR)
			.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
		if (!this.isTouchingWater()) {
			this.checkWaterState();
		}

		if (!this.world.isClient && onGround && this.fallDistance > 0.0F) {
			this.removeSoulSpeedBoost();
			this.addSoulSpeedBoostIfNeeded();
		}

		if (!this.world.isClient && this.fallDistance > 3.0F && onGround) {
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (!landedState.isAir()) {
				double d = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * d);
				((ServerWorld)this.world)
					.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, landedState), this.getX(), this.getY(), this.getZ(), i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.fall(heightDifference, onGround, landedState, landedPosition);
	}

	public boolean canBreatheInWater() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	public float getLeaningPitch(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLeaningPitch, this.leaningPitch);
	}

	@Override
	public void baseTick() {
		this.lastHandSwingProgress = this.handSwingProgress;
		if (this.firstUpdate) {
			this.getSleepingPosition().ifPresent(this::setPositionInBed);
		}

		if (this.shouldDisplaySoulSpeedEffects()) {
			this.displaySoulSpeedEffects();
		}

		super.baseTick();
		this.world.getProfiler().push("livingEntityBaseTick");
		boolean bl = this instanceof PlayerEntity;
		if (this.isAlive()) {
			if (this.isInsideWall()) {
				this.damage(DamageSource.IN_WALL, 1.0F);
			} else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
				double d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getSafeZone();
				if (d < 0.0) {
					double e = this.world.getWorldBorder().getDamagePerBlock();
					if (e > 0.0) {
						this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d * e)));
					}
				}
			}
		}

		if (this.isFireImmune() || this.world.isClient) {
			this.extinguish();
		}

		boolean bl2 = bl && ((PlayerEntity)this).getAbilities().invulnerable;
		if (this.isAlive()) {
			if (this.isSubmergedIn(FluidTags.WATER) && !this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
				if (!this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && !bl2) {
					this.setAir(this.getNextAirUnderwater(this.getAir()));
					if (this.getAir() == -20) {
						this.setAir(0);
						Vec3d vec3d = this.getVelocity();

						for (int i = 0; i < 8; i++) {
							double f = this.random.nextDouble() - this.random.nextDouble();
							double g = this.random.nextDouble() - this.random.nextDouble();
							double h = this.random.nextDouble() - this.random.nextDouble();
							this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + f, this.getY() + g, this.getZ() + h, vec3d.x, vec3d.y, vec3d.z);
						}

						this.damage(DamageSource.DROWN, 2.0F);
					}
				}

				if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
					this.stopRiding();
				}
			} else if (this.getAir() < this.getMaxAir()) {
				this.setAir(this.getNextAirOnLand(this.getAir()));
			}

			if (!this.world.isClient) {
				BlockPos blockPos = this.getBlockPos();
				if (!Objects.equal(this.lastBlockPos, blockPos)) {
					this.lastBlockPos = blockPos;
					this.applyMovementEffects(blockPos);
				}
			}
		}

		if (this.isAlive() && (this.isWet() || this.inPowderSnow)) {
			if (!this.world.isClient && this.wasOnFire) {
				this.playExtinguishSound();
			}

			this.extinguish();
		}

		if (this.hurtTime > 0) {
			this.hurtTime--;
		}

		if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
			this.timeUntilRegen--;
		}

		if (this.isDead()) {
			this.updatePostDeath();
		}

		if (this.playerHitTimer > 0) {
			this.playerHitTimer--;
		} else {
			this.attackingPlayer = null;
		}

		if (this.attacking != null && !this.attacking.isAlive()) {
			this.attacking = null;
		}

		if (this.attacker != null) {
			if (!this.attacker.isAlive()) {
				this.setAttacker(null);
			} else if (this.age - this.lastAttackedTime > 100) {
				this.setAttacker(null);
			}
		}

		this.tickStatusEffects();
		this.prevLookDirection = this.lookDirection;
		this.prevBodyYaw = this.bodyYaw;
		this.prevHeadYaw = this.headYaw;
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
		this.world.getProfiler().pop();
	}

	public boolean shouldDisplaySoulSpeedEffects() {
		return this.age % 5 == 0
			&& this.getVelocity().x != 0.0
			&& this.getVelocity().z != 0.0
			&& !this.isSpectator()
			&& EnchantmentHelper.hasSoulSpeed(this)
			&& this.isOnSoulSpeedBlock();
	}

	protected void displaySoulSpeedEffects() {
		Vec3d vec3d = this.getVelocity();
		this.world
			.addParticle(
				ParticleTypes.SOUL,
				this.getX() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
				this.getY() + 0.1,
				this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
				vec3d.x * -0.2,
				0.1,
				vec3d.z * -0.2
			);
		float f = this.random.nextFloat() * 0.4F + this.random.nextFloat() > 0.9F ? 0.6F : 0.0F;
		this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, f, 0.6F + this.random.nextFloat() * 0.4F);
	}

	protected boolean isOnSoulSpeedBlock() {
		return this.world.getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
	}

	@Override
	protected float getVelocityMultiplier() {
		return this.isOnSoulSpeedBlock() && EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this) > 0 ? 1.0F : super.getVelocityMultiplier();
	}

	protected boolean shouldRemoveSoulSpeedBoost(BlockState landingState) {
		return !landingState.isAir() || this.isFallFlying();
	}

	protected void removeSoulSpeedBoost() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance != null) {
			if (entityAttributeInstance.getModifier(SOUL_SPEED_BOOST_ID) != null) {
				entityAttributeInstance.removeModifier(SOUL_SPEED_BOOST_ID);
			}
		}
	}

	protected void addSoulSpeedBoostIfNeeded() {
		if (!this.getLandingBlockState().isAir()) {
			int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this);
			if (i > 0 && this.isOnSoulSpeedBlock()) {
				EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				if (entityAttributeInstance == null) {
					return;
				}

				entityAttributeInstance.addTemporaryModifier(
					new EntityAttributeModifier(
						SOUL_SPEED_BOOST_ID, "Soul speed boost", (double)(0.03F * (1.0F + (float)i * 0.35F)), EntityAttributeModifier.Operation.ADDITION
					)
				);
				if (this.getRandom().nextFloat() < 0.04F) {
					ItemStack itemStack = this.getEquippedStack(EquipmentSlot.FEET);
					itemStack.damage(1, this, player -> player.sendEquipmentBreakStatus(EquipmentSlot.FEET));
				}
			}
		}
	}

	protected void removePowderSnowSlow() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance != null) {
			if (entityAttributeInstance.getModifier(POWDER_SNOW_SLOW_ID) != null) {
				entityAttributeInstance.removeModifier(POWDER_SNOW_SLOW_ID);
			}
		}
	}

	protected void addPowderSnowSlowIfNeeded() {
		if (!this.getLandingBlockState().isAir()) {
			int i = this.getFrozenTicks();
			if (i > 0) {
				EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				if (entityAttributeInstance == null) {
					return;
				}

				float f = -0.05F * this.getFreezingScale();
				entityAttributeInstance.addTemporaryModifier(
					new EntityAttributeModifier(POWDER_SNOW_SLOW_ID, "Powder snow slow", (double)f, EntityAttributeModifier.Operation.ADDITION)
				);
			}
		}
	}

	protected void applyMovementEffects(BlockPos pos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
		if (i > 0) {
			FrostWalkerEnchantment.freezeWater(this, this.world, pos, i);
		}

		if (this.shouldRemoveSoulSpeedBoost(this.getLandingBlockState())) {
			this.removeSoulSpeedBoost();
		}

		this.addSoulSpeedBoostIfNeeded();
	}

	public boolean isBaby() {
		return false;
	}

	public float getScaleFactor() {
		return this.isBaby() ? 0.5F : 1.0F;
	}

	protected boolean shouldSwimInFluids() {
		return true;
	}

	@Override
	public boolean canBeRiddenInWater() {
		return false;
	}

	protected void updatePostDeath() {
		this.deathTime++;
		if (this.deathTime == 20 && !this.world.isClient()) {
			this.world.sendEntityStatus(this, (byte)60);
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	protected boolean shouldDropXp() {
		return !this.isBaby();
	}

	protected boolean shouldDropLoot() {
		return !this.isBaby();
	}

	protected int getNextAirUnderwater(int air) {
		int i = EnchantmentHelper.getRespiration(this);
		return i > 0 && this.random.nextInt(i + 1) > 0 ? air : air - 1;
	}

	protected int getNextAirOnLand(int air) {
		return Math.min(air + 4, this.getMaxAir());
	}

	protected int getXpToDrop(PlayerEntity player) {
		return 0;
	}

	protected boolean shouldAlwaysDropXp() {
		return false;
	}

	public Random getRandom() {
		return this.random;
	}

	@Nullable
	public LivingEntity getAttacker() {
		return this.attacker;
	}

	public int getLastAttackedTime() {
		return this.lastAttackedTime;
	}

	public void setAttacking(@Nullable PlayerEntity attacking) {
		this.attackingPlayer = attacking;
		this.playerHitTimer = this.age;
	}

	public void setAttacker(@Nullable LivingEntity attacker) {
		this.attacker = attacker;
		this.lastAttackedTime = this.age;
	}

	@Nullable
	public LivingEntity getAttacking() {
		return this.attacking;
	}

	public int getLastAttackTime() {
		return this.lastAttackTime;
	}

	public void onAttacking(Entity target) {
		if (target instanceof LivingEntity) {
			this.attacking = (LivingEntity)target;
		} else {
			this.attacking = null;
		}

		this.lastAttackTime = this.age;
	}

	public int getDespawnCounter() {
		return this.despawnCounter;
	}

	public void setDespawnCounter(int despawnCounter) {
		this.despawnCounter = despawnCounter;
	}

	public boolean hasNoDrag() {
		return this.noDrag;
	}

	public void setNoDrag(boolean noDrag) {
		this.noDrag = noDrag;
	}

	protected void onEquipStack(ItemStack stack) {
		SoundEvent soundEvent = stack.getEquipSound();
		if (!stack.isEmpty() && soundEvent != null && !this.isSpectator()) {
			this.emitGameEvent(GameEvent.EQUIP);
			this.playSound(soundEvent, 1.0F, 1.0F);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("Health", this.getHealth());
		nbt.putShort("HurtTime", (short)this.hurtTime);
		nbt.putInt("HurtByTimestamp", this.lastAttackedTime);
		nbt.putShort("DeathTime", (short)this.deathTime);
		nbt.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
		nbt.put("Attributes", this.getAttributes().toNbt());
		if (!this.activeStatusEffects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
				nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
			}

			nbt.put("ActiveEffects", nbtList);
		}

		nbt.putBoolean("FallFlying", this.isFallFlying());
		this.getSleepingPosition().ifPresent(pos -> {
			nbt.putInt("SleepingX", pos.getX());
			nbt.putInt("SleepingY", pos.getY());
			nbt.putInt("SleepingZ", pos.getZ());
		});
		DataResult<NbtElement> dataResult = this.brain.encode(NbtOps.INSTANCE);
		dataResult.resultOrPartial(LOGGER::error).ifPresent(brain -> nbt.put("Brain", brain));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setAbsorptionAmount(nbt.getFloat("AbsorptionAmount"));
		if (nbt.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
			this.getAttributes().readNbt(nbt.getList("Attributes", 10));
		}

		if (nbt.contains("ActiveEffects", 9)) {
			NbtList nbtList = nbt.getList("ActiveEffects", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				}
			}
		}

		if (nbt.contains("Health", 99)) {
			this.setHealth(nbt.getFloat("Health"));
		}

		this.hurtTime = nbt.getShort("HurtTime");
		this.deathTime = nbt.getShort("DeathTime");
		this.lastAttackedTime = nbt.getInt("HurtByTimestamp");
		if (nbt.contains("Team", 8)) {
			String string = nbt.getString("Team");
			Team team = this.world.getScoreboard().getTeam(string);
			boolean bl = team != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), team);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (nbt.getBoolean("FallFlying")) {
			this.setFlag(7, true);
		}

		if (nbt.contains("SleepingX", 99) && nbt.contains("SleepingY", 99) && nbt.contains("SleepingZ", 99)) {
			BlockPos blockPos = new BlockPos(nbt.getInt("SleepingX"), nbt.getInt("SleepingY"), nbt.getInt("SleepingZ"));
			this.setSleepingPosition(blockPos);
			this.dataTracker.set(POSE, EntityPose.SLEEPING);
			if (!this.firstUpdate) {
				this.setPositionInBed(blockPos);
			}
		}

		if (nbt.contains("Brain", 10)) {
			this.brain = this.deserializeBrain(new Dynamic(NbtOps.INSTANCE, nbt.get("Brain")));
		}
	}

	protected void tickStatusEffects() {
		Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				StatusEffect statusEffect = (StatusEffect)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(statusEffect);
				if (!statusEffectInstance.update(this, () -> this.onStatusEffectUpgraded(statusEffectInstance, true, null))) {
					if (!this.world.isClient) {
						iterator.remove();
						this.onStatusEffectRemoved(statusEffectInstance);
					}
				} else if (statusEffectInstance.getDuration() % 600 == 0) {
					this.onStatusEffectUpgraded(statusEffectInstance, false, null);
				}
			}
		} catch (ConcurrentModificationException var11) {
		}

		if (this.effectsChanged) {
			if (!this.world.isClient) {
				this.updatePotionVisibility();
				this.updateGlowing();
			}

			this.effectsChanged = false;
		}

		int i = this.dataTracker.get(POTION_SWIRLS_COLOR);
		boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
		if (i > 0) {
			boolean bl2;
			if (this.isInvisible()) {
				bl2 = this.random.nextInt(15) == 0;
			} else {
				bl2 = this.random.nextBoolean();
			}

			if (bl) {
				bl2 &= this.random.nextInt(5) == 0;
			}

			if (bl2 && i > 0) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;
				this.world
					.addParticle(
						bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f
					);
			}
		}
	}

	protected void updatePotionVisibility() {
		if (this.activeStatusEffects.isEmpty()) {
			this.clearPotionSwirls();
			this.setInvisible(false);
		} else {
			Collection<StatusEffectInstance> collection = this.activeStatusEffects.values();
			this.dataTracker.set(POTION_SWIRLS_AMBIENT, containsOnlyAmbientEffects(collection));
			this.dataTracker.set(POTION_SWIRLS_COLOR, PotionUtil.getColor(collection));
			this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
		}
	}

	private void updateGlowing() {
		boolean bl = this.isGlowing();
		if (this.getFlag(6) != bl) {
			this.setFlag(6, bl);
		}
	}

	public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
		double d = 1.0;
		if (this.isSneaky()) {
			d *= 0.8;
		}

		if (this.isInvisible()) {
			float f = this.getArmorVisibility();
			if (f < 0.1F) {
				f = 0.1F;
			}

			d *= 0.7 * (double)f;
		}

		if (entity != null) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
			EntityType<?> entityType = entity.getType();
			if (entityType == EntityType.SKELETON && itemStack.isOf(Items.SKELETON_SKULL)
				|| entityType == EntityType.ZOMBIE && itemStack.isOf(Items.ZOMBIE_HEAD)
				|| entityType == EntityType.CREEPER && itemStack.isOf(Items.CREEPER_HEAD)) {
				d *= 0.5;
			}
		}

		return d;
	}

	public boolean canTarget(LivingEntity target) {
		return target instanceof PlayerEntity && this.world.getDifficulty() == Difficulty.PEACEFUL ? false : target.canTakeDamage();
	}

	public boolean isTarget(LivingEntity entity, TargetPredicate predicate) {
		return predicate.test(this, entity);
	}

	public boolean canTakeDamage() {
		return !this.isInvulnerable() && this.isPartOfGame();
	}

	public boolean isPartOfGame() {
		return !this.isSpectator() && this.isAlive();
	}

	public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> effects) {
		for (StatusEffectInstance statusEffectInstance : effects) {
			if (!statusEffectInstance.isAmbient()) {
				return false;
			}
		}

		return true;
	}

	protected void clearPotionSwirls() {
		this.dataTracker.set(POTION_SWIRLS_AMBIENT, false);
		this.dataTracker.set(POTION_SWIRLS_COLOR, 0);
	}

	public boolean clearStatusEffects() {
		if (this.world.isClient) {
			return false;
		} else {
			Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();

			boolean bl;
			for (bl = false; iterator.hasNext(); bl = true) {
				this.onStatusEffectRemoved((StatusEffectInstance)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<StatusEffectInstance> getStatusEffects() {
		return this.activeStatusEffects.values();
	}

	public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects() {
		return this.activeStatusEffects;
	}

	public boolean hasStatusEffect(StatusEffect effect) {
		return this.activeStatusEffects.containsKey(effect);
	}

	@Nullable
	public StatusEffectInstance getStatusEffect(StatusEffect effect) {
		return (StatusEffectInstance)this.activeStatusEffects.get(effect);
	}

	public final boolean addStatusEffect(StatusEffectInstance effect) {
		return this.addStatusEffect(effect, null);
	}

	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		if (!this.canHaveStatusEffect(effect)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(effect.getEffectType());
			if (statusEffectInstance == null) {
				this.activeStatusEffects.put(effect.getEffectType(), effect);
				this.onStatusEffectApplied(effect, source);
				return true;
			} else if (statusEffectInstance.upgrade(effect)) {
				this.onStatusEffectUpgraded(statusEffectInstance, true, source);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		if (this.getGroup() == EntityGroup.UNDEAD) {
			StatusEffect statusEffect = effect.getEffectType();
			if (statusEffect == StatusEffects.REGENERATION || statusEffect == StatusEffects.POISON) {
				return false;
			}
		}

		return true;
	}

	public void setStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		if (this.canHaveStatusEffect(effect)) {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.put(effect.getEffectType(), effect);
			if (statusEffectInstance == null) {
				this.onStatusEffectApplied(effect, source);
			} else {
				this.onStatusEffectUpgraded(effect, true, source);
			}
		}
	}

	public boolean isUndead() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	@Nullable
	public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect type) {
		return (StatusEffectInstance)this.activeStatusEffects.remove(type);
	}

	public boolean removeStatusEffect(StatusEffect type) {
		StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(type);
		if (statusEffectInstance != null) {
			this.onStatusEffectRemoved(statusEffectInstance);
			return true;
		} else {
			return false;
		}
	}

	protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			effect.getEffectType().onApplied(this, this.getAttributes(), effect.getAmplifier());
		}
	}

	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
		this.effectsChanged = true;
		if (reapplyEffect && !this.world.isClient) {
			StatusEffect statusEffect = effect.getEffectType();
			statusEffect.onRemoved(this, this.getAttributes(), effect.getAmplifier());
			statusEffect.onApplied(this, this.getAttributes(), effect.getAmplifier());
		}
	}

	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			effect.getEffectType().onRemoved(this, this.getAttributes(), effect.getAmplifier());
		}
	}

	public void heal(float amount) {
		float f = this.getHealth();
		if (f > 0.0F) {
			this.setHealth(f + amount);
		}
	}

	public float getHealth() {
		return this.dataTracker.get(HEALTH);
	}

	public void setHealth(float health) {
		this.dataTracker.set(HEALTH, MathHelper.clamp(health, 0.0F, this.getMaxHealth()));
	}

	public boolean isDead() {
		return this.getHealth() <= 0.0F;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.world.isClient) {
			return false;
		} else if (this.isDead()) {
			return false;
		} else if (source.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
			return false;
		} else {
			if (this.isSleeping() && !this.world.isClient) {
				this.wakeUp();
			}

			this.despawnCounter = 0;
			float f = amount;
			boolean bl = false;
			float g = 0.0F;
			if (amount > 0.0F && this.blockedByShield(source)) {
				this.damageShield(amount);
				g = amount;
				amount = 0.0F;
				if (!source.isProjectile()) {
					Entity entity = source.getSource();
					if (entity instanceof LivingEntity) {
						this.takeShieldHit((LivingEntity)entity);
					}
				}

				bl = true;
			}

			this.limbDistance = 1.5F;
			boolean bl2 = true;
			if ((float)this.timeUntilRegen > 10.0F) {
				if (amount <= this.lastDamageTaken) {
					return false;
				}

				this.applyDamage(source, amount - this.lastDamageTaken);
				this.lastDamageTaken = amount;
				bl2 = false;
			} else {
				this.lastDamageTaken = amount;
				this.timeUntilRegen = 20;
				this.applyDamage(source, amount);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}

			if (source.isFallingBlock() && !this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
				this.damageHelmet(source, amount);
				amount *= 0.75F;
			}

			this.knockbackVelocity = 0.0F;
			Entity entity2 = source.getAttacker();
			if (entity2 != null) {
				if (entity2 instanceof LivingEntity && !source.isNeutral()) {
					this.setAttacker((LivingEntity)entity2);
				}

				if (entity2 instanceof PlayerEntity) {
					this.playerHitTimer = 100;
					this.attackingPlayer = (PlayerEntity)entity2;
				} else if (entity2 instanceof WolfEntity wolfEntity && wolfEntity.isTamed()) {
					this.playerHitTimer = 100;
					LivingEntity livingEntity = wolfEntity.getOwner();
					if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
						this.attackingPlayer = (PlayerEntity)livingEntity;
					} else {
						this.attackingPlayer = null;
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.world.sendEntityStatus(this, (byte)29);
				} else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
					this.world.sendEntityStatus(this, (byte)33);
				} else {
					byte b;
					if (source == DamageSource.DROWN) {
						b = 36;
					} else if (source.isFire()) {
						b = 37;
					} else if (source == DamageSource.SWEET_BERRY_BUSH) {
						b = 44;
					} else if (source == DamageSource.FREEZE) {
						b = 57;
					} else {
						b = 2;
					}

					this.world.sendEntityStatus(this, b);
				}

				if (source != DamageSource.DROWN && (!bl || amount > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity2 != null) {
					double i = entity2.getX() - this.getX();

					double j;
					for (j = entity2.getZ() - this.getZ(); i * i + j * j < 1.0E-4; j = (Math.random() - Math.random()) * 0.01) {
						i = (Math.random() - Math.random()) * 0.01;
					}

					this.knockbackVelocity = (float)(MathHelper.atan2(j, i) * 180.0F / (float)Math.PI - (double)this.getYaw());
					this.takeKnockback(0.4F, i, j);
				} else {
					this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.isDead()) {
				if (!this.tryUseTotem(source)) {
					SoundEvent soundEvent = this.getDeathSound();
					if (bl2 && soundEvent != null) {
						this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(source);
				}
			} else if (bl2) {
				this.playHurtSound(source);
			}

			boolean bl3 = !bl || amount > 0.0F;
			if (bl3) {
				this.lastDamageSource = source;
				this.lastDamageTime = this.world.getTime();
			}

			if (this instanceof ServerPlayerEntity) {
				Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, source, f, amount, bl);
				if (g > 0.0F && g < 3.4028235E37F) {
					((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
				}
			}

			if (entity2 instanceof ServerPlayerEntity) {
				Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, source, f, amount, bl);
			}

			return bl3;
		}
	}

	protected void takeShieldHit(LivingEntity attacker) {
		attacker.knockback(this);
	}

	protected void knockback(LivingEntity target) {
		target.takeKnockback(0.5, target.getX() - this.getX(), target.getZ() - this.getZ());
	}

	private boolean tryUseTotem(DamageSource source) {
		if (source.isOutOfWorld()) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.getStackInHand(hand);
				if (itemStack2.isOf(Items.TOTEM_OF_UNDYING)) {
					itemStack = itemStack2.copy();
					itemStack2.decrement(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
					Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
				}

				this.setHealth(1.0F);
				this.clearStatusEffects();
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
				this.world.sendEntityStatus(this, (byte)35);
			}

			return itemStack != null;
		}
	}

	@Nullable
	public DamageSource getRecentDamageSource() {
		if (this.world.getTime() - this.lastDamageTime > 40L) {
			this.lastDamageSource = null;
		}

		return this.lastDamageSource;
	}

	protected void playHurtSound(DamageSource source) {
		SoundEvent soundEvent = this.getHurtSound(source);
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean blockedByShield(DamageSource source) {
		Entity entity = source.getSource();
		boolean bl = false;
		if (entity instanceof PersistentProjectileEntity persistentProjectileEntity && persistentProjectileEntity.getPierceLevel() > 0) {
			bl = true;
		}

		if (!source.bypassesArmor() && this.isBlocking() && !bl) {
			Vec3d vec3d = source.getPosition();
			if (vec3d != null) {
				Vec3d vec3d2 = this.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.relativize(this.getPos()).normalize();
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
				if (vec3d3.dotProduct(vec3d2) < 0.0) {
					return true;
				}
			}
		}

		return false;
	}

	private void playEquipmentBreakEffects(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F, false
					);
			}

			this.spawnItemParticles(stack, 5);
		}
	}

	public void onDeath(DamageSource source) {
		if (!this.isRemoved() && !this.dead) {
			Entity entity = source.getAttacker();
			LivingEntity livingEntity = this.getPrimeAdversary();
			if (this.scoreAmount >= 0 && livingEntity != null) {
				livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, source);
			}

			if (this.isSleeping()) {
				this.wakeUp();
			}

			if (!this.world.isClient && this.hasCustomName()) {
				LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString());
			}

			this.dead = true;
			this.getDamageTracker().update();
			if (this.world instanceof ServerWorld) {
				if (entity != null) {
					entity.onKilledOther((ServerWorld)this.world, this);
				}

				this.drop(source);
				this.onKilledBy(livingEntity);
			}

			this.world.sendEntityStatus(this, (byte)3);
			this.setPose(EntityPose.DYING);
		}
	}

	protected void onKilledBy(@Nullable LivingEntity adversary) {
		if (!this.world.isClient) {
			boolean bl = false;
			if (adversary instanceof WitherEntity) {
				if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					BlockPos blockPos = this.getBlockPos();
					BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
					if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
						this.world.setBlockState(blockPos, blockState, 3);
						bl = true;
					}
				}

				if (!bl) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
					this.world.spawnEntity(itemEntity);
				}
			}
		}
	}

	protected void drop(DamageSource source) {
		Entity entity = source.getAttacker();
		int i;
		if (entity instanceof PlayerEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		} else {
			i = 0;
		}

		boolean bl = this.playerHitTimer > 0;
		if (this.shouldDropLoot() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			this.dropLoot(source, bl);
			this.dropEquipment(source, i, bl);
		}

		this.dropInventory();
		this.dropXp();
	}

	protected void dropInventory() {
	}

	protected void dropXp() {
		if (this.world instanceof ServerWorld
			&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
			ExperienceOrbEntity.spawn((ServerWorld)this.world, this.getPos(), this.getXpToDrop(this.attackingPlayer));
		}
	}

	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
	}

	public Identifier getLootTable() {
		return this.getType().getLootTableId();
	}

	protected void dropLoot(DamageSource source, boolean causedByPlayer) {
		Identifier identifier = this.getLootTable();
		LootTable lootTable = this.world.getServer().getLootManager().getTable(identifier);
		LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);
		lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), this::dropStack);
	}

	protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
		LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
			.random(this.random)
			.parameter(LootContextParameters.THIS_ENTITY, this)
			.parameter(LootContextParameters.ORIGIN, this.getPos())
			.parameter(LootContextParameters.DAMAGE_SOURCE, source)
			.optionalParameter(LootContextParameters.KILLER_ENTITY, source.getAttacker())
			.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, source.getSource());
		if (causedByPlayer && this.attackingPlayer != null) {
			builder = builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
		}

		return builder;
	}

	public void takeKnockback(double strength, double x, double z) {
		strength *= 1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		if (!(strength <= 0.0)) {
			this.velocityDirty = true;
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
			this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + strength) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
		}
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_GENERIC_HURT;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_GENERIC_DEATH;
	}

	protected SoundEvent getFallSound(int distance) {
		return distance > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL;
	}

	protected SoundEvent getDrinkSound(ItemStack stack) {
		return stack.getDrinkSound();
	}

	public SoundEvent getEatSound(ItemStack stack) {
		return stack.getEatSound();
	}

	@Override
	public void setOnGround(boolean onGround) {
		super.setOnGround(onGround);
		if (onGround) {
			this.climbingPos = Optional.empty();
		}
	}

	public Optional<BlockPos> getClimbingPos() {
		return this.climbingPos;
	}

	public boolean isClimbing() {
		if (this.isSpectator()) {
			return false;
		} else {
			BlockPos blockPos = this.getBlockPos();
			BlockState blockState = this.getBlockStateAtPos();
			if (blockState.isIn(BlockTags.CLIMBABLE)) {
				this.climbingPos = Optional.of(blockPos);
				return true;
			} else if (blockState.getBlock() instanceof TrapdoorBlock && this.canEnterTrapdoor(blockPos, blockState)) {
				this.climbingPos = Optional.of(blockPos);
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean canEnterTrapdoor(BlockPos pos, BlockState state) {
		if ((Boolean)state.get(TrapdoorBlock.OPEN)) {
			BlockState blockState = this.world.getBlockState(pos.down());
			if (blockState.isOf(Blocks.LADDER) && blockState.get(LadderBlock.FACING) == state.get(TrapdoorBlock.FACING)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAlive() {
		return !this.isRemoved() && this.getHealth() > 0.0F;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		boolean bl = super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
		int i = this.computeFallDamage(fallDistance, damageMultiplier);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.playBlockFallSound();
			this.damage(damageSource, (float)i);
			return true;
		} else {
			return bl;
		}
	}

	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.JUMP_BOOST);
		float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
		return MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
	}

	protected void playBlockFallSound() {
		if (!this.isSilent()) {
			int i = MathHelper.floor(this.getX());
			int j = MathHelper.floor(this.getY() - 0.2F);
			int k = MathHelper.floor(this.getZ());
			BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
			}
		}
	}

	@Override
	public void animateDamage() {
		this.maxHurtTime = 10;
		this.hurtTime = this.maxHurtTime;
		this.knockbackVelocity = 0.0F;
	}

	public int getArmor() {
		return MathHelper.floor(this.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
	}

	protected void damageArmor(DamageSource source, float amount) {
	}

	protected void damageHelmet(DamageSource source, float amount) {
	}

	protected void damageShield(float amount) {
	}

	protected float applyArmorToDamage(DamageSource source, float amount) {
		if (!source.bypassesArmor()) {
			this.damageArmor(source, amount);
			amount = DamageUtil.getDamageLeft(amount, (float)this.getArmor(), (float)this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
		}

		return amount;
	}

	protected float applyEnchantmentsToDamage(DamageSource source, float amount) {
		if (source.isUnblockable()) {
			return amount;
		} else {
			if (this.hasStatusEffect(StatusEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
				int i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = amount * (float)j;
				float g = amount;
				amount = Math.max(f / 25.0F, 0.0F);
				float h = g - amount;
				if (h > 0.0F && h < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
					} else if (source.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
					}
				}
			}

			if (amount <= 0.0F) {
				return 0.0F;
			} else {
				int k = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), source);
				if (k > 0) {
					amount = DamageUtil.getInflictedDamage(amount, (float)k);
				}

				return amount;
			}
		}
	}

	protected void applyDamage(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			amount = this.applyArmorToDamage(source, amount);
			amount = this.applyEnchantmentsToDamage(source, amount);
			float var8 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var8));
			float g = amount - var8;
			if (g > 0.0F && g < 3.4028235E37F && source.getAttacker() instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
			}

			if (var8 != 0.0F) {
				float h = this.getHealth();
				this.setHealth(h - var8);
				this.getDamageTracker().onDamage(source, h, var8);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - var8);
				this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
			}
		}
	}

	public DamageTracker getDamageTracker() {
		return this.damageTracker;
	}

	@Nullable
	public LivingEntity getPrimeAdversary() {
		if (this.damageTracker.getBiggestAttacker() != null) {
			return this.damageTracker.getBiggestAttacker();
		} else if (this.attackingPlayer != null) {
			return this.attackingPlayer;
		} else {
			return this.attacker != null ? this.attacker : null;
		}
	}

	public final float getMaxHealth() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
	}

	public final int getStuckArrowCount() {
		return this.dataTracker.get(STUCK_ARROW_COUNT);
	}

	public final void setStuckArrowCount(int stuckArrowCount) {
		this.dataTracker.set(STUCK_ARROW_COUNT, stuckArrowCount);
	}

	public final int getStingerCount() {
		return this.dataTracker.get(STINGER_COUNT);
	}

	public final void setStingerCount(int stingerCount) {
		this.dataTracker.set(STINGER_COUNT, stingerCount);
	}

	private int getHandSwingDuration() {
		if (StatusEffectUtil.hasHaste(this)) {
			return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
		} else {
			return this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		this.swingHand(hand, false);
	}

	public void swingHand(Hand hand, boolean fromServerPlayer) {
		if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.handSwinging = true;
			this.preferredHand = hand;
			if (this.world instanceof ServerWorld) {
				EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(this, hand == Hand.MAIN_HAND ? 0 : 3);
				ServerChunkManager serverChunkManager = ((ServerWorld)this.world).getChunkManager();
				if (fromServerPlayer) {
					serverChunkManager.sendToNearbyPlayers(this, entityAnimationS2CPacket);
				} else {
					serverChunkManager.sendToOtherNearbyPlayers(this, entityAnimationS2CPacket);
				}
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		switch (status) {
			case 2:
			case 33:
			case 36:
			case 37:
			case 44:
			case 57:
				this.limbDistance = 1.5F;
				this.timeUntilRegen = 20;
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
				this.knockbackVelocity = 0.0F;
				if (status == 33) {
					this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				DamageSource damageSource;
				if (status == 37) {
					damageSource = DamageSource.ON_FIRE;
				} else if (status == 36) {
					damageSource = DamageSource.DROWN;
				} else if (status == 44) {
					damageSource = DamageSource.SWEET_BERRY_BUSH;
				} else if (status == 57) {
					damageSource = DamageSource.FREEZE;
				} else {
					damageSource = DamageSource.GENERIC;
				}

				SoundEvent soundEvent = this.getHurtSound(damageSource);
				if (soundEvent != null) {
					this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.damage(DamageSource.GENERIC, 0.0F);
				this.lastDamageSource = damageSource;
				this.lastDamageTime = this.world.getTime();
				break;
			case 3:
				SoundEvent soundEvent2 = this.getDeathSound();
				if (soundEvent2 != null) {
					this.playSound(soundEvent2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				if (!(this instanceof PlayerEntity)) {
					this.setHealth(0.0F);
					this.onDeath(DamageSource.GENERIC);
				}
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 31:
			case 32:
			case 34:
			case 35:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
			case 43:
			case 45:
			case 53:
			case 56:
			case 58:
			case 59:
			default:
				super.handleStatus(status);
				break;
			case 29:
				this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.random.nextFloat() * 0.4F);
				break;
			case 30:
				this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
				break;
			case 46:
				int i = 128;

				for (int j = 0; j < 128; j++) {
					double d = (double)j / 127.0;
					float f = (this.random.nextFloat() - 0.5F) * 0.2F;
					float g = (this.random.nextFloat() - 0.5F) * 0.2F;
					float h = (this.random.nextFloat() - 0.5F) * 0.2F;
					double e = MathHelper.lerp(d, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					double k = MathHelper.lerp(d, this.prevY, this.getY()) + this.random.nextDouble() * (double)this.getHeight();
					double l = MathHelper.lerp(d, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					this.world.addParticle(ParticleTypes.PORTAL, e, k, l, (double)f, (double)g, (double)h);
				}
				break;
			case 47:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
				break;
			case 48:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
				break;
			case 49:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
				break;
			case 50:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
				break;
			case 51:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
				break;
			case 52:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
				break;
			case 54:
				HoneyBlock.addRichParticles(this);
				break;
			case 55:
				this.swapHandStacks();
				break;
			case 60:
				this.addDeathParticles();
		}
	}

	private void addDeathParticles() {
		for (int i = 0; i < 20; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world.addParticle(ParticleTypes.POOF, this.getParticleX(1.0), this.getRandomBodyY(), this.getParticleZ(1.0), d, e, f);
		}
	}

	private void swapHandStacks() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.OFFHAND);
		this.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
		this.equipStack(EquipmentSlot.MAINHAND, itemStack);
	}

	@Override
	protected void tickInVoid() {
		this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void tickHandSwing() {
		int i = this.getHandSwingDuration();
		if (this.handSwinging) {
			this.handSwingTicks++;
			if (this.handSwingTicks >= i) {
				this.handSwingTicks = 0;
				this.handSwinging = false;
			}
		} else {
			this.handSwingTicks = 0;
		}

		this.handSwingProgress = (float)this.handSwingTicks / (float)i;
	}

	@Nullable
	public EntityAttributeInstance getAttributeInstance(EntityAttribute attribute) {
		return this.getAttributes().getCustomInstance(attribute);
	}

	public double getAttributeValue(EntityAttribute attribute) {
		return this.getAttributes().getValue(attribute);
	}

	public double getAttributeBaseValue(EntityAttribute attribute) {
		return this.getAttributes().getBaseValue(attribute);
	}

	public AttributeContainer getAttributes() {
		return this.attributes;
	}

	public EntityGroup getGroup() {
		return EntityGroup.DEFAULT;
	}

	public ItemStack getMainHandStack() {
		return this.getEquippedStack(EquipmentSlot.MAINHAND);
	}

	public ItemStack getOffHandStack() {
		return this.getEquippedStack(EquipmentSlot.OFFHAND);
	}

	public boolean isHolding(Item item) {
		return this.isHolding(stack -> stack.isOf(item));
	}

	public boolean isHolding(Predicate<ItemStack> predicate) {
		return predicate.test(this.getMainHandStack()) || predicate.test(this.getOffHandStack());
	}

	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.getEquippedStack(EquipmentSlot.MAINHAND);
		} else if (hand == Hand.OFF_HAND) {
			return this.getEquippedStack(EquipmentSlot.OFFHAND);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void setStackInHand(Hand hand, ItemStack stack) {
		if (hand == Hand.MAIN_HAND) {
			this.equipStack(EquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != Hand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.equipStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	public boolean hasStackEquipped(EquipmentSlot slot) {
		return !this.getEquippedStack(slot).isEmpty();
	}

	@Override
	public abstract Iterable<ItemStack> getArmorItems();

	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Override
	public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

	protected void processEquippedStack(ItemStack stack) {
		NbtCompound nbtCompound = stack.getTag();
		if (nbtCompound != null) {
			stack.getItem().postProcessNbt(nbtCompound);
		}
	}

	public float getArmorVisibility() {
		Iterable<ItemStack> iterable = this.getArmorItems();
		int i = 0;
		int j = 0;

		for (ItemStack itemStack : iterable) {
			if (!itemStack.isEmpty()) {
				j++;
			}

			i++;
		}

		return i > 0 ? (float)j / (float)i : 0.0F;
	}

	@Override
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance.getModifier(SPRINTING_SPEED_BOOST_ID) != null) {
			entityAttributeInstance.removeModifier(SPRINTING_SPEED_BOOST);
		}

		if (sprinting) {
			entityAttributeInstance.addTemporaryModifier(SPRINTING_SPEED_BOOST);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	public float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean isImmobile() {
		return this.isDead();
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	private void onDismounted(Entity vehicle) {
		Vec3d vec3d;
		if (this.isRemoved()) {
			vec3d = this.getPos();
		} else if (!vehicle.isRemoved() && !this.world.getBlockState(vehicle.getBlockPos()).isIn(BlockTags.PORTALS)) {
			vec3d = vehicle.updatePassengerForDismount(this);
		} else {
			double d = Math.max(this.getY(), vehicle.getY());
			vec3d = new Vec3d(this.getX(), d, this.getZ());
		}

		this.requestTeleportAndDismount(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	protected float getJumpVelocity() {
		return 0.42F * this.getJumpVelocityMultiplier();
	}

	public double getJumpBoostVelocityModifier() {
		return this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? (double)(0.1F * (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1)) : 0.0;
	}

	protected void jump() {
		double d = (double)this.getJumpVelocity() + this.getJumpBoostVelocityModifier();
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, d, vec3d.z);
		if (this.isSprinting()) {
			float f = this.getYaw() * (float) (Math.PI / 180.0);
			this.setVelocity(this.getVelocity().add((double)(-MathHelper.sin(f) * 0.2F), 0.0, (double)(MathHelper.cos(f) * 0.2F)));
		}

		this.velocityDirty = true;
	}

	protected void knockDownwards() {
		this.setVelocity(this.getVelocity().add(0.0, -0.04F, 0.0));
	}

	protected void swimUpward(Tag<Fluid> fluid) {
		this.setVelocity(this.getVelocity().add(0.0, 0.04F, 0.0));
	}

	protected float getBaseMovementSpeedMultiplier() {
		return 0.8F;
	}

	public boolean canWalkOnFluid(Fluid fluid) {
		return false;
	}

	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
			double d = 0.08;
			boolean bl = this.getVelocity().y <= 0.0;
			if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
				d = 0.01;
				this.fallDistance = 0.0F;
			}

			FluidState fluidState = this.world.getFluidState(this.getBlockPos());
			if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
				double e = this.getY();
				float f = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
				float g = 0.02F;
				float h = (float)EnchantmentHelper.getDepthStrider(this);
				if (h > 3.0F) {
					h = 3.0F;
				}

				if (!this.onGround) {
					h *= 0.5F;
				}

				if (h > 0.0F) {
					f += (0.54600006F - f) * h / 3.0F;
					g += (this.getMovementSpeed() - g) * h / 3.0F;
				}

				if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
					f = 0.96F;
				}

				this.updateVelocity(g, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				Vec3d vec3d = this.getVelocity();
				if (this.horizontalCollision && this.isClimbing()) {
					vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
				}

				this.setVelocity(vec3d.multiply((double)f, 0.8F, (double)f));
				Vec3d vec3d2 = this.method_26317(d, bl, this.getVelocity());
				this.setVelocity(vec3d2);
				if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - this.getY() + e, vec3d2.z)) {
					this.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
				}
			} else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
				double i = this.getY();
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
					this.setVelocity(this.getVelocity().multiply(0.5, 0.8F, 0.5));
					Vec3d vec3d3 = this.method_26317(d, bl, this.getVelocity());
					this.setVelocity(vec3d3);
				} else {
					this.setVelocity(this.getVelocity().multiply(0.5));
				}

				if (!this.hasNoGravity()) {
					this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
				}

				Vec3d vec3d4 = this.getVelocity();
				if (this.horizontalCollision && this.doesNotCollide(vec3d4.x, vec3d4.y + 0.6F - this.getY() + i, vec3d4.z)) {
					this.setVelocity(vec3d4.x, 0.3F, vec3d4.z);
				}
			} else if (this.isFallFlying()) {
				Vec3d vec3d5 = this.getVelocity();
				if (vec3d5.y > -0.5) {
					this.fallDistance = 1.0F;
				}

				Vec3d vec3d6 = this.getRotationVector();
				float j = this.getPitch() * (float) (Math.PI / 180.0);
				double k = Math.sqrt(vec3d6.x * vec3d6.x + vec3d6.z * vec3d6.z);
				double l = vec3d5.horizontalLength();
				double m = vec3d6.length();
				float n = MathHelper.cos(j);
				n = (float)((double)n * (double)n * Math.min(1.0, m / 0.4));
				vec3d5 = this.getVelocity().add(0.0, d * (-1.0 + (double)n * 0.75), 0.0);
				if (vec3d5.y < 0.0 && k > 0.0) {
					double o = vec3d5.y * -0.1 * (double)n;
					vec3d5 = vec3d5.add(vec3d6.x * o / k, o, vec3d6.z * o / k);
				}

				if (j < 0.0F && k > 0.0) {
					double p = l * (double)(-MathHelper.sin(j)) * 0.04;
					vec3d5 = vec3d5.add(-vec3d6.x * p / k, p * 3.2, -vec3d6.z * p / k);
				}

				if (k > 0.0) {
					vec3d5 = vec3d5.add((vec3d6.x / k * l - vec3d5.x) * 0.1, 0.0, (vec3d6.z / k * l - vec3d5.z) * 0.1);
				}

				this.setVelocity(vec3d5.multiply(0.99F, 0.98F, 0.99F));
				this.move(MovementType.SELF, this.getVelocity());
				if (this.horizontalCollision && !this.world.isClient) {
					double q = this.getVelocity().horizontalLength();
					double r = l - q;
					float s = (float)(r * 10.0 - 3.0);
					if (s > 0.0F) {
						this.playSound(this.getFallSound((int)s), 1.0F, 1.0F);
						this.damage(DamageSource.FLY_INTO_WALL, s);
					}
				}

				if (this.onGround && !this.world.isClient) {
					this.setFlag(7, false);
				}
			} else {
				BlockPos blockPos = this.getVelocityAffectingPos();
				float t = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
				float u = this.onGround ? t * 0.91F : 0.91F;
				Vec3d vec3d7 = this.method_26318(movementInput, t);
				double v = vec3d7.y;
				if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
					v += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d7.y) * 0.2;
					this.fallDistance = 0.0F;
				} else if (this.world.isClient && !this.world.isChunkLoaded(blockPos)) {
					if (this.getY() > (double)this.world.getBottomY()) {
						v = -0.1;
					} else {
						v = 0.0;
					}
				} else if (!this.hasNoGravity()) {
					v -= d;
				}

				if (this.hasNoDrag()) {
					this.setVelocity(vec3d7.x, v, vec3d7.z);
				} else {
					this.setVelocity(vec3d7.x * (double)u, v * 0.98F, vec3d7.z * (double)u);
				}
			}
		}

		this.updateLimbs(this, this instanceof Flutterer);
	}

	public void updateLimbs(LivingEntity entity, boolean flutter) {
		entity.lastLimbDistance = entity.limbDistance;
		double d = entity.getX() - entity.prevX;
		double e = flutter ? entity.getY() - entity.prevY : 0.0;
		double f = entity.getZ() - entity.prevZ;
		float g = (float)Math.sqrt(d * d + e * e + f * f) * 4.0F;
		if (g > 1.0F) {
			g = 1.0F;
		}

		entity.limbDistance = entity.limbDistance + (g - entity.limbDistance) * 0.4F;
		entity.limbAngle = entity.limbAngle + entity.limbDistance;
	}

	public Vec3d method_26318(Vec3d vec3d, float f) {
		this.updateVelocity(this.getMovementSpeed(f), vec3d);
		this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
		this.move(MovementType.SELF, this.getVelocity());
		Vec3d vec3d2 = this.getVelocity();
		if ((this.horizontalCollision || this.jumping)
			&& (this.isClimbing() || this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this))) {
			vec3d2 = new Vec3d(vec3d2.x, 0.2, vec3d2.z);
		}

		return vec3d2;
	}

	public Vec3d method_26317(double d, boolean bl, Vec3d vec3d) {
		if (!this.hasNoGravity() && !this.isSprinting()) {
			double e;
			if (bl && Math.abs(vec3d.y - 0.005) >= 0.003 && Math.abs(vec3d.y - d / 16.0) < 0.003) {
				e = -0.003;
			} else {
				e = vec3d.y - d / 16.0;
			}

			return new Vec3d(vec3d.x, e, vec3d.z);
		} else {
			return vec3d;
		}
	}

	private Vec3d applyClimbingSpeed(Vec3d motion) {
		if (this.isClimbing()) {
			this.fallDistance = 0.0F;
			float f = 0.15F;
			double d = MathHelper.clamp(motion.x, -0.15F, 0.15F);
			double e = MathHelper.clamp(motion.z, -0.15F, 0.15F);
			double g = Math.max(motion.y, -0.15F);
			if (g < 0.0 && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder() && this instanceof PlayerEntity) {
				g = 0.0;
			}

			motion = new Vec3d(d, g, e);
		}

		return motion;
	}

	private float getMovementSpeed(float slipperiness) {
		return this.onGround ? this.getMovementSpeed() * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : this.flyingSpeed;
	}

	public float getMovementSpeed() {
		return this.movementSpeed;
	}

	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public boolean tryAttack(Entity target) {
		this.onAttacking(target);
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.tickActiveItemStack();
		this.updateLeaningPitch();
		if (!this.world.isClient) {
			int i = this.getStuckArrowCount();
			if (i > 0) {
				if (this.stuckArrowTimer <= 0) {
					this.stuckArrowTimer = 20 * (30 - i);
				}

				this.stuckArrowTimer--;
				if (this.stuckArrowTimer <= 0) {
					this.setStuckArrowCount(i - 1);
				}
			}

			int j = this.getStingerCount();
			if (j > 0) {
				if (this.stuckStingerTimer <= 0) {
					this.stuckStingerTimer = 20 * (30 - j);
				}

				this.stuckStingerTimer--;
				if (this.stuckStingerTimer <= 0) {
					this.setStingerCount(j - 1);
				}
			}

			this.method_30128();
			if (this.age % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (this.isSleeping() && !this.isSleepingInBed()) {
				this.wakeUp();
			}
		}

		this.tickMovement();
		double d = this.getX() - this.prevX;
		double e = this.getZ() - this.prevZ;
		float f = (float)(d * d + e * e);
		float g = this.bodyYaw;
		float h = 0.0F;
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		float k = 0.0F;
		if (f > 0.0025000002F) {
			k = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			float l = (float)MathHelper.atan2(e, d) * (180.0F / (float)Math.PI) - 90.0F;
			float m = MathHelper.abs(MathHelper.wrapDegrees(this.getYaw()) - l);
			if (95.0F < m && m < 265.0F) {
				g = l - 180.0F;
			} else {
				g = l;
			}
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.getYaw();
		}

		if (!this.onGround) {
			k = 0.0F;
		}

		this.stepBobbingAmount = this.stepBobbingAmount + (k - this.stepBobbingAmount) * 0.3F;
		this.world.getProfiler().push("headTurn");
		h = this.turnHead(g, h);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("rangeChecks");

		while (this.getYaw() - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.getYaw() - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		while (this.bodyYaw - this.prevBodyYaw < -180.0F) {
			this.prevBodyYaw -= 360.0F;
		}

		while (this.bodyYaw - this.prevBodyYaw >= 180.0F) {
			this.prevBodyYaw += 360.0F;
		}

		while (this.getPitch() - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.getPitch() - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.headYaw - this.prevHeadYaw < -180.0F) {
			this.prevHeadYaw -= 360.0F;
		}

		while (this.headYaw - this.prevHeadYaw >= 180.0F) {
			this.prevHeadYaw += 360.0F;
		}

		this.world.getProfiler().pop();
		this.lookDirection += h;
		if (this.isFallFlying()) {
			this.roll++;
		} else {
			this.roll = 0;
		}

		if (this.isSleeping()) {
			this.setPitch(0.0F);
		}
	}

	private void method_30128() {
		Map<EquipmentSlot, ItemStack> map = this.getEquipment();
		if (map != null) {
			this.swapHandStacks(map);
			if (!map.isEmpty()) {
				this.setEquipment(map);
			}
		}
	}

	@Nullable
	private Map<EquipmentSlot, ItemStack> getEquipment() {
		Map<EquipmentSlot, ItemStack> map = null;

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack;
			switch (equipmentSlot.getType()) {
				case HAND:
					itemStack = this.getStackInHandSlot(equipmentSlot);
					break;
				case ARMOR:
					itemStack = this.getArmorInSlot(equipmentSlot);
					break;
				default:
					continue;
			}

			ItemStack itemStack4 = this.getEquippedStack(equipmentSlot);
			if (!ItemStack.areEqual(itemStack4, itemStack)) {
				if (map == null) {
					map = Maps.newEnumMap(EquipmentSlot.class);
				}

				map.put(equipmentSlot, itemStack4);
				if (!itemStack.isEmpty()) {
					this.getAttributes().removeModifiers(itemStack.getAttributeModifiers(equipmentSlot));
				}

				if (!itemStack4.isEmpty()) {
					this.getAttributes().addTemporaryModifiers(itemStack4.getAttributeModifiers(equipmentSlot));
				}
			}
		}

		return map;
	}

	private void swapHandStacks(Map<EquipmentSlot, ItemStack> equipment) {
		ItemStack itemStack = (ItemStack)equipment.get(EquipmentSlot.MAINHAND);
		ItemStack itemStack2 = (ItemStack)equipment.get(EquipmentSlot.OFFHAND);
		if (itemStack != null
			&& itemStack2 != null
			&& ItemStack.areEqual(itemStack, this.getStackInHandSlot(EquipmentSlot.OFFHAND))
			&& ItemStack.areEqual(itemStack2, this.getStackInHandSlot(EquipmentSlot.MAINHAND))) {
			((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityStatusS2CPacket(this, (byte)55));
			equipment.remove(EquipmentSlot.MAINHAND);
			equipment.remove(EquipmentSlot.OFFHAND);
			this.setStackInHandSlot(EquipmentSlot.MAINHAND, itemStack.copy());
			this.setStackInHandSlot(EquipmentSlot.OFFHAND, itemStack2.copy());
		}
	}

	private void setEquipment(Map<EquipmentSlot, ItemStack> equipment) {
		List<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayListWithCapacity(equipment.size());
		equipment.forEach((slot, stack) -> {
			ItemStack itemStack = stack.copy();
			list.add(Pair.of(slot, itemStack));
			switch (slot.getType()) {
				case HAND:
					this.setStackInHandSlot(slot, itemStack);
					break;
				case ARMOR:
					this.setArmorInSlot(slot, itemStack);
			}
		});
		((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getId(), list));
	}

	private ItemStack getArmorInSlot(EquipmentSlot slot) {
		return this.equippedArmor.get(slot.getEntitySlotId());
	}

	private void setArmorInSlot(EquipmentSlot slot, ItemStack armor) {
		this.equippedArmor.set(slot.getEntitySlotId(), armor);
	}

	private ItemStack getStackInHandSlot(EquipmentSlot slot) {
		return this.equippedHand.get(slot.getEntitySlotId());
	}

	private void setStackInHandSlot(EquipmentSlot slot, ItemStack stack) {
		this.equippedHand.set(slot.getEntitySlotId(), stack);
	}

	protected float turnHead(float bodyRotation, float headRotation) {
		float f = MathHelper.wrapDegrees(bodyRotation - this.bodyYaw);
		this.bodyYaw += f * 0.3F;
		float g = MathHelper.wrapDegrees(this.getYaw() - this.bodyYaw);
		boolean bl = g < -90.0F || g >= 90.0F;
		if (g < -75.0F) {
			g = -75.0F;
		}

		if (g >= 75.0F) {
			g = 75.0F;
		}

		this.bodyYaw = this.getYaw() - g;
		if (g * g > 2500.0F) {
			this.bodyYaw += g * 0.2F;
		}

		if (bl) {
			headRotation *= -1.0F;
		}

		return headRotation;
	}

	public void tickMovement() {
		if (this.jumpingCooldown > 0) {
			this.jumpingCooldown--;
		}

		if (this.isLogicalSideForUpdatingMovement()) {
			this.bodyTrackingIncrements = 0;
			this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
		}

		if (this.bodyTrackingIncrements > 0) {
			double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
			double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
			double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
			double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.getYaw());
			this.setYaw(this.getYaw() + (float)g / (float)this.bodyTrackingIncrements);
			this.setPitch(this.getPitch() + (float)(this.serverPitch - (double)this.getPitch()) / (float)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.setPosition(d, e, f);
			this.setRotation(this.getYaw(), this.getPitch());
		} else if (!this.canMoveVoluntarily()) {
			this.setVelocity(this.getVelocity().multiply(0.98));
		}

		if (this.headTrackingIncrements > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
			this.headTrackingIncrements--;
		}

		Vec3d vec3d = this.getVelocity();
		double h = vec3d.x;
		double i = vec3d.y;
		double j = vec3d.z;
		if (Math.abs(vec3d.x) < 0.003) {
			h = 0.0;
		}

		if (Math.abs(vec3d.y) < 0.003) {
			i = 0.0;
		}

		if (Math.abs(vec3d.z) < 0.003) {
			j = 0.0;
		}

		this.setVelocity(h, i, j);
		this.world.getProfiler().push("ai");
		if (this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
		} else if (this.canMoveVoluntarily()) {
			this.world.getProfiler().push("newAi");
			this.tickNewAi();
			this.world.getProfiler().pop();
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("jump");
		if (this.jumping && this.shouldSwimInFluids()) {
			double k;
			if (this.isInLava()) {
				k = this.getFluidHeight(FluidTags.LAVA);
			} else {
				k = this.getFluidHeight(FluidTags.WATER);
			}

			boolean bl = this.isTouchingWater() && k > 0.0;
			double m = this.getSwimHeight();
			if (!bl || this.onGround && !(k > m)) {
				if (!this.isInLava() || this.onGround && !(k > m)) {
					if ((this.onGround || bl && k <= m) && this.jumpingCooldown == 0) {
						this.jump();
						this.jumpingCooldown = 10;
					}
				} else {
					this.swimUpward(FluidTags.LAVA);
				}
			} else {
				this.swimUpward(FluidTags.WATER);
			}
		} else {
			this.jumpingCooldown = 0;
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.forwardSpeed *= 0.98F;
		this.tickFallFlying();
		Box box = this.getBoundingBox();
		this.travel(new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed));
		this.world.getProfiler().pop();
		this.world.getProfiler().push("freezing");
		boolean bl2 = this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES);
		if (!this.world.isClient && !this.isDead()) {
			int n = this.getFrozenTicks();
			if (this.inPowderSnow && this.canFreeze()) {
				this.setFrozenTicks(Math.min(this.getMinFreezeDamageTicks(), n + 1));
			} else {
				this.setFrozenTicks(Math.max(0, n - 2));
			}
		}

		this.removePowderSnowSlow();
		this.addPowderSnowSlowIfNeeded();
		if (!this.world.isClient && this.age % 40 == 0 && this.isFreezing() && this.canFreeze()) {
			int o = bl2 ? 5 : 1;
			this.damage(DamageSource.FREEZE, (float)o);
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("push");
		if (this.riptideTicks > 0) {
			this.riptideTicks--;
			this.tickRiptide(box, this.getBoundingBox());
		}

		this.tickCramming();
		this.world.getProfiler().pop();
		if (!this.world.isClient && this.hurtByWater() && this.isWet()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}
	}

	public boolean hurtByWater() {
		return false;
	}

	private void tickFallFlying() {
		boolean bl = this.getFlag(7);
		if (bl && !this.onGround && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
				bl = true;
				int i = this.roll + 1;
				if (!this.world.isClient && i % 10 == 0) {
					int j = i / 10;
					if (j % 2 == 0) {
						itemStack.damage(1, this, player -> player.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
					}

					this.emitGameEvent(GameEvent.ELYTRA_FREE_FALL);
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}

		if (!this.world.isClient) {
			this.setFlag(7, bl);
		}
	}

	protected void tickNewAi() {
	}

	protected void tickCramming() {
		List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
			if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
				int j = 0;

				for (int k = 0; k < list.size(); k++) {
					if (!((Entity)list.get(k)).hasVehicle()) {
						j++;
					}
				}

				if (j > i - 1) {
					this.damage(DamageSource.CRAMMING, 6.0F);
				}
			}

			for (int l = 0; l < list.size(); l++) {
				Entity entity = (Entity)list.get(l);
				this.pushAway(entity);
			}
		}
	}

	protected void tickRiptide(Box a, Box b) {
		Box box = a.union(b);
		List<Entity> list = this.world.getOtherEntities(this, box);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof LivingEntity) {
					this.attackLivingEntity((LivingEntity)entity);
					this.riptideTicks = 0;
					this.setVelocity(this.getVelocity().multiply(-0.2));
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.riptideTicks = 0;
		}

		if (!this.world.isClient && this.riptideTicks <= 0) {
			this.setLivingFlag(4, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void attackLivingEntity(LivingEntity target) {
	}

	public void setRiptideTicks(int riptideTicks) {
		this.riptideTicks = riptideTicks;
		if (!this.world.isClient) {
			this.setLivingFlag(4, true);
		}
	}

	public boolean isUsingRiptide() {
		return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity != null && entity != this.getVehicle() && !this.world.isClient) {
			this.onDismounted(entity);
		}
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		this.stepBobbingAmount = 0.0F;
		this.fallDistance = 0.0F;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.serverX = x;
		this.serverY = y;
		this.serverZ = z;
		this.serverYaw = (double)yaw;
		this.serverPitch = (double)pitch;
		this.bodyTrackingIncrements = interpolationSteps;
	}

	@Override
	public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
		this.serverHeadYaw = (double)yaw;
		this.headTrackingIncrements = interpolationSteps;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
		PlayerEntity playerEntity = item.getThrower() != null ? this.world.getPlayerByUuid(item.getThrower()) : null;
		if (playerEntity instanceof ServerPlayerEntity) {
			Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayerEntity)playerEntity, item.getStack(), this);
		}
	}

	public void sendPickup(Entity item, int count) {
		if (!item.isRemoved()
			&& !this.world.isClient
			&& (item instanceof ItemEntity || item instanceof PersistentProjectileEntity || item instanceof ExperienceOrbEntity)) {
			((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(item, new ItemPickupAnimationS2CPacket(item.getId(), this.getId(), count));
		}
	}

	public boolean canSee(Entity entity) {
		if (entity.world != this.world) {
			return false;
		} else {
			Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
			Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
			return vec3d2.distanceTo(vec3d) > 128.0
				? false
				: this.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType()
					== HitResult.Type.MISS;
		}
	}

	@Override
	public float getYaw(float tickDelta) {
		return tickDelta == 1.0F ? this.headYaw : MathHelper.lerp(tickDelta, this.prevHeadYaw, this.headYaw);
	}

	public float getHandSwingProgress(float tickDelta) {
		float f = this.handSwingProgress - this.lastHandSwingProgress;
		if (f < 0.0F) {
			f++;
		}

		return this.lastHandSwingProgress + f * tickDelta;
	}

	public boolean canMoveVoluntarily() {
		return !this.world.isClient;
	}

	@Override
	public boolean collides() {
		return !this.isRemoved();
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && !this.isSpectator() && !this.isClimbing();
	}

	@Override
	protected void scheduleVelocityUpdate() {
		this.velocityModified = this.random.nextDouble() >= this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
	}

	@Override
	public float getHeadYaw() {
		return this.headYaw;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		this.headYaw = headYaw;
	}

	@Override
	public void setBodyYaw(float bodyYaw) {
		this.bodyYaw = bodyYaw;
	}

	@Override
	protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
		return positionInPortal(super.positionInPortal(portalAxis, portalRect));
	}

	public static Vec3d positionInPortal(Vec3d pos) {
		return new Vec3d(pos.x, pos.y, 0.0);
	}

	public float getAbsorptionAmount() {
		return this.absorptionAmount;
	}

	public void setAbsorptionAmount(float amount) {
		if (amount < 0.0F) {
			amount = 0.0F;
		}

		this.absorptionAmount = amount;
	}

	public void enterCombat() {
	}

	public void endCombat() {
	}

	protected void markEffectsDirty() {
		this.effectsChanged = true;
	}

	public abstract Arm getMainArm();

	public boolean isUsingItem() {
		return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
	}

	public Hand getActiveHand() {
		return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
	}

	private void tickActiveItemStack() {
		if (this.isUsingItem()) {
			if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
				this.activeItemStack = this.getStackInHand(this.getActiveHand());
				this.tickItemStackUsage(this.activeItemStack);
			} else {
				this.clearActiveItem();
			}
		}
	}

	protected void tickItemStackUsage(ItemStack stack) {
		stack.usageTick(this.world, this, this.getItemUseTimeLeft());
		if (this.shouldSpawnConsumptionEffects()) {
			this.spawnConsumptionEffects(stack, 5);
		}

		if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !stack.isUsedOnRelease()) {
			this.consumeItem();
		}
	}

	private boolean shouldSpawnConsumptionEffects() {
		int i = this.getItemUseTimeLeft();
		FoodComponent foodComponent = this.activeItemStack.getItem().getFoodComponent();
		boolean bl = foodComponent != null && foodComponent.isSnack();
		bl |= i <= this.activeItemStack.getMaxUseTime() - 7;
		return bl && i % 4 == 0;
	}

	private void updateLeaningPitch() {
		this.lastLeaningPitch = this.leaningPitch;
		if (this.isInSwimmingPose()) {
			this.leaningPitch = Math.min(1.0F, this.leaningPitch + 0.09F);
		} else {
			this.leaningPitch = Math.max(0.0F, this.leaningPitch - 0.09F);
		}
	}

	protected void setLivingFlag(int mask, boolean value) {
		int i = this.dataTracker.get(LIVING_FLAGS);
		if (value) {
			i |= mask;
		} else {
			i &= ~mask;
		}

		this.dataTracker.set(LIVING_FLAGS, (byte)i);
	}

	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			this.activeItemStack = itemStack;
			this.itemUseTimeLeft = itemStack.getMaxUseTime();
			if (!this.world.isClient) {
				this.setLivingFlag(1, true);
				this.setLivingFlag(2, hand == Hand.OFF_HAND);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (SLEEPING_POSITION.equals(data)) {
			if (this.world.isClient) {
				this.getSleepingPosition().ifPresent(this::setPositionInBed);
			}
		} else if (LIVING_FLAGS.equals(data) && this.world.isClient) {
			if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
				this.activeItemStack = this.getStackInHand(this.getActiveHand());
				if (!this.activeItemStack.isEmpty()) {
					this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime();
				}
			} else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
				this.activeItemStack = ItemStack.EMPTY;
				this.itemUseTimeLeft = 0;
			}
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
		super.lookAt(anchorPoint, target);
		this.prevHeadYaw = this.headYaw;
		this.bodyYaw = this.headYaw;
		this.prevBodyYaw = this.bodyYaw;
	}

	protected void spawnConsumptionEffects(ItemStack stack, int particleCount) {
		if (!stack.isEmpty() && this.isUsingItem()) {
			if (stack.getUseAction() == UseAction.DRINK) {
				this.playSound(this.getDrinkSound(stack), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (stack.getUseAction() == UseAction.EAT) {
				this.spawnItemParticles(stack, particleCount);
				this.playSound(this.getEatSound(stack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	private void spawnItemParticles(ItemStack stack, int count) {
		for (int i = 0; i < count; i++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.getPitch() * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.getPitch() * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.getX(), this.getEyeY(), this.getZ());
			this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void consumeItem() {
		Hand hand = this.getActiveHand();
		if (!this.activeItemStack.equals(this.getStackInHand(hand))) {
			this.stopUsingItem();
		} else {
			if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
				this.spawnConsumptionEffects(this.activeItemStack, 16);
				ItemStack itemStack = this.activeItemStack.finishUsing(this.world, this);
				if (itemStack != this.activeItemStack) {
					this.setStackInHand(hand, itemStack);
				}

				this.clearActiveItem();
			}
		}
	}

	public ItemStack getActiveItem() {
		return this.activeItemStack;
	}

	public int getItemUseTimeLeft() {
		return this.itemUseTimeLeft;
	}

	public int getItemUseTime() {
		return this.isUsingItem() ? this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft() : 0;
	}

	public void stopUsingItem() {
		if (!this.activeItemStack.isEmpty()) {
			this.activeItemStack.onStoppedUsing(this.world, this, this.getItemUseTimeLeft());
			if (this.activeItemStack.isUsedOnRelease()) {
				this.tickActiveItemStack();
			}
		}

		this.clearActiveItem();
	}

	public void clearActiveItem() {
		if (!this.world.isClient) {
			this.setLivingFlag(1, false);
		}

		this.activeItemStack = ItemStack.EMPTY;
		this.itemUseTimeLeft = 0;
	}

	public boolean isBlocking() {
		if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
			Item item = this.activeItemStack.getItem();
			return item.getUseAction(this.activeItemStack) != UseAction.BLOCK ? false : item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
		} else {
			return false;
		}
	}

	public boolean isHoldingOntoLadder() {
		return this.isSneaking();
	}

	public boolean isFallFlying() {
		return this.getFlag(7);
	}

	@Override
	public boolean isInSwimmingPose() {
		return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.FALL_FLYING;
	}

	public int getRoll() {
		return this.roll;
	}

	public boolean teleport(double x, double y, double z, boolean particleEffects) {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		double g = y;
		boolean bl = false;
		BlockPos blockPos = new BlockPos(x, y, z);
		World world = this.world;
		if (world.isChunkLoaded(blockPos)) {
			boolean bl2 = false;

			while (!bl2 && blockPos.getY() > world.getBottomY()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getMaterial().blocksMovement()) {
					bl2 = true;
				} else {
					g--;
					blockPos = blockPos2;
				}
			}

			if (bl2) {
				this.requestTeleport(x, g, z);
				if (world.isSpaceEmpty(this) && !world.containsFluid(this.getBoundingBox())) {
					bl = true;
				}
			}
		}

		if (!bl) {
			this.requestTeleport(d, e, f);
			return false;
		} else {
			if (particleEffects) {
				world.sendEntityStatus(this, (byte)46);
			}

			if (this instanceof PathAwareEntity) {
				((PathAwareEntity)this).getNavigation().stop();
			}

			return true;
		}
	}

	public boolean isAffectedBySplashPotions() {
		return true;
	}

	public boolean isMobOrPlayer() {
		return true;
	}

	public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
	}

	public boolean canEquip(ItemStack stack) {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new MobSpawnS2CPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return pose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(pose).scaled(this.getScaleFactor());
	}

	public ImmutableList<EntityPose> getPoses() {
		return ImmutableList.of(EntityPose.STANDING);
	}

	public Box getBoundingBox(EntityPose pose) {
		EntityDimensions entityDimensions = this.getDimensions(pose);
		return new Box(
			(double)(-entityDimensions.width / 2.0F),
			0.0,
			(double)(-entityDimensions.width / 2.0F),
			(double)(entityDimensions.width / 2.0F),
			(double)entityDimensions.height,
			(double)(entityDimensions.width / 2.0F)
		);
	}

	public Optional<BlockPos> getSleepingPosition() {
		return this.dataTracker.get(SLEEPING_POSITION);
	}

	public void setSleepingPosition(BlockPos pos) {
		this.dataTracker.set(SLEEPING_POSITION, Optional.of(pos));
	}

	public void clearSleepingPosition() {
		this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
	}

	public boolean isSleeping() {
		return this.getSleepingPosition().isPresent();
	}

	public void sleep(BlockPos pos) {
		if (this.hasVehicle()) {
			this.stopRiding();
		}

		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.getBlock() instanceof BedBlock) {
			this.world.setBlockState(pos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(true)), 3);
		}

		this.setPose(EntityPose.SLEEPING);
		this.setPositionInBed(pos);
		this.setSleepingPosition(pos);
		this.setVelocity(Vec3d.ZERO);
		this.velocityDirty = true;
	}

	private void setPositionInBed(BlockPos pos) {
		this.setPosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.6875, (double)pos.getZ() + 0.5);
	}

	private boolean isSleepingInBed() {
		return (Boolean)this.getSleepingPosition().map(pos -> this.world.getBlockState(pos).getBlock() instanceof BedBlock).orElse(false);
	}

	public void wakeUp() {
		this.getSleepingPosition().filter(this.world::isChunkLoaded).ifPresent(pos -> {
			BlockState blockState = this.world.getBlockState(pos);
			if (blockState.getBlock() instanceof BedBlock) {
				this.world.setBlockState(pos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), 3);
				Vec3d vec3dx = (Vec3d)BedBlock.findWakeUpPosition(this.getType(), this.world, pos, this.getYaw()).orElseGet(() -> {
					BlockPos blockPos2 = pos.up();
					return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
				});
				Vec3d vec3d2 = Vec3d.ofBottomCenter(pos).subtract(vec3dx).normalize();
				float f = (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d2.z, vec3d2.x) * 180.0F / (float)Math.PI - 90.0);
				this.setPosition(vec3dx.x, vec3dx.y, vec3dx.z);
				this.setYaw(f);
				this.setPitch(0.0F);
			}
		});
		Vec3d vec3d = this.getPos();
		this.setPose(EntityPose.STANDING);
		this.setPosition(vec3d.x, vec3d.y, vec3d.z);
		this.clearSleepingPosition();
	}

	@Nullable
	public Direction getSleepingDirection() {
		BlockPos blockPos = (BlockPos)this.getSleepingPosition().orElse(null);
		return blockPos != null ? BedBlock.getDirection(this.world, blockPos) : null;
	}

	@Override
	public boolean isInsideWall() {
		return !this.isSleeping() && super.isInsideWall();
	}

	@Override
	protected final float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return pose == EntityPose.SLEEPING ? 0.2F : this.getActiveEyeHeight(pose, dimensions);
	}

	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return super.getEyeHeight(pose, dimensions);
	}

	public ItemStack getArrowType(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	public ItemStack eatFood(World world, ItemStack stack) {
		if (stack.isFood()) {
			world.emitGameEvent(this, GameEvent.EAT, this.getCameraBlockPos());
			world.playSound(
				null,
				this.getX(),
				this.getY(),
				this.getZ(),
				this.getEatSound(stack),
				SoundCategory.NEUTRAL,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
			this.applyFoodEffects(stack, world, this);
			if (!(this instanceof PlayerEntity) || !((PlayerEntity)this).getAbilities().creativeMode) {
				stack.decrement(1);
			}

			this.emitGameEvent(GameEvent.EAT);
		}

		return stack;
	}

	private void applyFoodEffects(ItemStack stack, World world, LivingEntity targetEntity) {
		Item item = stack.getItem();
		if (item.isFood()) {
			for (Pair<StatusEffectInstance, Float> pair : item.getFoodComponent().getStatusEffects()) {
				if (!world.isClient && pair.getFirst() != null && world.random.nextFloat() < (Float)pair.getSecond()) {
					targetEntity.addStatusEffect(new StatusEffectInstance((StatusEffectInstance)pair.getFirst()));
				}
			}
		}
	}

	private static byte getEquipmentBreakStatus(EquipmentSlot slot) {
		switch (slot) {
			case MAINHAND:
				return 47;
			case OFFHAND:
				return 48;
			case HEAD:
				return 49;
			case CHEST:
				return 50;
			case FEET:
				return 52;
			case LEGS:
				return 51;
			default:
				return 47;
		}
	}

	public void sendEquipmentBreakStatus(EquipmentSlot slot) {
		this.world.sendEntityStatus(this, getEquipmentBreakStatus(slot));
	}

	public void sendToolBreakStatus(Hand hand) {
		this.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
	}

	@Override
	public Box getVisibilityBoundingBox() {
		if (this.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
			float f = 0.5F;
			return this.getBoundingBox().expand(0.5, 0.5, 0.5);
		} else {
			return super.getVisibilityBoundingBox();
		}
	}

	public static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
		Item item = stack.getItem();
		if (!stack.isOf(Items.CARVED_PUMPKIN) && (!(item instanceof BlockItem) || !(((BlockItem)item).getBlock() instanceof AbstractSkullBlock))) {
			if (item instanceof ArmorItem) {
				return ((ArmorItem)item).getSlotType();
			} else if (stack.isOf(Items.ELYTRA)) {
				return EquipmentSlot.CHEST;
			} else {
				return stack.isOf(Items.SHIELD) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
			}
		} else {
			return EquipmentSlot.HEAD;
		}
	}

	private static StackReference getStackReference(LivingEntity entity, EquipmentSlot slot) {
		return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND
			? StackReference.of(entity, slot, stack -> stack.isEmpty() || MobEntity.getPreferredEquipmentSlot(stack) == slot)
			: StackReference.of(entity, slot);
	}

	@Nullable
	private static EquipmentSlot getEquipmentSlot(int slotId) {
		if (slotId == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
			return EquipmentSlot.HEAD;
		} else if (slotId == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
			return EquipmentSlot.CHEST;
		} else if (slotId == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
			return EquipmentSlot.LEGS;
		} else if (slotId == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
			return EquipmentSlot.FEET;
		} else if (slotId == 98) {
			return EquipmentSlot.MAINHAND;
		} else {
			return slotId == 99 ? EquipmentSlot.OFFHAND : null;
		}
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		EquipmentSlot equipmentSlot = getEquipmentSlot(mappedIndex);
		return equipmentSlot != null ? getStackReference(this, equipmentSlot) : super.getStackReference(mappedIndex);
	}

	@Override
	public boolean canFreeze() {
		if (this.isSpectator()) {
			return false;
		} else {
			boolean bl = !this.getEquippedStack(EquipmentSlot.HEAD).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.CHEST).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.LEGS).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.FEET).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES);
			return bl && super.canFreeze();
		}
	}

	@Override
	public boolean isGlowing() {
		return !this.world.isClient() && this.hasStatusEffect(StatusEffects.GLOWING) || super.isGlowing();
	}

	public void readFromPacket(MobSpawnS2CPacket packet) {
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = (float)(packet.getYaw() * 360) / 256.0F;
		float h = (float)(packet.getPitch() * 360) / 256.0F;
		this.updateTrackedPosition(d, e, f);
		this.bodyYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
		this.headYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
		this.setId(packet.getId());
		this.setUuid(packet.getUuid());
		this.updatePositionAndAngles(d, e, f, g, h);
		this.setVelocity(
			(double)((float)packet.getVelocityX() / 8000.0F), (double)((float)packet.getVelocityY() / 8000.0F), (double)((float)packet.getVelocityZ() / 8000.0F)
		);
	}
}
