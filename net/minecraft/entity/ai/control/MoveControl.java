package net.minecraft.entity.ai.control;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class MoveControl {
	protected final MobEntity entity;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected double speed;
	protected float forwardMovement;
	protected float sidewaysMovement;
	protected MoveControl.State state = MoveControl.State.field_6377;

	public MoveControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public boolean isMoving() {
		return this.state == MoveControl.State.field_6378;
	}

	public double getSpeed() {
		return this.speed;
	}

	public void moveTo(double d, double e, double f, double g) {
		this.targetX = d;
		this.targetY = e;
		this.targetZ = f;
		this.speed = g;
		if (this.state != MoveControl.State.field_6379) {
			this.state = MoveControl.State.field_6378;
		}
	}

	public void strafeTo(float f, float g) {
		this.state = MoveControl.State.field_6376;
		this.forwardMovement = f;
		this.sidewaysMovement = g;
		this.speed = 0.25;
	}

	public void tick() {
		if (this.state == MoveControl.State.field_6376) {
			float f = (float)this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
			float g = (float)this.speed * f;
			float h = this.forwardMovement;
			float i = this.sidewaysMovement;
			float j = MathHelper.sqrt(h * h + i * i);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = MathHelper.sin(this.entity.yaw * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.entity.yaw * (float) (Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			EntityNavigation entityNavigation = this.entity.getNavigation();
			if (entityNavigation != null) {
				PathNodeMaker pathNodeMaker = entityNavigation.getNodeMaker();
				if (pathNodeMaker != null
					&& pathNodeMaker.getNodeType(
							this.entity.world,
							MathHelper.floor(this.entity.getX() + (double)m),
							MathHelper.floor(this.entity.getY()),
							MathHelper.floor(this.entity.getZ() + (double)n)
						)
						!= PathNodeType.field_12) {
					this.forwardMovement = 1.0F;
					this.sidewaysMovement = 0.0F;
					g = f;
				}
			}

			this.entity.setMovementSpeed(g);
			this.entity.setForwardSpeed(this.forwardMovement);
			this.entity.setSidewaysSpeed(this.sidewaysMovement);
			this.state = MoveControl.State.field_6377;
		} else if (this.state == MoveControl.State.field_6378) {
			this.state = MoveControl.State.field_6377;
			double d = this.targetX - this.entity.getX();
			double e = this.targetZ - this.entity.getZ();
			double o = this.targetY - this.entity.getY();
			double p = d * d + o * o + e * e;
			if (p < 2.5000003E-7F) {
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float q = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.yaw = this.changeAngle(this.entity.yaw, q, 90.0F);
			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
			BlockPos blockPos = new BlockPos(this.entity);
			BlockState blockState = this.entity.world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			VoxelShape voxelShape = blockState.getCollisionShape(this.entity.world, blockPos);
			if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0F, this.entity.getWidth())
				|| !voxelShape.isEmpty()
					&& this.entity.getY() < voxelShape.getMaximum(Direction.Axis.field_11052) + (double)blockPos.getY()
					&& !block.matches(BlockTags.field_15495)
					&& !block.matches(BlockTags.field_16584)) {
				this.entity.getJumpControl().setActive();
				this.state = MoveControl.State.field_6379;
			}
		} else if (this.state == MoveControl.State.field_6379) {
			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
			if (this.entity.onGround) {
				this.state = MoveControl.State.field_6377;
			}
		} else {
			this.entity.setForwardSpeed(0.0F);
		}
	}

	protected float changeAngle(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		float j = f + i;
		if (j < 0.0F) {
			j += 360.0F;
		} else if (j > 360.0F) {
			j -= 360.0F;
		}

		return j;
	}

	public double getTargetX() {
		return this.targetX;
	}

	public double getTargetY() {
		return this.targetY;
	}

	public double getTargetZ() {
		return this.targetZ;
	}

	public static enum State {
		field_6377,
		field_6378,
		field_6376,
		field_6379;
	}
}
