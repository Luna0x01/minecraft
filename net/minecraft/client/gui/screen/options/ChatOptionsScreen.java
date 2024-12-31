package net.minecraft.client.gui.screen.options;

import net.minecraft.class_3253;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;

public class ChatOptionsScreen extends Screen {
	private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.CHAT_VISIBILITY,
		GameOptions.Option.CHAT_COLOR,
		GameOptions.Option.CHAT_LINKS,
		GameOptions.Option.CHAT_OPACITY,
		GameOptions.Option.CHAT_LINKS_PROMPT,
		GameOptions.Option.CHAT_SCALE,
		GameOptions.Option.CHAT_HEIGHT_FOCUSED,
		GameOptions.Option.CHAT_HEIGHT_UNFOCUSED,
		GameOptions.Option.CHAT_WIDTH,
		GameOptions.Option.REDUCED_DEBUG_INFO,
		GameOptions.Option.NARRATOR,
		GameOptions.Option.AUTO_SUGGESTIONS
	};
	private final Screen parent;
	private final GameOptions options;
	private String title;
	private OptionButtonWidget field_15944;

	public ChatOptionsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		this.title = I18n.translate("options.chat.title");
		int i = 0;

		for (GameOptions.Option option : OPTIONS) {
			if (option.isNumeric()) {
				this.addButton(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option));
			} else {
				OptionButtonWidget optionButtonWidget = new OptionButtonWidget(
					option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option, this.options.method_18260(option)
				) {
					@Override
					public void method_18374(double d, double e) {
						ChatOptionsScreen.this.options.method_18258(this.getOption(), 1);
						this.message = ChatOptionsScreen.this.options.method_18260(GameOptions.Option.byOrdinal(this.id));
					}
				};
				this.addButton(optionButtonWidget);
				if (option == GameOptions.Option.NARRATOR) {
					this.field_15944 = optionButtonWidget;
					optionButtonWidget.active = class_3253.field_15887.method_14473();
				}
			}

			i++;
		}

		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 144, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				ChatOptionsScreen.this.client.options.save();
				ChatOptionsScreen.this.client.setScreen(ChatOptionsScreen.this.parent);
			}
		});
	}

	@Override
	public void method_18608() {
		this.client.options.save();
		super.method_18608();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	public void method_14501() {
		this.field_15944.message = this.options.method_18260(GameOptions.Option.byOrdinal(this.field_15944.id));
	}
}
