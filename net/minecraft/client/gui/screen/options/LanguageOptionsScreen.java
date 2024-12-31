package net.minecraft.client.gui.screen.options;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.Window;

public class LanguageOptionsScreen extends Screen {
	protected Screen parent;
	private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;
	private final GameOptions options;
	private final LanguageManager languageManager;
	private OptionButtonWidget forceUnicodeButton;
	private OptionButtonWidget doneButton;

	public LanguageOptionsScreen(Screen screen, GameOptions gameOptions, LanguageManager languageManager) {
		this.parent = screen;
		this.options = gameOptions;
		this.languageManager = languageManager;
	}

	@Override
	public void init() {
		this.forceUnicodeButton = this.addButton(
			new OptionButtonWidget(
				100, this.width / 2 - 155, this.height - 38, GameOptions.Option.FORCE_UNICODE, this.options.getValueMessage(GameOptions.Option.FORCE_UNICODE)
			)
		);
		this.doneButton = this.addButton(new OptionButtonWidget(6, this.width / 2 - 155 + 160, this.height - 38, I18n.translate("gui.done")));
		this.languageSelectionList = new LanguageOptionsScreen.LanguageSelectionListWidget(this.client);
		this.languageSelectionList.setButtonIds(7, 8);
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.languageSelectionList.handleMouse();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			switch (button.id) {
				case 5:
					break;
				case 6:
					this.client.setScreen(this.parent);
					break;
				case 100:
					if (button instanceof OptionButtonWidget) {
						this.options.getBooleanValue(((OptionButtonWidget)button).getOption(), 1);
						button.message = this.options.getValueMessage(GameOptions.Option.FORCE_UNICODE);
						Window window = new Window(this.client);
						int i = window.getWidth();
						int j = window.getHeight();
						this.init(this.client, i, j);
					}
					break;
				default:
					this.languageSelectionList.buttonClicked(button);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.languageSelectionList.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, I18n.translate("options.language"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.textRenderer, "(" + I18n.translate("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.render(mouseX, mouseY, tickDelta);
	}

	class LanguageSelectionListWidget extends ListWidget {
		private final List<String> languageCodes = Lists.newArrayList();
		private final Map<String, LanguageDefinition> languageDefinitions = Maps.newHashMap();

		public LanguageSelectionListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);

			for (LanguageDefinition languageDefinition : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
				this.languageDefinitions.put(languageDefinition.getCode(), languageDefinition);
				this.languageCodes.add(languageDefinition.getCode());
			}
		}

		@Override
		protected int getEntryCount() {
			return this.languageCodes.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			LanguageDefinition languageDefinition = (LanguageDefinition)this.languageDefinitions.get(this.languageCodes.get(index));
			LanguageOptionsScreen.this.languageManager.setLanguage(languageDefinition);
			LanguageOptionsScreen.this.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			LanguageOptionsScreen.this.textRenderer
				.setUnicode(LanguageOptionsScreen.this.languageManager.forcesUnicodeFont() || LanguageOptionsScreen.this.options.forcesUnicodeFont);
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.isRightToLeft());
			LanguageOptionsScreen.this.doneButton.message = I18n.translate("gui.done");
			LanguageOptionsScreen.this.forceUnicodeButton.message = LanguageOptionsScreen.this.options.getValueMessage(GameOptions.Option.FORCE_UNICODE);
			LanguageOptionsScreen.this.options.save();
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return ((String)this.languageCodes.get(index)).equals(LanguageOptionsScreen.this.languageManager.getLanguage().getCode());
		}

		@Override
		protected int getMaxPosition() {
			return this.getEntryCount() * 18;
		}

		@Override
		protected void renderBackground() {
			LanguageOptionsScreen.this.renderBackground();
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(true);
			LanguageOptionsScreen.this.drawCenteredString(
				LanguageOptionsScreen.this.textRenderer,
				((LanguageDefinition)this.languageDefinitions.get(this.languageCodes.get(index))).toString(),
				this.width / 2,
				y + 1,
				16777215
			);
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
		}
	}
}
