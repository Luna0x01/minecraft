package net.minecraft.client.gui.screen.options;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

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
	public void init() {
		this.keyBindingListWidget = new ControlsListWidget(this, this.client);
		this.buttons.add(new ButtonWidget(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done")));
		this.resetButton = this.addButton(new ButtonWidget(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll")));
		this.title = I18n.translate("controls.title");
		int i = 0;

		for (GameOptions.Option option : OPTIONS) {
			if (option.isNumeric()) {
				this.buttons.add(new OptionSliderWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option));
			} else {
				this.buttons
					.add(new OptionButtonWidget(option.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), option, this.options.getValueMessage(option)));
			}

			i++;
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.keyBindingListWidget.handleMouse();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 200) {
			this.client.setScreen(this.parent);
		} else if (button.id == 201) {
			for (KeyBinding keyBinding : this.client.options.allKeys) {
				keyBinding.setCode(keyBinding.getDefaultCode());
			}

			KeyBinding.updateKeysByCode();
		} else if (button.id < 100 && button instanceof OptionButtonWidget) {
			this.options.getBooleanValue(((OptionButtonWidget)button).getOption(), 1);
			button.message = this.options.getValueMessage(GameOptions.Option.byOrdinal(button.id));
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (this.selectedKeyBinding != null) {
			this.options.setKeyBindingCode(this.selectedKeyBinding, -100 + button);
			this.selectedKeyBinding = null;
			KeyBinding.updateKeysByCode();
		} else if (button != 0 || !this.keyBindingListWidget.mouseClicked(mouseX, mouseY, button)) {
			super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (button != 0 || !this.keyBindingListWidget.mouseReleased(mouseX, mouseY, button)) {
			super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (this.selectedKeyBinding != null) {
			if (code == 1) {
				this.options.setKeyBindingCode(this.selectedKeyBinding, 0);
			} else if (code != 0) {
				this.options.setKeyBindingCode(this.selectedKeyBinding, code);
			} else if (id > 0) {
				this.options.setKeyBindingCode(this.selectedKeyBinding, id + 256);
			}

			this.selectedKeyBinding = null;
			this.time = MinecraftClient.getTime();
			KeyBinding.updateKeysByCode();
		} else {
			super.keyPressed(id, code);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.keyBindingListWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		boolean bl = false;

		for (KeyBinding keyBinding : this.options.allKeys) {
			if (keyBinding.getCode() != keyBinding.getDefaultCode()) {
				bl = true;
				break;
			}
		}

		this.resetButton.active = bl;
		super.render(mouseX, mouseY, tickDelta);
	}
}
