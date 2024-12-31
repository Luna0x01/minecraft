package net.minecraft.entity.thrown;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		ThrowableEntity.registerDataFixes(dataFixer, "Snowball");
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			for (int i = 0; i < 8; i++) {
				this.world.addParticle(ParticleType.SNOWBALL, this.x, this.y, this.z, 0.0, 0.0, 0.0);
			}
		}
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

		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}
}
