package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends Entity {
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	private Block inBlock;
	private boolean inGround;
	public LivingEntity target;
	private int life;
	private int field_4041;
	public double powerX;
	public double powerY;
	public double powerZ;

	public ExplosiveProjectileEntity(World world) {
		super(world);
		this.setBounds(1.0F, 1.0F);
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

	public ExplosiveProjectileEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(world);
		this.setBounds(1.0F, 1.0F);
		this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.updatePosition(d, e, f);
		double j = (double)MathHelper.sqrt(g * g + h * h + i * i);
		this.powerX = g / j * 0.1;
		this.powerY = h / j * 0.1;
		this.powerZ = i / j * 0.1;
	}

	public ExplosiveProjectileEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(world);
		this.target = livingEntity;
		this.setBounds(1.0F, 1.0F);
		this.refreshPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.updatePosition(this.x, this.y, this.z);
		this.velocityX = this.velocityY = this.velocityZ = 0.0;
		d += this.random.nextGaussian() * 0.4;
		e += this.random.nextGaussian() * 0.4;
		f += this.random.nextGaussian() * 0.4;
		double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
		this.powerX = d / g * 0.1;
		this.powerY = e / g * 0.1;
		this.powerZ = f / g * 0.1;
	}

	@Override
	public void tick() {
		if (this.world.isClient || (this.target == null || !this.target.removed) && this.world.blockExists(new BlockPos(this))) {
			super.tick();
			this.setOnFireFor(1);
			if (this.inGround) {
				if (this.world.getBlockState(new BlockPos(this.blockX, this.blockY, this.blockZ)).getBlock() == this.inBlock) {
					this.life++;
					if (this.life == 600) {
						this.remove();
					}

					return;
				}

				this.inGround = false;
				this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
				this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
				this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
				this.life = 0;
				this.field_4041 = 0;
			} else {
				this.field_4041++;
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
			List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
			double d = 0.0;

			for (int i = 0; i < list.size(); i++) {
				Entity entity2 = (Entity)list.get(i);
				if (entity2.collides() && (!entity2.isPartOf(this.target) || this.field_4041 >= 25)) {
					float f = 0.3F;
					Box box = entity2.getBoundingBox().expand((double)f, (double)f, (double)f);
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

			if (entity != null) {
				blockHitResult = new BlockHitResult(entity);
			}

			if (blockHitResult != null) {
				this.onEntityHit(blockHitResult);
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.yaw = (float)(MathHelper.atan2(this.velocityZ, this.velocityX) * 180.0 / (float) Math.PI) + 90.0F;
			this.pitch = (float)(MathHelper.atan2((double)g, this.velocityY) * 180.0 / (float) Math.PI) - 90.0F;

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
			float h = this.getDrag();
			if (this.isTouchingWater()) {
				for (int j = 0; j < 4; j++) {
					float k = 0.25F;
					this.world
						.addParticle(
							ParticleType.BUBBLE,
							this.x - this.velocityX * (double)k,
							this.y - this.velocityY * (double)k,
							this.z - this.velocityZ * (double)k,
							this.velocityX,
							this.velocityY,
							this.velocityZ
						);
				}

				h = 0.8F;
			}

			this.velocityX = this.velocityX + this.powerX;
			this.velocityY = this.velocityY + this.powerY;
			this.velocityZ = this.velocityZ + this.powerZ;
			this.velocityX *= (double)h;
			this.velocityY *= (double)h;
			this.velocityZ *= (double)h;
			this.world.addParticle(ParticleType.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
			this.updatePosition(this.x, this.y, this.z);
		} else {
			this.remove();
		}
	}

	protected float getDrag() {
		return 0.95F;
	}

	protected abstract void onEntityHit(BlockHitResult hitResult);

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("xTile", (short)this.blockX);
		nbt.putShort("yTile", (short)this.blockY);
		nbt.putShort("zTile", (short)this.blockZ);
		Identifier identifier = Block.REGISTRY.getIdentifier(this.inBlock);
		nbt.putString("inTile", identifier == null ? "" : identifier.toString());
		nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		nbt.put("direction", this.toListNbt(new double[]{this.velocityX, this.velocityY, this.velocityZ}));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.blockX = nbt.getShort("xTile");
		this.blockY = nbt.getShort("yTile");
		this.blockZ = nbt.getShort("zTile");
		if (nbt.contains("inTile", 8)) {
			this.inBlock = Block.get(nbt.getString("inTile"));
		} else {
			this.inBlock = Block.getById(nbt.getByte("inTile") & 255);
		}

		this.inGround = nbt.getByte("inGround") == 1;
		if (nbt.contains("direction", 9)) {
			NbtList nbtList = nbt.getList("direction", 6);
			this.velocityX = nbtList.getDouble(0);
			this.velocityY = nbtList.getDouble(1);
			this.velocityZ = nbtList.getDouble(2);
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
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}
}
