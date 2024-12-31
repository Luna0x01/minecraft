package net.minecraft.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;

public class Style {
	public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
	public static final Identifier DEFAULT_FONT_ID = new Identifier("minecraft", "default");
	@Nullable
	private final TextColor color;
	@Nullable
	private final Boolean bold;
	@Nullable
	private final Boolean italic;
	@Nullable
	private final Boolean underlined;
	@Nullable
	private final Boolean strikethrough;
	@Nullable
	private final Boolean obfuscated;
	@Nullable
	private final ClickEvent clickEvent;
	@Nullable
	private final HoverEvent hoverEvent;
	@Nullable
	private final String insertion;
	@Nullable
	private final Identifier font;

	private Style(
		@Nullable TextColor color,
		@Nullable Boolean bold,
		@Nullable Boolean italic,
		@Nullable Boolean underlined,
		@Nullable Boolean strikethrough,
		@Nullable Boolean obfuscated,
		@Nullable ClickEvent clickEvent,
		@Nullable HoverEvent hoverEvent,
		@Nullable String insertion,
		@Nullable Identifier font
	) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		this.insertion = insertion;
		this.font = font;
	}

	@Nullable
	public TextColor getColor() {
		return this.color;
	}

	public boolean isBold() {
		return this.bold == Boolean.TRUE;
	}

	public boolean isItalic() {
		return this.italic == Boolean.TRUE;
	}

	public boolean isStrikethrough() {
		return this.strikethrough == Boolean.TRUE;
	}

	public boolean isUnderlined() {
		return this.underlined == Boolean.TRUE;
	}

	public boolean isObfuscated() {
		return this.obfuscated == Boolean.TRUE;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	@Nullable
	public ClickEvent getClickEvent() {
		return this.clickEvent;
	}

	@Nullable
	public HoverEvent getHoverEvent() {
		return this.hoverEvent;
	}

	@Nullable
	public String getInsertion() {
		return this.insertion;
	}

	public Identifier getFont() {
		return this.font != null ? this.font : DEFAULT_FONT_ID;
	}

	public Style withColor(@Nullable TextColor color) {
		return new Style(
			color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
		);
	}

	public Style withColor(@Nullable Formatting color) {
		return this.withColor(color != null ? TextColor.fromFormatting(color) : null);
	}

	public Style withBold(@Nullable Boolean bold) {
		return new Style(
			this.color, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
		);
	}

	public Style withItalic(@Nullable Boolean italic) {
		return new Style(
			this.color, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
		);
	}

	public Style withUnderline(@Nullable Boolean underline) {
		return new Style(
			this.color, this.bold, this.italic, underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
		);
	}

	public Style withClickEvent(@Nullable ClickEvent clickEvent) {
		return new Style(
			this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent, this.insertion, this.font
		);
	}

	public Style withHoverEvent(@Nullable HoverEvent hoverEvent) {
		return new Style(
			this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent, this.insertion, this.font
		);
	}

	public Style withInsertion(@Nullable String insertion) {
		return new Style(
			this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, insertion, this.font
		);
	}

	public Style withFont(@Nullable Identifier font) {
		return new Style(
			this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, font
		);
	}

	public Style withFormatting(Formatting formatting) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;
		switch (formatting) {
			case OBFUSCATED:
				boolean5 = true;
				break;
			case BOLD:
				boolean_ = true;
				break;
			case STRIKETHROUGH:
				boolean3 = true;
				break;
			case UNDERLINE:
				boolean4 = true;
				break;
			case ITALIC:
				boolean2 = true;
				break;
			case RESET:
				return EMPTY;
			default:
				textColor = TextColor.fromFormatting(formatting);
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	public Style withExclusiveFormatting(Formatting formatting) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;
		switch (formatting) {
			case OBFUSCATED:
				boolean5 = true;
				break;
			case BOLD:
				boolean_ = true;
				break;
			case STRIKETHROUGH:
				boolean3 = true;
				break;
			case UNDERLINE:
				boolean4 = true;
				break;
			case ITALIC:
				boolean2 = true;
				break;
			case RESET:
				return EMPTY;
			default:
				boolean5 = false;
				boolean_ = false;
				boolean3 = false;
				boolean4 = false;
				boolean2 = false;
				textColor = TextColor.fromFormatting(formatting);
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	public Style withFormatting(Formatting... formattings) {
		TextColor textColor = this.color;
		Boolean boolean_ = this.bold;
		Boolean boolean2 = this.italic;
		Boolean boolean3 = this.strikethrough;
		Boolean boolean4 = this.underlined;
		Boolean boolean5 = this.obfuscated;

		for (Formatting formatting : formattings) {
			switch (formatting) {
				case OBFUSCATED:
					boolean5 = true;
					break;
				case BOLD:
					boolean_ = true;
					break;
				case STRIKETHROUGH:
					boolean3 = true;
					break;
				case UNDERLINE:
					boolean4 = true;
					break;
				case ITALIC:
					boolean2 = true;
					break;
				case RESET:
					return EMPTY;
				default:
					textColor = TextColor.fromFormatting(formatting);
			}
		}

		return new Style(textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
	}

	public Style withParent(Style parent) {
		if (this == EMPTY) {
			return parent;
		} else {
			return parent == EMPTY
				? this
				: new Style(
					this.color != null ? this.color : parent.color,
					this.bold != null ? this.bold : parent.bold,
					this.italic != null ? this.italic : parent.italic,
					this.underlined != null ? this.underlined : parent.underlined,
					this.strikethrough != null ? this.strikethrough : parent.strikethrough,
					this.obfuscated != null ? this.obfuscated : parent.obfuscated,
					this.clickEvent != null ? this.clickEvent : parent.clickEvent,
					this.hoverEvent != null ? this.hoverEvent : parent.hoverEvent,
					this.insertion != null ? this.insertion : parent.insertion,
					this.font != null ? this.font : parent.font
				);
		}
	}

	public String toString() {
		return "Style{ color="
			+ this.color
			+ ", bold="
			+ this.bold
			+ ", italic="
			+ this.italic
			+ ", underlined="
			+ this.underlined
			+ ", strikethrough="
			+ this.strikethrough
			+ ", obfuscated="
			+ this.obfuscated
			+ ", clickEvent="
			+ this.getClickEvent()
			+ ", hoverEvent="
			+ this.getHoverEvent()
			+ ", insertion="
			+ this.getInsertion()
			+ ", font="
			+ this.getFont()
			+ '}';
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Style)) {
			return false;
		} else {
			Style style = (Style)obj;
			return this.isBold() == style.isBold()
				&& Objects.equals(this.getColor(), style.getColor())
				&& this.isItalic() == style.isItalic()
				&& this.isObfuscated() == style.isObfuscated()
				&& this.isStrikethrough() == style.isStrikethrough()
				&& this.isUnderlined() == style.isUnderlined()
				&& Objects.equals(this.getClickEvent(), style.getClickEvent())
				&& Objects.equals(this.getHoverEvent(), style.getHoverEvent())
				&& Objects.equals(this.getInsertion(), style.getInsertion())
				&& Objects.equals(this.getFont(), style.getFont());
		}
	}

	public int hashCode() {
		return Objects.hash(
			new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion}
		);
	}

	public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
		@Nullable
		public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				if (jsonObject == null) {
					return null;
				} else {
					Boolean boolean_ = parseNullableBoolean(jsonObject, "bold");
					Boolean boolean2 = parseNullableBoolean(jsonObject, "italic");
					Boolean boolean3 = parseNullableBoolean(jsonObject, "underlined");
					Boolean boolean4 = parseNullableBoolean(jsonObject, "strikethrough");
					Boolean boolean5 = parseNullableBoolean(jsonObject, "obfuscated");
					TextColor textColor = parseColor(jsonObject);
					String string = parseInsertion(jsonObject);
					ClickEvent clickEvent = getClickEvent(jsonObject);
					HoverEvent hoverEvent = getHoverEvent(jsonObject);
					Identifier identifier = getFont(jsonObject);
					return new Style(textColor, boolean_, boolean2, boolean3, boolean4, boolean5, clickEvent, hoverEvent, string, identifier);
				}
			} else {
				return null;
			}
		}

		@Nullable
		private static Identifier getFont(JsonObject root) {
			if (root.has("font")) {
				String string = JsonHelper.getString(root, "font");

				try {
					return new Identifier(string);
				} catch (InvalidIdentifierException var3) {
					throw new JsonSyntaxException("Invalid font name: " + string);
				}
			} else {
				return null;
			}
		}

		@Nullable
		private static HoverEvent getHoverEvent(JsonObject root) {
			if (root.has("hoverEvent")) {
				JsonObject jsonObject = JsonHelper.getObject(root, "hoverEvent");
				HoverEvent hoverEvent = HoverEvent.fromJson(jsonObject);
				if (hoverEvent != null && hoverEvent.getAction().isParsable()) {
					return hoverEvent;
				}
			}

			return null;
		}

		@Nullable
		private static ClickEvent getClickEvent(JsonObject root) {
			if (root.has("clickEvent")) {
				JsonObject jsonObject = JsonHelper.getObject(root, "clickEvent");
				String string = JsonHelper.getString(jsonObject, "action", null);
				ClickEvent.Action action = string == null ? null : ClickEvent.Action.byName(string);
				String string2 = JsonHelper.getString(jsonObject, "value", null);
				if (action != null && string2 != null && action.isUserDefinable()) {
					return new ClickEvent(action, string2);
				}
			}

			return null;
		}

		@Nullable
		private static String parseInsertion(JsonObject root) {
			return JsonHelper.getString(root, "insertion", null);
		}

		@Nullable
		private static TextColor parseColor(JsonObject root) {
			if (root.has("color")) {
				String string = JsonHelper.getString(root, "color");
				return TextColor.parse(string);
			} else {
				return null;
			}
		}

		@Nullable
		private static Boolean parseNullableBoolean(JsonObject root, String key) {
			return root.has(key) ? root.get(key).getAsBoolean() : null;
		}

		@Nullable
		public JsonElement serialize(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
			if (style.isEmpty()) {
				return null;
			} else {
				JsonObject jsonObject = new JsonObject();
				if (style.bold != null) {
					jsonObject.addProperty("bold", style.bold);
				}

				if (style.italic != null) {
					jsonObject.addProperty("italic", style.italic);
				}

				if (style.underlined != null) {
					jsonObject.addProperty("underlined", style.underlined);
				}

				if (style.strikethrough != null) {
					jsonObject.addProperty("strikethrough", style.strikethrough);
				}

				if (style.obfuscated != null) {
					jsonObject.addProperty("obfuscated", style.obfuscated);
				}

				if (style.color != null) {
					jsonObject.addProperty("color", style.color.getName());
				}

				if (style.insertion != null) {
					jsonObject.add("insertion", jsonSerializationContext.serialize(style.insertion));
				}

				if (style.clickEvent != null) {
					JsonObject jsonObject2 = new JsonObject();
					jsonObject2.addProperty("action", style.clickEvent.getAction().getName());
					jsonObject2.addProperty("value", style.clickEvent.getValue());
					jsonObject.add("clickEvent", jsonObject2);
				}

				if (style.hoverEvent != null) {
					jsonObject.add("hoverEvent", style.hoverEvent.toJson());
				}

				if (style.font != null) {
					jsonObject.addProperty("font", style.font.toString());
				}

				return jsonObject;
			}
		}
	}
}
