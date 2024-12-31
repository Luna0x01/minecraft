package net.minecraft.client.gui.widget;

import net.minecraft.client.option.GameOptions;

public class OptionButtonWidget extends ButtonWidget {
	private final GameOptions.Option option;

	public OptionButtonWidget(int i, int j, int k, String string) {
		this(i, j, k, null, string);
	}

	public OptionButtonWidget(int i, int j, int k, GameOptions.Option option, String string) {
		super(i, j, k, 150, 20, string);
		this.option = option;
	}

	public GameOptions.Option getOption() {
		return this.option;
	}
}
