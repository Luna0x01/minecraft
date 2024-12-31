package net.minecraft.client.render;

public class RenderTickCounter {
	public int ticksThisFrame;
	public float tickDelta;
	public float lastFrameDuration;
	private long prevTimeMillis;
	private final float timeScale;

	public RenderTickCounter(float f, long l) {
		this.timeScale = 1000.0F / f;
		this.prevTimeMillis = l;
	}

	public void beginRenderTick(long l) {
		this.lastFrameDuration = (float)(l - this.prevTimeMillis) / this.timeScale;
		this.prevTimeMillis = l;
		this.tickDelta = this.tickDelta + this.lastFrameDuration;
		this.ticksThisFrame = (int)this.tickDelta;
		this.tickDelta = this.tickDelta - (float)this.ticksThisFrame;
	}
}
