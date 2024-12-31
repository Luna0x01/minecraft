package net.minecraft.client.gui.screen.options;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.class_4122;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;

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
	public class_4122 getFocused() {
		return this.languageSelectionList;
	}

	@Override
	protected void init() {
		this.languageSelectionList = new LanguageOptionsScreen.LanguageSelectionListWidget(this.client);
		this.field_20307.add(this.languageSelectionList);
		this.forceUnicodeButton = this.addButton(
			new OptionButtonWidget(
				100, this.width / 2 - 155, this.height - 38, GameOptions.Option.FORCE_UNICODE, this.options.method_18260(GameOptions.Option.FORCE_UNICODE)
			) {
				@Override
				public void method_18374(double d, double e) {
					LanguageOptionsScreen.this.options.method_18258(this.getOption(), 1);
					this.message = LanguageOptionsScreen.this.options.method_18260(GameOptions.Option.FORCE_UNICODE);
					LanguageOptionsScreen.this.method_18594();
				}
			}
		);
		this.doneButton = this.addButton(new OptionButtonWidget(6, this.width / 2 - 155 + 160, this.height - 38, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				LanguageOptionsScreen.this.client.setScreen(LanguageOptionsScreen.this.parent);
			}
		});
		super.init();
	}

	private void method_18594() {
		this.client.field_19944.method_18314();
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
		protected boolean method_18414(int i, int j, double d, double e) {
			LanguageDefinition languageDefinition = (LanguageDefinition)this.languageDefinitions.get(this.languageCodes.get(i));
			LanguageOptionsScreen.this.languageManager.setLanguage(languageDefinition);
			LanguageOptionsScreen.this.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.isRightToLeft());
			LanguageOptionsScreen.this.doneButton.message = I18n.translate("gui.done");
			LanguageOptionsScreen.this.forceUnicodeButton.message = LanguageOptionsScreen.this.options.method_18260(GameOptions.Option.FORCE_UNICODE);
			LanguageOptionsScreen.this.options.save();
			LanguageOptionsScreen.this.method_18594();
			return true;
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
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(true);
			this.drawCenteredString(
				LanguageOptionsScreen.this.textRenderer,
				((LanguageDefinition)this.languageDefinitions.get(this.languageCodes.get(i))).toString(),
				this.width / 2,
				k + 1,
				16777215
			);
			LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
		}
	}
}
