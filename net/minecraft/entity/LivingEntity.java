package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3384;
import net.minecraft.class_3459;
import net.minecraft.class_3462;
import net.minecraft.class_4048;
import net.minecraft.class_4079;
import net.minecraft.class_4337;
import net.minecraft.class_4339;
import net.minecraft.class_4342;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DamageUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntity extends Entity {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final AttributeModifier SPRINTING_SPEED_MODIFIER = new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, 2)
		.setSerialized(false);
	protected static final TrackedData<Byte> field_14543 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> field_14550 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_14551 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_14552 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> STUCK_ARROWS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private AbstractEntityAttributeContainer attributeContainer;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<StatusEffect, StatusEffectInstance> statusEffects = Maps.newHashMap();
	private final DefaultedList<ItemStack> field_15465 = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> field_15466 = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public boolean handSwinging;
	public Hand mainHand;
	public int handSwingTicks;
	public int field_6771;
	public int hurtTime;
	public int maxHurtTime;
	public float knockbackVelocity;
	public int deathTime;
	public float lastHandSwingProgress;
	public float handSwingProgress;
	protected int field_14542;
	public float field_6748;
	public float field_6749;
	public float field_6750;
	public int defaultMaxHealth = 20;
	public float field_6752;
	public float field_6753;
	public float randomLargeSeed;
	public float randomSmallSeed;
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
	protected float distanceTraveled;
	protected float prevDistanceTraveled;
	protected float field_6776;
	protected int field_6777;
	protected float field_6778;
	protected boolean jumping;
	public float sidewaysSpeed;
	public float forwardSpeed;
	public float field_16513;
	public float field_6782;
	protected int bodyTrackingIncrements;
	protected double serverPitch;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverX;
	protected double field_16815;
	protected int field_16816;
	private boolean effectsChanged = true;
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int jumpingCooldown;
	private float absorptionAmount;
	protected ItemStack field_14546 = ItemStack.EMPTY;
	protected int field_14547;
	protected int field_14548;
	private BlockPos field_14545;
	private DamageSource field_15029;
	private long field_15030;
	protected int field_16817;
	private float field_16813;
	private float field_16814;

	protected LivingEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.initializeAttributes();
		this.setHealth(this.getMaxHealth());
		this.inanimate = true;
		this.randomSmallSeed = (float)((Math.random() + 1.0) * 0.01F);
		this.updatePosition(this.x, this.y, this.z);
		this.randomLargeSeed = (float)Math.random() * 12398.0F;
		this.yaw = (float)(Math.random() * (float) (Math.PI * 2));
		this.headYaw = this.yaw;
		this.stepHeight = 0.6F;
	}

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(field_14543, (byte)0);
		this.dataTracker.startTracking(field_14551, 0);
		this.dataTracker.startTracking(field_14552, false);
		this.dataTracker.startTracking(STUCK_ARROWS, 0);
		this.dataTracker.startTracking(field_14550, 1.0F);
	}

	protected void initializeAttributes() {
		this.getAttributeContainer().register(EntityAttributes.GENERIC_MAX_HEALTH);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ARMOR);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
		if (!this.isTouchingWater()) {
			this.updateWaterState();
		}

		if (!this.world.isClient && this.fallDistance > 3.0F && onGround) {
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (!landedState.isAir()) {
				double d = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * d);
				((ServerWorld)this.world).method_21261(new class_4337(class_4342.BLOCK, landedState), this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.fall(heightDifference, onGround, landedState, landedPos);
	}

	public boolean method_2607() {
		return this.method_2647() == class_3462.field_16819;
	}

	public float method_15642(float f) {
		return this.method_15643(this.field_16814, this.field_16813, f);
	}

	protected float method_15643(float f, float g, float h) {
		return f + (g - f) * h;
	}

	@Override
	public void baseTick() {
		this.lastHandSwingProgress = this.handSwingProgress;
		super.baseTick();
		this.world.profiler.push("livingEntityBaseTick");
		boolean bl = this instanceof PlayerEntity;
		if (this.isAlive()) {
			if (this.isInsideWall()) {
				this.damage(DamageSource.IN_WALL, 1.0F);
			} else if (bl && !this.world.method_8524().contains(this.getBoundingBox())) {
				double d = this.world.method_8524().getDistanceInsideBorder(this) + this.world.method_8524().getSafeZone();
				if (d < 0.0) {
					double e = this.world.method_8524().getBorderDamagePerBlock();
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
			if (this.method_15567(FluidTags.WATER)
				&& this.world.getBlockState(new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z)).getBlock() != Blocks.BUBBLE_COLUMN) {
				if (!this.method_2607() && !class_3459.method_15556(this) && !bl2) {
					this.setAir(this.getNextAirUnderwater(this.getAir()));
					if (this.getAir() == -20) {
						this.setAir(0);

						for (int i = 0; i < 8; i++) {
							float f = this.random.nextFloat() - this.random.nextFloat();
							float g = this.random.nextFloat() - this.random.nextFloat();
							float h = this.random.nextFloat() - this.random.nextFloat();
							this.world
								.method_16343(class_4342.field_21379, this.x + (double)f, this.y + (double)g, this.z + (double)h, this.velocityX, this.velocityY, this.velocityZ);
						}

						this.damage(DamageSource.DROWN, 2.0F);
					}
				}

				if (!this.world.isClient && this.hasMount() && this.getVehicle() != null && !this.getVehicle().method_15570()) {
					this.stopRiding();
				}
			} else if (this.getAir() < this.method_15585()) {
				this.setAir(this.method_15648(this.getAir()));
			}

			if (!this.world.isClient) {
				BlockPos blockPos = new BlockPos(this);
				if (!Objects.equal(this.field_14545, blockPos)) {
					this.field_14545 = blockPos;
					this.method_13046(blockPos);
				}
			}
		}

		if (this.isAlive() && this.method_15574()) {
			this.extinguish();
		}

		this.field_6752 = this.field_6753;
		if (this.hurtTime > 0) {
			this.hurtTime--;
		}

		if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
			this.timeUntilRegen--;
		}

		if (this.getHealth() <= 0.0F) {
			this.dropXp();
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
			} else if (this.ticksAlive - this.lastAttackedTime > 100) {
				this.setAttacker(null);
			}
		}

		this.tickStatusEffects();
		this.prevDistanceTraveled = this.distanceTraveled;
		this.prevBodyYaw = this.bodyYaw;
		this.prevHeadYaw = this.headYaw;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.world.profiler.pop();
	}

	protected void method_13046(BlockPos blockPos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
		if (i > 0) {
			FrostWalkerEnchantment.method_11464(this, this.world, blockPos, i);
		}
	}

	public boolean isBaby() {
		return false;
	}

	@Override
	public boolean method_15570() {
		return false;
	}

	protected void dropXp() {
		this.deathTime++;
		if (this.deathTime == 20) {
			if (!this.world.isClient
				&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.world.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getXpToDrop(this.attackingPlayer);

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.world.method_3686(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
				}
			}

			this.remove();

			for (int k = 0; k < 20; k++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.method_16343(
						class_4342.field_21360,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						this.y + (double)(this.random.nextFloat() * this.height),
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						d,
						e,
						f
					);
			}
		}
	}

	protected boolean shouldDropXp() {
		return !this.isBaby();
	}

	protected int getNextAirUnderwater(int air) {
		int i = EnchantmentHelper.getRespiration(this);
		return i > 0 && this.random.nextInt(i + 1) > 0 ? air : air - 1;
	}

	protected int method_15648(int i) {
		return Math.min(i + 4, this.method_15585());
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

	public int getLastHurtTimestamp() {
		return this.lastAttackedTime;
	}

	public void setAttacker(@Nullable LivingEntity entity) {
		this.attacker = entity;
		this.lastAttackedTime = this.ticksAlive;
	}

	public LivingEntity getAttacking() {
		return this.attacking;
	}

	public int getLastAttackTime() {
		return this.lastAttackTime;
	}

	public void method_6150(Entity entity) {
		if (entity instanceof LivingEntity) {
			this.attacking = (LivingEntity)entity;
		} else {
			this.attacking = null;
		}

		this.lastAttackTime = this.ticksAlive;
	}

	public int method_6117() {
		return this.despawnCounter;
	}

	protected void method_13045(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			Sound sound = Sounds.ITEM_ARMOR_EQUIP_GENERIC;
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem) {
				sound = ((ArmorItem)item).method_4602().method_16000();
			} else if (item == Items.ELYTRA) {
				sound = Sounds.ITEM_ARMOR_EQUIP_ELYTRA;
			}

			this.playSound(sound, 1.0F, 1.0F);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("Health", this.getHealth());
		nbt.putShort("HurtTime", (short)this.hurtTime);
		nbt.putInt("HurtByTimestamp", this.lastAttackedTime);
		nbt.putShort("DeathTime", (short)this.deathTime);
		nbt.putFloat("AbsorptionAmount", this.getAbsorption());

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				this.getAttributeContainer().removeAll(itemStack.getAttributes(equipmentSlot));
			}
		}

		nbt.put("Attributes", EntityAttributes.toNbt(this.getAttributeContainer()));

		for (EquipmentSlot equipmentSlot2 : EquipmentSlot.values()) {
			ItemStack itemStack2 = this.getStack(equipmentSlot2);
			if (!itemStack2.isEmpty()) {
				this.getAttributeContainer().replaceAll(itemStack2.getAttributes(equipmentSlot2));
			}
		}

		if (!this.statusEffects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.statusEffects.values()) {
				nbtList.add((NbtElement)statusEffectInstance.toNbt(new NbtCompound()));
			}

			nbt.put("ActiveEffects", nbtList);
		}

		nbt.putBoolean("FallFlying", this.method_13055());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setAbsorption(nbt.getFloat("AbsorptionAmount"));
		if (nbt.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
			EntityAttributes.fromNbt(this.getAttributeContainer(), nbt.getList("Attributes", 10));
		}

		if (nbt.contains("ActiveEffects", 9)) {
			NbtList nbtList = nbt.getList("ActiveEffects", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					this.statusEffects.put(statusEffectInstance.getStatusEffect(), statusEffectInstance);
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
			boolean bl = team != null && this.world.getScoreboard().method_6614(this.getEntityName(), team);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (nbt.getBoolean("FallFlying")) {
			this.setFlag(7, true);
		}
	}

	protected void tickStatusEffects() {
		Iterator<StatusEffect> iterator = this.statusEffects.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				StatusEffect statusEffect = (StatusEffect)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.statusEffects.get(statusEffect);
				if (!statusEffectInstance.method_6093(this)) {
					if (!this.world.isClient) {
						iterator.remove();
						this.method_2649(statusEffectInstance);
					}
				} else if (statusEffectInstance.getDuration() % 600 == 0) {
					this.method_6108(statusEffectInstance, false);
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

		int i = this.dataTracker.get(field_14551);
		boolean bl = this.dataTracker.get(field_14552);
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
					.method_16343(
						bl ? class_4342.field_21375 : class_4342.field_21393,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						d,
						e,
						f
					);
			}
		}
	}

	protected void updatePotionVisibility() {
		if (this.statusEffects.isEmpty()) {
			this.method_10981();
			this.setInvisible(false);
		} else {
			Collection<StatusEffectInstance> collection = this.statusEffects.values();
			this.dataTracker.set(field_14552, method_13040(collection));
			this.dataTracker.set(field_14551, PotionUtil.getColor(collection));
			this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
		}
	}

	public static boolean method_13040(Collection<StatusEffectInstance> collection) {
		for (StatusEffectInstance statusEffectInstance : collection) {
			if (!statusEffectInstance.isAmbient()) {
				return false;
			}
		}

		return true;
	}

	protected void method_10981() {
		this.dataTracker.set(field_14552, false);
		this.dataTracker.set(field_14551, 0);
	}

	public boolean method_6119() {
		if (this.world.isClient) {
			return false;
		} else {
			Iterator<StatusEffectInstance> iterator = this.statusEffects.values().iterator();

			boolean bl;
			for (bl = false; iterator.hasNext(); bl = true) {
				this.method_2649((StatusEffectInstance)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<StatusEffectInstance> getStatusEffectInstances() {
		return this.statusEffects.values();
	}

	public Map<StatusEffect, StatusEffectInstance> getStatusEffects() {
		return this.statusEffects;
	}

	public boolean hasStatusEffect(StatusEffect effect) {
		return this.statusEffects.containsKey(effect);
	}

	@Nullable
	public StatusEffectInstance getEffectInstance(StatusEffect effect) {
		return (StatusEffectInstance)this.statusEffects.get(effect);
	}

	public boolean method_2654(StatusEffectInstance statusEffectInstance) {
		if (!this.method_2658(statusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)this.statusEffects.get(statusEffectInstance.getStatusEffect());
			if (statusEffectInstance2 == null) {
				this.statusEffects.put(statusEffectInstance.getStatusEffect(), statusEffectInstance);
				this.method_2582(statusEffectInstance);
				return true;
			} else if (statusEffectInstance2.method_15551(statusEffectInstance)) {
				this.method_6108(statusEffectInstance2, true);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean method_2658(StatusEffectInstance instance) {
		if (this.method_2647() == class_3462.field_16819) {
			StatusEffect statusEffect = instance.getStatusEffect();
			if (statusEffect == StatusEffects.REGENERATION || statusEffect == StatusEffects.POISON) {
				return false;
			}
		}

		return true;
	}

	public boolean isAffectedBySmite() {
		return this.method_2647() == class_3462.field_16819;
	}

	@Nullable
	public StatusEffectInstance method_13052(@Nullable StatusEffect effect) {
		return (StatusEffectInstance)this.statusEffects.remove(effect);
	}

	public boolean method_13069(StatusEffect statusEffect) {
		StatusEffectInstance statusEffectInstance = this.method_13052(statusEffect);
		if (statusEffectInstance != null) {
			this.method_2649(statusEffectInstance);
			return true;
		} else {
			return false;
		}
	}

	protected void method_2582(StatusEffectInstance instance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			instance.getStatusEffect().method_6091(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	protected void method_6108(StatusEffectInstance instance, boolean bl) {
		this.effectsChanged = true;
		if (bl && !this.world.isClient) {
			StatusEffect statusEffect = instance.getStatusEffect();
			statusEffect.onRemoved(this, this.getAttributeContainer(), instance.getAmplifier());
			statusEffect.method_6091(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	protected void method_2649(StatusEffectInstance instance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			instance.getStatusEffect().onRemoved(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	public void heal(float f) {
		float g = this.getHealth();
		if (g > 0.0F) {
			this.setHealth(g + f);
		}
	}

	public float getHealth() {
		return this.dataTracker.get(field_14550);
	}

	public void setHealth(float f) {
		this.dataTracker.set(field_14550, MathHelper.clamp(f, 0.0F, this.getMaxHealth()));
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.world.isClient) {
			return false;
		} else if (this.getHealth() <= 0.0F) {
			return false;
		} else if (source.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
			return false;
		} else {
			this.despawnCounter = 0;
			float f = amount;
			if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getStack(EquipmentSlot.HEAD).isEmpty()) {
				this.getStack(EquipmentSlot.HEAD).damage((int)(amount * 4.0F + this.random.nextFloat() * amount * 2.0F), this);
				amount *= 0.75F;
			}

			boolean bl = false;
			float g = 0.0F;
			if (amount > 0.0F && this.method_13068(source)) {
				this.method_13072(amount);
				g = amount;
				amount = 0.0F;
				if (!source.isProjectile()) {
					Entity entity = source.getSource();
					if (entity instanceof LivingEntity) {
						this.method_13947((LivingEntity)entity);
					}
				}

				bl = true;
			}

			this.field_6749 = 1.5F;
			boolean bl2 = true;
			if ((float)this.timeUntilRegen > (float)this.defaultMaxHealth / 2.0F) {
				if (amount <= this.field_6778) {
					return false;
				}

				this.applyDamage(source, amount - this.field_6778);
				this.field_6778 = amount;
				bl2 = false;
			} else {
				this.field_6778 = amount;
				this.timeUntilRegen = this.defaultMaxHealth;
				this.applyDamage(source, amount);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}

			this.knockbackVelocity = 0.0F;
			Entity entity2 = source.getAttacker();
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
					} else {
						b = 2;
					}

					this.world.sendEntityStatus(this, b);
				}

				if (source != DamageSource.DROWN && (!bl || amount > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity2 != null) {
					double e = entity2.x - this.x;

					double h;
					for (h = entity2.z - this.z; e * e + h * h < 1.0E-4; h = (Math.random() - Math.random()) * 0.01) {
						e = (Math.random() - Math.random()) * 0.01;
					}

					this.knockbackVelocity = (float)(MathHelper.atan2(h, e) * 180.0F / (float)Math.PI - (double)this.yaw);
					this.method_6109(entity2, 0.4F, e, h);
				} else {
					this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.getHealth() <= 0.0F) {
				if (!this.method_13949(source)) {
					Sound sound = this.deathSound();
					if (bl2 && sound != null) {
						this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onKilled(source);
				}
			} else if (bl2) {
				this.method_13051(source);
			}

			boolean bl3 = !bl || amount > 0.0F;
			if (bl3) {
				this.field_15029 = source;
				this.field_15030 = this.world.getLastUpdateTime();
			}

			if (this instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16336.method_14224((ServerPlayerEntity)this, source, f, amount, bl);
				if (g > 0.0F && g < 3.4028235E37F) {
					((ServerPlayerEntity)this).method_15929(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
				}
			}

			if (entity2 instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16335.method_14379((ServerPlayerEntity)entity2, this, source, f, amount, bl);
			}

			return bl3;
		}
	}

	protected void method_13947(LivingEntity livingEntity) {
		livingEntity.method_6109(this, 0.5F, this.x - livingEntity.x, this.z - livingEntity.z);
	}

	private boolean method_13949(DamageSource damageSource) {
		if (damageSource.isOutOfWorld()) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.getStackInHand(hand);
				if (itemStack2.getItem() == Items.TOTEM_OF_UNDYING) {
					itemStack = itemStack2.copy();
					itemStack2.decrement(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
					serverPlayerEntity.method_15932(Stats.USED.method_21429(Items.TOTEM_OF_UNDYING));
					AchievementsAndCriterions.field_16326.method_14436(serverPlayerEntity, itemStack);
				}

				this.setHealth(1.0F);
				this.method_6119();
				this.method_2654(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
				this.method_2654(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
				this.world.sendEntityStatus(this, (byte)35);
			}

			return itemStack != null;
		}
	}

	@Nullable
	public DamageSource method_13493() {
		if (this.world.getLastUpdateTime() - this.field_15030 > 40L) {
			this.field_15029 = null;
		}

		return this.field_15029;
	}

	protected void method_13051(DamageSource damageSource) {
		Sound sound = this.getHurtSound(damageSource);
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private boolean method_13068(DamageSource damageSource) {
		if (!damageSource.bypassesArmor() && this.method_13054()) {
			Vec3d vec3d = damageSource.getPosition();
			if (vec3d != null) {
				Vec3d vec3d2 = this.getRotationVector(1.0F);
				Vec3d vec3d3 = vec3d.reverseSubtract(new Vec3d(this.x, this.y, this.z)).normalize();
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
				if (vec3d3.dotProduct(vec3d2) < 0.0) {
					return true;
				}
			}
		}

		return false;
	}

	public void method_6111(ItemStack stack) {
		super.playSound(Sounds.ENTITY_ITEM_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
		this.method_15640(stack, 5);
	}

	public void onKilled(DamageSource source) {
		if (!this.dead) {
			Entity entity = source.getAttacker();
			LivingEntity livingEntity = this.getOpponent();
			if (this.field_6777 >= 0 && livingEntity != null) {
				livingEntity.updateKilledAchievement(this, this.field_6777, source);
			}

			if (entity != null) {
				entity.onKilledOther(this);
			}

			this.dead = true;
			this.getDamageTracker().update();
			if (!this.world.isClient) {
				int i = 0;
				if (entity instanceof PlayerEntity) {
					i = EnchantmentHelper.getLooting((LivingEntity)entity);
				}

				if (this.shouldDropXp() && this.world.getGameRules().getBoolean("doMobLoot")) {
					boolean bl = this.playerHitTimer > 0;
					this.method_13044(bl, i, source);
				}
			}

			this.world.sendEntityStatus(this, (byte)3);
		}
	}

	protected void method_13044(boolean bl, int i, DamageSource damageSource) {
		this.dropLoot(bl, i);
		this.method_4472(bl, i);
	}

	protected void method_4472(boolean bl, int i) {
	}

	public void method_6109(Entity entity, float f, double d, double e) {
		if (!(this.random.nextDouble() < this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).getValue())) {
			this.velocityDirty = true;
			float g = MathHelper.sqrt(d * d + e * e);
			this.velocityX /= 2.0;
			this.velocityZ /= 2.0;
			this.velocityX -= d / (double)g * (double)f;
			this.velocityZ -= e / (double)g * (double)f;
			if (this.onGround) {
				this.velocityY /= 2.0;
				this.velocityY += (double)f;
				if (this.velocityY > 0.4F) {
					this.velocityY = 0.4F;
				}
			}
		}
	}

	@Nullable
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_GENERIC_HURT;
	}

	@Nullable
	protected Sound deathSound() {
		return Sounds.ENTITY_GENERIC_DEATH;
	}

	protected Sound getLandSound(int height) {
		return height > 4 ? Sounds.ENTITY_GENERIC_BIG_FALL : Sounds.ENTITY_GENERIC_SMALL_FALL;
	}

	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
	}

	public boolean isClimbing() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		if (this instanceof PlayerEntity && ((PlayerEntity)this).isSpectator()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			return block != Blocks.LADDER && block != Blocks.VINE ? block instanceof TrapdoorBlock && this.method_13039(blockPos, blockState) : true;
		}
	}

	private boolean method_13039(BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(TrapdoorBlock.field_18531)) {
			BlockState blockState = this.world.getBlockState(pos.down());
			if (blockState.getBlock() == Blocks.LADDER && blockState.getProperty(LadderBlock.FACING) == state.getProperty(TrapdoorBlock.FACING)) {
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
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		super.handleFallDamage(fallDistance, damageMultiplier);
		StatusEffectInstance statusEffectInstance = this.getEffectInstance(StatusEffects.JUMP_BOOST);
		float f = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
		int i = MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
		if (i > 0) {
			this.playSound(this.getLandSound(i), 1.0F, 1.0F);
			this.damage(DamageSource.FALL, (float)i);
			int j = MathHelper.floor(this.x);
			int k = MathHelper.floor(this.y - 0.2F);
			int l = MathHelper.floor(this.z);
			BlockState blockState = this.world.getBlockState(new BlockPos(j, k, l));
			if (!blockState.isAir()) {
				BlockSoundGroup blockSoundGroup = blockState.getBlock().getSoundGroup();
				this.playSound(blockSoundGroup.method_487(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
			}
		}
	}

	@Override
	public void animateDamage() {
		this.maxHurtTime = 10;
		this.hurtTime = this.maxHurtTime;
		this.knockbackVelocity = 0.0F;
	}

	public int getArmorProtectionValue() {
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_ARMOR);
		return MathHelper.floor(entityAttributeInstance.getValue());
	}

	protected void damageArmor(float value) {
	}

	protected void method_13072(float f) {
	}

	protected float applyArmorDamage(DamageSource source, float damage) {
		if (!source.bypassesArmor()) {
			this.damageArmor(damage);
			damage = DamageUtils.getDamageAfterProtection(
				damage, (float)this.getArmorProtectionValue(), (float)this.initializeAttribute(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue()
			);
		}

		return damage;
	}

	protected float applyEnchantmentsToDamage(DamageSource source, float amount) {
		if (source.isUnblockable()) {
			return amount;
		} else {
			if (this.hasStatusEffect(StatusEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
				int i = (this.getEffectInstance(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = amount * (float)j;
				float g = amount;
				amount = Math.max(f / 25.0F, 0.0F);
				float h = g - amount;
				if (h > 0.0F && h < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).method_15929(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
					} else if (source.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)source.getAttacker()).method_15929(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
					}
				}
			}

			if (amount <= 0.0F) {
				return 0.0F;
			} else {
				int k = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), source);
				if (k > 0) {
					amount = DamageUtils.method_12937(amount, (float)k);
				}

				return amount;
			}
		}
	}

	protected void applyDamage(DamageSource source, float damage) {
		if (!this.isInvulnerableTo(source)) {
			damage = this.applyArmorDamage(source, damage);
			damage = this.applyEnchantmentsToDamage(source, damage);
			float var8 = Math.max(damage - this.getAbsorption(), 0.0F);
			this.setAbsorption(this.getAbsorption() - (damage - var8));
			float g = damage - var8;
			if (g > 0.0F && g < 3.4028235E37F && source.getAttacker() instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)source.getAttacker()).method_15929(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
			}

			if (var8 != 0.0F) {
				float h = this.getHealth();
				this.setHealth(h - var8);
				this.getDamageTracker().onDamage(source, h, var8);
				this.setAbsorption(this.getAbsorption() - var8);
			}
		}
	}

	public DamageTracker getDamageTracker() {
		return this.damageTracker;
	}

	@Nullable
	public LivingEntity getOpponent() {
		if (this.damageTracker.getLastAttacker() != null) {
			return this.damageTracker.getLastAttacker();
		} else if (this.attackingPlayer != null) {
			return this.attackingPlayer;
		} else {
			return this.attacker != null ? this.attacker : null;
		}
	}

	public final float getMaxHealth() {
		return (float)this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).getValue();
	}

	public final int getStuckArrows() {
		return this.dataTracker.get(STUCK_ARROWS);
	}

	public final void setStuckArrows(int arrows) {
		this.dataTracker.set(STUCK_ARROWS, arrows);
	}

	private int getMiningSpeedMultiplier() {
		if (class_3459.method_15554(this)) {
			return 6 - (1 + class_3459.method_15555(this));
		} else {
			return this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getEffectInstance(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		if (!this.handSwinging || this.handSwingTicks >= this.getMiningSpeedMultiplier() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.handSwinging = true;
			this.mainHand = hand;
			if (this.world instanceof ServerWorld) {
				((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAnimationS2CPacket(this, hand == Hand.MAIN_HAND ? 0 : 3));
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		boolean bl = status == 33;
		boolean bl2 = status == 36;
		boolean bl3 = status == 37;
		if (status == 2 || bl || bl2 || bl3) {
			this.field_6749 = 1.5F;
			this.timeUntilRegen = this.defaultMaxHealth;
			this.maxHurtTime = 10;
			this.hurtTime = this.maxHurtTime;
			this.knockbackVelocity = 0.0F;
			if (bl) {
				this.playSound(Sounds.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			DamageSource damageSource;
			if (bl3) {
				damageSource = DamageSource.ON_FIRE;
			} else if (bl2) {
				damageSource = DamageSource.DROWN;
			} else {
				damageSource = DamageSource.GENERIC;
			}

			Sound sound = this.getHurtSound(damageSource);
			if (sound != null) {
				this.playSound(sound, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.damage(DamageSource.GENERIC, 0.0F);
		} else if (status == 3) {
			Sound sound2 = this.deathSound();
			if (sound2 != null) {
				this.playSound(sound2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.setHealth(0.0F);
			this.onKilled(DamageSource.GENERIC);
		} else if (status == 30) {
			this.playSound(Sounds.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
		} else if (status == 29) {
			this.playSound(Sounds.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.random.nextFloat() * 0.4F);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected void destroy() {
		this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void tickHandSwing() {
		int i = this.getMiningSpeedMultiplier();
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

	public EntityAttributeInstance initializeAttribute(EntityAttribute attribute) {
		return this.getAttributeContainer().get(attribute);
	}

	public AbstractEntityAttributeContainer getAttributeContainer() {
		if (this.attributeContainer == null) {
			this.attributeContainer = new EntityAttributeContainer();
		}

		return this.attributeContainer;
	}

	public class_3462 method_2647() {
		return class_3462.field_16818;
	}

	public ItemStack getMainHandStack() {
		return this.getStack(EquipmentSlot.MAINHAND);
	}

	public ItemStack getOffHandStack() {
		return this.getStack(EquipmentSlot.OFFHAND);
	}

	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.getStack(EquipmentSlot.MAINHAND);
		} else if (hand == Hand.OFF_HAND) {
			return this.getStack(EquipmentSlot.OFFHAND);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void equipStack(Hand hand, ItemStack stack) {
		if (hand == Hand.MAIN_HAND) {
			this.equipStack(EquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != Hand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.equipStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	public boolean method_13946(EquipmentSlot slot) {
		return !this.getStack(slot).isEmpty();
	}

	@Override
	public abstract Iterable<ItemStack> getArmorItems();

	public abstract ItemStack getStack(EquipmentSlot slot);

	@Override
	public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

	@Override
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance.getByUuid(SPRINTING_SPEED_BOOST_ID) != null) {
			entityAttributeInstance.method_6193(SPRINTING_SPEED_MODIFIER);
		}

		if (sprinting) {
			entityAttributeInstance.addModifier(SPRINTING_SPEED_MODIFIER);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean method_2610() {
		return this.getHealth() <= 0.0F;
	}

	public void method_6152(Entity entity) {
		if (!(entity instanceof BoatEntity) && !(entity instanceof AbstractHorseEntity)) {
			double l = entity.x;
			double m = entity.getBoundingBox().minY + (double)entity.height;
			double n = entity.z;
			Direction direction = entity.getMovementDirection();
			if (direction != null) {
				Direction direction2 = direction.rotateYClockwise();
				int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
				double o = Math.floor(this.x) + 0.5;
				double p = Math.floor(this.z) + 0.5;
				double q = this.getBoundingBox().maxX - this.getBoundingBox().minX;
				double r = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
				Box box = new Box(
					o - q / 2.0, entity.getBoundingBox().minY, p - r / 2.0, o + q / 2.0, Math.floor(entity.getBoundingBox().minY) + (double)this.height, p + r / 2.0
				);

				for (int[] js : is) {
					double s = (double)(direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1]);
					double t = (double)(direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1]);
					double u = o + s;
					double v = p + t;
					Box box2 = box.offset(s, 0.0, t);
					if (this.world.method_16387(this, box2)) {
						if (this.world.getBlockState(new BlockPos(u, this.y, v)).method_16913()) {
							this.refreshPositionAfterTeleport(u, this.y + 1.0, v);
							return;
						}

						BlockPos blockPos = new BlockPos(u, this.y - 1.0, v);
						if (this.world.getBlockState(blockPos).method_16913() || this.world.getFluidState(blockPos).matches(FluidTags.WATER)) {
							l = u;
							m = this.y + 1.0;
							n = v;
						}
					} else if (this.world.method_16387(this, box2.offset(0.0, 1.0, 0.0)) && this.world.getBlockState(new BlockPos(u, this.y + 1.0, v)).method_16913()) {
						l = u;
						m = this.y + 2.0;
						n = v;
					}
				}
			}

			this.refreshPositionAfterTeleport(l, m, n);
		} else {
			double d = (double)(this.width / 2.0F + entity.width / 2.0F) + 0.4;
			float f;
			if (entity instanceof BoatEntity) {
				f = 0.0F;
			} else {
				f = (float) (Math.PI / 2) * (float)(this.getDurability() == HandOption.RIGHT ? -1 : 1);
			}

			float h = -MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			float i = -MathHelper.cos(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			double e = Math.abs(h) > Math.abs(i) ? d / (double)Math.abs(h) : d / (double)Math.abs(i);
			double j = this.x + (double)h * e;
			double k = this.z + (double)i * e;
			this.updatePosition(j, entity.y + (double)entity.height + 0.001, k);
			if (!this.world.method_16387(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
				this.updatePosition(j, entity.y + (double)entity.height + 1.001, k);
				if (!this.world.method_16387(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
					this.updatePosition(entity.x, entity.y + (double)this.height + 0.001, entity.z);
				}
			}
		}
	}

	@Override
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	protected float getJumpVelocity() {
		return 0.42F;
	}

	protected void jump() {
		this.velocityY = (double)this.getJumpVelocity();
		if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
			this.velocityY = this.velocityY + (double)((float)(this.getEffectInstance(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
		}

		if (this.isSprinting()) {
			float f = this.yaw * (float) (Math.PI / 180.0);
			this.velocityX = this.velocityX - (double)(MathHelper.sin(f) * 0.2F);
			this.velocityZ = this.velocityZ + (double)(MathHelper.cos(f) * 0.2F);
		}

		this.velocityDirty = true;
	}

	protected void method_2634() {
		this.velocityY -= 0.04F;
	}

	protected void method_15645(Tag<Fluid> tag) {
		this.velocityY += 0.04F;
	}

	protected float method_13494() {
		return 0.8F;
	}

	public void method_2657(float f, float g, float h) {
		if (this.canMoveVoluntarily() || this.method_13003()) {
			double d = 0.08;
			if (this.velocityY <= 0.0 && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
				d = 0.01;
				this.fallDistance = 0.0F;
			}

			if (!this.isTouchingWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
				if (!this.isTouchingLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
					if (this.method_13055()) {
						if (this.velocityY > -0.5) {
							this.fallDistance = 1.0F;
						}

						Vec3d vec3d = this.getRotation();
						float m = this.pitch * (float) (Math.PI / 180.0);
						double n = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
						double o = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
						double p = vec3d.length();
						float q = MathHelper.cos(m);
						q = (float)((double)q * (double)q * Math.min(1.0, p / 0.4));
						this.velocityY += d * (-1.0 + (double)q * 0.75);
						if (this.velocityY < 0.0 && n > 0.0) {
							double r = this.velocityY * -0.1 * (double)q;
							this.velocityY += r;
							this.velocityX = this.velocityX + vec3d.x * r / n;
							this.velocityZ = this.velocityZ + vec3d.z * r / n;
						}

						if (m < 0.0F && n > 0.0) {
							double s = o * (double)(-MathHelper.sin(m)) * 0.04;
							this.velocityY += s * 3.2;
							this.velocityX = this.velocityX - vec3d.x * s / n;
							this.velocityZ = this.velocityZ - vec3d.z * s / n;
						}

						if (n > 0.0) {
							this.velocityX = this.velocityX + (vec3d.x / n * o - this.velocityX) * 0.1;
							this.velocityZ = this.velocityZ + (vec3d.z / n * o - this.velocityZ) * 0.1;
						}

						this.velocityX *= 0.99F;
						this.velocityY *= 0.98F;
						this.velocityZ *= 0.99F;
						this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
						if (this.horizontalCollision && !this.world.isClient) {
							double t = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
							double u = o - t;
							float v = (float)(u * 10.0 - 3.0);
							if (v > 0.0F) {
								this.playSound(this.getLandSound((int)v), 1.0F, 1.0F);
								this.damage(DamageSource.FLY_INTO_WALL, v);
							}
						}

						if (this.onGround && !this.world.isClient) {
							this.setFlag(7, false);
						}
					} else {
						float w = 0.91F;

						try (BlockPos.Pooled pooled = BlockPos.Pooled.method_12567(this.x, this.getBoundingBox().minY - 1.0, this.z)) {
							if (this.onGround) {
								w = this.world.getBlockState(pooled).getBlock().getSlipperiness() * 0.91F;
							}

							float x = 0.16277137F / (w * w * w);
							float y;
							if (this.onGround) {
								y = this.getMovementSpeed() * x;
							} else {
								y = this.flyingSpeed;
							}

							this.method_2492(f, g, h, y);
							w = 0.91F;
							if (this.onGround) {
								w = this.world.getBlockState(pooled.set(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getSlipperiness() * 0.91F;
							}

							if (this.isClimbing()) {
								float aa = 0.15F;
								this.velocityX = MathHelper.clamp(this.velocityX, -0.15F, 0.15F);
								this.velocityZ = MathHelper.clamp(this.velocityZ, -0.15F, 0.15F);
								this.fallDistance = 0.0F;
								if (this.velocityY < -0.15) {
									this.velocityY = -0.15;
								}

								boolean bl = this.isSneaking() && this instanceof PlayerEntity;
								if (bl && this.velocityY < 0.0) {
									this.velocityY = 0.0;
								}
							}

							this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
							if (this.horizontalCollision && this.isClimbing()) {
								this.velocityY = 0.2;
							}

							if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
								this.velocityY = this.velocityY + (0.05 * (double)(this.getEffectInstance(StatusEffects.LEVITATION).getAmplifier() + 1) - this.velocityY) * 0.2;
								this.fallDistance = 0.0F;
							} else {
								pooled.set(this.x, 0.0, this.z);
								if (!this.world.isClient || this.world.method_16359(pooled) && this.world.getChunk(pooled).isLoaded()) {
									if (!this.hasNoGravity()) {
										this.velocityY -= d;
									}
								} else if (this.y > 0.0) {
									this.velocityY = -0.1;
								} else {
									this.velocityY = 0.0;
								}
							}

							this.velocityY *= 0.98F;
							this.velocityX *= (double)w;
							this.velocityZ *= (double)w;
						}
					}
				} else {
					double l = this.y;
					this.method_2492(f, g, h, 0.02F);
					this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
					this.velocityX *= 0.5;
					this.velocityY *= 0.5;
					this.velocityZ *= 0.5;
					if (!this.hasNoGravity()) {
						this.velocityY -= d / 4.0;
					}

					if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + l, this.velocityZ)) {
						this.velocityY = 0.3F;
					}
				}
			} else {
				double e = this.y;
				float i = this.isSprinting() ? 0.9F : this.method_13494();
				float j = 0.02F;
				float k = (float)EnchantmentHelper.getDepthStrider(this);
				if (k > 3.0F) {
					k = 3.0F;
				}

				if (!this.onGround) {
					k *= 0.5F;
				}

				if (k > 0.0F) {
					i += (0.54600006F - i) * k / 3.0F;
					j += (this.getMovementSpeed() - j) * k / 3.0F;
				}

				if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
					i = 0.96F;
				}

				this.method_2492(f, g, h, j);
				this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
				this.velocityX *= (double)i;
				this.velocityY *= 0.8F;
				this.velocityZ *= (double)i;
				if (!this.hasNoGravity() && !this.isSprinting()) {
					if (this.velocityY <= 0.0 && Math.abs(this.velocityY - 0.005) >= 0.003 && Math.abs(this.velocityY - d / 16.0) < 0.003) {
						this.velocityY = -0.003;
					} else {
						this.velocityY -= d / 16.0;
					}
				}

				if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + e, this.velocityZ)) {
					this.velocityY = 0.3F;
				}
			}
		}

		this.field_6748 = this.field_6749;
		double ab = this.x - this.prevX;
		double ac = this.z - this.prevZ;
		double ad = this instanceof class_3384 ? this.y - this.prevY : 0.0;
		float ae = MathHelper.sqrt(ab * ab + ad * ad + ac * ac) * 4.0F;
		if (ae > 1.0F) {
			ae = 1.0F;
		}

		this.field_6749 = this.field_6749 + (ae - this.field_6749) * 0.4F;
		this.field_6750 = this.field_6750 + this.field_6749;
	}

	public float getMovementSpeed() {
		return this.movementSpeed;
	}

	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public boolean tryAttack(Entity target) {
		this.method_6150(target);
		return false;
	}

	public boolean isSleeping() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.method_13063();
		this.method_15649();
		if (!this.world.isClient) {
			int i = this.getStuckArrows();
			if (i > 0) {
				if (this.field_6771 <= 0) {
					this.field_6771 = 20 * (30 - i);
				}

				this.field_6771--;
				if (this.field_6771 <= 0) {
					this.setStuckArrows(i - 1);
				}
			}

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack;
				switch (equipmentSlot.getType()) {
					case HAND:
						itemStack = this.field_15465.get(equipmentSlot.method_13032());
						break;
					case ARMOR:
						itemStack = this.field_15466.get(equipmentSlot.method_13032());
						break;
					default:
						continue;
				}

				ItemStack itemStack4 = this.getStack(equipmentSlot);
				if (!ItemStack.equalsAll(itemStack4, itemStack)) {
					((ServerWorld)this.world)
						.getEntityTracker()
						.sendToOtherTrackingEntities(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack4));
					if (!itemStack.isEmpty()) {
						this.getAttributeContainer().removeAll(itemStack.getAttributes(equipmentSlot));
					}

					if (!itemStack4.isEmpty()) {
						this.getAttributeContainer().replaceAll(itemStack4.getAttributes(equipmentSlot));
					}

					switch (equipmentSlot.getType()) {
						case HAND:
							this.field_15465.set(equipmentSlot.method_13032(), itemStack4.isEmpty() ? ItemStack.EMPTY : itemStack4.copy());
							break;
						case ARMOR:
							this.field_15466.set(equipmentSlot.method_13032(), itemStack4.isEmpty() ? ItemStack.EMPTY : itemStack4.copy());
					}
				}
			}

			if (this.ticksAlive % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (!this.isGlowing) {
				boolean bl = this.hasStatusEffect(StatusEffects.GLOWING);
				if (this.getFlag(6) != bl) {
					this.setFlag(6, bl);
				}
			}
		}

		this.tickMovement();
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float f = (float)(d * d + e * e);
		float g = this.bodyYaw;
		float h = 0.0F;
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		float j = 0.0F;
		if (f > 0.0025000002F) {
			j = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			float k = (float)MathHelper.atan2(e, d) * (180.0F / (float)Math.PI) - 90.0F;
			float l = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - k);
			if (95.0F < l && l < 265.0F) {
				g = k - 180.0F;
			} else {
				g = k;
			}
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			j = 0.0F;
		}

		this.stepBobbingAmount = this.stepBobbingAmount + (j - this.stepBobbingAmount) * 0.3F;
		this.world.profiler.push("headTurn");
		h = this.turnHead(g, h);
		this.world.profiler.pop();
		this.world.profiler.push("rangeChecks");

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

		this.world.profiler.pop();
		this.distanceTraveled += h;
		if (this.method_13055()) {
			this.field_14548++;
		} else {
			this.field_14548 = 0;
		}
	}

	protected float turnHead(float bodyRotation, float headRotation) {
		float f = MathHelper.wrapDegrees(bodyRotation - this.bodyYaw);
		this.bodyYaw += f * 0.3F;
		float g = MathHelper.wrapDegrees(this.yaw - this.bodyYaw);
		boolean bl = g < -90.0F || g >= 90.0F;
		if (g < -75.0F) {
			g = -75.0F;
		}

		if (g >= 75.0F) {
			g = 75.0F;
		}

		this.bodyYaw = this.yaw - g;
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

		if (this.bodyTrackingIncrements > 0 && !this.method_13003()) {
			double d = this.x + (this.serverPitch - this.x) / (double)this.bodyTrackingIncrements;
			double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
			double f = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
			double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.bodyTrackingIncrements);
			this.pitch = (float)((double)this.pitch + (this.serverX - (double)this.pitch) / (double)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		} else if (!this.canMoveVoluntarily()) {
			this.velocityX *= 0.98;
			this.velocityY *= 0.98;
			this.velocityZ *= 0.98;
		}

		if (this.field_16816 > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_16815 - (double)this.headYaw) / (double)this.field_16816);
			this.field_16816--;
		}

		if (Math.abs(this.velocityX) < 0.003) {
			this.velocityX = 0.0;
		}

		if (Math.abs(this.velocityY) < 0.003) {
			this.velocityY = 0.0;
		}

		if (Math.abs(this.velocityZ) < 0.003) {
			this.velocityZ = 0.0;
		}

		this.world.profiler.push("ai");
		if (this.method_2610()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.field_16513 = 0.0F;
			this.field_6782 = 0.0F;
		} else if (this.canMoveVoluntarily()) {
			this.world.profiler.push("newAi");
			this.tickNewAi();
			this.world.profiler.pop();
		}

		this.world.profiler.pop();
		this.world.profiler.push("jump");
		if (this.jumping) {
			if (!(this.field_16693 > 0.0) || this.onGround && !(this.field_16693 > 0.4)) {
				if (this.isTouchingLava()) {
					this.method_15645(FluidTags.LAVA);
				} else if ((this.onGround || this.field_16693 > 0.0 && this.field_16693 <= 0.4) && this.jumpingCooldown == 0) {
					this.jump();
					this.jumpingCooldown = 10;
				}
			} else {
				this.method_15645(FluidTags.WATER);
			}
		} else {
			this.jumpingCooldown = 0;
		}

		this.world.profiler.pop();
		this.world.profiler.push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.field_16513 *= 0.98F;
		this.field_6782 *= 0.9F;
		this.method_13073();
		Box box = this.getBoundingBox();
		this.method_2657(this.sidewaysSpeed, this.forwardSpeed, this.field_16513);
		this.world.profiler.pop();
		this.world.profiler.push("push");
		if (this.field_16817 > 0) {
			this.field_16817--;
			this.method_15641(box, this.getBoundingBox());
		}

		this.tickCramming();
		this.world.profiler.pop();
	}

	private void method_13073() {
		boolean bl = this.getFlag(7);
		if (bl && !this.onGround && !this.hasMount()) {
			ItemStack itemStack = this.getStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() == Items.ELYTRA && ElytraItem.method_11370(itemStack)) {
				bl = true;
				if (!this.world.isClient && (this.field_14548 + 1) % 20 == 0) {
					itemStack.damage(1, this);
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
		List<Entity> list = this.world.method_16288(this, this.getBoundingBox(), EntityPredicate.method_15605(this));
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt("maxEntityCramming");
			if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
				int j = 0;

				for (int k = 0; k < list.size(); k++) {
					if (!((Entity)list.get(k)).hasMount()) {
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

	protected void method_15641(Box box, Box box2) {
		Box box3 = box.union(box2);
		List<Entity> list = this.world.getEntities(this, box3);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof LivingEntity) {
					this.method_15647((LivingEntity)entity);
					this.field_16817 = 0;
					this.velocityX *= -0.2;
					this.velocityY *= -0.2;
					this.velocityZ *= -0.2;
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.field_16817 = 0;
		}

		if (!this.world.isClient && this.field_16817 <= 0) {
			this.method_15644(4, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void method_15647(LivingEntity livingEntity) {
	}

	public void method_15650(int i) {
		this.field_16817 = i;
		if (!this.world.isClient) {
			this.method_15644(4, true);
		}
	}

	public boolean method_15646() {
		return (this.dataTracker.get(field_14543) & 4) != 0;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity != null && entity != this.getVehicle() && !this.world.isClient) {
			this.method_6152(entity);
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
		this.serverPitch = x;
		this.serverY = y;
		this.serverZ = z;
		this.serverYaw = (double)yaw;
		this.serverX = (double)pitch;
		this.bodyTrackingIncrements = interpolationSteps;
	}

	@Override
	public void method_15559(float f, int i) {
		this.field_16815 = (double)f;
		this.field_16816 = i;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public void sendPickup(Entity entity, int count) {
		if (!entity.removed && !this.world.isClient) {
			EntityTracker entityTracker = ((ServerWorld)this.world).getEntityTracker();
			if (entity instanceof ItemEntity || entity instanceof AbstractArrowEntity || entity instanceof ExperienceOrbEntity) {
				entityTracker.sendToOtherTrackingEntities(entity, new ChunkRenderDistanceCenterS2CPacket(entity.getEntityId(), this.getEntityId(), count));
			}
		}
	}

	public boolean canSee(Entity entity) {
		return this.world
				.method_3615(
					new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z),
					new Vec3d(entity.x, entity.y + (double)entity.getEyeHeight(), entity.z),
					class_4079.NEVER,
					true,
					false
				)
			== null;
	}

	@Override
	public float method_15591(float f) {
		return f == 1.0F ? this.headYaw : this.prevHeadYaw + (this.headYaw - this.prevHeadYaw) * f;
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
		return !this.removed;
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && !this.isClimbing();
	}

	@Override
	protected void scheduleVelocityUpdate() {
		this.velocityModified = this.random.nextDouble() >= this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).getValue();
	}

	@Override
	public float getHeadRotation() {
		return this.headYaw;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		this.headYaw = headYaw;
	}

	@Override
	public void setYaw(float yaw) {
		this.bodyYaw = yaw;
	}

	public float getAbsorption() {
		return this.absorptionAmount;
	}

	public void setAbsorption(float absorption) {
		if (absorption < 0.0F) {
			absorption = 0.0F;
		}

		this.absorptionAmount = absorption;
	}

	public void enterCombat() {
	}

	public void endCombat() {
	}

	protected void markEffectsDirty() {
		this.effectsChanged = true;
	}

	public abstract HandOption getDurability();

	public boolean method_13061() {
		return (this.dataTracker.get(field_14543) & 1) > 0;
	}

	public Hand method_13062() {
		return (this.dataTracker.get(field_14543) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
	}

	protected void method_13063() {
		if (this.method_13061()) {
			if (this.getStackInHand(this.method_13062()) == this.field_14546) {
				if (this.method_13065() <= 25 && this.method_13065() % 4 == 0) {
					this.method_13038(this.field_14546, 5);
				}

				if (--this.field_14547 == 0 && !this.world.isClient) {
					this.method_3217();
				}
			} else {
				this.method_13053();
			}
		}
	}

	private void method_15649() {
		this.field_16814 = this.field_16813;
		if (this.method_15584()) {
			this.field_16813 = Math.min(1.0F, this.field_16813 + 0.09F);
		} else {
			this.field_16813 = Math.max(0.0F, this.field_16813 - 0.09F);
		}
	}

	protected void method_15644(int i, boolean bl) {
		int j = this.dataTracker.get(field_14543);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(field_14543, (byte)j);
	}

	public void method_13050(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.method_13061()) {
			this.field_14546 = itemStack;
			this.field_14547 = itemStack.getMaxUseTime();
			if (!this.world.isClient) {
				this.method_15644(1, true);
				this.method_15644(2, hand == Hand.OFF_HAND);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (field_14543.equals(data) && this.world.isClient) {
			if (this.method_13061() && this.field_14546.isEmpty()) {
				this.field_14546 = this.getStackInHand(this.method_13062());
				if (!this.field_14546.isEmpty()) {
					this.field_14547 = this.field_14546.getMaxUseTime();
				}
			} else if (!this.method_13061() && !this.field_14546.isEmpty()) {
				this.field_14546 = ItemStack.EMPTY;
				this.field_14547 = 0;
			}
		}
	}

	@Override
	public void method_15563(class_4048.class_4049 arg, Vec3d vec3d) {
		super.method_15563(arg, vec3d);
		this.prevHeadYaw = this.headYaw;
		this.bodyYaw = this.headYaw;
		this.prevBodyYaw = this.bodyYaw;
	}

	protected void method_13038(ItemStack stack, int i) {
		if (!stack.isEmpty() && this.method_13061()) {
			if (stack.getUseAction() == UseAction.DRINK) {
				this.playSound(Sounds.ENTITY_GENERIC_DRINK, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (stack.getUseAction() == UseAction.EAT) {
				this.method_15640(stack, i);
				this.playSound(Sounds.ENTITY_GENERIC_EAT, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	private void method_15640(ItemStack itemStack, int i) {
		for (int j = 0; j < i; j++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
			this.world.method_16343(new class_4339(class_4342.ITEM, itemStack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void method_3217() {
		if (!this.field_14546.isEmpty() && this.method_13061()) {
			this.method_13038(this.field_14546, 16);
			this.equipStack(this.method_13062(), this.field_14546.method_11388(this.world, this));
			this.method_13053();
		}
	}

	public ItemStack method_13064() {
		return this.field_14546;
	}

	public int method_13065() {
		return this.field_14547;
	}

	public int method_13066() {
		return this.method_13061() ? this.field_14546.getMaxUseTime() - this.method_13065() : 0;
	}

	public void method_13067() {
		if (!this.field_14546.isEmpty()) {
			this.field_14546.method_11389(this.world, this, this.method_13065());
		}

		this.method_13053();
	}

	public void method_13053() {
		if (!this.world.isClient) {
			this.method_15644(1, false);
		}

		this.field_14546 = ItemStack.EMPTY;
		this.field_14547 = 0;
	}

	public boolean method_13054() {
		if (this.method_13061() && !this.field_14546.isEmpty()) {
			Item item = this.field_14546.getItem();
			return item.getUseAction(this.field_14546) != UseAction.BLOCK ? false : item.getMaxUseTime(this.field_14546) - this.field_14547 >= 5;
		} else {
			return false;
		}
	}

	public boolean method_13055() {
		return this.getFlag(7);
	}

	public int method_13056() {
		return this.field_14548;
	}

	public boolean method_13071(double x, double y, double z) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		this.x = x;
		this.y = y;
		this.z = z;
		boolean bl = false;
		BlockPos blockPos = new BlockPos(this);
		IWorld iWorld = this.world;
		Random random = this.getRandom();
		if (iWorld.method_16359(blockPos)) {
			boolean bl2 = false;

			while (!bl2 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = iWorld.getBlockState(blockPos2);
				if (blockState.getMaterial().blocksMovement()) {
					bl2 = true;
				} else {
					this.y--;
					blockPos = blockPos2;
				}
			}

			if (bl2) {
				this.refreshPositionAfterTeleport(this.x, this.y, this.z);
				if (iWorld.method_16387(this, this.getBoundingBox()) && !iWorld.method_16388(this.getBoundingBox())) {
					bl = true;
				}
			}
		}

		if (!bl) {
			this.refreshPositionAfterTeleport(d, e, f);
			return false;
		} else {
			int i = 128;

			for (int j = 0; j < 128; j++) {
				double g = (double)j / 127.0;
				float h = (random.nextFloat() - 0.5F) * 0.2F;
				float k = (random.nextFloat() - 0.5F) * 0.2F;
				float l = (random.nextFloat() - 0.5F) * 0.2F;
				double m = d + (this.x - d) * g + (random.nextDouble() - 0.5) * (double)this.width * 2.0;
				double n = e + (this.y - e) * g + random.nextDouble() * (double)this.height;
				double o = f + (this.z - f) * g + (random.nextDouble() - 0.5) * (double)this.width * 2.0;
				iWorld.method_16343(class_4342.field_21361, m, n, o, (double)h, (double)k, (double)l);
			}

			if (this instanceof PathAwareEntity) {
				((PathAwareEntity)this).getNavigation().stop();
			}

			return true;
		}
	}

	public boolean method_13057() {
		return true;
	}

	public boolean method_13948() {
		return true;
	}

	public void method_15058(BlockPos blockPos, boolean bl) {
	}
}
