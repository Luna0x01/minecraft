package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MoveToTargetPosGoal extends Goal {
	private final PathAwareEntity mob;
	private final double speed;
	protected int cooldown;
	private int tryingTime;
	private int safeWaitingTime;
	protected BlockPos targetPos = BlockPos.ORIGIN;
	private boolean reached;
	private final int range;

	public MoveToTargetPosGoal(PathAwareEntity pathAwareEntity, double d, int i) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.range = i;
		this.setCategoryBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.cooldown = 200 + this.mob.getRandom().nextInt(200);
			return this.findTargetPos();
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.mob.world, this.targetPos);
	}

	@Override
	public void start() {
		this.mob
			.getNavigation()
			.startMovingTo((double)((float)this.targetPos.getX()) + 0.5, (double)(this.targetPos.getY() + 1), (double)((float)this.targetPos.getZ()) + 0.5, this.speed);
		this.tryingTime = 0;
		this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
	}

	@Override
	public void stop() {
	}

	@Override
	public void tick() {
		if (this.mob.squaredDistanceToCenter(this.targetPos.up()) > 1.0) {
			this.reached = false;
			this.tryingTime++;
			if (this.tryingTime % 40 == 0) {
				this.mob
					.getNavigation()
					.startMovingTo((double)((float)this.targetPos.getX()) + 0.5, (double)(this.targetPos.getY() + 1), (double)((float)this.targetPos.getZ()) + 0.5, this.speed);
			}
		} else {
			this.reached = true;
			this.tryingTime--;
		}
	}

	protected boolean hasReached() {
		return this.reached;
	}

	private boolean findTargetPos() {
		int i = this.range;
		int j = 1;
		BlockPos blockPos = new BlockPos(this.mob);

		for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						BlockPos blockPos2 = blockPos.add(m, k - 1, n);
						if (this.mob.isInWalkTargetRange(blockPos2) && this.isTargetPos(this.mob.world, blockPos2)) {
							this.targetPos = blockPos2;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean isTargetPos(World world, BlockPos pos);
}
