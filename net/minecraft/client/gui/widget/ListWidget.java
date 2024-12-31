package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_4121;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

public abstract class ListWidget extends class_4121 {
	protected final MinecraftClient client;
	protected int width;
	protected int height;
	protected int yStart;
	protected int yEnd;
	protected int xEnd;
	protected int xStart;
	protected final int entryHeight;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected double field_20083;
	protected int selectedEntry;
	protected long time = Long.MIN_VALUE;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean dragging;

	public ListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.client = minecraftClient;
		this.width = i;
		this.height = j;
		this.yStart = k;
		this.yEnd = l;
		this.entryHeight = m;
		this.xStart = 0;
		this.xEnd = i;
	}

	public void updateBounds(int right, int height, int top, int bottom) {
		this.width = right;
		this.height = height;
		this.yStart = top;
		this.yEnd = bottom;
		this.xStart = 0;
		this.xEnd = right;
	}

	public void setRenderSelection(boolean renderSelection) {
		this.renderSelection = renderSelection;
	}

	protected void setHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = headerHeight;
		if (!renderHeader) {
			this.headerHeight = 0;
		}
	}

	public boolean method_18417() {
		return this.visible;
	}

	protected abstract int getEntryCount();

	public void method_18416(int i) {
	}

	@Override
	protected List<? extends class_4122> method_18423() {
		return Collections.emptyList();
	}

	protected boolean method_18414(int i, int j, double d, double e) {
		return true;
	}

	protected abstract boolean isEntrySelected(int index);

	protected int getMaxPosition() {
		return this.getEntryCount() * this.entryHeight + this.headerHeight;
	}

	protected abstract void renderBackground();

	protected void method_9528(int i, int j, int k, float f) {
	}

	protected abstract void method_1055(int i, int j, int k, int l, int m, int n, float f);

	protected void renderHeader(int x, int y, Tessellator tessellator) {
	}

	protected void clickedHeader(int mouseX, int mouseY) {
	}

	protected void renderDecorations(int mouseX, int mouseY) {
	}

	public int method_18411(double d, double e) {
		int i = this.xStart + this.width / 2 - this.getRowWidth() / 2;
		int j = this.xStart + this.width / 2 + this.getRowWidth() / 2;
		int k = MathHelper.floor(e - (double)this.yStart) - this.headerHeight + (int)this.field_20083 - 4;
		int l = k / this.entryHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount() ? l : -1;
	}

	protected void capYPosition() {
		this.field_20083 = MathHelper.clamp(this.field_20083, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.yEnd - this.yStart - 4));
	}

	public int getScrollAmount() {
		return (int)this.field_20083;
	}

	public boolean method_18415(double d, double e) {
		return e >= (double)this.yStart && e <= (double)this.yEnd && d >= (double)this.xStart && d <= (double)this.xEnd;
	}

	public void scroll(int amount) {
		this.field_20083 += (double)amount;
		this.capYPosition();
		this.yDrag = -2;
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.visible) {
			this.renderBackground();
			int i = this.getScrollbarPosition();
			int j = i + 6;
			this.capYPosition();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float f = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.xStart, (double)this.yEnd, 0.0)
				.texture((double)((float)this.xStart / 32.0F), (double)((float)(this.yEnd + (int)this.field_20083) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yEnd, 0.0)
				.texture((double)((float)this.xEnd / 32.0F), (double)((float)(this.yEnd + (int)this.field_20083) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yStart, 0.0)
				.texture((double)((float)this.xEnd / 32.0F), (double)((float)(this.yStart + (int)this.field_20083) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xStart, (double)this.yStart, 0.0)
				.texture((double)((float)this.xStart / 32.0F), (double)((float)(this.yStart + (int)this.field_20083) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int k = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
			int l = this.yStart + 4 - (int)this.field_20083;
			if (this.renderHeader) {
				this.renderHeader(k, l, tessellator);
			}

			this.method_6704(k, l, mouseX, mouseY, tickDelta);
			GlStateManager.disableDepthTest();
			this.renderHoleBackground(0, this.yStart, 255, 255);
			this.renderHoleBackground(this.yEnd, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ZERO, GlStateManager.class_2866.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int m = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.xStart, (double)(this.yStart + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.xEnd, (double)(this.yStart + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.xStart, (double)this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.xStart, (double)this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.xEnd, (double)(this.yEnd - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.xStart, (double)(this.yEnd - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
			int n = this.getMaxScroll();
			if (n > 0) {
				int o = (int)((float)((this.yEnd - this.yStart) * (this.yEnd - this.yStart)) / (float)this.getMaxPosition());
				o = MathHelper.clamp(o, 32, this.yEnd - this.yStart - 8);
				int p = (int)this.field_20083 * (this.yEnd - this.yStart - o) / n + this.yStart;
				if (p < this.yStart) {
					p = this.yStart;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)i, (double)this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)(p + o), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)p, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)(p + o - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)p, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(mouseX, mouseY);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	protected void method_18412(double d, double e, int i) {
		this.dragging = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.method_18412(d, e, i);
		if (this.method_18417() && this.method_18415(d, e)) {
			int j = this.method_18411(d, e);
			if (j == -1 && i == 0) {
				this.clickedHeader((int)(d - (double)(this.xStart + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.yStart) + (int)this.field_20083 - 4);
				return true;
			} else if (j != -1 && this.method_18414(j, i, d, e)) {
				if (this.method_18423().size() > j) {
					this.method_18421((class_4122)this.method_18423().get(j));
				}

				this.method_18425(true);
				this.method_18416(j);
				return true;
			} else {
				return this.dragging;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(d, e, i);
		}

		this.method_18423().forEach(arg -> arg.mouseReleased(d, e, i));
		return false;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (super.mouseDragged(d, e, i, f, g)) {
			return true;
		} else if (this.method_18417() && i == 0 && this.dragging) {
			if (e < (double)this.yStart) {
				this.field_20083 = 0.0;
			} else if (e > (double)this.yEnd) {
				this.field_20083 = (double)this.getMaxScroll();
			} else {
				double h = (double)this.getMaxScroll();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.yEnd - this.yStart) * (this.yEnd - this.yStart)) / (float)this.getMaxPosition());
				j = MathHelper.clamp(j, 32, this.yEnd - this.yStart - 8);
				double k = h / (double)(this.yEnd - this.yStart - j);
				if (k < 1.0) {
					k = 1.0;
				}

				this.field_20083 += g * k;
				this.capYPosition();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (!this.method_18417()) {
			return false;
		} else {
			this.field_20083 = this.field_20083 - d * (double)this.entryHeight / 2.0;
			return true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return !this.method_18417() ? false : super.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return !this.method_18417() ? false : super.charTyped(c, i);
	}

	public int getRowWidth() {
		return 220;
	}

	protected void method_6704(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.headerHeight;
			int p = this.entryHeight - 4;
			if (o > this.yEnd || o + p < this.yStart) {
				this.method_9528(n, i, o, f);
			}

			if (this.renderSelection && this.isEntrySelected(n)) {
				int q = this.xStart + this.width / 2 - this.getRowWidth() / 2;
				int r = this.xStart + this.width / 2 + this.getRowWidth() / 2;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)q, (double)(o + p + 2), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)r, (double)(o + p + 2), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)r, (double)(o - 2), 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)q, (double)(o - 2), 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o + p + 1), 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o + p + 1), 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o - 1), 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o - 1), 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				GlStateManager.enableTexture();
			}

			this.method_1055(n, i, o, p, k, l, f);
		}
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	protected void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.xStart, (double)bottom, 0.0).texture(0.0, (double)((float)bottom / 32.0F)).color(64, 64, 64, bottomAlpha).next();
		bufferBuilder.vertex((double)(this.xStart + this.width), (double)bottom, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)bottom / 32.0F))
			.color(64, 64, 64, bottomAlpha)
			.next();
		bufferBuilder.vertex((double)(this.xStart + this.width), (double)top, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)top / 32.0F))
			.color(64, 64, 64, topAlpha)
			.next();
		bufferBuilder.vertex((double)this.xStart, (double)top, 0.0).texture(0.0, (double)((float)top / 32.0F)).color(64, 64, 64, topAlpha).next();
		tessellator.draw();
	}

	public void setXPos(int x) {
		this.xStart = x;
		this.xEnd = x + this.width;
	}

	public int getItemHeight() {
		return this.entryHeight;
	}
}
