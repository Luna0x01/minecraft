package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

public class Framebuffer {
	public int textureWidth;
	public int textureHeight;
	public int viewportWidth;
	public int viewportHeight;
	public boolean useDepthAttachment;
	public int fbo;
	public int colorAttachment;
	public int depthAttachment;
	public float[] clearColor;
	public int texFilter;

	public Framebuffer(int i, int j, boolean bl) {
		this.useDepthAttachment = bl;
		this.fbo = -1;
		this.colorAttachment = -1;
		this.depthAttachment = -1;
		this.clearColor = new float[4];
		this.clearColor[0] = 1.0F;
		this.clearColor[1] = 1.0F;
		this.clearColor[2] = 1.0F;
		this.clearColor[3] = 0.0F;
		this.resize(i, j);
	}

	public void resize(int width, int height) {
		if (!GLX.supportsFbo()) {
			this.viewportWidth = width;
			this.viewportHeight = height;
		} else {
			GlStateManager.enableDepthTest();
			if (this.fbo >= 0) {
				this.delete();
			}

			this.attachTexture(width, height);
			this.checkFramebufferStatus();
			GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
		}
	}

	public void delete() {
		if (GLX.supportsFbo()) {
			this.endRead();
			this.unbind();
			if (this.depthAttachment > -1) {
				GLX.advancedDeleteRenderBuffers(this.depthAttachment);
				this.depthAttachment = -1;
			}

			if (this.colorAttachment > -1) {
				TextureUtil.deleteTexture(this.colorAttachment);
				this.colorAttachment = -1;
			}

			if (this.fbo > -1) {
				GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
				GLX.advancedDeleteFrameBuffers(this.fbo);
				this.fbo = -1;
			}
		}
	}

	public void attachTexture(int width, int height) {
		this.viewportWidth = width;
		this.viewportHeight = height;
		this.textureWidth = width;
		this.textureHeight = height;
		if (!GLX.supportsFbo()) {
			this.clear();
		} else {
			this.fbo = GLX.advancedGenFrameBuffers();
			this.colorAttachment = TextureUtil.getTexLevelParameter();
			if (this.useDepthAttachment) {
				this.depthAttachment = GLX.advancedGenRenderBuffers();
			}

			this.setTexFilter(9728);
			GlStateManager.bindTexture(this.colorAttachment);
			GL11.glTexImage2D(3553, 0, 32856, this.textureWidth, this.textureHeight, 0, 6408, 5121, (ByteBuffer)null);
			GLX.advancedBindFramebuffer(GLX.framebuffer, this.fbo);
			GLX.advancedFrameBufferTexture2D(GLX.framebuffer, GLX.colorAttachment, 3553, this.colorAttachment, 0);
			if (this.useDepthAttachment) {
				GLX.advancedBindRenderBuffer(GLX.renderbuffer, this.depthAttachment);
				GLX.advancedRenderBufferStorage(GLX.renderbuffer, 33190, this.textureWidth, this.textureHeight);
				GLX.advancedFramebufferRenderbuffer(GLX.framebuffer, GLX.depthAttachment, GLX.renderbuffer, this.depthAttachment);
			}

			this.clear();
			this.endRead();
		}
	}

	public void setTexFilter(int texFilter) {
		if (GLX.supportsFbo()) {
			this.texFilter = texFilter;
			GlStateManager.bindTexture(this.colorAttachment);
			GL11.glTexParameterf(3553, 10241, (float)texFilter);
			GL11.glTexParameterf(3553, 10240, (float)texFilter);
			GL11.glTexParameterf(3553, 10242, 10496.0F);
			GL11.glTexParameterf(3553, 10243, 10496.0F);
			GlStateManager.bindTexture(0);
		}
	}

	public void checkFramebufferStatus() {
		int i = GLX.advancedCheckFrameBufferStatus(GLX.framebuffer);
		if (i != GLX.completeFramebuffer) {
			if (i == GLX.incompleteFramebufferAttachment) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == GLX.incompleteFramebufferAttachmentMiss) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == GLX.incompleteFramebufferAttachmentDraw) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == GLX.incompleteFramebufferAttachmentRead) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}

	public void beginRead() {
		if (GLX.supportsFbo()) {
			GlStateManager.bindTexture(this.colorAttachment);
		}
	}

	public void endRead() {
		if (GLX.supportsFbo()) {
			GlStateManager.bindTexture(0);
		}
	}

	public void bind(boolean viewPort) {
		if (GLX.supportsFbo()) {
			GLX.advancedBindFramebuffer(GLX.framebuffer, this.fbo);
			if (viewPort) {
				GlStateManager.viewport(0, 0, this.viewportWidth, this.viewportHeight);
			}
		}
	}

	public void unbind() {
		if (GLX.supportsFbo()) {
			GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
		}
	}

	public void setClearColor(float r, float g, float b, float a) {
		this.clearColor[0] = r;
		this.clearColor[1] = g;
		this.clearColor[2] = b;
		this.clearColor[3] = a;
	}

	public void draw(int width, int height) {
		this.drawInternal(width, height, true);
	}

	public void drawInternal(int width, int height, boolean colorMaterial) {
		if (GLX.supportsFbo()) {
			GlStateManager.colorMask(true, true, true, false);
			GlStateManager.disableDepthTest();
			GlStateManager.depthMask(false);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)width, (double)height, 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.viewport(0, 0, width, height);
			GlStateManager.enableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableAlphaTest();
			if (colorMaterial) {
				GlStateManager.disableBlend();
				GlStateManager.enableColorMaterial();
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.beginRead();
			float f = (float)width;
			float g = (float)height;
			float h = (float)this.viewportWidth / (float)this.textureWidth;
			float i = (float)this.viewportHeight / (float)this.textureHeight;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
			bufferBuilder.vertex((double)f, (double)g, 0.0).texture((double)h, 0.0).color(255, 255, 255, 255).next();
			bufferBuilder.vertex((double)f, 0.0, 0.0).texture((double)h, (double)i).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, (double)i).color(255, 255, 255, 255).next();
			tessellator.draw();
			this.endRead();
			GlStateManager.depthMask(true);
			GlStateManager.colorMask(true, true, true, true);
		}
	}

	public void clear() {
		this.bind(true);
		GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		int i = 16384;
		if (this.useDepthAttachment) {
			GlStateManager.clearDepth(1.0);
			i |= 256;
		}

		GlStateManager.clear(i);
		this.unbind();
	}
}
