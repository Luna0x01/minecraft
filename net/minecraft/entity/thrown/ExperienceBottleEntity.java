package net.minecraft.entity.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ThrowableEntity {
	public ExperienceBottleEntity(World world) {
		super(EntityType.EXPERIENCE_BOTTLE, world);
	}

	public ExperienceBottleEntity(World world, LivingEntity livingEntity) {
		super(EntityType.EXPERIENCE_BOTTLE, livingEntity, world);
	}

	public ExperienceBottleEntity(World world, double d, double e, double f) {
		super(EntityType.EXPERIENCE_BOTTLE, d, e, f, world);
	}

	@Override
	protected float getGravity() {
		return 0.07F;
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (!this.world.isClient) {
			this.world.syncGlobalEvent(2002, new BlockPos(this), PotionUtil.getColor(Potions.WATER));
			int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.world.method_3686(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
			}

			this.remove();
		}
	}
}
