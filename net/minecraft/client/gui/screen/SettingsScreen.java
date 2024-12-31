package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

public class SettingsScreen extends Screen {
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
	protected void init() {
		this.title = I18n.translate("options.title");
		int i = 0;

		for (GameOptions.Option option : OPTIONS) {
			if (option.isNumeric()) {
				this.addButton(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option));
			} else {
				OptionButtonWidget optionButtonWidget = new OptionButtonWidget(
					option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), option, this.options.method_18260(option)
				) {
					@Override
					public void method_18374(double d, double e) {
						SettingsScreen.this.options.method_18258(this.getOption(), 1);
						this.message = SettingsScreen.this.options.method_18260(GameOptions.Option.byOrdinal(this.id));
					}
				};
				this.addButton(optionButtonWidget);
			}

			i++;
		}

		if (this.client.world != null) {
			Difficulty difficulty = this.client.world.method_16346();
			this.difficultyButton = new ButtonWidget(
				108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyButtonText(difficulty)
			) {
				@Override
				public void method_18374(double d, double e) {
					SettingsScreen.this.client.world.method_3588().setDifficulty(Difficulty.byOrdinal(SettingsScreen.this.client.world.method_16346().getId() + 1));
					SettingsScreen.this.difficultyButton.message = SettingsScreen.this.getDifficultyButtonText(SettingsScreen.this.client.world.method_16346());
				}
			};
			this.addButton(this.difficultyButton);
			if (this.client.isInSingleplayer() && !this.client.world.method_3588().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = new LockButtonWidget(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
					@Override
					public void method_18374(double d, double e) {
						SettingsScreen.this.client
							.setScreen(
								new ConfirmScreen(
									SettingsScreen.this,
									new TranslatableText("difficulty.lock.title").asFormattedString(),
									new TranslatableText("difficulty.lock.question", new TranslatableText(SettingsScreen.this.client.world.method_3588().getDifficulty().getName()))
										.asFormattedString(),
									109
								)
							);
					}
				};
				this.addButton(this.lockDifficultyButton);
				this.lockDifficultyButton.setLocked(this.client.world.method_3588().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.active = false;
			}
		} else {
			this.addButton(
				new OptionButtonWidget(
					GameOptions.Option.REALMS_NOTIFICATIONS.getOrdinal(),
					this.width / 2 - 155 + i % 2 * 160,
					this.height / 6 - 12 + 24 * (i >> 1),
					GameOptions.Option.REALMS_NOTIFICATIONS,
					this.options.method_18260(GameOptions.Option.REALMS_NOTIFICATIONS)
				) {
					@Override
					public void method_18374(double d, double e) {
						SettingsScreen.this.options.method_18258(this.getOption(), 1);
						this.message = SettingsScreen.this.options.method_18260(GameOptions.Option.byOrdinal(this.id));
					}
				}
			);
		}

		this.addButton(new ButtonWidget(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new SkinOptionsScreen(SettingsScreen.this));
			}
		});
		this.addButton(new ButtonWidget(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new SoundsScreen(SettingsScreen.this, SettingsScreen.this.options));
			}
		});
		this.addButton(new ButtonWidget(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.video")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new VideoOptionsScreen(SettingsScreen.this, SettingsScreen.this.options));
			}
		});
		this.addButton(new ButtonWidget(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.controls")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new ControlsOptionsScreen(SettingsScreen.this, SettingsScreen.this.options));
			}
		});
		this.addButton(
			new ButtonWidget(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.language")) {
				@Override
				public void method_18374(double d, double e) {
					SettingsScreen.this.client.options.save();
					SettingsScreen.this.client
						.setScreen(new LanguageOptionsScreen(SettingsScreen.this, SettingsScreen.this.options, SettingsScreen.this.client.getLanguageManager()));
				}
			}
		);
		this.addButton(new ButtonWidget(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new ChatOptionsScreen(SettingsScreen.this, SettingsScreen.this.options));
			}
		});
		this.addButton(new ButtonWidget(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new ResourcePackScreen(SettingsScreen.this));
			}
		});
		this.addButton(new ButtonWidget(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.snooper.view")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(new SnooperScreen(SettingsScreen.this, SettingsScreen.this.options));
			}
		});
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				SettingsScreen.this.client.options.save();
				SettingsScreen.this.client.setScreen(SettingsScreen.this.parent);
			}
		});
	}

	public String getDifficultyButtonText(Difficulty difficulty) {
		return new TranslatableText("options.difficulty").append(": ").append(difficulty.method_15537()).asFormattedString();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.client.setScreen(this);
		if (i == 109 && bl && this.client.world != null) {
			this.client.world.method_3588().setDifficultyLocked(true);
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.active = false;
			this.difficultyButton.active = false;
		}
	}

	@Override
	public void method_18608() {
		this.client.options.save();
		super.method_18608();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
