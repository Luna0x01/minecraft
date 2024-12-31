package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreditsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MINECRAFT_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private int totalTicks;
	private List<String> credits;
	private int creditsHeight;
	private float speed = 0.5F;

	@Override
	public void tick() {
		MusicTracker musicTracker = this.client.getMusicTracker();
		SoundManager soundManager = this.client.getSoundManager();
		if (this.totalTicks == 0) {
			musicTracker.stop();
			musicTracker.play(MusicTracker.MusicType.CREDITS);
			soundManager.resumeAll();
		}

		soundManager.tick();
		this.totalTicks++;
		float f = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		if ((float)this.totalTicks > f) {
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
		this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
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

			try {
				String string = "";
				String string2 = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
				int i = 274;
				InputStream inputStream = this.client.getResourceManager().getResource(new Identifier("texts/end.txt")).getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
				Random random = new Random(8124371L);

				while ((string = bufferedReader.readLine()) != null) {
					string = string.replaceAll("PLAYERNAME", this.client.getSession().getUsername());

					while (string.contains(string2)) {
						int j = string.indexOf(string2);
						String string3 = string.substring(0, j);
						String string4 = string.substring(j + string2.length());
						string = string3 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
					}

					this.credits.addAll(this.client.textRenderer.wrapLines(string, i));
					this.credits.add("");
				}

				inputStream.close();

				for (int k = 0; k < 8; k++) {
					this.credits.add("");
				}

				inputStream = this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));

				while ((string = bufferedReader.readLine()) != null) {
					string = string.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
					string = string.replaceAll("\t", "    ");
					this.credits.addAll(this.client.textRenderer.wrapLines(string, i));
					this.credits.add("");
				}

				inputStream.close();
				this.creditsHeight = this.credits.size() * 12;
			} catch (Exception var10) {
				LOGGER.error("Couldn't load credits", var10);
			}
		}
	}

	private void renderBackground(int mouseX, int mouseY, float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		int i = this.width;
		float f = 0.0F - ((float)this.totalTicks + tickDelta) * 0.5F * this.speed;
		float g = (float)this.height - ((float)this.totalTicks + tickDelta) * 0.5F * this.speed;
		float h = 0.015625F;
		float j = ((float)this.totalTicks + tickDelta - 0.0F) * 0.02F;
		float k = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
		float l = (k - 20.0F - ((float)this.totalTicks + tickDelta)) * 0.005F;
		if (l < j) {
			j = l;
		}

		if (j > 1.0F) {
			j = 1.0F;
		}

		j *= j;
		j = j * 96.0F / 255.0F;
		bufferBuilder.vertex(0.0, (double)this.height, (double)this.zOffset).texture(0.0, (double)(f * h)).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex((double)i, (double)this.height, (double)this.zOffset).texture((double)((float)i * h), (double)(f * h)).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex((double)i, 0.0, (double)this.zOffset).texture((double)((float)i * h), (double)(g * h)).color(j, j, j, 1.0F).next();
		bufferBuilder.vertex(0.0, 0.0, (double)this.zOffset).texture(0.0, (double)(g * h)).color(j, j, j, 1.0F).next();
		tessellator.draw();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground(mouseX, mouseY, tickDelta);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 274;
		int j = this.width / 2 - i / 2;
		int k = this.height + 50;
		float f = -((float)this.totalTicks + tickDelta) * this.speed;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, f, 0.0F);
		this.client.getTextureManager().bindTexture(MINECRAFT_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexture(j, k, 0, 0, 155, 44);
		this.drawTexture(j + 155, k, 0, 45, 155, 44);
		int l = k + 200;

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
					this.textRenderer.drawWithShadow(string.substring(3), (float)(j + (i - this.textRenderer.getStringWidth(string.substring(3))) / 2), (float)l, 16777215);
				} else {
					this.textRenderer.random.setSeed((long)m * 4238972211L + (long)(this.totalTicks / 4));
					this.textRenderer.drawWithShadow(string, (float)j, (float)l, 16777215);
				}
			}

			l += 12;
		}

		GlStateManager.popMatrix();
		this.client.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(0, 769);
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
