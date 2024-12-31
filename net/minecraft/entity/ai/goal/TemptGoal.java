package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class TemptGoal extends Goal {
	private final PathAwareEntity mob;
	private final double speed;
	private double lastPlayerX;
	private double lastPlayerY;
	private double lastPlayerZ;
	private double lastPlayerPitch;
	private double lastPlayerYaw;
	private PlayerEntity closestPlayer;
	private int cooldown;
	private boolean active;
	private final Ingredient field_16886;
	private final boolean canBeScared;

	public TemptGoal(PathAwareEntity pathAwareEntity, double d, Ingredient ingredient, boolean bl) {
		this(pathAwareEntity, d, bl, ingredient);
	}

	public TemptGoal(PathAwareEntity pathAwareEntity, double d, boolean bl, Ingredient ingredient) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.field_16886 = ingredient;
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
			this.closestPlayer = this.mob.world.method_16364(this.mob, 10.0);
			return this.closestPlayer == null
				? false
				: this.method_13103(this.closestPlayer.getMainHandStack()) || this.method_13103(this.closestPlayer.getOffHandStack());
		}
	}

	protected boolean method_13103(ItemStack itemStack) {
		return this.field_16886.test(itemStack);
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
	}

	@Override
	public void stop() {
		this.closestPlayer = null;
		this.mob.getNavigation().stop();
		this.cooldown = 100;
		this.active = false;
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.closestPlayer, (float)(this.mob.method_13081() + 20), (float)this.mob.getLookPitchSpeed());
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
