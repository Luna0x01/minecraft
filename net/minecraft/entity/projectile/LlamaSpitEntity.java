package net.minecraft.entity.projectile;

import java.util.UUID;
import net.minecraft.block.Material;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity extends Entity implements Projectile {
	public LlamaEntity owner;
	private CompoundTag tag;

	public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
		super(entityType, world);
	}

	public LlamaSpitEntity(World world, LlamaEntity llamaEntity) {
		this(EntityType.field_6124, world);
		this.owner = llamaEntity;
		this.updatePosition(
			llamaEntity.getX() - (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(llamaEntity.bodyYaw * (float) (Math.PI / 180.0)),
			llamaEntity.getEyeY() - 0.1F,
			llamaEntity.getZ() + (double)(llamaEntity.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(llamaEntity.bodyYaw * (float) (Math.PI / 180.0))
		);
	}

	public LlamaSpitEntity(World world, double d, double e, double f, double g, double h, double i) {
		this(EntityType.field_6124, world);
		this.updatePosition(d, e, f);

		for (int j = 0; j < 7; j++) {
			double k = 0.4 + 0.1 * (double)j;
			world.addParticle(ParticleTypes.field_11228, d, e, f, g * k, h, i * k);
		}

		this.setVelocity(g, h, i);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.tag != null) {
			this.readTag();
		}

		Vec3d vec3d = this.getVelocity();
		HitResult hitResult = ProjectileUtil.getCollision(
			this, this.getBoundingBox().stretch(vec3d).expand(1.0), entity -> !entity.isSpectator() && entity != this.owner, RayTraceContext.ShapeType.field_17559, true
		);
		if (hitResult != null) {
			this.method_7481(hitResult);
		}

		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		float g = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)g) * 180.0F / (float)Math.PI);

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

		this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
		this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
		float h = 0.99F;
		float i = 0.06F;
		if (!this.world.containsBlockWithMaterial(this.getBoundingBox(), Material.AIR)) {
			this.remove();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.remove();
		} else {
			this.setVelocity(vec3d.multiply(0.99F));
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.06F, 0.0));
			}

			this.updatePosition(d, e, f);
		}
	}

	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		Vec3d vec3d = new Vec3d(d, e, f)
			.normalize()
			.add(this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h)
			.multiply((double)g);
		this.setVelocity(vec3d);
		float i = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, f) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)i) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	public void method_7481(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.field_1331 && this.owner != null) {
			((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0F);
		} else if (type == HitResult.Type.field_1332 && !this.world.isClient) {
			this.remove();
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.contains("Owner", 10)) {
			this.tag = compoundTag.getCompound("Owner");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.owner != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			UUID uUID = this.owner.getUuid();
			compoundTag2.putUuid("OwnerUUID", uUID);
			compoundTag.put("Owner", compoundTag2);
		}
	}

	private void readTag() {
		if (this.tag != null && this.tag.containsUuid("OwnerUUID")) {
			UUID uUID = this.tag.getUuid("OwnerUUID");

			for (LlamaEntity llamaEntity : this.world.getNonSpectatingEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0))) {
				if (llamaEntity.getUuid().equals(uUID)) {
					this.owner = llamaEntity;
					break;
				}
			}
		}

		this.tag = null;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
