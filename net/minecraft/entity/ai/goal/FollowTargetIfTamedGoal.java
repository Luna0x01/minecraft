package net.minecraft.entity.ai.goal;

import com.google.common.base.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class FollowTargetIfTamedGoal<T extends LivingEntity> extends FollowTargetGoal {
	private TameableEntity tameable;

	public FollowTargetIfTamedGoal(TameableEntity tameableEntity, Class<T> class_, boolean bl, Predicate<? super T> predicate) {
		super(tameableEntity, class_, 10, bl, false, predicate);
		this.tameable = tameableEntity;
	}

	@Override
	public boolean canStart() {
		return !this.tameable.isTamed() && super.canStart();
	}
}
