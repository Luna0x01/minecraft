package net.minecraft.client.font;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class TextHandler {
	final TextHandler.WidthRetriever widthRetriever;

	public TextHandler(TextHandler.WidthRetriever widthRetriever) {
		this.widthRetriever = widthRetriever;
	}

	public float getWidth(@Nullable String text) {
		if (text == null) {
			return 0.0F;
		} else {
			MutableFloat mutableFloat = new MutableFloat();
			TextVisitFactory.visitFormatted(text, Style.EMPTY, (unused, style, codePoint) -> {
				mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
				return true;
			});
			return mutableFloat.floatValue();
		}
	}

	public float getWidth(StringVisitable text) {
		MutableFloat mutableFloat = new MutableFloat();
		TextVisitFactory.visitFormatted(text, Style.EMPTY, (unused, style, codePoint) -> {
			mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
			return true;
		});
		return mutableFloat.floatValue();
	}

	public float getWidth(OrderedText text) {
		MutableFloat mutableFloat = new MutableFloat();
		text.accept((index, style, codePoint) -> {
			mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
			return true;
		});
		return mutableFloat.floatValue();
	}

	public int getTrimmedLength(String text, int maxWidth, Style style) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)maxWidth);
		TextVisitFactory.visitForwards(text, style, widthLimitingVisitor);
		return widthLimitingVisitor.getLength();
	}

	public String trimToWidth(String text, int maxWidth, Style style) {
		return text.substring(0, this.getTrimmedLength(text, maxWidth, style));
	}

	public String trimToWidthBackwards(String text, int maxWidth, Style style) {
		MutableFloat mutableFloat = new MutableFloat();
		MutableInt mutableInt = new MutableInt(text.length());
		TextVisitFactory.visitBackwards(text, style, (index, stylex, codePoint) -> {
			float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(codePoint, stylex));
			if (f > (float)maxWidth) {
				return false;
			} else {
				mutableInt.setValue(index);
				return true;
			}
		});
		return text.substring(mutableInt.intValue());
	}

	public int getLimitedStringLength(String text, int maxWidth, Style style) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)maxWidth);
		TextVisitFactory.visitFormatted(text, style, widthLimitingVisitor);
		return widthLimitingVisitor.getLength();
	}

	@Nullable
	public Style getStyleAt(StringVisitable text, int x) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)x);
		return (Style)text.visit(
				(style, textx) -> TextVisitFactory.visitFormatted(textx, style, widthLimitingVisitor) ? Optional.empty() : Optional.of(style), Style.EMPTY
			)
			.orElse(null);
	}

	@Nullable
	public Style getStyleAt(OrderedText text, int x) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)x);
		MutableObject<Style> mutableObject = new MutableObject();
		text.accept((index, style, codePoint) -> {
			if (!widthLimitingVisitor.accept(index, style, codePoint)) {
				mutableObject.setValue(style);
				return false;
			} else {
				return true;
			}
		});
		return (Style)mutableObject.getValue();
	}

	public String limitString(String text, int maxWidth, Style style) {
		return text.substring(0, this.getLimitedStringLength(text, maxWidth, style));
	}

	public StringVisitable trimToWidth(StringVisitable text, int width, Style style) {
		final TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)width);
		return (StringVisitable)text.visit(new StringVisitable.StyledVisitor<StringVisitable>() {
			private final TextCollector collector = new TextCollector();

			@Override
			public Optional<StringVisitable> accept(Style style, String string) {
				widthLimitingVisitor.resetLength();
				if (!TextVisitFactory.visitFormatted(string, style, widthLimitingVisitor)) {
					String string2 = string.substring(0, widthLimitingVisitor.getLength());
					if (!string2.isEmpty()) {
						this.collector.add(StringVisitable.styled(string2, style));
					}

					return Optional.of(this.collector.getCombined());
				} else {
					if (!string.isEmpty()) {
						this.collector.add(StringVisitable.styled(string, style));
					}

					return Optional.empty();
				}
			}
		}, style).orElse(text);
	}

	public int method_35717(String text, int maxWidth, Style style) {
		TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)maxWidth);
		TextVisitFactory.visitFormatted(text, style, lineBreakingVisitor);
		return lineBreakingVisitor.getEndingIndex();
	}

	public static int moveCursorByWords(String text, int offset, int cursor, boolean consumeSpaceOrBreak) {
		int i = cursor;
		boolean bl = offset < 0;
		int j = Math.abs(offset);

		for (int k = 0; k < j; k++) {
			if (bl) {
				while (consumeSpaceOrBreak && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
					i--;
				}

				while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
					i--;
				}
			} else {
				int l = text.length();
				int m = text.indexOf(32, i);
				int n = text.indexOf(10, i);
				if (m == -1 && n == -1) {
					i = -1;
				} else if (m != -1 && n != -1) {
					i = Math.min(m, n);
				} else if (m != -1) {
					i = m;
				} else {
					i = n;
				}

				if (i == -1) {
					i = l;
				} else {
					while (consumeSpaceOrBreak && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
						i++;
					}
				}
			}
		}

		return i;
	}

	public void wrapLines(String text, int maxWidth, Style style, boolean retainTrailingWordSplit, TextHandler.LineWrappingConsumer consumer) {
		int i = 0;
		int j = text.length();
		Style style2 = style;

		while (i < j) {
			TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)maxWidth);
			boolean bl = TextVisitFactory.visitFormatted(text, i, style2, style, lineBreakingVisitor);
			if (bl) {
				consumer.accept(style2, i, j);
				break;
			}

			int k = lineBreakingVisitor.getEndingIndex();
			char c = text.charAt(k);
			int l = c != '\n' && c != ' ' ? k : k + 1;
			consumer.accept(style2, i, retainTrailingWordSplit ? l : k);
			i = l;
			style2 = lineBreakingVisitor.getEndingStyle();
		}
	}

	public List<StringVisitable> wrapLines(String text, int maxWidth, Style style) {
		List<StringVisitable> list = Lists.newArrayList();
		this.wrapLines(text, maxWidth, style, false, (stylex, start, end) -> list.add(StringVisitable.styled(text.substring(start, end), stylex)));
		return list;
	}

	public List<StringVisitable> wrapLines(StringVisitable text, int maxWidth, Style style) {
		List<StringVisitable> list = Lists.newArrayList();
		this.wrapLines(text, maxWidth, style, (textx, boolean_) -> list.add(textx));
		return list;
	}

	public List<StringVisitable> method_35714(StringVisitable stringVisitable, int maxWidth, Style style, StringVisitable stringVisitable2) {
		List<StringVisitable> list = Lists.newArrayList();
		this.wrapLines(
			stringVisitable,
			maxWidth,
			style,
			(stringVisitable2x, boolean_) -> list.add(boolean_ ? StringVisitable.concat(stringVisitable2, stringVisitable2x) : stringVisitable2x)
		);
		return list;
	}

	public void wrapLines(StringVisitable text, int maxWidth, Style style, BiConsumer<StringVisitable, Boolean> biConsumer) {
		List<TextHandler.StyledString> list = Lists.newArrayList();
		text.visit((stylex, textx) -> {
			if (!textx.isEmpty()) {
				list.add(new TextHandler.StyledString(textx, stylex));
			}

			return Optional.empty();
		}, style);
		TextHandler.LineWrappingCollector lineWrappingCollector = new TextHandler.LineWrappingCollector(list);
		boolean bl = true;
		boolean bl2 = false;
		boolean bl3 = false;

		while (bl) {
			bl = false;
			TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)maxWidth);

			for (TextHandler.StyledString styledString : lineWrappingCollector.parts) {
				boolean bl4 = TextVisitFactory.visitFormatted(styledString.literal, 0, styledString.style, style, lineBreakingVisitor);
				if (!bl4) {
					int i = lineBreakingVisitor.getEndingIndex();
					Style style2 = lineBreakingVisitor.getEndingStyle();
					char c = lineWrappingCollector.charAt(i);
					boolean bl5 = c == '\n';
					boolean bl6 = bl5 || c == ' ';
					bl2 = bl5;
					StringVisitable stringVisitable = lineWrappingCollector.collectLine(i, bl6 ? 1 : 0, style2);
					biConsumer.accept(stringVisitable, bl3);
					bl3 = !bl5;
					bl = true;
					break;
				}

				lineBreakingVisitor.offset(styledString.literal.length());
			}
		}

		StringVisitable stringVisitable2 = lineWrappingCollector.collectRemainers();
		if (stringVisitable2 != null) {
			biConsumer.accept(stringVisitable2, bl3);
		} else if (bl2) {
			biConsumer.accept(StringVisitable.EMPTY, false);
		}
	}

	class LineBreakingVisitor implements CharacterVisitor {
		private final float maxWidth;
		private int endIndex = -1;
		private Style endStyle = Style.EMPTY;
		private boolean nonEmpty;
		private float totalWidth;
		private int lastSpaceBreak = -1;
		private Style lastSpaceStyle = Style.EMPTY;
		private int count;
		private int startOffset;

		public LineBreakingVisitor(float maxWidth) {
			this.maxWidth = Math.max(maxWidth, 1.0F);
		}

		@Override
		public boolean accept(int i, Style style, int j) {
			int k = i + this.startOffset;
			switch (j) {
				case 10:
					return this.breakLine(k, style);
				case 32:
					this.lastSpaceBreak = k;
					this.lastSpaceStyle = style;
				default:
					float f = TextHandler.this.widthRetriever.getWidth(j, style);
					this.totalWidth += f;
					if (!this.nonEmpty || !(this.totalWidth > this.maxWidth)) {
						this.nonEmpty |= f != 0.0F;
						this.count = k + Character.charCount(j);
						return true;
					} else {
						return this.lastSpaceBreak != -1 ? this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle) : this.breakLine(k, style);
					}
			}
		}

		private boolean breakLine(int finishIndex, Style finishStyle) {
			this.endIndex = finishIndex;
			this.endStyle = finishStyle;
			return false;
		}

		private boolean hasLineBreak() {
			return this.endIndex != -1;
		}

		public int getEndingIndex() {
			return this.hasLineBreak() ? this.endIndex : this.count;
		}

		public Style getEndingStyle() {
			return this.endStyle;
		}

		public void offset(int extraOffset) {
			this.startOffset += extraOffset;
		}
	}

	static class LineWrappingCollector {
		final List<TextHandler.StyledString> parts;
		private String joined;

		public LineWrappingCollector(List<TextHandler.StyledString> parts) {
			this.parts = parts;
			this.joined = (String)parts.stream().map(styledString -> styledString.literal).collect(Collectors.joining());
		}

		public char charAt(int index) {
			return this.joined.charAt(index);
		}

		public StringVisitable collectLine(int lineLength, int skippedLength, Style style) {
			TextCollector textCollector = new TextCollector();
			ListIterator<TextHandler.StyledString> listIterator = this.parts.listIterator();
			int i = lineLength;
			boolean bl = false;

			while (listIterator.hasNext()) {
				TextHandler.StyledString styledString = (TextHandler.StyledString)listIterator.next();
				String string = styledString.literal;
				int j = string.length();
				if (!bl) {
					if (i > j) {
						textCollector.add(styledString);
						listIterator.remove();
						i -= j;
					} else {
						String string2 = string.substring(0, i);
						if (!string2.isEmpty()) {
							textCollector.add(StringVisitable.styled(string2, styledString.style));
						}

						i += skippedLength;
						bl = true;
					}
				}

				if (bl) {
					if (i <= j) {
						String string3 = string.substring(i);
						if (string3.isEmpty()) {
							listIterator.remove();
						} else {
							listIterator.set(new TextHandler.StyledString(string3, style));
						}
						break;
					}

					listIterator.remove();
					i -= j;
				}
			}

			this.joined = this.joined.substring(lineLength + skippedLength);
			return textCollector.getCombined();
		}

		@Nullable
		public StringVisitable collectRemainers() {
			TextCollector textCollector = new TextCollector();
			this.parts.forEach(textCollector::add);
			this.parts.clear();
			return textCollector.getRawCombined();
		}
	}

	@FunctionalInterface
	public interface LineWrappingConsumer {
		void accept(Style style, int start, int end);
	}

	static class StyledString implements StringVisitable {
		final String literal;
		final Style style;

		public StyledString(String literal, Style style) {
			this.literal = literal;
			this.style = style;
		}

		@Override
		public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
			return visitor.accept(this.literal);
		}

		@Override
		public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
			return styledVisitor.accept(this.style.withParent(style), this.literal);
		}
	}

	class WidthLimitingVisitor implements CharacterVisitor {
		private float widthLeft;
		private int length;

		public WidthLimitingVisitor(float maxWidth) {
			this.widthLeft = maxWidth;
		}

		@Override
		public boolean accept(int i, Style style, int j) {
			this.widthLeft = this.widthLeft - TextHandler.this.widthRetriever.getWidth(j, style);
			if (this.widthLeft >= 0.0F) {
				this.length = i + Character.charCount(j);
				return true;
			} else {
				return false;
			}
		}

		public int getLength() {
			return this.length;
		}

		public void resetLength() {
			this.length = 0;
		}
	}

	@FunctionalInterface
	public interface WidthRetriever {
		float getWidth(int codePoint, Style style);
	}
}
