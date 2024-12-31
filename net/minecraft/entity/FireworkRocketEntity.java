package net.minecraft.entity;

import net.minecraft.class_4079;
import net.minecraft.class_4342;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.Sounds;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkRocketEntity extends Entity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> SHOOTER_ENTITY_ID = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int life;
	private int lifeTime;
	private LivingEntity shooter;

	public FireworkRocketEntity(World world) {
		super(EntityType.FIREWORK_ROCKET, world);
		this.setBounds(0.25F, 0.25F);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
		this.dataTracker.startTracking(SHOOTER_ENTITY_ID, 0);
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 4096.0 && !this.wasShotByEntity();
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return super.shouldRender(x, y, z) && !this.wasShotByEntity();
	}

	public FireworkRocketEntity(World world, double d, double e, double f, ItemStack itemStack) {
		super(EntityType.FIREWORK_ROCKET, world);
		this.life = 0;
		this.setBounds(0.25F, 0.25F);
		this.updatePosition(d, e, f);
		int i = 1;
		if (!itemStack.isEmpty() && itemStack.hasNbt()) {
			this.dataTracker.set(ITEM, itemStack.copy());
			i += itemStack.getOrCreateNbtCompound("Fireworks").getByte("Flight");
		}

		this.velocityX = this.random.nextGaussian() * 0.001;
		this.velocityZ = this.random.nextGaussian() * 0.001;
		this.velocityY = 0.05;
		this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	public FireworkRocketEntity(World world, ItemStack itemStack, LivingEntity livingEntity) {
		this(world, livingEntity.x, livingEntity.y, livingEntity.z, itemStack);
		this.dataTracker.set(SHOOTER_ENTITY_ID, livingEntity.getEntityId());
		this.shooter = livingEntity;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void tick() {
		this.prevTickX = this.x;
		this.prevTickY = this.y;
		this.prevTickZ = this.z;
		super.tick();
		if (this.wasShotByEntity()) {
			if (this.shooter == null) {
				Entity entity = this.world.getEntityById(this.dataTracker.get(SHOOTER_ENTITY_ID));
				if (entity instanceof LivingEntity) {
					this.shooter = (LivingEntity)entity;
				}
			}

			if (this.shooter != null) {
				if (this.shooter.method_13055()) {
					Vec3d vec3d = this.shooter.getRotation();
					double d = 1.5;
					double e = 0.1;
					this.shooter.velocityX = this.shooter.velocityX + vec3d.x * 0.1 + (vec3d.x * 1.5 - this.shooter.velocityX) * 0.5;
					this.shooter.velocityY = this.shooter.velocityY + vec3d.y * 0.1 + (vec3d.y * 1.5 - this.shooter.velocityY) * 0.5;
					this.shooter.velocityZ = this.shooter.velocityZ + vec3d.z * 0.1 + (vec3d.z * 1.5 - this.shooter.velocityZ) * 0.5;
				}

				this.updatePosition(this.shooter.x, this.shooter.y, this.shooter.z);
				this.velocityX = this.shooter.velocityX;
				this.velocityY = this.shooter.velocityY;
				this.velocityZ = this.shooter.velocityZ;
			}
		} else {
			this.velocityX *= 1.15;
			this.velocityZ *= 1.15;
			this.velocityY += 0.04;
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
		}

		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);

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
		if (this.life == 0 && !this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
		}

		this.life++;
		if (this.world.isClient && this.life % 2 < 2) {
			this.world
				.method_16343(
					class_4342.field_21397, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.velocityY * 0.5, this.random.nextGaussian() * 0.05
				);
		}

		if (!this.world.isClient && this.life > this.lifeTime) {
			this.world.sendEntityStatus(this, (byte)17);
			this.explode();
			this.remove();
		}
	}

	private void explode() {
		float f = 0.0F;
		ItemStack itemStack = this.dataTracker.get(ITEM);
		NbtCompound nbtCompound = itemStack.isEmpty() ? null : itemStack.getNbtCompound("Fireworks");
		NbtList nbtList = nbtCompound != null ? nbtCompound.getList("Explosions", 10) : null;
		if (nbtList != null && !nbtList.isEmpty()) {
			f = (float)(5 + nbtList.size() * 2);
		}

		if (f > 0.0F) {
			if (this.shooter != null) {
				this.shooter.damage(DamageSource.FIREWORK, (float)(5 + nbtList.size() * 2));
			}

			double d = 5.0;
			Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

			for (LivingEntity livingEntity : this.world.getEntitiesInBox(LivingEntity.class, this.getBoundingBox().expand(5.0))) {
				if (livingEntity != this.shooter && !(this.squaredDistanceTo(livingEntity) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						BlockHitResult blockHitResult = this.world
							.method_3615(
								vec3d, new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.height * 0.5 * (double)i, livingEntity.z), class_4079.NEVER, true, false
							);
						if (blockHitResult == null || blockHitResult.type == BlockHitResult.Type.MISS) {
							bl = true;
							break;
						}
					}

					if (bl) {
						float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
						livingEntity.damage(DamageSource.FIREWORK, g);
					}
				}
			}
		}
	}

	public boolean wasShotByEntity() {
		return this.dataTracker.get(SHOOTER_ENTITY_ID) > 0;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 17 && this.world.isClient) {
			ItemStack itemStack = this.dataTracker.get(ITEM);
			NbtCompound nbtCompound = itemStack.isEmpty() ? null : itemStack.getNbtCompound("Fireworks");
			this.world.addFireworkParticle(this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ, nbtCompound);
		}

		super.handleStatus(status);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("Life", this.life);
		nbt.putInt("LifeTime", this.lifeTime);
		ItemStack itemStack = this.dataTracker.get(ITEM);
		if (!itemStack.isEmpty()) {
			nbt.put("FireworksItem", itemStack.toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.life = nbt.getInt("Life");
		this.lifeTime = nbt.getInt("LifeTime");
		ItemStack itemStack = ItemStack.from(nbt.getCompound("FireworksItem"));
		if (!itemStack.isEmpty()) {
			this.dataTracker.set(ITEM, itemStack);
		}
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
