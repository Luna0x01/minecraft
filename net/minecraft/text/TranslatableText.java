package net.minecraft.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.CommonI18n;

public class TranslatableText extends BaseText {
	private final String key;
	private final Object[] args;
	private final Object lock = new Object();
	private long languageReloadTimestamp = -1L;
	@VisibleForTesting
	List<Text> translations = Lists.newArrayList();
	public static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public TranslatableText(String string, Object... objects) {
		this.key = string;
		this.args = objects;

		for (Object object : objects) {
			if (object instanceof Text) {
				((Text)object).getStyle().setParent(this.getStyle());
			}
		}
	}

	@VisibleForTesting
	synchronized void updateTranslations() {
		synchronized (this.lock) {
			long l = CommonI18n.getTimeLoaded();
			if (l == this.languageReloadTimestamp) {
				return;
			}

			this.languageReloadTimestamp = l;
			this.translations.clear();
		}

		try {
			this.setTranslation(CommonI18n.translate(this.key));
		} catch (TranslationException var6) {
			this.translations.clear();

			try {
				this.setTranslation(CommonI18n.thisIsNotUsedAnyWhereAndThisMethodDoesNotWorkSoPleaseDoNotUseThis(this.key));
			} catch (TranslationException var5) {
				throw var6;
			}
		}
	}

	protected void setTranslation(String translation) {
		boolean bl = false;
		Matcher matcher = ARG_FORMAT.matcher(translation);
		int i = 0;
		int j = 0;

		try {
			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					LiteralText literalText = new LiteralText(String.format(translation.substring(j, k)));
					literalText.getStyle().setParent(this.getStyle());
					this.translations.add(literalText);
				}

				String string = matcher.group(2);
				String string2 = translation.substring(k, l);
				if ("%".equals(string) && "%%".equals(string2)) {
					LiteralText literalText2 = new LiteralText("%");
					literalText2.getStyle().setParent(this.getStyle());
					this.translations.add(literalText2);
				} else {
					if (!"s".equals(string)) {
						throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
					}

					String string3 = matcher.group(1);
					int m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
					if (m < this.args.length) {
						this.translations.add(this.getArg(m));
					}
				}

				j = l;
			}

			if (j < translation.length()) {
				LiteralText literalText3 = new LiteralText(String.format(translation.substring(j)));
				literalText3.getStyle().setParent(this.getStyle());
				this.translations.add(literalText3);
			}
		} catch (IllegalFormatException var12) {
			throw new TranslationException(this, var12);
		}
	}

	private Text getArg(int index) {
		if (index >= this.args.length) {
			throw new TranslationException(this, index);
		} else {
			Object object = this.args[index];
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
	public Iterator<Text> iterator() {
		this.updateTranslations();
		return Iterators.concat(method_7458(this.translations), method_7458(this.siblings));
	}

	@Override
	public String computeValue() {
		this.updateTranslations();
		StringBuilder stringBuilder = new StringBuilder();

		for (Text text : this.translations) {
			stringBuilder.append(text.computeValue());
		}

		return stringBuilder.toString();
	}

	public TranslatableText copy() {
		Object[] objects = new Object[this.args.length];

		for (int i = 0; i < this.args.length; i++) {
			if (this.args[i] instanceof Text) {
				objects[i] = ((Text)this.args[i]).copy();
			} else {
				objects[i] = this.args[i];
			}
		}

		TranslatableText translatableText = new TranslatableText(this.key, objects);
		translatableText.setStyle(this.getStyle().deepCopy());

		for (Text text : this.getSiblings()) {
			translatableText.append(text.copy());
		}

		return translatableText;
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
