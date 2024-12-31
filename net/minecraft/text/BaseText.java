package net.minecraft.text;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;

public abstract class BaseText implements Text {
	protected List<Text> siblings = Lists.newArrayList();
	private Style style;

	@Override
	public Text append(Text text) {
		text.getStyle().setParent(this.getStyle());
		this.siblings.add(text);
		return this;
	}

	@Override
	public List<Text> getSiblings() {
		return this.siblings;
	}

	@Override
	public Text append(String text) {
		return this.append(new LiteralText(text));
	}

	@Override
	public Text setStyle(Style style) {
		this.style = style;

		for (Text text : this.siblings) {
			text.getStyle().setParent(this.getStyle());
		}

		return this;
	}

	@Override
	public Style getStyle() {
		if (this.style == null) {
			this.style = new Style();

			for (Text text : this.siblings) {
				text.getStyle().setParent(this.style);
			}
		}

		return this.style;
	}

	public Iterator<Text> iterator() {
		return Iterators.concat(Iterators.forArray(new BaseText[]{this}), method_7458(this.siblings));
	}

	@Override
	public final String asUnformattedString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Text text : this) {
			stringBuilder.append(text.computeValue());
		}

		return stringBuilder.toString();
	}

	@Override
	public final String asFormattedString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Text text : this) {
			String string = text.computeValue();
			if (!string.isEmpty()) {
				stringBuilder.append(text.getStyle().asString());
				stringBuilder.append(string);
				stringBuilder.append(Formatting.RESET);
			}
		}

		return stringBuilder.toString();
	}

	public static Iterator<Text> method_7458(Iterable<Text> iterable) {
		Iterator<Text> iterator = Iterators.concat(Iterators.transform(iterable.iterator(), new Function<Text, Iterator<Text>>() {
			public Iterator<Text> apply(@Nullable Text text) {
				return text.iterator();
			}
		}));
		return Iterators.transform(iterator, new Function<Text, Text>() {
			public Text apply(@Nullable Text text) {
				Text text2 = text.copy();
				text2.setStyle(text2.getStyle().copy());
				return text2;
			}
		});
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof BaseText)) {
			return false;
		} else {
			BaseText baseText = (BaseText)object;
			return this.siblings.equals(baseText.siblings) && this.getStyle().equals(baseText.getStyle());
		}
	}

	public int hashCode() {
		return 31 * this.style.hashCode() + this.siblings.hashCode();
	}

	public String toString() {
		return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
	}
}
