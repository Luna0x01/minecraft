package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.Identifier;

public class EndPortalBlockEntityRenderer extends BlockEntityRenderer<EndPortalBlockEntity> {
	private static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
	private static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(16);

	public void render(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		float h = (float)this.dispatcher.cameraX;
		float j = (float)this.dispatcher.cameraY;
		float k = (float)this.dispatcher.cameraZ;
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		float l = 0.75F;

		for (int m = 0; m < 16; m++) {
			GlStateManager.pushMatrix();
			float n = (float)(16 - m);
			float o = 0.0625F;
			float p = 1.0F / (n + 1.0F);
			if (m == 0) {
				this.bindTexture(SKY_TEXTURE);
				p = 0.1F;
				n = 65.0F;
				o = 0.125F;
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			}

			if (m >= 1) {
				this.bindTexture(PORTAL_TEXTURE);
			}

			if (m == 1) {
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
				o = 0.5F;
			}

			float q = (float)(-(e + 0.75));
			float r = q + (float)Camera.getPosition().y;
			float s = q + n + (float)Camera.getPosition().y;
			float t = r / s;
			t = (float)(e + 0.75) + t;
			GlStateManager.translate(h, t, k);
			GlStateManager.genTex(GlStateManager.TexCoord.S, 9217);
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9217);
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9217);
			GlStateManager.genTex(GlStateManager.TexCoord.Q, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.S, 9473, this.createBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9473, this.createBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9473, this.createBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.Q, 9474, this.createBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.method_12289(GlStateManager.TexCoord.S);
			GlStateManager.method_12289(GlStateManager.TexCoord.T);
			GlStateManager.method_12289(GlStateManager.TexCoord.R);
			GlStateManager.method_12289(GlStateManager.TexCoord.Q);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, (float)(MinecraftClient.getTime() % 700000L) / 700000.0F, 0.0F);
			GlStateManager.scale(o, o, o);
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.rotate((float)(m * m * 4321 + m * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			GlStateManager.translate(-h, -k, -j);
			r = q + (float)Camera.getPosition().y;
			GlStateManager.translate((float)Camera.getPosition().x * n / r, (float)Camera.getPosition().z * n / r, -j);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			float v = (RANDOM.nextFloat() * 0.5F + 0.1F) * p;
			float w = (RANDOM.nextFloat() * 0.5F + 0.4F) * p;
			float x = (RANDOM.nextFloat() * 0.5F + 0.5F) * p;
			bufferBuilder.vertex(d, e + 0.75, f).color(v, w, x, 1.0F).next();
			bufferBuilder.vertex(d, e + 0.75, f + 1.0).color(v, w, x, 1.0F).next();
			bufferBuilder.vertex(d + 1.0, e + 0.75, f + 1.0).color(v, w, x, 1.0F).next();
			bufferBuilder.vertex(d + 1.0, e + 0.75, f).color(v, w, x, 1.0F).next();
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			this.bindTexture(SKY_TEXTURE);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.S);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.T);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.R);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.Q);
		GlStateManager.enableLighting();
	}

	private FloatBuffer createBuffer(float f1, float f2, float f3, float f4) {
		this.buffer.clear();
		this.buffer.put(f1).put(f2).put(f3).put(f4);
		this.buffer.flip();
		return this.buffer;
	}
}
