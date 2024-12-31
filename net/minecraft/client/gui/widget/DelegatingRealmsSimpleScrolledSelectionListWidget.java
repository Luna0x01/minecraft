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
	protected boolean method_18414(int i, int j, double d, double e) {
		return this.list.selectItem(i, j, d, e);
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
	protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
		this.list.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
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
			int m = this.getMaxScroll();
			if (m > 0) {
				int n = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getMaxPosition();
				n = MathHelper.clamp(n, 32, this.yEnd - this.yStart - 8);
				int o = (int)this.field_20083 * (this.yEnd - this.yStart - n) / m + this.yStart;
				if (o < this.yStart) {
					o = this.yStart;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)i, (double)this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(o + n), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)(o + n), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)o, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)i, (double)o, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(o + n - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)(o + n - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)o, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)i, (double)o, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(mouseX, mouseY);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.list.mouseScrolled(d) ? true : super.mouseScrolled(d);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.list.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.list.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.list.mouseDragged(d, e, i, f, g);
	}
}
