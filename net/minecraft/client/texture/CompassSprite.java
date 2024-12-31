package net.minecraft.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CompassSprite extends Sprite {
	public double field_2150;
	public double field_2151;
	public static String field_11201;

	public CompassSprite(String string) {
		super(string);
		field_11201 = string;
	}

	@Override
	public void update() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.world != null && minecraftClient.player != null) {
			this.method_5241(minecraftClient.world, minecraftClient.player.x, minecraftClient.player.z, (double)minecraftClient.player.yaw, false, false);
		} else {
			this.method_5241(null, 0.0, 0.0, 0.0, true, false);
		}
	}

	public void method_5241(World world, double d, double e, double f, boolean bl, boolean bl2) {
		if (!this.frames.isEmpty()) {
			double g = 0.0;
			if (world != null && !bl) {
				BlockPos blockPos = world.getSpawnPos();
				double h = (double)blockPos.getX() - d;
				double i = (double)blockPos.getZ() - e;
				f %= 360.0;
				g = -((f - 90.0) * Math.PI / 180.0 - Math.atan2(i, h));
				if (!world.dimension.canPlayersSleep()) {
					g = Math.random() * (float) Math.PI * 2.0;
				}
			}

			if (bl2) {
				this.field_2150 = g;
			} else {
				double j = g - this.field_2150;

				while (j < -Math.PI) {
					j += Math.PI * 2;
				}

				while (j >= Math.PI) {
					j -= Math.PI * 2;
				}

				j = MathHelper.clamp(j, -1.0, 1.0);
				this.field_2151 += j * 0.1;
				this.field_2151 *= 0.8;
				this.field_2150 = this.field_2150 + this.field_2151;
			}

			int k = (int)((this.field_2150 / (Math.PI * 2) + 1.0) * (double)this.frames.size()) % this.frames.size();

			while (k < 0) {
				k = (k + this.frames.size()) % this.frames.size();
			}

			if (k != this.frameIndex) {
				this.frameIndex = k;
				TextureUtil.method_7027((int[][])this.frames.get(this.frameIndex), this.width, this.height, this.x, this.y, false, false);
			}
		}
	}
}
