package net.minecraft.client.render;

public class Tessellator {
	private BufferBuilder buffer;
	private BufferRenderer renderer = new BufferRenderer();
	private static final Tessellator INSTANCE = new Tessellator(2097152);

	public static Tessellator getInstance() {
		return INSTANCE;
	}

	public Tessellator(int i) {
		this.buffer = new BufferBuilder(i);
	}

	public void draw() {
		this.buffer.end();
		this.renderer.draw(this.buffer);
	}

	public BufferBuilder getBuffer() {
		return this.buffer;
	}
}
