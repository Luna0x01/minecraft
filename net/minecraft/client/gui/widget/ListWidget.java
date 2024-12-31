package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class ListWidget {
	protected final MinecraftClient client;
	protected int width;
	protected int height;
	protected int yStart;
	protected int yEnd;
	protected int xEnd;
	protected int xStart;
	protected final int entryHeight;
	private int homeButtonId;
	private int endButtonId;
	protected int lastMouseX;
	protected int lastMouseY;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected float scrollSpeed;
	protected float scrollAmount;
	protected int selectedEntry = -1;
	protected long time;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean dragging = true;

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

	protected abstract int getEntryCount();

	protected abstract void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY);

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

	public int getEntryAt(int x, int y) {
		int i = this.xStart + this.width / 2 - this.getRowWidth() / 2;
		int j = this.xStart + this.width / 2 + this.getRowWidth() / 2;
		int k = y - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
		int l = k / this.entryHeight;
		return x < this.getScrollbarPosition() && x >= i && x <= j && l >= 0 && k >= 0 && l < this.getEntryCount() ? l : -1;
	}

	public void setButtonIds(int homeButtonId, int endButtonId) {
		this.homeButtonId = homeButtonId;
		this.endButtonId = endButtonId;
	}

	protected void capYPosition() {
		this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, (float)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.yEnd - this.yStart - 4));
	}

	public int getScrollAmount() {
		return (int)this.scrollAmount;
	}

	public boolean isMouseInList(int mouseY) {
		return mouseY >= this.yStart && mouseY <= this.yEnd && this.lastMouseX >= this.xStart && this.lastMouseX <= this.xEnd;
	}

	public void scroll(int amount) {
		this.scrollAmount += (float)amount;
		this.capYPosition();
		this.yDrag = -2;
	}

	public void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == this.homeButtonId) {
				this.scrollAmount = this.scrollAmount - (float)(this.entryHeight * 2 / 3);
				this.yDrag = -2;
				this.capYPosition();
			} else if (button.id == this.endButtonId) {
				this.scrollAmount = this.scrollAmount + (float)(this.entryHeight * 2 / 3);
				this.yDrag = -2;
				this.capYPosition();
			}
		}
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.visible) {
			this.lastMouseX = mouseX;
			this.lastMouseY = mouseY;
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
				.texture((double)((float)this.xStart / 32.0F), (double)((float)(this.yEnd + (int)this.scrollAmount) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yEnd, 0.0)
				.texture((double)((float)this.xEnd / 32.0F), (double)((float)(this.yEnd + (int)this.scrollAmount) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xEnd, (double)this.yStart, 0.0)
				.texture((double)((float)this.xEnd / 32.0F), (double)((float)(this.yStart + (int)this.scrollAmount) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.xStart, (double)this.yStart, 0.0)
				.texture((double)((float)this.xStart / 32.0F), (double)((float)(this.yStart + (int)this.scrollAmount) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int k = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
			int l = this.yStart + 4 - (int)this.scrollAmount;
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
				int o = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getMaxPosition();
				o = MathHelper.clamp(o, 32, this.yEnd - this.yStart - 8);
				int p = (int)this.scrollAmount * (this.yEnd - this.yStart - o) / n + this.yStart;
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

	public void handleMouse() {
		if (this.isMouseInList(this.lastMouseY)) {
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.lastMouseY >= this.yStart && this.lastMouseY <= this.yEnd) {
				int i = (this.width - this.getRowWidth()) / 2;
				int j = (this.width + this.getRowWidth()) / 2;
				int k = this.lastMouseY - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
				int l = k / this.entryHeight;
				if (l < this.getEntryCount() && this.lastMouseX >= i && this.lastMouseX <= j && l >= 0 && k >= 0) {
					this.selectEntry(l, false, this.lastMouseX, this.lastMouseY);
					this.selectedEntry = l;
				} else if (this.lastMouseX >= i && this.lastMouseX <= j && k < 0) {
					this.clickedHeader(this.lastMouseX - i, this.lastMouseY - this.yStart + (int)this.scrollAmount - 4);
				}
			}

			if (!Mouse.isButtonDown(0) || !this.isDragging()) {
				this.yDrag = -1;
			} else if (this.yDrag == -1) {
				boolean bl = true;
				if (this.lastMouseY >= this.yStart && this.lastMouseY <= this.yEnd) {
					int m = (this.width - this.getRowWidth()) / 2;
					int n = (this.width + this.getRowWidth()) / 2;
					int o = this.lastMouseY - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
					int p = o / this.entryHeight;
					if (p < this.getEntryCount() && this.lastMouseX >= m && this.lastMouseX <= n && p >= 0 && o >= 0) {
						boolean bl2 = p == this.selectedEntry && MinecraftClient.getTime() - this.time < 250L;
						this.selectEntry(p, bl2, this.lastMouseX, this.lastMouseY);
						this.selectedEntry = p;
						this.time = MinecraftClient.getTime();
					} else if (this.lastMouseX >= m && this.lastMouseX <= n && o < 0) {
						this.clickedHeader(this.lastMouseX - m, this.lastMouseY - this.yStart + (int)this.scrollAmount - 4);
						bl = false;
					}

					int q = this.getScrollbarPosition();
					int r = q + 6;
					if (this.lastMouseX >= q && this.lastMouseX <= r) {
						this.scrollSpeed = -1.0F;
						int s = this.getMaxScroll();
						if (s < 1) {
							s = 1;
						}

						int t = (int)((float)((this.yEnd - this.yStart) * (this.yEnd - this.yStart)) / (float)this.getMaxPosition());
						t = MathHelper.clamp(t, 32, this.yEnd - this.yStart - 8);
						this.scrollSpeed = this.scrollSpeed / ((float)(this.yEnd - this.yStart - t) / (float)s);
					} else {
						this.scrollSpeed = 1.0F;
					}

					if (bl) {
						this.yDrag = this.lastMouseY;
					} else {
						this.yDrag = -2;
					}
				} else {
					this.yDrag = -2;
				}
			} else if (this.yDrag >= 0) {
				this.scrollAmount = this.scrollAmount - (float)(this.lastMouseY - this.yDrag) * this.scrollSpeed;
				this.yDrag = this.lastMouseY;
			}

			int u = Mouse.getEventDWheel();
			if (u != 0) {
				if (u > 0) {
					u = -1;
				} else if (u < 0) {
					u = 1;
				}

				this.scrollAmount = this.scrollAmount + (float)(u * this.entryHeight / 2);
			}
		}
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public boolean isDragging() {
		return this.dragging;
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
				int q = this.xStart + (this.width / 2 - this.getRowWidth() / 2);
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
