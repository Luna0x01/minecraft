package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class OptionPairWidget extends EntryListWidget<OptionPairWidget.Pair> {
	public OptionPairWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOptions.Option... options) {
		super(minecraftClient, i, j, k, l, m);
		this.centerListVertically = false;
		this.method_18398(new OptionPairWidget.Pair(i, GameOptions.Option.FULLSCREEN_RESOLUTION));

		for (int n = 0; n < options.length; n += 2) {
			GameOptions.Option option = options[n];
			GameOptions.Option option2 = n < options.length - 1 ? options[n + 1] : null;
			this.method_18398(new OptionPairWidget.Pair(i, option, option2));
		}
	}

	@Nullable
	private static ButtonWidget method_18410(MinecraftClient minecraftClient, int i, int j, int k, @Nullable GameOptions.Option option) {
		if (option == null) {
			return null;
		} else {
			int l = option.getOrdinal();
			return (ButtonWidget)(option.isNumeric()
				? new OptionSliderWidget(l, i, j, k, 20, option, 0.0, 1.0)
				: new OptionButtonWidget(l, i, j, k, 20, option, minecraftClient.options.method_18260(option)) {
					@Override
					public void method_18374(double d, double e) {
						minecraftClient.options.method_18258(option, 1);
						this.message = minecraftClient.options.method_18260(GameOptions.Option.byOrdinal(this.id));
					}
				});
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	public final class Pair extends EntryListWidget.Entry<OptionPairWidget.Pair> {
		@Nullable
		private final ButtonWidget field_20081;
		@Nullable
		private final ButtonWidget field_20082;

		public Pair(ButtonWidget buttonWidget, @Nullable ButtonWidget buttonWidget2) {
			this.field_20081 = buttonWidget;
			this.field_20082 = buttonWidget2;
		}

		public Pair(int i, GameOptions.Option option) {
			this(OptionPairWidget.method_18410(OptionPairWidget.this.client, i / 2 - 155, 0, 310, option), null);
		}

		public Pair(int i, GameOptions.Option option, GameOptions.Option option2) {
			this(
				OptionPairWidget.method_18410(OptionPairWidget.this.client, i / 2 - 155, 0, 150, option),
				OptionPairWidget.method_18410(OptionPairWidget.this.client, i / 2 - 155 + 160, 0, 150, option2)
			);
		}

		@Override
		public void method_6700(int i, int j, int k, int l, boolean bl, float f) {
			if (this.field_20081 != null) {
				this.field_20081.y = this.method_18403();
				this.field_20081.method_891(k, l, f);
			}

			if (this.field_20082 != null) {
				this.field_20082.y = this.method_18403();
				this.field_20082.method_891(k, l, f);
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.field_20081.mouseClicked(d, e, i) ? true : this.field_20082 != null && this.field_20082.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			boolean bl = this.field_20081 != null && this.field_20081.mouseReleased(d, e, i);
			boolean bl2 = this.field_20082 != null && this.field_20082.mouseReleased(d, e, i);
			return bl || bl2;
		}

		@Override
		public void method_18401(float f) {
		}
	}
}
