package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TemptGoal extends Goal {
	private PathAwareEntity mob;
	private double speed;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	private PlayerEntity closestPlayer;
	private int cooldown;
	private boolean active;
	private Item food;
	private boolean canBeScared;
	private boolean field_3620;

	public TemptGoal(PathAwareEntity pathAwareEntity, double d, Item item, boolean bl) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.food = item;
		this.canBeScared = bl;
		this.setCategoryBits(3);
		if (!(pathAwareEntity.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.closestPlayer = this.mob.world.getClosestPlayer(this.mob, 10.0);
			if (this.closestPlayer == null) {
				return false;
			} else {
				ItemStack itemStack = this.closestPlayer.getMainHandStack();
				return itemStack == null ? false : itemStack.getItem() == this.food;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.canBeScared) {
			if (this.mob.squaredDistanceTo(this.closestPlayer) < 36.0) {
				if (this.closestPlayer.squaredDistanceTo(this.lastPlayerX, this.lastPlayerY, this.lastPlayerZ) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double)this.closestPlayer.pitch - this.lastPlayerPitch) > 5.0 || Math.abs((double)this.closestPlayer.yaw - this.lastPlayerYaw) > 5.0) {
					return false;
				}
			} else {
				this.lastPlayerX = this.closestPlayer.x;
				this.lastPlayerY = this.closestPlayer.y;
				this.lastPlayerZ = this.closestPlayer.z;
			}

			this.lastPlayerPitch = (double)this.closestPlayer.pitch;
			this.lastPlayerYaw = (double)this.closestPlayer.yaw;
		}

		return this.canStart();
	}

	@Override
	public void start() {
		this.lastPlayerX = this.closestPlayer.x;
		this.lastPlayerY = this.closestPlayer.y;
		this.lastPlayerZ = this.closestPlayer.z;
		this.active = true;
		this.field_3620 = ((MobNavigation)this.mob.getNavigation()).method_11032();
		((MobNavigation)this.mob.getNavigation()).method_11027(false);
	}

	@Override
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = 100;
		this.active = false;
		((MobNavigation)this.mob.getNavigation()).method_11027(this.field_3620);
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, 30.0F, (float)this.mob.getLookPitchSpeed());
		if (this.mob.squaredDistanceTo(this.closestPlayer) < 6.25) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
		}
	}

	public boolean isActive() {
		return this.active;
	}
}
