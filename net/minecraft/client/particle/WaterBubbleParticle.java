package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterBubbleParticle extends Particle {
	protected WaterBubbleParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.setMiscTexture(32);
		this.method_12244(0.02F, 0.02F);
		this.scale = this.scale * (this.field_13438.nextFloat() * 0.6F + 0.2F);
		this.velocityX = g * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityY = h * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.velocityZ = i * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.velocityY += 0.002;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.85F;
		this.velocityY *= 0.85F;
		this.velocityZ *= 0.85F;
		if (!this.field_13424.getFluidState(new BlockPos(this.field_13428, this.field_13429, this.field_13430)).matches(FluidTags.WATER)) {
			this.method_12251();
		}

		if (this.maxAge-- <= 0) {
			this.method_12251();
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new WaterBubbleParticle(world, d, e, f, g, h, i);
		}
	}
}
