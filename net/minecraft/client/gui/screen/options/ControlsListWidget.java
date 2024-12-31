package net.minecraft.client.gui.screen.options;

import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

public class ControlsListWidget extends EntryListWidget {
	private final ControlsOptionsScreen parent;
	private final MinecraftClient mc;
	private final EntryListWidget.Entry[] entries;
	private int maxKeyNameLength = 0;

	public ControlsListWidget(ControlsOptionsScreen controlsOptionsScreen, MinecraftClient minecraftClient) {
		super(minecraftClient, controlsOptionsScreen.width + 45, controlsOptionsScreen.height, 63, controlsOptionsScreen.height - 32, 20);
		this.parent = controlsOptionsScreen;
		this.mc = minecraftClient;
		KeyBinding[] keyBindings = (KeyBinding[])ArrayUtils.clone(minecraftClient.options.allKeys);
		this.entries = new EntryListWidget.Entry[keyBindings.length + KeyBinding.getCategories().size()];
		Arrays.sort(keyBindings);
		int i = 0;
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.getCategory();
			if (!string2.equals(string)) {
				string = string2;
				this.entries[i++] = new ControlsListWidget.CategoryEntry(string2);
			}

			int l = minecraftClient.textRenderer.getStringWidth(I18n.translate(keyBinding.getTranslationKey()));
			if (l > this.maxKeyNameLength) {
				this.maxKeyNameLength = l;
			}

			this.entries[i++] = new ControlsListWidget.KeyBindingEntry(keyBinding);
		}
	}

	@Override
	protected int getEntryCount() {
		return this.entries.length;
	}

	@Override
	public EntryListWidget.Entry getEntry(int index) {
		return this.entries[index];
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	public class CategoryEntry implements EntryListWidget.Entry {
		private final String name;
		private final int nameWidth;

		public CategoryEntry(String string) {
			this.name = I18n.translate(string);
			this.nameWidth = ControlsListWidget.this.mc.textRenderer.getStringWidth(this.name);
		}

		@Override
		public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			ControlsListWidget.this.mc
				.textRenderer
				.draw(
					this.name,
					ControlsListWidget.this.mc.currentScreen.width / 2 - this.nameWidth / 2,
					y + rowHeight - ControlsListWidget.this.mc.textRenderer.fontHeight - 1,
					16777215
				);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			return false;
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
		}

		@Override
		public void updatePosition(int index, int x, int y) {
		}
	}

	public class KeyBindingEntry implements EntryListWidget.Entry {
		private final KeyBinding keyBinding;
		private final String name;
		private final ButtonWidget keyBindingButton;
		private final ButtonWidget resetButton;

		private KeyBindingEntry(KeyBinding keyBinding) {
			this.keyBinding = keyBinding;
			this.name = I18n.translate(keyBinding.getTranslationKey());
			this.keyBindingButton = new ButtonWidget(0, 0, 0, 75, 20, I18n.translate(keyBinding.getTranslationKey()));
			this.resetButton = new ButtonWidget(0, 0, 0, 50, 20, I18n.translate("controls.reset"));
		}

		@Override
		public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			boolean bl = ControlsListWidget.this.parent.selectedKeyBinding == this.keyBinding;
			ControlsListWidget.this.mc
				.textRenderer
				.draw(this.name, x + 90 - ControlsListWidget.this.maxKeyNameLength, y + rowHeight / 2 - ControlsListWidget.this.mc.textRenderer.fontHeight / 2, 16777215);
			this.resetButton.x = x + 190;
			this.resetButton.y = y;
			this.resetButton.active = this.keyBinding.getCode() != this.keyBinding.getDefaultCode();
			this.resetButton.render(ControlsListWidget.this.mc, mouseX, mouseY);
			this.keyBindingButton.x = x + 105;
			this.keyBindingButton.y = y;
			this.keyBindingButton.message = GameOptions.getFormattedNameForKeyCode(this.keyBinding.getCode());
			boolean bl2 = false;
			if (this.keyBinding.getCode() != 0) {
				for (KeyBinding keyBinding : ControlsListWidget.this.mc.options.allKeys) {
					if (keyBinding != this.keyBinding && keyBinding.getCode() == this.keyBinding.getCode()) {
						bl2 = true;
						break;
					}
				}
			}

			if (bl) {
				this.keyBindingButton.message = Formatting.WHITE + "> " + Formatting.YELLOW + this.keyBindingButton.message + Formatting.WHITE + " <";
			} else if (bl2) {
				this.keyBindingButton.message = Formatting.RED + this.keyBindingButton.message;
			}

			this.keyBindingButton.render(ControlsListWidget.this.mc, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			if (this.keyBindingButton.isMouseOver(ControlsListWidget.this.mc, mouseX, mouseY)) {
				ControlsListWidget.this.parent.selectedKeyBinding = this.keyBinding;
				return true;
			} else if (this.resetButton.isMouseOver(ControlsListWidget.this.mc, mouseX, mouseY)) {
				ControlsListWidget.this.mc.options.setKeyBindingCode(this.keyBinding, this.keyBinding.getDefaultCode());
				KeyBinding.updateKeysByCode();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
			this.keyBindingButton.mouseReleased(mouseX, mouseY);
			this.resetButton.mouseReleased(mouseX, mouseY);
		}

		@Override
		public void updatePosition(int index, int x, int y) {
		}
	}
}
