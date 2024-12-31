package net.minecraft.client.gui.screen.options;

import net.minecraft.class_4107;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;

public class ControlsOptionsScreen extends Screen {
	private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.INVERT_MOUSE, GameOptions.Option.SENSITIVITY, GameOptions.Option.TOUCHSCREEN, GameOptions.Option.AUTO_JUMP
	};
	private final Screen parent;
	protected String title = "Controls";
	private final GameOptions options;
	public KeyBinding selectedKeyBinding;
	public long time;
	private ControlsListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public ControlsOptionsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	protected void init() {
		this.keyBindingListWidget = new ControlsListWidget(this, this.client);
		this.field_20307.add(this.keyBindingListWidget);
		this.method_18421(this.keyBindingListWidget);
		this.addButton(new ButtonWidget(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				ControlsOptionsScreen.this.client.setScreen(ControlsOptionsScreen.this.parent);
			}
		});
		this.resetButton = this.addButton(new ButtonWidget(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll")) {
			@Override
			public void method_18374(double d, double e) {
				for (KeyBinding keyBinding : ControlsOptionsScreen.this.client.options.allKeys) {
					keyBinding.method_18170(keyBinding.method_18172());
				}

				KeyBinding.updateKeysByCode();
			}
		});
		this.title = I18n.translate("controls.title");
		int i = 0;

		for (GameOptions.Option option : OPTIONS) {
			if (option.isNumeric()) {
				this.addButton(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option));
			} else {
				this.addButton(
					new OptionButtonWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option, this.options.method_18260(option)) {
						@Override
						public void method_18374(double d, double e) {
							ControlsOptionsScreen.this.options.method_18258(this.getOption(), 1);
							this.message = ControlsOptionsScreen.this.options.method_18260(GameOptions.Option.byOrdinal(this.id));
						}
					}
				);
			}

			i++;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.selectedKeyBinding != null) {
			this.options.method_18255(this.selectedKeyBinding, class_4107.class_4109.MOUSE.method_18162(i));
			this.selectedKeyBinding = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else if (i == 0 && this.keyBindingListWidget.mouseClicked(d, e, i)) {
			this.method_18425(true);
			this.method_18421(this.keyBindingListWidget);
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0 && this.keyBindingListWidget.mouseReleased(d, e, i)) {
			this.method_18425(false);
			return true;
		} else {
			return super.mouseReleased(d, e, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.selectedKeyBinding != null) {
			if (i == 256) {
				this.options.method_18255(this.selectedKeyBinding, class_4107.field_19910);
			} else {
				this.options.method_18255(this.selectedKeyBinding, class_4107.method_18155(i, j));
			}

			this.selectedKeyBinding = null;
			this.time = Util.method_20227();
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.keyBindingListWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.options.allKeys) {
			if (!keyBinding.method_18175()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(mouseX, mouseY, tickDelta);
	}
}
