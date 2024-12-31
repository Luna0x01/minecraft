package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;

public class LabelWidget extends DrawableHelper {
	protected int width = 200;
	protected int height = 20;
	public int x;
	public int y;
	private final List<String> texts;
	public int id;
	private boolean centered;
	public boolean visible = true;
	private boolean hasBorderer;
	private final int color;
	private int backgroundColor;
	private int upperRightColor;
	private int lowerLeftColor;
	private final TextRenderer textRenderer;
	private int borderThickness;

	public LabelWidget(TextRenderer textRenderer, int i, int j, int k, int l, int m, int n) {
		this.textRenderer = textRenderer;
		this.id = i;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
		this.texts = Lists.newArrayList();
		this.centered = false;
		this.hasBorderer = false;
		this.color = n;
		this.backgroundColor = -1;
		this.upperRightColor = -1;
		this.lowerLeftColor = -1;
		this.borderThickness = 0;
	}

	public void addLine(String text) {
		this.texts.add(I18n.translate(text));
	}

	public LabelWidget centered() {
		this.centered = true;
		return this;
	}

	public void render(MinecraftClient client, int mouseX, int mouseY) {
		if (this.visible) {
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			this.renderBorder(client, mouseX, mouseY);
			int i = this.y + this.height / 2 + this.borderThickness / 2;
			int j = i - this.texts.size() * 10 / 2;

			for (int k = 0; k < this.texts.size(); k++) {
				if (this.centered) {
					this.drawCenteredString(this.textRenderer, (String)this.texts.get(k), this.x + this.width / 2, j + k * 10, this.color);
				} else {
					this.drawWithShadow(this.textRenderer, (String)this.texts.get(k), this.x, j + k * 10, this.color);
				}
			}
		}
	}

	protected void renderBorder(MinecraftClient client, int mouseX, int mouseY) {
		if (this.hasBorderer) {
			int i = this.width + this.borderThickness * 2;
			int j = this.height + this.borderThickness * 2;
			int k = this.x - this.borderThickness;
			int l = this.y - this.borderThickness;
			fill(k, l, k + i, l + j, this.backgroundColor);
			this.drawHorizontalLine(k, k + i, l, this.upperRightColor);
			this.drawHorizontalLine(k, k + i, l + j, this.lowerLeftColor);
			this.drawVerticalLine(k, l, l + j, this.upperRightColor);
			this.drawVerticalLine(k + i, l, l + j, this.lowerLeftColor);
		}
	}
}
