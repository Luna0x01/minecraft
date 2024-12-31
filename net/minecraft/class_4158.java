package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4158 extends Screen {
	private static final Logger field_20279 = LogManager.getLogger();
	private static final Identifier field_20280 = new Identifier("textures/gui/title/mojang.png");
	private Identifier field_20281;

	@Override
	protected void init() {
		try {
			InputStream inputStream = this.client.getResourcePackLoader().method_19542().method_5897(class_4455.CLIENT_RESOURCES, field_20280);
			this.field_20281 = this.client.getTextureManager().registerDynamicTexture("logo", new NativeImageBackedTexture(class_4277.method_19472(inputStream)));
		} catch (IOException var2) {
			field_20279.error("Unable to load logo: {}", field_20280, var2);
		}
	}

	@Override
	public void removed() {
		this.client.getTextureManager().close(this.field_20281);
		this.field_20281 = null;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		Framebuffer framebuffer = new Framebuffer(this.width, this.height, true);
		framebuffer.bind(false);
		this.client.getTextureManager().bindTexture(this.field_20281);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.client.field_19944.method_18318(), 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)this.client.field_19944.method_18317(), (double)this.client.field_19944.method_18318(), 0.0)
			.texture(0.0, 0.0)
			.color(255, 255, 255, 255)
			.next();
		bufferBuilder.vertex((double)this.client.field_19944.method_18317(), 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int i = 256;
		int j = 256;
		this.client
			.drawLogo((this.client.field_19944.method_18321() - 256) / 2, (this.client.field_19944.method_18322() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		framebuffer.unbind();
		framebuffer.draw(this.client.field_19944.method_18317(), this.client.field_19944.method_18318());
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
	}
}
