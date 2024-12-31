package net.minecraft.client.render;

public class VertexBufferUploader extends BufferRenderer {
	private VertexBuffer buffer = null;

	@Override
	public void draw(BufferBuilder builder) {
		builder.reset();
		this.buffer.data(builder.getByteBuffer());
	}

	public void setBuffer(VertexBuffer buffer) {
		this.buffer = buffer;
	}
}
