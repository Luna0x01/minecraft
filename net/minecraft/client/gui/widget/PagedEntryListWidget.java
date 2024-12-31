package net.minecraft.client.gui.widget;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.collection.IntObjectStorage;

public class PagedEntryListWidget extends EntryListWidget {
	private final List<PagedEntryListWidget.DualDrawableEntry> widgetEntries = Lists.newArrayList();
	private final IntObjectStorage<DrawableHelper> widgets = new IntObjectStorage<>();
	private final List<TextFieldWidget> textFields = Lists.newArrayList();
	private final PagedEntryListWidget.ListEntry[][] pages;
	private int currentPageId;
	private final PagedEntryListWidget.Listener listener;
	private DrawableHelper currentWidget;

	public PagedEntryListWidget(
		MinecraftClient minecraftClient, int i, int j, int k, int l, int m, PagedEntryListWidget.Listener listener, PagedEntryListWidget.ListEntry[]... listEntrys
	) {
		super(minecraftClient, i, j, k, l, m);
		this.listener = listener;
		this.pages = listEntrys;
		this.centerListVertically = false;
		this.initWidgets();
		this.initWidgetEntries();
	}

	private void initWidgets() {
		for (PagedEntryListWidget.ListEntry[] listEntrys : this.pages) {
			for (int i = 0; i < listEntrys.length; i += 2) {
				PagedEntryListWidget.ListEntry listEntry = listEntrys[i];
				PagedEntryListWidget.ListEntry listEntry2 = i < listEntrys.length - 1 ? listEntrys[i + 1] : null;
				DrawableHelper drawableHelper = this.createWidget(listEntry, 0, listEntry2 == null);
				DrawableHelper drawableHelper2 = this.createWidget(listEntry2, 160, listEntry == null);
				PagedEntryListWidget.DualDrawableEntry dualDrawableEntry = new PagedEntryListWidget.DualDrawableEntry(drawableHelper, drawableHelper2);
				this.widgetEntries.add(dualDrawableEntry);
				if (listEntry != null && drawableHelper != null) {
					this.widgets.set(listEntry.getId(), drawableHelper);
					if (drawableHelper instanceof TextFieldWidget) {
						this.textFields.add((TextFieldWidget)drawableHelper);
					}
				}

				if (listEntry2 != null && drawableHelper2 != null) {
					this.widgets.set(listEntry2.getId(), drawableHelper2);
					if (drawableHelper2 instanceof TextFieldWidget) {
						this.textFields.add((TextFieldWidget)drawableHelper2);
					}
				}
			}
		}
	}

	private void initWidgetEntries() {
		this.widgetEntries.clear();

		for (int i = 0; i < this.pages[this.currentPageId].length; i += 2) {
			PagedEntryListWidget.ListEntry listEntry = this.pages[this.currentPageId][i];
			PagedEntryListWidget.ListEntry listEntry2 = i < this.pages[this.currentPageId].length - 1 ? this.pages[this.currentPageId][i + 1] : null;
			DrawableHelper drawableHelper = this.widgets.get(listEntry.getId());
			DrawableHelper drawableHelper2 = listEntry2 != null ? this.widgets.get(listEntry2.getId()) : null;
			PagedEntryListWidget.DualDrawableEntry dualDrawableEntry = new PagedEntryListWidget.DualDrawableEntry(drawableHelper, drawableHelper2);
			this.widgetEntries.add(dualDrawableEntry);
		}
	}

	public void setCurrentPage(int pageId) {
		if (pageId != this.currentPageId) {
			int i = this.currentPageId;
			this.currentPageId = pageId;
			this.initWidgetEntries();
			this.updateWidgetVisibility(i, pageId);
			this.scrollAmount = 0.0F;
		}
	}

	public int getCurrentPageId() {
		return this.currentPageId;
	}

	public int getMaxPages() {
		return this.pages.length;
	}

	public DrawableHelper getCurrentWidget() {
		return this.currentWidget;
	}

	public void previousPage() {
		if (this.currentPageId > 0) {
			this.setCurrentPage(this.currentPageId - 1);
		}
	}

	public void nextPage() {
		if (this.currentPageId < this.pages.length - 1) {
			this.setCurrentPage(this.currentPageId + 1);
		}
	}

	public DrawableHelper getWidget(int id) {
		return this.widgets.get(id);
	}

