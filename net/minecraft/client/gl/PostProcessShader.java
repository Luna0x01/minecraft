package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
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
	private Matrix4f projectionMatrix;

	public PostProcessShader(ResourceManager resourceManager, String string, Framebuffer framebuffer, Framebuffer framebuffer2) throws IOException {
		this.program = new JsonGlProgram(resourceManager, string);
		this.input = framebuffer;
		this.output = framebuffer2;
	}

	public void close() {
		this.program.close();
	}

	public void addAuxTarget(String string, Object object, int i, int j) {
		this.samplerNames.add(this.samplerNames.size(), string);
		this.samplerValues.add(this.samplerValues.size(), object);
		this.samplerWidths.add(this.samplerWidths.size(), i);
		this.samplerHeights.add(this.samplerHeights.size(), j);
	}

	public void setProjectionMatrix(Matrix4f matrix4f) {
		this.projectionMatrix = matrix4f;
	}

	public void render(float f) {
		this.input.endWrite();
		float g = (float)this.output.textureWidth;
		float h = (float)this.output.textureHeight;
		RenderSystem.viewport(0, 0, (int)g, (int)h);
		this.program.bindSampler("DiffuseSampler", this.input);

		for (int i = 0; i < this.samplerValues.size(); i++) {
			this.program.bindSampler((String)this.samplerNames.get(i), this.samplerValues.get(i));
			this.program
				.getUniformByNameOrDummy("AuxSize" + i)
				.set((float)((Integer)this.samplerWidths.get(i)).intValue(), (float)((Integer)this.samplerHeights.get(i)).intValue());
		}

		this.program.getUniformByNameOrDummy("ProjMat").set(this.projectionMatrix);
		this.program.getUniformByNameOrDummy("InSize").set((float)this.input.textureWidth, (float)this.input.textureHeight);
		this.program.getUniformByNameOrDummy("OutSize").set(g, h);
		this.program.getUniformByNameOrDummy("Time").set(f);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.program
			.getUniformByNameOrDummy("ScreenSize")
			.set((float)minecraftClient.getWindow().getFramebufferWidth(), (float)minecraftClient.getWindow().getFramebufferHeight());
		this.program.enable();
		this.output.clear(MinecraftClient.IS_SYSTEM_MAC);
		this.output.beginWrite(false);
		RenderSystem.depthMask(false);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)g, 0.0, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)g, (double)h, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, (double)h, 500.0).color(255, 255, 255, 255).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.depthMask(true);
		this.program.disable();
		this.output.endWrite();
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
