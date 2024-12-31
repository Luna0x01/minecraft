package net.minecraft;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.class_2864;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class class_4210 extends class_2864 {
	protected class_4210(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0, 8, 0.0F);
		this.scale = 5.0F;
		this.method_12250(1.0F);
		this.setColor(0.0F, 0.0F, 0.0F);
		this.setMiscTexture(0);
		this.maxAge = (int)((double)(this.scale * 12.0F) / (Math.random() * 0.8F + 0.2F));
		this.field_14950 = false;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.method_13844(0.0F);
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		if (this.age > this.maxAge / 2) {
			this.method_12250(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
		}

		this.setMiscTexture(this.field_13448 + this.field_13442 - 1 - this.age * this.field_13442 / this.maxAge);
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		if (this.field_13424.getBlockState(new BlockPos(this.field_13428, this.field_13429, this.field_13430)).isAir()) {
			this.velocityY -= 0.008F;
		}

		this.velocityX *= 0.92F;
		this.velocityY *= 0.92F;
		this.velocityZ *= 0.92F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class class_4211 implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new class_4210(world, d, e, f, g, h, i);
		}
	}
}
