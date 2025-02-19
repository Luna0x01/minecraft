package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class PersistentProjectileEntity extends ProjectileEntity {
	private static final double field_30657 = 2.0;
	private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CRITICAL_FLAG = 1;
	private static final int NO_CLIP_FLAG = 2;
	private static final int SHOT_FROM_CROSSBOW_FLAG = 4;
	@Nullable
	private BlockState inBlockState;
	protected boolean inGround;
	protected int inGroundTime;
	public PersistentProjectileEntity.PickupPermission pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
	public int shake;
	private int life;
	private double damage = 2.0;
	private int punch;
	private SoundEvent sound = this.getHitSound();
	@Nullable
	private IntOpenHashSet piercedEntities;
	@Nullable
	private List<Entity> piercingKilledEntities;

	protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.setPosition(x, y, z);
	}

	protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
		this(type, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world);
		this.setOwner(owner);
		if (owner instanceof PlayerEntity) {
			this.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
		}
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 10.0;
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(PROJECTILE_FLAGS, (byte)0);
		this.dataTracker.startTracking(PIERCE_LEVEL, (byte)0);
	}

	@Override
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		super.setVelocity(x, y, z, speed, divergence);
		this.life = 0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		super.setVelocityClient(x, y, z);
		this.life = 0;
	}

	@Override
	public void tick() {
		super.tick();
		boolean bl = this.isNoClip();
		Vec3d vec3d = this.getVelocity();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			double d = vec3d.horizontalLength();
			this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
			this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
			this.prevYaw = this.getYaw();
			this.prevPitch = this.getPitch();
		}

		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
			if (!voxelShape.isEmpty()) {
				Vec3d vec3d2 = this.getPos();

				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(vec3d2)) {
						this.inGround = true;
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
			this.extinguish();
		}

		if (this.inGround && !bl) {
			if (this.inBlockState != blockState && this.shouldFall()) {
				this.fall();
			} else if (!this.world.isClient) {
				this.age();
			}

			this.inGroundTime++;
		} else {
			this.inGroundTime = 0;
			Vec3d vec3d3 = this.getPos();
			Vec3d vec3d4 = vec3d3.add(vec3d);
			HitResult hitResult = this.world.raycast(new RaycastContext(vec3d3, vec3d4, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d4 = hitResult.getPos();
			}

			while (!this.isRemoved()) {
				EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d4);
				if (entityHitResult != null) {
					hitResult = entityHitResult;
				}

				if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult)hitResult).getEntity();
					Entity entity2 = this.getOwner();
					if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
						hitResult = null;
						entityHitResult = null;
					}
				}

				if (hitResult != null && !bl) {
					this.onCollision(hitResult);
					this.velocityDirty = true;
				}

				if (entityHitResult == null || this.getPierceLevel() <= 0) {
					break;
				}

				hitResult = null;
			}

			vec3d = this.getVelocity();
			double e = vec3d.x;
			double f = vec3d.y;
			double g = vec3d.z;
			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.world
						.addParticle(
							ParticleTypes.CRIT, this.getX() + e * (double)i / 4.0, this.getY() + f * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -e, -f + 0.2, -g
						);
				}
			}

			double h = this.getX() + e;
			double j = this.getY() + f;
			double k = this.getZ() + g;
			double l = vec3d.horizontalLength();
			if (bl) {
				this.setYaw((float)(MathHelper.atan2(-e, -g) * 180.0F / (float)Math.PI));
			} else {
				this.setYaw((float)(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI));
			}

			this.setPitch((float)(MathHelper.atan2(f, l) * 180.0F / (float)Math.PI));
			this.setPitch(updateRotation(this.prevPitch, this.getPitch()));
			this.setYaw(updateRotation(this.prevYaw, this.getYaw()));
			float m = 0.99F;
			float n = 0.05F;
			if (this.isTouchingWater()) {
				for (int o = 0; o < 4; o++) {
					float p = 0.25F;
					this.world.addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
				}

				m = this.getDragInWater();
			}

			this.setVelocity(vec3d.multiply((double)m));
			if (!this.hasNoGravity() && !bl) {
				Vec3d vec3d5 = this.getVelocity();
				this.setVelocity(vec3d5.x, vec3d5.y - 0.05F, vec3d5.z);
			}

			this.setPosition(h, j, k);
			this.checkBlockCollision();
		}
	}

	private boolean shouldFall() {
		return this.inGround && this.world.isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
	}

	private void fall() {
		this.inGround = false;
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
		this.life = 0;
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		super.move(movementType, movement);
		if (movementType != MovementType.SELF && this.shouldFall()) {
			this.fall();
		}
	}

	protected void age() {
		this.life++;
		if (this.life >= 1200) {
			this.discard();
		}
	}

	private void clearPiercingStatus() {
		if (this.piercingKilledEntities != null) {
			this.piercingKilledEntities.clear();
		}

		if (this.piercedEntities != null) {
			this.piercedEntities.clear();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		float f = (float)this.getVelocity().length();
		int i = MathHelper.ceil(MathHelper.clamp((double)f * this.damage, 0.0, 2.147483647E9));
		if (this.getPierceLevel() > 0) {
			if (this.piercedEntities == null) {
				this.piercedEntities = new IntOpenHashSet(5);
			}

			if (this.piercingKilledEntities == null) {
				this.piercingKilledEntities = Lists.newArrayListWithCapacity(5);
			}

			if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
				this.discard();
				return;
			}

			this.piercedEntities.add(entity.getId());
		}

		if (this.isCritical()) {
			long l = (long)this.random.nextInt(i / 2 + 2);
			i = (int)Math.min(l + (long)i, 2147483647L);
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource;
		if (entity2 == null) {
			damageSource = DamageSource.arrow(this, this);
		} else {
			damageSource = DamageSource.arrow(this, entity2);
			if (entity2 instanceof LivingEntity) {
				((LivingEntity)entity2).onAttacking(entity);
			}
		}

		boolean bl = entity.getType() == EntityType.ENDERMAN;
		int j = entity.getFireTicks();
		if (this.isOnFire() && !bl) {
			entity.setOnFireFor(5);
		}

		if (entity.damage(damageSource, (float)i)) {
			if (bl) {
				return;
			}

			if (entity instanceof LivingEntity livingEntity) {
				if (!this.world.isClient && this.getPierceLevel() <= 0) {
					livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
				}

				if (this.punch > 0) {
					Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.punch * 0.6);
					if (vec3d.lengthSquared() > 0.0) {
						livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
					}
				}

				if (!this.world.isClient && entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
				}

				this.onHit(livingEntity);
				if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
					((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
				}

				if (!entity.isAlive() && this.piercingKilledEntities != null) {
					this.piercingKilledEntities.add(livingEntity);
				}

				if (!this.world.isClient && entity2 instanceof ServerPlayerEntity serverPlayerEntity) {
					if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
						Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, this.piercingKilledEntities);
					} else if (!entity.isAlive() && this.isShotFromCrossbow()) {
						Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity));
					}
				}
			}

			this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0) {
				this.discard();
			}
		} else {
			entity.setFireTicks(j);
			this.setVelocity(this.getVelocity().multiply(-0.1));
			this.setYaw(this.getYaw() + 180.0F);
			this.prevYaw += 180.0F;
			if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
		super.onBlockHit(blockHitResult);
		Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
		this.setVelocity(vec3d);
		Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
		this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
		this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
		this.inGround = true;
		this.shake = 7;
		this.setCritical(false);
		this.setPierceLevel((byte)0);
		this.setSound(SoundEvents.ENTITY_ARROW_HIT);
		this.setShotFromCrossbow(false);
		this.clearPiercingStatus();
	}

	protected SoundEvent getHitSound() {
		return SoundEvents.ENTITY_ARROW_HIT;
	}

	protected final SoundEvent getSound() {
		return this.sound;
	}

	protected void onHit(LivingEntity target) {
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return ProjectileUtil.getEntityCollision(
			this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
		);
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putShort("life", (short)this.life);
		if (this.inBlockState != null) {
			nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
		}

		nbt.putByte("shake", (byte)this.shake);
		nbt.putBoolean("inGround", this.inGround);
		nbt.putByte("pickup", (byte)this.pickupType.ordinal());
		nbt.putDouble("damage", this.damage);
		nbt.putBoolean("crit", this.isCritical());
		nbt.putByte("PierceLevel", this.getPierceLevel());
		nbt.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
		nbt.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.life = nbt.getShort("life");
		if (nbt.contains("inBlockState", 10)) {
			this.inBlockState = NbtHelper.toBlockState(nbt.getCompound("inBlockState"));
		}

		this.shake = nbt.getByte("shake") & 255;
		this.inGround = nbt.getBoolean("inGround");
		if (nbt.contains("damage", 99)) {
			this.damage = nbt.getDouble("damage");
		}

		this.pickupType = PersistentProjectileEntity.PickupPermission.fromOrdinal(nbt.getByte("pickup"));
		this.setCritical(nbt.getBoolean("crit"));
		this.setPierceLevel(nbt.getByte("PierceLevel"));
		if (nbt.contains("SoundEvent", 8)) {
			this.sound = (SoundEvent)Registry.SOUND_EVENT.getOrEmpty(new Identifier(nbt.getString("SoundEvent"))).orElse(this.getHitSound());
		}

		this.setShotFromCrossbow(nbt.getBoolean("ShotFromCrossbow"));
	}

	@Override
	public void setOwner(@Nullable Entity entity) {
		super.setOwner(entity);
		if (entity instanceof PlayerEntity) {
			this.pickupType = ((PlayerEntity)entity).getAbilities().creativeMode
				? PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
				: PersistentProjectileEntity.PickupPermission.ALLOWED;
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
			if (this.tryPickup(player)) {
				player.sendPickup(this, 1);
				this.discard();
			}
		}
	}

	protected boolean tryPickup(PlayerEntity player) {
		switch (this.pickupType) {
			case ALLOWED:
				return player.getInventory().insertStack(this.asItemStack());
			case CREATIVE_ONLY:
				return player.getAbilities().creativeMode;
			default:
				return false;
		}
	}

	protected abstract ItemStack asItemStack();

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setPunch(int punch) {
		this.punch = punch;
	}

	public int getPunch() {
		return this.punch;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.13F;
	}

	public void setCritical(boolean critical) {
		this.setProjectileFlag(1, critical);
	}

	public void setPierceLevel(byte level) {
		this.dataTracker.set(PIERCE_LEVEL, level);
	}

	private void setProjectileFlag(int index, boolean flag) {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		if (flag) {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | index));
		} else {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~index));
		}
	}

	public boolean isCritical() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 1) != 0;
	}

	public boolean isShotFromCrossbow() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 4) != 0;
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	public void applyEnchantmentEffects(LivingEntity entity, float damageModifier) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, entity);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, entity);
		this.setDamage((double)(damageModifier * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getId() * 0.11F));
		if (i > 0) {
			this.setDamage(this.getDamage() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			this.setPunch(j);
		}

		if (EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, entity) > 0) {
			this.setOnFireFor(100);
		}
	}

	protected float getDragInWater() {
		return 0.6F;
	}

	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
		this.setProjectileFlag(2, noClip);
	}

	public boolean isNoClip() {
		return !this.world.isClient ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	public void setShotFromCrossbow(boolean shotFromCrossbow) {
		this.setProjectileFlag(4, shotFromCrossbow);
	}

	public static enum PickupPermission {
		DISALLOWED,
		ALLOWED,
		CREATIVE_ONLY;

		public static PersistentProjectileEntity.PickupPermission fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal > values().length) {
				ordinal = 0;
			}

			return values()[ordinal];
		}
	}
}
