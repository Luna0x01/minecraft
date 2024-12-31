package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4317;
import net.minecraft.class_4318;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorText extends BaseText {
	private static final Logger field_21516 = LogManager.getLogger();
	private final String pattern;
	@Nullable
	private final class_4317 field_21517;

	public SelectorText(String string) {
		this.pattern = string;
		class_4317 lv = null;

		try {
			class_4318 lv2 = new class_4318(new StringReader(string));
			lv = lv2.method_19818();
		} catch (CommandSyntaxException var4) {
			field_21516.warn("Invalid selector component: {}", string, var4.getMessage());
		}

		this.field_21517 = lv;
	}

	public String getPattern() {
		return this.pattern;
	}

	public Text method_20196(class_3915 arg) throws CommandSyntaxException {
		return (Text)(this.field_21517 == null ? new LiteralText("") : class_4317.method_19732(this.field_21517.method_19735(arg)));
	}

	@Override
	public String computeValue() {
		return this.pattern;
	}

	public SelectorText copy() {
		return new SelectorText(this.pattern);
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
