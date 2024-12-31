package net.minecraft.dragon;

import javax.annotation.Nullable;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.EndExitPortalFeature;

public class class_2982 extends class_2979 {
	private Vec3d field_14675;
	private int field_14676;

	public class_2982(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13182() {
		if (this.field_14676++ % 10 == 0) {
			float f = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
			float g = (this.dragon.getRandom().nextFloat() - 0.5F) * 4.0F;
			float h = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
			this.dragon
				.world
				.addParticle(ParticleType.HUGE_EXPLOSION, this.dragon.x + (double)f, this.dragon.y + 2.0 + (double)g, this.dragon.z + (double)h, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void method_13183() {
		this.field_14676++;
		if (this.field_14675 == null) {
			BlockPos blockPos = this.dragon.world.getHighestBlock(EndExitPortalFeature.ORIGIN);
			this.field_14675 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
		}

		double d = this.field_14675.method_12126(this.dragon.x, this.dragon.y, this.dragon.z);
		if (!(d < 100.0) && !(d > 22500.0) && !this.dragon.horizontalCollision && !this.dragon.verticalCollision) {
			this.dragon.setHealth(1.0F);
		} else {
			this.dragon.setHealth(0.0F);
		}
	}

	@Override
	public void method_13184() {
		this.field_14675 = null;
		this.field_14676 = 0;
	}

	@Override
	public float method_13186() {
		return 3.0F;
	}

	@Nullable
	@Override
	public Vec3d method_13187() {
		return this.field_14675;
	}

	@Override
	public class_2993<class_2982> method_13189() {
		return class_2993.DYING;
	}
}
