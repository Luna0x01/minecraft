package net.minecraft.client.gui.widget;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ButtonWidget extends AbstractPressableButtonWidget {
	public static final ButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {
	};
	protected final ButtonWidget.PressAction onPress;
	protected final ButtonWidget.TooltipSupplier tooltipSupplier;

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		this(x, y, width, height, message, onPress, EMPTY);
	}

	public ButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier) {
		super(x, y, width, height, message);
		this.onPress = onPress;
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.renderButton(matrices, mouseX, mouseY, delta);
		if (this.isHovered()) {
			this.renderToolTip(matrices, mouseX, mouseY);
		}
	}

	@Override
	public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
		this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
	}

	public interface PressAction {
		void onPress(ButtonWidget button);
	}

	public interface TooltipSupplier {
		void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY);
	}
}
