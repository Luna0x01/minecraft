package net.minecraft.client.render;

public class ClientTickTracker {
	public int ticksThisFrame;
	public float tickDelta;
	public float lastFrameDuration;
	private long field_1042;
	private final float field_15885;

	public ClientTickTracker(float f, long l) {
		this.field_15885 = 1000.0F / f;
		this.field_1042 = l;
	}

	public void method_18274(long l) {
		this.lastFrameDuration = (float)(l - this.field_1042) / this.field_15885;
		this.field_1042 = l;
		this.tickDelta = this.tickDelta + this.lastFrameDuration;
		this.ticksThisFrame = (int)this.tickDelta;
		this.tickDelta = this.tickDelta - (float)this.ticksThisFrame;
	}
}
