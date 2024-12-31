package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ChatSerializer {
	public static Text method_20189(Text text, Style style) {
		if (style.isEmpty()) {
			return text;
		} else {
			return text.getStyle().isEmpty() ? text.setStyle(style.deepCopy()) : new LiteralText("").append(text).setStyle(style.deepCopy());
		}
	}

	public static Text method_20185(@Nullable class_3915 arg, Text text, @Nullable Entity entity) throws CommandSyntaxException {
		Text text2;
		if (text instanceof ScoreText && arg != null) {
			ScoreText scoreText = (ScoreText)text;
			String string;
			if (scoreText.method_20195() != null) {
				List<? extends Entity> list = scoreText.method_20195().method_19735(arg);
				if (list.isEmpty()) {
					string = scoreText.getName();
				} else {
					if (list.size() != 1) {
						throw class_4062.field_19704.create();
					}

					string = ((Entity)list.get(0)).method_15586();
				}
			} else {
				string = scoreText.getName();
			}

			String string5 = entity != null && string.equals("*") ? entity.method_15586() : string;
			text2 = new ScoreText(string5, scoreText.getObjective());
			((ScoreText)text2).setScore(scoreText.computeValue());
			((ScoreText)text2).method_12607(arg);
		} else if (text instanceof SelectorText && arg != null) {
			text2 = ((SelectorText)text).method_20196(arg);
		} else if (text instanceof LiteralText) {
			text2 = new LiteralText(((LiteralText)text).getRawString());
		} else if (text instanceof KeyBindComponent) {
			text2 = new KeyBindComponent(((KeyBindComponent)text).getKeybind());
		} else {
			if (!(text instanceof TranslatableText)) {
				return text;
			}

			Object[] objects = ((TranslatableText)text).getArgs();

			for (int i = 0; i < objects.length; i++) {
				Object object = objects[i];
				if (object instanceof Text) {
					objects[i] = method_20185(arg, (Text)object, entity);
				}
			}

			text2 = new TranslatableText(((TranslatableText)text).getKey(), objects);
		}

		for (Text text8 : text.getSiblings()) {
			text2.append(method_20185(arg, text8, entity));
		}

		return method_20189(text2, text.getStyle());
	}

	public static Text method_20186(GameProfile gameProfile) {
		if (gameProfile.getName() != null) {
			return new LiteralText(gameProfile.getName());
		} else {
			return gameProfile.getId() != null ? new LiteralText(gameProfile.getId().toString()) : new LiteralText("(unknown)");
		}
	}

	public static Text method_20191(Collection<String> collection) {
		return method_20192(collection, string -> new LiteralText(string).formatted(Formatting.GREEN));
	}

	public static <T extends Comparable<T>> Text method_20192(Collection<T> collection, Function<T, Text> function) {
		if (collection.isEmpty()) {
			return new LiteralText("");
		} else if (collection.size() == 1) {
			return (Text)function.apply(collection.iterator().next());
		} else {
			List<T> list = Lists.newArrayList(collection);
			list.sort(Comparable::compareTo);
			return method_20193(collection, function);
		}
	}

	public static <T> Text method_20193(Collection<T> collection, Function<T, Text> function) {
		if (collection.isEmpty()) {
			return new LiteralText("");
		} else if (collection.size() == 1) {
			return (Text)function.apply(collection.iterator().next());
		} else {
			Text text = new LiteralText("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					text.append(new LiteralText(", ").formatted(Formatting.GRAY));
				}

				text.append((Text)function.apply(object));
				bl = false;
			}

			return text;
		}
	}

	public static Text method_20188(Text text) {
		return new LiteralText("[").append(text).append("]");
	}

	public static Text method_20187(Message message) {
		return (Text)(message instanceof Text ? (Text)message : new LiteralText(message.getString()));
	}
}
