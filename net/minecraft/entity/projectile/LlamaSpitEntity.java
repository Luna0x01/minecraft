package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LlamaEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LlamaSpitEntity extends Entity implements Projectile {
	public LlamaEntity owner;
	private NbtCompound ownerNbt;

	public LlamaSpitEntity(World world) {
		super(world);
	}

	public LlamaSpitEntity(World world, LlamaEntity llamaEntity) {
		super(world);
		this.owner = llamaEntity;
		this.updatePosition(
			llamaEntity.x - (double)(llamaEntity.width + 1.0F) * 0.5 * (double)MathHelper.sin(llamaEntity.bodyYaw * (float) (Math.PI / 180.0)),
			llamaEntity.y + (double)llamaEntity.getEyeHeight() - 0.1F,
			llamaEntity.z + (double)(llamaEntity.width + 1.0F) * 0.5 * (double)MathHelper.cos(llamaEntity.bodyYaw * (float) (Math.PI / 180.0))
		);
		this.setBounds(0.25F, 0.25F);
	}

	public LlamaSpitEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(world);
		this.updatePosition(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			world.addParticle(ParticleType.SPIT, d, e, f, g * k, h, i * k);
		}

		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ownerNbt != null) {
			this.setOwnerFromNbt();
		}

		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		BlockHitResult blockHitResult = this.world.rayTrace(vec3d, vec3d2);
		vec3d = new Vec3d(this.x, this.y, this.z);
		vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		if (blockHitResult != null) {
			vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
		}

		Entity entity = this.rayTrace(vec3d, vec3d2);
		if (entity != null) {
			blockHitResult = new BlockHitResult(entity);
		}

		if (blockHitResult != null) {
			this.onHit(blockHitResult);
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
		float h = 0.06F;
		if (!this.world.containsMaterial(this.getBoundingBox(), Material.AIR)) {
			this.remove();
		} else if (this.isTouchingWater()) {
			this.remove();
		} else {
			this.velocityX *= 0.99F;
			this.velocityY *= 0.99F;
			this.velocityZ *= 0.99F;
			if (!this.hasNoGravity()) {
				this.velocityY -= 0.06F;
			}

			this.updatePosition(this.x, this.y, this.z);
		}
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
		}
	}

	@Nullable
	private Entity rayTrace(Vec3d vec1, Vec3d vec2) {
		Entity entity = null;
		List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0));
		double d = 0.0;

		for (Entity entity2 : list) {
			if (entity2 != this.owner) {
				Box box = entity2.getBoundingBox().expand(0.3F);
				BlockHitResult blockHitResult = box.method_585(vec1, vec2);
				if (blockHitResult != null) {
					double e = vec1.squaredDistanceTo(blockHitResult.pos);
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

	public void onHit(BlockHitResult result) {
		if (result.entity != null && this.owner != null) {
			result.entity.damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0F);
		}

		if (!this.world.isClient) {
			this.remove();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("Owner", 10)) {
			this.ownerNbt = nbt.getCompound("Owner");
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.owner != null) {
			NbtCompound nbtCompound = new NbtCompound();
			UUID uUID = this.owner.getUuid();
			nbtCompound.putUuid("OwnerUUID", uUID);
			nbt.put("Owner", nbtCompound);
		}
	}

	private void setOwnerFromNbt() {
		if (this.ownerNbt != null && this.ownerNbt.containsUuid("OwnerUUID")) {
			UUID uUID = this.ownerNbt.getUuid("OwnerUUID");

			for (LlamaEntity llamaEntity : this.world.getEntitiesInBox(LlamaEntity.class, this.getBoundingBox().expand(15.0))) {
				if (llamaEntity.getUuid().equals(uUID)) {
					this.owner = llamaEntity;
					break;
				}
			}
		}

		this.ownerNbt = null;
	}
}
