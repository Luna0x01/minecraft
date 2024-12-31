package net.minecraft.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class ClientTickTracker {
	float ticksPerSecond;
	private double field_1041;
	public int ticksThisFrame;
	public float tickDelta;
	public float field_1039 = 1.0F;
	public float lastFrameDuration;
	private long field_1042;
	private long field_1043;
	private long field_1044;
	private double field_1045 = 1.0;

	public ClientTickTracker(float f) {
		this.ticksPerSecond = f;
		this.field_1042 = MinecraftClient.getTime();
		this.field_1043 = System.nanoTime() / 1000000L;
	}

	public void tick() {
		long l = MinecraftClient.getTime();
		long m = l - this.field_1042;
		long n = System.nanoTime() / 1000000L;
		double d = (double)n / 1000.0;
		if (m <= 1000L && m >= 0L) {
			this.field_1044 += m;
			if (this.field_1044 > 1000L) {
				long o = n - this.field_1043;
				double e = (double)this.field_1044 / (double)o;
				this.field_1045 = this.field_1045 + (e - this.field_1045) * 0.2F;
				this.field_1043 = n;
				this.field_1044 = 0L;
			}

			if (this.field_1044 < 0L) {
				this.field_1043 = n;
			}
		} else {
			this.field_1041 = d;
		}

		this.field_1042 = l;
		double f = (d - this.field_1041) * this.field_1045;
		this.field_1041 = d;
		f = MathHelper.clamp(f, 0.0, 1.0);
		this.lastFrameDuration = (float)((double)this.lastFrameDuration + f * (double)this.field_1039 * (double)this.ticksPerSecond);
		this.ticksThisFrame = (int)this.lastFrameDuration;
		this.lastFrameDuration = this.lastFrameDuration - (float)this.ticksThisFrame;
		if (this.ticksThisFrame > 10) {
			this.ticksThisFrame = 10;
		}

		this.tickDelta = this.lastFrameDuration;
	}
}
