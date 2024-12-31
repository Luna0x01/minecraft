package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Texts {
	public static String getRenderChatMessage(String string, boolean forceColor) {
		return !forceColor && !MinecraftClient.getInstance().options.chatColor ? Formatting.strip(string) : string;
	}

	public static List<Text> wrapLines(Text text, int width, TextRenderer textRenderer, boolean bl, boolean forceColor) {
		int i = 0;
		Text text2 = new LiteralText("");
		List<Text> list = Lists.newArrayList();
		List<Text> list2 = Lists.newArrayList(text);

		for (int j = 0; j < list2.size(); j++) {
			Text text3 = (Text)list2.get(j);
			String string = text3.computeValue();
			boolean bl2 = false;
			if (string.contains("\n")) {
				int k = string.indexOf(10);
				String string2 = string.substring(k + 1);
				string = string.substring(0, k + 1);
				LiteralText literalText = new LiteralText(string2);
				literalText.setStyle(text3.getStyle().deepCopy());
				list2.add(j + 1, literalText);
				bl2 = true;
			}

			String string3 = getRenderChatMessage(text3.getStyle().asString() + string, forceColor);
			String string4 = string3.endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
			int l = textRenderer.getStringWidth(string4);
			LiteralText literalText2 = new LiteralText(string4);
			literalText2.setStyle(text3.getStyle().deepCopy());
			if (i + l > width) {
				String string5 = textRenderer.trimToWidth(string3, width - i, false);
				String string6 = string5.length() < string3.length() ? string3.substring(string5.length()) : null;
				if (string6 != null && !string6.isEmpty()) {
					int m = string5.lastIndexOf(" ");
					if (m >= 0 && textRenderer.getStringWidth(string3.substring(0, m)) > 0) {
						string5 = string3.substring(0, m);
						if (bl) {
							m++;
						}

						string6 = string3.substring(m);
					} else if (i > 0 && !string3.contains(" ")) {
						string5 = "";
						string6 = string3;
					}

					LiteralText literalText3 = new LiteralText(string6);
					literalText3.setStyle(text3.getStyle().deepCopy());
					list2.add(j + 1, literalText3);
				}

				l = textRenderer.getStringWidth(string5);
				literalText2 = new LiteralText(string5);
				literalText2.setStyle(text3.getStyle().deepCopy());
				bl2 = true;
			}

			if (i + l <= width) {
				i += l;
				text2.append(literalText2);
			} else {
				bl2 = true;
			}

			if (bl2) {
				list.add(text2);
				i = 0;
				text2 = new LiteralText("");
			}
		}

		list.add(text2);
		return list;
	}
}
