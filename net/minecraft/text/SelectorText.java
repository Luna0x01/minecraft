package net.minecraft.text;

public class SelectorText extends BaseText {
	private final String pattern;

	public SelectorText(String string) {
		this.pattern = string;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public String computeValue() {
		return this.pattern;
	}

	public SelectorText copy() {
		SelectorText selectorText = new SelectorText(this.pattern);
		selectorText.setStyle(this.getStyle().deepCopy());

		for (Text text : this.getSiblings()) {
			selectorText.append(text.copy());
		}

		return selectorText;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof SelectorText)) {
			return false;
		} else {
			SelectorText selectorText = (SelectorText)object;
			return this.pattern.equals(selectorText.pattern) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
