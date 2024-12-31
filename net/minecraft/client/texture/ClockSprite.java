package net.minecraft.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class ClockSprite extends Sprite {
	private double field_2146;
	private double field_2147;

	public ClockSprite(String string) {
		super(string);
	}

	@Override
	public void update() {
		if (!this.frames.isEmpty()) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			double d = 0.0;
			if (minecraftClient.world != null && minecraftClient.player != null) {
				d = (double)minecraftClient.world.getSkyAngle(1.0F);
				if (!minecraftClient.world.dimension.canPlayersSleep()) {
					d = Math.random();
				}
			}

			double e = d - this.field_2146;

			while (e < -0.5) {
				e++;
			}

			while (e >= 0.5) {
				e--;
			}

			e = MathHelper.clamp(e, -1.0, 1.0);
			this.field_2147 += e * 0.1;
			this.field_2147 *= 0.8;
			this.field_2146 = this.field_2146 + this.field_2147;
			int i = (int)((this.field_2146 + 1.0) * (double)this.frames.size()) % this.frames.size();

			while (i < 0) {
				i = (i + this.frames.size()) % this.frames.size();
			}

			if (i != this.frameIndex) {
				this.frameIndex = i;
				TextureUtil.method_7027((int[][])this.frames.get(this.frameIndex), this.width, this.height, this.x, this.y, false, false);
			}
		}
	}
}
