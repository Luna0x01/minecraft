package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class OptionPairWidget extends EntryListWidget {
	private final List<OptionPairWidget.Pair> entries = Lists.newArrayList();

	public OptionPairWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOptions.Option... options) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;

		for (int n = 0; n < options.length; n += 2) {
			GameOptions.Option option = options[n];
			GameOptions.Option option2 = n < options.length - 1 ? options[n + 1] : null;
			ButtonWidget buttonWidget = this.createWidget(minecraftClient, i / 2 - 155, 0, option);
			ButtonWidget buttonWidget2 = this.createWidget(minecraftClient, i / 2 - 155 + 160, 0, option2);
			this.entries.add(new OptionPairWidget.Pair(buttonWidget, buttonWidget2));
		}
	}

	private ButtonWidget createWidget(MinecraftClient client, int x, int y, GameOptions.Option option) {
		if (option == null) {
			return null;
		} else {
			int i = option.getOrdinal();
			return (ButtonWidget)(option.isNumeric()
				? new OptionSliderWidget(i, x, y, option)
				: new OptionButtonWidget(i, x, y, option, client.options.getValueMessage(option)));
		}
	}

	public OptionPairWidget.Pair getEntry(int i) {
		return (OptionPairWidget.Pair)this.entries.get(i);
	}

	@Override
	protected int getEntryCount() {
		return this.entries.size();
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	public static class Pair implements EntryListWidget.Entry {
		private final MinecraftClient client = MinecraftClient.getInstance();
		private final ButtonWidget left;
		private final ButtonWidget right;

		public Pair(ButtonWidget buttonWidget, ButtonWidget buttonWidget2) {
			this.left = buttonWidget;
			this.right = buttonWidget2;
		}

		@Override
		public void method_6700(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			if (this.left != null) {
				this.left.y = k;
				this.left.method_891(this.client, n, o, f);
			}

			if (this.right != null) {
				this.right.y = k;
				this.right.method_891(this.client, n, o, f);
			}
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			if (this.left.isMouseOver(this.client, mouseX, mouseY)) {
				if (this.left instanceof OptionButtonWidget) {
					this.client.options.getBooleanValue(((OptionButtonWidget)this.left).getOption(), 1);
					this.left.message = this.client.options.getValueMessage(GameOptions.Option.byOrdinal(this.left.id));
				}

				return true;
			} else if (this.right != null && this.right.isMouseOver(this.client, mouseX, mouseY)) {
				if (this.right instanceof OptionButtonWidget) {
					this.client.options.getBooleanValue(((OptionButtonWidget)this.right).getOption(), 1);
					this.right.message = this.client.options.getValueMessage(GameOptions.Option.byOrdinal(this.right.id));
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
			if (this.left != null) {
				this.left.mouseReleased(mouseX, mouseY);
			}

			if (this.right != null) {
				this.right.mouseReleased(mouseX, mouseY);
			}
		}

		@Override
		public void method_9473(int i, int j, int k, float f) {
		}
	}
}
