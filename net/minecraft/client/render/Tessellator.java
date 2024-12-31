package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

public class Tessellator {
	private final BufferBuilder buffer;
	private static final Tessellator INSTANCE = new Tessellator();

	public static Tessellator getInstance() {
		RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
		return INSTANCE;
	}

	public Tessellator(int i) {
		this.buffer = new BufferBuilder(i);
	}

	public Tessellator() {
		this(2097152);
	}

	public void draw() {
		this.buffer.end();
		BufferRenderer.draw(this.buffer);
	}

	public BufferBuilder getBuffer() {
		return this.buffer;
	}
}
