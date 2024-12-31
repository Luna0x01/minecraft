package net.minecraft.dragon;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_2989 extends class_2980 {
	private int field_14685;
	private int field_14686;
	private AreaEffectCloudEntity field_14687;

	public class_2989(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void method_13182() {
		this.field_14685++;
		if (this.field_14685 % 2 == 0 && this.field_14685 < 10) {
			Vec3d vec3d = this.dragon.method_13162(1.0F).normalize();
			vec3d.rotateY((float) (-Math.PI / 4));
			double d = this.dragon.partHead.x;
			double e = this.dragon.partHead.y + (double)(this.dragon.partHead.height / 2.0F);
			double f = this.dragon.partHead.z;

			for (int i = 0; i < 8; i++) {
				double g = d + this.dragon.getRandom().nextGaussian() / 2.0;
				double h = e + this.dragon.getRandom().nextGaussian() / 2.0;
				double j = f + this.dragon.getRandom().nextGaussian() / 2.0;

				for (int k = 0; k < 6; k++) {
					this.dragon.world.addParticle(ParticleType.DRAGON_BREATH, g, h, j, -vec3d.x * 0.08F * (double)k, -vec3d.y * 0.6F, -vec3d.z * 0.08F * (double)k);
				}

				vec3d.rotateY((float) (Math.PI / 16));
			}
		}
	}

	@Override
	public void method_13183() {
		this.field_14685++;
		if (this.field_14685 >= 200) {
			if (this.field_14686 >= 4) {
				this.dragon.method_13168().method_13203(class_2993.TAKEOFF);
			} else {
				this.dragon.method_13168().method_13203(class_2993.SITTING_SCANNING);
			}
		} else if (this.field_14685 == 10) {
			Vec3d vec3d = new Vec3d(this.dragon.partHead.x - this.dragon.x, 0.0, this.dragon.partHead.z - this.dragon.z).normalize();
			float f = 5.0F;
			double d = this.dragon.partHead.x + vec3d.x * (double)f / 2.0;
			double e = this.dragon.partHead.z + vec3d.z * (double)f / 2.0;
			double g = this.dragon.partHead.y + (double)(this.dragon.partHead.height / 2.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(d), MathHelper.floor(g), MathHelper.floor(e));

			while (this.dragon.world.isAir(mutable)) {
				mutable.setPosition(MathHelper.floor(d), MathHelper.floor(--g), MathHelper.floor(e));
			}

			g = (double)(MathHelper.floor(g) + 1);
			this.field_14687 = new AreaEffectCloudEntity(this.dragon.world, d, g, e);
			this.field_14687.method_12954(this.dragon);
			this.field_14687.setRadius(f);
			this.field_14687.setDuration(200);
			this.field_14687.setParticleType(ParticleType.DRAGON_BREATH);
			this.field_14687.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE));
			this.dragon.world.spawnEntity(this.field_14687);
		}
	}

	@Override
	public void method_13184() {
		this.field_14685 = 0;
		this.field_14686++;
	}

	@Override
	public void method_13185() {
		if (this.field_14687 != null) {
			this.field_14687.remove();
			this.field_14687 = null;
		}
	}

	@Override
	public class_2993<class_2989> method_13189() {
		return class_2993.SITTING_FLAMING;
	}

	public void method_13190() {
		this.field_14686 = 0;
	}
}