	private void updateWidgetVisibility(int disablePageId, int enablePageId) {
		for (PagedEntryListWidget.ListEntry listEntry : this.pages[disablePageId]) {
			if (listEntry != null) {
				this.setVisible(this.widgets.get(listEntry.getId()), false);
			}
		}

		for (PagedEntryListWidget.ListEntry listEntry2 : this.pages[enablePageId]) {
			if (listEntry2 != null) {
				this.setVisible(this.widgets.get(listEntry2.getId()), true);
			}
		}
	}

	private void setVisible(DrawableHelper drawable, boolean visible) {
		if (drawable instanceof ButtonWidget) {
			((ButtonWidget)drawable).visible = visible;
		} else if (drawable instanceof TextFieldWidget) {
			((TextFieldWidget)drawable).setVisible(visible);
		} else if (drawable instanceof LabelWidget) {
			((LabelWidget)drawable).visible = visible;
		}
	}

	@Nullable
	private DrawableHelper createWidget(@Nullable PagedEntryListWidget.ListEntry entry, int offsetX, boolean isSingle) {
		if (entry instanceof PagedEntryListWidget.LabelSupplierEntry) {
			return this.createSliderWidget(this.width / 2 - 155 + offsetX, 0, (PagedEntryListWidget.LabelSupplierEntry)entry);
		} else if (entry instanceof PagedEntryListWidget.ButtonEntry) {
			return this.createSwitchWidget(this.width / 2 - 155 + offsetX, 0, (PagedEntryListWidget.ButtonEntry)entry);
		} else if (entry instanceof PagedEntryListWidget.TextFieldEntry) {
			return this.createTextWidget(this.width / 2 - 155 + offsetX, 0, (PagedEntryListWidget.TextFieldEntry)entry);
		} else {
			return entry instanceof PagedEntryListWidget.TextFieldLabelEntry
				? this.createLabelWidget(this.width / 2 - 155 + offsetX, 0, (PagedEntryListWidget.TextFieldLabelEntry)entry, isSingle)
				: null;
		}
	}

	public void setActive(boolean active) {
		for (PagedEntryListWidget.DualDrawableEntry dualDrawableEntry : this.widgetEntries) {
			if (dualDrawableEntry.first instanceof ButtonWidget) {
				((ButtonWidget)dualDrawableEntry.first).active = active;
			}

			if (dualDrawableEntry.second instanceof ButtonWidget) {
				((ButtonWidget)dualDrawableEntry.second).active = active;
			}
		}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		boolean bl = super.mouseClicked(mouseX, mouseY, button);
		int i = this.getEntryAt(mouseX, mouseY);
		if (i >= 0) {
			PagedEntryListWidget.DualDrawableEntry dualDrawableEntry = this.getEntry(i);
			if (this.currentWidget != dualDrawableEntry.prevClicked && this.currentWidget != null && this.currentWidget instanceof TextFieldWidget) {
				((TextFieldWidget)this.currentWidget).setFocused(false);
			}

			this.currentWidget = dualDrawableEntry.prevClicked;
		}

		return bl;
	}

	private SliderWidget createSliderWidget(int x, int y, PagedEntryListWidget.LabelSupplierEntry entry) {
		SliderWidget sliderWidget = new SliderWidget(
			this.listener, entry.getId(), x, y, entry.getLabel(), entry.getMin(), entry.getMax(), entry.getCurrentValue(), entry.getLabelSupplier()
		);
		sliderWidget.visible = entry.isVisible();
		return sliderWidget;
	}

	private SwitchWidget createSwitchWidget(int x, int y, PagedEntryListWidget.ButtonEntry entry) {
		SwitchWidget switchWidget = new SwitchWidget(this.listener, entry.getId(), x, y, entry.getLabel(), entry.isValueEnabled());
		switchWidget.visible = entry.isVisible();
		return switchWidget;
	}

	private TextFieldWidget createTextWidget(int x, int y, PagedEntryListWidget.TextFieldEntry entry) {
		TextFieldWidget textFieldWidget = new TextFieldWidget(entry.getId(), this.client.textRenderer, x, y, 150, 20);
		textFieldWidget.setText(entry.getLabel());
		textFieldWidget.setListener(this.listener);
		textFieldWidget.setVisible(entry.isVisible());
		textFieldWidget.setTextPredicate(entry.getPredicate());
		return textFieldWidget;
	}

