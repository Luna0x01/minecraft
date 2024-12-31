package net.minecraft.entity.projectile;

import net.minecraft.class_4342;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends Entity {
	public LivingEntity target;
	private int life;
	private int field_4041;
	public double powerX;
	public double powerY;
	public double powerZ;

	protected ExplosiveProjectileEntity(EntityType<?> entityType, World world, float f, float g) {
		super(entityType, world);
		this.setBounds(f, g);
	}

	public ExplosiveProjectileEntity(EntityType<?> entityType, double d, double e, double f, double g, double h, double i, World world, float j, float k) {
		this(entityType, world, j, k);
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.updatePosition(d, e, f);
		double l = (double)MathHelper.sqrt(g * g + h * h + i * i);
		this.powerX = g / l * 0.1;
		this.powerY = h / l * 0.1;
		this.powerZ = i / l * 0.1;
	}

	public ExplosiveProjectileEntity(EntityType<?> entityType, LivingEntity livingEntity, double d, double e, double f, World world, float g, float h) {
		this(entityType, world, g, h);
		this.target = livingEntity;
		this.refreshPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.updatePosition(this.x, this.y, this.z);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		d += this.random.nextGaussian() * 0.4;
		e += this.random.nextGaussian() * 0.4;
		f += this.random.nextGaussian() * 0.4;
		double i = (double)MathHelper.sqrt(d * d + e * e + f * f);
		this.powerX = d / i * 0.1;
		this.powerY = e / i * 0.1;
		this.powerZ = f / i * 0.1;
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

	@Override
	public void tick() {
		if (this.world.isClient || (this.target == null || !this.target.removed) && this.world.method_16359(new BlockPos(this))) {
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1);
			}

			this.field_4041++;
			BlockHitResult blockHitResult = ProjectileUtil.method_13286(this, true, this.field_4041 >= 25, this.target);
			if (blockHitResult != null) {
				this.onEntityHit(blockHitResult);
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			float f = this.getDrag();
			if (this.isTouchingWater()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
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

				f = 0.8F;
			}

			this.velocityX = this.velocityX + this.powerX;
			this.velocityY = this.velocityY + this.powerY;
			this.velocityZ = this.velocityZ + this.powerZ;
			this.velocityX *= (double)f;
			this.velocityY *= (double)f;
			this.velocityZ *= (double)f;
			this.world.method_16343(this.method_13283(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
			this.updatePosition(this.x, this.y, this.z);
		} else {
			this.remove();
		}
	}

	protected boolean isBurning() {
		return true;
	}

	protected ParticleEffect method_13283() {
		return class_4342.field_21363;
	}

	protected float getDrag() {
		return 0.95F;
	}

	protected abstract void onEntityHit(BlockHitResult hitResult);

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("direction", this.toListNbt(new double[]{this.velocityX, this.velocityY, this.velocityZ}));
		nbt.put("power", this.toListNbt(new double[]{this.powerX, this.powerY, this.powerZ}));
		nbt.putInt("life", this.life);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("power", 9)) {
			NbtList nbtList = nbt.getList("power", 6);
			if (nbtList.size() == 3) {
				this.powerX = nbtList.getDouble(0);
				this.powerY = nbtList.getDouble(1);
				this.powerZ = nbtList.getDouble(2);
			}
		}

		this.life = nbt.getInt("life");
		if (nbt.contains("direction", 9) && nbt.getList("direction", 6).size() == 3) {
			NbtList nbtList2 = nbt.getList("direction", 6);
			this.velocityX = nbtList2.getDouble(0);
			this.velocityY = nbtList2.getDouble(1);
			this.velocityZ = nbtList2.getDouble(2);
		} else {
			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public float getTargetingMargin() {
		return 1.0F;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			if (source.getAttacker() != null) {
				Vec3d vec3d = source.getAttacker().getRotation();
				if (vec3d != null) {
					this.velocityX = vec3d.x;
					this.velocityY = vec3d.y;
					this.velocityZ = vec3d.z;
					this.powerX = this.velocityX * 0.1;
					this.powerY = this.velocityY * 0.1;
					this.powerZ = this.velocityZ * 0.1;
				}

				if (source.getAttacker() instanceof LivingEntity) {
					this.target = (LivingEntity)source.getAttacker();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}
}
