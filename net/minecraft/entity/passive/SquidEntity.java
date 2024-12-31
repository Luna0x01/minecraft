package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SquidEntity extends WaterCreatureEntity {
	public float tiltAngle;
	public float prevTiltAngle;
	public float rollAngle;
	public float prevRollAngle;
	public float thrustTimer;
	public float prevThrustTimer;
	public float tentacleAngle;
	public float prevTentacleAngle;
	private float constantVelocityRate;
	private float thrustTimerSpeed;
	private float turningSpeed;
	private float constantVelocityX;
	private float constantVelocityY;
	private float constantVelocityZ;

	public SquidEntity(World world) {
		super(EntityType.SQUID, world);
		this.setBounds(0.8F, 0.8F);
		this.random.setSeed((long)(1 + this.getEntityId()));
		this.thrustTimerSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SquidEntity.SwimGoal(this));
		this.goals.add(1, new SquidEntity.class_3491());
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_SQUID_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_SQUID_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_SQUID_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SQUID_ENTITIE;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.prevTiltAngle = this.tiltAngle;
		this.prevRollAngle = this.rollAngle;
		this.prevThrustTimer = this.thrustTimer;
		this.prevTentacleAngle = this.tentacleAngle;
		this.thrustTimer = this.thrustTimer + this.thrustTimerSpeed;
		if ((double)this.thrustTimer > Math.PI * 2) {
			if (this.world.isClient) {
				this.thrustTimer = (float) (Math.PI * 2);
			} else {
				this.thrustTimer = (float)((double)this.thrustTimer - (Math.PI * 2));
				if (this.random.nextInt(10) == 0) {
					this.thrustTimerSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
				}

				this.world.sendEntityStatus(this, (byte)19);
			}
		}

		if (this.method_15575()) {
			if (this.thrustTimer < (float) Math.PI) {
				float f = this.thrustTimer / (float) Math.PI;
				this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
				if ((double)f > 0.75) {
					this.constantVelocityRate = 1.0F;
					this.turningSpeed = 1.0F;
				} else {
					this.turningSpeed *= 0.8F;
				}
			} else {
				this.tentacleAngle = 0.0F;
				this.constantVelocityRate *= 0.9F;
				this.turningSpeed *= 0.99F;
			}

			if (!this.world.isClient) {
				this.velocityX = (double)(this.constantVelocityX * this.constantVelocityRate);
				this.velocityY = (double)(this.constantVelocityY * this.constantVelocityRate);
				this.velocityZ = (double)(this.constantVelocityZ * this.constantVelocityRate);
			}

			float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.bodyYaw = this.bodyYaw + (-((float)MathHelper.atan2(this.velocityX, this.velocityZ)) * (180.0F / (float)Math.PI) - this.bodyYaw) * 0.1F;
			this.yaw = this.bodyYaw;
			this.rollAngle = (float)((double)this.rollAngle + Math.PI * (double)this.turningSpeed * 1.5);
			this.tiltAngle = this.tiltAngle + (-((float)MathHelper.atan2((double)g, this.velocityY)) * (180.0F / (float)Math.PI) - this.tiltAngle) * 0.1F;
		} else {
			this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.thrustTimer)) * (float) Math.PI * 0.25F;
			if (!this.world.isClient) {
				this.velocityX = 0.0;
				this.velocityZ = 0.0;
				if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
					this.velocityY = this.velocityY + (0.05 * (double)(this.getEffectInstance(StatusEffects.LEVITATION).getAmplifier() + 1) - this.velocityY);
				} else if (!this.hasNoGravity()) {
					this.velocityY -= 0.08;
				}

				this.velocityY *= 0.98F;
			}

			this.tiltAngle = (float)((double)this.tiltAngle + (double)(-90.0F - this.tiltAngle) * 0.02);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (super.damage(source, amount) && this.getAttacker() != null) {
			this.method_15773();
			return true;
		} else {
			return false;
		}
	}

	private Vec3d method_15772(Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.rotateX(this.prevTiltAngle * (float) (Math.PI / 180.0));
		return vec3d2.rotateY(-this.prevBodyYaw * (float) (Math.PI / 180.0));
	}

	private void method_15773() {
		this.playSound(Sounds.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
		Vec3d vec3d = this.method_15772(new Vec3d(0.0, -1.0, 0.0)).add(this.x, this.y, this.z);

		for (int i = 0; i < 30; i++) {
			Vec3d vec3d2 = this.method_15772(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
			Vec3d vec3d3 = vec3d2.multiply(0.3 + (double)(this.random.nextFloat() * 2.0F));
			((ServerWorld)this.world).method_21261(class_4342.field_21372, vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1F);
		}
	}

	@Override
	public void method_2657(float f, float g, float h) {
		this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return this.y > 45.0 && this.y < (double)iWorld.method_8483();
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 19) {
			this.thrustTimer = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	public void setConstantVelocity(float f, float g, float h) {
		this.constantVelocityX = f;
		this.constantVelocityY = g;
		this.constantVelocityZ = h;
	}

	public boolean hasConstantVelocity() {
		return this.constantVelocityX != 0.0F || this.constantVelocityY != 0.0F || this.constantVelocityZ != 0.0F;
	}

	class SwimGoal extends Goal {
		private final SquidEntity squid;

		public SwimGoal(SquidEntity squidEntity2) {
			this.squid = squidEntity2;
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			int i = this.squid.method_6117();
			if (i > 100) {
				this.squid.setConstantVelocity(0.0F, 0.0F, 0.0F);
			} else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.touchingWater || !this.squid.hasConstantVelocity()) {
				float f = this.squid.getRandom().nextFloat() * (float) (Math.PI * 2);
				float g = MathHelper.cos(f) * 0.2F;
				float h = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
				float j = MathHelper.sin(f) * 0.2F;
				this.squid.setConstantVelocity(g, h, j);
			}
		}
	}

	class class_3491 extends Goal {
		private int field_16929;

		private class_3491() {
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = SquidEntity.this.getAttacker();
			return SquidEntity.this.isTouchingWater() && livingEntity != null ? SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0 : false;
		}

		@Override
		public void start() {
			this.field_16929 = 0;
		}

		@Override
		public void tick() {
			this.field_16929++;
			LivingEntity livingEntity = SquidEntity.this.getAttacker();
			if (livingEntity != null) {
				Vec3d vec3d = new Vec3d(SquidEntity.this.x - livingEntity.x, SquidEntity.this.y - livingEntity.y, SquidEntity.this.z - livingEntity.z);
				BlockState blockState = SquidEntity.this.world
					.getBlockState(new BlockPos(SquidEntity.this.x + vec3d.x, SquidEntity.this.y + vec3d.y, SquidEntity.this.z + vec3d.z));
				FluidState fluidState = SquidEntity.this.world
					.getFluidState(new BlockPos(SquidEntity.this.x + vec3d.x, SquidEntity.this.y + vec3d.y, SquidEntity.this.z + vec3d.z));
				if (fluidState.matches(FluidTags.WATER) || blockState.isAir()) {
					double d = vec3d.length();
					if (d > 0.0) {
						vec3d.normalize();
						float f = 3.0F;
						if (d > 5.0) {
							f = (float)((double)f - (d - 5.0) / 5.0);
						}

						if (f > 0.0F) {
							vec3d = vec3d.multiply((double)f);
						}
					}

					if (blockState.isAir()) {
						vec3d = vec3d.subtract(0.0, vec3d.y, 0.0);
					}

					SquidEntity.this.setConstantVelocity((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
				}

				if (this.field_16929 % 10 == 5) {
					SquidEntity.this.world.method_16343(class_4342.field_21379, SquidEntity.this.x, SquidEntity.this.y, SquidEntity.this.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}
}