	private LabelWidget createLabelWidget(int x, int y, PagedEntryListWidget.TextFieldLabelEntry entry, boolean isSingle) {
		LabelWidget labelWidget;
		if (isSingle) {
			labelWidget = new LabelWidget(this.client.textRenderer, entry.getId(), x, y, this.width - x * 2, 20, -1);
		} else {
			labelWidget = new LabelWidget(this.client.textRenderer, entry.getId(), x, y, 150, 20, -1);
		}

		labelWidget.visible = entry.isVisible();
		labelWidget.addLine(entry.getLabel());
		labelWidget.centered();
		return labelWidget;
	}

	public void updateText(char id, int code) {
		if (this.currentWidget instanceof TextFieldWidget) {
			TextFieldWidget textFieldWidget = (TextFieldWidget)this.currentWidget;
			if (!Screen.isPaste(code)) {
				if (code == 15) {
					textFieldWidget.setFocused(false);
					int k = this.textFields.indexOf(this.currentWidget);
					if (Screen.hasShiftDown()) {
						if (k == 0) {
							k = this.textFields.size() - 1;
						} else {
							k--;
						}
					} else if (k == this.textFields.size() - 1) {
						k = 0;
					} else {
						k++;
					}

					this.currentWidget = (DrawableHelper)this.textFields.get(k);
					textFieldWidget = (TextFieldWidget)this.currentWidget;
					textFieldWidget.setFocused(true);
					int l = textFieldWidget.y + this.entryHeight;
					int m = textFieldWidget.y;
					if (l > this.yEnd) {
						this.scrollAmount = this.scrollAmount + (float)(l - this.yEnd);
					} else if (m < this.yStart) {
						this.scrollAmount = (float)m;
					}
				} else {
					textFieldWidget.keyPressed(id, code);
				}
			} else {
				String string = Screen.getClipboard();
				String[] strings = string.split(";");
				int i = this.textFields.indexOf(this.currentWidget);
				int j = i;

				for (String string2 : strings) {
					TextFieldWidget textFieldWidget2 = (TextFieldWidget)this.textFields.get(j);
					textFieldWidget2.setText(string2);
					textFieldWidget2.method_13838(textFieldWidget2.getId(), string2);
					if (j == this.textFields.size() - 1) {
						j = 0;
					} else {
						j++;
					}

					if (j == i) {
						break;
					}
				}
			}
		}
	}

	public PagedEntryListWidget.DualDrawableEntry getEntry(int i) {
		return (PagedEntryListWidget.DualDrawableEntry)this.widgetEntries.get(i);
	}

