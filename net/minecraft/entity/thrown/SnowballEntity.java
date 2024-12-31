package net.minecraft.entity.thrown;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class SnowballEntity extends ThrowableEntity {
	public SnowballEntity(World world) {
		super(world);
	}

	public SnowballEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
	}

	public SnowballEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (result.entity != null) {
			int i = 0;
			if (result.entity instanceof BlazeEntity) {
				i = 3;
			}

			result.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i);
		}

		for (int j = 0; j < 8; j++) {
			this.world.addParticle(ParticleType.SNOWBALL, this.x, this.y, this.z, 0.0, 0.0, 0.0);
		}

		if (!this.world.isClient) {
			this.remove();
		}
	}
}
