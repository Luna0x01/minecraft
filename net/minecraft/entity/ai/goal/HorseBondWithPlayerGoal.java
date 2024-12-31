package net.minecraft.entity.ai.goal;

import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class HorseBondWithPlayerGoal extends Goal {
	private final AbstractHorseEntity field_15482;
	private final double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public HorseBondWithPlayerGoal(AbstractHorseEntity abstractHorseEntity, double d) {
		this.field_15482 = abstractHorseEntity;
		this.speed = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.field_15482.method_13990() && this.field_15482.hasPassengers()) {
			Vec3d vec3d = RandomVectorGenerator.method_2799(this.field_15482, 5, 4);
			if (vec3d == null) {
				return false;
			} else {
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		this.field_15482.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_15482.method_13990() && !this.field_15482.getNavigation().isIdle() && this.field_15482.hasPassengers();
	}

	@Override
	public void tick() {
		if (!this.field_15482.method_13990() && this.field_15482.getRandom().nextInt(50) == 0) {
			Entity entity = (Entity)this.field_15482.getPassengerList().get(0);
			if (entity == null) {
				return;
			}

			if (entity instanceof PlayerEntity) {
				int i = this.field_15482.method_13997();
				int j = this.field_15482.method_13976();
				if (j > 0 && this.field_15482.getRandom().nextInt(j) < i) {
					this.field_15482.method_14004((PlayerEntity)entity);
					return;
				}

				this.field_15482.method_14006(5);
			}

			this.field_15482.removeAllPassengers();
			this.field_15482.method_13979();
			this.field_15482.world.sendEntityStatus(this.field_15482, (byte)6);
		}
	}
}
