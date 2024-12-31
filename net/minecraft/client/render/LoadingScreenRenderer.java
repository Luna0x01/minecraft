package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.AnError;
import net.minecraft.client.util.Window;
import net.minecraft.util.ProgressListener;

public class LoadingScreenRenderer implements ProgressListener {
	private String field_1028 = "";
	private MinecraftClient client;
	private String title = "";
	private long field_1031 = MinecraftClient.getTime();
	private boolean field_1032;
	private Window window;
	private Framebuffer framebuffer;

	public LoadingScreenRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.window = new Window(minecraftClient);
		this.framebuffer = new Framebuffer(minecraftClient.width, minecraftClient.height, false);
		this.framebuffer.setTexFilter(9728);
	}

	@Override
	public void setTitleAndTask(String title) {
		this.field_1032 = false;
		this.method_884(title);
	}

	@Override
	public void setTitle(String title) {
		this.field_1032 = true;
		this.method_884(title);
	}

	private void method_884(String title) {
		this.title = title;
		if (!this.client.running) {
			if (!this.field_1032) {
				throw new AnError();
			}
		} else {
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			if (GLX.supportsFbo()) {
				int i = this.window.getScaleFactor();
				GlStateManager.ortho(0.0, (double)(this.window.getWidth() * i), (double)(this.window.getHeight() * i), 0.0, 100.0, 300.0);
			} else {
				Window window = new Window(this.client);
				GlStateManager.ortho(0.0, window.getScaledWidth(), window.getScaledHeight(), 0.0, 100.0, 300.0);
			}

			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -200.0F);
		}
	}

	@Override
	public void setTask(String task) {
		if (!this.client.running) {
			if (!this.field_1032) {
				throw new AnError();
			}
		} else {
			this.field_1031 = 0L;
			this.field_1028 = task;
			this.setProgressPercentage(-1);
			this.field_1031 = 0L;
		}
	}

	@Override
	public void setProgressPercentage(int percentage) {
		if (!this.client.running) {
			if (!this.field_1032) {
				throw new AnError();
			}
		} else {
			long l = MinecraftClient.getTime();
			if (l - this.field_1031 >= 100L) {
				this.field_1031 = l;
				Window window = new Window(this.client);
				int i = window.getScaleFactor();
				int j = window.getWidth();
				int k = window.getHeight();
				if (GLX.supportsFbo()) {
					this.framebuffer.clear();
				} else {
					GlStateManager.clear(256);
				}

				this.framebuffer.bind(false);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.ortho(0.0, window.getScaledWidth(), window.getScaledHeight(), 0.0, 100.0, 300.0);
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, 0.0F, -200.0F);
				if (!GLX.supportsFbo()) {
					GlStateManager.clear(16640);
				}

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
				float f = 32.0F;
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(0.0, (double)k, 0.0).texture(0.0, (double)((float)k / f)).color(64, 64, 64, 255).next();
				bufferBuilder.vertex((double)j, (double)k, 0.0).texture((double)((float)j / f), (double)((float)k / f)).color(64, 64, 64, 255).next();
				bufferBuilder.vertex((double)j, 0.0, 0.0).texture((double)((float)j / f), 0.0).color(64, 64, 64, 255).next();
				bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, 0.0).color(64, 64, 64, 255).next();
				tessellator.draw();
				if (percentage >= 0) {
					int m = 100;
					int n = 2;
					int o = j / 2 - m / 2;
					int p = k / 2 + 16;
					GlStateManager.disableTexture();
					bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex((double)o, (double)p, 0.0).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)o, (double)(p + n), 0.0).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)(o + m), (double)(p + n), 0.0).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)(o + m), (double)p, 0.0).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)o, (double)p, 0.0).color(128, 255, 128, 255).next();
					bufferBuilder.vertex((double)o, (double)(p + n), 0.0).color(128, 255, 128, 255).next();
					bufferBuilder.vertex((double)(o + percentage), (double)(p + n), 0.0).color(128, 255, 128, 255).next();
					bufferBuilder.vertex((double)(o + percentage), (double)p, 0.0).color(128, 255, 128, 255).next();
					tessellator.draw();
					GlStateManager.enableTexture();
				}

				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				this.client
					.textRenderer
					.drawWithShadow(this.title, (float)((j - this.client.textRenderer.getStringWidth(this.title)) / 2), (float)(k / 2 - 4 - 16), 16777215);
				this.client
					.textRenderer
					.drawWithShadow(this.field_1028, (float)((j - this.client.textRenderer.getStringWidth(this.field_1028)) / 2), (float)(k / 2 - 4 + 8), 16777215);
				this.framebuffer.unbind();
				if (GLX.supportsFbo()) {
					this.framebuffer.draw(j * i, k * i);
				}

				this.client.updateDisplay();

				try {
					Thread.yield();
				} catch (Exception var15) {
				}
			}
		}
	}

	@Override
	public void setDone() {
	}
}
