package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;

public class StreamScreen extends Screen {
	private static final GameOptions.Option[] STREAM_NUMERICAL_VALUES = new GameOptions.Option[]{
		GameOptions.Option.STREAM_BYTES_PER_PIXEL,
		GameOptions.Option.STREAM_FPS,
		GameOptions.Option.STREAM_KBPS,
		GameOptions.Option.STREAM_SEND_METADATA,
		GameOptions.Option.STREAM_VOLUME_MIC,
		GameOptions.Option.STREAM_VOLUME_SYSTEM,
		GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR,
		GameOptions.Option.STREAM_COMPRESSION
	};
	private static final GameOptions.Option[] STREAM_BOOLEAN_VALUES = new GameOptions.Option[]{
		GameOptions.Option.STREAM_CHAT_ENABLED, GameOptions.Option.STREAM_CHAT_USER_FILTER
	};
	private final Screen parent;
	private final GameOptions options;
	private String title;
	private String chatTitle;
	private int chatTitleY;
	private boolean modified = false;

	public StreamScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	public void init() {
		int i = 0;
		this.title = I18n.translate("options.stream.title");
		this.chatTitle = I18n.translate("options.stream.chat.title");

		for (GameOptions.Option option : STREAM_NUMERICAL_VALUES) {
			if (option.isNumeric()) {
				this.buttons.add(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option));
			} else {
				this.buttons
					.add(
						new OptionButtonWidget(
							option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option, this.options.getValueMessage(option)
						)
					);
			}

			i++;
		}

		if (i % 2 == 1) {
			i++;
		}

		this.chatTitleY = this.height / 6 + 24 * (i >> 1) + 6;
		i += 2;

		for (GameOptions.Option option2 : STREAM_BOOLEAN_VALUES) {
			if (option2.isNumeric()) {
				this.buttons.add(new OptionSliderWidget(option2.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option2));
			} else {
				this.buttons
					.add(
						new OptionButtonWidget(
							option2.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), option2, this.options.getValueMessage(option2)
						)
					);
			}

			i++;
		}

		this.buttons.add(new ButtonWidget(200, this.width / 2 - 155, this.height / 6 + 168, 150, 20, I18n.translate("gui.done")));
		ButtonWidget buttonWidget = new ButtonWidget(201, this.width / 2 + 5, this.height / 6 + 168, 150, 20, I18n.translate("options.stream.ingestSelection"));
		buttonWidget.active = this.client.getTwitchStreamProvider().isReady() && this.client.getTwitchStreamProvider().getIngestServers().length > 0
			|| this.client.getTwitchStreamProvider().isTesting();
		this.buttons.add(buttonWidget);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id < 100 && button instanceof OptionButtonWidget) {
				GameOptions.Option option = ((OptionButtonWidget)button).getOption();
				this.options.getBooleanValue(option, 1);
				button.message = this.options.getValueMessage(GameOptions.Option.byOrdinal(button.id));
				if (this.client.getTwitchStreamProvider().isLive()
					&& option != GameOptions.Option.STREAM_CHAT_ENABLED
					&& option != GameOptions.Option.STREAM_CHAT_USER_FILTER) {
					this.modified = true;
				}
			} else if (button instanceof OptionSliderWidget) {
				if (button.id == GameOptions.Option.STREAM_VOLUME_MIC.getOrdinal()) {
					this.client.getTwitchStreamProvider().setStreamVolume();
				} else if (button.id == GameOptions.Option.STREAM_VOLUME_SYSTEM.getOrdinal()) {
					this.client.getTwitchStreamProvider().setStreamVolume();
				} else if (this.client.getTwitchStreamProvider().isLive()) {
					this.modified = true;
				}
			}

			if (button.id == 200) {
				this.client.options.save();
				this.client.setScreen(this.parent);
			} else if (button.id == 201) {
				this.client.options.save();
				this.client.setScreen(new StreamIngestScreen(this));
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		this.drawCenteredString(this.textRenderer, this.chatTitle, this.width / 2, this.chatTitleY, 16777215);
		if (this.modified) {
			this.drawCenteredString(
				this.textRenderer, Formatting.RED + I18n.translate("options.stream.changes"), this.width / 2, 20 + this.textRenderer.fontHeight, 16777215
			);
		}

		super.render(mouseX, mouseY, tickDelta);
	}
}
