package net.minecraft.entity.thrown;

import net.minecraft.class_4342;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class SnowballEntity extends ThrowableEntity {
	public SnowballEntity(World world) {
		super(EntityType.SNOWBALL, world);
	}

	public SnowballEntity(World world, LivingEntity livingEntity) {
		super(EntityType.SNOWBALL, livingEntity, world);
	}

	public SnowballEntity(World world, double d, double e, double f) {
		super(EntityType.SNOWBALL, d, e, f, world);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			for (int i = 0; i < 8; i++) {
				this.world.method_16343(class_4342.field_21355, this.x, this.y, this.z, 0.0, 0.0, 0.0);
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