	@Override
	public int getEntryCount() {
		return this.widgetEntries.size();
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	public static class ButtonEntry extends PagedEntryListWidget.ListEntry {
		private final boolean valueEnabled;

		public ButtonEntry(int i, String string, boolean bl, boolean bl2) {
			super(i, string, bl);
			this.valueEnabled = bl2;
		}

		public boolean isValueEnabled() {
			return this.valueEnabled;
		}
	}

	public static class DualDrawableEntry implements EntryListWidget.Entry {
		private final MinecraftClient client = MinecraftClient.getInstance();
		private final DrawableHelper first;
		private final DrawableHelper second;
		private DrawableHelper prevClicked;

		public DualDrawableEntry(@Nullable DrawableHelper drawableHelper, @Nullable DrawableHelper drawableHelper2) {
			this.first = drawableHelper;
			this.second = drawableHelper2;
		}

		public DrawableHelper getFirst() {
			return this.first;
		}

		public DrawableHelper getSecond() {
			return this.second;
		}

		@Override
		public void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.method_9500(this.first, k, n, o, false, f);
			this.method_9500(this.second, k, n, o, false, f);
		}

		private void method_9500(DrawableHelper drawableHelper, int i, int j, int k, boolean bl, float f) {
			if (drawableHelper != null) {
				if (drawableHelper instanceof ButtonWidget) {
					this.method_9502((ButtonWidget)drawableHelper, i, j, k, bl, f);
				} else if (drawableHelper instanceof TextFieldWidget) {
					this.renderTextField((TextFieldWidget)drawableHelper, i, bl);
				} else if (drawableHelper instanceof LabelWidget) {
					this.renderLabel((LabelWidget)drawableHelper, i, j, k, bl);
				}
			}
		}

		private void method_9502(ButtonWidget buttonWidget, int i, int j, int k, boolean bl, float f) {
			buttonWidget.y = i;
			if (!bl) {
				buttonWidget.method_891(this.client, j, k, f);
			}
		}

		private void renderTextField(TextFieldWidget widget, int y, boolean hidden) {
			widget.y = y;
			if (!hidden) {
				widget.render();
			}
		}

		private void renderLabel(LabelWidget widget, int y, int mouseX, int mouseY, boolean hidden) {
			widget.y = y;
			if (!hidden) {
				widget.render(this.client, mouseX, mouseY);
			}
		}

		@Override
		public void method_9473(int i, int j, int k, float f) {
			this.method_9500(this.first, k, 0, 0, true, f);
			this.method_9500(this.second, k, 0, 0, true, f);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			boolean bl = this.drawableClicked(this.first, mouseX, mouseY, button);
			boolean bl2 = this.drawableClicked(this.second, mouseX, mouseY, button);
			return bl || bl2;
		}

		private boolean drawableClicked(DrawableHelper drawable, int mouseX, int mouseY, int button) {
			if (drawable == null) {
				return false;
			} else if (drawable instanceof ButtonWidget) {
				return this.buttonClicked((ButtonWidget)drawable, mouseX, mouseY, button);
			} else {
				if (drawable instanceof TextFieldWidget) {
					this.textFieldClicked((TextFieldWidget)drawable, mouseX, mouseY, button);
				}

				return false;
			}
		}

		private boolean buttonClicked(ButtonWidget widget, int mouseX, int mouseY, int button) {
			boolean bl = widget.isMouseOver(this.client, mouseX, mouseY);
			if (bl) {
				this.prevClicked = widget;
			}

			return bl;
		}

		private void textFieldClicked(TextFieldWidget widget, int mouseX, int mouseY, int button) {
			widget.method_920(mouseX, mouseY, button);
			if (widget.isFocused()) {
				this.prevClicked = widget;
			}
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
			this.drawableReleased(this.first, mouseX, mouseY, button);
			this.drawableReleased(this.second, mouseX, mouseY, button);
		}

		private void drawableReleased(DrawableHelper drawable, int mouseX, int mouseY, int button) {
			if (drawable != null) {
				if (drawable instanceof ButtonWidget) {
					this.buttonReleased((ButtonWidget)drawable, mouseX, mouseY, button);
				}
			}
		}

		private void buttonReleased(ButtonWidget widget, int mouseX, int mouseY, int button) {
			widget.mouseReleased(mouseX, mouseY);
		}
	}

	public static class LabelSupplierEntry extends PagedEntryListWidget.ListEntry {
		private final SliderWidget.LabelSupplier labelSupplier;
		private final float min;
		private final float max;
		private final float currentValue;

		public LabelSupplierEntry(int i, String string, boolean bl, SliderWidget.LabelSupplier labelSupplier, float f, float g, float h) {
			super(i, string, bl);
			this.labelSupplier = labelSupplier;
			this.min = f;
			this.max = g;
			this.currentValue = h;
		}

		public SliderWidget.LabelSupplier getLabelSupplier() {
			return this.labelSupplier;
		}

		public float getMin() {
			return this.min;
		}

		public float getMax() {
			return this.max;
		}

		public float getCurrentValue() {
			return this.currentValue;
		}
	}

	public static class ListEntry {
		private final int id;
		private final String label;
		private final boolean visible;

		public ListEntry(int i, String string, boolean bl) {
			this.id = i;
			this.label = string;
			this.visible = bl;
		}

		public int getId() {
			return this.id;
		}

		public String getLabel() {
			return this.label;
		}

		public boolean isVisible() {
			return this.visible;
		}
	}

	public interface Listener {
		void setBooleanValue(int id, boolean value);

		void setFloatValue(int id, float value);

		void setStringValue(int id, String text);
	}

	public static class TextFieldEntry extends PagedEntryListWidget.ListEntry {
		private final Predicate<String> predicate;

		public TextFieldEntry(int i, String string, boolean bl, Predicate<String> predicate) {
			super(i, string, bl);
			this.predicate = (Predicate<String>)MoreObjects.firstNonNull(predicate, Predicates.alwaysTrue());
		}

		public Predicate<String> getPredicate() {
			return this.predicate;
		}
	}

	public static class TextFieldLabelEntry extends PagedEntryListWidget.ListEntry {
		public TextFieldLabelEntry(int i, String string, boolean bl) {
			super(i, string, bl);
		}
	}
}
