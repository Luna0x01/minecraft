package net.minecraft.client.gui.screen.options;

import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

public class ControlsListWidget extends EntryListWidget<ControlsListWidget.class_4161> {
	private final ControlsOptionsScreen parent;
	private final MinecraftClient mc;
	private int maxKeyNameLength;

	public ControlsListWidget(ControlsOptionsScreen controlsOptionsScreen, MinecraftClient minecraftClient) {
		super(minecraftClient, controlsOptionsScreen.width + 45, controlsOptionsScreen.height, 63, controlsOptionsScreen.height - 32, 20);
		this.parent = controlsOptionsScreen;
		this.mc = minecraftClient;
		KeyBinding[] keyBindings = (KeyBinding[])ArrayUtils.clone(minecraftClient.options.allKeys);
		Arrays.sort(keyBindings);
		String string = null;

		for (KeyBinding keyBinding : keyBindings) {
			String string2 = keyBinding.getCategory();
			if (!string2.equals(string)) {
				string = string2;
				this.method_18398(new ControlsListWidget.CategoryEntry(string2));
			}

			int i = minecraftClient.textRenderer.getStringWidth(I18n.translate(keyBinding.getTranslationKey()));
			if (i > this.maxKeyNameLength) {
				this.maxKeyNameLength = i;
			}

			this.method_18398(new ControlsListWidget.KeyBindingEntry(keyBinding));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	public class CategoryEntry extends ControlsListWidget.class_4161 {
		private final String name;
		private final int nameWidth;

		public CategoryEntry(String string) {
			this.name = I18n.translate(string);
			this.nameWidth = ControlsListWidget.this.mc.textRenderer.getStringWidth(this.name);
		}

		@Override
		public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
			ControlsListWidget.this.mc
				.textRenderer
				.method_18355(
					this.name,
					(float)(ControlsListWidget.this.mc.currentScreen.width / 2 - this.nameWidth / 2),
					(float)(this.method_18403() + j - ControlsListWidget.this.mc.textRenderer.fontHeight - 1),
					16777215
				);
		}
	}

	public class KeyBindingEntry extends ControlsListWidget.class_4161 {
		private final KeyBinding keyBinding;
		private final String name;
		private final ButtonWidget keyBindingButton;
		private final ButtonWidget resetButton;

		private KeyBindingEntry(KeyBinding keyBinding) {
			this.keyBinding = keyBinding;
			this.name = I18n.translate(keyBinding.getTranslationKey());
			this.keyBindingButton = new ButtonWidget(0, 0, 0, 75, 20, I18n.translate(keyBinding.getTranslationKey())) {
				@Override
				public void method_18374(double d, double e) {
					ControlsListWidget.this.parent.selectedKeyBinding = keyBinding;
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 0, 50, 20, I18n.translate("controls.reset")) {
				@Override
				public void method_18374(double d, double e) {
					ControlsListWidget.this.mc.options.method_18255(keyBinding, keyBinding.method_18172());
					KeyBinding.updateKeysByCode();
				}
			};
		}

		@Override
		public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
			int m = this.method_18403();
			int n = this.method_18404();
			boolean bl2 = ControlsListWidget.this.parent.selectedKeyBinding == this.keyBinding;
			ControlsListWidget.this.mc
				.textRenderer
				.method_18355(
					this.name,
					(float)(n + 90 - ControlsListWidget.this.maxKeyNameLength),
					(float)(m + j / 2 - ControlsListWidget.this.mc.textRenderer.fontHeight / 2),
					16777215
				);
			this.resetButton.x = n + 190;
			this.resetButton.y = m;
			this.resetButton.active = !this.keyBinding.method_18175();
			this.resetButton.method_891(k, l, f);
			this.keyBindingButton.x = n + 105;
			this.keyBindingButton.y = m;
			this.keyBindingButton.message = this.keyBinding.method_18174();
			boolean bl3 = false;
			if (!this.keyBinding.method_18173()) {
				for (KeyBinding keyBinding : ControlsListWidget.this.mc.options.allKeys) {
					if (keyBinding != this.keyBinding && this.keyBinding.method_18171(keyBinding)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.keyBindingButton.message = Formatting.WHITE + "> " + Formatting.YELLOW + this.keyBindingButton.message + Formatting.WHITE + " <";
			} else if (bl3) {
				this.keyBindingButton.message = Formatting.RED + this.keyBindingButton.message;
			}

			this.keyBindingButton.method_891(k, l, f);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.keyBindingButton.mouseClicked(d, e, i) ? true : this.resetButton.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			return this.keyBindingButton.mouseReleased(d, e, i) || this.resetButton.mouseReleased(d, e, i);
		}
	}

	public abstract static class class_4161 extends EntryListWidget.Entry<ControlsListWidget.class_4161> {
	}
}
