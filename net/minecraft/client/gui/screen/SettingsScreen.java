package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

public class SettingsScreen extends Screen implements IdentifiableBooleanConsumer {
	private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{GameOptions.Option.FIELD_OF_VIEW};
	private final Screen parent;
	private final GameOptions options;
	private ButtonWidget difficultyButton;
	private LockButtonWidget lockDifficultyButton;
	protected String title = "Options";

	public SettingsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	public void init() {
		int i = 0;
		this.title = I18n.translate("options.title");

		for (GameOptions.Option option : OPTIONS) {
			if (option.isNumeric()) {
				this.buttons.add(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option));
			} else {
				OptionButtonWidget optionButtonWidget = new OptionButtonWidget(
					option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option, this.options.getValueMessage(option)
				);
				this.buttons.add(optionButtonWidget);
			}

			i++;
		}

		if (this.client.world != null) {
			Difficulty difficulty = this.client.world.getGlobalDifficulty();
			this.difficultyButton = new ButtonWidget(
				108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyButtonText(difficulty)
			);
			this.buttons.add(this.difficultyButton);
			if (this.client.isInSingleplayer() && !this.client.world.getLevelProperties().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = new LockButtonWidget(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y);
				this.buttons.add(this.lockDifficultyButton);
				this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.active = false;
			}
		} else {
			this.buttons
				.add(
					new OptionButtonWidget(
						GameOptions.Option.REALMS_NOTIFICATIONS.getOrdinal(),
						this.width / 2 - 155 + i % 2 * 160,
						this.height / 6 - 12 + 24 * (i >> 1),
						GameOptions.Option.REALMS_NOTIFICATIONS,
						this.options.getValueMessage(GameOptions.Option.REALMS_NOTIFICATIONS)
					)
				);
		}

		this.buttons.add(new ButtonWidget(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")));
		this.buttons.add(new ButtonWidget(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds")));
		this.buttons.add(new ButtonWidget(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.video")));
		this.buttons.add(new ButtonWidget(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.controls")));
		this.buttons.add(new ButtonWidget(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.language")));
		this.buttons.add(new ButtonWidget(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title")));
		this.buttons.add(new ButtonWidget(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack")));
		this.buttons.add(new ButtonWidget(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.snooper.view")));
		this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")));
	}

	public String getDifficultyButtonText(Difficulty difficulty) {
		Text text = new LiteralText("");
		text.append(new TranslatableText("options.difficulty"));
		text.append(": ");
		text.append(new TranslatableText(difficulty.getName()));
		return text.asFormattedString();
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		this.client.setScreen(this);
		if (id == 109 && confirmed && this.client.world != null) {
			this.client.world.getLevelProperties().setDifficultyLocked(true);
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.active = false;
			this.difficultyButton.active = false;
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id < 100 && button instanceof OptionButtonWidget) {
				GameOptions.Option option = ((OptionButtonWidget)button).getOption();
				this.options.getBooleanValue(option, 1);
				button.message = this.options.getValueMessage(GameOptions.Option.byOrdinal(button.id));
			}

			if (button.id == 108) {
				this.client.world.getLevelProperties().setDifficulty(Difficulty.byOrdinal(this.client.world.getGlobalDifficulty().getId() + 1));
				this.difficultyButton.message = this.getDifficultyButtonText(this.client.world.getGlobalDifficulty());
			}

			if (button.id == 109) {
				this.client
					.setScreen(
						new ConfirmScreen(
							this,
							new TranslatableText("difficulty.lock.title").asFormattedString(),
							new TranslatableText("difficulty.lock.question", new TranslatableText(this.client.world.getLevelProperties().getDifficulty().getName()))
								.asFormattedString(),
							109
						)
					);
			}

			if (button.id == 110) {
				this.client.options.save();
				this.client.setScreen(new SkinOptionsScreen(this));
			}

			if (button.id == 101) {
				this.client.options.save();
				this.client.setScreen(new VideoOptionsScreen(this, this.options));
			}

			if (button.id == 100) {
				this.client.options.save();
				this.client.setScreen(new ControlsOptionsScreen(this, this.options));
			}

			if (button.id == 102) {
				this.client.options.save();
				this.client.setScreen(new LanguageOptionsScreen(this, this.options, this.client.getLanguageManager()));
			}

			if (button.id == 103) {
				this.client.options.save();
				this.client.setScreen(new ChatOptionsScreen(this, this.options));
			}

			if (button.id == 104) {
				this.client.options.save();
				this.client.setScreen(new SnooperScreen(this, this.options));
			}

			if (button.id == 200) {
				this.client.options.save();
				this.client.setScreen(this.parent);
			}

			if (button.id == 105) {
				this.client.options.save();
				this.client.setScreen(new ResourcePackScreen(this));
			}

			if (button.id == 106) {
				this.client.options.save();
				this.client.setScreen(new SoundsScreen(this, this.options));
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
