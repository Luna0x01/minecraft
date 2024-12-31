package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.LandType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PathAwareEntity extends MobEntity {
	public static final UUID FLEEING_SPEED_BONUS_ID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
	public static final AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_BONUS_ID, "Fleeing speed bonus", 2.0, 2)
		.setSerialized(false);
	private BlockPos positionTarget = BlockPos.ORIGIN;
	private float positionTargetRange = -1.0F;
	private final Goal goal;
	private boolean field_6809;
	private float field_14565 = LandType.WATER.getWeight();

	public PathAwareEntity(World world) {
		super(world);
		this.goal = new GoToWalkTargetGoal(this, 1.0);
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

			if (!this.field_6809) {
				this.goals.add(2, this.goal);
				if (this.getNavigation() instanceof MobNavigation) {
					this.field_14565 = this.method_13075(LandType.WATER);
					this.method_13076(LandType.WATER, 0.0F);
				}

				this.field_6809 = true;
			}

			this.method_6175(f);
			if (f > 4.0F) {
				this.getNavigation().startMovingTo(entity, 1.0);
			}

			if (f > 6.0F) {
				double d = (entity.x - this.x) / (double)f;
				double e = (entity.y - this.y) / (double)f;
				double g = (entity.z - this.z) / (double)f;
				this.velocityX = this.velocityX + d * Math.abs(d) * 0.4;
				this.velocityY = this.velocityY + e * Math.abs(e) * 0.4;
				this.velocityZ = this.velocityZ + g * Math.abs(g) * 0.4;
			}

			if (f > 10.0F) {
				this.detachLeash(true, true);
			}
		} else if (!this.isLeashed() && this.field_6809) {
			this.field_6809 = false;
			this.goals.method_4497(this.goal);
			if (this.getNavigation() instanceof MobNavigation) {
				this.method_13076(LandType.WATER, this.field_14565);
			}

			this.method_6173();
		}
	}

	protected void method_6175(float f) {
	}
}
