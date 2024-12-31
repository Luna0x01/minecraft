package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;

public class SnooperScreen extends Screen {
	private final Screen parent;
	private final GameOptions options;
	private final List<String> entryNames = Lists.newArrayList();
	private final List<String> entryValues = Lists.newArrayList();
	private String title;
	private String[] description;
	private SnooperScreen.SnooperEntryListWidget entryList;
	private ButtonWidget enableButton;

	public SnooperScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	public void init() {
		this.title = I18n.translate("options.snooper.title");
		String string = I18n.translate("options.snooper.desc");
		List<String> list = Lists.newArrayList();

		for (String string2 : this.textRenderer.wrapLines(string, this.width - 30)) {
			list.add(string2);
		}

		this.description = (String[])list.toArray(new String[list.size()]);
		this.entryNames.clear();
		this.entryValues.clear();
		this.enableButton = this.addButton(
			new ButtonWidget(1, this.width / 2 - 152, this.height - 30, 150, 20, this.options.getValueMessage(GameOptions.Option.SNOOPER_ENABLED))
		);
		this.buttons.add(new ButtonWidget(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.translate("gui.done")));
		boolean bl = this.client.getServer() != null && this.client.getServer().getSnooper() != null;

		for (Entry<String, String> entry : new TreeMap(this.client.getSnooper().getAllInfo()).entrySet()) {
			this.entryNames.add((bl ? "C " : "") + (String)entry.getKey());
			this.entryValues.add(this.textRenderer.trimToWidth((String)entry.getValue(), this.width - 220));
		}

		if (bl) {
			for (Entry<String, String> entry2 : new TreeMap(this.client.getServer().getSnooper().getAllInfo()).entrySet()) {
				this.entryNames.add("S " + (String)entry2.getKey());
				this.entryValues.add(this.textRenderer.trimToWidth((String)entry2.getValue(), this.width - 220));
			}
		}

		this.entryList = new SnooperScreen.SnooperEntryListWidget();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.entryList.handleMouse();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 2) {
				this.options.save();
				this.options.save();
				this.client.setScreen(this.parent);
			}

			if (button.id == 1) {
				this.options.getBooleanValue(GameOptions.Option.SNOOPER_ENABLED, 1);
				this.enableButton.message = this.options.getValueMessage(GameOptions.Option.SNOOPER_ENABLED);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.entryList.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		int i = 22;

		for (String string : this.description) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 8421504);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	class SnooperEntryListWidget extends ListWidget {
		public SnooperEntryListWidget() {
			super(
				SnooperScreen.this.client,
				SnooperScreen.this.width,
				SnooperScreen.this.height,
				80,
				SnooperScreen.this.height - 40,
				SnooperScreen.this.textRenderer.fontHeight + 1
			);
		}

		@Override
		protected int getEntryCount() {
			return SnooperScreen.this.entryNames.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return false;
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			SnooperScreen.this.textRenderer.draw((String)SnooperScreen.this.entryNames.get(index), 10, y, 16777215);
			SnooperScreen.this.textRenderer.draw((String)SnooperScreen.this.entryValues.get(index), 230, y, 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 10;
		}
	}
}
