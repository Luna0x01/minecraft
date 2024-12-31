package net.minecraft.client.render;

public class BlockBufferBuilderStorage {
	private final BufferBuilder[] renderLayers = new BufferBuilder[RenderLayer.values().length];

	public BlockBufferBuilderStorage() {
		this.renderLayers[RenderLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
		this.renderLayers[RenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
		this.renderLayers[RenderLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
		this.renderLayers[RenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
	}

	public BufferBuilder get(RenderLayer renderLayer) {
		return this.renderLayers[renderLayer.ordinal()];
	}

	public BufferBuilder get(int index) {
		return this.renderLayers[index];
	}
}
