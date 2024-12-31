package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.particle.ParticleType;
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
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DamageUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class LivingEntity extends Entity {
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
	private final ItemStack[] equippedItems = new ItemStack[2];
	private final ItemStack[] field_14549 = new ItemStack[4];
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
	public float field_6782;
	protected int bodyTrackingIncrements;
	protected double serverPitch;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverX;
	private boolean effectsChanged = true;
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int jumpingCooldown;
	private float absorptionAmount;
	protected ItemStack field_14546;
	protected int field_14547;
	protected int field_14548;
	private BlockPos field_14545;
	private DamageSource field_15029;
	private long field_15030;

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public LivingEntity(World world) {
		super(world);
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
			if (landedState.getMaterial() != Material.AIR) {
				double d = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * d);
				((ServerWorld)this.world).addParticle(ParticleType.BLOCK_DUST, this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15F, Block.getByBlockState(landedState));
			}
		}

		super.fall(heightDifference, onGround, landedState, landedPos);
	}

	public boolean method_2607() {
		return false;
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
			} else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
				double d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getSafeZone();
				if (d < 0.0) {
					double e = this.world.getWorldBorder().getBorderDamagePerBlock();
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
			if (!this.isSubmergedIn(Material.WATER)) {
				this.setAir(300);
			} else {
				if (!this.method_2607() && !this.hasStatusEffect(StatusEffects.WATER_BREATHING) && !bl2) {
					this.setAir(this.getNextAirUnderwater(this.getAir()));
					if (this.getAir() == -20) {
						this.setAir(0);

						for (int i = 0; i < 8; i++) {
							float f = this.random.nextFloat() - this.random.nextFloat();
							float g = this.random.nextFloat() - this.random.nextFloat();
							float h = this.random.nextFloat() - this.random.nextFloat();
							this.world.addParticle(ParticleType.BUBBLE, this.x + (double)f, this.y + (double)g, this.z + (double)h, this.velocityX, this.velocityY, this.velocityZ);
						}

						this.damage(DamageSource.DROWN, 2.0F);
					}
				}

				if (!this.world.isClient && this.hasMount() && this.getVehicle() instanceof LivingEntity) {
					this.stopRiding();
				}
			}

			if (!this.world.isClient) {
				BlockPos blockPos = new BlockPos(this);
				if (!Objects.equal(this.field_14545, blockPos)) {
					this.field_14545 = blockPos;
					this.method_13046(blockPos);
				}
			}
		}

		if (this.isAlive() && this.tickFire()) {
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

	protected void dropXp() {
		this.deathTime++;
		if (this.deathTime == 20) {
			if (!this.world.isClient
				&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.world.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getXpToDrop(this.attackingPlayer);

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
				}
			}

			this.remove();

			for (int k = 0; k < 20; k++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.addParticle(
						ParticleType.EXPLOSION,
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

	protected int getXpToDrop(PlayerEntity player) {
		return 0;
	}

	protected boolean shouldAlwaysDropXp() {
		return false;
	}

	public Random getRandom() {
		return this.random;
	}

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

	protected void method_13045(@Nullable ItemStack itemStack) {
		if (itemStack != null) {
			Sound sound = Sounds.ITEM_ARMOR_EQUIP_GENERIC;
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem) {
				sound = ((ArmorItem)item).getMaterial().method_11355();
			} else if (item == Items.ELYTRA) {
				sound = Sounds.ITEM_ARMOR_EQUIP_LEATHER;
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
			if (itemStack != null) {
				this.getAttributeContainer().removeAll(itemStack.getAttributes(equipmentSlot));
			}
		}

		nbt.put("Attributes", EntityAttributes.toNbt(this.getAttributeContainer()));

		for (EquipmentSlot equipmentSlot2 : EquipmentSlot.values()) {
			ItemStack itemStack2 = this.getStack(equipmentSlot2);
			if (itemStack2 != null) {
				this.getAttributeContainer().replaceAll(itemStack2.getAttributes(equipmentSlot2));
			}
		}

		if (!this.statusEffects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.statusEffects.values()) {
				nbtList.add(statusEffectInstance.toNbt(new NbtCompound()));
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
			this.world.getScoreboard().addPlayerToTeam(this.getEntityName(), string);
		}

		if (nbt.getBoolean("FallFlying")) {
			this.setFlag(7, true);
		}
	}

	protected void tickStatusEffects() {
		Iterator<StatusEffect> iterator = this.statusEffects.keySet().iterator();

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
					.addParticle(
						bl ? ParticleType.AMBIENT_MOB_SPELL : ParticleType.MOB_SPELL,
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

	public void clearStatusEffects() {
		if (!this.world.isClient) {
			Iterator<StatusEffectInstance> iterator = this.statusEffects.values().iterator();

			while (iterator.hasNext()) {
				this.method_2649((StatusEffectInstance)iterator.next());
				iterator.remove();
			}
		}
	}

	public Collection<StatusEffectInstance> getStatusEffectInstances() {
		return this.statusEffects.values();
	}

	public boolean hasStatusEffect(StatusEffect effect) {
		return this.statusEffects.containsKey(effect);
	}

	@Nullable
	public StatusEffectInstance getEffectInstance(StatusEffect effect) {
		return (StatusEffectInstance)this.statusEffects.get(effect);
	}

	public void addStatusEffect(StatusEffectInstance instance) {
		if (this.method_2658(instance)) {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.statusEffects.get(instance.getStatusEffect());
			if (statusEffectInstance == null) {
				this.statusEffects.put(instance.getStatusEffect(), instance);
				this.method_2582(instance);
			} else {
				statusEffectInstance.setFrom(instance);
				this.method_6108(statusEffectInstance, true);
			}
		}
	}

	public boolean method_2658(StatusEffectInstance instance) {
		if (this.getGroup() == EntityGroup.UNDEAD) {
			StatusEffect statusEffect = instance.getStatusEffect();
			if (statusEffect == StatusEffects.REGENERATION || statusEffect == StatusEffects.POISON) {
				return false;
			}
		}

		return true;
	}

	public boolean isAffectedBySmite() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	@Nullable
	public StatusEffectInstance method_13052(@Nullable StatusEffect effect) {
		return (StatusEffectInstance)this.statusEffects.remove(effect);
	}

	public void removeStatusEffect(StatusEffect effect) {
		StatusEffectInstance statusEffectInstance = this.method_13052(effect);
		if (statusEffectInstance != null) {
			this.method_2649(statusEffectInstance);
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

	public final float getHealth() {
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
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else if (source.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
				return false;
			} else {
				if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && this.getStack(EquipmentSlot.HEAD) != null) {
					this.getStack(EquipmentSlot.HEAD).damage((int)(amount * 4.0F + this.random.nextFloat() * amount * 2.0F), this);
					amount *= 0.75F;
				}

				boolean bl = false;
				if (amount > 0.0F && this.method_13068(source)) {
					this.method_13072(amount);
					if (source.isProjectile()) {
						amount = 0.0F;
					} else {
						amount *= 0.33F;
						if (source.getSource() instanceof LivingEntity) {
							((LivingEntity)source.getSource()).method_6109(this, 0.5F, this.x - source.getSource().x, this.z - source.getSource().z);
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
				Entity entity = source.getAttacker();
				if (entity != null) {
					if (entity instanceof LivingEntity) {
						this.setAttacker((LivingEntity)entity);
					}

					if (entity instanceof PlayerEntity) {
						this.playerHitTimer = 100;
						this.attackingPlayer = (PlayerEntity)entity;
					} else if (entity instanceof WolfEntity) {
						WolfEntity wolfEntity = (WolfEntity)entity;
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
						this.world.sendEntityStatus(this, (byte)2);
					}

					if (source != DamageSource.DROWN && (!bl || amount > 0.0F)) {
						this.scheduleVelocityUpdate();
					}

					if (entity != null) {
						double d = entity.x - this.x;

						double e;
						for (e = entity.z - this.z; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
							d = (Math.random() - Math.random()) * 0.01;
						}

						this.knockbackVelocity = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI - (double)this.yaw);
						this.method_6109(entity, 0.4F, d, e);
					} else {
						this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
					}
				}

				if (this.getHealth() <= 0.0F) {
					Sound sound = this.deathSound();
					if (bl2 && sound != null) {
						this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onKilled(source);
				} else if (bl2) {
					this.method_13051(source);
				}

				if (!bl || amount > 0.0F) {
					this.field_15029 = source;
					this.field_15030 = this.world.getLastUpdateTime();
				}

				return !bl || amount > 0.0F;
			}
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
		Sound sound = this.method_13048();
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
		this.playSound(Sounds.ENTITY_ITEM_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

		for (int i = 0; i < 5; i++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
			this.world.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(stack.getItem()));
		}
	}

	public void onKilled(DamageSource source) {
		if (!this.dead) {
			Entity entity = source.getAttacker();
			LivingEntity livingEntity = this.getOpponent();
			if (this.field_6777 >= 0 && livingEntity != null) {
				livingEntity.updateKilledAdvancementCriterion(this, this.field_6777);
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
	protected Sound method_13048() {
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
		if ((Boolean)state.get(TrapdoorBlock.OPEN)) {
			BlockState blockState = this.world.getBlockState(pos.down());
			if (blockState.getBlock() == Blocks.LADDER && blockState.get(LadderBlock.FACING) == state.get(TrapdoorBlock.FACING)) {
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
			if (blockState.getMaterial() != Material.AIR) {
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
				amount = f / 25.0F;
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
			float var7 = Math.max(damage - this.getAbsorption(), 0.0F);
			this.setAbsorption(this.getAbsorption() - (damage - var7));
			if (var7 != 0.0F) {
				float g = this.getHealth();
				this.setHealth(g - var7);
				this.getDamageTracker().onDamage(source, g, var7);
				this.setAbsorption(this.getAbsorption() - var7);
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
		if (this.hasStatusEffect(StatusEffects.HASTE)) {
			return 6 - (1 + this.getEffectInstance(StatusEffects.HASTE).getAmplifier());
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
		if (status == 2 || bl) {
			this.field_6749 = 1.5F;
			this.timeUntilRegen = this.defaultMaxHealth;
			this.maxHurtTime = 10;
			this.hurtTime = this.maxHurtTime;
			this.knockbackVelocity = 0.0F;
			if (bl) {
				this.playSound(Sounds.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			Sound sound = this.method_13048();
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

	public EntityGroup getGroup() {
		return EntityGroup.DEFAULT;
	}

	@Nullable
	public ItemStack getMainHandStack() {
		return this.getStack(EquipmentSlot.MAINHAND);
	}

	@Nullable
	public ItemStack getOffHandStack() {
		return this.getStack(EquipmentSlot.OFFHAND);
	}

	@Nullable
	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.getStack(EquipmentSlot.MAINHAND);
		} else if (hand == Hand.OFF_HAND) {
			return this.getStack(EquipmentSlot.OFFHAND);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void equipStack(Hand hand, @Nullable ItemStack stack) {
		if (hand == Hand.MAIN_HAND) {
			this.equipStack(EquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != Hand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.equipStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	@Override
	public abstract Iterable<ItemStack> getArmorItems();

	@Nullable
	public abstract ItemStack getStack(EquipmentSlot slot);

	@Override
	public abstract void equipStack(EquipmentSlot slot, @Nullable ItemStack stack);

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
		if (!(entity instanceof BoatEntity) && !(entity instanceof HorseBaseEntity)) {
			double l = entity.x;
			double m = entity.getBoundingBox().minY + (double)entity.height;
			double n = entity.z;
			Direction direction = entity.getMovementDirection();
			Direction direction2 = direction.rotateYClockwise();
			int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
			double o = Math.floor(this.x) + 0.5;
			double p = Math.floor(this.z) + 0.5;
			double q = this.getBoundingBox().maxX - this.getBoundingBox().minX;
			double r = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
			Box box = new Box(o - q / 2.0, this.getBoundingBox().minY, p - r / 2.0, o + q / 2.0, this.getBoundingBox().maxY, p + r / 2.0);

			for (int[] ks : is) {
				double u = (double)(direction.getOffsetX() * ks[0] + direction2.getOffsetX() * ks[1]);
				double v = (double)(direction.getOffsetZ() * ks[0] + direction2.getOffsetZ() * ks[1]);
				double w = o + u;
				double x = p + v;
				Box box2 = box.offset(u, 1.0, v);
				if (!this.world.method_11488(box2)) {
					if (this.world.getBlockState(new BlockPos(w, this.y, x)).method_11739()) {
						this.refreshPositionAfterTeleport(w, this.y + 1.0, x);
						return;
					}

					BlockPos blockPos = new BlockPos(w, this.y - 1.0, x);
					if (this.world.getBlockState(blockPos).method_11739() || this.world.getBlockState(blockPos).getMaterial() == Material.WATER) {
						l = w;
						m = this.y + 1.0;
						n = x;
					}
				} else if (!this.world.method_11488(box2.offset(0.0, 1.0, 0.0)) && this.world.getBlockState(new BlockPos(w, this.y + 1.0, x)).method_11739()) {
					l = w;
					m = this.y + 2.0;
					n = x;
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
			if (this.world.method_11488(this.getBoundingBox())) {
				this.updatePosition(j, entity.y + (double)entity.height + 1.001, k);
				if (this.world.method_11488(this.getBoundingBox())) {
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
		this.velocityY += 0.04F;
	}

	protected void method_10979() {
		this.velocityY += 0.04F;
	}

	protected float method_13494() {
		return 0.8F;
	}

	public void travel(float f, float g) {
		if (this.canMoveVoluntarily() || this.method_13003()) {
			if (!this.isTouchingWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
				if (!this.isTouchingLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
					if (this.method_13055()) {
						if (this.velocityY > -0.5) {
							this.fallDistance = 1.0F;
						}

						Vec3d vec3d = this.getRotation();
						float k = this.pitch * (float) (Math.PI / 180.0);
						double l = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
						double m = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
						double n = vec3d.length();
						float o = MathHelper.cos(k);
						o = (float)((double)o * (double)o * Math.min(1.0, n / 0.4));
						this.velocityY += -0.08 + (double)o * 0.06;
						if (this.velocityY < 0.0 && l > 0.0) {
							double p = this.velocityY * -0.1 * (double)o;
							this.velocityY += p;
							this.velocityX = this.velocityX + vec3d.x * p / l;
							this.velocityZ = this.velocityZ + vec3d.z * p / l;
						}

						if (k < 0.0F) {
							double q = m * (double)(-MathHelper.sin(k)) * 0.04;
							this.velocityY += q * 3.2;
							this.velocityX = this.velocityX - vec3d.x * q / l;
							this.velocityZ = this.velocityZ - vec3d.z * q / l;
						}

						if (l > 0.0) {
							this.velocityX = this.velocityX + (vec3d.x / l * m - this.velocityX) * 0.1;
							this.velocityZ = this.velocityZ + (vec3d.z / l * m - this.velocityZ) * 0.1;
						}

						this.velocityX *= 0.99F;
						this.velocityY *= 0.98F;
						this.velocityZ *= 0.99F;
						this.move(this.velocityX, this.velocityY, this.velocityZ);
						if (this.horizontalCollision && !this.world.isClient) {
							double r = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
							double s = m - r;
							float t = (float)(s * 10.0 - 3.0);
							if (t > 0.0F) {
								this.playSound(this.getLandSound((int)t), 1.0F, 1.0F);
								this.damage(DamageSource.FLY_INTO_WALL, t);
							}
						}

						if (this.onGround && !this.world.isClient) {
							this.setFlag(7, false);
						}
					} else {
						float u = 0.91F;
						BlockPos.Pooled pooled = BlockPos.Pooled.method_12567(this.x, this.getBoundingBox().minY - 1.0, this.z);
						if (this.onGround) {
							u = this.world.getBlockState(pooled).getBlock().slipperiness * 0.91F;
						}

						float v = 0.16277136F / (u * u * u);
						float w;
						if (this.onGround) {
							w = this.getMovementSpeed() * v;
						} else {
							w = this.flyingSpeed;
						}

						this.updateVelocity(f, g, w);
						u = 0.91F;
						if (this.onGround) {
							u = this.world.getBlockState(pooled.set(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().slipperiness * 0.91F;
						}

						if (this.isClimbing()) {
							float y = 0.15F;
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

						this.move(this.velocityX, this.velocityY, this.velocityZ);
						if (this.horizontalCollision && this.isClimbing()) {
							this.velocityY = 0.2;
						}

						if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
							this.velocityY = this.velocityY + (0.05 * (double)(this.getEffectInstance(StatusEffects.LEVITATION).getAmplifier() + 1) - this.velocityY) * 0.2;
						} else {
							pooled.set(this.x, 0.0, this.z);
							if (!this.world.isClient || this.world.blockExists(pooled) && this.world.getChunk(pooled).isLoaded()) {
								if (!this.hasNoGravity()) {
									this.velocityY -= 0.08;
								}
							} else if (this.y > 0.0) {
								this.velocityY = -0.1;
							} else {
								this.velocityY = 0.0;
							}
						}

						this.velocityY *= 0.98F;
						this.velocityX *= (double)u;
						this.velocityZ *= (double)u;
						pooled.method_12576();
					}
				} else {
					double e = this.y;
					this.updateVelocity(f, g, 0.02F);
					this.move(this.velocityX, this.velocityY, this.velocityZ);
					this.velocityX *= 0.5;
					this.velocityY *= 0.5;
					this.velocityZ *= 0.5;
					if (!this.hasNoGravity()) {
						this.velocityY -= 0.02;
					}

					if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + e, this.velocityZ)) {
						this.velocityY = 0.3F;
					}
				}
			} else {
				double d = this.y;
				float h = this.method_13494();
				float i = 0.02F;
				float j = (float)EnchantmentHelper.getDepthStrider(this);
				if (j > 3.0F) {
					j = 3.0F;
				}

				if (!this.onGround) {
					j *= 0.5F;
				}

				if (j > 0.0F) {
					h += (0.54600006F - h) * j / 3.0F;
					i += (this.getMovementSpeed() - i) * j / 3.0F;
				}

				this.updateVelocity(f, g, i);
				this.move(this.velocityX, this.velocityY, this.velocityZ);
				this.velocityX *= (double)h;
				this.velocityY *= 0.8F;
				this.velocityZ *= (double)h;
				if (!this.hasNoGravity()) {
					this.velocityY -= 0.02;
				}

				if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + d, this.velocityZ)) {
					this.velocityY = 0.3F;
				}
			}
		}

		this.field_6748 = this.field_6749;
		double z = this.x - this.prevX;
		double aa = this.z - this.prevZ;
		float ab = MathHelper.sqrt(z * z + aa * aa) * 4.0F;
		if (ab > 1.0F) {
			ab = 1.0F;
		}

		this.field_6749 = this.field_6749 + (ab - this.field_6749) * 0.4F;
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
						itemStack = this.equippedItems[equipmentSlot.method_13032()];
						break;
					case ARMOR:
						itemStack = this.field_14549[equipmentSlot.method_13032()];
						break;
					default:
						continue;
				}

				ItemStack itemStack2 = this.getStack(equipmentSlot);
				if (!ItemStack.equalsAll(itemStack2, itemStack)) {
					((ServerWorld)this.world)
						.getEntityTracker()
						.sendToOtherTrackingEntities(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack2));
					if (itemStack != null) {
						this.getAttributeContainer().removeAll(itemStack.getAttributes(equipmentSlot));
					}

					if (itemStack2 != null) {
						this.getAttributeContainer().replaceAll(itemStack2.getAttributes(equipmentSlot));
					}

					switch (equipmentSlot.getType()) {
						case HAND:
							this.equippedItems[equipmentSlot.method_13032()] = itemStack2 == null ? null : itemStack2.copy();
							break;
						case ARMOR:
							this.field_14549[equipmentSlot.method_13032()] = itemStack2 == null ? null : itemStack2.copy();
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
		float l = 0.0F;
		if (f > 0.0025000002F) {
			l = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			g = (float)MathHelper.atan2(e, d) * (180.0F / (float)Math.PI) - 90.0F;
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			l = 0.0F;
		}

		this.stepBobbingAmount = this.stepBobbingAmount + (l - this.stepBobbingAmount) * 0.3F;
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
			this.forwardSpeed = 0.0F;
			this.field_6782 = 0.0F;
		} else if (this.canMoveVoluntarily()) {
			this.world.profiler.push("newAi");
			this.tickNewAi();
			this.world.profiler.pop();
		}

		this.world.profiler.pop();
		this.world.profiler.push("jump");
		if (this.jumping) {
			if (this.isTouchingWater()) {
				this.method_2634();
			} else if (this.isTouchingLava()) {
				this.method_10979();
			} else if (this.onGround && this.jumpingCooldown == 0) {
				this.jump();
				this.jumpingCooldown = 10;
			}
		} else {
			this.jumpingCooldown = 0;
		}

		this.world.profiler.pop();
		this.world.profiler.push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.forwardSpeed *= 0.98F;
		this.field_6782 *= 0.9F;
		this.method_13073();
		this.travel(this.sidewaysSpeed, this.forwardSpeed);
		this.world.profiler.pop();
		this.world.profiler.push("push");
		this.tickCramming();
		this.world.profiler.pop();
	}

	private void method_13073() {
		boolean bl = this.getFlag(7);
		if (bl && !this.onGround && !this.hasMount()) {
			ItemStack itemStack = this.getStack(EquipmentSlot.CHEST);
			if (itemStack != null && itemStack.getItem() == Items.ELYTRA && ElytraItem.method_11370(itemStack)) {
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
		List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox(), EntityPredicate.method_13025(this));
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				this.pushAway(entity);
			}
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
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

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public void sendPickup(Entity entity, int count) {
		if (!entity.removed && !this.world.isClient) {
			EntityTracker entityTracker = ((ServerWorld)this.world).getEntityTracker();
			if (entity instanceof ItemEntity) {
				entityTracker.sendToOtherTrackingEntities(entity, new ChunkRenderDistanceCenterS2CPacket(entity.getEntityId(), this.getEntityId()));
			}

			if (entity instanceof AbstractArrowEntity) {
				entityTracker.sendToOtherTrackingEntities(entity, new ChunkRenderDistanceCenterS2CPacket(entity.getEntityId(), this.getEntityId()));
			}

			if (entity instanceof ExperienceOrbEntity) {
				entityTracker.sendToOtherTrackingEntities(entity, new ChunkRenderDistanceCenterS2CPacket(entity.getEntityId(), this.getEntityId()));
			}
		}
	}

	public boolean canSee(Entity entity) {
		return this.world
				.rayTrace(
					new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z),
					new Vec3d(entity.x, entity.y + (double)entity.getEyeHeight(), entity.z),
					false,
					true,
					false
				)
			== null;
	}

	@Override
	public Vec3d getRotation() {
		return this.getRotationVector(1.0F);
	}

	@Override
	public Vec3d getRotationVector(float vector) {
		if (vector == 1.0F) {
			return this.getRotationVector(this.pitch, this.headYaw);
		} else {
			float f = this.prevPitch + (this.pitch - this.prevPitch) * vector;
			float g = this.prevHeadYaw + (this.headYaw - this.prevHeadYaw) * vector;
			return this.getRotationVector(f, g);
		}
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
		return !this.removed;
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
			ItemStack itemStack = this.getStackInHand(this.method_13062());
			if (itemStack == this.field_14546) {
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

	public void method_13050(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (itemStack != null && !this.method_13061()) {
			this.field_14546 = itemStack;
			this.field_14547 = itemStack.getMaxUseTime();
			if (!this.world.isClient) {
				int i = 1;
				if (hand == Hand.OFF_HAND) {
					i |= 2;
				}

				this.dataTracker.set(field_14543, (byte)i);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (field_14543.equals(data) && this.world.isClient) {
			if (this.method_13061() && this.field_14546 == null) {
				this.field_14546 = this.getStackInHand(this.method_13062());
				if (this.field_14546 != null) {
					this.field_14547 = this.field_14546.getMaxUseTime();
				}
			} else if (!this.method_13061() && this.field_14546 != null) {
				this.field_14546 = null;
				this.field_14547 = 0;
			}
		}
	}

	protected void method_13038(@Nullable ItemStack stack, int i) {
		if (stack != null && this.method_13061()) {
			if (stack.getUseAction() == UseAction.DRINK) {
				this.playSound(Sounds.ENTITY_GENERIC_DRINK, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (stack.getUseAction() == UseAction.EAT) {
				for (int j = 0; j < i; j++) {
					Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
					vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
					vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
					double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
					Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
					vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
					vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
					vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
					if (stack.isUnbreakable()) {
						this.world
							.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(stack.getItem()), stack.getData());
					} else {
						this.world.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(stack.getItem()));
					}
				}

				this.playSound(Sounds.ENTITY_GENERIC_EAT, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	protected void method_3217() {
		if (this.field_14546 != null && this.method_13061()) {
			this.method_13038(this.field_14546, 16);
			ItemStack itemStack = this.field_14546.method_11388(this.world, this);
			if (itemStack != null && itemStack.count == 0) {
				itemStack = null;
			}

			this.equipStack(this.method_13062(), itemStack);
			this.method_13053();
		}
	}

	@Nullable
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
		if (this.field_14546 != null) {
			this.field_14546.method_11389(this.world, this, this.method_13065());
		}

		this.method_13053();
	}

	public void method_13053() {
		if (!this.world.isClient) {
			this.dataTracker.set(field_14543, (byte)0);
		}

		this.field_14546 = null;
		this.field_14547 = 0;
	}

	public boolean method_13054() {
		if (this.method_13061() && this.field_14546 != null) {
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
		World world = this.world;
		Random random = this.getRandom();
		if (world.blockExists(blockPos)) {
			boolean bl2 = false;

			while (!bl2 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getMaterial().blocksMovement()) {
					bl2 = true;
				} else {
					this.y--;
					blockPos = blockPos2;
				}
			}

			if (bl2) {
				this.refreshPositionAfterTeleport(this.x, this.y, this.z);
				if (world.doesBoxCollide(this, this.getBoundingBox()).isEmpty() && !world.containsFluid(this.getBoundingBox())) {
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
				world.addParticle(ParticleType.NETHER_PORTAL, m, n, o, (double)h, (double)k, (double)l);
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
}
