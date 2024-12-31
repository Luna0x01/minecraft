package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.math.MathHelper;

public class DelegatingRealmsSimpleScrolledSelectionListWidget extends ListWidget {
	private final RealmsSimpleScrolledSelectionList list;

	public DelegatingRealmsSimpleScrolledSelectionListWidget(
		RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m
	) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.list = realmsSimpleScrolledSelectionList;
	}

	@Override
	protected int getEntryCount() {
		return this.list.getItemCount();
	}

	@Override
	protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
		this.list.selectItem(index, doubleClick, lastMouseX, lastMouseY);
	}

	@Override
	protected boolean isEntrySelected(int index) {
		return this.list.isSelectedItem(index);
	}

	@Override
	protected void renderBackground() {
		this.list.renderBackground();
	}

	@Override
	protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		this.list.renderItem(index, x, y, rowHeight, mouseX, mouseY);
	}

	public int getWidth() {
		return super.width;
	}

	public int getLastMouseY() {
		return super.lastMouseY;
	}

	public int getLastMouseX() {
		return super.lastMouseX;
	}

	@Override
	protected int getMaxPosition() {
		return this.list.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.list.getScrollbarPosition();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
	}

	@Override
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
			int k = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
			int l = this.yStart + 4 - (int)this.scrollAmount;
			if (this.renderHeader) {
				this.renderHeader(k, l, tessellator);
			}

			this.renderList(k, l, mouseX, mouseY);
			GlStateManager.disableDepthTest();
			int m = 4;
			this.renderHoleBackground(0, this.yStart, 255, 255);
			this.renderHoleBackground(this.yEnd, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ZERO, GlStateManager.class_2866.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
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
}
