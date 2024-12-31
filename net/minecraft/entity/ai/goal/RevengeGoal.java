package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal extends TrackTargetGoal {
	private final boolean groupRevenge;
	private int lastAttackedTime;
	private final Class<?>[] noRevengeTypes;

	public RevengeGoal(PathAwareEntity pathAwareEntity, boolean bl, Class<?>... classs) {
		super(pathAwareEntity, true);
		this.groupRevenge = bl;
		this.noRevengeTypes = classs;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		int i = this.mob.getLastHurtTimestamp();
		LivingEntity livingEntity = this.mob.getAttacker();
		return i != this.lastAttackedTime && livingEntity != null && this.canTrack(livingEntity, false);
	}

	@Override
	public void start() {
		this.mob.setTarget(this.mob.getAttacker());
		this.field_14597 = this.mob.getTarget();
		this.lastAttackedTime = this.mob.getLastHurtTimestamp();
		this.field_14598 = 300;
		if (this.groupRevenge) {
			double d = this.getFollowRange();

			for (PathAwareEntity pathAwareEntity : this.mob
				.world
				.getEntitiesInBox(this.mob.getClass(), new Box(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0, this.mob.y + 1.0, this.mob.z + 1.0).expand(d, 10.0, d))) {
				if (this.mob != pathAwareEntity
					&& pathAwareEntity.getTarget() == null
					&& (!(this.mob instanceof TameableEntity) || ((TameableEntity)this.mob).getOwner() == ((TameableEntity)pathAwareEntity).getOwner())
					&& !pathAwareEntity.isTeammate(this.mob.getAttacker())) {
					boolean bl = false;

					for (Class<?> class_ : this.noRevengeTypes) {
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
