package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtShort;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class LivingEntity extends Entity {
	private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final AttributeModifier SPRINTING_SPEED_MODIFIER = new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, 2)
		.setSerialized(false);
	private AbstractEntityAttributeContainer attributeContainer;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<Integer, StatusEffectInstance> statusEffects = Maps.newHashMap();
	private final ItemStack[] equippedItems = new ItemStack[5];
	public boolean handSwinging;
	public int handSwingTicks;
	public int field_6771;
	public int hurtTime;
	public int maxHurtTime;
	public float knockbackVelocity;
	public int deathTime;
	public float lastHandSwingProgress;
	public float handSwingProgress;
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
	protected float field_6782;
	protected int bodyTrackingIncrements;
	protected double serverX;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverPitch;
	private boolean effectsChanged = true;
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int jumpingCooldown;
	private float absorptionAmount;

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
		this.yaw = (float)(Math.random() * (float) Math.PI * 2.0);
		this.headYaw = this.yaw;
		this.stepHeight = 0.6F;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.track(7, 0);
		this.dataTracker.track(8, (byte)0);
		this.dataTracker.track(9, (byte)0);
		this.dataTracker.track(6, 1.0F);
	}

	protected void initializeAttributes() {
		this.getAttributeContainer().register(EntityAttributes.GENERIC_MAX_HEALTH);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		this.getAttributeContainer().register(EntityAttributes.GENERIC_MOVEMENT_SPEED);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, Block landedBlock, BlockPos landedPosition) {
		if (!this.isTouchingWater()) {
			this.updateWaterState();
		}

		if (!this.world.isClient && this.fallDistance > 3.0F && onGround) {
			BlockState blockState = this.world.getBlockState(landedPosition);
			Block block = blockState.getBlock();
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (block.getMaterial() != Material.AIR) {
				double d = (double)Math.min(0.2F + f / 15.0F, 10.0F);
				if (d > 2.5) {
					d = 2.5;
				}

				int i = (int)(150.0 * d);
				((ServerWorld)this.world).addParticle(ParticleType.BLOCK_DUST, this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15F, Block.getByBlockState(blockState));
			}
		}

		super.fall(heightDifference, onGround, landedBlock, landedPosition);
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
					this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d * this.world.getWorldBorder().getBorderDamagePerBlock())));
				}
			}
		}

		if (this.isFireImmune() || this.world.isClient) {
			this.extinguish();
		}

		boolean bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
		if (this.isAlive()) {
			if (this.isSubmergedIn(Material.WATER)) {
				if (!this.method_2607() && !this.hasStatusEffect(StatusEffect.WATER_BREATHING.id) && !bl2) {
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

				if (!this.world.isClient && this.hasVehicle() && this.vehicle instanceof LivingEntity) {
					this.startRiding(null);
				}
			} else {
				this.setAir(300);
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

	public boolean isBaby() {
		return false;
	}

	protected void dropXp() {
		this.deathTime++;
		if (this.deathTime == 20) {
			if (!this.world.isClient
				&& (this.playerHitTimer > 0 || this.shouldAlwaysDropXp())
				&& this.shouldDropXp()
				&& this.world.getGameRules().getBoolean("doMobLoot")) {
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

	public void setAttacker(LivingEntity entity) {
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

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("HealF", this.getHealth());
		nbt.putShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
		nbt.putShort("HurtTime", (short)this.hurtTime);
		nbt.putInt("HurtByTimestamp", this.lastAttackedTime);
		nbt.putShort("DeathTime", (short)this.deathTime);
		nbt.putFloat("AbsorptionAmount", this.getAbsorption());

		for (ItemStack itemStack : this.getArmorStacks()) {
			if (itemStack != null) {
				this.attributeContainer.removeAll(itemStack.getAttributes());
			}
		}

		nbt.put("Attributes", EntityAttributes.toNbt(this.getAttributeContainer()));

		for (ItemStack itemStack2 : this.getArmorStacks()) {
			if (itemStack2 != null) {
				this.attributeContainer.replaceAll(itemStack2.getAttributes());
			}
		}

		if (!this.statusEffects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (StatusEffectInstance statusEffectInstance : this.statusEffects.values()) {
				nbtList.add(statusEffectInstance.toNbt(new NbtCompound()));
			}

			nbt.put("ActiveEffects", nbtList);
		}
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
					this.statusEffects.put(statusEffectInstance.getEffectId(), statusEffectInstance);
				}
			}
		}

		if (nbt.contains("HealF", 99)) {
			this.setHealth(nbt.getFloat("HealF"));
		} else {
			NbtElement nbtElement = nbt.get("Health");
			if (nbtElement == null) {
				this.setHealth(this.getMaxHealth());
			} else if (nbtElement.getType() == 5) {
				this.setHealth(((NbtFloat)nbtElement).floatValue());
			} else if (nbtElement.getType() == 2) {
				this.setHealth((float)((NbtShort)nbtElement).shortValue());
			}
		}

		this.hurtTime = nbt.getShort("HurtTime");
		this.deathTime = nbt.getShort("DeathTime");
		this.lastAttackedTime = nbt.getInt("HurtByTimestamp");
	}

	protected void tickStatusEffects() {
		Iterator<Integer> iterator = this.statusEffects.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer)iterator.next();
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.statusEffects.get(integer);
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

		int i = this.dataTracker.getInt(7);
		boolean bl = this.dataTracker.getByte(8) > 0;
		if (i > 0) {
			boolean bl2 = false;
			if (!this.isInvisible()) {
				bl2 = this.random.nextBoolean();
			} else {
				bl2 = this.random.nextInt(15) == 0;
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
			int i = StatusEffectStrings.method_3475(this.statusEffects.values());
			this.dataTracker.setProperty(8, Byte.valueOf((byte)(StatusEffectStrings.method_4633(this.statusEffects.values()) ? 1 : 0)));
			this.dataTracker.setProperty(7, i);
			this.setInvisible(this.hasStatusEffect(StatusEffect.INVISIBILITY.id));
		}
	}

	protected void method_10981() {
		this.dataTracker.setProperty(8, (byte)0);
		this.dataTracker.setProperty(7, 0);
	}

	public void clearStatusEffects() {
		Iterator<Integer> iterator = this.statusEffects.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer)iterator.next();
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.statusEffects.get(integer);
			if (!this.world.isClient) {
				iterator.remove();
				this.method_2649(statusEffectInstance);
			}
		}
	}

	public Collection<StatusEffectInstance> getStatusEffectInstances() {
		return this.statusEffects.values();
	}

	public boolean hasStatusEffect(int id) {
		return this.statusEffects.containsKey(id);
	}

	public boolean hasStatusEffect(StatusEffect effect) {
		return this.statusEffects.containsKey(effect.id);
	}

	public StatusEffectInstance getEffectInstance(StatusEffect effect) {
		return (StatusEffectInstance)this.statusEffects.get(effect.id);
	}

	public void addStatusEffect(StatusEffectInstance instance) {
		if (this.method_2658(instance)) {
			if (this.statusEffects.containsKey(instance.getEffectId())) {
				((StatusEffectInstance)this.statusEffects.get(instance.getEffectId())).setFrom(instance);
				this.method_6108((StatusEffectInstance)this.statusEffects.get(instance.getEffectId()), true);
			} else {
				this.statusEffects.put(instance.getEffectId(), instance);
				this.method_2582(instance);
			}
		}
	}

	public boolean method_2658(StatusEffectInstance instance) {
		if (this.getGroup() == EntityGroup.UNDEAD) {
			int i = instance.getEffectId();
			if (i == StatusEffect.REGENERATION.id || i == StatusEffect.POISON.id) {
				return false;
			}
		}

		return true;
	}

	public boolean isAffectedBySmite() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	public void removeEffect(int id) {
		this.statusEffects.remove(id);
	}

	public void method_6149(int id) {
		StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.statusEffects.remove(id);
		if (statusEffectInstance != null) {
			this.method_2649(statusEffectInstance);
		}
	}

	protected void method_2582(StatusEffectInstance instance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			StatusEffect.STATUS_EFFECTS[instance.getEffectId()].method_6091(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	protected void method_6108(StatusEffectInstance instance, boolean bl) {
		this.effectsChanged = true;
		if (bl && !this.world.isClient) {
			StatusEffect.STATUS_EFFECTS[instance.getEffectId()].onRemoved(this, this.getAttributeContainer(), instance.getAmplifier());
			StatusEffect.STATUS_EFFECTS[instance.getEffectId()].method_6091(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	protected void method_2649(StatusEffectInstance instance) {
		this.effectsChanged = true;
		if (!this.world.isClient) {
			StatusEffect.STATUS_EFFECTS[instance.getEffectId()].onRemoved(this, this.getAttributeContainer(), instance.getAmplifier());
		}
	}

	public void heal(float f) {
		float g = this.getHealth();
		if (g > 0.0F) {
			this.setHealth(g + f);
		}
	}

	public final float getHealth() {
		return this.dataTracker.getFloat(6);
	}

	public void setHealth(float f) {
		this.dataTracker.setProperty(6, MathHelper.clamp(f, 0.0F, this.getMaxHealth()));
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
			} else if (source.isFire() && this.hasStatusEffect(StatusEffect.FIRE_RESISTANCE)) {
				return false;
			} else {
				if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && this.getMainSlot(4) != null) {
					this.getMainSlot(4).damage((int)(amount * 4.0F + this.random.nextFloat() * amount * 2.0F), this);
					amount *= 0.75F;
				}

				this.field_6749 = 1.5F;
				boolean bl = true;
				if ((float)this.timeUntilRegen > (float)this.defaultMaxHealth / 2.0F) {
					if (amount <= this.field_6778) {
						return false;
					}

					this.applyDamage(source, amount - this.field_6778);
					this.field_6778 = amount;
					bl = false;
				} else {
					this.field_6778 = amount;
					this.timeUntilRegen = this.defaultMaxHealth;
					this.applyDamage(source, amount);
					this.hurtTime = this.maxHurtTime = 10;
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

				if (bl) {
					this.world.sendEntityStatus(this, (byte)2);
					if (source != DamageSource.DROWN) {
						this.scheduleVelocityUpdate();
					}

					if (entity != null) {
						double d = entity.x - this.x;

						double e;
						for (e = entity.z - this.z; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
							d = (Math.random() - Math.random()) * 0.01;
						}

						this.knockbackVelocity = (float)(MathHelper.atan2(e, d) * 180.0 / (float) Math.PI - (double)this.yaw);
						this.method_6109(entity, amount, d, e);
					} else {
						this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
					}
				}

				if (this.getHealth() <= 0.0F) {
					String string = this.getDeathSound();
					if (bl && string != null) {
						this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onKilled(source);
				} else {
					String string2 = this.getHurtSound();
					if (bl && string2 != null) {
						this.playSound(string2, this.getSoundVolume(), this.getSoundPitch());
					}
				}

				return true;
			}
		}
	}

	public void method_6111(ItemStack stack) {
		this.playSound("random.break", 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

		for (int i = 0; i < 5; i++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) Math.PI / 180.0F);
			vec3d = vec3d.rotateY(-this.yaw * (float) Math.PI / 180.0F);
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) Math.PI / 180.0F);
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) Math.PI / 180.0F);
			vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
			this.world.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(stack.getItem()));
		}
	}

	public void onKilled(DamageSource source) {
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
				this.dropLoot(this.playerHitTimer > 0, i);
				this.method_4472(this.playerHitTimer > 0, i);
				if (this.playerHitTimer > 0 && this.random.nextFloat() < 0.025F + (float)i * 0.01F) {
					this.method_4473();
				}
			}
		}

		this.world.sendEntityStatus(this, (byte)3);
	}

	protected void method_4472(boolean bl, int i) {
	}

	public void method_6109(Entity entity, float f, double d, double e) {
		if (!(this.random.nextDouble() < this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).getValue())) {
			this.velocityDirty = true;
			float g = MathHelper.sqrt(d * d + e * e);
			float h = 0.4F;
			this.velocityX /= 2.0;
			this.velocityY /= 2.0;
			this.velocityZ /= 2.0;
			this.velocityX -= d / (double)g * (double)h;
			this.velocityY += (double)h;
			this.velocityZ -= e / (double)g * (double)h;
			if (this.velocityY > 0.4F) {
				this.velocityY = 0.4F;
			}
		}
	}

	protected String getHurtSound() {
		return "game.neutral.hurt";
	}

	protected String getDeathSound() {
		return "game.neutral.die";
	}

	protected void method_4473() {
	}

	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
	}

	public boolean isClimbing() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		Block block = this.world.getBlockState(new BlockPos(i, j, k)).getBlock();
		return (block == Blocks.LADDER || block == Blocks.VINE) && (!(this instanceof PlayerEntity) || !((PlayerEntity)this).isSpectator());
	}

	@Override
	public boolean isAlive() {
		return !this.removed && this.getHealth() > 0.0F;
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		super.handleFallDamage(fallDistance, damageMultiplier);
		StatusEffectInstance statusEffectInstance = this.getEffectInstance(StatusEffect.JUMP_BOOST);
		float f = statusEffectInstance != null ? (float)(statusEffectInstance.getAmplifier() + 1) : 0.0F;
		int i = MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.damage(DamageSource.FALL, (float)i);
			int j = MathHelper.floor(this.x);
			int k = MathHelper.floor(this.y - 0.2F);
			int l = MathHelper.floor(this.z);
			Block block = this.world.getBlockState(new BlockPos(j, k, l)).getBlock();
			if (block.getMaterial() != Material.AIR) {
				Block.Sound sound = block.sound;
				this.playSound(sound.getStepSound(), sound.getVolume() * 0.5F, sound.getPitch() * 0.75F);
			}
		}
	}

	protected String getFallSound(int distance) {
		return distance > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
	}

	@Override
	public void animateDamage() {
		this.hurtTime = this.maxHurtTime = 10;
		this.knockbackVelocity = 0.0F;
	}

	public int getArmorProtectionValue() {
		int i = 0;

		for (ItemStack itemStack : this.getArmorStacks()) {
			if (itemStack != null && itemStack.getItem() instanceof ArmorItem) {
				int l = ((ArmorItem)itemStack.getItem()).protection;
				i += l;
			}
		}

		return i;
	}

	protected void damageArmor(float value) {
	}

	protected float applyArmorDamage(DamageSource source, float damage) {
		if (!source.bypassesArmor()) {
			int i = 25 - this.getArmorProtectionValue();
			float f = damage * (float)i;
			this.damageArmor(damage);
			damage = f / 25.0F;
		}

		return damage;
	}

	protected float applyEnchantmentsToDamage(DamageSource source, float amount) {
		if (source.isUnblockable()) {
			return amount;
		} else {
			if (this.hasStatusEffect(StatusEffect.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
				int i = (this.getEffectInstance(StatusEffect.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = amount * (float)j;
				amount = f / 25.0F;
			}

			if (amount <= 0.0F) {
				return 0.0F;
			} else {
				int k = EnchantmentHelper.method_3524(this.getArmorStacks(), source);
				if (k > 20) {
					k = 20;
				}

				if (k > 0 && k <= 20) {
					int l = 25 - k;
					float g = amount * (float)l;
					amount = g / 25.0F;
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
		return this.dataTracker.getByte(9);
	}

	public final void setStuckArrows(int arrows) {
		this.dataTracker.setProperty(9, (byte)arrows);
	}

	private int getMiningSpeedMultiplier() {
		if (this.hasStatusEffect(StatusEffect.HASTE)) {
			return 6 - (1 + this.getEffectInstance(StatusEffect.HASTE).getAmplifier()) * 1;
		} else {
			return this.hasStatusEffect(StatusEffect.MINING_FATIGUE) ? 6 + (1 + this.getEffectInstance(StatusEffect.MINING_FATIGUE).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand() {
		if (!this.handSwinging || this.handSwingTicks >= this.getMiningSpeedMultiplier() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.handSwinging = true;
			if (this.world instanceof ServerWorld) {
				((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityAnimationS2CPacket(this, 0));
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 2) {
			this.field_6749 = 1.5F;
			this.timeUntilRegen = this.defaultMaxHealth;
			this.hurtTime = this.maxHurtTime = 10;
			this.knockbackVelocity = 0.0F;
			String string = this.getHurtSound();
			if (string != null) {
				this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.damage(DamageSource.GENERIC, 0.0F);
		} else if (status == 3) {
			String string2 = this.getDeathSound();
			if (string2 != null) {
				this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.setHealth(0.0F);
			this.onKilled(DamageSource.GENERIC);
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

	public abstract ItemStack getStackInHand();

	public abstract ItemStack getMainSlot(int slot);

	public abstract ItemStack getArmorSlot(int i);

	@Override
	public abstract void setArmorSlot(int armorSlot, ItemStack item);

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

	@Override
	public abstract ItemStack[] getArmorStacks();

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
		double d = entity.x;
		double e = entity.getBoundingBox().minY + (double)entity.height;
		double f = entity.z;
		int i = 1;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k < i; k++) {
				if (j != 0 || k != 0) {
					int l = (int)(this.x + (double)j);
					int m = (int)(this.z + (double)k);
					Box box = this.getBoundingBox().offset((double)j, 1.0, (double)k);
					if (this.world.method_3608(box).isEmpty()) {
						if (World.isOpaque(this.world, new BlockPos(l, (int)this.y, m))) {
							this.refreshPositionAfterTeleport(this.x + (double)j, this.y + 1.0, this.z + (double)k);
							return;
						}

						if (World.isOpaque(this.world, new BlockPos(l, (int)this.y - 1, m))
							|| this.world.getBlockState(new BlockPos(l, (int)this.y - 1, m)).getBlock().getMaterial() == Material.WATER) {
							d = this.x + (double)j;
							e = this.y + 1.0;
							f = this.z + (double)k;
						}
					}
				}
			}
		}

		this.refreshPositionAfterTeleport(d, e, f);
	}

	@Override
	public boolean shouldRenderName() {
		return false;
	}

	protected float getJumpVelocity() {
		return 0.42F;
	}

	protected void jump() {
		this.velocityY = (double)this.getJumpVelocity();
		if (this.hasStatusEffect(StatusEffect.JUMP_BOOST)) {
			this.velocityY = this.velocityY + (double)((float)(this.getEffectInstance(StatusEffect.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
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

	public void travel(float f, float g) {
		if (this.canMoveVoluntarily()) {
			if (!this.isTouchingWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
				if (!this.isTouchingLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
					float k = 0.91F;
					if (this.onGround) {
						k = this.world
								.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
								.getBlock()
								.slipperiness
							* 0.91F;
					}

					float l = 0.16277136F / (k * k * k);
					float m;
					if (this.onGround) {
						m = this.getMovementSpeed() * l;
					} else {
						m = this.flyingSpeed;
					}

					this.updateVelocity(f, g, m);
					k = 0.91F;
					if (this.onGround) {
						k = this.world
								.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
								.getBlock()
								.slipperiness
							* 0.91F;
					}

					if (this.isClimbing()) {
						float o = 0.15F;
						this.velocityX = MathHelper.clamp(this.velocityX, (double)(-o), (double)o);
						this.velocityZ = MathHelper.clamp(this.velocityZ, (double)(-o), (double)o);
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

					if (this.world.isClient
						&& (!this.world.blockExists(new BlockPos((int)this.x, 0, (int)this.z)) || !this.world.getChunk(new BlockPos((int)this.x, 0, (int)this.z)).isLoaded())) {
						if (this.y > 0.0) {
							this.velocityY = -0.1;
						} else {
							this.velocityY = 0.0;
						}
					} else {
						this.velocityY -= 0.08;
					}

					this.velocityY *= 0.98F;
					this.velocityX *= (double)k;
					this.velocityZ *= (double)k;
				} else {
					double e = this.y;
					this.updateVelocity(f, g, 0.02F);
					this.move(this.velocityX, this.velocityY, this.velocityZ);
					this.velocityX *= 0.5;
					this.velocityY *= 0.5;
					this.velocityZ *= 0.5;
					this.velocityY -= 0.02;
					if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + e, this.velocityZ)) {
						this.velocityY = 0.3F;
					}
				}
			} else {
				double d = this.y;
				float h = 0.8F;
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
					i += (this.getMovementSpeed() * 1.0F - i) * j / 3.0F;
				}

				this.updateVelocity(f, g, i);
				this.move(this.velocityX, this.velocityY, this.velocityZ);
				this.velocityX *= (double)h;
				this.velocityY *= 0.8F;
				this.velocityZ *= (double)h;
				this.velocityY -= 0.02;
				if (this.horizontalCollision && this.doesNotCollide(this.velocityX, this.velocityY + 0.6F - this.y + d, this.velocityZ)) {
					this.velocityY = 0.3F;
				}
			}
		}

		this.field_6748 = this.field_6749;
		double p = this.x - this.prevX;
		double q = this.z - this.prevZ;
		float r = MathHelper.sqrt(p * p + q * q) * 4.0F;
		if (r > 1.0F) {
			r = 1.0F;
		}

		this.field_6749 = this.field_6749 + (r - this.field_6749) * 0.4F;
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

			for (int j = 0; j < 5; j++) {
				ItemStack itemStack = this.equippedItems[j];
				ItemStack itemStack2 = this.getMainSlot(j);
				if (!ItemStack.equalsAll(itemStack2, itemStack)) {
					((ServerWorld)this.world).getEntityTracker().sendToOtherTrackingEntities(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), j, itemStack2));
					if (itemStack != null) {
						this.attributeContainer.removeAll(itemStack.getAttributes());
					}

					if (itemStack2 != null) {
						this.attributeContainer.replaceAll(itemStack2.getAttributes());
					}

					this.equippedItems[j] = itemStack2 == null ? null : itemStack2.copy();
				}
			}

			if (this.ticksAlive % 20 == 0) {
				this.getDamageTracker().update();
			}
		}

		this.tickMovement();
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float f = (float)(d * d + e * e);
		float g = this.bodyYaw;
		float h = 0.0F;
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		float k = 0.0F;
		if (f > 0.0025000002F) {
			k = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			g = (float)MathHelper.atan2(e, d) * 180.0F / (float) Math.PI - 90.0F;
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			k = 0.0F;
		}

		this.stepBobbingAmount = this.stepBobbingAmount + (k - this.stepBobbingAmount) * 0.3F;
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

		if (this.bodyTrackingIncrements > 0) {
			double d = this.x + (this.serverX - this.x) / (double)this.bodyTrackingIncrements;
			double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
			double f = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
			double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.bodyTrackingIncrements);
			this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		} else if (!this.canMoveVoluntarily()) {
			this.velocityX *= 0.98;
			this.velocityY *= 0.98;
			this.velocityZ *= 0.98;
		}

		if (Math.abs(this.velocityX) < 0.005) {
			this.velocityX = 0.0;
		}

		if (Math.abs(this.velocityY) < 0.005) {
			this.velocityY = 0.0;
		}

		if (Math.abs(this.velocityZ) < 0.005) {
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
		this.travel(this.sidewaysSpeed, this.forwardSpeed);
		this.world.profiler.pop();
		this.world.profiler.push("push");
		if (!this.world.isClient) {
			this.tickCramming();
		}

		this.world.profiler.pop();
	}

	protected void tickNewAi() {
	}

	protected void tickCramming() {
		List<Entity> list = this.world
			.getEntitiesIn(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntityPredicate.EXCEPT_SPECTATOR, new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					return entity.isPushable();
				}
			}));
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
	public void startRiding(Entity entity) {
		if (this.vehicle != null && entity == null) {
			if (!this.world.isClient) {
				this.method_6152(this.vehicle);
			}

			if (this.vehicle != null) {
				this.vehicle.rider = null;
			}

			this.vehicle = null;
		} else {
			super.startRiding(entity);
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
				.rayTrace(new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z), new Vec3d(entity.x, entity.y + (double)entity.getEyeHeight(), entity.z))
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

	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getUuid().toString());
	}

	public boolean isInSameTeam(LivingEntity entity) {
		return this.isInTeam(entity.getScoreboardTeam());
	}

	public boolean isInTeam(AbstractTeam team) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
	}

	public void enterCombat() {
	}

	public void endCombat() {
	}

	protected void markEffectsDirty() {
		this.effectsChanged = true;
	}
}
