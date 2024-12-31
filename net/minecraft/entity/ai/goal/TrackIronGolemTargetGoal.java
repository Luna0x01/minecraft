package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.village.Village;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
	private final IronGolemEntity field_3622;
	private LivingEntity field_6866;

	public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
		super(ironGolemEntity, false, true);
		this.field_3622 = ironGolemEntity;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		Village village = this.field_3622.method_2870();
		if (village == null) {
			return false;
		} else {
			this.field_6866 = village.getClosestAttacker(this.field_3622);
			if (this.field_6866 instanceof CreeperEntity) {
				return false;
			} else if (this.canTrack(this.field_6866, false)) {
				return true;
			} else if (this.mob.getRandom().nextInt(20) == 0) {
				this.field_6866 = village.method_6229(this.field_3622);
				return this.canTrack(this.field_6866, false);
			} else {
				return false;
			}
		}
	}

	@Override
	public void start() {
		this.field_3622.setTarget(this.field_6866);
		super.start();
	}
}
