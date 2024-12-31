package net.minecraft.datafixer.fix;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.class_3402;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
	public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> method_15141(Dynamic<?> dynamic) {
		return dynamic.update("pages", dynamic2 -> (Dynamic)DataFixUtils.orElse(dynamic2.getStream().map(stream -> stream.map(dynamicxx -> {
					if (!dynamicxx.getStringValue().isPresent()) {
						return dynamicxx;
					} else {
						String string = (String)dynamicxx.getStringValue().get();
						Text text = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									text = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
									if (text == null) {
										text = new LiteralText("");
									}
								} catch (JsonParseException var6) {
								}

								if (text == null) {
									try {
										text = Text.Serializer.deserializeText(string);
									} catch (JsonParseException var5) {
									}
								}

								if (text == null) {
									try {
										text = Text.Serializer.lenientDeserializeText(string);
									} catch (JsonParseException var4) {
									}
								}

								if (text == null) {
									text = new LiteralText(string);
								}
							} else {
								text = new LiteralText(string);
							}
						} else {
							text = new LiteralText("");
						}

						return dynamicxx.createString(Text.Serializer.serialize(text));
					}
				})).map(dynamic::createList), dynamic.emptyList()));
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16592);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemWrittenBookPagesStrictJsonFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_15141))
		);
	}
}
