package net.minecraft.client.gui.widget;

public class ButtonWidget extends AbstractPressableButtonWidget {
	protected final ButtonWidget.PressAction onPress;

	public ButtonWidget(int i, int j, int k, int l, String string, ButtonWidget.PressAction pressAction) {
		super(i, j, k, l, string);
		this.onPress = pressAction;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	public interface PressAction {
		void onPress(ButtonWidget buttonWidget);
	}
}
