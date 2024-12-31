package net.minecraft.entity.projectile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_4079;
import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.World;

public abstract class AbstractArrowEntity extends Entity implements Projectile {
	private static final Predicate<Entity> field_17097 = EntityPredicate.field_16705.and(EntityPredicate.field_16700.and(Entity::collides));
	private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(AbstractArrowEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> field_17094 = DataTracker.registerData(AbstractArrowEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	@Nullable
	private BlockState field_17095;
	protected boolean inGround;
	protected int inGroundTime;
	public AbstractArrowEntity.PickupPermission pickupType = AbstractArrowEntity.PickupPermission.DISALLOWED;
	public int shake;
	public UUID field_17096;
	private int life;
	private int field_4022;
	private double damage = 2.0;
	private int punch;

	protected AbstractArrowEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setBounds(0.5F, 0.5F);
	}

	protected AbstractArrowEntity(EntityType<?> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.updatePosition(d, e, f);
	}

	protected AbstractArrowEntity(EntityType<?> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() - 0.1F, livingEntity.z, world);
		this.method_15946(livingEntity);
		if (livingEntity instanceof PlayerEntity) {
			this.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
		}
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage() * 10.0;
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(PROJECTILE_FLAGS, (byte)0);
		this.dataTracker.startTracking(field_17094, Optional.empty());
	}

	public void method_13278(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin(f * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		this.velocityX = this.velocityX + entity.velocityX;
		this.velocityZ = this.velocityZ + entity.velocityZ;
		if (!entity.onGround) {
			this.velocityY = this.velocityY + entity.velocityY;
		}
	}

	@Override
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x /= (double)f;
		y /= (double)f;
		z /= (double)f;
		x += this.random.nextGaussian() * 0.0075F * (double)divergence;
		y += this.random.nextGaussian() * 0.0075F * (double)divergence;
		z += this.random.nextGaussian() * 0.0075F * (double)divergence;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		float g = MathHelper.sqrt(x * x + z * z);
		this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(y, (double)g) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.life = 0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			this.life = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		boolean bl = this.method_15953();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}

		BlockPos blockPos = new BlockPos(this.blockX, this.blockY, this.blockZ);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
			if (!voxelShape.isEmpty()) {
				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(new Vec3d(this.x, this.y, this.z))) {
						this.inGround = true;
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.tickFire()) {
			this.extinguish();
		}

		if (this.inGround && !bl) {
			if (this.field_17095 != blockState && this.world.method_16387(null, this.getBoundingBox().expand(0.05))) {
				this.inGround = false;
				this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
				this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
				this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
				this.life = 0;
				this.field_4022 = 0;
			} else {
				this.method_15948();
			}

			this.inGroundTime++;
		} else {
			this.inGroundTime = 0;
			this.field_4022++;
			Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
			Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			BlockHitResult blockHitResult = this.world.method_3615(vec3d, vec3d2, class_4079.NEVER, true, false);
			vec3d = new Vec3d(this.x, this.y, this.z);
			vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			if (blockHitResult != null) {
				vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
			}

			Entity entity = this.getEntityCollision(vec3d, vec3d2);
			if (entity != null) {
				blockHitResult = new BlockHitResult(entity);
			}

			if (blockHitResult != null && blockHitResult.entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)blockHitResult.entity;
				Entity entity2 = this.method_15950();
				if (entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer(playerEntity)) {
					blockHitResult = null;
				}
			}

