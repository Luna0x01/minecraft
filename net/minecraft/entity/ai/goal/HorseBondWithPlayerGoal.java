package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class HorseBondWithPlayerGoal extends Goal {
	private HorseBaseEntity horse;
	private double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public HorseBondWithPlayerGoal(HorseBaseEntity horseBaseEntity, double d) {
		this.horse = horseBaseEntity;
		this.speed = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.horse.isTame() && this.horse.hasPassengers()) {
			Vec3d vec3d = RandomVectorGenerator.method_2799(this.horse, 5, 4);
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
		this.horse.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.horse.getNavigation().isIdle() && this.horse.hasPassengers();
	}

	@Override
	public void tick() {
		if (this.horse.getRandom().nextInt(50) == 0) {
			Entity entity = (Entity)this.horse.getPassengerList().get(0);
			if (entity == null) {
				return;
			}

			if (entity instanceof PlayerEntity) {
				int i = this.horse.getTemper();
				int j = this.horse.getMaxTemper();
				if (j > 0 && this.horse.getRandom().nextInt(j) < i) {
					this.horse.bondWithPlayer((PlayerEntity)entity);
					this.horse.world.sendEntityStatus(this.horse, (byte)7);
					return;
				}

				this.horse.addTemper(5);
			}

			this.horse.removeAllPassengers();
			this.horse.playAngrySound();
			this.horse.world.sendEntityStatus(this.horse, (byte)6);
		}
	}
}
