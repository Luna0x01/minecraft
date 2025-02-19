package net.minecraft.client.gui.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

public class OrderedTextTooltipComponent implements TooltipComponent {
	private final OrderedText text;

	public OrderedTextTooltipComponent(OrderedText text) {
		this.text = text;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return textRenderer.getWidth(this.text);
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
		textRenderer.draw(this.text, (float)x, (float)y, -1, true, matrix4f, immediate, false, 0, 15728880);
	}
}
