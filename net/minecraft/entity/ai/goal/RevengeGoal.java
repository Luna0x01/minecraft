package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal extends TrackTargetGoal {
	private boolean groupRevenge;
	private int lastAttackedTime;
	private final Class[] noRevengeTypes;

	public RevengeGoal(PathAwareEntity pathAwareEntity, boolean bl, Class... classs) {
		super(pathAwareEntity, false);
		this.groupRevenge = bl;
		this.noRevengeTypes = classs;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		int i = this.mob.getLastHurtTimestamp();
		return i != this.lastAttackedTime && this.canTrack(this.mob.getAttacker(), false);
	}

	@Override
	public void start() {
		this.mob.setTarget(this.mob.getAttacker());
		this.lastAttackedTime = this.mob.getLastHurtTimestamp();
		if (this.groupRevenge) {
			double d = this.getFollowRange();

			for (PathAwareEntity pathAwareEntity : this.mob
				.world
				.getEntitiesInBox(this.mob.getClass(), new Box(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0, this.mob.y + 1.0, this.mob.z + 1.0).expand(d, 10.0, d))) {
				if (this.mob != pathAwareEntity && pathAwareEntity.getTarget() == null && !pathAwareEntity.isInSameTeam(this.mob.getAttacker())) {
					boolean bl = false;

					for (Class class_ : this.noRevengeTypes) {
						if (pathAwareEntity.getClass() == class_) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						this.setMobEntityTarget(pathAwareEntity, this.mob.getAttacker());
					}
				}
			}
		}

		super.start();
	}

	protected void setMobEntityTarget(PathAwareEntity mob, LivingEntity target) {
		mob.setTarget(target);
	}
}
