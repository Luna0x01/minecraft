package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.class_4122;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

public class LabelWidget extends DrawableHelper implements class_4122 {
	protected int width;
	protected int height;
	public int x;
	public int y;
	private final List<String> texts;
	private boolean centered;
	public boolean visible;
	private boolean hasBorderer;
	private final int color;
	private int backgroundColor;
	private int upperRightColor;
	private int lowerLeftColor;
	private final TextRenderer textRenderer;
	private int borderThickness;

	public void method_18397(int i, int j, float f) {
		if (this.visible) {
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			this.method_6695(i, j, f);
			int k = this.y + this.height / 2 + this.borderThickness / 2;
			int l = k - this.texts.size() * 10 / 2;

			for (int m = 0; m < this.texts.size(); m++) {
				if (this.centered) {
					this.drawCenteredString(this.textRenderer, (String)this.texts.get(m), this.x + this.width / 2, l + m * 10, this.color);
				} else {
					this.drawWithShadow(this.textRenderer, (String)this.texts.get(m), this.x, l + m * 10, this.color);
				}
			}
		}
	}

	protected void method_6695(int i, int j, float f) {
		if (this.hasBorderer) {
			int k = this.width + this.borderThickness * 2;
			int l = this.height + this.borderThickness * 2;
			int m = this.x - this.borderThickness;
			int n = this.y - this.borderThickness;
			fill(m, n, m + k, n + l, this.backgroundColor);
			this.drawHorizontalLine(m, m + k, n, this.upperRightColor);
			this.drawHorizontalLine(m, m + k, n + l, this.lowerLeftColor);
			this.drawVerticalLine(m, n, n + l, this.upperRightColor);
			this.drawVerticalLine(m + k, n, n + l, this.lowerLeftColor);
		}
	}
}
