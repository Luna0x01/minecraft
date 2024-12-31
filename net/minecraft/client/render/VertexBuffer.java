package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;

public class VertexBuffer {
	private int id;
	private final VertexFormat format;
	private int size;

	public VertexBuffer(VertexFormat vertexFormat) {
		this.format = vertexFormat;
		this.id = GLX.gl15GenBuffers();
	}

	public void bind() {
		GLX.gl15BindBuffer(GLX.arrayBuffer, this.id);
	}

	public void data(ByteBuffer buffer) {
		this.bind();
		GLX.gl15BufferData(GLX.arrayBuffer, buffer, 35044);
		this.unbind();
		this.size = buffer.limit() / this.format.getVertexSize();
	}

	public void draw(int mode) {
		GL11.glDrawArrays(mode, 0, this.size);
	}

	public void unbind() {
		GLX.gl15BindBuffer(GLX.arrayBuffer, 0);
	}

	public void delete() {
		if (this.id >= 0) {
			GLX.gl15DeleteBuffers(this.id);
			this.id = -1;
		}
	}
}
