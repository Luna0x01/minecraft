package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class PathAwareEntity extends MobEntity {
	public static final UUID FLEEING_SPEED_BONUS_ID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
	public static final AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_BONUS_ID, "Fleeing speed bonus", 2.0, 2)
		.setSerialized(false);
	private BlockPos positionTarget = BlockPos.ORIGIN;
	private float positionTargetRange = -1.0F;
	private float field_14565 = LandType.WATER.getWeight();

	public PathAwareEntity(World world) {
		super(world);
	}

	public float getPathfindingFavor(BlockPos pos) {
		return 0.0F;
	}

	@Override
	public boolean canSpawn() {
		return super.canSpawn() && this.getPathfindingFavor(new BlockPos(this.x, this.getBoundingBox().minY, this.z)) >= 0.0F;
	}

	public boolean shouldContinue() {
		return !this.navigation.isIdle();
	}

	public boolean isInWalkTargetRange() {
		return this.isInWalkTargetRange(new BlockPos(this));
	}

	public boolean isInWalkTargetRange(BlockPos pos) {
		return this.positionTargetRange == -1.0F ? true : this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
	}

	public void setPositionTarget(BlockPos posTarget, int range) {
		this.positionTarget = posTarget;
		this.positionTargetRange = (float)range;
	}

	public BlockPos getPositionTarget() {
		return this.positionTarget;
	}

	public float getPositionTargetRange() {
		return this.positionTargetRange;
	}

	public void method_6173() {
		this.positionTargetRange = -1.0F;
	}

	public boolean hasPositionTarget() {
		return this.positionTargetRange != -1.0F;
	}

	@Override
	protected void updateLeash() {
		super.updateLeash();
		if (this.isLeashed() && this.getLeashOwner() != null && this.getLeashOwner().world == this.world) {
			Entity entity = this.getLeashOwner();
			this.setPositionTarget(new BlockPos((int)entity.x, (int)entity.y, (int)entity.z), 5);
			float f = this.distanceTo(entity);
			if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
				if (f > 10.0F) {
					this.detachLeash(true, true);
				}

				return;
			}

			this.method_6175(f);
			if (f > 10.0F) {
				this.detachLeash(true, true);
				this.goals.method_13098(1);
			} else if (f > 6.0F) {
				double d = (entity.x - this.x) / (double)f;
				double e = (entity.y - this.y) / (double)f;
				double g = (entity.z - this.z) / (double)f;
				this.velocityX = this.velocityX + d * Math.abs(d) * 0.4;
				this.velocityY = this.velocityY + e * Math.abs(e) * 0.4;
				this.velocityZ = this.velocityZ + g * Math.abs(g) * 0.4;
			} else {
				this.goals.method_13099(1);
				float h = 2.0F;
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y - this.y, entity.z - this.z).normalize().multiply((double)Math.max(f - 2.0F, 0.0F));
				this.getNavigation().startMovingTo(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z, this.method_13951());
			}
		}
	}

	protected double method_13951() {
		return 1.0;
	}

	protected void method_6175(float f) {
	}
}
