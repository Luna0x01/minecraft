package net.minecraft.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

public class TranslatableText extends BaseText implements ParsableText {
	private static final Language EMPTY_LANGUAGE = new Language();
	private static final Language LANGUAGE = Language.getInstance();
	private final String key;
	private final Object[] args;
	private final Object lock = new Object();
	private long languageReloadTimestamp = -1L;
	protected final List<Text> translations = Lists.newArrayList();
	public static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public TranslatableText(String string, Object... objects) {
		this.key = string;
		this.args = objects;

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof Text) {
				Text text = ((Text)object).deepCopy();
				this.args[i] = text;
				text.getStyle().setParent(this.getStyle());
			} else if (object == null) {
				this.args[i] = "null";
			}
		}
	}

	@VisibleForTesting
	synchronized void updateTranslations() {
		synchronized (this.lock) {
			long l = LANGUAGE.getTimeLoaded();
			if (l == this.languageReloadTimestamp) {
				return;
			}

			this.languageReloadTimestamp = l;
			this.translations.clear();
		}

		try {
			this.setTranslation(LANGUAGE.translate(this.key));
		} catch (TranslationException var6) {
			this.translations.clear();

			try {
				this.setTranslation(EMPTY_LANGUAGE.translate(this.key));
			} catch (TranslationException var5) {
				throw var6;
			}
		}
	}

	protected void setTranslation(String string) {
		Matcher matcher = ARG_FORMAT.matcher(string);

		try {
			int i = 0;
			int j = 0;

			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					Text text = new LiteralText(String.format(string.substring(j, k)));
					text.getStyle().setParent(this.getStyle());
					this.translations.add(text);
				}

				String string2 = matcher.group(2);
				String string3 = string.substring(k, l);
				if ("%".equals(string2) && "%%".equals(string3)) {
					Text text2 = new LiteralText("%");
					text2.getStyle().setParent(this.getStyle());
					this.translations.add(text2);
				} else {
					if (!"s".equals(string2)) {
						throw new TranslationException(this, "Unsupported format: '" + string3 + "'");
					}

					String string4 = matcher.group(1);
					int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;
					if (m < this.args.length) {
						this.translations.add(this.getArg(m));
					}
				}

				j = l;
			}

			if (j < string.length()) {
				Text text3 = new LiteralText(String.format(string.substring(j)));
				text3.getStyle().setParent(this.getStyle());
				this.translations.add(text3);
			}
		} catch (IllegalFormatException var11) {
			throw new TranslationException(this, var11);
		}
	}

	private Text getArg(int i) {
		if (i >= this.args.length) {
			throw new TranslationException(this, i);
		} else {
			Object object = this.args[i];
			Text text;
			if (object instanceof Text) {
				text = (Text)object;
			} else {
				text = new LiteralText(object == null ? "null" : object.toString());
				text.getStyle().setParent(this.getStyle());
			}

			return text;
		}
	}

	@Override
	public Text setStyle(Style style) {
		super.setStyle(style);

		for (Object object : this.args) {
			if (object instanceof Text) {
				((Text)object).getStyle().setParent(this.getStyle());
			}
		}

		if (this.languageReloadTimestamp > -1L) {
			for (Text text : this.translations) {
				text.getStyle().setParent(style);
			}
		}

		return this;
	}

	@Override
	public Stream<Text> stream() {
		this.updateTranslations();
		return Streams.concat(new Stream[]{this.translations.stream(), this.siblings.stream()}).flatMap(Text::stream);
	}

	@Override
	public String asString() {
		this.updateTranslations();
		StringBuilder stringBuilder = new StringBuilder();

		for (Text text : this.translations) {
			stringBuilder.append(text.asString());
		}

		return stringBuilder.toString();
	}

	public TranslatableText method_11020() {
		Object[] objects = new Object[this.args.length];

		for (int i = 0; i < this.args.length; i++) {
			if (this.args[i] instanceof Text) {
				objects[i] = ((Text)this.args[i]).deepCopy();
			} else {
				objects[i] = this.args[i];
			}
		}

		return new TranslatableText(this.key, objects);
	}

	@Override
	public Text parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		Object[] objects = new Object[this.args.length];

		for (int j = 0; j < objects.length; j++) {
			Object object = this.args[j];
			if (object instanceof Text) {
				objects[j] = Texts.parse(serverCommandSource, (Text)object, entity, i);
			} else {
				objects[j] = object;
			}
		}

		return new TranslatableText(this.key, objects);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof TranslatableText)) {
			return false;
		} else {
			TranslatableText translatableText = (TranslatableText)object;
			return Arrays.equals(this.args, translatableText.args) && this.key.equals(translatableText.key) && super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		int i = super.hashCode();
		i = 31 * i + this.key.hashCode();
		return 31 * i + Arrays.hashCode(this.args);
	}

	@Override
	public String toString() {
		return "TranslatableComponent{key='"
			+ this.key
			+ '\''
			+ ", args="
			+ Arrays.toString(this.args)
			+ ", siblings="
			+ this.siblings
			+ ", style="
			+ this.getStyle()
			+ '}';
	}

	public String getKey() {
		return this.key;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
