package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
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
		super(world);
		this.setBounds(0.95F, 0.95F);
		this.random.setSeed((long)(1 + this.getEntityId()));
		this.thrustTimerSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
		this.goals.add(0, new SquidEntity.SwimGoal(this));
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
	protected String getAmbientSound() {
		return null;
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected Item getDefaultDrop() {
		return null;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(3 + lootingMultiplier) + 1;

		for (int j = 0; j < i; j++) {
			this.dropItem(new ItemStack(Items.DYE, 1, DyeColor.BLACK.getSwappedId()), 0.0F);
		}
	}

	@Override
	public boolean isTouchingWater() {
		return this.world.method_3610(this.getBoundingBox().expand(0.0, -0.6F, 0.0), Material.WATER, this);
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

		if (this.touchingWater) {
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
			this.bodyYaw = this.bodyYaw + (-((float)MathHelper.atan2(this.velocityX, this.velocityZ)) * 180.0F / (float) Math.PI - this.bodyYaw) * 0.1F;
			this.yaw = this.bodyYaw;
			this.rollAngle = (float)((double)this.rollAngle + Math.PI * (double)this.turningSpeed * 1.5);
			this.tiltAngle = this.tiltAngle + (-((float)MathHelper.atan2((double)g, this.velocityY)) * 180.0F / (float) Math.PI - this.tiltAngle) * 0.1F;
		} else {
			this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.thrustTimer)) * (float) Math.PI * 0.25F;
			if (!this.world.isClient) {
				this.velocityX = 0.0;
				this.velocityY -= 0.08;
				this.velocityY *= 0.98F;
				this.velocityZ = 0.0;
			}

			this.tiltAngle = (float)((double)this.tiltAngle + (double)(-90.0F - this.tiltAngle) * 0.02);
		}
	}

	@Override
	public void travel(float f, float g) {
		this.move(this.velocityX, this.velocityY, this.velocityZ);
	}

	@Override
	public boolean canSpawn() {
		return this.y > 45.0 && this.y < (double)this.world.getSeaLevel() && super.canSpawn();
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

	static class SwimGoal extends Goal {
		private SquidEntity squid;

		public SwimGoal(SquidEntity squidEntity) {
			this.squid = squidEntity;
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
				float f = this.squid.getRandom().nextFloat() * (float) Math.PI * 2.0F;
				float g = MathHelper.cos(f) * 0.2F;
				float h = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
				float j = MathHelper.sin(f) * 0.2F;
				this.squid.setConstantVelocity(g, h, j);
			}
		}
	}
}
