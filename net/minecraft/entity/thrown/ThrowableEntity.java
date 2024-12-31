package net.minecraft.entity.thrown;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ThrowableEntity extends Entity implements Projectile {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	protected boolean inGround;
	public int shake;
	protected LivingEntity field_6932;
	private UUID field_17098;
	public Entity field_14820;
	private int field_14819;

	protected ThrowableEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setBounds(0.25F, 0.25F);
	}

	protected ThrowableEntity(EntityType<?> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.updatePosition(d, e, f);
	}

	protected ThrowableEntity(EntityType<?> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() - 0.1F, livingEntity.z, world);
		this.field_6932 = livingEntity;
		this.field_17098 = livingEntity.getUuid();
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity((double)f, (double)g, (double)h, modifierZ, modifierXYZ);
		this.velocityX = this.velocityX + user.velocityX;
		this.velocityZ = this.velocityZ + user.velocityZ;
		if (!user.onGround) {
			this.velocityY = this.velocityY + user.velocityY;
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
		if (this.shake > 0) {
			this.shake--;
		}

		if (this.inGround) {
			this.inGround = false;
			this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
			this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
			this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
		}

		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		BlockHitResult blockHitResult = this.world.rayTrace(vec3d, vec3d2);
		vec3d = new Vec3d(this.x, this.y, this.z);
		vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		if (blockHitResult != null) {
			vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
		}

		Entity entity = null;
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0));
		double d = 0.0;
		boolean bl = false;

		for (int i = 0; i < list.size(); i++) {
			Entity entity2 = (Entity)list.get(i);
			if (entity2.collides()) {
				if (entity2 == this.field_14820) {
					bl = true;
				} else if (this.field_6932 != null && this.ticksAlive < 2 && this.field_14820 == null) {
					this.field_14820 = entity2;
					bl = true;
				} else {
					bl = false;
					Box box = entity2.getBoundingBox().expand(0.3F);
					BlockHitResult blockHitResult2 = box.method_585(vec3d, vec3d2);
					if (blockHitResult2 != null) {
						double e = vec3d.squaredDistanceTo(blockHitResult2.pos);
						if (e < d || d == 0.0) {
							entity = entity2;
							d = e;
						}
					}
				}
			}
		}

		if (this.field_14820 != null) {
			if (bl) {
				this.field_14819 = 2;
			} else if (this.field_14819-- <= 0) {
				this.field_14820 = null;
			}
		}

		if (entity != null) {
			blockHitResult = new BlockHitResult(entity);
		}

		if (blockHitResult != null) {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK && this.world.getBlockState(blockHitResult.getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
				this.setInNetherPortal(blockHitResult.getBlockPos());
			} else {
				this.onCollision(blockHitResult);
			}
		}

		this.x = this.x + this.velocityX;
		this.y = this.y + this.velocityY;
		this.z = this.z + this.velocityZ;
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
		float g = 0.99F;
		float h = this.getGravity();
		if (this.isTouchingWater()) {
			for (int j = 0; j < 4; j++) {
				float k = 0.25F;
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

			g = 0.8F;
		}

		this.velocityX *= (double)g;
		this.velocityY *= (double)g;
		this.velocityZ *= (double)g;
		if (!this.hasNoGravity()) {
			this.velocityY -= (double)h;
		}

		this.updatePosition(this.x, this.y, this.z);
	}

	protected float getGravity() {
		return 0.03F;
	}

	protected abstract void onCollision(BlockHitResult result);

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putInt("xTile", this.blockX);
		nbt.putInt("yTile", this.blockY);
		nbt.putInt("zTile", this.blockZ);
		nbt.putByte("shake", (byte)this.shake);
		nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		if (this.field_17098 != null) {
			nbt.put("owner", NbtHelper.fromUuid(this.field_17098));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.blockX = nbt.getInt("xTile");
		this.blockY = nbt.getInt("yTile");
		this.blockZ = nbt.getInt("zTile");
		this.shake = nbt.getByte("shake") & 255;
		this.inGround = nbt.getByte("inGround") == 1;
		this.field_6932 = null;
		if (nbt.contains("owner", 10)) {
			this.field_17098 = NbtHelper.toUuid(nbt.getCompound("owner"));
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.field_6932 == null && this.field_17098 != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.field_17098);
			if (entity instanceof LivingEntity) {
				this.field_6932 = (LivingEntity)entity;
			} else {
				this.field_17098 = null;
			}
		}

		return this.field_6932;
	}
}
