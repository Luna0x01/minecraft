package net.minecraft.client.realms.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public interface Errable {
	void error(Text text);

	default void error(String string) {
		this.error(new LiteralText(string));
	}
}
