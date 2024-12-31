package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class class_4214 extends Particle {
	private float field_20636;

	protected class_4214(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setMiscTexture(32);
		this.maxAge = (int)(Math.random() * 60.0) + 30;
		this.field_14950 = false;
		this.velocityX = 0.0;
		this.velocityY = -0.05;
		this.velocityZ = 0.0;
		this.method_12244(0.02F, 0.02F);
		this.scale = this.scale * (this.field_13438.nextFloat() * 0.6F + 0.2F);
		this.gravityStrength = 0.002F;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		float f = 0.6F;
		this.velocityX = this.velocityX + (double)(0.6F * MathHelper.cos(this.field_20636));
		this.velocityZ = this.velocityZ + (double)(0.6F * MathHelper.sin(this.field_20636));
		this.velocityX *= 0.07;
		this.velocityZ *= 0.07;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		if (!this.field_13424.getFluidState(new BlockPos(this.field_13428, this.field_13429, this.field_13430)).matches(FluidTags.WATER)) {
			this.method_12251();
		}

		if (this.age++ >= this.maxAge || this.field_13434) {
			this.method_12251();
		}

		this.field_20636 = (float)((double)this.field_20636 + 0.08);
	}

	public static class class_4215 implements ParticleFactory<class_4343> {
		@Nullable
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new class_4214(world, d, e, f);
		}
	}
}
