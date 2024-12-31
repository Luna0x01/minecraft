package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class StreamIndicatorHud {
	private static final Identifier STREAM_INDICATOR = new Identifier("textures/gui/stream_indicator.png");
	private final MinecraftClient client;
	private float streamIndicatorAlpha = 1.0F;
	private int streamIndicatorAlphaFade = 1;

	public StreamIndicatorHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void render(int x, int y) {
		if (this.client.getTwitchStreamProvider().isLive()) {
			GlStateManager.enableBlend();
			int i = this.client.getTwitchStreamProvider().getViewerCount();
			if (i > 0) {
				String string = "" + i;
				int j = this.client.textRenderer.getStringWidth(string);
				int k = 20;
				int l = x - j - 1;
				int m = y + 20 - 1;
				int o = y + 20 + this.client.textRenderer.fontHeight - 1;
				GlStateManager.disableTexture();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				GlStateManager.color(0.0F, 0.0F, 0.0F, (0.65F + 0.35000002F * this.streamIndicatorAlpha) / 2.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)l, (double)o, 0.0).next();
				bufferBuilder.vertex((double)x, (double)o, 0.0).next();
				bufferBuilder.vertex((double)x, (double)m, 0.0).next();
				bufferBuilder.vertex((double)l, (double)m, 0.0).next();
				tessellator.draw();
				GlStateManager.enableTexture();
				this.client.textRenderer.draw(string, x - j, y + 20, 16777215);
			}

			this.render(x, y, this.getPauseTextureStart(), 0);
			this.render(x, y, this.getMuteTextureStart(), 17);
		}
	}

	private void render(int x, int y, int u, int v) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F + 0.35000002F * this.streamIndicatorAlpha);
		this.client.getTextureManager().bindTexture(STREAM_INDICATOR);
		float f = 150.0F;
		float g = 0.0F;
		float h = (float)u * 0.015625F;
		float i = 1.0F;
		float j = (float)(u + 16) * 0.015625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)(x - 16 - v), (double)(y + 16), (double)f).texture((double)g, (double)j).next();
		bufferBuilder.vertex((double)(x - v), (double)(y + 16), (double)f).texture((double)i, (double)j).next();
		bufferBuilder.vertex((double)(x - v), (double)(y + 0), (double)f).texture((double)i, (double)h).next();
		bufferBuilder.vertex((double)(x - 16 - v), (double)(y + 0), (double)f).texture((double)g, (double)h).next();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private int getPauseTextureStart() {
		return this.client.getTwitchStreamProvider().isPaused() ? 16 : 0;
	}

	private int getMuteTextureStart() {
		return this.client.getTwitchStreamProvider().isMuted() ? 48 : 32;
	}

	public void tick() {
		if (this.client.getTwitchStreamProvider().isLive()) {
			this.streamIndicatorAlpha = this.streamIndicatorAlpha + 0.025F * (float)this.streamIndicatorAlphaFade;
			if (this.streamIndicatorAlpha < 0.0F) {
				this.streamIndicatorAlphaFade *= -1;
				this.streamIndicatorAlpha = 0.0F;
			} else if (this.streamIndicatorAlpha > 1.0F) {
				this.streamIndicatorAlphaFade *= -1;
				this.streamIndicatorAlpha = 1.0F;
			}
		} else {
			this.streamIndicatorAlpha = 1.0F;
			this.streamIndicatorAlphaFade = 1;
		}
	}
}
