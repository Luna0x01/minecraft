package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
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
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
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
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public abstract class LivingEntity extends Entity {
	private static final UUID ATTR_SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final EntityAttributeModifier ATTR_SPRINTING_SPEED_BOOST = new EntityAttributeModifier(
			ATTR_SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, EntityAttributeModifier.Operation.field_6331
		)
		.setSerialize(false);
	protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(
		LivingEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS
	);
	protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F);
	private AbstractEntityAttributeContainer attributes;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
	private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public boolean isHandSwinging;
	public Hand preferredHand;
	public int handSwingTicks;
	public int stuckArrowTimer;
	public int field_20347;
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
	public final int defaultMaximumHealth = 20;
	public final float randomLargeSeed;
	public final float randomSmallSeed;
	public float bodyYaw;
	public float prevBodyYaw;
	public float headYaw;
	public float prevHeadYaw;
	public float flyingSpeed = 0.02F;
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
	private DamageSource lastDamageSource;
	private long lastDamageTime;
	protected int pushCooldown;
	private float leaningPitch;
	private float lastLeaningPitch;
	protected Brain<?> brain;

	protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		this.initAttributes();
		this.setHealth(this.getMaximumHealth());
		this.inanimate = true;
		this.randomSmallSeed = (float)((Math.random() + 1.0) * 0.01F);
		this.refreshPosition();
		this.randomLargeSeed = (float)Math.random() * 12398.0F;
		this.yaw = (float)(Math.random() * (float) (Math.PI * 2));
		this.headYaw = this.yaw;
		this.stepHeight = 0.6F;
		this.brain = this.deserializeBrain(new Dynamic(NbtOps.INSTANCE, new CompoundTag()));
	}

	public Brain<?> getBrain() {
		return this.brain;
	}

	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return new Brain(ImmutableList.of(), ImmutableList.of(), dynamic);
	}

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public boolean canTarget(EntityType<?> entityType) {
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

	protected void initAttributes() {
		this.getAttributes().register(EntityAttributes.MAX_HEALTH);
		this.getAttributes().register(EntityAttributes.KNOCKBACK_RESISTANCE);
		this.getAttributes().register(EntityAttributes.MOVEMENT_SPEED);
		this.getAttributes().register(EntityAttributes.ARMOR);
		this.getAttributes().register(EntityAttributes.ARMOR_TOUGHNESS);
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (!this.isTouchingWater()) {
			this.checkWaterState();
		}

		if (!this.world.isClient && this.fallDistance > 3.0F && bl) {
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (!blockState.isAir()) {
				double e = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * e);
				((ServerWorld)this.world)
					.spawnParticles(new BlockStateParticleEffect(ParticleTypes.field_11217, blockState), this.getX(), this.getY(), this.getZ(), i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.fall(d, bl, blockState, blockPos);
	}

	public boolean canBreatheInWater() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	public float getLeaningPitch(float f) {
		return MathHelper.lerp(f, this.lastLeaningPitch, this.leaningPitch);
	}

	@Override
	public void baseTick() {
		this.lastHandSwingProgress = this.handSwingProgress;
		if (this.firstUpdate) {
			this.getSleepingPosition().ifPresent(this::setPositionInBed);
		}

		super.baseTick();
		this.world.getProfiler().push("livingEntityBaseTick");
		boolean bl = this instanceof PlayerEntity;
		if (this.isAlive()) {
			if (this.isInsideWall()) {
				this.damage(DamageSource.IN_WALL, 1.0F);
			} else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
				double d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getBuffer();
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

		boolean bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
		if (this.isAlive()) {
			if (this.isInFluid(FluidTags.field_15517)
				&& this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).getBlock() != Blocks.field_10422) {
				if (!this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && !bl2) {
					this.setAir(this.getNextAirUnderwater(this.getAir()));
					if (this.getAir() == -20) {
						this.setAir(0);
						Vec3d vec3d = this.getVelocity();

						for (int i = 0; i < 8; i++) {
							float f = this.random.nextFloat() - this.random.nextFloat();
							float g = this.random.nextFloat() - this.random.nextFloat();
							float h = this.random.nextFloat() - this.random.nextFloat();
							this.world.addParticle(ParticleTypes.field_11247, this.getX() + (double)f, this.getY() + (double)g, this.getZ() + (double)h, vec3d.x, vec3d.y, vec3d.z);
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
				BlockPos blockPos = new BlockPos(this);
				if (!Objects.equal(this.lastBlockPos, blockPos)) {
					this.lastBlockPos = blockPos;
					this.applyFrostWalker(blockPos);
				}
			}
		}

		if (this.isAlive() && this.isWet()) {
			this.extinguish();
		}

		if (this.hurtTime > 0) {
			this.hurtTime--;
		}

		if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
			this.timeUntilRegen--;
		}

		if (this.getHealth() <= 0.0F) {
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
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.world.getProfiler().pop();
	}

	protected void applyFrostWalker(BlockPos blockPos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9122, this);
		if (i > 0) {
			FrostWalkerEnchantment.freezeWater(this, this.world, blockPos, i);
		}
	}

	public boolean isBaby() {
		return false;
	}

	public float getScaleFactor() {
		return this.isBaby() ? 0.5F : 1.0F;
	}

	@Override
	public boolean canBeRiddenInWater() {
		return false;
	}

	protected void updatePostDeath() {
		this.deathTime++;
		if (this.deathTime == 20) {
			this.remove();

			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world.addParticle(ParticleTypes.field_11203, this.getParticleX(1.0), this.getRandomBodyY(), this.getParticleZ(1.0), d, e, f);
			}
		}
	}

	protected boolean canDropLootAndXp() {
		return !this.isBaby();
	}

	protected int getNextAirUnderwater(int i) {
		int j = EnchantmentHelper.getRespiration(this);
		return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
	}

	protected int getNextAirOnLand(int i) {
		return Math.min(i + 4, this.getMaxAir());
	}

	protected int getCurrentExperience(PlayerEntity playerEntity) {
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

	public void setAttacker(@Nullable LivingEntity livingEntity) {
		this.attacker = livingEntity;
		this.lastAttackedTime = this.age;
	}

	@Nullable
	public LivingEntity getAttacking() {
		return this.attacking;
	}

	public int getLastAttackTime() {
		return this.lastAttackTime;
	}

	public void onAttacking(Entity entity) {
		if (entity instanceof LivingEntity) {
			this.attacking = (LivingEntity)entity;
		} else {
			this.attacking = null;
		}

		this.lastAttackTime = this.age;
	}

	public int getDespawnCounter() {
		return this.despawnCounter;
	}

	public void setDespawnCounter(int i) {
		this.despawnCounter = i;
	}

	protected void onEquipStack(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			SoundEvent soundEvent = SoundEvents.field_14883;
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem) {
				soundEvent = ((ArmorItem)item).getMaterial().getEquipSound();
			} else if (item == Items.field_8833) {
				soundEvent = SoundEvents.field_14966;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putFloat("Health", this.getHealth());
		compoundTag.putShort("HurtTime", (short)this.hurtTime);
		compoundTag.putInt("HurtByTimestamp", this.lastAttackedTime);
		compoundTag.putShort("DeathTime", (short)this.deathTime);
		compoundTag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
		compoundTag.put("Attributes", EntityAttributes.toTag(this.getAttributes()));
		if (!this.activeStatusEffects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
				listTag.add(statusEffectInstance.toTag(new CompoundTag()));
			}

			compoundTag.put("ActiveEffects", listTag);
		}

		compoundTag.putBoolean("FallFlying", this.isFallFlying());
		this.getSleepingPosition().ifPresent(blockPos -> {
			compoundTag.putInt("SleepingX", blockPos.getX());
			compoundTag.putInt("SleepingY", blockPos.getY());
			compoundTag.putInt("SleepingZ", blockPos.getZ());
		});
		compoundTag.put("Brain", this.brain.serialize(NbtOps.INSTANCE));
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.setAbsorptionAmount(compoundTag.getFloat("AbsorptionAmount"));
		if (compoundTag.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
			EntityAttributes.fromTag(this.getAttributes(), compoundTag.getList("Attributes", 10));
		}

		if (compoundTag.contains("ActiveEffects", 9)) {
			ListTag listTag = compoundTag.getList("ActiveEffects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag2);
				if (statusEffectInstance != null) {
					this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				}
			}
		}

		if (compoundTag.contains("Health", 99)) {
			this.setHealth(compoundTag.getFloat("Health"));
		}

		this.hurtTime = compoundTag.getShort("HurtTime");
		this.deathTime = compoundTag.getShort("DeathTime");
		this.lastAttackedTime = compoundTag.getInt("HurtByTimestamp");
		if (compoundTag.contains("Team", 8)) {
			String string = compoundTag.getString("Team");
			Team team = this.world.getScoreboard().getTeam(string);
			boolean bl = team != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), team);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (compoundTag.getBoolean("FallFlying")) {
			this.setFlag(7, true);
		}

		if (compoundTag.contains("SleepingX", 99) && compoundTag.contains("SleepingY", 99) && compoundTag.contains("SleepingZ", 99)) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("SleepingX"), compoundTag.getInt("SleepingY"), compoundTag.getInt("SleepingZ"));
			this.setSleepingPosition(blockPos);
			this.dataTracker.set(POSE, EntityPose.field_18078);
			if (!this.firstUpdate) {
				this.setPositionInBed(blockPos);
			}
		}

		if (compoundTag.contains("Brain", 10)) {
			this.brain = this.deserializeBrain(new Dynamic(NbtOps.INSTANCE, compoundTag.get("Brain")));
		}
	}

	protected void tickStatusEffects() {
		Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				StatusEffect statusEffect = (StatusEffect)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(statusEffect);
				if (!statusEffectInstance.update(this, () -> this.onStatusEffectUpgraded(statusEffectInstance, true))) {
					if (!this.world.isClient) {
						iterator.remove();
						this.onStatusEffectRemoved(statusEffectInstance);
					}
				} else if (statusEffectInstance.getDuration() % 600 == 0) {
					this.onStatusEffectUpgraded(statusEffectInstance, false);
				}
			}
		} catch (ConcurrentModificationException var11) {
		}

		if (this.effectsChanged) {
			if (!this.world.isClient) {
				this.updatePotionVisibility();
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
					.addParticle(bl ? ParticleTypes.field_11225 : ParticleTypes.field_11226, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
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
			this.setInvisible(this.hasStatusEffect(StatusEffects.field_5905));
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
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6169);
			Item item = itemStack.getItem();
			EntityType<?> entityType = entity.getType();
			if (entityType == EntityType.field_6137 && item == Items.SKELETON_SKULL
				|| entityType == EntityType.field_6051 && item == Items.ZOMBIE_HEAD
				|| entityType == EntityType.field_6046 && item == Items.CREEPER_HEAD) {
				d *= 0.5;
			}
		}

		return d;
	}

	public boolean canTarget(LivingEntity livingEntity) {
		return true;
	}

	public boolean isTarget(LivingEntity livingEntity, TargetPredicate targetPredicate) {
		return targetPredicate.test(this, livingEntity);
	}

	public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> collection) {
		for (StatusEffectInstance statusEffectInstance : collection) {
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

	public boolean hasStatusEffect(StatusEffect statusEffect) {
		return this.activeStatusEffects.containsKey(statusEffect);
	}

	@Nullable
	public StatusEffectInstance getStatusEffect(StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activeStatusEffects.get(statusEffect);
	}

	public boolean addStatusEffect(StatusEffectInstance statusEffectInstance) {
		if (!this.canHaveStatusEffect(statusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)this.activeStatusEffects.get(statusEffectInstance.getEffectType());
			if (statusEffectInstance2 == null) {
				this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				this.onStatusEffectApplied(statusEffectInstance);
				return true;
			} else if (statusEffectInstance2.upgrade(statusEffectInstance)) {
				this.onStatusEffectUpgraded(statusEffectInstance2, true);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean canHaveStatusEffect(StatusEffectInstance statusEffectInstance) {
		if (this.getGroup() == EntityGroup.UNDEAD) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			if (statusEffect == StatusEffects.field_5924 || statusEffect == StatusEffects.field_5899) {
				return false;
			}
		}

		return true;
	}

	public boolean isUndead() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	@Nullable
	public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activeStatusEffects.remove(statusEffect);
	}

	public boolean removeStatusEffect(StatusEffect statusEffect) {
		StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(statusEffect);
		if (statusEffectInstance != null) {
			this.onStatusEffectRemoved(statusEffectInstance);
			return true;
		} else {
			return false;
		}
	}

	protected void onStatusEffectApplied(StatusEffectInstance statusEffectInstance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			statusEffectInstance.getEffectType().onApplied(this, this.getAttributes(), statusEffectInstance.getAmplifier());
		}
	}

	protected void onStatusEffectUpgraded(StatusEffectInstance statusEffectInstance, boolean bl) {
		this.effectsChanged = true;
		if (bl && !this.world.isClient) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			statusEffect.onRemoved(this, this.getAttributes(), statusEffectInstance.getAmplifier());
			statusEffect.onApplied(this, this.getAttributes(), statusEffectInstance.getAmplifier());
		}
	}

	protected void onStatusEffectRemoved(StatusEffectInstance statusEffectInstance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			statusEffectInstance.getEffectType().onRemoved(this, this.getAttributes(), statusEffectInstance.getAmplifier());
		}
	}

	public void heal(float f) {
		float g = this.getHealth();
		if (g > 0.0F) {
			this.setHealth(g + f);
		}
	}

	public float getHealth() {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		// java.lang.OutOfMemoryError: Java heap space
		//   at java.base/jdk.internal.misc.Unsafe.allocateUninitializedArray(Unsafe.java:1375)
		//   at java.base/java.lang.StringConcatHelper.newArray(StringConcatHelper.java:497)
		//   at java.base/java.lang.StringLatin1.replace(StringLatin1.java:313)
		//   at java.base/java.lang.String.replace(String.java:2808)
		//   at org.jetbrains.java.decompiler.main.collectors.ImportCollector.getShortName(ImportCollector.java:103)
		//   at org.jetbrains.java.decompiler.main.collectors.ImportCollector.getShortName(ImportCollector.java:99)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getTypeName(ExprProcessor.java:778)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastTypeName(ExprProcessor.java:799)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastTypeName(ExprProcessor.java:795)
		//   at org.jetbrains.java.decompiler.util.TextBuffer.appendCastTypeName(TextBuffer.java:91)
		//   at org.jetbrains.java.decompiler.modules.decompiler.exps.ConstExprent.toJava(ConstExprent.java:225)
		//   at org.jetbrains.java.decompiler.modules.decompiler.exps.ConstExprent.toString(ConstExprent.java:654)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.markExprOddity(ExprProcessor.java:743)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.markExprOddities(ExprProcessor.java:720)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.markExprOddities(ExprProcessor.java:707)
		//   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.markExprOddities(ExprProcessor.java:701)
		//   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:454)
		//
		// Bytecode:
		// 00: aload 0
		// 01: getfield net/minecraft/entity/LivingEntity.dataTracker Lnet/minecraft/entity/data/DataTracker;
		// 04: getstatic net/minecraft/entity/LivingEntity.HEALTH Lnet/minecraft/entity/data/TrackedData;
		// 07: invokevirtual net/minecraft/entity/data/DataTracker.get (Lnet/minecraft/entity/data/TrackedData;)Ljava/lang/Object;
		// 0a: checkcast java/lang/Float
		// 0d: invokevirtual java/lang/Float.floatValue ()F
		// 10: freturn
	}

	public void setHealth(float f) {
		this.dataTracker.set(HEALTH, MathHelper.clamp(f, 0.0F, this.getMaximumHealth()));
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.world.isClient) {
			return false;
		} else if (this.getHealth() <= 0.0F) {
			return false;
		} else if (damageSource.isFire() && this.hasStatusEffect(StatusEffects.field_5918)) {
			return false;
		} else {
			if (this.isSleeping() && !this.world.isClient) {
				this.wakeUp();
			}

			this.despawnCounter = 0;
			float g = f;
			if ((damageSource == DamageSource.ANVIL || damageSource == DamageSource.FALLING_BLOCK) && !this.getEquippedStack(EquipmentSlot.field_6169).isEmpty()) {
				this.getEquippedStack(EquipmentSlot.field_6169)
					.damage((int)(f * 4.0F + this.random.nextFloat() * f * 2.0F), this, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6169));
				f *= 0.75F;
			}

			boolean bl = false;
			float h = 0.0F;
			if (f > 0.0F && this.blockedByShield(damageSource)) {
				this.damageShield(f);
				h = f;
				f = 0.0F;
				if (!damageSource.isProjectile()) {
					Entity entity = damageSource.getSource();
					if (entity instanceof LivingEntity) {
						this.takeShieldHit((LivingEntity)entity);
					}
				}

				bl = true;
			}

			this.limbDistance = 1.5F;
			boolean bl2 = true;
			if ((float)this.timeUntilRegen > 10.0F) {
				if (f <= this.lastDamageTaken) {
					return false;
				}

				this.applyDamage(damageSource, f - this.lastDamageTaken);
				this.lastDamageTaken = f;
				bl2 = false;
			} else {
				this.lastDamageTaken = f;
				this.timeUntilRegen = 20;
				this.applyDamage(damageSource, f);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}

			this.knockbackVelocity = 0.0F;
			Entity entity2 = damageSource.getAttacker();
			if (entity2 != null) {
				if (entity2 instanceof LivingEntity) {
					this.setAttacker((LivingEntity)entity2);
				}

				if (entity2 instanceof PlayerEntity) {
					this.playerHitTimer = 100;
					this.attackingPlayer = (PlayerEntity)entity2;
				} else if (entity2 instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity2;
					if (wolfEntity.isTamed()) {
						this.playerHitTimer = 100;
						LivingEntity livingEntity = wolfEntity.getOwner();
						if (livingEntity != null && livingEntity.getType() == EntityType.field_6097) {
							this.attackingPlayer = (PlayerEntity)livingEntity;
						} else {
							this.attackingPlayer = null;
						}
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.world.sendEntityStatus(this, (byte)29);
				} else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
					this.world.sendEntityStatus(this, (byte)33);
				} else {
					byte b;
					if (damageSource == DamageSource.DROWN) {
						b = 36;
					} else if (damageSource.isFire()) {
						b = 37;
					} else if (damageSource == DamageSource.SWEET_BERRY_BUSH) {
						b = 44;
					} else {
						b = 2;
					}

					this.world.sendEntityStatus(this, b);
				}

				if (damageSource != DamageSource.DROWN && (!bl || f > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity2 != null) {
					double i = entity2.getX() - this.getX();

					double j;
					for (j = entity2.getZ() - this.getZ(); i * i + j * j < 1.0E-4; j = (Math.random() - Math.random()) * 0.01) {
						i = (Math.random() - Math.random()) * 0.01;
					}

					this.knockbackVelocity = (float)(MathHelper.atan2(j, i) * 180.0F / (float)Math.PI - (double)this.yaw);
					this.takeKnockback(entity2, 0.4F, i, j);
				} else {
					this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.getHealth() <= 0.0F) {
				if (!this.tryUseTotem(damageSource)) {
					SoundEvent soundEvent = this.getDeathSound();
					if (bl2 && soundEvent != null) {
						this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(damageSource);
				}
			} else if (bl2) {
				this.playHurtSound(damageSource);
			}

			boolean bl3 = !bl || f > 0.0F;
			if (bl3) {
				this.lastDamageSource = damageSource;
				this.lastDamageTime = this.world.getTime();
			}

			if (this instanceof ServerPlayerEntity) {
				Criterions.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, damageSource, g, f, bl);
				if (h > 0.0F && h < 3.4028235E37F) {
					((ServerPlayerEntity)this).increaseStat(Stats.field_15380, Math.round(h * 10.0F));
				}
			}

			if (entity2 instanceof ServerPlayerEntity) {
				Criterions.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, damageSource, g, f, bl);
			}

			return bl3;
		}
	}

	protected void takeShieldHit(LivingEntity livingEntity) {
		livingEntity.knockback(this);
	}

	protected void knockback(LivingEntity livingEntity) {
		livingEntity.takeKnockback(this, 0.5F, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
	}

	private boolean tryUseTotem(DamageSource damageSource) {
		if (damageSource.isOutOfWorld()) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.getStackInHand(hand);
				if (itemStack2.getItem() == Items.field_8288) {
					itemStack = itemStack2.copy();
					itemStack2.decrement(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
					serverPlayerEntity.incrementStat(Stats.field_15372.getOrCreateStat(Items.field_8288));
					Criterions.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
				}

				this.setHealth(1.0F);
				this.clearStatusEffects();
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.field_5924, 900, 1));
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.field_5898, 100, 1));
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

	protected void playHurtSound(DamageSource damageSource) {
		SoundEvent soundEvent = this.getHurtSound(damageSource);
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private boolean blockedByShield(DamageSource damageSource) {
		Entity entity = damageSource.getSource();
		boolean bl = false;
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.getPierceLevel() > 0) {
				bl = true;
			}
		}

		if (!damageSource.bypassesArmor() && this.isBlocking() && !bl) {
			Vec3d vec3d = damageSource.getPosition();
			if (vec3d != null) {
				Vec3d vec3d2 = this.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.reverseSubtract(this.getPos()).normalize();
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
				if (vec3d3.dotProduct(vec3d2) < 0.0) {
					return true;
				}
			}
		}

		return false;
	}

	private void playEquipmentBreakEffects(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.getX(), this.getY(), this.getZ(), SoundEvents.field_15075, this.getSoundCategory(), 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F, false
					);
			}

			this.spawnItemParticles(itemStack, 5);
		}
	}

	public void onDeath(DamageSource damageSource) {
		if (!this.removed && !this.dead) {
			Entity entity = damageSource.getAttacker();
			LivingEntity livingEntity = this.getPrimeAdversary();
			if (this.scoreAmount >= 0 && livingEntity != null) {
				livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
			}

			if (entity != null) {
				entity.onKilledOther(this);
			}

			if (this.isSleeping()) {
				this.wakeUp();
			}

			this.dead = true;
			this.getDamageTracker().update();
			if (!this.world.isClient) {
				this.drop(damageSource);
				this.onKilledBy(livingEntity);
			}

			this.world.sendEntityStatus(this, (byte)3);
			this.setPose(EntityPose.field_18082);
		}
	}

	protected void onKilledBy(@Nullable LivingEntity livingEntity) {
		if (!this.world.isClient) {
			boolean bl = false;
			if (livingEntity instanceof WitherEntity) {
				if (this.world.getGameRules().getBoolean(GameRules.field_19388)) {
					BlockPos blockPos = new BlockPos(this);
					BlockState blockState = Blocks.field_10606.getDefaultState();
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

	protected void drop(DamageSource damageSource) {
		Entity entity = damageSource.getAttacker();
		int i;
		if (entity instanceof PlayerEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		} else {
			i = 0;
		}

		boolean bl = this.playerHitTimer > 0;
		if (this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.field_19391)) {
			this.dropLoot(damageSource, bl);
			this.dropEquipment(damageSource, i, bl);
		}

		this.dropInventory();
		this.dropXp();
	}

	protected void dropInventory() {
	}

	protected void dropXp() {
		if (!this.world.isClient
			&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.field_19391))) {
			int i = this.getCurrentExperience(this.attackingPlayer);

			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
			}
		}
	}

	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
	}

	public Identifier getLootTable() {
		return this.getType().getLootTableId();
	}

	protected void dropLoot(DamageSource damageSource, boolean bl) {
		Identifier identifier = this.getLootTable();
		LootTable lootTable = this.world.getServer().getLootManager().getSupplier(identifier);
		LootContext.Builder builder = this.getLootContextBuilder(bl, damageSource);
		lootTable.dropLimited(builder.build(LootContextTypes.field_1173), this::dropStack);
	}

	protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource damageSource) {
		LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
			.setRandom(this.random)
			.put(LootContextParameters.field_1226, this)
			.put(LootContextParameters.field_1232, new BlockPos(this))
			.put(LootContextParameters.field_1231, damageSource)
			.putNullable(LootContextParameters.field_1230, damageSource.getAttacker())
			.putNullable(LootContextParameters.field_1227, damageSource.getSource());
		if (bl && this.attackingPlayer != null) {
			builder = builder.put(LootContextParameters.field_1233, this.attackingPlayer).setLuck(this.attackingPlayer.getLuck());
		}

		return builder;
	}

	public void takeKnockback(Entity entity, float f, double d, double e) {
		if (!(this.random.nextDouble() < this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue())) {
			this.velocityDirty = true;
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = new Vec3d(d, 0.0, e).normalize().multiply((double)f);
			this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + (double)f) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
		}
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14940;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14732;
	}

	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.field_14928 : SoundEvents.field_15018;
	}

	protected SoundEvent getDrinkSound(ItemStack itemStack) {
		return itemStack.getDrinkSound();
	}

	public SoundEvent getEatSound(ItemStack itemStack) {
		return itemStack.getEatSound();
	}

	public boolean isClimbing() {
		if (this.isSpectator()) {
			return false;
		} else {
			BlockState blockState = this.getBlockState();
			Block block = blockState.getBlock();
			return block != Blocks.field_9983 && block != Blocks.field_10597 && block != Blocks.field_16492
				? block instanceof TrapdoorBlock && this.canEnterTrapdoor(new BlockPos(this), blockState)
				: true;
		}
	}

	public BlockState getBlockState() {
		return this.world.getBlockState(new BlockPos(this));
	}

	private boolean canEnterTrapdoor(BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.get(TrapdoorBlock.OPEN)) {
			BlockState blockState2 = this.world.getBlockState(blockPos.down());
			if (blockState2.getBlock() == Blocks.field_9983 && blockState2.get(LadderBlock.FACING) == blockState.get(TrapdoorBlock.FACING)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAlive() {
		return !this.removed && this.getHealth() > 0.0F;
	}

	@Override
	public boolean handleFallDamage(float f, float g) {
		boolean bl = super.handleFallDamage(f, g);
		int i = this.computeFallDamage(f, g);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.playBlockFallSound();
			this.damage(DamageSource.FALL, (float)i);
			return true;
		} else {
			return bl;
		}
	}

	protected int computeFallDamage(float f, float g) {
		StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.field_5913);
		float h = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
		return MathHelper.ceil((f - 3.0F - h) * g);
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
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.ARMOR);
		return MathHelper.floor(entityAttributeInstance.getValue());
	}

	protected void damageArmor(float f) {
	}

	protected void damageShield(float f) {
	}

	protected float applyArmorToDamage(DamageSource damageSource, float f) {
		if (!damageSource.bypassesArmor()) {
			this.damageArmor(f);
			f = DamageUtil.getDamageLeft(f, (float)this.getArmor(), (float)this.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
		}

		return f;
	}

	protected float applyEnchantmentsToDamage(DamageSource damageSource, float f) {
		if (damageSource.isUnblockable()) {
			return f;
		} else {
			if (this.hasStatusEffect(StatusEffects.field_5907) && damageSource != DamageSource.OUT_OF_WORLD) {
				int i = (this.getStatusEffect(StatusEffects.field_5907).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float g = f * (float)j;
				float h = f;
				f = Math.max(g / 25.0F, 0.0F);
				float k = h - f;
				if (k > 0.0F && k < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).increaseStat(Stats.field_15425, Math.round(k * 10.0F));
					} else if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.field_15397, Math.round(k * 10.0F));
					}
				}
			}

			if (f <= 0.0F) {
				return 0.0F;
			} else {
				int l = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), damageSource);
				if (l > 0) {
					f = DamageUtil.getInflictedDamage(f, (float)l);
				}

				return f;
			}
		}
	}

	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			f = this.applyArmorToDamage(damageSource, f);
			f = this.applyEnchantmentsToDamage(damageSource, f);
			float var8 = Math.max(f - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F && damageSource.getAttacker() instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.field_15408, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				float i = this.getHealth();
				this.setHealth(i - var8);
				this.getDamageTracker().onDamage(damageSource, i, var8);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - var8);
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

	public final float getMaximumHealth() {
		return (float)this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getValue();
	}

	public final int getStuckArrowCount() {
		return this.dataTracker.get(STUCK_ARROW_COUNT);
	}

	public final void setStuckArrowCount(int i) {
		this.dataTracker.set(STUCK_ARROW_COUNT, i);
	}

	public final int getStingerCount() {
		return this.dataTracker.get(STINGER_COUNT);
	}

	public final void setStingerCount(int i) {
		this.dataTracker.set(STINGER_COUNT, i);
	}

	private int getHandSwingDuration() {
		if (StatusEffectUtil.hasHaste(this)) {
			return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
		} else {
			return this.hasStatusEffect(StatusEffects.field_5901) ? 6 + (1 + this.getStatusEffect(StatusEffects.field_5901).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		this.swingHand(hand, false);
	}

	public void swingHand(Hand hand, boolean bl) {
		if (!this.isHandSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.isHandSwinging = true;
			this.preferredHand = hand;
			if (this.world instanceof ServerWorld) {
				EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(this, hand == Hand.field_5808 ? 0 : 3);
				ServerChunkManager serverChunkManager = ((ServerWorld)this.world).getChunkManager();
				if (bl) {
					serverChunkManager.sendToNearbyPlayers(this, entityAnimationS2CPacket);
				} else {
					serverChunkManager.sendToOtherNearbyPlayers(this, entityAnimationS2CPacket);
				}
			}
		}
	}

	@Override
	public void handleStatus(byte b) {
		switch (b) {
			case 2:
			case 33:
			case 36:
			case 37:
			case 44:
				boolean bl = b == 33;
				boolean bl2 = b == 36;
				boolean bl3 = b == 37;
				boolean bl4 = b == 44;
				this.limbDistance = 1.5F;
				this.timeUntilRegen = 20;
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
				this.knockbackVelocity = 0.0F;
				if (bl) {
					this.playSound(SoundEvents.field_14663, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				DamageSource damageSource;
				if (bl3) {
					damageSource = DamageSource.ON_FIRE;
				} else if (bl2) {
					damageSource = DamageSource.DROWN;
				} else if (bl4) {
					damageSource = DamageSource.SWEET_BERRY_BUSH;
				} else {
					damageSource = DamageSource.GENERIC;
				}

				SoundEvent soundEvent = this.getHurtSound(damageSource);
				if (soundEvent != null) {
					this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.damage(DamageSource.GENERIC, 0.0F);
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
			default:
				super.handleStatus(b);
				break;
			case 29:
				this.playSound(SoundEvents.field_15150, 1.0F, 0.8F + this.world.random.nextFloat() * 0.4F);
				break;
			case 30:
				this.playSound(SoundEvents.field_15239, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
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
					this.world.addParticle(ParticleTypes.field_11214, e, k, l, (double)f, (double)g, (double)h);
				}
				break;
			case 47:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6173));
				break;
			case 48:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6171));
				break;
			case 49:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6169));
				break;
			case 50:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6174));
				break;
			case 51:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6172));
				break;
			case 52:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6166));
				break;
			case 54:
				HoneyBlock.addRichParticles(this);
		}
	}

	@Override
	protected void destroy() {
		this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void tickHandSwing() {
		int i = this.getHandSwingDuration();
		if (this.isHandSwinging) {
			this.handSwingTicks++;
			if (this.handSwingTicks >= i) {
				this.handSwingTicks = 0;
				this.isHandSwinging = false;
			}
		} else {
			this.handSwingTicks = 0;
		}

		this.handSwingProgress = (float)this.handSwingTicks / (float)i;
	}

	public EntityAttributeInstance getAttributeInstance(EntityAttribute entityAttribute) {
		return this.getAttributes().get(entityAttribute);
	}

	public AbstractEntityAttributeContainer getAttributes() {
		if (this.attributes == null) {
			this.attributes = new EntityAttributeContainer();
		}

		return this.attributes;
	}

	public EntityGroup getGroup() {
		return EntityGroup.DEFAULT;
	}

	public ItemStack getMainHandStack() {
		return this.getEquippedStack(EquipmentSlot.field_6173);
	}

	public ItemStack getOffHandStack() {
		return this.getEquippedStack(EquipmentSlot.field_6171);
	}

	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.field_5808) {
			return this.getEquippedStack(EquipmentSlot.field_6173);
		} else if (hand == Hand.field_5810) {
			return this.getEquippedStack(EquipmentSlot.field_6171);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void setStackInHand(Hand hand, ItemStack itemStack) {
		if (hand == Hand.field_5808) {
			this.equipStack(EquipmentSlot.field_6173, itemStack);
		} else {
			if (hand != Hand.field_5810) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.equipStack(EquipmentSlot.field_6171, itemStack);
		}
	}

	public boolean hasStackEquipped(EquipmentSlot equipmentSlot) {
		return !this.getEquippedStack(equipmentSlot).isEmpty();
	}

	@Override
	public abstract Iterable<ItemStack> getArmorItems();

	public abstract ItemStack getEquippedStack(EquipmentSlot equipmentSlot);

	@Override
	public abstract void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack);

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
	public void setSprinting(boolean bl) {
		super.setSprinting(bl);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (entityAttributeInstance.getModifier(ATTR_SPRINTING_SPEED_BOOST_ID) != null) {
			entityAttributeInstance.removeModifier(ATTR_SPRINTING_SPEED_BOOST);
		}

		if (bl) {
			entityAttributeInstance.addModifier(ATTR_SPRINTING_SPEED_BOOST);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean isImmobile() {
		return this.getHealth() <= 0.0F;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	private void onDismounted(Entity entity) {
		if (this.world.getBlockState(new BlockPos(entity)).getBlock().matches(BlockTags.field_21780)) {
			this.updatePosition(entity.getX(), entity.getBodyY(1.0) + 0.001, entity.getZ());
		} else if (!(entity instanceof BoatEntity) && !(entity instanceof HorseBaseEntity)) {
			double t = entity.getX();
			double u = entity.getBodyY(1.0);
			double v = entity.getZ();
			Direction direction = entity.getMovementDirection();
			if (direction != null && direction.getAxis() != Direction.Axis.field_11052) {
				Direction direction2 = direction.rotateYClockwise();
				int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
				double w = Math.floor(this.getX()) + 0.5;
				double x = Math.floor(this.getZ()) + 0.5;
				double y = this.getBoundingBox().x2 - this.getBoundingBox().x1;
				double z = this.getBoundingBox().z2 - this.getBoundingBox().z1;
				Box box3 = new Box(
					w - y / 2.0, entity.getBoundingBox().y1, x - z / 2.0, w + y / 2.0, Math.floor(entity.getBoundingBox().y1) + (double)this.getHeight(), x + z / 2.0
				);

				for (int[] js : is) {
					double aa = (double)(direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1]);
					double ab = (double)(direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1]);
					double ac = w + aa;
					double ad = x + ab;
					Box box4 = box3.offset(aa, 0.0, ab);
					if (this.world.doesNotCollide(this, box4)) {
						BlockPos blockPos = new BlockPos(ac, this.getY(), ad);
						if (this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) {
							this.requestTeleport(ac, this.getY() + 1.0, ad);
							return;
						}

						BlockPos blockPos2 = new BlockPos(ac, this.getY() - 1.0, ad);
						if (this.world.getBlockState(blockPos2).hasSolidTopSurface(this.world, blockPos2, this)
							|| this.world.getFluidState(blockPos2).matches(FluidTags.field_15517)) {
							t = ac;
							u = this.getY() + 1.0;
							v = ad;
						}
					} else {
						BlockPos blockPos3 = new BlockPos(ac, this.getY() + 1.0, ad);
						if (this.world.doesNotCollide(this, box4.offset(0.0, 1.0, 0.0)) && this.world.getBlockState(blockPos3).hasSolidTopSurface(this.world, blockPos3, this)) {
							t = ac;
							u = this.getY() + 2.0;
							v = ad;
						}
					}
				}
			}

			this.requestTeleport(t, u, v);
		} else {
			double d = (double)(this.getWidth() / 2.0F + entity.getWidth() / 2.0F) + 0.4;
			Box box = entity.getBoundingBox();
			float f;
			double e;
			int i;
			if (entity instanceof BoatEntity) {
				e = box.y2;
				i = 2;
				f = 0.0F;
			} else {
				e = box.y1;
				i = 3;
				f = (float) (Math.PI / 2) * (float)(this.getMainArm() == Arm.field_6183 ? -1 : 1);
			}

			float k = -this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f;
			float l = -MathHelper.sin(k);
			float m = -MathHelper.cos(k);
			double n = Math.abs(l) > Math.abs(m) ? d / (double)Math.abs(l) : d / (double)Math.abs(m);
			Box box2 = this.getBoundingBox().offset(-this.getX(), -this.getY(), -this.getZ());
			ImmutableSet<Entity> immutableSet = ImmutableSet.of(this, entity);
			double o = this.getX() + (double)l * n;
			double p = this.getZ() + (double)m * n;
			double q = 0.001;

			for (int r = 0; r < i; r++) {
				double s = e + q;
				if (this.world.doesNotCollide(this, box2.offset(o, s, p), immutableSet)) {
					this.updatePosition(o, s, p);
					return;
				}

				q++;
			}

			this.updatePosition(entity.getX(), entity.getBodyY(1.0) + 0.001, entity.getZ());
		}
	}

	@Override
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	protected float getJumpVelocity() {
		return 0.42F * this.getJumpVelocityMultiplier();
	}

	protected void jump() {
		float f = this.getJumpVelocity();
		if (this.hasStatusEffect(StatusEffects.field_5913)) {
			f += 0.1F * (float)(this.getStatusEffect(StatusEffects.field_5913).getAmplifier() + 1);
		}

		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)f, vec3d.z);
		if (this.isSprinting()) {
			float g = this.yaw * (float) (Math.PI / 180.0);
			this.setVelocity(this.getVelocity().add((double)(-MathHelper.sin(g) * 0.2F), 0.0, (double)(MathHelper.cos(g) * 0.2F)));
		}

		this.velocityDirty = true;
	}

	protected void knockDownwards() {
		this.setVelocity(this.getVelocity().add(0.0, -0.04F, 0.0));
	}

	protected void swimUpward(Tag<Fluid> tag) {
		this.setVelocity(this.getVelocity().add(0.0, 0.04F, 0.0));
	}

	protected float getBaseMovementSpeedMultiplier() {
		return 0.8F;
	}

	public void travel(Vec3d vec3d) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: aload 0
		// 001: invokevirtual net/minecraft/entity/LivingEntity.canMoveVoluntarily ()Z
		// 004: ifne 00e
		// 007: aload 0
		// 008: invokevirtual net/minecraft/entity/LivingEntity.isLogicalSideForUpdatingMovement ()Z
		// 00b: ifeq 548
		// 00e: ldc2_w 0.08
		// 011: dstore 2
		// 012: aload 0
		// 013: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 016: getfield net/minecraft/util/math/Vec3d.y D
		// 019: dconst_0
		// 01a: dcmpg
		// 01b: ifgt 022
		// 01e: bipush 1
		// 01f: goto 023
		// 022: bipush 0
		// 023: istore 4
		// 025: iload 4
		// 027: ifeq 03d
		// 02a: aload 0
		// 02b: getstatic net/minecraft/entity/effect/StatusEffects.field_5906 Lnet/minecraft/entity/effect/StatusEffect;
		// 02e: invokevirtual net/minecraft/entity/LivingEntity.hasStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Z
		// 031: ifeq 03d
		// 034: ldc2_w 0.01
		// 037: dstore 2
		// 038: aload 0
		// 039: fconst_0
		// 03a: putfield net/minecraft/entity/LivingEntity.fallDistance F
		// 03d: aload 0
		// 03e: invokevirtual net/minecraft/entity/LivingEntity.isTouchingWater ()Z
		// 041: ifeq 1cb
		// 044: aload 0
		// 045: instanceof net/minecraft/entity/player/PlayerEntity
		// 048: ifeq 058
		// 04b: aload 0
		// 04c: checkcast net/minecraft/entity/player/PlayerEntity
		// 04f: getfield net/minecraft/entity/player/PlayerEntity.abilities Lnet/minecraft/entity/player/PlayerAbilities;
		// 052: getfield net/minecraft/entity/player/PlayerAbilities.flying Z
		// 055: ifne 1cb
		// 058: aload 0
		// 059: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 05c: dstore 5
		// 05e: aload 0
		// 05f: invokevirtual net/minecraft/entity/LivingEntity.isSprinting ()Z
		// 062: ifeq 06b
		// 065: ldc_w 0.9
		// 068: goto 06f
		// 06b: aload 0
		// 06c: invokevirtual net/minecraft/entity/LivingEntity.getBaseMovementSpeedMultiplier ()F
		// 06f: fstore 7
		// 071: ldc 0.02
		// 073: fstore 8
		// 075: aload 0
		// 076: invokestatic net/minecraft/enchantment/EnchantmentHelper.getDepthStrider (Lnet/minecraft/entity/LivingEntity;)I
		// 079: i2f
		// 07a: fstore 9
		// 07c: fload 9
		// 07e: ldc_w 3.0
		// 081: fcmpl
		// 082: ifle 08a
		// 085: ldc_w 3.0
		// 088: fstore 9
		// 08a: aload 0
		// 08b: getfield net/minecraft/entity/LivingEntity.onGround Z
		// 08e: ifne 099
		// 091: fload 9
		// 093: ldc_w 0.5
		// 096: fmul
		// 097: fstore 9
		// 099: fload 9
		// 09b: fconst_0
		// 09c: fcmpl
		// 09d: ifle 0c5
		// 0a0: fload 7
		// 0a2: ldc_w 0.54600006
		// 0a5: fload 7
		// 0a7: fsub
		// 0a8: fload 9
		// 0aa: fmul
		// 0ab: ldc_w 3.0
		// 0ae: fdiv
		// 0af: fadd
		// 0b0: fstore 7
		// 0b2: fload 8
		// 0b4: aload 0
		// 0b5: invokevirtual net/minecraft/entity/LivingEntity.getMovementSpeed ()F
		// 0b8: fload 8
		// 0ba: fsub
		// 0bb: fload 9
		// 0bd: fmul
		// 0be: ldc_w 3.0
		// 0c1: fdiv
		// 0c2: fadd
		// 0c3: fstore 8
		// 0c5: aload 0
		// 0c6: getstatic net/minecraft/entity/effect/StatusEffects.field_5900 Lnet/minecraft/entity/effect/StatusEffect;
		// 0c9: invokevirtual net/minecraft/entity/LivingEntity.hasStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Z
		// 0cc: ifeq 0d4
		// 0cf: ldc_w 0.96
		// 0d2: fstore 7
		// 0d4: aload 0
		// 0d5: fload 8
		// 0d7: aload 1
		// 0d8: invokevirtual net/minecraft/entity/LivingEntity.updateVelocity (FLnet/minecraft/util/math/Vec3d;)V
		// 0db: aload 0
		// 0dc: getstatic net/minecraft/entity/MovementType.field_6308 Lnet/minecraft/entity/MovementType;
		// 0df: aload 0
		// 0e0: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 0e3: invokevirtual net/minecraft/entity/LivingEntity.move (Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V
		// 0e6: aload 0
		// 0e7: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 0ea: astore 10
		// 0ec: aload 0
		// 0ed: getfield net/minecraft/entity/LivingEntity.horizontalCollision Z
		// 0f0: ifeq 110
		// 0f3: aload 0
		// 0f4: invokevirtual net/minecraft/entity/LivingEntity.isClimbing ()Z
		// 0f7: ifeq 110
		// 0fa: new net/minecraft/util/math/Vec3d
		// 0fd: dup
		// 0fe: aload 10
		// 100: getfield net/minecraft/util/math/Vec3d.x D
		// 103: ldc2_w 0.2
		// 106: aload 10
		// 108: getfield net/minecraft/util/math/Vec3d.z D
		// 10b: invokespecial net/minecraft/util/math/Vec3d.<init> (DDD)V
		// 10e: astore 10
		// 110: aload 0
		// 111: aload 10
		// 113: fload 7
		// 115: f2d
		// 116: ldc2_w 0.800000011920929
		// 119: fload 7
		// 11b: f2d
		// 11c: invokevirtual net/minecraft/util/math/Vec3d.multiply (DDD)Lnet/minecraft/util/math/Vec3d;
		// 11f: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 122: aload 0
		// 123: invokevirtual net/minecraft/entity/LivingEntity.hasNoGravity ()Z
		// 126: ifne 188
		// 129: aload 0
		// 12a: invokevirtual net/minecraft/entity/LivingEntity.isSprinting ()Z
		// 12d: ifne 188
		// 130: aload 0
		// 131: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 134: astore 11
		// 136: iload 4
		// 138: ifeq 16b
		// 13b: aload 11
		// 13d: getfield net/minecraft/util/math/Vec3d.y D
		// 140: ldc2_w 0.005
		// 143: dsub
		// 144: invokestatic java/lang/Math.abs (D)D
		// 147: ldc2_w 0.003
		// 14a: dcmpl
		// 14b: iflt 16b
		// 14e: aload 11
		// 150: getfield net/minecraft/util/math/Vec3d.y D
		// 153: dload 2
		// 154: ldc2_w 16.0
		// 157: ddiv
		// 158: dsub
		// 159: invokestatic java/lang/Math.abs (D)D
		// 15c: ldc2_w 0.003
		// 15f: dcmpg
		// 160: ifge 16b
		// 163: ldc2_w -0.003
		// 166: dstore 12
		// 168: goto 178
		// 16b: aload 11
		// 16d: getfield net/minecraft/util/math/Vec3d.y D
		// 170: dload 2
		// 171: ldc2_w 16.0
		// 174: ddiv
		// 175: dsub
		// 176: dstore 12
		// 178: aload 0
		// 179: aload 11
		// 17b: getfield net/minecraft/util/math/Vec3d.x D
		// 17e: dload 12
		// 180: aload 11
		// 182: getfield net/minecraft/util/math/Vec3d.z D
		// 185: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (DDD)V
		// 188: aload 0
		// 189: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 18c: astore 11
		// 18e: aload 0
		// 18f: getfield net/minecraft/entity/LivingEntity.horizontalCollision Z
		// 192: ifeq 1c8
		// 195: aload 0
		// 196: aload 11
		// 198: getfield net/minecraft/util/math/Vec3d.x D
		// 19b: aload 11
		// 19d: getfield net/minecraft/util/math/Vec3d.y D
		// 1a0: ldc2_w 0.6000000238418579
		// 1a3: dadd
		// 1a4: aload 0
		// 1a5: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 1a8: dsub
		// 1a9: dload 5
		// 1ab: dadd
		// 1ac: aload 11
		// 1ae: getfield net/minecraft/util/math/Vec3d.z D
		// 1b1: invokevirtual net/minecraft/entity/LivingEntity.doesNotCollide (DDD)Z
		// 1b4: ifeq 1c8
		// 1b7: aload 0
		// 1b8: aload 11
		// 1ba: getfield net/minecraft/util/math/Vec3d.x D
		// 1bd: ldc2_w 0.30000001192092896
		// 1c0: aload 11
		// 1c2: getfield net/minecraft/util/math/Vec3d.z D
		// 1c5: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (DDD)V
		// 1c8: goto 548
		// 1cb: aload 0
		// 1cc: invokevirtual net/minecraft/entity/LivingEntity.isInLava ()Z
		// 1cf: ifeq 269
		// 1d2: aload 0
		// 1d3: instanceof net/minecraft/entity/player/PlayerEntity
		// 1d6: ifeq 1e6
		// 1d9: aload 0
		// 1da: checkcast net/minecraft/entity/player/PlayerEntity
		// 1dd: getfield net/minecraft/entity/player/PlayerEntity.abilities Lnet/minecraft/entity/player/PlayerAbilities;
		// 1e0: getfield net/minecraft/entity/player/PlayerAbilities.flying Z
		// 1e3: ifne 269
		// 1e6: aload 0
		// 1e7: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 1ea: dstore 5
		// 1ec: aload 0
		// 1ed: ldc 0.02
		// 1ef: aload 1
		// 1f0: invokevirtual net/minecraft/entity/LivingEntity.updateVelocity (FLnet/minecraft/util/math/Vec3d;)V
		// 1f3: aload 0
		// 1f4: getstatic net/minecraft/entity/MovementType.field_6308 Lnet/minecraft/entity/MovementType;
		// 1f7: aload 0
		// 1f8: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 1fb: invokevirtual net/minecraft/entity/LivingEntity.move (Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V
		// 1fe: aload 0
		// 1ff: aload 0
		// 200: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 203: ldc2_w 0.5
		// 206: invokevirtual net/minecraft/util/math/Vec3d.multiply (D)Lnet/minecraft/util/math/Vec3d;
		// 209: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 20c: aload 0
		// 20d: invokevirtual net/minecraft/entity/LivingEntity.hasNoGravity ()Z
		// 210: ifne 226
		// 213: aload 0
		// 214: aload 0
		// 215: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 218: dconst_0
		// 219: dload 2
		// 21a: dneg
		// 21b: ldc2_w 4.0
		// 21e: ddiv
		// 21f: dconst_0
		// 220: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 223: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 226: aload 0
		// 227: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 22a: astore 7
		// 22c: aload 0
		// 22d: getfield net/minecraft/entity/LivingEntity.horizontalCollision Z
		// 230: ifeq 266
		// 233: aload 0
		// 234: aload 7
		// 236: getfield net/minecraft/util/math/Vec3d.x D
		// 239: aload 7
		// 23b: getfield net/minecraft/util/math/Vec3d.y D
		// 23e: ldc2_w 0.6000000238418579
		// 241: dadd
		// 242: aload 0
		// 243: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 246: dsub
		// 247: dload 5
		// 249: dadd
		// 24a: aload 7
		// 24c: getfield net/minecraft/util/math/Vec3d.z D
		// 24f: invokevirtual net/minecraft/entity/LivingEntity.doesNotCollide (DDD)Z
		// 252: ifeq 266
		// 255: aload 0
		// 256: aload 7
		// 258: getfield net/minecraft/util/math/Vec3d.x D
		// 25b: ldc2_w 0.30000001192092896
		// 25e: aload 7
		// 260: getfield net/minecraft/util/math/Vec3d.z D
		// 263: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (DDD)V
		// 266: goto 548
		// 269: aload 0
		// 26a: invokevirtual net/minecraft/entity/LivingEntity.isFallFlying ()Z
		// 26d: ifeq 43a
		// 270: aload 0
		// 271: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 274: astore 5
		// 276: aload 5
		// 278: getfield net/minecraft/util/math/Vec3d.y D
		// 27b: ldc2_w -0.5
		// 27e: dcmpl
		// 27f: ifle 287
		// 282: aload 0
		// 283: fconst_1
		// 284: putfield net/minecraft/entity/LivingEntity.fallDistance F
		// 287: aload 0
		// 288: invokevirtual net/minecraft/entity/LivingEntity.getRotationVector ()Lnet/minecraft/util/math/Vec3d;
		// 28b: astore 6
		// 28d: aload 0
		// 28e: getfield net/minecraft/entity/LivingEntity.pitch F
		// 291: ldc_w 0.017453292
		// 294: fmul
		// 295: fstore 7
		// 297: aload 6
		// 299: getfield net/minecraft/util/math/Vec3d.x D
		// 29c: aload 6
		// 29e: getfield net/minecraft/util/math/Vec3d.x D
		// 2a1: dmul
		// 2a2: aload 6
		// 2a4: getfield net/minecraft/util/math/Vec3d.z D
		// 2a7: aload 6
		// 2a9: getfield net/minecraft/util/math/Vec3d.z D
		// 2ac: dmul
		// 2ad: dadd
		// 2ae: invokestatic java/lang/Math.sqrt (D)D
		// 2b1: dstore 8
		// 2b3: aload 5
		// 2b5: invokestatic net/minecraft/entity/LivingEntity.squaredHorizontalLength (Lnet/minecraft/util/math/Vec3d;)D
		// 2b8: invokestatic java/lang/Math.sqrt (D)D
		// 2bb: dstore 10
		// 2bd: aload 6
		// 2bf: invokevirtual net/minecraft/util/math/Vec3d.length ()D
		// 2c2: dstore 12
		// 2c4: fload 7
		// 2c6: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 2c9: fstore 14
		// 2cb: fload 14
		// 2cd: f2d
		// 2ce: fload 14
		// 2d0: f2d
		// 2d1: dconst_1
		// 2d2: dload 12
		// 2d4: ldc2_w 0.4
		// 2d7: ddiv
		// 2d8: invokestatic java/lang/Math.min (DD)D
		// 2db: dmul
		// 2dc: dmul
		// 2dd: d2f
		// 2de: fstore 14
		// 2e0: aload 0
		// 2e1: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 2e4: dconst_0
		// 2e5: dload 2
		// 2e6: ldc2_w -1.0
		// 2e9: fload 14
		// 2eb: f2d
		// 2ec: ldc2_w 0.75
		// 2ef: dmul
		// 2f0: dadd
		// 2f1: dmul
		// 2f2: dconst_0
		// 2f3: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 2f6: astore 5
		// 2f8: aload 5
		// 2fa: getfield net/minecraft/util/math/Vec3d.y D
		// 2fd: dconst_0
		// 2fe: dcmpg
		// 2ff: ifge 337
		// 302: dload 8
		// 304: dconst_0
		// 305: dcmpl
		// 306: ifle 337
		// 309: aload 5
		// 30b: getfield net/minecraft/util/math/Vec3d.y D
		// 30e: ldc2_w -0.1
		// 311: dmul
		// 312: fload 14
		// 314: f2d
		// 315: dmul
		// 316: dstore 15
		// 318: aload 5
		// 31a: aload 6
		// 31c: getfield net/minecraft/util/math/Vec3d.x D
		// 31f: dload 15
		// 321: dmul
		// 322: dload 8
		// 324: ddiv
		// 325: dload 15
		// 327: aload 6
		// 329: getfield net/minecraft/util/math/Vec3d.z D
		// 32c: dload 15
		// 32e: dmul
		// 32f: dload 8
		// 331: ddiv
		// 332: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 335: astore 5
		// 337: fload 7
		// 339: fconst_0
		// 33a: fcmpg
		// 33b: ifge 37a
		// 33e: dload 8
		// 340: dconst_0
		// 341: dcmpl
		// 342: ifle 37a
		// 345: dload 10
		// 347: fload 7
		// 349: invokestatic net/minecraft/util/math/MathHelper.sin (F)F
		// 34c: fneg
		// 34d: f2d
		// 34e: dmul
		// 34f: ldc2_w 0.04
		// 352: dmul
		// 353: dstore 15
		// 355: aload 5
		// 357: aload 6
		// 359: getfield net/minecraft/util/math/Vec3d.x D
		// 35c: dneg
		// 35d: dload 15
		// 35f: dmul
		// 360: dload 8
		// 362: ddiv
		// 363: dload 15
		// 365: ldc2_w 3.2
		// 368: dmul
		// 369: aload 6
		// 36b: getfield net/minecraft/util/math/Vec3d.z D
		// 36e: dneg
		// 36f: dload 15
		// 371: dmul
		// 372: dload 8
		// 374: ddiv
		// 375: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 378: astore 5
		// 37a: dload 8
		// 37c: dconst_0
		// 37d: dcmpl
		// 37e: ifle 3b3
		// 381: aload 5
		// 383: aload 6
		// 385: getfield net/minecraft/util/math/Vec3d.x D
		// 388: dload 8
		// 38a: ddiv
		// 38b: dload 10
		// 38d: dmul
		// 38e: aload 5
		// 390: getfield net/minecraft/util/math/Vec3d.x D
		// 393: dsub
		// 394: ldc2_w 0.1
		// 397: dmul
		// 398: dconst_0
		// 399: aload 6
		// 39b: getfield net/minecraft/util/math/Vec3d.z D
		// 39e: dload 8
		// 3a0: ddiv
		// 3a1: dload 10
		// 3a3: dmul
		// 3a4: aload 5
		// 3a6: getfield net/minecraft/util/math/Vec3d.z D
		// 3a9: dsub
		// 3aa: ldc2_w 0.1
		// 3ad: dmul
		// 3ae: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 3b1: astore 5
		// 3b3: aload 0
		// 3b4: aload 5
		// 3b6: ldc2_w 0.9900000095367432
		// 3b9: ldc2_w 0.9800000190734863
		// 3bc: ldc2_w 0.9900000095367432
		// 3bf: invokevirtual net/minecraft/util/math/Vec3d.multiply (DDD)Lnet/minecraft/util/math/Vec3d;
		// 3c2: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 3c5: aload 0
		// 3c6: getstatic net/minecraft/entity/MovementType.field_6308 Lnet/minecraft/entity/MovementType;
		// 3c9: aload 0
		// 3ca: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 3cd: invokevirtual net/minecraft/entity/LivingEntity.move (Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V
		// 3d0: aload 0
		// 3d1: getfield net/minecraft/entity/LivingEntity.horizontalCollision Z
		// 3d4: ifeq 41f
		// 3d7: aload 0
		// 3d8: getfield net/minecraft/entity/LivingEntity.world Lnet/minecraft/world/World;
		// 3db: getfield net/minecraft/world/World.isClient Z
		// 3de: ifne 41f
		// 3e1: aload 0
		// 3e2: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 3e5: invokestatic net/minecraft/entity/LivingEntity.squaredHorizontalLength (Lnet/minecraft/util/math/Vec3d;)D
		// 3e8: invokestatic java/lang/Math.sqrt (D)D
		// 3eb: dstore 15
		// 3ed: dload 10
		// 3ef: dload 15
		// 3f1: dsub
		// 3f2: dstore 17
		// 3f4: dload 17
		// 3f6: ldc2_w 10.0
		// 3f9: dmul
		// 3fa: ldc2_w 3.0
		// 3fd: dsub
		// 3fe: d2f
		// 3ff: fstore 19
		// 401: fload 19
		// 403: fconst_0
		// 404: fcmpl
		// 405: ifle 41f
		// 408: aload 0
		// 409: aload 0
		// 40a: fload 19
		// 40c: f2i
		// 40d: invokevirtual net/minecraft/entity/LivingEntity.getFallSound (I)Lnet/minecraft/sound/SoundEvent;
		// 410: fconst_1
		// 411: fconst_1
		// 412: invokevirtual net/minecraft/entity/LivingEntity.playSound (Lnet/minecraft/sound/SoundEvent;FF)V
		// 415: aload 0
		// 416: getstatic net/minecraft/entity/damage/DamageSource.FLY_INTO_WALL Lnet/minecraft/entity/damage/DamageSource;
		// 419: fload 19
		// 41b: invokevirtual net/minecraft/entity/LivingEntity.damage (Lnet/minecraft/entity/damage/DamageSource;F)Z
		// 41e: pop
		// 41f: aload 0
		// 420: getfield net/minecraft/entity/LivingEntity.onGround Z
		// 423: ifeq 437
		// 426: aload 0
		// 427: getfield net/minecraft/entity/LivingEntity.world Lnet/minecraft/world/World;
		// 42a: getfield net/minecraft/world/World.isClient Z
		// 42d: ifne 437
		// 430: aload 0
		// 431: bipush 7
		// 433: bipush 0
		// 434: invokevirtual net/minecraft/entity/LivingEntity.setFlag (IZ)V
		// 437: goto 548
		// 43a: aload 0
		// 43b: invokevirtual net/minecraft/entity/LivingEntity.getVelocityAffectingPos ()Lnet/minecraft/util/math/BlockPos;
		// 43e: astore 5
		// 440: aload 0
		// 441: getfield net/minecraft/entity/LivingEntity.world Lnet/minecraft/world/World;
		// 444: aload 5
		// 446: invokevirtual net/minecraft/world/World.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 449: invokevirtual net/minecraft/block/BlockState.getBlock ()Lnet/minecraft/block/Block;
		// 44c: invokevirtual net/minecraft/block/Block.getSlipperiness ()F
		// 44f: fstore 6
		// 451: aload 0
		// 452: getfield net/minecraft/entity/LivingEntity.onGround Z
		// 455: ifeq 461
		// 458: fload 6
		// 45a: ldc_w 0.91
		// 45d: fmul
		// 45e: goto 464
		// 461: ldc_w 0.91
		// 464: fstore 7
		// 466: aload 0
		// 467: aload 0
		// 468: fload 6
		// 46a: invokespecial net/minecraft/entity/LivingEntity.getMovementSpeed (F)F
		// 46d: aload 1
		// 46e: invokevirtual net/minecraft/entity/LivingEntity.updateVelocity (FLnet/minecraft/util/math/Vec3d;)V
		// 471: aload 0
		// 472: aload 0
		// 473: aload 0
		// 474: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 477: invokespecial net/minecraft/entity/LivingEntity.applyClimbingSpeed (Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;
		// 47a: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 47d: aload 0
		// 47e: getstatic net/minecraft/entity/MovementType.field_6308 Lnet/minecraft/entity/MovementType;
		// 481: aload 0
		// 482: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 485: invokevirtual net/minecraft/entity/LivingEntity.move (Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V
		// 488: aload 0
		// 489: invokevirtual net/minecraft/entity/LivingEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 48c: astore 8
		// 48e: aload 0
		// 48f: getfield net/minecraft/entity/LivingEntity.horizontalCollision Z
		// 492: ifne 49c
		// 495: aload 0
		// 496: getfield net/minecraft/entity/LivingEntity.jumping Z
		// 499: ifeq 4b9
		// 49c: aload 0
		// 49d: invokevirtual net/minecraft/entity/LivingEntity.isClimbing ()Z
		// 4a0: ifeq 4b9
		// 4a3: new net/minecraft/util/math/Vec3d
		// 4a6: dup
		// 4a7: aload 8
		// 4a9: getfield net/minecraft/util/math/Vec3d.x D
		// 4ac: ldc2_w 0.2
		// 4af: aload 8
		// 4b1: getfield net/minecraft/util/math/Vec3d.z D
		// 4b4: invokespecial net/minecraft/util/math/Vec3d.<init> (DDD)V
		// 4b7: astore 8
		// 4b9: aload 8
		// 4bb: getfield net/minecraft/util/math/Vec3d.y D
		// 4be: dstore 9
		// 4c0: aload 0
		// 4c1: getstatic net/minecraft/entity/effect/StatusEffects.field_5902 Lnet/minecraft/entity/effect/StatusEffect;
		// 4c4: invokevirtual net/minecraft/entity/LivingEntity.hasStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Z
		// 4c7: ifeq 4f2
		// 4ca: dload 9
		// 4cc: ldc2_w 0.05
		// 4cf: aload 0
		// 4d0: getstatic net/minecraft/entity/effect/StatusEffects.field_5902 Lnet/minecraft/entity/effect/StatusEffect;
		// 4d3: invokevirtual net/minecraft/entity/LivingEntity.getStatusEffect (Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/entity/effect/StatusEffectInstance;
		// 4d6: invokevirtual net/minecraft/entity/effect/StatusEffectInstance.getAmplifier ()I
		// 4d9: bipush 1
		// 4da: iadd
		// 4db: i2d
		// 4dc: dmul
		// 4dd: aload 8
		// 4df: getfield net/minecraft/util/math/Vec3d.y D
		// 4e2: dsub
		// 4e3: ldc2_w 0.2
		// 4e6: dmul
		// 4e7: dadd
		// 4e8: dstore 9
		// 4ea: aload 0
		// 4eb: fconst_0
		// 4ec: putfield net/minecraft/entity/LivingEntity.fallDistance F
		// 4ef: goto 52c
		// 4f2: aload 0
		// 4f3: getfield net/minecraft/entity/LivingEntity.world Lnet/minecraft/world/World;
		// 4f6: getfield net/minecraft/world/World.isClient Z
		// 4f9: ifeq 508
		// 4fc: aload 0
		// 4fd: getfield net/minecraft/entity/LivingEntity.world Lnet/minecraft/world/World;
		// 500: aload 5
		// 502: invokevirtual net/minecraft/world/World.isChunkLoaded (Lnet/minecraft/util/math/BlockPos;)Z
		// 505: ifeq 518
		// 508: aload 0
		// 509: invokevirtual net/minecraft/entity/LivingEntity.hasNoGravity ()Z
		// 50c: ifne 52c
		// 50f: dload 9
		// 511: dload 2
		// 512: dsub
		// 513: dstore 9
		// 515: goto 52c
		// 518: aload 0
		// 519: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 51c: dconst_0
		// 51d: dcmpl
		// 51e: ifle 529
		// 521: ldc2_w -0.1
		// 524: dstore 9
		// 526: goto 52c
		// 529: dconst_0
		// 52a: dstore 9
		// 52c: aload 0
		// 52d: aload 8
		// 52f: getfield net/minecraft/util/math/Vec3d.x D
		// 532: fload 7
		// 534: f2d
		// 535: dmul
		// 536: dload 9
		// 538: ldc2_w 0.9800000190734863
		// 53b: dmul
		// 53c: aload 8
		// 53e: getfield net/minecraft/util/math/Vec3d.z D
		// 541: fload 7
		// 543: f2d
		// 544: dmul
		// 545: invokevirtual net/minecraft/entity/LivingEntity.setVelocity (DDD)V
		// 548: aload 0
		// 549: aload 0
		// 54a: getfield net/minecraft/entity/LivingEntity.limbDistance F
		// 54d: putfield net/minecraft/entity/LivingEntity.lastLimbDistance F
		// 550: aload 0
		// 551: invokevirtual net/minecraft/entity/LivingEntity.getX ()D
		// 554: aload 0
		// 555: getfield net/minecraft/entity/LivingEntity.prevX D
		// 558: dsub
		// 559: dstore 2
		// 55a: aload 0
		// 55b: invokevirtual net/minecraft/entity/LivingEntity.getZ ()D
		// 55e: aload 0
		// 55f: getfield net/minecraft/entity/LivingEntity.prevZ D
		// 562: dsub
		// 563: dstore 4
		// 565: aload 0
		// 566: instanceof net/minecraft/entity/Flutterer
		// 569: ifeq 578
		// 56c: aload 0
		// 56d: invokevirtual net/minecraft/entity/LivingEntity.getY ()D
		// 570: aload 0
		// 571: getfield net/minecraft/entity/LivingEntity.prevY D
		// 574: dsub
		// 575: goto 579
		// 578: dconst_0
		// 579: dstore 6
		// 57b: dload 2
		// 57c: dload 2
		// 57d: dmul
		// 57e: dload 6
		// 580: dload 6
		// 582: dmul
		// 583: dadd
		// 584: dload 4
		// 586: dload 4
		// 588: dmul
		// 589: dadd
		// 58a: invokestatic net/minecraft/util/math/MathHelper.sqrt (D)F
		// 58d: ldc_w 4.0
		// 590: fmul
		// 591: fstore 8
		// 593: fload 8
		// 595: fconst_1
		// 596: fcmpl
		// 597: ifle 59d
		// 59a: fconst_1
		// 59b: fstore 8
		// 59d: aload 0
		// 59e: dup
		// 59f: getfield net/minecraft/entity/LivingEntity.limbDistance F
		// 5a2: fload 8
		// 5a4: aload 0
		// 5a5: getfield net/minecraft/entity/LivingEntity.limbDistance F
		// 5a8: fsub
		// 5a9: ldc_w 0.4
		// 5ac: fmul
		// 5ad: fadd
		// 5ae: putfield net/minecraft/entity/LivingEntity.limbDistance F
		// 5b1: aload 0
		// 5b2: dup
		// 5b3: getfield net/minecraft/entity/LivingEntity.limbAngle F
		// 5b6: aload 0
		// 5b7: getfield net/minecraft/entity/LivingEntity.limbDistance F
		// 5ba: fadd
		// 5bb: putfield net/minecraft/entity/LivingEntity.limbAngle F
		// 5be: return
	}

	private Vec3d applyClimbingSpeed(Vec3d vec3d) {
		if (this.isClimbing()) {
			this.fallDistance = 0.0F;
			float f = 0.15F;
			double d = MathHelper.clamp(vec3d.x, -0.15F, 0.15F);
			double e = MathHelper.clamp(vec3d.z, -0.15F, 0.15F);
			double g = Math.max(vec3d.y, -0.15F);
			if (g < 0.0 && this.getBlockState().getBlock() != Blocks.field_16492 && this.isHoldingOntoLadder() && this instanceof PlayerEntity) {
				g = 0.0;
			}

			vec3d = new Vec3d(d, g, e);
		}

		return vec3d;
	}

	private float getMovementSpeed(float f) {
		return this.onGround ? this.getMovementSpeed() * (0.21600002F / (f * f * f)) : this.flyingSpeed;
	}

	public float getMovementSpeed() {
		return this.movementSpeed;
	}

	public void setMovementSpeed(float f) {
		this.movementSpeed = f;
	}

	public boolean tryAttack(Entity entity) {
		this.onAttacking(entity);
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
				if (this.field_20347 <= 0) {
					this.field_20347 = 20 * (30 - j);
				}

				this.field_20347--;
				if (this.field_20347 <= 0) {
					this.setStingerCount(j - 1);
				}
			}

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack;
				switch (equipmentSlot.getType()) {
					case field_6177:
						itemStack = this.equippedHand.get(equipmentSlot.getEntitySlotId());
						break;
					case field_6178:
						itemStack = this.equippedArmor.get(equipmentSlot.getEntitySlotId());
						break;
					default:
						continue;
				}

				ItemStack itemStack4 = this.getEquippedStack(equipmentSlot);
				if (!ItemStack.areEqualIgnoreDamage(itemStack4, itemStack)) {
					((ServerWorld)this.world)
						.getChunkManager()
						.sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack4));
					if (!itemStack.isEmpty()) {
						this.getAttributes().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
					}

					if (!itemStack4.isEmpty()) {
						this.getAttributes().replaceAll(itemStack4.getAttributeModifiers(equipmentSlot));
					}

					switch (equipmentSlot.getType()) {
						case field_6177:
							this.equippedHand.set(equipmentSlot.getEntitySlotId(), itemStack4.copy());
							break;
						case field_6178:
							this.equippedArmor.set(equipmentSlot.getEntitySlotId(), itemStack4.copy());
					}
				}
			}

			if (this.age % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (!this.glowing) {
				boolean bl = this.hasStatusEffect(StatusEffects.field_5912);
				if (this.getFlag(6) != bl) {
					this.setFlag(6, bl);
				}
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
			float m = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - l);
			if (95.0F < m && m < 265.0F) {
				g = l - 180.0F;
			} else {
				g = l;
			}
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			k = 0.0F;
		}

		this.stepBobbingAmount = this.stepBobbingAmount + (k - this.stepBobbingAmount) * 0.3F;
		this.world.getProfiler().push("headTurn");
		h = this.turnHead(g, h);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("rangeChecks");

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		while (this.bodyYaw - this.prevBodyYaw < -180.0F) {
			this.prevBodyYaw -= 360.0F;
		}

		while (this.bodyYaw - this.prevBodyYaw >= 180.0F) {
			this.prevBodyYaw += 360.0F;
		}

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
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
			this.pitch = 0.0F;
		}
	}

	protected float turnHead(float f, float g) {
		float h = MathHelper.wrapDegrees(f - this.bodyYaw);
		this.bodyYaw += h * 0.3F;
		float i = MathHelper.wrapDegrees(this.yaw - this.bodyYaw);
		boolean bl = i < -90.0F || i >= 90.0F;
		if (i < -75.0F) {
			i = -75.0F;
		}

		if (i >= 75.0F) {
			i = 75.0F;
		}

		this.bodyYaw = this.yaw - i;
		if (i * i > 2500.0F) {
			this.bodyYaw += i * 0.2F;
		}

		if (bl) {
			g *= -1.0F;
		}

		return g;
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
			double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.bodyTrackingIncrements);
			this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
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
		if (this.jumping) {
			if (!(this.waterHeight > 0.0) || this.onGround && !(this.waterHeight > 0.4)) {
				if (this.isInLava()) {
					this.swimUpward(FluidTags.field_15518);
				} else if ((this.onGround || this.waterHeight > 0.0 && this.waterHeight <= 0.4) && this.jumpingCooldown == 0) {
					this.jump();
					this.jumpingCooldown = 10;
				}
			} else {
				this.swimUpward(FluidTags.field_15517);
			}
		} else {
			this.jumpingCooldown = 0;
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.forwardSpeed *= 0.98F;
		this.initAi();
		Box box = this.getBoundingBox();
		this.travel(new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed));
		this.world.getProfiler().pop();
		this.world.getProfiler().push("push");
		if (this.pushCooldown > 0) {
			this.pushCooldown--;
			this.push(box, this.getBoundingBox());
		}

		this.tickCramming();
		this.world.getProfiler().pop();
	}

	private void initAi() {
		boolean bl = this.getFlag(7);
		if (bl && !this.onGround && !this.hasVehicle()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6174);
			if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
				bl = true;
				if (!this.world.isClient && (this.roll + 1) % 20 == 0) {
					itemStack.damage(1, this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.field_6174));
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
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt(GameRules.field_19405);
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

	protected void push(Box box, Box box2) {
		Box box3 = box.union(box2);
		List<Entity> list = this.world.getEntities(this, box3);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof LivingEntity) {
					this.attackLivingEntity((LivingEntity)entity);
					this.pushCooldown = 0;
					this.setVelocity(this.getVelocity().multiply(-0.2));
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.pushCooldown = 0;
		}

		if (!this.world.isClient && this.pushCooldown <= 0) {
			this.setLivingFlag(4, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void attackLivingEntity(LivingEntity livingEntity) {
	}

	public void setPushCooldown(int i) {
		this.pushCooldown = i;
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
	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.serverX = d;
		this.serverY = e;
		this.serverZ = f;
		this.serverYaw = (double)g;
		this.serverPitch = (double)h;
		this.bodyTrackingIncrements = i;
	}

	@Override
	public void updateTrackedHeadRotation(float f, int i) {
		this.serverHeadYaw = (double)f;
		this.headTrackingIncrements = i;
	}

	public void setJumping(boolean bl) {
		this.jumping = bl;
	}

	public void sendPickup(Entity entity, int i) {
		if (!entity.removed && !this.world.isClient && (entity instanceof ItemEntity || entity instanceof ProjectileEntity || entity instanceof ExperienceOrbEntity)) {
			((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(entity, new ItemPickupAnimationS2CPacket(entity.getEntityId(), this.getEntityId(), i));
		}
	}

	public boolean canSee(Entity entity) {
		Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
		Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
		return this.world
				.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.field_1348, this))
				.getType()
			== HitResult.Type.field_1333;
	}

	@Override
	public float getYaw(float f) {
		return f == 1.0F ? this.headYaw : MathHelper.lerp(f, this.prevHeadYaw, this.headYaw);
	}

	public float getHandSwingProgress(float f) {
		float g = this.handSwingProgress - this.lastHandSwingProgress;
		if (g < 0.0F) {
			g++;
		}

		return this.lastHandSwingProgress + g * f;
	}

	public boolean canMoveVoluntarily() {
		return !this.world.isClient;
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && !this.isClimbing();
	}

	@Override
	protected void scheduleVelocityUpdate() {
		this.velocityModified = this.random.nextDouble() >= this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue();
	}

	@Override
	public float getHeadYaw() {
		return this.headYaw;
	}

	@Override
	public void setHeadYaw(float f) {
		this.headYaw = f;
	}

	@Override
	public void setYaw(float f) {
		this.bodyYaw = f;
	}

	public float getAbsorptionAmount() {
		return this.absorptionAmount;
	}

	public void setAbsorptionAmount(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.absorptionAmount = f;
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
		return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.field_5810 : Hand.field_5808;
	}

	private void tickActiveItemStack() {
		if (this.isUsingItem()) {
			if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
				this.activeItemStack.usageTick(this.world, this, this.getItemUseTimeLeft());
				if (this.shouldSpawnConsumptionEffects()) {
					this.spawnConsumptionEffects(this.activeItemStack, 5);
				}

				if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
					this.consumeItem();
				}
			} else {
				this.clearActiveItem();
			}
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

	protected void setLivingFlag(int i, boolean bl) {
		int j = this.dataTracker.get(LIVING_FLAGS);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(LIVING_FLAGS, (byte)j);
	}

	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			this.activeItemStack = itemStack;
			this.itemUseTimeLeft = itemStack.getMaxUseTime();
			if (!this.world.isClient) {
				this.setLivingFlag(1, true);
				this.setLivingFlag(2, hand == Hand.field_5810);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (SLEEPING_POSITION.equals(trackedData)) {
			if (this.world.isClient) {
				this.getSleepingPosition().ifPresent(this::setPositionInBed);
			}
		} else if (LIVING_FLAGS.equals(trackedData) && this.world.isClient) {
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
	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.lookAt(entityAnchor, vec3d);
		this.prevHeadYaw = this.headYaw;
		this.bodyYaw = this.headYaw;
		this.prevBodyYaw = this.bodyYaw;
	}

	protected void spawnConsumptionEffects(ItemStack itemStack, int i) {
		if (!itemStack.isEmpty() && this.isUsingItem()) {
			if (itemStack.getUseAction() == UseAction.field_8946) {
				this.playSound(this.getDrinkSound(itemStack), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (itemStack.getUseAction() == UseAction.field_8950) {
				this.spawnItemParticles(itemStack, i);
				this.playSound(this.getEatSound(itemStack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	private void spawnItemParticles(ItemStack itemStack, int i) {
		for (int j = 0; j < i; j++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.getX(), this.getEyeY(), this.getZ());
			this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.field_11218, itemStack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void consumeItem() {
		if (!this.activeItemStack.equals(this.getStackInHand(this.getActiveHand()))) {
			this.stopUsingItem();
		} else {
			if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
				this.spawnConsumptionEffects(this.activeItemStack, 16);
				this.setStackInHand(this.getActiveHand(), this.activeItemStack.finishUsing(this.world, this));
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
			return item.getUseAction(this.activeItemStack) != UseAction.field_8949 ? false : item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
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
		return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.field_18077;
	}

	public int getRoll() {
		return this.roll;
	}

	public boolean teleport(double d, double e, double f, boolean bl) {
		double g = this.getX();
		double h = this.getY();
		double i = this.getZ();
		double j = e;
		boolean bl2 = false;
		BlockPos blockPos = new BlockPos(d, e, f);
		World world = this.world;
		if (world.isChunkLoaded(blockPos)) {
			boolean bl3 = false;

			while (!bl3 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getMaterial().blocksMovement()) {
					bl3 = true;
				} else {
					j--;
					blockPos = blockPos2;
				}
			}

			if (bl3) {
				this.requestTeleport(d, j, f);
				if (world.doesNotCollide(this) && !world.containsFluid(this.getBoundingBox())) {
					bl2 = true;
				}
			}
		}

		if (!bl2) {
			this.requestTeleport(g, h, i);
			return false;
		} else {
			if (bl) {
				world.sendEntityStatus(this, (byte)46);
			}

			if (this instanceof MobEntityWithAi) {
				((MobEntityWithAi)this).getNavigation().stop();
			}

			return true;
		}
	}

	public boolean isAffectedBySplashPotions() {
		return true;
	}

	public boolean method_6102() {
		return true;
	}

	public void setNearbySongPlaying(BlockPos blockPos, boolean bl) {
	}

	public boolean canPickUp(ItemStack itemStack) {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new MobSpawnS2CPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose entityPose) {
		return entityPose == EntityPose.field_18078 ? SLEEPING_DIMENSIONS : super.getDimensions(entityPose).scaled(this.getScaleFactor());
	}

	public Optional<BlockPos> getSleepingPosition() {
		return this.dataTracker.get(SLEEPING_POSITION);
	}

	public void setSleepingPosition(BlockPos blockPos) {
		this.dataTracker.set(SLEEPING_POSITION, Optional.of(blockPos));
	}

	public void clearSleepingPosition() {
		this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
	}

	public boolean isSleeping() {
		return this.getSleepingPosition().isPresent();
	}

	public void sleep(BlockPos blockPos) {
		if (this.hasVehicle()) {
			this.stopRiding();
		}

		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof BedBlock) {
			this.world.setBlockState(blockPos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(true)), 3);
		}

		this.setPose(EntityPose.field_18078);
		this.setPositionInBed(blockPos);
		this.setSleepingPosition(blockPos);
		this.setVelocity(Vec3d.ZERO);
		this.velocityDirty = true;
	}

	private void setPositionInBed(BlockPos blockPos) {
		this.updatePosition((double)blockPos.getX() + 0.5, (double)((float)blockPos.getY() + 0.6875F), (double)blockPos.getZ() + 0.5);
	}

	private boolean isSleepingInBed() {
		return (Boolean)this.getSleepingPosition().map(blockPos -> this.world.getBlockState(blockPos).getBlock() instanceof BedBlock).orElse(false);
	}

	public void wakeUp() {
		this.getSleepingPosition().filter(this.world::isChunkLoaded).ifPresent(blockPos -> {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getBlock() instanceof BedBlock) {
				this.world.setBlockState(blockPos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), 3);
				Vec3d vec3d = (Vec3d)BedBlock.findWakeUpPosition(this.getType(), this.world, blockPos, 0).orElseGet(() -> {
					BlockPos blockPos2 = blockPos.up();
					return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
				});
				this.updatePosition(vec3d.x, vec3d.y, vec3d.z);
			}
		});
		this.setPose(EntityPose.field_18076);
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
	protected final float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityPose == EntityPose.field_18078 ? 0.2F : this.getActiveEyeHeight(entityPose, entityDimensions);
	}

	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return super.getEyeHeight(entityPose, entityDimensions);
	}

	public ItemStack getArrowType(ItemStack itemStack) {
		return ItemStack.EMPTY;
	}

	public ItemStack eatFood(World world, ItemStack itemStack) {
		if (itemStack.isFood()) {
			world.playSound(
				null,
				this.getX(),
				this.getY(),
				this.getZ(),
				this.getEatSound(itemStack),
				SoundCategory.field_15254,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
			this.applyFoodEffects(itemStack, world, this);
			if (!(this instanceof PlayerEntity) || !((PlayerEntity)this).abilities.creativeMode) {
				itemStack.decrement(1);
			}
		}

		return itemStack;
	}

	private void applyFoodEffects(ItemStack itemStack, World world, LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		if (item.isFood()) {
			for (Pair<StatusEffectInstance, Float> pair : item.getFoodComponent().getStatusEffects()) {
				if (!world.isClient && pair.getLeft() != null && world.random.nextFloat() < (Float)pair.getRight()) {
					livingEntity.addStatusEffect(new StatusEffectInstance((StatusEffectInstance)pair.getLeft()));
				}
			}
		}
	}

	private static byte getEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot) {
			case field_6173:
				return 47;
			case field_6171:
				return 48;
			case field_6169:
				return 49;
			case field_6174:
				return 50;
			case field_6166:
				return 52;
			case field_6172:
				return 51;
			default:
				return 47;
		}
	}

	public void sendEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
		this.world.sendEntityStatus(this, getEquipmentBreakStatus(equipmentSlot));
	}

	public void sendToolBreakStatus(Hand hand) {
		this.sendEquipmentBreakStatus(hand == Hand.field_5808 ? EquipmentSlot.field_6173 : EquipmentSlot.field_6171);
	}
}
