package net.minecraft.text;

import java.util.function.UnaryOperator;
import net.minecraft.util.Formatting;

public interface MutableText extends Text {
	MutableText setStyle(Style style);

	default MutableText append(String text) {
		return this.append(new LiteralText(text));
	}

	MutableText append(Text text);

	default MutableText styled(UnaryOperator<Style> styleUpdater) {
		this.setStyle((Style)styleUpdater.apply(this.getStyle()));
		return this;
	}

	default MutableText fillStyle(Style styleOverride) {
		this.setStyle(styleOverride.withParent(this.getStyle()));
		return this;
	}

	default MutableText formatted(Formatting... formattings) {
		this.setStyle(this.getStyle().withFormatting(formattings));
		return this;
	}

	default MutableText formatted(Formatting formatting) {
		this.setStyle(this.getStyle().withFormatting(formatting));
		return this;
	}
}
