package net.minecraft.entity.thrown;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ThrowableEntity {
	public ExperienceBottleEntity(World world) {
		super(world);
	}

	public ExperienceBottleEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
	}

	public ExperienceBottleEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	protected float getGravity() {
		return 0.07F;
	}

	@Override
	protected float method_3234() {
		return 0.7F;
	}

	@Override
	protected float method_3235() {
		return -20.0F;
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (!this.world.isClient) {
			this.world.syncGlobalEvent(2002, new BlockPos(this), 0);
			int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
			}

			this.remove();
		}
	}
}
