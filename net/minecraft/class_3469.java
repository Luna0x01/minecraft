package net.minecraft;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class class_3469 extends Goal {
	private int field_16848;
	private final PathAwareEntity field_16849;
	private LivingEntity field_16850;
	private class_3467 field_16851;

	public class_3469(PathAwareEntity pathAwareEntity) {
		this.field_16849 = pathAwareEntity;
	}

	@Override
	public boolean canStart() {
		List<BoatEntity> list = this.field_16849.world.getEntitiesInBox(BoatEntity.class, this.field_16849.getBoundingBox().expand(5.0));
		boolean bl = false;

		for (BoatEntity boatEntity : list) {
			if (boatEntity.getPrimaryPassenger() != null
				&& (
					MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).sidewaysSpeed) > 0.0F
						|| MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).field_16513) > 0.0F
				)) {
				bl = true;
				break;
			}
		}

		return this.field_16850 != null && (MathHelper.abs(this.field_16850.sidewaysSpeed) > 0.0F || MathHelper.abs(this.field_16850.field_16513) > 0.0F) || bl;
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_16850 != null
			&& this.field_16850.hasMount()
			&& (MathHelper.abs(this.field_16850.sidewaysSpeed) > 0.0F || MathHelper.abs(this.field_16850.field_16513) > 0.0F);
	}

	@Override
	public void start() {
		for (BoatEntity boatEntity : this.field_16849.world.getEntitiesInBox(BoatEntity.class, this.field_16849.getBoundingBox().expand(5.0))) {
			if (boatEntity.getPrimaryPassenger() != null && boatEntity.getPrimaryPassenger() instanceof LivingEntity) {
				this.field_16850 = (LivingEntity)boatEntity.getPrimaryPassenger();
				break;
			}
		}

		this.field_16848 = 0;
		this.field_16851 = class_3467.GO_TO_BOAT;
	}

	@Override
	public void stop() {
		this.field_16850 = null;
	}

	@Override
	public void tick() {
		boolean bl = MathHelper.abs(this.field_16850.sidewaysSpeed) > 0.0F || MathHelper.abs(this.field_16850.field_16513) > 0.0F;
		float f = this.field_16851 == class_3467.GO_IN_BOAT_DIRECTION ? (bl ? 0.17999999F : 0.0F) : 0.135F;
		this.field_16849.method_2492(this.field_16849.sidewaysSpeed, this.field_16849.forwardSpeed, this.field_16849.field_16513, f);
		this.field_16849.move(MovementType.SELF, this.field_16849.velocityX, this.field_16849.velocityY, this.field_16849.velocityZ);
		if (--this.field_16848 <= 0) {
			this.field_16848 = 10;
			if (this.field_16851 == class_3467.GO_TO_BOAT) {
				BlockPos blockPos = new BlockPos(this.field_16850).offset(this.field_16850.getHorizontalDirection().getOpposite());
				blockPos = blockPos.add(0, -1, 0);
				this.field_16849.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
				if (this.field_16849.distanceTo(this.field_16850) < 4.0F) {
					this.field_16848 = 0;
					this.field_16851 = class_3467.GO_IN_BOAT_DIRECTION;
				}
			} else if (this.field_16851 == class_3467.GO_IN_BOAT_DIRECTION) {
				Direction direction = this.field_16850.getMovementDirection();
				BlockPos blockPos2 = new BlockPos(this.field_16850).offset(direction, 10);
				this.field_16849.getNavigation().startMovingTo((double)blockPos2.getX(), (double)(blockPos2.getY() - 1), (double)blockPos2.getZ(), 1.0);
				if (this.field_16849.distanceTo(this.field_16850) > 12.0F) {
					this.field_16848 = 0;
					this.field_16851 = class_3467.GO_TO_BOAT;
				}
			}
		}
	}
}
