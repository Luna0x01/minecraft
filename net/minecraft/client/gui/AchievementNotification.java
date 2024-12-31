package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancement.Achievement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

public class AchievementNotification extends DrawableHelper {
	private static final Identifier ACHIEVEMENT_BACKGROUND = new Identifier("textures/gui/achievement/achievement_background.png");
	private MinecraftClient client;
	private int width;
	private int height;
	private String title;
	private String name;
	private Achievement achievement;
	private long time;
	private ItemRenderer itemRenderer;
	private boolean permanent;

	public AchievementNotification(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
	}

	public void display(Achievement achieved) {
		this.title = I18n.translate("achievement.get");
		this.name = achieved.getText().asUnformattedString();
		this.time = MinecraftClient.getTime();
		this.achievement = achieved;
		this.permanent = false;
	}

	public void displayRaw(Achievement achieved) {
		this.title = achieved.getText().asUnformattedString();
		this.name = achieved.getDescription();
		this.time = MinecraftClient.getTime() + 2500L;
		this.achievement = achieved;
		this.permanent = true;
	}

	private void render() {
		GlStateManager.viewport(0, 0, this.client.width, this.client.height);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.width = this.client.width;
		this.height = this.client.height;
		Window window = new Window(this.client);
		this.width = window.getWidth();
		this.height = window.getHeight();
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
	}

	public void tick() {
		if (this.achievement != null && this.time != 0L && MinecraftClient.getInstance().player != null) {
			double d = (double)(MinecraftClient.getTime() - this.time) / 3000.0;
			if (!this.permanent) {
				if (d < 0.0 || d > 1.0) {
					this.time = 0L;
					return;
				}
			} else if (d > 0.5) {
				d = 0.5;
			}

			this.render();
			GlStateManager.disableDepthTest();
			GlStateManager.depthMask(false);
			double e = d * 2.0;
			if (e > 1.0) {
				e = 2.0 - e;
			}

			e *= 4.0;
			e = 1.0 - e;
			if (e < 0.0) {
				e = 0.0;
			}

			e *= e;
			e *= e;
			int i = this.width - 160;
			int j = 0 - (int)(e * 36.0);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture();
			this.client.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
			GlStateManager.disableLighting();
			this.drawTexture(i, j, 96, 202, 160, 32);
			if (this.permanent) {
				this.client.textRenderer.drawTrimmed(this.name, i + 30, j + 7, 120, -1);
			} else {
				this.client.textRenderer.draw(this.title, i + 30, j + 7, -256);
				this.client.textRenderer.draw(this.name, i + 30, j + 18, -1);
			}

			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.itemRenderer.method_12461(this.achievement.logo, i + 8, j + 8);
			GlStateManager.disableLighting();
			GlStateManager.depthMask(true);
			GlStateManager.enableDepthTest();
		}
	}

	public void reset() {
		this.achievement = null;
		this.time = 0L;
	}
}
