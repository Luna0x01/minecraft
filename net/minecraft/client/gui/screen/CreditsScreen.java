package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreditsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MINECRAFT_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier field_15950 = new Identifier("textures/gui/title/edition.png");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private final boolean field_15951;
	private final Runnable field_15952;
	private float field_15953;
	private List<String> credits;
	private int creditsHeight;
	private float speed = 0.5F;

	public CreditsScreen(boolean bl, Runnable runnable) {
		this.field_15951 = bl;
		this.field_15952 = runnable;
		if (!bl) {
			this.speed = 0.75F;
		}
	}

	@Override
	public void tick() {
		this.client.getMusicTracker().tick();
		this.client.getSoundManager().tick();
		float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		if (this.field_15953 > f) {
			this.close();
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == 1) {
			this.close();
		}
	}

	private void close() {
		this.field_15952.run();
		this.client.setScreen(null);
	}

	@Override
	public boolean shouldPauseGame() {
		return true;
	}

	@Override
	public void init() {
		if (this.credits == null) {
			this.credits = Lists.newArrayList();
			Resource resource = null;

			try {
				String string = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
				int i = 274;
				if (this.field_15951) {
					resource = this.client.getResourceManager().getResource(new Identifier("texts/end.txt"));
					InputStream inputStream = resource.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					Random random = new Random(8124371L);

					String string2;
					while ((string2 = bufferedReader.readLine()) != null) {
						string2 = string2.replaceAll("PLAYERNAME", this.client.getSession().getUsername());

						while (string2.contains(string)) {
							int j = string2.indexOf(string);
							String string3 = string2.substring(0, j);
							String string4 = string2.substring(j + string.length());
							string2 = string3 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
						}

						this.credits.addAll(this.client.textRenderer.wrapLines(string2, 274));
						this.credits.add("");
					}

					inputStream.close();

					for (int k = 0; k < 8; k++) {
						this.credits.add("");
					}
				}

				InputStream inputStream2 = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2, StandardCharsets.UTF_8));

				String string5;
				while ((string5 = bufferedReader2.readLine()) != null) {
					string5 = string5.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
					string5 = string5.replaceAll("\t", "    ");
					this.credits.addAll(this.client.textRenderer.wrapLines(string5, 274));
					this.credits.add("");
				}

				inputStream2.close();
				this.creditsHeight = this.credits.size() * 12;
			} catch (Exception var14) {
				LOGGER.error("Couldn't load credits", var14);
			} finally {
				IOUtils.closeQuietly(resource);
			}
		}
	}

	private void renderBackground(int mouseX, int mouseY, float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		int i = this.width;
		float f = -this.field_15953 * 0.5F * this.speed;
		float g = (float)this.height - this.field_15953 * 0.5F * this.speed;
		float h = 0.015625F;
		float j = this.field_15953 * 0.02F;
		float k = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		float l = (k - 20.0F - this.field_15953) * 0.005F;
		if (l < j) {
			j = l;
		}

		if (j > 1.0F) {
			j = 1.0F;
		}

		j *= j;
		j = j * 96.0F / 255.0F;
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.zOffset).texture(0.0, (double)(f * 0.015625F)).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex((double)i, (double)this.height, (double)this.zOffset)
			.texture((double)((float)i * 0.015625F), (double)(f * 0.015625F))
			.color(j, j, j, 1.0F)
			.next();
		bufferBuilder.vertex((double)i, 0.0, (double)this.zOffset).texture((double)((float)i * 0.015625F), (double)(g * 0.015625F)).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.zOffset).texture(0.0, (double)(g * 0.015625F)).color(j, j, j, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground(mouseX, mouseY, tickDelta);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 274;
		int j = this.width / 2 - 137;
		int k = this.height + 50;
		this.field_15953 += tickDelta;
		float f = -this.field_15953 * this.speed;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, f, 0.0F);
		this.client.getTextureManager().bindTexture(MINECRAFT_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.drawTexture(j, k, 0, 0, 155, 44);
		this.drawTexture(j + 155, k, 0, 45, 155, 44);
		this.client.getTextureManager().bindTexture(field_15950);
		drawTexture(j + 88, k + 37, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
		GlStateManager.disableAlphaTest();
		int l = k + 100;

		for (int m = 0; m < this.credits.size(); m++) {
			if (m == this.credits.size() - 1) {
				float g = (float)l + f - (float)(this.height / 2 - 6);
				if (g < 0.0F) {
					GlStateManager.translate(0.0F, -g, 0.0F);
				}
			}

			if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height) {
				String string = (String)this.credits.get(m);
				if (string.startsWith("[C]")) {
					this.textRenderer.drawWithShadow(string.substring(3), (float)(j + (274 - this.textRenderer.getStringWidth(string.substring(3))) / 2), (float)l, 16777215);
				} else {
					this.textRenderer.random.setSeed((long)((float)((long)m * 4238972211L) + this.field_15953 / 4.0F));
					this.textRenderer.drawWithShadow(string, (float)j, (float)l, 16777215);
				}
			}

			l += 12;
		}

		GlStateManager.popMatrix();
		this.client.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.method_12287(GlStateManager.class_2870.ZERO, GlStateManager.class_2866.ONE_MINUS_SRC_COLOR);
		int n = this.width;
		int o = this.height;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)o, (double)this.zOffset).texture(0.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)n, (double)o, (double)this.zOffset).texture(1.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex((double)n, 0.0, (double)this.zOffset).texture(1.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.zOffset).texture(0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();
		GlStateManager.disableBlend();
		super.render(mouseX, mouseY, tickDelta);
	}
}
