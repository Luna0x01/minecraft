package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;

public class ButtonWidget extends DrawableHelper {
	protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
	protected int width = 200;
	protected int height = 20;
	public int x;
	public int y;
	public String message;
	public int id;
	public boolean active = true;
	public boolean visible = true;
	protected boolean hovered;

	public ButtonWidget(int i, int j, int k, String string) {
		this(i, j, k, 200, 20, string);
	}

	public ButtonWidget(int i, int j, int k, int l, int m, String string) {
		this.id = i;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
		this.message = string;
	}

	protected int getYImage(boolean isHovered) {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (isHovered) {
			i = 2;
		}

		return i;
	}

	public void method_891(MinecraftClient client, int i, int j, float f) {
		if (this.visible) {
			TextRenderer textRenderer = client.textRenderer;
			client.getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			int k = this.getYImage(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			this.drawTexture(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexture(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
			this.mouseDragged(client, i, j);
			int l = 14737632;
			if (!this.active) {
				l = 10526880;
			} else if (this.hovered) {
				l = 16777120;
			}

			this.drawCenteredString(textRenderer, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
		}
	}

	protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
	}

	public void mouseReleased(int mouseX, int mouseY) {
	}

	public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
		return this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	public boolean isHovered() {
		return this.hovered;
	}

	public void renderToolTip(int mouseX, int mouseY) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
