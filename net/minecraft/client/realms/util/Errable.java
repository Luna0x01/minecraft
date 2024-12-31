package net.minecraft.client.realms.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public interface Errable {
	void error(Text errorMessage);

	default void error(String errorMessage) {
		this.error(new LiteralText(errorMessage));
	}
}
