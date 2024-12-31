package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class class_4102 implements ArgumentType<class_4102.class_4103> {
	private static final Collection<String> field_19867 = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

	public static class_4102 method_18091() {
		return new class_4102();
	}

	public static Text method_18093(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return ((class_4102.class_4103)commandContext.getArgument(string, class_4102.class_4103.class))
			.method_18094((class_3915)commandContext.getSource(), ((class_3915)commandContext.getSource()).method_17575(2));
	}

	public class_4102.class_4103 parse(StringReader stringReader) throws CommandSyntaxException {
		return class_4102.class_4103.method_18095(stringReader, true);
	}

	public Collection<String> getExamples() {
		return field_19867;
	}

	public static class class_2306 {
		private final int field_10393;
		private final int field_19870;
		private final class_4317 field_19871;

		public class_2306(int i, int j, class_4317 arg) {
			this.field_10393 = i;
			this.field_19870 = j;
			this.field_19871 = arg;
		}

		public int method_9512() {
			return this.field_10393;
		}

		public int method_9513() {
			return this.field_19870;
		}

		@Nullable
		public Text method_18096(class_3915 arg) throws CommandSyntaxException {
			return class_4317.method_19732(this.field_19871.method_19735(arg));
		}
	}

	public static class class_4103 {
		private final String field_19868;
		private final class_4102.class_2306[] field_19869;

		public class_4103(String string, class_4102.class_2306[] args) {
			this.field_19868 = string;
			this.field_19869 = args;
		}

		public Text method_18094(class_3915 arg, boolean bl) throws CommandSyntaxException {
			if (this.field_19869.length != 0 && bl) {
				Text text = new LiteralText(this.field_19868.substring(0, this.field_19869[0].method_9512()));
				int i = this.field_19869[0].method_9512();

				for (class_4102.class_2306 lv : this.field_19869) {
					Text text2 = lv.method_18096(arg);
					if (i < lv.method_9512()) {
						text.append(this.field_19868.substring(i, lv.method_9512()));
					}

					if (text2 != null) {
						text.append(text2);
					}

					i = lv.method_9513();
				}

				if (i < this.field_19868.length()) {
					text.append(this.field_19868.substring(i, this.field_19868.length()));
				}

				return text;
			} else {
				return new LiteralText(this.field_19868);
			}
		}

		public static class_4102.class_4103 method_18095(StringReader stringReader, boolean bl) throws CommandSyntaxException {
			String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
			if (!bl) {
				stringReader.setCursor(stringReader.getTotalLength());
				return new class_4102.class_4103(string, new class_4102.class_2306[0]);
			} else {
				List<class_4102.class_2306> list = Lists.newArrayList();
				int i = stringReader.getCursor();

				while (true) {
					int j;
					class_4317 lv2;
					while (true) {
						if (!stringReader.canRead()) {
							return new class_4102.class_4103(string, (class_4102.class_2306[])list.toArray(new class_4102.class_2306[list.size()]));
						}

						if (stringReader.peek() == '@') {
							j = stringReader.getCursor();

							try {
								class_4318 lv = new class_4318(stringReader);
								lv2 = lv.method_19818();
								break;
							} catch (CommandSyntaxException var8) {
								if (var8.getType() != class_4318.field_21211 && var8.getType() != class_4318.field_21209) {
									throw var8;
								}

								stringReader.setCursor(j + 1);
							}
						} else {
							stringReader.skip();
						}
					}

					list.add(new class_4102.class_2306(j - i, stringReader.getCursor() - i, lv2));
				}
			}
		}
	}
}
