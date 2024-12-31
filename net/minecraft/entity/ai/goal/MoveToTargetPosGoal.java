package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;

public abstract class MoveToTargetPosGoal extends Goal {
	private final PathAwareEntity mob;
	public double speed;
	protected int cooldown;
	protected int tryingTime;
	private int safeWaitingTime;
	protected BlockPos targetPos = BlockPos.ORIGIN;
	private boolean reached;
	private final int range;
	private final int field_16881;
	public int field_16880;

	public MoveToTargetPosGoal(PathAwareEntity pathAwareEntity, double d, int i) {
		this(pathAwareEntity, d, i, 1);
	}

	public MoveToTargetPosGoal(PathAwareEntity pathAwareEntity, double d, int i, int j) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.range = i;
		this.field_16880 = 0;
		this.field_16881 = j;
		this.setCategoryBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.cooldown = this.method_15694(this.mob);
			return this.findTargetPos();
		}
	}

	protected int method_15694(PathAwareEntity pathAwareEntity) {
		return 200 + pathAwareEntity.getRandom().nextInt(200);
	}

	@Override
	public boolean shouldContinue() {
		return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.method_11012(this.mob.world, this.targetPos);
	}

	@Override
	public void start() {
		this.mob
			.getNavigation()
			.startMovingTo((double)((float)this.targetPos.getX()) + 0.5, (double)(this.targetPos.getY() + 1), (double)((float)this.targetPos.getZ()) + 0.5, this.speed);
		this.tryingTime = 0;
		this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
	}

	public double method_15695() {
		return 1.0;
	}

	@Override
	public void tick() {
		if (this.mob.squaredDistanceToCenter(this.targetPos.up()) > this.method_15695()) {
			this.reached = false;
			this.tryingTime++;
			if (this.method_15696()) {
				this.mob
					.getNavigation()
					.startMovingTo(
						(double)((float)this.targetPos.getX()) + 0.5,
						(double)(this.targetPos.getY() + this.method_15697()),
						(double)((float)this.targetPos.getZ()) + 0.5,
						this.speed
					);
			}
		} else {
			this.reached = true;
			this.tryingTime--;
		}
	}

	public boolean method_15696() {
		return this.tryingTime % 40 == 0;
	}

	public int method_15697() {
		return 1;
	}

	protected boolean hasReached() {
		return this.reached;
	}

	private boolean findTargetPos() {
		int i = this.range;
		int j = this.field_16881;
		BlockPos blockPos = new BlockPos(this.mob);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = this.field_16880; k <= j; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutable.set(blockPos).method_19934(m, k - 1, n);
						if (this.mob.isInWalkTargetRange(mutable) && this.method_11012(this.mob.world, mutable)) {
							this.targetPos = mutable;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos);
}
