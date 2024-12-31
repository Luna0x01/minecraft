package net.minecraft.client.particle;

import net.minecraft.world.World;

public class class_2864 extends Particle {
	private final int field_13448;
	private final int field_13442;
	private final float field_13443;
	private float field_13444;
	private float field_13445;
	private float field_13446;
	private boolean field_13447;

	public class_2864(World world, double d, double e, double f, int i, int j, float g) {
		super(world, d, e, f);
		this.field_13448 = i;
		this.field_13442 = j;
		this.field_13443 = g;
	}

	public void method_12258(int i) {
		float f = (float)((i & 0xFF0000) >> 16) / 255.0F;
		float g = (float)((i & 0xFF00) >> 8) / 255.0F;
		float h = (float)((i & 0xFF) >> 0) / 255.0F;
		float j = 1.0F;
		this.setColor(f * j, g * j, h * j);
	}

	public void method_12259(int i) {
		this.field_13444 = (float)((i & 0xFF0000) >> 16) / 255.0F;
		this.field_13445 = (float)((i & 0xFF00) >> 8) / 255.0F;
		this.field_13446 = (float)((i & 0xFF) >> 0) / 255.0F;
		this.field_13447 = true;
	}

	@Override
	public boolean method_12248() {
		return true;
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
			if (this.field_13447) {
				this.red = this.red + (this.field_13444 - this.red) * 0.2F;
				this.green = this.green + (this.field_13445 - this.green) * 0.2F;
				this.blue = this.blue + (this.field_13446 - this.blue) * 0.2F;
			}
		}

		this.setMiscTexture(this.field_13448 + (this.field_13442 - 1 - this.age * this.field_13442 / this.maxAge));
		this.velocityY = this.velocityY + (double)this.field_13443;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.91F;
		this.velocityY *= 0.91F;
		this.velocityZ *= 0.91F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	@Override
	public int method_12243(float f) {
		return 15728880;
	}
}
