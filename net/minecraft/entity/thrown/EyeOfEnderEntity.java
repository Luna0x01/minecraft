package net.minecraft.entity.thrown;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EyeOfEnderEntity extends Entity {
	private double targetX;
	private double targetY;
	private double targetZ;
	private int lifespan;
	private boolean dropsItem;

	public EyeOfEnderEntity(World world) {
		super(world);
		this.setBounds(0.25F, 0.25F);
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

	public EyeOfEnderEntity(World world, double d, double e, double f) {
		super(world);
		this.lifespan = 0;
		this.setBounds(0.25F, 0.25F);
		this.updatePosition(d, e, f);
	}

	public void initTargetPos(BlockPos pos) {
		double d = (double)pos.getX();
		int i = pos.getY();
		double e = (double)pos.getZ();
		double f = d - this.x;
		double g = e - this.z;
		float h = MathHelper.sqrt(f * f + g * g);
		if (h > 12.0F) {
			this.targetX = this.x + f / (double)h * 12.0;
			this.targetZ = this.z + g / (double)h * 12.0;
			this.targetY = this.y + 8.0;
		} else {
			this.targetX = d;
			this.targetY = (double)i;
			this.targetZ = e;
		}

		this.lifespan = 0;
		this.dropsItem = this.random.nextInt(5) > 0;
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.velocityX = x;
		this.velocityY = y;
		this.velocityZ = z;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.prevYaw = this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
		}
	}

	@Override
	public void tick() {
		this.prevTickX = this.x;
		this.prevTickY = this.y;
		this.prevTickZ = this.z;
		super.tick();
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
		if (!this.world.isClient) {
			double d = this.targetX - this.x;
			double e = this.targetZ - this.z;
			float g = (float)Math.sqrt(d * d + e * e);
			float h = (float)MathHelper.atan2(e, d);
			double i = (double)f + (double)(g - f) * 0.0025;
			if (g < 1.0F) {
				i *= 0.8;
				this.velocityY *= 0.8;
			}

			this.velocityX = Math.cos((double)h) * i;
			this.velocityZ = Math.sin((double)h) * i;
			if (this.y < this.targetY) {
				this.velocityY = this.velocityY + (1.0 - this.velocityY) * 0.015F;
			} else {
				this.velocityY = this.velocityY + (-1.0 - this.velocityY) * 0.015F;
			}
		}

		float j = 0.25F;
		if (this.isTouchingWater()) {
			for (int k = 0; k < 4; k++) {
				this.world
					.addParticle(
						ParticleType.BUBBLE,
						this.x - this.velocityX * (double)j,
						this.y - this.velocityY * (double)j,
						this.z - this.velocityZ * (double)j,
						this.velocityX,
						this.velocityY,
						this.velocityZ
					);
			}
		} else {
			this.world
				.addParticle(
					ParticleType.NETHER_PORTAL,
					this.x - this.velocityX * (double)j + this.random.nextDouble() * 0.6 - 0.3,
					this.y - this.velocityY * (double)j - 0.5,
					this.z - this.velocityZ * (double)j + this.random.nextDouble() * 0.6 - 0.3,
					this.velocityX,
					this.velocityY,
					this.velocityZ
				);
		}

		if (!this.world.isClient) {
			this.updatePosition(this.x, this.y, this.z);
			this.lifespan++;
			if (this.lifespan > 80 && !this.world.isClient) {
				this.remove();
				if (this.dropsItem) {
					this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.EYE_OF_ENDER)));
				} else {
					this.world.syncGlobalEvent(2003, new BlockPos(this), 0);
				}
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 15728880;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
