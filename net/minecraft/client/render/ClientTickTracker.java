package net.minecraft.client.render;

import net.minecraft.client.MinecraftClient;

public class ClientTickTracker {
	public int ticksThisFrame;
	public float tickDelta;
	public float lastFrameDuration;
	private long field_1042;
	private float field_15885;

	public ClientTickTracker(float f) {
		this.field_15885 = 1000.0F / f;
		this.field_1042 = MinecraftClient.getTime();
	}

	public void tick() {
		long l = MinecraftClient.getTime();
		this.lastFrameDuration = (float)(l - this.field_1042) / this.field_15885;
		this.field_1042 = l;
		this.tickDelta = this.tickDelta + this.lastFrameDuration;
		this.ticksThisFrame = (int)this.tickDelta;
		this.tickDelta = this.tickDelta - (float)this.ticksThisFrame;
	}
}