			if (blockHitResult != null && !bl) {
				this.onHit(blockHitResult);
				this.velocityDirty = true;
			}

			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.world
						.method_16343(
							class_4342.field_21382,
							this.x + this.velocityX * (double)i / 4.0,
							this.y + this.velocityY * (double)i / 4.0,
							this.z + this.velocityZ * (double)i / 4.0,
							-this.velocityX,
							-this.velocityY + 0.2,
							-this.velocityZ
						);
				}
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (bl) {
				this.yaw = (float)(MathHelper.atan2(-this.velocityX, -this.velocityZ) * 180.0F / (float)Math.PI);
			} else {
				this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
			}

			this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)g) * 180.0F / (float)Math.PI);

			while (this.pitch - this.prevPitch < -180.0F) {
				this.prevPitch -= 360.0F;
			}

			while (this.pitch - this.prevPitch >= 180.0F) {
				this.prevPitch += 360.0F;
			}

			while (this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while (this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}

			this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
			this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
			float h = 0.99F;
			float j = 0.05F;
			if (this.isTouchingWater()) {
				for (int k = 0; k < 4; k++) {
					float l = 0.25F;
					this.world
						.method_16343(
							class_4342.field_21379,
							this.x - this.velocityX * 0.25,
							this.y - this.velocityY * 0.25,
							this.z - this.velocityZ * 0.25,
							this.velocityX,
							this.velocityY,
							this.velocityZ
						);
				}

				h = this.method_15952();
			}

			this.velocityX *= (double)h;
			this.velocityY *= (double)h;
			this.velocityZ *= (double)h;
			if (!this.hasNoGravity() && !bl) {
				this.velocityY -= 0.05F;
			}

			this.updatePosition(this.x, this.y, this.z);
			this.checkBlockCollision();
		}
	}

	protected void method_15948() {
		this.life++;
		if (this.life >= 1200) {
			this.remove();
		}
	}

	protected void onHit(BlockHitResult result) {
		if (result.entity != null) {
			this.method_15947(result);
		} else {
			BlockPos blockPos = result.getBlockPos();
			this.blockX = blockPos.getX();
			this.blockY = blockPos.getY();
			this.blockZ = blockPos.getZ();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.field_17095 = blockState;
			this.velocityX = (double)((float)(result.pos.x - this.x));
			this.velocityY = (double)((float)(result.pos.y - this.y));
			this.velocityZ = (double)((float)(result.pos.z - this.z));
			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ) * 20.0F;
			this.x = this.x - this.velocityX / (double)f;
			this.y = this.y - this.velocityY / (double)f;
			this.z = this.z - this.velocityZ / (double)f;
			this.playSound(this.method_15949(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.shake = 7;
			this.setCritical(false);
			if (!blockState.isAir()) {
				this.field_17095.onEntityCollision(this.world, blockPos, this);
			}
		}
	}

	protected void method_15947(BlockHitResult blockHitResult) {
		Entity entity = blockHitResult.entity;
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		int i = MathHelper.ceil((double)f * this.damage);
		if (this.isCritical()) {
			i += this.random.nextInt(i / 2 + 2);
		}

		Entity entity2 = this.method_15950();
		DamageSource damageSource;
		if (entity2 == null) {
			damageSource = DamageSource.arrow(this, this);
		} else {
			damageSource = DamageSource.arrow(this, entity2);
		}

		if (this.isOnFire() && !(entity instanceof EndermanEntity)) {
			entity.setOnFireFor(5);
		}

		if (entity.damage(damageSource, (float)i)) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (!this.world.isClient) {
					livingEntity.setStuckArrows(livingEntity.getStuckArrows() + 1);
				}

				if (this.punch > 0) {
					float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
					if (g > 0.0F) {
						livingEntity.addVelocity(this.velocityX * (double)this.punch * 0.6F / (double)g, 0.1, this.velocityZ * (double)this.punch * 0.6F / (double)g);
					}
				}

				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
				}

				this.onHit(livingEntity);
				if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0F));
				}
			}

			this.playSound(Sounds.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (!(entity instanceof EndermanEntity)) {
				this.remove();
			}
		} else {
			this.velocityX *= -0.1F;
			this.velocityY *= -0.1F;
			this.velocityZ *= -0.1F;
			this.yaw += 180.0F;
			this.prevYaw += 180.0F;
			this.field_4022 = 0;
			if (!this.world.isClient && this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ < 0.001F) {
				if (this.pickupType == AbstractArrowEntity.PickupPermission.ALLOWED) {
					this.dropItem(this.asItemStack(), 0.1F);
				}

				this.remove();
			}
		}
	}

	protected Sound method_15949() {
		return Sounds.ENTITY_ARROW_HIT;
	}

	@Override
	public void move(MovementType type, double movementX, double movementY, double movementZ) {
		super.move(type, movementX, movementY, movementZ);
		if (this.inGround) {
			this.blockX = MathHelper.floor(this.x);
			this.blockY = MathHelper.floor(this.y);
			this.blockZ = MathHelper.floor(this.z);
		}
	}

	protected void onHit(LivingEntity target) {
	}

	@Nullable
	protected Entity getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		Entity entity = null;
		List<Entity> list = this.world.method_16288(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0), field_17097);
		double d = 0.0;

		for (int i = 0; i < list.size(); i++) {
			Entity entity2 = (Entity)list.get(i);
			if (entity2 != this.method_15950() || this.field_4022 >= 5) {
				Box box = entity2.getBoundingBox().expand(0.3F);
				BlockHitResult blockHitResult = box.method_585(currentPosition, nextPosition);
				if (blockHitResult != null) {
					double e = currentPosition.squaredDistanceTo(blockHitResult.pos);
					if (e < d || d == 0.0) {
						entity = entity2;
						d = e;
					}
				}
			}
		}

		return entity;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("xTile", this.blockX);
		nbt.putInt("yTile", this.blockY);
		nbt.putInt("zTile", this.blockZ);
		nbt.putShort("life", (short)this.life);
		if (this.field_17095 != null) {
			nbt.put("inBlockState", NbtHelper.method_20139(this.field_17095));
		}

		nbt.putByte("shake", (byte)this.shake);
		nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		nbt.putByte("pickup", (byte)this.pickupType.ordinal());
		nbt.putDouble("damage", this.damage);
		nbt.putBoolean("crit", this.isCritical());
		if (this.field_17096 != null) {
			nbt.putUuid("OwnerUUID", this.field_17096);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.blockX = nbt.getInt("xTile");
		this.blockY = nbt.getInt("yTile");
		this.blockZ = nbt.getInt("zTile");
		this.life = nbt.getShort("life");
		if (nbt.contains("inBlockState", 10)) {
			this.field_17095 = NbtHelper.toBlockState(nbt.getCompound("inBlockState"));
		}

		this.shake = nbt.getByte("shake") & 255;
		this.inGround = nbt.getByte("inGround") == 1;
		if (nbt.contains("damage", 99)) {
			this.damage = nbt.getDouble("damage");
		}

		if (nbt.contains("pickup", 99)) {
			this.pickupType = AbstractArrowEntity.PickupPermission.fromOrdinal(nbt.getByte("pickup"));
		} else if (nbt.contains("player", 99)) {
			this.pickupType = nbt.getBoolean("player") ? AbstractArrowEntity.PickupPermission.ALLOWED : AbstractArrowEntity.PickupPermission.DISALLOWED;
		}

		this.setCritical(nbt.getBoolean("crit"));
		if (nbt.containsUuid("OwnerUUID")) {
			this.field_17096 = nbt.getUuid("OwnerUUID");
		}
	}

	public void method_15946(@Nullable Entity entity) {
		this.field_17096 = entity == null ? null : entity.getUuid();
	}

	@Nullable
	public Entity method_15950() {
		return this.field_17096 != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntity(this.field_17096) : null;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient && (this.inGround || this.method_15953()) && this.shake <= 0) {
			boolean bl = this.pickupType == AbstractArrowEntity.PickupPermission.ALLOWED
				|| this.pickupType == AbstractArrowEntity.PickupPermission.CREATIVE_ONLY && player.abilities.creativeMode
				|| this.method_15953() && this.method_15950().getUuid() == player.getUuid();
			if (this.pickupType == AbstractArrowEntity.PickupPermission.ALLOWED && !player.inventory.insertStack(this.asItemStack())) {
				bl = false;
			}

			if (bl) {
				player.sendPickup(this, 1);
				this.remove();
			}
		}
	}

	protected abstract ItemStack asItemStack();

	@Override
	protected boolean canClimb() {
		return false;
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

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	public void setCritical(boolean critical) {
		this.method_15945(1, critical);
	}

	private void method_15945(int i, boolean bl) {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		if (bl) {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | i));
		} else {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~i));
		}
	}

	public boolean isCritical() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 1) != 0;
	}

	public void applyEnchantmentEffects(LivingEntity entity, float damageModifier) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, entity);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, entity);
		this.setDamage((double)(damageModifier * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.method_16346().getId() * 0.11F));
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

	protected float method_15952() {
		return 0.6F;
	}

	public void method_15951(boolean bl) {
		this.noClip = bl;
		this.method_15945(2, bl);
	}

	public boolean method_15953() {
		return !this.world.isClient ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	public static enum PickupPermission {
		DISALLOWED,
		ALLOWED,
		CREATIVE_ONLY;

		public static AbstractArrowEntity.PickupPermission fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal > values().length) {
				ordinal = 0;
			}

			return values()[ordinal];
		}
	}
}
