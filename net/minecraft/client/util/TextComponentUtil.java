package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextComponentUtil {
	public static String getRenderChatMessage(String string, boolean bl) {
		return !bl && !MinecraftClient.getInstance().options.chatColors ? Formatting.strip(string) : string;
	}

	public static List<Text> wrapLines(Text text, int i, TextRenderer textRenderer, boolean bl, boolean bl2) {
		int j = 0;
		Text text2 = new LiteralText("");
		List<Text> list = Lists.newArrayList();
		List<Text> list2 = Lists.newArrayList(text);

		for (int k = 0; k < list2.size(); k++) {
			Text text3 = (Text)list2.get(k);
			String string = text3.asString();
			boolean bl3 = false;
			if (string.contains("\n")) {
				int l = string.indexOf(10);
				String string2 = string.substring(l + 1);
				string = string.substring(0, l + 1);
				Text text4 = new LiteralText(string2).setStyle(text3.getStyle().deepCopy());
				list2.add(k + 1, text4);
				bl3 = true;
			}

			String string3 = getRenderChatMessage(text3.getStyle().asString() + string, bl2);
			String string4 = string3.endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
			int m = textRenderer.getStringWidth(string4);
			Text text5 = new LiteralText(string4).setStyle(text3.getStyle().deepCopy());
			if (j + m > i) {
				String string5 = textRenderer.trimToWidth(string3, i - j, false);
				String string6 = string5.length() < string3.length() ? string3.substring(string5.length()) : null;
				if (string6 != null && !string6.isEmpty()) {
					int n = string6.charAt(0) != ' ' ? string5.lastIndexOf(32) : string5.length();
					if (n >= 0 && textRenderer.getStringWidth(string3.substring(0, n)) > 0) {
						string5 = string3.substring(0, n);
						if (bl) {
							n++;
						}

						string6 = string3.substring(n);
					} else if (j > 0 && !string3.contains(" ")) {
						string5 = "";
						string6 = string3;
					}

					Text text6 = new LiteralText(string6).setStyle(text3.getStyle().deepCopy());
					list2.add(k + 1, text6);
				}

				m = textRenderer.getStringWidth(string5);
				text5 = new LiteralText(string5);
				text5.setStyle(text3.getStyle().deepCopy());
				bl3 = true;
			}

			if (j + m <= i) {
				j += m;
				text2.append(text5);
			} else {
				bl3 = true;
			}

			if (bl3) {
				list.add(text2);
				j = 0;
				text2 = new LiteralText("");
			}
		}

		list.add(text2);
		return list;
	}
}
