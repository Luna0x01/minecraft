package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

public class StopAndLookAtEntityGoal extends LookAtEntityGoal {
	public StopAndLookAtEntityGoal(MobEntity mobEntity, Class<? extends Entity> class_, float f, float g) {
		super(mobEntity, class_, f, g);
		this.setCategoryBits(3);
	}
}
