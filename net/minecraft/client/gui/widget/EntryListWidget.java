package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractParentElement implements Drawable, Selectable {
	protected final MinecraftClient client;
	protected final int itemHeight;
	private final List<E> children = new EntryListWidget.Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected boolean centerListVertically = true;
	private double scrollAmount;
	private boolean renderSelection = true;
	private boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	@Nullable
	private E selected;
	private boolean renderBackground = true;
	private boolean renderHorizontalShadows = true;
	@Nullable
	private E hoveredEntry;

	public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.itemHeight = itemHeight;
		this.left = 0;
		this.right = width;
	}

	public void setRenderSelection(boolean renderSelection) {
		this.renderSelection = renderSelection;
	}

	protected void setRenderHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = headerHeight;
		if (!renderHeader) {
			this.headerHeight = 0;
		}
	}

	public int getRowWidth() {
		return 220;
	}

	@Nullable
	public E getSelectedOrNull() {
		return this.selected;
	}

	public void setSelected(@Nullable E entry) {
		this.selected = entry;
	}

	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}

	public void setRenderHorizontalShadows(boolean renderHorizontalShadows) {
		this.renderHorizontalShadows = renderHorizontalShadows;
	}

	@Nullable
	public E getFocused() {
		return (E)super.getFocused();
	}

	@Override
	public final List<E> children() {
		return this.children;
	}

	protected final void clearEntries() {
		this.children.clear();
	}

	protected void replaceEntries(Collection<E> newEntries) {
		this.children.clear();
		this.children.addAll(newEntries);
	}

	protected E getEntry(int index) {
		return (E)this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	protected boolean isSelectedEntry(int index) {
		return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
	}

	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
		int i = this.getRowWidth() / 2;
		int j = this.left + this.width / 2;
		int k = j - i;
		int l = j + i;
		int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int n = m / this.itemHeight;
		return (E)(x < (double)this.getScrollbarPositionX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount()
			? this.children().get(n)
			: null);
	}

	public void updateSize(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setLeftPos(int left) {
		this.left = left;
		this.right = left + this.width;
	}

	protected int getMaxPosition() {
		return this.getEntryCount() * this.itemHeight + this.headerHeight;
	}

	protected void clickedHeader(int x, int y) {
	}

	protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
	}

	protected void renderBackground(MatrixStack matrices) {
	}

	protected void renderDecorations(MatrixStack matrices, int mouseX, int mouseY) {
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int i = this.getScrollbarPositionX();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		this.hoveredEntry = this.isMouseOver((double)mouseX, (double)mouseY) ? this.getEntryAtPosition((double)mouseX, (double)mouseY) : null;
		if (this.renderBackground) {
			RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			float f = 32.0F;
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
		}

		int k = this.getRowLeft();
		int l = this.top + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(matrices, k, l, tessellator);
		}

		this.renderList(matrices, k, l, mouseX, mouseY, delta);
		if (this.renderHorizontalShadows) {
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(519);
			float g = 32.0F;
			int m = -100;
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.top, -100.0).texture(0.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((double)(this.left + this.width), (double)this.top, -100.0)
				.texture((float)this.width / 32.0F, (float)this.top / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex((double)(this.left + this.width), 0.0, -100.0).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((double)this.left, 0.0, -100.0).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((double)this.left, (double)this.height, -100.0).texture(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
			bufferBuilder.vertex((double)(this.left + this.width), (double)this.height, -100.0)
				.texture((float)this.width / 32.0F, (float)this.height / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex((double)(this.left + this.width), (double)this.bottom, -100.0)
				.texture((float)this.width / 32.0F, (float)this.bottom / 32.0F)
				.color(64, 64, 64, 255)
				.next();
			bufferBuilder.vertex((double)this.left, (double)this.bottom, -100.0).texture(0.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();
			tessellator.draw();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
			);
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int n = 4;
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)this.left, (double)(this.top + 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)(this.top + 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.left, (double)(this.bottom - 4), 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
		}

		int o = this.getMaxScroll();
		if (o > 0) {
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			int p = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
			int q = (int)this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
			if (q < this.top) {
				q = this.top;
			}

			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex((double)i, (double)this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)j, (double)this.bottom, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)j, (double)this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)i, (double)this.top, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)i, (double)(q + p), 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)j, (double)(q + p), 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)j, (double)q, 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)i, (double)q, 0.0).color(128, 128, 128, 255).next();
			bufferBuilder.vertex((double)i, (double)(q + p - 1), 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(j - 1), (double)(q + p - 1), 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)(j - 1), (double)q, 0.0).color(192, 192, 192, 255).next();
			bufferBuilder.vertex((double)i, (double)q, 0.0).color(192, 192, 192, 255).next();
			tessellator.draw();
		}

		this.renderDecorations(matrices, mouseX, mouseY);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	protected void centerScrollOn(E entry) {
		this.setScrollAmount((double)(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2));
	}

	protected void ensureVisible(E entry) {
		int i = this.getRowTop(this.children().indexOf(entry));
		int j = i - this.top - 4 - this.itemHeight;
		if (j < 0) {
			this.scroll(j);
		}

		int k = this.bottom - i - this.itemHeight - this.itemHeight;
		if (k < 0) {
			this.scroll(-k);
		}
	}

	private void scroll(int amount) {
		this.setScrollAmount(this.getScrollAmount() + (double)amount);
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public int method_35721() {
		return (int)this.getScrollAmount() - this.height - this.headerHeight;
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return this.width / 2 + 124;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			E entry = this.getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					this.setFocused(entry);
					this.setDragging(true);
					return true;
				}
			} else if (button == 0) {
				this.clickedHeader(
					(int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4
				);
				return true;
			}

			return this.scrolling;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (button == 0 && this.scrolling) {
			if (mouseY < (double)this.top) {
				this.setScrollAmount(0.0);
			} else if (mouseY > (double)this.bottom) {
				this.setScrollAmount((double)this.getMaxScroll());
			} else {
				double d = (double)Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
				double e = Math.max(1.0, d / (double)(i - j));
				this.setScrollAmount(this.getScrollAmount() + deltaY * e);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 264) {
			this.moveSelection(EntryListWidget.MoveDirection.DOWN);
			return true;
		} else if (keyCode == 265) {
			this.moveSelection(EntryListWidget.MoveDirection.UP);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(EntryListWidget.MoveDirection direction) {
		this.moveSelectionIf(direction, entry -> true);
	}

	protected void ensureSelectedEntryVisible() {
		E entry = this.getSelectedOrNull();
		if (entry != null) {
			this.setSelected(entry);
			this.ensureVisible(entry);
		}
	}

	protected void moveSelectionIf(EntryListWidget.MoveDirection direction, Predicate<E> predicate) {
		int i = direction == EntryListWidget.MoveDirection.UP ? -1 : 1;
		if (!this.children().isEmpty()) {
			int j = this.children().indexOf(this.getSelectedOrNull());

			while (true) {
				int k = MathHelper.clamp(j + i, 0, this.getEntryCount() - 1);
				if (j == k) {
					break;
				}

				E entry = (E)this.children().get(k);
				if (predicate.test(entry)) {
					this.setSelected(entry);
					this.ensureVisible(entry);
					break;
				}

				j = k;
			}
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
	}

	protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		int i = this.getEntryCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int j = 0; j < i; j++) {
			int k = this.getRowTop(j);
			int l = this.getRowBottom(j);
			if (l >= this.top && k <= this.bottom) {
				int m = y + j * this.itemHeight + this.headerHeight;
				int n = this.itemHeight - 4;
				E entry = this.getEntry(j);
				int o = this.getRowWidth();
				if (this.renderSelection && this.isSelectedEntry(j)) {
					int p = this.left + this.width / 2 - o / 2;
					int q = this.left + this.width / 2 + o / 2;
					RenderSystem.disableTexture();
					RenderSystem.setShader(GameRenderer::getPositionShader);
					float f = this.isFocused() ? 1.0F : 0.5F;
					RenderSystem.setShaderColor(f, f, f, 1.0F);
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
					bufferBuilder.vertex((double)p, (double)(m + n + 2), 0.0).next();
					bufferBuilder.vertex((double)q, (double)(m + n + 2), 0.0).next();
					bufferBuilder.vertex((double)q, (double)(m - 2), 0.0).next();
					bufferBuilder.vertex((double)p, (double)(m - 2), 0.0).next();
					tessellator.draw();
					RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
					bufferBuilder.vertex((double)(p + 1), (double)(m + n + 1), 0.0).next();
					bufferBuilder.vertex((double)(q - 1), (double)(m + n + 1), 0.0).next();
					bufferBuilder.vertex((double)(q - 1), (double)(m - 1), 0.0).next();
					bufferBuilder.vertex((double)(p + 1), (double)(m - 1), 0.0).next();
					tessellator.draw();
					RenderSystem.enableTexture();
				}

				int r = this.getRowLeft();
				entry.render(matrices, j, k, r, o, n, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
			}
		}
	}

	public int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}

	protected int getRowTop(int index) {
		return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
	}

	private int getRowBottom(int index) {
		return this.getRowTop(index) + this.itemHeight;
	}

	protected boolean isFocused() {
		return false;
	}

	@Override
	public Selectable.SelectionType getType() {
		if (this.isFocused()) {
			return Selectable.SelectionType.FOCUSED;
		} else {
			return this.hoveredEntry != null ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}

	@Nullable
	protected E remove(int index) {
		E entry = (E)this.children.get(index);
		return this.removeEntry((E)this.children.get(index)) ? entry : null;
	}

	protected boolean removeEntry(E entry) {
		boolean bl = this.children.remove(entry);
		if (bl && entry == this.getSelectedOrNull()) {
			this.setSelected(null);
		}

		return bl;
	}

	@Nullable
	protected E getHoveredEntry() {
		return this.hoveredEntry;
	}

	void setEntryParentList(EntryListWidget.Entry<E> entry) {
		entry.parentList = this;
	}

	protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
		List<E> list = this.children();
		if (list.size() > 1) {
			int i = list.indexOf(entry);
			if (i != -1) {
				builder.put(NarrationPart.POSITION, new TranslatableText("narrator.position.list", i + 1, list.size()));
			}
		}
	}

	class Entries extends AbstractList<E> {
		private final List<E> entries = Lists.newArrayList();

		public E get(int i) {
			return (E)this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		public E set(int i, E entry) {
			E entry2 = (E)this.entries.set(i, entry);
			EntryListWidget.this.setEntryParentList(entry);
			return entry2;
		}

		public void add(int i, E entry) {
			this.entries.add(i, entry);
			EntryListWidget.this.setEntryParentList(entry);
		}

		public E remove(int i) {
			return (E)this.entries.remove(i);
		}
	}

	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements Element {
		@Deprecated
		EntryListWidget<E> parentList;

		public abstract void render(
			MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		);

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
		}
	}

	protected static enum MoveDirection {
		UP,
		DOWN;
	}
}
