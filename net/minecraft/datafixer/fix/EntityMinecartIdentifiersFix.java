package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_3402;

public class EntityMinecartIdentifiersFix extends DataFix {
	private static final List<String> MINECARTS = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

	public EntityMinecartIdentifiersFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = this.getInputSchema().findChoiceType(class_3402.field_16596);
		TaggedChoiceType<String> taggedChoiceType2 = this.getOutputSchema().findChoiceType(class_3402.field_16596);
		return this.fixTypeEverywhere(
			"EntityMinecartIdentifiersFix",
			taggedChoiceType,
			taggedChoiceType2,
			dynamicOps -> pair -> {
					if (!Objects.equals(pair.getFirst(), "Minecart")) {
						return pair;
					} else {
						Typed<? extends Pair<String, ?>> typed = (Typed<? extends Pair<String, ?>>)taggedChoiceType.point(dynamicOps, "Minecart", pair.getSecond())
							.orElseThrow(IllegalStateException::new);
						Dynamic<?> dynamic = (Dynamic<?>)typed.getOrCreate(DSL.remainderFinder());
						int i = dynamic.getInt("Type");
						String string;
						if (i > 0 && i < MINECARTS.size()) {
							string = (String)MINECARTS.get(i);
						} else {
							string = "MinecartRideable";
						}

						return Pair.of(
							string,
							((Optional)((Type)taggedChoiceType2.types().get(string)).read(typed.write()).getSecond())
								.orElseThrow(() -> new IllegalStateException("Could not read the new minecart."))
						);
					}
				}
		);
	}
}
