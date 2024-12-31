package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.resource.ResourceManager;

public class PostProcessShader implements AutoCloseable {
	private final JsonGlProgram program;
	public final Framebuffer input;
	public final Framebuffer output;
	private final List<Object> samplerValues = Lists.newArrayList();
	private final List<String> samplerNames = Lists.newArrayList();
	private final List<Integer> samplerWidths = Lists.newArrayList();
	private final List<Integer> samplerHeights = Lists.newArrayList();
	private Matrix4f field_20976;

	public PostProcessShader(ResourceManager resourceManager, String string, Framebuffer framebuffer, Framebuffer framebuffer2) throws IOException {
		this.program = new JsonGlProgram(resourceManager, string);
		this.input = framebuffer;
		this.output = framebuffer2;
	}

	public void close() {
		this.program.close();
	}

	public void addAuxTarget(String name, Object target, int width, int height) {
		this.samplerNames.add(this.samplerNames.size(), name);
		this.samplerValues.add(this.samplerValues.size(), target);
		this.samplerWidths.add(this.samplerWidths.size(), width);
		this.samplerHeights.add(this.samplerHeights.size(), height);
	}

	private void preRender() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableFog();
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture();
		GlStateManager.bindTexture(0);
	}

	public void method_19443(Matrix4f matrix4f) {
		this.field_20976 = matrix4f;
	}

	public void render(float time) {
		this.preRender();
		this.input.unbind();
		float f = (float)this.output.textureWidth;
		float g = (float)this.output.textureHeight;
		GlStateManager.viewport(0, 0, (int)f, (int)g);
		this.program.bindSampler("DiffuseSampler", this.input);

		for (int i = 0; i < this.samplerValues.size(); i++) {
			this.program.bindSampler((String)this.samplerNames.get(i), this.samplerValues.get(i));
			this.program
				.method_6937("AuxSize" + i)
				.method_6977((float)((Integer)this.samplerWidths.get(i)).intValue(), (float)((Integer)this.samplerHeights.get(i)).intValue());
		}

		this.program.method_6937("ProjMat").method_19442(this.field_20976);
		this.program.method_6937("InSize").method_6977((float)this.input.textureWidth, (float)this.input.textureHeight);
		this.program.method_6937("OutSize").method_6977(f, g);
		this.program.method_6937("Time").method_6976(time);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.program.method_6937("ScreenSize").method_6977((float)minecraftClient.field_19944.method_18317(), (float)minecraftClient.field_19944.method_18318());
		this.program.enable();
		this.output.clear();
		this.output.bind(false);
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)f, (double)g, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, (double)g, 500.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
		this.program.disable();
		this.output.unbind();
		this.input.endRead();

		for (Object object : this.samplerValues) {
			if (object instanceof Framebuffer) {
				((Framebuffer)object).endRead();
			}
		}
	}

	public JsonGlProgram getProgram() {
		return this.program;
	}
}
