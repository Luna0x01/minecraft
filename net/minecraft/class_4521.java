package net.minecraft;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class class_4521 extends class_3407 {
	public class_4521(Schema schema, boolean bl) {
		super("EntityTippedArrowFix", schema, bl);
	}

	@Override
	protected String method_15257(String string) {
		return Objects.equals(string, "TippedArrow") ? "Arrow" : string;
	}
}
