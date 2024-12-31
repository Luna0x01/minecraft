package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
	private final Set<Item> field_14593;
	private final boolean canBeScared;

	public TemptGoal(PathAwareEntity pathAwareEntity, double d, Item item, boolean bl) {
		this(pathAwareEntity, d, bl, Sets.newHashSet(new Item[]{item}));
	}

	public TemptGoal(PathAwareEntity pathAwareEntity, double d, boolean bl, Set<Item> set) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.field_14593 = set;
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
			return this.closestPlayer == null
				? false
				: this.method_13103(this.closestPlayer.getMainHandStack()) || this.method_13103(this.closestPlayer.getOffHandStack());
		}
	}

	protected boolean method_13103(@Nullable ItemStack itemStack) {
		return itemStack == null ? false : this.field_14593.contains(itemStack.getItem());
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
